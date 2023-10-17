package vista.frames;

import java.util.HashMap;
import java.util.Map;

import controlador.Controlador;
import controlador.TC;

public class FactoriaGUI {

	private Map<TC, Parent_GUI> map_guis;
	
	public FactoriaGUI() {
		map_guis = new HashMap<TC, Parent_GUI>();
	}
	
	public Parent_GUI getGUI(TC mensaje, Controlador ctrl, Object datos) {
		//Si la interfaz ya está creada, se devuelve, si no, se crea y se devuelve.
		Parent_GUI gui;
		
		if(map_guis.get(mensaje) != null) {
			gui = map_guis.get(mensaje);
			gui.setDatos(datos); 					//TODO Mirar cómo hacen para actualizar las guis
			return gui;
		}
		else {
			switch(mensaje) {
			case PanelDiseno_Click_AddAgregacion: gui = new GUI_AnadirAgregacion(ctrl); break;
			case PanelDiseno_Click_AnadirRestriccionAEntidad: gui = new GUI_InsertarRestriccionAEntidad(ctrl); break;
	        case PanelDiseno_Click_AnadirRestriccionAAtributo: gui = new GUI_InsertarRestriccionAAtributo(ctrl); break;
	        case PanelDiseno_Click_AnadirRestriccionARelacion: gui = new GUI_InsertarRestriccionARelacion(ctrl); break;
	        case PanelDiseno_Click_TablaUniqueAEntidad: gui = new GUI_TablaUniqueEntidad(ctrl); break;
	        case PanelDiseno_Click_TablaUniqueARelacion: gui = new GUI_TablaUniqueRelacion(ctrl); break;
	        case PanelDiseno_Click_AnadirAtributoEntidad: gui = new GUI_AnadirAtributoEntidad(ctrl); break;
	        case PanelDiseno_Click_RenombrarAtributo: gui = new GUI_RenombrarAtributo(ctrl); break;
	        case PanelDiseno_Click_InsertarRelacionNormal: gui = new GUI_InsertarRelacion(ctrl); break;
	        case PanelDiseno_Click_RenombrarRelacion: gui = new GUI_RenombrarRelacion(ctrl); break;
	        case PanelDiseno_Click_EditarDominioAtributo: gui = new GUI_EditarDominioAtributo(ctrl); break;
	        case PanelDiseno_Click_AnadirSubAtributoAAtributo: gui = new GUI_AnadirSubAtributoAtributo(ctrl); break;
	        case PanelDiseno_Click_EstablecerEntidadPadre: gui = new GUI_EstablecerEntidadPadre(ctrl); break;
	        case PanelDiseno_Click_QuitarEntidadPadre: gui = new GUI_QuitarEntidadPadre(ctrl); break;
	        case PanelDiseno_Click_AnadirEntidadHija: gui = new GUI_AnadirEntidadHija(ctrl); break;
	        case PanelDiseno_Click_QuitarEntidadHija: gui = new GUI_QuitarEntidadHija(ctrl); break;
	        case PanelDiseno_Click_AnadirEntidadARelacion: gui = new GUI_AnadirEntidadARelacion(ctrl); break;
	        case PanelDiseno_Click_QuitarEntidadARelacion: gui = new GUI_QuitarEntidadARelacion(ctrl); break;
	        case PanelDiseno_Click_EditarCardinalidadEntidad: gui = new GUI_EditarCardinalidadEntidad(ctrl); break;
	        case PanelDiseno_Click_RenombrarDominio: gui = new GUI_RenombrarDominio(ctrl); break;
	        case PanelDiseno_Click_ModificarDominio: gui = new GUI_ModificarDominio(ctrl); break;
            case PanelDiseno_Click_EditarAgregacion: gui = new GUI_RenombrarAgregacion(ctrl); break;
            case PanelDiseno_Click_InsertarEntidad: gui = new GUI_InsertarEntidad(ctrl); break;
            case PanelDiseno_Click_RenombrarEntidad: gui = new GUI_RenombrarEntidad(ctrl); break;
			default: gui = null;
			}
			
			if(gui != null) {
				gui.setDatos(datos);
				map_guis.put(mensaje, gui);
			}
			return gui;
		}
		
		
	}
}
