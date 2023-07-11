# Iridium

## kotlin jar-in-jars made easy
<br>
<br>

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
    id("org.teamvoided.iridium") version "1.1.0"
    id("iridium.project.parent-script")
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
<summary>Mod Build Script</summary>

# The Mod Build Script plugin will auto generate a mod json for you
### here are the properties
- modId (the mod id as a string)
- modName (name of the mod as a string)
- modEntrypoints (entry points of the mod as a LinkedHashMap of String and List of Strings where the initial string is the name of the entrypoint and the list of strings contains the actual entry points)
- modMixinFiles (the mixin files of the mod as a list of strings)
- modDepends (the dependencies of the mod as a LinkedHashMap of String and String where the initial string is the mod id and the secondary string is the version of the dependency)
- isModParent (if this is the parent of all the other mods (if it is a jar-in-jar mod [boolean])
- customModIcon (a custom icon for the mod defaults to "assets/{modId}/icon.png)"

## Setting a property in the gradle build script
```kotlin
//other gradle stuff
val examplePropertyName by extra("examplePropertyValue")
```
</details>
