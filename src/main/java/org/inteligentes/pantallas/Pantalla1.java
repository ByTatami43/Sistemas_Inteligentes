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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.inteligentes.AgenteInterfaz;

/**
 * Formulario de registro de productos.
 * Permite al usuario introducir el nombre, URL y precio objetivo de un artículo.
 * Realiza las validaciones previas en cliente antes de delegar en el Agente de Interfaz.
 */
public class Pantalla1 extends JPanel {

    private JTextArea urlInput;
    private JTextField precioInput;
    private JTextField nombreInput;
    private JButton botonAdd;
    private JButton botonVerLista;
    private static final Color COLOR_BLOQUE = new Color(153, 153, 153);
    private static final Color FONDO_GRIS        = new Color(224, 224, 224);
    private static final Color COLOR_GRIS_OSCURO = new Color(51, 51, 51);
    private static final Color COLOR_TEXTO       = new Color(153, 153, 153);
    private static final Color COLOR_BORDE_FINO  = new Color(85, 85, 85);
    private static final Color COLOR_HOVER       = new Color(68, 68, 68);

    public Pantalla1(CardLayout bloqueProductoLayout, JPanel contenedor, Pantalla2 pantalla2, AgenteInterfaz agente) {
        setBackground(FONDO_GRIS);
        setLayout(new GridBagLayout());

        // JPanel auxiliar para que pueda agrupar el recuadro para añadir un producto y el de ver la lista de productos
        JPanel centerBlock = new JPanel();
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));
        centerBlock.setBackground(FONDO_GRIS);

        // El recuadro negro donde se añade el producto 
        JPanel bloqueProducto = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        bloqueProducto.setOpaque(false);
        bloqueProducto.setBackground(COLOR_GRIS_OSCURO);
        bloqueProducto.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Campo de la URL
        urlInput = new JTextArea(3, 40);
        urlInput.setLineWrap(true);
        urlInput.setWrapStyleWord(true);
        estiloCampo(urlInput, 15);
        configurarPlaceholder(urlInput, "Pega aquí la URL del producto");

        // Campo del precio y su etiqueta
        JLabel precioBuscado = new JLabel("Precio buscado (€)");
        precioBuscado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        precioBuscado.setForeground(COLOR_TEXTO);
        precioBuscado.setAlignmentX(Component.LEFT_ALIGNMENT);

        precioInput = new JTextField(10);
        estiloCampo(precioInput, 14);
        configurarPlaceholder(precioInput, "0,00");

        // Campo del nombre y su etiqueta
        JLabel nombreProducto = new JLabel("Nombre del Producto");
        nombreProducto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nombreProducto.setForeground(COLOR_TEXTO);
        nombreProducto.setAlignmentX(Component.LEFT_ALIGNMENT);

        nombreInput = new JTextField(10);
        estiloCampo(nombreInput, 14);
        configurarPlaceholder(nombreInput, "Escriba el nombre del producto");

        // Hace que aparezca scrollbar si el bloque es muy largo
        JScrollPane scrollPane = new JScrollPane(urlInput);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_GRIS_OSCURO);

        // Agrupa la etiqueta y el campo del precio
        JPanel precioPanel = new JPanel();
        precioPanel.setLayout(new BoxLayout(precioPanel, BoxLayout.Y_AXIS));
        precioPanel.setBackground(COLOR_GRIS_OSCURO);
        precioPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        precioPanel.add(precioBuscado);
        precioPanel.add(Box.createVerticalStrut(5));
        precioPanel.add(precioInput);

        // Agrupa la etiqueta y el campo del nombre
        JPanel nombrePanel = new JPanel();
        nombrePanel.setLayout(new BoxLayout(nombrePanel, BoxLayout.Y_AXIS));
        nombrePanel.setBackground(COLOR_GRIS_OSCURO);
        nombrePanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        nombrePanel.add(nombreProducto);
        nombrePanel.add(Box.createVerticalStrut(5));
        nombrePanel.add(nombreInput);

        // Boton para añadir el producto
        botonAdd = new JButton("Añadir Producto") {
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
        botonAdd.setContentAreaFilled(false);
        botonAdd.setBackground(COLOR_GRIS_OSCURO);
        botonAdd.setForeground(Color.WHITE);
        botonAdd.setFocusPainted(false);
        botonAdd.setBorder(new BotonRedondeado(COLOR_BORDE_FINO, 1, 8));
        botonAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
        // Cuando el raton pasa por encima se pone mas claro el recuadro
        botonAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonAdd.setBackground(COLOR_HOVER); }
            public void mouseExited(MouseEvent e)  { botonAdd.setBackground(COLOR_GRIS_OSCURO); }
        });
        // Manejar del boton para añadir el producto
        botonAdd.addActionListener(e -> {
            String url = urlInput.getText().trim();
            String precio = precioInput.getText().trim();
            String nombre = nombreInput.getText().trim();
            // Validaciones previas en cliente
            if (nombre.isEmpty() || nombre.equals("Escriba el nombre del producto")) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar en blanco.");
                return;
            } else if (url.isEmpty() || url.equals("Pega aquí la URL del producto")) {
                JOptionPane.showMessageDialog(this, "La URL no puede estar vacia.");
                return;
            } else if (precio.isEmpty() || precio.equals("0,00")) {
                JOptionPane.showMessageDialog(this, "Tienes que elegir un umbral de precio.");
                return;
            }

            // Comprobamos si el enlace ya existe en pantalla2 para evitar añadir productos repetidos
            if (pantalla2.contieneEnlace(url)) {
                JOptionPane.showMessageDialog(this, "Este producto ya está en la lista.");
                return;
            }
            if (pantalla2.getNumProductos() >= 10) {
                JOptionPane.showMessageDialog(this, "No puedes añadir más de 10 productos.");
                return;
            }

            // Validación de formato del precio y conversión a número
            double umbral;
            try {
                // Adapta la entrada del usuario cambiando comas por puntos antes del parseo
                umbral = Double.parseDouble(precio.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.");
                return;
            }

            // Comunicación con el Sistema Multiagente
            agente.solicitarScraping(url, nombre, umbral);
            agente.solicitarActualizacion();
            //  Cambia la vista al listado principal de productos para que el usuario vea el producto que acaba de añadir
            bloqueProductoLayout.show(contenedor, "pantalla2");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(COLOR_GRIS_OSCURO);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        btnPanel.add(botonAdd);

        // Agrupa el recuadro de la url, el del precio y el del nombre
        JPanel urlYPrecioBox = new JPanel();
        urlYPrecioBox.setLayout(new BoxLayout(urlYPrecioBox, BoxLayout.Y_AXIS));
        urlYPrecioBox.add(nombrePanel);
        urlYPrecioBox.add(Box.createVerticalStrut(10));
        urlYPrecioBox.add(scrollPane);
        urlYPrecioBox.add(precioPanel);
        urlYPrecioBox.setBackground(COLOR_GRIS_OSCURO);
        urlYPrecioBox.setOpaque(true);
        bloqueProducto.add(urlYPrecioBox, BorderLayout.CENTER);
        bloqueProducto.add(btnPanel, BorderLayout.SOUTH);

        // Igual que con el otro boton solo que para ver la lista de productos
        botonVerLista = new JButton("VER LISTA DE PRODUCTOS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        botonVerLista.setContentAreaFilled(false);
        botonVerLista.setBackground(COLOR_GRIS_OSCURO);
        botonVerLista.setForeground(Color.WHITE);
        botonVerLista.setFocusPainted(false);
        botonVerLista.setBorderPainted(false);
        botonVerLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVerLista.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonVerLista.setPreferredSize(new Dimension(280, 42));

        botonVerLista.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVerLista.setBackground(COLOR_HOVER); }
            public void mouseExited(MouseEvent e)  { botonVerLista.setBackground(COLOR_GRIS_OSCURO); }
        });
        botonVerLista.addActionListener(e -> {
            bloqueProductoLayout.show(contenedor, "pantalla2");
            agente.solicitarActualizacion(); // Asegura sincronización de datos antes de pintar el panel
        });

        JPanel btnListaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnListaPanel.setBackground(FONDO_GRIS);
        btnListaPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
        btnListaPanel.add(botonVerLista);

        centerBlock.add(bloqueProducto);
        centerBlock.add(btnListaPanel);
        add(centerBlock);

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }



    private void estiloCampo(JTextComponent bloque, int tamanioFuente) {
        bloque.setFont(new Font("SansSerif", Font.PLAIN, tamanioFuente));
        bloque.setBackground(new Color(51, 51, 51));
        bloque.setCaretColor(Color.WHITE);
        bloque.setBorder(new BotonRedondeado(new Color(85, 85, 85), 1, 8));
    }

    // Configurar el comportamiento dinámico de los placeholders (textos fantasma).
    private void configurarPlaceholder(JTextComponent bloque, String texto) {
        bloque.setForeground(COLOR_BLOQUE);
        bloque.setText(texto);
        bloque.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (bloque.getText().equals(texto)) {
                    bloque.setText("");
                    bloque.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (bloque.getText().trim().isEmpty()) {
                    bloque.setForeground(COLOR_BLOQUE);
                    bloque.setText(texto);
                }
            }
        });
    }

}
