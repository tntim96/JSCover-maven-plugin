JSCover-maven-plugin
====================

Maven plugins for [JSCover](http://tntim96.github.com/JSCover/).

This will automatically run the JavaScript tests and generate JavaScript code coverage reports.

Currently supports:
* Server and file-mode plugins (generate your coverage reports **without ever running a server**)
* QUnit and Jasmine test frameworks
* WebDriver drivers (e.g. PhantomJS, Firefox, Chrome, IE, etc...)
* Coverage thresholds
* Multiple report formats (JSCover HTML, LCOV, Cobertura XML)
* Ant style test inclusion and exclusions

See the server configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/server).

See the file configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/file-system).

## Working examples

See [JSCover-Samples](https://github.com/tntim96/JSCover-Samples).


## Releasing

`mvn clean source:jar javadoc:jar package gpg:sign deploy`

## Snapshots

Development snapshots available at
[snapshots on sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/github/tntim96/).

## Verifying signatures
Generate signatures:

`mvn clean source:jar javadoc:jar package gpg:sign`

Verify:

`find . -name "*.asc" -exec gpg -v --verify '{}' \;`

Count signatures:

`find . -name "*.asc" | wc -l`