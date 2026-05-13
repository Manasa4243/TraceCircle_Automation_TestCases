package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    public DashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // Locators
    By dashboardHeader = By.xpath("//h1[contains(text(),'Dashboard')]");
    By dppManagement = By.xpath("//span[text()='DPP Management']");
    By gtinDocumentMenu = By.xpath("//span[text()='GTIN Document']");
    By addUploadBtn = By.xpath("//button[contains(text(),'Upload GTIN')]");
  private  By orgMenu = By.xpath("//span[text()='Org Management']");
    private By plantsMenu = By.xpath("//a[@href='/plants']");
   private By addPlantBtn = By.xpath("//button[contains(text(),'Add Plant')]");

public void clickOrganizationMenu() {
    wait.until(ExpectedConditions.elementToBeClickable(orgMenu)).click();
}

public void clickPlantsMenu() {
    wait.until(ExpectedConditions.elementToBeClickable(plantsMenu)).click();
}

public void clickAddPlant() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(addPlantBtn));
    wait.until(ExpectedConditions.elementToBeClickable(addPlantBtn)).click();
}
    // Actions
    public void waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
    }

    public void goToGTINUploadPage() {
        wait.until(ExpectedConditions.elementToBeClickable(dppManagement)).click();
        wait.until(ExpectedConditions.elementToBeClickable(gtinDocumentMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(addUploadBtn)).click();
    }
    
}