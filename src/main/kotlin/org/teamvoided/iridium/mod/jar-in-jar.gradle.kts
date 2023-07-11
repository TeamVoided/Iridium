package org.teamvoided.iridium.mod

import org.teamvoided.iridium.config.Config

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

dependencies {
    Config.modules.forEach {
        implementation(include(modProject(":$it"))!!)
    }
}

fun DependencyHandler.modProject(path: String) = project(path, configuration = "namedElements")