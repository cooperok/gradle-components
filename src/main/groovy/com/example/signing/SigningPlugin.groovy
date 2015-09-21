package com.example.signing

import org.gradle.api.*

class SigningPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        //Загружаем значения для подписи приложения
        if (project.hasProperty("debugSigningPropertiesPath") && project.hasProperty("releaseSigningPropertiesPath")) {

            //Файлы в которых хранятся значения для подписи
            File debugPropsFile = new File(System.getenv('HOME') + "/" + project.property("debugSigningPropertiesPath"))
            File releasePropsFile = new File(System.getenv('HOME') + "/" + project.property("releaseSigningPropertiesPath"))

            if (debugPropsFile.exists() && releasePropsFile.exists()) {
                Properties debugProps = new Properties()
                debugProps.load(new FileInputStream(debugPropsFile))

                Properties releaseProps = new Properties()
                releaseProps.load(new FileInputStream(releasePropsFile))

                //Дописываем в конфиг загруженные значения
                project.android.signingConfigs {
                    debug {
                        storeFile project.file(debugPropsFile.getParent() + "/" + debugProps['keystore'])
                        storePassword debugProps['keystore.password']
                        keyAlias debugProps['keyAlias']
                        keyPassword debugProps['keyPassword']
                    }
                    release {
                        storeFile project.file(releasePropsFile.getParent() + "/" + releaseProps['keystore'])
                        storePassword releaseProps['keystore.password']
                        keyAlias releaseProps['keyAlias']
                        keyPassword releaseProps['keyPassword']
                    }
                }
                project.android.buildTypes {
                    debug {
                        signingConfig project.android.signingConfigs.debug
                    }
                    release {
                        signingConfig project.android.signingConfigs.release
                    }
                }
            }

        }

    }

}