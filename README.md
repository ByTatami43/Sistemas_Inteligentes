# Price Scraper — Sistema Multiagente con JADE

Monitor inteligente de precios de eBay basado en un Sistema Multiagente (SMA) implementado con el framework JADE. La aplicación permite rastrear el precio de productos en tiempo real, guardar el historial y recibir alertas visuales cuando el precio cae por debajo de un umbral definido por el usuario.

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

### 1. Clonar el repositorio

```bash
git clone [URL DE REPOSITORIO]
cd Sistemas_Inteligentes
```

### 2. Descargar dependencias

Maven descarga automáticamente todas las dependencias, incluyendo JADE desde el repositorio oficial de TILab:

```bash
mvn clean install -DskipTests
```

### 3. Instalar los navegadores de Playwright

Playwright necesita descargar el binario de Chromium la primera vez. Este paso es obligatorio para que el scraping funcione:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

---

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
Agente de Interfaz iniciado
Agente Interfaz registrado correctamente en el DF.
Agente de Procesamiento AgenteProcesamiento@... iniciado.
Agente Procesamiento registrado correctamente en el DF.
Agente percepcion-webScrapping registrado correctamente en el DF.
```

Se abrirán dos ventanas: la **GUI de administración de JADE** y la ventana principal de la aplicación **Price Scraper**.

