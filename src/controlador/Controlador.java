package controlador;


import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import org.w3c.dom.Document;

import modelo.servicios.ServiciosAtributos;
import modelo.servicios.ServiciosDominios;
import modelo.servicios.ServiciosEntidades;
import modelo.servicios.ServiciosRelaciones;
import modelo.servicios.ServiciosReporte;
import modelo.servicios.GeneradorEsquema;
import modelo.servicios.ServiciosAgregaciones;
import vista.frames.GUI_Pregunta;
import modelo.transfers.Transfer;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferConexion;
import modelo.transfers.TransferDominio;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;
import persistencia.EntidadYAridad;
import vista.GUIPrincipal;
import vista.componentes.ArchivosRecientes;
import vista.frames.GUI_About;
import vista.frames.GUI_AnadirAgregacion;
import vista.frames.GUI_Manual;
import vista.frames.GUI_AnadirAtributo;
import vista.frames.GUI_AnadirAtributoEntidad;
import vista.frames.GUI_AnadirAtributoRelacion;
import vista.frames.GUI_AnadirEntidadARelacion;
import vista.frames.GUI_AnadirEntidadHija;
import vista.frames.GUI_AnadirSubAtributoAtributo;
import vista.frames.GUI_Conexion;
import vista.frames.GUI_EditarCardinalidadEntidad;
import vista.frames.GUI_EditarDominioAtributo;
import vista.frames.GUI_Eliminar;
import vista.frames.GUI_EstablecerEntidadPadre;
import vista.frames.GUI_Galeria;
import vista.frames.GUI_InsertarDominio;
import vista.frames.GUI_InsertarEntidad;
import vista.frames.GUI_InsertarRelacion;
import vista.frames.GUI_InsertarRestriccionAAtributo;
import vista.frames.GUI_InsertarRestriccionAEntidad;
import vista.frames.GUI_InsertarRestriccionARelacion;
import vista.frames.GUI_ModificarAtributo;
import vista.frames.GUI_ModificarDominio;
import vista.frames.GUI_ModificarEntidad;
import vista.frames.GUI_ModificarRelacion;
import vista.frames.GUI_QuitarEntidadARelacion;
import vista.frames.GUI_QuitarEntidadHija;
import vista.frames.GUI_QuitarEntidadPadre;
import vista.frames.GUI_Recientes;
import vista.frames.GUI_RenombrarAgregacion;
import vista.frames.GUI_RenombrarAtributo;
import vista.frames.GUI_RenombrarDominio;
import vista.frames.GUI_RenombrarEntidad;
import vista.frames.GUI_RenombrarRelacion;
import vista.frames.GUI_Report;
import vista.frames.GUI_SeleccionarConexion;
import vista.frames.GUI_TablaUniqueEntidad;
import vista.frames.GUI_TablaUniqueRelacion;
import vista.frames.GUI_Zoom;
import vista.lenguaje.Lenguaje;
import vista.frames.GUI_SaveAs;
import vista.tema.Theme;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Controlador {
	// GUIs
	private GUIPrincipal theGUIPrincipal;
	private GUI_SaveAs theGUIWorkSpace;
	private GUI_Recientes theGUIRecientes;
	// Fuera
	private GUI_ModificarEntidad theGUIModificarEntidad;
	private GUI_ModificarRelacion theGUIModificarRelacion;
	private GUI_ModificarAtributo theGUIModificarAtributo;
	private GUI_InsertarEntidad theGUIInsertarEntidad;
	private GUI_InsertarRelacion theGUIInsertarRelacion;
	private GUI_InsertarDominio theGUIInsertarDominio;
	private GUI_Conexion theGUIConexion;
	private GUI_SeleccionarConexion theGUISeleccionarConexion;
	// Entidades
	private GUI_RenombrarEntidad theGUIRenombrarEntidad;
	private GUI_AnadirAtributoEntidad theGUIAnadirAtributoEntidad;
	private GUI_InsertarRestriccionAEntidad theGUIAnadirRestriccionAEntidad;
	private GUI_TablaUniqueEntidad theGUITablaUniqueEntidad;
	// Atributos
	private GUI_RenombrarAtributo theGUIRenombrarAtributo;
	private GUI_EditarDominioAtributo theGUIEditarDominioAtributo;
	private GUI_AnadirSubAtributoAtributo theGUIAnadirSubAtributoAtributo;
	private GUI_InsertarRestriccionAAtributo theGUIAnadirRestriccionAAtributo;
	// Relaciones IsA
	private GUI_EstablecerEntidadPadre theGUIEstablecerEntidadPadre;
	private GUI_QuitarEntidadPadre theGUIQuitarEntidadPadre;
	private GUI_AnadirEntidadHija theGUIAnadirEntidadHija;
	private GUI_QuitarEntidadHija theGUIQuitarEntidadHija;
	// Relaciones Normales
	private GUI_RenombrarRelacion theGUIRenombrarRelacion;
	private GUI_AnadirAtributoRelacion theGUIAnadirAtributoRelacion;
	private GUI_AnadirAtributo theGUIAnadirAtributo;
	private GUI_Eliminar theGUIEliminar;
	private GUI_AnadirEntidadARelacion theGUIAnadirEntidadARelacion;
	private GUI_QuitarEntidadARelacion theGUIQuitarEntidadARelacion;
	private GUI_EditarCardinalidadEntidad theGUIEditarCardinalidadEntidad;
	private GUI_InsertarRestriccionARelacion theGUIAnadirRestriccionARelacion;
	private GUI_TablaUniqueRelacion theGUITablaUniqueRelacion;
	//Agregaciones
	private GUI_RenombrarAgregacion theGUIModificarAgregacion;
	private GUI_AnadirAgregacion theGUIAddAgregacion;
	// Dominios
	private GUI_RenombrarDominio theGUIRenombrarDominio;
	private GUI_ModificarDominio theGUIModificarElementosDominio;
	//About
	private GUI_About about;
	//Recientes
	private static ArchivosRecientes archivosRecent = new ArchivosRecientes();
	//report
	private GUI_Report report;
	//manual
	private GUI_Zoom zoom;
	private static int valorZoom;
	//manual
	private GUI_Manual manual;
	//galeria
	private GUI_Galeria galeria;
	// Servicios
	private ServiciosEntidades theServiciosEntidades;
	private ServiciosAtributos theServiciosAtributos;
	private ServiciosRelaciones theServiciosRelaciones;
	private ServiciosDominios theServiciosDominios;
	private GeneradorEsquema theServiciosSistema;
	private ServiciosReporte theServiciosReporte;
	private ServiciosAgregaciones theServiciosAgregaciones;

	//Otros
	private String path;
	private Vector<TransferAtributo> listaAtributos;
	private boolean cambios;
	private boolean nullAttrs;
	private boolean confirmarEliminaciones;
	private boolean cuadricula;
	private boolean ocultarConceptual;
	private boolean ocultarLogico;
	private boolean ocultarFisico;
	private boolean ocultarDominios;
	private File filetemp;
	private File fileguardar;
	private GUI_Pregunta panelOpciones;
	private Theme theme;
	private int modoVista;
	private static Stack<Document> pilaDeshacer;
	private Vector<TransferEntidad> listaEntidades;
	private Vector<TransferRelacion> listaRelaciones;

	//private Vector<TransferAgregacion> listaAgregaciones; por el momento no parece necesario 

	private boolean modoSoporte;
	
	//Para boton Deshacer solo afecta a acciones con elementos
	private TC ultimoMensaje;
	private Object ultimosDatos;
	//private TransferEntidad auxTransferEntidad;
	private Vector auxTransferAtributos;
	private Point2D posAux;
	private String antigoNombreAtributo;
	private String antiguoDominioAtributo;
	private boolean antiguoCompuestoAtribuo;
	private boolean antiguoMultivaloradoAtribuo;
	private boolean antiguoNotnullAtribuo;
	private boolean antiguoUniqueAtribuo;
	private boolean antiguoClavePrimaria;
	//private int idPadreAntigua;
	private Vector<TransferEntidad> hijosAntiguo;
	private Vector<TransferEntidad> entidadesAntiguo;
	private TransferEntidad padreAntiguo;
	private TransferRelacion antiguaIsA;
	private Vector<TransferAtributo> antiguosAtributosRel;
	private Vector<TransferEntidad> antiguasEntidadesRel;
	private Vector<TransferAtributo> antiguosSubatributos;
	
	private Transfer copiado;
	
	private long tiempoGuardado = System.currentTimeMillis()/1000;//ultima vez que se guardo el documento en milisegudos
	
	private int contFicherosDeshacer = 0;
	private int limiteFicherosDeshacer = 0;
	private boolean auxDeshacer = false;
	
	public Controlador() {
		iniciaFrames();
		cambios = false;
		// GUIPrincipal
		theGUIPrincipal = new GUIPrincipal();
		theGUIPrincipal.setControlador(this);
		theme = Theme.getInstancia();
		pilaDeshacer = new Stack<Document>();
		setListaEntidades(new Vector<TransferEntidad>());
		setListaRelaciones(new Vector<TransferRelacion>());
		modoSoporte=false;
		cuadricula=false;
		//valorZoom=0;
	}
	
	public static void main(String[] args)throws Exception {
		
		for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) 
            if ("Nimbus".equals(info.getName())) {
            	try { javax.swing.UIManager.setLookAndFeel(info.getClassName());} 
            	catch (ClassNotFoundException | InstantiationException | IllegalAccessException| UnsupportedLookAndFeelException e) {e.printStackTrace();}
            break;
        }
		//creamos la carpeta projects si no existe
		File directory = new File(System.getProperty("user.dir")+"/projects");
	    if (!directory.exists()) directory.mkdir();
	        
	    //Creamos la carpeta incidences si no existe
	    File incidences=new File(System.getProperty("user.dir")+"/incidences");
	    if (!incidences.exists()) incidences.mkdir();
	    
		// Obtenemos configuración inicial (si la hay)
		ConfiguradorInicial conf = new ConfiguradorInicial();
		conf.leerFicheroConfiguracion();
		
		// Obtenemos el lenguaje en el que vamos a trabajar
		
		Lenguaje.encuentraLenguajes();//AQUI 
		Theme.loadThemes();
		Theme.changeTheme(conf.obtenTema());
		//valorZoom=conf.obtenZoom();
		
		
		if (conf.existeFichero()){
			archivosRecent.recibeRecientes(conf.darRecientes());
			Vector<String> lengs = Lenguaje.obtenLenguajesDisponibles();
			boolean encontrado = false;
			int k = 0;
			while (!encontrado && k < lengs.size()){
				encontrado = lengs.get(k).equalsIgnoreCase(conf.obtenLenguaje());
				k++;
			}
			
			if (encontrado) Lenguaje.cargaLenguaje(conf.obtenLenguaje());
			else Lenguaje.cargaLenguajePorDefecto();
			
		}else{
			Lenguaje.cargaLenguajePorDefecto();
			
			Theme.loadDefaultTheme();
		}
		Controlador controlador = new Controlador();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
        			controlador.setFiletemp(File.createTempFile("dbcase", "xml"));
        			creaFicheroXML(controlador.getFiletemp());
        		} catch (IOException e) {
        			JOptionPane.showMessageDialog(null,
        				Lenguaje.text(Lenguaje.ERROR_TEMP_FILE),
        				Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
        		}
        		String ruta = controlador.getFiletemp().getPath();
        		controlador.setPath(ruta);
        		controlador.setNullAttrs(conf.obtenNullAttr());
        		
        		// Abrimos el documento guardado últimamente
        		File ultimo = new File(conf.obtenUltimoProyecto());
        		if (ultimo.exists()){
        			archivosRecent.add(ultimo);
        			//controlador.setZoom(0);
        			controlador.setFileguardar(ultimo);
        			controlador.setModoVista(conf.obtenModoVista());
        			controlador.setZoom(conf.obtenZoom());
        			controlador.setNullAttrs(conf.obtenNullAttr());
        			String abrirPath =conf.obtenUltimoProyecto();
        			String tempPath =controlador.filetemp.getAbsolutePath();
        			FileCopy(abrirPath, tempPath);
        			
        			// Reinicializamos la GUIPrincipal
        			controlador.getTheGUIPrincipal().setActiva(controlador.getModoVista());
        			// Reiniciamos los datos de los servicios de sistema
        			controlador.getTheServiciosSistema().reset();
        			controlador.setCambios(false);
        			controlador.getTheGUIPrincipal().loadInfo();
        			controlador.getTheGUIPrincipal().cambiarZoom(valorZoom);
        			
        		}else {
        			controlador.setModoVista(0);
        			controlador.getTheGUIPrincipal().setActiva(controlador.getModoVista());
        		}
        		// Establecemos la base de datos por defecto
        		if (conf.existeFichero())
        			controlador.getTheGUIPrincipal().cambiarConexion(conf.obtenGestorBBDD());
        		
        		if (ultimo.exists()){
        			//Almacenamos nuestro primer fichero en la carpeta usada para la tarea deshacer
           		 	File directorio = new File(System.getProperty("user.dir")+"/deshacer");
           		 	if (!directorio.exists()) {
           		 		if (directorio.mkdirs()) {
           		 			// System.out.println("Directorio creado");
           		 		} 
           		 		else {
           		 			System.out.println("Error al crear directorio");
           		 		}
           		 	}
           		 	else {
           		 		for (File file: Objects.requireNonNull(directorio.listFiles())) {
           		 			if (!file.isDirectory()) {
           		 				file.delete();
        		 			}
        		 		}
        		 	}
           		 	controlador.guardarDeshacer();
        		}
        		
        		else{
        			//Almacenamos nuestro primer fichero en la carpeta usada para la tarea deshacer
        			File temp = new File(System.getProperty("user.dir")+"/projects/temp");
        			controlador.setFileguardar(temp);
           		 	File directorio = new File(System.getProperty("user.dir")+"/deshacer");
           		 	if (!directorio.exists()) {
           		 		if (directorio.mkdirs()) {
           		 			// System.out.println("Directorio creado");
           		 		} 
           		 		else {
           		 			System.out.println("Error al crear directorio");
           		 		}
           		 	}
           		 	else {
           		 		for (File file: Objects.requireNonNull(directorio.listFiles())) {
           		 			if (!file.isDirectory()) {
           		 				file.delete();
           		 			}
           		 		}
           		 	}
           		 	controlador.guardarDeshacer();
        		}
        		
        		
            }
        });
	}
	
	public static boolean creaFicheroXML(File f){
		FileWriter fw;
		String ruta = f.getPath();
		try {
			fw = new FileWriter(ruta);
			fw.write("<?xml version=" + '\"' + "1.0" + '\"' + " encoding="
					+ '\"' + "utf-8" + '\"' + " ?>" + '\n');
			//"ISO-8859-1"
			fw.write("<Inf_dbcase>" + "\n");
			fw.write("<EntityList proximoID=\"1\">" + "\n" + "</EntityList>"+ "\n");
			fw.write("<RelationList proximoID=\"1\">" + "\n" + "</RelationList>"+ "\n");
			fw.write("<AttributeList proximoID=\"1\">" + "\n" + "</AttributeList>"+ "\n");
			fw.write("<DomainList proximoID=\"1\">" + "\n" + "</DomainList>");
			fw.write("<AggregationList proximoID=\"1\">" + "\n" + "</AggregationList>");
			fw.write("</Inf_dbcase>" +"\n");
			fw.close();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					Lenguaje.text(Lenguaje.ERROR_CREATING_FILE) + "\n" +ruta,
					Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
			return false;
		}		
	}
	private void iniciaFrames() {
		// Creamos todos los servicios y les asignamos el controlador
		theServiciosEntidades = new ServiciosEntidades(); 
		theServiciosEntidades.setControlador(this);		
		theServiciosAtributos = new ServiciosAtributos();
		theServiciosAtributos.setControlador(this);
		theServiciosRelaciones = new ServiciosRelaciones();
		theServiciosRelaciones.setControlador(this);
		theServiciosDominios = new ServiciosDominios();
		theServiciosDominios.setControlador(this);
		theServiciosSistema = new GeneradorEsquema();
		theServiciosSistema.reset();
		theServiciosSistema.setControlador(this);
		theServiciosReporte=new ServiciosReporte();
		theServiciosReporte.setControlador(this);
		theServiciosAgregaciones = new ServiciosAgregaciones();
		theServiciosAgregaciones.setControlador(this);
		// Fuera
		theGUIInsertarEntidad = new GUI_InsertarEntidad();
		theGUIInsertarEntidad.setControlador(this);
		theGUIInsertarRelacion = new GUI_InsertarRelacion();
		theGUIInsertarRelacion.setControlador(this);
		theGUIInsertarDominio = new GUI_InsertarDominio();
		theGUIInsertarDominio.setControlador(this);
		theGUIConexion = new GUI_Conexion();
		theGUIConexion.setControlador(this);
		theGUISeleccionarConexion = new GUI_SeleccionarConexion();
		theGUISeleccionarConexion.setControlador(this);
		theGUIEliminar =new GUI_Eliminar();
		theGUIEliminar.setControlador(this);

		// Entidades
		theGUIRenombrarEntidad = new GUI_RenombrarEntidad();
		theGUIRenombrarEntidad.setControlador(this);
		theGUIAnadirAtributoEntidad = new GUI_AnadirAtributoEntidad();
		theGUIAnadirAtributoEntidad.setControlador(this);
		theGUIAnadirRestriccionAEntidad = new GUI_InsertarRestriccionAEntidad();
		theGUIAnadirRestriccionAEntidad.setControlador(this);
		theGUIAnadirAtributo = new GUI_AnadirAtributo();
		theGUIAnadirAtributo.setControlador(this);
		theGUIModificarEntidad=new GUI_ModificarEntidad();
		theGUIModificarEntidad.setControlador(this);

		// Atributos
		theGUIRenombrarAtributo = new GUI_RenombrarAtributo();
		theGUIRenombrarAtributo.setControlador(this);
		theGUIEditarDominioAtributo = new GUI_EditarDominioAtributo();
		theGUIEditarDominioAtributo.setControlador(this);
		theGUIAnadirSubAtributoAtributo = new GUI_AnadirSubAtributoAtributo();
		theGUIAnadirSubAtributoAtributo.setControlador(this);
		theGUIAnadirRestriccionAAtributo = new GUI_InsertarRestriccionAAtributo();
		theGUIAnadirRestriccionAAtributo.setControlador(this);

		// Relaciones IsA
		theGUIEstablecerEntidadPadre = new GUI_EstablecerEntidadPadre();
		theGUIEstablecerEntidadPadre.setControlador(this);
		theGUIQuitarEntidadPadre = new GUI_QuitarEntidadPadre();
		theGUIQuitarEntidadPadre.setControlador(this);
		theGUIAnadirEntidadHija = new GUI_AnadirEntidadHija();
		theGUIAnadirEntidadHija.setControlador(this);
		theGUIQuitarEntidadHija = new GUI_QuitarEntidadHija();
		theGUIQuitarEntidadHija.setControlador(this);

		// Relaciones Normales
		theGUIRenombrarRelacion = new GUI_RenombrarRelacion();
		theGUIRenombrarRelacion.setControlador(this);
		theGUIAnadirEntidadARelacion = new GUI_AnadirEntidadARelacion();
		theGUIAnadirEntidadARelacion.setControlador(this);
		theGUIQuitarEntidadARelacion = new GUI_QuitarEntidadARelacion();
		theGUIQuitarEntidadARelacion.setControlador(this);
		theGUIEditarCardinalidadEntidad = new GUI_EditarCardinalidadEntidad();
		theGUIEditarCardinalidadEntidad.setControlador(this);
		theGUIAnadirAtributoRelacion = new GUI_AnadirAtributoRelacion();
		theGUIAnadirAtributoRelacion.setControlador(this);
		theGUIAnadirRestriccionARelacion = new GUI_InsertarRestriccionARelacion();
		theGUIAnadirRestriccionARelacion.setControlador(this);
		theGUIModificarRelacion= new GUI_ModificarRelacion();
		theGUIModificarRelacion.setControlador(this);
		theGUIModificarAtributo= new GUI_ModificarAtributo();
		theGUIModificarAtributo.setControlador(this);
		// Dominios
		theGUIRenombrarDominio = new GUI_RenombrarDominio();
		theGUIRenombrarDominio.setControlador(this);
		theGUIModificarElementosDominio = new GUI_ModificarDominio();
		theGUIModificarElementosDominio.setControlador(this);
		//Agregaciones
		theGUIModificarAgregacion = new GUI_RenombrarAgregacion();
		theGUIModificarAgregacion.setControlador(this);
		theGUIAddAgregacion = new GUI_AnadirAgregacion();
		theGUIAddAgregacion.setControlador(this);
		
		// Otras
		about = new GUI_About();
		manual = new GUI_Manual();
		galeria = new GUI_Galeria();
		theGUIWorkSpace = new GUI_SaveAs(true);
		theGUIWorkSpace.setControlador(this);
		panelOpciones= new GUI_Pregunta();
		report=new GUI_Report();
		report.setControlador(this);
		zoom=new GUI_Zoom();
		zoom.setControlador(this);
	}
		
	// Mensajes que le manda la GUI_WorkSpace al Controlador
	public void mensajeDesde_GUIWorkSpace(TC mensaje, Object datos){
		switch (mensaje){
			case GUI_WorkSpace_Recent:{
				archivosRecent.add((File)datos);
				break;
			}
			case GUI_WorkSpace_Nuevo:{
				this.setPath((String)datos);
				SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
						getTheServiciosSistema().reset();
						theGUIPrincipal.loadInfo();
						getTheGUIPrincipal().reiniciar();
		        }});
				setCambios(false);
				File temp = new File(System.getProperty("user.dir")+"/projects/temp");
				this.setFileguardar(temp);
				File directory = new File(System.getProperty("user.dir")+"/deshacer");
				if (directory.exists()) {
			    	for (File file: Objects.requireNonNull(directory.listFiles())) {
			             if (!file.isDirectory()) {
			                file.delete();
			             }
			         }
				}
       		 	
				this.contFicherosDeshacer = 0;
				this.limiteFicherosDeshacer = 0;
				this.auxDeshacer = false;
				this.guardarDeshacer();
				this.tiempoGuardado = System.currentTimeMillis()/1000;
				break;
			}
			case GUI_WorkSpace_Click_Abrir:{
				this.contFicherosDeshacer = 0;
				this.limiteFicherosDeshacer = 0;
				this.auxDeshacer = false;
				String abrirPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(abrirPath, tempPath);
				SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
						getTheServiciosSistema().reset();
						theGUIPrincipal.loadInfo();
						getTheGUIPrincipal().reiniciar();
		        }});
				setCambios(false);
				File directory = new File(System.getProperty("user.dir")+"/deshacer");
				if (directory.exists()) {
			    	for (File file: Objects.requireNonNull(directory.listFiles())) {
			             if (!file.isDirectory()) {
			                file.delete();
			             }
			         }
				}
				this.guardarDeshacer();
				this.tiempoGuardado = System.currentTimeMillis()/1000;
				break;
			}
			
			case GUI_WorkSpace_Click_Abrir_Lenguaje:{
				String abrirPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(abrirPath, tempPath);
				SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
						getTheServiciosSistema().reset();
						theGUIPrincipal.loadInfo();
						getTheGUIPrincipal().reiniciar();
		        }});
				setCambios(false);
				break;
			}
			
			case GUI_WorkSpace_Click_Abrir_Tema:{
				String abrirPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(abrirPath, tempPath);
				SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
						getTheServiciosSistema().reset();
						theGUIPrincipal.loadInfo();
						getTheGUIPrincipal().reiniciar();
		        }});
				setCambios(false);
				break;
			}
			
			case GUI_WorkSpace_Click_Abrir_Deshacer:{//tenemos que diferenciar si abrimos un nuevo proyecto o el de deshacer
				String abrirPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(abrirPath, tempPath);
				SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
						getTheServiciosSistema().reset();
						theGUIPrincipal.loadInfo();
						getTheGUIPrincipal().reiniciar();
		        }});
				//setCambios(false);
				//this.getTheGUIPrincipal().getPanelDiseno().grabFocus();
				break;
			}
			
			case GUI_WorkSpace_Click_Guardar:{
				String guardarPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(tempPath, guardarPath);
				this.getTheGUIWorkSpace().setInactiva();
				setCambios(false);
				this.tiempoGuardado = System.currentTimeMillis()/1000;
				if (this.fileguardar.getPath() != (String)datos) {
					File directory = new File(System.getProperty("user.dir")+"/deshacer");
					if (directory.exists()) {
						for (File file: Objects.requireNonNull(directory.listFiles())) {
				            if (!file.isDirectory()) {
				                file.delete();
				            }
				        }
					}
					File temp = new File(guardarPath);
	    			this.setFileguardar(temp);
					this.contFicherosDeshacer = 0;
					this.limiteFicherosDeshacer = 0;
					this.guardarDeshacer();
				}
				break;
			}
			
			case GUI_WorkSpace_Click_Guardar_Backup:{
				String guardarPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(tempPath, guardarPath);
				this.getTheGUIWorkSpace().setInactiva();
				setCambios(false);
				//this.tiempoGuardado = System.currentTimeMillis()/1000;
				if (this.fileguardar.getPath() != (String)datos) {
					File directory = new File(System.getProperty("user.dir")+"/deshacer");
					if (directory.exists()) {
						for (File file: Objects.requireNonNull(directory.listFiles())) {
				            if (!file.isDirectory()) {
				                file.delete();
				            }
				        }
					}
					File temp = new File(guardarPath);
	    			//this.setFileguardar(temp);
					//this.contFicherosDeshacer = 0;
					//this.limiteFicherosDeshacer = 0;
					//this.guardarDeshacer();
				}
				break;
			}
			
			case GUI_WorkSpace_Click_GuardarDeshacer:{
				String guardarPath =(String)datos;
				String tempPath =this.filetemp.getAbsolutePath();
				FileCopy(tempPath, guardarPath);
				this.getTheGUIWorkSpace().setInactiva();
				setCambios(false);
				//this.tiempoGuardado = System.currentTimeMillis()/1000;
				break;
			}
			
			case GUI_WorkSpace_ERROR_CreacionFicherosXML:{
				JOptionPane.showMessageDialog(null,Lenguaje.text(Lenguaje.INITIAL_ERROR)+"\n" +
						Lenguaje.text(Lenguaje.OF_XMLFILES)+"\n"+this.getPath(),Lenguaje.text(Lenguaje.DBCASE),JOptionPane.ERROR_MESSAGE);
				break;
			}
			default: break;
		}// Switch
	}


	// Mensajes que manda el Panel de Diseño al Controlador
	public void mensajeDesde_PanelDiseno(TC mensaje, Object datos){
		
		 long tiempoActual = System.currentTimeMillis()/1000;
		 long ti = (tiempoActual - this.tiempoGuardado);
		 //System.out.println(ti);// lo que ha transcurrido en segundos desde la ultima ve que se guardo
		 if (cambios && ti > 600) // si ha pasado mas de media hora
			 this.guardarBackup();
		
		switch(mensaje){
		case PanelDiseno_Click_AddAgregacion:{
			TransferRelacion rel = (TransferRelacion) datos;
			this.getTheGUIAddAgregacion().setRelacion(rel);
			this.getTheGUIAddAgregacion().setActiva();
			break;
		}
		case PanelDiseno_Click_EditarAgregacion:{
			TransferRelacion rel = (TransferRelacion) datos;
			this.getTheGUIRenombrarAgregacion().setRelacion(rel);
			this.getTheGUIRenombrarAgregacion().setActiva();
			break;
		}
		case PanelDiseno_Pertenece_A_Agregacion:{
			Vector v = (Vector) datos;
			TransferRelacion rel = (TransferRelacion) v.get(0);
			boolean perteneceAgregacion = this.getTheServiciosAgregaciones().perteneceAgregacion(rel);
			v.add(perteneceAgregacion);
			break;
		}
		case PanelDiseno_Click_EliminarAgregacion:{
			Transfer t = (Transfer)datos;
			if(t instanceof TransferAgregacion) {
				TransferAgregacion agre = (TransferAgregacion) datos;
				this.getTheServiciosAgregaciones().eliminarAgregacion(agre);
			}
			else if(t instanceof TransferRelacion) {
				TransferRelacion rel = (TransferRelacion) datos;
				this.getTheServiciosAgregaciones().eliminarAgregacion(rel);
			}
			
			break;
		}
		case PanelDiseno_Click_InsertarEntidad:{
			Point2D punto = (Point2D) datos;
			this.getTheGUIInsertarEntidad().setPosicionEntidad(punto);
			this.getTheGUIInsertarEntidad().setActiva();
			break;
		}
		case PanelDiseno_Click_InsertarAtributo:{
			Vector<Transfer> listaTransfers = (Vector<Transfer>) datos;
			//removemos IsA
			Iterator<Transfer> itr = listaTransfers.iterator();
			while(itr.hasNext()) {
				Transfer t = itr.next();
				if(t instanceof TransferRelacion) { 
					if(((TransferRelacion) t).isIsA())
						itr.remove();
				}else if(t instanceof TransferAtributo)
					if(!((TransferAtributo) t).getCompuesto())
						itr.remove();
			}
			if(listaTransfers.isEmpty())
				JOptionPane.showMessageDialog(null,
					"ERROR.\nAdd an entity, a relation or an aggregation first\n",
					Lenguaje.text(Lenguaje.ADD_ATTRIBUTE),
					JOptionPane.PLAIN_MESSAGE);
			else {
				this.getTheGUIAnadirAtributo().setListaTransfers(listaTransfers);
				this.getTheGUIAnadirAtributo().setActiva();
			}
			break;
		}
		
		case PanelDiseno_Click_Eliminar:{
			Vector<Transfer> listaTransfers = (Vector<Transfer>) datos;
			if(listaTransfers.isEmpty())
				JOptionPane.showMessageDialog(null,
					"ERROR.\nAdd an entity, a relation or an aggregation first\n",
					Lenguaje.text(Lenguaje.DELETE),
					JOptionPane.PLAIN_MESSAGE);
			else {
				this.getTheGUIEliminar().setListaTransfers(listaTransfers);
				this.getTheGUIEliminar().setActiva();
			}
			break;
		}
		
		case PanelDiseno_Click_RenombrarEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheGUIRenombrarEntidad().setEntidad(te);
			this.getTheGUIRenombrarEntidad().setActiva();
			break;
		}
		case PanelDiseno_Click_DebilitarEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			if(!te.isDebil() && this.getTheServiciosRelaciones().tieneHermanoDebil(te))
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ALREADY_WEAK_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
			else this.getTheServiciosEntidades().debilitarEntidad(te);
			break;
		}
		case PanelDiseno_Click_EliminarEntidad:{
			Vector<Object> v = (Vector<Object>) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			this.auxTransferAtributos = te.getListaAtributos();
			boolean preguntar =  (Boolean) v.get(1);
			int intAux = (int) v.get(2);
			int respuesta=0;
			if(!confirmarEliminaciones) preguntar=false;
			if(preguntar == true){
				String tieneAtributos ="";
				if (!te.getListaAtributos().isEmpty()) tieneAtributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING)+"\n";
				String tieneRelacion="";
				if (te.isDebil()) tieneRelacion = Lenguaje.text(Lenguaje.WARNING_DELETE_WEAK_RELATION)+"\n";
				respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.ENTITY)+" \""+te.getNombre()+"\" " +Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM)+"\n"+
						tieneAtributos+tieneRelacion + Lenguaje.text(Lenguaje.WISH_CONTINUE),
						Lenguaje.text(Lenguaje.DELETE_ENTITY));	
			}
			//Si quiere borrar la entidad
			/*Se entrará con preguntar a false si se viene de eliminar una relación débil 
			 * (para borrar también la entidad y sus atributos)*/
			if ((respuesta == 0)||(preguntar==false)){
				// Eliminamos sus atributos
				Vector lista_atributos = te.getListaAtributos();
				int conta = 0;
				TransferAtributo ta = new TransferAtributo(this);
				while (lista_atributos != null && conta < lista_atributos.size()){
					String idAtributo = (String) lista_atributos.get(conta);
					ta.setIdAtributo(Integer.valueOf(idAtributo));
					this.getTheServiciosAtributos().eliminarAtributo(ta, 1);
					conta++;
				}
				//Si la entidad es débil eliminamos la relación débil asociada
				if(te.isDebil()){
					Vector <TransferRelacion> lista_rel = this.getTheServiciosRelaciones().ListaDeRelacionesNoVoid();
					int cont=0,aux =0;
					boolean encontrado= false;
					EntidadYAridad eya;
					int idEntidad;
					TransferRelacion tr = new TransferRelacion();
					//Para cada relación
					while (cont < lista_rel.size()){
						//Si la relación es débil 
						if (lista_rel.get(cont).getTipo().equals("Debil")){
							//Compruebo si las entidades asociadas son la entidad débil que se va a eliminar
							while((!encontrado)&&(aux < lista_rel.get(cont).getListaEntidadesYAridades().size())){
								eya=(EntidadYAridad)(lista_rel.get(cont).getListaEntidadesYAridades().get(aux));
								idEntidad = eya.getEntidad();
								if(te.getIdEntidad()== idEntidad){
									tr.setIdRelacion(lista_rel.get(cont).getIdRelacion());
									this.getTheServiciosRelaciones().eliminarRelacionNormal(tr, 1);
									encontrado=true;
								}
								aux++;
							}
							aux=0;
						}
						cont++;							
					}
				}
				// Eliminamos la entidad
				this.getTheServiciosEntidades().eliminarEntidad(te, intAux);
			}
			break;
		}
		case PanelDiseno_Click_AnadirRestriccionAEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheGUIAnadirRestriccionAEntidad().setEntidad(te);
			this.getTheGUIAnadirRestriccionAEntidad().setActiva();
			break;
		}
		case PanelDiseno_Click_AnadirRestriccionAAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIAnadirRestriccionAAtributo().setAtributo(ta);
			this.getTheGUIAnadirRestriccionAAtributo().setActiva();
			break;
		}
		case PanelDiseno_Click_AnadirRestriccionARelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIAnadirRestriccionARelacion().setRelacion(tr);
			this.getTheGUIAnadirRestriccionARelacion().setActiva();
			break;
		}
		case PanelDiseno_Click_TablaUniqueAEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			theGUITablaUniqueEntidad = new GUI_TablaUniqueEntidad();
			theGUITablaUniqueEntidad.setControlador(this);
			this.getTheGUITablaUniqueEntidad().setEntidad(te);
			this.getTheGUITablaUniqueEntidad().setActiva();
			break;
		}
		case PanelDiseno_Click_TablaUniqueARelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			theGUITablaUniqueRelacion = new GUI_TablaUniqueRelacion();
			theGUITablaUniqueRelacion.setControlador(this);
			this.getTheGUITablaUniqueRelacion().setRelacion(tr);
			this.getTheGUITablaUniqueRelacion().setActiva();
			break;
		}
		case PanelDiseno_Click_AnadirAtributoEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheGUIAnadirAtributoEntidad().setEntidad(te);
			this.getTheGUIAnadirAtributoEntidad().setActiva();
			break;
		}
		case PanelDiseno_Click_RenombrarAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIRenombrarAtributo().setAtributo(ta);
			this.getTheGUIRenombrarAtributo().setActiva();
			break;
		}
		case PanelDiseno_Click_EliminarAtributo:{
			Vector<Object> v = (Vector<Object>) datos;
			TransferAtributo ta = (TransferAtributo) v.get(0);
			this.antiguosSubatributos = ta.getListaComponentes();
			int intAux = (int) v.get(2);
			boolean preguntar =  (Boolean) v.get(1);
			int respuesta=0;
			if(!confirmarEliminaciones) preguntar=false;
			if(preguntar == true){
				String eliminarSubatributos = "";
				if (!ta.getListaComponentes().isEmpty())
					eliminarSubatributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING)+"\n";
				respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.ATTRIBUTE)+" \""+ta.getNombre()+"\" "+Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM)+"\n" +
						eliminarSubatributos + Lenguaje.text(Lenguaje.WISH_CONTINUE),
						Lenguaje.text(Lenguaje.DELETE_ATTRIB));	
			}
			if (respuesta == 0){
				if(ta.getUnique()){
					Vector<Object> ve = new Vector<Object>();
					TransferAtributo clon_atributo = ta.clonar();
					ve.add(clon_atributo);
					ve.add(1);
					this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo,ve);
				}
				TransferAtributo clon_atributo2 = ta.clonar();
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarReferenciasUniqueAtributo,clon_atributo2);
				TransferAtributo clon_atributo3 = ta.clonar();
				this.getTheServiciosAtributos().eliminarAtributo(clon_atributo3, intAux);
			}
			break;
		}
		case PanelDiseno_Click_InsertarRelacionNormal:{
			Point2D punto = (Point2D) datos;
			this.getTheGUIInsertarRelacion().setPosicionRelacion(punto);
			this.getTheGUIInsertarRelacion().setActiva();
			break;
		}
		case PanelDiseno_Click_RenombrarRelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIRenombrarRelacion().setRelacion(tr);
			this.getTheGUIRenombrarRelacion().setActiva();
			break;
		}
		/*Aunque desde el panel de diseño no se puede debilitar una relación este caso sigue
		 * utilizándose cuando se crea una entidad débil ya que debe generarse también una 
		 * relación débil asociada a ella.*/
		case PanelDiseno_Click_DebilitarRelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			//Si es una relacion fuerte... 
			if (tr.getTipo().equals("Normal")){
				int numDebiles = this.getTheServiciosRelaciones().numEntidadesDebiles(tr);
				// ...y tiene más de una entidad débil no se puede debilitar
				if(numDebiles>1){
					JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATION_WEAK_ENTITIES), Lenguaje.text(Lenguaje.ERROR), 0);
					break;
				}
				int respuesta1=-1;//-1 no hay conflicto, 0 el usuario dice SI, 1 el usuario dice NO
				int respuesta2=-1;
				// ...y tiene atributos y se quiere debilitar hay que eliminar sus atributos
				if(!tr.getListaAtributos().isEmpty()){
					respuesta1 = panelOpciones.setActiva(
							Lenguaje.text(Lenguaje.WEAK_RELATION)+" \""+tr.getNombre()+"\""+
							Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING2)+"\n" +
							Lenguaje.text(Lenguaje.WISH_CONTINUE),
							Lenguaje.text(Lenguaje.DBCASE));
				}
				// ...y tiene una entidad débil hay que cambiar la cardinalidad
				if(numDebiles == 1 && respuesta1!=1 ){
					respuesta2 = panelOpciones.setActiva(
							Lenguaje.text(Lenguaje.WEAK_RELATION)+"\""+tr.getNombre()+"\""+
							Lenguaje.text(Lenguaje.MODIFYING_CARDINALITY) +".\n" +
							Lenguaje.text(Lenguaje.WISH_CONTINUE),
							Lenguaje.text(Lenguaje.DBCASE));
				}
				if (respuesta2==0 && respuesta1!=1){
					//Aqui se fija la cardinalidad de la entidad débil como de 1 a 1.		
					Vector<Object> v = new Vector<Object>();
					EntidadYAridad informacion;
					int i=0;
					boolean actualizado =false;
					while((!actualizado)&&(i < tr.getListaEntidadesYAridades().size())){
						informacion =(EntidadYAridad) (tr.getListaEntidadesYAridades().get(i));
						int idEntidad = informacion.getEntidad();
						if (this.getTheServiciosEntidades().esDebil(idEntidad)){
							actualizado=true;
							int idRelacion = tr.getIdRelacion();
							int finRango = 1;
							int iniRango = 1;
							String nombre = tr.getNombre();
							Point2D posicion = tr.getPosicion();
							Vector<Object> listaEnti = tr.getListaEntidadesYAridades();
							EntidadYAridad aux =(EntidadYAridad)listaEnti.get(i);
							aux.setFinalRango(1);
							aux.setPrincipioRango(1);
							listaEnti.remove(i);
							listaEnti.add(aux);
							Vector<Object> listaAtri = tr.getListaAtributos();
							String tipo = tr.getTipo();
							String rol = tr.getRol();
							v.add(idRelacion);
							v.add(idEntidad);
							v.add(iniRango);
							v.add(finRango);
							v.add(nombre);
							v.add(listaEnti);
							v.add(listaAtri);
							v.add(tipo);
							v.add(rol);
							v.add(posicion);
							this.getTheServiciosRelaciones().aridadEntidadUnoUno(v);
						}
						i++;
						
					}					
				}
				if (respuesta1 == 0 && respuesta2!=1){
					// Eliminamos sus atributos
					Vector lista_atributos = tr.getListaAtributos();
					int cont = 0;
					TransferAtributo ta = new TransferAtributo(this);
					while (cont < lista_atributos.size()){
						String idAtributo = (String) lista_atributos.get(cont);
						ta.setIdAtributo(Integer.valueOf(idAtributo));
						this.getTheServiciosAtributos().eliminarAtributo(ta, 1);				
						cont++;
					}
				}
				if(respuesta1!=1 && respuesta2!=1){
				// Modificamos la relacion
					tr.getListaAtributos().clear();
					this.getTheServiciosRelaciones().debilitarRelacion(tr);
				}
			}
			else{
				this.getTheServiciosRelaciones().debilitarRelacion(tr);
			}
			break;			
		}
		case PanelDiseno_Click_EditarDominioAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIEditarDominioAtributo().setAtributo(ta);
			this.getTheGUIEditarDominioAtributo().setActiva();
			break;
		}
		case PanelDiseno_Click_EditarCompuestoAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			// Si es un atributo compuesto y tiene subatributos al ponerlo como simple hay que eliminar sus atributos
			if (ta.getCompuesto() && !ta.getListaComponentes().isEmpty()){
			/*	Object[] options = {Lenguaje.getMensaje(Lenguaje.YES),Lenguaje.getMensaje(Lenguaje.NO)};
				int respuesta = JOptionPane.showOptionDialog(
						null,
						Lenguaje.getMensaje(Lenguaje.MODIFY_ATTRIBUTE)+"\""+ta.getNombre()+"\""+
						Lenguaje.getMensaje(Lenguaje.DELETE_ATTRIBUTES_WARNING3)+"\n" +
						Lenguaje.getMensaje(Lenguaje.WISH_CONTINUE),
						Lenguaje.getMensaje(Lenguaje.DBCASE),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);*/
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.MODIFY_ATTRIBUTE)+"\""+ta.getNombre()+"\""+
						Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING3)+"\n" +
						Lenguaje.text(Lenguaje.WISH_CONTINUE),
						Lenguaje.text(Lenguaje.DBCASE));
				if (respuesta == 0){
					// Eliminamos sus subatributos
					Vector lista_atributos = ta.getListaComponentes();
					int cont = 0;
					TransferAtributo tah = new TransferAtributo(this);
					while (cont < lista_atributos.size()){
						String idAtributo = (String) lista_atributos.get(cont);
						tah.setIdAtributo(Integer.valueOf(idAtributo));
						this.getTheServiciosAtributos().eliminarAtributo(tah, 1);				
						cont++;
					}
					// Modificamos el atributo
					ta.getListaComponentes().clear();
					this.getTheServiciosAtributos().editarCompuestoAtributo(ta);
				}
			}
			// Si no es compuesto o es compuesto pero no tiene subatributos
			else{
				this.getTheServiciosAtributos().editarCompuestoAtributo(ta);
			}
			break;
		}
		case PanelDiseno_Click_EditarNotNullAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheServiciosAtributos().editarNotNullAtributo(ta);
			break;			
		}
		case PanelDiseno_Click_EditarUniqueAtributo:{
			//hola
			Vector<Object> ve = (Vector<Object>) datos;
			TransferAtributo ta = (TransferAtributo) ve.get(0);
			this.getTheServiciosAtributos().editarUniqueAtributo(ta);
			
			this.getTheServiciosEntidades().ListaDeEntidades();
			this.getTheServiciosAtributos().ListaDeAtributos();
			this.getTheServiciosRelaciones().ListaDeRelaciones();
			//modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
			Vector<TransferRelacion> relaciones = this. getTheGUIPrincipal().getListaRelaciones();
			Vector<TransferEntidad> entidades = this.getTheGUIPrincipal().getListaEntidades();
			boolean encontrado = false;
			boolean esEntidad = false;
			TransferEntidad te = null;
			TransferRelacion tr= null;
			int i=0;
			while (i< entidades.size() && !encontrado){
				te= entidades.get(i);
				if (this.getTheServiciosEntidades().tieneAtributo(te, ta)){
					encontrado=true;
					esEntidad=true;
				}
				i++;
			}			
			i=0;
			while (i< relaciones.size() && !encontrado){
				tr= relaciones.get(i);
				if (this.getTheServiciosRelaciones().tieneAtributo(tr, ta)){
					encontrado=true;
				}
				i++;
			}
			if (encontrado){
				if (esEntidad){
					Vector v = new Vector();
					v.add(te);
					v.add(ta);
					this.getTheServiciosEntidades().setUniqueUnitario(v);
				}else{//esRelacion
					Vector v = new Vector();
					v.add(tr);
					v.add(ta);
					this.getTheServiciosRelaciones().setUniqueUnitario(v);
				}
			}
				
			break;			
		}
		case PanelDiseno_Click_EliminarReferenciasUniqueAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheServiciosAtributos().editarUniqueAtributo(ta);
			
			//this.getTheServiciosEntidades().ListaDeEntidades();
			//this.getTheServiciosAtributos().ListaDeAtributos();
			//this.getTheServiciosRelaciones().ListaDeRelaciones();
			//modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
			Vector<TransferRelacion> relaciones = this. getTheGUIPrincipal().getListaRelaciones();
			Vector<TransferEntidad> entidades = this.getTheGUIPrincipal().getListaEntidades();
			boolean encontrado = false;
			boolean esEntidad = false;
			TransferEntidad te = null;
			TransferRelacion tr= null;
			int i=0;
			while (i< entidades.size() && !encontrado){
				te= entidades.get(i);
				if (this.getTheServiciosEntidades().tieneAtributo(te, ta)){
					encontrado=true;
					esEntidad=true;
				}
				i++;
			}			
			i=0;
			while (i< relaciones.size() && !encontrado){
				tr= relaciones.get(i);
				if (this.getTheServiciosRelaciones().tieneAtributo(tr, ta)){
					encontrado=true;
				}
				i++;
			}
			if (encontrado){
				if (esEntidad){
					Vector v = new Vector();
					v.add(te);
					v.add(ta);
					this.getTheServiciosEntidades().eliminarReferenciasUnitario(v);
				}else{//esRelacion
					Vector v = new Vector();
					v.add(tr);
					v.add(ta);
					this.getTheServiciosRelaciones().eliminarReferenciasUnitario(v);
				}
			}
				
			break;			
		}
		case PanelDiseno_Click_ModificarUniqueAtributo:{
			Vector v1 = (Vector)datos;
			TransferAtributo ta = (TransferAtributo) v1.get(0);
			String antiguoNombre =(String)v1.get(1);
			
			this.getTheServiciosAtributos().editarUniqueAtributo(ta);
			
			this.getTheServiciosEntidades().ListaDeEntidades();
			this.getTheServiciosAtributos().ListaDeAtributos();
			this.getTheServiciosRelaciones().ListaDeRelaciones();
			//modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
			Vector<TransferRelacion> relaciones = this. getTheGUIPrincipal().getListaRelaciones();
			Vector<TransferEntidad> entidades = this.getTheGUIPrincipal().getListaEntidades();
			boolean encontrado = false;
			boolean esEntidad = false;
			TransferEntidad te = null;
			TransferRelacion tr= null;
			int i=0;
			while (i< entidades.size() && !encontrado){
				te= entidades.get(i);
				if (this.getTheServiciosEntidades().tieneAtributo(te, ta)){
					encontrado=true;
					esEntidad=true;
				}
				i++;
			}			
			i=0;
			while (i< relaciones.size() && !encontrado){
				tr= relaciones.get(i);
				if (this.getTheServiciosRelaciones().tieneAtributo(tr, ta)){
					encontrado=true;
				}
				i++;
			}
			if (encontrado){
				if (esEntidad){
					Vector v = new Vector();
					v.add(te);
					v.add(ta);
					v.add(antiguoNombre);
					this.getTheServiciosEntidades().renombraUnique(v);
				}else{//esRelacion
					Vector v = new Vector();
					v.add(tr);
					v.add(ta);
					v.add(antiguoNombre);
					this.getTheServiciosRelaciones().renombraUnique(v);
				}
			}
				
			break;			
		}
		case PanelDiseno_Click_EditarMultivaloradoAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheServiciosAtributos().editarMultivaloradoAtributo(ta);
			break;			
		}
		case PanelDiseno_Click_AnadirSubAtributoAAtributo:{
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIAnadirSubAtributoAtributo().setAtributo(ta);
			this.getTheGUIAnadirSubAtributoAtributo().setActiva();
			break;
		}
		case PanelDiseno_Click_EditarClavePrimariaAtributo:{
			Vector<Object> v = (Vector<Object>) datos;
			this.getTheServiciosAtributos().editarClavePrimariaAtributo(v);
			break;
		}
		case PanelDiseno_MoverEntidad:{
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheServiciosEntidades().moverPosicionEntidad(te);
			break;
		}
		case PanelDiseno_MoverAtributo:{
			
			
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheServiciosAtributos().moverPosicionAtributo(ta);
			break;
		}
		case PanelDiseno_MoverRelacion:{			
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheServiciosRelaciones().moverPosicionRelacion(tr);
			break;
		}
		case PanelDiseno_Click_AnadirAtributoRelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			// Si es una relacion debil no se pueden anadir atributos
			if (tr.getTipo().equals("Debil")){
				JOptionPane.showMessageDialog(
						null,"ERROR.\n" +
						Lenguaje.text(Lenguaje.NO_ATTRIBUTES_RELATION)+".\n" +
						Lenguaje.text(Lenguaje.THE_RELATION)+"\""+tr.getNombre()+"\""+ Lenguaje.text(Lenguaje.IS_WEAK)+".\n",
						Lenguaje.text(Lenguaje.ADD_ENTITY_RELATION),
						JOptionPane.PLAIN_MESSAGE);
				return;
			}

			this.getTheGUIAnadirAtributoRelacion().setRelacion(tr);
			this.getTheGUIAnadirAtributoRelacion().setActiva();
			break;
		}
		/*
		 * Relaciones IsA
		 */
		case PanelDiseno_Click_EstablecerEntidadPadre:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIEstablecerEntidadPadre().setRelacion(tr);
			this.getTheGUIEstablecerEntidadPadre().setActiva();
			break;
		}
		case PanelDiseno_Click_QuitarEntidadPadre:{
			TransferRelacion tr = (TransferRelacion) datos;
			//this.idPadreAntigua = tr.getEntidadYAridad(0).getEntidad();
			this.getTheGUIQuitarEntidadPadre().setRelacion(tr);
			this.getTheGUIQuitarEntidadPadre().setActiva();
			break;
		}
		case PanelDiseno_Click_AnadirEntidadHija:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIAnadirEntidadHija().setRelacion(tr);
			this.getTheGUIAnadirEntidadHija().setActiva();
			break;
		}
		case PanelDiseno_Click_QuitarEntidadHija:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIQuitarEntidadHija().setRelacion(tr);
			this.getTheGUIQuitarEntidadHija().setActiva();
			break;
		}
		case PanelDiseno_Click_EliminarRelacionIsA:{
			Vector<Object> v = (Vector<Object>) datos;
			TransferRelacion tr = (TransferRelacion) v.get(0);
			boolean preguntar =  (Boolean) v.get(1);
			int respuesta=0;
			if(!confirmarEliminaciones) preguntar=false;
			if(preguntar == true){
				respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.ISA_RELATION_DELETE)+"\n" +
						Lenguaje.text(Lenguaje.WISH_CONTINUE),
						Lenguaje.text(Lenguaje.DELETE_ISA_RELATION));	
			}
			if (respuesta == 0) {
				this.getTheServiciosRelaciones().eliminarRelacionIsA(tr);
				this.getTheServiciosEntidades().eliminarRelacionDeEntidad(tr);
			}
			break;
		}
		
		case PanelDiseno_Click_InsertarRelacionIsA:{
			Point2D punto = (Point2D) datos;
			TransferRelacion tr = new TransferRelacion();
			tr.setPosicion(punto);
			this.getTheServiciosRelaciones().anadirRelacionIsA(tr);
			break;
		}
		/*
		 * Relaciones normales
		 */
		case PanelDiseno_Click_EliminarRelacionNormal:{
			Vector<Object> v = (Vector<Object>) datos;
			TransferRelacion tr = (TransferRelacion) v.get(0);
			int intAux = (int) v.get(2);
			Vector vtaAux = tr.getListaAtributos();
			Vector<TransferAtributo> vta = new Vector<TransferAtributo>();
			Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
			Vector<TransferEntidad> vte = new Vector<TransferEntidad>();
			
			for(int i = 0; i < vtaAux.size(); ++i) {
				int id = Integer.valueOf((String) vtaAux.get(i));
				for(int j = 0; j < this.listaAtributos.size(); ++j) {
					if(id == this.listaAtributos.get(j).getIdAtributo()) vta.add(this.listaAtributos.get(j));
				}
			}
			
			//this.antiguosAtributosRel = vta;
			
			for(int i = 0; i < veya.size(); ++i) {
				int id = veya.get(i).getEntidad();
				for(int j = 0; j < this.listaEntidades.size(); ++j) {
					if(id == this.listaEntidades.get(j).getIdEntidad()) vte.add(this.listaEntidades.get(j));
				}
			}
			
			//this.antiguasEntidadesRel = vte;
			
			
			boolean preguntar =  (Boolean) v.get(1);
			int respuesta=0;
			if(!confirmarEliminaciones) preguntar=false;
			if(preguntar == true){
				String tieneAtributos ="";
				if (!tr.getListaAtributos().isEmpty()) 
					tieneAtributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING)+"\n";
				String tieneEntidad="";
				//Informar de que también se va a eliminar la entidad débil asociada
				if(tr.getTipo().equals("Debil"))
					tieneEntidad = Lenguaje.text(Lenguaje.WARNING_DELETE_WEAK_ENTITY)+"\n";
				respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.THE_RELATION)+" \""+tr.getNombre()+"\" "+
						Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM)+"\n" +
						tieneAtributos + tieneEntidad+
						Lenguaje.text(Lenguaje.WISH_CONTINUE),
						Lenguaje.text(Lenguaje.DELETE_RELATION));
			}
			//Si se desea eliminar la relación
			if (respuesta == 0){
				// Eliminamos sus atributos				
				Vector lista_atributos = tr.getListaAtributos();
				int conta = 0;
				TransferAtributo ta = new TransferAtributo(this);
				while (conta < lista_atributos.size()){
					String idAtributo = (String) lista_atributos.get(conta);
					ta.setIdAtributo(Integer.valueOf(idAtributo));
					this.getTheServiciosAtributos().eliminarAtributo(ta, 1);				
					conta++;
				}
				//Se elimina también la entidad débil asociada
				if(tr.getTipo().equals("Debil")){
					Vector lista_entidades = tr.getListaEntidadesYAridades();
					int cont = 0;
					TransferEntidad te = new TransferEntidad();
					while (cont < lista_entidades.size()){
						EntidadYAridad eya=(EntidadYAridad)(tr.getListaEntidadesYAridades().get(cont));
						int idEntidad = eya.getEntidad();
						te.setIdEntidad(idEntidad);	
						//Tengo que rellenar los atributos de te
						Vector<TransferEntidad> auxiliar=(this.theGUIQuitarEntidadARelacion.getListaEntidades()); //falla aqui				if (auxiliar == null) 
						auxiliar = this.getListaEntidades();
						boolean encontrado= false;
						int i=0;
						if (auxiliar != null) {
							while ((!encontrado)&&(i<auxiliar.size())){
								if(auxiliar.get(i).getIdEntidad()==idEntidad){
									encontrado=true;
									te.setListaAtributos(auxiliar.get(i).getListaAtributos());								
								}
								else
									i++;								
							}
						}
						//Elimino también la entidad débil
						if (this.getTheServiciosEntidades().esDebil(idEntidad)){							
							//Esto es para borrar los atributos de la entidad débil y la propia entidad débil
							Vector<Object> vAux = new Vector<Object>();
							vAux.add(te);
							vAux.add(false);
							if(vAux.size()==2) vAux.add(1);
							else vAux.set(2, 1);
							this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarEntidad,vAux);
						}
						cont++;							
					}
				}
				
				// Eliminamos la relacion
				
				this.getTheServiciosAgregaciones().eliminarAgregacion(tr);
				this.getTheServiciosEntidades().eliminarRelacionDeEntidad(tr);
				this.getTheServiciosRelaciones().eliminarRelacionNormal(tr,intAux);
			}
			break;
		}
		case PanelDiseno_Click_AnadirEntidadARelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIAnadirEntidadARelacion().setRelacion(tr);
			this.getTheGUIAnadirEntidadARelacion().setActiva();
			break;
		}
		case PanelDiseno_Click_QuitarEntidadARelacion:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIQuitarEntidadARelacion().setRelacion(tr);
			this.getTheGUIQuitarEntidadARelacion().setActiva();
			break;
		}
		case PanelDiseno_Click_EditarCardinalidadEntidad:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheGUIEditarCardinalidadEntidad().setRelacion(tr);
			this.getTheGUIEditarCardinalidadEntidad().setActiva();
			break;
		}
		/*
		 * Dominios
		 */
		case PanelDiseno_Click_CrearDominio:{
			this.getTheGUIInsertarDominio().setActiva();
			break;
		}
		case PanelDiseno_Click_RenombrarDominio:{
			TransferDominio td = (TransferDominio) datos;
			this.getTheGUIRenombrarDominio().setDominio(td);
			this.getTheGUIRenombrarDominio().setActiva();
			break;
		}
		case PanelDiseno_Click_EliminarDominio:{
			TransferDominio td = (TransferDominio) datos;
			int respuesta = panelOpciones.setActiva(
					Lenguaje.text(Lenguaje.DOMAIN)+" \""+td.getNombre()+"\" "+ Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM)+"\n" +
					Lenguaje.text(Lenguaje.MODIFYING_ATTRIBUTES_WARNING4)+"\n" +
					Lenguaje.text(Lenguaje.WISH_CONTINUE),
					Lenguaje.text(Lenguaje.DELETE_DOMAIN));
			if (respuesta == 0){
				modelo.transfers.TipoDominio valorBase = td.getTipoBase();
				String dominioEliminado = td.getNombre();
				this.getTheServiciosAtributos().ListaDeAtributos();
				int cont = 0;
				TransferAtributo ta = new TransferAtributo(this);
				while (cont < listaAtributos.size()){
					ta = listaAtributos.get(cont);
					if (ta.getDominio().equals(dominioEliminado)){
						Vector<Object> v = new Vector();
						v.add(ta);
						String valorB = new String();
						if(valorBase.name().equals("TEXT")||valorBase.name().equals("VARCHAR"))
							valorB = valorBase.toString() + "(20)";
						else valorB= valorBase.toString();
						
						v.add(valorB);
						
						if(valorBase.name().equals("TEXT")||valorBase.name().equals("VARCHAR")) v.add("20");
						this.getTheServiciosAtributos().editarDomnioAtributo(v);
					}
					cont++;
				}
				// Eliminamos el dominio
				this.getTheServiciosDominios().eliminarDominio(td);
			}
			break;
		}
		case PanelDiseno_Click_ModificarDominio:{
			TransferDominio td = (TransferDominio) datos;
			this.getTheGUIModificarElementosDominio().setDominio(td);
			this.getTheGUIModificarElementosDominio().setActiva();
			break;
		}case PanelDiseno_Click_OrdenarValoresDominio:{
			TransferDominio td = (TransferDominio) datos;
			quicksort((Vector<String>)td.getListaValores());
			
			Vector<Object> v = new Vector();
			v.add(td);
			v.add(td.getListaValores());
			this.getTheServiciosDominios().modificarElementosDominio(v);
			break;
		}
		
		/*
		 * Panel de informacion
		 */
		case PanelDiseno_MostrarDatosEnPanelDeInformacion:{
			JTree arbol = (JTree) datos;
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MostrarDatosEnPanelDeInformacion, arbol);
			break;
		}	
		case PanelDiseno_ActualizarDatosEnTablaDeVolumenes:{
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ActualizarDatosEnTablaDeVolumenes,datos);
			break;
		}
		case PanelDiseno_MostrarDatosEnTablaDeVolumenes:{
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MostrarDatosEnTablaDeVolumenes,datos);
			break;
		}
		
		case PanelDiseno_Click_Copiar:{
			this.copiado = (Transfer) datos;
			break;
		}
		
		case PanelDiseno_Click_Pegar:{
			if(this.copiado instanceof TransferEntidad) {
				TransferEntidad te = (TransferEntidad) copiado;
				int p = te.getPegado();
				TransferEntidad nueva = new TransferEntidad();
				Point2D punto = (Point2D) datos;
				nueva.setPosicion(punto);
				nueva.setNombre(te.getNombre() + Integer.toString(p));
				nueva.setDebil(te.isDebil());
				nueva.setListaAtributos(new Vector());
				nueva.setListaRelaciones(new Vector());
				nueva.setListaClavesPrimarias(new Vector());
				nueva.setListaRestricciones(new Vector());
				nueva.setListaUniques(new Vector());
				this.mensajeDesde_GUI(TC.GUIInsertarEntidad_Click_BotonInsertar, nueva);
				/*Vector<TransferAtributo> atributos = te.getListaAtributos();
				for (int i = 0; i < atributos.size(); ++i) {
					for (int j = 0; j < this.listaAtributos.size(); ++j) {
						//System.out.println(atributos.get(i));
						//System.out.println(this.listaAtributos.get(j).getIdAtributo());
						if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
							Vector<Object> v = new Vector<Object>();
							v.add(nueva);
							TransferAtributo TA = this.listaAtributos.get(j);
							TransferAtributo nuevoTA = new TransferAtributo(this);
							double x = nueva.getPosicion().getX();
							double y = nueva.getPosicion().getY();
							nuevoTA.setPosicion(new Point2D.Double(x,y));
							nuevoTA.setClavePrimaria(TA.getClavePrimaria());
							nuevoTA.setCompuesto(TA.getCompuesto());
							nuevoTA.setDominio(TA.getDominio());
							nuevoTA.setFrecuencia(TA.getFrecuencia());
							nuevoTA.setIdAtributo(TA.getIdAtributo() + 10);
							nuevoTA.setListaComponentes(TA.getListaComponentes());
							nuevoTA.setListaRestricciones(TA.getListaComponentes());
							nuevoTA.setMultivalorado(TA.getMultivalorado());
							nuevoTA.setNombre(TA.getNombre());
							nuevoTA.setNotnull(TA.getNotnull());
							nuevoTA.setUnique(TA.getUnique());
							nuevoTA.setVolumen(TA.getVolumen());
							v.add(nuevoTA);
							v.add("10");
							mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
						}	
					}
					
				}*/
				//nueva.setListaClavesPrimarias(te.getListaClavesPrimarias());
				nueva.setListaRestricciones(te.getListaRestricciones());
				nueva.setListaUniques(te.getListaUniques());
				//nueva.setIdEntidad(te.getIdEntidad() + 10);
				nueva.setFrecuencia(te.getFrecuencia());
				nueva.setVolumen(te.getVolumen());
				nueva.setOffsetAttr(te.getOffsetAttr());
				te.setPegado(p + 1);
				ActualizaArbol(te);
			}
			
			if(this.copiado instanceof TransferRelacion) {
				TransferRelacion tr = (TransferRelacion) copiado;
				int p = tr.getPegado();
				TransferRelacion nueva = new TransferRelacion();
				Point2D punto = (Point2D) datos;
				nueva.setPosicion(punto);
				if (tr.getTipo().equals("IsA")) {
					this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, tr.getPosicion());
				}
				
				else {
					
					nueva.setNombre(tr.getNombre() + Integer.toString(p));
					nueva.setCheckQuitarFlechas(tr.getCheckQuitarFlechas());
					nueva.setListaEntidadesYAridades(new Vector<EntidadYAridad>());
					nueva.setFrecuencia(tr.getFrecuencia());
					nueva.setIdRelacion(tr.getIdRelacion()+10);
					nueva.setListaRestricciones(tr.getListaRestricciones());
					nueva.setListaUniques(tr.getListaUniques());
					nueva.setOffsetAttr(tr.getOffsetAttr());
					nueva.setRelacionConCardinalidad(tr.getRelacionConCardinalidad());
					nueva.setRelacionConCardinalidad1(tr.getRelacionConCardinalidad1());
					nueva.setRelacionConMinMax(tr.getRelacionConMinMax());
					nueva.setRelacionConParticipacion(tr.getRelacionConParticipacion());
					nueva.setVolumen(tr.getVolumen());
					nueva.setRol(tr.getRol());
					nueva.setTipo(tr.getTipo());
					nueva.setListaAtributos(new Vector());
					this.mensajeDesde_GUI(TC.GUIInsertarRelacion_Click_BotonInsertar, nueva);
					Vector<TransferAtributo> atributos = tr.getListaAtributos();
					/*for (int i = 0; i < atributos.size(); ++i) {
						for (int j = 0; j < this.listaAtributos.size(); ++j) {
							//System.out.println(atributos.get(i));
							//System.out.println(this.listaAtributos.get(j).getIdAtributo());
							if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
								Vector<Object> v = new Vector<Object>();
								v.add(nueva);
								TransferAtributo TA = this.listaAtributos.get(j);
								TransferAtributo nuevoTA = new TransferAtributo(this);
								double x = nueva.getPosicion().getX();
								double y = nueva.getPosicion().getY();
								nuevoTA.setPosicion(new Point2D.Double(x,y));
								nuevoTA.setClavePrimaria(TA.getClavePrimaria());
								nuevoTA.setCompuesto(TA.getCompuesto());
								nuevoTA.setDominio(TA.getDominio());
								nuevoTA.setFrecuencia(TA.getFrecuencia());
								nuevoTA.setIdAtributo(TA.getIdAtributo() + 10);
								nuevoTA.setListaComponentes(TA.getListaComponentes());
								nuevoTA.setListaRestricciones(TA.getListaComponentes());
								nuevoTA.setMultivalorado(TA.getMultivalorado());
								nuevoTA.setNombre(TA.getNombre());
								nuevoTA.setNotnull(TA.getNotnull());
								nuevoTA.setUnique(TA.getUnique());
								nuevoTA.setVolumen(TA.getVolumen());
								v.add(nuevoTA);
								v.add("10");
								mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
							}	
						}
						
					}*/
					tr.setPegado(p+1);
					ActualizaArbol(tr);
					
				}
			}
			
			if(this.copiado instanceof TransferAtributo) {
				return;
				/*TransferAtributo ta = (TransferAtributo) copiado;
				int p = ta.getPegado();
				TransferAtributo nuevo = new TransferAtributo(this);
				Point2D punto = (Point2D) datos;
				nuevo.setPosicion(punto);
				//obtenemos a que elemento pertenece
				Transfer elem_mod = this.getTheServiciosAtributos().eliminaRefererenciasAlAtributo(ta);
				nuevo.setClavePrimaria(ta.getClavePrimaria());
				nuevo.setCompuesto(ta.getCompuesto());
				nuevo.setDominio(ta.getDominio());
				nuevo.setNombre(ta.getNombre() + Integer.toString(p));
				nuevo.setIdAtributo(ta.getIdAtributo() + 10);
				nuevo.setMultivalorado(ta.getMultivalorado());
				nuevo.setNotnull(ta.getNotnull());
				nuevo.setUnique(ta.getUnique());
				nuevo.setSubatributo(ta.isSubatributo());
				nuevo.setVolumen(ta.getVolumen());
				nuevo.setListaComponentes(ta.getListaComponentes());
				nuevo.setFrecuencia(ta.getFrecuencia());
				nuevo.setListaRestricciones(ta.getListaRestricciones());
				double x = elem_mod.getPosicion().getX();
				double y = elem_mod.getPosicion().getY();
				nuevo.setPosicion(new Point2D.Double(x,y));
				
				if(elem_mod instanceof TransferEntidad) {
					Vector<Object> v = new Vector<Object>();
					v.add(elem_mod);
					v.add(nuevo);
					v.add("10");
					mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
				}
				
				else if(elem_mod instanceof TransferRelacion) {
					Vector<Object> v = new Vector<Object>();
					v.add(elem_mod);
					v.add(nuevo);
					v.add("10");
					mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
				}
				
				else if(elem_mod instanceof TransferAtributo) {
					Vector<Object> v = new Vector<Object>();
					v.add(elem_mod);
					v.add(nuevo);
					v.add("10");
					mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v);
				}
				
				ta.setPegado(p + 1);
				ActualizaArbol(ta);
				*/
			}
			
			break;
		}
		
		default: break;
		} // switch 
	}
	

	private GUI_RenombrarAgregacion getTheGUIRenombrarAgregacion() {
		// TODO Auto-generated method stub
		return theGUIModificarAgregacion;
	}

	private GUI_AnadirAgregacion getTheGUIAddAgregacion() {
		return theGUIAddAgregacion;
	}
	/*case GUI_Principal_DESHACER:{
		funcionDeshacer(this.ultimoMensaje, this.ultimosDatos);
		break;
	}*/
	// Mensajes que manda la GUIPrincipal al Controlador
	@SuppressWarnings("static-access")
	public void mensajeDesde_GUIPrincipal(TC mensaje, Object datos){
		
		switch(mensaje){
		case GUIPrincipal_ObtenDBMSDisponibles: {
			Vector<TransferConexion> vtc = 
				this.getTheServiciosSistema().obtenerTiposDeConexion();
			this.getTheGUIPrincipal().setListaConexiones(vtc);
			break;
		}
		case GUIPrincipal_ActualizameLaListaDeEntidades: {
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIPrincipal_ActualizameLaListaDeAtributos:{
			this.getTheServiciosAtributos().ListaDeAtributos();
			break;
		}
		case GUIPrincipal_ActualizameLaListaDeRelaciones:{
			this.getTheServiciosRelaciones().ListaDeRelaciones();
			break;
		}

		case GUIPrincipal_ActualizameLaListaDeAgregaciones:{
			this.getTheServiciosAgregaciones().ListaDeAgregaciones();
			break;
		}

		case GUIPrincipal_ActualizameLaListaDeDominios:{
			this.getTheServiciosDominios().ListaDeDominios();
			break;
		}
		case GUI_Principal_ABOUT:{
			about.setActiva(true);
			break;
		}
		case GUI_Principal_MANUAL:{
			manual.setActiva(true);
			break;
		}
		case GUI_Principal_GALERIA:{
			galeria.setActiva(true);
			break;
		}
		case GUI_Principal_RESET:{
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) {
						filetemp.delete();
						this.getTheGUIWorkSpace().nuevoTemp();
						setCambios(false);
				}else if (respuesta==0) {
						theGUIWorkSpace = new GUI_SaveAs(true);
						theGUIWorkSpace.setControlador(this);
						if (this.getTheGUIWorkSpace().setActiva(2)){
							filetemp.delete();
							this.getTheGUIWorkSpace().nuevoTemp();
							setCambios(false);
						}
				}		
			}else{
				filetemp.delete();
				this.getTheGUIWorkSpace().nuevoTemp();
				setCambios(false);
			}
			break;
		}
		case GUI_Principal_REPORT:{
			report.setActiva();
			break;
		}
		
		/*case GUI_Principal_DESHACER:{
			funcionDeshacer(this.ultimoMensaje, this.ultimosDatos);
			break;
		}*/
		
		case GUI_Principal_DESHACER2:{
			String str = fileguardar.getPath().replace(".xml","");
			String ruta = "";
		    if (str.contains("projects"))
				ruta = str.replace("projects","deshacer") + Integer.toString(this.contFicherosDeshacer-2) + ".xml";
			else if (str.contains("Examples"))
				ruta = str.replace("Examples","deshacer") + Integer.toString(this.contFicherosDeshacer-2) + ".xml";
		    if(this.contFicherosDeshacer > 1) 
		    	this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Deshacer, ruta);
		    else return;
		    this.contFicherosDeshacer = this.contFicherosDeshacer-1;
		    this.auxDeshacer = false;
		    setCambios(true);
		    //this.getTheGUIPrincipal().getPanelDiseno().grabFocus();
		    break;
		}
		
		case GUI_Principal_REHACER:{
			String str = fileguardar.getPath().replace(".xml","");
			String ruta = "";
		    
		    if (str.contains("projects"))
				ruta = str.replace("projects","deshacer") + Integer.toString(this.contFicherosDeshacer) + ".xml";
			else if (str.contains("Examples"))
				ruta = str.replace("Examples","deshacer") + Integer.toString(this.contFicherosDeshacer) + ".xml";
		    
		    if (this.contFicherosDeshacer == this.limiteFicherosDeshacer || this.auxDeshacer == true) return;
		    this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Deshacer, ruta);
		    ++this.contFicherosDeshacer;
		    setCambios(true);
		    this.getTheGUIPrincipal().getPanelDiseno().grabFocus();
		    break;
		}
		
		case GUI_Principal_Vista1:{
			this.getTheGUIPrincipal().modoProgramador();break;
		}
		case GUI_Principal_Vista2:{
			this.getTheGUIPrincipal().modoVerTodo();
			break;
		}
		case GUI_Principal_Vista3:{
			this.getTheGUIPrincipal().modoDiseno();
			break;
		}
		case GUI_Principal_Zoom:{
			zoom.setActiva();
			break;
		}
		case GUI_Principal_Zoom_Aceptar:{
			this.getTheGUIPrincipal().cambiarZoom((int) datos);
			this.setZoom((int) datos);
			break;
		}
		case GUI_Principal_IniciaFrames:{
			iniciaFrames();
			break;
		}
		/*
		 * Barra de menus
		 */
		case GUI_Principal_Click_Submenu_Salir:{
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) guardarYSalir();
				else if (respuesta==0) {
						theGUIWorkSpace = new GUI_SaveAs(true);
						theGUIWorkSpace.setControlador(this);
						if (this.getTheGUIWorkSpace().setActiva(2)) salir();
				}	
				else if (respuesta==2) {
					
				}
			}
			
			else guardarYSalir();
			break;
		}
		case GUI_Principal_NULLATTR:{
			setNullAttrs(!nullAttrs);
			this.getTheGUIPrincipal().loadInfo();
			break;
		}
		case GUI_Principal_ConfirmarEliminaciones:{
			setConfirmarEliminaciones(!confirmarEliminaciones);
			this.getTheGUIPrincipal().loadInfo();
			break;
		}
		case GUI_Principal_ModoSoporte:{
			setModoSoporte(!modoSoporte);
			this.getTheGUIPrincipal().loadInfo();
			break;
		}
		case GUI_Principal_Cuadricula:{
			try {
				this.getTheGUIPrincipal().modoCuadricula(!cuadricula);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setCuadricula(!cuadricula);
			//this.getTheGUIPrincipal().loadInfo();
			break;
		}
		case GUI_Principal_Click_Imprimir:{
			this.getTheGUIPrincipal().imprimir();break;
		}
		case GUI_Principal_Click_ModoProgramador:{
			this.getTheGUIPrincipal().modoProgramador();break;
		}
		case GUI_Principal_Click_ModoDiseno:{
			this.getTheGUIPrincipal().modoDiseno();
			break;
		}
		case GUI_Principal_Click_ModoVerTodo:{
			this.getTheGUIPrincipal().modoVerTodo();
			break;
		}
		case GUI_Principal_Click_Salir:{
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) guardarYSalir();
				else if (respuesta==0) {
						theGUIWorkSpace = new GUI_SaveAs(true);
						theGUIWorkSpace.setControlador(this);
						if (this.getTheGUIWorkSpace().setActiva(2)) salir();
				}	
				else if (respuesta==2) {
					
				}
			}
			
			else guardarYSalir();
			break;
		}
		case GUI_Principal_Click_Submenu_Abrir:{
			theGUIWorkSpace = new GUI_SaveAs(true);
			theGUIWorkSpace.setControlador(this);
			theGUIWorkSpace.setModoSoporte(this.getModoSoporte());
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) {
						this.getTheGUIWorkSpace().setActiva(1);
				}else if (respuesta==0) {
						boolean guardado = this.getTheGUIWorkSpace().setActiva(2);
						if (guardado){
							this.getTheGUIWorkSpace().setActiva(1);
						}
				}		
			}else{
				this.getTheGUIWorkSpace().setActiva(1);
			}
			break;
		}
		
		case GUI_Principal_Click_Submenu_Abrir_Casos:{
			theGUIWorkSpace = new GUI_SaveAs(false);//si mandamos false se va al directorio de casos prueba
			theGUIWorkSpace.setControlador(this);
			theGUIWorkSpace.setModoSoporte(this.getModoSoporte());
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) {
						this.getTheGUIWorkSpace().setActiva(4);
				}else if (respuesta==0) {
						boolean guardado = this.getTheGUIWorkSpace().setActiva(2);
						if (guardado){
							this.getTheGUIWorkSpace().setActiva(4);
						}
				}		
			}else{
				this.getTheGUIWorkSpace().setActiva(4);
			}
			break;
		}
		
		case GUI_Principal_Click_Submenu_Recientes:{
			this.theGUIRecientes = new GUI_Recientes(archivosRecent.darRecientes(),this);
			break;
		}
		
		case GUI_Principal_Click_Submenu_Guardar:{
			theGUIWorkSpace = new GUI_SaveAs(true);
			theGUIWorkSpace.setControlador(this);
			this.getTheGUIWorkSpace().setActiva(2);
			getTheGUIPrincipal().setTitle(getTitle());
			break;
		}
		case GUI_Principal_EditarElemento:{
			if(datos instanceof TransferEntidad) {
				TransferEntidad te= (TransferEntidad)datos;
				if(this.getTheGUIModificarEntidad()==null)
					this.setTheGUIModificarEntidad(new GUI_ModificarEntidad());
				this.getTheGUIModificarEntidad().setEntidad(te);
				this.getTheGUIModificarEntidad().setActiva();
			}
			
			else if(datos instanceof TransferRelacion) {
				TransferRelacion tr= (TransferRelacion) datos;
				if(this.getTheGUIModificarRelacion()==null) {
					this.setTheGUIModificarRelacion(new GUI_ModificarRelacion());
				}
				this.getTheGUIModificarRelacion().setRelacion(tr);
				this.getTheGUIModificarRelacion().setActiva();
			}
			else if (datos instanceof TransferAtributo) {
				Vector<TransferDominio> lista = this.getTheServiciosDominios().getListaDeDominios();
				this.getTheGUIModificarAtributo().setListaDominios(lista);
				
				TransferAtributo ta=(TransferAtributo) datos;
				
				if(this.getTheGUIModificarAtributo()==null)
					this.setTheGUIModificarAtributo(new GUI_ModificarAtributo());
				//Le pasamos a la GUI el transfer del atributo seleccionado
				this.getTheGUIModificarAtributo().setTransferAtributo(ta);
				//Buscamos a quien pertenece este atributo
				
				
				String nombrePadre="";
				DAOEntidades daoEntidades = new DAOEntidades(this.getPath());
				Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
				
				DAORelaciones daoRelaciones = new DAORelaciones(this.getPath());
				Vector <TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
				
				for(int i=0;i<listaE.size();++i) {
					TransferEntidad transferE=listaE.get(i);
					Vector<String> listaA=transferE.getListaAtributos();
					for(int j=0;j<listaA.size();++j) {
						if(listaA.get(j).equals(Integer.toString(ta.getIdAtributo()))) {
							nombrePadre=transferE.getNombre();
						}
					}
				}
				for(int i=0;i<listaR.size();++i) {
					TransferRelacion transferR=listaR.get(i);
					Vector<String> listaA=transferR.getListaAtributos();
					for(int j=0;j<listaA.size();++j) {
						if(listaA.get(j).equals(Integer.toString(ta.getIdAtributo()))) {
							nombrePadre=transferR.getNombre();
						}
					}
				}
				
				this.getTheGUIModificarAtributo().setNombrePadre(nombrePadre);
				this.getTheGUIModificarAtributo().setActiva();
			}
			break;
		}
		case GUI_Principal_Click_Submenu_GuardarComo:{
			theGUIWorkSpace = new GUI_SaveAs(true);
			theGUIWorkSpace.setControlador(this);
			this.getTheGUIWorkSpace().setActiva(3);
			getTheGUIPrincipal().setTitle(getTitle());
			break;
		}
		case GUI_Principal_Click_Submenu_Nuevo:{
			if (cambios){
				int respuesta = panelOpciones.setActiva(
						Lenguaje.text(Lenguaje.WISH_SAVE),
						Lenguaje.text(Lenguaje.DBCASE),true);
				if (respuesta==1) {
						filetemp.delete();
						this.getTheGUIWorkSpace().nuevoTemp();
						setCambios(false);
				}else if (respuesta==0) {
						theGUIWorkSpace = new GUI_SaveAs(true);
						theGUIWorkSpace.setControlador(this);
						if (this.getTheGUIWorkSpace().setActiva(2)){
							filetemp.delete();
							this.getTheGUIWorkSpace().nuevoTemp();
							setCambios(false);
						}
				}		
			}else{
				filetemp.delete();
				this.getTheGUIWorkSpace().nuevoTemp();
				setCambios(false);
			}
			break;
		}
		case GUI_Principal_CambiarLenguaje:{
			// Extraer lenguaje seleccionado
			String lenguaje = (String)datos;
			
			// Cambiar lenguaje
			Lenguaje.cargaLenguaje(lenguaje);
			
			/* guardar, "guardado", tempguarda... y todo eso. guardar en un temporal nuevo y luego abrirlo para dejarlo como estuviese*/ 
			boolean cambios = this.cambios;
			File fileguardar = this.fileguardar;
			boolean aux = this.auxDeshacer;
			try{
				if (filetemp.exists()){
					File guardado =File.createTempFile("dbcase", "xml");
					FileCopy(filetemp.getAbsolutePath(), guardado.getAbsolutePath());
					mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Lenguaje, guardado.getAbsolutePath());
					guardado.delete();
				}
				else this.getTheGUIWorkSpace().nuevoTemp();
			}
			catch(IOException e){}
					
			this.fileguardar=fileguardar;
			this.cambios = cambios;
			this.auxDeshacer = aux;
			
			break;
		}
		case GUI_Principal_CambiarTema:{
			theme.changeTheme((String)datos);
			/* guardar, "guardado", tempguarda... y todo eso. guardar en un temporal nuevo y luego abrirlo para dejarlo como estuviese*/ 
			boolean cambios = this.cambios;
			File fileguardar = this.fileguardar;
			boolean aux = this.auxDeshacer;
			try{
				if (filetemp.exists()){
					File guardado =File.createTempFile("dbcase", "xml");
					FileCopy(filetemp.getAbsolutePath(), guardado.getAbsolutePath());
					mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Tema, guardado.getAbsolutePath());
					guardado.delete();
				}
				else this.getTheGUIWorkSpace().nuevoTemp();
			}catch(IOException e){}
					
			this.fileguardar=fileguardar;
			this.cambios= cambios;
			this.auxDeshacer = aux;
			break;
		}
		/*
		 * Limpiar pantalla
		 */
		case GUI_Principal_Click_BotonLimpiarPantalla:{
			this.getTheServiciosSistema().reset();
			break;
		}
		/*
		 * Generacion del script SQL
		 */
		case GUI_Principal_Click_BotonGenerarModeloRelacional:{
			this.getTheServiciosSistema().generaModeloRelacional();
			break;
		}
		case GUI_Principal_Click_BotonGenerarScriptSQL:{
			TransferConexion tc = (TransferConexion) datos;
			this.getTheServiciosSistema().generaScriptSQL(tc);
			break;
		}
		case GUI_Principal_Click_BotonGenerarArchivoScriptSQL:{
			String texto = (String) datos;
			this.getTheServiciosSistema().exportarCodigo(texto, true);
			break;
		}
		case GUI_Principal_Click_BotonGenerarArchivoModelo:{
			String texto = (String) datos;
			this.getTheServiciosSistema().exportarCodigo(texto, false);
			break;
		}
		case GUI_Principal_Click_BotonEjecutarEnDBMS:{
			TransferConexion tc = (TransferConexion)datos;
			this.getTheGuiSeleccionarConexion().setConexion(tc);
			this.getTheGuiSeleccionarConexion().setActiva();
			break;
		}
		case GUI_Principal_Click_SubmenuAnadirEntidad:{
			Point2D punto = (Point2D) datos;
			this.getTheGUIInsertarEntidad().setPosicionEntidad(punto);
			this.getTheGUIInsertarEntidad().setActiva();
			break;
		}
		default:break;
		} // switch
	}

	// Mensajes que le mandan las GUIs al controlador
	public void mensajeDesde_GUI(TC mensaje, Object datos){
		switch(mensaje){
		
		case GUIInsertarAgregacion:{
			Vector v = (Vector) datos;
			TransferRelacion t = (TransferRelacion) v.elementAt(0); //relacion sobre el que se construye la agregacion
			String nombre = (String) v.elementAt(1); //nombre de la nueva agregacion
			TransferAgregacion agreg = new TransferAgregacion();
			boolean sepuede = true;
			
			//comprobamos que esa relaci�n no pertenece a alguna agregacion existente:
			Vector<TransferAgregacion> agregaciones = this.getTheServiciosAgregaciones().ListaDeAgregaciones();			
			for(int i = 0; i < agregaciones.size() && sepuede; ++i) {
				TransferAgregacion actual_agreg = agregaciones.get(i);
				Vector lista_relaciones = actual_agreg.getListaRelaciones();
				String relacionId =  (String) lista_relaciones.get(0); //solo hay una relacion por agregacion
				if( Integer.parseInt(relacionId) == t.getIdRelacion()) {
					JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELACION_YA_TIENE_AGREGACION), Lenguaje.text(Lenguaje.ERROR), 0);
					sepuede=false;
				}
			}
			
			if(sepuede) {
				agreg.setNombre(nombre);
				Vector relaciones = new Vector();
				this.getTheServiciosRelaciones().getSubesquema(t,relaciones);//tenemos que quitar del menu conceptual que se pueda hacer sobre entidades(comentalo)

				if(relaciones.size() == 1) {
					agreg.setListaRelaciones(relaciones);
					agreg.setListaAtributos(new Vector());
					
					this.getTheServiciosAgregaciones().anadirAgregacion(agreg);
					ActualizaArbol(agreg);
					this.getTheServiciosSistema().reset();
					
				}
				
				else {
					JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.AGREG_MAS_RELACIONES), Lenguaje.text(Lenguaje.ERROR), 0);
				}
			}

			
			
			break;
		}
		
		case GUIInsertarEntidad_Click_BotonInsertar:{
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheServiciosEntidades().anadirEntidad(te,pilaDeshacer);
			ActualizaArbol(te);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarEntidadDebil_Click_BotonInsertar:{
			TransferEntidad te = (TransferEntidad) datos;
			boolean exito = this.getTheServiciosEntidades().SePuedeAnadirEntidad(te);
			this.getTheGUIInsertarEntidad().comprobadaEntidad(exito);
			ActualizaArbol(te);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIModificarAtributo_Click_ModificarAtributo:{
			Vector<Object> v= (Vector<Object>)datos;
			TransferAtributo ta= (TransferAtributo)v.get(0);
			this.antigoNombreAtributo = ta.getNombre();
			this.antiguoDominioAtributo = ta.getDominio();
			this.antiguoCompuestoAtribuo = ta.getCompuesto();
			this.antiguoMultivaloradoAtribuo = ta.getMultivalorado();
			this.antiguoNotnullAtribuo = ta.getNotnull();
			this.antiguoUniqueAtribuo = ta.getUnique();
			this.antiguoClavePrimaria = ta.getClavePrimaria();
			String nuevoNombre=(String)v.get(1);
			boolean clavePrimaraSelected=(boolean)v.get(2);
			boolean compuestoSelected=(boolean)v.get(3);
			boolean notNullSelected=(boolean)v.get(4);
			boolean uniqueSelected=(boolean)v.get(5);
			boolean multivaloradoSelected=(boolean)v.get(6);

			//Creamos un vector para renombrar
			Vector<Object> vRenombrar= new Vector<Object>();
			vRenombrar.add(ta);
			vRenombrar.add(nuevoNombre);
			if(!ta.getNombre().equals(nuevoNombre)) {
				this.getTheServiciosAtributos().renombrarAtributo(vRenombrar);
			}
			//Creamos un vector para modificar el dominio
			Vector <Object> vDominio=new Vector<Object>();
			vDominio.add(ta);
			String dominio=(String)v.get(7);
			if(v.size()==9) { //Significa que el vector tiene un campo tamaño
				String tamano=(String)v.get(8);	
				vDominio.add(dominio+"("+tamano+")");
				vDominio.add(tamano);
			}
			else {
				vDominio.add(dominio);
			}
			this.getTheServiciosAtributos().editarDomnioAtributo(vDominio);
			//Buscamos si el atributo pertenece a una entidad y si es asi a cual
			
			DAOEntidades daoEntidades = new DAOEntidades(this.getPath());
			Vector<TransferEntidad> entidades = daoEntidades.ListaDeEntidades();
			TransferEntidad te=new TransferEntidad();
			boolean encontrado=false;
			for(int i=0;i<entidades.size();++i) {
				Vector<String> atributos=entidades.get(i).getListaAtributos();
				for(int j=0;j<atributos.size();j++) {
					if(atributos.get(j).equals(Integer.toString(ta.getIdAtributo()))) {
						te=entidades.get(i);
						encontrado=true;
					}
				}
			}
			//Modificamos los valores ClavePrimaria, Compuesto, Unique, NotNull y Multivalorado si es necesario
			if(encontrado && clavePrimaraSelected!=ta.isClavePrimaria()) {
				Vector<Object> vClavePrimaria= new Vector<Object>();
				vClavePrimaria.add(ta);
				vClavePrimaria.add(te);
				//vClavePrimaria.add(0);
				this.getTheServiciosAtributos().editarClavePrimariaAtributo(vClavePrimaria);
			}
			if(compuestoSelected!=ta.getCompuesto()) {
				this.getTheServiciosAtributos().editarCompuestoAtributo(ta);
			}
			if(uniqueSelected!=ta.getUnique()) {
				this.getTheServiciosAtributos().editarUniqueAtributo(ta);
			}
			if(notNullSelected!=ta.getNotnull()) {
				this.theServiciosAtributos.editarNotNullAtributo(ta);
			}
			if(multivaloradoSelected!=ta.isMultivalorado()) {
				this.getTheServiciosAtributos().editarMultivaloradoAtributo(ta);
			}
			ActualizaArbol(ta);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIModificarEntidad_Click_ModificarEntidad:{
			Vector<Object> v= (Vector<Object>)datos;
			TransferEntidad te= (TransferEntidad) v.get(0);
			boolean eraDebil=te.isDebil();
			String nuevoNombre=(String) v.get(1);
			boolean debilitar=(boolean)v.get(2);
			//Si se ha modificado su nombre la renombramos
			if(te.getNombre()!=nuevoNombre) {
				Vector<Object> v1= new Vector<Object>();
				v1.add(te);
				v1.add(nuevoNombre);
				this.getTheServiciosEntidades().renombrarEntidad(v1);
			}
			//Si se ha debilitado añadimos la relación entre la entidad modificada y la especificada en la GUI
			if(debilitar) {
				boolean debilitada = false;
				if(!eraDebil) {
					this.getTheServiciosEntidades().debilitarEntidad(te);
				}
				TransferEntidad te2=(TransferEntidad)v.get(4);
				TransferRelacion tr=(TransferRelacion)v.get(3);
				if(this.getTheServiciosRelaciones().SePuedeAnadirRelacion(tr)) {
					Vector<Object>v2=new Vector<Object>();
					v2.add(tr);
					v2.add(te);
					v2.add(Integer.toString(1));//Inicio
					v2.add("n");//Fin
					v2.add("");//Rol
					//INcluimos en el vector MarcadaConCardinalidad(true), MarcadaConParticipacion(false), MarcadaConMinMax(false)
					v2.add(true);
					v2.add(false);
					v2.add(false);
					//Debilitamos la entidad y añadimos a la nueva relacion
					this.getTheServiciosRelaciones().anadirRelacion(tr, 1);//mandamos un 1, se anade la relacion por otro metodo
					this.getTheServiciosRelaciones().anadirEntidadARelacion(v2, 1);//mandamos un 1, se anade la relacion por otro metodo
					//dudaa
					Vector<Object>v3=new Vector<Object>();
					v3.add(tr);
					v3.add(te2);
					v3.add(Integer.toString(0));//Inicio
					v3.add("1");//Fin
					v3.add("");//Rol
					//INcluimos en el vector MarcadaConCardinalidad(true), MarcadaConParticipacion(false), MarcadaConMinMax(false)
					v3.add(true);
					v3.add(false);
					v3.add(false);
					this.getTheServiciosRelaciones().anadirEntidadARelacion(v3, 1);//mandamos un 1, se anade la relacion por otro metodo
				}
				else if(!eraDebil) {
					this.getTheServiciosEntidades().debilitarEntidad(te);
				}
			}
			else if(eraDebil) {
				this.getTheServiciosEntidades().debilitarEntidad(te);
				DAORelaciones dao = new DAORelaciones(this.getPath());
				Vector <TransferRelacion> lista_relaciones = dao.ListaDeRelaciones();
				//this.getTheServiciosRelaciones().restablecerDebilidadRelaciones();
				for(int i = 0; i<lista_relaciones.size(); ++i) {
					TransferRelacion tr = lista_relaciones.get(i);
					Vector<EntidadYAridad> eya = tr.getListaEntidadesYAridades();
					for(int j = 0; j < eya.size(); ++j) {
						if (eya.get(j).getEntidad() == te.getIdEntidad() && tr.getTipo().equals("Debil"))
							this.getTheServiciosRelaciones().debilitarRelacion(tr);
					}					
				}
				
			}
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarEntidadDebil_Entidad_Relacion_Repetidos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENTITY_REL), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case GUIRenombrarEntidad_Click_BotonRenombrar:{
			Vector v = (Vector) datos;
			this.getTheServiciosEntidades().renombrarEntidad(v);
			
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirAtributoEntidad_Click_BotonAnadir:{
			Vector<Transfer> vectorTransfers = (Vector<Transfer>)datos;
			this.getTheServiciosEntidades().anadirAtributo(vectorTransfers);	
			ActualizaArbol((Transfer)vectorTransfers.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirAtributoAgregacion_Click_BotonAnadir:{
			Vector<Transfer> vectorTransfers = (Vector<Transfer>)datos;
			this.getTheServiciosAgregaciones().anadirAtributo(vectorTransfers);	
			ActualizaArbol((Transfer)vectorTransfers.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirAtributoRelacion_Click_BotonAnadir:{
			Vector<Transfer> vectorTransfers = (Vector<Transfer>)datos;
			this.getTheServiciosRelaciones().anadirAtributo(vectorTransfers);
			ActualizaArbol((Transfer)vectorTransfers.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirAtributoEntidad_ActualizameLaListaDeDominios: {
			this.getTheServiciosDominios().ListaDeDominios();
			break;
		}
		case GUIPonerRestriccionesAEntidad_Click_BotonAceptar:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosEntidades().setRestricciones(v);	
			ActualizaArbol((Transfer)v.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIPonerRestriccionesARelacion_Click_BotonAceptar:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosRelaciones().setRestricciones(v);	
			ActualizaArbol((Transfer)v.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIPonerRestriccionesAAtributo_Click_BotonAceptar:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosAtributos().setRestricciones(v);	
			ActualizaArbol((Transfer)v.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarRestriccionAEntidad_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosEntidades().anadirRestriccion(v);	
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarRestriccionAEntidad_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosEntidades().quitarRestriccion(v);
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarRestriccionAAtributo_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosAtributos().anadirRestriccion(v);	
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarRestriccionAAtributo_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosAtributos().quitarRestriccion(v);
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarRestriccionARelacion_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosRelaciones().anadirRestriccion(v);	
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarRestriccionARelacion_Click_BotonAnadir:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosRelaciones().quitarRestriccion(v);	
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIPonerUniquesAEntidad_Click_BotonAceptar:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosEntidades().setUniques(v);	
			ActualizaArbol((Transfer)v.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIPonerUniquesARelacion_Click_BotonAceptar:{
			Vector v  = (Vector<Transfer>)datos;
			this.getTheServiciosRelaciones().setUniques(v);	
			ActualizaArbol((Transfer)v.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		
		case GUIEditarDominioAtributo_ActualizameLaListaDeDominios: {
			this.getTheServiciosDominios().ListaDeDominios();
			break;
		}
		case GUIAnadirSubAtributoAtributo_ActualizameLaListaDeDominios: {
			this.getTheServiciosDominios().ListaDeDominios();
			break;
		}
		case GUIAnadirAtributoRelacion_ActualizameLaListaDeDominios:{
			this.getTheServiciosDominios().ListaDeDominios();
			break;
		}
		case GUIRenombrarAtributo_Click_BotonRenombrar:{
			Vector v = (Vector) datos;
			this.getTheServiciosAtributos().renombrarAtributo(v);	
			
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIRenombrarAgregacion_Click_BotonRenombrar:{
			Vector v = (Vector) datos;
			TransferRelacion rel = (TransferRelacion)v.get(0);
			String nombre = (String)v.get(1);
			this.getTheServiciosAgregaciones().renombrarAgregacion(rel,nombre);	
			
			this.getTheGUIRenombrarAgregacion().setInactiva();
			
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEditarDominioAtributo_Click_BotonEditar:{
			//hola
			Vector v = (Vector) datos;
			TransferAtributo ta = (TransferAtributo) v.get(0);
			this.antiguoDominioAtributo = ta.getDominio();
			this.getTheServiciosAtributos().editarDomnioAtributo(v);
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEditarCompuestoAtributo_Click_BotonAceptar:{
			TransferAtributo ta = (TransferAtributo)datos;
			this.getTheServiciosAtributos().editarCompuestoAtributo(ta);	
			ActualizaArbol(ta);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEditarMultivaloradoAtributo_Click_BotonAceptar:{
			TransferAtributo ta = (TransferAtributo)datos;
			this.getTheServiciosAtributos().editarMultivaloradoAtributo(ta);		
			ActualizaArbol(ta);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirSubAtributoAtributo_Click_BotonAnadir:{
			Vector<Transfer> vectorTransfers = (Vector<Transfer>)datos;
			this.getTheServiciosAtributos().anadirAtributo(vectorTransfers);		
			ActualizaArbol((Transfer)vectorTransfers.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEditarClavePrimariaAtributo_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIEditarClavePrimariaAtributo_ActualizameListaAtributos:{
			this.getTheServiciosAtributos().ListaDeAtributos();
			break;
		}
		case GUIEditarClavePrimariaAtributo_Click_BotonAceptar:{
			Vector<Object> vectorAtributoyEntidad = (Vector<Object>)datos;
			//vectorAtributoyEntidad.add(0);
			this.getTheServiciosAtributos().editarClavePrimariaAtributo(vectorAtributoyEntidad);
			ActualizaArbol((Transfer)vectorAtributoyEntidad.get(1));
			this.getTheServiciosSistema().reset();
			break;
		}
		/*
		 * Relaciones
		 */
		case GUIInsertarRelacion_Click_BotonInsertar:{
			TransferRelacion tr = (TransferRelacion) datos;
			this.getTheServiciosRelaciones().anadirRelacion(tr, 0);
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIInsertarRelacionDebil_Click_BotonInsertar:{
			TransferRelacion tr = (TransferRelacion) datos;
			boolean exito =this.getTheServiciosRelaciones().SePuedeAnadirRelacion(tr);
			this.getTheGUIInsertarEntidad().comprobadaRelacion(exito);
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIRenombrarRelacion_Click_BotonRenombrar:{
			Vector<Object> v = (Vector) datos; 
			TransferRelacion tr= (TransferRelacion) v.get(0);
			String nuevoNombre = (String) v.get(1);
			this.getTheServiciosRelaciones().renombrarRelacion(tr,nuevoNombre);
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEstablecerEntidadPadre_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIEstablecerEntidadPadre_ClickBotonAceptar:{
			Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
			this.getTheServiciosRelaciones().establecerEntidadPadreEnRelacionIsA(relacionIsAyEntidadPadre);
			ActualizaArbol((Transfer)relacionIsAyEntidadPadre.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarEntidadPadre_ClickBotonSi:{
			TransferRelacion tr = (TransferRelacion) datos;
			//this.idPadreAntigua = tr.getEntidadYAridad(0).getEntidad();
			Vector<EntidadYAridad> eyaV = tr.getListaEntidadesYAridades();
			EntidadYAridad eya = (EntidadYAridad) eyaV.get(0);
			int idPadre = eya.getEntidad();
			TransferEntidad te = new TransferEntidad();
			for (int i = 0; i < this.listaEntidades.size(); ++i) {
				if(idPadre == this.listaEntidades.get(i).getIdEntidad()) this.padreAntiguo = this.listaEntidades.get(i);
			}
			
			//obtenemos las hijas
			Vector<TransferEntidad> th = new Vector<TransferEntidad>();
			Vector<EntidadYAridad> hijas = new Vector<EntidadYAridad>();
			for (int i = 1; i < eyaV.size(); i++) {
				EntidadYAridad eyaH = (EntidadYAridad) eyaV.get(i);
				hijas.add(eyaH);
			}
			
			for(int i = 0; i < hijas.size(); ++i) {
				EntidadYAridad e = (EntidadYAridad) hijas.get(i);
				int idHija = e.getEntidad();
				for (int j = 0; j < this.listaEntidades.size(); ++j) {
					if(idHija == this.listaEntidades.get(j).getIdEntidad()) th.add(this.listaEntidades.get(j));
				}
			}
			
			this.hijosAntiguo = th;
			
			
			this.getTheServiciosRelaciones().quitarEntidadPadreEnRelacionIsA(tr);
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIAnadirEntidadHija_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIAnadirEntidadHija_ClickBotonAnadir:{
			Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
			this.getTheServiciosRelaciones().anadirEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
			ActualizaArbol((Transfer)relacionIsAyEntidadPadre.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarEntidadHija_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIQuitarEntidadHija_ClickBotonQuitar:{
			Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
			this.getTheServiciosRelaciones().quitarEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
			ActualizaArbol((Transfer)relacionIsAyEntidadPadre.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		/*
		 * Relaciones normales
		 */
		case GUIAnadirEntidadARelacion_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIAnadirEntidadARelacion_ClickBotonAnadir:{
			// v tiene: [transferRelacion, idEntidad, inicioRango, finalRango, rol]
			Vector v = (Vector) datos;
			//Vamos a controlar que no se añada una segunda entidad débil a una relación débil
			TransferRelacion tr = (TransferRelacion)v.get(0);
			TransferEntidad te = (TransferEntidad)v.get(1);
			boolean cardinalidadSeleccionada=(boolean)v.get(5);
			boolean participacionSeleccionada=(boolean)v.get(6);
			boolean minMaxSeleccionado=(boolean) v.get(7);
			boolean cardinalidadMax1Seleccionada;
			if(v.size()==8) cardinalidadMax1Seleccionada=false;
			else cardinalidadMax1Seleccionada=(boolean) v.get(8);
			
			tr.setRelacionConCardinalidad(cardinalidadSeleccionada);
			tr.setRelacionConParticipacion(participacionSeleccionada);
			tr.setRelacionConMinMax(minMaxSeleccionado);
			tr.setRelacionConCardinalidad1(cardinalidadMax1Seleccionada);
			
			Vector<EntidadYAridad> vectorTupla = tr.getListaEntidadesYAridades();
			boolean relDebil = tr.getTipo().equals("Debil");
			boolean entDebil = te.isDebil();
			boolean relTieneEntDebil = false; 
			for(int i=0; i<vectorTupla.size(); i++){
				int entidad =vectorTupla.get(i).getEntidad();
				if(this.getTheServiciosEntidades().esDebil(entidad)){
					relTieneEntDebil=true;
					break;
				}
			}
			if(relDebil && entDebil && relTieneEntDebil)
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ALREADY_WEAK_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
			else this.getTheServiciosRelaciones().anadirEntidadARelacion(v, 0);

			//a�adimos la relacion a la entidad para que sepa a que relaciones esta conectada

			this.getTheServiciosEntidades().anadirRelacionAEntidad(v);
			
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIQuitarEntidadARelacion_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;			
		}
		case GUIQuitarEntidadARelacion_ClickBotonQuitar:{
			//Vector<Transfer> v = (Vector<Transfer>) datos;
			Vector<Object> v = (Vector<Object>) datos;
			this.getTheServiciosRelaciones().quitarEntidadARelacion(v);
			ActualizaArbol((Transfer)v.get(0));
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIEditarCardinalidadEntidad_ActualizameListaEntidades:{
			this.getTheServiciosEntidades().ListaDeEntidades();
			break;
		}
		case GUIEditarCardinalidadEntidad_ClickBotonEditar:{
			Vector<Object> v = (Vector<Object>) datos;
			TransferRelacion tr=(TransferRelacion)v.get(0);
			//Esto es para corregir inconsistencias en relaciones ternarias y de orden superior
			
			boolean cardinalidadSeleccionada=(boolean)v.get(6);
			boolean participacionSeleccionada=(boolean)v.get(7);
			boolean minMaxSeleccionado=(boolean) v.get(8);
			boolean cardinalidadMax1Seleccionada=(boolean)v.get(9);
			
			tr.setRelacionConCardinalidad(cardinalidadSeleccionada);
			tr.setRelacionConParticipacion(participacionSeleccionada);
			tr.setRelacionConMinMax(minMaxSeleccionado);
			tr.setRelacionConCardinalidad1(cardinalidadMax1Seleccionada);
			
			this.getTheServiciosRelaciones().editarAridadEntidad(v);
			ActualizaArbol(tr);
			this.getTheServiciosSistema().reset();
			break;
		}
		/*
		 * Dominios
		 */
		case GUIInsertarDominio_Click_BotonInsertar:{
			TransferDominio td = (TransferDominio) datos;
			this.getTheServiciosDominios().anadirDominio(td);
			
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIRenombrarDominio_Click_BotonRenombrar:{
			Vector<Object> v = (Vector) datos; 
			
			TransferDominio td = (TransferDominio) v.get(0);
			String nuevoNombre = (String) v.get(1);
			String dominioRenombrado = td.getNombre();
			this.getTheServiciosAtributos().ListaDeAtributos();
			int cont = 0;
			TransferAtributo ta = new TransferAtributo(this);
			while (cont < listaAtributos.size()){
				ta = listaAtributos.get(cont);
				if (ta.getDominio().equals(dominioRenombrado)){
					Vector<Object> vect = new Vector();
					vect.add(ta);
					vect.add(nuevoNombre);
					
					this.getTheServiciosAtributos().editarDomnioAtributo(vect);
				}
				cont++;
			}
			this.getTheServiciosDominios().renombrarDominio(v);
			this.getTheServiciosSistema().reset();
			break;
		}
		case GUIModificarDominio_Click_BotonEditar:{
			Vector<Object> v = (Vector) datos; 
			this.getTheServiciosDominios().modificarDominio(v);
			
			this.getTheServiciosSistema().reset();
			break;
		}

		/*
		 * Conectar a DBMS
		 */
		case GUIConfigurarConexionDBMS_Click_BotonEjecutar:{
			TransferConexion tc = (TransferConexion)datos;
			this.getTheServiciosSistema().ejecutarScriptEnDBMS(tc, this.theGUIPrincipal.getInstrucciones());
			break;
		}
		
		case GUIConexionDBMS_PruebaConexion:{
			TransferConexion tc = (TransferConexion)datos;
			this.getTheServiciosSistema().compruebaConexion(tc);
			break;
		}
		case GUIReport_ReportarIncidencia:{
			Vector<Object> d=(Vector<Object>) datos;
			String textoIncidencia=(String)d.get(0);
			boolean anadirDiagrama=(boolean) d.get(1);
			this.getTheServiciosReporte().crearIncidencia(textoIncidencia, anadirDiagrama);
			break;
		}
		
		default: break;
		} // Switch	
	}
	

	private void borrarSiguientesDeshacer(){
		File directory = new File(System.getProperty("user.dir")+"/deshacer");
		if (directory.exists()) {
			for (File file: Objects.requireNonNull(directory.listFiles())) {
	            if (!file.isDirectory()) {
	            	String str = file.getAbsolutePath();
	            	
	                file.delete();
	            }
	        }
		}
	}

		

	// Mensajes que mandan los Servicios de Entidades al Controlador
	public void mensajeDesde_SE(TC mensaje, Object datos){

		int intAux = 2;
		if (mensaje == TC.SE_EliminarEntidad_HECHO) {
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
			intAux = (int) aux.get(2);
		}
		
		if(mensaje == TC.SE_MoverPosicionEntidad_HECHO || mensaje == TC.SE_InsertarEntidad_HECHO || mensaje == TC.SE_RenombrarEntidad_HECHO || mensaje == TC.SE_AnadirAtributoAEntidad_HECHO || (mensaje == TC.SE_EliminarEntidad_HECHO && intAux == 0)) {
			//this.ultimoMensaje = mensaje;
			//this.ultimosDatos = datos;
			
			//this.borrarSiguientesDeshacer();
			
			this.guardarDeshacer();
			
			this.auxDeshacer = true;
			
			if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
			
			if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
		}
		
		
		switch(mensaje){

		/*
		 * Listar entidades
		 */
		case SE_ListarEntidades_HECHO: {
			this.getTheGUIPrincipal().setListaEntidades((Vector) datos);
			this.getTheGUIEstablecerEntidadPadre().setListaEntidades((Vector) datos);
			this.getTheGUIAnadirEntidadHija().setListaEntidades((Vector)datos);
			this.getTheGUIQuitarEntidadHija().setListaEntidades((Vector)datos);
			this.getTheGUIAnadirEntidadARelacion().setListaEntidades((Vector)datos);
			this.getTheGUIQuitarEntidadARelacion().setListaEntidades((Vector)datos);
			this.getTheGUIEditarCardinalidadEntidad().setListaEntidades((Vector)datos);
			this.getTheGUIInsertarEntidad().setListaEntidades((Vector)datos);
			this.getTheGUIModificarEntidad().setListaEntidades((Vector)datos);
			this.setListaEntidades((Vector)datos);
			break;
		}
		/*
		 * Insercion de entidades
		 */
		case SE_InsertarEntidad_ERROR_NombreDeEntidadEsVacio:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadEsVacio:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_InsertarEntidad_ERROR_NombreDeEntidadYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
		
			break;
		}
		
		case SE_InsertarRelacion_ERROR_NombreDeEntidadYaExisteComoAgregacion:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_AGREG_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
		
			break;
		}
		
		case SE_InsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_InsertarEntidad_ERROR_DAO:{
			this.getTheGUIInsertarEntidad().setInactiva();
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SE_InsertarEntidad_HECHO:{
			
			
			this.getTheGUIInsertarEntidad().setInactiva();
			setCambios(true);
			TransferEntidad te = (TransferEntidad) datos;
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarEntidad, te);
			//this.listaEntidades.add(te);
			break;
		}

		/*
		 * Renombrar entidades
		 */
		case SE_RenombrarEntidad_ERROR_NombreDeEntidadEsVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExiste:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_RenombrarEntidad_ERROR_DAOEntidades:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_RenombrarEntidad_ERROR_DAORelaciones:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_RenombrarEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarEntidad, te);
			this.getTheGUIRenombrarEntidad().setInactiva();
			break;
		}
		/*
		 * Debilitar/Fortalecer una entidad
		 */
		case SE_DebilitarEntidad_ERROR_DAOEntidades:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_DebilitarEntidad_HECHO:{
			
			
			TransferEntidad te = (TransferEntidad) datos;
			setCambios(true);			
			ActualizaArbol(te);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_DebilitarEntidad, te);
			break;
		}
		/*
		 * Añadir atributo a una relacion
		 */
		case SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoVacio:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ATTRIB_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ATTRIB_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_AnadirAtributoAEntidad_ERROR_TamanoNoEsEntero:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE3), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_AnadirAtributoAEntidad_ERROR_TamanoEsNegativo:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE2), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_AnadirAtributoAEntidad_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			this.getTheGUIAnadirAtributoRelacion().setInactiva();
			break;
		}
		case SE_AnadirAtributoAEntidad_ERROR_DAOEntidades:{
			Vector<Transfer> v = (Vector<Transfer>) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			this.getTheGUIAnadirAtributoRelacion().setInactiva();
			break;
		} 
		case SE_AnadirAtributoAEntidad_HECHO:{
			
			
			Vector<Transfer> v = (Vector<Transfer>) datos;
			v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirAtributoAEntidad, v);
			this.getTheGUIAnadirAtributoEntidad().setInactiva();
			//meter un if para cuando ya este
			TransferAtributo ta = (TransferAtributo) v.get(1);
			boolean esta = false;
			for (int i = 0; i < this.listaAtributos.size(); ++i) {
				if(ta.getIdAtributo() == this.listaAtributos.get(i).getIdAtributo()) esta = true;
			}
			
			if (!esta) this.listaAtributos.add(ta);
			break;
		}
		/*
		 * Elimimacion de una entidad
		 */
		case SE_EliminarEntidad_ERROR_DAOEntidades:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SE_EliminarEntidad_HECHO:{
			
			
			((Vector)datos).get(0);
			setCambios(true);
			((Vector)datos).get(1);
			
			ActualizaArbol(null);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarEntidad, datos);
			break;
		}
		/*
		 * Mover Entidad en el panel de diseno (cambiar la posicion)
		 */
		case SE_MoverPosicionEntidad_ERROR_DAOEntidades:{
			TransferEntidad te = (TransferEntidad) datos;
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverEntidad_ERROR, te);
			break;
		}
		case SE_MoverPosicionEntidad_HECHO:{
			
			
			setCambios(true);
			TransferEntidad te = (TransferEntidad) datos;
			this.posAux = te.getPosicion();
			for (int i = 0; i < this.listaEntidades.size(); ++i) {
				if(te.getNombre() == this.listaEntidades.get(i).getNombre()) {
					posAux = this.listaEntidades.get(i).getPosicion();
				}
			}
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverEntidad_HECHO, te);
			break;
		}
		/*
		 * Restricciones a entidad
		 */
		case SE_AnadirRestriccionAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionEntidad, te);
			//this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
			break;
		}
		case SE_QuitarRestriccionAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionEntidad, te);
			break;
		}
		case SE_setRestriccionesAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesEntidad, te);
			break;
		}
		/*
		 * Restricciones a entidad
		 */
		case SE_AnadirUniqueAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			TransferEntidad clon_entidad =te.clonar();
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirUniqueEntidad, clon_entidad);
			//this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
			break;
		}
		case SE_QuitarUniqueAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			TransferEntidad clon_entidad =te.clonar();
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarUniqueEntidad, clon_entidad);
			break;
		}
		case SE_setUniquesAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(1);
			TransferEntidad clon_entidad =te.clonar();
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniquesEntidad, clon_entidad);
			break;
		}
		case SE_setUniqueUnitarioAEntidad_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferEntidad te = (TransferEntidad) v.get(0);
			TransferEntidad clon_entidad =te.clonar();
			setCambios(true);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniqueUnitarioEntidad, clon_entidad);
			break;
		}
		default:
			break;
		} // switch
	}

	// Mensajes que mandan los Servicios de Dominios al Controlador
	public void mensajeDesde_SD(TC mensaje, Object datos){
		
		
		
		if(mensaje == TC.SD_InsertarDominio_HECHO || mensaje == TC.SD_RenombrarDominio_HECHO || mensaje == TC.SD_EliminarDominio_HECHO) {
			//this.ultimoMensaje = mensaje;
			//this.ultimosDatos = datos;
			
			
	
			this.guardarDeshacer();
			
			this.auxDeshacer = true;
			if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
			
			if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
		}
		
		
		switch(mensaje){
		/*
		 * Listar dominios
		 */
		case SD_ListarDominios_HECHO: {
			this.getTheGUIPrincipal().setListaDominios((Vector) datos);
			this.getTheGUIAnadirAtributoEntidad().setListaDominios((Vector) datos);
			this.getTheGUIAnadirAtributo().setListaDominios((Vector) datos);
			this.getTheGUIEditarDominioAtributo().setListaDominios((Vector) datos);
			this.getTheGUIAnadirAtributoRelacion().setListaDominios((Vector) datos);
			this.getTheGUIAnadirSubAtributoAtributo().setListaDominios((Vector) datos);
			this.getTheGUIModificarAtributo().setListaDominios((Vector)datos);
			break;
		}
		/*
		 * Insercion de dominios
		 */
		case SD_InsertarDominio_ERROR_NombreDeDominioEsVacio:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}		
		case SD_InsertarDominio_ERROR_NombreDeDominioYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_InsertarDominio_ERROR_DAO:{
			this.getTheGUIInsertarDominio().setInactiva();
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.DOMAINS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_InsertarDominio_ERROR_ValorNoValido:{
			Vector v = (Vector) datos;
			v.get(0);
			String error = (String) v.get(1);
			JOptionPane.showMessageDialog(null, error, Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_InsertarDominio_HECHO:{
			
			
			this.getTheGUIInsertarDominio().setInactiva();
			TransferDominio td = (TransferDominio) datos;
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarDominio, td);
			break;
		}
		
		/*
		 * Renombrar dominios
		 */
		case SD_RenombrarDominio_ERROR_NombreDeDominioEsVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_RenombrarDominio_ERROR_NombreDeDominioYaExiste:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_RenombrarDominio_ERROR_DAODominios:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.DOMAINS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_RenombrarDominio_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferDominio td = (TransferDominio) v.get(0);
			v.get(1);
			v.get(2);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarDominio, td);
			this.getTheGUIRenombrarDominio().setInactiva();
			break;
		}
		/*
		 * Elimimacion de un dominio
		 */
		case SD_EliminarDominio_ERROR_DAODominios:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.DOMAINS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_EliminarDominio_HECHO:{
			
			
			setCambios(true);
			TransferDominio td = (TransferDominio) ((Vector)datos).get(0);
	
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarDominio, td);
			break;
		}
		/*
		 * Modificar dominios
		 */
		case SD_ModificarTipoBaseDominio_HECHO:{
			
			
			setCambios(true);
			Vector v = (Vector) datos;
			TransferDominio td = (TransferDominio) v.get(0);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ModificarTipoBaseDominio, td);
			break;
		}
    	case SD_ModificarTipoBaseDominio_ERROR_DAODominios:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_ModificarTipoBaseDominio_ERROR_TipoBaseDominioEsVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_TYPE_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_ModificarElementosDominio_HECHO:{
			
			
			setCambios(true);
			Vector v = (Vector) datos;
			TransferDominio td = (TransferDominio) v.get(0);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ModificarTipoBaseDominio, td);
			this.getTheGUIModificarElementosDominio().setInactiva();
			break;
		}
    	case SD_ModificarElementosDominio_ERROR_DAODominios:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_DOM_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_ModificarElementosDominio_ERROR_ElementosDominioEsVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_VALUES), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SD_ModificarElementosDominio_ERROR_ValorNoValido:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_VALUE), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		default: break;
		} // switch
	}
	
	// Mensajes que mandan los Servicios de Atributos al Controlador
	public void mensajeDesde_SA(TC mensaje, Object datos){
		int intAux = 2;
		if (mensaje == TC.SA_EliminarAtributo_HECHO) {
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
			intAux = (int) aux.get(2);
		}
		
		/*if (mensaje == TC.SA_EditarClavePrimariaAtributo_HECHO){
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
			intAux = (int) aux.get(2);
		}
		
		if (mensaje == TC.SA_EditarUniqueAtributo_HECHO){
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
			intAux = (int) aux.get(1);
		}*/
		
		
		if(mensaje == TC.SA_MoverPosicionAtributo_HECHO || (mensaje == TC.SA_EliminarAtributo_HECHO && intAux == 0)  || mensaje == TC.SA_EditarUniqueAtributo_HECHO || mensaje == TC.SA_EditarDominioAtributo_HECHO || mensaje == TC.SA_EditarCompuestoAtributo_HECHO || mensaje == TC.SA_EditarMultivaloradoAtributo_HECHO || mensaje == TC.SA_EditarNotNullAtributo_HECHO || mensaje == TC.SA_AnadirSubAtributoAtributo_HECHO || mensaje == TC.SA_EditarClavePrimariaAtributo_HECHO) {
			//this.ultimoMensaje = mensaje;
			//this.ultimosDatos = datos;
			
			
			this.guardarDeshacer();
			
			this.auxDeshacer = true;
			if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
			
			if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
		}
		
		
		
		switch(mensaje){

		case SA_ListarAtributos_HECHO: {
			// Ponemos la lista de atributos actualizada a las GUIs que lo necesitan
			this.getTheGUIPrincipal().setListaAtributos((Vector) datos);
			this.setListaAtributos((Vector) datos);
			break;
		}
		/*
		 * Eliminacion de atributos
		 */
		case SA_EliminarAtributo_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SA_EliminarAtributo_HECHO:{
			
			setCambios(true);
			Vector<Transfer> vectorAtributoYElemMod = (Vector<Transfer>) datos;
			vectorAtributoYElemMod.get(0);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarAtributo, vectorAtributoYElemMod);
			ActualizaArbol(null);
			break;
		}
		/*
		 * Renombrar atributo
		 */
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ATTRIB_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;			
		}
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_SUBATR_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;			
		}
		case SA_RenombrarAtributo_ERROR_DAOAtributos:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_RenombrarAtributo_HECHO:{
	
			
			setCambios(true);
			Vector v = (Vector) datos;
			TransferAtributo ta = (TransferAtributo) v.get(0);
			String antiguoNombre = (String) v.get(2);
			
			TransferAtributo clon_atributo = ta.clonar();
			Vector v1 = new Vector();
			v1.add(clon_atributo);
			v1.add(antiguoNombre);
			this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_ModificarUniqueAtributo,v1);
			
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarAtributo, ta);
			this.getTheGUIRenombrarAtributo().setInactiva();			
			break;
		}
		/*
		 * Editar dominio atributo
		 */
		case SA_EditarDominioAtributo_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE1), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarDominioAtributo_ERROR_TamanoEsNegativo:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE2), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarDominioAtributo_HECHO:{
		
			
			setCambios(true);
			TransferAtributo ta = (TransferAtributo) datos;
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarDominioAtributo, ta);
			this.getTheGUIEditarDominioAtributo().setInactiva();

			break;
		}
		/*
		 * Editar caracter compuesto de atributo
		 */
		case SA_EditarCompuestoAtributo_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarCompuestoAtributo_HECHO:{
			
			
			setCambios(true);
			TransferAtributo ta = (TransferAtributo) datos;
			ta.getNombre();
			ActualizaArbol(ta);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarCompuestoAtributo, ta);
			break;
		}
		/*
		 * Editar caracter multivalorado de atributo
		 */
		case SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarMultivaloradoAtributo_HECHO:{
			
			
			setCambios(true);
			TransferAtributo ta = (TransferAtributo) datos;
			ActualizaArbol(ta);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarMultivaloradoAtributo, ta);
			break;
		}

		case SA_EditarNotNullAtributo_ERROR_DAOAtributos:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_EditarNotNullAtributo_HECHO:{
			
			
			setCambios(true);
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarNotNullAtributo, ta);
			ActualizaArbol(ta);
			break;
		}
		case SA_EditarUniqueAtributo_ERROR_DAOAtributos:{
		/*	TransferAtributo ta = (TransferAtributo) datos;
			//JOptionPane.showMessageDialog(null, Lenguaje.getMensaje(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.getMensaje(Lenguaje.ERROR), 0);
			this.getTheGUIPrincipal().anadeMensajeAreaDeSucesos(
					"ERROR: No se ha podido editar el caracter unique del atributo \""+ ta.getNombre() + "\". " +
			"Se ha producido un error en el acceso al fichero de atributos.");*/
			break;
		}
		case SA_EditarUniqueAtributo_HECHO:{
			
			
			setCambios(true);
			Vector<Object> ve = (Vector<Object>) datos;
			TransferAtributo ta = (TransferAtributo) ve.get(0);
			ActualizaArbol(ta);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarUniqueAtributo, ta);
			break;
		}
		
		/*
		 * Añadir un subatributo a un atributo
		 */
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio:{
			Vector v = (Vector) datos;
			v.get(0);
			
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_SUBATTR_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste:{
			Vector v = (Vector) datos;
			v.get(0);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_SUBATR_NAME), Lenguaje.text(Lenguaje.ERROR), 0);

			break;
		}
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE1), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;	
		}
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE2), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;	
		}
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre:{
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;
		}
		case SA_AnadirSubAtributoAtributo_HECHO:{
			
			
			setCambios(true);
			Vector v = (Vector) datos;
			v.get(0);
			v.get(1);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirSubAtributoAAtributo, v);
			this.getTheGUIAnadirSubAtributoAtributo().setInactiva();
			
			TransferAtributo ta = (TransferAtributo) v.get(1);
			boolean esta = false;
			for (int i = 0; i < this.listaAtributos.size(); ++i) {
				if(ta.getIdAtributo() == this.listaAtributos.get(i).getIdAtributo()) esta = true;
			}
			
			if (!esta) this.listaAtributos.add(ta);
			break;
		}
		/*
		 * Editar atributo como clave primaria de una entidad
		 */
		case SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades:{
			Vector<Transfer> vt = (Vector<Transfer>) datos;
			vt.get(1);
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			break;	
		}
		case SA_EditarClavePrimariaAtributo_HECHO:{
			
			
			setCambios(true);
			Vector<Transfer> vt = (Vector<Transfer>) datos;
			vt.get(0);
			vt.get(1);
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarClavePrimariaAtributo, vt);
			break;
		}
		/*
		 * Restricciones a Atributo
		 */
		case SA_AnadirRestriccionAAtributo_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferAtributo te = (TransferAtributo) v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionAtributo, te);
			//this.getTheGUIAnadirRestriccionAAtributo().setInactiva();
			break;
		}
		case SA_QuitarRestriccionAAtributo_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferAtributo te = (TransferAtributo) v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionAtributo, te);
			break;
		}
		case SA_setRestriccionesAAtributo_HECHO:{
			
			
			Vector v = (Vector) datos;
			TransferAtributo te = (TransferAtributo) v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesAtributo, te);
			break;
		}
		/*
		 * Mover Atributo en el panel de diseno (cambiar la posicion)
		 */
		case SA_MoverPosicionAtributo_ERROR_DAOAtributos:{
			TransferAtributo ta = (TransferAtributo) datos;
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverAtributo_ERROR, ta);
			break;
		}
		case SA_MoverPosicionAtributo_HECHO:{
			
			
			setCambios(true);
			TransferAtributo ta = (TransferAtributo) datos;
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverAtributo_HECHO, ta);
			break;
		}
		default: break;
		} // switch
	}
	
	//mensajes que manda el ServivioAgregaciones al controlador
	public void mensajeDesde_AG(TC mensaje, Object datos) {

		if(mensaje == TC.SAG_RenombrarAgregacion_HECHO || mensaje == TC.SAG_InsertarAgregacion_HECHO || mensaje == TC.SAG_AnadirAtributoAAgregacion_HECHO || mensaje == TC.SAG_EliminarAgregacion_HECHO) {
			//this.ultimoMensaje = mensaje;
			//this.ultimosDatos = datos;
			
			
			this.guardarDeshacer();
			
			this.auxDeshacer = true;
			if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
			
			if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);

		}

		switch(mensaje) {
		
		case SAG_InsertarAgregacion_ERROR_NombreVacio: {
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_AG_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SAG_InsertarAgregacion_ERROR_NombreDeYaExiste: {
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_AGREG_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SAG_InsertarAgregacion_ERROR_DAO:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.AGGREGATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}
		case SAG_RenombrarAgregacion_ERROR_NombreVacio:{
			JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_AGREG_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
			break;
		}

		case SAG_ListarAgregacion_HECHO:{ // igual hay mas clases en las que hay que cambiar la lista de agregaciones
			this.getTheGUIPrincipal().setListaAgregaciones((Vector) datos);
			break;
		}
		
		case SAG_InsertarAgregacion_HECHO:{
			setCambios(true);
			//this.getTheGUIInsertarRelacion().setInactiva();
			TransferAgregacion ta = (TransferAgregacion) datos;
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarAgregacion, ta);
			break;
		}

		case SAG_RenombrarAgregacion_HECHO:{
			setCambios(true);
			Vector v = (Vector) datos;
			TransferAgregacion tr = (TransferAgregacion) v.get(0);
			v.get(1);
			v.get(2);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarAgregacion, tr);
			break;
		}

		case SAG_AnadirAtributoAAgregacion_HECHO:{
			Vector<Transfer> v = (Vector<Transfer>) datos;
			v.get(0);
			v.get(1);
			setCambios(true);
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirAtributoAAgregacion, v);
			this.getTheGUIAnadirAtributoEntidad().setInactiva();
			
			//meter un if para cuando ya este
			TransferAtributo ta = (TransferAtributo) v.get(1);
			boolean esta = false;
			for (int i = 0; i < this.listaAtributos.size(); ++i) {
				if(ta.getIdAtributo() == this.listaAtributos.get(i).getIdAtributo()) esta = true;
			}
			
			if (!esta) this.listaAtributos.add(ta);
			break;
		}
		
		case SAG_EliminarAgregacion_HECHO:{
			
			TransferAgregacion tagre = (TransferAgregacion) datos;
			
			this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarAgregacion, tagre);
			ActualizaArbol(null);
			break;
		}

		default:
			break;

		}
	}
	
	// Mensajes que mandan los Servicios de Relaciones al Controlador
	public void mensajeDesde_SR(TC mensaje, Object datos){
		int intAux = 2;
		TransferRelacion taux = new TransferRelacion();
		if (mensaje == TC.SR_EliminarRelacionNormal_HECHO) {
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
			intAux = (int) aux.get(1);
		}
		
		if (mensaje == TC.SR_InsertarRelacion_HECHO) {
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
			//intAux = (int) aux.get(1);
			taux= (TransferRelacion) aux.get(0);
		}
		
		if (mensaje == TC.SR_AnadirEntidadARelacion_HECHO) {
			Vector<Object> aux = (Vector<Object>) datos;
			//intAux = (int) aux.get(aux.size()-1);
		}
			
		if(mensaje == TC.SR_MoverPosicionRelacion_HECHO || mensaje == TC.SR_InsertarRelacion_HECHO || mensaje == TC.SR_EliminarRelacion_HECHO || mensaje == TC.SR_RenombrarRelacion_HECHO || mensaje == TC.SR_AnadirAtributoARelacion_HECHO || mensaje == TC.SR_EstablecerEntidadPadre_HECHO || mensaje == TC.SR_QuitarEntidadPadre_HECHO || mensaje == TC.SR_AnadirEntidadHija_HECHO || mensaje == TC.SR_QuitarEntidadHija_HECHO || mensaje == TC.SR_EliminarRelacionIsA_HECHO || (mensaje == TC.SR_EliminarRelacionNormal_HECHO && intAux == 0) || mensaje == TC.SR_InsertarRelacionIsA_HECHO || mensaje == TC.SR_AnadirEntidadARelacion_HECHO || mensaje == TC.SR_QuitarEntidadARelacion_HECHO || mensaje == TC.SR_EditarCardinalidadEntidad_HECHO) {
			//this.ultimoMensaje = mensaje;
			//this.ultimosDatos = datos;
			
			
			this.guardarDeshacer();
			
			this.auxDeshacer = true;
			if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
			
			if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
			else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
		}
		
		
		
		
		switch(mensaje){

			case SR_ListarRelaciones_HECHO: {
				this.getTheGUIPrincipal().setListaRelaciones((Vector) datos);
				this.setListaRelaciones((Vector)datos);
				break;
			}
			/*
			 * Insercion de Relaciones
			 */
			case SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}		
			case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoAgregacion:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_AGREG_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				break;
			}
			case SR_InsertarRelacion_ERROR_NombreDelRolYaExiste:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ROL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			
			case SR_InsertarRelacion_ERROR_NombreDeRolNecesario:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.NECESARY_ROL), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			
			case SR_InsertarRelacion_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				this.getTheGUIInsertarRelacion().setInactiva();
				
				break;
			}
			case SR_InsertarRelacion_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIInsertarRelacion().setInactiva();
				Vector<Object> v = (Vector<Object>) datos;
				TransferRelacion te = (TransferRelacion) v.get(0);
				//this.listaRelaciones.add(te);//ojo
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarRelacion, te);
				
				break;
			}
			/*
			 * Eliminacion de una relacion
			 */
			case SR_EliminarRelacion_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			
			/*creo q esta no se usa nunca*/
			case SR_EliminarRelacion_HECHO:{
				
				
				setCambios(true);
				TransferRelacion tr = (TransferRelacion) datos;
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacion, tr);
				
				break;
			}
	
			// Renombrar relacion
			case SR_RenombrarRelacion_ERROR_NombreDeRelacionEsVacio:{
				Vector v = (Vector) datos;
				v.get(2);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExiste:{
				Vector v = (Vector) datos;
				v.get(1);
				v.get(2);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_REL_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad:{
				this.getTheGUIRenombrarRelacion().setInactiva();
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENT_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				this.getTheGUIRenombrarRelacion().setActiva();
				
				
				break;
			}
			case SR_RenombrarRelacion_ERROR_DAORelaciones:{
				Vector v = (Vector) datos;
				v.get(1);
				v.get(2);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_RenombrarRelacion_ERROR_DAOEntidades:{
				Vector v = (Vector) datos;
				v.get(1);
				v.get(2);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ENTITIES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_RenombrarRelacion_HECHO:{
				
				setCambios(true);
				Vector v = (Vector) datos;
				TransferRelacion tr = (TransferRelacion) v.get(0);
				v.get(1);
				v.get(2);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarRelacion, tr);
				this.getTheGUIRenombrarRelacion().setInactiva();
				break;
			}
			/*
			 * Debilitar una relacion
			 */
			case SR_DebilitarRelacion_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				break;
			}
			case SR_DebilitarRelacion_HECHO:{
				
				
				setCambios(true);
				TransferRelacion tr = (TransferRelacion) datos;
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_DebilitarRelacion, tr);
				ActualizaArbol(tr);
				break;
			}
			/*
			 * Restricciones a Relacion
			 */
			case SR_AnadirRestriccionARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion te = (TransferRelacion) v.get(0);
				v.get(1);
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionRelacion, te);
				//this.getTheGUIAnadirRestriccionAAtributo().setInactiva();
				break;
			}
			case SR_QuitarRestriccionARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion te = (TransferRelacion) v.get(0);
				v.get(1);
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionRelacion, te);
				break;
			}
			case SR_setRestriccionesARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion te = (TransferRelacion) v.get(1);
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesRelacion, te);
				break;
			}
			
			/*
			 * Mover Relacion en el panel de diseno (cambiar la posicion)
			 */
			case SR_MoverPosicionRelacion_ERROR_DAORelaciones:{
				TransferRelacion tr = (TransferRelacion) datos;
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverRelacion_ERROR, tr);
				break;
			}
			case SR_MoverPosicionRelacion_HECHO:{
				
				
				setCambios(true);
				TransferRelacion tr = (TransferRelacion) datos;
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverRelacion_HECHO, tr);
				break;
			}
	
			/*
			 * Añadir atributo a una relacion
			 */
			case SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoVacio:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.EMPTY_ATTRIB_NAME), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoYaExiste:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ATTRIB_NAME_REL), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirAtributoARelacion_ERROR_TamanoNoEsEntero:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE1), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirAtributoARelacion_ERROR_TamanoEsNegativo:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_SIZE2), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirAtributoARelacion_ERROR_DAOAtributos:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				this.getTheGUIAnadirAtributoRelacion().setInactiva();
				break;
			}
			case SR_AnadirAtributoARelacion_ERROR_DAORelaciones:{
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				this.getTheGUIAnadirAtributoRelacion().setInactiva();
				break;
			}
			case SR_AnadirAtributoARelacion_HECHO:{
				
				
				setCambios(true);
				Vector<Transfer> v = (Vector<Transfer>) datos;
				v.get(0);
				v.get(1);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirAtributoARelacion, v);
				this.getTheGUIAnadirAtributoRelacion().setInactiva();
				//meter un if para cuando ya este
				TransferAtributo ta = (TransferAtributo) v.get(1);
				boolean esta = false;
				for (int i = 0; i < this.listaAtributos.size(); ++i) {
					if(ta.getIdAtributo() == this.listaAtributos.get(i).getIdAtributo()) esta = true;
				}
				
				if (!esta) this.listaAtributos.add(ta);
				break;
			}
	
			/*
			 * Establecer la entidad padre en una relacion IsA
			 */
			case SR_EstablecerEntidadPadre_ERROR_DAORelaciones:{
				this.getTheGUIEstablecerEntidadPadre().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EstablecerEntidadPadre_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIEstablecerEntidadPadre().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EstablecerEntidadPadre, vt);
				break;
			}
			/*
			 * Quitar la entidad padre en una relacion IsA
			 */
			case SR_QuitarEntidadPadre_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				this.getTheGUIQuitarEntidadPadre().setInactiva();
				
				break;			
			}
			case SR_QuitarEntidadPadre_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIQuitarEntidadPadre().setInactiva();
				TransferRelacion tr = (TransferRelacion) datos;
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarEntidadPadre, tr);			
				break;			
			}
			/*
			 * Anadir una entidad hija a una relacion IsA
			 */
			case SR_AnadirEntidadHija_ERROR_DAORelaciones:{
				this.getTheGUIEstablecerEntidadPadre().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadHija_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIAnadirEntidadHija().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirEntidadHija, vt);
				break;
			}
			/*
			 * Quitar una entidad hija en una relacion IsA
			 */
			case SR_QuitarEntidadHija_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				this.getTheGUIQuitarEntidadHija().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				
				break;
			}
			case SR_QuitarEntidadHija_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIQuitarEntidadHija().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(1);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarEntidadHija, vt);
				break;
			}
			/*
			 * Eliminar una relacion IsA
			 */
			case SR_EliminarRelacionIsA_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EliminarRelacionIsA_HECHO:{
			
				
				setCambios(true);
				TransferRelacion tr = (TransferRelacion) datos;
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacionIsA, tr);
				ActualizaArbol(null);
				break;
			}
			/*
			 * Eliminar una relacion Normal
			 */
			case SR_EliminarRelacionNormal_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EliminarRelacionNormal_HECHO:{
				
				
				setCambios(true);
				Vector<Object> v = (Vector<Object>) datos;
				TransferRelacion tr = (TransferRelacion) v.get(0);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacionNormal, tr);
				ActualizaArbol(null);
				break;
			}
			/*
			 * Insertar una relacion IsA
			 */
			case SR_InsertarRelacionIsA_ERROR_DAORelaciones:{
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;	
			}
			case SR_InsertarRelacionIsA_HECHO:{
				
				
				setCambios(true);
				TransferRelacion tr = (TransferRelacion) datos;
				this.antiguaIsA = tr;
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarRelacionIsA, tr);
				ActualizaArbol(tr);
				break;
			}
			/*
			 * Anadir una entidad a una relacion normal
			 */
			case SR_AnadirEntidadARelacion_ERROR_InicioNoEsEnteroOn:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY1), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_ERROR_InicioEsNegativo:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY2), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_ERROR_FinalNoEsEnteroOn:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY3), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_ERROR_FinalEsNegativo:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY4), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_ERROR_InicioMayorQueFinal:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY5), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_ERROR_DAORelaciones:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_AnadirEntidadARelacion_HECHO:{
				
				
				setCambios(true);
				Vector v = (Vector) datos;
				TransferRelacion tr= (TransferRelacion) v.get(0);
				v.get(1);
				v.get(2);
				v.get(3);
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirEntidadARelacion, v);
				this.getTheGUIAnadirEntidadARelacion().setInactiva();
				break;
			}
			/*
			 * Quitar una entidad en una relacion Normal
			 */
			case SR_QuitarEntidadARelacion_ERROR_DAORelaciones:{
				this.getTheGUIQuitarEntidadARelacion().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(0);
				vt.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_QuitarEntidadARelacion_HECHO:{
				
				
				setCambios(true);
				this.getTheGUIQuitarEntidadARelacion().setInactiva();
				Vector<Transfer> vt = (Vector<Transfer>) datos;
				vt.get(0);
				vt.get(1);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarEntidadARelacion, vt);
				break;
			}
			/*
			 * Editar la aridad de una entidad en una relacion
			 */
			case SR_EditarCardinalidadEntidad_ERROR_InicioNoEsEnteroOn:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY1), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_ERROR_InicioEsNegativo:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY2), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_ERROR_FinalNoEsEnteroOn:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY3), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_ERROR_FinalEsNegativo:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY4), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_ERROR_InicioMayorQueFinal:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INCORRECT_CARDINALITY5), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_ERROR_DAORelaciones:{
				Vector v = (Vector) datos;
				v.get(0);
				v.get(1);
				JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATIONS_FILE_ERROR), Lenguaje.text(Lenguaje.ERROR), 0);
				
				break;
			}
			case SR_EditarCardinalidadEntidad_HECHO:{
				
				
				setCambios(true);
				Vector v = (Vector) datos;
				v.get(1);
				v.get(2);
				v.get(3);
				v.get(4);
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarCardinalidadEntidad, v);
				this.getTheGUIEditarCardinalidadEntidad().setInactiva();
				break;
	
			}
			
			case SR_AridadEntidadUnoUno_HECHO:{
				
				
				setCambios(true);
				Vector v = (Vector) datos;
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_CardinalidadUnoUno, v);
				break;
			} // switch
			case SR_AnadirUniqueARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion tr = (TransferRelacion) v.get(0);
				TransferRelacion clon_relacion =tr.clonar();
				v.get(1);
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirUniqueRelacion, clon_relacion);
				//this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
				break;
			}
			case SR_QuitarUniqueARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion tr = (TransferRelacion) v.get(0);
				TransferRelacion clon_relacion =tr.clonar();
				v.get(1);
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarUniqueRelacion, clon_relacion);
				break;
			}
	
			case SR_setUniquesARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion tr = (TransferRelacion) v.get(1);
				TransferRelacion clon_relacion =tr.clonar();
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniquesRelacion, clon_relacion);
				break;
			}
			case SR_setUniqueUnitarioARelacion_HECHO:{
				
				
				Vector v = (Vector) datos;
				TransferRelacion tr = (TransferRelacion) v.get(0);
				TransferRelacion clon_relacion =tr.clonar();
				setCambios(true);
				
				this.getTheGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniqueUnitarioRelacion, clon_relacion);
				break;
			}
			
			default: break;
		}
	}
	
	
		// Mensajes que mandan los Servicios del Sistema al Controlador
		@SuppressWarnings("incomplete-switch")
		public void mensajeDesde_SS(TC mensaje, Object datos){
			switch(mensaje){
				case SS_ValidacionM:{
					String info = (String) datos;
					this.getTheGUIPrincipal().escribeEnModelo(info);
					break;
				}
				case SS_ValidacionC:{
					String info = (String) datos;
					this.getTheGUIPrincipal().escribeEnCodigo(info);
					break;
				}
				case SS_GeneracionScriptSQL:{
					String info = (String) datos;
					this.getTheGUIPrincipal().escribeEnCodigo(info);
					this.getTheGUIPrincipal().setScriptGeneradoCorrectamente(true);
					break;
				}
				case SS_GeneracionArchivoScriptSQL:{
					String info = (String) datos;
					this.getTheGUIPrincipal().escribeEnCodigo(info);
					break;
				}
				case SS_GeneracionModeloRelacional:{
					String info = (String) datos;
					this.getTheGUIPrincipal().escribeEnModelo(info);
					break;
				}
			}// switch
	}
	//Utilidades
	
	private void guardarBackup() {
		String ruta = "";
		if (fileguardar != null && fileguardar.exists()) {
			ruta = fileguardar.getPath().replace(".xml","") + "Backup.xml";
		}
		else {
			String str = this.filetemp.getAbsolutePath();
			ruta = str.substring(0, str.length() - 27) + "LastProyectBackup.xml";
		}
		this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Guardar_Backup, ruta);
		//File f = new File(ruta);
		//this.setFileguardar(f);
	}
	
	private void guardarDeshacer() {
		String str = fileguardar.getPath().replace(".xml","");
		String ruta = "";
		if (str.contains("projects"))
			ruta = str.replace("projects","deshacer") + Integer.toString(this.contFicherosDeshacer) + ".xml";
		else if (str.contains("Examples"))
			ruta = str.replace("Examples","deshacer") + Integer.toString(this.contFicherosDeshacer) + ".xml";	    
	    boolean existe = existe(ruta);
		this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_GuardarDeshacer, ruta);
		++this.contFicherosDeshacer;
		if( !existe)++this.limiteFicherosDeshacer;
		if(this.getContFicherosDeshacer()==1)this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
		else this.getTheGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);
		
		if(this.getContFicherosDeshacer()==this.getLimiteFicherosDeshacer() || this.auxDeshacer == true)this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
		else this.getTheGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);

	}
	
	private boolean existe(String ruta) {
		boolean r = false;
		File directory = new File(System.getProperty("user.dir")+"/deshacer");
		if (directory.exists()) {
			for (File file: Objects.requireNonNull(directory.listFiles())) {
				if (file.getPath().equals(ruta)) {
					r = true;
	            }
	        }
		}
		return r;
	}
	
	private static void quicksort(Vector<String> a) {
        quicksort(a, 0, a.size() - 1);
    }

    // quicksort a[left] to a[right]
    private static void quicksort(Vector<String> a, int left, int right) {
        if (right <= left) return;
        int i = partition(a, left, right);
        quicksort(a, left, i-1);
        quicksort(a, i+1, right);
    }

    // partition a[left] to a[right], assumes left < right
    private static int partition(Vector<String> a, int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while ((a.get(++i).compareToIgnoreCase(a.get(right))<0))      // find item on left to swap
                ;                               // a[right] acts as sentinel
            while ((a.get(right).compareToIgnoreCase(a.get(--j))<0))      // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            exch(a, i, j);                      // swap two elements into place
        }
        exch(a, i, right);                      // swap with partition element
        return i;
    }
    private static void exch(Vector<String> a, int i, int j) {
        //exchanges++;
        String swap = a.get(i);
        a.set(i,a.get(j));
        a.set(j,swap);
    }
    
    public static void FileCopy(String sourceFile, String destinationFile) {
		try {
			File inFile = new File(sourceFile);
			File outFile = new File(destinationFile);

			FileInputStream in = new FileInputStream(inFile);
			FileOutputStream out = new FileOutputStream(outFile);

			int c;
			while( (c = in.read() ) != -1) out.write(c);
			in.close();
			out.close();
		} catch(IOException e) {
			System.err.println(e);
		}
	}
    
    private void guardarConf() {
    	String ruta = "";
		if (fileguardar != null && fileguardar.exists()) ruta = fileguardar.getPath();
		ConfiguradorInicial conf = new ConfiguradorInicial(
			Lenguaje.getIdiomaActual(),
			this.getTheGUIPrincipal().getConexionActual().getRuta(),
			ruta, theme.getThemeName(), this.getTheGUIPrincipal().getPanelsMode(), nullAttrs, valorZoom
		);
		conf.ponRecientes(archivosRecent.darRecientes());
		conf.guardarFicheroCofiguracion();
    }
    
    private void guardarYSalir() {
    	guardarConf();
    	salir();
    }
    
    private void salir() {
    	filetemp.delete();
    	eliminarCarpetaDeshacer();
		System.exit(0);
    }
    
    private void eliminarCarpetaDeshacer(){
    	File directory = new File(System.getProperty("user.dir")+"/deshacer");
    	if (directory.exists()) {
	    	for (File file: Objects.requireNonNull(directory.listFiles())) {
	             if (!file.isDirectory()) {
	                 file.delete();
	             }
	         }
	    	 directory.delete();
    	}	 
    }
    
    private void ActualizaArbol(Transfer t){
    	this.getTheGUIPrincipal().getPanelDiseno().EnviaInformacionNodo(t);
    }
    
	/**
	 * Getters y Setters
	 */
	private void setListaAtributos(Vector datos) {
		this.listaAtributos = datos;
	}
	public GUI_AnadirAtributoEntidad getTheGUIAnadirAtributoEntidad() {
		return theGUIAnadirAtributoEntidad;
	}
	public void setTheGUIAnadirAtributoEntidad(GUI_AnadirAtributoEntidad theGUIAnadirAtributoEntidad) {
		this.theGUIAnadirAtributoEntidad = theGUIAnadirAtributoEntidad;
	}
	public GUI_InsertarRestriccionAEntidad getTheGUIAnadirRestriccionAEntidad() {
		return theGUIAnadirRestriccionAEntidad;
	}
	public GUI_InsertarRestriccionAAtributo getTheGUIAnadirRestriccionAAtributo() {
		return theGUIAnadirRestriccionAAtributo;
	}
	public GUI_InsertarRestriccionARelacion getTheGUIAnadirRestriccionARelacion() {
		return theGUIAnadirRestriccionARelacion;
	}
	public void setTheGUIAnadirRestriccionAEntidad(GUI_InsertarRestriccionAEntidad theGUIAnadirRestriccionAEntidad) {
		this.theGUIAnadirRestriccionAEntidad = theGUIAnadirRestriccionAEntidad;
	}
	public GUI_AnadirAtributoRelacion getTheGUIAnadirAtributoRelacion() {
		return theGUIAnadirAtributoRelacion;
	}
	public GUI_AnadirAtributo getTheGUIAnadirAtributo() {
		return theGUIAnadirAtributo;
	}
	public void setTheGUIAnadirAtributoRelacion(GUI_AnadirAtributoRelacion theGUIAnadirAtributoRelacion) {
		this.theGUIAnadirAtributoRelacion = theGUIAnadirAtributoRelacion;
	}
	public GUI_AnadirEntidadHija getTheGUIAnadirEntidadHija() {
		return theGUIAnadirEntidadHija;
	}
	public void setTheGUIAnadirEntidadHija(GUI_AnadirEntidadHija theGUIAnadirEntidadHija) {
		this.theGUIAnadirEntidadHija = theGUIAnadirEntidadHija;
	}
	public GUI_AnadirSubAtributoAtributo getTheGUIAnadirSubAtributoAtributo() {
		return theGUIAnadirSubAtributoAtributo;
	}
	public void setTheGUIAnadirSubAtributoAtributo(GUI_AnadirSubAtributoAtributo theGUIAnadirSubAtributoAtributo) {
		this.theGUIAnadirSubAtributoAtributo = theGUIAnadirSubAtributoAtributo;
	}
	public GUI_EditarDominioAtributo getTheGUIEditarDominioAtributo() {
		return theGUIEditarDominioAtributo;
	}
	public void setTheGUIEditarDominioAtributo(GUI_EditarDominioAtributo theGUIEditarDominioAtributo) {
		this.theGUIEditarDominioAtributo = theGUIEditarDominioAtributo;
	}
	public GUI_EstablecerEntidadPadre getTheGUIEstablecerEntidadPadre() {
		return theGUIEstablecerEntidadPadre;
	}
	public void setTheGUIEstablecerEntidadPadre(GUI_EstablecerEntidadPadre theGUIEstablecerEntidadPadre) {
		this.theGUIEstablecerEntidadPadre = theGUIEstablecerEntidadPadre;
	}
	public GUI_InsertarEntidad getTheGUIInsertarEntidad() {
		return theGUIInsertarEntidad;
	}
	public void setTheGUIInsertarEntidad(GUI_InsertarEntidad theGUIInsertarEntidad) {
		this.theGUIInsertarEntidad = theGUIInsertarEntidad;
	}
	public GUI_ModificarEntidad getTheGUIModificarEntidad() {
		return theGUIModificarEntidad;
	}
	public void setTheGUIModificarEntidad (GUI_ModificarEntidad theGUIModificarEntidad) {
		this.theGUIModificarEntidad=theGUIModificarEntidad;
	}
	public GUI_ModificarRelacion getTheGUIModificarRelacion() {
		return theGUIModificarRelacion;
	}
	public void setTheGUIModificarRelacion (GUI_ModificarRelacion theGUIModificarRelacion) {
		this.theGUIModificarRelacion=theGUIModificarRelacion;
	}
	public GUI_ModificarAtributo getTheGUIModificarAtributo() {
		return theGUIModificarAtributo;
	}
	public void setTheGUIModificarAtributo (GUI_ModificarAtributo theGUIModificarAtributo) {
		this.theGUIModificarAtributo=theGUIModificarAtributo;
	}
	public GUI_InsertarRelacion getTheGUIInsertarRelacion() {
		return theGUIInsertarRelacion;
	}
	public GUI_InsertarDominio getTheGUIInsertarDominio() {
		return theGUIInsertarDominio;
	}
	public void setTheGUIInsertarRelacion(GUI_InsertarRelacion theGUIInsertarRelacion) {
		this.theGUIInsertarRelacion = theGUIInsertarRelacion;
	}
	public GUI_Conexion getTheGUIConfigurarConexionDBMS(){
		return theGUIConexion;
	}
	public void setTheGUIConfigurarConexionDBMS(GUI_Conexion theGUIConexion){
		this.theGUIConexion = theGUIConexion;
	}
	public GUI_SeleccionarConexion getTheGuiSeleccionarConexion(){
		return theGUISeleccionarConexion;
	}
	public void setTheGuiSelecceionarConexion(GUI_SeleccionarConexion selector){
		this.theGUISeleccionarConexion = selector;
	}
	public GUIPrincipal getTheGUIPrincipal() {
		return theGUIPrincipal;
	}
	public void setTheGUIPrincipal(GUIPrincipal theGUIPrincipal) {
		this.theGUIPrincipal = theGUIPrincipal;
	}
	public GUI_QuitarEntidadHija getTheGUIQuitarEntidadHija() {
		return theGUIQuitarEntidadHija;
	}
	public void setTheGUIQuitarEntidadHija(GUI_QuitarEntidadHija theGUIQuitarEntidadHija) {
		this.theGUIQuitarEntidadHija = theGUIQuitarEntidadHija;
	}
	public GUI_QuitarEntidadPadre getTheGUIQuitarEntidadPadre() {
		return theGUIQuitarEntidadPadre;
	}
	public void setTheGUIQuitarEntidadPadre(GUI_QuitarEntidadPadre theGUIQuitarEntidadPadre) {
		this.theGUIQuitarEntidadPadre = theGUIQuitarEntidadPadre;
	}
	public GUI_RenombrarAtributo getTheGUIRenombrarAtributo() {
		return theGUIRenombrarAtributo;
	}
	public void setTheGUIRenombrarAtributo(GUI_RenombrarAtributo theGUIRenombrarAtributo) {
		this.theGUIRenombrarAtributo = theGUIRenombrarAtributo;
	}
	public GUI_RenombrarEntidad getTheGUIRenombrarEntidad() {
		return theGUIRenombrarEntidad;
	}
	public void setTheGUIRenombrarEntidad(GUI_RenombrarEntidad theGUIRenombrarEntidad) {
		this.theGUIRenombrarEntidad = theGUIRenombrarEntidad;
	}
	public GUI_RenombrarRelacion getTheGUIRenombrarRelacion() {
		return theGUIRenombrarRelacion;
	}
	public void setTheGUIRenombrarRelacion(GUI_RenombrarRelacion theGUIRenombrarRelacion) {
		this.theGUIRenombrarRelacion = theGUIRenombrarRelacion;
	}
	public GUI_RenombrarDominio getTheGUIRenombrarDominio() {
		return theGUIRenombrarDominio;
	}
	public void setTheGUIRenombrarDominio(GUI_RenombrarDominio theGUIRenombrarDominio) {
		this.theGUIRenombrarDominio = theGUIRenombrarDominio;
	}
	public GUI_ModificarDominio getTheGUIModificarElementosDominio() {
		return theGUIModificarElementosDominio;
	}
	public void setTheGUIModificarElementosDominio(GUI_ModificarDominio theGUIModificarElementosDominio) {
		this.theGUIModificarElementosDominio = theGUIModificarElementosDominio;
	}
	public ServiciosAtributos getTheServiciosAtributos() {
		return theServiciosAtributos;
	}
	public void setTheServiciosAtributos(ServiciosAtributos theServiciosAtributos) {
		this.theServiciosAtributos = theServiciosAtributos;
	}
	public ServiciosEntidades getTheServiciosEntidades() {
		return theServiciosEntidades;
	}
	public void setTheServiciosEntidades(ServiciosEntidades theServiciosEntidades) {
		this.theServiciosEntidades = theServiciosEntidades;
	}
	public ServiciosAgregaciones getTheServiciosAgregaciones() {
		return theServiciosAgregaciones;
	}

	public void setTheServiciosAgregaciones(ServiciosAgregaciones theServiciosAgregaciones) {
		this.theServiciosAgregaciones = theServiciosAgregaciones;
	}
	public ServiciosRelaciones getTheServiciosRelaciones() {
		return theServiciosRelaciones;
	}
	public void setTheServiciosRelaciones(ServiciosRelaciones theServiciosRelaciones) {
		this.theServiciosRelaciones = theServiciosRelaciones;
	}
	public ServiciosDominios getTheServiciosDominios() {
		return theServiciosDominios;
	}
	public void setTheServiciosDominios(ServiciosDominios theServiciosDominios) {
		this.theServiciosDominios = theServiciosDominios;
	}
	
	private GUI_Eliminar getTheGUIEliminar() {
		// TODO Auto-generated method stub
		return theGUIEliminar;
	}
	public GUI_AnadirEntidadARelacion getTheGUIAnadirEntidadARelacion() {
		return theGUIAnadirEntidadARelacion;
	}
	public void setTheGUIAnadirEntidadARelacion(GUI_AnadirEntidadARelacion theGUIAnadirEntidadARelacion) {
		this.theGUIAnadirEntidadARelacion = theGUIAnadirEntidadARelacion;
	}
	public GUI_QuitarEntidadARelacion getTheGUIQuitarEntidadARelacion() {
		return theGUIQuitarEntidadARelacion;
	}
	public void setTheGUIQuitarEntidadARelacion(GUI_QuitarEntidadARelacion theGUIQuitarEntidadARelacion) {
		this.theGUIQuitarEntidadARelacion = theGUIQuitarEntidadARelacion;
	}
	public GUI_EditarCardinalidadEntidad getTheGUIEditarCardinalidadEntidad() {
		return theGUIEditarCardinalidadEntidad;
	}
	public void setTheGUIEditarCardinalidadEntidad(GUI_EditarCardinalidadEntidad theGUIEditarCardinalidadEntidad) {
		this.theGUIEditarCardinalidadEntidad = theGUIEditarCardinalidadEntidad;
	}
	public GeneradorEsquema getTheServiciosSistema() {
		return theServiciosSistema;
	}
	public void setTheServiciosSistema(GeneradorEsquema theServiciosSistema) {
		this.theServiciosSistema = theServiciosSistema;
	}
	public GUI_SaveAs getTheGUIWorkSpace() {
		return theGUIWorkSpace;
	}
	public void setTheGUIWorkSpace(GUI_SaveAs theGUIWorkSpace) {
		this.theGUIWorkSpace = theGUIWorkSpace;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public File getFiletemp(){
		return filetemp;
	}
	public void setFiletemp(File temp){
		this.filetemp = temp;
	}
	public File getFileguardar(){
		return fileguardar;
	}
	public void setFileguardar(File guardar){
		this.fileguardar = guardar;
	}
	public GUI_Pregunta getPanelOpciones(){
		return this.panelOpciones;
	}
	private GUI_TablaUniqueEntidad getTheGUITablaUniqueEntidad() {
		return this.theGUITablaUniqueEntidad;
	}
	private GUI_TablaUniqueRelacion getTheGUITablaUniqueRelacion() {
		return this.theGUITablaUniqueRelacion;
	}
	private void setModoVista(int m) {
		this.modoVista = m;
	}
	private int getModoVista() {
		return modoVista;
	}
	private void setCambios(boolean b){
		cambios=b;
		getTheGUIPrincipal().setTitle(getTitle());
	}
	public boolean isNullAttrs() {
		return nullAttrs;
	}
	public void setNullAttrs(boolean nullAttrs) {
		this.nullAttrs = nullAttrs;
	}
	public String getTitle() {
		try {
			return Lenguaje.text(Lenguaje.DBCASE)+" - "+getFileguardar().getName() + (cambios?"*":"");
		}catch(NullPointerException e) {
			return Lenguaje.text(Lenguaje.DBCASE)+" - "+this.getFiletemp().getPath();
		}
	}

	public boolean getConfirmarEliminaciones() {
		return confirmarEliminaciones;
	}

	public void setConfirmarEliminaciones(boolean confirmarEliminaciones) {
		this.confirmarEliminaciones = confirmarEliminaciones;
	}

	public Vector<TransferEntidad> getListaEntidades() {
		return listaEntidades;
	}

	public void setListaEntidades(Vector<TransferEntidad> listaEntidades) {
		this.listaEntidades = listaEntidades;
	}

	public Vector<TransferRelacion> getListaRelaciones() {
		return this.listaRelaciones;
	}
	
	public Vector<TransferAtributo> getListaAtributos() {
		return listaAtributos;
	}

	public void setListaRelaciones(Vector<TransferRelacion> listaRelaciones) {
		this.listaRelaciones = listaRelaciones;
	}

	public ServiciosReporte getTheServiciosReporte() {
		return theServiciosReporte;
	}

	public void setTheServiciosReporte(ServiciosReporte theServiciosReporte) {
		this.theServiciosReporte = theServiciosReporte;
	}

	public boolean getModoSoporte() {
		return modoSoporte;
	}

	public void setCuadricula(boolean cuadricula) {
		this.cuadricula = cuadricula;
	}
	
	public void setOcultarConceptual(boolean c) {
		this.ocultarConceptual = c;
	}
	
	public void setOcultarLogico(boolean l) {
		this.ocultarLogico = l;
	}
	
	public void setOcultarFisico(boolean f) {
		this.ocultarFisico = f;
	}
	
	public void setOcultarDominios(boolean d) {
		this.ocultarDominios = d;
	}
	
	public void setModoSoporte(boolean modo) {
		this.modoSoporte = modo;
	}
	
	public boolean getCaudricula() {
		return cuadricula;
	}
	
	public boolean getOcultarConceptual() {
		return ocultarConceptual;
	}
	
	public boolean getOcultarLogico() {
		return ocultarLogico;
	}
	
	public boolean getOcultarFisico() {
		return ocultarFisico;
	}

	public boolean getOcultarDominios() {
		return ocultarDominios;
	}
	
	public int getZoom() {
		return this.valorZoom;
	}

	public void setZoom(int z) {
		this.valorZoom = z;
	}
	
	public int getContFicherosDeshacer() {
		return this.contFicherosDeshacer;
	}
	
	public int getLimiteFicherosDeshacer() {
		return this.limiteFicherosDeshacer;
	}
	
	public boolean getAuxDeshacer() {
		return this.auxDeshacer;
	}
	
	
	/*public void funcionDeshacer(TC mensaje, Object datos) {
		switch (mensaje) {
			case SE_InsertarEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				v.add(datos);
				v.add(true);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarEntidad, v);
				break;
			}
			case SE_RenombrarEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				// Mandamos mensaje + datos al controlador
				this.mensajeDesde_GUI(TC.GUIRenombrarEntidad_Click_BotonRenombrar, v);
				break;
			}
			
			case SE_AnadirAtributoAEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);//this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
			}
			
			case SE_EliminarEntidad_HECHO:{
				Vector<Object> v2 = (Vector<Object>) datos;
				Vector atributos = this.auxTransferAtributos;
				Vector<TransferRelacion> relaciones = (Vector<TransferRelacion>) v2.get(1);
				this.mensajeDesde_GUI(TC.GUIInsertarEntidad_Click_BotonInsertar, v2.get(0));
				for (int i = 0; i < atributos.size(); ++i) {
					
					for (int j = 0; j < this.listaAtributos.size(); ++j) {
						//System.out.println(atributos.get(i));
						//System.out.println(this.listaAtributos.get(j).getIdAtributo());
						if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
							Vector<Object> v = new Vector<Object>();
							v.add(v2.get(0));
							v.add(this.listaAtributos.get(j));
							mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
						}	
					}
					
				}
				for (int i = 0; i < relaciones.size(); ++i) {
					Vector<Object> v = new Vector<Object>();
					v.add(relaciones.get(i));
					v.add(v2.get(0));
					v.add(String.valueOf(0));
					v.add("n");
					v.add("");
					v.add(false);
					v.add(false);
					v.add(false);
					v.add(false);
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir,v);
				}
				
				break;
			}
			
			/*case SE_MoverPosicionEntidad_HECHO:{ //ni idea de por que no funciona
				TransferEntidad te = (TransferEntidad) datos;
				Point2D pos = te.getPosicion();
				TransferEntidad teAux = new TransferEntidad();
				/*for (int i = 0; i < this.listaEntidades.size(); ++i) {
					if(te.getNombre() == this.listaEntidades.get(i).getNombre()) {
						pos = this.listaEntidades.get(i).getPosicion();
					}
				}
				te.setPosicion(this.posAux);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_MoverEntidad, te);
				this.getTheGUIPrincipal().getPanelDiseno().repaint();
			}*/

			
			/*case SA_EliminarAtributo_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(v2.get(0));
				TransferAtributo ta = (TransferAtributo) v2.get(0);
				v.add("10");
				if (v2.get(1) instanceof TransferEntidad) {
					this.mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
				}
				else if (v2.get(1) instanceof TransferRelacion)this.mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
				else if (v2.get(1) instanceof TransferAtributo)this.mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v);
				
				//Nose xq peta
				
				/*if(ta.getCompuesto()) {
					Vector atributos = this.antiguosSubatributos;
					for (int i = 0; i < atributos.size(); ++i) {
						for (int j = 0; j < this.listaAtributos.size(); ++j) {
							if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
								Vector<Object> v3 = new Vector<Object>();
								v3.add(ta);
								v3.add(this.listaAtributos.get(j));
								v3.add("10");
								mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v3);
							}	
						}
						
					}
				}
				
				break;
			}
			
			case SE_setUniqueUnitarioAEntidad_HECHO:{//es el ultimo mensaje cuando se renombra un atributo
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				if (v2.size() == 3) {
					TransferAtributo ta = (TransferAtributo) v2.get(1);
					if(ta.getNombre() != v2.get(2)) {
						v.add(v2.get(1));
						v.add(v2.get(2));
						this.mensajeDesde_GUI(TC.GUIRenombrarAtributo_Click_BotonRenombrar, v);
					}
				}
				break;
				/*else {
					TransferAtributo ta = (TransferAtributo) v2.get(1);
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo,ta);
				}
				
			}
			
			case SA_EditarUniqueAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				Vector<Object> v = new Vector<Object>();
				if (ta.getNombre() != this.antigoNombreAtributo) {
					v.add(ta);
					v.add(this.antigoNombreAtributo);
					this.mensajeDesde_GUI(TC.GUIRenombrarAtributo_Click_BotonRenombrar, v);
				}
				if (!ta.getDominio().equals(this.antiguoDominioAtributo)) {
					v.add(ta);
					v.add(this.antiguoDominioAtributo);
					this.mensajeDesde_GUI(TC.GUIEditarDominioAtributo_Click_BotonEditar, v);
				}
				if (ta.getCompuesto() != this.antiguoCompuestoAtribuo) {
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarCompuestoAtributo,ta);
				}
				if (ta.getMultivalorado() != this.antiguoMultivaloradoAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarMultivaloradoAtributo,ta);
				}
				if (ta.getNotnull() != this.antiguoNotnullAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarNotNullAtributo,ta);
				}
				/*if(ta.getUnique() != this.antiguoUniqueAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo,ta);
				}
				break;
			}
			
			case SA_EditarDominioAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(ta);
				v.add(this.antiguoDominioAtributo);
				this.mensajeDesde_GUI(TC.GUIEditarDominioAtributo_Click_BotonEditar, v);
				break;
			}
			
			case SA_EditarCompuestoAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getCompuesto() != this.antiguoCompuestoAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarCompuestoAtributo,ta);
				break;
			}
			
			case SA_EditarMultivaloradoAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getMultivalorado() != this.antiguoMultivaloradoAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarMultivaloradoAtributo,ta);
				break;
			}
			
			case SA_EditarNotNullAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getNotnull() != this.antiguoNotnullAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarNotNullAtributo,ta);
				break;
			}
			
			case SA_AnadirSubAtributoAtributo_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
			}
			
			case SA_EditarClavePrimariaAtributo_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				TransferAtributo ta = (TransferAtributo) v.get(0);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarClavePrimariaAtributo, v);
				break;
			}
			
			case SD_InsertarDominio_HECHO:{
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarDominio, datos);
				this.getTheGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			
			case SD_RenombrarDominio_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				this.mensajeDesde_GUI(TC.GUIRenombrarDominio_Click_BotonRenombrar, v);
				this.getTheGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			
			case SD_EliminarDominio_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				TransferDominio td = (TransferDominio) v.get(0);
				this.mensajeDesde_GUI(TC.GUIInsertarDominio_Click_BotonInsertar, td);
				this.getTheGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			
			case SR_InsertarRelacion_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(tr);
				v.add(true);// se uede enviar el atributo this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarRelacionNormal, v);
				break;
			}
			
			
			case SR_RenombrarRelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				this.mensajeDesde_GUI(TC.GUIRenombrarRelacion_Click_BotonRenombrar,v);
				break;
			}
			
			//case SR_DebilitarRelacion_HECHO:{}
			
			case SR_AnadirAtributoARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);//this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
				
			}
			
			case SR_EstablecerEntidadPadre_HECHO:{
				Vector<Object> v2 = (Vector<Object>) datos;
				TransferRelacion tr = (TransferRelacion) v2.get(0);
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadPadre_ClickBotonSi, tr);
				break;
				
			}
			
			case SR_QuitarEntidadPadre_HECHO:{
				Vector<Object> v = new Vector<Object>();
				TransferRelacion tr = (TransferRelacion) datos;
				TransferEntidad te = this.padreAntiguo;
				v.add(tr);
				v.add(te);
				this.mensajeDesde_GUI(TC.GUIEstablecerEntidadPadre_ClickBotonAceptar, v);
				
				Vector<TransferEntidad> th = this.hijosAntiguo;
				for(int i = 0; i < th.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(tr);
					v2.add(th.get(i));
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v2);
				}
				
				break;
				
			}
			
			case SR_AnadirEntidadHija_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadHija_ClickBotonQuitar, v);
				break;
			}
			
			case SR_QuitarEntidadHija_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v);
				break;
			}
			
			case SR_EliminarRelacionIsA_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				
				
				//obtenemos el padre
				
				TransferEntidad tP = this.padreAntiguo;
				
				//obtenemos las hijas
				Vector<TransferEntidad> th = this.hijosAntiguo;
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, tr.getPosicion());
				Vector<Object> v1 = new Vector<Object>();
				v1.add(this.antiguaIsA);
				v1.add(tP);
				this.mensajeDesde_GUI(TC.GUIEstablecerEntidadPadre_ClickBotonAceptar, v1);
				
				for(int i = 0; i < th.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(this.antiguaIsA);
					v2.add(th.get(i));
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v2);
				}
				break;
				
				
			}
			
			case SR_EliminarRelacionNormal_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				
				//obtenemos las hijas
				Vector<TransferEntidad> vte = this.antiguasEntidadesRel;
				Vector<TransferAtributo> vta = this.antiguosAtributosRel;
				Vector<EntidadYAridad> veya = new Vector<EntidadYAridad>();
				tr.setListaEntidadesYAridades(veya);
			
				this.mensajeDesde_GUI(TC.GUIInsertarRelacion_Click_BotonInsertar, tr);
				
				for(int i = 0; i < vte.size(); ++i) {
					Vector<Object> v1 = new Vector<Object>();
					v1.add(tr);
					v1.add(vte.get(i));
					v1.add("0");
					v1.add("n");
					v1.add("");
					v1.add(true);
					v1.add(false);
					v1.add(false);
					v1.add(false);
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, v1);
				}
				
				for(int i = 0; i < vta.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(tr);
					v2.add(vta.get(i));
					v2.add("10");
					this.mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v2);
				}
				
				break;
				
			}
			
			case SR_InsertarRelacionIsA_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(tr);
				v.add(true);// se uede enviar el atributo this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarRelacionIsA, v);
				break;
				
			}
			
			case SR_AnadirEntidadARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add(v2.get(4));
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadARelacion_ClickBotonQuitar, v);
				break;
				
			}
			
			case SR_QuitarEntidadARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add("0");
				v.add("n");
				v.add(v2.get(2));
				v.add(true);
				v.add(false);
				v.add(false);
				v.add(false);
				this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, v);
				break;
				
			}
			
			case SR_EditarCardinalidadEntidad_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add("0");
				v.add("n");
				v.add(v2.get(5));
				v.add(v2.get(4));
				v.add(true);
				v.add(false);
				v.add(false);
				v.add(false);
				this.mensajeDesde_GUI(TC.GUIEditarCardinalidadEntidad_ClickBotonEditar, v);
				break;
			}		
			
			
			default: break;

		}
	}
	*/
}





