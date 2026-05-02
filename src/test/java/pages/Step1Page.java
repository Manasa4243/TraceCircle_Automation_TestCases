package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Step1Page {

    WebDriver driver;
    WebDriverWait wait;

    public Step1Page(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    private By orgName = By.xpath("//label[normalize-space()='Organization Name']/following::input[1]");
    private By email = By.xpath("//label[normalize-space()='Email']/following::input[1]");
    private By location = By.xpath("//label[normalize-space()='Location']/following::input[1]");
    private By onboardBtn = By.xpath("//button[contains(text(),'Onboard')]");

    public void enterOrgName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(orgName)).sendKeys(name);
    }

    public void enterEmail(String mail) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(email)).sendKeys(mail);
    }

    public void enterLocation(String loc) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(location)).sendKeys(loc);
    }

    public void clickOnboard() {
        wait.until(ExpectedConditions.elementToBeClickable(onboardBtn)).click();
    }

    public void completeStep1(String name, String mail, String loc) {
        enterOrgName(name);
        enterEmail(mail);
        enterLocation(loc);
        clickOnboard();
    }
}