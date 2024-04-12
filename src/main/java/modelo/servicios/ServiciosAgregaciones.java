package modelo.servicios;

import misc.Config;
import controlador.Contexto;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;

import java.util.Iterator;
import java.util.Vector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosAgregaciones {

    //Devuelve actualizada la lista de agregaciones
    public Vector<TransferAgregacion> ListaDeAgregaciones() throws ExceptionAp {
        // Creamos el DAO de agregaciones
        DAOAgregaciones dao = new DAOAgregaciones(Config.getPath());
        // Utilizando el DAO obtenemos la lista de agregaciones
        Vector<TransferAgregacion> lista_agregaciones = dao.ListaDeAgregaciones();
        // Se lo devolvemos al controlador
        return lista_agregaciones;
    }

    public Contexto anadirAgregacion(TransferAgregacion ta) throws ExceptionAp {
        if (ta.getNombre().isEmpty()) {
            return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreVacio);
        }
        DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> lista = daoAgregaciones.ListaDeAgregaciones();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferAgregacion elem_tr = (TransferAgregacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeYaExiste,ta);
            }
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
        for (Iterator it = listaE.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste, ta);
            }
        }

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
        for (Iterator it = listaR.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste, ta);
            }
        }

        int id = daoAgregaciones.anadirAgregacion(ta);
        if (id == -1) return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_DAO, ta);
        else {
            ta.setIdAgregacion(id);
            Vector<Object> v = new Vector<Object>();
            v.add(daoAgregaciones.consultarAgregacion(ta));
            return new Contexto(true, TC.SAG_InsertarAgregacion_HECHO, v);
        }
    }

    public boolean perteneceAgregacion(TransferRelacion rel) throws ExceptionAp {
        DAOAgregaciones daoAgre = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
        for (TransferAgregacion agre : agregaciones) {
            String idRelDeAgre = (String) agre.getListaRelaciones().get(0);
            if (idRelDeAgre.equals(Integer.toString(rel.getIdRelacion()))) {
                return true;
            }
        }
        return false;
    }

    public Contexto renombrarAgregacion(TransferRelacion tr, String nuevoNombre) throws ExceptionAp {
        TransferAgregacion ta = this.buscarAgregaciondeRelacion(tr);
        Vector<Object> v = new Vector<Object>();
        v.add(ta);
        v.add(nuevoNombre);
        v.add(ta.getNombre());

        // Si el nuevo nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
            return new Contexto(false, TC.SAG_RenombrarAgregacion_ERROR_NombreVacio, v);
        }
        // Si hay una relacion que ya tiene el "nuevoNombre" -> ERROR
        DAORelaciones dao = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaRelaciones = dao.ListaDeRelaciones();
        int i = 0;
        TransferRelacion rel;
        while (i < listaRelaciones.size()) {
            rel = listaRelaciones.get(i);
            if (rel.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste, ta);
            }
            i++;
        }
        // Si hay una entidad que ya tiene el "nuevoNombre" -> ERROR
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
		/*if (listaE == null){
			controlador.mensajeDesde_SR(TC.SR_RenombrarRelacion_ERROR_DAOEntidades,v);
			return;
		}*/
        for (Iterator it = listaE.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste, ta);
            }
        }
        // Si hay una agregacion distinta que ya tiene el "nuevoNombre" -> ERROR
        DAOAgregaciones daoAgreg = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> listaAgregaciones = daoAgreg.ListaDeAgregaciones();
        int j = 0;
        TransferAgregacion agreg;
        while (j < listaAgregaciones.size()) {
            agreg = listaAgregaciones.get(j);
            if (agreg.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase()) && agreg.getIdAgregacion() != ta.getIdAgregacion()) {
                return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_NombreDeYaExiste, ta);
            }
            j++;
        }
        // Modificamos el nombre
        ta.setNombre(nuevoNombre);
        if (daoAgreg.modificarAgregacion(ta) == false) {
            return new Contexto(false, TC.SAG_InsertarAgregacion_ERROR_DAO, v);
        } else
            return new Contexto(true, TC.SAG_RenombrarAgregacion_HECHO, v);
    }

    private TransferAgregacion buscarAgregaciondeRelacion(TransferRelacion rel) throws ExceptionAp {
        TransferAgregacion ta = new TransferAgregacion();
        DAOAgregaciones daoAgre = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
        for (TransferAgregacion agre : agregaciones) {
            if (agre.getListaRelaciones().contains(Integer.toString(rel.getIdRelacion())))
                ta = agre;
        }
        return ta;
    }

    public Contexto eliminarAgregacion(TransferRelacion tr) throws ExceptionAp {
        String idRel = Integer.toString(tr.getIdRelacion());
        DAOAgregaciones daoAgre = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
        for (TransferAgregacion agre : agregaciones) {
            Vector relacion = agre.getListaRelaciones(); // solo tiene un elemento
            if (relacion.contains(idRel)) {
                daoAgre.borrarAgregacion(agre);
                Vector<Object> v = new Vector<Object>();
                v.add(agre);
                return new Contexto(true, TC.SAG_EliminarAgregacion_HECHO, v);
            }
        }
        return new Contexto(false, null, null);
    }

    public Contexto eliminarAgregacion(TransferAgregacion ta) throws ExceptionAp {
        DAOAgregaciones daoAgre = new DAOAgregaciones(Config.getPath());
        daoAgre.borrarAgregacion(ta);
        Vector<Object> v = new Vector<Object>();
        v.add(ta);
        return new Contexto(true, TC.SAG_EliminarAgregacion_HECHO, v);
    }

    public Contexto anadirAtributo(Vector v) throws ExceptionAp {
        TransferAgregacion te = (TransferAgregacion) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        // Si nombre de atributo es vacio -> ERROR
        if (ta.getNombre().isEmpty()) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoVacio, v);
        }

        // Si nombre de atributo ya existe en esa entidad-> ERROR
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
        if (lista == null) {
            // este tipo de mensajes habra que modificarlos en el controlador para que
            // el mensaje de error que lancen sea relativo a agregaciones no a entidades
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos, v);
        }
        for (int i = 0; i < te.getListaAtributos().size(); i++)
            if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) te.getListaAtributos().get(i)))).toLowerCase().equals(ta.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoYaExiste, v);
            }

        // Si hay tamano y no es un entero positivo -> ERROR
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_TamanoEsNegativo, v);
                }
            } catch (Exception e) {
                return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_TamanoNoEsEntero, v);
            }
        }
        // Creamos el atributo
        // de momento al no representarse, esto no se utiliza ta.setPosicion(te.nextAttributePos(ta.getPosicion()));
        int idNuevoAtributo = daoAtributos.anadirAtributo(ta);
        if (idNuevoAtributo == -1) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos, v);
        }
        // Anadimos el atributo a la lista de atributos de la entidad
        ta.setIdAtributo(idNuevoAtributo);
        te.getListaAtributos().add(Integer.toString(idNuevoAtributo));

        DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
        if (!daoAgregaciones.modificarAgregacion(te)) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOEntidades, v);
        }

        // Si todo ha ido bien devolvemos al controlador la agregacion modificada y el nuevo atributo
        return new Contexto(true, TC.SAG_AnadirAtributoAAgregacion_HECHO, v);
    }
}