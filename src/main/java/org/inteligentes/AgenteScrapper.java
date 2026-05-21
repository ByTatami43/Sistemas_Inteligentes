package org.inteligentes;

import jade.core.Agent;
import com.microsoft.playwright.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.sniffer.Message;
import org.json.JSONObject;
import java.nio.file.Paths;

public class AgenteScrapper extends Agent {
        Playwright playwright;
        Browser browser; // tenemos un browser para buscar sobre las paginas
        BrowserContext browserContext; // contexto del browser

        // el setup de la AGENTE PERCEPCION (webScrapping)
        protected void setup() {

            // Inicializamos la libreria para hacer webScrapping
            try{
            playwright = Playwright.create();
            BrowserType.LaunchOptions opciones = new BrowserType.LaunchOptions().setHeadless(true);
            browser = playwright.chromium().launch(opciones);
            browserContext = browser.newContext(new Browser.NewContextOptions());
            } catch (Exception e) {
                System.out.println("No se ha podido iniciar el playwrigth para hacer webScrapping:");
                e.printStackTrace();
            }
            // DF
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("percepcion-scrapping");
            sd.setName("percepcion-scrapping");
            dfd.addServices(sd);
            try{
                DFService.register(this,dfd);
                System.out.println("Agente percepcion-webScrapping registrado correctamente en el DF.");
            }catch (FIPAException fe){
                fe.printStackTrace();
            }

            addBehaviour(new EscucharPeticiones());

            // ejemplos
            //scrapePrecio("https://www.ebay.es/p/20056257992?iid=318339191907");
            //scrapePrecio("https://www.ebay.es/p/14093762670?iid=800048876464");
            //scrapePrecio("https://www.ebay.es/p/21071474434?iid=257518067495");
        }
        class EscucharPeticiones extends CyclicBehaviour{
            public void action() {
                MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(template);

                if(msg!=null){

                    String mensaje = msg.getContent();
                    System.out.println("Mensaje recibido iniciando scrapping de " + mensaje);

                    String[] enlaces = mensaje.split(";");
                    StringBuilder respuesta = new StringBuilder();

                    for(String enlace: enlaces) {
                        if (!enlace.isEmpty()) {
                            Double precio = scrapePrecio(enlace);

                            // hacemos una lista tipo 10.2;12.2;...
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
                    // bloqueamos el comportamiento si no hay msg
                    block();
                }

            }
        }


        public Double scrapePrecio(String enlace){
            Page page = browserContext.newPage();
            Double precio = (double) -1;
            try{
                System.out.println("Iniciando la busqueda del precio");
                page.navigate(enlace);

                System.out.println("Titulo del producto: " + page.title());

                // Si no lo encuentra en 2 secs pasamos al siguiente por comodidad
                page.waitForSelector(".x-price-primary",new Page.WaitForSelectorOptions().setTimeout(2000));

                // Obtener texto
                String resultado = page.locator(".x-price-primary").innerText();

                resultado = resultado.replace("USD", "").replace("EUR", "").trim().replace(",",".");

                System.out.println("Precio encontrado: " + resultado );
                precio = Double.parseDouble(resultado);
            } catch (com.microsoft.playwright.TimeoutError e) {
                System.out.println("No se ha encontrado el precio del producto: " + enlace);
            }catch (Exception e){
                System.out.println("No se ha podido cargar el enlace: " + enlace);
            } finally{
                page.close();
            }
            return precio;
        }
        protected void takeDown() {
            if(browserContext != null) browserContext.close();
            if(browser != null) browser.close();
            if(playwright != null) playwright.close();
            try{
                DFService.deregister(this);
                System.out.println("Agente percepcion desregistrado del DF.");
            } catch (FIPAException fe){
                fe.printStackTrace();
            }
        }

}

