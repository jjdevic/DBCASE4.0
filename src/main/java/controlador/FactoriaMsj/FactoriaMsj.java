package controlador.FactoriaMsj;

import controlador.TC;
import vista.Lenguaje;

public class FactoriaMsj {

	public static String getMsj(TC tc) {
		String msj;
		switch(tc) {
		case SA_EliminarAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio: msj= Lenguaje.text(Lenguaje.EMPTY_ATTRIB_NAME); break;
		case SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste: msj= Lenguaje.text(Lenguaje.REPEATED_SUBATR_NAME); break;
		case SA_RenombrarAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_EditarDominioAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero: msj= Lenguaje.text(Lenguaje.INCORRECT_SIZE1); break;
		case SA_EditarDominioAtributo_ERROR_TamanoEsNegativo: msj= Lenguaje.text(Lenguaje.INCORRECT_SIZE2); break;
		case SA_EditarCompuestoAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_EditarNotNullAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio: msj= Lenguaje.text(Lenguaje.EMPTY_SUBATTR_NAME); break;
		case SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste: msj= Lenguaje.text(Lenguaje.REPEATED_SUBATR_NAME); break;
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero: msj= Lenguaje.text(Lenguaje.INCORRECT_SIZE1); break;
		case SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo: msj= Lenguaje.text(Lenguaje.INCORRECT_SIZE2); break;
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		case SA_MoverPosicionAtributo_ERROR_DAOAtributos: msj= Lenguaje.text(Lenguaje.ATTRIBUTES_FILE_ERROR); break;
		//case : msj= Lenguaje.text(); break;
		
		default: msj = null; break;
		}
		return msj;
	}
}
