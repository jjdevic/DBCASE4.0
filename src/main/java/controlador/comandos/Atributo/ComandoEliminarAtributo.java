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

import java.util.Vector;

public class ComandoEliminarAtributo extends Comando{

	public ComandoEliminarAtributo(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp  {
		Contexto resultado = null;
		Vector<Object> v = (Vector<Object>) datos;
        TransferAtributo ta = (TransferAtributo) v.get(0);
        ctrl.setAntiguosSubatributos(ta.getListaComponentes());
        
        int intAux = (int) v.get(2);
        boolean preguntar = (Boolean) v.get(1);
        int respuesta = 0;
        if (!ctrl.getConfirmarEliminaciones()) preguntar = false;
        if (preguntar) {
            String eliminarSubatributos = "";
            if (!ta.getListaComponentes().isEmpty())
                eliminarSubatributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING) + "\n";
            
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
            		UtilsFunc.crearVector(Lenguaje.text(Lenguaje.ATTRIBUTE) + " \"" + ta.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                            eliminarSubatributos + Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DELETE_ATTRIB), null), false);
            respuesta = gui.setActiva(0);        
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
            resultado = getFactoriaServicios().getServicioAtributos().eliminarAtributo(clon_atributo3, intAux);
        }
        
        return resultado;
	}

}
