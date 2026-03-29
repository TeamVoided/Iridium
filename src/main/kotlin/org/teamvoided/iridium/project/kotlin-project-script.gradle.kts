package org.teamvoided.iridium.project

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
    withJavadocJar()
}