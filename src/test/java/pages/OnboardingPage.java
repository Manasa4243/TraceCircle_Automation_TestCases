package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OnboardingPage {

    WebDriver driver;
    WebDriverWait wait;

    public OnboardingPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ================= STEP 1 =================

    private By orgName = By.xpath("//label[normalize-space()='Organization Name']/following::input[1]");
    private By email = By.xpath("//label[normalize-space()='Email']/following::input[1]");
    private By location = By.xpath("//label[normalize-space()='Location']/following::input[1]");
    private By onboardBtn = By.xpath("//button[contains(text(),'Onboard')]");

    // ================= STEP 2 =================

    private By step2Email = By.xpath("//input[@type='email']");

    // ⚠️ Use flexible locator (label text may vary)
    private By password = By.xpath("//input[@type='password'][1]");
    private By confirmPassword = By.xpath("(//input[@type='password'])[2]");

    private By createAccountBtn = By.xpath("//button[contains(text(),'Create') or contains(text(),'Onboard')]");

    // ================= COMMON =================

    private By successMsg = By.xpath("//*[contains(text(),'success') or contains(text(),'created')]");
    private By emailError = By.xpath("//*[contains(text(),'already') or contains(text(),'exists')]");

    // ================= COMMON METHOD =================

    private void type(By locator, String value) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        element.clear();

        try {
            element.sendKeys(value);
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].value='" + value + "';", element);
        }
    }

    // ================= STEP 1 METHODS =================

    public void enterOrgName(String name) {
        type(orgName, name);
    }

    public void enterEmail(String mail) {
        type(email, mail);
    }

    public void enterLocation(String loc) {
        type(location, loc);
    }

    public void clickOnboard() {
        wait.until(ExpectedConditions.elementToBeClickable(onboardBtn)).click();
    }

    public void completeOnboarding(String name, String mail, String loc) {
        enterOrgName(name);
        enterEmail(mail);
        enterLocation(loc);
        clickOnboard();
    }

    // ================= STEP 2 METHODS (FIXED) =================

    public void enterPassword(String pass) {
        type(password, pass);
    }

    public void enterConfirmPassword(String pass) {
        type(confirmPassword, pass);
    }

    public void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(createAccountBtn)).click();
    }

    // ✅ FINAL STEP 2 FLOW (NO ORG ID)
    public void completeStep2(String pass) {
        enterPassword(pass);
        enterConfirmPassword(pass);
        clickCreateAccount();
    }

    // ================= VALIDATIONS =================

    public String getSuccessMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg)).getText();
    }

    public String getEmailErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailError)).getText();
    }

    public boolean isOnboardButtonEnabled() {
        return wait.until(
                ExpectedConditions.presenceOfElementLocated(onboardBtn)
        ).isEnabled();
    }

    public String getStep2EmailValue() {
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(step2Email)
        );
        return emailField.getAttribute("value");
    }
}