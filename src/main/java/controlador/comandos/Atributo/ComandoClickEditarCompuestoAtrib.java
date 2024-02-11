package controlador.comandos.Atributo;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import utils.UtilsFunc;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoClickEditarCompuestoAtrib extends Comando{

	public ComandoClickEditarCompuestoAtrib(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		TransferAtributo ta = (TransferAtributo) datos;
        // Si es un atributo compuesto y tiene subatributos al ponerlo como simple hay que eliminar sus atributos
        if (ta.getCompuesto() && !ta.getListaComponentes().isEmpty()) {
	/*	Object[] options = {Lenguaje.getMensaje(Lenguaje.YES),Lenguaje.getMensaje(Lenguaje.NO)};
		int respuesta = JOptionPane.showOptionDialog(
				null,
				Lenguaje.getMensaje(Lenguaje.MODIFY_ATTRIBUTE)+"\""+ta.getNombre()+"\""+
				Lenguaje.getMensaje(Lenguaje.DELETE_ATTRIBUTES_WARNING3)+"\n" +
				Lenguaje.getMensaje(Lenguaje.WISH_CONTINUE),
				Lenguaje.getMensaje(Lenguaje.DBCASE),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);*/
        	Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, null, false);
            gui.setDatos(
            		UtilsFunc.crearVector(Lenguaje.text(Lenguaje.MODIFY_ATTRIBUTE) + "\"" + ta.getNombre() + "\"" +
                            Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING3) + "\n" +
                            Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DBCASE), null, TC.EliminarSubatributosAtributo, ta, null));
            gui.setActiva(0);
        }
        // Si no es compuesto o es compuesto pero no tiene subatributos
        else {
            Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
            tratarContexto(ctxt);
        }
	}

}
