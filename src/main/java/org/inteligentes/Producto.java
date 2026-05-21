package org.inteligentes;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.Serializable;

public class Producto implements Serializable{
    private String nombre;
    private String enlace;
    private Double umbral;
    private boolean alerta; 
    private ArrayList<LocalDateTime> fechas;
    private ArrayList<Double> precios;

    public Producto(String nombre, String enlace, Double umbral) {
        this.nombre = nombre;
        this.enlace = enlace;
        this.umbral = umbral;
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

    public LocalDateTime ultimaFecha() {
        return fechas.isEmpty()?null:fechas.get(fechas.size() - 1);
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
        precios.add(precioActual);
    }

    public Double getUmbral() {
        return umbral;
    }

    public void setUmbral(Double umbral) {
        this.umbral = umbral;
    }

    public boolean isAlerta(){
        return this.alerta;
    }

    public void updateAlerta(){
        this.alerta = precios.get(precios.size()-1) < umbral;
    }

    //Otra auxiliar para rellenar BBDD
    public void upProducto(LocalDateTime fecha, Double precio){
        precios.add(precio);
        fechas.add(fecha);
    }
}