package org.teamvoided.iridium.project

import org.teamvoided.iridium.config.Config.authors
import org.teamvoided.iridium.config.Config.githubRepo
import org.teamvoided.iridium.config.Config.license

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val publishScriptExtension = extensions.create("publishScript", PublishScriptExtension::class.java)

afterEvaluate {
    publishing {
        repositories {
            publishScriptExtension.repositories().forEach {
                maven {
                    name = it.key
                    credentials(PasswordCredentials::class)
                    setUrl(it.value)
                }
            }
        }

        publications {
            register<MavenPublication>(project.name) {
                from(components["java"])

                this.groupId = project.group.toString()
                this.artifactId = project.name
                this.version = rootProject.version.toString()

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
                        licenses {
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