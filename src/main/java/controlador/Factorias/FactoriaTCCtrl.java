package controlador.Factorias;

import controlador.TC;

/* Esta factoría se encarga de devolver el TC "Controlador_" que corresponde a un mensaje que proviene de los servicios o de las vistas. 
 * Por ahora no están los mensajes de error que provienen de servicios, porque la FactoriaMsj se encarga directamente de 
 * traducirlos a cadenas de texto que se muestran después en un JOptionPane. */

public class FactoriaTCCtrl {

	public static TC getTCCtrl(TC mensajeSer) {
		TC resultado = null; 
		switch(mensajeSer) {
		case SA_RenombrarAtributo_HECHO: 
		case PanelDiseno_Click_RenombrarAtributo: 
			resultado = TC.Controlador_RenombrarAtributo;
		case SA_EliminarAtributo_HECHO:
		case PanelDiseno_Click_EliminarAtributo:
			resultado = TC.Controlador_EliminarAtributo;
		case SA_EditarDominioAtributo_HECHO:
		case PanelDiseno_Click_EditarDominioAtributo:
			resultado = TC.Controlador_EditarDominioAtributo;
		case SA_EditarCompuestoAtributo_HECHO:
		case PanelDiseno_Click_EditarCompuestoAtributo:
			resultado = TC.Controlador_EditarCompuestoAtributo;
		case SA_EditarMultivaloradoAtributo_HECHO:
		case PanelDiseno_Click_EditarMultivaloradoAtributo:
			resultado = TC.Controlador_EditarMultivaloradoAtributo;
		case SA_EditarNotNullAtributo_HECHO: 
		case PanelDiseno_Click_EditarNotNullAtributo:
			resultado = TC.Controlador_EditarNotNullAtributo;
		case SA_EditarUniqueAtributo_HECHO:
		case PanelDiseno_Click_EditarUniqueAtributo:
			resultado = TC.Controlador_EditarUniqueAtributo;
		case SA_AnadirSubAtributoAtributo_HECHO:
		case PanelDiseno_Click_AnadirSubAtributoAAtributo:
			resultado = TC.Controlador_AnadirSubAtributoAAtributo;
		case SA_EditarClavePrimariaAtributo_HECHO:
		case PanelDiseno_Click_EditarClavePrimariaAtributo:
			resultado = TC.Controlador_EditarClavePrimariaAtributo;
		case SA_AnadirRestriccionAAtributo_HECHO:
		case PanelDiseno_Click_AnadirRestriccionAAtributo:
			resultado = TC.Controlador_AnadirRestriccionAtributo;
		case SA_QuitarRestriccionAAtributo_HECHO:
			resultado = TC.Controlador_QuitarRestriccionAtributo;
		case SA_setRestriccionesAAtributo_HECHO:
			resultado = TC.Controlador_setRestriccionesAtributo;
		case SA_MoverPosicionAtributo_HECHO:
			resultado = TC.Controlador_MoverAtributo_HECHO;
		default: break;
		}
		return resultado;
	}
}
