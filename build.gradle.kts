// 1) IMPORTS deben ir al principio
import org.gradle.api.tasks.Delete

// 2) Bloque buildscript: aquí indicamos que use el plugin de Safe Args versión 2.6.0
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0")
    }
}

// 3) allprojects: repositorios para todos los módulos
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// 4) Tarea clean (puede ir al final)
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
