JSCover-file-maven-plugin
=========================

## Working examples

See [JSCover-Samples](https://github.com/tntim96/JSCover-Samples).


## Configuration

### File instrumentation
Run with `mvn jscover-file:instrument`

```XML
    <plugin>
        <groupId>com.github.tntim96</groupId>
        <artifactId>jscover-file-maven-plugin</artifactId>
        <version>2.0.1</version>
        <configuration>
            <srcDir>src</srcDir>
            <testDirectory>src/test/javascript</testDirectory>
            <instrumentPathArgs>
                <arg>--no-instrument=main/webapp/js/vendor/</arg>
                <arg>--no-instrument=test</arg>
            </instrumentPathArgs>
            <excludeArgs>
                <excludeArg>--exclude=main/java</excludeArg>
                <excludeArg>--exclude=main/resources</excludeArg>
            </excludeArgs>
        </configuration>
    </plugin>
```

### File instrumentation and run tests

Run with `mvn jscover-file:jscover`

```XML
    <plugin>
        <groupId>com.github.tntim96</groupId>
        <artifactId>jscover-file-maven-plugin</artifactId>
        <version>2.0.1</version>
        <configuration>
            <srcDir>src</srcDir>
            <testDirectory>src/test/javascript</testDirectory>
            <testIncludes>qunit-*.html</testIncludes>
            <testType>QUnit</testType>
            <!--
            <testType>Jasmine</testType>
            <testType>Mocha</testType>
            <testType>Custom</testType>
            <testRunnerClassName>org.your.class.WebDriverRunner</webDriverClassName>
            -->
            <lineCoverageMinimum>82</lineCoverageMinimum>
            <branchCoverageMinimum>48</branchCoverageMinimum>
            <functionCoverageMinimum>70</functionCoverageMinimum>
            <timeOutSeconds>10</timeOutSeconds>
            <instrumentPathArgs>
                <arg>--no-instrument=main/webapp/js/vendor/</arg>
                <arg>--no-instrument=test</arg>
            </instrumentPathArgs>
            <excludeArgs>
                <excludeArg>--exclude=main/java</excludeArg>
                <excludeArg>--exclude=main/resources</excludeArg>
            </excludeArgs>
            <reportCoberturaXML>true</reportCoberturaXML>
            <reportLCOV>true</reportLCOV>
            <webDriverClassName>org.openqa.selenium.firefox.FirefoxDriver</webDriverClassName>
            <!--<webDriverClassName>org.openqa.selenium.htmlunit.HtmlUnitDriver</webDriverClassName>-->
            <!--
            <webDriverClassName>org.openqa.selenium.chrome.ChromeDriver</webDriverClassName>
            <systemProperties>
                <property>
                    <name>webdriver.chrome.driver</name>
                    <value>C:/bin/chromedriver.exe</value>
                </property>
            </systemProperties>
            -->
            <!--
            <webDriverClassName>org.openqa.selenium.edge.EdgeDriver</webDriverClassName>
            <systemProperties>
                <property>
                    <name>webdriver.edge.driver</name>
                    <value>C:/bin/msedgedriver.exe</value>
                </property>
            </systemProperties>
            -->
        </configuration>
    </plugin>
```

Note: the `systemProperties` property is only required if the driver file is not on the executable path