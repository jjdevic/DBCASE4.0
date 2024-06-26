package vista.iconos;

import vista.tema.Theme;

import java.awt.*;
import java.awt.geom.Line2D;

public class relationIcon extends icon {

    public relationIcon() {
        super();
    }

    public relationIcon(String tipo) {
        super(tipo);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        Theme theme = Theme.getInstancia();
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setColor(theme.relation());
        Polygon s = new Polygon();
        s.addPoint((int) (getIconWidth() * .1), (int) (getIconHeight() * .6));
        s.addPoint((int) (getIconWidth() * .5), (int) (getIconHeight() * .2));
        s.addPoint((int) (getIconWidth() * .9), (int) (getIconHeight() * .6));
        s.addPoint((int) (getIconWidth() * .5), getIconHeight());
        g.fillPolygon(s);
        g2d.setColor(theme.labelFontColorDark());
        g.drawPolygon(s);
        if (pintarMas()) {
            g2d.setColor(theme.labelFontColorLight());
            g2d.draw(new Line2D.Double(getIconWidth() * .45, getIconHeight() * .6, getIconWidth() * .55, getIconHeight() * .6));
            g2d.draw(new Line2D.Double(getIconWidth() * .5, getIconHeight() * .5, getIconWidth() * .5, getIconHeight() * .7));
        }
    }
}
