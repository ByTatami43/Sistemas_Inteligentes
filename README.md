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

## Datos de ejemplo para la ejecución de la práctica

Para probar el correcto funcionamiento del sistema, la extracción de datos mediante web scrapping y la lógica de alertas del Agente de Procesamiento, se recomienda utilizar los siguientes datos de ejemplo en el formulario de la interfaz gráfica:

**Monitor Gaming** 
* **Umbral**: 200.00 eur aprox
* **URL**: https://www.ebay.es/itm/188407383759?_skw=monitor+gaming&itmmeta=01KSDN6P9MHTN9JW3BBJ56BYRS&hash=item2bddf486cf:g:5TIAAeSwpmZqD0m0&itmprp=enc%3AAQALAAAA8GfYFPkwiKCW4ZNSs2u11xAsIfigOd2Rz%2FWLoOaGAaIUaX64If5vFCulkR%2BJM4CuhFsSgWnq4XX31JZkHCzWGF6PY13gjBIrlK4iofzUqjoCHTBB9qbyfnqwbxP%2FcEAhmTeq1leZ5nu627VufG4r0tO87LVnZQwz4chhkNuKW01TOFXCJZ%2FjBRKFwUap710j4ROtOBbSiqWAMHDqlheG9h6LrcfE2ozKWV8ElEUiIZGk78kS0svdP2RDdxsamxuABfp9F%2BdvdgfJ19Kx4qtyqRct85dUSYtCDzFGi86Z%2B11GrX0P%2B%2F6HVTZFyAhJMPSgKA%3D%3D%7Ctkp%3ABk9SR4blmrXLZw 

**Tarjeta Gráfica** 
* **Umbral**: 225.00 eur aprox
* **URL**: https://www.ebay.es/itm/397948401953?_skw=tarjeta+grafica&itmmeta=01KSDP5XTBW5FP4D77AZSJ9N20&hash=item5ca792b921%3Ag%3AouoAAeSw6YBpt-vK&itmprp=enc%3AAQALAAAA8GfYFPkwiKCW4ZNSs2u11xAFS9RJK3uWgFD28VXesY1lLQLA7R8JTLUV7MlyW2oRx7xzVG82MYx%2Bo%2FCENXqrdmKx3m4OjKqsPj6Rp%2BLqYukMWaofpPEXE%2BQ09fgws6LpjprBt16kcn%2F3zA9Wd99uw790%2B8GLZvvm%2F2ZWE3MUGzbjyEOcz%2F3Gth35PpfdH0B0JHdoUcFQJnE9%2BZC%2FkXVtaDeWF4749fBTivyPBxo0clmnS7oZ7jzDtGQieKLMO8AaOsGA3dm3KzxOw0%2FR%2FDAcBa7hxKGHIDwSJJ%2FWgnjaddFXM%2FJjtOXi%2BGrNmawbMNv2Hw%3D%3D%7Ctkp%3ABk9SR77dl7bLZw&LH_BIN=1

**Ratón** 
* **Umbral**: 30.00eur aprox.
* **URL**: https://www.ebay.es/itm/178161783338?_skw=logitech&itmmeta=01KSDP2W22YM2M25ZH1ESFG0HV&hash=item297b45122a:g:vBkAAeSwRHNqEx91&itmprp=enc%3AAQALAAAAwGfYFPkwiKCW4ZNSs2u11xCFTbp2Uh5li2JiJYZ%2Ff8qTjZ17IOMp3up4ABs5FxtNcRBUTlD%2BNF6sYMNcRnRhbI%2BrY1DWPeeENGMiBFEH5SPCSS7KIrxcsu%2B8dz%2BKMijPgpqEzvmJuffNUKQxicoKre82TIzSugvjB0G8cdOFpC9MziE95%2BZfnVB0UfMp09T4479fpGnag7afeJ1H6EAY%2BUXWXmaM9tjB%2Buf9BIO0UMIDD887TeDekvrqZy3NsVOR7g%3D%3D%7Ctkp%3ABk9SR6bBi7bLZw

**Smartphone** 
* **Umbral**: 165.00 eur aprox
* **URL**: https://www.ebay.es/itm/397977260320?_skw=iphone&itmmeta=01KSDPA99VVTAEA3VRNT65H3Q1&hash=item5ca94b1120%3Ag%3Aec0AAeSwtK1qEESd&itmprp=enc%3AAQALAAAAwGfYFPkwiKCW4ZNSs2u11xDdBn82e7nKaRhtAegX3aFVfrUcr7awCFoShlNWsACPVw6oijnhdqw5a54ZM7YgW%2FXI4aaHmC3XertcfNrI%2BNaWwIoeclVgbsUPnauyC3ANygNsgusxNWV6BDxyvkd2IW09A%2FdY%2FTTQXu8uBmU4HU0pJxlzI01TfMeniqfYEcMY1gMrQ5eU9zfoarUfvhSCjWplhQ8jUIL7LdbZxHl8A4nX6whGzQrzqI%2BhwlKWuK2HLg%3D%3D%7Ctkp%3ABlBMUIaVqbbLZw&LH_BIN=1&LH_ItemCondition=2500

**Tarjeta microsd** 
* **Umbral**: 6.00 eur aprox
* **URL**: https://www.ebay.es/itm/287121715784?_skw=tarjeta+micro+sd&itmmeta=01KSDPC0HEYYKHSXYH5SG7WJ60&hash=item42d9c9b248%3Ag%3AcY4AAeSwsFtpg23x&itmprp=enc%3AAQALAAAA8GfYFPkwiKCW4ZNSs2u11xDatH%2FlBpMFluRe4GS%2BqJODwO%2BEylhLAG8H4cycCxSD7dsWtHsA7oBqcxQ%2FdfFNba195%2Bh%2FnDJH%2BXGP0J2HJX1o1lgo9chDpiU2EFtOe%2BFmCV4Ok98nTBHf6uoJh1iq7AsZzfSADTo5hUSqS8VXkUycgMtO8VA57XCFVBXRLGqhV%2Fphs0JdWbA4Bh4OKXEZ35yf74gF7CrHUWs%2FrldD3N2QUD006l58y9koeZ2r3gzGXo4yoSWcZs4wzyQSEQ5Wzx8MorteMBuek1cwKM4VTdRg9%2Btv6lXFeYsHdlyXnkbZJQ%3D%3D%7Ctkp%3ABFBM7oiwtstn&LH_BIN=1

---

## Diagrama de la Arquitectura del Sistema

---

## Declaración de uso de IA

De acuerdo con las normativas de la asignatura, declaramos el uso de herramientas de Inteligencia Artificial Generativa (Claude(Anthropic) y Gemini(Google)) como asistente pedagógico y de desarrollo de software durante la realización de este proyecto.

El uso de IA se ha limitado a las siguientes tareas:
* **Diseño del protocolo de comunicación:** Asistencia para estructurar correctamente los mensajes ACL aplicando las performativas semánticas adecuadas(REQUEST, INFORM, PROPOSE) según el estándar FIPA.
* **Integración de Hilos (JADE vs Swing):** Consulta sobre patrones de diseño seguros para comunicar el Agente de Interfaz con la GUI sin bloquear el *Event Dispatch Thread* de java, implementando 'SwingUtilities.invokeLater'.
* **Optimización de Comportamientos:** Guía en la implementación de comportamientos bloqueantes('MessageTemplate' con 'block()') para evitar la saturación de CPU, y uso de 'TicketBehaviour' para los ciclos de scraping.
* **Resolución de Errores (Debugging):** Ayuda puntual en la corrección de errores de compilación y tipográficos, así como en la comprensión del uso de la interfaz 'Serializable' para el envío de objetos entre contenedores.

La concepción general de la arquitectura, la implementación del scraping con Playwright, el diseño lógico de evaluación de mercado, la persistencia en CSV, el diseño visual de la interfaz y la integración final del código han sido desarrollados íntegramente de forma manual por los miembros del grupo.

---

## Autores
* Daniel Zhan - ByTatami43
* Gabriel Samuel Vigil Rodríguez - ChambaVigil
* Nicolás Cidoncha Rodríguez de la Flor - Zumooo
* Lucas Lillo Ramírez - Ldelillo
* Jose Manuel Iglesias Molina - jm-iglesias

