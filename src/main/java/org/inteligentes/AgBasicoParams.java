package org.inteligentes;

import jade.core.Agent;

public class AgBasicoParams extends Agent
{
    protected void setup()
    {
        Object [] listaparametros = getArguments();
        if ((listaparametros == null) || (listaparametros.length < 1))
        {
            System.out.println("No se han introducido parametros");
        }
        else
        {
            System.out.println("Agente JADE con Parametros: Soy el agente " + getLocalName());
            int i;
            for (i=0;i<listaparametros.length;i++)
                System.out.println("Parametro " + i + "es:" + (String) listaparametros[i]);
        }
        doWait(10000 );
        doDelete();
    }
    protected void takeDown()
    {
        System.out.println("Bye…..");
    }
}