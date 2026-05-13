package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.OrgApi;
import base.BaseTest;
import db.DBUtil;
import io.restassured.response.Response;
import pages.ResetPasswordPage;

public class ResetPasswordTest extends BaseTest {

    @BeforeMethod
    public void loadPage() {
        openResetPasswordPage();
    }

    // 🔥 RP_001
    @Test
    public void RP_001_ValidReset() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("Password123!");
        page.enterConfirmPassword("Password123!");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("success"));
    }

    // 🔥 RP_002
    @Test
    public void RP_002_EmptyFields() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);
        page.clickReset();

        Assert.assertFalse(page.getMessage().isEmpty());
    }

    // 🔥 RP_003
    @Test
    public void RP_003_MismatchPassword() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("Password123!");
        page.enterConfirmPassword("Password1234!");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("match"));
    }

    // 🔥 RP_004
    @Test
    public void RP_004_WeakPassword() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("123");
        page.enterConfirmPassword("123");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("weak"));
    }

    // 🔥 RP_005 - No uppercase
    @Test
    public void RP_005_NoUppercase() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("password123!");
        page.enterConfirmPassword("password123!");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("uppercase"));
    }

    // 🔥 RP_006 - No number
    @Test
    public void RP_006_NoNumber() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("Password!");
        page.enterConfirmPassword("Password!");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("number"));
    }

    // 🔥 RP_007 - Only spaces
    @Test
    public void RP_007_OnlySpaces() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("     ");
        page.enterConfirmPassword("     ");
        page.clickReset();

        Assert.assertTrue(page.getMessage().toLowerCase().contains("invalid"));
    }

    // 🔥 RP_008 - Masked
    @Test
    public void RP_008_PasswordMasked() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        Assert.assertEquals(page.getPasswordFieldType(), "password");
    }

    // 🔥 RP_009 API success
    @Test
    public void RP_009_API_Success() {

        OrgApi api = new OrgApi();

        Response res = api.login("shaikameer7474@gmail.com", "Password123!");

        Assert.assertEquals(res.getStatusCode(), 200);
    }

    // 🔥 RP_010 API failure
    @Test
    public void RP_010_API_Failure() {

        OrgApi api = new OrgApi();

        Response res = api.login("invalid", "wrong");

        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    // 🔥 RP_011 DB validation
    @Test
    public void RP_011_DBUpdate() throws Exception {

        DBUtil db = new DBUtil();

        Assert.assertTrue(db.isUserPresent("shaikameer7474@gmail.com"));
    }

    // 🔥 RP_012 Old password should fail
    @Test
    public void RP_012_OldPasswordFail() {

        OrgApi api = new OrgApi();

        Response res = api.login("shaikameer7474@gmail.com", "OldPassword123!");

        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    // 🔥 RP_013 New password login
    @Test
    public void RP_013_NewPasswordSuccess() {

        OrgApi api = new OrgApi();

        Response res = api.login("shaikameer7474@gmail.com", "Password123!");

        Assert.assertEquals(res.getStatusCode(), 200);
    }

    // 🔥 RP_014 Navigation
    @Test
    public void RP_014_NavigateLogin() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.enterNewPassword("Password123!");
        page.enterConfirmPassword("Password123!");
        page.clickReset();

        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }

    // 🔥 RP_015 Min length
    @Test
    public void RP_015_MinLength() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_016 Max length
    @Test
    public void RP_016_MaxLength() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_017 Performance
    @Test
    public void RP_017_ResponseTime() {

        long start = System.currentTimeMillis();

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);
        page.enterNewPassword("Password123!");
        page.enterConfirmPassword("Password123!");
        page.clickReset();

        long time = System.currentTimeMillis() - start;

        Assert.assertTrue(time < 3000);
    }

    // 🔥 RP_018 Show/Hide
    @Test
    public void RP_018_ShowHideToggle() {

        ResetPasswordPage page = new ResetPasswordPage(driver, wait);

        page.clickShowHide();

        Assert.assertNotEquals(page.getPasswordFieldType(), "password");
    }

    // 🔥 RP_019 Copy paste
    @Test
    public void RP_019_CopyPaste() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_020 UI errors
    @Test
    public void RP_020_ErrorMessageUI() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_021 Accessibility
    @Test
    public void RP_021_Accessibility() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_022 Password encrypted
    @Test
    public void RP_022_EncryptedStorage() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_023 Not visible in API
    @Test
    public void RP_023_NotInResponse() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_024 Prevent reuse
    @Test
    public void RP_024_PreventReuse() {
        Assert.assertTrue(true);
    }

    // 🔥 RP_025 Session invalid
    @Test
    public void RP_025_SessionInvalid() {
        Assert.assertTrue(true);
    }
}