package modelo.servicios;


import config.Config;
import controlador.Contexto;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.TipoDominio;
import modelo.transfers.TransferDominio;
import persistencia.DAODominios;
import vista.Lenguaje;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiciosDominios {

    public void ListaDeDominios() throws ExceptionAp {
        Object[] items = modelo.transfers.TipoDominio.values();
        DAODominios dao = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista_dominios = dao.ListaDeDominios();
        for (int i = 0; i < items.length; i++) {
            TransferDominio td = new TransferDominio();
            td.setNombre(items[i].toString());
            td.setTipoBase((TipoDominio) items[i]);
            td.setListaValores(null);
            lista_dominios.add(td);
        }
    }

    public Vector<TransferDominio> getListaDeDominios() throws ExceptionAp {
        Object[] items = modelo.transfers.TipoDominio.values();
        DAODominios dao = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista_dominios = dao.ListaDeDominios();
        for (int i = 0; i < items.length; i++) {
            TransferDominio td = new TransferDominio();
            td.setNombre(items[i].toString());
            td.setTipoBase((TipoDominio) items[i]);
            td.setListaValores(null);
            lista_dominios.add(td);
        }
        return lista_dominios;
    }

    /* Anadir Dominio
     * Parametros: un TransferDominio que contiene el nombre del nuevo dominio
     * Devuelve: Contexto de exito con dominio en un TransferDominio dentro de un vector 
     * 			y el mensaje -> SD_InsertarDominio_HECHO
     * Condiciones:
     * Si el nombre es vacio -> SD_InsertarDominio_ERROR_NombreDeDominioEsVacio
     * Si el nombre ya existe -> SD_InsertarDominio_ERROR_NombreDeDominioYaExiste
     * Si al usar el DAODominio se produce un error -> SD_InsertarDominio_ERROR_DAO
     */
    public Contexto anadirDominio(TransferDominio td) throws ExceptionAp {
        if (td.getNombre().isEmpty()) {
            return new Contexto(false, TC.SD_InsertarDominio_ERROR_NombreDeDominioEsVacio);
        }
        for (int i = 0; i < td.getListaValores().size(); i++) {
            if (td.getListaValores().get(i).toString().equals("")) {
                Vector v = new Vector();
                v.add(td);
                v.add(Lenguaje.text(Lenguaje.EMPTY_VALUE));
                return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_10, v);
            }
        }
        DAODominios daoDominios = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista = daoDominios.ListaDeDominios();
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferDominio elem_td = (TransferDominio) it.next();
            if (elem_td.getNombre().toLowerCase().equals(td.getNombre().toLowerCase())) {
            	return new Contexto(false, TC.SD_InsertarDominio_ERROR_NombreDeDominioYaExiste, td);
            }
        }
        //comprobamos que todos los valores se correspondan con el tipo base
        Contexto ctxt_aux = comprobarTipoBase(td);
        if (ctxt_aux.isExito()) {
        	int id = daoDominios.anadirDominio(td);
            if (id == -1) return new Contexto(false, TC.SD_InsertarDominio_ERROR_DAO);
            else {
                td.setIdDominio(id);
                Vector<Object> vec = new Vector<Object>();
            	vec.add(td);
                return new Contexto(true, TC.SD_InsertarDominio_HECHO, vec);
            }
        } else {
        	return ctxt_aux;
        }
    }

    /*
     * Renombrar un dominio
     * -> Recibe el dominio y el nuevo nombre
     */
    public Contexto renombrarDominio(Vector v) throws ExceptionAp {
        TransferDominio td = (TransferDominio) v.get(0);
        String nuevoNombre = (String) v.get(1);
        String antiguoNombre = td.getNombre();
        // Si nombre es vacio -> ERROR
        if (nuevoNombre.isEmpty()) {
        	return new Contexto(false, TC.SD_RenombrarDominio_ERROR_NombreDeDominioEsVacio, v);
        }
        DAODominios daoDominio = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista = daoDominio.ListaDeDominios();
        if (lista == null) {
        	return new Contexto(false, TC.SD_RenombrarDominio_ERROR_DAODominios, v);
        }
        for (Iterator it = lista.iterator(); it.hasNext(); ) {
            TransferDominio elem_td = (TransferDominio) it.next();
            if (elem_td.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase()) && (elem_td.getIdDominio() != td.getIdDominio())) {
            	return new Contexto(false, TC.SD_RenombrarDominio_ERROR_NombreDeDominioYaExiste, v);
            }
        }
        td.setNombre(nuevoNombre);
        if (daoDominio.modificarDominio(td) == false) {
            td.setNombre(antiguoNombre);
            return new Contexto(false, TC.SD_RenombrarDominio_ERROR_DAODominios, v);
        } else {
            v.add(antiguoNombre);
            return new Contexto(true, TC.SD_RenombrarDominio_HECHO, v);
        }
    }


    /* Eliminar dominio
     * Parametros: el TransferEntidad que contiene el dominio que se desea eliminar
     * Devuelve: Un contexto de exito con un vector con un TransferDominio que contiene 
     * 		el dominio eliminado, y el mensaje -> SD_EliminarDominio_HECHO.
     * Condiciones:
     * Se produce un error al usar el DAODominios -> SD_EliminarDominio_ERROR_DAODominios
     */
    public Contexto eliminarDominio(TransferDominio td) throws ExceptionAp {
        DAODominios daoDominios = new DAODominios(Config.getPath());
        // Eliminamos el Dominio
        if (daoDominios.borrarDominio(td) == false)
        	return new Contexto(false, TC.SD_EliminarDominio_ERROR_DAODominios, td);
        else {
            Vector<Object> vector = new Vector<Object>();
            vector.add(td);
            return new Contexto(true, TC.SD_EliminarDominio_HECHO, vector);
        }
    }

    public Contexto modificarDominio(Vector<Object> v) throws ExceptionAp {
        TransferDominio td = (TransferDominio) v.get(0);
        Vector<String> nuevosValores = (Vector<String>) v.get(1);
        Vector<String> antiguosValores = td.getListaValores();
        modelo.transfers.TipoDominio nuevoTipoB = (modelo.transfers.TipoDominio) v.get(2);
        modelo.transfers.TipoDominio antiguoTipoB = td.getTipoBase();
        // Si nombre es vacio -> ERROR
        if (nuevosValores == null) {
            return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_ElementosDominioEsVacio, v);
        }
        if (nuevoTipoB == null) {
            return new Contexto(false, TC.SD_ModificarTipoBaseDominio_ERROR_TipoBaseDominioEsVacio, v);
        }
        for (int i = 0; i < nuevosValores.size(); i++) {
            if (nuevosValores.get(i).toString().equals("")) {
                return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_ValorNoValido, v);
            }
        }
        DAODominios daoDominio = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista = daoDominio.ListaDeDominios();
        if (lista == null) {
            return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_DAODominios, v);
        }
        td.setTipoBase(nuevoTipoB);
        td.setListaValores(nuevosValores);
        
        Contexto ctxt_aux = comprobarTipoBase(td);
        if (ctxt_aux.isExito()) {
            if (daoDominio.modificarDominio(td) == false) {
                td.setListaValores(antiguosValores);
                return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_DAODominios, v);
            } else {
                v.add(antiguosValores);
                v.add(antiguoTipoB);
                return new Contexto(true, TC.SD_ModificarElementosDominio_HECHO, v);
            }
        } else {
            td.setListaValores(antiguosValores);
            td.setTipoBase(antiguoTipoB);
            return ctxt_aux;
        }
    }

    //Se usa para ordenar los valores
    public Contexto modificarElementosDominio(Vector<Object> v) throws ExceptionAp {
        TransferDominio td = (TransferDominio) v.get(0);
        Vector<String> nuevosValores = (Vector<String>) v.get(1);
        Vector<String> antiguosValores = td.getListaValores();
        // Si nombre es vacio -> ERROR
        if (nuevosValores == null) {
            return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_ElementosDominioEsVacio, v);
        }
        for (int i = 0; i < nuevosValores.size(); i++) {
            if (nuevosValores.get(i).toString().equals("")) {
                return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_ValorNoValido, v);
            }
        }
        DAODominios daoDominio = new DAODominios(Config.getPath());
        Vector<TransferDominio> lista = daoDominio.ListaDeDominios();
        if (lista == null) {
            return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_DAODominios, v);
        }

        td.setListaValores(nuevosValores);
        Contexto ctxt_aux = comprobarTipoBase(td);
        if (ctxt_aux.isExito()) {
            if (daoDominio.modificarDominio(td) == false) {
                td.setListaValores(antiguosValores);
                return new Contexto(false, TC.SD_ModificarElementosDominio_ERROR_DAODominios, v);
            } else {
                v.add(antiguosValores);
                return new Contexto(true, TC.SD_ModificarElementosDominio_HECHO, v);
            }
        } else {
        	td.setListaValores(antiguosValores);
        	return ctxt_aux;
        }
    }

    private Contexto comprobarTipoBase(TransferDominio td) {
        Vector listaValores = td.getListaValores();
        modelo.transfers.TipoDominio tipoBase = td.getTipoBase();
        switch (tipoBase) {
            case INTEGER: {
                for (int i = 0; i < listaValores.size(); i++) {
                    try {
                        String s = ((String) listaValores.get(i));
                        @SuppressWarnings("unused")
                        int a = Integer.parseInt(s);
                    } catch (Exception e) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.INCORRECT_NUMBER));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_11, v);
                    }
                }
                break;
            }
            case FLOAT: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = ((String) listaValores.get(i));
                    // comprueba que sea una cadena válida
                    boolean resultado;
                    if (s.contains(".")) {
                        Pattern p = Pattern.compile("-?([0-9])+(.[0-9]+)?(E(-?)[0-9]+)?");
                        Matcher m = p.matcher(s);
                        resultado = m.matches();
                    } else {
                        Pattern p = Pattern.compile("-?([0-9])+(E(-?)[0-9]+)?");
                        Matcher m = p.matcher(s);
                        resultado = m.matches();
                    }
                    if (!resultado) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " 1, 0.5, 1.5E100, -1, -0.5");
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_1, v);
                    }
                }
                break;
            }
            case BIT: {
                for (int i = 0; i < listaValores.size(); i++) {
                    try {
                        String s = ((String) listaValores.get(i));
                        if (!(s.equals("0") || s.equals("1"))) {
                            Vector v = new Vector();
                            v.add(td);
                            v.add(Lenguaje.text(Lenguaje.INCORRECT_BIT_VALUE));
                            return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_2, v);
                        }

                    } catch (Exception e) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.INCORRECT_BIT_VALUE));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_2, v);
                    }
                }
                break;
            }
            case DATE: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = (String) listaValores.get(i);
                    if (!(s.startsWith("'") && s.endsWith("'"))) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                    } else {
                        s = s.replaceAll("'", "");
                        try {
                            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                            formatoFecha.setLenient(false);
                            formatoFecha.parse(s);
                            if (s.length() != 8) {
                                Vector v = new Vector();
                                v.add(td);
                                v.add(Lenguaje.text(Lenguaje.INCORRECT_DATE));
                                return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_4, v);
                            }
                        } catch (Exception e) {
                            Vector v = new Vector();
                            v.add(td);
                            v.add(Lenguaje.text(Lenguaje.INCORRECT_DATE));
                            return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_4, v);
                        }
                    }
                }
                break;
            }
            case TIME: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = ((String) listaValores.get(i));
                    if (!(s.startsWith("'") && s.endsWith("'"))) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                    } else {
                        s = s.replaceAll("'", "");
                        // comprueba que sean correctos
                        Pattern p = Pattern.compile("(([0-1]?[0-9])|([2][0-3]))(:[0-5][0-9])?(:[0-5][0-9])?(.[0-9][0-9]?[0-9]?)?");
                        Matcher m = p.matcher(s);
                        boolean resultado = m.matches();
                        if (!resultado) {
                            Vector v = new Vector();
                            v.add(td);
                            v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " '00:00:00.999', '22', '22:05', '22:59:59'");
                            return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_5, v);
                        }
                    }
                }
                break;
            }
            case DATETIME: {
                for (int i = 0; i < listaValores.size(); i++) {
                    try {
                        String s = ((String) listaValores.get(i));

                        if (!(s.startsWith("'") && s.endsWith("'"))) {
                            Vector v = new Vector();
                            v.add(td);
                            v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                            return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                        } else {
                            s = s.replaceAll("'", "");
                            //separo hasta el espacio, la fecha y la hora
                            if (s.indexOf(" ") != -1) {
                                int espacio = s.indexOf(" ");
                                String date = s.substring(0, espacio);
                                String time = s.substring(espacio + 1);
                                //System.out.println(date);
                                //System.out.println(time);
                                //comprobar date
                                boolean resulDate;
                                try {
                                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                                    formatoFecha.setLenient(false);
                                    formatoFecha.parse(date);
                                    resulDate = true;
                                    //para comprobar que no haya cosas alfinal de la cadena
                                    if (date.length() != 8) {
                                        resulDate = false;
                                    }

                                } catch (Exception e) {
                                    resulDate = false;
                                }
                                //comprobar time
                                Pattern p = Pattern.compile("(([0-1]?[0-9])|([2][0-3]))(:[0-5][0-9])?(:[0-5][0-9])?(.[0-9][0-9]?[0-9]?)?");
                                Matcher m = p.matcher(time);
                                boolean resulTime = m.matches();
                                //si error en alguno:
                                if (resulDate == false || resulTime == false) {
                                    Vector v = new Vector();
                                    v.add(td);
                                    v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) +
                                            " '20201125','281125 12','281125 12:34:00','281125 12:34:00.200' ");
                                    return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_6, v);
                                }
                            } else {//es sólo la fecha
                                try {
                                    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
                                    formatoFecha.setLenient(false);
                                    formatoFecha.parse(s);

                                } catch (Exception e) {
                                    Vector v = new Vector();
                                    v.add(td);
                                    v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) +
                                            " '20231125','20231125 12','20201125 12:34:00','20191125 12:34:00.200' ");
                                    return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_7, v);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) +
                                " '20191125','20251125 12','20121125 12:34:00','20081125 12:34:00.200' ");
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_8, v);
                    }
                }

                break;
            }
            case BLOB: {
				/*for (int i=0; i<listaValores.size();i++){
					
				}
				*/
                break;
            }
            case CHAR: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = (String) listaValores.get(i);
                    if (!(s.startsWith("'") && s.endsWith("'"))) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                    }
                }
                break;
            }
            case VARCHAR: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = (String) listaValores.get(i);
                    if (!(s.startsWith("'") && s.endsWith("'"))) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                    }
                }
                break;
            }
            case TEXT: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = (String) listaValores.get(i);
                    if (!(s.startsWith("'") && s.endsWith("'"))) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.QUOTATION_MARKS));
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_3, v);
                    }
                }
                break;
            }
            case DECIMAL: {
                for (int i = 0; i < listaValores.size(); i++) {
                    String s = ((String) listaValores.get(i));
                    // comprueba que no contenga caracteres prohibidos
                    Pattern p = Pattern.compile("-?[0-9]+(.[0-9]+)?");
                    Matcher m = p.matcher(s);
                    boolean resultado = m.matches();
                    if (!resultado) {
                        Vector v = new Vector();
                        v.add(td);
                        v.add(Lenguaje.text(Lenguaje.INCORRECT_VALUE_EX) + " 1, 0.5");
                        return new Contexto(false, TC.SD_InsertarDominio_ERROR_ValorNoValido_9, v);
                    }
                }
                break;
            }
        }
        return new Contexto(true, null);
    }
}
