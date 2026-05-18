package org.inteligentes;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;

public class AgentExample extends Agent {
    protected CyclicBehaviour cyclicBehaviour;

    protected void setup(){
        System.out.println("Me lo prometieron");
        System.out.println("AID: " + this.getAID());
        System.out.println("Entrando en espera");
        this.doWait(10000);
        System.out.println("Saliendo de espera, entrando en suspendido");
        this.doSuspend();
        System.out.println("Saliendo de suspendido");
        AgentContainer container=(AgentContainer) getContainerController();
        Object[] params=new Object[1];
        params[0]="nuevo_parametro";
        try{
            AgentController agnt=container.createNewAgent("nuevoAgente", "es.upm.ejemplo.AgBasicoParams", params);
            agnt.start();
        }
        catch(Exception e){e.printStackTrace();}
    }
}
