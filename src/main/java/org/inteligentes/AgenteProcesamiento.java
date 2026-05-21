package org.inteligentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        sd.setType("procesmiento-datos");
        sd.setName("servicio-procesamiento");
        dfd.addServices(sd);
        csv = new ControladorCSV();
        try {
            DFService.register(this, dfd);
            System.out.println("Agente Procesamiento registrado correctamente en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        productos = csv.inicializarCSV();
        addBehaviour(new EscucharScrapperBehaviour());
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

    private class EscucharScrapperBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    Producto prod = (Producto) msg.getContentObject();
                    Double precioActual = prod.getPrecioActual();
                    System.out.println("[Procesamiento] Procesando actualizacion para: " + prod.getNombre() + " ("
                            + precioActual + "€)");
                    Double precioAnterior = obtenerUltimoPrecioCSV(prod.getEnlace());
                    String estadoCalculado = evaluarTendencia(precioActual, precioAnterior, prod.getUmbral());
                    prod.updateAlerta();
                    csv.guardarEnCSV(prod);
                    AID agenteInterfazAID = buscarInterfazEnDF();
                    if (agenteInterfazAID != null) {
                        ACLMessage paraInterfaz = new ACLMessage(
                                prod.isAlerta() ? ACLMessage.PROPOSE : ACLMessage.INFORM);
                        paraInterfaz.addReceiver(agenteInterfazAID);
                        paraInterfaz.setContentObject(prod);
                        paraInterfaz.setConversationId(estadoCalculado);
                        send(paraInterfaz);
                        System.out.println("[Procesamiento] Mensaje enviado a la Interfaz. Estado: " + estadoCalculado);
                    } else {
                        System.out.println(
                                "[Procesamiento] No se pudo enviar a la Interfaz: Agente no encontrado en el DF");
                    }
                } catch (UnreadableException | IOException e) {
                    System.out.println("[Procesamiento] Error al procesar el mensaje");
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }

    private String evaluarTendencia(Double precioActual, Double precioAnterior, Double umbral) {
        if (precioActual <= umbral) {
            return "Precio optimo";
        }
        if (precioAnterior == null) {
            return "Primer registro";
        }
        double variacion = (precioActual - precioAnterior) / precioAnterior;

        if (precioActual > umbral && variacion > 0) {
            return "Precio por encima del objetivo";
        } else if (variacion == 0) {
            return "Precio estable";
        } else if (variacion < 0 && variacion >= -0.05) {
            return "Tendencia bajista leve";
        } else {
            return "Bajada significativa";
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

}
