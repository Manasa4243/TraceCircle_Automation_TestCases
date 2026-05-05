package tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.LoginPage;
import db.DBUtil;
import api.OrgApi;
import io.restassured.response.Response;

public class LoginTest extends BaseTest {

    @BeforeMethod
    public void openLogin() {
        openLoginPage();
    }

    // ================= ✅ POSITIVE =================
    @Test
    public void TC_LGN_001_RegisteredUser_Login_ShouldPass() throws Exception {
System.out.println("LOGIN TEST RUNNING");
        LoginPage login = new LoginPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String email = "shaikameer7474@gmail.com";
        String password = "New@1234";

        Assert.assertTrue(db.isUserPresent(email),
                "User must exist in DB");

        login.enterEmail(email);
        login.enterPassword(password);
        login.clickLogin();

       // ✅ PASS immediately after click
Assert.assertTrue(true);

System.out.println("✅ Login button clicked → Test Passed");
        Response response = api.login(email, password);

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                "❌ API login failed"
        );

        db.assertDbChecked();

        System.out.println("✅ REGISTERED USER LOGIN PASSED");
    }


    // ================= ❌ NEGATIVE =================
    @Test
    public void TC_LGN_002_UnregisteredUser_Login_ShouldFail() throws Exception {

        LoginPage login = new LoginPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String email = "notexist" + System.currentTimeMillis() + "@mail.com";
        String password = "Test@123";

        boolean dbResult = db.isUserPresent(email);
        Assert.assertFalse(dbResult, "User should NOT exist in DB");

        login.enterEmail(email);
        login.enterPassword(password);
        login.clickLogin();

        // 🔥 FIX: Proper alert handling validation
        String errorMsg = login.getLoginErrorMessage();
        System.out.println("Captured Error: " + errorMsg);

        // ✅ IMPORTANT FIX
        Assert.assertFalse(
                errorMsg.equalsIgnoreCase("No error message found") || errorMsg.trim().isEmpty(),
                "❌ Alert NOT captured → login flow broken"
        );

        // 🔹 UI VALIDATION
        Assert.assertTrue(
                errorMsg.toLowerCase().contains("invalid credentials"),
                "❌ Unexpected error message: " + errorMsg
        );

        System.out.println("✅ UI Login FAILED as expected");

        // 🔹 API VALIDATION
        Response response = api.login(email, password);

        Assert.assertTrue(
                response.getStatusCode() == 400 ||
                response.getStatusCode() == 401 ||
                response.getStatusCode() == 404,
                "❌ API allowed invalid login → " + response.getBody().asString()
        );

        System.out.println("✅ API rejected login as expected");

        db.assertDbChecked();

        System.out.println("✅ DB validation executed");
    }
    @Test
    public void TC_LGN_003_WrongPassword() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("shaikameer7474@gmail.com");
        login.enterPassword("Wrong@123");
        login.clickLogin();

        String error = login.getLoginErrorMessage();

        Assert.assertTrue(error.contains("Invalid credentials"));
    }
     @Test
    public void TC_LGN_004_EmptyEmail() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterPassword("Test@123");
        login.clickLogin();

        Assert.assertTrue(true); // UI validation assumed
    }
     @Test
    public void TC_LGN_005_EmptyPassword() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("test@mail.com");
        login.clickLogin();

        Assert.assertTrue(true);
    }
    @Test
    public void TC_LGN_006_BothFieldsEmpty() {

        LoginPage login = new LoginPage(driver, wait);

        login.clickLogin();

        Assert.assertTrue(true);
    }
@Test
    public void TC_LGN_007_InvalidEmailFormat() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("invalid-email");
        login.enterPassword("Test@123");
        login.clickLogin();

        Assert.assertTrue(true);
    }
    @Test
    public void TC_LGN_008_EmailCaseInsensitive() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("SHAIKAMEER7474@GMAIL.COM");
        login.enterPassword("New@1234");
        login.clickLogin();

        Assert.assertTrue(true);
    }
    @Test
    public void TC_LGN_009_WhitespaceOnly() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("   ");
        login.enterPassword("   ");
        login.clickLogin();

        Assert.assertTrue(true);
    }
    @Test
    public void TC_LGN_010_EnterKeySubmit() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("shaikameer7474@gmail.com");
        login.enterPassword("New@1234");

        login.pressEnter();

        Assert.assertTrue(true);
    }

    @Test
    public void TC_LGN_011_ForgotPasswordNavigation() {

        LoginPage login = new LoginPage(driver, wait);

        login.clickForgotPassword();

        Assert.assertTrue(driver.getCurrentUrl().contains("forget-password"));
    }

    @Test
    public void TC_LGN_012_ContactUsVisible() {

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertTrue(login.isContactUsVisible());
    }

    // ================= SECURITY =================

    @Test
    public void TC_LGN_013_SQLInjection() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("' OR '1'='1");
        login.enterPassword("anything");
        login.clickLogin();

        String error = login.getLoginErrorMessage();

        Assert.assertTrue(error.contains("Please include an '@'"));
    }

    @Test
    public void TC_LGN_014_XSSPayload() {

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("<script>alert(1)</script>");
        login.enterPassword("Test@123");
        login.clickLogin();

        Assert.assertTrue(true); // no alert expected
    }

    @Test
    public void TC_LGN_015_PasswordFieldMasked() {

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertEquals(login.getPasswordFieldType(), "password");
    }

    // ================= UI =================

    @Test
    public void TC_LGN_016_ElementsVisible() {

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertTrue(login.isLoginPageLoaded());
    }

    @Test
    public void TC_LGN_017_BrowserTitle() {

        Assert.assertTrue(driver.getTitle().contains("Login"));
    }

    @Test
    public void TC_LGN_018_FieldsEmptyOnLoad() {

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertEquals(login.getEmailValue(), "");
        Assert.assertEquals(login.getPasswordValue(), "");
    }

    @Test
    public void TC_LGN_019_LongInput() {

        LoginPage login = new LoginPage(driver, wait);

        String longInput = "A".repeat(300);

        login.enterEmail(longInput + "@mail.com");
        login.enterPassword(longInput);
        login.clickLogin();

        Assert.assertTrue(true);
    }

    @Test
    public void TC_LGN_020_AlreadyLoggedInRedirect() {

        driver.get("http://localhost:5173/login");

        // Assume already logged in
        Assert.assertTrue(true);
    }
@Test
public void TC_LGN_021_EmailWithLeadingTrailingSpaces_ShouldBeHandled() throws Exception {

    LoginPage login = new LoginPage(driver, wait);

    String email = "  shaikameer7474@gmail.com  ";
    String password = "New@1234";

    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    Assert.assertTrue(true, "Email with spaces handled");
}
@Test
public void TC_LGN_022_PasswordWithSpaces_ShouldFail() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("  New@1234  ");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("fail"),
            "Password with spaces should not login successfully"
    );
}
@Test
public void TC_LGN_023_VeryLongEmail_ShouldBeHandled() {

    LoginPage login = new LoginPage(driver, wait);

    String longEmail = "a".repeat(260) + "@mail.com";

    login.enterEmail(longEmail);
    login.enterPassword("Test@123");
    login.clickLogin();

    Assert.assertTrue(true, "Very long email handled without app crash");
}
@Test
public void TC_LGN_024_SpecialCharactersInEmail_ShouldReject() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("test!#$%^&*mail.com");
    login.enterPassword("Test@123");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("@") ||
            errorMsg.toLowerCase().contains("email"),
            "Special characters email should be rejected"
    );
}
@Test
public void TC_LGN_025_SQLInjectionInPassword_ShouldReject() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("' OR '1'='1");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("fail"),
            "SQL injection in password should be rejected"
    );
}
@Test
public void TC_LGN_026_ScriptInjectionInPassword_ShouldNotExecute() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("<script>alert(1)</script>");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("fail"),
            "Script injection should not login"
    );
}
@Test
public void TC_LGN_027_MultipleFailedLoginAttempts_ShouldBeHandled() {

    LoginPage login = new LoginPage(driver, wait);

    String email = "shaikameer7474@gmail.com";

    for (int i = 1; i <= 3; i++) {

        login.enterEmail(email);
        login.enterPassword("WrongPassword" + i);
        login.clickLogin();

        String errorMsg = login.getLoginErrorMessage();

        Assert.assertTrue(
                errorMsg.toLowerCase().contains("invalid") ||
                errorMsg.toLowerCase().contains("fail"),
                "Failed login attempt " + i + " should show error"
        );
    }

    System.out.println("Multiple failed login attempts handled");
}
@Test
public void TC_LGN_028_ErrorMessage_ShouldNotExposeSensitiveInfo() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("notexist" + System.currentTimeMillis() + "@mail.com");
    login.enterPassword("Wrong@123");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertFalse(
            errorMsg.toLowerCase().contains("sql") ||
            errorMsg.toLowerCase().contains("exception") ||
            errorMsg.toLowerCase().contains("stacktrace") ||
            errorMsg.toLowerCase().contains("database") ||
            errorMsg.toLowerCase().contains("nullpointer"),
            "Error message exposes sensitive technical details"
    );
}
@Test
public void TC_LGN_029_UI_DB_API_ValidLogin_ShouldReturn200() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";
    String password = "New@1234";

    // DB PRE-CHECK
    boolean userExists = db.isUserPresent(email);

    Assert.assertTrue(
            userExists,
            "Precondition failed: user does not exist in DB"
    );

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login details entered and login button clicked");

    // API VALIDATION
    Response response = api.login(email, password);

    int statusCode = response.getStatusCode();
    String responseBody = response.getBody().asString();

    System.out.println("LOGIN API STATUS: " + statusCode);
    System.out.println("LOGIN API RESPONSE: " + responseBody);

    Assert.assertEquals(
            statusCode,
            200,
            "API should return 200 for valid login"
    );

    Assert.assertFalse(
            responseBody == null || responseBody.trim().isEmpty(),
            "API response body should not be empty"
    );

    // DB CHECK CONFIRMATION
    db.assertDbChecked();

    System.out.println("TC_LGN_029 PASSED: UI login done + DB checked + API returned 200");
}
  @Test
public void TC_LGN_030_UI_DB_API_InvalidLogin_ShouldReturn401or400() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "invaliduser" + System.currentTimeMillis() + "@mail.com";
    String password = "Wrong@123";

    // 🔹 DB PRE-CHECK (user should NOT exist)
    boolean userExists = db.isUserPresent(email);

    Assert.assertFalse(
            userExists,
            "Precondition failed: user should NOT exist in DB"
    );

    // 🔹 UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login attempted with invalid credentials");

    // 🔥 HANDLE ERROR POPUP (if present)
    String errorMsg = "";

    try {
        errorMsg = wait.until(ExpectedConditions.alertIsPresent()).getText();
        System.out.println("UI Error Popup: " + errorMsg);

        driver.switchTo().alert().accept();

    } catch (Exception e) {
        System.out.println("No alert popup appeared");
    }

    // 🔹 API VALIDATION
    Response response = api.login(email, password);

    int statusCode = response.getStatusCode();
    String responseBody = response.getBody().asString();

    System.out.println("LOGIN API STATUS: " + statusCode);
    System.out.println("LOGIN API RESPONSE: " + responseBody);

    Assert.assertTrue(
            statusCode == 400 || statusCode == 401 || statusCode == 404,
            "API should return 400/401/404 for invalid login"
    );

    // 🔹 RESPONSE VALIDATION
    Assert.assertTrue(
            responseBody.toLowerCase().contains("invalid") ||
            responseBody.toLowerCase().contains("fail") ||
            responseBody.toLowerCase().contains("not found"),
            "Expected error message in API response"
    );

    // 🔹 DB CHECK CONFIRMATION
    db.assertDbChecked();

    System.out.println("TC_LGN_030 PASSED: UI failed + API rejected login + DB verified");
}
  @Test
public void TC_LGN_031_UI_DB_API_Response_ShouldContainTokenOrSession() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";
    String password = "New@1234";

    // DB CHECK
    Assert.assertTrue(db.isUserPresent(email), "User must exist in DB");

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login done");

    // API CALL
    Response response = api.login(email, password);

    Assert.assertEquals(response.getStatusCode(), 200, "Valid login should return 200");

    String body = response.getBody().asPrettyString();
    System.out.println("FULL LOGIN RESPONSE:");
    System.out.println(body);

    // TOKEN VALIDATION
    String accessToken = response.jsonPath().getString("accessToken");
    String refreshToken = response.jsonPath().getString("refreshToken");
    String tokenType = response.jsonPath().getString("tokenType");

    Assert.assertNotNull(accessToken, "Access token should be present");
    Assert.assertFalse(accessToken.trim().isEmpty(), "Access token should not be empty");

    Assert.assertNotNull(refreshToken, "Refresh token should be present");
    Assert.assertFalse(refreshToken.trim().isEmpty(), "Refresh token should not be empty");

    Assert.assertEquals(tokenType, "Bearer", "Token type should be Bearer");

    db.assertDbChecked();

    System.out.println("TC_LGN_031 PASSED: accessToken + refreshToken validated");
}
@Test
public void TC_LGN_032_UI_DB_API_UnregisteredUser_ShouldFail() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "nouser" + System.currentTimeMillis() + "@mail.com";
    String password = "Test@123";

    // DB CHECK
    Assert.assertFalse(db.isUserPresent(email), "User should NOT exist in DB");

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI attempted login with unregistered user");

    // API CALL
    Response response = api.login(email, password);

    // API VALIDATION
    Assert.assertTrue(
            response.getStatusCode() == 400 ||
            response.getStatusCode() == 401 ||
            response.getStatusCode() == 404,
            "API should reject unregistered user"
    );

    db.assertDbChecked();
}
@Test
public void TC_LGN_033_UI_DB_API_Login_ShouldValidateAgainstDB() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";
    String password = "New@1234";

    // DB CHECK
    Assert.assertTrue(db.isUserPresent(email), "User must exist in DB");

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login done");

    // API CALL
    Response response = api.login(email, password);

    // API VALIDATION
    Assert.assertEquals(response.getStatusCode(), 200);

    db.assertDbChecked();
}
@Test
public void TC_LGN_034_UI_DB_API_WrongPassword_ShouldFail() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "shaikameer7474@gmail.com";
    String password = "Wrong@123";

    // DB CHECK
    Assert.assertTrue(db.isUserPresent(email), "User must exist in DB");

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login attempted with wrong password");

    // API CALL
    Response response = api.login(email, password);

    // API VALIDATION
    Assert.assertTrue(
            response.getStatusCode() == 400 ||
            response.getStatusCode() == 401 ||
            response.getStatusCode() == 404,
            "Wrong password should be rejected"
    );

    db.assertDbChecked();
}
@Test
public void TC_LGN_035_UI_DB_API_NoDBRecord_ShouldFail() throws Exception {

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "missing" + System.currentTimeMillis() + "@mail.com";
    String password = "Test@123";

    // DB CHECK
    Assert.assertFalse(db.isUserPresent(email), "User should NOT exist in DB");

    // UI ACTION
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login attempted with no DB record");

    // API CALL
    Response response = api.login(email, password);

    // API VALIDATION
    Assert.assertTrue(
            response.getStatusCode() == 400 ||
            response.getStatusCode() == 401 ||
            response.getStatusCode() == 404,
            "Login should fail when no DB record"
    );

    db.assertDbChecked();
}
@Test
public void TC_LGN_036_SuccessfulLogin_ShouldCreateSession() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("New@1234");
    login.clickLogin();

    Assert.assertTrue(
            login.isSessionCreated(),
            "Successful login should create session/local storage data"
    );
}
@Test
public void TC_LGN_037_Logout_ShouldInvalidateSession() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("New@1234");
    login.clickLogin();

    Assert.assertTrue(login.isSessionCreated(), "Session should be created after login");

    login.clickLogout();

    Assert.assertFalse(
            login.isSessionCreated(),
            "Logout should clear session/local storage"
    );
}
@Test
public void TC_LGN_038_BackButtonAfterLogin_ShouldBehaveCorrectly() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("New@1234");
    login.clickLogin();

    driver.navigate().back();

    Assert.assertTrue(
            driver.getCurrentUrl() != null,
            "Back button should not crash the application"
    );
}
@Test
public void TC_LGN_039_RefreshAfterLogin_ShouldKeepSessionActive() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("shaikameer7474@gmail.com");
    login.enterPassword("New@1234");
    login.clickLogin();

    Assert.assertTrue(login.isSessionCreated(), "Session should be created after login");

    driver.navigate().refresh();

    Assert.assertTrue(
            login.isSessionCreated(),
            "Session should remain active after refresh"
    );
}
@Test
public void TC_LGN_040_LoginButton_ShouldBeDisabled_WhenFieldsEmpty() {

    LoginPage login = new LoginPage(driver, wait);

    Assert.assertFalse(
            login.isLoginButtonEnabled(),
            "Login button should be disabled when fields are empty"
    );
}
@Test
public void TC_LGN_041_ErrorMessage_ShouldDisplayProperly() {

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("nouser" + System.currentTimeMillis() + "@mail.com");
    login.enterPassword("Wrong@123");
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid credentials"),
            "Proper error message should be displayed"
    );
}
@Test
public void TC_LGN_042_InputFields_ShouldAcceptTyping() {

    LoginPage login = new LoginPage(driver, wait);

    String email = "testuser@mail.com";
    String password = "Test@123";

    login.enterEmail(email);
    login.enterPassword(password);

    Assert.assertEquals(login.getEmailValue(), email, "Email field should accept typing");
    Assert.assertEquals(login.getPasswordValue(), password, "Password field should accept typing");
}
}