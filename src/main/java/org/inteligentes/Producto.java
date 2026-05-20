package org.inteligentes;

import java.time.LocalDateTime;

public class Producto {
    String nombre;
    String enlace;
    LocalDateTime fecha;
    Double precioActual;
    Double precioMinimo;
    Double umbral;
    boolean alerta;
    public Producto(String nombre, String enlace,Double umbral){
        this.nombre = nombre;
        this.enlace = enlace;
        this.umbral = umbral;
        alerta = false;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(Double precioActual) {
        this.precioActual = precioActual;
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

}
