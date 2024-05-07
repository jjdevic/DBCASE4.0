package controlador.comandos.Atributo;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import excepciones.ExceptionAp;
import modelo.transfers.TransferAtributo;

import java.util.Vector;

public class ComandoEliminarSubatributos extends Comando{

	public ComandoEliminarSubatributos(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		Contexto resultado = null;
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
        resultado = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
        return resultado;
	}

}
