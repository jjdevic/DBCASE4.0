package modelo.servicios;

import config.Config;
import controlador.Contexto;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.*;
import org.w3c.dom.Document;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosEntidades {

    public void ListaDeEntidades() throws ExceptionAp {
        DAOEntidades dao = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista_entidades = dao.ListaDeEntidades();
    }

    //Devuelve actualizada la lista de entidades
    public Vector<TransferEntidad> ListaDeEntidadesNOVoid() throws ExceptionAp {
        DAOEntidades dao = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista_entidades = dao.ListaDeEntidades();
        return lista_entidades;
    }

    /* Anadir Entidad
     * Parametros: un TransferEntidad que contiene el nombre de la nueva entidad y la posicion donde debe ir dibujado.
     * Devuelve: Contexto de exito con un vector con la entidad en un TransferEntidad, 
     * 		y el mensaje -> SE_InsertarEntidad_HECHO
     * Condiciones:
     * Si el nombre es vacio -> SE_InsertarEntidad_ERROR_NombreDeEntidadEsVacio
     * Si el nombre ya existe -> SE_InsertarEntidad_ERROR_NombreDeEntidadYaExiste
     * Si al usar el DAOEntidades se produce un error -> SE_InsertarEntidad_ERROR_DAO
     */

    public Contexto anadirEntidad(TransferEntidad te, Stack<Document> pilaDeshacer) throws ExceptionAp {
        if (te.getNombre().isEmpty()) {
            return new Contexto(false, TC.SE_InsertarEntidad_ERROR_NombreDeEntidadEsVacio, null);
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(te.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_InsertarEntidad_ERROR_NombreDeEntidadYaExiste, te);
            }
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
        for (Iterator it = listaR.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(te.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_InsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion, te);
            }
        }
        DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
        Vector<TransferAgregacion> listaA = daoAgregaciones.ListaDeAgregaciones();
        for (Iterator it = listaA.iterator(); it.hasNext(); ) {
            TransferAgregacion elem_te = (TransferAgregacion) it.next();
            if (elem_te.getNombre().toLowerCase().equals(te.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_InsertarRelacion_ERROR_NombreDeEntidadYaExisteComoAgregacion, te);
            }
        }

        //Aquí se añade la entidad
        int id = daoEntidades.anadirEntidad(te, pilaDeshacer);
        if (id == -1) return new Contexto(false, TC.SE_InsertarEntidad_ERROR_DAO, null);
        else {
            te.setIdEntidad(id);
            Vector<Object> vec = new Vector<Object>();
        	vec.add(daoEntidades.consultarEntidad(te));
            return new Contexto(true, TC.SE_InsertarEntidad_HECHO, vec);
        }
    }

    /* Se puede Anadir Entidad
     * Realiza las comprobaciones oportunas para ver si se puede introducir una entidad pero NO la inserta, simplemente
     * devuelve true o false indicando si se puede realizar la acción.
     * Parametros: un TransferEntidad que contiene el nombre de la nueva entidad y la posicion donde debe ir dibujado.
     * Devuelve: Contexto resultante.
     * Condiciones:
     * Si el nombre es vacio -> SE_InsertarEntidad_ERROR_NombreDeEntidadEsVacio
     * Si el nombre ya existe -> SE_InsertarEntidad_ERROR_NombreDeEntidadYaExiste
     * Si al usar el DAOEntidades se produce un error -> SE_InsertarEntidad_ERROR_DAO
     */
    public Contexto SePuedeAnadirEntidad(TransferEntidad te) throws ExceptionAp {
        if (te.getNombre().isEmpty()) {
            return new Contexto(false, TC.SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadEsVacio, null);
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(te.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExiste, te);
            }
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
        for (Iterator it = listaR.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(te.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SE_ComprobarInsertarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion, te);
            }
        }
        return new Contexto(true, null);
    }

    /*
     * Renombrar una en entidad
     * -> Recibe la entidad y el nuevo nombre
     */
    public Contexto renombrarEntidad(Vector v) throws ExceptionAp  {
        TransferEntidad te = (TransferEntidad) v.get(0);
        String nuevoNombre = (String) v.get(1);
        String antiguoNombre = te.getNombre();
        // Si nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_NombreDeEntidadEsVacio, v);
        }
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferEntidad elem_te = (TransferEntidad) it.next();
            if (elem_te.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase()) && (elem_te.getIdEntidad() != te.getIdEntidad())) {
                return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExiste, v);
            }
        }
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
        if (listaR == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAORelaciones, v);
        }
        for (Iterator it = listaR.iterator(); it.hasNext(); ) {
            TransferRelacion elem_tr = (TransferRelacion) it.next();
            if (elem_tr.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())) {
                return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_NombreDeEntidadYaExisteComoRelacion, te);
            }
        }
        te.setNombre(nuevoNombre);
        if (daoEntidades.modificarEntidad(te) == false) {
            te.setNombre(antiguoNombre);
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        } else {
            v.add(antiguoNombre);
            return new Contexto(true, TC.SE_RenombrarEntidad_HECHO, v);
        }
    }

    /* Debilitar/Fortalecer una entidad
     * -> Entidad a la que hay que voltear su caracter debil
     */
    public Contexto debilitarEntidad(TransferEntidad te) throws ExceptionAp {
        te.setDebil(!te.isDebil());
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        if (daoEntidades.modificarEntidad(te) == false) {
            te.setDebil(!te.isDebil());
            return new Contexto(false, TC.SE_DebilitarEntidad_ERROR_DAOEntidades, te);
        } else {
        	Vector<Object> v = new Vector<Object>();
        	v.add(te);
        	return new Contexto(true, TC.SE_DebilitarEntidad_HECHO, v);
        }
    }

    public boolean esDebil(int i) throws ExceptionAp {
        DAOEntidades dao = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaentidades = dao.ListaDeEntidades();
        for (int j = 0; j < listaentidades.size(); j++) {
            if (listaentidades.get(j).getIdEntidad() == i)
                return listaentidades.get(j).isDebil();
        }
        return false;
    }


    public Contexto anadirRelacionAEntidad(Vector v) throws ExceptionAp {
        //la relacion es el primer elemento del vector y la entidad el siguiente
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        te.getListaRelaciones().add(tr.getIdRelacion());
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        if (!daoEntidades.modificarEntidad(te)) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOEntidades, v); //el mensaje de error es el mismo
        } else return new Contexto(true, null);
    }

    public void eliminarRelacionDeEntidad(TransferRelacion tr) throws ExceptionAp {
        DAOEntidades dao = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> listaentidades = dao.ListaDeEntidades();

        for (TransferEntidad entidad : listaentidades) {
            Vector listaRelaciones = entidad.getListaRelaciones();
            if (listaRelaciones.contains(Integer.toString(tr.getIdRelacion()))) {
                listaRelaciones.remove(Integer.toString(tr.getIdRelacion()));
            }
            entidad.setListaRelaciones(listaRelaciones);
            dao.modificarEntidad(entidad);
        }
    }

    /*
     * Anadir un atributo a una entidad
     * -> en v viene la entidad (pos 0) y el atributo (pos 1)
     */
    public Contexto anadirAtributo(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        // Si nombre de atributo es vacio -> ERROR
        if (ta.getNombre().isEmpty()) {
        	v.add("0"); //anadimos un flag para identificar que no se ha anadido el atributo
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoVacio, v);
        }

        // Si nombre de atributo ya existe en esa entidad-> ERROR
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
        if (lista == null) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos, v);
        }
        for (int i = 0; i < te.getListaAtributos().size(); i++) {
        	
            if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) te.getListaAtributos().get(i)))).toLowerCase().equals(ta.getNombre().toLowerCase())) {
            	v.add("0");
            	return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoYaExiste, v);
            }
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
        ta.setPosicion(te.nextAttributePos(ta.getPosicion()));
        int idNuevoAtributo = daoAtributos.anadirAtributo(ta);
        if (idNuevoAtributo == -1) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos, v);
        }
        // Anadimos el atributo a la lista de atributos de la entidad
        ta.setIdAtributo(idNuevoAtributo);
        te.getListaAtributos().add(Integer.toString(idNuevoAtributo));

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        if (!daoEntidades.modificarEntidad(te)) {
            return new Contexto(false, TC.SE_AnadirAtributoAEntidad_ERROR_DAOEntidades, v);
        }

        // Si todo ha ido bien devolvemos al controlador la entidad modificada y el nuevo atributo
        v.add("1");
        return new Contexto(true, TC.SE_AnadirAtributoAEntidad_HECHO, v);
    }


    /* Eliminar entidad
     * Parametros: el TransferEntidad que contiene la entidad que se desea eliminar
     * Devuelve: Un contexto de exito con un vector con un TransferEntidad que contiene la entidad eliminada
     * 		 y el mensaje -> SE_EliminarEntidad_HECHO.
     * Condiciones:
     * Se se produce un error al usar el DAOEntidades -> SE_EliminarEntidad_ERROR_DAOEntidades
     */
    public Contexto eliminarEntidad(TransferEntidad te, int vieneDeOtro) throws ExceptionAp {
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        // Eliminamos la entidad
        if (daoEntidades.borrarEntidad(te) == false)
            return new Contexto(false, TC.SE_EliminarEntidad_ERROR_DAOEntidades, te);
        else {
            Vector<TransferRelacion> vectorRelacionesModificadas = this.eliminaRefererenciasAEntidad(te);
            Vector<Object> vectorEntidadEliminadaYvectorRelacionesModificadas = new Vector<Object>();
            vectorEntidadEliminadaYvectorRelacionesModificadas.add(te);
            vectorEntidadEliminadaYvectorRelacionesModificadas.add(vectorRelacionesModificadas);
            vectorEntidadEliminadaYvectorRelacionesModificadas.add(vieneDeOtro);
            return new Contexto(true, TC.SE_EliminarEntidad_HECHO, vectorEntidadEliminadaYvectorRelacionesModificadas);
        }
    }

    /*
     * Unicamente una entidad puede estar referenciada en las relaciones que hay en el sistema. Puede darse
     * el caso de que una entidad no este referenciada (la entidad esta sola). En este caso al eliminar las
     * referencias no se modifica ningun elemento del sistema (devolveremos null).
     * Por el contrario, si esta referenciada, puede estarlo en varias relaciones. Para cada una de las
     * relaciones la modificaremos quitando la referencia, la persistimos.
     * Deolveremos un vector de relaciones modificadas. Cuando no este referenciada, el vector estara vacio.
     */
    private Vector<TransferRelacion> eliminaRefererenciasAEntidad(TransferEntidad te) throws ExceptionAp {
        Vector<TransferRelacion> vectorRelaciones = new Vector<TransferRelacion>();
        int idEntidad = te.getIdEntidad();
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> listaRelaciones = daoRelaciones.ListaDeRelaciones();
        int contListaRelaciones = 0;
        while (contListaRelaciones < listaRelaciones.size()) {
            // Obtenemos la relacion
            TransferRelacion tr = listaRelaciones.get(contListaRelaciones);
            // Obtenemos la lista de entidades y aridades
            Vector listaEntidadesYAridades = tr.getListaEntidadesYAridades();
            // Recorremos la lista
            int contListaEntidadesYAridades = 0;
            boolean referenciada = false;
            while (contListaEntidadesYAridades < listaEntidadesYAridades.size() && !referenciada) {
                EntidadYAridad eya = (EntidadYAridad) listaEntidadesYAridades.get(contListaEntidadesYAridades);
                // Si esta referenciad
                if (eya.getEntidad() == idEntidad) {
                    referenciada = true;
                    listaEntidadesYAridades.remove(contListaEntidadesYAridades);
                }
                contListaEntidadesYAridades++;
            }
            // Si estaba referenciada la moficamos en la persistecia
            if (referenciada) {
                daoRelaciones = new DAORelaciones(Config.getPath());
                daoRelaciones.modificarRelacion(tr);
                vectorRelaciones.add(tr);
            }
            contListaRelaciones++;
        }
        return vectorRelaciones;
    }

    public Contexto anadirRestriccion(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        String restriccion = (String) v.get(1);

        if (restriccion.isEmpty()) return new Contexto(true, TC.SE_AnadirRestriccionAEntidad_HECHO, v);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }

        Vector<String> vRestricciones = te.getListaRestricciones();
        vRestricciones.add(restriccion);
        te.setListaRestricciones(vRestricciones);

        if (daoEntidades.modificarEntidad(te) == false) {
            return new Contexto(true, TC.SE_AnadirRestriccionAEntidad_HECHO, v);
        } else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto quitarRestriccion(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        String restriccion = (String) v.get(1);

        if (restriccion.isEmpty()) return new Contexto(true, TC.SE_QuitarRestriccionAEntidad_HECHO, v);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
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

        if (daoEntidades.modificarEntidad(te))
            return new Contexto(true, TC.SE_QuitarRestriccionAEntidad_HECHO, v);
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto setRestricciones(Vector v) throws ExceptionAp {
        Vector restricciones = (Vector) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        
        //Cambiar el orden de los elementos en el vector para que el primer elemento sea el transfer.
        v.set(0, te);
        v.set(1, restricciones);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
        te.setListaRestricciones(restricciones);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_setRestriccionesAEntidad_HECHO, v);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto anadirUnique(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        String unique = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (unique.isEmpty()) return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, null);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }

        Vector<String> vUniques = te.getListaUniques();
        vUniques.add(unique);
        te.setListaUniques(vUniques);

        //te.setNombre(nuevoNombre);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_AnadirUniqueAEntidad_HECHO, v);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto quitarUnique(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        String unique = (String) v.get(1);

        if (unique.isEmpty()) return new Contexto(true, null);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }

        Vector<String> vUniques = te.getListaUniques();
        int i = 0;
        boolean encontrado = false;
        while (i < vUniques.size() && !encontrado) {
            if (vUniques.get(i).equals(unique)) {
                vUniques.remove(i);
                encontrado = true;
            }
            i++;
        }
        te.setListaUniques(vUniques);

        if (daoEntidades.modificarEntidad(te))
            return new Contexto(true, TC.SE_QuitarUniqueAEntidad_HECHO, v);
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto setUniques(Vector v) throws ExceptionAp {
        Vector uniques = (Vector) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        
        //Preparar la salida.
        Vector<Object> v_aux = new Vector<Object>();
        v_aux.add(te);
        v_aux.add(uniques);

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v_aux);
        }
        te.setListaUniques(uniques);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_setUniquesAEntidad_HECHO, v_aux);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v_aux);
        }
    }

    /*
     * Quitar/poner un Unique unitario a la entidad
     * */
    public Contexto setUniqueUnitario(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        Vector uniques = te.getListaUniques();
        Vector uniquesCopia = new Vector();

        boolean encontrado = false;
        int i = 0;
        while (i < uniques.size()) {
            if ((uniques.get(i)).equals(ta.getNombre())) encontrado = true;
            else uniquesCopia.add(uniques.get(i));
            i++;
        }

        if (!encontrado)
            uniquesCopia.add(ta.getNombre());

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);

        }
        te.setListaUniques(uniquesCopia);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_setUniqueUnitarioAEntidad_HECHO, v);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto eliminarReferenciasUnitario(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        Vector uniques = te.getListaUniques();
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

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
        
        te.setListaUniques(uniquesCopia);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_setUniqueUnitarioAEntidad_HECHO, v);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    public Contexto renombraUnique(Vector v) throws ExceptionAp {
        TransferEntidad te = (TransferEntidad) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        String antiguoNombre = (String) v.get(2);
        Vector uniques = te.getListaUniques();
        Vector uniquesCopia = new Vector();
        
        int i = 0;
        while (i < uniques.size()) {
            if (uniques.get(i).toString().equals(antiguoNombre)) {
                String s = uniques.get(i).toString();
                s = s.replaceAll(antiguoNombre, ta.getNombre());
                uniquesCopia.add(s);
            } else uniquesCopia.add(uniques.get(i));
            i++;
        }

        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> lista = daoEntidades.ListaDeEntidades();
        if (lista == null) {
            return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
        te.setListaUniques(uniquesCopia);
        if (daoEntidades.modificarEntidad(te)) {
            return new Contexto(true, TC.SE_setUniqueUnitarioAEntidad_HECHO, v);
        }
        else {
        	return new Contexto(false, TC.SE_RenombrarEntidad_ERROR_DAOEntidades, v);
        }
    }

    /*
     * Mover una entidad (cambiar su posicion)
     */
    public Contexto moverPosicionEntidad(TransferEntidad te) throws ExceptionAp  {
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        if (daoEntidades.modificarEntidad(te) == false)
            return new Contexto(false, TC.SE_MoverPosicionEntidad_ERROR_DAOEntidades, te);
        else {
        	Vector<Object> v = new Vector<Object>();
        	v.add(te);
        	return new Contexto(true, TC.SE_MoverPosicionEntidad_HECHO, v);
        } 
    }

    public boolean tieneAtributo(TransferEntidad te, TransferAtributo ta) {
        for (int i = 0; i < te.getListaAtributos().size(); i++) {
            if (Integer.parseInt((String) te.getListaAtributos().get(i)) == ta.getIdAtributo())
                return true;
        }
        return false;
    }
}