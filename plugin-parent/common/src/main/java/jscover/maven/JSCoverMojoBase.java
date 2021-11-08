package jscover.maven;

import com.google.javascript.jscomp.CompilerOptions;
import jscover.ConfigurationCommon;
import jscover.filesystem.ConfigurationForFS;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static jscover.ConfigurationCommon.*;

public abstract class JSCoverMojoBase extends AbstractMojo {
    protected ConfigurationCommon defaults = new ConfigurationCommon();
    //JSCover Common Parameters
    @Parameter
    protected boolean includeBranch = defaults.isIncludeBranch();
    @Parameter
    protected boolean includeFunction = defaults.isIncludeFunction();
    @Parameter
    protected boolean localStorage = true;
    @Parameter
    protected boolean includeUnloadedJS = defaults.isIncludeUnloadedJS();
    @Parameter
    protected final List<String> instrumentPathArgs = new ArrayList<String>();
    @Parameter
    protected File reportDir = new File("target/reports/jscover-maven");
    @Parameter
    protected String ECMAVersion = defaults.getECMAVersion().name();
    @Parameter
    protected boolean detectCoalesce = defaults.isDetectCoalesce();


    //Test Parameters
    @Parameter(required = true)
    protected File testDirectory = new File("src/test/javascript/spec");

    protected void setCommonConfiguration(ConfigurationCommon config) throws MojoExecutionException {
        config.setIncludeBranch(includeBranch);
        config.setIncludeFunction(includeFunction);
        config.setLocalStorage(localStorage);
        config.setIncludeUnloadedJS(includeUnloadedJS);
        config.setECMAVersion(CompilerOptions.LanguageMode.valueOf(ECMAVersion));
        config.setDetectCoalesce(detectCoalesce);
        for (String instrumentArg : instrumentPathArgs) {
            if (instrumentArg.startsWith(NO_INSTRUMENT_PREFIX)) {
                config.addNoInstrument(instrumentArg);
            } else if (instrumentArg.startsWith(NO_INSTRUMENT_REG_PREFIX)) {
                config.addNoInstrumentReg(instrumentArg);
            } else if (instrumentArg.startsWith(ONLY_INSTRUMENT_REG_PREFIX)) {
                config.addOnlyInstrumentReg(instrumentArg);
            } else {
                throw new MojoExecutionException(format("Invalid instrument path option '%s'", instrumentArg));
            }
        }
    }


    protected ConfigurationForFS getConfigurationForFS(File srcDir, List<String> excludeArgs) throws MojoExecutionException {
        ConfigurationForFS config = new ConfigurationForFS();
        //Common parameters
        setCommonConfiguration(config);
        //File-System parameters
        config.setSrcDir(srcDir);
        config.setDestDir(reportDir);
        for (String excludeArg : excludeArgs) {
            if (excludeArg.startsWith(ConfigurationForFS.EXCLUDE_PREFIX)) {
                config.addExclude(excludeArg);
            } else if (excludeArg.startsWith(ConfigurationForFS.EXCLUDE_REG_PREFIX)) {
                config.addExcludeReg(excludeArg);
            } else {
                throw new MojoExecutionException(format("Invalid exclude argument '%s'", excludeArg));
            }
        }
        return config;
    }
}
