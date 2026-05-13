package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
    private By forgotPassword = By.xpath("//a[contains(text(),'Forgot')]");
private By contactUs = By.xpath("//*[contains(text(),'Contact')]");

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
    // 🔹 Press Enter (TC_LGN_010)
public void pressEnter() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField))
        .sendKeys(Keys.ENTER);
}
public String getEmailValidationMessage() {
    try {
        return driver.findElement(emailField).getAttribute("validationMessage");
    } catch (Exception e) {
        return "";
    }
}
public String getPasswordValidationMessage() {
    try {
        return driver.findElement(passwordField).getAttribute("validationMessage");
    } catch (Exception e) {
        return "";
    }
}

// 🔹 Forgot Password click (TC_LGN_011)
public void clickForgotPassword() {
    wait.until(ExpectedConditions.elementToBeClickable(forgotPassword)).click();
}


// 🔹 Contact Us visible (TC_LGN_012)
public boolean isContactUsVisible() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(contactUs)).isDisplayed();
}


// 🔹 Password field type check (TC_LGN_015)
public String getPasswordFieldType() {
    return driver.findElement(passwordField).getAttribute("type");
}   


// 🔹 Login page loaded (TC_LGN_016)
public boolean isLoginPageLoaded() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(loginBtn)).isDisplayed();
}


// 🔹 Get email value (TC_LGN_018)
public String getEmailValue() {
    return driver.findElement(emailField).getAttribute("value");
}
public boolean isLoginButtonEnabled() {
    return wait.until(ExpectedConditions.presenceOfElementLocated(loginBtn)).isEnabled();
}

// 🔹 Get password value (TC_LGN_018)
public String getPasswordValue() {
    return driver.findElement(passwordField).getAttribute("value");
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
    public boolean isSessionCreated() {
    try {
        Long localStorageSize = (Long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return window.localStorage.length;");

        Long sessionStorageSize = (Long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return window.sessionStorage.length;");

        return localStorageSize > 0 || sessionStorageSize > 0;

    } catch (Exception e) {
        return false;
    }
}
public void clickLogout() {
    By logoutBtn = By.xpath("//button[contains(text(),'Logout') or contains(text(),'Sign out') or contains(text(),'Log out')]");
    wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
}





}