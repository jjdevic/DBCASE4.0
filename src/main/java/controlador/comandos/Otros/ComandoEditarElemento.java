package controlador.comandos.Otros;

import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import controlador.Factorias.FactoriaTCCtrl;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferDominio;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;
import vista.frames.Parent_GUI;

public class ComandoEditarElemento extends Comando{

	public ComandoEditarElemento(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		if (datos instanceof TransferEntidad) {
            TransferEntidad te = (TransferEntidad) datos;
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarEntidad), datos, false);
            gui.setDatos(te);
            gui.setActiva();
        } 
        else if (datos instanceof TransferRelacion) {
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarRelacion), datos, false);
            TransferRelacion tr = (TransferRelacion) datos;
            gui.setDatos(tr);
            gui.setActiva();
        } 
        else if (datos instanceof TransferAtributo) {
            Vector<TransferDominio> lista = getFactoriaServicios().getServicioDominios().getListaDeDominios();
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarAtributo), datos, false);
            //this.getTheGUIModificarAtributo().setListaDominios(lista);
            TransferAtributo ta = (TransferAtributo) datos;
            
            //Buscamos a quien pertenece este atributo
            String nombrePadre = "";
            DAOEntidades daoEntidades = new DAOEntidades(ctrl.getPath());
            Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();

            DAORelaciones daoRelaciones = new DAORelaciones(ctrl.getPath());
            Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();

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
            gui.setDatos(ta);
            gui.setActiva();
        }
	}

}
