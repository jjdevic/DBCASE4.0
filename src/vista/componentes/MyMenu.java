package vista.componentes;


import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.Transfer;
import modelo.transfers.TransferConexion;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;
import vista.iconos.IconLabel;
import vista.iconos.perspective.allIcon;
import vista.iconos.perspective.codeIcon;
import vista.iconos.perspective.diagramIcon;
import vista.lenguaje.Lenguaje;
import vista.tema.Theme;


// ICONOS: https://icon-icons.com/es/

@SuppressWarnings("serial")
public class MyMenu extends JMenuBar{

	private int[] coords;
	private int diagramWidth;
	private JMenu menuSistema;
	private JMenuItem submenuNuevo;
	private JMenuItem submenuAbrir;
	private JMenuItem submenuAbrirCasos;
	private JMenuItem submenuAbrirRecentFiles;
	private JMenuItem submenuGuardar;
	private JMenuItem submenuGuardarComo;
	private AbstractButton submenuImprimir;
	private AbstractButton submenuExportarJPEG;
	private AbstractButton submenuSalir;
	private AbstractButton menuOpciones;
	private JMenu menuLenguajes;
	private Vector<JRadioButtonMenuItem> elementosMenuLenguajes;
	private Vector<JRadioButtonMenuItem> elementosBaseDatos;
	private JRadioButtonMenuItem nullAttr;
	private JRadioButtonMenuItem confirmarEliminaciones;
	private JRadioButtonMenuItem modoSoporte;
	private JMenu optionsMenu;
	private JMenu menuAyuda;
	private JMenu menuVista;
	private JMenu menuConceptual;
	private JMenu menuLogico;
	private JMenu menuFisico;
	private JMenu menuDominios;
	private AbstractButton submenuAcercaDe;
	private AbstractButton submenuReportarIncidencia;
	private AbstractButton submenuManual;
	private AbstractButton submenuGaleria;
	private AbstractButton submenuVista1;
	private AbstractButton submenuVista2;
	private AbstractButton submenuVista3;
	private AbstractButton submenuZoom;
	private AbstractButton submenuCuadricula;
	private AbstractButton submenuAnadirEntidad;
	private AbstractButton submenuAnadirRelacion;
	private AbstractButton submenuAnadirRelacionIsA;
	private AbstractButton submenuAnadirAtributo;
	private AbstractButton submenuGenerarLogico;
	private AbstractButton submenuGuardarComoLogico;
	//private AbstractButton submenuBaseDatos;
	private AbstractButton submenuGenerarFisico;
	private AbstractButton submenuGuardarComoFisico;
	private AbstractButton submenuEjecutarFisico;
	private AbstractButton submenuCrearDominio;
	private Theme theme;
	private MenuListener a;
	private diagramIcon diagramIcon;
	private codeIcon codeIcon;
	private allIcon allIcon;
	private JButton deshacer;
	private JButton rehacer;
	private JButton reset;
	private Vector<Transfer> listaTransfers;

	@SuppressWarnings("deprecation")
	public MyMenu(Controlador c) {
		coords = new int[2];
		coords[0]=70;
		coords[1]=450;
		this.theme = Theme.getInstancia();
		setOpaque(true);
		setBorder(BorderFactory.createCompoundBorder(null,null));
		//File
		menuSistema = new JMenu();
		menuSistema.setForeground(theme.fontColor());
		menuSistema.setFont(theme.font());
		menuSistema.addMenuListener(a);
		add(menuSistema);
		menuSistema.setText(Lenguaje.text(Lenguaje.FILE));
		menuSistema.setMnemonic(Lenguaje.text(Lenguaje.FILE).charAt(0));
			//File/new
			submenuNuevo = new JMenuItem();
			submenuNuevo.setFont(theme.font());
			submenuNuevo.setForeground(theme.fontColor());
			menuSistema.add(submenuNuevo);
			ImageIcon nuevo = new ImageIcon();
			try {
				nuevo.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/Nuevo.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuNuevo.setIcon(nuevo);
			submenuNuevo.setText(Lenguaje.text(Lenguaje.NEW));
			submenuNuevo.setMnemonic(Lenguaje.text(Lenguaje.NEW).charAt(0));
			submenuNuevo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Nuevo, null);
				}
			});
			//File/open
			submenuAbrir = new JMenuItem();
			submenuAbrir.setForeground(theme.fontColor());
			submenuAbrir.setFont(theme.font());
			menuSistema.add(submenuAbrir);
			ImageIcon abrir = new ImageIcon();
			try {
				abrir.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/Abrir.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuAbrir.setIcon(abrir);
			submenuAbrir.setText(Lenguaje.text(Lenguaje.OPEN)+"...");
			submenuAbrir.setMnemonic(Lenguaje.text(Lenguaje.OPEN).charAt(0));
			submenuAbrir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Abrir, null);
				}
			});
			
			//File/abrirCasos
			submenuAbrirCasos = new JMenuItem();
			submenuAbrirCasos.setForeground(theme.fontColor());
			submenuAbrirCasos.setFont(theme.font());
			menuSistema.add(submenuAbrirCasos);
			ImageIcon abrirCasos = new ImageIcon();
			try {
				abrirCasos.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/AbrirCasos.PNG")));
      } catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
      submenuAbrirCasos.setIcon(abrirCasos);
			submenuAbrirCasos.setText(Lenguaje.text(Lenguaje.OPEN_CASOS)+"...");
			//submenuAbrir.setMnemonic(Lenguaje.text(Lenguaje.OPEN).charAt(0));
			submenuAbrirCasos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Abrir_Casos, null);
        }
			});
      
			//File/openRecentFiles
			submenuAbrirRecentFiles = new JMenuItem(); 
			submenuAbrirRecentFiles.setForeground(theme.fontColor());
			submenuAbrirRecentFiles.setFont(theme.font());
			menuSistema.add(submenuAbrirRecentFiles);
			ImageIcon abrir2 = new ImageIcon();
			try {
				abrir2.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/AbrirReciente.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuAbrirRecentFiles.setIcon(abrir2);
			submenuAbrirRecentFiles.setText(Lenguaje.text(Lenguaje.RECENT_FILES)+"...");
			//submenuAbrirRecentFiles.setMnemonic(Lenguaje.text(Lenguaje.RECENT_FILES).charAt(0));
			submenuAbrirRecentFiles.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Recientes, null);
				}
			});
			
			
			
			//File/separator
			menuSistema.add(new JSeparator());
			//File/save
			submenuGuardar = new JMenuItem();
			submenuGuardar.setFont(theme.font());
			submenuGuardar.setForeground(theme.fontColor());
			menuSistema.add(submenuGuardar);
			ImageIcon save = new ImageIcon();
			try {
				save.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/Save.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuGuardar.setIcon(save);
			submenuGuardar.setText(Lenguaje.text(Lenguaje.SAVE));
			submenuGuardar.setMnemonic(Lenguaje.text(Lenguaje.SAVE).charAt(0));
			submenuGuardar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Guardar, null);
				}
			});
			
			
			//File/save as...
			submenuGuardarComo = new JMenuItem();
			submenuGuardarComo.setForeground(theme.fontColor());
			submenuGuardarComo.setFont(theme.font());
			menuSistema.add(submenuGuardarComo);
			ImageIcon saveAs = new ImageIcon();
			try {
				saveAs.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/Save As.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuGuardarComo.setIcon(saveAs);
			submenuGuardarComo.setText(Lenguaje.text(Lenguaje.SAVE_AS)+"...");
			submenuGuardarComo.setMnemonic(Lenguaje.text(Lenguaje.SAVE_AS).charAt(1));
			submenuGuardarComo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_GuardarComo, null);
				}
			});
			
			
			submenuGuardarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
			
			//File/separator2
			menuSistema.add(new JSeparator());
			//File/imprimir
			submenuImprimir = new JMenuItem();
			submenuImprimir.setFont(theme.font());
			submenuImprimir.setForeground(theme.fontColor());
			menuSistema.add(submenuImprimir);
			ImageIcon print = new ImageIcon();
			try {
				print.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/print.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuImprimir.setIcon(print);
			submenuImprimir.setText(Lenguaje.text(Lenguaje.PRINT_DIAGRAM));
			submenuImprimir.setMnemonic(Lenguaje.text(Lenguaje.PRINT_DIAGRAM).charAt(0));
			submenuImprimir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Imprimir, null);
				}
			});
			//File/Export
			submenuExportarJPEG = new JMenuItem();
			submenuExportarJPEG.setFont(theme.font());
			submenuExportarJPEG.setForeground(theme.fontColor());
			menuSistema.add(submenuExportarJPEG);
			ImageIcon exportar = new ImageIcon();
			try {
				exportar.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/export.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuExportarJPEG.setIcon(exportar);
			submenuExportarJPEG.setText(Lenguaje.text(Lenguaje.EXPORT_DIAGRAM));
			submenuExportarJPEG.setMnemonic(Lenguaje.text(Lenguaje.EXPORT_DIAGRAM).charAt(0));
			submenuExportarJPEG.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					exportarJPG(evt);
				}
			});
			//File/Separator
			menuSistema.add(new JSeparator());
			//File/salir
			submenuSalir = new JMenuItem();
			submenuSalir.setFont(theme.font());
			submenuSalir.setForeground(theme.fontColor());
			menuSistema.add(submenuSalir);
			ImageIcon exit = new ImageIcon();
			try {
				exit.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/exit.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuSalir.setIcon(exit);
			submenuSalir.setText(Lenguaje.text(Lenguaje.EXIT_MINCASE));
			submenuSalir.setMnemonic(Lenguaje.text(Lenguaje.EXIT_MINCASE).charAt(0));
			submenuSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_Submenu_Salir, null);
				}
			});
		
		menuOpciones = new JMenu();
		menuOpciones.setForeground(theme.fontColor());
		menuOpciones.setFont(theme.font());
		
	    //menuOpciones.add(iconosPerspectiva);
		add(menuOpciones);
		menuOpciones.setText(Lenguaje.text(Lenguaje.OPTIONS));
		menuOpciones.setMnemonic(Lenguaje.text(Lenguaje.OPTIONS).charAt(0));
		optionsMenu = new JMenu();
		optionsMenu.setForeground(theme.fontColor());
		for(String s : this.theme.getAvaiableThemes()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem();
			item.setText(s);
			item.setFont(theme.font());
			item.setForeground(theme.fontColor());
			item.setActionCommand(s);
			if(s.equals(theme.getThemeName()))item.setSelected(true);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_CambiarTema, e.getActionCommand());
					int i = 0;
					for(String s : theme.getAvaiableThemes()) {
						if(theme.getThemeName().equals(s))optionsMenu.getItem(i).setSelected(true);
						else optionsMenu.getItem(i).setSelected(false);
						i++;
					}
				}
			});
			optionsMenu.add(item);
		}
		optionsMenu.setFont(theme.font());
		menuOpciones.add(optionsMenu);
		optionsMenu.setText(Lenguaje.text(Lenguaje.THEME));
		optionsMenu.setMnemonic(Lenguaje.text(Lenguaje.THEME).charAt(0));
		
		//Opciones/Lenguaje
		menuLenguajes = new JMenu();
		menuLenguajes.setForeground(theme.fontColor());
		menuLenguajes.setFont(theme.font());
		menuOpciones.add(menuLenguajes);
		menuLenguajes.setText(Lenguaje.text(Lenguaje.SELECT_LANGUAGE));
		menuLenguajes.setMnemonic(Lenguaje.text(Lenguaje.SELECT_LANGUAGE).charAt(0));
		elementosMenuLenguajes = new Vector<JRadioButtonMenuItem>(0,1);
		Vector<String> lenguajes = Lenguaje.obtenLenguajesDisponibles();
		for (int m=0; m<lenguajes.size(); m++){
			JRadioButtonMenuItem lenguaje = new JRadioButtonMenuItem();
			lenguaje.setText(lenguajes.get(m));
			lenguaje.setFont(theme.font());
			lenguaje.setSelected(lenguajes.get(m).equalsIgnoreCase(Lenguaje.getIdiomaActual()));
			lenguaje.setForeground(theme.fontColor());
			lenguaje.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JRadioButtonMenuItem check = (JRadioButtonMenuItem) e.getSource();
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_CambiarLenguaje, check.getText());
					// Actualizar los checkBox
					for (int k=0; k<elementosMenuLenguajes.size(); k++){
						JRadioButtonMenuItem l = elementosMenuLenguajes.get(k);
						l.setSelected(l.getText().equalsIgnoreCase(Lenguaje.getIdiomaActual()));
					}
				}
			});
			menuLenguajes.add(lenguaje);
			elementosMenuLenguajes.add(lenguaje);
		}
		
		nullAttr = new JRadioButtonMenuItem();
		menuOpciones.add(nullAttr);
		nullAttr.setText(Lenguaje.text(Lenguaje.NULLATTR));
		nullAttr.setMnemonic(Lenguaje.text(Lenguaje.NULLATTR).charAt(0));
		nullAttr.setSelected(c.isNullAttrs());
		nullAttr.setForeground(theme.fontColor());
		nullAttr.setFont(theme.font());
		nullAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_NULLATTR, null);
			}
		});
		
		confirmarEliminaciones=new JRadioButtonMenuItem();
		menuOpciones.add(confirmarEliminaciones);
		confirmarEliminaciones.setText(Lenguaje.text(Lenguaje.CONFIRM_DELETIONS));
		confirmarEliminaciones.setMnemonic(Lenguaje.text(Lenguaje.CONFIRM_DELETIONS).charAt(0));
		confirmarEliminaciones.setSelected(c.getConfirmarEliminaciones());
		confirmarEliminaciones.setForeground(theme.fontColor());
		confirmarEliminaciones.setFont(theme.font());
		confirmarEliminaciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_ConfirmarEliminaciones, null);
			}
		});
		modoSoporte=new JRadioButtonMenuItem();
		menuOpciones.add(modoSoporte);
		modoSoporte.setSelected(false);
		modoSoporte.setText(Lenguaje.text(Lenguaje.HELP_DESK_MODE));
		modoSoporte.setMnemonic(Lenguaje.text(Lenguaje.HELP_DESK_MODE).charAt(0));
		modoSoporte.setForeground(theme.fontColor());
		modoSoporte.setFont(theme.font());
		modoSoporte.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_ModoSoporte, null);
			}
			
		});
		
			
			//Vista
			menuVista = new JMenu();
			menuVista.setForeground(theme.fontColor());
			menuVista.setFont(theme.font());
			add(menuVista);
			menuVista.setText(Lenguaje.text(Lenguaje.VISTA));
			menuVista.setMnemonic(Lenguaje.text(Lenguaje.VISTA).charAt(0));
			
			//Vista/Vista1
			submenuVista1 = new JMenuItem();
			submenuVista1.setFont(theme.font());
			submenuVista1.setForeground(theme.fontColor());
			menuVista.add(submenuVista1);
			ImageIcon vista1 = new ImageIcon();
			try {
				vista1.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/vista1.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuVista1.setIcon(vista1);
			submenuVista1.setText(Lenguaje.text(Lenguaje.VISTA1));
			submenuVista1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Vista1, null);
				}
			});
			
			//Vista/Vista2
			submenuVista2 = new JMenuItem();
			submenuVista2.setFont(theme.font());
			submenuVista2.setForeground(theme.fontColor());
			menuVista.add(submenuVista2);
			ImageIcon vista2 = new ImageIcon();
			try {
				vista2.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/vista2.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuVista2.setIcon(vista2);
			submenuVista2.setText(Lenguaje.text(Lenguaje.VISTA2));
			submenuVista2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Vista2, null);
				}
			});
			
			//Vista/Vista3
			submenuVista3 = new JMenuItem();
			submenuVista3.setFont(theme.font());
			submenuVista3.setForeground(theme.fontColor());
			menuVista.add(submenuVista3);
			ImageIcon vista3 = new ImageIcon();
			try {
				vista3.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/vista3.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuVista3.setIcon(vista3);
			submenuVista3.setText(Lenguaje.text(Lenguaje.VISTA3));
			submenuVista3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Vista3, null);
				}
			});
			
			//Vista/Cuadricula
			submenuCuadricula = new JRadioButtonMenuItem();
			submenuCuadricula.setFont(theme.font());
			submenuCuadricula.setForeground(theme.fontColor());
			menuVista.add(submenuCuadricula);
			ImageIcon cuadricula = new ImageIcon();
			try {
				cuadricula.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/Cuadricula.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuCuadricula.setIcon(cuadricula);
			submenuCuadricula.setText(Lenguaje.text(Lenguaje.CUADRICULA));
			submenuCuadricula.setMnemonic(Lenguaje.text(Lenguaje.CUADRICULA).charAt(0));
			submenuCuadricula.setToolTipText(Lenguaje.text(Lenguaje.DESARROLLO));
			submenuCuadricula.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Cuadricula, null);
				}
			});
			
			//Vista/Zoom
			submenuZoom = new JMenuItem();
			submenuZoom.setFont(theme.font());
			submenuZoom.setForeground(theme.fontColor());
			menuVista.add(submenuZoom);
			ImageIcon zoom = new ImageIcon();
			try {
				zoom.setImage(ImageIO.read(getClass().getResource("/vista/imagenes/zoom.PNG")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1);
			}
			submenuZoom.setIcon(zoom);
			submenuZoom.setToolTipText(Lenguaje.text(Lenguaje.DESARROLLO));
			submenuZoom.setText(Lenguaje.text(Lenguaje.ZOOM));
			submenuZoom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Zoom, null);
				}
			});
			
			//Menu Conceptual
			menuConceptual = new JMenu();
			menuConceptual.setForeground(theme.fontColor());
			menuConceptual.setFont(theme.font());
			add(menuConceptual);
			menuConceptual.setText(Lenguaje.text(Lenguaje.CONCEPTUAL));	
			menuConceptual.setMnemonic(Lenguaje.text(Lenguaje.CONCEPTUAL).charAt(0));
			
			submenuAnadirEntidad = new JMenuItem();
			submenuAnadirEntidad.setFont(theme.font());
			submenuAnadirEntidad.setForeground(theme.fontColor());
			menuConceptual.add(submenuAnadirEntidad);
			submenuAnadirEntidad.setText(Lenguaje.text(Lenguaje.ADD_ENTITY));
			submenuAnadirEntidad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Point2D p = new Point2D.Double(coords[0],coords[1]);
				c.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarEntidad,p);
				aumentaCoords();
				}
			});
			
			submenuAnadirRelacion = new JMenuItem();
			submenuAnadirRelacion.setFont(theme.font());
			submenuAnadirRelacion.setForeground(theme.fontColor());
			menuConceptual.add(submenuAnadirRelacion);
			submenuAnadirRelacion.setText(Lenguaje.text(Lenguaje.ADD_RELATION));
			submenuAnadirRelacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Point2D p = new Point2D.Double(coords[0],coords[1]);
				c.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionNormal, p);
				aumentaCoords();
				}
			});
			
			submenuAnadirRelacionIsA = new JMenuItem();
			submenuAnadirRelacionIsA.setFont(theme.font());
			submenuAnadirRelacionIsA.setForeground(theme.fontColor());
			menuConceptual.add(submenuAnadirRelacionIsA);
			submenuAnadirRelacionIsA.setText(Lenguaje.text(Lenguaje.ADD_ISARELATION));
			submenuAnadirRelacionIsA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Point2D p = new Point2D.Double(coords[0],coords[1]);
				c.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, p);
				aumentaCoords();
				}
			});
			
			submenuAnadirAtributo = new JMenuItem();
			submenuAnadirAtributo.setFont(theme.font());
			submenuAnadirAtributo.setForeground(theme.fontColor());
			menuConceptual.add(submenuAnadirAtributo);
			submenuAnadirAtributo.setText(Lenguaje.text(Lenguaje.ADD_ATTRIBUTE));
			submenuAnadirAtributo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				listaTransfers = new Vector<Transfer>();
            	DAORelaciones daoRelaciones = new DAORelaciones(c.getPath());
            	listaTransfers.addAll(daoRelaciones.ListaDeRelaciones());
        		DAOEntidades daoEntidades = new DAOEntidades(c.getPath());
        		listaTransfers.addAll(daoEntidades.ListaDeEntidades());
        		DAOAtributos daoAtributos = new DAOAtributos(c);
        		listaTransfers.addAll(daoAtributos.ListaDeAtributos());
        		DAOAgregaciones daoAgregaciones = new DAOAgregaciones(c.getPath());
        		listaTransfers.addAll(daoAgregaciones.ListaDeAgregaciones());
				c.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarAtributo, listaTransfers);
				}
			});
			
			menuLogico = new JMenu();
			menuLogico.setForeground(theme.fontColor());
			menuLogico.setFont(theme.font());
			add(menuLogico);
			menuLogico.setText(Lenguaje.text(Lenguaje.LOGICO));	
			menuLogico.setMnemonic(Lenguaje.text(Lenguaje.LOGICO).charAt(0));
			
			submenuGenerarLogico = new JMenuItem();
			submenuGenerarLogico.setFont(theme.font());
			submenuGenerarLogico.setForeground(theme.fontColor());
			menuLogico.add(submenuGenerarLogico);
			submenuGenerarLogico.setText(Lenguaje.text(Lenguaje.GENERATE));
			submenuGenerarLogico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Thread(new Runnable(){
					public void run() {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_BotonGenerarModeloRelacional, null);
						c.getTheGUIPrincipal().getModeloText().goToTop();
					}
				}).start();
				}
			});
			
			submenuGuardarComoLogico = new JMenuItem();
			submenuGuardarComoLogico.setFont(theme.font());
			submenuGuardarComoLogico.setForeground(theme.fontColor());
			menuLogico.add(submenuGuardarComoLogico);
			submenuGuardarComoLogico.setText(Lenguaje.text(Lenguaje.SAVE_AS));
			submenuGuardarComoLogico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Thread hilo = new Thread(new Runnable(){
					public void run() {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_BotonGenerarArchivoModelo, c.getTheGUIPrincipal().getModeloText().getText());
					}
				});
				hilo.start();
				}
			});
			
			menuFisico = new JMenu();
			menuFisico.setForeground(theme.fontColor());
			menuFisico.setFont(theme.font());
			menuFisico.setToolTipText(Lenguaje.text(Lenguaje.BASEDATOS));
			add(menuFisico);
			menuFisico.setText(Lenguaje.text(Lenguaje.FISICO));	
			menuFisico.setMnemonic(Lenguaje.text(Lenguaje.FISICO).charAt(0));
			
			/*JComboBox cboSeleccionDBMS = new JComboBox();
			for (int i=0; i < c.getTheGUIPrincipal().getListaConexiones().size(); i++)
				cboSeleccionDBMS.insertItemAt(((TransferConexion) c.getTheGUIPrincipal().getListaConexiones().get(i)).getRuta(), ((TransferConexion) c.getTheGUIPrincipal().getListaConexiones().get(i)).getTipoConexion());
			
			cboSeleccionDBMS.setSelectedIndex(0);
			cboSeleccionDBMS.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					JComboBox cbo = (JComboBox) e.getSource();
					c.getTheGUIPrincipal().cambiarConexion((String)cbo.getSelectedItem());// Cambiar la conexionActual
				}
			});
			cboSeleccionDBMS.setMaximumSize(new Dimension(500,40));
			cboSeleccionDBMS.setFont(theme.font());
			cboSeleccionDBMS.setRenderer(new MyComboBoxRenderer());
			menuFisico.add(cboSeleccionDBMS);*/
			
			submenuGenerarFisico = new JMenuItem();
			submenuGenerarFisico.setFont(theme.font());
			submenuGenerarFisico.setForeground(theme.fontColor());
			menuFisico.add(submenuGenerarFisico);
			submenuGenerarFisico.setText(Lenguaje.text(Lenguaje.GENERATE));
			submenuGenerarFisico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Thread hilo = new Thread(new Runnable(){
					public void run() {
						c.getTheGUIPrincipal().getConexionActual().setDatabase("");
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_BotonGenerarScriptSQL, c.getTheGUIPrincipal().getConexionActual());
						
						// Restaurar el sistema
						c.getTheGUIPrincipal().getConexionActual().setDatabase("");
						c.getTheGUIPrincipal().getModeloText().goToTop();
					}
				});
				hilo.start();
				}
			});
			
			submenuGuardarComoFisico = new JMenuItem();
			submenuGuardarComoFisico.setFont(theme.font());
			submenuGuardarComoFisico.setForeground(theme.fontColor());
			menuFisico.add(submenuGuardarComoFisico);
			submenuGuardarComoFisico.setText(Lenguaje.text(Lenguaje.SAVE_AS));
			submenuGuardarComoFisico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Thread hilo = new Thread(new Runnable(){
					public void run() {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_BotonGenerarArchivoScriptSQL, c.getTheGUIPrincipal().getCodigoText().getText());
					}
				});
				hilo.start();
				}
			});
			
			submenuEjecutarFisico = new JMenuItem();
			submenuEjecutarFisico.setFont(theme.font());
			submenuEjecutarFisico.setForeground(theme.fontColor());
			menuFisico.add(submenuEjecutarFisico);
			submenuEjecutarFisico.setText(Lenguaje.text(Lenguaje.EXECUTE));
			submenuEjecutarFisico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//c.getTheGUIPrincipal().botonEjecutarEnDBMSActionPerformed(evt);
				Thread hilo = new Thread(new Runnable(){
					public void run() {
						// Comprobar si hay codigo
						if (!c.getTheGUIPrincipal().getScriptGeneradoCorrectamente()){
							JOptionPane.showMessageDialog(null,
								Lenguaje.text(Lenguaje.ERROR)+".\n" +
								Lenguaje.text(Lenguaje.MUST_GENERATE_SCRIPT_EX),
								Lenguaje.text(Lenguaje.DBCASE),
								JOptionPane.PLAIN_MESSAGE);
							return;
						}
						
						// Ejecutar en DBMS
						TransferConexion tc = new TransferConexion(
								c.getTheGUIPrincipal().getCBO().getSelectedIndex(),
								c.getTheGUIPrincipal().getCBO().getSelectedItem().toString());
						
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_BotonEjecutarEnDBMS, tc);
					}
				});
				hilo.start();
				}
			});
			
			menuDominios = new JMenu();
			menuDominios.setForeground(theme.fontColor());
			menuDominios.setFont(theme.font());
			add(menuDominios);
			menuDominios.setText(Lenguaje.text(Lenguaje.DOMINIOS));	
			menuDominios.setMnemonic(Lenguaje.text(Lenguaje.DOMINIOS).charAt(0));
			
			submenuCrearDominio = new JMenuItem();
			submenuCrearDominio.setFont(theme.font());
			submenuCrearDominio.setForeground(theme.fontColor());
			menuDominios.add(submenuCrearDominio);
			submenuCrearDominio.setText(Lenguaje.text(Lenguaje.CREAR_DOMINIO));
			submenuCrearDominio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					c.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_CrearDominio, 0);				}
			});
			
			//Ayuda
			menuAyuda = new JMenu();
			menuAyuda.setForeground(theme.fontColor());
			menuAyuda.setFont(theme.font());
			add(menuAyuda);
			menuAyuda.setText(Lenguaje.text(Lenguaje.HELP));
			menuAyuda.setMnemonic(Lenguaje.text(Lenguaje.HELP).charAt(0));
			
				//Ayuda/acerca de
				submenuAcercaDe = new JMenuItem();
				submenuAcercaDe.setFont(theme.font());
				submenuAcercaDe.setForeground(theme.fontColor());
				menuAyuda.add(submenuAcercaDe);
				submenuAcercaDe.setText(Lenguaje.text(Lenguaje.ABOUT));
				submenuAcercaDe.setMnemonic(Lenguaje.text(Lenguaje.ABOUT).charAt(0));
				submenuAcercaDe.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_ABOUT, null);
					}
				});
				
				//Ayuda/Manual
				submenuManual = new JMenuItem();
				submenuManual.setFont(theme.font());
				submenuManual.setForeground(theme.fontColor());
				menuAyuda.add(submenuManual);
				submenuManual.setText(Lenguaje.text(Lenguaje.MANUAL));
				submenuManual.setMnemonic(Lenguaje.text(Lenguaje.MANUAL).charAt(0));
				submenuManual.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_MANUAL, null);
					}
				});
				
				//Ayuda/Reportar incidencia
				submenuReportarIncidencia= new JMenuItem();
				submenuReportarIncidencia.setFont(theme.font());
				submenuReportarIncidencia.setForeground(theme.fontColor());
				menuAyuda.add(submenuReportarIncidencia);
				submenuReportarIncidencia.setText(Lenguaje.text(Lenguaje.REPORT));
				submenuReportarIncidencia.setMnemonic(Lenguaje.text(Lenguaje.REPORT).charAt(0));
				submenuReportarIncidencia.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_REPORT, null);
						
					}
				});
				
				//Ayuda/Galeria
				/*submenuGaleria = new JMenuItem();
				submenuGaleria.setFont(theme.font());
				submenuGaleria.setForeground(theme.fontColor());
				menuAyuda.add(submenuGaleria);
				submenuGaleria.setText(Lenguaje.text(Lenguaje.GALERIA));
				submenuGaleria.setMnemonic(Lenguaje.text(Lenguaje.GALERIA).charAt(0));
				submenuGaleria.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_GALERIA, null);
					}
				});*/
			
			//botones vistas
			JToolBar iconosPerspectiva = new JToolBar();
			diagramIcon = new diagramIcon(false);
			IconLabel diagramLabel = new IconLabel(diagramIcon);
			diagramLabel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	            	c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_ModoDiseno, null);
	            }
	        });
			allIcon = new allIcon(false);
			IconLabel allLabel = new IconLabel(allIcon);
			allLabel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	            	c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_ModoVerTodo, null);
	            }
	        });
			codeIcon = new codeIcon(false);
			IconLabel codeLabel = new IconLabel(codeIcon);
			codeLabel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	            	c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_Click_ModoProgramador, null);
	            }
	        });
			//Boton deshacer
			deshacer=new JButton();
			Dimension d= new Dimension(30,30);
			deshacer.setPreferredSize(d);
			try {
			    Image img = ImageIO.read(getClass().getResource("/vista/imagenes/undoIcon.png"));
			    deshacer.setIcon(new ImageIcon(img));
			  } catch (Exception ex) {
			    System.out.println(ex);
			  }
			deshacer.setForeground(theme.fontColor());
			if(c.getContFicherosDeshacer()==1) deshacer.setBackground(Color.GRAY);
			else deshacer.setBackground(Color.WHITE);
			add(deshacer);
			deshacer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_DESHACER, null);
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_DESHACER2, null);
					deshacer.transferFocus();
				}
			});
			
			//Boton rehacer
			rehacer=new JButton();
			rehacer.setPreferredSize(d);
			try {
			    Image img = ImageIO.read(getClass().getResource("/vista/imagenes/redoIcon.png"));
			    rehacer.setIcon(new ImageIcon(img));
			  } catch (Exception ex) {
			    System.out.println(ex);
			  }
			rehacer.setForeground(theme.fontColor());
			if(c.getContFicherosDeshacer()==c.getLimiteFicherosDeshacer() || c.getAuxDeshacer() == true) rehacer.setBackground(Color.GRAY);
			else rehacer.setBackground(Color.WHITE);
			add(rehacer);
			rehacer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_DESHACER, null);
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_REHACER, null);
					rehacer.transferFocus();
				}
			});
			
			//Boton reset
			/*reset=new JButton();
			reset.setPreferredSize(d);
			try {
			    Image img = ImageIO.read(getClass().getResource("/vista/imagenes/Reset.png"));
			    reset.setIcon(new ImageIcon(img));
			  } catch (Exception ex) {
			    System.out.println(ex);
			  }
			//reset.setForeground(theme.fontColor());
			reset.setToolTipText("Reset");
			add(reset);
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					c.mensajeDesde_GUIPrincipal(TC.GUI_Principal_RESET, null);
					
				}
			});*/
			
			KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_DOWN_MASK);
			deshacer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlZ, "Deshacer");
			deshacer.getActionMap().put("Deshacer", new AbstractAction() {
	            public void actionPerformed(ActionEvent e) {
	            	deshacer.doClick();
					deshacer.transferFocus();
	            }
			});
			
			KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y,InputEvent.CTRL_DOWN_MASK);
			rehacer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlY, "Rehacer");
			rehacer.getActionMap().put("Rehacer", new AbstractAction() {
	            public void actionPerformed(ActionEvent e) {
	            	rehacer.doClick();
					rehacer.transferFocus();
	            }
			});
			
			iconosPerspectiva.add(Box.createRigidArea(new Dimension(4,0)));
			iconosPerspectiva.add(diagramLabel);
			iconosPerspectiva.add(Box.createRigidArea(new Dimension(14,0)));
			iconosPerspectiva.add(allLabel);
			iconosPerspectiva.add(Box.createRigidArea(new Dimension(14,0)));
			iconosPerspectiva.add(codeLabel);
			iconosPerspectiva.add(Box.createRigidArea(new Dimension(4,0)));
			iconosPerspectiva.setBounds(0, 0, 60, 80);
			iconosPerspectiva.setFloatable(false);
			iconosPerspectiva.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); 
			add(iconosPerspectiva);
			add(Box.createRigidArea(new Dimension(50,0)));
			
			
			
	}
	
	/******************
	 * Listeners
	 ******************/
	private void exportarJPG(ActionEvent evt) {
		MyFileChooser fileChooser = new MyFileChooser();
		fileChooser.setDialogTitle(Lenguaje.text(Lenguaje.DBCASE));
		fileChooser.setFileFilter(new FileNameExtensionFilter(Lenguaje.text(Lenguaje.JPEG_FILES), "jpg"));
		int resul = fileChooser.showSaveDialog(null);
		if (resul == 0){
			File ruta = fileChooser.getSelectedFile();
			JOptionPane.showMessageDialog(
				null,
				Lenguaje.text(Lenguaje.INFO)+"\n"+
				Lenguaje.text(Lenguaje.OK_EXPORT)+".\n" +
				Lenguaje.text(Lenguaje.FILE)+": "+ruta,
				Lenguaje.text(Lenguaje.DBCASE),
				JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	/************
	 * Modifiers
	 ************/
	public void setModoVista(int m) {
		diagramIcon.setSelected(m==1);
		allIcon.setSelected(m==0);
		codeIcon.setSelected(m==2);
		this.repaint();
	}
	
	private void aumentaCoords() {
		coords[0]=coords[0]<diagramWidth?coords[0]+150:70;
		coords[1]=coords[0]==70?coords[1]-70:coords[1];
	}

	
	public JButton getDeshacer() {
		return this.deshacer;
	}
	
	public JButton getRehacer() {
		return this.rehacer;
	}
	
	
}