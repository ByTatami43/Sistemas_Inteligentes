package org.inteligentes.pantallas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * Clase utilitaria para personalizar los bordes de los componentes Swing.
 * Implementa la interfaz Border nativa de Java para dibujar contornos 
 * con esquinas redondeadas utilizando la API gráfica de Java 2D.
 */
public class BotonRedondeado implements Border {
    private final Color color; // Color del trazo del borde
    private final int grosor; // Grosor de la línea en píxeles
    private final int radio; // Radio de curvatura de las esquinas

    public BotonRedondeado(Color color, int grosor, int radio) {
        this.color = color;
        this.grosor = grosor;
        this.radio = radio;
    }

    /**
     * Define los márgenes internos del componente.
     * Esto evita que el texto interno o el contenido colisione visualmente 
     * con las líneas curvas del borde dibujado.
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(10, 10, 10, 10);
    }

    //Indica si el borde es completamente opaco o si tiene partes transparentes.
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    //Método principal de renderizado. Sobrescribe el dibujado por defecto de Swing.
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