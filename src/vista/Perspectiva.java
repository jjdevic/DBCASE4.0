package vista;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import vista.componentes.GUIPanels.ImagePanel;


/*
 * Clase que maneja las perspectivas
 * */
public class Perspectiva {
	
	private Container mainPanel;
	//private ImagePanel diagrama;
	private JPanel diagrama;
	private JPanel codigos;
	private JSplitPane diagramaSplitCode;
	private JSplitPane splitCodigos;
	private JSplitPane infoSplitMapa;
	private JSplitPane programmerSplit;
	private JTabbedPane infoPanel;
	private byte modo;
		
	public Perspectiva(Container mainPanel, JPanel diagrama, JPanel codigos, JTabbedPane infoPanel){
		this.mainPanel = mainPanel;
		this.diagrama = diagrama;
		this.codigos = codigos;
		this.infoPanel = infoPanel;
		this.splitCodigos = ((JSplitPane) codigos.getComponent(0));
		this.infoSplitMapa = (JSplitPane) (((JSplitPane) diagrama.getComponent(0)).getComponent(2));
		this.diagramaSplitCode = new JSplitPane();
		this.diagramaSplitCode.setBorder(null);
		this.programmerSplit = new JSplitPane();
		this.programmerSplit.setBorder(null);
	}
	
	public void loadDefaultView() {
		if(modo==0) modoVerTodo();
		else if(modo==1) modoDiseno();
		else if(modo==2) modoProgramador();
		else modoVerTodo();
	}
	/*
	 * Muestra todos los paneles
	 * */
	public void modoVerTodo() {
		mainPanel.removeAll();
		infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
		mainPanel.add(diagramaSplitCode);
		splitCodigos.setOrientation(JSplitPane.VERTICAL_SPLIT);
		diagramaSplitCode.add(codigos, JSplitPane.RIGHT);
		diagramaSplitCode.add(diagrama, JSplitPane.LEFT);
		diagramaSplitCode.setResizeWeight(1);
		infoSplitMapa.setResizeWeight(0.2);
		//diagramaSplitCode.setResizeWeight(0);
		diagramaSplitCode.setVisible(true);
		programmerSplit.setVisible(false);
		mainPanel.revalidate();		
		mainPanel.repaint();
		modo = 0;
	}
	
	/*
	 * Muestra solo los paneles de diseno del diagrama
	 * */
	public void modoDiseno() {
		mainPanel.removeAll();
		infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
		mainPanel.add(diagrama);
		infoSplitMapa.setResizeWeight(0.2);
		diagramaSplitCode.setResizeWeight(0.1);
		diagramaSplitCode.setVisible(false);
		programmerSplit.setVisible(false);
		mainPanel.revalidate();
		mainPanel.repaint();
		modo = 1;
	}
	
	/*public void cambiarZoom(int zoom) {
		if(modo==0) {
			
			//diagrama.revalidate();
			//diagrama.repaint();
			mainPanel.removeAll();
			Double d = Double.valueOf(zoom);
			Graphics g = diagrama.getGraphics();
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform savedXForm = g2d.getTransform();
			g2d.scale(1.5, 1.5);
			diagrama.paint(g);
			g2d.setTransform(savedXForm);
			infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
			mainPanel.add(diagramaSplitCode);
			splitCodigos.setOrientation(JSplitPane.VERTICAL_SPLIT);
			diagramaSplitCode.add(codigos, JSplitPane.RIGHT);
			diagramaSplitCode.add(diagrama, JSplitPane.LEFT);
			infoSplitMapa.setResizeWeight(0.2);
			diagramaSplitCode.setResizeWeight(0);
			diagramaSplitCode.setVisible(true);
			programmerSplit.setVisible(false);
			mainPanel.revalidate();		
			mainPanel.repaint();
		}
		
		else if (modo==1) {
			mainPanel.removeAll();
			Double d = Double.valueOf(zoom);
			Graphics g = diagrama.getGraphics();
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform savedXForm = g2d.getTransform();
			g2d.scale(1.5, 1.5);
			diagrama.paint(g);
			g2d.setTransform(savedXForm);
			infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
			mainPanel.add(diagrama);
			infoSplitMapa.setResizeWeight(0.2);
			diagramaSplitCode.setResizeWeight(0.1);
			diagramaSplitCode.setVisible(false);
			programmerSplit.setVisible(false);
			mainPanel.revalidate();
			mainPanel.repaint();
		}
		
		else if (modo==2) {
			mainPanel.removeAll();
			Double d = Double.valueOf(zoom);
			Graphics g = diagrama.getGraphics();
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform savedXForm = g2d.getTransform();
			g2d.scale(1.5, 1.5);
			diagrama.paint(g);
			g2d.setTransform(savedXForm);
			splitCodigos.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			programmerSplit.add(infoPanel, JSplitPane.LEFT);
			programmerSplit.add(codigos, JSplitPane.RIGHT);
			mainPanel.add(programmerSplit);
			diagramaSplitCode.setVisible(false);
			programmerSplit.setVisible(true);
			mainPanel.revalidate();
			mainPanel.repaint();
		}
		/*Double d = Double.valueOf(zoom);
		Graphics g = panelDiseno.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform savedXForm = g2d.getTransform();
		g2d.scale(1.5, 1.5);
		panelDiseno.paint(g);
		g2d.setTransform(savedXForm);
		this.revalidate();
		this.repaint();
		panelDiseno.revalidate();
		panelDiseno.repaint();
	}*/
	
	public void modoCuadricula(boolean cuadricula) throws IOException {
		/*if (cuadricula) {
			Image img = ImageIO.read(getClass().getResource("/vista/imagenes/casillas.PNG"));
			if (modo == 0) {
				mainPanel.removeAll();
				infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
				mainPanel.add(diagramaSplitCode);
				splitCodigos.setOrientation(JSplitPane.VERTICAL_SPLIT);
				diagramaSplitCode.add(codigos, JSplitPane.RIGHT);
				diagramaSplitCode.add(diagrama, JSplitPane.LEFT);
				infoSplitMapa.setResizeWeight(0.2);
				diagramaSplitCode.setResizeWeight(0);
				diagramaSplitCode.setVisible(true);
				programmerSplit.setVisible(false);
				mainPanel.revalidate();		
				mainPanel.repaint();
				diagrama.setFondo(img, diagrama.getGraphics());	
			}
			else if (modo == 1) {
				mainPanel.removeAll();
				infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
				mainPanel.add(diagrama);
				infoSplitMapa.setResizeWeight(0.2);
				diagramaSplitCode.setResizeWeight(0.1);
				diagramaSplitCode.setVisible(false);
				programmerSplit.setVisible(false);
				mainPanel.revalidate();
				mainPanel.repaint();
				diagrama.setFondo(img, diagrama.getGraphics());
			}
			else if (modo == 2) {
				mainPanel.removeAll();
				splitCodigos.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
				programmerSplit.add(infoPanel, JSplitPane.LEFT);
				programmerSplit.add(codigos, JSplitPane.RIGHT);
				mainPanel.add(programmerSplit);
				diagramaSplitCode.setVisible(false);
				programmerSplit.setVisible(true);
				mainPanel.revalidate();
				mainPanel.repaint();
				diagrama.setFondo(img, diagrama.getGraphics());
			}
		}
		else {
			if (modo == 0) {
				mainPanel.removeAll();
				infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
				mainPanel.add(diagramaSplitCode);
				splitCodigos.setOrientation(JSplitPane.VERTICAL_SPLIT);
				diagramaSplitCode.add(codigos, JSplitPane.RIGHT);
				diagramaSplitCode.add(diagrama, JSplitPane.LEFT);
				infoSplitMapa.setResizeWeight(0.2);
				diagramaSplitCode.setResizeWeight(0);
				diagramaSplitCode.setVisible(true);
				programmerSplit.setVisible(false);
				mainPanel.revalidate();		
				mainPanel.repaint();
				diagrama.setFondo(null, diagrama.getGraphics());	
			}
			else if (modo == 1) {
				mainPanel.removeAll();
				infoSplitMapa.add(infoPanel, JSplitPane.RIGHT);
				mainPanel.add(diagrama);
				infoSplitMapa.setResizeWeight(0.2);
				diagramaSplitCode.setResizeWeight(0.1);
				diagramaSplitCode.setVisible(false);
				programmerSplit.setVisible(false);
				mainPanel.revalidate();
				mainPanel.repaint();
				diagrama.setFondo(null, diagrama.getGraphics());
			}
			else if (modo == 2) {
				mainPanel.removeAll();
				splitCodigos.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
				programmerSplit.add(infoPanel, JSplitPane.LEFT);
				programmerSplit.add(codigos, JSplitPane.RIGHT);
				mainPanel.add(programmerSplit);
				diagramaSplitCode.setVisible(false);
				programmerSplit.setVisible(true);
				mainPanel.revalidate();
				mainPanel.repaint();
				diagrama.setFondo(null, diagrama.getGraphics());
			}
		}*/
	}
	
	/*
	 * Muestra solo los paneles de edicion de codigo
	 * */
	public void modoProgramador() {
		mainPanel.removeAll();
		splitCodigos.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		programmerSplit.add(infoPanel, JSplitPane.LEFT);
		programmerSplit.add(codigos, JSplitPane.RIGHT);
		mainPanel.add(programmerSplit);
		diagramaSplitCode.setVisible(false);
		programmerSplit.setVisible(true);
		mainPanel.revalidate();
		mainPanel.repaint();
		modo = 2;
	}
	
	public byte getPanelsMode() {
		return modo;
	}
	
	
}
