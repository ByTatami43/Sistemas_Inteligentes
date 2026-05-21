package org.inteligentes;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.Serializable;

public class Producto implements Serializable{

    private String nombre;
    private String enlace;
    private Double precioMinimo;
    private Double umbral;
    private boolean alerta; 
    private ArrayList<LocalDateTime> fechas;
    private ArrayList<Double> precios;
    
    public Producto(String nombre, String enlace, Double umbral) {
        this.nombre = nombre;
        this.enlace = enlace;
        this.umbral = umbral;
        this.precioMinimo = Double.MAX_VALUE;
        alerta = false;
        fechas = new ArrayList<LocalDateTime>();
        precios = new ArrayList<Double>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public ArrayList<LocalDateTime> getFechas() {
        return new ArrayList<>(fechas);
    }

    //ESTO NO LO VEO NECESARIO SALVO PARA AÑADIR A MANO
    public void setFecha(LocalDateTime fecha) {
        fechas.add(fecha);
    }

    public ArrayList<Double> getPrecios() {
        return new ArrayList<>(precios);
    }

    public Double getPrecioActual() {
        return precios.isEmpty()?null:precios.get(precios.size() - 1);
    }

    public void setPrecioActual(Double precioActual) {
        setFecha(LocalDateTime.now());
        if (precioActual < precioMinimo)
            setPrecioMinimo(precioActual);
        precios.add(precioActual);
    }

    public Double getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(Double precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public Double getUmbral() {
        return umbral;
    }

    public void setUmbral(Double umbral) {
        this.umbral = umbral;
    }

    public boolean isAlerta() {
        return alerta;
    }

    public void setAlerta(boolean alerta) {
        this.alerta = alerta;
    }


    //Otra auxiliar para rellenar BBDD
    public void upProducto(LocalDateTime fecha, Double precio){
        if (precio < precioMinimo)
            setPrecioMinimo(precio);
        precios.add(precio);
        fechas.add(fecha);
    }
}
