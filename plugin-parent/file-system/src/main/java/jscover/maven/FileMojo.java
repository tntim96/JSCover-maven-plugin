package jscover.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

//@Mojo(name = "file", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
@Mojo(name = "file", requiresDirectInvocation = true)
public class FileMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
