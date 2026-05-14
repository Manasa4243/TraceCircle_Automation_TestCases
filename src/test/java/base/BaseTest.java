package base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;



public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // URLs
    protected String LOGIN_URL = "http://localhost:5173/login";
    protected String ONBOARD_URL = "http://localhost:5173/onboard-super-admin";
    protected String FORGOT_PASSWORD_URL = "http://localhost:5173/forget-password";
    protected String VERIFY_OTP_URL = "http://localhost:5173/verify-otp";
    protected String RESET_PASSWORD_URL = "http://localhost:5173/new-password";
    protected String GTIN_URL = "http://localhost:5173/gtin/add/";
    protected String PLANTS_URL = "http://localhost:5173/plants";

    @BeforeMethod
    public void setup() {

        // Automatically downloads matching ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Chrome options for CI/CD
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

        // Launch browser
        driver = new ChromeDriver(options);

        // Maximize browser
        driver.manage().window().maximize();

        // Explicit wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Delete cookies
        driver.manage().deleteAllCookies();
    }

    // Open Login Page
    protected void openLoginPage() {
        driver.get(LOGIN_URL);
    }

    // Open Onboarding Page
    protected void openOnboardPage() {
        driver.get(ONBOARD_URL);
    }

    // Open Forgot Password Page
    protected void openForgotPasswordPage() {
        driver.get(FORGOT_PASSWORD_URL);
    }

    // Open OTP Page
    protected void openOTPPage() {
        driver.get(VERIFY_OTP_URL);
    }

    // Open Reset Password Page
    protected void openResetPasswordPage() {
        driver.get(RESET_PASSWORD_URL);
    }

    // Open Plants Page
    protected void openPlantsPage() {
        driver.get(PLANTS_URL);
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}