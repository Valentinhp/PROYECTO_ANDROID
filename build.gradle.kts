import org.gradle.api.tasks.Delete

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
