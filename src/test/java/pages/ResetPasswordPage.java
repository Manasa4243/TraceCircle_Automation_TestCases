package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ResetPasswordPage {

    WebDriver driver;
    WebDriverWait wait;

    public ResetPasswordPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // 🔹 Locators (update if needed)
    private By newPassword = By.id("newPassword");
    private By confirmPassword = By.id("confirmPassword");
    private By resetBtn = By.xpath("//button[contains(text(),'Reset')]");
    private By message = By.xpath("//div[contains(@class,'message')]");
    private By showToggle = By.xpath("//span[contains(@class,'eye')]");

    // 🔹 Actions
    public void enterNewPassword(String pwd) {
        driver.findElement(newPassword).clear();
        driver.findElement(newPassword).sendKeys(pwd);
    }

    public void enterConfirmPassword(String pwd) {
        driver.findElement(confirmPassword).clear();
        driver.findElement(confirmPassword).sendKeys(pwd);
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetBtn)).click();
    }

    public String getMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(message)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getPasswordFieldType() {
        return driver.findElement(newPassword).getAttribute("type");
    }

    public void clickShowHide() {
        driver.findElement(showToggle).click();
    }
}