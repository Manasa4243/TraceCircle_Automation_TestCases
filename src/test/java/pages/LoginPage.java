package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    private By emailField = By.xpath("//input[@type='email']");
    private By passwordField = By.xpath("//input[@type='password']");
    private By loginBtn = By.xpath("//button[contains(text(),'Login')]");

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).clear();
        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).clear();
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
    }

    // ✅ FIXED METHOD (WAIT + CAPTURE ALERT PROPERLY)
    public String getLoginErrorMessage() {
        try {
            // 🔥 WAIT until alert appears
            wait.until(ExpectedConditions.alertIsPresent());

            String alertText = driver.switchTo().alert().getText();

            // 🔥 CLOSE alert
            driver.switchTo().alert().accept();

            return alertText;

        } catch (Exception e) {
            return "No error message found";
        }
    }

    // (no changes here)
    public boolean isLoginSuccessful() {
        try {
            // example: dashboard or redirect check
            return !driver.getCurrentUrl().contains("login");
        } catch (Exception e) {
            return false;
        }
    }
}