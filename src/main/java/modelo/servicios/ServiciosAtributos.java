package modelo.servicios;

import controlador.Config;
import controlador.TC;
import modelo.transfers.*;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;

import java.util.Vector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosAtributos {

	//private Controlador controlador;
	
    public Vector<TransferAtributo> getListaDeAtributos() {
        DAOAtributos dao = new DAOAtributos();
        Vector<TransferAtributo> lista_atributos = dao.ListaDeAtributos();
        return lista_atributos;
    }

    //TODO Cambiar el tipo de retorno a Contexto en vez de TC en todos los métodos

    /* Añadir atributo
     * -> en v viene el atributo padre (pos 0) y el atributo hijo (pos 1)
     */
    public TC anadirAtributo(Vector v) {
        TransferAtributo tap = (TransferAtributo) v.get(0);
        TransferAtributo tah = (TransferAtributo) v.get(1);
        
        // Si nombre de atributo hijo es vacio -> ERROR
        if (tah.getNombre().isEmpty()) {
            //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio, v);
            return TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio;
        }
        // Si nombre de atributo ya existe en esa entidad-> ERROR
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
        if (lista == null) {
            //controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo, v);
            return TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo;
        }
        for (int i = 0; i < tap.getListaComponentes().size(); i++)
            if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) tap.getListaComponentes().get(i)))).toLowerCase().equals(tah.getNombre().toLowerCase())) {
                //controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste, v);
                return TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste;
            }


        // Si hay tamano y no es un entero positivo -> ERROR
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo, v);
                    return TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo;
                }
            } catch (Exception e) {
                //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero, v);
                return TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero;
            }
        }
        // Creamos el atributo
        //DAOAtributos daoAtributos = new DAOAtributos(this.controlador);
        int idNuevoAtributo = daoAtributos.anadirAtributo(tah);
        if (idNuevoAtributo == -1) {
            //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo, v);
            return TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo;
        }
        // Anadimos el atributo a la lista de subatributos del atributo
        tah.setIdAtributo(idNuevoAtributo);
        tap.getListaComponentes().add(Integer.toString(idNuevoAtributo));
        if (!daoAtributos.modificarAtributo(tap)) {
            //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre, v);
            return TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre;
        }
        // Si todo ha ido bien devolvemos al controlador la el atributo padre modificado y el nuevo atributo
        //this.controlador.mensajeDesde_SA(TC.SA_AnadirSubAtributoAtributo_HECHO, v);
        return TC.SA_AnadirSubAtributoAtributo_HECHO;
    }


    /*
     * Eliminar atributo
     * Parametros: recibe un transfer atributo del con el atributo a eliminar
     * Devuelve:
     * el transfer atributo que contiene el atributo eliminado y el mensaje SA_EliminarAtributo_HECHO
     * Condiciones:
     * Si es un atributo compuesto hay que eliminar tambien sus subatributos.
     * Si se produce un error al usar el DAOAtributos ->  SA_EliminarAtributo_ERROR_DAOAtributos
     * Hay que comprobar primero que el atributo que viene en el transfer exista con un consultar
     */

    public TC eliminarAtributo(TransferAtributo ta, int vieneDeOtro) {//si el entero es 1 viene de eliminar entidad o relacion
        DAOAtributos daoAtributos = new DAOAtributos();
        TransferAtributo aux = ta;
        ta = daoAtributos.consultarAtributo(ta);
        
        //el atributo puede haber sido eliminado al eliminar la entidad o relacion a la que pertenecía
        //al hacer una eliminación de múltiples nodos
        if (ta == null) return TC.SA_EliminarAtributo_ERROR_DAOAtributos;; //TODO crear mensaje error
        ta.setClavePrimaria(aux.getClavePrimaria());
        ta.setCompuesto(aux.getCompuesto());
        ta.setDominio(aux.getDominio());
        ta.setFrecuencia(aux.getFrecuencia());
        //ta.setIdAtributo(aux.getIdAtributo() + 10);
        ta.setListaComponentes(aux.getListaComponentes());
        ta.setListaRestricciones(aux.getListaComponentes());
        ta.setMultivalorado(aux.getMultivalorado());
        //ta.setNombre(aux.getNombre());
        ta.setNotnull(aux.getNotnull());
        ta.setUnique(aux.getUnique());
        ta.setVolumen(aux.getVolumen());

        // Si no es compuesto
        if (!ta.getCompuesto()) {
            if (daoAtributos.borrarAtributo(ta) == false)
                //controlador.mensajeDesde_SA(TC.SA_EliminarAtributo_ERROR_DAOAtributos, ta);
            	return TC.SA_EliminarAtributo_ERROR_DAOAtributos;
            else {
                Transfer elem_mod = this.eliminaRefererenciasAlAtributo(ta);
                if (elem_mod instanceof TransferAtributo) {
                    TransferAtributo t = (TransferAtributo) elem_mod;
                    Vector<TransferAtributo> cta = getListaDeAtributos();
                    for (int i = 0; i < cta.size(); ++i) {
                        if (cta.get(i).getIdAtributo() == t.getIdAtributo())
                            ((TransferAtributo) elem_mod).setClavePrimaria(cta.get(i).getClavePrimaria());
                    }
                }

                Vector<Object> vectorAtributoYElemMod = new Vector<Object>();
                vectorAtributoYElemMod.add(ta);
                vectorAtributoYElemMod.add(elem_mod);
                if (vectorAtributoYElemMod.size() == 2) vectorAtributoYElemMod.add(vieneDeOtro);
                else vectorAtributoYElemMod.set(2, vieneDeOtro);
                //controlador.mensajeDesde_SA(TC.SA_EliminarAtributo_HECHO, vectorAtributoYElemMod);
                return TC.SA_EliminarAtributo_HECHO;
            }
        }
        /*
         * Si es compuesto...
         * 1.- Obtenemos sus subatributos
         * 2.- Los eliminamos recursivamente
         * 3.- Mandamos al controlador el atributo que se ha eliminado y el elem modificado tras
         * 	   tras usar el metodo elimina referencias.
         */

        else {
            Vector lista_idSubatributos = ta.getListaComponentes();
            int cont = 0;
            while (cont < lista_idSubatributos.size()) {
                int idAtributoHijo = Integer.parseInt((String) lista_idSubatributos.get(cont));
                TransferAtributo ta_hijo = new TransferAtributo();
                ta_hijo.setIdAtributo(idAtributoHijo);
                this.eliminarAtributo(ta_hijo, 1);
                cont++;
            }
            // Ya estan eliminados todos sus subatributos. Ponemos compuesto a falso y eliminamos
            //ta.setCompuesto(false);
            daoAtributos = new DAOAtributos();
            daoAtributos.modificarAtributo(ta);
            if (daoAtributos.borrarAtributo(ta) == false)
                //controlador.mensajeDesde_SA(TC.SA_EliminarAtributo_ERROR_DAOAtributos, ta);
            	return TC.SA_EliminarAtributo_ERROR_DAOAtributos;
            else {
                Transfer elem_mod = this.eliminaRefererenciasAlAtributo(ta);
                Vector<Object> vectorAtributoYElemMod = new Vector<Object>();
                vectorAtributoYElemMod.add(ta);
                vectorAtributoYElemMod.add(elem_mod);
                if (vectorAtributoYElemMod.size() == 2) vectorAtributoYElemMod.add(vieneDeOtro);
                else vectorAtributoYElemMod.set(2, vieneDeOtro);
                //controlador.mensajeDesde_SA(TC.SA_EliminarAtributo_HECHO, vectorAtributoYElemMod);
                return TC.SA_EliminarAtributo_HECHO;
            }
        }
    }


    /*
     * Este metodo sirve para eliminar las referencias que hay hacia un atributo. Las referencias
     * pueden provenir de 3 lugares: de una entidad, de una relacion o de otro atibuto (si es hijo
     * de un atributo compuesto). El metodo moficara el elemento (uno de estos 3) que lo referencia
     * y lo devolvera en un transfer para comunicar la modificacion al controlador.
     */
    public Transfer eliminaRefererenciasAlAtributo(TransferAtributo ta) {
        // Obtenemos el identificador del atributo
        int idAtributo = ta.getIdAtributo();
        boolean enEntidad = false;
        boolean enRelacion = false;
        boolean enAtributo = false;
        boolean enAgregacion = false;

        // Buscamos si esta en entidades
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector listaEntidades = daoEntidades.ListaDeEntidades();
        int j = 0;
        while (j < listaEntidades.size() && !enEntidad) {
            // Obetenemos la entidad y la lista de atributos de la entidad
            TransferEntidad te = (TransferEntidad) listaEntidades.get(j);
            Vector listaAtributos = te.getListaAtributos();
            int k = 0;
            while (k < listaAtributos.size() && !enEntidad) {
                int id_posible = Integer.parseInt((String) listaAtributos.get(k));
                if (idAtributo == id_posible) {
                    // Es un atributo de una entidad
                    enEntidad = true;
                    // Lo quitamos de la lista
                    listaAtributos.remove(k);
                    // Si esta en la lista de claves primarias la quitamos
                    Vector listaClaves = te.getListaClavesPrimarias();
                    int l = 0;
                    while (l < listaClaves.size()) {
                        int id_clave = Integer.parseInt((String) listaClaves.get(l));
                        if (id_clave == idAtributo) {
                            listaClaves.remove(l);
                            te.setListaClavesPrimarias(listaClaves);
                        }
                        l++;
                    }
                    // Modificamos en la persistencia la entidad y lo devolvemos
                    daoEntidades.modificarEntidad(te);
                    return te;
                }
                k++;
            }
            j++;
        }
        // Buscamos si esta en relaciones
        // Sabemos que no puede estar en una entidad y relacion a la vez.
        if (!enEntidad) {
            DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
            Vector listaRelaciones = daoRelaciones.ListaDeRelaciones();
            j = 0;
            while (j < listaRelaciones.size() && !enRelacion) {
                // Obetenemos la relacion y la lista de atributos de la relacion
                TransferRelacion tr = (TransferRelacion) listaRelaciones.get(j);
                Vector listaAtributos = tr.getListaAtributos();
                int k = 0;
                while (k < listaAtributos.size() && !enRelacion) {
                    int id_posible = Integer.parseInt((String) listaAtributos.get(k));
                    if (idAtributo == id_posible) {
                        // Es un atributo de una relacion
                        enRelacion = true;
                        // Lo quitamos de la lista
                        listaAtributos.remove(k);
                        // Modificamos en la persistencia la relacion y la devolvemos
                        daoRelaciones.modificarRelacion(tr);
                        return tr;
                    }
                    k++;
                }
                j++;
            }
        }

        if (!enEntidad && !enRelacion) {
            // Buscamos si esta en atributos, es decir es un subatributo de un compuesto.
            DAOAtributos daoAtributos = new DAOAtributos();
            Vector listaAtributos = daoAtributos.ListaDeAtributos();
            j = 0;
            while (j < listaAtributos.size() && !enAtributo) {
                // Obetenemos el atributo y la lista de subatributos del atributo
                TransferAtributo ta_padre = (TransferAtributo) listaAtributos.get(j);
                Vector listaSubatributos = ta_padre.getListaComponentes();
                int k = 0;
                while (k < listaSubatributos.size() && !enAtributo) {
                    int id_posible = Integer.parseInt((String) listaSubatributos.get(k));
                    if (idAtributo == id_posible) {
                        // Es un subatributo de un atributo
                        enAtributo = true;
                        // Lo quitamos de la lista de componentes
                        listaSubatributos.remove(k);
                        // Modificamos en la persistencia el atributo y lo devolvemos
                        boolean clavePrim = ta_padre.getClavePrimaria();
                        daoAtributos.modificarAtributo(ta_padre);
                        return ta_padre;
                    }
                    k++;
                }
                j++;
            }

        }

        if (!enEntidad && !enRelacion && !enAtributo) {
            // Buscamos si esta en agregaciones
            DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
            Vector listaAgregaciones = daoAgregaciones.ListaDeAgregaciones();
            j = 0;
            while (j < listaAgregaciones.size() && !enAgregacion) {
                // Obetenemos el atributo y la lista de subatributos del atributo
                TransferAgregacion ta_padre = (TransferAgregacion) listaAgregaciones.get(j);
                Vector listaAtributos = ta_padre.getListaAtributos();
                int k = 0;
                while (k < listaAtributos.size() && !enAgregacion) {
                    int id_posible = Integer.parseInt((String) listaAtributos.get(k));
                    if (idAtributo == id_posible) {
                        // Es un subatributo de un atributo
                        enAtributo = true;
                        // Lo quitamos de la lista de componentes
                        listaAtributos.remove(k);
                        // Modificamos en la persistencia el atributo y lo devolvemos
                        //boolean clavePrim = ta_padre.getClavePrimaria();
                        daoAgregaciones.modificarAgregacion(ta_padre);
                        return ta_padre;
                    }
                    k++;
                }
                j++;
            }

        }
        // Si devuelve null es que el atributo no esta referenciado (ERROR!)
        return null;
    }


    /*
     * Renombrar atributo
     * -> Recibe el atributo y el nuevo nombre
     */
    public TC renombrarAtributo(Vector v) {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String nuevoNombre = (String) v.get(1);
        String antiguoNombre = ta.getNombre();
        // Si el nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio, v);
            return TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio;
        }
        int idAtributo = ta.getIdAtributo();
        boolean encontrado = false;
        // Buscamos si esta en entidades
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector listaEntidades = daoEntidades.ListaDeEntidades();
        int j = 0;
        while (j < listaEntidades.size() && !encontrado) {
            // Obetenemos la entidad y la lista de atributos de la entidad
            TransferEntidad te = (TransferEntidad) listaEntidades.get(j);
            Vector listaAtributos = te.getListaAtributos();
            int k = 0;
            while (k < listaAtributos.size()) {
                int id_posible = Integer.parseInt((String) listaAtributos.get(k));
                if (idAtributo == id_posible) {
                    // Es un atributo de una entidad
                    encontrado = true;
                    // Si nombre de atributo ya existe en esa entidad-> ERROR
                    DAOAtributos daoAtributos = new DAOAtributos();
                    for (int i = 0; i < te.getListaAtributos().size(); i++)
                        if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) te.getListaAtributos().get(i)))).toLowerCase().equals(nuevoNombre.toLowerCase())
                                && i != k) {
                            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste, v);
                            return TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste;
                        }
                }
                k++;
            }
            j++;
        }
        // Buscamos si esta en relaciones
        // Sabemos que no puede estar en una entidad y relacion a la vez.
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector listaRelaciones = daoRelaciones.ListaDeRelaciones();
        j = 0;
        while (j < listaRelaciones.size() && !encontrado) {
            // Obetenemos la relacion y la lista de atributos de la relacion
            TransferRelacion tr = (TransferRelacion) listaRelaciones.get(j);
            Vector listaAtributos = tr.getListaAtributos();
            int k = 0;
            while (k < listaAtributos.size()) {
                int id_posible = Integer.parseInt((String) listaAtributos.get(k));
                if (idAtributo == id_posible) {
                    // Es un atributo de una relacion
                    encontrado = true;

                    // Si nombre de atributo ya existe en esa entidad-> ERROR
                    DAOAtributos daoAtributos = new DAOAtributos();
                    for (int i = 0; i < tr.getListaAtributos().size(); i++)
                        if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) tr.getListaAtributos().get(i)))).toLowerCase().equals(nuevoNombre.toLowerCase())
                                && i != k) {
                            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste, v);
                            return TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste;
                        }
                }
                k++;
            }
            j++;
        }
        // Buscamos si esta en atributos, es decir es un subatributo de un compuesto.
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector listaAtributos = daoAtributos.ListaDeAtributos();
        j = 0;
        while (j < listaAtributos.size()) {
            // Obetenemos el atributo y la lista de subatributos del atributo
            TransferAtributo ta_padre = (TransferAtributo) listaAtributos.get(j);
            Vector listaSubatributos = ta_padre.getListaComponentes();
            int k = 0;
            while (k < listaSubatributos.size() && !encontrado) {
                int id_posible = Integer.parseInt((String) listaSubatributos.get(k));
                if (idAtributo == id_posible) {
                    // Es un subatributo de un atributo
                    encontrado = true;
                    // Si nombre de atributo ya existe en esa entidad-> ERROR
                    for (int i = 0; i < listaSubatributos.size(); i++)
                        if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) listaSubatributos.get(i)))).toLowerCase().equals(nuevoNombre.toLowerCase())
                                && i != k) {
                            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste, v);
                            return TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste;
                        }
                }
                k++;
            }
            j++;
        }
        // Modificamos el atributo
        ta.setNombre(nuevoNombre);
        if (daoAtributos.modificarAtributo(ta) == false) {
            ta.setNombre(antiguoNombre);
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_DAOAtributos, v);
            return TC.SA_RenombrarAtributo_ERROR_DAOAtributos;
        } else {
            v.add(antiguoNombre);
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_HECHO, v);
            return TC.SA_RenombrarAtributo_HECHO;
        }
    }

    /*
     * Editar dominio de atributo
     * -> Recibe v con el atributo, el nuevo dominio y si tiene tamano el tamano
     */

    public TC editarDomnioAtributo(Vector<Object> v) {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String nuevoDominio = (String) v.get(1);
        // Si tiene tamano comprobamos que es correcto
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    //this.controlador.mensajeDesde_SA(TC.SA_EditarDominioAtributo_ERROR_TamanoEsNegativo, ta);
                    return TC.SA_EditarDominioAtributo_ERROR_TamanoEsNegativo;
                }


            } catch (Exception e) {
                //this.controlador.mensajeDesde_SA(TC.SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero, ta);
                return TC.SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero;
            }
        }
        // Modificamos el atributo
        DAOAtributos daoAtributos = new DAOAtributos();
        ta.setDominio(nuevoDominio);
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_EditarDominioAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_EditarDominioAtributo_ERROR_DAOAtributos;
        else
            //controlador.mensajeDesde_SA(TC.SA_EditarDominioAtributo_HECHO, ta);
        	return TC.SA_EditarDominioAtributo_HECHO;
    }


    /*
     * Editar compuesto de un atributo
     * -> Hay que voltear el valor de compuesto
     */

    public TC editarCompuestoAtributo(TransferAtributo ta) {
        // Modificamos el atributo
        ta.setCompuesto(!ta.getCompuesto());
        // Ponemos su dominio a null si es compuesto
        if (ta.getCompuesto()) ta.setDominio("null");
        // Persistimos
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_EditarCompuestoAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_EditarCompuestoAtributo_ERROR_DAOAtributos;
        else
            //controlador.mensajeDesde_SA(TC.SA_EditarCompuestoAtributo_HECHO, ta);
        	return TC.SA_EditarCompuestoAtributo_HECHO;
    }


    /*
     * Editar multivalorado de un atributo
     * -> Hay que voltear el valor de multuvalorado
     */

    public TC editarMultivaloradoAtributo(TransferAtributo ta) {
        // Modificamos el atributo
        ta.setMultivalorado(!ta.isMultivalorado());
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos;
        else
            //controlador.mensajeDesde_SA(TC.SA_EditarMultivaloradoAtributo_HECHO, ta);
        	return TC.SA_EditarMultivaloradoAtributo_HECHO;
    }

    public TC editarNotNullAtributo(TransferAtributo ta) {
        // Modificamos el atributo
        ta.setNotnull(!ta.getNotnull());
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_EditarNotNullAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_EditarNotNullAtributo_ERROR_DAOAtributos;
        else
            //controlador.mensajeDesde_SA(TC.SA_EditarNotNullAtributo_HECHO, ta);
        	return TC.SA_EditarNotNullAtributo_HECHO;
    }

    public TC editarUniqueAtributo(TransferAtributo ta) {
        // Modificamos el atributo
        ta.setUnique(!ta.getUnique());
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_EditarUniqueAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_EditarUniqueAtributo_ERROR_DAOAtributos;
        else {
            Vector<Object> ve = new Vector<Object>();
            ve.add(ta);
            //controlador.mensajeDesde_SA(TC.SA_EditarUniqueAtributo_HECHO, ve);
            return TC.SA_EditarUniqueAtributo_HECHO;
        }
    }

    public TC anadirRestriccion(Vector v) {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String restriccion = (String) v.get(1);
        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) return null; //TODO Añadir TC.restriccionsinnombre_ERROR

        DAOAtributos daoAtributoes = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributoes.ListaDeAtributos();
        if (lista == null) {
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_DAOAtributos, v);
            return TC.SA_RenombrarAtributo_ERROR_DAOAtributos;
        }

        Vector<String> vRestricciones = ta.getListaRestricciones();
        vRestricciones.add(restriccion);
        ta.setListaRestricciones(vRestricciones);

        if (daoAtributoes.modificarAtributo(ta) != false)
            //controlador.mensajeDesde_SA(TC.SA_AnadirRestriccionAAtributo_HECHO, v);
        	return TC.SA_AnadirRestriccionAAtributo_HECHO;
        return null; //TODO Añadir mensaje de error
    }

    public TC quitarRestriccion(Vector v) {
        TransferAtributo te = (TransferAtributo) v.get(0);
        String restriccion = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) return null; //TODO Aniadir mensaje

        DAOAtributos daoAtributoes = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributoes.ListaDeAtributos();
        if (lista == null) {
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_DAOAtributos, v);
            return TC.SA_RenombrarAtributo_ERROR_DAOAtributos;
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

        if (daoAtributoes.modificarAtributo(te) != false)
            //controlador.mensajeDesde_SA(TC.SA_QuitarRestriccionAAtributo_HECHO, v);
        	return TC.SA_QuitarRestriccionAAtributo_HECHO;
        return null; //TODO Aniadir mensaje error
    }

    public TC setRestricciones(Vector v) {
        Vector restricciones = (Vector) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);

        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos();
        if (lista == null) {
            //controlador.mensajeDesde_SA(TC.SA_RenombrarAtributo_ERROR_DAOAtributos, v);
            return TC.SA_RenombrarAtributo_ERROR_DAOAtributos;
        }
        ta.setListaRestricciones(restricciones);
        if (daoAtributos.modificarAtributo(ta) != false)
            //controlador.mensajeDesde_SA(TC.SA_setRestriccionesAAtributo_HECHO, v);
        	return TC.SA_setRestriccionesAAtributo_HECHO;
        return null; //TODO Aniadir mensaje error
    }

    /*
     * Mover un atributo (cambiar su posicion)
     */
    public TC moverPosicionAtributo(TransferAtributo ta) {
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
            //controlador.mensajeDesde_SA(TC.SA_MoverPosicionAtributo_ERROR_DAOAtributos, ta);
        	return TC.SA_MoverPosicionAtributo_ERROR_DAOAtributos;
        else
            //controlador.mensajeDesde_SA(TC.SA_MoverPosicionAtributo_HECHO, ta);
        	return TC.SA_MoverPosicionAtributo_HECHO;
    }

    /**
     * Metodo que pone/quita el atributo ta como clave primaria de la entidad a la que pertence
     * En el vector viene el atributo (pos 0) y la entidad (pos 1)
     * Hay que negar el valor de esClavePrimaria del atributo
     */
    public TC editarClavePrimariaAtributo(Vector<Object> v) {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);

        // Si era clave primaria
        if (ta.isClavePrimaria()) {
            te.getListaClavesPrimarias().remove(String.valueOf(ta.getIdAtributo()));
            ta.setClavePrimaria(false);

        }
        // Si no era clave primaria
        else {
            te.getListaClavesPrimarias().add(String.valueOf(ta.getIdAtributo()));
            ta.setClavePrimaria(true);
            ta.setNotnull(false);
            ta.setMultivalorado(false);
            ta.setUnique(false);
        }

        // Persistimos la entidad y devolvemos el mensaje
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        if (daoEntidades.modificarEntidad(te) == false) return TC.SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades;
            //controlador.mensajeDesde_SA(TC.SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades, v);
        else return TC.SA_EditarClavePrimariaAtributo_HECHO;
        	//controlador.mensajeDesde_SA(TC.SA_EditarClavePrimariaAtributo_HECHO, v);
    }

    public String getNombreAtributo(int id) {
        DAOAtributos daoAtributos = new DAOAtributos();
        return daoAtributos.nombreDeAtributo(id);
    }

    public boolean idUnique(int id) {
        DAOAtributos daoAtributos = new DAOAtributos();
        return daoAtributos.uniqueAtributo(id);
    }
}
