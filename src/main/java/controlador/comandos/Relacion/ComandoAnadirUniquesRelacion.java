package controlador.comandos.Relacion;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.Transfer;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferRelacion;

import java.util.Vector;

public class ComandoAnadirUniquesRelacion extends Comando {

	public ComandoAnadirUniquesRelacion(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		Vector v = (Vector<Transfer>) datos;
        tratarContexto(getFactoriaServicios().getServicioRelaciones().setUniques(v));
        
        TransferRelacion relacion = (TransferRelacion) v.get(1);
        ActualizaArbol(relacion);
        getFactoriaServicios().getServicioSistema().reset();
        
        Vector<String> vUniques = relacion.getListaUniques();
        Vector<String> vAtributos = relacion.getListaAtributos();
        
        Vector<TransferAtributo> listaAtributos = (Vector<TransferAtributo>) ctrl.mensaje(TC.ObtenerListaAtributos, null);
        
        for (int i = 0; i < vUniques.size(); i++) {
            for (int j = 0; j < vAtributos.size(); j++)
                if (vUniques.get(i).equals(getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(j))))) {
                    if (!getFactoriaServicios().getServicioAtributos().idUnique(Integer.parseInt(vAtributos.get(j)))) {
                        int numAtributo = -1;
                        for (int k = 0; k < listaAtributos.size(); k++) {
                            String nombre = getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(j)));
                            if (((TransferAtributo) listaAtributos.get(k)).getNombre().equals(nombre)) {
                                numAtributo = k;
                            }
                        }
                        final TransferAtributo atributo = (TransferAtributo) listaAtributos.get(numAtributo);
                        TransferAtributo clon_atributo = atributo.clonar();
                        tratarContexto(getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo));
                    }
                }
        }
        
        for (int i = 0; i < vAtributos.size(); i++) {
            if (getFactoriaServicios().getServicioAtributos().idUnique(Integer.parseInt(vAtributos.get(i)))) {
                boolean encontrado = false;
                for (int j = 0; j < vUniques.size(); j++) {
                    if (vUniques.get(j).equals(getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(i))))) {
                        encontrado = true;
                    }
                }
                if (!encontrado) {
                    int numAtributo = -1;
                    for (int k = 0; k < listaAtributos.size(); k++) {
                        String nombre = getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(i)));
                        if (((TransferAtributo) listaAtributos.get(k)).getNombre().equals(nombre)) {
                            numAtributo = k;
                        }
                    }
                    final TransferAtributo atributo = (TransferAtributo) listaAtributos.get(numAtributo);
                    TransferAtributo clon_atributo = atributo.clonar();
                    tratarContexto(getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo));
                }
            }
        }
        return null;
	}

}
