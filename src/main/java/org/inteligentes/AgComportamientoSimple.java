package org.inteligentes;

import jade.core.Agent;
import jade.core.behaviours.*;
public class AgComportamientoSimple extends Agent
{
    class ComportamientoSimple extends SimpleBehaviour
    {
        public void action()
        {
            for(int i=0;i<10;i++)
                System.out.println("Ejecuto tarea " + i);
        }
        public boolean done()
        {
            return true;
            //return false;
        }
    }
    protected void setup()
    {
        System.out.println("Primer Agente JADE con Comportamiento Simple");
        ComportamientoSimple cs= new ComportamientoSimple();
        addBehaviour(cs);
        ComportamientoSimple2 cs2 = new ComportamientoSimple2();
        addBehaviour(cs2);
    }
}