package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OTPPage {

    WebDriver driver;
    WebDriverWait wait;

    public OTPPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // 🔹 Locators (adjust based on your UI)
    private By otpFields = By.xpath("//input[@type='text']");
    private By verifyBtn = By.xpath("//button[contains(text(),'Verify')]");
    private By resendBtn = By.xpath("//button[contains(text(),'Resend')]");
    private By message = By.xpath("//div[contains(@class,'message')]");

    // 🔹 Actions
    public void enterOTP(String otp) {
        java.util.List<WebElement> fields = driver.findElements(otpFields);

        for (int i = 0; i < otp.length() && i < fields.size(); i++) {
            fields.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
    }

    public void clickVerify() {
        wait.until(ExpectedConditions.elementToBeClickable(verifyBtn)).click();
    }

    public void clickResend() {
        wait.until(ExpectedConditions.elementToBeClickable(resendBtn)).click();
    }

    public String getMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(message)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void pressEnter() {
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
    }
}