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
            scrapePrecio("https://www.ebay.es/p/20056257992?iid=318339191907");
            scrapePrecio("https://www.ebay.es/p/14093762670?iid=800048876464");
            scrapePrecio("https://www.ebay.es/p/21071474434?iid=257518067495");
        }
        public Double scrapePrecio(String enlace){
            Page page = browserContext.newPage();
            Double precio = (double) -1;
            try{
                System.out.println("Iniciando la busqueda del precio");
                page.navigate(enlace);

                System.out.println("Titulo del producto: " + page.title());

                page.waitForSelector(".x-price-primary");

                // Obtener texto
                String resultado = page.locator(".x-price-primary").innerText();

                resultado = resultado.replace("USD", "").trim().replace(",",".");

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

