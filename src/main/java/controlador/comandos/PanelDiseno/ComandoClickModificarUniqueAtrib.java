package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Controlador;
import controlador.comandos.Comando;
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

        ctrl.getTheServiciosAtributos().editarUniqueAtributo(ta);

        ctrl.getTheServiciosEntidades().ListaDeEntidades();
        ctrl.getTheServiciosAtributos().getListaDeAtributos();
        ctrl.getTheServiciosRelaciones().ListaDeRelaciones();
        //modificar la tabla de Uniques de la entidad o la relacion a la que pertenece
        Vector<TransferRelacion> relaciones = ctrl.getTheGUIPrincipal().getListaRelaciones();
        Vector<TransferEntidad> entidades = ctrl.getTheGUIPrincipal().getListaEntidades();
        boolean encontrado = false;
        boolean esEntidad = false;
        TransferEntidad te = null;
        TransferRelacion tr = null;
        int i = 0;
        while (i < entidades.size() && !encontrado) {
            te = entidades.get(i);
            if (ctrl.getTheServiciosEntidades().tieneAtributo(te, ta)) {
                encontrado = true;
                esEntidad = true;
            }
            i++;
        }
        i = 0;
        while (i < relaciones.size() && !encontrado) {
            tr = relaciones.get(i);
            if (ctrl.getTheServiciosRelaciones().tieneAtributo(tr, ta)) {
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
                ctrl.getTheServiciosEntidades().renombraUnique(v);
            } else {//esRelacion
                v.add(tr);
                v.add(ta);
                v.add(antiguoNombre);
                ctrl.getTheServiciosRelaciones().renombraUnique(v);
            }
        }
	}

}
