[![Build Status](https://ci.jenkins.io/job/Plugins/job/typetalk-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/typetalk-plugin/job/master/)

This is a Jenkins plugin which notifies to [Typetalk](https://typetalk.com/).
See [Wiki page](https://wiki.jenkins-ci.org/display/JENKINS/Typetalk+Plugin) for more details.

# How to build
- ref : https://wiki.jenkins-ci.org/display/JENKINS/Gradle+JPI+Plugin

## Launch for development

```
gradle clean server
```

## Package a plugin file

```
gradle clean jpi
```

If you don't install gradle, you can use gradlew / gradlew.bat instead of gradle.
