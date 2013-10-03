package jscover.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "jscover", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JSCoverMojo extends AbstractMojo {
    @Parameter
    private String[] args;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("args = " + args);
    }
}
