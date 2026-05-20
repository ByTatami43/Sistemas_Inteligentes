package org.inteligentes;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Producto {

    private class PHistorial {
        
        private ArrayList<LocalDateTime> fecha;
        private ArrayList<Double> precios;

        private PHistorial(){
            this.fecha = new ArrayList<LocalDateTime>();
            this.precios = new ArrayList<Double>();
        }
    }

    private String nombre;
    private String enlace;
    private Double precioMinimo;
    private Double umbral;
    private boolean alerta;
    private PHistorial historial;

    public Producto(String nombre, String enlace, Double umbral) {
        this.nombre = nombre;
        this.enlace = enlace;
        this.umbral = umbral;
        this.precioMinimo = Double.MAX_VALUE;
        alerta = false;
        historial = new PHistorial();
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
        return new ArrayList<>(historial.fecha);
    }

    //ESTO NO LO VEO NECESARIO SALVO PARA AÑADIR A MANO
    public void setFecha(LocalDateTime fecha) {
        historial.fecha.add(fecha);
    }

    public ArrayList<Double> getPrecios() {
        return new ArrayList<>(historial.precios);
    }

    public Double getPrecioActual() {
        return historial.precios.isEmpty()?null:historial.precios.get(historial.precios.size() - 1);
    }

    public void setPrecioActual(Double precioActual) {
        setFecha(LocalDateTime.now());
        if (precioActual < precioMinimo)
            setPrecioMinimo(precioActual);
        historial.precios.add(precioActual);
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
        historial.precios.add(precio);
        historial.fecha.add(fecha);
    }
}
