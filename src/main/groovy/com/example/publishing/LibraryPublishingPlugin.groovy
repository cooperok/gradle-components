package com.example.publishing;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication

/**
 * Плагин добавляет возможность публиковать скомпилированные aar библиотеки
 */
class LibraryPublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //Определяем свойства, которые можно будет переопределить на проекте
        project.metaClass.GROUP_ID = 'com.example'
        project.metaClass.DEBUG_PREFIX = '-dev'
        project.metaClass.PUBLICATION_PROPERTIES = ['qa.level': 'basic', 'dev.team': 'core']

        project.publishing {
            publications {
                project.android.buildTypes.all { variant ->
                    "${variant.name}Aar"(MavenPublication) {

                        def manifest = new XmlSlurper().parse(project.android.sourceSets.main.manifest.srcFile);
                        def packageName = manifest['@package'].text()
                        def libVersion = manifest['@android:versionName'].text()
                        def artifactName = project.getName()

                        groupId project.GROUP_ID
                        version = libVersion
                        artifactId variant.name == 'debug' ? artifactName + project.DEBUG_PREFIX : artifactName

                        // Tell maven to prepare the generated "*.aar" file for publishing
                        artifact("$project.buildDir/outputs/aar/${project.getName()}-${variant.name}-${libVersion}.aar")

                        pom.withXml {
                            //Создаем дополнительно сексцию с зависимостями
                            def dependenciesNode = asNode().appendNode('dependencies')

                            //Перебираем зависимости двух конфигурация, общую и конкретной сборки (debugCompile либо releaseCompile)
                            def configurationNames = ["${variant.name}Compile", 'compile']

                            configurationNames.each { configurationName ->
                                project.configurations[configurationName].allDependencies.each {
                                    if (it.group != null && it.name != null) {
                                        def dependencyNode = dependenciesNode.appendNode('dependency')
                                        dependencyNode.appendNode('groupId', it.group)
                                        dependencyNode.appendNode('artifactId', it.name)
                                        dependencyNode.appendNode('version', it.version)

                                        //Если в зависимости указано исключение, необходимо так же указать и его
                                        if (it.excludeRules.size() > 0) {
                                            def exclusionsNode = dependencyNode.appendNode('exclusions')
                                            it.excludeRules.each { rule ->
                                                def exclusionNode = exclusionsNode.appendNode('exclusion')
                                                exclusionNode.appendNode('groupId', rule.group)
                                                exclusionNode.appendNode('artifactId', rule.module)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        project.android.buildTypes.all { variant ->

            def publicationName = "${variant.name}Aar"
            def taskName = "${variant.name}Publication"

            project.task("$taskName") {
                doLast {
                    project.tasks."generatePomFileFor${publicationName.capitalize()}Publication".execute()
                }
            }

            project.tasks."$taskName" << {
                project.artifactoryPublish {
                    doFirst {
                        publications(publicationName)
                        project.clientConfig.publisher.repoKey = "libs-${variant.name}-local"
                    }
                }
            }

            project.tasks."assemble${variant.name.capitalize()}".dependsOn(project.tasks."$taskName")
        }

        project.artifactory {
            contextUrl = ArtifactoryUrl
            publish {
                repository {
                    username = ArtifactoryUser
                    password = ArtifactoryPassword
                }
                defaults {
                    publishArtifacts = true

                    // Properties to be attached to the published artifacts.
                    properties = project.PUBLICATION_PROPERTIES
                }
            }
        }

    }

}