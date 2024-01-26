package controlador.comandos;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import controlador.comandos.Frames.ComandoAnadirEntidadARelacion;
import controlador.comandos.Frames.ComandoAnadirUniquesEntidad;
import controlador.comandos.Frames.ComandoAnadirUniquesRelacion;
import controlador.comandos.Frames.ComandoClickInsertarAgregacion;
import controlador.comandos.Frames.ComandoClickModificarAtributo;
import controlador.comandos.Frames.ComandoClickModificarEntidad;
import controlador.comandos.Frames.ComandoQuitarEntidadPadre;
import controlador.comandos.GUI_Workspace.ComandoClickAbrir;
import controlador.comandos.GUI_Workspace.ComandoClickGuardar;
import controlador.comandos.GUI_Workspace.ComandoClickGuardarBackup;
import controlador.comandos.GUI_Workspace.ComandoWorkspaceNuevo;
import controlador.comandos.PanelDiseno.ComandoClickDebilitarRelacion;
import controlador.comandos.PanelDiseno.ComandoClickEditarCompuestoAtrib;
import controlador.comandos.PanelDiseno.ComandoClickEditarUniqueAtributo;
import controlador.comandos.PanelDiseno.ComandoClickEliminarAtributo;
import controlador.comandos.PanelDiseno.ComandoClickEliminarDominio;
import controlador.comandos.PanelDiseno.ComandoClickEliminarEntidad;
import controlador.comandos.PanelDiseno.ComandoClickEliminarRefUniqueAtr;
import controlador.comandos.PanelDiseno.ComandoClickEliminarRelacionNormal;
import controlador.comandos.PanelDiseno.ComandoClickModificarUniqueAtrib;
import controlador.comandos.PanelDiseno.ComandoClickPegar;

public class FactoriaComandos {

	public static Comando getComando(TC mensaje, Controlador ctrl) {
		Comando res = null;
		switch(mensaje) {
		
		/* GUI_WorkSpace */
		case GUI_WorkSpace_Nuevo: res = new ComandoWorkspaceNuevo(ctrl); break;
		case GUI_WorkSpace_Click_Abrir: res = new ComandoClickAbrir(ctrl); break;
		case GUI_WorkSpace_Click_Guardar: res = new ComandoClickGuardar(ctrl); break;
		case GUI_WorkSpace_Click_Guardar_Backup: res = new ComandoClickGuardarBackup(ctrl); break;
		
		/* Panel diseno */
		case PanelDiseno_Click_EliminarEntidad: res = new ComandoClickEliminarEntidad(ctrl); break;
        case PanelDiseno_Click_EliminarAtributo: res = new ComandoClickEliminarAtributo(ctrl); break;
        case PanelDiseno_Click_DebilitarRelacion: res = new ComandoClickDebilitarRelacion(ctrl); break;
        case PanelDiseno_Click_EditarUniqueAtributo: res = new ComandoClickEditarUniqueAtributo(ctrl); break;
        case PanelDiseno_Click_EditarCompuestoAtributo: res = new ComandoClickEditarCompuestoAtrib(ctrl); break;
        case PanelDiseno_Click_Pegar: res = new ComandoClickPegar(ctrl);
        case PanelDiseno_Click_EliminarReferenciasUniqueAtributo: res = new ComandoClickEliminarRefUniqueAtr(ctrl); break;
        case PanelDiseno_Click_ModificarUniqueAtributo: res = new ComandoClickModificarUniqueAtrib(ctrl); break;
        case PanelDiseno_Click_EliminarRelacionNormal: res = new ComandoClickEliminarRelacionNormal(ctrl); break;
        case PanelDiseno_Click_EliminarDominio: res = new ComandoClickEliminarDominio(ctrl); break;
        
        /* Frames */
        case GUIInsertarAgregacion: res = new ComandoClickInsertarAgregacion(ctrl); break;
        case GUIModificarAtributo_Click_ModificarAtributo: res = new ComandoClickModificarAtributo(ctrl); break;
        case GUIAnadirEntidadARelacion_ClickBotonAnadir: res = new ComandoAnadirEntidadARelacion(ctrl); break;
        case GUIPonerUniquesAEntidad_Click_BotonAceptar: res = new ComandoAnadirUniquesEntidad(ctrl); break;
        case GUIModificarEntidad_Click_ModificarEntidad: res = new ComandoClickModificarEntidad(ctrl); break;
        case GUIQuitarEntidadPadre_ClickBotonSi: res = new ComandoQuitarEntidadPadre(ctrl); break;
        case GUIPonerUniquesARelacion_Click_BotonAceptar: res = new ComandoAnadirUniquesRelacion(ctrl); break;
		
		default: break;
		}
		return res;
	}
}
