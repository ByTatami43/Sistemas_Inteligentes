package org.inteligentes;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;

public class AgentInterfaz extends Agent {
    protected CyclicBehaviour cyclicBehaviour;

    protected void setup(){
        System.out.println(" Agente de Interfaz iniciado");
    }
}
