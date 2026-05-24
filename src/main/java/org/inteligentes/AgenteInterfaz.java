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

/**
 * Agente de Interfaz.
 * Actúa como la pasarela de comunicación entre la GUI de usuario (Swing) 
 * y el Sistema Multiagente (JADE). Gestiona las peticiones de usuario y 
 * actualiza las pantallas con los datos procedentes del Agente de Procesamiento.
 */
public class AgenteInterfaz extends Agent {
    protected CyclicBehaviour cyclicBehaviour;
    private Pantalla2 pantalla2; // Referencia a la pantalla principal de listado de productos para su actualización

    protected void setup(){
        // Registro del agente en el DF para que otros agentes puedan encontrarlo
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
        // invokeLater para asegurar que la creación de la GUI se realice en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Price Scraper");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            CardLayout cardLayout = new CardLayout();
            JPanel contenedor = new JPanel(cardLayout);
            //Pasamos el agente, 'this', a las pantallas para que puedan invocar métodos
            Pantalla3 pantalla3 = new Pantalla3(cardLayout, contenedor,this);
            Pantalla2 pantalla2 = new Pantalla2(cardLayout, contenedor, pantalla3,this);
            Pantalla1 pantalla1 = new Pantalla1(cardLayout, contenedor, pantalla2, this);

            contenedor.add(pantalla1, "pantalla1");
            contenedor.add(pantalla2, "pantalla2");
            contenedor.add(pantalla3, "pantalla3");

            frame.add(contenedor);
            frame.setVisible(true);

            this.pantalla2 = pantalla2;
        });
        // Behaviour 1: Escucha los mensajes que el propio agente se envía desde el hilo de Swing para iniciar el proceso de scraping o actualizar la información
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Filtro bloqueante: Solo despierta este comportamiento si llega un mensaje de tipo REQUEST
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // Caso 1: Petición de añadir y screpear un nuevo producto
                    if (msg.getContent().startsWith("SCRAPING;")) {
                        String[] partes = msg.getContent().substring(9).split(";");
                        addBehaviour(new OneShotBehaviour() {
                            public void action() {
                                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                                request.addReceiver(new AID("AgenteProcesamiento", AID.ISLOCALNAME));
                                request.setContent(partes[0] + ";" + partes[1] + ";" + partes[2]);
                                send(request);
                                System.out.println("[Interfaz] Enviado a Procesamiento: " + request.getContent());
                            }
                        });
                    } else if (msg.getContent().equals("ACTUALIZAR")) { // Caso 2: Petición de actualizar la información de los productos
                        addBehaviour(new OneShotBehaviour() {
                            public void action() {
                                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                                request.addReceiver(new AID("AgenteProcesamiento", AID.ISLOCALNAME));
                                request.setContent("ACTUALIZAR");
                                send(request);
                            }
                        });
                    }
                } else {
                    block(); // Bloquea el comportamiento hasta que llegue un nuevo mensaje a la cola
                }
            }
        });

        // Behaviour 2: escucha el INFORM del procesamiento con el HashMap serializado
        addBehaviour(new ActualizarGUI());
    }

    /**
     * Método llamado por la GUI para solicitar el monitoreo de un producto.
     * Envía un mensaje asíncrono al propio agente para respetar la separación de hilos.
     */
    public void solicitarScraping(String url, String nombre, double umbral) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID()); // me lo mando a mí mismo
        msg.setContent("SCRAPING;" + url + ";" + nombre + ";" + umbral);
        send(msg); // send() es thread-safe
    }

    /**
     * Método puente llamado por la GUI para forzar la actualización de los datos en pantalla.
     */
    public void solicitarActualizacion() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID());
        msg.setContent("ACTUALIZAR");
        send(msg);
    }

    /**
     * Comportamiento cíclico que recibe los datos procesados en forma de objeto HashMap y fuerza la actualización de la interfaz visual.
     */
    class ActualizarGUI extends CyclicBehaviour {
        public void action() {
            // Espera una confirmación de tipo INFORM que indique que los datos están listos
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                try {
                    HashMap<String, Producto> productosActualizados =
                            (HashMap<String, Producto>) msg.getContentObject();
                    
                    // Modificamos los componentes visuales estrictamente dentro del hilo de Swing
                    SwingUtilities.invokeLater(() -> {
                        pantalla2.actualizarProductos(productosActualizados);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block(); // Bloquea el comportamiento hasta que llegue un nuevo mensaje a la cola 
            }
        }
    }

    @Override
    protected void takeDown() {
        try{
            DFService.deregister(this);
            System.out.println("Agente Interfaz desregistrado del DF.");
        } catch (FIPAException fe){
            fe.printStackTrace();
        }
    }
}
