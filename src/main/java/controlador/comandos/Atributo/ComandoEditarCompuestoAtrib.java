package controlador.comandos.Atributo;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import excepciones.ExceptionAp;
import misc.UtilsFunc;
import modelo.transfers.TransferAtributo;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoEditarCompuestoAtrib extends Comando{

	public ComandoEditarCompuestoAtrib(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp  {
		Contexto resultado = null;
		TransferAtributo ta = (TransferAtributo) datos;
		boolean actuar = true;
        // Si es un atributo compuesto y tiene subatributos al ponerlo como simple hay que eliminar sus atributos
        if (ta.getCompuesto() && !ta.getListaComponentes().isEmpty()) {
        	Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
        			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.MODIFY_ATTRIBUTE) + "\"" + ta.getNombre() + "\"" +
                            Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING3) + "\n" +
                            Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DBCASE), null), false);
            int respuesta = gui.setActiva(0);
            if(respuesta != 0) {
            	actuar = false;
            	ta.getListaComponentes().clear();
            }
        }
        
        if(actuar) {
            resultado = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
        }
        return resultado;
	}
}