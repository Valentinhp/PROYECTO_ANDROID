// settings.gradle.kts (EN LA RAÍZ del proyecto, junto a build.gradle.kts)
pluginManagement {
    repositories {
        // Estos repositorios son los que Gradle usará para buscar los plugins
        gradlePluginPortal()  // generalmente contiene muchos plugins de Gradle
        google()              // aquí está el plugin de Android
        mavenCentral()        // y cualquier otro plugin en Maven Central
    }
    plugins {
        // Aquí “declaro” las versiones de cada plugin que voy a usar en mis módulos:
        id("com.android.application") version "8.10.1" apply false
        id("org.jetbrains.kotlin.android") version "1.8.21" apply false
        id("org.jetbrains.kotlin.kapt") version "1.8.21" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply false
    }
}

// Defino sólo los repositorios donde se buscarán las dependencias de todos los módulos:
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// Nombre del proyecto (opcional, pero suele definirse aquí)
rootProject.name = "RC_MECHA_MAINT_S"
include(":app")
