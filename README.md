# Price Scraper — Sistema Multiagente con JADE

Monitor inteligente de precios basado en un Sistema Multiagente (SMA) implementado con el framework JADE. La aplicación permite rastrear el precio de productos en tiempo real, guardar el historial y recibir alertas visuales cuando el precio cae por debajo de un umbral definido por el usuario.

## Requisitos previos

Antes de instalar el proyecto, asegúrate de tener lo siguiente:

- **Java 11+** — el proyecto compila con `source/target 11` (ver `pom.xml`)
- **Apache Maven 3.6+** — para gestión de dependencias y ejecución
- Conexión a internet (descarga de dependencias Maven y binarios de Playwright en el primer arranque)

Verificar versiones instaladas:

```bash
java -version
mvn -version
```

---

 ## Instalación
1. Clonar el repositorio
```bash
git clone https://github.com/USUARIO/Sistemas_Inteligentes.git
cd Sistemas_Inteligentes
```

2. Instalar JADE manualmente
Instálalo en tu repositorio Maven local ejecutando este comando desde la raíz del proyecto.

Windows:
```bash
mvn install:install-file "-Dfile=lib/jade.jar" "-DgroupId=com.tilab.jade" "-DartifactId=jade" "-Dversion=4.6.0" "-Dpackaging=jar"
```
  Linux / macOS:
```bash
bashmvn install:install-file -Dfile=lib/jade.jar -DgroupId=com.tilab.jade -DartifactId=jade -Dversion=4.6.0 -Dpackaging=jar
```
Deberías ver BUILD SUCCESS.

3. Descargar el resto de dependencias
```bash
mvn clean install -DskipTests
```
Playwright descarga el navegador Chromium automáticamente la primera vez que se ejecuta la aplicación. No se necesita ningún paso adicional.
En caso de que no se descargue correctamente ejecutar esto:
```bash
mvn exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium"
```
## Dependencias
Todas las dependencias están declaradas en `pom.xml` y se gestionan con Maven:

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| [JADE](https://jade.tilab.com/) | 4.6.0 | Framework del sistema multiagente (FIPA) |
| [Playwright](https://playwright.dev/java/) | 1.44.0 | Navegador headless para web scraping |
| [JFreeChart](https://www.jfree.org/jfreechart/) | 1.5.4 | Gráficas de evolución de precios |
| [org.json](https://github.com/stleary/JSON-java) | 20240303 | Procesamiento de JSON |

> **Nota:** JADE se descarga desde el repositorio Maven de TILab (`https://jade.tilab.com/maven/`), que está declarado en el `pom.xml`. No hace falta añadirlo manualmente.

---

## Ejecución

Desde la raíz del proyecto, lanzar la plataforma JADE con los tres agentes:

```bash
mvn exec:java
```
Esto ejecuta el comando configurado en el `pom.xml`

Al arrancar correctamente verás en consola:

```
Agente Procesamiento registrado correctamente en el DF.
Agente Interfaz registrado correctamente en el DF.
Agente percepcion-webScrapping registrado correctamente en el DF.
```

Y al salir: 
```
Agente Procesamiento desregistrado del DF.
Agente percepcion desregistrado del DF.
```
Se abrirán dos ventanas: la **GUI de administración de JADE** y la ventana principal de la aplicación **Price Scraper**.

