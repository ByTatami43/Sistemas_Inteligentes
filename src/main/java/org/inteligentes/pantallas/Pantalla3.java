package org.inteligentes.pantallas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Pantalla3 extends JPanel {

    private final JLabel lblNombreProducto;
    private final JLabel lblUrl;
    private final JLabel lblPrecioActual;
    private final JLabel lblVariacion;
    private final JLabel lblUmbral;
    private final JPanel historialPanel;
    private final List<RegistroPrecio> historial = new ArrayList<>();

    /* Modelo de datos de un registro de precio en el historial */
    public static class RegistroPrecio {
        public String timestamp;
        public double precioInicial;
        public double precioFinal;

        public RegistroPrecio(String timestamp, double precioInicial, double precioFinal) {
            this.timestamp = timestamp;
            this.precioInicial = precioInicial;
            this.precioFinal = precioFinal;
        }
    }

    public Pantalla3(CardLayout bloqueProductoLayout, JPanel contenedor) {
        Color fondoGris        = new Color(224, 224, 224);
        Color colorGrisOscuro  = new Color(51, 51, 51);
        Color colorLinea       = new Color(220, 220, 220);
        Color colorEtiqueta    = new Color(120, 120, 120);
        Color colorTextoNombre = new Color(50, 80, 140);
        Color colorMorado      = new Color(140, 70, 220);

        setBackground(fondoGris);
        setLayout(new GridBagLayout());

        /* JPanel auxiliar con ancho fijo pero altura dinámica */
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

        /* El recuadro blanco que agrupa todo el contenido de la pantalla */
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
        bloqueProducto.setBorder(new EmptyBorder(25, 30, 25, 30));
        bloqueProducto.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Botón morado decorativo de cabecera con el título de la app */
        JLabel tituloApp = new JLabel("Price Scraper", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tituloApp.setOpaque(false);
        tituloApp.setBackground(colorMorado);
        tituloApp.setForeground(Color.WHITE);
        tituloApp.setFont(new Font("SansSerif", Font.BOLD, 14));
        tituloApp.setMaximumSize(new Dimension(180, 40));
        tituloApp.setPreferredSize(new Dimension(180, 40));
        tituloApp.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Etiqueta y campo de URL */
        JLabel lblUrlTitulo = new JLabel("URL", SwingConstants.CENTER);
        lblUrlTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUrlTitulo.setForeground(colorEtiqueta);
        lblUrlTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUrlTitulo.setBorder(new EmptyBorder(20, 0, 5, 0));

        lblUrl = new JLabel("");
        lblUrl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUrl.setForeground(colorGrisOscuro);
        lblUrl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorLinea, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        lblUrl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        /* Etiqueta y nombre del producto */
        JLabel lblProductTitulo = new JLabel("Product");
        lblProductTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblProductTitulo.setForeground(colorEtiqueta);
        lblProductTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblProductTitulo.setBorder(new EmptyBorder(20, 0, 5, 0));

        lblNombreProducto = new JLabel("—");
        lblNombreProducto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombreProducto.setForeground(colorTextoNombre);
        lblNombreProducto.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* Fila con los tres bloques de precio: Precio Actual, Variación y Umbral */
        JPanel preciosPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        preciosPanel.setOpaque(false);
        preciosPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        preciosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        preciosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        lblPrecioActual = new JLabel("—");
        lblVariacion    = new JLabel("—");
        lblUmbral       = new JLabel("—");

        preciosPanel.add(bloquePrecio("Precio Actual", lblPrecioActual, colorEtiqueta, colorGrisOscuro));
        preciosPanel.add(bloquePrecio("Variación",     lblVariacion,    colorEtiqueta, colorGrisOscuro));
        preciosPanel.add(bloquePrecio("Umbral",        lblUmbral,       colorEtiqueta, colorGrisOscuro));

        /* Cabecera de la tabla de historial */
        JPanel headerHistorial = new JPanel(new GridLayout(1, 3));
        headerHistorial.setOpaque(false);
        headerHistorial.setBorder(new EmptyBorder(8, 0, 8, 0));
        headerHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hTimestamp = new JLabel("Timestamp");
        JLabel hInicial   = new JLabel("Initial Price");
        JLabel hFinal     = new JLabel("Final Price");
        for (JLabel l : new JLabel[]{hTimestamp, hInicial, hFinal}) {
            l.setFont(new Font("SansSerif", Font.BOLD, 13));
            l.setForeground(colorGrisOscuro);
        }
        headerHistorial.add(hTimestamp);
        headerHistorial.add(hInicial);
        headerHistorial.add(hFinal);

        /* Separador debajo de la cabecera */
        JSeparator sepHeader = new JSeparator();
        sepHeader.setForeground(colorLinea);
        sepHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* Panel del historial de precios que crece con cada registro */
        historialPanel = new JPanel();
        historialPanel.setLayout(new BoxLayout(historialPanel, BoxLayout.Y_AXIS));
        historialPanel.setOpaque(false);
        historialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* Botón para volver a la pantalla de la lista de productos */
        JButton botonVolver = new JButton("Back to Product List") {
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
        botonVolver.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        botonVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonVolver.setBorder(new EmptyBorder(0, 0, 0, 0));
        /* Cuando el raton pasa por encima se pone mas claro el recuadro */
        botonVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVolver.setBackground(new Color(68, 68, 68)); }
            public void mouseExited(MouseEvent e)  { botonVolver.setBackground(colorGrisOscuro); }
        });
        botonVolver.addActionListener(e -> bloqueProductoLayout.show(contenedor, "pantalla2"));

        /* Ensamblado del contenido dentro del bloque blanco */
        bloqueProducto.add(tituloApp);
        bloqueProducto.add(lblUrlTitulo);
        bloqueProducto.add(lblUrl);
        bloqueProducto.add(lblProductTitulo);
        bloqueProducto.add(lblNombreProducto);
        bloqueProducto.add(preciosPanel);
        bloqueProducto.add(headerHistorial);
        bloqueProducto.add(sepHeader);
        bloqueProducto.add(historialPanel);
        bloqueProducto.add(Box.createVerticalStrut(25));
        bloqueProducto.add(botonVolver);

        centerBlock.add(bloqueProducto);
        add(centerBlock);
    }

    /* Construye uno de los tres bloques de precio (etiqueta arriba, valor abajo) */
    private JPanel bloquePrecio(String titulo, JLabel valor, Color colorEtiqueta, Color colorValor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitulo.setForeground(colorEtiqueta);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        valor.setFont(new Font("SansSerif", Font.BOLD, 22));
        valor.setForeground(colorValor);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valor);
        return panel;
    }

    /* Carga los datos de un producto en la pantalla, limpiando lo anterior */
    public void cargarProducto(String nombre, String url, double precioActual, double variacion, double umbral) {
        lblNombreProducto.setText(nombre);
        lblUrl.setText(url);
        lblPrecioActual.setText(String.format("%.2f €", precioActual));
        lblVariacion.setText(String.format("%+.2f €", variacion));
        lblUmbral.setText(String.format("%.2f €", umbral));

        historial.clear();
        historialPanel.removeAll();
        historialPanel.revalidate();
        historialPanel.repaint();
    }

    /* Añade un registro al historial de precios y repinta */
    public void agregarRegistroHistorial(RegistroPrecio r) {
        historial.add(r);
        historialPanel.add(crearFilaHistorial(r));
        historialPanel.revalidate();
        historialPanel.repaint();
    }

    /* Construye una fila del historial con timestamp, precio inicial y precio final */
    private JPanel crearFilaHistorial(RegistroPrecio r) {
        JPanel fila = new JPanel(new GridLayout(1, 3));
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(10, 0, 10, 0));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTimestamp = new JLabel(r.timestamp);
        JLabel lblInicial   = new JLabel(String.format("%.2f €", r.precioInicial));
        JLabel lblFinal     = new JLabel(String.format("%.2f €", r.precioFinal));

        for (JLabel l : new JLabel[]{lblTimestamp, lblInicial, lblFinal}) {
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            l.setForeground(new Color(60, 60, 60));
        }

        fila.add(lblTimestamp);
        fila.add(lblInicial);
        fila.add(lblFinal);
        return fila;
    }

    /* main para probar Pantalla3 de forma aislada */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Product Detail");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(820, 720);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel contenedor = new JPanel(cardLayout);

            Pantalla3 p3 = new Pantalla3(cardLayout, contenedor);
            contenedor.add(p3, "pantalla3");
            contenedor.add(new JLabel("Pantalla 2"), "pantalla2");

            p3.cargarProducto(
                    "Wireless Headphones Pro",
                    "https://example-store.com/product/12345",
                    119.99,
                    -10.00,
                    125.00
            );
            p3.agregarRegistroHistorial(new RegistroPrecio("2025-11-17", 129.99, 119.99));
            p3.agregarRegistroHistorial(new RegistroPrecio("2025-11-16", 132.00, 125.00));
            p3.agregarRegistroHistorial(new RegistroPrecio("2025-11-15", 135.00, 129.99));

            cardLayout.show(contenedor, "pantalla3");
            frame.add(contenedor);
            frame.setVisible(true);
        });
    }
}