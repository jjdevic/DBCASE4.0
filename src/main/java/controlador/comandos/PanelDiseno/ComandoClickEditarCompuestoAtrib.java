package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Contexto;
import controlador.Controlador;
import controlador.comandos.Comando;
import modelo.transfers.TransferAtributo;
import vista.Lenguaje;

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
            int respuesta = ctrl.getPanelOpciones().setActiva(
                    Lenguaje.text(Lenguaje.MODIFY_ATTRIBUTE) + "\"" + ta.getNombre() + "\"" +
                            Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING3) + "\n" +
                            Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DBCASE));
            if (respuesta == 0) {
                // Eliminamos sus subatributos
                Vector lista_atributos = ta.getListaComponentes();
                int cont = 0;
                TransferAtributo tah = new TransferAtributo();
                while (cont < lista_atributos.size()) {
                    String idAtributo = (String) lista_atributos.get(cont);
                    tah.setIdAtributo(Integer.parseInt(idAtributo));
                    Contexto ctxt = ctrl.getFactoriaServicios().getServicioAtributos().eliminarAtributo(tah, 1);
                    ctrl.tratarContexto(ctxt);
                    cont++;
                }
                // Modificamos el atributo
                ta.getListaComponentes().clear();
                Contexto ctxt = ctrl.getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
                ctrl.tratarContexto(ctxt);
            }
        }
        // Si no es compuesto o es compuesto pero no tiene subatributos
        else {
            Contexto ctxt = ctrl.getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
            ctrl.tratarContexto(ctxt);
        }
	}

}
