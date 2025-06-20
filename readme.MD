============================================================
RC Mecha Maint (PROYECTO_ANDROID)
Aplicación Android para gestionar mantenimientos, diagnóstico,
comparador de precios y registro de facturas
TABLA DE CONTENIDO

Descripción general

Características principales

Tecnologías y dependencias

Capturas de pantalla

Requisitos del entorno

Instalación y puesta en marcha

Configuración del proyecto

Estructura de carpetas

Detalles de cada módulo

Flujo de datos y arquitectura

Pruebas

CI / CD (Integración continua y despliegue)

Código de conducta

Cómo contribuir

Convenciones de código

Empaquetado, firmado y lanzamiento

Preguntas frecuentes (FAQ)

Licencia

Contacto

DESCRIPCIÓN GENERAL
RC Mecha Maint es una app Android (Kotlin + XML) que:

Registra y muestra recordatorios de mantenimiento.

Diagnostica fallas a partir de síntomas.

Compara precios de autopartes entre talleres.

Almacena y visualiza facturas (opción OCR con ML Kit).

Gestiona lista de vehículos y perfil de usuario.

CARACTERÍSTICAS PRINCIPALES

Pantalla Inicio con tarjetas dinámicas (mantenimientos, resumen).

CRUD de Vehículos (alta, edición y eliminación).

Módulo Diagnóstico (síntomas → falla probable → recomendación).

Módulo Comparador (autopartes, precios por taller, filtros).

Módulo Facturas (captura de foto/galería, OCR opcional, listado).

Persistencia con Room (DAO, Repository, ViewModel, LiveData/Flow).

Navegación con Navigation Component y Safe Args.

Temas Claro/Oscuro automáticos según sistema.

Notificaciones locales para recordatorios programados.

TECNOLOGÍAS Y DEPENDENCIAS

Lenguaje: Kotlin (jvmTarget 17)

Plugins: Android Gradle Plugin, Kotlin Gradle Plugin, Navigation Safe Args

Arquitectura: MVVM (ViewModel + LiveData/Flow)

Persistencia: Room

Navegación: Navigation Component

OCR (opcional): ML Kit Text Recognition

UI: Material Components, ConstraintLayout, RecyclerView, CardView

Librerías auxiliares: Glide, MPAndroidChart, Coroutines

Testing: JUnit, AndroidX Test, Espresso

CAPTURAS DE PANTALLA
Añade en docs/screenshots/:

inicio.png

vehiculos.png

diagnostico.png

comparador.png

facturas.png

REQUISITOS DEL ENTORNO

JDK 17

Android Studio Flamingo (2023.3.1) o superior

Android SDK Platform 33

Gradle 7.6 (wrapper incluido)

Variables de entorno (opcional):
JAVA_HOME → ruta al JDK 17
ANDROID_HOME → ruta al SDK Android

INSTALACIÓN Y PUESTA EN MARCHA

Clonar repositorio
git clone https://github.com/Valentinhp/PROYECTO_ANDROID.git
cd PROYECTO_ANDROID

Abrir en Android Studio (File → Open)

Configurar JDK (File → Project Structure → SDK Location → Java 17)

Sincronizar Gradle cuando lo solicite

Ejecutar la app ▶ Run en dispositivo o emulador API 33+

Opcional por CLI:

./gradlew build

./gradlew installDebug

./gradlew test

./gradlew connectedAndroidTest

CONFIGURACIÓN DEL PROYECTO

gradle.properties
org.gradle.jvmargs=-Xmx2048m
kotlin.code.style=official

settings.gradle.kts
rootProject.name = "RC_Mecha_Maint_S"
include(":app")

app/build.gradle.kts
android {
compileSdk = 33
defaultConfig {
applicationId = "com.project.rc_mecha_maint"
minSdk = 21
targetSdk = 33
versionCode = 1
versionName = "1.0.0"
}
buildFeatures {
viewBinding = true
compose = false
}
kotlinOptions {
jvmTarget = "17"
}
buildTypes {
release {
isMinifyEnabled = true
proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
}
}
}

ESTRUCTURA DE CARPETAS
PROYECTO_ANDROID/
├─ app/
│ ├─ src/main/java/com/project/rc_mecha_maint/
│ │ ├─ ui/ (inicio, vehiculos, diagnostico, comparador, facturas)
│ │ ├─ data/ (local, repository, model)
│ │ └─ util/ (extensiones, helpers)
│ └─ src/main/res/ (layout, drawable, values, navigation)
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle/ (wrapper)
├─ gradle.properties
├─ requirements.txt
└─ README.md

DETALLES DE MÓDULOS

UI: Fragments + Adapters + ViewModels (LiveData/Flow)

Data: Room (Entities, DAOs, Database), Repositories

OCR: ML Kit en Facturas (opcional)

FLUJO DE DATOS Y ARQUITECTURA
View (Fragment) → ViewModel → Repository → DAO (Room) → SQLite
Resultados via LiveData/Flow regresan al ViewModel y UI.
Operaciones pesadas con Coroutines (Dispatchers.IO).

PRUEBAS

Unit tests (app/src/test) → ./gradlew test

Instrumented tests (app/src/androidTest) → ./gradlew connectedAndroidTest

CI / CD (GitHub Actions sugerido)

Checkout

Setup JDK 17

Cache Gradle

./gradlew build

./gradlew test

Opcional: ./gradlew connectedAndroidTest

CÓDIGO DE CONDUCTA
Incluye CODE_OF_CONDUCT.md con Contributor Covenant v2.1.

CÓMO CONTRIBUIR

Fork del repo

Branch feature/mi-mejora

Commits atómicos y descriptivos

Añadir tests

Pull request con descripción

CONVENCIONES DE CÓDIGO

Guía de estilo Kotlin oficial

camelCase para variables y métodos

PascalCase para clases

Límite 120 caracteres por línea

Documentar con KDoc

// TODO: para pendientes

EMPAQUETADO, FIRMADO Y LANZAMIENTO

keystore en app/keystore/ (no subir)

signingConfigs en build.gradle.kts

minifyEnabled + proguard-rules.pro

./gradlew assembleRelease / bundleRelease

Subir AAB a Play Console

PREGUNTAS FRECUENTES
Q: ¿Por qué Room?
R: Simplifica acceso a datos y migraciones.

Q: ¿Cómo forzar tema oscuro?
R: AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

Q: ¿Migrar a Compose?
R: Habilita compose=true en buildFeatures y añade dependencias.

LICENCIA
MIT License. Ver archivo LICENSE.

CONTACTO
Autor: Valentín H.
Correo: tuemail@ejemplo.com
GitHub: https://github.com/Valentinhp
