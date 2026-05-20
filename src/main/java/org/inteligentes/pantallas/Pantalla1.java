package org.inteligentes.pantallas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Pantalla1 extends JPanel {

    private JTextArea urlInput;
    private JTextField precioInput;
    private JTextField nombreInput;
    private JButton botonAdd;
    private JButton botonVerLista;

    public Pantalla1(CardLayout bloqueProductoLayout, JPanel contenedor, Pantalla2 pantalla2) {
        Color fondoGris = new Color(224, 224, 224);
        Color colorGrisOscuro = new Color(51, 51, 51);
        Color colorTexto = new Color(153, 153 , 153 );
        Color colorBordeFino = new Color(85, 85, 85);

        setBackground(fondoGris);
        setLayout(new GridBagLayout());

        /* JPanel auxiliar para que pueda agrupar el recuadro para añadir un producto y el de ver la lista de productos*/
        JPanel centerBlock = new JPanel();
        centerBlock.setLayout(new BoxLayout(centerBlock, BoxLayout.Y_AXIS));
        centerBlock.setBackground(fondoGris);

        /* El recuadro negro donde se añade el producto */
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
        bloqueProducto.setBackground(colorGrisOscuro);
        bloqueProducto.setBorder(new EmptyBorder(25, 20, 25, 20));

        /* Basicamente donde se pone el texto de la url */
        urlInput = new JTextArea(3, 40); // JTextArea porque el texto puede ser muy largo
        urlInput.setLineWrap(true); // Permite que el texto siga en la linea siguiente
        urlInput.setWrapStyleWord(true); // El salto de linea no corta palabras completas
        urlInput.setFont(new Font("SansSerif", Font.PLAIN, 15));
        urlInput.setBackground(colorGrisOscuro);
        urlInput.setForeground(colorTexto);
        urlInput.setCaretColor(Color.WHITE);
        urlInput.setText("Pega aquí la URL del producto");
        urlInput.setBorder(new BordeRedondeado(colorBordeFino, 1, 8));
        /* Monitoriza si el usuario esta escribiendo aqui, si lo esta reemplaza el texto original por el que escriba
        * el usuario sino se queda como estaba originalmente */
        urlInput.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (urlInput.getText().equals("Pega aquí la URL del producto")) {
                    urlInput.setText("");
                    urlInput.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (urlInput.getText().trim().isEmpty()) {
                    urlInput.setForeground(colorTexto);
                    urlInput.setText("Pega aquí la URL del producto");
                }
            }
        });

        /* Lo mismo que lo anterior pero para el precio */
        JLabel precioBuscado = new JLabel("Precio buscado (€)");
        precioBuscado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        precioBuscado.setForeground(new Color(153 , 153 , 153));
        precioBuscado.setAlignmentX(Component.LEFT_ALIGNMENT);

        precioInput = new JTextField(10);
        precioInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        precioInput.setBackground(colorGrisOscuro);
        precioInput.setForeground(colorTexto);
        precioInput.setCaretColor(Color.WHITE);
        precioInput.setText("0,00");
        precioInput.setBorder(new BordeRedondeado(colorBordeFino, 1, 8));

        precioInput.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (precioInput.getText().equals("0,00")) {
                    precioInput.setText("");
                    precioInput.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (precioInput.getText().trim().isEmpty()) {
                    precioInput.setForeground(colorTexto);
                    precioInput.setText("0,00");
                }
            }
        });

        JLabel nombreProducto = new JLabel("Nombre del Producto");
        nombreProducto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nombreProducto.setForeground(new Color(153 , 153 , 153));
        nombreProducto.setAlignmentX(Component.LEFT_ALIGNMENT);

        nombreInput = new JTextField(10);
        nombreInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nombreInput.setBackground(colorGrisOscuro);
        nombreInput.setForeground(colorTexto);
        nombreInput.setCaretColor(Color.WHITE);
        nombreInput.setText("Escriba el nombre del producto");
        nombreInput.setBorder(new BordeRedondeado(colorBordeFino, 1, 8));

        nombreInput.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (nombreInput.getText().equals("Escriba el nombre del producto")) {
                    nombreInput.setText("");
                    nombreInput.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (nombreInput.getText().trim().isEmpty()) {
                    nombreInput.setForeground(colorTexto);
                    nombreInput.setText("Escriba el nombre del producto");
                }
            }
        });

        /* Hace que aparezca scrollbar si el texto es muy largo */
        JScrollPane scrollPane = new JScrollPane(urlInput);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(colorGrisOscuro);

        /* Al igual que lo que hace centerBlock, este es usado para agrupar el recuadro del precio */
        JPanel precioPanel = new JPanel();
        precioPanel.setLayout(new BoxLayout(precioPanel, BoxLayout.Y_AXIS));
        precioPanel.setBackground(colorGrisOscuro);
        precioPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        precioPanel.add(precioBuscado);
        precioPanel.add(Box.createVerticalStrut(5));
        precioPanel.add(precioInput);

        JPanel nombrePanel = new JPanel();
        nombrePanel.setLayout(new BoxLayout(nombrePanel, BoxLayout.Y_AXIS));
        nombrePanel.setBackground(colorGrisOscuro);
        nombrePanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        nombrePanel.add(nombreProducto);
        nombrePanel.add(Box.createVerticalStrut(5));
        nombrePanel.add(nombreInput);

        /* Boton para añadir el producto */
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
        botonAdd.setBackground(colorGrisOscuro);
        botonAdd.setForeground(Color.WHITE);
        botonAdd.setFocusPainted(false);
        botonAdd.setBorder(new BordeRedondeado(colorBordeFino, 1, 8));
        botonAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
        /* Cuando el raton pasa por encima se pone mas claro el recuadro */
        botonAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonAdd.setBackground(new Color(68, 68, 68)); }
            public void mouseExited(MouseEvent e)  { botonAdd.setBackground(colorGrisOscuro); }
        });
        /* */
        botonAdd.addActionListener(e -> {
            String url = urlInput.getText().trim();
            String precio = precioInput.getText();
            String nombre = nombreInput.getText();
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

            double umbral;
            try {
                umbral = Double.parseDouble(precio.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.");
                return;
            }

            pantalla2.agregarProducto(new Pantalla2.Producto(nombre, url, umbral, false));
            bloqueProductoLayout.show(contenedor, "pantalla2");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(colorGrisOscuro);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        btnPanel.add(botonAdd);

        /* Agrupa el recuadro de la url con el del precio */
        JPanel urlYPrecioBox = new JPanel();
        urlYPrecioBox.setLayout(new BoxLayout(urlYPrecioBox, BoxLayout.Y_AXIS));
        urlYPrecioBox.add(nombrePanel);
        urlYPrecioBox.add(Box.createVerticalStrut(10));
        urlYPrecioBox.add(scrollPane);
        urlYPrecioBox.add(precioPanel);
        urlYPrecioBox.setBackground(colorGrisOscuro);
        urlYPrecioBox.setOpaque(true);
        bloqueProducto.add(urlYPrecioBox, BorderLayout.CENTER);
        bloqueProducto.add(btnPanel, BorderLayout.SOUTH);

        /* Igual que con el otro boton solo que para ver la lista de productos*/
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
        botonVerLista.setBackground(colorGrisOscuro);
        botonVerLista.setForeground(Color.WHITE);
        botonVerLista.setFocusPainted(false);
        botonVerLista.setBorderPainted(false);
        botonVerLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVerLista.setFont(new Font("SansSerif", Font.BOLD, 13));
        botonVerLista.setPreferredSize(new Dimension(280, 42));

        botonVerLista.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonVerLista.setBackground(new Color(68,68,68)); }
            public void mouseExited(MouseEvent e)  { botonVerLista.setBackground(colorGrisOscuro); }
        });
        botonVerLista.addActionListener(e -> bloqueProductoLayout.show(contenedor, "pantalla2"));

        JPanel btnListaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnListaPanel.setBackground(fondoGris);
        btnListaPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
        btnListaPanel.add(botonVerLista);

        centerBlock.add(bloqueProducto);
        centerBlock.add(btnListaPanel);
        add(centerBlock);

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Extractor Amazon");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 720);
            frame.setLocationRelativeTo(null);

            CardLayout bloqueProductoLayout = new CardLayout();
            JPanel contenedor = new JPanel(bloqueProductoLayout);

            // Se crean en orden inverso de dependencia: 3 → 2 → 1
            Pantalla3 pantalla3 = new Pantalla3(bloqueProductoLayout, contenedor);
            Pantalla2 pantalla2 = new Pantalla2(bloqueProductoLayout, contenedor, pantalla3);
            Pantalla1 pantalla1 = new Pantalla1(bloqueProductoLayout, contenedor, pantalla2);

            contenedor.add(pantalla1, "pantalla1");
            contenedor.add(pantalla2, "pantalla2");
            contenedor.add(pantalla3, "pantalla3");

            frame.add(contenedor);
            frame.setVisible(true);
        });
    }

    static class BordeRedondeado implements Border {
        private final Color color;
        private final int grosor;
        private final int radio;

        public BordeRedondeado(Color color, int grosor, int radio) {
            this.color = color;
            this.grosor = grosor;
            this.radio = radio;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawRoundRect(x + grosor / 2, y + grosor / 2, width - grosor, height - grosor, radio, radio);
            g2.dispose();
        }
    }
}