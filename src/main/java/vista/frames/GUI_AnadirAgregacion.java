package vista.frames;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.Transfer;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;
import vista.utils.ImagesPath;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class GUI_AnadirAgregacion extends Parent_GUI {

    private TransferRelacion relacion;
    private JDialog pane;
    private JTextField cajaNombre;
    private JButton botonInsertar;
    private JLabel explicacion;

    public GUI_AnadirAgregacion(Controlador controlador) {
        super(controlador);
    }

    protected void initComponents() {

        setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);
        this.setSize(300, 170);
        pane = new JDialog();
        pane.setTitle(Lenguaje.text(Lenguaje.ADD_AGREG));
        pane.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        pane.setResizable(false);
        pane.setModal(true);
        pane.setTitle(Lenguaje.text(Lenguaje.INT_NOM_AGREG));
        pane.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        pane.setSize(700, 200);
        pane.setAlwaysOnTop(false);
        pane.setLocationRelativeTo(null);
        pane.setLayout(null);

        cajaNombre = new JTextField();
        cajaNombre.setFont(theme.font());
        cajaNombre.setBounds(100, 10, 232, 30);
        cajaNombre.setForeground(theme.labelFontColorDark());
        cajaNombre.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    botonInsertarActionPerformed(relacion, cajaNombre.getText());
                    pane.dispose();
                } else if (e.getKeyCode() == 27) {
                    pane.dispose();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        pane.add(cajaNombre);

        botonInsertar = new JButton();
        botonInsertar.setFont(theme.font());
        botonInsertar.setText(Lenguaje.text(Lenguaje.INSERT));
        botonInsertar.setBounds(200, 100, Lenguaje.text(Lenguaje.INSERT).length() > 8 ? 110 + Lenguaje.text(Lenguaje.INSERT).length() * 4 : Lenguaje.text(Lenguaje.INSERT).length() < 4 ? 60 : 110, 30);
        pane.add(botonInsertar);
        botonInsertar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                botonInsertarActionPerformed(relacion, cajaNombre.getText());
                pane.dispose();
            }
        });
        botonInsertar.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    botonInsertarActionPerformed(relacion, cajaNombre.getText());
                    pane.dispose();
                } else if (e.getKeyCode() == 27) {
                    pane.dispose();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        botonInsertar.setMnemonic(Lenguaje.text(Lenguaje.INSERT).charAt(0));

        explicacion = new JLabel();
        pane.add(explicacion);
        explicacion.setText(Lenguaje.text(Lenguaje.NAME));
        explicacion.setOpaque(false);
        explicacion.setBounds(25, 10, 67, 25);
        explicacion.setFont(theme.font());
        explicacion.setFocusable(false);
        explicacion.setAlignmentX(0.0f);

    }

    private void botonInsertarActionPerformed(Transfer t, String nombre) {
        Vector<Object> v = new Vector<Object>();
        v.add(t);
        v.add(nombre);
        controlador.mensajeDesde_GUI(TC.GUIInsertarAgregacion, v);
    }

    public void setActiva() {
        SwingUtilities.invokeLater(doFocus);
        this.centraEnPantalla();
        pane.setVisible(true);
    }

    private Runnable doFocus = new Runnable() {
        public void run() {
            cajaNombre.grabFocus();
        }
    };

    public void setInactiva() {
        pane.setVisible(false);
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
