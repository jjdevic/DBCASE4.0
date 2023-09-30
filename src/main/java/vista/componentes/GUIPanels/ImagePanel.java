package vista.componentes.GUIPanels;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private Image imagen;

    public ImagePanel() {
    }

    public ImagePanel(Image imagenInicial) {
        if (imagenInicial != null) {
            imagen = imagenInicial;
        }
        this.paint(getGraphics());
    }

    @Override
    public void paint(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
        } else {
            setOpaque(true);
        }
        super.paint(g);
    }

    public void setFondo(Image imagenInicial, Graphics g) {
        if (imagenInicial != null) {
            g.drawImage(imagenInicial, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
        } else {
            setOpaque(true);
        }
        super.paint(g);
    }

}
