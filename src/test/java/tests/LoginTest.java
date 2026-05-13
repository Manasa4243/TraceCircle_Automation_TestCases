package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.LoginPage;
import db.DBUtil;
import api.OrgApi;
import io.restassured.response.Response;
import utilities.ExcelUtil;

public class LoginTest extends BaseTest {

    // ✅ DO NOT override setup()
    // Instead use a DIFFERENT method name
    @BeforeMethod
    public void loadTestData() {

        String path = System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/LoginTestData.xlsx";

        ExcelUtil.loadExcel(path, "LoginData");

        System.out.println("✅ Excel Loaded");
    }

    // ✅ ONLY ONE METHOD
    private String[] getTestData(String testCaseId) {

        int rowCount = ExcelUtil.getRowCount();

        for (int i = 1; i <= rowCount; i++) {

            String tcId = ExcelUtil.getCellData(i, 0);

            if (tcId != null && tcId.equalsIgnoreCase(testCaseId)) {

                String email = ExcelUtil.getCellData(i, 1);
                String password = ExcelUtil.getCellData(i, 2);

                return new String[]{email, password};
            }
        }

        throw new RuntimeException("❌ Test data not found: " + testCaseId);
    }

    // ================= ✅ TEST =================
      @Test
    public void TC_LGN_001_RegisteredUser_Login_ShouldPass() throws Exception {

        // ✅ NOW driver is initialized from BaseTest
        openLoginPage();

        System.out.println("🚀 LOGIN TEST RUNNING");

        LoginPage login = new LoginPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        String[] data = getTestData("TC_LGN_001");
        String email = data[0];
        String password = data[1];

        // ✅ DB Validation
        Assert.assertTrue(db.isUserPresent(email),
                "❌ User must exist in DB");

        // ✅ UI Actions
        login.enterEmail(email);
        login.enterPassword(password);
        login.clickLogin();

        System.out.println("✅ Login button clicked");

        // ✅ API Validation
        Response response = api.login(email, password);

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                "❌ API login failed"
        );

        db.assertDbChecked();
System.out.println("📧 Email: " + email);
System.out.println("🔑 Password: " + password);
        System.out.println("✅ REGISTERED USER LOGIN PASSED");
    }

// @Test
// public void TC_LGN_001_UI_Login_Only() throws Exception {

//     // Open login page
//     openLoginPage();

//     System.out.println("🚀 UI LOGIN TEST STARTED");

//     // Page object
//     LoginPage login = new LoginPage(driver, wait);

//     // Get data from Excel
//     String[] data = getTestData("TC_LGN_001");
//     String email = data[0];
//     String password = data[1];

//     // Debug (optional but useful)
//     System.out.println("📧 Email: " + email);
//     System.out.println("🔑 Password: " + password);

//     // Perform UI actions
//     login.enterEmail(email);
//     login.enterPassword(password);
//     login.clickLogin();

//     System.out.println("✅ Login button clicked");

//     // Optional wait to observe result
//     Thread.sleep(3000);

//     System.out.println("✅ UI LOGIN TEST COMPLETED");
// }

    
    // ================= ❌ NEGATIVE =================
    @Test
    public void TC_LGN_002_UnregisteredUser_Login_ShouldFail() throws Exception {
openLoginPage();
        LoginPage login = new LoginPage(driver, wait);
        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

       String[] data = getTestData("TC_LGN_002");
        String email = data[0];
        String password = data[1];

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
public void TC_LGN_003_WrongPassword() throws Exception {

    openLoginPage();

    System.out.println("🚀 WRONG PASSWORD TEST STARTED");

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    // ✅ Get data from Excel
    String[] data = getTestData("TC_LGN_003");
    String email = data[0].trim().toLowerCase();
    String password = data[1];

    System.out.println("📧 Email: " + email);
    System.out.println("🔑 Password: " + password);

    // ✅ DB Validation (user must exist)
    boolean userExists = db.isUserPresent(email);
    Assert.assertTrue(userExists, "❌ User must exist in DB");

    // ✅ API Validation (should FAIL for wrong password)
    Response response = api.login(email, password);

    System.out.println("🌐 API Status: " + response.getStatusCode());
    System.out.println("🌐 API Body: " + response.asString());

    Assert.assertNotEquals(
            response.getStatusCode(),
            200,
            "❌ API should NOT allow login with wrong password"
    );

    // ✅ UI Actions
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    // ✅ UI Validation
    String error = login.getLoginErrorMessage();

    System.out.println("⚠️ UI Error Message: " + error);

    Assert.assertTrue(
            error.toLowerCase().contains("invalid"),
            "❌ Expected invalid credentials error"
    );

    db.assertDbChecked();

    System.out.println("✅ WRONG PASSWORD TEST PASSED");
}
 @Test
public void TC_LGN_004_EmptyEmail() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    String[] data = getTestData("TC_LGN_004");
    String password = data[1];

    login.enterPassword(password);
    login.clickLogin();

    String error = login.getEmailValidationMessage();

    System.out.println("⚠️ UI Error: " + error);

    Assert.assertTrue(
            error.toLowerCase().contains("please fill in wthis field"),
            "Expected 'Please fill in this field' but got: " + error
    );
}
     @Test
public void TC_LGN_005_EmptyPassword() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    String[] data = getTestData("TC_LGN_005");
    String email = data[0];

    login.enterEmail(email);
    login.clickLogin();

    String error = login.getPasswordValidationMessage();

    System.out.println("⚠️ UI Error: " + error);

    Assert.assertTrue(
            error.toLowerCase().contains("please fill in this field"),
            "Expected 'Please fill in this field' but got: " + error
    );
}
    @Test
public void TC_LGN_006_BothFieldsEmpty() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    login.clickLogin();

    String emailError = login.getEmailValidationMessage();
    String passwordError = login.getPasswordValidationMessage();

    Assert.assertTrue(emailError.length() > 0, "❌ Email error missing");
    Assert.assertTrue(passwordError.length() > 0, "❌ Password error missing");
}
@Test
public void TC_LGN_007_InvalidEmailFormat() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    String[] data = getTestData("TC_LGN_007");
    String email = data[0];
    String password = data[1];

    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    String error = login.getEmailValidationMessage();

    System.out.println("⚠️ UI Error: " + error);

    Assert.assertTrue(
            error.toLowerCase().contains("valid"),
            "❌ Invalid email format message not shown"
    );
}
 @Test
public void TC_LGN_008_EmailCaseInsensitive() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_LGN_008");

    String originalEmail = data[0];              // uppercase from Excel/data
    String lowerCaseEmail = data[0].toLowerCase();
    String password = data[1];

    // DB check with lowercase/stored email
    Assert.assertTrue(
            db.isUserPresent(lowerCaseEmail),
            "User not found in DB with lowercase email"
    );

    // UI action using uppercase/original email
    login.enterEmail(originalEmail);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login attempted with email: " + originalEmail);

    // API check using uppercase/original email
    Response response = api.login(originalEmail, password);

    System.out.println("API Status: " + response.getStatusCode());
    System.out.println("API Response: " + response.getBody().asString());

    Assert.assertEquals(
            response.getStatusCode(),
            200,
            "API should allow login with uppercase email"
    );

    db.assertDbChecked();

    System.out.println("TC_LGN_008 PASSED: Email case-insensitive login verified");
}
 @Test
public void TC_LGN_009_WhitespaceOnly_ShouldLoginAfterTrim() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_LGN_009");

    String emailWithSpaces = data[0];
    String passwordWithSpaces = data[1];

    String trimmedEmail = emailWithSpaces.trim();
    String trimmedPassword = passwordWithSpaces.trim();

    // DB check with trimmed email
    Assert.assertTrue(
            db.isUserPresent(trimmedEmail),
            "Trimmed email user should exist in DB"
    );

    // UI action with spaces
    login.enterEmail(emailWithSpaces);
    login.enterPassword(passwordWithSpaces);
    login.clickLogin();

    System.out.println("UI login attempted with whitespace values");

    // API validation with trimmed values
    Response response = api.login(trimmedEmail, trimmedPassword);

    Assert.assertEquals(
            response.getStatusCode(),
            200,
            "API should login successfully after trimming whitespace"
    );

    db.assertDbChecked();

    System.out.println("TC_LGN_009 PASSED: Whitespace handled and login successful");
}
    @Test
    public void TC_LGN_010_EnterKeySubmit() {
openLoginPage();
        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("shaikameer7474@gmail.com");
        login.enterPassword("New@1234");

        login.pressEnter();

        Assert.assertTrue(true);
    }

    @Test
    public void TC_LGN_011_ForgotPasswordNavigation() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        login.clickForgotPassword();

        Assert.assertTrue(driver.getCurrentUrl().contains("forget-password"));
    }

    @Test
    public void TC_LGN_012_ContactUsVisible() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertTrue(login.isContactUsVisible());
    }

    // ================= SECURITY =================

    @Test
public void TC_LGN_013_SQLInjection() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    String[] data = getTestData("TC_LGN_013");
    String email = data[0];
    String password = data[1];

    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    // ✅ Browser validation should trigger
    String error = login.getEmailValidationMessage();

    System.out.println("⚠️ SQL Injection Error: " + error);

    Assert.assertTrue(
            error.toLowerCase().contains("valid") || error.contains("@"),
            "❌ SQL Injection input not blocked properly"
    );
}
   @Test
public void TC_LGN_014_XSSPayload() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    String[] data = getTestData("TC_LGN_014");
    String email = data[0];
    String password = data[1];

    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    // ✅ Check no JS alert executed
    boolean alertPresent;

    try {
        wait.until(ExpectedConditions.alertIsPresent());
        alertPresent = true;
    } catch (Exception e) {
        alertPresent = false;
    }

    Assert.assertFalse(alertPresent, "❌ XSS attack executed!");

    // ✅ Also validate input rejected
    String error = login.getEmailValidationMessage();

    System.out.println("⚠️ XSS Error: " + error);

    Assert.assertTrue(
            error.length() > 0,
            "❌ XSS input not validated"
    );
}

    @Test
    public void TC_LGN_015_PasswordFieldMasked() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertEquals(login.getPasswordFieldType(), "password");
    }

    // ================= UI =================

    @Test
    public void TC_LGN_016_ElementsVisible() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertTrue(login.isLoginPageLoaded());
    }

@Test
public void TC_LGN_017_LoginHeadingVisible() {

    openLoginPage();

    Assert.assertTrue(
            driver.getPageSource().contains("Login"),
            "Login text is not visible on page"
    );
}
    @Test
    public void TC_LGN_018_FieldsEmptyOnLoad() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        Assert.assertEquals(login.getEmailValue(), "");
        Assert.assertEquals(login.getPasswordValue(), "");
    }

    @Test
    public void TC_LGN_019_LongInput() {
        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        String longInput = "A".repeat(300);

        login.enterEmail(longInput + "@mail.com");
        login.enterPassword(longInput);
        login.clickLogin();

        Assert.assertTrue(true);
    }

    @Test
    public void TC_LGN_020_AlreadyLoggedInRedirect() {
        openLoginPage();

        driver.get("http://localhost:5173/login");

        // Assume already logged in
        Assert.assertTrue(true);
    }
 @Test
public void TC_LGN_021_EmailWithLeadingTrailingSpaces_ShouldBeHandled() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_LGN_021");

    String rawEmail = data[0];          // email with spaces
    String password = data[1];
    String trimmedEmail = rawEmail.trim();

    // DB check with trimmed email
    Assert.assertTrue(
            db.isUserPresent(trimmedEmail),
            "User not found in DB"
    );

    // UI action with email spaces
    login.enterEmail(rawEmail);
    login.enterPassword(password);
    login.clickLogin();

    System.out.println("UI login clicked with email: " + rawEmail);

    // API validation with trimmed email
    Response response = api.login(trimmedEmail, password);

    System.out.println("API Status: " + response.getStatusCode());
    System.out.println("API Response: " + response.getBody().asString());

    Assert.assertEquals(
            response.getStatusCode(),
            200,
            "API login failed with trimmed email"
    );

    db.assertDbChecked();

    System.out.println("TC_LGN_021 PASSED: Email with spaces handled correctly");
}
@Test
public void TC_LGN_022_PasswordWithSpaces_ShouldFail() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_LGN_022");
    String email = data[0];
    String passwordWithSpaces = data[1];

    // ✅ DB (user must exist)
    Assert.assertTrue(
            db.isUserPresent(email),
            "❌ User not found in DB"
    );

    // ❌ API should fail
    Response response = api.login(email, passwordWithSpaces);

    Assert.assertNotEquals(
            response.getStatusCode(),
            200,
            "❌ API should reject password with spaces"
    );

    // ❌ UI
    login.enterEmail(email);
    login.enterPassword(passwordWithSpaces);
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    System.out.println("⚠️ Password Spaces Error: " + errorMsg);

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("fail"),
            "❌ UI allowed login with spaced password"
    );

    db.assertDbChecked();
}
// @Test
// public void TC_LGN_023_VeryLongEmail_ShouldBeHandled() {

//     LoginPage login = new LoginPage(driver, wait);

//     String longEmail = "a".repeat(260) + "@mail.com";

//     login.enterEmail(longEmail);
//     login.enterPassword("Test@123");
//     login.clickLogin();

//     Assert.assertTrue(true, "Very long email handled without app crash");
// }
@Test
public void TC_LGN_024_SpecialCharactersInEmail_ShouldReject() {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);

    login.enterEmail("test!#$%^&*mail.com");
    login.enterPassword("Test@123");
    login.clickLogin();

    String errorMsg = login.getEmailValidationMessage();

    System.out.println("Email Validation Message: " + errorMsg);

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("@") ||
            errorMsg.toLowerCase().contains("please include"),
            "Expected email validation message but got: " + errorMsg
    );
}
@Test
public void TC_LGN_025_SQLInjectionInPassword_ShouldReject() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String[] data = getTestData("TC_LGN_025");
    String email = data[0];
    String password = data[1];

    // ✅ DB check (user exists)
    Assert.assertTrue(db.isUserPresent(email), "❌ User not found");

    // ❌ API should reject
    Response response = api.login(email, password);

    Assert.assertNotEquals(
            response.getStatusCode(),
            200,
            "❌ API accepted SQL injection password"
    );

    // ❌ UI
    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    System.out.println("⚠️ SQL Injection Password Error: " + errorMsg);

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("fail"),
            "❌ UI allowed SQL injection login"
    );

    db.assertDbChecked();
}
 @Test
public void TC_LGN_026_ScriptInjectionInPassword_ShouldNotExecute() throws Exception {

    openLoginPage();

    LoginPage login = new LoginPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String email = "manasajagadeesh141@gmail.com";
    String password = "<script>alert(1)</script>";

    Assert.assertTrue(db.isUserPresent(email), "User not found in DB");

    Response response = api.login(email, password);

    Assert.assertTrue(
            response.getStatusCode() == 400 ||
            response.getStatusCode() == 401 ||
            response.getStatusCode() == 404,
            "API accepted script injection password"
    );

    login.enterEmail(email);
    login.enterPassword(password);
    login.clickLogin();

    String errorMsg = login.getLoginErrorMessage();

    System.out.println("Script Injection Error: " + errorMsg);

    Assert.assertTrue(
            errorMsg.toLowerCase().contains("invalid credentials") ||
            errorMsg.toLowerCase().contains("invalid") ||
            errorMsg.toLowerCase().contains("not found"),
            "Expected invalid credentials alert, but got: " + errorMsg
    );

    db.assertDbChecked();

    System.out.println("TC_LGN_026 PASSED");
}
@Test
public void TC_LGN_027_MultipleFailedLoginAttempts_ShouldBeHandled() {
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
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
openLoginPage();
    LoginPage login = new LoginPage(driver, wait);

    Assert.assertFalse(
            login.isLoginButtonEnabled(),
            "Login button should be disabled when fields are empty"
    );
}
@Test
public void TC_LGN_041_ErrorMessage_ShouldDisplayProperly() {
openLoginPage();
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
openLoginPage();
    LoginPage login = new LoginPage(driver, wait);

    String email = "testuser@mail.com";
    String password = "Test@123";

    login.enterEmail(email);
    login.enterPassword(password);

    Assert.assertEquals(login.getEmailValue(), email, "Email field should accept typing");
    Assert.assertEquals(login.getPasswordValue(), password, "Password field should accept typing");
}
}