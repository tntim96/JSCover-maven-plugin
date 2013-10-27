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
public class ServerMojo extends JSCoverMojo {
    private ConfigurationForServer defaults = new ConfigurationForServer();

    //JSCover Server Parameters
    @Parameter
    private int port = defaults.getPort();
    @Parameter
    private boolean includeUnloadedJS = defaults.isIncludeUnloadedJS();
    @Parameter
    private File documentRoot = defaults.getDocumentRoot();

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (Object key : systemProperties.keySet()) {
            System.setProperty((String) key, (String) systemProperties.get(key));
        }
        final ConfigurationForServer config = getConfigurationForServer();

        Runnable jsCover = new Runnable() {
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

    protected WebDriverRunner getWebDriverRunner() {
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
