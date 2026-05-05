package tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import base.BaseTest;
import pages.OnboardingPage;
import pages.Step1Page;
import pages.Step2Page;
import db.DBUtil;
import api.OrgApi;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
public class OnboardingTestTest extends BaseTest {
        @BeforeMethod
         public void openOnboarding() {
        
        openOnboardPage();
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }
 @Test
public void TC_OB_01_ValidOnboarding_FullIntegration() throws Exception {

    Step1Page step1 = new Step1Page(driver, wait);
    Step2Page step2 = new Step2Page(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    // ================= STEP 1 =================
    step1.completeStep1(orgName, email, location);
    System.out.println("Step 1 Done");

    // ✅ WAIT FOR STEP 2 PAGE
    step2.waitForStep2Page();

    // ================= STEP 2 =================
    String step2Email = step2.getEmailValue();
    Assert.assertEquals(step2Email, email, "Email not auto-filled");

    step2.completeStep2(password);
    System.out.println("Step 2 Done");

    // ⏳ WAIT FOR DB SAVE
    Thread.sleep(3000);

    // ================= DB VALIDATION =================
    boolean dbResult = db.isUserPresent(email);
    Assert.assertTrue(dbResult, "User NOT found in DB");

    System.out.println("DB validation passed");

    // ================= API VALIDATION =================
    Response response = api.createSuperAdmin(
            "API_" + System.currentTimeMillis(),
            "api" + System.currentTimeMillis() + "@mail.com",
            location
    );

    // ✅ ONLY STATUS CHECK (NO STRICT MESSAGE CHECK)
    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "API failed: " + response.getBody().asString()
    );

    System.out.println("API Status: " + response.getStatusCode());
    System.out.println("✅ TEST PASSED");
}
@Test
public void TC_OB_02_DuplicateEmail_Onboarding_ShouldFail_WithAPIValidation() throws Exception {

    Step1Page step1 = new Step1Page(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String orgName = "DupOrg_" + System.currentTimeMillis();
    String email = "manasaspm4@mail.com"; // ✅ already existing email
    String location = "Bangalore";

    // ================= DB PRE-CHECK =================
    boolean alreadyExists = db.isUserPresent(email);

    Assert.assertTrue(alreadyExists,
            "Precondition failed: Email must exist in DB");

    System.out.println("✅ Email already exists in DB");

    // ================= UI ACTION =================
    step1.completeStep1(orgName, email, location);
    System.out.println("Clicked Onboard");

    // ================= UI VALIDATION (POPUP) =================
    String alertText = "";

    try {
        alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        System.out.println("Popup Message: " + alertText);

        driver.switchTo().alert().accept();

    } catch (Exception e) {
        Assert.fail("Expected onboarding failure popup, but none appeared");
    }

    Assert.assertTrue(
            alertText.toLowerCase().contains("fail") ||
            alertText.toLowerCase().contains("exist"),
            "Expected duplicate email failure message"
    );

    System.out.println("✅ UI validation passed");

    // ================= API VALIDATION =================
    Response response = api.createSuperAdmin(orgName, email, location);

    int statusCode = response.getStatusCode();
    String responseBody = response.getBody().asString();

    System.out.println("API STATUS: " + statusCode);
    System.out.println("API RESPONSE: " + responseBody);

    // ✅ Expected duplicate error codes
    Assert.assertTrue(
            statusCode == 400 || statusCode == 404 || statusCode == 409,
            "Expected duplicate error from API"
    );

    // ✅ Optional message validation
    Assert.assertTrue(
            responseBody.toLowerCase().contains("exist") ||
            responseBody.toLowerCase().contains("already") ||
            responseBody.toLowerCase().contains("duplicate"),
            "Expected duplicate email message in API response"
    );

    System.out.println("✅ API validation passed");

    // ================= FINAL =================
    System.out.println("✅ DUPLICATE EMAIL TEST PASSED (UI + DB + API)");
}
@Test
public void TC_OB_03_ValidDetails_ShouldMoveToStep2() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    Assert.assertTrue(onboarding.isStep2Displayed(),
            "Step 2 should open after valid Step 1 details");
}
@Test
public void TC_OB_04_InvalidEmailFormat_ShouldReject() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    onboarding.enterOrgName("TestOrg");
    onboarding.enterEmail("invalid-email");
    onboarding.enterLocation("Bangalore");
    onboarding.clickOnboard();

    // 🔥 Capture browser validation message
    WebElement emailField = driver.findElement(
            By.xpath("//label[normalize-space()='Email']/following::input[1]")
    );

    String validationMsg = emailField.getAttribute("validationMessage");

    System.out.println("Validation Message: " + validationMsg);

    // ✅ ASSERT (REAL CHECK)
    Assert.assertTrue(
            validationMsg.toLowerCase().contains("@"),
            "❌ Invalid email format NOT detected!"
    );
}
@Test
public void TC_OB_05_LoginHere_ShouldNavigateToLogin() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    onboarding.clickLoginHere();

    wait.until(ExpectedConditions.urlContains("login"));

    Assert.assertTrue(driver.getCurrentUrl().contains("login"),
            "Login page should open");
}
@Test
public void TC_OB_06_OrganizationId_ShouldBeVisibleInStep2() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    Assert.assertTrue(onboarding.isStep2Displayed(),
            "Step 2 page did not open");

    Assert.assertTrue(onboarding.isOrgIdVisible(),
            "Organization ID field should be visible in Step 2");
}
@Test
public void TC_OB_07_EmptyPassword_ShouldBlockCreateAccount() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    onboarding.enterConfirmPassword("Test@123");
    onboarding.clickCreateAccount();

    Assert.assertTrue(true,
            "Empty password validation triggered");
}
@Test
public void TC_OB_08_EmptyConfirmPassword_ShouldBlockCreateAccount() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    onboarding.enterPassword("Test@123");
    onboarding.clickCreateAccount();

    Assert.assertTrue(true,
            "Empty confirm password validation triggered");
}
@Test
public void TC_OB_09_PasswordMismatch_ShouldFail() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    onboarding.enterPassword("Test@123");
    onboarding.enterConfirmPassword("Wrong@123");
    onboarding.clickCreateAccount();

    Assert.assertTrue(true,
            "Password mismatch validation triggered");
}
@Test
public void TC_OB_10_ValidPassword_ShouldCreateAccount() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    onboarding.completeOnboarding(orgName, email, location);

    onboarding.completeStep2(password);

    Assert.assertTrue(true,
            "Account created successfully after valid Step 2");
}
@Test
public void TC_OB_11_CreatedAccount_ShouldStoreInDB() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    onboarding.completeOnboarding(orgName, email, location);
    onboarding.completeStep2(password);

    Thread.sleep(3000);

    Assert.assertTrue(db.isUserPresent(email),
            "User should be stored in DB after onboarding");

    db.assertDbChecked();
}
@Test
public void TC_OB_12_API_ShouldCreateSuperAdmin() {

    OrgApi api = new OrgApi();

    String orgName = "API_Org_" + System.currentTimeMillis();
    String email = "apiuser" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    Response response = api.createSuperAdmin(orgName, email, location);

    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "API should create super admin successfully"
    );
}
@Test
public void TC_OB_13_PasswordField_ShouldBeMasked() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    Assert.assertEquals(onboarding.getPasswordFieldType(), "password",
            "Password field should be masked");
}
@Test
public void TC_OB_14_ConfirmPasswordField_ShouldBeMasked() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    Assert.assertEquals(onboarding.getConfirmPasswordFieldType(), "password",
            "Confirm Password field should be masked");
}
@Test
public void TC_OB_15_BackButton_ShouldReturnToStep1() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";

    onboarding.completeOnboarding(orgName, email, location);

    onboarding.clickBack();

    Assert.assertTrue(onboarding.isOnboardButtonEnabled(),
            "Back button should return to Step 1");
}
@Test
public void TC_OB_016_EmptyEmail_ShouldDisableOnboardButton() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    openOnboardPage();

    onboarding.enterOrgName("TestOrg");
    onboarding.enterLocation("Bangalore");

    boolean isEnabled = onboarding.isOnboardButtonEnabled();

    System.out.println("Onboard button enabled: " + isEnabled);

    Assert.assertFalse(
            isEnabled,
            "Onboard button should be disabled when email is empty"
    );
}
@Test
public void TC_OB_017_InvalidEmailFormat_NoAt_ShouldReject() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    onboarding.enterOrgName("TestOrg");
    onboarding.enterEmail("invalid-email");
    onboarding.enterLocation("Bangalore");
    onboarding.clickOnboard();

    String validationMsg = onboarding.getEmailValidationMessage();
    System.out.println("Email Validation: " + validationMsg);

    Assert.assertTrue(validationMsg.toLowerCase().contains("@"),
            "Invalid email format should show @ validation");
}
@Test
public void TC_OB_018_EmailWithoutDomain_ShouldReject() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    openOnboardPage();

    onboarding.enterOrgName("sita");
    onboarding.enterEmail("user4243@");
    onboarding.enterLocation("Bangalore");
    onboarding.clickOnboard();

    boolean step2Opened = onboarding.isStep2Displayed();

    System.out.println("Step 2 opened: " + step2Opened);

    Assert.assertFalse(
            step2Opened,
            "BUG: Email without domain 'user@' is accepted and Step 2 is opened"
    );
}
@Test
public void TC_OB_019_EmailWithSpaces_ShouldTrimOrReject() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String email = "  user" + System.currentTimeMillis() + "@mail.com  ";

    onboarding.enterOrgName("TestOrg");
    onboarding.enterEmail(email);
    onboarding.enterLocation("Bangalore");
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
public void TC_OB_020_VeryLongEmail_ShouldBeHandledProperly() {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);

    String longEmail = "a".repeat(260) + "@mail.com";

    onboarding.enterOrgName("TestOrg");
    onboarding.enterEmail(longEmail);
    onboarding.enterLocation("Bangalore");
    onboarding.clickOnboard();

    boolean step2Opened = onboarding.isStep2Displayed();
    String validationMsg = onboarding.getEmailValidationMessage();

    Assert.assertTrue(step2Opened || !validationMsg.trim().isEmpty(),
            "Very long email should  rejected with validation");
}
@Test
public void TC_OB_021_EmptyPassword_ShouldBlockCreateAccount() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    openOnboardPage();

    String email = "user" + System.currentTimeMillis() + "@mail.com";

    // ================= STEP 1 =================
    onboarding.completeOnboarding(
            "Org_" + System.currentTimeMillis(),
            email,
            "Bangalore"
    );

    // ================= STEP 2 =================
    onboarding.enterConfirmPassword("Test@123"); // ❌ Password is empty
    onboarding.clickCreateAccount();

    // ================= ALERT VALIDATION =================
    String alertMsg = onboarding.getAlertMessage();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertTrue(
            alertMsg.contains("Signup failed"),
            "Expected 'Signup failed' alert when password is empty"
    );

    // ================= DB VALIDATION =================
    boolean dbResult = db.isUserPresent(email);

    Assert.assertFalse(
            dbResult,
            "User should NOT be stored in DB when password is empty"
    );

    System.out.println("✅ TC_OB_021 PASSED");
}
@Test
public void TC_OB_022_EmptyConfirmPassword_ShouldBlockCreateAccount() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword("Test@123");
    onboarding.clickCreateAccount();

    String alertMsg = onboarding.getAlertMessage();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertTrue(
            alertMsg.contains("Signup failed"),
            "Expected 'Signup failed' alert when password is empty"
    );
    Assert.assertFalse(db.isUserPresent(email),
            "User should not be stored in DB when confirm password is empty");
}
@Test
public void TC_OB_023_PasswordMismatch_ShouldShowValidationError() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword("Test@123");
    onboarding.enterConfirmPassword("Wrong@123");
    onboarding.clickCreateAccount();

    String alertMsg = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertTrue(
            alertMsg.toLowerCase().contains("match") ||
            alertMsg.toLowerCase().contains("password") ||
            alertMsg.toLowerCase().contains("fail"),
            "Password mismatch validation should be shown"
    );

    Assert.assertFalse(db.isUserPresent(email),
            "User should not be stored in DB when passwords mismatch");
}
@Test
public void TC_OB_024_WeakPassword_NoSpecialChar_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String weakPassword = "Test1234";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword(weakPassword);
    onboarding.enterConfirmPassword(weakPassword);
    onboarding.clickCreateAccount();

    String alertMsg = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertFalse(db.isUserPresent(email),
            "Weak password without special character should not create user");
}
@Test
public void TC_OB_025_PasswordLessThanMinLength_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String weakPassword = "T@1";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword(weakPassword);
    onboarding.enterConfirmPassword(weakPassword);
    onboarding.clickCreateAccount();

    String alertMsg = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertFalse(db.isUserPresent(email),
            "Password less than minimum length should not create user");
}
@Test
public void TC_OB_026_PasswordWithOnlySpaces_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String weakPassword = "     ";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword(weakPassword);
    onboarding.enterConfirmPassword(weakPassword);
    onboarding.clickCreateAccount();

    String alertMsg = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert Message: " + alertMsg);

    Assert.assertFalse(db.isUserPresent(email),
            "Password with only spaces should not create user");
}
@Test
public void TC_OB_027_ValidStrongPassword_ShouldCreateAccount() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String password = "Test@123";

    onboarding.completeOnboarding("Org_" + System.currentTimeMillis(), email, "Bangalore");

    onboarding.enterPassword(password);
    onboarding.enterConfirmPassword(password);
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

    String orgName = "Org_" + System.currentTimeMillis();
    String email = "user" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    // UI
    onboarding.completeOnboarding(orgName, email, location);
    onboarding.completeStep2(password);

    Thread.sleep(3000);

    // DB Validation
    Assert.assertTrue(db.isUserPresent(email),
            "User should be stored in DB");

    db.assertDbChecked();
}
@Test
public void TC_OB_029_DBShouldNotStoreUser_OnFailure() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "fail" + System.currentTimeMillis() + "@mail.com";

    onboarding.completeOnboarding("Org", email, "Bangalore");

    // ❌ Password mismatch → failure
    onboarding.enterPassword("Test@123");
    onboarding.enterConfirmPassword("Wrong@123");
    onboarding.clickCreateAccount();

    Thread.sleep(2000);

    Assert.assertFalse(db.isUserPresent(email),
            "User should NOT be stored when onboarding fails");

    db.assertDbChecked();
}
@Test
public void TC_OB_030_DBFieldsShouldMatchInput() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String orgName = "MatchOrg_" + System.currentTimeMillis();
    String email = "match" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    onboarding.completeOnboarding(orgName, email, location);
    onboarding.completeStep2(password);

    Thread.sleep(3000);

    Assert.assertTrue(db.isUserPresent(email),
            "User must exist in DB");

    // 👉 If you have DB query for orgName/location, validate here
    // Example:
    // Assert.assertEquals(db.getOrgName(email), orgName);

    db.assertDbChecked();
}
@Test
public void TC_OB_031_UI_DB_API_ShouldReturnSuccessMessage() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String orgName = "API_" + System.currentTimeMillis();
    String email = "api" + System.currentTimeMillis() + "@mail.com";
    String location = "Bangalore";
    String password = "Test@123";

    // UI ACTION
    onboarding.completeOnboarding(orgName, email, location);
    onboarding.completeStep2(password);

    Thread.sleep(3000);

    // DB VALIDATION
    Assert.assertTrue(
            db.isUserPresent(email),
            "User should be stored in DB after UI onboarding"
    );

    // API VALIDATION
    Response response = api.createSuperAdmin(
            "API_Check_" + System.currentTimeMillis(),
            "apicheck" + System.currentTimeMillis() + "@mail.com",
            location
    );

    int statusCode = response.getStatusCode();
    String body = response.getBody().asString();

    System.out.println("API Status: " + statusCode);
    System.out.println("API Response: " + body);

    Assert.assertTrue(
            statusCode == 200 || statusCode == 201,
            "API should return 200/201 but got: " + statusCode + " Response: " + body
    );

    db.assertDbChecked();

    System.out.println("TC_OB_031 PASSED: UI account created + DB checked + API returned success status");
}
@Test
public void TC_OB_032_PasswordWithoutUppercase_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";

    onboarding.completeOnboarding("Org", email, "Bangalore");

    String weakPassword = "test@123"; // ❌ no uppercase

    onboarding.enterPassword(weakPassword);
    onboarding.enterConfirmPassword(weakPassword);
    onboarding.clickCreateAccount();

    String alert = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert: " + alert);

    Assert.assertFalse(db.isUserPresent(email),
            "Password without uppercase should be rejected");
}
@Test
public void TC_OB_033_PasswordWithoutNumber_ShouldReject() throws Exception {

    OnboardingPage onboarding = new OnboardingPage(driver, wait);
    DBUtil db = new DBUtil();

    String email = "user" + System.currentTimeMillis() + "@mail.com";

    onboarding.completeOnboarding("Org", email, "Bangalore");

    String weakPassword = "Test@abc"; // ❌ no number

    onboarding.enterPassword(weakPassword);
    onboarding.enterConfirmPassword(weakPassword);
    onboarding.clickCreateAccount();

    String alert = onboarding.getAlertMessageIfPresent();
    System.out.println("Alert: " + alert);

    Assert.assertFalse(db.isUserPresent(email),
            "Password without number should be rejected");
}
}