package org.inteligentes;

import jade.core.Agent;
import com.microsoft.playwright.*;
import org.json.JSONObject;
import java.nio.file.Paths;

public class AgenteScrapper extends Agent {
        protected void setup() {
            String url = "https://www.pccomponentes.com/amd-ryzen-7-7800x3d-42-ghz-5-ghz";

            try (Playwright playwright = Playwright.create()) {
                BrowserType.LaunchOptions opciones = new BrowserType.LaunchOptions().setHeadless(true);
                Browser browser = playwright.chromium().launch(opciones);
                BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                );

                Page page = context.newPage();
                System.out.println("Navegando a la web...");
                page.navigate(url);

                System.out.println("Título de la página cargada: " + page.title());

                String jsonText = page.locator("#microdata-product-script").textContent();

                JSONObject json = new JSONObject(jsonText);

                String precio = json
                        .getJSONObject("offers")
                        .getJSONObject("offers")
                        .getString("price");

                System.out.println("Precio: " + precio + " €");

                browser.close();
            }
        }
}

