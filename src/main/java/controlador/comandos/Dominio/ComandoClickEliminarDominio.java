package controlador.comandos.Dominio;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferDominio;
import vista.Lenguaje;

public class ComandoClickEliminarDominio extends Comando{

	public ComandoClickEliminarDominio(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		TransferDominio td = (TransferDominio) datos;
        int respuesta = ctrl.getPanelOpciones().setActiva(
                Lenguaje.text(Lenguaje.DOMAIN) + " \"" + td.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                        Lenguaje.text(Lenguaje.MODIFYING_ATTRIBUTES_WARNING4) + "\n" +
                        Lenguaje.text(Lenguaje.WISH_CONTINUE),
                Lenguaje.text(Lenguaje.DELETE_DOMAIN));
        if (respuesta == 0) {
            modelo.transfers.TipoDominio valorBase = td.getTipoBase();
            String dominioEliminado = td.getNombre();
            //TODO Tomarlas de modelos
            getFactoriaServicios().getServicioAtributos().getListaDeAtributos();
            int cont = 0;
            TransferAtributo ta = new TransferAtributo();
            while (cont < ctrl.getListaAtributos().size()) {
                ta = ctrl.getListaAtributos().get(cont);
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
