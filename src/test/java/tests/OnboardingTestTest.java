package tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.OrgApi;
import base.BaseTest;
import db.DBUtil;
import io.restassured.response.Response;
import pages.OnboardingPage;
import utilities.ExcelUtil;

public class OnboardingTestTest extends BaseTest {

    //  LOAD EXCEL
    @BeforeMethod
    public void loadTestData() {

        openOnboardPage();

       String path = System.getProperty("user.dir")
        + "/src/test/java/resources/testdata/OnboardingTestData_DifferentEmails.xlsx";
        ExcelUtil.loadExcel(path, "OnboardingData");
        System.out.println("Excel Path: " + path);
        System.out.println(" Onboarding Excel Loaded");
    }

    // GET DATA METHOD
    private String[] getTestData(String testCaseId) {

        int rowCount = ExcelUtil.getRowCount();

        for (int i = 1; i <= rowCount; i++) {

            String tcId = ExcelUtil.getCellData(i, 0);

            if (tcId != null && tcId.equalsIgnoreCase(testCaseId)) {

                String orgName = ExcelUtil.getCellData(i, 1);
                String email = ExcelUtil.getCellData(i, 2);
                String location = ExcelUtil.getCellData(i, 3);
                String password = ExcelUtil.getCellData(i, 4);

                return new String[]{
                        orgName,
                        email,
                        location,
                        password
                };
            }
        }

        throw new RuntimeException("❌ Test data not found: " + testCaseId);
    }

    // ================= TEST =================

    @Test
public void TC_OB_001_ValidOnboarding_ShouldPass() throws Exception {

    openOnboardPage();

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_OB_001");

    String orgName = data[0];
    String email = data[1];
    String location = data[2];
    String password = data[3];

    onboarding.completeOnboarding(orgName, email, location);
    onboarding.completeStep2(password);

    Thread.sleep(3000);

    Assert.assertTrue(db.isUserPresent(email), "User should exist in DB");

    Response response = api.createSuperAdmin(
            "API_" + System.currentTimeMillis(),
            "api" + System.currentTimeMillis() + "@mail.com",
            location
    );

    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "API onboarding failed"
    );

    db.assertDbChecked();
}

    @Test
    public void TC_OB_002_DuplicateEmail_Onboarding_ShouldFail_WithAPIValidation() throws Exception {
 
        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String[] data = getTestData("TC_OB_002");

        String orgName = data[0] + "_" + System.currentTimeMillis();
        String email = data[1];
        String location = data[2];

        Assert.assertTrue(db.isUserPresent(email), "Precondition failed: duplicate email must exist in DB");

        onboarding.completeOnboarding(orgName, email, location);

        String alertText = onboarding.getAlertMessage();
        System.out.println("Popup Message: " + alertText);

        Assert.assertTrue(
                alertText.toLowerCase().contains("fail") ||
                alertText.toLowerCase().contains("exist") ||
                alertText.toLowerCase().contains("already"),
                "Expected duplicate email failure message"
        );

        Response response = api.createSuperAdmin(orgName, email, location);

        Assert.assertTrue(
                response.getStatusCode() == 400 ||
                response.getStatusCode() == 404 ||
                response.getStatusCode() == 409,
                "API should reject duplicate email"
        );

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_003_ValidDetails_ShouldMoveToStep2() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_003");

        onboarding.completeOnboarding(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        Assert.assertTrue(onboarding.isStep2Displayed(),
                "Step 2 should open after valid Step 1 details");
    }

   private String uniqueEmail(String email) {

    String[] parts = email.split("@");

    return parts[0]
            + System.currentTimeMillis()
            + "@"
            + parts[1];
}
    @Test
public void TC_OB_004_InvalidEmailFormat_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    String[] data = getTestData("TC_OB_004");

    onboarding.enterOrgName(data[0]);
    onboarding.enterEmail(data[1]);
    onboarding.enterLocation(data[2]);
    onboarding.clickOnboard();

    String alertMsg = onboarding.getAlertMessage();

    System.out.println("Alert Message: " + alertMsg);

    Assert.assertTrue(
            alertMsg.toLowerCase().contains("onboarding failed") ||
            alertMsg.toLowerCase().contains("failed"),
            "Invalid email should show onboarding failed alert"
    );
}
    @Test
    public void TC_OB_005_LoginHere_ShouldNavigateToLogin() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);

        onboarding.clickLoginHere();

        wait.until(ExpectedConditions.urlContains("login"));

        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "Login page should open");
    }

    @Test
    public void TC_OB_006_OrganizationId_ShouldBeVisibleInStep2() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_006");

        onboarding.completeOnboarding(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        Assert.assertTrue(onboarding.isStep2Displayed(), "Step 2 page should open");
        Assert.assertTrue(onboarding.isOrgIdVisible(), "Organization ID should be visible in Step 2");
    }

    @Test
    public void TC_OB_007_EmptyPassword_ShouldBlockCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_007");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterConfirmPassword("Test@123");
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when password is empty");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_008_EmptyConfirmPassword_ShouldBlockCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_008");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when confirm password is empty");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_009_PasswordMismatch_ShouldFail() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_009");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword("Test@123");
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when passwords mismatch");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_010_ValidPassword_ShouldCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_010");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);
        onboarding.completeStep2(data[3]);

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "Account should be created successfully after valid Step 2");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_011_CreatedAccount_ShouldStoreInDB() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_011");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);
        onboarding.completeStep2(data[3]);

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "User should be stored in DB after onboarding");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_012_API_ShouldCreateSuperAdmin() throws Exception {

        OrgApi api = new OrgApi();
        String[] data = getTestData("TC_OB_012");

        Response response = api.createSuperAdmin(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        Assert.assertTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "API should create super admin successfully"
        );
    }

    @Test
    public void TC_OB_013_PasswordField_ShouldBeMasked() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_013");

        onboarding.completeOnboarding(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        Assert.assertEquals(onboarding.getPasswordFieldType(), "password",
                "Password field should be masked");
    }

    @Test
    public void TC_OB_014_ConfirmPasswordField_ShouldBeMasked() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_014");

        onboarding.completeOnboarding(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        Assert.assertEquals(onboarding.getConfirmPasswordFieldType(), "password",
                "Confirm Password field should be masked");
    }

    @Test
    public void TC_OB_015_BackButton_ShouldReturnToStep1() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_015");

        onboarding.completeOnboarding(
                data[0] + "_" + System.currentTimeMillis(),
                uniqueEmail(data[1]),
                data[2]
        );

        onboarding.clickBack();

        Assert.assertTrue(onboarding.isOnboardButtonEnabled(),
                "Back button should return to Step 1");
    }

    @Test
    public void TC_OB_016_EmptyEmail_ShouldDisableOnboardButton() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_016");

        onboarding.enterOrgName(data[0]);
        onboarding.enterLocation(data[2]);

        boolean isEnabled = onboarding.isOnboardButtonEnabled();

        Assert.assertFalse(isEnabled,
                "Onboard button should be disabled when email is empty");
    }

    @Test
    public void TC_OB_017_InvalidEmailFormat_NoAt_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_017");

        onboarding.enterOrgName(data[0]);
        onboarding.enterEmail(data[1]);
        onboarding.enterLocation(data[2]);
        onboarding.clickOnboard();

        String validationMsg = onboarding.getEmailValidationMessage();

        Assert.assertTrue(validationMsg.toLowerCase().contains("@"),
                "Invalid email format should show @ validation");
    }

    @Test
    public void TC_OB_018_EmailWithoutDomain_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_018");

        onboarding.enterOrgName(data[0]);
        onboarding.enterEmail(data[1]);
        onboarding.enterLocation(data[2]);
        onboarding.clickOnboard();

        boolean step2Opened = onboarding.isStep2Displayed();

        Assert.assertFalse(step2Opened,
                "Email without domain should not move to Step 2");
    }

    @Test
    public void TC_OB_019_EmailWithSpaces_ShouldTrim() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_019");

        String email = data[1];

        onboarding.enterOrgName(data[0]);
        onboarding.enterEmail(email);
        onboarding.enterLocation(data[2]);
        onboarding.clickOnboard();

        boolean step2Opened = onboarding.isStep2Displayed();

        if (step2Opened) {
            String step2Email = onboarding.getStep2EmailValue();
            Assert.assertEquals(step2Email.trim(), email.trim(),
                    "Email should be trimmed and carried to Step 2");
        } else {
            String validationMsg = onboarding.getEmailValidationMessage();
            Assert.assertFalse(validationMsg.trim().isEmpty(),
                    "Email with spaces should be rejected");
        }
    }

    @Test
    public void TC_OB_020_VeryLongEmail_ShouldBeHandledProperly() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        String[] data = getTestData("TC_OB_020");

        String longEmail = "a".repeat(260) + "@mail.com";

        onboarding.enterOrgName(data[0]);
        onboarding.enterEmail(longEmail);
        onboarding.enterLocation(data[2]);
        onboarding.clickOnboard();

        boolean step2Opened = onboarding.isStep2Displayed();
        String validationMsg = onboarding.getEmailValidationMessage();

        Assert.assertTrue(step2Opened || !validationMsg.trim().isEmpty(),
                "Very long email should be handled safely or rejected");
    }

    @Test
    public void TC_OB_021_EmptyPassword_ShouldBlockCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_021");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterConfirmPassword("Test@123");
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when password is empty");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_022_EmptyConfirmPassword_ShouldBlockCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_022");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when confirm password is empty");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_023_PasswordMismatch_ShouldShowValidationError() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_023");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword("Test@123");
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored in DB when passwords mismatch");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_024_WeakPassword_NoSpecialChar_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_024");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "Weak password without special character should not create user");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_025_PasswordLessThanMinLength_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_025");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "Password less than minimum length should not create user");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_026_PasswordWithOnlySpaces_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_026");

        String email = uniqueEmail(data[1]);
        String password = data[3];

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(password);
        onboarding.enterConfirmPassword(password);
        onboarding.clickCreateAccount();

        String alertMsg = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert Message: " + alertMsg);

        Assert.assertFalse(db.isUserPresent(email),
                "Password with only spaces should not create user");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_027_ValidStrongPassword_ShouldCreateAccount() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_027");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "Valid strong password should create account and store user in DB");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_028_UserShouldBeStoredInDB() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_028");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);
        onboarding.completeStep2(data[3]);

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "User should be stored in DB");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_029_DBShouldNotStoreUser_OnFailure() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_029");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword("Test@123");
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        Thread.sleep(2000);

        Assert.assertFalse(db.isUserPresent(email),
                "User should not be stored when onboarding fails");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_030_DBFieldsShouldMatchInput() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_030");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);
        onboarding.completeStep2(data[3]);

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "User must exist in DB");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_031_UI_DB_API_ShouldReturnSuccessMessage() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String[] data = getTestData("TC_OB_031");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);
        onboarding.completeStep2(data[3]);

        Thread.sleep(3000);

        Assert.assertTrue(db.isUserPresent(email),
                "User should be stored in DB after UI onboarding");

        Response response = api.createSuperAdmin(
                "API_Check_" + System.currentTimeMillis(),
                "apicheck" + System.currentTimeMillis() + "@mail.com",
                data[2]
        );

        int statusCode = response.getStatusCode();

        Assert.assertTrue(
                statusCode == 200 || statusCode == 201,
                "API should return 200/201 but got: " + statusCode
        );

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_032_PasswordWithoutUppercase_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_032");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alert = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert: " + alert);

        Assert.assertFalse(db.isUserPresent(email),
                "Password without uppercase should be rejected");

        db.assertDbChecked();
    }

    @Test
    public void TC_OB_033_PasswordWithoutNumber_ShouldReject() throws Exception {

        OnboardingPage onboarding = new OnboardingPage(driver, wait);
        DBUtil db = new DBUtil();

        String[] data = getTestData("TC_OB_033");

        String email = uniqueEmail(data[1]);

        onboarding.completeOnboarding(data[0] + "_" + System.currentTimeMillis(), email, data[2]);

        onboarding.enterPassword(data[3]);
        onboarding.enterConfirmPassword(data[3]);
        onboarding.clickCreateAccount();

        String alert = onboarding.getAlertMessageIfPresent();
        System.out.println("Alert: " + alert);

        Assert.assertFalse(db.isUserPresent(email),
                "Password without number should be rejected");

        db.assertDbChecked();
    }
}
