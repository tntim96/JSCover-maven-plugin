package jscover.maven;

import jscover.Main;
import jscover.filesystem.ConfigurationForFS;
import jscover.util.IoUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//@Mojo(name = "file", requiresDirectInvocation = true)
@Mojo(name = "jscover", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class FileMojo extends JSCoverMojo {
    //private ConfigurationForFS defaults = new ConfigurationForFS();
    private IoUtils ioUtils = IoUtils.getInstance();

    @Parameter
    private File srcDir = new File("src");
    @Parameter
    protected final List<String> excludeArgs = new ArrayList<String>();

    public void execute() throws MojoExecutionException, MojoFailureException {
        setSystemProperties();
        final ConfigurationForFS config = getConfigurationForFS(srcDir, excludeArgs);
        config.validate();
        if (config.isInvalid())
            throw new MojoExecutionException("Invalid configuration");

        if (!ioUtils.isSubDirectory(testDirectory, srcDir))
            throw new MojoExecutionException(String.format("'testDirectory' '%s' should be a sub-directory of 'srcDir' '%s'", testDirectory, srcDir));
        Main main = new Main();
        main.initialize();
        main.runFileSystem(config);
        getLog().info("Ran JSCover instrumentation");
        String relativeDirectory = ioUtils.getRelativePath(testDirectory, srcDir);
        File testDir = new File(reportDir, relativeDirectory);
        try {
            new FileTestRunner(getWebClient(), getWebDriverRunner(), config, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML).runTests(getTestFiles(testDir));
        } finally {
            stopWebClient();
        }
    }

}
