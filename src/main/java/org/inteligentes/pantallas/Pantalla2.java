package org.inteligentes.pantallas;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.inteligentes.AgenteInterfaz;
import org.inteligentes.Producto;

/**
 * Listado principal del Dashboard.
 * Renderiza dinámicamente cada producto bajo seguimiento. Modifica sus componentes 
 * y colores (Azul/Rojo) según las alertas de mercado inyectadas desde el Agente Procesador.
 */
public class Pantalla2 extends JPanel {
    private Producto productoSeleccionado = null; // Guarda el producto bajo inspección detallada
    private final JPanel listaPanel; // Contenedor de las filas de productos
    private final List<Producto> productos = new ArrayList<>(); // Caché local de los productos
    private final Pantalla3 pantalla3;
    private final CardLayout bloqueProductoLayout;
    private final JPanel contenedor;
    private AgenteInterfaz agenteInterfaz;
    public Pantalla2(CardLayout bloqueProductoLayout, JPanel contenedor, Pantalla3 pantalla3, AgenteInterfaz agente) {
        this.pantalla3 = pantalla3;
        this.bloqueProductoLayout = bloqueProductoLayout;
        this.contenedor = contenedor;
        this.agenteInterfaz = agente;
        Color fondoGris       = new Color(224, 224, 224);
        Color colorGrisOscuro = new Color(51, 51, 51);
        Color colorLinea      = new Color(220, 220, 220);

        setBackground(fondoGris);
        setLayout(new GridBagLayout());

        // JPanel auxiliar con ancho fijo pero altura dinámica para que crezca al añadir productos 
        JPanel centerBlock = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, super.getPreferredSize().height);
            }
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(700, Integer.MAX_VALUE);
            }
        };
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));
        centerBlock.setBackground(fondoGris);

        // Título de la pantalla
        JLabel titulo = new JLabel("Lista de productos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(colorGrisOscuro);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(0, 4, 16, 0));

        // El recuadro blanco que contiene la tabla de productos
        JPanel bloqueProducto = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        bloqueProducto.setOpaque(false);
        bloqueProducto.setBackground(Color.WHITE);
        bloqueProducto.setLayout(new BoxLayout(bloqueProducto, BoxLayout.Y_AXIS));
        bloqueProducto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Cabecera de la tabla con las columnas Product y Action
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblProduct = new JLabel("Producto");
        lblProduct.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblProduct.setForeground(colorGrisOscuro);

        JLabel lblAction = new JLabel("Acción");
        lblAction.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblAction.setForeground(colorGrisOscuro);

        header.add(lblProduct, BorderLayout.WEST);
        header.add(lblAction, BorderLayout.EAST);

        // Separador debajo de la cabecera
        JSeparator sepHeader = new JSeparator();
        sepHeader.setForeground(colorLinea);
        sepHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        bloqueProducto.add(header);
        bloqueProducto.add(sepHeader);

        // Panel de filas que crece cada vez que se añade un producto
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(Color.WHITE);
        listaPanel.setOpaque(false);
        bloqueProducto.add(listaPanel);

        // Botón para volver a la pantalla de añadir producto
        JButton botonVolver = new JButton("Volver") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        botonVolver.setContentAreaFilled(false);
        botonVolver.setBackground(colorGrisOscuro);
        botonVolver.setForeground(Color.WHITE);
        botonVolver.setFocusPainted(false);
        botonVolver.setBorderPainted(false);
        botonVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVolver.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonVolver.setPreferredSize(new Dimension(150, 40));
        // Cuando el raton pasa por encima se pone mas claro el recuadro
        botonVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVolver.setBackground(new Color(68, 68, 68)); }
            public void mouseExited(MouseEvent e)  { botonVolver.setBackground(colorGrisOscuro); }
        });
        botonVolver.addActionListener(e -> bloqueProductoLayout.show(contenedor, "pantalla1"));

        JPanel botonVolverPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        botonVolverPanel.setBackground(fondoGris);
        botonVolverPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        botonVolverPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonVolverPanel.add(botonVolver);

        centerBlock.add(titulo);
        centerBlock.add(bloqueProducto);
        centerBlock.add(botonVolverPanel);

        add(centerBlock);
    }

    /**
     * Invocado por el hilo de ejecución de JADE (Agente Interfaz) cada vez que 
     * cambian los estados o llega un nuevo lote de precios. Rehace la vista.
     * @param productosActualizados Mapa completo de productos actualizado desde el Agente Procesamiento
     */
    public void actualizarProductos(HashMap<String, Producto> productosActualizados) {
        System.out.println("[Pantalla2] actualizarProductos llamado con " + productosActualizados.size() + " productos");
        // Sincroniza la lista local con la lista actualizada del agente
        productos.clear();
        productos.addAll(productosActualizados.values());
        System.out.println("[Pantalla2] productos en lista: " + productos.size());

        // Limpieza total del lienzo gráfico viejo para evitar solapamientos
        listaPanel.removeAll();

        // Reconstrucción del listado fila por fila
        for (int i = 0; i < productos.size(); i++) {
            System.out.println("[Pantalla2] " + productos.get(i).getNombre() + " alerta: " + productos.get(i).isAlerta());
            if (i > 0) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(220, 220, 220));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                listaPanel.add(sep);
            }
            listaPanel.add(crearFila(productos.get(i)));
        }
        listaPanel.revalidate();
        listaPanel.repaint();
        System.out.println("[Pantalla2] repintado completado");
        
        /*Si el usuario tiene abierta la pantalla de detalles (Pantalla3) 
          de un producto concreto, le inyectamos los datos actualizados en background.*/
        if (productoSeleccionado != null) {
            Producto actualizado = productos.stream()
                    .filter(p -> p.getEnlace().equals(productoSeleccionado.getEnlace()))
                    .findFirst()
                    .orElse(null);
            if (actualizado != null) {
                pantalla3.cargarProducto(actualizado);
            }
        }
    }

    // Construye el panel de una fila con el nombre y el botón de acción
    private JPanel crearFila(Producto p) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        fila.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Muestra el nombre y el precio actual si lo tiene
        String texto = p.getNombre();
        if (p.getPrecioActual() != null) {
            texto += String.format("  (%.2f €)", p.getPrecioActual());
        }
        JLabel lblNombre = new JLabel(texto);
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombre.setForeground(new Color(50, 80, 140));

        fila.add(lblNombre, BorderLayout.WEST);
        fila.add(crearBotonFila(p), BorderLayout.EAST); // El botón cambia según el estado lógico de alerta

        return fila;
    }

    /**
     * Creación de botones.
     * Evalúa el flag booleano 'isAlerta' inyectado por el Agente de Procesamiento 
     * para cambiar dinámicamente la semántica del botón (Azul-Estable / Rojo-Oportunidad de compra).
     * @param p Producto asociado a esta fila para configurar el botón de acción
     */
    private JButton crearBotonFila(Producto p) {
        int indice = productos.indexOf(p);
        boolean alerta = p.isAlerta();
        String texto   = alerta ? "ALERTA" : "Ver";
        Color colorAlerta = alerta ? new Color(210, 45, 45) : new Color(52, 120, 210);
        Color hover    = alerta ? new Color(180, 30, 30) : new Color(30, 90, 170);

        JButton botonVer = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        botonVer.setContentAreaFilled(false);
        botonVer.setBackground(colorAlerta);
        botonVer.setForeground(Color.WHITE);
        botonVer.setFocusPainted(false);
        botonVer.setBorderPainted(false);
        botonVer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVer.setFont(new Font("SansSerif", Font.BOLD, 12));
        botonVer.setPreferredSize(new Dimension(100, 32));
        // Cuando el raton pasa por encima se oscurece ligeramente
        botonVer.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVer.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { botonVer.setBackground(colorAlerta); }
        });
        // Al pulsar carga el producto en Pantalla3 y navega a ella
        botonVer.addActionListener(e -> {
            productoSeleccionado = productos.get(indice);
            agenteInterfaz.solicitarActualizacion();
            pantalla3.cargarProducto(productoSeleccionado);
            bloqueProductoLayout.show(contenedor, "pantalla3");
        });

        return botonVer;
    }

        /**
        * Método de utilidad para verificar si un producto ya existe en la lista bajo seguimiento.
        * Evita duplicados al añadir nuevos productos desde Pantalla1.
        */
    public boolean contieneEnlace(String url) {
        return productos.stream().anyMatch(p -> p.getEnlace().equals(url));
    }
}