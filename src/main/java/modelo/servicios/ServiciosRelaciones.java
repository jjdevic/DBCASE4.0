package modelo.servicios;

import misc.Config;
import controlador.Contexto;
import controlador.TC;
import modelo.transfers.*;
import persistencia.*;

import java.util.Iterator;
import java.util.Vector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosRelaciones {

    //Devuelve actualizada la lista de relaciones
    public Vector<TransferRelacion> ListaDeRelacionesNoVoid() {
        // Creamos el DAO de relaciones
        DAORelaciones dao = new DAORelaciones(Config.getPath());
        // Utilizando el DAO obtenemos la lista de Relaciones
        Vector<TransferRelacion> lista_relaciones = dao.ListaDeRelaciones();
        return lista_relaciones;
    }

    /* Anadir Relacion
     * Parametros: un TransferRelacion que contiene el nombre de la nueva Relacion y la posicion donde debe ir dibujada.
     * Devuelve: La Relacion en un TransferRelacion y el mensaje -> SR_InsertarRelacion_HECHO
     * Condiciones:
     * Si el nombre es vacio -> SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio
     * Si el nombre ya existe -> SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste
     * Si al usar el DAORelaciones se produce un error -> SR_InsertarRelacion_ERROR_DAO
     */

    //hay que controlar que no exista el nombre de una agregacion
    public Contexto anadirRelacion(TransferRelacion tr, int deOtro) {
        if (tr.getNombre().isEmpty()) {
            return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio, null);
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(tr.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste, tr);
            }
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
        for (Iterator it = listaE.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(tr.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad, tr);
            }
        }
        DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> listaA = daoAgregaciones.ListaDeAgregaciones();
        for (Iterator it = listaA.iterator(); it.hasNext(); ) {
            TransferAgregacion elem_te = (TransferAgregacion) it.next();
            if (elem_te.getNombre().toLowerCase().equals(tr.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoAgregacion, tr);
            }
        }

        int id = daoRelaciones.anadirRelacion(tr);
        if (id == -1) return new Contexto(false, TC.SR_InsertarRelacion_ERROR_DAORelaciones, tr);
        else {
            tr.setIdRelacion(id);
            Vector<Object> v = new Vector<Object>();
            v.add(daoRelaciones.consultarRelacion(tr));
            //v.add(deOtro);
            return new Contexto(true, TC.SR_InsertarRelacion_HECHO, v);
        }
    }

    /* Se puede Anadir Relacion
     * Parametros: un TransferRelacion que contiene el nombre de la nueva Relacion y la posicion donde debe ir dibujada.
     * Devuelve: La Relacion en un TransferRelacion y el mensaje -> SR_InsertarRelacion_HECHO
     * Condiciones:
     * Si el nombre es vacio -> SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio
     * Si el nombre ya existe -> SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste
     * Si al usar el DAORelaciones se produce un error -> SR_InsertarRelacion_ERROR_DAO
     */
    public Contexto SePuedeAnadirRelacion(TransferRelacion tr) {
        if (tr.getNombre().isEmpty()) {
        	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionEsVacio, null);
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(tr.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionYaExiste, tr);
            }
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
        for (Iterator it = listaE.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(tr.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad, tr);
            }
        }
        return new Contexto(true, null);
    }

    /*
     * Anadir una relacion IsA
     */
    public Contexto anadirRelacionIsA(TransferRelacion tr) {
        tr.setNombre("IsA");
        tr.setTipo("IsA");
        tr.setListaAtributos(new Vector());
        tr.setListaEntidadesYAridades(new Vector());
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        int id = daoRelaciones.anadirRelacion(tr);
        if (id == -1) return new Contexto(false, TC.SR_InsertarRelacionIsA_ERROR_DAORelaciones, tr);
        else {
            tr.setIdRelacion(id);
            Vector<Object> v = new Vector<Object>();
            v.add(tr);
            return new Contexto(true, TC.SR_InsertarRelacionIsA_HECHO, v);
        }
    }


    /*
     *  Eliminar una relación IsA
     */
    public Contexto eliminarRelacionIsA(TransferRelacion tr) {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.borrarRelacion(tr))
        	return new Contexto(false, TC.SR_EliminarRelacionIsA_ERROR_DAORelaciones, tr);
        else {
        	Vector<Object> v = new Vector<Object>();
            v.add(tr);
        	return new Contexto(true, TC.SR_EliminarRelacionIsA_HECHO, v);
        }
    }

    /*
     *  Eliminar una relación Normal
     */

    public Contexto eliminarRelacionNormal(TransferRelacion tr, int vieneDeEntidadDebil) {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<Object> v = new Vector<Object>();
        if (!daoRelaciones.borrarRelacion(tr))
        	return new Contexto(false, TC.SR_EliminarRelacionNormal_ERROR_DAORelaciones, tr);
        else {
            v.add(tr);
            if (v.size() == 1) v.add(vieneDeEntidadDebil);
            else v.set(1, vieneDeEntidadDebil);
            return new Contexto(true, TC.SR_EliminarRelacionNormal_HECHO, v);
        }
    }

    public Contexto renombrarRelacion(TransferRelacion tr, String nuevoNombre) {
        Vector<Object> v = new Vector<Object>();
        v.add(tr);
        v.add(nuevoNombre);
        v.add(tr.getNombre());

        // Si el nuevo nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_NombreDeRelacionEsVacio, v);
        }
        // Si el nuevo nombre es IsA (o variantes) -> ERROR
        if (nuevoNombre.toLowerCase().equals("isa")) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_NombreIsA, v);
        }
        // Si hay una relacion que ya tiene el "nuevoNombre" -> ERROR
        DAORelaciones dao = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaRelaciones = dao.ListaDeRelaciones();
        int i = 0;
        TransferRelacion rel;
        while (i < listaRelaciones.size()) {
            rel = listaRelaciones.get(i);
            if (rel.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase()) && rel.getIdRelacion() != tr.getIdRelacion()) {
            	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExiste, v);
            }
            i++;
        }

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
		/*if (listaE == null){
			controlador.mensajeDesde_SR(TC.SR_RenombrarRelacion_ERROR_DAOEntidades,v);
			return;
		}*/
        for (Iterator it = listaE.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())) {
            	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_NombreDeRelacionYaExisteComoEntidad, tr);
            }
        }

        // Modificamos el nombre
        tr.setNombre(nuevoNombre);
        if (dao.modificarRelacion(tr) == false) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        } else
        	return new Contexto(true, TC.SR_RenombrarRelacion_HECHO, v);
    }


    /*
     * Debilitar relacion
     * -> hay que invertir el valor de "debil"
     */
    public Contexto debilitarRelacion(TransferRelacion tr) {
        String tipoViejo = tr.getTipo();
        if (tipoViejo.equals("Debil")) tr.setTipo("Normal");
        else tr.setTipo("Debil");

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (daoRelaciones.modificarRelacion(tr) == false) {
            tr.setTipo(tipoViejo);
            return new Contexto(false, TC.SR_DebilitarRelacion_ERROR_DAORelaciones, tr);
        } else {
        	Vector<Object> v = new Vector<Object>();
            v.add(tr);
        	return new Contexto(true, TC.SR_DebilitarRelacion_HECHO, tr);
        }
    }

    public void restablecerDebilidadRelaciones() {
        DAORelaciones dao = new DAORelaciones(Config.getPath());
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferRelacion> lista_relaciones = dao.ListaDeRelaciones();
        Vector<TransferEntidad> lista_entidades = daoEntidades.ListaDeEntidades();
        //Comprobamos si tiene alguna entidad debil

        for (int i = 0; i < lista_relaciones.size(); ++i) {
            boolean tieneEntidadesDebiles = false;
            TransferRelacion tr = lista_relaciones.get(i);
            if (tr.getTipo().equals("Debil")) {
                Vector<EntidadYAridad> lista_entidadesYAridades = tr.getListaEntidadesYAridades();
                for (int j = 0; j < lista_entidadesYAridades.size(); ++j) {
                    int ent = lista_entidadesYAridades.get(j).getEntidad();
                    if (lista_entidades.get(lista_entidadesYAridades.get(j).getEntidad()).isDebil()) {
                        tieneEntidadesDebiles = true;
                    }
                }
            }
            if (!tieneEntidadesDebiles) {
                this.debilitarRelacion(tr);
            }
        }
    }

    /*
     *Devuelve cierto si la relación es débil y falso en caso contrario
     */
    public boolean esDebil(TransferEntidad te) {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaRelaciones = daoRelaciones.ListaDeRelaciones();
        
        for (int i = 0; i < listaRelaciones.size(); i++) {//para cada relación en el sistema
            TransferRelacion tr = listaRelaciones.get(i);
            if (tr.getTipo().equals("Debil")) {//si esta relación es débil
                return true;
            }
        }
        return false;
    }

    public boolean tieneHermanoDebil(TransferEntidad te) {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaRelaciones = daoRelaciones.ListaDeRelaciones();
        ServiciosEntidades sEntidades = new ServiciosEntidades();
        
        for (int i = 0; i < listaRelaciones.size(); i++) {//para cada relación en el sistema
            TransferRelacion tr = listaRelaciones.get(i);
            if (tr.getTipo().equals("Debil")) {//si esta relación es débil
                Vector<EntidadYAridad> vectorEntidadesAridades = tr.getListaEntidadesYAridades();
                boolean tieneALaEntidad = false;
                boolean tieneAlgunaDebil = false;
                for (int j = 0; j < vectorEntidadesAridades.size(); j++) {
                    int entidad = vectorEntidadesAridades.get(j).getEntidad();
                    if (te.getIdEntidad() == entidad) {
                        tieneALaEntidad = true;
                    }
                    if (sEntidades.esDebil(entidad)) {
                        tieneAlgunaDebil = true;
                    }
                }
                if (tieneALaEntidad && tieneAlgunaDebil)
                    return true;
            }
        }
        return false;
    }

    public int numEntidadesDebiles(TransferRelacion tr) {
        Vector<EntidadYAridad> vectorEntidadesAridades = tr.getListaEntidadesYAridades();
        int numero = 0;
        ServiciosEntidades sEntidades = new ServiciosEntidades();
        
        for (int j = 0; j < vectorEntidadesAridades.size(); j++) {
            int entidad = vectorEntidadesAridades.get(j).getEntidad();
            if (sEntidades.esDebil(entidad))
                numero++;
        }
        return numero;
    }

    public int idEntidadDebil(TransferRelacion tr) {
        Vector<EntidadYAridad> vectorEntidadesAridades = tr.getListaEntidadesYAridades();
        int entidad = 0;
        ServiciosEntidades sEntidades = new ServiciosEntidades();
        
        boolean encontrado = false;
        int j = 0;
        while ((j < vectorEntidadesAridades.size()) && (!encontrado)) {
            entidad = vectorEntidadesAridades.get(j).getEntidad();
            if (sEntidades.esDebil(entidad))
                encontrado = true;
            else
                j++;
        }
        return entidad;
    }

    public void getSubesquema(TransferRelacion tr, Vector rel) {
        if (!rel.contains(tr.getIdRelacion())) {//si  est� en el vector que llevamos hasta ahora aqui se acaba esta rama
            rel.add(tr.getIdRelacion()); //si no, se a�ade y exploramos las entidades que tiene para ver sus relaciones
            Vector<EntidadYAridad> entidadesRelacionadas = tr.getListaEntidadesYAridades();
            ServiciosEntidades serEn = new ServiciosEntidades();
            
            Vector<TransferEntidad> entidades = serEn.ListaDeEntidadesNOVoid();
            Vector<TransferRelacion> relaciones = this.ListaDeRelacionesNoVoid();
            for (int i = 0; i < entidadesRelacionadas.size(); ++i) {//recorremos entidades que participan en la relacion que estamos estudiando
                for (int j = 0; j < entidades.size(); ++j) {//para cada una comparamos con las entidades del DAO para coger su transfer
                    if (entidades.get(j).getIdEntidad() == entidadesRelacionadas.get(i).getEntidad()) {
                        Vector<String> relacionesDeEntidad = entidades.get(j).getListaRelaciones();//sacamos las relaciones en que participa
                        for (int k = 0; k < relacionesDeEntidad.size(); ++k) {//las recorremos
                            for (int h = 0; h < relaciones.size(); ++h) {//comparamos conlas relaciones del DAO para coger su transfer
                                if (Integer.valueOf(relacionesDeEntidad.get(k)) == relaciones.get(h).getIdRelacion() && relaciones.get(h).getIdRelacion() != tr.getIdRelacion()) { //Si la relacion no es la estudiada
                                    getSubesquema(relaciones.get(h), rel);//para cada relacion en que participen las entidades de la relacion que estamos estudiando repetimos el proceso recursivamente
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Anadir un atributo a una relacion
     * -> en v viene la relacion (pos 0) y el atributo (pos 1)
     */
    public Contexto anadirAtributo(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        // Si nombre de atributo es vacio -> ERROR
        if (ta.getNombre().isEmpty()) {
        	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoVacio, v);
        }
        // Si nombre de atributo ya existe en esa entidad-> ERROR
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
        if (lista == null) {
        	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_DAOAtributos, v);
        }
        for (int i = 0; i < tr.getListaAtributos().size(); i++)
            if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) tr.getListaAtributos().get(i)))).toLowerCase().equals(ta.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_NombreDeAtributoYaExiste, v);
            }

        // Si hay tamano y no es un entero positivo -> ERROR
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_TamanoEsNegativo, v);
                }
            } catch (Exception e) {
            	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_TamanoNoEsEntero, v);
            }
        }
        // Creamos el atributo
        ta.setPosicion(tr.nextAttributePos(ta.getPosicion()));
        //DAOAtributos daoAtributos = new DAOAtributos(this.controlador);
        int idNuevoAtributo = daoAtributos.anadirAtributo(ta);
        if (idNuevoAtributo == -1) {
        	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_DAOAtributos, v);
        }
        // Anadimos el atributo a la lista de atributos de la relacion
        ta.setIdAtributo(idNuevoAtributo);
        tr.getListaAtributos().add(Integer.toString(idNuevoAtributo));
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr)) {
        	return new Contexto(false, TC.SR_AnadirAtributoARelacion_ERROR_DAORelaciones, v);
        }
        // Si todo ha ido bien devolvemos la relacion modificada y el nuevo atributo
        return new Contexto(true, TC.SR_AnadirAtributoARelacion_HECHO, v);
    }


    public Contexto anadirRestriccion(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        String restriccion = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones , v);
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }

        Vector<String> vRestricciones = tr.getListaRestricciones();
        vRestricciones.add(restriccion);
        tr.setListaRestricciones(vRestricciones);

        //te.setNombre(nuevoNombre);
        if (daoRelaciones.modificarRelacion(tr)) {
        	return new Contexto(true, TC.SR_AnadirRestriccionARelacion_HECHO, v);
        } else return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
    }

    public Contexto quitarRestriccion(Vector v) {
        TransferRelacion te = (TransferRelacion) v.get(0);
        String restriccion = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) return new Contexto(false, null);
        
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }

        Vector<String> vRestricciones = te.getListaRestricciones();
        int i = 0;
        boolean encontrado = false;
        while (i < vRestricciones.size() && !encontrado) {
            if (vRestricciones.get(i).equals(restriccion)) {
                vRestricciones.remove(i);
                encontrado = true;
            }
            i++;
        }
        te.setListaRestricciones(vRestricciones);

        if (daoRelaciones.modificarRelacion(te)) {
        	return new Contexto(true, TC.SR_QuitarRestriccionARelacion_HECHO, v);
        }
        else return new Contexto(false, null);
    }

    public Contexto setRestricciones(Vector v) {
        Vector restricciones = (Vector) v.get(0);
        TransferRelacion tr = (TransferRelacion) v.get(1);

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
        tr.setListaRestricciones(restricciones);
        if (daoRelaciones.modificarRelacion(tr))
        	return new Contexto(true, TC.SR_setRestriccionesARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
    }

    public Contexto anadirUnique(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        String unique = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (unique.isEmpty()) {
            //controlador.mensajeDesde_SE(TC.SE_RenombrarEntidad_ERROR_NombreDeEntidadEsVacio, v);
            return new Contexto(false, null);
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }

        Vector<String> vUniques = tr.getListaUniques();
        vUniques.add(unique);
        tr.setListaUniques(vUniques);

        //te.setNombre(nuevoNombre);
        if (daoRelaciones.modificarRelacion(tr))
            return new Contexto(true, TC.SR_AnadirUniqueARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }
    }

    public Contexto quitarUnique(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        String unique = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (unique.isEmpty()) {
            return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
        
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
            return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }

        Vector<String> vUniques = tr.getListaUniques();
        int i = 0;
        boolean encontrado = false;
        while (i < vUniques.size() && !encontrado) {
            if (vUniques.get(i).equals(unique)) {
                vUniques.remove(i);
                encontrado = true;
            }
            i++;
        }
        tr.setListaUniques(vUniques);

        if (daoRelaciones.modificarRelacion(tr) == false) {
            return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        } else {
        	return new Contexto(true, TC.SR_QuitarUniqueARelacion_HECHO, v);
        }
    }

    public Contexto setUniques(Vector v) {
        Vector uniques = (Vector) v.get(0);
        TransferRelacion tr = (TransferRelacion) v.get(1);

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }
        tr.setListaUniques(uniques);
        if (daoRelaciones.modificarRelacion(tr))
        	return new Contexto(true, TC.SR_setUniquesARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }
    }

    /*
     * Quitar/poner un Unique unitario a la entidad
     * */
    public Contexto setUniqueUnitario(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        Vector uniques = tr.getListaUniques();
        Vector uniquesCopia = new Vector();
        ;
        boolean encontrado = false;
        int i = 0;
        while (i < uniques.size()) {
            if ((uniques.get(i)).equals(ta.getNombre())) encontrado = true;
            else uniquesCopia.add(uniques.get(i));
            i++;
        }
        if (!encontrado) uniquesCopia.add(ta.getNombre());

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }
        tr.setListaUniques(uniquesCopia);
        if (daoRelaciones.modificarRelacion(tr))
        	return new Contexto(true, TC.SR_setUniqueUnitarioARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAOEntidades, v);
        }
    }

    public Contexto eliminarReferenciasUnitario(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        Vector uniques = tr.getListaUniques();
        Vector uniquesCopia = new Vector();
        int i = 0;
        while (i < uniques.size()) {
            if (uniques.get(i).toString().contains(ta.getNombre())) {
                String s = uniques.get(i).toString();
                String text = ta.getNombre();
                int pos = s.indexOf(text);
                String s1 = s.substring(0, pos);
                String s2;
                if (s.indexOf(",", pos) > 0) {
                    s2 = s.substring(s.indexOf(",", pos) + 1, s.length());
                    s = s1 + s2;
                } else {
                    s2 = "";
                    if (s1.lastIndexOf(",") > 0)
                        s1 = s1.substring(0, s.lastIndexOf(","));
                    s = s1 + s2;
                }
                s = s.replaceAll(" ", "");
                s = s.replaceAll(",", ", ");
                uniquesCopia.add(s);
            } else uniquesCopia.add(uniques.get(i));
            i++;
        }

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
            return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
        tr.setListaUniques(uniquesCopia);
        if (daoRelaciones.modificarRelacion(tr))
            return new Contexto(true, TC.SR_setUniqueUnitarioARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v); 
        }
    }

    public Contexto renombraUnique(Vector v) {
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        String antiguoNombre = (String) v.get(2);
        Vector uniques = tr.getListaUniques();
        Vector uniquesCopia = new Vector();
        int i = 0;
        while (i < uniques.size()) {
            if (((TransferAtributo) uniques.get(i)).getNombre().contains(antiguoNombre)) {
                String s = uniques.get(i).toString();
                s = s.replaceAll(antiguoNombre, ta.getNombre());
                uniquesCopia.add(s);
            } else uniquesCopia.add(uniques.get(i));
            i++;
        }

        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> lista = daoRelaciones.ListaDeRelaciones();
        if (lista == null) {
            return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
        tr.setListaUniques(uniquesCopia);
        if (daoRelaciones.modificarRelacion(tr))
            return new Contexto(true, TC.SR_setUniqueUnitarioARelacion_HECHO, v);
        else {
        	return new Contexto(false, TC.SR_RenombrarRelacion_ERROR_DAORelaciones, v);
        }
    }

    /*
     * Mover una relacion (cambiar su posicion)
     */
    public Contexto moverPosicionRelacion(TransferRelacion tr) {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (daoRelaciones.modificarRelacion(tr) == false)
            return new Contexto(false, TC.SR_MoverPosicionRelacion_ERROR_DAORelaciones, tr);
        else {
        	Vector<Object> v = new Vector<Object>();
            v.add(tr);
        	return new Contexto(true, TC.SR_MoverPosicionRelacion_HECHO, v);
        }
    }


    /*
     * Establecer la entidad padre en una relacion IsA
     */
    public Contexto establecerEntidadPadreEnRelacionIsA(Vector<Transfer> datos) {
        TransferRelacion tr = (TransferRelacion) datos.get(0);
        TransferEntidad te = (TransferEntidad) datos.get(1);
        // Ponemos en la primera posicion de la lista de entidades la entidad padre
        Vector<EntidadYAridad> listaEntidades = tr.getListaEntidadesYAridades();
        tr.setRol("Padre");
        EntidadYAridad eya = new EntidadYAridad();
        eya.setEntidad(te.getIdEntidad());
        listaEntidades.add(0, eya);
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_EstablecerEntidadPadre_ERROR_DAORelaciones, datos);
        else return new Contexto(true, TC.SR_EstablecerEntidadPadre_HECHO, datos);
    }

    /*
     * Quitar la entidad padre en una relacion IsA
     * - Se quita la entidad padre y todas las hijas si las tiene
     */
    public Contexto quitarEntidadPadreEnRelacionIsA(TransferRelacion tr) {
        tr.getListaEntidadesYAridades().removeAllElements();
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_QuitarEntidadPadre_ERROR_DAORelaciones, tr);
        else {
        	Vector<Object> v = new Vector<Object>();
            v.add(tr);
        	return new Contexto(true, TC.SR_QuitarEntidadPadre_HECHO, v);
        }
    }

    /*
     * Anadir una entidad hija a una relacion IsA
     */
    public Contexto anadirEntidadHijaEnRelacionIsA(Vector<Transfer> datos) {
        TransferRelacion tr = (TransferRelacion) datos.get(0);
        TransferEntidad te = (TransferEntidad) datos.get(1);
        // Anadimos la entidad hija a la lista de entidades la entidad padre
        Vector<EntidadYAridad> listaEntidades = tr.getListaEntidadesYAridades();
        EntidadYAridad eya = new EntidadYAridad();
        eya.setEntidad(te.getIdEntidad());
        listaEntidades.add(eya);
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_AnadirEntidadHija_ERROR_DAORelaciones, datos);
        else return new Contexto(true, TC.SR_AnadirEntidadHija_HECHO, datos);
    }


    /*
     * Quitar una entidad hija en una relacion IsA
     */
    public Contexto quitarEntidadHijaEnRelacionIsA(Vector<Transfer> datos) {
        TransferRelacion tr = (TransferRelacion) datos.get(0);
        TransferEntidad te = (TransferEntidad) datos.get(1);
        // Quitamos la entidad hija de la lista de entidadesYaridades de la relacion
        Vector<EntidadYAridad> listaEntidades = tr.getListaEntidadesYAridades();
        int cont = 0;
        boolean salir = false;
        while (cont < listaEntidades.size() && !salir) {
            EntidadYAridad eya = listaEntidades.get(cont);
            if (eya.getEntidad() == te.getIdEntidad()) {
                listaEntidades.remove(cont);
                salir = true;
            }
            cont++;
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_QuitarEntidadHija_ERROR_DAORelaciones, datos);
        else return new Contexto(true, TC.SR_QuitarEntidadHija_HECHO, datos);
    }

    /*
     * Anadir una entidad a una relacion
     */
    public Contexto anadirEntidadARelacion(Vector v, int deOtro) {
        // Sacamos los componentes del vector que sabemos que son correctos
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        boolean cardinalidadSeleccionada = (boolean) v.get(5);
        boolean participacionSeleccionada = (boolean) v.get(6);
        boolean minMaxSeleccionado = (boolean) v.get(7);
        boolean cardinalidadMax1Seleccionada;
        if (v.size() == 8) {
            if (!te.isDebil()) cardinalidadMax1Seleccionada = true;
            else cardinalidadMax1Seleccionada = false;
        } else cardinalidadMax1Seleccionada = (boolean) v.get(8);
        //if(v.size()==8)cardinalidadMax1Seleccionada=true;
        //else cardinalidadMax1Seleccionada=(boolean)v.get(8);

        String aux = (String) v.get(4);
        tr.setRol(aux);
        int idEntidad = te.getIdEntidad();
        //Comprobacion de que el rol que se va a asignar no está ya en esa relación
        for (Iterator it = tr.getListaEntidadesYAridades().iterator(); it.hasNext(); ) {
            EntidadYAridad elem_tr = (EntidadYAridad) it.next();
            if (idEntidad == elem_tr.getEntidad()) {
                if (elem_tr.getRol().toLowerCase().equals(tr.getRol().toLowerCase())) {
                    if (elem_tr.getRol().equals(""))
                    	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRolNecesario, tr);
                    else return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDelRolYaExiste, tr);
                }
            }
        }


        // Obtenemos el inicio de rango. Si no es entero positivo o n -> ERROR y salimos
        String inicioEnCadena = (String) v.get(2);
        int inicioEnInt;
        //Obtenemos el final de rango. Si no es entero positivo o n -> ERROR y salimos
        String finalEnCadena = (String) v.get(3);
        int finalEnInt;
        //Si las cardinalidades son las dos vacías entonces se sobreentiende cardinalidad n a n
        if ((inicioEnCadena.equals("")) && (finalEnCadena.equals(""))) {
            v.set(2, "n");
            inicioEnCadena = "n";
            v.set(3, "n");
            finalEnCadena = "n";
        }
        //Si no son ambas vacias, se comprueba que sean válidas
        if (inicioEnCadena.equals("n")) inicioEnInt = Integer.MAX_VALUE;
        else {
            try {
                inicioEnInt = Integer.parseInt(inicioEnCadena);
            } catch (Exception e) {
            	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_InicioNoEsEnteroOn, v);
            }
        }
        if (inicioEnInt < 0) {
        	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_InicioEsNegativo, v);
        }

        if (finalEnCadena.equals("n")) finalEnInt = Integer.MAX_VALUE;
        else {
            try {
                finalEnInt = Integer.parseInt(finalEnCadena);
            } catch (Exception e) {
            	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_FinalNoEsEnteroOn, v);
            }
        }
        if (finalEnInt < 0) {
        	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_FinalEsNegativo, v);
        }
        // Aqui ya sabemos que los valores (individualmete) son correctos
        if (inicioEnInt > finalEnInt) {
        	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_InicioMayorQueFinal, v);
        }
        // Aqui ya sabemos que los valores (conjuntamente) son correctos

        if (cardinalidadMax1Seleccionada) finalEnInt = 1;
        Vector veya = tr.getListaEntidadesYAridades();
        EntidadYAridad eya = new EntidadYAridad();
        eya.setEntidad(idEntidad);
        eya.setPrincipioRango(inicioEnInt);
        eya.setFinalRango(finalEnInt);
        eya.setRol(tr.getRol());
        eya.setTineEtiqueta(minMaxSeleccionado);
        eya.setMarcadaConCardinalidad(cardinalidadSeleccionada);
        eya.setMarcadaConMinMax(minMaxSeleccionado);
        eya.setMarcadaConParticipacion(participacionSeleccionada);
        eya.setMarcadaConCardinalidadMax1(cardinalidadMax1Seleccionada);


        // Lo anadimos a la lista de entidades y aridades de la relacion y lo persistimos
        veya.add(eya);
        //v.add(veya);
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_AnadirEntidadARelacion_ERROR_DAORelaciones, v);
        else {
            //v.add(deOtro);
        	return new Contexto(true, TC.SR_AnadirEntidadARelacion_HECHO, v);
            //v.add("1");
        }
    }

    /*
     * Editar la aridad de una entidad en una relacion
     */
    public Contexto editarAridadEntidad(Vector<Object> v) {
        // Sacamos los componentes del vector que sabemos que son correctos
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        boolean cardinalidadSeleccionada = (boolean) v.get(6);
        boolean participacionSeleccionada = (boolean) v.get(7);
        boolean minMaxSeleccionado = (boolean) v.get(8);
        boolean cardinalidadMax1Seleccionada = (boolean) v.get(9);

        int idEntidad = te.getIdEntidad();
        String rol = (String) v.get(4);
        String rolViejo = (String) v.get(5);
        if (!rol.equals(rolViejo)) {//Si he modificado el rol entonces compruebo que siga siendo válido
            //Comprobacion de que el rol que se va a asignar no está ya en esa relación
            for (Iterator it = tr.getListaEntidadesYAridades().iterator(); it.hasNext(); ) {
                EntidadYAridad elem_tr = (EntidadYAridad) it.next();
                if (idEntidad == elem_tr.getEntidad()) {
                    if (elem_tr.getRol().toLowerCase().equals(rol.toLowerCase())) {
                        if (elem_tr.getRol().equals(""))
                        	return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDeRolNecesario, tr);
                        else return new Contexto(false, TC.SR_InsertarRelacion_ERROR_NombreDelRolYaExiste, tr);
                    }
                }
            }
        }

        // Obtenemos el inicio de rango. Si no es entero positivo o n -> ERROR y salimos
        String inicioEnCadena = (String) v.get(2);
        int inicioEnInt;
        if (inicioEnCadena.equals("n")) inicioEnInt = Integer.MAX_VALUE;
        else {
            try {
                inicioEnInt = Integer.parseInt(inicioEnCadena);
            } catch (Exception e) {
            	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_InicioNoEsEnteroOn, v);
            }
        }
        if (inicioEnInt < 0) {
        	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_InicioEsNegativo, v);
        }
        // Obtenemos el final de rango. Si no es entero positivo o n -> ERROR y salimos
        String finalEnCadena = (String) v.get(3);
        int finalEnInt;
        if (finalEnCadena.equals("n")) finalEnInt = Integer.MAX_VALUE;
        else {
            try {
                finalEnInt = Integer.parseInt(finalEnCadena);
            } catch (Exception e) {
            	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_FinalNoEsEnteroOn, v);
            }
        }
        if (finalEnInt < 0) {
        	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_FinalEsNegativo, v);
        }
        // Aqui ya sabemos que los valores (individualmete) son correctos
        if (inicioEnInt > finalEnInt) {
        	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_InicioMayorQueFinal, v);
        }
        // Aqui ya sabemos que los valores (conjuntamente) son correctos
        Vector veya = tr.getListaEntidadesYAridades();
        int cont = 0;
        boolean salir = false;
        while (cont < veya.size() && !salir) {
            EntidadYAridad eya = (EntidadYAridad) veya.get(cont);
            if ((eya.getEntidad() == idEntidad) && (eya.getRol().equals(rolViejo))) salir = true;
            else cont++;
        }
        EntidadYAridad eya = (EntidadYAridad) veya.get(cont);
        if (cardinalidadMax1Seleccionada) finalEnInt = 1; //hola
        eya.setPrincipioRango(inicioEnInt);
        eya.setFinalRango(finalEnInt);
        eya.setRol(rol);
        eya.setTineEtiqueta(minMaxSeleccionado);
        eya.setMarcadaConCardinalidad(cardinalidadSeleccionada);
        eya.setMarcadaConMinMax(minMaxSeleccionado);
        eya.setMarcadaConParticipacion(participacionSeleccionada);
        eya.setMarcadaConCardinalidadMax1(cardinalidadMax1Seleccionada);

        veya.setElementAt(eya, cont);
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_EditarCardinalidadEntidad_ERROR_DAORelaciones, v);
        else return new Contexto(true, TC.SR_EditarCardinalidadEntidad_HECHO, v);
    }

    /*
     * Forzar la aridad de una entidad en una relacion para que sea uno a uno
     */
    public Contexto aridadEntidadUnoUno(Vector v) {
    	return new Contexto(true, TC.SR_AridadEntidadUnoUno_HECHO, v);
    }

    /*
     * Quitar una entidad de una relacion
     */
    public Contexto quitarEntidadARelacion(Vector<Object> datos) {
        TransferRelacion tr = (TransferRelacion) datos.get(0);
        TransferEntidad te = (TransferEntidad) datos.get(1);
        String rol = (String) datos.get(2);
        // Quitamos la entidad de la lista de entidadesYaridades de la relacion
        Vector<EntidadYAridad> listaEntidades = tr.getListaEntidadesYAridades();
        int cont = 0;
        boolean salir = false;
        while (cont < listaEntidades.size() && !salir) {
            EntidadYAridad eya = listaEntidades.get(cont);
            if (eya.getEntidad() == te.getIdEntidad() && (eya.getRol().equals(rol))) {
                listaEntidades.remove(cont);
                salir = true;
            }
            cont++;
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        if (!daoRelaciones.modificarRelacion(tr))
        	return new Contexto(false, TC.SR_QuitarEntidadARelacion_ERROR_DAORelaciones, datos);
        else return new Contexto(true, TC.SR_QuitarEntidadARelacion_HECHO, datos);
    }


    public boolean tieneAtributo(TransferRelacion tr, TransferAtributo ta) {
        for (int i = 0; i < tr.getListaAtributos().size(); i++) {
            if (Integer.parseInt((String) tr.getListaAtributos().get(i)) == ta.getIdAtributo())
                return true;
        }
        return false;
    }
}