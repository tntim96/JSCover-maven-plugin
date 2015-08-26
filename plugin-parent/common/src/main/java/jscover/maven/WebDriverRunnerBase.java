package jscover.maven;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public abstract class WebDriverRunnerBase implements WebDriverRunner {
    protected Log log = new SystemStreamLog();
    protected int timeOutSeconds = 1;

    public void setTimeOutSeconds(int timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
    }
}
