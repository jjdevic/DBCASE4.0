package vista.frames;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;
import vista.componentes.MyComboBoxRenderer;
import vista.utils.ImagesPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Vector;


@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class GUI_ModificarEntidad extends Parent_GUI {
    
    private Point2D posicionEntidad;
    private JTextField cajaNombre;
    private JCheckBox CasillaEsDebil;
    private JLabel explicacion;
    private JButton botonModificar;
    private JTextField jTextRelacion;
    private JLabel nombreRelacion;
    private JComboBox comboEntidadesFuertes;
    private JLabel selecFuerte;
    private Vector<TransferEntidad> listaEntidades;
    private Vector<String> items;
    private TransferRelacion relacion;
    private TransferEntidad entidad;
    private boolean factibleEntidad;//Sirve para la comprobación de si se puede añadir una entidad debil

    public GUI_ModificarEntidad(Controlador controlador) {
    	super(controlador);
    }

    protected void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Lenguaje.text(Lenguaje.MODIFY_ENTITY));
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);
        cajaNombre = this.getCajaNombre(100, 10);
        cajaNombre.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    botonModificarActionPerformed(null);
                } else if (e.getKeyCode() == 27) {
                    botonCancelarActionPerformed(null);
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        {
            botonModificar = botonInsertar(200, 400);
            getContentPane().add(botonModificar);
            botonModificar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonModificarActionPerformed(evt);
                }
            });
            botonModificar.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        botonModificarActionPerformed(null);
                    } else if (e.getKeyCode() == 27) {
                        botonCancelarActionPerformed(null);
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
            botonModificar.setMnemonic(Lenguaje.text(Lenguaje.MODIFY).charAt(0));
        }
        {
            explicacion = new JLabel();
            getContentPane().add(explicacion);
            explicacion.setText(Lenguaje.text(Lenguaje.NAME));
            explicacion.setOpaque(false);
            explicacion.setBounds(25, 10, 67, 25);
            explicacion.setFont(theme.font());
            explicacion.setFocusable(false);
            explicacion.setAlignmentX(0.0f);
        }
        {
            getContentPane().add(cajaNombre);
        }
        {
            CasillaEsDebil = new JCheckBox();
            getContentPane().add(CasillaEsDebil);
            CasillaEsDebil.setText(Lenguaje.text(Lenguaje.WEAK_ENTITY));
            CasillaEsDebil.setFont(theme.font());
            CasillaEsDebil.setBounds(100, 40, 170, 25);
            CasillaEsDebil.setOpaque(false);
            CasillaEsDebil.setBorderPaintedFlat(true);
            CasillaEsDebil.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10)
                        CasillaEsDebil.setSelected(!CasillaEsDebil.isSelected());
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
            /*Si la entidad es debil, entonces hay que activar la parte de la ventana que permite seleccionar la entidad
             * fuerte, y dar un nombre a la relación que unira la entidad fuerte y la débil.*/
            CasillaEsDebil.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (CasillaEsDebil.isSelected()) {
                    	listaEntidades = (Vector<TransferEntidad>) controlador.mensaje(TC.ObtenerListaEntidades, null);

                        //Generamos los items del comboEntidades
                        items = generaItems();
                        items.remove(entidad.getNombre());
                        //Los ordenamos alfabeticamente
                        if (items.size() == 0)
                            JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.CREATE_STRONG_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
                        else {
                            ampliarVentana();
                            jTextRelacion.setFont(theme.font());
                            selecFuerte.setVisible(true);
                            selecFuerte.setEnabled(true);
                            comboEntidadesFuertes.setEnabled(true);
                            comboEntidadesFuertes.setVisible(true);
                            jTextRelacion.setEnabled(true);
                            jTextRelacion.setVisible(true);
                            nombreRelacion.setEnabled(true);
                            nombreRelacion.setVisible(true);
                            if (items.size() != 0)
                                comboEntidadesFuertes.setModel(new javax.swing.DefaultComboBoxModel(items));
                        }
                    } else {
                        reducirVentana();
                        selecFuerte.setEnabled(false);
                        selecFuerte.setVisible(false);
                        comboEntidadesFuertes.setEnabled(false);
                        comboEntidadesFuertes.setVisible(false);
                        jTextRelacion.setEnabled(false);
                        jTextRelacion.setVisible(false);
                        nombreRelacion.setEnabled(false);
                        nombreRelacion.setVisible(false);
                    }
                }
            });
        }
        {
            selecFuerte = new JLabel();
            getContentPane().add(selecFuerte);
            selecFuerte.setText(Lenguaje.text(Lenguaje.SELECT_STRONG_ENTITY));
            selecFuerte.setFont(theme.font());
            selecFuerte.setBounds(100, 78, 200, 25);
            selecFuerte.setOpaque(false);
            selecFuerte.setEnabled(false);
            selecFuerte.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            selecFuerte.setVisible(false);
        }
        {
            if (comboEntidadesFuertes == null)
                comboEntidadesFuertes = new JComboBox();
            comboEntidadesFuertes.setRenderer(new MyComboBoxRenderer());
            getContentPane().add(comboEntidadesFuertes);
            comboEntidadesFuertes.setEnabled(false);
            comboEntidadesFuertes.setBounds(100, 105, 200, 25);
            comboEntidadesFuertes.setFont(theme.font());
            comboEntidadesFuertes.setVisible(false);
        }
        {
            nombreRelacion = new JLabel();
            getContentPane().add(nombreRelacion);
            nombreRelacion.setText(Lenguaje.text(Lenguaje.WRITE_RELATION_WEAK));
            nombreRelacion.setBounds(100, 130, 200, 25);
            nombreRelacion.setFont(theme.font());
            nombreRelacion.setEnabled(false);
            nombreRelacion.setOpaque(false);
            nombreRelacion.setVisible(false);
        }
        {
            jTextRelacion = new JTextField();
            getContentPane().add(jTextRelacion);
            jTextRelacion.setEnabled(false);
            jTextRelacion.setBounds(100, 163, 200, 25);
            jTextRelacion.setBorder(BorderFactory.createLineBorder(theme.lines(), 1));
            jTextRelacion.setVisible(false);
            jTextRelacion.setForeground(theme.labelFontColorDark());
            botonModificar.setText(Lenguaje.text(Lenguaje.MODIFY));
        }
        botonModificar.setSize(10, 400);

        this.addMouseListener(this);
        this.addKeyListener(this);

    }

    /*Redimensiona la ventana para que se puedan mostrar y solicitar los datos necesarios
     * para hacer las entidades débiles*/
    public void ampliarVentana() {
        this.setSize(350, 280);
        botonModificar.setBounds(200, 200, 120, 30);
    }

    /*Redimensiona la ventana para que se oculten la parte de las entidades débiles*/
    public void reducirVentana() {
        this.setSize(350, 280);
        botonModificar.setBounds(200, 100, 120, 30);
    }

    /*
     * Oyentes de los botones
     */
    private void botonModificarActionPerformed(java.awt.event.ActionEvent evt) {
        TransferEntidad te = new TransferEntidad();

        //Si la entidad débil y la relación se llaman de diferente manera
        if (!this.cajaNombre.getText().equals(this.jTextRelacion.getText())) {
            //Generamos el transfer que mandaremos al controlador para generar la entidad débil
        	
            Vector<Object> v = new Vector<Object>();
            v.add(this.getEntidad());
            v.add(this.cajaNombre.getText());
            v.add(this.CasillaEsDebil.isSelected());
            if (this.CasillaEsDebil.isSelected()) {
                TransferRelacion tr = new TransferRelacion();
                Point2D p = new Point();
                p.setLocation(150, 150);
                tr.setPosicion(p);
                tr.setNombre(this.jTextRelacion.getText());
                tr.setListaAtributos(new Vector());
                tr.setListaEntidadesYAridades(new Vector());
                tr.setListaRestricciones(new Vector());
                tr.setListaUniques(new Vector());
                tr.setTipo("Debil");
                v.add(tr);
                v.add(this.getListaEntidades().get(indiceAsociado(this.comboEntidadesFuertes.getSelectedIndex())));
            }
            // Mandamos mensaje + datos al controlador
            this.getControlador().mensajeDesde_GUI(TC.GUIModificarEntidad_Click_ModificarEntidad, v);
        } else {
            te.setNombre(this.cajaNombre.getText());
            this.getControlador().mensajeDesde_GUI(TC.GUIInsertarEntidadDebil_Entidad_Relacion_Repetidos, te);
            this.factibleEntidad = false;
            return;
        }
        this.setVisible(false);
    }

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.setInactiva();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 27: {
                this.setInactiva();
                break;
            }
            case 10: {
                this.botonModificarActionPerformed(null);
                break;
            }
        }
    }

    /*
     * Activar y desactivar el dialogo
     */
    public void setActiva() {
        reducirVentana();
        this.cajaNombre.setText(this.getEntidad().getNombre());
        this.jTextRelacion.setEnabled(false);
        this.selecFuerte.setEnabled(false);
        this.comboEntidadesFuertes.setEnabled(false);
        this.jTextRelacion.setEnabled(false);
        this.jTextRelacion.setText("");
        this.nombreRelacion.setEnabled(false);
        this.CasillaEsDebil.setSelected(this.getEntidad().isDebil());
        this.centraEnPantalla();
        this.cajaNombre.grabFocus();
        SwingUtilities.invokeLater(doFocus);
        this.setVisible(true);
        if (this.CasillaEsDebil.isSelected()) {
            ampliarVentana();
            jTextRelacion.setFont(theme.font());
            selecFuerte.setVisible(true);
            selecFuerte.setEnabled(true);
            comboEntidadesFuertes.setEnabled(true);
            comboEntidadesFuertes.setVisible(true);
            jTextRelacion.setEnabled(true);
            jTextRelacion.setVisible(true);
            nombreRelacion.setEnabled(true);
            nombreRelacion.setVisible(true);
            if (items.size() != 0)
                comboEntidadesFuertes.setModel(new javax.swing.DefaultComboBoxModel(items));

        } else {
            reducirVentana();
            this.factibleEntidad = false;
            selecFuerte.setEnabled(false);
            selecFuerte.setVisible(false);
            comboEntidadesFuertes.setEnabled(false);
            comboEntidadesFuertes.setVisible(false);
            jTextRelacion.setEnabled(false);
            jTextRelacion.setVisible(false);
            nombreRelacion.setEnabled(false);
            nombreRelacion.setVisible(false);
        }
    }

    private Runnable doFocus = new Runnable() {
        public void run() {
            cajaNombre.grabFocus();
        }
    };

    public void setInactiva() {
        this.setVisible(false);
    }

    //	@SuppressWarnings("unchecked")
    private Vector<String> generaItems() {
        // Generamos los items
        int cont = 0;
        int indice = 0;
        Vector<String> items = new Vector<String>(this.listaEntidades.size());
        while (cont < this.listaEntidades.size()) {
            TransferEntidad te = this.listaEntidades.get(cont);
            if (!te.isDebil()) {
                items.add(indice, te.getNombre());
                indice++;
            }
            cont++;
        }
        return items;
    }

    /*Dada la posición seleccionada en el comboBox devuelve el índice correspondiente a dicho
     * elementeo en la lista de Entidades. Es necesario porque al ordenar alfabeticamente se perdió
     * la correspondencia.*/
    private int indiceAsociado(int selec) {
        boolean encontrado = false;
        int i = 0;
        while ((i < this.getListaEntidades().size()) && (!encontrado)) {
            if ((this.items.get(selec)).equals(this.getListaEntidades().get(i).getNombre())) {
                encontrado = true;
                return i;
            } else i++;
        }
        return i;
    }

    /*
     * Getters y Setters
     */
    public Point2D getPosicionEntidad() {
        return posicionEntidad;
    }

    public void setPosicionEntidad(Point2D posicionEntidad) {
        this.posicionEntidad = posicionEntidad;
    }

    public Controlador getControlador() {
        return controlador;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public Vector<TransferEntidad> getListaEntidades() {
        return listaEntidades;
    }

    public void setListaEntidades(Vector<TransferEntidad> listaEntidades) {
        this.listaEntidades = listaEntidades;
    }

    public TransferRelacion getRelacion() {
        return relacion;
    }

    public void setRelacion(TransferRelacion relacion) {
        this.relacion = relacion;
    }

    public TransferEntidad getEntidad() {
        return entidad;
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
