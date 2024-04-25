package vista.frames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import controlador.Controlador;
import controlador.TC;
import oracle.net.nt.TcpNTAdapter;
import vista.GUIPrincipal;

/* Esta factoría se encarga de devolver la GUI correspondiente al mensaje recibido. */
public class FactoriaGUI {

	private Map<TC, Parent_GUI> map_guis;
	
	private GUIPrincipal gui_principal;
	
	private Controlador ctrl;
	
	public FactoriaGUI(Controlador ctrl) {
		map_guis = new HashMap<TC, Parent_GUI>();
		this.ctrl = ctrl;
	}
	
	public GUIPrincipal getGUIPrincipal() {
		if(gui_principal == null) {
			gui_principal = new GUIPrincipal();
			gui_principal.setControlador(ctrl);
		}
		return gui_principal;
	}
	
	/** @param destroyPreviousAndCreate Si es true y hay una gui asociada al parámetro 'mensaje' ya almacenada, se destruye 
	 * 									y se crea una nueva.						
	 * */
	public Parent_GUI getGUI(TC mensaje, Object datos, boolean destroyPreviousAndCreate) {
		Parent_GUI gui;
		
		//Si podemos reutilizar el frame y hay uno para reutilizar, lo hacemos.
		if(!destroyPreviousAndCreate && map_guis.get(mensaje) != null) {
			gui = map_guis.get(mensaje);
			gui.setDatos(datos); 
		}
		else {
			switch(mensaje) {
			case Controlador_InsertarAtributo: gui = new GUI_AnadirAtributo(ctrl); break;
			case Controlador_InsertarAgregacion: gui = new GUI_AnadirAgregacion(ctrl); break;
			case Controlador_AnadirRestriccionEntidad: gui = new GUI_InsertarRestriccionAEntidad(ctrl); break;
	        case Controlador_AnadirRestriccionAtributo: gui = new GUI_InsertarRestriccionAAtributo(ctrl); break;
	        case Controlador_AnadirRestriccionRelacion: gui = new GUI_InsertarRestriccionARelacion(ctrl); break;
	        case Controlador_TablaUniqueAEntidad: gui = new GUI_TablaUniqueEntidad(ctrl); break;
	        case Controlador_TablaUniqueARelacion: gui = new GUI_TablaUniqueRelacion(ctrl); break;
	        case Controlador_AnadirAtributoAEntidad: gui = new GUI_AnadirAtributoEntidad(ctrl); break;
	        case Controlador_AnadirAtributoARelacion: gui = new GUI_AnadirAtributoRelacion(ctrl); break;
	        case Controlador_RenombrarAtributo: gui = new GUI_RenombrarAtributo(ctrl); break;
	        case Controlador_InsertarRelacion: gui = new GUI_InsertarRelacion(ctrl); break;
	        case Controlador_RenombrarRelacion: gui = new GUI_RenombrarRelacion(ctrl); break;
	        case Controlador_EditarDominioAtributo: gui = new GUI_EditarDominioAtributo(ctrl); break;
	        case Controlador_AnadirSubAtributoAAtributo: gui = new GUI_AnadirSubAtributoAtributo(ctrl); break;
	        case Controlador_EstablecerEntidadPadre: gui = new GUI_EstablecerEntidadPadre(ctrl); break;
	        case Controlador_QuitarEntidadPadre: gui = new GUI_QuitarEntidadPadre(ctrl); break;
	        case Controlador_AnadirEntidadHija: gui = new GUI_AnadirEntidadHija(ctrl); break;
	        case Controlador_QuitarEntidadHija: gui = new GUI_QuitarEntidadHija(ctrl); break;
	        case Controlador_AnadirEntidadARelacion: gui = new GUI_AnadirEntidadARelacion(ctrl); break;
	        case Controlador_QuitarEntidadARelacion: gui = new GUI_QuitarEntidadARelacion(ctrl); break;
	        case Controlador_EditarCardinalidadEntidad: gui = new GUI_EditarCardinalidadEntidad(ctrl); break;
	        case Controlador_RenombrarDominio: gui = new GUI_RenombrarDominio(ctrl); break;
	        case Controlador_ModificarTipoBaseDominio: gui = new GUI_ModificarDominio(ctrl); break;
	        case Controlador_ModificarValoresDominio: gui = new GUI_ModificarDominio(ctrl); break;
            case Controlador_EditarAgregacion: gui = new GUI_RenombrarAgregacion(ctrl); break;
            case Controlador_InsertarEntidad: gui = new GUI_InsertarEntidad(ctrl); break;
            case Controlador_RenombrarEntidad: gui = new GUI_RenombrarEntidad(ctrl); break;
            case Controlador_RenombrarAgregacion: gui = new GUI_RenombrarAgregacion(ctrl); break;
            case Controlador_InsertarDominio: gui = new GUI_InsertarDominio(ctrl); break;
            case Controlador_EjecutarEnDBMS: gui = new GUI_SeleccionarConexion(ctrl); break;
            case Controlador_ConfigurarConexionDBMS: gui = new GUI_Conexion(ctrl); break; 
            case Controlador_ModificarEntidad: gui = new GUI_ModificarEntidad(ctrl); break;
            case Controlador_ModificarAtributo: gui = new GUI_ModificarAtributo(ctrl); break;
            case Controlador_ModificarRelacion: gui = new GUI_ModificarRelacion(ctrl); break;
            case Controlador_Eliminar: gui = new GUI_Eliminar(ctrl); break;
            case GUI_WorkSpace: gui = new GUI_SaveAs(ctrl, (Boolean) datos); break;
            case GUI_Recientes: gui = new GUI_Recientes(ctrl, (ArrayList<File>) datos); break;
            case GUI_Principal_REPORT: gui = new GUI_Report(ctrl); break;
            case GUI_Principal_Zoom: gui = new GUI_Zoom(ctrl); break;
            case GUI_Pregunta: gui = new GUI_Pregunta(ctrl); break;
			default: gui = null;
			}
			
			if(gui != null) {
				gui.setDatos(datos);
				map_guis.put(mensaje, gui);
			}
		}
		return gui;
	}
	
	public void destroyAll() {
		map_guis.clear();
	}
	
	public GUI_About getAbout() {
		return new GUI_About();
	}
	
	public GUI_Manual getManual() {
		return new GUI_Manual();
	}
	
	public GUI_Galeria getGaleria() {
		return new GUI_Galeria();
	}
}
