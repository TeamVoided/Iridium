package org.teamvoided.iridium.project

import org.gradle.configurationcache.extensions.capitalized
import org.teamvoided.iridium.config.Config.authors
import org.teamvoided.iridium.config.Config.githubRepo
import org.teamvoided.iridium.config.Config.license

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val publishScriptExtension = extensions.create("publishScript", PublishScriptExtension::class.java, project)

afterEvaluate {
    tasks {
        create("publishSnapshots") {
            group = "publishing"

            publishScriptExtension.publications().forEach {
                if (!it.isSnapshot) return@forEach

                dependsOn("publish${it.name.capitalized()}PublicationTo${publishScriptExtension.releaseRepository()!!.first}Repository")
                dependsOn("publish${it.name.capitalized()}PublicationToMavenLocal")
            }
        }
    }

    java {
        if (publishScriptExtension.publishSources())
            withSourcesJar()
        if (publishScriptExtension.publishJavadoc())
            withJavadocJar()
    }

    publishing {
        repositories {
            maven {
                val nameNullable = publishScriptExtension.releaseRepository()?.first
                val urlNullable = publishScriptExtension.releaseRepository()?.second
                if (nameNullable == null) println("[WARNING] Repository name is null!")
                if (urlNullable == null) println("[WARNING] Repository url is null!")

                name = nameNullable ?: ""
                url = uri(urlNullable ?: "")

                if (nameNullable != null) {
                    val usernameEnv = System.getProperty("${nameNullable}Username") ?: throw NullPointerException("Variable ${nameNullable}Username not found!")
                    val passwordEnv = System.getProperty("${nameNullable}Password") ?: throw NullPointerException("Variable ${nameNullable}Password not found!")

                    credentials {
                        username = usernameEnv
                        password = passwordEnv
                    }
                }
            }
        }

        afterEvaluate {
            publications {
                publishScriptExtension.publications().forEach {
                    if (!it.isSnapshot && project.gradle.startParameter.taskNames.contains("publishSnapshots"))
                        return@forEach

                    if (it.isSnapshot && !project.gradle.startParameter.taskNames.contains("publishSnapshots"))
                        return@forEach

                    register<MavenPublication>(it.name) {
                        from(components["java"])

                        this.groupId = project.group.toString()
                        this.artifactId = base.archivesName.get()
                        val versionStr = project.version.toString()
                        this.version =
                            if (it.isSnapshot)
                                "$versionStr-SNAPSHOT"
                            else versionStr

                        pom {
                            name.set(project.name)
                            description.set(project.description)

                            developers {
                                authors.forEach {
                                    developer {
                                        name.set(it)
                                    }
                                }
                            }

                            licenses {
                                license {
                                    name.set(license)
                                    url.set("https://github.com/$githubRepo/blob/master/LICENSE")
                                }
                            }

                            url.set("https://github.com/$githubRepo")

                            scm {
                                connection.set("scm:git:git://github.com/${githubRepo}.git")
                                url.set("https://github.com/${githubRepo}/tree/main")
                            }
                        }
                    }
                }
            }
        }
    }
}