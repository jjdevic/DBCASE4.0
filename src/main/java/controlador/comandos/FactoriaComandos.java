package controlador.comandos;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import controlador.comandos.Agregacion.ComandoClickInsertarAgregacion;
import controlador.comandos.Atributo.ComandoClickEditarCompuestoAtrib;
import controlador.comandos.Atributo.ComandoClickEditarUniqueAtributo;
import controlador.comandos.Atributo.ComandoClickEliminarAtributo;
import controlador.comandos.Atributo.ComandoClickEliminarRefUniqueAtr;
import controlador.comandos.Atributo.ComandoClickModificarAtributo;
import controlador.comandos.Atributo.ComandoClickModificarUniqueAtrib;
import controlador.comandos.Atributo.ComandoEliminarSubatributos;
import controlador.comandos.Dominio.ComandoClickEliminarDominio;
import controlador.comandos.Entidad.ComandoAnadirUniquesEntidad;
import controlador.comandos.Entidad.ComandoClickEliminarEntidad;
import controlador.comandos.Entidad.ComandoClickModificarEntidad;
import controlador.comandos.Entidad.ComandoInsertarEntidadDebil;
import controlador.comandos.Otros.ComandoEditarElemento;
import controlador.comandos.Relacion.ComandoAnadirEntidadARelacion;
import controlador.comandos.Relacion.ComandoAnadirUniquesRelacion;
import controlador.comandos.Relacion.ComandoClickDebilitarRelacion;
import controlador.comandos.Relacion.ComandoClickEliminarRelacionNormal;
import controlador.comandos.Relacion.ComandoModificarCardinalidadRelacion1a1;
import controlador.comandos.Relacion.ComandoQuitarEntidadPadre;
import controlador.comandos.Vistas.ComandoClickAbrir;
import controlador.comandos.Vistas.ComandoClickGuardar;
import controlador.comandos.Vistas.ComandoClickGuardarBackup;
import controlador.comandos.Vistas.ComandoClickPegar;
import controlador.comandos.Vistas.ComandoWorkspaceNuevo;

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
		case GUI_WorkSpace_Click_Abrir: res = new ComandoClickAbrir(ctrl); break;
		case GUI_WorkSpace_Click_Guardar: res = new ComandoClickGuardar(ctrl); break;
		case GUI_WorkSpace_Click_Guardar_Backup: res = new ComandoClickGuardarBackup(ctrl); break;
		
		case PanelDiseno_Click_EliminarEntidad: res = new ComandoClickEliminarEntidad(ctrl); break;
        case PanelDiseno_Click_EliminarAtributo: res = new ComandoClickEliminarAtributo(ctrl); break;
        case PanelDiseno_Click_DebilitarRelacion: res = new ComandoClickDebilitarRelacion(ctrl); break;
        case PanelDiseno_Click_EditarUniqueAtributo: res = new ComandoClickEditarUniqueAtributo(ctrl); break;
        case PanelDiseno_Click_EditarCompuestoAtributo: res = new ComandoClickEditarCompuestoAtrib(ctrl); break;
        case PanelDiseno_Click_Pegar: res = new ComandoClickPegar(ctrl); break;
        case PanelDiseno_Click_EliminarReferenciasUniqueAtributo: res = new ComandoClickEliminarRefUniqueAtr(ctrl); break;
        case PanelDiseno_Click_ModificarUniqueAtributo: res = new ComandoClickModificarUniqueAtrib(ctrl); break;
        case PanelDiseno_Click_EliminarRelacionNormal: case Controlador_EliminarRelacionNormal: res = new ComandoClickEliminarRelacionNormal(ctrl); break;
        case PanelDiseno_Click_EliminarDominio: res = new ComandoClickEliminarDominio(ctrl); break;
        
        case GUIInsertarAgregacion: res = new ComandoClickInsertarAgregacion(ctrl); break;
        case GUIModificarAtributo_Click_ModificarAtributo: res = new ComandoClickModificarAtributo(ctrl); break;
        case GUIAnadirEntidadARelacion_ClickBotonAnadir: res = new ComandoAnadirEntidadARelacion(ctrl); break;
        case GUIPonerUniquesAEntidad_Click_BotonAceptar: res = new ComandoAnadirUniquesEntidad(ctrl); break;
        case GUIModificarEntidad_Click_ModificarEntidad: res = new ComandoClickModificarEntidad(ctrl); break;
        case GUIQuitarEntidadPadre_ClickBotonSi: res = new ComandoQuitarEntidadPadre(ctrl); break;
        case GUIPonerUniquesARelacion_Click_BotonAceptar: res = new ComandoAnadirUniquesRelacion(ctrl); break;
        case GUI_Principal_EditarElemento: res = new ComandoEditarElemento(ctrl); break;
        case GUIInsertarEntidadDebil_Click_BotonInsertar: res = new ComandoInsertarEntidadDebil(ctrl); break;
        case ModificarCardinalidadRelacion_1a1:	res = new ComandoModificarCardinalidadRelacion1a1(ctrl); break;
    	case EliminarSubatributosAtributo: res = new ComandoEliminarSubatributos(ctrl); break;
		default: break;
		}
		return res;
	}
}
