package org.inteligentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class AgenteProcesamiento extends Agent {
    private HashMap<String, Producto> productos;
    private ControladorCSV csv;

    @Override
    protected void setup() {
        System.out.println("Agente de Procesamiento " + getAID().getName() + " iniciado.");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("procesamiento-datos");
        sd.setName("procesamiento-datos");
        dfd.addServices(sd);
        csv = new ControladorCSV();
        try {
            DFService.register(this, dfd);
            System.out.println("Agente Procesamiento registrado correctamente en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        productos = csv.inicializarCSV();
        addBehaviour(new SolicitudActualizacionInterfaz());
        addBehaviour(new SolicitudesProgramadasScrapper(this, 30000));
        addBehaviour(new RecibirActualizacionScrapper());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("Agente Procesamiento desregistrado del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class SolicitudActualizacionInterfaz extends CyclicBehaviour {
        @Override
        public void action() {
            try {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    try {
                        // enlace;nombre;umbral
                        String[] aux = msg.getContent().split(";");
                        if (!productos.containsKey(aux[0])) {
                            productos.put(aux[0], new Producto(aux[1], aux[0], Double.parseDouble(aux[2])));

                            ACLMessage msgAux = new ACLMessage(ACLMessage.REQUEST);
                            AID agenteAID = buscarScrapperEnDF();
                            msgAux.addReceiver(agenteAID);
                            //mando enlace
                            msgAux.setContent(aux[0]);
                            send(msgAux);
                        }
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

    private class RecibirActualizacionScrapper extends CyclicBehaviour {
        @Override
        public void action() {
            try {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    try {
                        // enlace;precio;enlace;precio... si precio -1, no se ha encontrado
                        System.out.println(msg.getContent());
                        String[] aux = msg.getContent().split(";");
                        for (int x = 0; x < aux.length-1; x += 2) {
                            if (Double.parseDouble(aux[x + 1]) >= 0) {
                                productos.get(aux[x]).setPrecioActual(Double.parseDouble(aux[x + 1]));
                                csv.guardarEnCSV(productos.get(aux[x]));
                            }
                        }
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
                //mando que productos quiero actualizar (todos)
                msg.setContent(productorToString());
                send(msg);
            } catch (Exception e) {
                System.out.println("[Procesamiento] Error al enviar el mensaje");
                e.printStackTrace();
            }
        }
    }

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
