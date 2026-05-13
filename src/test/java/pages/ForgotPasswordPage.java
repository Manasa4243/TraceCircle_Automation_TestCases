package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ForgotPasswordPage {

    WebDriver driver;
    WebDriverWait wait;

    public ForgotPasswordPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    private By emailField = By.xpath("//label[normalize-space()='Email']/following::input[1]");
    private By sendOtpBtn = By.xpath("//button[contains(text(),'Send') or contains(text(),'OTP')]");

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(emailField));
        element.clear();
        element.sendKeys(email);
    }

    public void clickSendOtp() {
        wait.until(ExpectedConditions.elementToBeClickable(sendOtpBtn)).click();
    }

    public String getEmailValidationMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        return element.getAttribute("validationMessage");
    }
    private By loginLink = By.xpath("//a[contains(text(),'Login')]");

public void clickLoginLink() {
    wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
}

    public String getAlertMessageIfPresent() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            alert.accept();
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSendOtpButtonEnabled() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(sendOtpBtn)).isEnabled();
    }

    public String getEmailValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmailValue'");
    }
}