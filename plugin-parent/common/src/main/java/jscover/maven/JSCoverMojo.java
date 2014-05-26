package jscover.maven;

import jscover.ConfigurationCommon;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static jscover.ConfigurationCommon.NO_INSTRUMENT_PREFIX;
import static jscover.ConfigurationCommon.NO_INSTRUMENT_REG_PREFIX;
import static jscover.ConfigurationCommon.ONLY_INSTRUMENT_REG_PREFIX;
import static jscover.maven.TestType.JasmineHtmlReporter;

public abstract class JSCoverMojo extends AbstractMojo {
    private ConfigurationCommon defaults = new ConfigurationCommon();
    protected WebDriver webClient;

    //JSCover Common Parameters
    @Parameter
    protected boolean includeBranch = defaults.isIncludeBranch();
    @Parameter
    protected boolean includeFunction = defaults.isIncludeFunction();
    @Parameter
    protected boolean localStorage = defaults.isLocalStorage();
    @Parameter
    protected final List<String> instrumentPathArgs = new ArrayList<String>();
    @Parameter
    protected File reportDir = new File("target/reports/jscover-maven");
    @Parameter
    protected int JSVersion = defaults.getJSVersion();

    //Test Parameters
    @Parameter(required = true)
    protected File testDirectory = new File("src/test/javascript/spec");
    @Parameter(required = true)
    protected String testIncludes = "*.html";
    @Parameter
    protected String testExcludes;
    @Parameter
    protected TestType testType = JasmineHtmlReporter;
    @Parameter(required = true)
    protected int lineCoverageMinimum;
    @Parameter
    protected int branchCoverageMinimum;
    @Parameter
    protected int functionCoverageMinimum;
    @Parameter
    protected String webDriverClassName = PhantomJSDriver.class.getName();
    @Parameter
    protected Properties systemProperties = new Properties();
    @Parameter
    protected boolean reportLCOV;
    @Parameter
    protected boolean reportCoberturaXML;

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
            return FileUtils.getFiles(testDirectory, testIncludes, testExcludes);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem finding test pages", e);
        }
    }

    protected WebDriverRunner getWebDriverRunner() {
        WebDriverRunner webDriverRunner = new JasmineHtmlReporterWebDriverRunner();
        if (TestType.QUnit == testType) {
            webDriverRunner = new QUnitWebDriverRunner();
        } else if (TestType.JasmineTrivialReporter == testType) {
            webDriverRunner = new JasmineTrivialReporterWebDriverRunner();
        }
        return webDriverRunner;
    }

    protected WebDriver getWebClient() {
        Class<WebDriver> webDriverClass = getWebDriverClass();
        try {
            try {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--allow-file-access-from-files");
                webClient = webDriverClass.getConstructor(ChromeOptions.class).newInstance(options);
                return webClient;
            } catch (final NoSuchMethodException e) {}
            try {
                webClient = webDriverClass.getConstructor(Capabilities.class).newInstance(getDesiredCapabilities());
                return webClient;
            } catch (final NoSuchMethodException e) {
                return webClient = webDriverClass.newInstance();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Capabilities getDesiredCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        return capabilities;
    }

    @SuppressWarnings("unchecked")
    protected Class<WebDriver> getWebDriverClass() {
        try {
            return (Class<WebDriver>) Class.forName(webDriverClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopWebClient() {
        try {
            webClient.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            webClient.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void setCommonConfiguration(ConfigurationCommon config) {
        config.setIncludeBranch(includeBranch);
        config.setIncludeFunction(includeFunction);
        config.setLocalStorage(localStorage);
        config.setJSVersion(JSVersion);
        for (String instrumentArg : instrumentPathArgs) {
            if (instrumentArg.startsWith(NO_INSTRUMENT_PREFIX)) {
                config.addNoInstrument(instrumentArg);
            } else if (instrumentArg.startsWith(NO_INSTRUMENT_REG_PREFIX)) {
                config.addNoInstrumentReg(instrumentArg);
            } else if (instrumentArg.startsWith(ONLY_INSTRUMENT_REG_PREFIX)) {
                config.addOnlyInstrumentReg(instrumentArg);
            }
        }
    }
}