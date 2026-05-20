package org.inteligentes;

import jade.core.Agent;
import com.microsoft.playwright.*;
import org.json.JSONObject;
import java.nio.file.Paths;

public class AgenteScrapper extends Agent {
        Playwright playwright;
        Browser browser; // tenemos un browser para buscar sobre las paginas
        BrowserContext browserContext; // contexto del browser

        // el setup de la AGENTE PERCEPCION (webScrapping)
        protected void setup() {

            playwright = Playwright.create();
            BrowserType.LaunchOptions opciones = new BrowserType.LaunchOptions().setHeadless(true);
            browser = playwright.chromium().launch(opciones);
            browserContext = browser.newContext(new Browser.NewContextOptions());

            // ejemplos
            scrapePrecio("https://www.pccomponentes.com/amd-ryzen-7-7800x3d-42-ghz-5-ghz");
            scrapePrecio("https://www.pccomponentes.com/placa-base-asus-tuf-gaming-b850-plus-wifi");

        }
        public Double scrapePrecio(String enlace){
            Page page = browserContext.newPage();
            Double precio = (double) -1;
            try{
                System.out.println("Iniciando la busqueda del precio");
                page.navigate(enlace);

                System.out.println("Título de la página cargada: " + page.title());

                String jsonText = page.locator("#microdata-product-script").textContent();

                JSONObject json = new JSONObject(jsonText);

                String resultado = json
                        .getJSONObject("offers")
                        .getJSONObject("offers")
                        .getString("price");

                System.out.println("Precio encontrado: " + resultado );
                precio = Double.parseDouble(resultado);
            } catch (Exception e) {
                System.out.println("No se ha encontrado el precio el error es:");
                e.printStackTrace();
            } finally {
                page.close();
            }
            return precio;
        }
        protected void takeDown() {
            if(browserContext != null) browserContext.close();
            if(browser != null) browser.close();
            if(playwright != null) playwright.close();
        }

}

