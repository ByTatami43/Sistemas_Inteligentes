package org.inteligentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class AgentTest extends Agent{
    protected CyclicBehaviour cyclicBehaviour;

    public void setup(){
        System.out.println("Illegals in my yard");
        cyclicBehaviour = new CyclicBehaviour(this) {
            public void action() {
                block();
            }
        };
        addBehaviour(cyclicBehaviour);
    }
}
