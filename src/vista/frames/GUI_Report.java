package vista.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import controlador.Controlador;
import controlador.TC;
import vista.imagenes.ImagePath;
import vista.lenguaje.Lenguaje;
import vista.tema.myColor;

public class GUI_Report extends JFrame{
	private JLabel encabezado;
	private JLabel reportarIncidencia;
	private JTextArea textoIncidencia;
	private JLabel avisoTextoVacio;
	private JButton aceptar;
	private Controlador c;
	//private JButton enviarMail;
	
	public GUI_Report() {
		this.setTitle(Lenguaje.text(Lenguaje.REPORT));
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagePath.LOGODBDT)).getImage());
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		this.setSize(800, 850);
		
		JPanel panel=new JPanel();
		
		encabezado=new JLabel();
		reportarIncidencia=new JLabel();
		textoIncidencia=new JTextArea();
		avisoTextoVacio=new JLabel();
		aceptar=new JButton();
		//enviarMail=new JButton();
		
		panel.setLayout(null);
	
		panel.add(encabezado);
		panel.add(reportarIncidencia);
		panel.add(textoIncidencia);
		panel.add(avisoTextoVacio);
		panel.add(aceptar);
		//panel.add(enviarMail);
		
		this.add(panel);
		
		//JScrollPane scrollBar=new JScrollPane(panel ,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scrollBar.getHorizontalScrollBar().setValue(1);
		
		this.setEnabled(true);
	}
	
	public void setActiva() {
		setLocationRelativeTo(null);
	

		encabezado.setText(Lenguaje.text(Lenguaje.REPORT_HEADER));
		Font font = new Font("Italic", Font. BOLD,21);
		encabezado.setFont(font);
		encabezado.setBounds(179,20,800,40);
		
		reportarIncidencia.setText(Lenguaje.text(Lenguaje.REPORT_ISSUE));
		reportarIncidencia.setBounds(42, 100, 500, 21);

		
		textoIncidencia.setBounds(41,150,700,400);
		textoIncidencia.setLineWrap(true);
		Font fontTextArea=new Font("Calibri",Font.PLAIN,18);
		textoIncidencia.setFont(fontTextArea);
		textoIncidencia.setText("");
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		textoIncidencia.setBorder(border);
		myColor c = new myColor(0,0,0);
		textoIncidencia.setForeground(c);
		
		avisoTextoVacio.setText(Lenguaje.text(Lenguaje.EMPTY_REPORT_WARNING));
		avisoTextoVacio.setBounds(25,620,400,40);
		avisoTextoVacio.setForeground(Color.RED);
		avisoTextoVacio.setVisible(false);
		
		aceptar.setText(Lenguaje.text(Lenguaje.REPORT_BUTTON));
		aceptar.setBounds(317,570,150,40);
		try {
		    Image img = ImageIO.read(getClass().getResource("/vista/imagenes/emailIcon.png"));
		    Image newimg = img.getScaledInstance( 35, 35,  java.awt.Image.SCALE_SMOOTH ) ;  
		    aceptar.setIcon(new ImageIcon(newimg));
		  } catch (Exception ex) {
		    System.out.println(ex);
		  }
		aceptar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				botonReportarActionPerformed(evt);
			}
			
		});
		
		this.setVisible(true);
	}
	public void botonReportarActionPerformed(java.awt.event.ActionEvent evt) {
		
		if(textoIncidencia.getText().equals("")) {
			avisoTextoVacio.setVisible(true);
		}
		
		else {
			Vector<Object> datos= new Vector <Object>();
			datos.add(this.textoIncidencia.getText());
			datos.add(true);
			c.mensajeDesde_GUI(TC.GUIReport_ReportarIncidencia, datos);
			
			this.setVisible(false);
		}
		 
	}

	public Controlador getControlador() {
		return c;
	}

	public void setControlador(Controlador controlador) {
		this.c = controlador;
	}
}
