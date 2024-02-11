package controlador.comandos.Atributo;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;

public class ComandoClickEditarUniqueAtributo extends Comando{

	public ComandoClickEditarUniqueAtributo(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector<Object> ve = (Vector<Object>) datos;
        TransferAtributo ta = (TransferAtributo) ve.get(0);
        Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(ta);
        tratarContexto(ctxt);

        //TODO Cabiar los getListas, no tomarlas de controller sino de modelo
        getFactoriaServicios().getServicioEntidades().ListaDeEntidades();
        getFactoriaServicios().getServicioAtributos().getListaDeAtributos();
        getFactoriaServicios().getServicioRelaciones().ListaDeRelaciones();
        //modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
        Vector<TransferRelacion> relaciones = (Vector<TransferRelacion>) ctrl.mensaje(TC.ObtenerListaRelaciones, null);
        Vector<TransferEntidad> entidades = (Vector<TransferEntidad>) ctrl.mensaje(TC.ObtenerListaEntidades, null);
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
            if (esEntidad) {
                Vector v = new Vector();
                v.add(te);
                v.add(ta);
                getFactoriaServicios().getServicioEntidades().setUniqueUnitario(v);
            } else {//esRelacion
                Vector v = new Vector();
                v.add(tr);
                v.add(ta);
                getFactoriaServicios().getServicioRelaciones().setUniqueUnitario(v);
            }
        }
	}

}