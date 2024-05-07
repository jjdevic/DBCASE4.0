package controlador.comandos;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import controlador.comandos.Agregacion.ComandoInsertarAgregacion;
import controlador.comandos.Atributo.*;
import controlador.comandos.Dominio.ComandoEliminarDominio;
import controlador.comandos.Entidad.ComandoAnadirUniquesEntidad;
import controlador.comandos.Entidad.ComandoEliminarEntidad;
import controlador.comandos.Entidad.ComandoInsertarEntidadDebil;
import controlador.comandos.Entidad.ComandoModificarEntidad;
import controlador.comandos.Relacion.*;
import controlador.comandos.Vistas.*;

/**
 * Esta factoría devuelve el comando asociado a un mensaje TC.
 */
public class FactoriaComandos {

	/**
	 * @param mensaje Mensaje TC cuyo comando asociado se quiere obtener
	 * @param ctrl Controlador
	 * @return Comando asociado a mensaje si existe, null en caso contrario
	 */
	public static Comando getComando(TC mensaje, Controlador ctrl) {
		Comando res = null;
		switch(mensaje) {
		
		case GUI_WorkSpace_Nuevo: res = new ComandoWorkspaceNuevo(ctrl); break;
		case GUI_WorkSpace_Click_Abrir: res = new ComandoAbrir(ctrl); break;
		case GUI_WorkSpace_Click_Guardar: case Guardar: res = new ComandoGuardar(ctrl); break;
		case GUI_WorkSpace_Click_Guardar_Backup: res = new ComandoGuardarBackup(ctrl); break;
		
		case PanelDiseno_Click_EliminarEntidad: res = new ComandoEliminarEntidad(ctrl); break;
        case PanelDiseno_Click_EliminarAtributo: res = new ComandoEliminarAtributo(ctrl); break;
        case PanelDiseno_Click_DebilitarRelacion: res = new ComandoDebilitarRelacion(ctrl); break;
        case PanelDiseno_Click_EditarUniqueAtributo: res = new ComandoEditarUniqueAtributo(ctrl); break;
        case PanelDiseno_Click_EditarCompuestoAtributo: res = new ComandoEditarCompuestoAtrib(ctrl); break;
        case PanelDiseno_Click_Pegar: res = new ComandoPegar(ctrl); break;
        case PanelDiseno_Click_EliminarReferenciasUniqueAtributo: res = new ComandoEliminarRefUniqueAtr(ctrl); break;
        case PanelDiseno_Click_ModificarUniqueAtributo: res = new ComandoModificarUniqueAtrib(ctrl); break;
        case PanelDiseno_Click_EliminarRelacionNormal: case Controlador_EliminarRelacionNormal: res = new ComandoEliminarRelacionNormal(ctrl); break;
        case PanelDiseno_Click_EliminarDominio: res = new ComandoEliminarDominio(ctrl); break;
        
        case GUIInsertarAgregacion: res = new ComandoInsertarAgregacion(ctrl); break;
        case GUIModificarAtributo_Click_ModificarAtributo: res = new ComandoModificarAtributo(ctrl); break;
        case GUIAnadirEntidadARelacion_ClickBotonAnadir: res = new ComandoAnadirEntidadARelacion(ctrl); break;
        case GUIPonerUniquesAEntidad_Click_BotonAceptar: res = new ComandoAnadirUniquesEntidad(ctrl); break;
        case GUIModificarEntidad_Click_ModificarEntidad: res = new ComandoModificarEntidad(ctrl); break;
        case GUIQuitarEntidadPadre_ClickBotonSi: res = new ComandoQuitarEntidadPadre(ctrl); break;
        case GUIPonerUniquesARelacion_Click_BotonAceptar: res = new ComandoAnadirUniquesRelacion(ctrl); break;
        case GUI_Principal_EditarElemento: res = new ComandoEditarElemento(ctrl); break;
        case GUIInsertarEntidadDebil_Click_BotonInsertar: res = new ComandoInsertarEntidadDebil(ctrl); break;
        case ModificarCardinalidadRelacion_1a1:	res = new ComandoModificarCardinalidadRelacion1a1(ctrl); break;
    	case EliminarSubatributosAtributo: res = new ComandoEliminarSubatributos(ctrl); break;
    	case Controlador_InsertarRelacionDebil: res = new ComandoCrearRelacionDebil(ctrl); break;
		default: break;
		}
		return res;
	}
}
