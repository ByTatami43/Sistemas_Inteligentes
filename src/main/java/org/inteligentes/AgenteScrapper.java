package org.inteligentes;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Agente Scraper.
 * Actúa como los "ojos" del Sistema Multiagente en Internet.
 * Utiliza la librería Playwright para instanciar navegadores Chromium en modo 'headless' 
 * (sin interfaz gráfica) y extraer información de las tiendas online.
 */
public class AgenteScrapper extends Agent {
        Playwright playwright;
        Browser browser; // tenemos un browser para buscar sobre las paginas
        BrowserContext browserContext; // contexto del browser

        // el setup de la AGENTE PERCEPCION (webScrapping)
        protected void setup() {

            // Inicializamos la libreria para hacer webScrapping
            try{
            playwright = Playwright.create();
            // Configuración Headless: Ejecuta el navegador en background sin renderizar píxeles en pantalla.
            BrowserType.LaunchOptions opciones = new BrowserType.LaunchOptions().setHeadless(true);
            browser = playwright.chromium().launch(opciones);
            // Se crea un contexto global para todas las páginas. Evita lanzar un proceso nuevo por cada producto.
            browserContext = browser.newContext(new Browser.NewContextOptions());
            } catch (Exception e) {
                System.out.println("No se ha podido iniciar el playwrigth para hacer webScrapping:");
                e.printStackTrace();
            }
            // Registro en el DF
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("percepcion-scrapping");
            sd.setName("percepcion-scrapping");
            dfd.addServices(sd);
            try{
                DFService.register(this,dfd);
                System.out.println("Agente Percepcion-webScrapping registrado correctamente en el DF.");
            }catch (FIPAException fe){
                fe.printStackTrace();
            }

            // Comportamiento para escuchar peticiones de scrapping
            addBehaviour(new EscucharPeticiones());

            // ejemplos
            //scrapePrecio("https://www.ebay.es/p/20056257992?iid=318339191907");
            //scrapePrecio("https://www.ebay.es/p/14093762670?iid=800048876464");
            //scrapePrecio("https://www.ebay.es/p/21071474434?iid=257518067495");
        }

        /**
         * Comportamiento Cíclico: Receptor de órdenes de rastreo.
         * Despierta cuando el Agente de Procesamiento le pide buscar uno o varios enlaces.
         */
        class EscucharPeticiones extends CyclicBehaviour{
            public void action() {
                // Solo atiende mensajes de tipo REQUEST
                MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(template);

                if(msg!=null){

                    String mensaje = msg.getContent();
                    System.out.println("Mensaje recibido iniciando scrapping de " + mensaje);

                    // Desempaqueta el lote de URLs separadas por punto y coma
                    String[] enlaces = mensaje.split(";");
                    StringBuilder respuesta = new StringBuilder();

                    // Procesa cada URL de forma secuencial
                    for(String enlace: enlaces) {
                        if (!enlace.isEmpty()) {
                            Double precio = scrapePrecio(enlace);

                            // Reensambla la respuesta en formato URL;Precio;
                            respuesta.append(enlace).append(";").append(precio).append(";");
                        }
                    }

                    // Hacemos un mensaje tipo INFORM
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(respuesta.toString());

                    // Le respondemos
                    myAgent.send(reply);
                    System.out.println("Devolvemos precios: " + respuesta.toString());
                }else{
                    // Bloqueamos el comportamiento si no hay msg
                    block();
                }

            }
        }



        /**
         * Lógica principal de Percepción.
         * Abre una pestaña virtual, espera a que los scripts de la página 
         * pinten el precio, lo extrae y lo limpia.
         * @param enlace La URL del producto a rastrear.
         * @return El precio numérico extraído, o -1.0 si ocurre un fallo (Timeout, elemento no encontrado).
         */
        public Double scrapePrecio(String enlace){
            // Se abre una pestaña nueva dentro del contexto existente
            Page page = browserContext.newPage();
            Double precio = (double) -1; // Valor por defecto en caso de error
            try{
                System.out.println("Iniciando la busqueda del precio");
                page.navigate(enlace);

                System.out.println("Titulo del producto: " + page.title());

                // Si no lo encuentra en 2 secs pasamos al siguiente por comodidad
                page.waitForSelector(".x-price-primary",new Page.WaitForSelectorOptions().setTimeout(2000));

                // Obtener texto
                String resultado = page.locator(".x-price-primary").innerText();

                // Limpieza de la cadena para el parseo matemático
                resultado = resultado.replace("USD", "").replace("EUR", "").trim().replace(",",".");
                resultado = resultado.split("[^0-9.]")[0].trim();
                System.out.println("Precio encontrado: " + resultado );
                precio = Double.parseDouble(resultado);
            } catch (com.microsoft.playwright.TimeoutError e) {
                System.out.println("No se ha encontrado el precio del producto: " + enlace);
            }catch (Exception e){
                System.out.println("No se ha podido cargar el enlace: " + enlace);
            } finally{
                // Aseguramos que la pestaña virtual se destruya SIEMPRE, 
                // independientemente de si el scraping fue un éxito o si lanzó una excepción.
                page.close();
            }
            return precio;
        }

        /**
         * Se ejecuta al terminar la vida del agente para matar los procesos de Chromium 
         * y liberar los puertos de comunicación entre Java y el navegador.
         */
        protected void takeDown() {
            if(browserContext != null) browserContext.close();
            if(browser != null) browser.close();
            if(playwright != null) playwright.close();
            try{
                DFService.deregister(this);
                System.out.println("Agente Percepcion desregistrado del DF.");
            } catch (FIPAException fe){
                fe.printStackTrace();
            }
        }

}

