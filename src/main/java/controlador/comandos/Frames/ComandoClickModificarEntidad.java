package controlador.comandos.Frames;

import java.util.Objects;
import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import modelo.transfers.Transfer;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAORelaciones;
import persistencia.EntidadYAridad;

public class ComandoClickModificarEntidad extends Comando{

	public ComandoClickModificarEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector<Object> v = (Vector<Object>) datos;
        TransferEntidad te = (TransferEntidad) v.get(0);
        boolean eraDebil = te.isDebil();
        String nuevoNombre = (String) v.get(1);
        boolean debilitar = (boolean) v.get(2);
        //Si se ha modificado su nombre la renombramos
        if (!Objects.equals(te.getNombre(), nuevoNombre)) {
            Vector<Object> v1 = new Vector<Object>();
            v1.add(te);
            v1.add(nuevoNombre);
            getFactoriaServicios().getServicioEntidades().renombrarEntidad(v1);
        }
        //Si se ha debilitado añadimos la relación entre la entidad modificada y la especificada en la GUI
        if (debilitar) {
            boolean debilitada = false;
            if (!eraDebil) {
            	getFactoriaServicios().getServicioEntidades().debilitarEntidad(te);
            }
            TransferEntidad te2 = (TransferEntidad) v.get(4);
            TransferRelacion tr = (TransferRelacion) v.get(3);
            if (getFactoriaServicios().getServicioRelaciones().SePuedeAnadirRelacion(tr)) {
                Vector<Object> v2 = new Vector<Object>();
                v2.add(tr);
                v2.add(te);
                v2.add(Integer.toString(1));//Inicio
                v2.add("n");//Fin
                v2.add("");//Rol
                //INcluimos en el vector MarcadaConCardinalidad(true), MarcadaConParticipacion(false), MarcadaConMinMax(false)
                v2.add(true);
                v2.add(false);
                v2.add(false);
                //Debilitamos la entidad y añadimos a la nueva relacion
                getFactoriaServicios().getServicioRelaciones().anadirRelacion(tr, 1);//mandamos un 1, se anade la relacion por otro metodo
                getFactoriaServicios().getServicioRelaciones().anadirEntidadARelacion(v2, 1);//mandamos un 1, se anade la relacion por otro metodo
                //dudaa
                Vector<Object> v3 = new Vector<Object>();
                v3.add(tr);
                v3.add(te2);
                v3.add(Integer.toString(0));//Inicio
                v3.add("1");//Fin
                v3.add("");//Rol
                //INcluimos en el vector MarcadaConCardinalidad(true), MarcadaConParticipacion(false), MarcadaConMinMax(false)
                v3.add(true);
                v3.add(false);
                v3.add(false);
                getFactoriaServicios().getServicioRelaciones().anadirEntidadARelacion(v3, 1);//mandamos un 1, se anade la relacion por otro metodo
            } else if (!eraDebil) {
            	getFactoriaServicios().getServicioEntidades().debilitarEntidad(te);
            }
        } else if (eraDebil) {
        	getFactoriaServicios().getServicioEntidades().debilitarEntidad(te);
            DAORelaciones dao = new DAORelaciones(ctrl.getPath());
            Vector<TransferRelacion> lista_relaciones = dao.ListaDeRelaciones();
            //getFactoriaServicios().getServicioRelaciones().restablecerDebilidadRelaciones();
            for (TransferRelacion tr : lista_relaciones) {
                Vector<EntidadYAridad> eya = tr.getListaEntidadesYAridades();
                for (EntidadYAridad entidadYAridad : eya) {
                    if (entidadYAridad.getEntidad() == te.getIdEntidad() && tr.getTipo().equals("Debil"))
                        getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
                }
            }

        }
        ActualizaArbol((Transfer) v.get(0));
        getFactoriaServicios().getServicioSistema().reset();
	}

}
