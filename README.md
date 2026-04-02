# SwingBrowser

Bundles for the swingbrowser-api.

To release:
* git checkout master; git pull;
* git checkout develop;
* mvn jgitflow:release-start;
* mvn jgitflow:release-finish;
* git push --tags; git push --all

## Introduction

The Swingbrowser API is developed to make HTML browsing plugable in Java. You can choose for:
the jxbrowser software from [TeamDev](https://teamdev.com/jxbrowser/), an external browser or, rather old code, the Java-FX WebView component.

## Prerequisites

For the jxbrowser software you need a license key. In github, create a secret `LICENSE` This will be used in the workflow action.
In Maven it is the `jxbrowser.license.key` property.

## Contents 

### Folder structure

There are 5 folders among them the api and a fork of the org.eclipse.equinox.html bundle implementing the OSGi http service api. 

### File formats 

It's all Java, with maven as building engine.

## Usage

In the numworx authoring environment, the swingbrowser api is used to display the html5 player pages of all activities.

## License

* The JxBrowser software is licensed as in https://teamdev.com/jxbrowser/individual-license-agreement/
* The OSGi html service implementation has an [Eclipse Public License Version 1.0](http://www.eclipse.org/legal/epl-v10.html)
* All others copyright Utrecht University (2025). All rights reserved. 

## Contact 

[Wim van Velthoven](mailto:w.p.g.vanvelthoven@uu.nl)
