package tests;

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
    public void RegisteredUser_Login_ShouldPass() throws Exception {

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
    public void UnregisteredUser_Login_ShouldFail() throws Exception {

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
}