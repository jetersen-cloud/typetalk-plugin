[![Build Status](https://ci.jenkins.io/job/Plugins/job/typetalk-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/typetalk-plugin/job/master/)

This is a Jenkins plugin which notifies to [Typetalk](https://typetalk.com/).
See [Plugin page](https://plugins.jenkins.io/typetalk/) for more details.

# How to build
- ref : https://www.jenkins.io/doc/developer/plugin-development/build-process/

## Launch for development

```
./mvnw hpi:run
```

## Package a plugin file

```
./mvnw clean hpi:hpi
```

If you don't install gradle, you can use gradlew / gradlew.bat instead of gradle.
