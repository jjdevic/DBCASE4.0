package vista.iconos.perspective;

import vista.iconos.icon;
import vista.tema.Theme;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class codeIcon extends icon {
    public codeIcon(boolean selected) {
        super("perspective", selected);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D rect = new RoundRectangle2D.Double(
                x + getIconWidth() * offset,
                y + getIconHeight() * offset,
                getIconWidth() - getIconWidth() * offset * 3,
                getIconHeight() - getIconHeight() * offset * 3, 15, 15);
        Theme theme = Theme.getInstancia();
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setColor(theme.toolBar());
        g2d.fill(rect);
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(isSelected() ? theme.attribute() : theme.lines());
        g2d.draw(rect);
        g2d.draw(new Line2D.Double(getIconWidth() * .35, getIconHeight() * offset, getIconWidth() * .35, getIconHeight() * offset * 8));
        g2d.draw(new Line2D.Double(getIconWidth() * .6, getIconHeight() * offset, getIconWidth() * .6, getIconHeight() * offset * 8));
    }
}
