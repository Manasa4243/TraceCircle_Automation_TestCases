package base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // ✅ URLs
     
    protected String LOGIN_URL = "http://localhost:5173/login";
   protected String ONBOARD_URL = "http://localhost:5173/onboard-super-admin";

    @BeforeMethod
    public void setup() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        driver.manage().deleteAllCookies();

        // ❌ Do NOT open any URL here
        // Each test class will decide which page to open
    }

    // ✅ Open Login page
    protected void openLoginPage() {
        driver.get(LOGIN_URL);
    }

    // ✅ Open Onboarding page
    protected void openOnboardPage() {
        driver.get(ONBOARD_URL);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {

        Thread.sleep(5000);

        if (driver != null) {
            driver.quit();
        }
    }
}