package org.inteligentes;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ControladorCSV {

    private final String CSV_FILE = "historial_precios.csv";

    public HashMap<String, Producto> inicializarCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("Nombre;Timestamp;Enlace;Umbral;Precio\n");
                return new HashMap<>();
            } catch (IOException e) {
                System.out.println("[CSV] Error al inicializar el archivo");
                return new HashMap<>();
            }
        } else {
            return recuperarCSV();
        }
    }

    public void guardarEnCSV(Producto prod) {
        try (FileWriter fw = new FileWriter(CSV_FILE, true); BufferedWriter bw = new BufferedWriter(fw)) {
            String nombreLimpio = prod.getNombre().replace(";", " ");
            bw.write(String.format("%s;%s;%s;%.2f;%.2f\n", nombreLimpio, prod.ultimaFecha(), prod.getEnlace(),
                    prod.getUmbral(), prod.getPrecioActual()));
        } catch (IOException e) {
            System.out.println("[CSV] Error al escribir el nuevo precio");
            e.printStackTrace();
        }
    }

    private HashMap<String, Producto> recuperarCSV() {
        HashMap<String, Producto> productos = new HashMap<>();
        File file = new File(CSV_FILE);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;

            // Saltar cabecera
            br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length != 5) {
                    continue;
                }

                String nombre = partes[0];
                LocalDateTime fecha = LocalDateTime.parse(partes[1]);
                String enlace = partes[2];
                Double umbral = Double.parseDouble(partes[3]);
                Double precio = Double.parseDouble(partes[4]);

                // Si no existe el producto, lo creamos
                if (!productos.containsKey(nombre)) {
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
