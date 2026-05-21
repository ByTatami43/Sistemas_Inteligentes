package org.inteligentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.inteligentes.pantallas.Pantalla1;
import org.inteligentes.pantallas.Pantalla2;
import org.inteligentes.pantallas.Pantalla3;

import javax.swing.*;
import java.awt.*;

public class AgenteInterfaz extends Agent {
    protected CyclicBehaviour cyclicBehaviour;
    private Pantalla1 pantalla1;
    private Pantalla2 pantalla2;
    private Pantalla3 pantalla3;

    protected void setup(){
        System.out.println(" Agente de Interfaz iniciado");
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
    }

    public void solicitarScraping(String url, String nombre, double umbral) {
        System.out.println("Solicitud recibida: " + nombre + " | " + url + " | " + umbral);
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID("AgenteScraper", AID.ISLOCALNAME));
                msg.setContent(url + "||" + nombre + "||" + umbral);
                send(msg);
            }
        });
    }

    // El behaviour de recepción llama a este método para actualizar la GUI
    public void actualizarProducto(String url, String estado, double precio) {
        SwingUtilities.invokeLater(() -> {
            pantalla2.actualizarAlerta(url, estado.equals("ALERTA"));
        });
    }
}
