package controlador.comandos.Entidad;

import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.Transfer;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;

public class ComandoAnadirUniquesEntidad extends Comando {

	public ComandoAnadirUniquesEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector v = (Vector<Transfer>) datos;
        getFactoriaServicios().getServicioEntidades().setUniques(v);
        TransferEntidad entidad = (TransferEntidad) v.get(1);
        ActualizaArbol(entidad);
        getFactoriaServicios().getServicioSistema().reset();
        
        Vector<String> vUniques = entidad.getListaUniques();
        Vector<String> vAtributos = entidad.getListaAtributos();
        
        //Recorrer cada unique de la entidad seleccionada, buscando casos en los que estén marcados en la lista vUniques
        //mientras que según el controlador no figuren como uniques en la aplicación (este sería el caso en el que
        //se ha seleccionado un nuevo unique)
        Vector<TransferAtributo> listaAtributos = (Vector<TransferAtributo>) ctrl.mensaje(TC.ObtenerListaAtributos, null);
        
        for (int i = 0; i < vUniques.size(); i++) {
            for (int j = 0; j < vAtributos.size(); j++) 
            	
            	//Si el unique numero i es igual al nombre del atributo j
                if (vUniques.get(i).equals(getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(j))))) {
                    //Si el atributo j no es unique
                	if (!getFactoriaServicios().getServicioAtributos().idUnique(Integer.parseInt(vAtributos.get(j)))) {
                        int numAtributo = -1;
                        //Buscar la posicion del atributo j en la lista de atributos de la GUIPrincipal
                        for (int k = 0; k < listaAtributos.size(); k++) {
                        	
                            String nombre = getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(j)));
                            if (((TransferAtributo) listaAtributos.get(k)).getNombre().equals(nombre)) {
                                numAtributo = k;
                            }
                        }
                        //Hacer el atributo j unique
                        final TransferAtributo atributo = (TransferAtributo) listaAtributos.get(numAtributo);
                        TransferAtributo clon_atributo = atributo.clonar();
                        getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo);
                    }
                }
        }
        
        //Recorrer cada atributo de la entidad seleccionada, buscando casos en los que un atributo es unique pero no está en vUniques.
        //Este sería el caso en el que se elimina un unique que estaba previamente en la lista
        for (int i = 0; i < vAtributos.size(); i++) {
        	//Si el atributo i es unique
            if (getFactoriaServicios().getServicioAtributos().idUnique(Integer.parseInt(vAtributos.get(i)))) {
                boolean encontrado = false;
                //Comprobar que el atributo i está marcado como tal en vUniques
                for (int j = 0; j < vUniques.size(); j++) {
                    if (vUniques.get(j).equals(getFactoriaServicios().getServicioAtributos().getNombreAtributo(Integer.parseInt(vAtributos.get(i))))) {
                        encontrado = true;
                    }
                }
                //Si no está marcado como unique en vUniques, desmarcarlo.
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
                    //EditarUniqueAtributo hace unique = !unique
                    getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(clon_atributo);
                }
            }
        }
	}

}
