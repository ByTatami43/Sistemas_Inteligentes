package org.inteligentes.pantallas;

import org.inteligentes.Producto;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Pantalla3 extends JPanel {

    private final JLabel lblNombreProducto;
    private final JLabel lblUrl;
    private final JLabel lblPrecioActual;
    private final JLabel lblVariacion;
    private final JLabel lblUmbral;
    private final JPanel historialPanel;
    private final ChartPanel panelGrafica;

    // ELIMINAMOS offsetIzq porque la propia gráfica empujará el contenido

    public Pantalla3(CardLayout bloqueProductoLayout, JPanel contenedor) {
        Color fondoGris        = new Color(224, 224, 224);
        Color colorGrisOscuro  = new Color(51, 51, 51);
        Color colorLinea       = new Color(220, 220, 220);
        Color colorEtiqueta    = new Color(120, 120, 120);
        Color colorTextoNombre = new Color(50, 80, 140);

        setBackground(fondoGris);
        setLayout(new GridBagLayout());

        /* JPanel auxiliar con ancho fijo pero altura dinámica */
        JPanel centerBlock = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, super.getPreferredSize().height);
            }
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(800, Integer.MAX_VALUE);
            }
        };
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));
        centerBlock.setBackground(fondoGris);

        /* El recuadro blanco principal */
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

        // URL
        JLabel lblUrlTitulo = new JLabel("URL", SwingConstants.CENTER);
        lblUrlTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUrlTitulo.setForeground(colorEtiqueta);
        lblUrlTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUrlTitulo.setBorder(new EmptyBorder(0, 0, 5, 650));

        lblUrl = new JLabel("");
        lblUrl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUrl.setForeground(colorGrisOscuro);
        lblUrl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorLinea, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        lblUrl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // PAnel divisor
        JPanel panelDivisor = new JPanel();
        panelDivisor.setLayout(new BoxLayout(panelDivisor, BoxLayout.X_AXIS));
        panelDivisor.setOpaque(false);
        panelDivisor.setAlignmentX(Component.CENTER_ALIGNMENT);


        // grafica
        panelGrafica = new ChartPanel(null);
        panelGrafica.setOpaque(false);
        panelGrafica.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panelGrafica.setPreferredSize(new Dimension(250, 250));
        panelGrafica.setMinimumSize(new Dimension(250, 250));
        panelGrafica.setMaximumSize(new Dimension(350, 400));
        panelGrafica.setAlignmentY(Component.TOP_ALIGNMENT); // Para que se alinee arriba con el texto



        // panel detalles para los detalles
        JPanel panelDetalles = new JPanel();
        panelDetalles.setLayout(new BoxLayout(panelDetalles, BoxLayout.Y_AXIS));
        panelDetalles.setOpaque(false);
        panelDetalles.setAlignmentY(Component.TOP_ALIGNMENT); // Para que se alinee arriba con la gráfica

        /* Componentes de la columna derecha (sin offsetIzq) */
        JLabel lblProductTitulo = new JLabel("Producto");
        lblProductTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblProductTitulo.setForeground(colorEtiqueta);
        lblProductTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblProductTitulo.setBorder(new EmptyBorder(0, 0, 5, 0));

        lblNombreProducto = new JLabel("—");
        lblNombreProducto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombreProducto.setForeground(colorTextoNombre);
        lblNombreProducto.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        JPanel headerHistorial = new JPanel(new GridLayout(1, 2));
        headerHistorial.setOpaque(false);
        headerHistorial.setBorder(new EmptyBorder(8, 0, 8, 0));
        headerHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hTimestamp = new JLabel("Timestamp");
        JLabel hInicial   = new JLabel("Precio");
        for (JLabel l : new JLabel[]{hTimestamp, hInicial}) {
            l.setFont(new Font("SansSerif", Font.BOLD, 13));
            l.setForeground(colorGrisOscuro);
        }
        headerHistorial.add(hTimestamp);
        headerHistorial.add(hInicial);

        JPanel sepWrapper = new JPanel(new BorderLayout());
        sepWrapper.setOpaque(false);
        sepWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator lineaReal = new JSeparator();
        lineaReal.setForeground(colorLinea);
        sepWrapper.add(lineaReal, BorderLayout.CENTER);

        historialPanel = new JPanel();
        historialPanel.setLayout(new BoxLayout(historialPanel, BoxLayout.Y_AXIS));
        historialPanel.setOpaque(false);
        historialPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollHistorial = new JScrollPane(historialPanel);
        scrollHistorial.setOpaque(false);
        scrollHistorial.getViewport().setOpaque(false);
        scrollHistorial.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollHistorial.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollHistorial.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollHistorial.getVerticalScrollBar().setUnitIncrement(16);
        scrollHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollHistorial.setPreferredSize(new Dimension(400, 150));
        scrollHistorial.setMinimumSize(new Dimension(400, 100));
        scrollHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Añadimos al panel de la derecha
        panelDetalles.add(lblProductTitulo);
        panelDetalles.add(lblNombreProducto);
        panelDetalles.add(preciosPanel);
        panelDetalles.add(headerHistorial);
        panelDetalles.add(sepWrapper);
        panelDetalles.add(scrollHistorial);

        // Añadimos las dos columnas al panel divisor central
        panelDivisor.add(panelGrafica);
        panelDivisor.add(Box.createHorizontalStrut(30)); // 30px de margen entre grafica y textos
        panelDivisor.add(panelDetalles);


        // Boton para volver
        JButton botonVolver = new JButton("Volver a la lista de productos") {
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

        botonVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVolver.setBackground(new Color(68, 68, 68)); }
            public void mouseExited(MouseEvent e)  { botonVolver.setBackground(colorGrisOscuro); }
        });
        botonVolver.addActionListener(e -> bloqueProductoLayout.show(contenedor, "pantalla2"));

        // ponemos ya junto
        bloqueProducto.add(lblUrlTitulo);
        bloqueProducto.add(lblUrl);
        bloqueProducto.add(Box.createVerticalStrut(25)); // Espacio entre URL y columnas

        bloqueProducto.add(panelDivisor); // Añadimos el bloque que contiene la gráfica y los textos

        bloqueProducto.add(Box.createVerticalStrut(25));
        bloqueProducto.add(botonVolver);

        centerBlock.add(bloqueProducto);
        add(centerBlock);
    }

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

    public void cargarProducto(Producto p) {
        lblNombreProducto.setText(p.getNombre());
        lblUrl.setText(p.getEnlace());
        lblUmbral.setText(String.format("%.2f €", p.getUmbral()));

        Double actual = p.getPrecioActual();
        if (actual != null) {
            lblPrecioActual.setText(String.format("%.2f €", actual));
            ArrayList<Double> precios = p.getPrecios();
            if (precios.size() >= 2) {
                double anterior = precios.get(precios.size() - 2);
                double variacion = actual - anterior;
                lblVariacion.setText(String.format("%+.2f €", variacion));
            } else {
                lblVariacion.setText("—");
            }
        } else {
            lblPrecioActual.setText("—");
            lblVariacion.setText("—");
        }

        historialPanel.removeAll();
        ArrayList<Double> precios = p.getPrecios();
        ArrayList<LocalDateTime> fechas = p.getFechas();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (int i = precios.size() - 1; i >= 1; i--) {
            String ts = fechas.get(i).format(fmt);
            double precioIni = precios.get(i - 1);
            historialPanel.add(crearFilaHistorial(ts, precioIni));
        }
        historialPanel.revalidate();
        historialPanel.repaint();

        // grafica
        // 1. Grafica
        XYSeries serie = new XYSeries("PRECIO");

        for (int i = 0; i < precios.size(); i++) {
            serie.add(i, precios.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);
        JFreeChart chart = ChartFactory.createXYLineChart(null,null, null,
                dataset, PlotOrientation.VERTICAL, false, true, false);
        // obtenemos el plot
        XYPlot plot = chart.getXYPlot();
        // quitamos los fondos del plot
        plot.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        // quitamos los ejes
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);
        // estilo linea azul
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(135, 206, 235));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));
        plot.setRenderer(renderer);
        // linea negra (UMBRAL)
        double precioUmbral = p.getUmbral();
        ValueMarker marker = new ValueMarker(precioUmbral);
        System.out.println(p.getUmbral());
        marker.setPaint(Color.BLACK); // Color de la línea punteada
        float[] dashPattern = {10.0f, 10.0f};
        marker.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
        plot.addRangeMarker(marker);
        double maximaDistancia = 0.0;
        for (Double precio : precios) {
            double distancia = Math.abs(precio - precioUmbral);
            if (distancia > maximaDistancia) {
                maximaDistancia = distancia;
            }
        }
        if (maximaDistancia == 0.0) {
            maximaDistancia = 1.0; // Por si todos los precios son exactamente iguales al umbral
        } else {
            maximaDistancia = maximaDistancia * 1.1;
        }

        // 3. Obligamos al eje Y a ser perfectamente simétrico respecto al umbral
        double limiteInferior = precioUmbral - maximaDistancia;
        double limiteSuperior = precioUmbral + maximaDistancia;

        NumberAxis yAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRange(false); // Apagamos el zoom automático
        yAxis.setRange(limiteInferior, limiteSuperior); // Centramos la cámara
        panelGrafica.setChart(chart);


        this.revalidate();
    }

    private JPanel crearFilaHistorial(String timestamp, double precioInicial) {
        JPanel fila = new JPanel(new GridLayout(1, 2));
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(10, 0, 10, 0));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTimestamp = new JLabel(timestamp);
        JLabel lblInicial   = new JLabel(String.format("%.2f €", precioInicial));

        for (JLabel l : new JLabel[]{lblTimestamp, lblInicial}) {
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            l.setForeground(new Color(60, 60, 60));
        }

        fila.add(lblTimestamp);
        fila.add(lblInicial);
        return fila;
    }
}