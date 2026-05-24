package org.inteligentes;

import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Agente de Procesamiento y Toma de Decisiones.
 * Es el núcleo lógico del Sistema Multiagente. Se encarga de:
 *  Mantener el estado en memoria (HashMap de Productos).
 *  Gestionar la persistencia de datos (vía ControladorCSV).
 *  Orquestar la comunicación entre la vista (Interfaz) y los sensores (Scraper).
 */
public class AgenteProcesamiento extends Agent {
    private HashMap<String, Producto> productos; // Estructura de datos principal. Mapea la URL (String) con su objeto Producto lógico.
    private ControladorCSV csv; // Encapsula toda la lógica de lectura/escritura en CSV.

    @Override
    protected void setup() {
        System.out.println("Agente de Procesamiento " + getAID().getName() + " iniciado.");

        // Publica sus servicios para permitir el descubrimiento por parte de otros agentes.
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("procesamiento-datos");
        sd.setName("procesamiento-datos");
        dfd.addServices(sd);

        // Recupera el estado de ejecuciones anteriores (persistencia)
        csv = new ControladorCSV();
        try {
            DFService.register(this, dfd);
            System.out.println("Agente Procesamiento registrado correctamente en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        productos = csv.inicializarCSV();
        productos.values().forEach(Producto::updateAlerta);
        // Agrega los comportamientos necesarios para la operación del agente.
        addBehaviour(new SolicitudActualizacionInterfaz());
        addBehaviour(new SolicitudesProgramadasScrapper(this, 30000)); // Actúa como un cronometro para el sistema, ejecutándose cada 30 segundos (30000 ms)
        addBehaviour(new RecibirActualizacionScrapper());
    }


    //Limpieza de recursos. Se ejecuta automáticamente cuando el contenedor JADE "mata" al agente.
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("Agente Procesamiento desregistrado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    /**
     * Comportamiento Cíclico: Escucha Peticiones de la Interfaz.
     * Atiende dos tipos de comandos: Registrar nuevos productos ("SCRAPING;...") 
     * o Forzar refresco de la vista ("ACTUALIZAR").
     */
    private class SolicitudActualizacionInterfaz extends CyclicBehaviour {
        @Override
        public void action() {
            try {
                // Solo atiende peticiones directas de acción (REQUEST)
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    try {
                        // Caso 1: La GUI solicita los datos más recientes
                        if (msg.getContent().equals("ACTUALIZAR")) {
                            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                            inform.addReceiver(buscarInterfazEnDF());
                            inform.setContentObject(new HashMap<>(productos)); // Serializa y envía el mapa completo de productos
                            send(inform);
                        } else { // Caso 2: El usuario acaba de registrar un nuevo enlace
                            // enlace;nombre;umbral
                            String[] aux = msg.getContent().split(";");
                            if (!productos.containsKey(aux[0])) {
                                productos.put(aux[0], new Producto(aux[1], aux[0], Double.parseDouble(aux[2]))); //Crea el producto y lo mete en el HashMap

                                // Manda el scrapper a buscar el precio
                                ACLMessage msgAux = new ACLMessage(ACLMessage.REQUEST);
                                AID agenteAID = buscarScrapperEnDF();
                                msgAux.addReceiver(agenteAID);
                                msgAux.setContent(aux[0]);
                                send(msgAux);

                                // Manda el HashMap actualizado a la interfaz aunque sin precio
                                ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                                inform.addReceiver(buscarInterfazEnDF());
                                inform.setContentObject(new HashMap<>(productos));
                                send(inform);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[Procesamiento] Error al enviar el mensaje");
                        e.printStackTrace();
                    }
                } else {
                    block(); // Duerme el hilo hasta nuevo mensaje
                }
            } catch (Exception e) {
                System.out.println("[Procesamiento] Error al recibir el mensaje");
                e.printStackTrace();
            }
        }
    }

    /**
     * Comportamiento Cíclico: Recepción y Persistencia de Percepciones.
     * Escucha los resultados que envía el Agente Scraper, 
     * actualiza la memoria, guarda en disco y avisa a la interfaz.
     */
    private class RecibirActualizacionScrapper extends CyclicBehaviour {
        @Override
        public void action() {
            try {
                // Solo atiende confirmaciones de datos obtenidos (INFORM)
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    try {
                        // enlace;precio;enlace;precio... Si precio -1, no se ha encontrado
                        System.out.println(msg.getContent());
                        String[] aux = msg.getContent().split(";");
                        // Parseo en pares (Clave-Valor) saltando de 2 en 2
                        for (int x = 0; x < aux.length-1; x += 2) {
                            if (Double.parseDouble(aux[x + 1]) >= 0) {
                                // Actualiza el estado del objeto (la clase Producto maneja las fechas internamente)
                                productos.get(aux[x]).setPrecioActual(Double.parseDouble(aux[x + 1]));
                                productos.get(aux[x]).updateAlerta();
                                System.out.println("[Procesamiento] " + aux[x] + " precio: " + aux[x+1] + " umbral: " + productos.get(aux[x]).getUmbral() + " alerta: " + productos.get(aux[x]).isAlerta());
                                csv.guardarEnCSV(productos.get(aux[x])); //Escribe la nueva fila en el CSV
                            }
                        }

                        // Avisa a la GUI de que hay nuevos datos en el sistema
                        ACLMessage msgM = new ACLMessage(ACLMessage.REQUEST);
                        AID agenteAID = buscarInterfazEnDF();
                        msgM.addReceiver(agenteAID);
                        //mando nuestro mapa de productos para que la interfaz pueda actualizarlo
                        msgM.setContentObject(new HashMap<>(productos));
                        send(msgM);
                    } catch (Exception e) {
                        System.out.println("[Procesamiento] Error al enviar el mensaje");
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            } catch (Exception e) {
                System.out.println("[Procesamiento] Error al recibir el mensaje");
                e.printStackTrace();
            }
        }
    }

    /**
     * Tarea Programada.
     * Obliga al Scraper a barrer periódicamente todas las URLs almacenadas 
     * para mantener la base de datos viva y detectar bajadas repentinas de precio.
     */
    private class SolicitudesProgramadasScrapper extends TickerBehaviour {
        public SolicitudesProgramadasScrapper(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {
            try {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                AID agenteAID = buscarScrapperEnDF();
                msg.addReceiver(agenteAID);
                //Mando que productos quiero actualizar (todos)
                msg.setContent(productorToString()); // Envía un string concatenado con TODAS las URLs
                send(msg);
            } catch (Exception e) {
                System.out.println("[Procesamiento] Error al enviar el mensaje");
                e.printStackTrace();
            }
        }
    }

    /**
     * Búsqueda de la Interfaz Gráfica.
     */
    private AID buscarInterfazEnDF() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("interfaz-usuario");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                return result[0].getName();
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

    /**
     * Búsqueda del Agente Scraper.
     */
    private AID buscarScrapperEnDF() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("percepcion-scrapping");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                return result[0].getName();
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }


    /**
     * Serializador manual rápido.
     * Convierte las URLs del HashMap en una única cadena separada por punto y coma.
     * * @return String con formato "url1;url2;url3"
     */
    private String productorToString() {
        StringBuilder aux = new StringBuilder();
        String[] auxL = productos.keySet().toArray(new String[productos.size()]);
        for (int i = 0; i < auxL.length; i++) {
            aux.append((i != 0 ? ";" : "") + auxL[i]);
        }
        System.out.println(aux.toString());
        return aux.toString();
    }
}
