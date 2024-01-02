JSCover-maven-plugin
====================
[![Build Status](https://github.com/tntim96/JSCover-maven-plugin/workflows/Java-CI/badge.svg)](https://github.com/tntim96/JSCover-maven-plugin/actions?query=workflow%3A%22Java-CI%22)
[![codecov](https://codecov.io/gh/tntim96/JSCover-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/tntim96/JSCover-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tntim96/jscover-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tntim96/jscover-maven-plugin)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-green.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

Maven plugins for [JSCover](http://tntim96.github.io/JSCover/).

This plugin has the following goals:
* `jscover-file:instrument` instrument the JavaScript on the file-system
* `jscover-file:jscover` run the JavaScript tests and generate JavaScript code coverage using `file-mode`
* `jscover-server:jscover` run the JavaScript tests and generate JavaScript code coverage using `server-mode`

Automatic running of tests currently supports:
* Server and file-mode plugins (generate your coverage reports **without ever running a server**)
* QUnit, Jasmine, Jasmine2, Mocha and custom test frameworks
* WebDriver drivers (Firefox, Chrome)
* Coverage thresholds
* Multiple report formats (JSCover HTML, LCOV, Cobertura XML)
* Ant style test inclusion and exclusions

Requires Java 11

See the server configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/server).

See the file configuration
[here](https://github.com/tntim96/JSCover-maven-plugin/tree/master/plugin-parent/file-system).

## Working examples

See [JSCover-Samples](https://github.com/tntim96/JSCover-Samples).


## Releasing

`mvn -DperformRelease=true clean deploy`

In case of error 'gpg: signing failed: Inappropriate ioctl for device' run

`export GPG_TTY=$(tty)`

## Snapshots

[Development snapshots on sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/github/tntim96/).

## Verifying signatures
Generate signatures:

`mvn clean install`

Verify:

`find . -name "*.asc" -exec gpg -v --verify '{}' \;`

Count signatures:

`find . -name "*.asc" | wc -l`

## Checking for dependency/plugin updates

`mvn versions:display-dependency-updates`

`mvn versions:display-plugin-updates`

## Running tests

`mvn clean && mvn clean verify && mvn surefire-report:report-only -Daggregate=true`
