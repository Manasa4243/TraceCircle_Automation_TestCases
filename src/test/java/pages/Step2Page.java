package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Step2Page {

    WebDriver driver;
    WebDriverWait wait;

    public Step2Page(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // 🔥 FIXED: More stable locator (based on label, not just type)
    private By emailField = By.xpath("//label[contains(text(),'Email')]/following::input[1]");

    private By password = By.xpath("(//input[@type='password'])[1]");
    private By confirmPassword = By.xpath("(//input[@type='password'])[2]");
    private By createAccountBtn = By.xpath("//button[contains(text(),'Create') or contains(text(),'Onboard')]");

    // 🔥 FIXED: Better wait (handles navigation properly)
    public void waitForStep2Page() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(emailField),
                ExpectedConditions.urlContains("step2")   // if URL changes
        ));
    }

    // ✅ Get auto-filled email safely (no stale element issue)
    public String getEmailValue() {
        waitForStep2Page();
        return driver.findElement(emailField).getAttribute("value");
    }

    public void enterPassword(String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(password)).sendKeys(pass);
    }

    public void enterConfirmPassword(String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPassword)).sendKeys(pass);
    }

    public void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(createAccountBtn)).click();
    }

    public void completeStep2(String pass) {
        enterPassword(pass);
        enterConfirmPassword(pass);
        clickCreateAccount();
    }
}