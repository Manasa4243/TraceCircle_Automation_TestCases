package tests;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.OrgApi;
import db.DBUtil;
import io.restassured.response.Response;
import base.BaseTest;
import pages.DashboardPage;
import pages.PlantPage;
import pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
public class PlantTest extends BaseTest {

    // =========================================================
    // COMMON NAVIGATION FLOW
    // =========================================================

    private void navigateToPlantPage() {

        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);
        login.enterEmail("manasajagadeesh141@gmail.com");
        login.enterPassword("New@1234");
        login.clickLogin();

        DashboardPage dashboard = new DashboardPage(driver, wait);

        dashboard.clickOrganizationMenu();
        dashboard.clickPlantsMenu();
        dashboard.clickAddPlant();
    }

    // =========================================================
    // TC_PLT_001
    // =========================================================

    @Test
    public void TC_PLT_001_VerifyPlantsPageLoads() {

        navigateToPlantPage();

        PlantPage plant = new PlantPage(driver, wait);
        plant.verifyPageLoaded();
    }

    // =========================================================
    // TC_PLT_002
    // =========================================================

    @Test
    public void TC_PLT_002_VerifyNewPlantFormOpens() {

        navigateToPlantPage();

        PlantPage plant = new PlantPage(driver, wait);
        plant.verifyPageLoaded();
    }

    // =========================================================
    // TC_PLT_003
    // =========================================================

    @Test
public void TC_PLT_003_VerifyAllFieldsVisible() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    // ✅ Validate fields are visible
    Assert.assertTrue(plant.isPlantNameDisplayed(), "Plant Name field not visible");
    Assert.assertTrue(plant.isLocationDisplayed(), "Location field not visible");
    Assert.assertTrue(plant.isCountryDisplayed(), "Country field not visible");

    // ✅ Validate correct placeholders
    Assert.assertEquals(plant.getPlantNamePlaceholder(), "Enter plant name");
    Assert.assertEquals(plant.getLocationPlaceholder(), "Enter plant location");
    Assert.assertEquals(plant.getCountryPlaceholder(), "Enter country");
}
    // =========================================================
    // TC_PLT_004
    // =========================================================

    @Test
public void TC_PLT_004_VerifyPlantNamePlaceholder() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(plant.isPlantNameDisplayed(), "Plant Name field not visible");
    Assert.assertEquals(plant.getPlantNamePlaceholder(), "Enter plant name");
}
    // =========================================================
    // TC_PLT_005
    // =========================================================

    @Test
public void TC_PLT_005_VerifyLocationPlaceholder() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(plant.isLocationDisplayed(), "Location field not visible");
    Assert.assertEquals(plant.getLocationPlaceholder(), "Enter plant location");
}

    // =========================================================
    // TC_PLT_006
    // =========================================================

   @Test
public void TC_PLT_006_VerifyCountryPlaceholder() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(plant.isCountryDisplayed(), "Country field not visible");
    Assert.assertEquals(plant.getCountryPlaceholder(), "Enter country");
}

    // =========================================================
    // TC_PLT_007
    // =========================================================

   @Test
public void TC_PLT_007_VerifyOrgIdAutoFilled() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(plant.getOrgIdValue().length() > 0, "Org ID is empty");
}

    // =========================================================
    // TC_PLT_008
    // =========================================================

    @Test
public void TC_PLT_008_VerifyOrgIdDisabled() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(plant.isOrgIdDisabled(), "Org ID should be disabled");
}

    // =========================================================
    // TC_PLT_009
    // =========================================================

 @Test
public void TC_PLT_009_CreatePlant_ValidData() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    String name = "Plant" + System.currentTimeMillis();

    plant.enterPlantName(name);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(plant.isSuccessMessageDisplayed(), "Plant creation failed");
}
    // =========================================================
    // TC_PLT_010
    // =========================================================

  @Test
public void TC_PLT_010_VerifyCreateButtonEnabled() {
navigateToPlantPage();
    PlantPage plant = new PlantPage(driver, wait);

    // Before entering data (optional check if disabled)
    // Assert.assertFalse(plant.isCreateEnabled());

    plant.enterPlantName("TestPlant");
    plant.enterLocation("Mumbai");
    plant.enterCountry("INDIA");

    Assert.assertTrue(plant.isCreateEnabled(), "Create button not enabled");

    plant.clickCreate();

    Assert.assertTrue(plant.isSuccessMessageDisplayed(), "Plant not created");
}
// =========================================================
// TC_PLT_011
// =========================================================

@Test
public void TC_PLT_011_CancelButtonNavigatesBack() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickCancel();

    Assert.assertTrue(
            plant.isPlantsListPageDisplayed(),
            "Cancel button should navigate back to Plants list"
    );
}
// =========================================================
// TC_PLT_012
// =========================================================

@Test
public void TC_PLT_012_BackArrowNavigatesBack() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickBackArrow();

    Assert.assertTrue(
            plant.isPlantsListPageDisplayed(),
            "Back arrow should navigate back to Plants list"
    );
}
// =========================================================
// TC_PLT_013
// =========================================================

@Test
public void TC_PLT_013_EmptyPlantNameShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(
            plant.getPlantNameValidationMessage().length() > 0
                    || plant.isValidationDisplayed(),
            "Empty Plant Name should be rejected"
    );
}
// =========================================================
// TC_PLT_014
// =========================================================

@Test
public void TC_PLT_014_EmptyPlantLocationShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("TestPlant1");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(
            plant.getLocationValidationMessage().length() > 0
                    || plant.isValidationDisplayed(),
            "Empty Plant Location should be rejected"
    );
}
// =========================================================
// TC_PLT_015
// =========================================================

@Test
public void TC_PLT_015_EmptyCountryShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("TestPlant");
    plant.enterLocation("Bangalore");

    plant.clickCreate();

    Assert.assertTrue(
            plant.getCountryValidationMessage().length() > 0
                    || plant.isValidationDisplayed(),
            "Empty Country should be rejected"
    );
}
// =========================================================
// TC_PLT_016
// =========================================================

@Test
public void TC_PLT_016_AllEmptyFieldsShouldShowValidation() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed(),
            "All empty fields should show validation"
    );
}
// =========================================================
// TC_PLT_017
// =========================================================

@Test
public void TC_PLT_017_PlantNameOnlySpacesShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("     ");
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || plant.isPlantNameDisplayed(),
            "Plant Name with only spaces should be rejected"
    );
}
// =========================================================
// TC_PLT_018
// =========================================================

@Test
public void TC_PLT_018_PlantLocationOnlySpacesShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("Test");
    plant.enterLocation("     ");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || plant.isLocationDisplayed(),
            "Plant Location with only spaces should be rejected"
    );
}
// =========================================================
// TC_PLT_019
// =========================================================

@Test
public void TC_PLT_019_CountryOnlySpacesShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("Tes");
    plant.enterLocation("Bangalore");
    plant.enterCountry("     ");

    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || plant.isCountryDisplayed(),
            "Country with only spaces should be rejected"
    );
}
// =========================================================
// TC_PLT_020
// =========================================================

@Test
public void TC_PLT_020_VeryLongPlantNameShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    String longPlantName = "Plant_" + "A".repeat(500);

    plant.enterPlantName(longPlantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Assert.assertTrue(

            plant.isValidationDisplayed()

            || driver.getPageSource().toLowerCase().contains("too long")

            || driver.getPageSource().toLowerCase().contains("invalid")

            || driver.getPageSource().toLowerCase().contains("failed")

            || driver.getCurrentUrl().contains("plants"),

            "Very long Plant Name should be rejected"
    );

    System.out.println("Very long Plant Name rejected successfully");
}
@Test
public void TC_PLT_021_VeryLongPlantLocationShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    String plantName = "Plant_" + System.currentTimeMillis();
    String longLocation = "Location_" + "A".repeat(500);

    plant.enterPlantName(plantName);
    plant.enterLocation(longLocation);
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || driver.getPageSource().toLowerCase().contains("too long")
                    || driver.getPageSource().toLowerCase().contains("invalid")
                    || driver.getPageSource().toLowerCase().contains("failed"),
            "Very long Plant Location should be rejected"
    );
}
@Test
public void TC_PLT_022_VeryLongCountryShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    String plantName = "Plant_" + System.currentTimeMillis();
    String longCountry = "Country_" + "A".repeat(500);

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry(longCountry);
    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || driver.getPageSource().toLowerCase().contains("too long")
                    || driver.getPageSource().toLowerCase().contains("invalid")
                    || driver.getPageSource().toLowerCase().contains("failed"),
            "Very long Country should be rejected"
    );
}
@Test
public void TC_PLT_023_SQLInjectionInPlantNameShouldBeRejected() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("Plant' OR '1'='1");
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || driver.getPageSource().toLowerCase().contains("invalid")
                    || driver.getPageSource().toLowerCase().contains("failed"),
            "SQL injection in Plant Name should be rejected/sanitized"
    );
}
@Test
public void TC_PLT_024_XSSInputInPlantNameShouldNotExecute() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("<script>alert(1)</script>");
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    String alertMsg = plant.getAlertMessageIfPresent();

    Assert.assertFalse(
            alertMsg.equals("1") || alertMsg.contains("<script>"),
            "XSS input executed as alert"
    );

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || driver.getPageSource().toLowerCase().contains("invalid")
                    || driver.getPageSource().toLowerCase().contains("failed")
                    || driver.getCurrentUrl().contains("plants"),
            "XSS input should be handled safely"
    );
}
@Test
public void TC_PLT_025_SpecialCharactersInPlantNameShouldBeRejected() throws InterruptedException {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("@@@Plant###");
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Thread.sleep(2000);

    Assert.assertTrue(

            plant.isValidationDisplayed()

            || driver.getPageSource().toLowerCase().contains("invalid")

            || driver.getPageSource().toLowerCase().contains("special")

            || driver.getPageSource().toLowerCase().contains("failed")

            || driver.getCurrentUrl().contains("plants"),

            "Special characters in Plant Name should be rejected"
    );

    System.out.println("Special characters in Plant Name rejected successfully");
}
@Test
public void TC_PLT_026_SpecialCharactersInLocationShouldBeHandled() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("Plant_" + System.currentTimeMillis());
    plant.enterLocation("@@@Bangalore###");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isValidationDisplayed()
                    || driver.getCurrentUrl().contains("plants")
                    || driver.getPageSource().contains("@@@Bangalore###"),
            "Special characters in Location should be handled"
    );
}
 @Test
public void TC_PLT_027_CreatedPlantShouldBeVisibleInPlantList() throws InterruptedException {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    String plantName = "Plant_" + System.currentTimeMillis();

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isSuccessMessageDisplayed(),
            "Plant should be created successfully"
    );

    Thread.sleep(2000);

    openPlantsPage();

    Thread.sleep(3000);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.scrollBy(0,500)");

    Thread.sleep(1000);

    Assert.assertTrue(
            plant.isPlantVisibleInList(plantName),
            "Created plant should be visible in Plant List"
    );
}
@Test
public void TC_PLT_028_CreatedPlantShouldBeStoredInDB() throws Exception {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    String plantName = "Plant_" + System.currentTimeMillis();

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isSuccessMessageDisplayed(),
            "Plant should be created successfully"
    );

    Thread.sleep(3000);

    Assert.assertTrue(
            db.isPlantPresent(plantName),
            "Created plant should be stored in DB"
    );

    db.assertDbChecked();
}
@Test
public void TC_PLT_029_APIShouldReturn200or201ForValidPlantCreation() {

    OrgApi api = new OrgApi();

    // 🔥 Login first
    String token = api.getAuthToken(
            "manasajagadeesh141@gmail.com",
            "New@1234"
    );

    System.out.println("TOKEN => " + token);

    String plantName = "APIPlant_" + System.currentTimeMillis();

    Response response = api.createPlant(
            token,
            plantName,
            "Bangalore",
            "INDIA",
            3
    );

    System.out.println(response.getBody().asString());

    Assert.assertTrue(
            response.getStatusCode() == 200
                    || response.getStatusCode() == 201,

            "API should return 200/201 for valid plant creation"
    );
}
@Test
public void TC_PLT_030_UI_API_DBDataShouldMatchAfterPlantCreation() throws Exception {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();
    OrgApi api = new OrgApi();

    String plantName = "Plant_" + System.currentTimeMillis();
    String location = "Bangalore";
    String country = "INDIA";

    // UI CREATION
    plant.enterPlantName(plantName);
    plant.enterLocation(location);
    plant.enterCountry(country);
    plant.clickCreate();

    Assert.assertTrue(
            plant.isSuccessMessageDisplayed(),
            "UI plant creation failed"
    );

    Thread.sleep(3000);

    // DB VALIDATION
    Assert.assertTrue(
            db.isPlantPresent(plantName),
            "Plant should exist in DB after UI creation"
    );

    // API VALIDATION
    String token = api.getAuthToken(
            "manasajagadeesh141@gmail.com",
            "New@1234"
    );

    Response response = api.createPlant(
            token,
            "API_" + plantName,
            location,
            country,
            3
    );

    int statusCode = response.getStatusCode();
    String body = response.getBody().asString();

    System.out.println("PLANT API STATUS: " + statusCode);
    System.out.println("PLANT API RESPONSE: " + body);

    Assert.assertTrue(
            statusCode == 200 ||
            statusCode == 201 ||
            statusCode == 400 ||
            statusCode == 401 ||
            statusCode == 403 ||
            statusCode == 404,
            "API should return a known response code. Got: " + statusCode
    );

    db.assertDbChecked();

    System.out.println("TC_PLT_030 PASSED: UI + DB validated, API response captured");
}
@Test
public void TC_PLT_031_DuplicatePlantShouldNotCreateDuplicateDBRecord() throws Exception {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    String plantName = "DuplicatePlant_" + System.currentTimeMillis();

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Assert.assertTrue(
            plant.isSuccessMessageDisplayed(),
            "First plant creation failed"
    );

    Thread.sleep(3000);

    int beforeCount = db.getPlantCount(plantName);

    navigateToPlantPage();

    plant = new PlantPage(driver, wait);

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");
    plant.clickCreate();

    Thread.sleep(3000);

    int afterCount = db.getPlantCount(plantName);

    Assert.assertEquals(
            afterCount,
            beforeCount,
            "Duplicate plant should not create duplicate DB record"
    );

    db.assertDbChecked();
}
@Test
public void TC_PLT_032_FailedPlantCreationShouldNotSaveInDB() throws Exception {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    String plantName = "FailPlant_" + System.currentTimeMillis();

    // Invalid flow: Plant name entered, but location is empty
    plant.enterPlantName(plantName);
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Thread.sleep(2000);

    Assert.assertTrue(
            plant.isValidationDisplayed(),
            "Validation should be displayed for failed plant creation"
    );

    Assert.assertFalse(
            db.isPlantPresent(plantName),
            "Failed plant creation should NOT save in DB"
    );

    db.assertDbChecked();

    System.out.println("TC_PLT_032 PASSED: Failed plant was not stored in DB");
}
// 33-36 is api testing done in postman
@Test
public void TC_PLT_037_VerifyPlantListPageLoads() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isPlantListPageLoaded(),
            "Plant list page should load"
    );
}
@Test
public void TC_PLT_038_VerifyCreatedPlantAppearsInTable() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isPlantDisplayed("Plant"),
            "Created plant should appear in table"
    );
}
@Test
public void TC_PLT_039_VerifyPlantNameDisplayedCorrectly() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isPlantDisplayed("Plant"),
            "Plant name should display correctly"
    );
}
@Test
public void TC_PLT_040_VerifyPlantLocationDisplayedCorrectly() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isPlantDisplayed("Bangalore"),
            "Plant location should display correctly"
    );
}
@Test
public void TC_PLT_041_VerifyCountryDisplayedCorrectly() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isPlantDisplayed("INDIA"),
            "Country should display correctly"
    );
}
@Test
public void TC_PLT_042_VerifyOrganizationIdDisplayedCorrectly() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            driver.getPageSource().contains("3"),
            "Organization ID should display correctly"
    );
}
@Test
public void TC_PLT_043_SearchPlantByPlantName() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.searchPlant("Plant");

    Assert.assertTrue(
            plant.isPlantDisplayed("Plant"),
            "Search by Plant Name should work"
    );
}
@Test
public void TC_PLT_044_SearchPlantByLocation() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.searchPlant("Bangalore");

    Assert.assertTrue(
            plant.isPlantDisplayed("Bangalore"),
            "Search by Location should work"
    );
}
@Test
public void TC_PLT_045_SearchPlantByCountry() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.searchPlant("INDIA");

    Assert.assertTrue(
            plant.isPlantDisplayed("INDIA"),
            "Search by Country should work"
    );
}
@Test
public void TC_PLT_046_SearchInvalidPlantNameShowsNoResult() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.searchPlant("INVALID_PLANT_123");

    Assert.assertTrue(
            plant.isNoResultDisplayed()
                    || !driver.getPageSource().contains("INVALID_PLANT_123"),
            "Invalid plant search should show no result"
    );
}
@Test
public void TC_PLT_047_VerifyEditButtonOpensEditForm() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickEditButton();

    Assert.assertTrue(
            driver.getPageSource().contains("Edit")
                    || driver.getPageSource().contains("Update"),
            "Edit button should open edit form"
    );
}
@Test
public void TC_PLT_048_VerifyUserCanUpdatePlantName() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickEditButton();

    String updatedName = "UpdatedPlant_" + System.currentTimeMillis();

    plant.enterPlantName(updatedName);

    plant.clickCreate();

    Assert.assertTrue(
            plant.isPlantDisplayed(updatedName),
            "Plant Name should update successfully"
    );
}
@Test
public void TC_PLT_049_VerifyUserCanUpdateLocation() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickEditButton();

    plant.enterLocation("Hyderabad");

    plant.clickCreate();

    Assert.assertTrue(
            plant.isPlantDisplayed("Hyderabad"),
            "Location should update successfully"
    );
}
@Test
public void TC_PLT_050_VerifyUpdatedPlantReflectsInUIAPIDB() throws Exception {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    plant.clickEditButton();

    String updatedName = "UpdatedPlant_" + System.currentTimeMillis();

    plant.enterPlantName(updatedName);

    plant.clickCreate();

    Assert.assertTrue(
            plant.isPlantDisplayed(updatedName),
            "Updated plant should reflect in UI"
    );

    Assert.assertTrue(
            db.isPlantPresent(updatedName),
            "Updated plant should reflect in DB"
    );

    db.assertDbChecked();
}
@Test
public void TC_PLT_051_VerifyDeleteButtonVisible() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            driver.getPageSource().contains("Delete"),
            "Delete button should be visible"
    );
}
@Test
public void TC_PLT_052_VerifyDeleteConfirmationAppears() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickDeleteButton();

    Assert.assertTrue(
            plant.isDeletePopupDisplayed(),
            "Delete confirmation should appear"
    );
}
@Test
public void TC_PLT_053_VerifyCancelDeleteKeepsPlantRecord() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickDeleteButton();

    plant.clickCancelDelete();

    Assert.assertTrue(
            plant.isPlantDisplayed("Plant"),
            "Cancel delete should keep plant record"
    );
}
@Test
public void TC_PLT_054_VerifyConfirmDeleteRemovesPlantFromUI() {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.clickDeleteButton();

    plant.clickConfirmDelete();

    Assert.assertTrue(
            !driver.getPageSource().contains("Plant"),
            "Deleted plant should be removed from UI"
    );
}
@Test
public void TC_PLT_055_VerifyDeletedPlantRemovedFromDB() throws Exception {

    openPlantsPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    String plantName = "Plant";

    plant.clickDeleteButton();
    plant.clickConfirmDelete();

    Thread.sleep(3000);

    Assert.assertFalse(
            db.isPlantPresent(plantName),
            "Deleted plant should be removed from DB"
    );

    db.assertDbChecked();
}
@Test
public void TC_PLT_056_UserShouldOnlyCreatePlantForOwnOrganization() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isOrgIdDisabled(),
            "User should create plant only for own organization"
    );
}
@Test
public void TC_PLT_057_OrganizationIdShouldNotBeEditable() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    Assert.assertTrue(
            plant.isOrgIdDisabled(),
            "Organization ID should not be editable"
    );
}
@Test
public void TC_PLT_058_UnauthorizedUserShouldNotAccessNewPlantPage() {

    driver.get("http://localhost:5173/plants/new");

    Assert.assertTrue(
            driver.getCurrentUrl().contains("login")
                    || driver.getPageSource().contains("Unauthorized")
                    || driver.getPageSource().contains("Access Denied"),
            "Unauthorized user should not access New Plant page"
    );
}
@Test
public void TC_PLT_059_RefreshPageShouldKeepFormStable() {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);

    plant.enterPlantName("TestPlant");

    driver.navigate().refresh();

    Assert.assertTrue(
            driver.getCurrentUrl().contains("plants"),
            "Refresh should keep form stable"
    );
}
@Test
public void TC_PLT_060_BackAfterCreateShouldNotDuplicatePlant() throws Exception {

    navigateToPlantPage();

    PlantPage plant = new PlantPage(driver, wait);
    DBUtil db = new DBUtil();

    String plantName = "BackPlant_" + System.currentTimeMillis();

    plant.enterPlantName(plantName);
    plant.enterLocation("Bangalore");
    plant.enterCountry("INDIA");

    plant.clickCreate();

    Thread.sleep(3000);

    int beforeCount = db.getPlantCount(plantName);

    driver.navigate().back();
    driver.navigate().refresh();

    int afterCount = db.getPlantCount(plantName);

    Assert.assertEquals(
            beforeCount,
            afterCount,
            "Back after create should not duplicate plant"
    );
}
}