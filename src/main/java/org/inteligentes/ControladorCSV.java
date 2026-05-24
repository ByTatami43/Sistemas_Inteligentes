package org.inteligentes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;

/**
 * Gestor de Persistencia y Sistema de Archivos.
 * Se encarga de serializar y deserializar el estado del Sistema Multiagente en disco duro.
 */
public class ControladorCSV {

    private final String CSV_FILE = "historial_precios.csv"; // Ruta estática relativa del archivo csv


    /**
     * Rutina de arranque.
     * Comprueba la integridad del sistema de archivos al iniciar el Agente de Procesamiento.
     * * @return Un HashMap vacío si es la primera ejecución, o el estado histórico recuperado.
     */
    public HashMap<String, Producto> inicializarCSV() {
        File file = new File(CSV_FILE);
        // Si el archivo no existe, realiza la configuración inicial (crea archivo)
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("Nombre;Timestamp;Enlace;Umbral;Precio\n"); // Escribe la cabecera del CSV (formato de columnas)
                return new HashMap<>();
            } catch (IOException e) {
                System.out.println("[CSV] Error al inicializar el archivo");
                return new HashMap<>();
            }
        } else {
            return recuperarCSV(); // Si ya existe, delega en la rutina de recuperación de estado
        }
    }

    /**
     * Operación de escritura en disco.
     * Guarda un nuevo registro de precio al final del archivo.
     * * @param prod El producto cuyo estado actual se va a registrar.
     */
    public void guardarEnCSV(Producto prod) {
        try (FileWriter fw = new FileWriter(CSV_FILE, true); BufferedWriter bw = new BufferedWriter(fw)) {
            String nombreLimpio = prod.getNombre().replace(";", " "); // Reemplaza los posibles ";" en el nombre del producto por espacios
            // Fuerza que los números decimales se guarden con punto '.'
            bw.write(String.format(Locale.US,"%s;%s;%s;%.2f;%.2f\n", nombreLimpio, prod.ultimaFecha(), prod.getEnlace(),
                    prod.getUmbral(), prod.getPrecioActual()));
        } catch (IOException e) {
            System.out.println("[CSV] Error al escribir el nuevo precio");
            e.printStackTrace();
        }
    }

    /**
     * Recuperación del estado histórico.
     * Lee el archivo desde el disco y reconstruye el HashMap de Productos
     * con todos sus arrays de precios y fechas intactos.
     */
    private HashMap<String, Producto> recuperarCSV() {
        HashMap<String, Producto> productos = new HashMap<>();
        File file = new File(CSV_FILE);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;

            br.readLine(); // Saltar cabecera para no intentar parsearla como producto
            while ((linea = br.readLine()) != null) { // Lectura secuencial hasta encontrar el EOF
                String[] partes = linea.split(";");

                // Si la línea no coincide con el formato esperado, la salta
                if (partes.length != 5) {
                    continue;
                }

                // Extracción y casteo de tipos
                String nombre = partes[0];
                LocalDateTime fecha = LocalDateTime.parse(partes[1]);
                String enlace = partes[2];
                Double umbral = Double.parseDouble(partes[3]);
                Double precio = Double.parseDouble(partes[4]);
                
                // Si no existe el producto, lo creamos
                if (!productos.containsKey(enlace)) {
                    Producto nuevo = new Producto(nombre, enlace, umbral);
                    productos.put(enlace, nuevo);
                }

                // Añadimos la muestra histórica
                productos.get(enlace).upProducto(fecha, precio);
            }
        } catch (IOException e) {
            System.out.println("[CSV] Error al cargar datos");

            e.printStackTrace();
        }
        return productos;
    }
}
