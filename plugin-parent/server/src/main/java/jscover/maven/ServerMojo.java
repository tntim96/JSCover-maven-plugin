package jscover.maven;

import jscover.Main;
import jscover.server.ConfigurationForServer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "jscover", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ServerMojo extends JSCoverMojo {
    private ConfigurationForServer defaults = new ConfigurationForServer();

    @Parameter
    private int port = defaults.getPort();
    @Parameter
    private File documentRoot = defaults.getDocumentRoot();
    @Parameter
    protected int timeOutSecondsForSave = 15;

    public void execute() throws MojoExecutionException, MojoFailureException {
        setSystemProperties();
        final ConfigurationForServer config = getConfigurationForServer();
        config.validate();
        if (config.isInvalid())
            throw new MojoExecutionException("Invalid configuration");

        final Main main = new Main();
        Runnable jsCover = () -> {
            main.initialize();
            main.runServer(config);
        };
        Thread jsCoverThread = new Thread(jsCover);
        jsCoverThread.start();
        getLog().info("Started JSCover server");
        try {
            ServerTestRunner serverTestRunner = new ServerTestRunner(getWebDriver(), getWebDriverRunner(), config, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML, timeOutSecondsForSave);
            serverTestRunner.runTests(getTestFiles());
        } finally {
            stopWebClient();
            main.stop();
            getLog().info("Stopped JSCover server");
        }
    }

    private ConfigurationForServer getConfigurationForServer() throws MojoExecutionException {
        ConfigurationForServer config = new ConfigurationForServer();
        //Common parameters
        setCommonConfiguration(config);
        //Server parameters
        config.setDocumentRoot(documentRoot);
        config.setPort(port);
        config.setReportDir(reportDir);
        return config;
    }
}
