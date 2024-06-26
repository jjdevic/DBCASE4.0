package modelo.servicios;

import config.Config;
import controlador.Contexto;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.*;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosAtributos {
	
	private FactoriaServicios facServicios;
	
	public ServiciosAtributos(FactoriaServicios f) {
		this.facServicios = f;
	}
	
    public Vector<TransferAtributo> getListaDeAtributos() throws ExceptionAp{
        DAOAtributos dao = new DAOAtributos();
        Vector<TransferAtributo> lista_atributos = dao.ListaDeAtributos();
        return lista_atributos;
    }

    /* Añadir atributo
     * -> en v viene el atributo padre (pos 0) y el atributo hijo (pos 1)
     */
    public Contexto anadirAtributo(Vector v) throws ExceptionAp {
        TransferAtributo tap = (TransferAtributo) v.get(0);
        TransferAtributo tah = (TransferAtributo) v.get(1);
        
        // Si nombre de atributo hijo es vacio -> ERROR
        if (tah.getNombre().isEmpty()) {
            return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoVacio);
        }
        // Si nombre de atributo ya existe en esa entidad-> ERROR
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
        if (lista == null) {
            return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo);
        }
        for (int i = 0; i < tap.getListaComponentes().size(); i++)
            if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) tap.getListaComponentes().get(i)))).toLowerCase().equals(tah.getNombre().toLowerCase())) {
                return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_NombreDeAtributoYaExiste);
            }


        // Si hay tamano y no es un entero positivo -> ERROR
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoEsNegativo);
                }
            } catch (Exception e) {
                return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_TamanoNoEsEntero);
            }
        }
        
        // Creamos el atributo
        int idNuevoAtributo = daoAtributos.anadirAtributo(tah);
        if (idNuevoAtributo == -1) {
            return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosHijo);
        }
        // Anadimos el atributo a la lista de subatributos del atributo
        tah.setIdAtributo(idNuevoAtributo);
        tap.getListaComponentes().add(Integer.toString(idNuevoAtributo));
        if (!daoAtributos.modificarAtributo(tap)) {
            return new Contexto(false, TC.SA_AnadirSubAtributoAtributo_ERROR_DAOAtributosPadre);
        }
        // Si todo ha ido bien devolvemos el atributo padre modificado y el nuevo atributo
        return new Contexto(true, TC.SA_AnadirSubAtributoAtributo_HECHO, v);
    }

    /*
     * Eliminar atributo
     * Parametros: recibe un transfer atributo del con el atributo a eliminar, y un entero que indica si viene de eliminar entidad o relacion
     * Devuelve:
     * el transfer atributo que contiene el atributo eliminado y el mensaje SA_EliminarAtributo_HECHO y 
     * los contextos de las posibles llamadas recursivas si hay subatributos
     * Condiciones:
     * Si es un atributo compuesto hay que eliminar tambien sus subatributos.
     * Si se produce un error al usar el DAOAtributos ->  SA_EliminarAtributo_ERROR_DAOAtributos
     * Hay que comprobar primero que el atributo que viene en el transfer exista con un consultar
     */
    public Contexto eliminarAtributo(TransferAtributo ta, int vieneDeOtro) throws ExceptionAp { 
    	//Primera llamada a eliminarAtributo_imp
    	Deque<Contexto> dq = eliminarAtributo_imp(ta, vieneDeOtro);
    	
    	//Extraer contexto principal (ultimo contexto devuelto por la llamada)
    	Contexto c = dq.getLast();
    	
    	//Eliminar el contexto principal de dq
    	dq.removeLast(); 
    	
    	//Aniadir todos los contextos al vector de datos del contexto principal (excepto el propio ctxt principal)
    	Vector<Contexto> subContextos = new Vector<Contexto>();
    	while(!dq.isEmpty()) subContextos.add(dq.pop());
    	c.setSubContextos(subContextos);
    	
    	return c;
    }
    
    private Deque<Contexto> eliminarAtributo_imp(TransferAtributo ta, int vieneDeOtro) throws ExceptionAp {
        DAOAtributos daoAtributos = new DAOAtributos();
        TransferAtributo aux = ta;
        ta = daoAtributos.consultarAtributo(ta);
        Deque<Contexto> resultado = new LinkedList<Contexto>();
        
        //el atributo puede haber sido eliminado al eliminar la entidad o relacion a la que pertenecía
        //al hacer una eliminación de múltiples nodos
        if (ta == null) {
        	resultado.add(new Contexto(false, TC.SA_EliminarAtributo_ERROR_DAOAtributos)); 
        	return resultado;
        }

        // Si no es compuesto
        if (!ta.getCompuesto()) {
            if (daoAtributos.borrarAtributo(ta) == false) {
            	resultado.add(new Contexto(false, TC.SA_EliminarAtributo_ERROR_DAOAtributos));
            	return resultado;
            }
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
                
                resultado.add(new Contexto(true, TC.SA_EliminarAtributo_HECHO, vectorAtributoYElemMod));
                return resultado;
            }
        }
        /*
         * Si es compuesto...
         * 1.- Obtenemos sus subatributos
         * 2.- Los eliminamos recursivamente
         * 3.- Insertamos en la doble cola todos los contextos devueltos por la llamada recursiva.
         */

        else {
            Vector lista_idSubatributos = ta.getListaComponentes();
            int cont = 0;
            
            boolean elim_ok = true;
            
            while (cont < lista_idSubatributos.size() && elim_ok) {
                int idAtributoHijo = Integer.parseInt((String) lista_idSubatributos.get(cont));
                TransferAtributo ta_hijo = new TransferAtributo();
                ta_hijo.setIdAtributo(idAtributoHijo);
                
                Deque<Contexto> dq_aux = this.eliminarAtributo_imp(ta_hijo, 1);
                //Determinar si se ha conseguido eliminar el atributo que queriamos eliminar
                boolean exito_subat = dq_aux.getLast().isExito();
                
                //Aniadir todos los contextos solo si se ha conseguido, si no se terminará la ejecución de la función al terminar el bucle.
                if(exito_subat) resultado.addAll(dq_aux);
                
                elim_ok = exito_subat;
                cont++;
            }
            //Si no se ha conseguido eliminar algun atributo, terminar.
            if(!elim_ok) {
            	resultado.add(new Contexto(false, TC.SA_EliminarAtributo_ERROR_DAOAtributos));
            	return resultado;
            }
            
            // Ya estan eliminados todos sus subatributos. Ponemos compuesto a falso y eliminamos
            //ta.setCompuesto(false);
            daoAtributos = new DAOAtributos();
            daoAtributos.modificarAtributo(ta);
            if (daoAtributos.borrarAtributo(ta) == false) {
            	resultado.add(new Contexto(false, TC.SA_EliminarAtributo_ERROR_DAOAtributos));
            	return resultado;
            }
            	
            else {
            	/*Crear contexto con atributo eliminado, el elem modificado tras
                * 	   tras usar el metodo elimina referencias, y vieneDeOtro.
                * El contexto se aniade a la doble cola 'resultado' y se devuelve esa doble cola. */
                Transfer elem_mod = this.eliminaRefererenciasAlAtributo(ta);
                Vector<Object> vectorAtributoYElemMod = new Vector<Object>();
                vectorAtributoYElemMod.add(0, ta);
                vectorAtributoYElemMod.add(1, elem_mod);
                
                if (vectorAtributoYElemMod.size() == 2) vectorAtributoYElemMod.add(2, vieneDeOtro);
                else vectorAtributoYElemMod.set(2, vieneDeOtro);
                
                resultado.add(new Contexto(true, TC.SA_EliminarAtributo_HECHO, vectorAtributoYElemMod));
                return resultado;
            }
        }
    }


    /*
     * Este metodo sirve para eliminar las referencias que hay hacia un atributo. Las referencias
     * pueden provenir de 3 lugares: de una entidad, de una relacion o de otro atibuto (si es hijo
     * de un atributo compuesto). El metodo moficara el elemento (uno de estos 3) que lo referencia
     * y lo devolvera en un transfer para comunicar la modificacion.
     */
    public Transfer eliminaRefererenciasAlAtributo(TransferAtributo ta) throws ExceptionAp {
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
    public Contexto renombrarAtributo(Vector v) throws ExceptionAp  {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String nuevoNombre = (String) v.get(1);
        String antiguoNombre = ta.getNombre();
        
        TransferEntidad tEntidadPoseedora = null; //Entidad a la que pertenece el atributo (si pertenece a una entidad)
        TransferRelacion tRelacionPoseedora = null; //Relacion a la que pertenece el atributo (si pertenece a una relacion).
        
        // Si el nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoEsVacio);
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
                    tEntidadPoseedora = te;
                    
                    // Si nombre de atributo ya existe en esa entidad-> ERROR
                    DAOAtributos daoAtributos = new DAOAtributos();
                    for (int i = 0; i < te.getListaAtributos().size(); i++)
                        if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) te.getListaAtributos().get(i)))).toLowerCase().equals(nuevoNombre.toLowerCase())
                                && i != k) {
                            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste);
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
                    tRelacionPoseedora = tr;

                    // Si nombre de atributo ya existe en esa entidad-> ERROR
                    DAOAtributos daoAtributos = new DAOAtributos();
                    for (int i = 0; i < tr.getListaAtributos().size(); i++)
                        if (daoAtributos.nombreDeAtributo((Integer.parseInt((String) tr.getListaAtributos().get(i)))).toLowerCase().equals(nuevoNombre.toLowerCase())
                                && i != k) {
                            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste);
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
                            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_NombreDeAtributoYaExiste);
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
            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
        } else {
        	v.add(antiguoNombre);
        	
            //Tratar las referencias a uniques en el elemento poseedor del atributo.
        	
        	//Preparar datos en v_aux para llamada a ServicioEntidades o ServivioRelaciones
            Vector<Object> v_aux = new Vector<Object>();
            
            v_aux.add(ta.clonar());
            v_aux.add(antiguoNombre);
            
            Contexto aux = null;
            Vector<Contexto> subContextos = new Vector<Contexto>();
            
            if(tEntidadPoseedora != null) {
            	v_aux.add(0, tEntidadPoseedora);
            	aux = facServicios.getServicioEntidades().renombraUnique(v_aux);
            	subContextos.add(aux);
            }
            else if (tRelacionPoseedora != null) {
            	v_aux.add(0, tRelacionPoseedora);
            	aux = facServicios.getServicioRelaciones().renombraUnique(v_aux);
            	subContextos.add(aux);
            }
            
            if(aux != null && !aux.isExito()) {
            	//Si no ha ido bien devolvemos el contexto de error.
            	return aux;
            } else {
            	return new Contexto(true, TC.SA_RenombrarAtributo_HECHO, v, subContextos);
            }
        }
    }

    /*
     * Editar dominio de atributo
     * -> Recibe v con el atributo, el nuevo dominio y si tiene tamano el tamano
     */

    public Contexto editarDomnioAtributo(Vector<Object> v) throws ExceptionAp{
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String nuevoDominio = (String) v.get(1);
        // Si tiene tamano comprobamos que es correcto
        if (v.size() == 3) {
            try {
                int tamano = Integer.parseInt((String) v.get(2));
                if (tamano < 1) {
                    return new Contexto(false, TC.SA_EditarDominioAtributo_ERROR_TamanoEsNegativo);
                }
            } catch (Exception e) {
                return new Contexto(false, TC.SA_EditarDominioAtributo_ERROR_TamanoNoEsEntero);
            }
        }
        
        // Modificamos el atributo
        DAOAtributos daoAtributos = new DAOAtributos();
        ta.setDominio(nuevoDominio);
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_EditarDominioAtributo_ERROR_DAOAtributos);
        else {
        	Vector<Object> vec = new Vector<Object>();
        	vec.add(ta);
        	return new Contexto(true, TC.SA_EditarDominioAtributo_HECHO, vec);
        }
    }


    /*
     * Editar compuesto de un atributo
     * -> Hay que voltear el valor de compuesto
     */

    public Contexto editarCompuestoAtributo(TransferAtributo ta) throws ExceptionAp {
    	Vector<Contexto> subContextos = new Vector<Contexto>();
    	
    	//Si era compuesto, eliminar todos sus subatributos.
    	if(ta.getCompuesto()) {
    		Vector lista_atributos = ta.getListaComponentes();
    		
    		if(lista_atributos != null && !lista_atributos.isEmpty()) {
	            int cont = 0;
	            TransferAtributo tah = new TransferAtributo();
	            
	            while (cont < lista_atributos.size()) {
	                String idAtributo = (String) lista_atributos.get(cont);
	                tah.setIdAtributo(Integer.valueOf(idAtributo));
	                subContextos.add(eliminarAtributo(tah, 1));
	                cont++;
	            }
    		}
    		
    		if(ta.getListaComponentes() != null) ta.getListaComponentes().clear();
    	}
    	
    	
        // Modificamos el atributo
        ta.setCompuesto(!ta.getCompuesto());
        // Ponemos su dominio a null si es compuesto
        if (ta.getCompuesto()) ta.setDominio("null");
        // Persistimos
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_EditarCompuestoAtributo_ERROR_DAOAtributos, subContextos);
        else {
        	Vector<Object> vec = new Vector<Object>();
        	vec.add(ta);
        	return new Contexto(true, TC.SA_EditarCompuestoAtributo_HECHO, vec, subContextos);
        }
    }


    /*
     * Editar multivalorado de un atributo
     * -> Hay que voltear el valor de multuvalorado
     */

    public Contexto editarMultivaloradoAtributo(TransferAtributo ta) throws ExceptionAp {
        // Modificamos el atributo
        ta.setMultivalorado(!ta.isMultivalorado());
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_EditarMultivaloradoAtributo_ERROR_DAOAtributos);
        else {
        	Vector<Object> vec = new Vector<Object>();
            vec.add(ta);
        	return new Contexto(true, TC.SA_EditarMultivaloradoAtributo_HECHO, vec);
        }
    }

    public Contexto editarNotNullAtributo(TransferAtributo ta) throws ExceptionAp {
        // Modificamos el atributo
        ta.setNotnull(!ta.getNotnull());
        
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_EditarNotNullAtributo_ERROR_DAOAtributos, ta);
        else {
        	Vector<Object> vec = new Vector<Object>();
        	vec.add(ta);
        	return new Contexto(true, TC.SA_EditarNotNullAtributo_HECHO, vec);
        }
    }

    public Contexto editarUniqueAtributo(TransferAtributo ta) throws ExceptionAp {
        // Modificamos el atributo
        ta.setUnique(!ta.getUnique());
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_EditarUniqueAtributo_ERROR_DAOAtributos);
        else {
            Vector<Object> ve = new Vector<Object>();
            ve.add(ta);
            return new Contexto(true, TC.SA_EditarUniqueAtributo_HECHO, ve);
        }
    }

    public Contexto anadirRestriccion(Vector v) throws ExceptionAp {
        TransferAtributo ta = (TransferAtributo) v.get(0);
        String restriccion = (String) v.get(1);
        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);

        DAOAtributos daoAtributoes = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributoes.ListaDeAtributos();
        if (lista == null) {
            return new Contexto(false,  TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
        }

        Vector<String> vRestricciones = ta.getListaRestricciones();
        vRestricciones.add(restriccion);
        ta.setListaRestricciones(vRestricciones);

        if (daoAtributoes.modificarAtributo(ta) != false)
        	return new Contexto(true, TC.SA_AnadirRestriccionAAtributo_HECHO, v);
        
        return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
    }

    public Contexto quitarRestriccion(Vector v) throws ExceptionAp {
        TransferAtributo te = (TransferAtributo) v.get(0);
        String restriccion = (String) v.get(1);

        // Si nombre es vacio -> ERROR
        if (restriccion.isEmpty()) return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);

        DAOAtributos daoAtributoes = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributoes.ListaDeAtributos();
        if (lista == null) {
            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
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
        	return new Contexto(true, TC.SA_QuitarRestriccionAAtributo_HECHO, v);
        
        return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
    }

    public Contexto setRestricciones(Vector v) throws ExceptionAp {
        Vector restricciones = (Vector) v.get(0);
        TransferAtributo ta = (TransferAtributo) v.get(1);
        
        //Cambiar el orden de los elementos en el vector para que el primer elemento sea el transfer.
        v.set(0, ta);
        v.set(1, restricciones);

        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos();
        if (lista == null) {
            return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
        }
        ta.setListaRestricciones(restricciones);
        if (daoAtributos.modificarAtributo(ta) != false)
        	return new Contexto(true, TC.SA_setRestriccionesAAtributo_HECHO, v);
        return new Contexto(false, TC.SA_RenombrarAtributo_ERROR_DAOAtributos);
    }

    /*
     * Mover un atributo (cambiar su posicion)
     */
    public Contexto moverPosicionAtributo(TransferAtributo ta) throws ExceptionAp {
        DAOAtributos daoAtributos = new DAOAtributos();
        if (daoAtributos.modificarAtributo(ta) == false)
        	return new Contexto(false, TC.SA_MoverPosicionAtributo_ERROR_DAOAtributos);
        else {
        	Vector<Object> vec = new Vector<Object>();
        	vec.add(ta);
        	return new Contexto(true, TC.SA_MoverPosicionAtributo_HECHO, vec);
        }
    }

    /**
     * Metodo que pone/quita el atributo ta como clave primaria de la entidad a la que pertence
     * En el vector viene el atributo (pos 0) y la entidad (pos 1)
     * Hay que negar el valor de esClavePrimaria del atributo
     */
    public Contexto editarClavePrimariaAtributo(Vector<Object> v) throws ExceptionAp {
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
        if (daoEntidades.modificarEntidad(te) == false) return new Contexto(false,  TC.SA_EditarClavePrimariaAtributo_ERROR_DAOEntidades, v);
        else return new Contexto(true,  TC.SA_EditarClavePrimariaAtributo_HECHO, v);
    }

    public String getNombreAtributo(int id) throws ExceptionAp  {
        DAOAtributos daoAtributos = new DAOAtributos();
        return daoAtributos.nombreDeAtributo(id);
    }

    public boolean idUnique(int id) throws ExceptionAp  {
        DAOAtributos daoAtributos = new DAOAtributos();
        return daoAtributos.uniqueAtributo(id);
    }
}