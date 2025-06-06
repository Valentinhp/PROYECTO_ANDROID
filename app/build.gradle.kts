// app/build.gradle.kts

// IMPORTS NECESARIOS
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.gradle.api.JavaVersion

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")           // <-- Agregado aquí

}

android {
    namespace = "com.project.rc_mecha_maint"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.project.rc_mecha_maint"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // -------------------------------
    // FORZAR USAR JAVA 17, NO JAVA 21
    // -------------------------------

    // 1) Esto asegura que toda la compilación de Java use Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 2) Esto obliga a Kotlin a compilar para jvmTarget = "17"
    kotlinOptions {
        jvmTarget = "17"
    }

    // 3) Esto configura el “toolchain” de Kotlin para usar JDK 17 al generar bytecode
    kotlin {
        jvmToolchain {
            (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

// ------------------------------------------------------------------
// Este bloque fuerza a TODAS las tareas de Kotlin (KotlinCompile y KAPT)
// a compilar con jvmTarget = "17", evitando que use Java 21 internamente.
// ------------------------------------------------------------------
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
        // Para KAPT, a veces es útil añadir este argumento:
        // freeCompilerArgs += listOf("-Xjvm-default=compatibility")
    }
}

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Room
    implementation("androidx.room:room-runtime:2.5.0")
    kapt("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.0")

    // ML Kit Text Recognition (On-Device Bundled, última versión válida)
    implementation("com.google.mlkit:text-recognition:16.0.1")
    // MPAndroidChart (está en JitPack)
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("io.coil-kt:coil:2.3.0")


    // Pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
