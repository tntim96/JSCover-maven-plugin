package jscover.maven;

import static jscover.maven.TestType.Jasmine;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class JSCoverMojo extends JSCoverMojoBase {
    protected WebDriver webDriver;

    //Test Parameters
    @Parameter
    protected String testIncludes = "*.html";
    @Parameter
    protected String testExcludes;
    @Parameter
    protected TestType testType = Jasmine;
    @Parameter
    protected int lineCoverageMinimum;
    @Parameter
    protected int branchCoverageMinimum;
    @Parameter
    protected int functionCoverageMinimum;
    @Parameter
    protected String webDriverClassName = ChromeDriver.class.getName();
    @Parameter
    protected String testRunnerClassName;
    @Parameter
    protected Properties systemProperties = new Properties();
    @Parameter
    protected boolean reportLCOV;
    @Parameter
    protected boolean reportCoberturaXML;
    @Parameter
    protected int timeOutSeconds = 10;
    @Parameter
    protected String httpProxy;

    protected void setSystemProperties() {
        for (Object key : systemProperties.keySet()) {
            System.setProperty((String) key, (String) systemProperties.get(key));
        }
    }

    protected List<File> getTestFiles() throws MojoExecutionException {
        return getTestFiles(testDirectory);
    }

    protected List<File> getTestFiles(File testDirectory) throws MojoExecutionException {
        try {
            List<File> files = FileUtils.getFiles(testDirectory, testIncludes, testExcludes);
            if (files.isEmpty()) {
                throw new MojoExecutionException("No tests found in " + testDirectory.getAbsolutePath() + ". Includes:" + testIncludes + ", Excludes:" + testExcludes);
            }
            return files;
        } catch (IOException|IllegalStateException e) {
            throw new MojoExecutionException("Problem finding test pages", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected WebDriverRunner getWebDriverRunner() throws MojoExecutionException {
        if (testRunnerClassName != null) {
            try {
                return ((Class<WebDriverRunner>) Class.forName(testRunnerClassName)).getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
        WebDriverRunner webDriverRunner = testType.getWebDriverRunner();
        webDriverRunner.setTimeOutSeconds(timeOutSeconds);
        return webDriverRunner;
    }

    protected WebDriver getWebDriver() {
        try {
            if (webDriverClassName.contains("Chrome")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--allow-file-access-from-files");
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--headless");
                setCommonOptions(options);
                return webDriver = new ChromeDriver(options);
            } else if (webDriverClassName.contains("Firefox")) {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-headless");
                setCommonOptions(options);
                return webDriver = new FirefoxDriver(options);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Unsupported driver " + webDriverClassName);
    }


    protected void setCommonOptions(AbstractDriverOptions options) {
        if (httpProxy != null) {
            options.setProxy(new Proxy().setHttpProxy(httpProxy));
        }
    }

    public void stopWebClient() {
        try {
            webDriver.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}