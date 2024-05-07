package controlador.comandos.Vistas;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.Factorias.FactoriaTCCtrl;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import vista.frames.Parent_GUI;

import java.util.Vector;

public class ComandoEditarElemento extends Comando{

	public ComandoEditarElemento(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		if (datos instanceof TransferEntidad) {
            TransferEntidad te = (TransferEntidad) datos;
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarEntidad), datos, false);
            gui.setActiva();
        } 
        else if (datos instanceof TransferRelacion) {
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarRelacion), datos, false);
            gui.setActiva();
        } 
        else if (datos instanceof TransferAtributo) {
            TransferAtributo ta = (TransferAtributo) datos;
            
            //Buscamos a quien pertenece este atributo
            String nombrePadre = "";
            Vector<TransferEntidad> listaE = (Vector<TransferEntidad>) ctrl.mensaje(TC.ObtenerListaEntidades, null);
            Vector<TransferRelacion> listaR = (Vector<TransferRelacion>) ctrl.mensaje(TC.ObtenerListaRelaciones, null);

            for (TransferEntidad transferE : listaE) {
                Vector<String> listaA = transferE.getListaAtributos();
                for (String s : listaA) {
                    if (s.equals(Integer.toString(ta.getIdAtributo()))) {
                        nombrePadre = transferE.getNombre();
                    }
                }
            }
            for (TransferRelacion transferR : listaR) {
                Vector<String> listaA = transferR.getListaAtributos();
                for (String s : listaA) {
                    if (s.equals(Integer.toString(ta.getIdAtributo()))) {
                        nombrePadre = transferR.getNombre();
                    }
                }
            }

            //Establecer datos para la gui correspondiente y activarla
            Vector<Object> v = new Vector<Object>();
            v.add(ta);
            v.add(nombrePadre);
            
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarAtributo), null, false);
            gui.setDatos(v);
            gui.setActiva();
        }
		return null;
	}

}
