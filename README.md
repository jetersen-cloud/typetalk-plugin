[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/typetalk-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/typetalk-plugin/)

This is a Jenkins plugin which notifies to [Typetalk](https://typetalk.in/).
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

# Restriction

Now you cannot build this plugin with JDK8.
You may be able to build it with JDK8 after updating "Gradle JPI Plugin".
