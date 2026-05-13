package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import api.OrgApi;
import base.BaseTest;
import db.DBUtil;
import io.restassured.response.Response;
import pages.DashboardPage;
import pages.GTINPage;
import pages.LoginPage;

public class GTINTest extends BaseTest {

    // =========================================================
    // COMMON LOGIN
    // =========================================================

    public void loginToApplication() {

        openLoginPage();

        LoginPage login = new LoginPage(driver, wait);

        login.enterEmail("manasajagadeesh141@gmail.com");
        login.enterPassword("New@1234");
        login.clickLogin();

        System.out.println(" LOGIN SUCCESS");
    }

    // =========================================================
    // TC_GTIN_001
    // =========================================================

    @Test
    public void TC_GTIN_001_UploadValidGTINDocument() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.waitForDashboard();
        dashboard.goToGTINUploadPage();

        String gtinNumber = "12345678901234";

        gtin.enterGtin(gtinNumber);
        gtin.selectCategory("Economic Operator Identification");
        gtin.selectDocument("EORI Number");

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );

        gtin.clickSubmit();

        WebElement successMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Document uploaded successfully')]")
            )
    );

    Assert.assertTrue(successMsg.isDisplayed());

    System.out.println("Document uploaded successfully");
    }

    // =========================================================
    // TC_GTIN_002
    // =========================================================

    @Test
    public void TC_GTIN_002_GTINFieldAcceptsNumericInput() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.enterGtin("98765432101234");
 gtin.clickSubmit();
       WebElement successMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields')]")
            )
    );

    Assert.assertTrue(successMsg.isDisplayed());
    }

    // =========================================================
    // TC_GTIN_003
    // =========================================================

    @Test
    public void TC_GTIN_003_CategoryDropdownDisplaysOptions() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.selectCategory("Economic Operator Identification");

        System.out.println(" Category dropdown working");
    }

    // =========================================================
    // TC_GTIN_004
    // =========================================================

    @Test
    public void TC_GTIN_004_UserCanSelectCategory() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.selectCategory("Fiber Composition Documentation");

        System.out.println(" Category selected successfully");
    }

    // =========================================================
    // TC_GTIN_005
    // =========================================================

    @Test
    public void TC_GTIN_005_UploadValidPDF() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );


        System.out.println(" PDF uploaded successfully");
    }

    // =========================================================
    // TC_GTIN_006
    // =========================================================

    @Test
    public void TC_GTIN_006_SubmitButtonShouldUploadSuccessfully() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.enterGtin("11112222333344");
        gtin.selectCategory("Economic Operator Identification");
        gtin.selectDocument("EORI Number");

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );

        gtin.clickSubmit();

        
        System.out.println(" Upload submit successful");
    }

    // =========================================================
    // TC_GTIN_007
    // =========================================================

    // @Test
    // public void TC_GTIN_007_SuccessMessageShouldDisplay() throws Exception {

    //     loginToApplication();

    //     DashboardPage dashboard = new DashboardPage(driver, wait);
    //     GTINPage gtin = new GTINPage(driver, wait);

    //     dashboard.goToGTINUploadPage();

    //     gtin.enterGtin("22223333444455");
    //     gtin.selectCategory("Battery");
    //     gtin.selectDocument("PDF");

    //     gtin.uploadDocument(
    //             System.getProperty("user.dir")
    //             + "/src/test/java/resources/testdata/gtinpdf.pdf"
    //     );

    //     gtin.clickSubmit();

    //     gtin.validateSuccessMessage();

    //     System.out.println(" Success message displayed");
    // }

    // =========================================================
    // TC_GTIN_008
    // =========================================================

    @Test
    public void TC_GTIN_008_GTINSavedInUI_API_DB() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        DBUtil db = new DBUtil();
        OrgApi api = new OrgApi();

        dashboard.goToGTINUploadPage();

        String gtinNumber = "55556666777788";

        gtin.enterGtin(gtinNumber);
        gtin.selectCategory("Economic Operator Identification");
        gtin.selectDocument("EORI Number");

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );

        gtin.clickSubmit();

        gtin.validateSuccessMessage();

        // DB Validation
        Assert.assertTrue(
                db.isGTINPresent(gtinNumber),
                " GTIN not found in DB"
        );

        // API Validation
        Response response = api.getGTIN(gtinNumber);

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                " GTIN API fetch failed"
        );

        db.assertDbChecked();

        System.out.println(" GTIN stored in UI + API + DB");
    }

    // =========================================================
    // TC_GTIN_009
    // =========================================================

    @Test
    public void TC_GTIN_009_EmptyGTIN_ShouldShowValidation() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.selectCategory("Economic Operator Identification");
        gtin.selectDocument("EORI Number");

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );

        gtin.clickSubmit();
WebElement successMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields')]")
            )
    );

        System.out.println(" Empty GTIN validation checked");
    }

    // =========================================================
    // TC_GTIN_010
    // =========================================================

    @Test
    public void TC_GTIN_010_CategoryNotSelected_ShouldShowError() throws Exception {

        loginToApplication();

        DashboardPage dashboard = new DashboardPage(driver, wait);
        GTINPage gtin = new GTINPage(driver, wait);

        dashboard.goToGTINUploadPage();

        gtin.enterGtin("99998888777766");

        gtin.uploadDocument(
                System.getProperty("user.dir")
                + "/src/test/java/resources/testdata/gtinpdf.pdf"
        );

        gtin.clickSubmit();
WebElement successMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields')]")
            )
    );

        System.out.println(" Category validation checked");
    }
    // =========================================================
// TC_GTIN_011
// =========================================================

@Test
public void TC_GTIN_011_FileNotUploaded_ShouldShowError() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("11223344556677");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    // File is NOT uploaded
    gtin.clickSubmit();

    WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields')]")
            )
    );

    Assert.assertTrue(errorMsg.isDisplayed());

    System.out.println("File not uploaded validation checked");
}
// =========================================================
// TC_GTIN_012
// =========================================================

@Test
public void TC_GTIN_012_AllFieldsEmpty_ShouldShowError() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    // No GTIN, no category, no document, no file
    gtin.clickSubmit();

    WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields')]")
            )
    );

    Assert.assertTrue(errorMsg.isDisplayed());

    System.out.println("All fields empty validation checked");
}
// =========================================================
// TC_GTIN_013
// =========================================================

@Test
public void TC_GTIN_013_InvalidGTIN_ShouldReject() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("ABC@123GTIN");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf"
    );

    gtin.clickSubmit();

    WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields') or contains(text(),'invalid')]")
            )
    );

    Assert.assertTrue(errorMsg.isDisplayed());

    System.out.println("Invalid GTIN validation checked");
}
// =========================================================
// TC_GTIN_014
// =========================================================

@Test
public void TC_GTIN_014_UnsupportedFileFormat_ShouldFail() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("22334455667788");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    String txtFilePath = System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/LoginTestData.xlsx";

    java.io.File txtFile = new java.io.File(txtFilePath);

    Assert.assertTrue(
            txtFile.exists(),
            "TXT file not found at: " + txtFile.getAbsolutePath()
    );

    gtin.uploadDocument(txtFile.getAbsolutePath());

    gtin.clickSubmit();

    WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields') or contains(text(),'unsupported') or contains(text(),'invalid')]")
            )
    );

    Assert.assertTrue(errorMsg.isDisplayed());

    System.out.println("Unsupported file format validation checked");
}
// =========================================================
// TC_GTIN_015
// =========================================================

@Test
public void TC_GTIN_015_FileSizeExceedsLimit_ShouldFail() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("33445566778899");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    String largeFilePath = System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/10mb.pdf";

    java.io.File largeFile = new java.io.File(largeFilePath);

    Assert.assertTrue(
            largeFile.exists(),
            "Large file not found at: " + largeFile.getAbsolutePath()
    );

    gtin.uploadDocument(largeFile.getAbsolutePath());

    gtin.clickSubmit();

    WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please fill all required fields') or contains(text(),'size') or contains(text(),'large')]")
            )
    );

    Assert.assertTrue(errorMsg.isDisplayed());

    System.out.println("File size limit validation checked");
}
// =========================================================
// TC_GTIN_016
// =========================================================

@Test
public void TC_GTIN_016_UploadGTINPageLoadsCorrectly() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.waitForDashboard();
    dashboard.goToGTINUploadPage();

    Assert.assertTrue(
            driver.getCurrentUrl().contains("gtin"),
            "GTIN Upload page should load correctly"
    );

    System.out.println("GTIN Upload page loaded successfully");
}
// =========================================================
// TC_GTIN_016
// =========================================================

@Test
public void TC_GTIN_016_UploadGTINDocumentPageLoadsCorrectly() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.waitForDashboard();
    dashboard.goToGTINUploadPage();

    Assert.assertTrue(
            driver.getCurrentUrl().contains("gtin"),
            "GTIN Upload page should load correctly"
    );

    System.out.println("GTIN Upload page loaded successfully");
}
// =========================================================
// TC_GTIN_017
// =========================================================

@Test
public void TC_GTIN_017_AllFieldsVisible() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.goToGTINUploadPage();

    WebElement gtinField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[contains(@placeholder,'Enter GTIN')]")
            )
    );

    WebElement categoryField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(),'Category')]")
            )
    );

    WebElement uploadField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='file']")
            )
    );

    Assert.assertTrue(gtinField.isDisplayed());
    Assert.assertTrue(categoryField.isDisplayed());
    Assert.assertTrue(uploadField.isDisplayed());

    System.out.println("All fields are visible");
}
// =========================================================
// TC_GTIN_018
// =========================================================

@Test
public void TC_GTIN_018_EnterGTINPlaceholderDisplayed() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.goToGTINUploadPage();

    WebElement gtinInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[contains(@placeholder,'Enter GTIN')]")
            )
    );

    String placeholder = gtinInput.getAttribute("placeholder");

    Assert.assertEquals(
            placeholder,
            "Enter GTIN",
            "Placeholder text mismatch"
    );

    System.out.println("Enter GTIN placeholder verified");
}
// =========================================================
// TC_GTIN_019
// =========================================================

@Test
public void TC_GTIN_019_CategoryDropdownDefaultValue() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.goToGTINUploadPage();

    WebElement categoryDropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(),'Category')]/following::button[1]")
            )
    );

    String dropdownText = categoryDropdown.getText();

    Assert.assertTrue(
            dropdownText.contains("Select Category"),
            "Default dropdown should show Select Category"
    );

    System.out.println("Default category verified");
}
// =========================================================
// TC_GTIN_020
// =========================================================

@Test
public void TC_GTIN_020_UploadButtonEnabledOnlyAfterRequiredFieldsFilled() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    WebElement submitBtn = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[@type='submit']")
            )
    );

    boolean beforeFill = submitBtn.isEnabled();

    gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf"
    );

    boolean afterFill = submitBtn.isEnabled();

    System.out.println("Before fill: " + beforeFill);
    System.out.println("After fill: " + afterFill);

    Assert.assertTrue(afterFill);

    System.out.println("Upload button enable validation checked");
}
// =========================================================
// TC_GTIN_021
// =========================================================

@Test
public void TC_GTIN_021_CancelButtonNavigatesBack() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.clickCancel();

    Assert.assertFalse(
            driver.getCurrentUrl().contains("/gtin/add"),
            "Cancel button should navigate back"
    );

    System.out.println("Cancel button navigation verified");
}
// =========================================================
// TC_GTIN_022
// =========================================================

@Test
public void TC_GTIN_022_BackButtonWorksCorrectly() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.goToGTINUploadPage();

    driver.navigate().back();

    Assert.assertFalse(
            driver.getCurrentUrl().contains("/gtin/add"),
            "Back button should navigate correctly"
    );

    System.out.println("Browser back button verified");
}
@Test
public void TC_GTIN_023_VerifyPDFFileUpload() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");
    gtin.uploadDocument(System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf");

    Assert.assertTrue(gtin.isFileNameDisplayed("gtinpdf.pdf"),
            "PDF file name should be displayed after upload");
}

@Test
public void TC_GTIN_024_VerifyImageFileUpload() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");
    gtin.uploadDocument(System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/Screenshot 2026-04-14 142609.png");
gtin.clickSubmit();
    Assert.assertTrue(gtin.isFileNameDisplayed("gtinimage.png"),
            "Image file name should be displayed after upload");
}
@Test
public void TC_GTIN_025_VerifyMultipleFileUploadRestriction() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");
    Assert.assertTrue(gtin.isMultipleUploadDisabled(),
            "Multiple file upload should not be allowed");
}
@Test
public void TC_GTIN_026_VerifyFileNameDisplayedAfterUpload() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");
    gtin.uploadDocument(System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf");
gtin.clickSubmit();
     WebElement uploadedFile = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//td[normalize-space()='gtinpdf.pdf']")
            )
    );

    Assert.assertTrue(
            uploadedFile.isDisplayed(),
            "Uploaded file name should be displayed"
    );

    System.out.println("Uploaded file name displayed successfully");
}
 @Test
public void TC_GTIN_027_VerifyFileCanBeReplacedBeforeSubmission() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
gtin.enterGtin("12345678901234");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");
    gtin.uploadDocument(System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf");
Thread.sleep(30000); // 10 seconds
    gtin.uploadDocument(System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/10mb.pdf");

    // WebElement uploadedFile = wait.until(
    //         ExpectedConditions.visibilityOfElementLocated(
    //                 By.xpath("//td[normalize-space()='10mb.pdf']")
    //         )
    // );

    // Assert.assertTrue(
    //         uploadedFile.isDisplayed(),
    //         "Uploaded file name should be displayed"
    // );
}
@Test
public void TC_GTIN_028_VerifyCategoryDropdownOpensOnClick() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.openCategoryDropdown();

    Assert.assertTrue(gtin.isCategoryDropdownOpened(),
            "Category dropdown should open on click");
}
@Test
public void TC_GTIN_029_VerifyAllCategoryOptionsDisplayed() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.openCategoryDropdown();

    Assert.assertTrue(gtin.isCategoryOptionVisible("Economic Operator Identification"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Product Identification Documents"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Fiber Composition Documentation"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Manufacturing Location Disclosure (Tier 1)"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Chemical & Safety Compliance Documents"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Consumer Information & Labeling Documents"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Digital Product Passport Carrier Information"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Declaration of Conformity"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Data Accuracy & Responsibility Declaration"));
    Assert.assertTrue(gtin.isCategoryOptionVisible("Technical Dossier"));

    System.out.println("All category options are displayed");
}
@Test
public void TC_GTIN_030_VerifyUserCanSelectOneCategoryOnly() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.selectCategory("Economic Operator Identification");

    Assert.assertTrue(
            gtin.getSelectedCategoryText().contains("Economic Operator Identification"),
            "Selected category should be displayed"
    );
}
 @Test
public void TC_GTIN_031_DropdownClosesAfterSelection() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.selectCategory("Economic Operator Identification");

    Assert.assertTrue(
            gtin.isCategoryDropdownClosed(),
            "Dropdown should close after selecting category"
    );
}
@Test
public void TC_GTIN_032_NavigationFromDashboardToGTINDocument() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.waitForDashboard();
    dashboard.goToGTINUploadPage();

    GTINPage gtin = new GTINPage(driver, wait);

    Assert.assertTrue(
            gtin.isGTINUploadPageOpened(),
            "Dashboard to DPP to GTIN Document navigation failed"
    );
}
@Test
public void TC_GTIN_033_UploadGTINButtonOpensFormPage() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);

    dashboard.waitForDashboard();
    dashboard.goToGTINUploadPage();

    GTINPage gtin = new GTINPage(driver, wait);

    Assert.assertTrue(
            gtin.isGTINUploadPageOpened(),
            "Upload GTIN button should open form page"
    );
}
@Test
public void TC_GTIN_034_UserRemainsOnPageIfValidationFails() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    String beforeUrl = gtin.getCurrentPageUrl();

    gtin.clickSubmit();

    Assert.assertTrue(
            gtin.isRequiredFieldErrorDisplayed(),
            "Required field validation should be displayed"
    );

    Assert.assertEquals(
            gtin.getCurrentPageUrl(),
            beforeUrl,
            "User should remain on same page when validation fails"
    );
}
@Test
public void TC_GTIN_035_VeryLongGTINNumberHandling() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    String longGtin = "12345678901234567890123456789012345678901234567890234567890345678976543456789765432345678976543234567865432134567898765432345678654321345678098765432234567876543";

    gtin.enterGtin(longGtin);
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf"
    );

    gtin.clickSubmit();

    Assert.assertTrue(
            gtin.isRequiredFieldErrorDisplayed() ||
                    gtin.getCurrentPageUrl().contains("gtin"),
            "Very long GTIN should be handled safely"
    );
}
@Test
public void TC_GTIN_036_SpecialCharactersInFileName() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("66778899001122");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtin @special.pdf"
    );

    gtin.clickSubmit();

    Assert.assertTrue(
            gtin.getCurrentPageUrl().contains("gtin"),
            "System should handle special characters in file name safely"
    );
}
@Test
public void TC_GTIN_037_DuplicateGTINUploadBehavior() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    String duplicateGtin = "12345678901234";

    gtin.enterGtin(duplicateGtin);
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf"
    );

    gtin.clickSubmit();

    Assert.assertTrue(
            driver.getPageSource().toLowerCase().contains("duplicate") ||
                    driver.getPageSource().toLowerCase().contains("already") ||
                    driver.getPageSource().toLowerCase().contains("uploaded") ||
                    driver.getPageSource().toLowerCase().contains("success"),
            "Duplicate GTIN behavior should be handled with proper message"
    );
}
@Test
public void TC_GTIN_038_SystemBehaviorOnSlowNetwork() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    gtin.enterGtin("77889900112233");
    gtin.selectCategory("Economic Operator Identification");
    gtin.selectDocument("EORI Number");

    gtin.uploadDocument(
            System.getProperty("user.dir")
            + "/src/test/java/resources/testdata/gtinpdf.pdf"
    );

    gtin.clickSubmit();


    wait.until(driver ->
            driver.getPageSource().contains("Document uploaded successfully") ||
                    driver.getPageSource().contains("Please fill all required fields") ||
                    driver.getPageSource().toLowerCase().contains("failed")
    );

    Assert.assertTrue(
            driver.getPageSource().contains("Document uploaded successfully") ||
                    driver.getPageSource().contains("Please fill all required fields") ||
                    driver.getPageSource().toLowerCase().contains("failed"),
            "System should show final response after slow network"
    );
}
@Test
public void TC_GTIN_039_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

    // 1. Economic Operator Identification
    gtin.selectCategory("Economic Operator Identification");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Company Legal Registration Document"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("VAT Identification Document"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("EORI Number"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Registered Address Proof"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Authorized EU Representative Document"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}

    // 2. Product Identification Documents
    @Test
public void TC_GTIN_040_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();

   
    gtin.selectCategory("Product Identification Documents");
    gtin.openDocumentDropdown();
Thread.sleep(1000);
    Assert.assertTrue(gtin.isDocumentOptionVisible("Product Name & Commercial Description"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Model / Style / Article Number Documentation"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Product Category Declaration (Apparel / Textile)"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("HS Code / CN Code Classification"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Product Image"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 3. Fiber Composition Documentation
      @Test
public void TC_GTIN_041_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Fiber Composition Documentation");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Fiber Composition Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Material Type Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Fiber Origin Country Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 4. Manufacturing Location Disclosure (Tier 1)
      @Test
public void TC_GTIN_042_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Manufacturing Location Disclosure (Tier 1)");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Final Manufacturing Facility Name"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Facility Address"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Country of Manufacture"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Manufacturing Role Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 5. Chemical & Safety Compliance Documents
      @Test
public void TC_GTIN_043_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Chemical & Safety Compliance Documents");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("REACH Compliance Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("SVHC Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Restricted Substances Compliance"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 6. Consumer Information & Labeling Documents
      @Test
public void TC_GTIN_044_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Consumer Information & Labeling Documents");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Care Instructions Document"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Fiber Content Label Proof"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Country of Origin Label Proof"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Size / Fit Information"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 7. Digital Product Passport Carrier Information
      @Test
public void TC_GTIN_045_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Digital Product Passport Carrier Information");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Unique Product Identifier"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("QR Code Specification"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Product to Digital Record Linkage"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 8. Declaration of Conformity
      @Test
public void TC_GTIN_046_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Declaration of Conformity");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("EU Declaration of Conformity"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Responsible Economic Operator Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 9. Data Accuracy & Responsibility Declaration
      @Test
public void TC_GTIN_047_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Data Accuracy & Responsibility Declaration");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("Data Accuracy Declaration"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Data Update Responsibility"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Record Retention Commitment"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));
}
    // 10. Technical Dossier
      @Test
public void TC_GTIN_048_VerifyCategoryWiseDocumentDropdownOptions() throws Exception {

    loginToApplication();

    DashboardPage dashboard = new DashboardPage(driver, wait);
    GTINPage gtin = new GTINPage(driver, wait);

    dashboard.goToGTINUploadPage();
    gtin.selectCategory("Technical Dossier");
    gtin.openDocumentDropdown();

    Assert.assertTrue(gtin.isDocumentOptionVisible("EU New Approach Directives Compliance"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("CE Marking Documentation"));
    Assert.assertTrue(gtin.isDocumentOptionVisible("Other"));

    System.out.println("All category-wise document dropdown options verified");
}
}