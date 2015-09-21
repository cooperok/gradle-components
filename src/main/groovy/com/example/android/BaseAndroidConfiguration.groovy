package com.example.android;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Плагин устанавливает базовые настройки Android приложений
 */
public class BaseAndroidConfiguration implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.android {

            compileSdkVersion 21
            buildToolsVersion "22.0.1"

            defaultConfig {
                minSdkVersion 15
                targetSdkVersion 21
            }

            //Меняем имя скомпилированного приложения
            if (it.hasProperty('applicationVariants')) {
                applicationVariants.all { variant ->
                    variant.outputs.each { output ->
                        def outputFile = output.outputFile
                        if (outputFile != null && outputFile.name.endsWith('.apk')) {
                            def manifest = new XmlSlurper().parse(project.android.sourceSets.main.manifest.srcFile)
                            def versionName = manifest['@android:versionName'].text()
                            def fileName = outputFile.name.replace('.apk', "-${versionName}.apk")
                            output.outputFile = new File(outputFile.parent, fileName)
                        }
                    }
                }
            }

            if (it.hasProperty('libraryVariants')) {
                libraryVariants.all { variant ->
                    variant.outputs.each { output ->
                        def outputFile = output.outputFile
                        if (outputFile != null && outputFile.name.endsWith('.aar')) {
                            def manifest = new XmlSlurper().parse(project.android.sourceSets.main.manifest.srcFile)
                            def versionName = manifest['@android:versionName'].text()
                            def fileName = outputFile.name.replace('.aar', "-${versionName}.aar")
                            output.outputFile = new File(outputFile.parent, fileName)
                        }
                    }
                }
            }

            buildTypes {
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
                }
            }

            packagingOptions {
                exclude 'META-INF/NOTICE.txt'
                exclude 'META-INF/LICENSE.txt'
            }

            lintOptions {
                abortOnError false
            }

        }
    }

}