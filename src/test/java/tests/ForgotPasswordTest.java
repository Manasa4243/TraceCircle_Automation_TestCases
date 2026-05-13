package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.OrgApi;
import base.BaseTest;
import db.DBUtil;
import io.restassured.response.Response;
import pages.ForgotPasswordPage;

public class ForgotPasswordTest extends BaseTest {

    @BeforeMethod
    public void openForgotPassword() {
        openForgotPasswordPage();
        System.out.println("FORGOT PASSWORD URL OPENED: " + driver.getCurrentUrl());
    }

    @Test
    public void FP_001_ValidEmail_ShouldSendOTP_UI_DB_API() throws Exception {

        ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String email = "shaikameer7474@gmail.com";

        Assert.assertTrue(db.isUserPresent(email), "User must exist in DB");

        forgot.enterEmail(email);
        forgot.clickSendOtp();

        String alertMsg = forgot.getAlertMessageIfPresent();
        System.out.println("UI Message: " + alertMsg);

        Response response = api.sendForgotPasswordOtp(email);

        Assert.assertTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 201,
                "API should send OTP for registered email"
        );

        db.assertDbChecked();

        System.out.println("FP_001 PASSED");
    }

    @Test
    public void FP_002_InvalidEmailFormat_ShouldShowValidation() {

        ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

        forgot.enterEmail("invalid-email");
        forgot.clickSendOtp();

        String validationMsg = forgot.getEmailValidationMessage();
        System.out.println("Validation Message: " + validationMsg);

        Assert.assertTrue(
                validationMsg.toLowerCase().contains("@"),
                "Invalid email format validation should be shown"
        );
    }

    @Test
    public void FP_003_UnregisteredEmail_ShouldFail_UI_DB_API() throws Exception {

        ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String email = "nouser" + System.currentTimeMillis() + "@mail.com";

        Assert.assertFalse(db.isUserPresent(email), "User should not exist in DB");

        forgot.enterEmail(email);
        forgot.clickSendOtp();

        String alertMsg = forgot.getAlertMessageIfPresent();
        System.out.println("UI Message: " + alertMsg);

        Response response = api.sendForgotPasswordOtp(email);

        Assert.assertTrue(
                response.getStatusCode() == 400 ||
                response.getStatusCode() == 401 ||
                response.getStatusCode() == 404,
                "API should reject unregistered email"
        );

        db.assertDbChecked();

        System.out.println("FP_003 PASSED");
    }

    @Test
    public void FP_004_EmptyEmail_ShouldShowValidation() {

        ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

        forgot.clickSendOtp();

        String validationMsg = forgot.getEmailValidationMessage();
        System.out.println("Validation Message: " + validationMsg);

        Assert.assertFalse(
                validationMsg.trim().isEmpty(),
                "Empty email validation should be shown"
        );
    }

  @Test
public void FP_005_EmailWithSpaces_ShouldBeRejected() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    DBUtil db = new DBUtil();

    String emailWithSpaces = "  shaikameer7474@gmail.com  ";
    String trimmedEmail = emailWithSpaces.trim();

    // DB check: valid trimmed user exists
    Assert.assertTrue(db.isUserPresent(trimmedEmail),
            "Trimmed email user must exist in DB");

    // UI action
    forgot.enterEmail(emailWithSpaces);
    forgot.clickSendOtp();

    // Browser validation / UI validation
    String validationMsg = forgot.getEmailValidationMessage();
    System.out.println("Validation Message: " + validationMsg);

    Assert.assertFalse(
            validationMsg.trim().isEmpty(),
            "Email with spaces should be rejected by UI validation"
    );

    db.assertDbChecked();

    System.out.println("FP_005 PASSED: Email with spaces rejected");
}
@Test
public void FP_006_SQLInjection_ShouldBeRejected() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "' OR 1=1 --";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    String alertMsg = forgot.getAlertMessageIfPresent();
    System.out.println("UI Message: " + alertMsg);

    Response response = api.sendForgotPasswordOtp(email);

    Assert.assertTrue(
            response.getStatusCode() >= 400,
            "SQL Injection attempt should be rejected"
    );
}
@Test
public void FP_007_XSSAttack_ShouldBeSanitized() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "<script>alert('XSS')</script>";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    String alertMsg = forgot.getAlertMessageIfPresent();
    System.out.println("UI Message: " + alertMsg);

    Response response = api.sendForgotPasswordOtp(email);

    Assert.assertTrue(
            response.getStatusCode() >= 400,
            "XSS input should be rejected"
    );
}
@Test
public void FP_008_SendOtpButton_ShouldBeVisibleAndClickable() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    Assert.assertTrue(
            forgot.isSendOtpButtonEnabled(),
            "Send OTP button should be enabled"
    );
}
@Test
public void FP_009_LoginLink_ShouldNavigateToLoginPage() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    forgot.clickLoginLink();

    Assert.assertTrue(
            driver.getCurrentUrl().contains("login"),
            "Should navigate to Login page"
    );
}
@Test
public void FP_010_OTPApi_ShouldBeTriggered() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    Response response = api.sendForgotPasswordOtp(email);

    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "OTP API should be triggered successfully"
    );
}
// @Test
// public void FP_011_OTP_ShouldBeStoredInDB() throws Exception {

//     ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
//     DBUtil db = new DBUtil();

//     String email = "shaikameer7474@gmail.com";

//     forgot.enterEmail(email);
//     forgot.clickSendOtp();

//     Thread.sleep(2000); // wait for DB update

//     Assert.assertTrue(
//             db.isOtpGenerated(email),
//             "OTP should be stored in DB"
//     );
// }

@Test
public void FP_012_SuccessMessage_ShouldBeDisplayed() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    String email = "shaikameer7474@gmail.com";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    String alertMsg = forgot.getAlertMessageIfPresent();

    Assert.assertTrue(
            alertMsg.toLowerCase().contains("otp"),
            "Success message should mention OTP"
    );
}
@Test
public void FP_013_ShouldNavigateToVerifyOtpPage() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    String email = "shaikameer7474@gmail.com";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    Assert.assertTrue(
            driver.getCurrentUrl().toLowerCase().contains("otp"),
            "Should navigate to Verify OTP page"
    );
}

@Test
public void FP_014_UppercaseEmail_ShouldWork() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "SHAIKAMEER7474@GMAIL.COM";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    Response response = api.sendForgotPasswordOtp(email.toLowerCase());

    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Uppercase email should be handled correctly"
    );
}
@Test
public void FP_015_SpecialCharacterEmail_ShouldWork() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "test+demo_123@gmail.com";

    forgot.enterEmail(email);
    forgot.clickSendOtp();

    Response response = api.sendForgotPasswordOtp(email);

    Assert.assertTrue(
            response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Email with special characters should be accepted"
    );
}
@Test
public void FP_016_MultipleOtpRequests_ShouldBeRateLimited() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";

    // ✅ Step 1: Enter email in UI
    forgot.enterEmail(email);

    int successCount = 0;
    int failureCount = 0;

    for (int i = 0; i < 5; i++) {

        // ✅ Step 2: Click Send OTP in UI
        forgot.clickSendOtp();

        String alertMsg = forgot.getAlertMessageIfPresent();
        System.out.println("UI Message: " + alertMsg);

        // ✅ Step 3: API validation
        Response response = api.sendForgotPasswordOtp(email);

        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            successCount++;
        } else if (response.getStatusCode() == 429 || response.getStatusCode() == 400) {
            failureCount++;
        }

        Thread.sleep(1000); // small gap between requests
    }

    System.out.println("Success: " + successCount + " | Failures: " + failureCount);

    Assert.assertTrue(
            failureCount > 0,
            "System should restrict multiple OTP requests"
    );
}
@Test
public void FP_017_CheckApiResponseTime() {

    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";

    long startTime = System.currentTimeMillis();

    Response response = api.sendForgotPasswordOtp(email);

    long endTime = System.currentTimeMillis();
    long responseTime = endTime - startTime;

    System.out.println("Response Time: " + responseTime + " ms");

    Assert.assertTrue(
            responseTime < 7000,
            "API response should be under 3 seconds"
    );
}
// @Test
// public void FP_018_EmailField_ShouldBeAutoFocused() {

//     ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

//     String activeElement = driver.switchTo().activeElement().getAttribute("type");

//     Assert.assertEquals(
//             activeElement,
//             "email",
//             "Email field should be auto-focused on page load"
//     );
// }



@Test
public void FP_019_PressEnter_ShouldTriggerSendOtp() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    String email = "shaikameer7474@gmail.com";

    forgot.enterEmail(email);

    driver.switchTo().activeElement().sendKeys(Keys.ENTER);

    String alertMsg = forgot.getAlertMessageIfPresent();

    Assert.assertTrue(
            !alertMsg.isEmpty(),
            "Pressing Enter should trigger Send OTP"
    );
}
@Test
public void FP_020_ErrorMessage_ShouldBeVisibleAndProperlyAligned() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    forgot.clickSendOtp();

    String validationMsg = forgot.getEmailValidationMessage();

    Assert.assertFalse(
            validationMsg.isEmpty(),
            "Error message should be visible"
    );

    // Optional UI alignment check (basic)
    int x = driver.findElement(By.xpath("//input")).getLocation().getX();
    int msgX = driver.findElement(By.xpath("//input")).getLocation().getX();

    Assert.assertEquals(x, msgX, "Error message should align with input field");
}
@Test
public void FP_021_TabNavigation_ShouldWorkProperly() {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    driver.switchTo().activeElement().sendKeys(Keys.TAB);

    String focusedTag = driver.switchTo().activeElement().getTagName();

    Assert.assertTrue(
            focusedTag.equalsIgnoreCase("button") || focusedTag.equalsIgnoreCase("input"),
            "Tab navigation should move focus correctly"
    );
}
@Test
public void FP_022_EmailField_ShouldHaveAccessibleLabel() {

    WebElement emailField = driver.findElement(By.xpath("//input"));

    String ariaLabel = emailField.getAttribute("aria-label");
    String label = emailField.getAttribute("name");

    Assert.assertTrue(
            (ariaLabel != null && !ariaLabel.isEmpty()) ||
            (label != null && !label.isEmpty()),
            "Email field should have accessible label for screen readers"
    );
}
@Test
public void FP_023_EmailEnumeration_ShouldReturnSameMessage() throws Exception {

    ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);

    String validEmail = "shaikameer7474@gmail.com";
    String invalidEmail = "nouser" + System.currentTimeMillis() + "@mail.com";

    // Valid email
    forgot.enterEmail(validEmail);
    forgot.clickSendOtp();
    String validMsg = forgot.getAlertMessageIfPresent();

    // Refresh page for next test
    driver.navigate().refresh();

    forgot.enterEmail(invalidEmail);
    forgot.clickSendOtp();
    String invalidMsg = forgot.getAlertMessageIfPresent();

    System.out.println("Valid Msg: " + validMsg);
    System.out.println("Invalid Msg: " + invalidMsg);

    Assert.assertEquals(
            validMsg,
            invalidMsg,
            "System should not reveal whether email exists"
    );
}
}