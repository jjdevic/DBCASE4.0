package vista.frames;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;
import vista.utils.ImagesPath;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class GUI_RenombrarAgregacion extends Parent_GUI {
    private TransferRelacion relacion;
    private JLabel explicacion;
    private JTextField cajaNombre;
    private JButton botonRenombrar;
    

    public GUI_RenombrarAgregacion(Controlador controlador) {
    	super(controlador);
    }

    protected void initComponents() {
        setTitle(Lenguaje.text(Lenguaje.RENAME_AGGREGATION));
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);
        this.setSize(300, 170);
        cajaNombre = this.getCajaNombre(25, 40);
        cajaNombre.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    botonRenombrarActionPerformed(null);
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
            botonRenombrar = boton(120, 90, Lenguaje.text(Lenguaje.RENAME));
            getContentPane().add(botonRenombrar);
            botonRenombrar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonRenombrarActionPerformed(evt);
                }
            });
            botonRenombrar.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) botonRenombrarActionPerformed(null);
                    else if (e.getKeyCode() == 27) botonCancelarActionPerformed(null);
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        }
        getContentPane().add(cajaNombre);
        {
            explicacion = new JLabel();
            getContentPane().add(explicacion);
            explicacion.setFont(theme.font());
            explicacion.setText(Lenguaje.text(Lenguaje.WRITE_NAME_AGREGATION_SELECTED));
            explicacion.setOpaque(false);
            explicacion.setBounds(25, 10, 236, 25);
            explicacion.setFocusable(false);
        }
        this.addMouseListener(this);
        this.addKeyListener(this);
    }

    /*
     * Activar y desactivar el dialogo
     */
    public void setActiva() {
        SwingUtilities.invokeLater(doFocus);
        this.centraEnPantalla();
        this.setVisible(true);
    }

    private Runnable doFocus = new Runnable() {
        public void run() {
            cajaNombre.grabFocus();
        }
    };

    public void setInactiva() {
        this.setVisible(false);
    }

    /*
     * Oyentes de los botones
     */
    private void botonRenombrarActionPerformed(java.awt.event.ActionEvent evt) {
        // Datos que mandamos al controlador
        Vector<Object> v = new Vector<Object>();
        v.add(this.getRelacion());
        v.add(this.cajaNombre.getText());
        // Mandamos mensaje + datos al controlador
        this.getControlador().mensajeDesde_GUI(TC.GUIRenombrarAgregacion_Click_BotonRenombrar, v);
    }

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 27: {
                this.setInactiva();
                break;
            }
            case 10: {
                this.botonRenombrarActionPerformed(null);
                break;
            }
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

    public TransferRelacion getRelacion() {
        return relacion;
    }

    public void setRelacion(TransferRelacion relacion) {
        this.relacion = relacion;
    }

	@Override
	public void setDatos(Object datos) {
		this.relacion = (TransferRelacion) datos;
	}

	@Override
	public int setActiva(int op) {
		return 0;
	}
}