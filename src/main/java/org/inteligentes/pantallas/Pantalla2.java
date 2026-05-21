package org.inteligentes.pantallas;

import org.inteligentes.Producto;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Pantalla2 extends JPanel {

    private final JPanel listaPanel;
    private final List<Producto> productos = new ArrayList<>();
    private final Pantalla3 pantalla3;
    private final CardLayout bloqueProductoLayout;
    private final JPanel contenedor;

    public Pantalla2(CardLayout bloqueProductoLayout, JPanel contenedor, Pantalla3 pantalla3) {
        this.pantalla3 = pantalla3;
        this.bloqueProductoLayout = bloqueProductoLayout;
        this.contenedor = contenedor;

        Color fondoGris       = new Color(224, 224, 224);
        Color colorGrisOscuro = new Color(51, 51, 51);
        Color colorLinea      = new Color(220, 220, 220);

        setBackground(fondoGris);
        setLayout(new GridBagLayout());

        /* JPanel auxiliar con ancho fijo pero altura dinámica para que crezca al añadir productos */
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

        /* Título de la pantalla */
        JLabel titulo = new JLabel("Product List");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(colorGrisOscuro);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(0, 4, 16, 0));

        /* El recuadro blanco que contiene la tabla de productos */
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

        /* Cabecera de la tabla con las columnas Product y Action */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblProduct = new JLabel("Product");
        lblProduct.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblProduct.setForeground(colorGrisOscuro);

        JLabel lblAction = new JLabel("Action");
        lblAction.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblAction.setForeground(colorGrisOscuro);

        header.add(lblProduct, BorderLayout.WEST);
        header.add(lblAction, BorderLayout.EAST);

        /* Separador debajo de la cabecera */
        JSeparator sepHeader = new JSeparator();
        sepHeader.setForeground(colorLinea);
        sepHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        bloqueProducto.add(header);
        bloqueProducto.add(sepHeader);

        /* Panel de filas que crece cada vez que se añade un producto */
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(Color.WHITE);
        listaPanel.setOpaque(false);
        bloqueProducto.add(listaPanel);

        /* Botón para volver a la pantalla de añadir producto */
        JButton botonVolver = new JButton("Back to Home") {
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
        /* Cuando el raton pasa por encima se pone mas claro el recuadro */
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

    /* Añade un producto a la lista y repinta el panel */
    public void agregarProducto(Producto p) {
        productos.add(p);

        /* Separador entre filas, excepto antes de la primera */
        if (productos.size() > 1) {
            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(220, 220, 220));
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            listaPanel.add(sep);
        }

        listaPanel.add(crearFila(p));
        listaPanel.revalidate();
        listaPanel.repaint();
    }


    /* Construye el panel de una fila con el nombre y el botón de acción */
    private JPanel crearFila(Producto p) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        fila.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblNombre = new JLabel(p.getNombre());
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombre.setForeground(new Color(50, 80, 140));

        fila.add(lblNombre, BorderLayout.WEST);
        fila.add(crearBotonFila(p), BorderLayout.EAST);

        return fila;
    }

    /* Crea el botón de acción de cada fila, rojo si hay alerta, azul si no */
    private JButton crearBotonFila(Producto p) {
        boolean alerta = p.isAlerta();
        String texto   = alerta ? "ALERT" : "View Item";
        Color colorBtn = alerta ? new Color(210, 45, 45) : new Color(52, 120, 210);
        Color hover    = alerta ? new Color(180, 30, 30) : new Color(30, 90, 170);

        JButton btn = new JButton(texto) {
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
        btn.setContentAreaFilled(false);
        btn.setBackground(colorBtn);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(100, 32));
        /* Cuando el raton pasa por encima se oscurece ligeramente */
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(colorBtn); }
        });
        /* Al pulsar carga el producto en Pantalla3 y navega a ella */
        btn.addActionListener(e -> {
            pantalla3.cargarProducto(p);
            bloqueProductoLayout.show(contenedor, "pantalla3");
        });

        return btn;
    }
}