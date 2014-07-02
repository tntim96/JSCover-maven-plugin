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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

//@Mojo(name = "file", requiresDirectInvocation = true)
@Mojo(name = "file", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class FileMojo extends JSCoverMojo {
    //private ConfigurationForFS defaults = new ConfigurationForFS();
    IoUtils ioUtils = IoUtils.getInstance();

    @Parameter
    private File srcDir = new File("src");
    @Parameter
    private File destDir = new File("target/reports/jscover-maven");
    @Parameter
    protected final List<String> excludeArgs = new ArrayList<String>();

    public void execute() throws MojoExecutionException, MojoFailureException {
        setSystemProperties();
        final ConfigurationForFS config = getConfigurationForFS();

        Main main = new Main();
        main.initialize();
        main.runFileSystem(config);
        if (!ioUtils.isSubDirectory(testDirectory, srcDir))
            throw new MojoExecutionException(String.format("'testDirectory' '%s' should be a sub-directory of 'srcDir' '%s'", testDirectory, srcDir));
        String relativeDirectory = ioUtils.getRelativePath(testDirectory, srcDir);
        File testDir = new File(destDir, relativeDirectory);
        try {
            new FileTestRunner(getWebClient(), getWebDriverRunner(), config, lineCoverageMinimum, branchCoverageMinimum, functionCoverageMinimum, reportLCOV, reportCoberturaXML).runTests(getTestFiles(testDir));
        } finally {
            stopWebClient();
        }
    }

    private ConfigurationForFS getConfigurationForFS() throws MojoExecutionException {
        ConfigurationForFS config = new ConfigurationForFS();
        //Common parameters
        setCommonConfiguration(config);
        //File-System parameters
        config.setSrcDir(srcDir);
        config.setDestDir(destDir);
        for (String excludeArg : excludeArgs) {
            if (excludeArg.startsWith(ConfigurationForFS.EXLCUDE_PREFIX)) {
                config.addExclude(excludeArg);
            } else if (excludeArg.startsWith(ConfigurationForFS.EXLCUDE_REG_PREFIX)) {
                config.addExcludeReg(excludeArg);
            } else {
                throw new MojoExecutionException(format("Invalid exclude argument '%s'", excludeArg));
            }
        }
        return config;
    }
}
