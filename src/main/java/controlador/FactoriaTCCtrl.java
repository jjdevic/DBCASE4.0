package controlador;

/** Esta factoría se encarga de devolver el TC "Controlador_" que corresponde a un mensaje que proviene de los servicios 
 * o de las vistas. 
 * No están los mensajes de error que provienen de servicios, porque la FactoriaMsj se encarga directamente de 
 * traducirlos a cadenas de texto. */

public class FactoriaTCCtrl {

	/** 
	 * @param mensaje Mensaje que se desea traducir
	 * @return Mensaje traducido si existe, null en caso contrario
	 */
	public static TC getTCCtrl(TC mensaje) {
		TC resultado = null; 
		switch(mensaje) {
		//Atrib
		case SA_RenombrarAtributo_HECHO: case PanelDiseno_Click_RenombrarAtributo: resultado = TC.Controlador_RenombrarAtributo; break;
		case SA_EliminarAtributo_HECHO: case PanelDiseno_Click_EliminarAtributo: resultado = TC.Controlador_EliminarAtributo; break;
		case SA_EditarDominioAtributo_HECHO: case PanelDiseno_Click_EditarDominioAtributo: resultado = TC.Controlador_EditarDominioAtributo; break;
		case SA_EditarCompuestoAtributo_HECHO: case PanelDiseno_Click_EditarCompuestoAtributo: resultado = TC.Controlador_EditarCompuestoAtributo; break;
		case SA_EditarMultivaloradoAtributo_HECHO: case PanelDiseno_Click_EditarMultivaloradoAtributo: resultado = TC.Controlador_EditarMultivaloradoAtributo; break;
		case SA_EditarNotNullAtributo_HECHO: case PanelDiseno_Click_EditarNotNullAtributo: resultado = TC.Controlador_EditarNotNullAtributo; break;
		case SA_EditarUniqueAtributo_HECHO: case PanelDiseno_Click_EditarUniqueAtributo: resultado = TC.Controlador_EditarUniqueAtributo; break;
		case SA_AnadirSubAtributoAtributo_HECHO: case PanelDiseno_Click_AnadirSubAtributoAAtributo: resultado = TC.Controlador_AnadirSubAtributoAAtributo; break;
		case SA_EditarClavePrimariaAtributo_HECHO: case PanelDiseno_Click_EditarClavePrimariaAtributo: resultado = TC.Controlador_EditarClavePrimariaAtributo; break;
		case SA_AnadirRestriccionAAtributo_HECHO: case PanelDiseno_Click_AnadirRestriccionAAtributo: resultado = TC.Controlador_AnadirRestriccionAtributo; break;
		case SA_QuitarRestriccionAAtributo_HECHO: resultado = TC.Controlador_QuitarRestriccionAtributo; break;
		case SA_setRestriccionesAAtributo_HECHO: resultado = TC.Controlador_setRestriccionesAtributo; break;
		case SA_MoverPosicionAtributo_HECHO: resultado = TC.Controlador_MoverAtributo_HECHO; break;
		case PanelDiseno_Click_InsertarAtributo: case SA_AnadirAtributo_HECHO: resultado = TC.Controlador_InsertarAtributo; break;
		case GUI_Principal_EditarAtributo: resultado = TC.Controlador_ModificarAtributo; break;
		
		//Entidad
		case PanelDiseno_Click_TablaUniqueAEntidad: resultado = TC.Controlador_TablaUniqueAEntidad; break;
		case SE_InsertarEntidad_HECHO: case PanelDiseno_Click_InsertarEntidad: case GUIInsertarEntidadDebil_Click_BotonInsertar: resultado = TC.Controlador_InsertarEntidad; break; 
		case SE_RenombrarEntidad_HECHO: case PanelDiseno_Click_RenombrarEntidad: resultado = TC.Controlador_RenombrarEntidad; break; 
		case PanelDiseno_Click_AnadirRestriccionAEntidad: resultado = TC.Controlador_AnadirRestriccionEntidad; break;
		case SE_EliminarEntidad_HECHO: case PanelDiseno_Click_EliminarEntidad: resultado = TC.Controlador_EliminarEntidad; break;
		case GUI_Principal_EditarEntidad: resultado = TC.Controlador_ModificarEntidad; break;
		case SE_MoverPosicionEntidad_HECHO: resultado = TC.Controlador_MoverEntidad_HECHO; break;
		case SE_setRestriccionesAEntidad_HECHO: resultado = TC.Controlador_setRestriccionesEntidad; break;
		case SE_DebilitarEntidad_HECHO: resultado = TC.Controlador_DebilitarEntidad; break;
		case SE_setUniqueUnitarioAEntidad_HECHO: resultado = TC.Controlador_setUniqueUnitarioEntidad; break;
		case SE_setUniquesAEntidad_HECHO: resultado = TC.Controlador_setUniquesEntidad; break;
		
		//Relacion
		case SR_AnadirRestriccionARelacion_HECHO: case PanelDiseno_Click_AnadirRestriccionARelacion: resultado = TC.Controlador_AnadirRestriccionRelacion; break;
		case PanelDiseno_Click_TablaUniqueARelacion: resultado = TC.Controlador_TablaUniqueARelacion; break;
		case SE_AnadirAtributoAEntidad_HECHO: case PanelDiseno_Click_AnadirAtributoEntidad: resultado = TC.Controlador_AnadirAtributoAEntidad; break; 
		case SR_InsertarRelacion_HECHO: case PanelDiseno_Click_InsertarRelacionNormal: resultado = TC.Controlador_InsertarRelacion; break; 
		case SR_RenombrarRelacion_HECHO: case PanelDiseno_Click_RenombrarRelacion: resultado = TC.Controlador_RenombrarRelacion; break; 
		case SR_EstablecerEntidadPadre_HECHO: case PanelDiseno_Click_EstablecerEntidadPadre: resultado = TC.Controlador_EstablecerEntidadPadre; break; 
		case SR_QuitarEntidadPadre_HECHO: case PanelDiseno_Click_QuitarEntidadPadre: resultado = TC.Controlador_QuitarEntidadPadre; break; 
		case SR_AnadirEntidadHija_HECHO: case PanelDiseno_Click_AnadirEntidadHija: resultado = TC.Controlador_AnadirEntidadHija; break; 
		case SR_QuitarEntidadHija_HECHO: case PanelDiseno_Click_QuitarEntidadHija: resultado = TC.Controlador_QuitarEntidadHija; break; 
		case SR_AnadirEntidadARelacion_HECHO: case PanelDiseno_Click_AnadirEntidadARelacion: resultado = TC.Controlador_AnadirEntidadARelacion; break; 
		case SR_QuitarEntidadARelacion_HECHO: case PanelDiseno_Click_QuitarEntidadARelacion: resultado = TC.Controlador_QuitarEntidadARelacion; break; 
		case SR_EditarCardinalidadEntidad_HECHO: case PanelDiseno_Click_EditarCardinalidadEntidad: resultado = TC.Controlador_EditarCardinalidadEntidad; break; 
		case PanelDiseno_Click_AnadirAtributoRelacion: case SR_AnadirAtributoARelacion_HECHO: resultado = TC.Controlador_AnadirAtributoARelacion; break;
		case SR_EliminarRelacionNormal_HECHO: case PanelDiseno_Click_EliminarRelacionNormal: resultado = TC.Controlador_EliminarRelacionNormal; break;
		case SR_InsertarRelacionIsA_HECHO: case PanelDiseno_Click_InsertarRelacionIsA: resultado = TC.Controlador_InsertarRelacionIsA; break;
		case GUI_Principal_EditarRelacion: resultado = TC.Controlador_ModificarRelacion; break;
		case SR_MoverPosicionRelacion_HECHO: resultado = TC.Controlador_MoverRelacion_HECHO; break;
		case SR_setRestriccionesARelacion_HECHO: resultado = TC.Controlador_setRestriccionesRelacion; break;
		case SR_DebilitarRelacion_HECHO: resultado = TC.Controlador_DebilitarRelacion; break;
		case SR_setUniqueUnitarioARelacion_HECHO: resultado = TC.Controlador_setUniqueUnitarioRelacion; break;
		case SR_EliminarRelacionIsA_HECHO: resultado = TC.Controlador_EliminarRelacionIsA; break;
		case SR_setUniquesARelacion_HECHO: resultado = TC.Controlador_setUniquesRelacion; break;
		
		//Dominio
		case SD_RenombrarDominio_HECHO: case PanelDiseno_Click_RenombrarDominio: resultado = TC.Controlador_RenombrarDominio; break; 
		case SD_EliminarDominio_HECHO: resultado = TC.Controlador_EliminarDominio; break;
		case SD_ModificarElementosDominio_HECHO: resultado = TC.Controlador_ModificarValoresDominio; break;
		case SD_ModificarTipoBaseDominio_HECHO: case PanelDiseno_Click_ModificarDominio: resultado = TC.Controlador_ModificarTipoBaseDominio; break; 
		case SD_InsertarDominio_HECHO: case PanelDiseno_Click_CrearDominio: resultado = TC.Controlador_InsertarDominio; break;
		
		//Agregacion
		case PanelDiseno_Click_EditarAgregacion: resultado = TC.Controlador_EditarAgregacion; break; 
		case SAG_RenombrarAgregacion_HECHO: case GUIRenombrarAgregacion_Click_BotonRenombrar: resultado = TC.Controlador_RenombrarAgregacion; break;
		case PanelDiseno_Click_AddAgregacion: case SAG_InsertarAgregacion_HECHO: resultado = TC.Controlador_InsertarAgregacion; break;
		case PanelDiseno_Click_EliminarAgregacion: case SAG_EliminarAgregacion_HECHO: resultado = TC.Controlador_EliminarAgregacion; break;
		case GUIAnadirAtributoAgregacion_Click_BotonAnadir: case SAG_AnadirAtributoAAgregacion_HECHO: resultado = TC.Controlador_AnadirAtributoAAgregacion; break;
		
		//Conexiones
		case GUI_Principal_Click_BotonEjecutarEnDBMS: resultado = TC.Controlador_EjecutarEnDBMS; break;
		case GUISeleccionarConexion_ClickNueva: case GUISeleccionarConexion_ClickEditar: resultado = TC.Controlador_ConfigurarConexionDBMS; break;
		
		//Funcionalidad eliminar
		case PanelDiseno_Click_Eliminar: resultado = TC.Controlador_Eliminar; break;
		default: break;
		}
		return resultado;
	}
}
