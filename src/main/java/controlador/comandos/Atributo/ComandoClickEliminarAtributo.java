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

public class ComandoClickEliminarAtributo extends Comando{

	public ComandoClickEliminarAtributo(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector<Object> v = (Vector<Object>) datos;
        TransferAtributo ta = (TransferAtributo) v.get(0);
        int intAux = (int) v.get(2);
        boolean preguntar = (Boolean) v.get(1);
        Boolean respuesta = false;
        if (!ctrl.getConfirmarEliminaciones()) preguntar = false;
        if (preguntar) {
            String eliminarSubatributos = "";
            if (!ta.getListaComponentes().isEmpty())
                eliminarSubatributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING) + "\n";
            
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, null, false);
            gui.setDatos(
            		UtilsFunc.crearVector(Lenguaje.text(Lenguaje.ATTRIBUTE) + " \"" + ta.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                            eliminarSubatributos + Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DELETE_ATTRIB), null));
        }
        if (!respuesta) {
            if (ta.getUnique()) {
                Vector<Object> ve = new Vector<Object>();
                TransferAtributo clon_atributo = ta.clonar();
                ve.add(clon_atributo);
                ve.add(1);
                ctrl.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo, ve);
            }
            TransferAtributo clon_atributo2 = ta.clonar();
            ctrl.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarReferenciasUniqueAtributo, clon_atributo2);
            TransferAtributo clon_atributo3 = ta.clonar();
            Contexto ctxt = getFactoriaServicios().getServicioAtributos().eliminarAtributo(clon_atributo3, intAux);
            tratarContexto(ctxt);
        }
	}

}
