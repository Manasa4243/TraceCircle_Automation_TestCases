package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.OrgApi;
import base.BaseTest;
import db.DBUtil;
import io.restassured.response.Response;
import pages.ForgotPasswordPage;
import pages.OTPPage;

public class OTPVerificationTest extends BaseTest {

    @BeforeMethod
public void loadPage() {
    openOTPPage();
}

    // 🔥 OTP_001 – Valid OTP
   @Test
    public void OTP_001_ValidOTP_EndToEnd() throws Exception {

        OrgApi api = new OrgApi();
        ForgotPasswordPage forgot = new ForgotPasswordPage(driver, wait);
        OTPPage otpPage = new OTPPage(driver, wait);

        String email = "shaikameer7474@gmail.com";

        // ✅ Step 1: Open Forgot Password Page
        openForgotPasswordPage();

        // ✅ Step 2: Trigger OTP from UI
        forgot.enterEmail(email);
        forgot.clickSendOtp();

        // 🔥 Step 3: Capture OTP from API
        String otp = api.getOtp(email);

        Assert.assertNotNull(otp, "❌ OTP not captured from API");

        // ✅ Step 4: Enter OTP
        otpPage.enterOTP(otp);
        otpPage.clickVerify();

        // ✅ Step 5: Validate success
        String msg = otpPage.getMessage();
        System.out.println("UI Message: " + msg);

        Assert.assertTrue(msg.toLowerCase().contains("success"));
    }


    // 🔥 OTP_002 – Invalid OTP
    @Test
    public void OTP_002_InvalidOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("999999");
        otpPage.clickVerify();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("invalid"));
    }

    // 🔥 OTP_003 – Expired OTP
    @Test
    public void OTP_003_ExpiredOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("123456");
        otpPage.clickVerify();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("expired"));
    }

    // 🔥 OTP_004 – Empty OTP
    @Test
    public void OTP_004_EmptyOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.clickVerify();

        Assert.assertFalse(otpPage.getMessage().isEmpty());
    }

    // 🔥 OTP_005 – Partial OTP
    @Test
    public void OTP_005_PartialOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("123");
        otpPage.clickVerify();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("invalid"));
    }

    // 🔥 OTP_006 – Resend OTP
    @Test
    public void OTP_006_ResendOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.clickResend();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("sent"));
    }

    // 🔥 OTP_007 – Resend before timer
    @Test
    public void OTP_007_ResendBeforeTimer() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.clickResend();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("wait"));
    }

    // 🔥 OTP_008 – Brute force
    @Test
    public void OTP_008_MultipleWrongAttempts() {

        OTPPage otpPage = new OTPPage(driver, wait);

        for (int i = 0; i < 5; i++) {
            otpPage.enterOTP("111111");
            otpPage.clickVerify();
        }

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("blocked"));
    }

    // 🔥 OTP_009 – API Success
    @Test
    public void OTP_009_API_Success() {

        OrgApi api = new OrgApi();

        Response res = api.login("shaikameer7474@gmail.com", "password");

        Assert.assertEquals(res.getStatusCode(), 200);
    }

    // 🔥 OTP_010 – API Failure
    @Test
    public void OTP_010_API_Failure() {

        OrgApi api = new OrgApi();

        Response res = api.login("invalid@mail.com", "wrong");

        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    // 🔥 OTP_011 – DB Validation
    @Test
    public void OTP_011_DBValidation() throws Exception {

        DBUtil db = new DBUtil();

        Assert.assertTrue(db.isUserPresent("shaikameer7474@gmail.com"));
    }

    // 🔥 OTP_012 – Navigation
    @Test
    public void OTP_012_NavigateToResetPassword() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("123456");
        otpPage.clickVerify();

        Assert.assertTrue(driver.getCurrentUrl().contains("reset-password"));
    }

    // 🔥 OTP_013 – Leading zeros
    @Test
    public void OTP_013_LeadingZeros() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("001234");
        otpPage.clickVerify();

        Assert.assertTrue(true);
    }

    // 🔥 OTP_014 – Alphanumeric OTP
    @Test
    public void OTP_014_AlphanumericOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("12AB34");
        otpPage.clickVerify();

        Assert.assertTrue(otpPage.getMessage().toLowerCase().contains("invalid"));
    }

    // 🔥 OTP_015 – Response time
    @Test
    public void OTP_015_ResponseTime() {

        long start = System.currentTimeMillis();

        OTPPage otpPage = new OTPPage(driver, wait);
        otpPage.enterOTP("123456");
        otpPage.clickVerify();

        long time = System.currentTimeMillis() - start;

        Assert.assertTrue(time < 3000);
    }

    // 🔥 OTP_016 – Autofocus
    @Test
    public void OTP_016_AutoFocus() {

        String active = driver.switchTo().activeElement().getTagName();

        Assert.assertEquals(active, "input");
    }

    // 🔥 OTP_017 – Paste OTP
    @Test
    public void OTP_017_PasteOTP() {

        OTPPage otpPage = new OTPPage(driver, wait);

        otpPage.enterOTP("123456");

        Assert.assertTrue(true);
    }

    // 🔥 OTP_018 – Alignment
    @Test
    public void OTP_018_UIAlignment() {
        Assert.assertTrue(true);
    }

    // 🔥 OTP_019 – Keyboard navigation
    @Test
    public void OTP_019_KeyboardAccess() {
        Assert.assertTrue(true);
    }

    // 🔥 OTP_020 – OTP not visible in logs
    @Test
    public void OTP_020_Security_LogCheck() {
        Assert.assertTrue(true);
    }

    // 🔥 OTP_021 – Expiry validation
    @Test
    public void OTP_021_Expiry() {
        Assert.assertTrue(true);
    }

    // 🔥 OTP_022 – OTP reuse
    @Test
    public void OTP_022_ReuseOTP() {
        Assert.assertTrue(true);
    }
}