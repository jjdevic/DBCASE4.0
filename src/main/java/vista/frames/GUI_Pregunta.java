
package vista.frames;

import vista.Lenguaje;

import javax.swing.*;

import controlador.Controlador;
import controlador.TC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

@SuppressWarnings("serial")
public class GUI_Pregunta extends Parent_GUI {

    private int respuesta;
    private JTextPane pregunta;
    private JButton botonNo;
    private JButton botonSi;
    private JButton botonCancelar;
    private TC mensCasoAceptar;
    private Object datosCasoAceptar;
    private TC mensCasoCancelar;

    public GUI_Pregunta(Controlador controlador) {
        super(controlador);
    }

    protected void initComponents() {
        getContentPane().setLayout(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        this.setSize(320, 180);
        {
            pregunta = new JTextPane();
            getContentPane().add(pregunta);
            pregunta.setForeground(theme.labelFontColorDark());
            pregunta.setFont(theme.font());
            pregunta.setBounds(10, 10, 280, 50);
            pregunta.setEditable(false);
            pregunta.setOpaque(false);
            pregunta.setFocusable(false);
        }
        {
            botonSi = boton(230, 90, Lenguaje.text(Lenguaje.YES));
            getContentPane().add(botonSi);
            botonSi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonSiActionPerformed(evt);
                }

            });
            botonSi.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        botonCancelarActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_Y || e.getKeyCode() == KeyEvent.VK_S) {
                        botonSiActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_N) {
                        botonNoActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        botonNo.grabFocus();
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        }
        {
            botonNo = boton(170, 90, Lenguaje.text(Lenguaje.NO));
            getContentPane().add(botonNo);
            botonNo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonNoActionPerformed(evt);
                }
            });
            botonNo.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        botonCancelarActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_Y || e.getKeyCode() == KeyEvent.VK_S) {
                        botonSiActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_N) {
                        botonNoActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        botonSi.grabFocus();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        botonCancelar.grabFocus();
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        }
        {
            botonCancelar = boton(10, 90, Lenguaje.text(Lenguaje.CANCEL));
            getContentPane().add(botonCancelar);
            //botonCancelar.setVisible(false);
            botonCancelar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    botonCancelarActionPerformed(evt);
                }
            });
            botonCancelar.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        botonCancelarActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_Y || e.getKeyCode() == KeyEvent.VK_S) {
                        botonSiActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_N) {
                        botonNoActionPerformed(e);
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        botonNo.grabFocus();
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }
            });
        }
        this.addKeyListener(this);
    }

    public int setActiva(String mensaje, String titulo) {
        return setActiva(mensaje, titulo, false);
    }

    public int setActiva(String mensaje, String titulo, boolean cancelar) {
        pregunta.setText(mensaje);
        setTitle(titulo);
        botonCancelar.setVisible(cancelar);

        respuesta = -1;
        this.centraEnPantalla();
        this.setVisible(true);
        return respuesta;
    }

    public void setInactiva() {
        this.setVisible(false);
    }

    private void botonSiActionPerformed(ActionEvent evt) {
        controlador.mensajeDesde_GUI(mensCasoAceptar, datosCasoAceptar);
        setInactiva();
    }

    private void botonSiActionPerformed(KeyEvent evt) {
    	controlador.mensajeDesde_GUI(mensCasoAceptar, datosCasoAceptar);
        setInactiva();
    }

    private void botonNoActionPerformed(ActionEvent evt) {
        respuesta = 1;
        setInactiva();
    }

    private void botonNoActionPerformed(KeyEvent evt) {
        respuesta = 1;
        setInactiva();
    }

    private void botonCancelarActionPerformed(ActionEvent evt) {
        respuesta = 2;
        setInactiva();
    }

    private void botonCancelarActionPerformed(KeyEvent evt) {
        respuesta = 2;
        setInactiva();
    }

    public void setInactiva2() {
        respuesta = 2;
        this.setVisible(false);
    }

    
    
	@Override
	public void setDatos(Object datos) {
		try {
			Vector<Object> v = (Vector<Object>) datos;
			
			/* Esperamos un vector cuya primera componente sea el mensaje, la segunda el titulo, la tercera
			*  si se permite o no la opción cancelar, la cuarta el mensaje que debemos enviar al controlador en caso 
			*  de que la operación sea confirmada, la quinta los datos a enviar al controlador en caso de éxito, 
			*  y la sexta el posible mensaje al controlador en caso de que se pulse 'No'. */
			
			String mensaje = (String) v.get(0);
			String titulo = (String) v.get(1);
			boolean cancelar = (boolean) v.get(2);
			this.mensCasoAceptar = (TC) v.get(3);
			this.datosCasoAceptar = v.get(4);
			this.mensCasoCancelar = (TC) v.get(5);
			
			pregunta.setText(mensaje);
	        setTitle(titulo);
	        botonCancelar.setVisible(cancelar);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setActiva() {
		this.centraEnPantalla();
        this.setVisible(true);
	}

	@Override
	public boolean setActiva(int op) {
		boolean result = false;
		if(op == 0) {
			this.centraEnPantalla();
	        this.setVisible(true);
	        result = true;
		}
		return result;
	}
}