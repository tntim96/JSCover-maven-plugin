package jscover.maven;

import jscover.Main;
import jscover.server.ConfigurationForServer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static jscover.ConfigurationCommon.*;

@Mojo(name = "server", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ServerMojo extends AbstractMojo {
    private ConfigurationForServer defaults = new ConfigurationForServer();

    //JSCover Common Parameters
    @Parameter
    private boolean includeBranch = defaults.isIncludeBranch();
    @Parameter
    private boolean includeFunction = defaults.isIncludeFunction();
    @Parameter
    private boolean localStorage = defaults.isLocalStorage();
    @Parameter
    private final List<String> instrumentPathArgs = new ArrayList<String>();
    @Parameter
    private File reportDir = new File("target/reports/jscover-maven");
    @Parameter
    private int JSVersion = defaults.getJSVersion();

    //JSCover Server Parameters
    @Parameter
    private int port = defaults.getPort();
    @Parameter
    private boolean includeUnloadedJS = defaults.isIncludeUnloadedJS();
    @Parameter
    private File documentRoot = defaults.getDocumentRoot();

    //Test Parameters
    @Parameter(required = true)
    private File testDirectory = new File("src/test/javascript/spec");
    @Parameter(required = true)
    private String testIncludes = "*.html";
    @Parameter
    private String testExcludes;
    @Parameter
    private String testType = "Jasmine";
    @Parameter(required = true)
    private int lineCoverageMinimum;
    @Parameter
    private int branchCoverageMinimum;
    @Parameter
    private int functionCoverageMinimum;
    @Parameter
    private String webDriverClassName = PhantomJSDriver.class.getName();
    @Parameter
    private Properties systemProperties = new Properties();
    @Parameter
    private boolean reportLCOV;
    @Parameter
    private boolean reportCoberturaXML;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        for (Object key : systemProperties.keySet()) {
            System.setProperty((String) key, (String) systemProperties.get(key));
        }
        final ConfigurationForServer config = getConfigurationForServer();

        Runnable jsCover = new Runnable() {
            @Override
            public void run() {
                try {
                    Main main = new Main();
                    main.initialize();
                    main.runServer(config);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Thread jsCoverThread = new Thread(jsCover);
        jsCoverThread.start();

        List<File> testPages = null;
        try {
            testPages = FileUtils.getFiles(testDirectory, testIncludes, testExcludes);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem finding test pages", e);
        }
        new TestRunner(getWebClient(), getWebDriverRunner(), config, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML).runTests(testPages);
    }

    private WebDriver getWebClient() {
        Class<WebDriver> webDriverClass = getWebDriverClass();
        try {
            try {
                return webDriverClass.getConstructor(Capabilities.class).newInstance(getDesiredCapabilities());
            } catch (final NoSuchMethodException e) {
                return webDriverClass.newInstance();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Capabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        return desiredCapabilities;
    }

    private Class<WebDriver> getWebDriverClass() {
        try {
            return (Class<WebDriver>) Class.forName(webDriverClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private WebDriverRunner getWebDriverRunner() {
        WebDriverRunner webDriverRunner = new JasmineWebDriverRunner();
        if ("QUnit".equalsIgnoreCase(testType)) {
            webDriverRunner = new QUnitWebDriverRunner();
        }
        return webDriverRunner;
    }

    private ConfigurationForServer getConfigurationForServer() {
        ConfigurationForServer config = new ConfigurationForServer();
        //Common parameters
        config.setIncludeBranch(includeBranch);
        config.setIncludeFunction(includeFunction);
        config.setLocalStorage(localStorage);
        config.setReportDir(reportDir);
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
        //Server parameters
        config.setDocumentRoot(documentRoot);
        config.setPort(port);
        config.setIncludeUnloadedJS(includeUnloadedJS);
        return config;
    }
}
