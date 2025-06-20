# **Manual Técnico del Sistema**

## **1. Información General**
- **Nombre del Proyecto**: [Rayos smart mechanic]  
- **Versión del Sistema**: 1.0.0  
- **Fecha de Creación**: 20 de junio de 2025  
- **Autor(es)**: HERNANDEZ POSADA LEONARDO VALENTIN, ALCANTAR GARZA ANGEL DE JESUS, LLAMAS VALLE JOSÉ SEBASTIÁN SANTACRUZ DE LUNA JOSUE FERNANDO  
- **Propósito**: Proporcionar información técnica detallada sobre la instalación, configuración, funcionamiento y mantenimiento del sistema.

---

## **2. Introducción**
### **2.1 Descripción del Sistema**
Este sistema es una aplicación móvil Android desarrollada en Kotlin, orientada a la gestión de vehículos, talleres, autopartes, recordatorios y servicios relacionados. Permite a los usuarios registrar información de sus vehículos, llevar un historial de mantenimientos, comparar autopartes, gestionar facturas y encontrar talleres cercanos, entre otras funcionalidades.

### **2.2 Alcance**
Este manual cubre la instalación, configuración, operación, mantenimiento y resolución de problemas del sistema. No incluye detalles sobre el desarrollo de nuevas funcionalidades ni la personalización avanzada del código fuente.

---

## **3. Requisitos del Sistema**
### **3.1 Requisitos de Hardware**
- Procesador: Dual Core 1.5 GHz o superior
- Memoria RAM: 2 GB mínimo (4 GB recomendado)
- Espacio en Disco: 1 GB disponible
- Otros: Dispositivo Android compatible o emulador

### **3.2 Requisitos de Software**
- Sistema Operativo: Windows, macOS o Linux (para desarrollo); Android 7.0+ (para ejecución)
- Frameworks: Android SDK, Gradle, Java 11 o superior
- Librerías: Incluidas en el archivo `build.gradle.kts` (consultar dependencias específicas)
- Otros: Android Studio (recomendado), JDK 11+

---

## **4. Instalación**
### **4.1 Descarga de Archivos**
1. Clona el repositorio desde el sistema de control de versiones (ejemplo: GitHub) desde el siguiente enlace:
[repositorio en github](https://github.com/Valentinhp/PROYECTO_ANDROID/)

2. Descarga el archivo comprimido del proyecto si no usas Git.

### **4.2 Instalación Paso a Paso**
1. Instala Android Studio y configura el SDK de Android.
2. Abre Android Studio y selecciona "Open an existing project".
3. Navega a la carpeta raíz del proyecto y ábrela.
4. Espera a que Gradle sincronice las dependencias automáticamente.
5. Conecta un dispositivo Android o inicia un emulador.
6. Haz clic en "Run" para compilar e instalar la aplicación.

### **4.3 Configuración Inicial**
- Configura los parámetros básicos en los archivos de recursos si es necesario (por ejemplo, archivos JSON en `assets/`).
- Si la app requiere endpoints o claves API, agrégalas en los archivos de configuración correspondientes.
- No se requiere configuración de base de datos local adicional (usa almacenamiento interno/archivos).

---

## **5. Arquitectura del Sistema**
### **5.2 Componentes Principales**
- **UI (Interfaz de Usuario)**: Archivos XML en `res/layout/` y actividades/fragments en `src/main/java/com/project/`.
- **Lógica de Negocio**: Clases Kotlin en `src/main/java/com/project/`.
- **Recursos**: Imágenes, iconos y archivos de configuración en `res/` y `assets/`.
- **Datos**: Archivos JSON en `assets/` para autopartes, talleres, servicios y fallas.
- **Configuración**: Archivos Gradle y propiedades para la gestión de dependencias y compilación.

---

## **6. Operación del Sistema**
### **6.1 Descripción General**
El usuario interactúa con la aplicación a través de una interfaz intuitiva, donde puede registrar vehículos, añadir recordatorios, consultar talleres, comparar autopartes y gestionar facturas. La información se almacena localmente y se accede mediante la lógica implementada en las clases Kotlin.

### **6.2 Flujos de Trabajo Principales**
1. **Registro de Vehículo**: El usuario ingresa los datos de su vehículo y los guarda en la app.
2. **Gestión de Recordatorios**: El usuario añade recordatorios de mantenimiento y recibe notificaciones.
3. **Consulta de Talleres**: El usuario visualiza talleres cercanos y sus detalles.
4. **Comparador de Autopartes**: El usuario compara precios y características de autopartes.
5. **Gestión de Facturas**: El usuario almacena y consulta facturas de servicios realizados.

---

## **7. Resolución de Problemas**
### **7.1 Problemas Comunes**
| Problema                       | Causa Posible                  | Solución                            |
|--------------------------------|---------------------------------|-------------------------------------|
| Error al compilar el proyecto  | Dependencias no sincronizadas   | Ejecutar "Sync Project with Gradle Files" en Android Studio |
| La app no inicia en el emulador| Emulador mal configurado        | Verificar configuración del AVD y reiniciar el emulador     |
| Recursos no encontrados        | Archivos faltantes en `res/`    | Verificar que todos los recursos estén presentes            |

### **7.2 Contacto para Soporte**
Contactar al equipo de desarrollo.

---

## **8. Mantenimiento**
### **8.1 Tareas Periódicas**
- Realizar respaldos regulares del código fuente.
- Actualizar las dependencias en los archivos Gradle.
- Revisar y limpiar archivos de recursos no utilizados.

### **8.2 Actualización del Sistema**
1. Realiza un pull del repositorio para obtener la última versión.
2. Sincroniza las dependencias con Gradle.
3. Compila y prueba la aplicación antes de desplegar.

---

## **9. Seguridad**
### **9.1 Recomendaciones**
- Configurar contraseñas seguras para cualquier acceso administrativo.
- Mantener actualizado el sistema operativo y las herramientas de desarrollo.
- No exponer archivos sensibles en el repositorio.

### **9.2 Gestión de Accesos**
La gestión de accesos se realiza a nivel de dispositivo Android.

---

## **10. Aprobaciones**
| Product manager | Angel De Jesús Alcantar Garza& José Sebastián Llamas Valle | 20/06/2025       |
|---------------|---------------------|---------------|-------------|
| Product Owner | Josué Fernando Santacruz De Luna     |               | 20/06/2025     |
| Líder Técnico | Leonardo Valentín Hernandez Posada  |               | 20/06/2025     |

---

## **11. Referencias**
- Documentación oficial de Android: https://developer.android.com/
- Guía de Gradle: https://docs.gradle.org/

---
