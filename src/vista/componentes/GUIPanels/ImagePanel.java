package vista.componentes.GUIPanels;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class ImagePanel extends JPanel{
	
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
        } 
        else {
            setOpaque(true);
        }
        super.paint(g);
    }
	
	public void setFondo(Image imagenInicial, Graphics g) {
        if (imagenInicial != null) {
            g.drawImage(imagenInicial, 0, 0, getWidth(), getHeight(), this);
            setOpaque(false);
        } 
        else {
            setOpaque(true);
        }
        super.paint(g);
    }
	
}
