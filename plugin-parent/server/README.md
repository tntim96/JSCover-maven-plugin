JSCover-server-maven-plugin
===========================

Run with `mvn jscover-server:server`

```XML
    <plugin>
        <groupId>com.github.tntim96</groupId>
        <artifactId>jscover-server-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <testDirectory>./src/test/javascript</testDirectory>
            <testIncludes>index.html</testIncludes>
            <testType>QUnit</testType>
            <!--
            <testType>JasmineHtmlReporter</testType>
            <testType>JasmineTrivialReporter</testType>
            <testType>Custom</testType>
            <testRunnerClassName>org.your.class.WebDriverRunner</webDriverClassName>
            -->
            <lineCoverageMinimum>82</lineCoverageMinimum>
            <branchCoverageMinimum>48</branchCoverageMinimum>
            <functionCoverageMinimum>70</functionCoverageMinimum>
            <instrumentPathArgs>
                <arg>--no-instrument=target</arg>
            </instrumentPathArgs>
            <includeUnloadedJS>true</includeUnloadedJS>
            <reportCoberturaXML>true</reportCoberturaXML>
            <reportLCOV>true</reportLCOV>
            <!--<webDriverClassName>org.openqa.selenium.firefox.FirefoxDriver</webDriverClassName>-->
            <!--<webDriverClassName>org.openqa.selenium.htmlunit.HtmlUnitDriver</webDriverClassName>-->
            <!--
            <webDriverClassName>org.openqa.selenium.chrome.ChromeDriver</webDriverClassName>
            <systemProperties>
                <property>
                    <name>webdriver.chrome.driver</name>
                    <value>C:/java/chromedriver_win32_2.0/chromedriver.exe</value>
                </property>
            </systemProperties>
            -->
        </configuration>
    </plugin>
```

Note: the `systemProperties` property is only required if the driver file is not on the executable path