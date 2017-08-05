JSCover-report-maven-plugin
=========================

## Working examples

See [JSCover-Samples](https://github.com/tntim96/JSCover-Samples).


## Configuration

### Merge
Run with `mvn jscover-report:merge`

```XML
<plugin>
    <groupId>com.github.tntim96</groupId>
    <artifactId>jscover-report-maven-plugin</artifactId>
    <version>2.0.1</version>
    <executions>
        <execution>
            <phase>post-integration-test</phase>
            <goals>
                <goal>merge</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <destDir>target/merged</destDir>
        <mergeDirStrings>
            <mergeDirString>target/report1</mergeDirString>
            <mergeDirString>target/report2</mergeDirString>
        </mergeDirStrings>
    </configuration>
</plugin>
```

### Report Format Conversion

Run with `mvn jscover-report:format`

```XML
<plugin>
    <groupId>com.github.tntim96</groupId>
    <artifactId>jscover-report-maven-plugin</artifactId>
    <version>1.0.16-SNAPSHOT</version>
    <executions>
        <execution>
            <phase>post-integration-test</phase>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <reportDir>target/merged</reportDir>
        <reportDir>src/main/webapp/js</reportDir>
        <coberturaXML>true</coberturaXML>
    </configuration>
</plugin>
```

Note: the `systemProperties` property is only required if the driver file is not on the executable path