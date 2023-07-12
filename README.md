# Iridium

## kotlin jar-in-jars made easy

<details>
<summary>Iridium Usage</summary>

## settings.gradle.kts
```kotlin
pluginManagement {
    repositories {
        //other repos
        maven("https://maven.brokenfuse.me/releases")
        //projects will eventually be migrated to https://maven.teamvoided.org
        //as of right now https://maven.teamvoided.org just points to https://maven.brokenfuse.me
    }
}
```

## root build.gradle.kts
```kotlin
plugins {
    id("org.teamvoided.iridium") version "1.2.0"
    //maven publishing id("iridium.project.publish-script")
}
```

## module build.gradle.kts
```kotlin
plugins {
    id("iridium.mod.build-script")
    //upload mod to modrinth id("iridium.mod.upload-script")
    //maven publishing id("iridium.project.publish-script")
}
```

## jar-in-jar script
```kotlin
plugins {
    id("iridium.mod.jar-in-jar")
    id("iridium.mod.build-script")
    //upload mod to modrinth id("iridium.mod.upload-script")
}
```
</details>

<details>
<summary>The JarInJar Script</summary>

## This is what you came here for right???
#### The Jar In Jar script will automatically add all modules defined in the iridium config file to the fabric "include" gradle configuration which will automatically add them to ur jar

</details>

<details>
<summary>Mod Build Script V1.2.0+</summary>

# The Mod Build Script plugin will auto generate a mod json for you
## As of iridium version 1.2.0 a clojure is used to define its properties
###### To set the description/version you still use the default project.description/version properties

### Here's an example
```kotlin
plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("iridium.mod.build-script")
}

modSettings {
    modId("example-mod")
    modName("Example Mod")
    // other properties
}

base.archivesName.set("example-mod")
version = project.properties["mod_version"] as String
description = "Example Mod Description"
group = project.properties["maven_group"] as String
```
</details>

<details>
<summary>Mod Build Script V1.1.1 and under</summary>

# The Mod Build Script plugin will auto generate a mod json for you
### here are the properties
- modId (the mod id as a string)
- modName (name of the mod as a string)
- modEntrypoints (entry points of the mod as a LinkedHashMap of String and List of Strings where the initial string is the name of the entrypoint and the list of strings contains the actual entry points)
- modMixinFiles (the mixin files of the mod as a list of strings)
- modDepends (the dependencies of the mod as a LinkedHashMap of String and String where the initial string is the mod id and the secondary string is the version of the dependency)
- isModParent (if this is the parent of all the other mods (if it is a jar-in-jar mod [boolean])
- customModIcon (a custom icon for the mod defaults to "assets/{modId}/icon.png)"

## To set the description/version use the default project.description/version properties

## Setting a property in the gradle build script
```kotlin
//other gradle stuff
val examplePropertyName by extra("examplePropertyValue")
```
</details>

### This is cool and all but what if I want one of my modules to depend on another
#### Well fret not, you can simply use the "dependencyHelper" extension provided by the mod build script
###### Here's an example
```kotlin
plugins {
    //other stuff
    id("iridium.mod.build-script")
}

dependencies {
    implementation(dependencyHelper.modProject(":some-module"))
}
```
