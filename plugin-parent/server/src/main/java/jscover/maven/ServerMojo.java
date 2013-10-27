package jscover.maven;

import jscover.ConfigurationCommon;
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

    @Parameter
    private int port = defaults.getPort();
    @Parameter
    private boolean includeUnloadedJS = defaults.isIncludeUnloadedJS();
    @Parameter
    private File documentRoot = defaults.getDocumentRoot();

    public void execute() throws MojoExecutionException, MojoFailureException {
        setSystemProperties();
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
        new TestRunner(getWebClient(), getWebDriverRunner(), config, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML).runTests(getTestFiles());
    }

    private ConfigurationForServer getConfigurationForServer() {
        ConfigurationForServer config = new ConfigurationForServer();
        //Common parameters
        setCommonConfiguration(config);
        //Server parameters
        config.setDocumentRoot(documentRoot);
        config.setPort(port);
        config.setIncludeUnloadedJS(includeUnloadedJS);
        config.setReportDir(reportDir);
        return config;
    }
}
