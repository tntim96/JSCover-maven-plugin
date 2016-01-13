package jscover.maven;

import org.apache.maven.plugin.MojoExecutionException;

public enum TestType {
    QUnit, Jasmine, Mocha, Custom;

    public WebDriverRunner getWebDriverRunner() throws MojoExecutionException {
        if (this == QUnit) {
            return new QUnitWebDriverRunner();
        } else if (this == Jasmine) {
            return new JasmineWebDriverRunner();
        } else if (this == Mocha) {
            return new MochaWebDriverRunner();
        }
        throw new MojoExecutionException("Please provide a custom test type class that implements "+WebDriverRunner.class.getName());
    }
}
