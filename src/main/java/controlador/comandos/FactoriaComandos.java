package controlador.comandos;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
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
		switch(mensaje) {
		
		/* GUI_WorkSpace */
		case GUI_WorkSpace_Nuevo: return new ComandoWorkspaceNuevo(ctrl);
		case GUI_WorkSpace_Click_Abrir: return new ComandoClickAbrir(ctrl);
		case GUI_WorkSpace_Click_Guardar: return new ComandoClickGuardar(ctrl);
		case GUI_WorkSpace_Click_Guardar_Backup: return new ComandoClickGuardarBackup(ctrl);
		
		/* Panel diseno */
		case PanelDiseno_Click_EliminarEntidad: return new ComandoClickEliminarEntidad(ctrl);
        case PanelDiseno_Click_EliminarAtributo: return new ComandoClickEliminarAtributo(ctrl);
        case PanelDiseno_Click_DebilitarRelacion: return new ComandoClickDebilitarRelacion(ctrl);
        case PanelDiseno_Click_EditarUniqueAtributo: return new ComandoClickEditarUniqueAtributo(ctrl);
        case PanelDiseno_Click_EditarCompuestoAtributo: return new ComandoClickEditarCompuestoAtrib(ctrl);
        case PanelDiseno_Click_Pegar: return new ComandoClickPegar(ctrl);
        case PanelDiseno_Click_EliminarReferenciasUniqueAtributo: return new ComandoClickEliminarRefUniqueAtr(ctrl);
        case PanelDiseno_Click_ModificarUniqueAtributo: return new ComandoClickModificarUniqueAtrib(ctrl);
        case PanelDiseno_Click_EliminarRelacionNormal: return new ComandoClickEliminarRelacionNormal(ctrl);
        case PanelDiseno_Click_EliminarDominio: return new ComandoClickEliminarDominio(ctrl);
		
		default: return null;
		}
	}
}
