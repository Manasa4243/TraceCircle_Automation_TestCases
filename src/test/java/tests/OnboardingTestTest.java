package tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.Step1Page;
import pages.Step2Page;
import db.DBUtil;
import api.OrgApi;
import io.restassured.response.Response;

public class OnboardingTestTest extends BaseTest {
 @Test
public void ValidOnboarding_FullIntegration() throws Exception {

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
public void DuplicateEmail_Onboarding_ShouldFail_WithAPIValidation() throws Exception {

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
}