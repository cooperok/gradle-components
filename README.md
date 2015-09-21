# gradle-components
Example project to show how to separate duplicated code and duplicated gradle configurations into jars and gradle plugins

## Artifactory properties
Properties for Gradle are not set inside project. You can add them globally to Gradle inside ~/.gradle/gradle.properties file. For example

```
ArtifactoryUrl=http://localhost:8081
ArtifactoryUser=admin
ArtifactoryPassword=password
```

## How to use
To compile all components and publish them you can run this command
```
./gradlew assemble test compileComponents artifactoryPublish
```

You can add dependency for every component inside another project this way

```
buildscript {
    repositories {
        jcenter()
        maven {
            url "$ArtifactoryUrl/libs-release-local"
            credentials {
                username = ArtifactoryUser
                password = ArtifactoryPassword
            }
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.0'
        classpath 'com.example:android-library-publishing-plugin:1.0'
        classpath 'com.example:android-signing-plugin:1.0'
        classpath 'com.example:android-base-plugin:1.0'
    }
}

allprojects {
    repositories {
        jcenter()
        maven {
            url "$ArtifactoryUrl/libs-release-local"
            credentials {
                username = ArtifactoryUser
                password = ArtifactoryPassword
            }
        }
    }
}

dependencie {
    compile 'com.example:util:1.0'
    compile 'com.example:logger:1.0'
}

apply plugin: 'android-library-publishing'
apply plugin: 'android-signing'
apply plugin: 'android-base'
```