package org.teamvoided.iridium.mod

import com.modrinth.minotaur.dependencies.DependencyType
import org.teamvoided.iridium.config.Config
import org.teamvoided.iridium.helper.DependencyHelper

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

dependencies {
    Config.modules.forEach {
        implementation(DependencyHelper.jarInclude(project, ":$it", DependencyType.EMBEDDED))
    }
}
