````markdown
# RC Mecha Maint (PROYECTO_ANDROID)

> **Aplicación Android completa para gestionar mantenimientos de vehículos, diagnosticar fallas, comparar precios de autopartes y registrar facturas.**

---

## 📑 Tabla de contenido

1. [Descripción](#descripción)  
2. [Características principales](#características-principales)  
3. [Tecnologías y dependencias](#tecnologías-y-dependencias)  
4. [Capturas de pantalla](#capturas-de-pantalla)  
5. [Requisitos de entorno](#requisitos-de-entorno)  
6. [Instalación y puesta en marcha](#instalación-y-puesta-en-marcha)  
7. [Configuración del proyecto](#configuración-del-proyecto)  
8. [Estructura de carpetas](#estructura-de-carpetas)  
9. [Detalles de los módulos](#detalles-de-los-módulos)  
10. [Flujo de datos y arquitectura](#flujo-de-datos-y-arquitectura)  
11. [Pruebas](#pruebas)  
12. [Cómo contribuir](#cómo-contribuir)  
13. [Convenciones de código](#convenciones-de-código)  
14. [Empaquetado y lanzamiento](#empaquetado-y-lanzamiento)  
15. [Preguntas frecuentes](#preguntas-frecuentes)  
16. [Licencia](#licencia)  
17. [Contacto](#contacto)  

---

## 📖 Descripción

RC Mecha Maint es una aplicación nativa de Android creada en Kotlin + Jetpack Compose (o XML según prefieras) que:

- Lleva **recordatorios** de próximos mantenimientos (aceite, frenos, llantas…).  
- Realiza un **diagnóstico de fallas** basado en síntomas ingresados por el usuario.  
- Permite **comparar precios** de autopartes entre distintos talleres.  
- Gestiona el **historial de facturas**, con subida de imagen y visualización de datos.  
- Administra una **lista de vehículos** y un **perfil de usuario**.  

Su objetivo es centralizar todo lo relacionado con el mantenimiento automotriz en un solo lugar, con una interfaz clara y persistencia local usando Room.

---

## ✨ Características principales

- **Inicio** con tarjetas dinámicas: próximos mantenimientos y resumen rápido.  
- **CRUD de Vehículos**: alta, edición y baja.  
- **Módulo Diagnóstico**: entrada de síntomas→resultado de falla→recomendaciones.  
- **Módulo Comparador**: lista de autopartes + precios por taller + filtros.  
- **Módulo Facturas**: captura de foto (o galería), OCR opcional y almacenamiento.  
- **Persistencia** con Room (DAO, Repository, ViewModel, LiveData/Flow).  
- **Navegación** con Navigation Component y Safe Args.  
- **Temas Claro/Oscuro** automáticos según sistema.  
- **Notificaciones** locales para recordatorios programados.  

---

## 🛠 Tecnologías y dependencias

- **Lenguaje**: Kotlin (jvmTarget 17)  
- **Plugins**:  
  - Android Gradle Plugin  
  - Kotlin Gradle Plugin  
  - Navigation Safe Args  
- **Arquitectura**: MVVM (ViewModel + LiveData/Flow)  
- **Persistencia**: Room  
- **Navegación**: Navigation Component  
- **OCR (opcional)**: ML Kit Text Recognition  
- **UI Components**: Material Components, ConstraintLayout, RecyclerView, CardView  
- **Librerías auxiliares**:  
  - Glide (carga de imágenes)  
  - MPAndroidChart (gráficos en reportes)  
  - Coroutines (tareas en background)  
- **Testing**: JUnit, AndroidX Test, Espresso  

---

## 📸 Capturas de pantalla

> _(añade capturas en `docs/screenshots/` y enlázalas aquí)_

1. **Pantalla Inicio**  
2. **CRUD Vehículos**  
3. **Diagnóstico**  
4. **Comparador de autopartes**  
5. **Listado de facturas**  

---

## 🔧 Requisitos de entorno

- **JDK 17**  
- **Android Studio Flamingo (2023.3.1)** o superior  
- **Android SDK Platform 33**  
- **Gradle 7.6** (wrapper incluido)  
- Variables de entorno (solo si usas CLI):  
  - `JAVA_HOME` → ruta al JDK 17  
  - `ANDROID_HOME` → ruta al SDK Android  

---

## 🚀 Instalación y puesta en marcha

1. **Clonar repositorio**  
   ```bash
   git clone https://github.com/Valentinhp/PROYECTO_ANDROID.git
   cd PROYECTO_ANDROID
````

2. **Abrir en Android Studio**

   * `File` → `Open` → seleccionar la carpeta del proyecto.
3. **Configurar JDK**

   * `File` → `Project Structure` → `SDK Location` → apuntar a **Java 17**.
4. **Sincronizar Gradle**

   * Android Studio propondrá sincronizar; acepta.
5. **Ejecutar la app**

   * Conecta un emulador o dispositivo con API ≥ 33.
   * Pulsa ▶️ (“Run app”).

---

## ⚙️ Configuración del proyecto

* **`gradle.properties`**

  ```properties
  org.gradle.jvmargs=-Xmx2048m
  kotlin.code.style=official
  ```
* **`app/build.gradle.kts`**

  * `minSdk = 21` (o el que prefieras)
  * `targetSdk = 33`
  * Habilita ViewBinding o Jetpack Compose:

    ```kotlin
    buildFeatures {
      viewBinding = true
      compose = false
    }
    ```
* **Variables Secretas** (si las tuvieras):

  * Crear `local.properties` con claves de API (no comitear).

---

## 📂 Estructura de carpetas

```
PROYECTO_ANDROID/
├─ app/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/com/project/rc_mecha_maint/
│  │  │  │  ├─ ui/
│  │  │  │  │  ├─ inicio/
│  │  │  │  │  ├─ vehiculos/
│  │  │  │  │  ├─ diagnostico/
│  │  │  │  │  ├─ comparador/
│  │  │  │  │  └─ facturas/
│  │  │  │  ├─ data/
│  │  │  │  │  ├─ local/       (Room: entidades, DAOs)
│  │  │  │  │  ├─ repository/
│  │  │  │  │  └─ model/
│  │  │  │  └─ util/            (extensiones, helpers, toasts)
│  │  │  └─ res/
│  │  │     ├─ layout/
│  │  │     ├─ drawable/
│  │  │     ├─ values/          (strings, colors, themes)
│  │  │     └─ navigation/      (archivos nav_graph.xml)
│  └─ build.gradle.kts
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle/
│  └─ wrapper/
├─ gradle.properties
├─ requirements.txt
└─ README.md
```

---

## 🔍 Detalles de los módulos

### 1. UI (`ui/…`)

* **Fragments**: una clase por pantalla.
* **Adapters**: para RecyclerView en listados.
* **ViewModels**: lógica UI + obtención de datos.

### 2. Data (`data/…`)

* **Entities**: tablas de Room (`@Entity`).
* **DAOs**: consultas a la base de datos.
* **Database**: clase abstracta `AppDatabase`.
* **Repositories**: un solo punto de acceso a datos (local + remoto).

### 3. Utilidades (`util/…`)

* Extensiones de Kotlin (`Context.toast()`, formateo de fechas…).
* Helpers para conversiones (timestamp → fecha).

---

## 🔄 Flujo de datos y arquitectura

1. **View (Fragment)** → 2. **ViewModel** → 3. **Repository** → 4. **DAO (Room)**
2. **LiveData / Flow** retorna el resultado al ViewModel → se observa desde el Fragment.

Para operaciones de borde (OCR, red) usa **Coroutines** y **Dispatchers.IO**.

---

## ✅ Pruebas

* **Unit tests** en `app/src/test/java/…`

  ```bash
  ./gradlew test
  ```
* **Instrumented tests** en `app/src/androidTest/java/…`

  ```bash
  ./gradlew connectedAndroidTest
  ```
* Usa **Espresso** para UI testing y **Mockito** para mocks (si lo agregas).

---

## 🤝 Cómo contribuir

1. Haz **fork** del proyecto.
2. Crea una rama con tu feature/bugfix:

   ```bash
   git checkout -b feature/nombre-descriptivo
   ```
3. Implementa tu cambio y asegúrate de que compile.
4. Añade pruebas si aplica.
5. Haz **commit** con mensaje claro:

   ```
   feat: agrega validación de fechas en Recordatorios
   ```
6. Envía un **Pull Request**, describe tu cambio y por qué ayuda al proyecto.

---

## 📏 Convenciones de código

* Usa **Kotlin official style guide**.
* Nombres en **camelCase** para variables y métodos.
* Clases y archivos en **PascalCase**.
* Máximo 120 caracteres por línea.
* Comentarios `// TODO:` para pendientes.
* Documenta funciones públicas con KDoc:

  ```kotlin
  /**
   * Calcula los días restantes hasta la próxima fecha de mantenimiento.
   */
  fun calcularDiasRestantes(...): Int { … }
  ```

---

## 🚚 Empaquetado y lanzamiento

1. Configura **signingConfigs** en `app/build.gradle.kts` con tu keystore.
2. Habilita minificación y obfuscación (ProGuard/R8):

   ```kotlin
   buildTypes {
     release {
       minifyEnabled = true
       proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
     }
   }
   ```
3. Genera APK o AAB:

   ```bash
   ./gradlew assembleRelease
   # o
   ./gradlew bundleRelease
   ```
4. Sube el AAB al Play Console.

---

## ❓ Preguntas frecuentes

* **¿Por qué Room en lugar de SQLite directo?**
  Room simplifica el acceso a la base y evita boilerplate.

* **¿Cómo habilito temas Dark/Light?**
  Se detecta automáticamente según el modo del sistema. Puedes forzar con:

  ```kotlin
  AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
  ```

* **¿Puedo usar Jetpack Compose?**
  Sí: en `buildFeatures { compose = true }` y configura `composeOptions`.

---

## 📄 Licencia

Este proyecto está bajo la **MIT License**.
Consulta el archivo `LICENSE` para detalles.

---

## 📬 Contacto

* **Autor:** Valentín H.
* **Correo:** [tuemail@ejemplo.com](mailto:tuemail@ejemplo.com)
* **GitHub:** [github.com/Valentinhp](https://github.com/Valentinhp)

¡Gracias por usar y contribuir a RC Mecha Maint! 🚀

```
```
