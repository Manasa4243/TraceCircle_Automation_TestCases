package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GTINPage {

    WebDriver driver;
    WebDriverWait wait;

    public GTINPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // 🔹 Dashboard
    private By dashboard = By.xpath("//h1[contains(text(),'Dashboard')]");

    // 🔹 Navigation
    private By dppManagement = By.xpath("//span[normalize-space()='DPP Management']");
    private By gtinDocument = By.xpath("//span[normalize-space()='GTIN Document']");
    private By addGtinBtn = By.xpath("//button[contains(.,'Upload GTIN')]");

    // 🔹 GTIN input
    private By gtinField = By.xpath("//input[contains(@placeholder,'Enter GTIN')]");

    // 🔹 Category Dropdown
    private By categoryDropdown = By.xpath("//label[contains(text(),'Category')]/following::button[1]");

    // 🔹 Document Dropdown
    private By documentDropdown = By.xpath("//label[contains(text(),'Document')]/following::button[1]");

    // 🔹 Dropdown Options (generic)
    private String dropdownOption = "//div[@role='option' and normalize-space()='%s']";

    // 🔹 File Upload
    private By uploadInput = By.xpath("//input[@type='file']");

    // 🔹 Submit
    private By submitBtn = By.xpath("//button[@type='submit']");

    // 🔹 Success Message
    private By successMsg = By.xpath("//*[contains(text(),'success') or contains(text(),'uploaded')]");
private By cancelButton = By.xpath("//button[normalize-space()='Cancel']");
    // =========================================================
    // 🔹 METHODS
    // =========================================================

    public void waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboard));
    }

    public void clickDppManagement() {
        wait.until(ExpectedConditions.elementToBeClickable(dppManagement)).click();
    }

    public void clickGtinDocument() {
        wait.until(ExpectedConditions.elementToBeClickable(gtinDocument)).click();
    }

    public void clickAddGtinButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addGtinBtn)).click();
    }

    public void enterGtin(String gtin) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(gtinField)).clear();
        driver.findElement(gtinField).sendKeys(gtin);
    }

    // 🔥 CATEGORY DROPDOWN
    public void selectCategory(String category) {
        wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();

        By option = By.xpath(String.format(dropdownOption, category));
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    // 🔥 DOCUMENT DROPDOWN
    public void selectDocument(String document) {
        wait.until(ExpectedConditions.elementToBeClickable(documentDropdown)).click();

        By option = By.xpath(String.format(dropdownOption, document));
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    // 🔥 FILE UPLOAD (FIXED)
    public void uploadDocument(String filePath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(uploadInput))
            .sendKeys(filePath);
    }

    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();
    }

    public void validateSuccessMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg));
    }
    public void clickCancel() {
    wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
}
public boolean isMultipleUploadDisabled() {
    String multipleAttr = wait.until(
            ExpectedConditions.presenceOfElementLocated(uploadInput)
    ).getAttribute("multiple");

    return multipleAttr == null || multipleAttr.isEmpty();
}

public boolean isFileNameDisplayed(String fileName) {
    return driver.getPageSource().contains(fileName);
}

public void openCategoryDropdown() {
    wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();
}

public boolean isCategoryDropdownOpened() {
    return driver.getPageSource().contains("Economic Operator Identification") ||
           driver.getPageSource().contains("Fiber Composition Documentation");
}

public boolean isCategoryOptionVisible(String category) {
    return driver.getPageSource().contains(category);
}

public String getSelectedCategoryText() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(categoryDropdown)).getText();
}
public boolean isCategoryDropdownClosed() {
    return !driver.getPageSource().contains("Fiber Composition Documentation");
}

public boolean isGTINUploadPageOpened() {
    return driver.getCurrentUrl().contains("gtin") ||
            driver.getPageSource().contains("Enter GTIN");
}

public boolean isUploadButtonVisible() {
    return driver.getPageSource().contains("Upload GTIN");
}

public boolean isRequiredFieldErrorDisplayed() {
    return driver.getPageSource().contains("Please fill all required fields");
}

public String getCurrentPageUrl() {
    return driver.getCurrentUrl();
}
public void openDocumentDropdown() {
    wait.until(ExpectedConditions.elementToBeClickable(documentDropdown)).click();
}

public boolean isDocumentOptionVisible(String documentName) {

    try {

        By option = By.xpath(
            "//div[@role='option' and normalize-space()='" + documentName + "']"
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(option));

        return driver.findElement(option).isDisplayed();

    } catch (Exception e) {

        return false;
    }
}
    public void validateGtinRequiredError() {}
public void validateCategoryError() {}
public void validateDocumentError() {}
public void validateFileTypeError() {}
public void validateDuplicateError() {}
public void validateNavigationBack() {}

public String getUploadSuccessMessage() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUploadSuccessMessage'");
}
}