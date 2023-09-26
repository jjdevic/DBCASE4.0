package vista.frames;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import controlador.Controlador;
import controlador.TC;
import vista.frames.Parent_GUI;
import vista.lenguaje.Lenguaje;

public class GUI_Zoom extends Parent_GUI{
	int zoomInicial; 
	private JTextPane pregunta;
	private JButton botonMas;
	private JLabel valorZoom;
	private JButton botonMenos;
	private JButton botonAceptar;
	private Controlador c;
	//private JButton enviarMail;
	
	public GUI_Zoom(){
		this.initComponents();
	}
	
	private void initComponents() {
		getContentPane().setLayout(null);
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		setResizable(true);
		this.setSize(320, 200);
		
		pregunta = new JTextPane();
		getContentPane().add(pregunta);
		pregunta.setText(Lenguaje.text(Lenguaje.ZOOM_HEADER));
		pregunta.setForeground(theme.labelFontColorDark());
		pregunta.setFont(theme.font());
		pregunta.setBounds(10, 10, 280, 50);
		pregunta.setEditable(false);
		pregunta.setOpaque(false);
		pregunta.setFocusable(false);
		
		botonMas = boton(205, 80, "+");
		getContentPane().add(botonMas);
		botonMas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				botonMasActionPerformed(evt);
			}
		});
		botonMas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_PLUS){botonMasActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_A){botonAceptarActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_MINUS){botonMenosActionPerformed(e);}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		
		valorZoom = new JLabel();
		getContentPane().add(valorZoom);
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
		valorZoom.setAlignmentX(CENTER_ALIGNMENT);
		valorZoom.setAlignmentY(CENTER_ALIGNMENT);
		valorZoom.setForeground(theme.fontColor());
		valorZoom.setBorder(BorderFactory.createLineBorder(theme.fontColor()));
		valorZoom.setFont(theme.font());
		valorZoom.setBounds(125, 80, 55, 30);
		pregunta.setEditable(false);
		pregunta.setOpaque(false);
		pregunta.setFocusable(false);
		//valorZoom.setText(String.valueOf(zoomInicial));
		valorZoom.setVisible(true);
		
		botonMenos = boton(40, 80, "-");
		getContentPane().add(botonMenos);
		botonMenos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				botonMenosActionPerformed(evt);
			}
		});
		botonMenos.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_PLUS){botonMasActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_A){botonAceptarActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_MINUS){botonMenosActionPerformed(e);}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		
		botonAceptar = boton(100, 120, Lenguaje.text(Lenguaje.ACEPTAR));
		getContentPane().add(botonAceptar);
		botonAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				botonAceptarActionPerformed(evt);
			}
		});
		botonMenos.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_PLUS){botonMasActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_A){botonAceptarActionPerformed(e);}
				else if(e.getKeyCode()==KeyEvent.VK_MINUS){botonMenosActionPerformed(e);}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		
		this.addKeyListener(this);
	}
	
	/*public GUI_Zoom() {
		zoomInicial = c.getZoom();//???
		this.setTitle(Lenguaje.text(Lenguaje.ZOOM));
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagePath.LOGODBDT)).getImage());
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		this.setSize(320, 180);
		
		JPanel panel=new JPanel();
		
		encabezado=new JLabel();
		modificarZoom=new JLabel();
		mas=new JButton();
		valorZoom=new JLabel();
		menos=new JButton();
		aceptar=new JButton();
		
		panel.setLayout(null);
	
		panel.add(encabezado);
		panel.add(modificarZoom);
		panel.add(mas);
		panel.add(valorZoom);
		panel.add(menos);
		panel.add(aceptar);
		//panel.add(enviarMail);
		
		this.add(panel);
		
		//JScrollPane scrollBar=new JScrollPane(panel ,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scrollBar.getHorizontalScrollBar().setValue(1);
		
		this.setEnabled(true);
	}*/
	
	public void setActiva() {
		zoomInicial = c.getZoom();
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
		setTitle(Lenguaje.text(Lenguaje.DBCASE));
		this.centraEnPantalla();
		this.setVisible(true);
	}
	
	public void setInactiva(){
		this.setVisible(false);
	}
	
	private void botonMasActionPerformed(ActionEvent evt) {
		zoomInicial = zoomInicial + 5;
		getContentPane().add(valorZoom);
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
	}
	
	private void botonMenosActionPerformed(ActionEvent evt) {
		zoomInicial = zoomInicial - 5;
		getContentPane().add(valorZoom);
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
	}
	
	private void botonAceptarActionPerformed(ActionEvent evt) {
		c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Zoom_Aceptar, zoomInicial);
		setInactiva();
	}	
	
	private void botonMasActionPerformed(KeyEvent evt) {
		zoomInicial = zoomInicial + 5;
		getContentPane().add(valorZoom);
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
	}
	
	private void botonMenosActionPerformed(KeyEvent evt) {
		zoomInicial = zoomInicial - 5;
		getContentPane().add(valorZoom);
		if(zoomInicial<10 && zoomInicial>-10)valorZoom.setText("  " + String.valueOf(zoomInicial)+"%");
		else valorZoom.setText(" " +String.valueOf(zoomInicial)+"%");
	}
	
	private void botonAceptarActionPerformed(KeyEvent evt) {
		c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Zoom_Aceptar, zoomInicial);
		setInactiva();
	}	

	public Controlador getControlador() {
		return c;
	}

	public void setControlador(Controlador controlador) {
		this.c = controlador;
	}
}
