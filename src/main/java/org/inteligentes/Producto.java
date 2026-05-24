package org.inteligentes;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Representa el estado y el historial de un producto.
 */
public class Producto implements Serializable{
    private String nombre;
    private String enlace;
    private Double umbral;
    private boolean alerta; // Flag lógico de estado. Determina si la vista (Swing) debe pintar el botón en rojo (ALERTA)

    // Arrays paralelos: Mantienen sincronizados los precios con su marca de tiempo exacta
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

    public String getEnlace() {
        return enlace;
    }


    /**
     * Devuelve una nueva instancia de la lista en lugar de la referencia original evitando que otra clase modifique 
     * el historial de fechas.
     */
    public ArrayList<LocalDateTime> getFechas() {
        return new ArrayList<>(fechas);
    }

    /**
     * Recupera el timestamp de la última lectura realizada.
     * @return LocalDateTime exacto o null si el array está vacío.
     */
    public LocalDateTime ultimaFecha() {
        return fechas.isEmpty()?null:fechas.get(fechas.size() - 1);
    }

    /**
     * Devuelve una nueva instancia de la lista en lugar de la referencia original evitando que otra clase modifique 
     * el historial de precios.
     */
    public ArrayList<Double> getPrecios() {
        return new ArrayList<>(precios);
    }

    /**
     * Recupera el valor económico más reciente.
     */
    public Double getPrecioActual() {
        return precios.isEmpty()?null:precios.get(precios.size() - 1);
    }

    /*
     * Apila el nuevo precio y marca automáticamente el momento exacto (Timestamp) 
     * en el que el Sistema Operativo procesó la lectura.
     */
    public void setPrecioActual(Double precioActual) {
        fechas.add(LocalDateTime.now());
        precios.add(precioActual);
    }

    public Double getUmbral() {
        return umbral;
    }

    public boolean isAlerta(){
        return this.alerta;
    }

    /**
     * Compara la última percepción del entorno con el objetivo del usuario.
     */
    public void updateAlerta(){
        if (!precios.isEmpty()) {
            this.alerta = precios.get(precios.size()-1) < umbral;
        }
    }

    //Otra auxiliar para rellenar BBDD
    public void upProducto(LocalDateTime fecha, Double precio){
        precios.add(precio);
        fechas.add(fecha);
    }
}