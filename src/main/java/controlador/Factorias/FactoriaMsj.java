package controlador.Factorias;

import controlador.TC;
import vista.Lenguaje;

/**
 * Esta factoría se encarga de devolver la cadena de texto de error asociada a un mensaje TC de error
 */

public class FactoriaMsj {

	/**
	 * @param tc Mensaje TC de error
	 * @return Cadena de texto de error asociada a tc si existe, null en caso contrario
	 */
	public static String getMsj(TC tc) {
		String resultado = null;
		Integer msj;
		switch(tc) {
		//Atrib
		case SA_EliminarAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio: msj= Lenguaje.EMPTY_ATTRIB_NAME; break;
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste: msj= Lenguaje.REPEATED_SUBATR_NAME; break;
		case SA_RenombrarAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_EditarDominioAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero: msj= Lenguaje.INCORRECT_SIZE1; break;
		case SA_EditarDominioAtributo_ERROR_TamanoEsNegativo: msj= Lenguaje.INCORRECT_SIZE2; break;
		case SA_EditarCompuestoAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_EditarNotNullAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio: msj= Lenguaje.EMPTY_SUBATTR_NAME; break;
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste: msj= Lenguaje.REPEATED_SUBATR_NAME; break;
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero: msj= Lenguaje.INCORRECT_SIZE1; break;
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo: msj= Lenguaje.INCORRECT_SIZE2; break;
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		case SA_MoverPosicionAtributo_ERROR_DAOAtributos: msj= Lenguaje.ATTRIBUTES_FILE_ERROR; break;
		
		//Agreg
		case SAG_InsertarAgregacion_ERROR_NombreVacio: msj = Lenguaje.EMPTY_AG_NAME; break;
        case SAG_InsertarAgregacion_ERROR_NombreDeYaExiste: msj = Lenguaje.REPEATED_AGREG_NAME; break;
        case SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SAG_InsertarAgregacion_ERROR_DAO: msj = Lenguaje.AGGREGATIONS_FILE_ERROR; break;
        case SAG_RenombrarAgregacion_ERROR_NombreVacio: msj = Lenguaje.EMPTY_AGREG_NAME; break;
        
        //Relaciones
        case SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio: msj = Lenguaje.EMPTY_REL_NAME; break;
        case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoAgregacion: msj = Lenguaje.REPEATED_AGREG_NAME; break;
        case SR_InsertarRelacion_ERROR_NombreDelRolYaExiste: msj = Lenguaje.REPEATED_ROL_NAME; break;
        case SR_InsertarRelacion_ERROR_NombreDeRolNecesario: msj = Lenguaje.NECESARY_ROL; break;
        case SR_InsertarRelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_EliminarRelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_RenombrarRelacion_ERROR_NombreDeRelacionEsVacio: msj = Lenguaje.EMPTY_REL_NAME; break;
        case SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExiste: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SR_RenombrarRelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_RenombrarRelacion_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SR_DebilitarRelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_MoverPosicionRelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoVacio: msj = Lenguaje.EMPTY_ATTRIB_NAME; break;
        case SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoYaExiste: msj = Lenguaje.REPEATED_ATTRIB_NAME_REL; break;
        case SR_AnadirAtributoARelacion_ERROR_TamanoNoEsEntero: msj = Lenguaje.INCORRECT_SIZE1; break;
        case SR_AnadirAtributoARelacion_ERROR_TamanoEsNegativo: msj = Lenguaje.INCORRECT_SIZE2; break;
        case SR_AnadirAtributoARelacion_ERROR_DAOAtributos: msj = Lenguaje.ATTRIBUTES_FILE_ERROR; break;
        case SR_AnadirAtributoARelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_EstablecerEntidadPadre_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_QuitarEntidadPadre_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_AnadirEntidadHija_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_QuitarEntidadHija_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_EliminarRelacionIsA_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_EliminarRelacionNormal_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_InsertarRelacionIsA_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_AnadirEntidadARelacion_ERROR_InicioNoEsEnteroOn: msj = Lenguaje.INCORRECT_CARDINALITY1; break;
        case SR_AnadirEntidadARelacion_ERROR_InicioEsNegativo: msj = Lenguaje.INCORRECT_CARDINALITY2; break;
        case SR_AnadirEntidadARelacion_ERROR_FinalNoEsEnteroOn: msj = Lenguaje.INCORRECT_CARDINALITY3; break;
        case SR_AnadirEntidadARelacion_ERROR_FinalEsNegativo: msj = Lenguaje.INCORRECT_CARDINALITY4; break;
        case SR_AnadirEntidadARelacion_ERROR_InicioMayorQueFinal: msj = Lenguaje.INCORRECT_CARDINALITY5; break;
        case SR_AnadirEntidadARelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_QuitarEntidadARelacion_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SR_EditarCardinalidadEntidad_ERROR_InicioNoEsEnteroOn: msj = Lenguaje.INCORRECT_CARDINALITY1; break;
        case SR_EditarCardinalidadEntidad_ERROR_InicioEsNegativo: msj = Lenguaje.INCORRECT_CARDINALITY2; break;   
        case SR_EditarCardinalidadEntidad_ERROR_FinalNoEsEnteroOn: msj = Lenguaje.INCORRECT_CARDINALITY3; break;
        case SR_EditarCardinalidadEntidad_ERROR_FinalEsNegativo: msj = Lenguaje.INCORRECT_CARDINALITY4; break;
        case SR_EditarCardinalidadEntidad_ERROR_InicioMayorQueFinal: msj = Lenguaje.INCORRECT_CARDINALITY5; break;
        case SR_EditarCardinalidadEntidad_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        
        //Dominio
        case SD_InsertarDominio_ERROR_NombreDeDominioEsVacio: msj = Lenguaje.EMPTY_DOM_NAME; break;
        case SD_InsertarDominio_ERROR_NombreDeDominioYaExiste: msj = Lenguaje.REPEATED_DOM_NAME; break;
        case SD_InsertarDominio_ERROR_DAO: msj = Lenguaje.DOMAINS_FILE_ERROR; break;
        case SD_RenombrarDominio_ERROR_NombreDeDominioEsVacio: msj = Lenguaje.EMPTY_DOM_NAME; break;
        case SD_RenombrarDominio_ERROR_NombreDeDominioYaExiste: msj = Lenguaje.REPEATED_DOM_NAME; break;
        case SD_RenombrarDominio_ERROR_DAODominios: msj = Lenguaje.DOMAINS_FILE_ERROR; break;
        case SD_EliminarDominio_ERROR_DAODominios: msj = Lenguaje.DOMAINS_FILE_ERROR; break;
        case SD_ModificarTipoBaseDominio_ERROR_DAODominios: msj = Lenguaje.REPEATED_DOM_NAME; break;
        case SD_ModificarTipoBaseDominio_ERROR_TipoBaseDominioEsVacio: msj = Lenguaje.EMPTY_TYPE_NAME; break;
        case SD_ModificarElementosDominio_ERROR_DAODominios: msj = Lenguaje.REPEATED_DOM_NAME; break; 
        case SD_ModificarElementosDominio_ERROR_ElementosDominioEsVacio: msj = Lenguaje.EMPTY_VALUES; break;
        case SD_InsertarDominio_ERROR_ValorNoValido_1: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " 1, 0.5, 1.5E100, -1, -0.5";
        case SD_InsertarDominio_ERROR_ValorNoValido_2: resultado = Lenguaje.text(Lenguaje.INCORRECT_BIT_VALUE);
        case SD_InsertarDominio_ERROR_ValorNoValido_3: resultado = Lenguaje.text(Lenguaje.QUOTATION_MARKS);
        case SD_InsertarDominio_ERROR_ValorNoValido_4: resultado = Lenguaje.text(Lenguaje.INCORRECT_DATE);
        case SD_InsertarDominio_ERROR_ValorNoValido_5: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " '00:00:00.999', '22', '22:05', '22:59:59'";
        case SD_InsertarDominio_ERROR_ValorNoValido_6: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " '20201125','281125 12','281125 12:34:00','281125 12:34:00.200' ";
        case SD_InsertarDominio_ERROR_ValorNoValido_7: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " '20231125','20231125 12','20201125 12:34:00','20191125 12:34:00.200' ";
        case SD_InsertarDominio_ERROR_ValorNoValido_8: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " '20191125','20251125 12','20121125 12:34:00','20081125 12:34:00.200' ";
        case SD_InsertarDominio_ERROR_ValorNoValido_9: resultado = Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " 1, 0.5";
        case SD_InsertarDominio_ERROR_ValorNoValido_10: resultado = Lenguaje.text(Lenguaje.EMPTY_VALUE);
        case SD_InsertarDominio_ERROR_ValorNoValido_11: resultado = Lenguaje.text(Lenguaje.INCORRECT_NUMBER);
        
        //Entidad
        case SE_InsertarEntidad_ERROR_NombreDeEntidadEsVacio: msj = Lenguaje.EMPTY_ENT_NAME; break;
        case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadEsVacio: msj = Lenguaje.EMPTY_ENT_NAME; break;
        case SE_InsertarEntidad_ERROR_NombreDeEntidadYaExiste: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExiste: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SE_InsertarRelacion_ERROR_NombreDeEntidadYaExisteComoAgregacion: msj = Lenguaje.REPEATED_AGREG_NAME; break;
        case SE_InsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SE_InsertarEntidad_ERROR_DAO: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SE_RenombrarEntidad_ERROR_NombreDeEntidadEsVacio: msj = Lenguaje.EMPTY_ENT_NAME; break;
        case SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExiste: msj = Lenguaje.REPEATED_ENT_NAME; break;
        case SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion: msj = Lenguaje.REPEATED_REL_NAME; break;
        case SE_RenombrarEntidad_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SE_RenombrarEntidad_ERROR_DAORelaciones: msj = Lenguaje.RELATIONS_FILE_ERROR; break;
        case SE_DebilitarEntidad_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoVacio: msj = Lenguaje.EMPTY_ATTRIB_NAME; break;
        case SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoYaExiste: msj = Lenguaje.REPEATED_ATTRIB_NAME; break;
        case SE_AnadirAtributoAEntidad_ERROR_TamanoNoEsEntero: msj = Lenguaje.INCORRECT_SIZE3; break;
        case SE_AnadirAtributoAEntidad_ERROR_TamanoEsNegativo: msj = Lenguaje.INCORRECT_SIZE2; break;
        case SE_AnadirAtributoAEntidad_ERROR_DAOAtributos: msj = Lenguaje.ATTRIBUTES_FILE_ERROR; break;
        case SE_AnadirAtributoAEntidad_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SE_EliminarEntidad_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        case SE_MoverPosicionEntidad_ERROR_DAOEntidades: msj = Lenguaje.ENTITIES_FILE_ERROR; break;
        
        case FALLO_CREAR_ARCHIVO: msj = Lenguaje.ERROR_CREATING_FILE; break;
        case SCRIPT_ERROR: msj = Lenguaje.SCRIPT_ERROR; break;
        case MUST_GENERATE_SCRIPT: msj = Lenguaje.MUST_GENERATE_SCRIPT; break;
        case NO_DB_CONEXION: msj = Lenguaje.NO_DB_CONEXION; break;
        case CANT_CLOSE_CONEXION: msj = Lenguaje.CANT_CLOSE_CONEXION; break;
        case CANT_EXECUTE_SCRIPT: msj = Lenguaje.CANT_EXECUTE_SCRIPT; break;
        
        case ERROR_PERSISTENCIA: msj = Lenguaje.UNESPECTED_XML_ERROR; break;
		
		default: msj = null; break;
		}
		//Transformar a texto si no ha sido ya transformado
		if(resultado == null && msj != null) resultado = Lenguaje.text(msj);
		return resultado;
	}
}
