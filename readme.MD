RC Mecha Maint (PROYECTO\_ANDROID)

Aplicación Android completa para gestionar mantenimientos de vehículos, diagnosticar fallas, comparar precios de autopartes y registrar facturas.

---

1. Tabla de contenido

   1. Descripción
   2. Características principales
   3. Tecnologías y dependencias
   4. Capturas de pantalla
   5. Requisitos de entorno
   6. Instalación y puesta en marcha
   7. Configuración del proyecto
   8. Estructura de carpetas
   9. Detalles de los módulos
   10. Flujo de datos y arquitectura
   11. Pruebas
   12. Cómo contribuir
   13. Convenciones de código
   14. Empaquetado y lanzamiento
   15. Preguntas frecuentes
   16. Licencia
   17. Contacto

---

Descripción

RC Mecha Maint es una aplicación nativa de Android creada en Kotlin que:

* Lleva recordatorios de próximos mantenimientos (aceite, frenos, llantas, etc.).
* Realiza un diagnóstico de fallas basado en síntomas ingresados por el usuario.
* Permite comparar precios de autopartes entre distintos talleres.
* Gestiona el historial de facturas, con subida de imagen y visualización de datos.
* Administra una lista de vehículos y un perfil de usuario.

Su objetivo es centralizar todo lo relacionado con el mantenimiento automotriz en un solo lugar, con una interfaz clara y persistencia local usando Room.

---

Características principales

* Inicio con tarjetas dinámicas: próximos mantenimientos y resumen rápido.
* CRUD de Vehículos: alta, edición y baja.
* Módulo Diagnóstico: entrada de síntomas, resultado de falla y recomendaciones.
* Módulo Comparador: lista de autopartes, precios por taller y filtros.
* Módulo Facturas: captura de foto (o galería), OCR opcional y almacenamiento.
* Persistencia con Room (DAO, Repository, ViewModel, LiveData/Flow).
* Navegación con Navigation Component y Safe Args.
* Temas Claro/Oscuro automáticos según el sistema.
* Notificaciones locales para recordatorios programados.

---

Tecnologías y dependencias

* Lenguaje: Kotlin (jvmTarget 17)
* Plugins: Android Gradle Plugin, Kotlin Gradle Plugin, Navigation Safe Args
* Arquitectura: MVVM (ViewModel y LiveData/Flow)
* Persistencia: Room
* Navegación: Navigation Component
* OCR (opcional): ML Kit Text Recognition
* Componentes de UI: Material Components, ConstraintLayout, RecyclerView, CardView
* Librerías auxiliares: Glide (carga de imágenes), MPAndroidChart (gráficos en reportes), Coroutines (tareas en background)
* Testing: JUnit, AndroidX Test, Espresso

---

Capturas de pantalla

Añade las capturas en docs/screenshots/ y enlázalas aquí:

1. Pantalla Inicio
2. CRUD Vehículos
3. Diagnóstico
4. Comparador de autopartes
5. Listado de facturas

---

Requisitos de entorno

* JDK 17
* Android Studio Flamingo (2023.3.1) o superior
* Android SDK Platform 33
* Gradle 7.6 (wrapper incluido)
* Variables de entorno (si usas línea de comandos):
  JAVA\_HOME → ruta al JDK 17
  ANDROID\_HOME → ruta al SDK Android

---

Instalación y puesta en marcha

1. Clonar repositorio
   git clone [https://github.com/Valentinhp/PROYECTO\_ANDROID.git](https://github.com/Valentinhp/PROYECTO_ANDROID.git)
   cd PROYECTO\_ANDROID

2. Abrir en Android Studio
   En el menú File, selecciona Open y elige la carpeta del proyecto.

3. Configurar JDK
   En File > Project Structure > SDK Location, apunta a Java 17.

4. Sincronizar Gradle
   Cuando Android Studio lo solicite, acepta la sincronización.

5. Ejecutar la app
   Conecta un emulador o dispositivo con API 33 o superior y pulsa el botón Run.

---

Configuración del proyecto

Archivo gradle.properties:
org.gradle.jvmargs=-Xmx2048m
kotlin.code.style=official

En app/build.gradle.kts:
minSdk = 21 (o el que prefieras)
targetSdk = 33
En buildFeatures habilita viewBinding o Compose:
viewBinding = true
compose = false

Variables Secretas (si las tuvieras):
Crea local.properties con claves de API (no subir al repositorio).

---

Estructura de carpetas

PROYECTO\_ANDROID/
├─ app/
│   ├─ src/
│   │   ├─ main/
│   │   │   ├─ java/com/project/rc\_mecha\_maint/
│   │   │   │   ├─ ui/
│   │   │   │   │   ├─ inicio/
│   │   │   │   │   ├─ vehiculos/
│   │   │   │   │   ├─ diagnostico/
│   │   │   │   │   ├─ comparador/
│   │   │   │   │   └─ facturas/
│   │   │   │   ├─ data/
│   │   │   │   │   ├─ local/    (Room: entidades, DAOs)
│   │   │   │   │   ├─ repository/
│   │   │   │   │   └─ model/
│   │   │   │   └─ util/         (extensiones, helpers, toasts)
│   │   │   └─ res/
│   │   │       ├─ layout/
│   │   │       ├─ drawable/
│   │   │       ├─ values/      (strings, colors, themes)
│   │   │       └─ navigation/  (archivos nav\_graph.xml)
│   └─ build.gradle.kts
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle/
│   └─ wrapper/
├─ gradle.properties
├─ requirements.txt
└─ README.txt (este archivo)

---

Detalles de los módulos

1. UI (en la carpeta ui/…):

   * Fragments: una clase por pantalla.
   * Adapters: para los RecyclerView.
   * ViewModels: lógica de UI y obtención de datos.

2. Data (en la carpeta data/…):

   * Entities: tablas de Room marcadas con @Entity.
   * DAOs: interfaces con las consultas a la base de datos.
   * Database: clase abstracta AppDatabase.
   * Repositories: punto único de acceso a datos (local y remoto).

3. Utilidades (en util/…):

   * Extensiones de Kotlin, por ejemplo Context.toast().
   * Helpers para conversiones de timestamp a fecha, etc.

---

Flujo de datos y arquitectura

El flujo típico es:
View (Fragment) -> ViewModel -> Repository -> DAO (Room)
El resultado se devuelve como LiveData o Flow al ViewModel y se observa desde el Fragment.
Para operaciones de red u OCR utiliza Coroutines con Dispatchers.IO.

---

Pruebas

* Unit tests en app/src/test/java/…
  Ejecutar con: ./gradlew test

* Instrumented tests en app/src/androidTest/java/…
  Ejecutar con: ./gradlew connectedAndroidTest

Se recomienda usar Espresso para pruebas de UI y Mockito para mocks si lo necesitas.

---

Cómo contribuir

1. Haz fork del proyecto.
2. Crea una rama descriptiva: git checkout -b feature/nombre-descriptivo
3. Implementa tu cambio y verifica que compile.
4. Añade pruebas si aplica.
5. Haz commit con un mensaje claro, por ejemplo:
   feat: agrega validación de fechas en Recordatorios
6. Envía un Pull Request describiendo tu aporte y el motivo.

---

Convenciones de código

* Sigue la guía oficial de estilo de Kotlin.
* Variables y métodos en camelCase.
* Clases y archivos en PascalCase.
* Líneas de código de máximo 120 caracteres.
* Usa comentarios // TODO: para tareas pendientes.
* Documenta funciones públicas con KDoc.

---

Empaquetado y lanzamiento

1. Configura signingConfigs en app/build.gradle.kts con tu keystore.
2. Activa minificación y obfuscación en buildTypes -> release:

   * minifyEnabled = true
   * proguardFiles(getDefault..., "proguard-rules.pro")
3. Genera el APK o AAB:
   ./gradlew assembleRelease
   o
   ./gradlew bundleRelease
4. Sube el AAB al Play Console.

---

Preguntas frecuentes

* ¿Por qué usar Room en lugar de SQLite directo?
  Porque Room reduce boilerplate y gestiona el esquema y las migraciones.

* ¿Cómo habilitar temas Claro y Oscuro?
  Se detectan automáticamente según el sistema. Para forzar uno:
  AppCompatDelegate.setDefaultNightMode(MODE\_NIGHT\_YES)

* ¿Puedo usar Jetpack Compose?
  Sí. En build.gradle.kts activa compose en buildFeatures y configura composeOptions.

---

Licencia

Este proyecto está bajo la MIT License. Consulta el archivo LICENSE para más detalles.

---

Contacto

Autor: Valentín H.
Correo: [tuemail@ejemplo.com](mailto:tuemail@ejemplo.com)
GitHub: [https://github.com/Valentinhp](https://github.com/Valentinhp)

¡Gracias por usar y contribuir a RC Mecha Maint!
