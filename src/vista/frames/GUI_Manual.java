package vista.frames;

// Hola mikel
//hola artu

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import vista.componentes.GUIPanels.ReportPanel;
import vista.imagenes.ImagePath;
import vista.lenguaje.Lenguaje;
import vista.tema.Theme;

public class GUI_Manual extends JFrame{

	private JTextPane texto1;
	private JTextPane texto2;
	private JTextPane texto3;
	private JTextPane texto4;
	
	
	private Theme theme = Theme.getInstancia();

	
	public GUI_Manual() {
		this.setTitle(Lenguaje.text(Lenguaje.MANUAL));
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagePath.LOGODBDT)).getImage());
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setResizable(true);
		//setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel panel_principal=new JPanel();
		JPanel primer_panel=new JPanel();
		JPanel segundo_panel=new JPanel();
		JPanel tercer_panel=new JPanel();
		
		primer_panel.setBackground(theme.background());
		segundo_panel.setBackground(theme.background());
		tercer_panel.setBackground(theme.background());
		
		texto1 = new ReportPanel();
		texto1.setEditable(false);
		texto1.setBorder(BorderFactory.createEmptyBorder());
		
		texto2 = new ReportPanel();
		texto2.setEditable(false);
		texto2.setBorder(BorderFactory.createEmptyBorder());
		
		texto3 = new ReportPanel();
		texto3.setEditable(false);
		texto3.setBorder(BorderFactory.createEmptyBorder());
		
		texto4 = new ReportPanel();
		texto4.setEditable(false);
		texto4.setBorder(BorderFactory.createEmptyBorder());
		
		ImageIcon im1 = new ImageIcon(getClass().getClassLoader().getResource(ImagePath.IMAGE1));
		JLabel label1 = new JLabel (im1);
		label1.setBorder(BorderFactory.createEmptyBorder());
		
		ImageIcon im2 = new ImageIcon(getClass().getClassLoader().getResource(ImagePath.IMAGE2));
		JLabel label2 = new JLabel (im2);
		label2.setBorder(BorderFactory.createEmptyBorder());
		
		ImageIcon im3 = new ImageIcon(getClass().getClassLoader().getResource(ImagePath.IMAGE3));
		JLabel label3 = new JLabel (im3);
		label3.setBorder(BorderFactory.createEmptyBorder());

		primer_panel.setLayout(new BorderLayout());
		primer_panel.add(texto1,BorderLayout.NORTH);
		primer_panel.add(label1,BorderLayout.SOUTH);
		
		segundo_panel.setLayout(new BorderLayout());
		segundo_panel.add(texto2,BorderLayout.NORTH);
		segundo_panel.add(label2,BorderLayout.SOUTH);

		tercer_panel.setLayout(new BorderLayout());
		tercer_panel.add(texto3,BorderLayout.NORTH);
		tercer_panel.add(label3,BorderLayout.CENTER);
		tercer_panel.add(texto4,BorderLayout.SOUTH);
		
		
		panel_principal.setLayout(new BorderLayout());
		panel_principal.add(primer_panel,BorderLayout.NORTH);
		panel_principal.add(segundo_panel,BorderLayout.CENTER);
		panel_principal.add(tercer_panel,BorderLayout.SOUTH);
		
		JScrollPane scrollBar=new JScrollPane(panel_principal,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scrollBar.getVerticalScrollBar().setValue(scrollBar.getVerticalScrollBar().getMaximum());
		//scrollBar.getVerticalScrollBar().setValue(0);
		/*JScrollPane scrollBar = new JScrollPane();
		scrollBar.setViewportView(panel_principal);*/
		this.add(scrollBar);
	
		
	}
	public void setActiva(boolean b) {
		
		setLocationRelativeTo(null);
		texto1.setText("");
		//css
		texto1.setText("<style>"
				+ "body{background-color:"+theme.background().hexValue()+";margin:50;padding:0;}"
				+ "h1{text-align:center;font-size:30px;padding-top:15px;color:"+theme.fontColor().hexValue()+"}"
				+ "h2{font-style:italic;text-align:center;font-size:14px;color:"+theme.paragraph().hexValue()+"}"
				+ "h3{text-align:center;font-size:12px;color:"+theme.fontColor().hexValue()+"}"
				+ "li{padding-top:0px;margin-left:8px}"
				+ "p{font-size:11px;padding-left:30px;color:"+theme.paragraph().hexValue()+"}"
				+ "</style><p></p>");
		texto2.setText("<style>"
				+ "body{background-color:"+theme.background().hexValue()+";margin:50;padding:0;}"
				+ "h1{text-align:center;font-size:30px;padding-top:15px;color:"+theme.fontColor().hexValue()+"}"
				+ "h2{font-style:italic;text-align:center;font-size:14px;color:"+theme.paragraph().hexValue()+"}"
				+ "h3{text-align:center;font-size:12px;color:"+theme.fontColor().hexValue()+"}"
				+ "li{padding-top:0px;margin-left:8px}"
				+ "p{font-size:11px;padding-left:30px;color:"+theme.paragraph().hexValue()+"}"
				+ "</style><p></p>");
		texto3.setText("<style>"
				+ "body{background-color:"+theme.background().hexValue()+";margin:50;padding:0;}"
				+ "h1{text-align:center;font-size:30px;padding-top:15px;color:"+theme.fontColor().hexValue()+"}"
				+ "h2{font-style:italic;text-align:center;font-size:14px;color:"+theme.paragraph().hexValue()+"}"
				+ "h3{text-align:center;font-size:12px;color:"+theme.fontColor().hexValue()+"}"
				+ "li{padding-top:0px;margin-left:8px}"
				+ "p{font-size:11px;padding-left:30px;color:"+theme.paragraph().hexValue()+"}"
				+ "</style><p></p>");
		texto4.setText("<style>"
				+ "body{background-color:"+theme.background().hexValue()+";margin:50;padding:0;}"
				+ "h1{text-align:center;font-size:30px;padding-top:15px;color:"+theme.fontColor().hexValue()+"}"
				+ "h2{font-style:italic;text-align:center;font-size:14px;color:"+theme.paragraph().hexValue()+"}"
				+ "h3{text-align:center;font-size:12px;color:"+theme.fontColor().hexValue()+"}"
				+ "li{padding-top:0px;margin-left:8px}"
				+ "p{font-size:11px;padding-left:30px;color:"+theme.paragraph().hexValue()+"}"
				+ "</style><p></p>");
		
		//texto
		texto1.setText(
			"<body><h1>"+Lenguaje.text(Lenguaje.DB_CASE_TOOL)+"</h1>" +
			"<h3>"+Lenguaje.text(Lenguaje.MANUALUSUARIO)+"</h3>" +
			"<p><strong>"+Lenguaje.text(Lenguaje.DESRIPCION)+"</p></strong>"+
			"<ul><li><p><strong>"+Lenguaje.text(Lenguaje.MODELO_CONCEPTUAL)+"</strong>"+
				Lenguaje.text(Lenguaje.MODELO_CONCEPTUAL_DESC)+"</p>" +
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MODELO_LOGICO)+"</strong>"+
				Lenguaje.text(Lenguaje.MODELO_LOGICO_DESC)+"</p>" +
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MODELO_FISICO)+"</strong>" +
				Lenguaje.text(Lenguaje.MODELO_FISICO_DESC)+"</p><br><br>"+
				"<ul><li><p><strong>"+Lenguaje.text(Lenguaje.ESQUEMAS)+"</strong></p>"+
				"<p>"+Lenguaje.text(Lenguaje.ESQUEMAS_DESC)+"</p>"+
				"<ul><li><p><strong>"+Lenguaje.text(Lenguaje.ESQUEMA_CONCEPTUAL)+"</p></strong>"+
				"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_CONCEPTUALDESC1)+"</p>"+
				"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_CONCEPTUALDESC2)+"</p>"+
				"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_CONCEPTUALDESC3)+"</p><br>");
			
		texto2.setText(
			"<li><p><strong>"+Lenguaje.text(Lenguaje.ESQUEMA_LOGICO)+"</p></strong>"+
			"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_LOGICO_DESC)+"</p><br>");
		
		texto3.setText(
			"<li><p><strong>"+Lenguaje.text(Lenguaje.ESQUEMA_FISICO)+"</p></strong>"+
			"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_FISICO_DESC1)+"</p>"+
			"<p>"+Lenguaje.text(Lenguaje.ESQUEMA_FISICO_DESC2)+"</p></li></ul><br><br>");
			
		texto4.setText(
			"<li><p><strong>"+Lenguaje.text(Lenguaje.ELEMENTOS_Y_DOMINIOS)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.ELEMENTOS_Y_DOMINIOS_DESC1)+"</p>"+
			"<p>"+Lenguaje.text(Lenguaje.ELEMENTOS_Y_DOMINIOS_DESC2)+"</p><br><br>"+
			
			"<li><p><strong>"+Lenguaje.text(Lenguaje.VISTAS)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.VISTAS_DESC1)+"</p>"+
			"<p>"+Lenguaje.text(Lenguaje.VISTAS_DESC2)+"</p><br><br>"+
			
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_DISENO)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_DISENO_DESC)+"</p><br><br>"+
			
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENUS)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENUS_DESC)+"</p>"+
			"<ul><li><p><strong>"+Lenguaje.text(Lenguaje.MENU_ARCHIVO)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_ARCHIVO_DESC1)+"</p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_ARCHIVO_DESC2)+"</p><br>"+
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_OPCIONES)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_OPCIONES_DESC1)+"</p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_OPCIONES_DESC2)+"</p><br>"+
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_AYUDA)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_AYUDA_DESC)+"</p><br>"+
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_VISTA)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_VISTA_DESC)+"</p><br>" +
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_CONCEPTUAL)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_CONCEPTUALDESC)+"</p><br>" +
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_LOGICO)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_LOGICODESC)+"</p><br>" + 
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_FISICO)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_FISICODESC)+"</p>" +
			"<p>"+Lenguaje.text(Lenguaje.MENU_FISICODESC2)+"</p><br>" +
			"<li><p><strong>"+Lenguaje.text(Lenguaje.MENU_DOMINIOS)+"</strong></p>"+
			"<p>"+Lenguaje.text(Lenguaje.MENU_DOMINIOSDESC)+"</p><br>" 
			//"<h3>"+Lenguaje.text(Lenguaje.GALERY_NEXT_OPTION)+"</h3>"
			);
		this.setVisible(true);
	}

}
