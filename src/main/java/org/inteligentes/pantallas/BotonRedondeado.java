package org.inteligentes.pantallas;

import javax.swing.border.Border;
import java.awt.*;

public class BotonRedondeado implements Border {
    private final Color color;
    private final int grosor;
    private final int radio;

    public BotonRedondeado(Color color, int grosor, int radio) {
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