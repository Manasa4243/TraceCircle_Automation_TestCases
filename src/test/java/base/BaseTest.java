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

    // ✅ ADD: URLs
    protected String ONBOARD_URL = "http://localhost:5173/onboard-super-admin";
    protected String LOGIN_URL = "http://localhost:5173/login";

    @BeforeMethod
    public void setup() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        // ✅ Proper wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // ✅ Clean state
        driver.manage().deleteAllCookies();

        // 🔥 DEFAULT (keep your existing behavior)
        driver.get(ONBOARD_URL);
    }

    // ✅ ADD: reusable method to switch URL
    protected void openLoginPage() {
        driver.get(LOGIN_URL);
    }

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