package vista.frames;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferEntidad;
import vista.Lenguaje;
import vista.componentes.CustomCellEditor;
import vista.utils.ImagesPath;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

@SuppressWarnings({"serial", "unchecked"})
public class GUI_TablaUniqueEntidad extends Parent_GUI {

    
    private TransferEntidad entidad;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JPanel panelBotones;
    private JLabel jLabel1;
    private JButton botonNueva;
    private JButton botonEliminar;
    private JButton botonAceptar;
    private JTable tablaConjuntos;
    private Vector<JButton> botones;

    public GUI_TablaUniqueEntidad(Controlador controlador) {
        super(controlador);
    }

    protected void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);
        this.setSize(400, 350);
        {
            jScrollPane1 = new JScrollPane();
            getContentPane().add(jScrollPane1);
            jScrollPane1.setBounds(0, 0, 340, 200);
        }
        {
            jScrollPane2 = new JScrollPane();
            this.panelBotones = new JPanel();
            getContentPane().add(jScrollPane2);
            jScrollPane2.setBounds(0, 200, 340, 70);
        }
        {
            botonAceptar = boton(280, 280, Lenguaje.text(Lenguaje.ACCEPT));
            getContentPane().add(botonAceptar);
            botonAceptar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonAceptarActionPerformed(evt);
                }
            });
            botonAceptar.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) botonCancelarActionPerformed(null);
                    else if (e.getKeyCode() == 27) botonCancelarActionPerformed(null);
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        }
        {
            botonNueva = new JButton();
            botonNueva.setFont(theme.font());
            getContentPane().add(botonNueva);
            botonNueva.setText("+");
            botonNueva.setBounds(350, 10, 45, 45);
            botonNueva.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 27) {
                        botonCancelarActionPerformed(null);
                    }
                }
            });
            botonNueva.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonNuevaActionPerformed(evt);
                }
            });
        }
        {
            botonEliminar = new JButton();
            getContentPane().add(botonEliminar);
            botonEliminar.setText("-");
            botonEliminar.setFont(theme.font());
            botonEliminar.setBounds(350, 55, 45, 45);
            botonEliminar.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 27) botonCancelarActionPerformed(null);
                    else if (e.getKeyCode() == 37) botonNueva.grabFocus();
                }
            });
            botonEliminar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonEliminarActionPerformed(evt);
                }
            });
        }
        {
            jLabel1 = new JLabel();
            getContentPane().add(jLabel1);
            jLabel1.setText(Lenguaje.text(Lenguaje.ATTRIBUTES) + ":");
            jLabel1.setBounds(20, 174, 88, 17);
        }
        this.addKeyListener(this);
        TableModel tablaModel = new DefaultTableModel(new String[][]{{""}}, new String[]{Lenguaje.text(Lenguaje.TABLE_UNIQUE) + ":"});
        tablaConjuntos = new JTable(tablaModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jScrollPane1.setViewportView(tablaConjuntos);
        tablaConjuntos.setFont(theme.font());
        tablaConjuntos.setBackground(theme.background());
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(theme.font());
                return this;
            }
        };
        setForeground(theme.background());
        tablaConjuntos.setDefaultEditor(Object.class, CustomCellEditor.make());
        tablaConjuntos.getColumnModel().getColumn(0).setCellRenderer(r);
        tablaConjuntos.setRowHeight(25);
        tablaConjuntos.getTableHeader().setReorderingAllowed(false);//columnas fijadas
        this.addKeyListener(this);
    }

    /*
     * Oyentes de los botones
     */
    private void botonAceptarActionPerformed(java.awt.event.ActionEvent evt) {
        Vector<String> predicados = new Vector<String>();
        int i = 0;
        boolean correcto = true;
        while (i < tablaConjuntos.getRowCount() && correcto) {
            String s = tablaConjuntos.getValueAt(i, 0).toString();
            if (!s.startsWith("UNIQUE(") || !s.endsWith(")")) correcto = false;
            i++;
        }
        if (correcto) {
            i = 0;
            while (i < tablaConjuntos.getRowCount() && correcto) {
                String s = tablaConjuntos.getValueAt(i, 0).toString();
                //comprbamos que los atributos elegidos pertenezcan a la entidad
                try {
                    s = s.substring(7, s.length() - 1);
                    correcto = comprobarAtributos(s);
                } catch (Exception e) {
                    correcto = false;
                }
                s = s.replaceAll("  ", "");
                s = s.replaceAll(",", ", ");
                predicados.add(s);
                i++;
            }
            if (!correcto) {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.UNIQUE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
            } else {
                Vector<Object> v = new Vector<Object>();
                v.add(predicados);
                v.add(this.entidad);
                controlador.mensajeDesde_GUI(TC.GUIPonerUniquesAEntidad_Click_BotonAceptar, v);
                this.setInactiva();
            }
        }
    }

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.setInactiva();
    }

    private void botonNuevaActionPerformed(ActionEvent evt) {
        String[][] items = new String[tablaConjuntos.getRowCount() + 1][1];
        int i = 0;
        while (i < tablaConjuntos.getRowCount()) {
            items[i][0] = tablaConjuntos.getValueAt(i, 0).toString();
            i++;
        }
        items[tablaConjuntos.getRowCount()][0] = "UNIQUE(...)";
        TableModel tablaModel =
                new DefaultTableModel(items,
                        new String[]{Lenguaje.text(Lenguaje.TABLE_UNIQUE) + ":"});
        tablaConjuntos.setModel(tablaModel);
    }

    private void botonEliminarActionPerformed(ActionEvent evt) {
        try {
            int selec = tablaConjuntos.getSelectedRow();
            String[][] items = new String[tablaConjuntos.getRowCount() - 1][1];
            int i = 0;
            int j = 0;
            while (i < tablaConjuntos.getRowCount()) {
                if (selec != i) {
                    items[j][0] = tablaConjuntos.getValueAt(i, 0).toString();
                    j++;
                }
                i++;
            }
            TableModel tablaModel = new DefaultTableModel(items,
                    new String[]{Lenguaje.text(Lenguaje.TABLE_UNIQUE) + ":"});
            tablaConjuntos.setModel(tablaModel);

        } catch (Exception e) {
            System.out.println("no hay ninguna seleccionada");
        }

    }

    /*
     * Activar y desactivar el dialogo
     */
    public void setActiva() {
        this.centraEnPantalla();
        this.setTitle(Lenguaje.text(Lenguaje.ADD_UNIQUE) + " of " + entidad.getNombre());
        muestraEntradas();
        muestraAtributos();
        SwingUtilities.invokeLater(doFocus);
        this.setVisible(true);
    }

    private Runnable doFocus = new Runnable() {
        public void run() {
            for (int i = 0; i < botones.size(); i++)
                botones.get(i).grabFocus();
            botonNueva.grabFocus();
        }
    };

    public void setInactiva() {
        this.setVisible(false);
        this.dispose();
    }

    private boolean comprobarAtributos(String s) {
        int pos0 = 0;
        int pos1 = s.indexOf(",");
        while (pos0 != -1) {
            String subS;
            if (pos1 != -1) subS = s.substring(pos0, pos1);
            else subS = s.substring(pos0, s.length());
            subS = subS.replaceAll(" ", "");
            subS = subS.replaceAll(",", "");
            pos0 = pos1;
            pos1 = s.indexOf(",", pos1 + 1);
            //comprobar que subS sea un atributo
            boolean esAtributo = false;
            for (int i = 0; i < botones.size(); i++)
                if (subS.equals(botones.get(i).getText())) esAtributo = true;

            if (!esAtributo) return false;
        }
        return true;
    }

    private void muestraEntradas() {
        int i = 0;
        Vector<String> unitarios = new Vector<String>();

        String[][] items = new String[entidad.getListaUniques().size() + unitarios.size()][1];
        i = 0;
        while (i < +unitarios.size()) {
            items[i][0] = "UNIQUE(" + unitarios.get(i).toString() + ")";
            i++;
        }

        i = 0;
        while (i < entidad.getListaUniques().size()) {
            items[i + unitarios.size()][0] = "UNIQUE(" + entidad.getListaUniques().get(i).toString() + ")";
            i++;
        }
        TableModel tablaModel = new DefaultTableModel(items,
                new String[]{Lenguaje.text(Lenguaje.TABLE_UNIQUE) + ":"});
        tablaConjuntos.setModel(tablaModel);
    }

    private void muestraAtributos() {
        botones = new Vector<JButton>();
        int i = 0;
        while (i < entidad.getListaAtributos().size()) {
            final JButton boton1 = new JButton();
            boton1.setFont(theme.font());
            boton1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    ponAtributo(boton1.getText());
                }
            });
            boton1.setText((String) controlador.mensaje(TC.GetNombreAtributo, Integer.parseInt(entidad.getListaAtributos().get(i).toString())));
            boton1.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        botonCancelarActionPerformed(null);
                    } else if (e.getKeyCode() == 27) {
                        botonCancelarActionPerformed(null);
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
            this.botones.add(boton1);
            boton1.setBounds(0, 25 * i, 80, 25);
            this.panelBotones.add(boton1);
            boton1.setBounds(0, 25 * i, 80, 25);
            i++;
        }
        jScrollPane2.getViewport().setView(panelBotones);
    }

    private void ponAtributo(String text) {
        try {
            int row = tablaConjuntos.getSelectedRow();
            int col = tablaConjuntos.getSelectedColumn();
            String s = tablaConjuntos.getModel().getValueAt(row, col).toString();
            s = s.substring(0, s.length() - 1);
            if (s.contains(text)) {
                int pos = s.indexOf(text);
                String s1 = s.substring(0, pos);
                String s2;
                if (s.indexOf(",", pos) > 0) {
                    s2 = s.substring(s.indexOf(",", pos) + 1, s.length());
                    s = s1 + s2 + ")";
                } else {
                    s2 = "";
                    if (s1.lastIndexOf(",") > 0)
                        s1 = s1.substring(0, s.lastIndexOf(","));
                    s = s1 + s2 + ")";
                }
                s = s.replaceAll(" ", "");
                s = s.replaceAll(",", ", ");
            } else {
                if (s.endsWith("...")) s = s.substring(0, s.length() - 3);
                else {
                    if (!s.endsWith("(")) s = s + ", ";
                }
                s = s + text + ")";
                s = s.replaceAll(" ", "");
                s = s.replaceAll(",", ", ");
            }
            tablaConjuntos.getModel().setValueAt(s, row, col);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ERROR_TABLE),
                    Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Getters y Setters
     */
    public Controlador getControlador() {
        return controlador;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void setEntidad(TransferEntidad entidad) {
        this.entidad = entidad;
    }

	@Override
	public void setDatos(Object datos) {
		this.entidad = (TransferEntidad) datos;
	}

	@Override
	public int setActiva(int op) {
		return 0;
	}
}
