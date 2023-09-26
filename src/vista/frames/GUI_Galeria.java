package vista.frames;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import vista.componentes.GUIPanels.ReportPanel;
import vista.imagenes.ImagePath;
import vista.lenguaje.Lenguaje;
import vista.tema.Theme;


public class GUI_Galeria extends JFrame{
	
	private JTextPane texto1;
	private Theme theme = Theme.getInstancia();
	
	public GUI_Galeria() {
		this.setTitle(Lenguaje.text(Lenguaje.GALERIA));
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagePath.LOGODBDT)).getImage());
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setResizable(true);
		//setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel panel=new JPanel();
		
		texto1 = new ReportPanel();
		texto1.setEditable(false);
		panel.add(texto1);
		JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
		//texto
		texto1.setText(
			"<body><h1>"+Lenguaje.text(Lenguaje.DB_CASE_TOOL)+"</h1>" +
			"<h3>"+Lenguaje.text(Lenguaje.GALERIADESC)+"</h3>");
		this.setVisible(true);
	}
}
