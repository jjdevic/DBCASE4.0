package controlador.comandos.Atributo;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;

public class ComandoModificarUniqueAtrib extends Comando{

	public ComandoModificarUniqueAtrib(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		Contexto resultado = null;
		Vector v1 = (Vector) datos;
        TransferAtributo ta = (TransferAtributo) v1.get(0);
        String antiguoNombre = (String) v1.get(1);

        Contexto ctxt = getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(ta);
        tratarContexto(ctxt);

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
            Vector v = new Vector();
            if (esEntidad) {
                v.add(te);
                v.add(ta);
                v.add(antiguoNombre);
                resultado = getFactoriaServicios().getServicioEntidades().renombraUnique(v);
            } else {//esRelacion
                v.add(tr);
                v.add(ta);
                v.add(antiguoNombre);
                resultado = getFactoriaServicios().getServicioRelaciones().renombraUnique(v);
            }
        }
        return resultado;
	}

}
