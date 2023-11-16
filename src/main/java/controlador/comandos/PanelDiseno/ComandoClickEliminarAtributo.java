package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import controlador.comandos.Comando;
import modelo.transfers.TransferAtributo;
import vista.Lenguaje;

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
        int respuesta = 0;
        if (!ctrl.getConfirmarEliminaciones()) preguntar = false;
        if (preguntar) {
            String eliminarSubatributos = "";
            if (!ta.getListaComponentes().isEmpty())
                eliminarSubatributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING) + "\n";
            respuesta = ctrl.getPanelOpciones().setActiva(
                    Lenguaje.text(Lenguaje.ATTRIBUTE) + " \"" + ta.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                            eliminarSubatributos + Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DELETE_ATTRIB));
        }
        if (respuesta == 0) {
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
            Contexto ctxt = ctrl.getTheServiciosAtributos().eliminarAtributo(clon_atributo3, intAux);
            ctrl.tratarContexto(ctxt);
        }
	}

}
