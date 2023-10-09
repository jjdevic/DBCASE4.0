package controlador.comandos;

import controlador.Controlador;
import controlador.TC;
import controlador.comandos.GUI_Workspace.ComandoClickAbrir;
import controlador.comandos.GUI_Workspace.ComandoClickGuardar;
import controlador.comandos.GUI_Workspace.ComandoClickGuardarBackup;
import controlador.comandos.GUI_Workspace.ComandoWorkspaceNuevo;

public class FactoriaComandos {

	public static Comando getComando(TC mensaje, Controlador ctrl) {
		switch(mensaje) {
		case GUI_WorkSpace_Nuevo: return new ComandoWorkspaceNuevo(ctrl);
		case GUI_WorkSpace_Click_Abrir: return new ComandoClickAbrir(ctrl);
		case GUI_WorkSpace_Click_Guardar: return new ComandoClickGuardar(ctrl);
		case GUI_WorkSpace_Click_Guardar_Backup: return new ComandoClickGuardarBackup(ctrl);
		default: return null;
		}
	}
}
