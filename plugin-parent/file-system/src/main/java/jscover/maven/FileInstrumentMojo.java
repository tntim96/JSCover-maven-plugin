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

@Mojo(name = "instrument", defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES, threadSafe = true)
public class FileInstrumentMojo extends JSCoverMojoBase {
    //private ConfigurationForFS defaults = new ConfigurationForFS();
    IoUtils ioUtils = IoUtils.getInstance();

    @Parameter
    private File srcDir = new File("src");
    @Parameter
    protected final List<String> excludeArgs = new ArrayList<String>();

    public void execute() throws MojoExecutionException, MojoFailureException {
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
    }

}
