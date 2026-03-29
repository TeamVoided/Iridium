package org.teamvoided.iridium.project

import org.teamvoided.iridium.config.IridiumProps.authors
import org.teamvoided.iridium.config.IridiumProps.githubRepo
import org.teamvoided.iridium.config.IridiumProps.license

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val publishScript = extensions.create("publishScript", PublishScriptExtension::class.java, project)

afterEvaluate {
    if (publishScript.releaseRepository() == null) return@afterEvaluate

    tasks {
        register("publishSnapshots") {
            group = "publishing"

            publishScript.publications().forEach {
                if (!it.isSnapshot) return@forEach

                dependsOn("publish${it.name.uppercase()}PublicationTo${publishScript.releaseRepository()!!.first}Repository")
                dependsOn("publish${it.name.uppercase()}PublicationToMavenLocal")
            }
        }
    }

    java {
        if (publishScript.publishSources) {
            withSourcesJar()
        }
        if (publishScript.publishJavadoc) {
            withJavadocJar()
        }
    }

    publishing {
        repositories {
            maven {
                val nameNullable = publishScript.releaseRepository()?.first
                val urlNullable = publishScript.releaseRepository()?.second
                if (nameNullable == null) println("[WARNING] Repository name is null!")
                if (urlNullable == null) {
                    println("[WARNING] Repository url is null!")
                    return@maven
                }

                name = nameNullable ?: ""
                url = uri(urlNullable)

                if (nameNullable != null) {
                    val usernameEnv = System.getProperty("${nameNullable}Username")
                    val passwordEnv = System.getProperty("${nameNullable}Password")

                    if (usernameEnv == null) {
                        println("[WARNING] Variable $nameNullable Username not found!")
                        return@maven
                    }

                    if (passwordEnv == null) {
                        println("[WARNING] Variable $nameNullable Password not found!")
                        return@maven
                    }

                    credentials {
                        username = usernameEnv
                        password = passwordEnv
                    }
                }
            }
        }

        afterEvaluate {
            publications {
                publishScript.publications().forEach {
                    if (!it.isSnapshot && project.gradle.startParameter.taskNames.contains("publishSnapshots")) {
                        return@forEach
                    }

                    if (it.isSnapshot && !project.gradle.startParameter.taskNames.contains("publishSnapshots")) {
                        return@forEach
                    }

                    register<MavenPublication>(it.name) {
                        from(components["java"])

                        this.groupId = project.group.toString()
                        this.artifactId = base.archivesName.get()
                        val versionStr = project.version.toString()
                        this.version = if (it.isSnapshot) "$versionStr-SNAPSHOT" else versionStr

                        pom {
                            name.set(project.name)
                            description.set(project.description)

                            developers {
                                if (authors.isNotEmpty()) {
                                    authors.forEach { dev -> developer { name.set(dev) } }
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