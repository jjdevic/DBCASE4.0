package vista.componentes.GUIPanels;

import vista.componentes.CustomCellEditor;
import vista.componentes.MyTableModel;

import javax.swing.*;

@SuppressWarnings("serial")
public class TablaVolumenes extends JTable {

    public TablaVolumenes() {
        super(new MyTableModel());
        setRowHeight(30);
        getTableHeader().setReorderingAllowed(false);//columnas fijadas
        setDefaultEditor(Object.class, CustomCellEditor.make());
        putClientProperty("terminateEditOnFocusLost", false);
    }

    public void refresh(String[][] s) {
        ((MyTableModel) this.getModel()).refresh(s);
        ((MyTableModel) this.getModel()).fireTableDataChanged();
    }
}
