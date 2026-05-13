package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PlantPage {

    WebDriver driver;
    WebDriverWait wait;

    public PlantPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // =========================================================
    // LOCATORS
    // =========================================================

private By pageHeader = By.xpath("//*[contains(text(),'New Plant')]");
private By plantsMenu = By.xpath("//a[@href='/plants' and .//span[text()='Plants']]");
private By addPlantBtn = By.xpath("//button[contains(text(),'Add Plant')]");
private By plantName = By.xpath("//input[@placeholder='Enter plant name']");
private By plantLocation = By.xpath("//input[contains(@placeholder,'location') or contains(@placeholder,'Location')]");
private By country = By.xpath("//input[@placeholder='Enter country']");
private By orgId = By.xpath("//input[@disabled and @value]");
private By createBtn = By.xpath("//button[@type='submit' and contains(.,'Create')]");
private By cancelBtn = By.xpath("//button[contains(.,'Cancel')]");
private By successMsg = By.xpath("//*[contains(text(),'Plant created successfully')]");
private By backArrow = By.xpath("//button[.//*[contains(@class,'lucide-arrow-left')]]");
private By searchBox = By.xpath("//input[contains(@placeholder,'Search')]");
private By editButton = By.xpath("(//button[contains(.,'Edit')])[1]");
private By deleteButton = By.xpath("(//button[contains(.,'Delete')])[1]");
private By confirmDeleteButton = By.xpath("//button[contains(.,'Confirm') or contains(.,'Delete')]");
private By cancelDeleteButton = By.xpath("//button[contains(.,'Cancel')]");
private By deletePopup = By.xpath("//*[contains(text(),'delete') or contains(text(),'Delete')]");
    // =========================================================
    // PAGE LOAD
    // =========================================================

    public void verifyPageLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageHeader));
    }

    // =========================================================
    // NAVIGATION METHODS (FIXED)
    // =========================================================

    public void clickPlantsMenu() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(plantsMenu));
    wait.until(ExpectedConditions.elementToBeClickable(plantsMenu)).click();
}

    public void clickAddPlant() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(addPlantBtn));
    wait.until(ExpectedConditions.elementToBeClickable(addPlantBtn)).click();
}

    // =========================================================
    // ACTION METHODS
    // =========================================================

   public void enterPlantName(String name) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(plantName));
    driver.findElement(plantName).clear();
    driver.findElement(plantName).sendKeys(name);
}

public void enterLocation(String location) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(plantLocation));
    driver.findElement(plantLocation).clear();
    driver.findElement(plantLocation).sendKeys(location);
}

public void enterCountry(String countryName) {
    driver.findElement(country).clear();
    driver.findElement(country).sendKeys(countryName);
}
public void clickCreate() {
    wait.until(ExpectedConditions.elementToBeClickable(createBtn)).click();
}


    public void clickCancel() {

    wait.until(ExpectedConditions.visibilityOfElementLocated(cancelBtn));

    wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
}
public void clickBackArrow() {

    wait.until(ExpectedConditions.visibilityOfElementLocated(backArrow));

    wait.until(ExpectedConditions.elementToBeClickable(backArrow)).click();
}


public boolean isPlantsListPageDisplayed() {
    return driver.getCurrentUrl().contains("plants")
            && !driver.getPageSource().contains("New Plant");
}

public String getPlantNameValidationMessage() {
    return driver.findElement(plantName).getAttribute("validationMessage");
}

public String getLocationValidationMessage() {
    return driver.findElement(plantLocation).getAttribute("validationMessage");
}

public String getCountryValidationMessage() {
    return driver.findElement(country).getAttribute("validationMessage");
}

public boolean isValidationDisplayed() {
    return driver.getPageSource().toLowerCase().contains("required")
            || driver.getPageSource().toLowerCase().contains("please")
            || !getPlantNameValidationMessage().isEmpty()
            || !getLocationValidationMessage().isEmpty()
            || !getCountryValidationMessage().isEmpty();
}
    // =========================================================
    // GETTERS (FOR VALIDATION)
    // =========================================================

    public String getPlantNamePlaceholder() {
        return driver.findElement(plantName).getAttribute("placeholder");
    }

    public String getLocationPlaceholder() {
        return driver.findElement(plantLocation).getAttribute("placeholder");
    }

    public String getCountryPlaceholder() {
        return driver.findElement(country).getAttribute("placeholder");
    }

    public String getOrgIdValue() {
        return driver.findElement(orgId).getAttribute("value");
    }

    public boolean isOrgIdDisabled() {
        return !driver.findElement(orgId).isEnabled();
    }

    public boolean isSuccessMessageDisplayed() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg)).isDisplayed();
}
    public boolean isPlantNameDisplayed() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(plantName)).isDisplayed();
}

public boolean isLocationDisplayed() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(plantLocation)).isDisplayed();
}

public boolean isCountryDisplayed() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(country)).isDisplayed();
}
public boolean isCreateEnabled() {
    return driver.findElement(createBtn).isEnabled();
}
public boolean isPlantVisibleInList(String plantName) {
    return driver.getPageSource().contains(plantName);
}

public boolean isPlantListPageLoaded() {
    return driver.getCurrentUrl().contains("plants");
}

public boolean isPlantDisplayed(String value) {
    return driver.getPageSource().contains(value);
}

public void searchPlant(String value) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox)).clear();
    driver.findElement(searchBox).sendKeys(value);
}

public boolean isNoResultDisplayed() {
    return driver.getPageSource().toLowerCase().contains("no results")
            || driver.getPageSource().toLowerCase().contains("not found");
}

public void clickEditButton() {
    wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
}

public void clickDeleteButton() {
    wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
}

public boolean isDeletePopupDisplayed() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(deletePopup)).isDisplayed();
}

public void clickConfirmDelete() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton)).click();
}

public void clickCancelDelete() {
    wait.until(ExpectedConditions.elementToBeClickable(cancelDeleteButton)).click();
}

public String getAlertMessageIfPresent() {
    try {
        org.openqa.selenium.Alert alert =
                wait.until(ExpectedConditions.alertIsPresent());

        String text = alert.getText();
        alert.accept();
        return text;

    } catch (Exception e) {
        return "";
    }
}



}