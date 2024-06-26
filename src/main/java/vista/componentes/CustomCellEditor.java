package vista.componentes;

import vista.tema.Theme;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class CustomCellEditor extends DefaultCellEditor {

    private static Theme theme = Theme.getInstancia();

    public static CustomCellEditor make() {
        JTextField field = new JTextField();
        field.setBackground(theme.background());
        field.setFont(theme.font());
        field.setBorder(null);

        return new CustomCellEditor(field);
    }

    public CustomCellEditor(JTextField textField) {
        super(textField);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return super.getTableCellEditorComponent(table, null, isSelected, row, column);
    }

}