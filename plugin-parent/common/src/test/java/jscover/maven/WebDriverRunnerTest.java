package jscover.maven;

import org.junit.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class WebDriverRunnerTest {
    protected WebDriver webDriver = getWebDriver();

    private WebDriver getWebDriver() {
        try {
            @SuppressWarnings(value = "unchecked")
            Class<WebDriver> webDriverClass = (Class<WebDriver>) Class.forName(System.getProperty("webDriverClass"));
            return webDriverClass.newInstance();
        } catch(Exception e) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("headless");
            return new ChromeDriver(options);
        }
    }

    @After
    public void tearDown() {
        try {
            webDriver.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    protected String getFilePath(String pathname) {
        if (System.getProperty("user.dir").endsWith("JSCover-maven-plugin"))
            pathname = "plugin-parent/common/" + pathname;
        return "file:///" + new File(pathname).getAbsolutePath().replaceAll("\\\\", "/");
    }
}
