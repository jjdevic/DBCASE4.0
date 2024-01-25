package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;

public class ComandoClickModificarUniqueAtrib extends Comando{

	public ComandoClickModificarUniqueAtrib(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector v1 = (Vector) datos;
        TransferAtributo ta = (TransferAtributo) v1.get(0);
        String antiguoNombre = (String) v1.get(1);

        Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(ta);
        tratarContexto(ctxt);

        //TODO modelo
        getFactoriaServicios().getServicioEntidades().ListaDeEntidades();
        getFactoriaServicios().getServicioAtributos().getListaDeAtributos();
        getFactoriaServicios().getServicioRelaciones().ListaDeRelaciones();
        //modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
        Vector<TransferRelacion> relaciones = ctrl.getFactoriaGUI().getGUIPrincipal().getListaRelaciones();
        Vector<TransferEntidad> entidades = ctrl.getFactoriaGUI().getGUIPrincipal().getListaEntidades();
        boolean encontrado = false;
        boolean esEntidad = false;
        TransferEntidad te = null;
        TransferRelacion tr = null;
        int i = 0;
        while (i < entidades.size() && !encontrado) {
            te = entidades.get(i);
            if (getFactoriaServicios().getServicioEntidades().tieneAtributo(te, ta)) {
                encontrado = true;
                esEntidad = true;
            }
            i++;
        }
        i = 0;
        while (i < relaciones.size() && !encontrado) {
            tr = relaciones.get(i);
            if (getFactoriaServicios().getServicioRelaciones().tieneAtributo(tr, ta)) {
                encontrado = true;
            }
            i++;
        }
        if (encontrado) {
            Vector v = new Vector();
            if (esEntidad) {
                v.add(te);
                v.add(ta);
                v.add(antiguoNombre);
                getFactoriaServicios().getServicioEntidades().renombraUnique(v);
            } else {//esRelacion
                v.add(tr);
                v.add(ta);
                v.add(antiguoNombre);
                getFactoriaServicios().getServicioRelaciones().renombraUnique(v);
            }
        }
	}

}
