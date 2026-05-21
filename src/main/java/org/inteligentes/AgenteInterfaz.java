package org.inteligentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.inteligentes.pantallas.Pantalla1;
import org.inteligentes.pantallas.Pantalla2;
import org.inteligentes.pantallas.Pantalla3;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class AgenteInterfaz extends Agent {
    protected CyclicBehaviour cyclicBehaviour;
    private Pantalla1 pantalla1;
    private Pantalla2 pantalla2;
    private Pantalla3 pantalla3;

    protected void setup(){
        System.out.println(" Agente de Interfaz iniciado");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("interfaz-usuario");
        sd.setName("interfaz");
        dfd.addServices(sd);
        try{
            DFService.register(this,dfd);
            System.out.println("Agente Interfaz registrado correctamente en el DF.");
        }catch (FIPAException fe){
            fe.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Price Scraper");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            CardLayout cardLayout = new CardLayout();
            JPanel contenedor = new JPanel(cardLayout);

            Pantalla3 pantalla3 = new Pantalla3(cardLayout, contenedor);
            Pantalla2 pantalla2 = new Pantalla2(cardLayout, contenedor, pantalla3);
            Pantalla1 pantalla1 = new Pantalla1(cardLayout, contenedor, pantalla2, this);

            contenedor.add(pantalla1, "pantalla1");
            contenedor.add(pantalla2, "pantalla2");
            contenedor.add(pantalla3, "pantalla3");

            frame.add(contenedor);
            frame.setVisible(true);

            this.pantalla2 = pantalla2;
            this.pantalla3 = pantalla3;
        });
        addBehaviour(new ActualizarGUI());
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null && msg.getContent().startsWith("SCRAPING;")) {
                    String[] partes = msg.getContent().substring(9).split(";");
                    String url    = partes[0];
                    String nombre = partes[1];
                    String umbral = partes[2];
                    // Ahora SÍ estamos en el hilo del agente, podemos añadir behaviours
                    addBehaviour(new OneShotBehaviour() {
                        public void action() {
                            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                            request.addReceiver(new AID("AgenteProcesamiento", AID.ISLOCALNAME));
                            request.setContent(url + ";" + nombre + ";" + umbral);
                            send(request);
                            System.out.println("[Interfaz] Mensaje enviado a Procesamiento: " + request.getContent());
                        }
                    });
                } else {
                    block();
                }
            }
        });
    }

    public void solicitarScraping(String url, String nombre, double umbral) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID()); // me lo mando a mí mismo
        msg.setContent("SCRAPING;" + url + ";" + nombre + ";" + umbral);
        send(msg); // send() es thread-safe
    }


    class ActualizarGUI extends CyclicBehaviour{
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = blockingReceive(mt);
            if (msg != null) {
                try {
                    HashMap<String, Producto> productosActualizados =
                            (HashMap<String, Producto>) msg.getContentObject();
                    SwingUtilities.invokeLater(() -> {
                        pantalla2.actualizarProductos(productosActualizados);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
