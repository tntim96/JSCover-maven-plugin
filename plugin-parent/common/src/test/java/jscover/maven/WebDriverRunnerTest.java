package jscover.maven;

import org.junit.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;

public class WebDriverRunnerTest {
    protected WebDriver webDriver = new PhantomJSDriver();

    @After
    public void tearDown() {
        try {
            webDriver.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
