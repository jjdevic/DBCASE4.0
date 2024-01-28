package controlador.comandos.Relacion;

import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import modelo.transfers.Transfer;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferRelacion;

public class ComandoAnadirUniquesRelacion extends Comando {

	public ComandoAnadirUniquesRelacion(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector v = (Vector<Transfer>) datos;
        getFactoriaServicios().getServicioRelaciones().setUniques(v);
        TransferRelacion relacion = (TransferRelacion) v.get(1);
        ActualizaArbol(relacion);
        getFactoriaServicios().getServicioSistema().reset();
        
        Vector<String> vUniques = relacion.getListaUniques();
        Vector<String> vAtributos = relacion.getListaAtributos();
        
        Vector<TransferAtributo> listaAtributos = ctrl.getListaAtributos();
        
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
                        getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo);
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
                    getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo);
                }
            }
        }
	}

}
