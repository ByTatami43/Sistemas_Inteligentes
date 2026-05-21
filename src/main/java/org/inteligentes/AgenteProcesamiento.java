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

public class AgenteProcesamiento extends Agent  {
    private final String CSV_FILE = "historial_precios.csv";

    @Override
    protected void setup(){
        System.out.println("Agente de Procesamiento " + getAID().getName() + " iniciado.");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("procesmiento-datos");
        sd.setName("servicio-procesamiento");
        dfd.addServices(sd);

        try{
            DFService.register(this, dfd);
            System.out.println("Agente Procesamiento registrado correctamente en el DF.");
        } catch (FIPAException fe){
            fe.printStackTrace();
        }
        inicializarCSV();
        addBehaviour(new EscucharScrapperBehaviour());
    }

    @Override
    protected void takeDown(){
        try{
            DFService.deregister(this);
            System.out.println("Agente Procesamiento desregistrado del DF.");
        } catch (FIPAException fe){
            fe.printStackTrace();
        }
    }

    private class EscucharScrapperBehaviour extends CyclicBehaviour{
        @Override
        public void action(){
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null){
                try{
                    Producto prod = (Producto) msg.getContentObject();
                    Double precioActual = prod.getPrecioActual();
                    System.out.println("[Procesamiento] Procesando actualizacion para: " + prod.getNombre() + " (" + precioActual + "€)");
                    Double precioAnterior = obtenerUltimoPrecioCSV(prod.getEnlace());
                    String estadoCalculado = evaluarTendencia(precioActual, precioAnterior, prod.getUmbral());
                    if (precioActual <= prod.getUmbral()){
                        prod.setAlerta(true);
                    } else{
                        prod.setAlerta(false);
                    }
                    guardarEnCSV(prod.getEnlace(), prod.getNombre(), prod.getUmbral(), precioActual);
                    AID agenteInterfazAID = buscarInterfazEnDF();
                    if (agenteInterfazAID != null){
                        ACLMessage paraInterfaz = new ACLMessage(prod.isAlerta() ? ACLMessage.PROPOSE : ACLMessage.INFORM);
                        paraInterfaz.addReceiver(agenteInterfazAID);
                        paraInterfaz.setContentObject(prod);
                        paraInterfaz.setConversationId(estadoCalculado);
                        send(paraInterfaz);
                        System.out.println("[Procesamiento] Mensaje enviado a la Interfaz. Estado: " + estadoCalculado);
                    } else{
                        System.out.println("[Procesamiento] No se pudo enviar a la Interfaz: Agente no encontrado en el DF");
                    }
                } catch (UnreadableException | IOException e){
                    System.out.println("[Procesamiento] Error al procesar el mensaje");
                    e.printStackTrace();
                }
            } else{
                block();
            }
        }
    }

    private String evaluarTendencia(Double precioActual, Double precioAnterior, Double umbral){
        if (precioActual <= umbral){
            return "Precio optimo";
        }
        if (precioAnterior == null){
            return "Primer registro";
        }
        double variacion = (precioActual - precioAnterior) / precioAnterior;

        if (precioActual > umbral && variacion > 0){
            return "Precio por encima del objetivo";
        }
        else if (variacion == 0){
            return "Precio estable";
        }
        else if (variacion < 0 && variacion >= -0.05){
            return "Tendencia bajista leve";
        }
        else{
            return "Bajada significativa";
        }
    }

    private AID buscarInterfazEnDF(){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("interfaz-usuario");
        template.addServices(sd);
        try{
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                return result[0].getName();
            }
        } catch (FIPAException fe){
            fe.printStackTrace();
        }
        return null;
    }

    private void inicializarCSV(){
        File file = new File(CSV_FILE);
        if (!file.exists()){
            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
                bw.write("Timestamp,Enlace,Nombre,Umbral,Precio\n");
            } catch (IOException e){
                System.out.println("[CSV] Error al inicializar el archivo");
            }
        }
    }

    private void guardarEnCSV(String enlace, String nombre, Double umbral, Double precio){
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (FileWriter fw = new FileWriter(CSV_FILE, true); BufferedWriter bw = new BufferedWriter(fw)){
            String nombreLimpio = nombre.replace(",", " ");
            bw.write(String.format("%s,%s,%s,%.2f,%.2f\n", timestamp, enlace, nombreLimpio, umbral, precio));
        } catch (IOException e){
            System.out.println("[CSV] Error al escribir el nuevo precio");
            e.printStackTrace();
        }
    }

    private Double obtenerUltimoPrecioCSV(String enlace){
        Double ultimoPrecio = null;
        File file = new File(CSV_FILE);
        if (!file.exists()) return null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)){
            String linea;
            br.readLine();
            while((linea = br.readLine()) != null){
                String[] datos = linea.split(",");
                if (datos.length >= 5 && datos[1].equals(enlace)){
                    ultimoPrecio = Double.parseDouble(datos[4]);
                }
            }
        } catch (IOException | NumberFormatException e){
            System.out.println("[CSV] Error al leer el historico");
        }
        return ultimoPrecio;
    }
}
