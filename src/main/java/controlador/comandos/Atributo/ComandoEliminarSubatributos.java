package controlador.comandos.Atributo;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import modelo.transfers.TransferAtributo;

public class ComandoEliminarSubatributos extends Comando{

	public ComandoEliminarSubatributos(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		TransferAtributo ta = (TransferAtributo) datos;
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
