package org.inteligentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class AgenteInterfaz extends Agent {
    protected CyclicBehaviour cyclicBehaviour;

    protected void setup(){
        System.out.println(" Agente de Interfaz iniciado");
    }
}
