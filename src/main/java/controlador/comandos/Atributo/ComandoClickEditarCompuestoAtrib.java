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
            		UtilsFunc.crearVectorSinNulls(Lenguaje.text(Lenguaje.MODIFY_ATTRIBUTE) + "\"" + ta.getNombre() + "\"" +
                            Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING3) + "\n" +
                            Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DBCASE), null));
            Boolean respuesta = gui.setActiva(0);
            if (!respuesta) {
                // Eliminamos sus subatributos
                Vector lista_atributos = ta.getListaComponentes();
                int cont = 0;
                TransferAtributo tah = new TransferAtributo();
                while (cont < lista_atributos.size()) {
                    String idAtributo = (String) lista_atributos.get(cont);
                    tah.setIdAtributo(Integer.parseInt(idAtributo));
                    Contexto ctxt = getFactoriaServicios().getServicioAtributos().eliminarAtributo(tah, 1);
                    tratarContexto(ctxt);
                    cont++;
                }
                // Modificamos el atributo
                ta.getListaComponentes().clear();
                Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
                tratarContexto(ctxt);
            }
        }
        // Si no es compuesto o es compuesto pero no tiene subatributos
        else {
            Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
            tratarContexto(ctxt);
        }
	}

}
