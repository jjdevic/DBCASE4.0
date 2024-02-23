package controlador.comandos.Dominio;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferDominio;
import utils.UtilsFunc;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoClickEliminarDominio extends Comando{

	public ComandoClickEliminarDominio(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		TransferDominio td = (TransferDominio) datos;
		Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
				UtilsFunc.crearVector(Lenguaje.text(Lenguaje.DOMAIN) + " \"" + td.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                        Lenguaje.text(Lenguaje.MODIFYING_ATTRIBUTES_WARNING4) + "\n" +
                        Lenguaje.text(Lenguaje.WISH_CONTINUE),
                Lenguaje.text(Lenguaje.DELETE_DOMAIN), null), false);
        
        int respuesta = gui.setActiva(0);
        if (respuesta == 0) {
            modelo.transfers.TipoDominio valorBase = td.getTipoBase();
            String dominioEliminado = td.getNombre();
            int cont = 0;
            TransferAtributo ta = new TransferAtributo();
            Vector<TransferAtributo> lista_atrib = (Vector<TransferAtributo>) ctrl.mensaje(TC.ObtenerListaAtributos, null);
            while (cont < lista_atrib.size()) {
                ta = lista_atrib.get(cont);
                if (ta.getDominio().equals(dominioEliminado)) {
                    Vector<Object> v = new Vector();
                    v.add(ta);
                    String valorB;
                    if (valorBase.name().equals("TEXT") || valorBase.name().equals("VARCHAR"))
                        valorB = valorBase + "(20)";
                    else valorB = valorBase.toString();

                    v.add(valorB);

                    if (valorBase.name().equals("TEXT") || valorBase.name().equals("VARCHAR")) v.add("20");
                    Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarDomnioAtributo(v);
                    tratarContexto(ctxt);
                }
                cont++;
            }
            // Eliminamos el dominio
            getFactoriaServicios().getServicioDominios().eliminarDominio(td);
        }
	}

}
