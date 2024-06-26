package modelo.servicios;

import config.Config;
import controlador.Contexto;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.conectorDBMS.ConectorDBMS;
import modelo.conectorDBMS.FactoriaConectores;
import modelo.transfers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import persistencia.DAOAtributos;
import persistencia.DAODominios;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;
import vista.Lenguaje;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class GeneradorEsquema {
    //atributos para la generacion de los modelos
    private String sqlHTML = "";
    private String mr = "";
    private TransferConexion conexionScriptGenerado = null;
    private RestriccionesPerdidas restriccionesPerdidas = new RestriccionesPerdidas();
    //aqui se almacenaran las tablas ya creadas, organizadas por el id de la entidad /relacion / agregacion (clave) y con el objeto tabla como valor.
    private Hashtable<Integer, Tabla> tablasEntidades = new Hashtable<Integer, Tabla>();
    private Hashtable<Integer, Tabla> tablasRelaciones = new Hashtable<Integer, Tabla>();
    private Hashtable<Integer, Tabla> tablasAgregaciones = new Hashtable<Integer, Tabla>();
    private Vector<Tabla> tablasMultivalorados = new Vector<Tabla>();
    private Hashtable<Integer, Enumerado> tiposEnumerados = new Hashtable<Integer, Enumerado>();
    private ValidadorBD validadorBD;

    public GeneradorEsquema() {
    	this.validadorBD = ValidadorBD.getInstancia(this);
    }
    
    protected boolean estaEnVectorDeEnteros(Vector sinParam, int valor) {
        int i = 0;
        boolean encontrado = false;
        int elem = 0;
        while (i < sinParam.size() && !encontrado) {
            elem = this.objectToInt(sinParam.elementAt(i));
            if (elem == valor) encontrado = true;
            i++;
        }
        return encontrado;
    }

    protected TransferRelacion dameRel(String id) throws ExceptionAp {
        DAORelaciones daoRel = new DAORelaciones(Config.getPath());
        Vector relaciones = daoRel.ListaDeRelaciones();
        TransferRelacion rel = null;
        for (int i = 0; i < relaciones.size(); ++i) {
            if (((TransferRelacion) relaciones.get(i)).getIdRelacion() == Integer.parseInt(id))
                rel = (TransferRelacion) relaciones.get(i);
        }
        return rel;
    }

    protected TransferEntidad dameEnt(int id) throws ExceptionAp {
        DAOEntidades daoEnt = new DAOEntidades(Config.getPath());
        Vector entidades = daoEnt.ListaDeEntidades();
        TransferEntidad ent = null;
        for (int i = 0; i < entidades.size(); ++i) {
            if (((TransferEntidad) entidades.get(i)).getIdEntidad() == id) ent = (TransferEntidad) entidades.get(i);
        }
        return ent;
    }

    //metodos de recorrido de los daos para la creacion de las tablas.


    private void generaTablasAgregaciones() {
		/*DAOAgregaciones daoAgregaciones = new DAOAgregaciones(Config.getPath());
		Vector<TransferAgregacion> agregaciones = daoAgregaciones.ListaDeAgregaciones();
		
		
		
		//recorremos las agregaciones generando las tablas correspondientes
		for(int i = 0; i < agregaciones.size(); ++i) {
			Vector<TransferAtributo>multivalorados=new Vector<TransferAtributo>();
			TransferAgregacion tag = agregaciones.elementAt(i);
			TransferRelacion tr = dameRel((String) tag.getListaRelaciones().get(0));
			tablasRelaciones.remove(tag.getListaRelaciones().get(0)); esto no sirve porqu en generaTablasRelaciones construye las tablas a partir del dao igual que aqui
			Vector<EntidadYAridad> entderel = tr.getListaEntidadesYAridades();
			TransferEntidad en = dameEnt(entderel.get(0).getEntidad());
			
			Tabla tabla = new Tabla(tag.getNombre(),en.getListaRestricciones(), controlador);
			//recorremos los atributos de la agregacion a�adiendolos a la tabla
			Vector<TransferAtributo> atribs=this.dameAtributosEnTransfer(tag.getListaAtributos());
			for(int j = 0; j < atribs.size() ; ++j) {
				TransferAtributo ta=atribs.elementAt(j);
				if(ta.getUnique()) restriccionesPerdidas.add(new restriccionPerdida(tag.getNombre(), ta+" "+Lenguaje.text(Lenguaje.IS_UNIQUE), restriccionPerdida.TABLA));
				if (ta.getCompuesto()) 
					tabla.aniadeListaAtributos(this.atributoCompuesto(ta, tag.getNombre(),""),en.getListaRestricciones(),tiposEnumerados);
				else if (ta.isMultivalorado()) multivalorados.add(ta);
				else{ 
					tabla.aniadeAtributo(ta.getNombre(), ta.getDominio(),tag.getNombre(), tiposEnumerados,ta.getListaRestricciones(), ta.getUnique(), ta.getNotnull());
					for(String rest : (Vector<String>)ta.getListaRestricciones()) {
						restriccionesPerdidas.add(new restriccionPerdida(tag.getNombre(), rest, restriccionPerdida.TABLA));
					}
				}
			}
			//a�adimos las claves primarias de las entidades
			
			for(int j = 0; j < entderel.size(); ++j) {
				TransferEntidad te =  dameEnt(entderel.get(j).getEntidad());
				Vector claves_primarias = te.getListaClavesPrimarias();
				for(int k = 0; k < claves_primarias.size(); ++k) {
					tabla.aniadeClavePrimaria((String)claves_primarias.get(k), null, tag.getNombre());
				}
			}
			
			

		}*/
    }

    private void generaTablasEntidades() throws ExceptionAp {
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferEntidad> entidades = daoEntidades.ListaDeEntidades();

        //recorremos las entidades generando las tablas correspondientes.
        for (int i = 0; i < entidades.size(); i++) {
            Vector<TransferAtributo> multivalorados = new Vector<TransferAtributo>();
            TransferEntidad te = entidades.elementAt(i);
            Tabla tabla = new Tabla(te.getNombre(), te.getListaRestricciones());
            Vector<TransferAtributo> atribs = this.dameAtributosEnTransfer(te.getListaAtributos());
            for (String rest : (Vector<String>) te.getListaRestricciones()) {
                restriccionesPerdidas.add(new RestriccionPerdida(te.getNombre(), rest, RestriccionPerdida.TABLA));
            }
            //recorremos los atributos aniadiendolos a la tabla
            for (int j = 0; j < atribs.size(); j++) {
                TransferAtributo ta = atribs.elementAt(j);
                if (ta.getUnique())
                    restriccionesPerdidas.add(new RestriccionPerdida(te.getNombre(), ta + " " + Lenguaje.text(Lenguaje.IS_UNIQUE), RestriccionPerdida.TABLA));
                if (ta.getCompuesto())
                    tabla.aniadeListaAtributos(this.atributoCompuesto(ta, te.getNombre(), ""), te.getListaRestricciones(), tiposEnumerados);
                else if (ta.isMultivalorado()) multivalorados.add(ta);
                else {
                    tabla.aniadeAtributo(ta.getNombre(), ta.getDominio(), te.getNombre(), tiposEnumerados, ta.getListaRestricciones(), ta.getUnique(), ta.getNotnull());
                    for (String rest : (Vector<String>) ta.getListaRestricciones())
                        restriccionesPerdidas.add(new RestriccionPerdida(te.getNombre(), rest, RestriccionPerdida.TABLA));
                }
            }
            // Anadimos las claves a la relacion

            //aniadimos las claves primarias o logeneraTablasEntidades discriminantes si la entidad es debil.
            Vector<TransferAtributo> claves = this.dameAtributosEnTransfer(te.getListaClavesPrimarias());
            for (int c = 0; c < claves.size(); c++) {
                TransferAtributo ta = claves.elementAt(c);
                if (ta.isMultivalorado()) multivalorados.add(ta);
                else if (ta.getCompuesto())
                    tabla.aniadeListaClavesPrimarias(this.atributoCompuesto(ta, te.getNombre(), ""));
                    //else if(te.isDebil()) tabla.aniadeListaClavesPrimarias(claves);
                else //si es normal, lo aniadimos como clave primaria.
                    tabla.aniadeClavePrimaria(ta.getNombre(), ta.getDominio(), te.getNombre());
            }

            //aniadimos a las tablas del sistema.
            tablasEntidades.put(te.getIdEntidad(), tabla);
            //tratamos los multivalorados que hayan surgido en el proceso.
            for (int mul = 0; mul < multivalorados.size(); mul++) {
                TransferAtributo multi = multivalorados.elementAt(mul);
                this.atributoMultivalorado(multi, te.getIdEntidad());
            }

            // Establecimiento de uniques
            Vector<String> listaUniques = te.getListaUniques();
            for (int m = 0; m < listaUniques.size(); m++) tabla.getUniques().add(listaUniques.get(m));
        }
    }

    private void generaTablasRelaciones() throws ExceptionAp {
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
        Vector<TransferRelacion> relaciones = daoRelaciones.ListaDeRelaciones();
        List<Integer> list_normales = new ArrayList<Integer>();
        List<Integer> list_isA = new ArrayList<Integer>();
        List<Integer> list_debil = new ArrayList<Integer>();
        
        for (int i = 0; i < relaciones.size(); i++) {
        	TransferRelacion tr = relaciones.elementAt(i);
        	if(tr.getTipo().equalsIgnoreCase("Normal")) list_normales.add(i);
        	else if (tr.isIsA()) list_isA.add(i);
        	else list_debil.add(i);
        }
        
        // si es del tipo IsA, actualizamos aniadiendo la clave del padre a
        // las tablas hijas.
        for(Integer i: list_isA) {
        	TransferRelacion tr = relaciones.elementAt(i);
            Vector<TransferAtributo> multivalorados = new Vector<TransferAtributo>();
            Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
            for (String rest : (Vector<String>) tr.getListaRestricciones())
                restriccionesPerdidas.add(new RestriccionPerdida(tr.getNombre(), rest, RestriccionPerdida.TABLA));
            
            /*
             * recorremos todas las entidades asociadas a la relacion.
             * sabemos ademas, por criterios del disenio, que la primera
             * entidad es siempre padre.
             */
            EntidadYAridad padre = veya.firstElement();
            for (int e = 1; e < veya.size(); e++) {
                EntidadYAridad hija = veya.elementAt(e);
                // aniadimos la informacion de clave a las tablas hijas,
                // buscandolas en el sistema.
                tablasEntidades.get(hija.getEntidad()).aniadeListaAtributos(tablasEntidades.get(padre.getEntidad())
                        .getPrimaries(), tr.getListaRestricciones(), tiposEnumerados);

                tablasEntidades.get(hija.getEntidad()).aniadeListaClavesPrimarias(
                        tablasEntidades.get(padre.getEntidad()).getPrimaries());

                Vector<String[]> clavesPadre = tablasEntidades.get(padre.getEntidad()).getPrimaries();
                String[] referenciadas = new String[clavesPadre.size()];
                for (int q = 0; q < clavesPadre.size(); q++) {
                    referenciadas[q] = clavesPadre.get(q)[0];
                }

                tablasEntidades.get(hija.getEntidad())
                        .aniadeListaClavesForaneas(
                                tablasEntidades.get(padre.getEntidad()).getPrimaries(),
                                tablasEntidades.get(padre.getEntidad()).getNombreTabla(), referenciadas);
            }
        }
        
        for(Integer i: list_debil) {
        	TransferRelacion tr = relaciones.elementAt(i);
            Vector<TransferAtributo> multivalorados = new Vector<TransferAtributo>();
            Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
            for (String rest : (Vector<String>) tr.getListaRestricciones())
                restriccionesPerdidas.add(new RestriccionPerdida(tr.getNombre(), rest, RestriccionPerdida.TABLA));
            
        	/*
             * buscamos la entidad debil, que ya tiene tabla y le aniadimos
             * los atributos de las entidades fuertes de las que dependa.
             * Ademas los pondremos como claves foraneas. Contaremos, las
             * entidades fuertes y las debiles que aparezcan, pues este sera
             * el criterio a seguir a la hora de reasignar las claves.
             */
            Vector<TransferEntidad> fuertes = new Vector<TransferEntidad>();
            Vector<TransferEntidad> debiles = new Vector<TransferEntidad>();
            for (int s = 0; s < veya.size(); s++) {
                TransferEntidad aux = new TransferEntidad();
                EntidadYAridad eya = veya.elementAt(s);
                aux.setIdEntidad(eya.getEntidad());
                aux = daoEntidades.consultarEntidad(aux);
                if (aux.isDebil()) debiles.add(aux);
                else fuertes.add(aux);
            }
            // ahora recorremos las fuertes, sacando sus claves y
            // metiendolas en las debiles.
            for (int f = 0; f < fuertes.size(); f++) {
                TransferEntidad fuerte = fuertes.elementAt(f);
                Tabla tFuerte = tablasEntidades.get(fuerte.getIdEntidad());
                for (int d = 0; d < debiles.size(); d++) {
                    TransferEntidad debil = debiles.elementAt(d);
                    Tabla tDebil = tablasEntidades.get(debil.getIdEntidad());
                    tDebil.aniadeListaAtributos(tFuerte.getPrimaries(), fuerte.getListaRestricciones(), tiposEnumerados);
                    Vector<String[]> clavesFuerte = tFuerte.getPrimaries();
                    String[] referenciadas = new String[clavesFuerte.size()];
                    for (int q = 0; q < clavesFuerte.size(); q++) referenciadas[q] = clavesFuerte.get(q)[0];
                    tDebil.aniadeListaClavesForaneas(tFuerte.getPrimaries(), tFuerte.getNombreTabla(), referenciadas);
                    tDebil.aniadeListaClavesPrimarias(tFuerte.getPrimaries());
                }
            }
        }
        
        // recorremos las relaciones creando sus tablas, en funcion de su tipo.
        for (Integer i: list_normales) {
            TransferRelacion tr = relaciones.elementAt(i);
            Vector<TransferAtributo> multivalorados = new Vector<TransferAtributo>();
			/* Aniadiremos los atributos propios y las
			 	claves de las entidades implicadas.*/
            Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
            for (String rest : (Vector<String>) tr.getListaRestricciones())
                restriccionesPerdidas.add(new RestriccionPerdida(tr.getNombre(), rest, RestriccionPerdida.TABLA));
            
            // creamos la tabla
            Tabla tabla = new Tabla(tr.getNombre(), tr.getListaRestricciones());
            // aniadimos los atributos propios.
            Vector<TransferAtributo> ats = this.dameAtributosEnTransfer(tr.getListaAtributos());
            for (int a = 0; a < ats.size(); a++) {
                TransferAtributo ta = ats.elementAt(a);
                if (ta.getUnique())
                    restriccionesPerdidas.add(new RestriccionPerdida(tr.getNombre(), ta + " " + Lenguaje.text(Lenguaje.IS_UNIQUE), RestriccionPerdida.TABLA));
                if (ta.getCompuesto())
                    tabla.aniadeListaAtributos(this.atributoCompuesto(ta, tr.getNombre(), ""), ta.getListaRestricciones(), tiposEnumerados);
                else if (ta.isMultivalorado()) multivalorados.add(ta);
                else {
                    tabla.aniadeAtributo(ta.getNombre(), ta.getDominio(), tr.getNombre(), tiposEnumerados, ta.getListaRestricciones(), ta.getUnique(), ta.getNotnull());
                    for (String rest : (Vector<String>) ta.getListaRestricciones())
                        restriccionesPerdidas.add(new RestriccionPerdida(tr.getNombre(), rest, RestriccionPerdida.TABLA));
                }
            }

            // TRATAMIENTO DE ENTIDADES
            // Comprobar si todas las entidades estan con relacion 0..1 o 1..1
            boolean soloHayUnos = true;
            int k = 0;
            while (soloHayUnos && k < veya.size()) {
                EntidadYAridad eya = veya.get(k);
                if (eya.getFinalRango() <= 1) k++;
                else soloHayUnos = false;
            }

            //Para cada entidad...
            boolean esLaPrimeraDel1a1 = true;
            for (int m = 0; m < veya.size(); m++) {
                // Aniadir su clave primaria a la relacion (es clave foranea)
                EntidadYAridad eya = veya.elementAt(m);
                int idEntidad = eya.getEntidad();
                TransferEntidad aux_e = new TransferEntidad();
                aux_e.setIdEntidad(idEntidad);
                TransferEntidad entidad = daoEntidades.consultarEntidad(aux_e);
                
                Tabla ent = tablasEntidades.get(eya.getEntidad());
                Vector<String[]> previasPrimarias;
                
                if (ent.getPrimaries().isEmpty()) previasPrimarias = ent.getAtributos();
                else previasPrimarias = ent.getPrimaries();
                
                //...pero antes renombrarla con el rol
                Vector<String[]> primarias = new Vector<String[]>();
                String[] referenciadas = new String[previasPrimarias.size()];

                for (int q = 0; q < previasPrimarias.size(); q++) {
                    String[] clave = new String[5];
                    clave[3] = "0";
                    clave[4] = eya.getPrincipioRango() == 0 ? "0" : "1";
                    if (!eya.getRol().equals("")) {
                        clave[0] = eya.getRol() + "_" + previasPrimarias.get(q)[0];
                    } else clave[0] = previasPrimarias.get(q)[0];
                    clave[1] = previasPrimarias.get(q)[1];
                    clave[2] = previasPrimarias.get(q)[2];
                    primarias.add(clave);
                    referenciadas[q] = previasPrimarias.get(q)[0];
                }

                tabla.aniadeListaAtributos(primarias, tr.getListaRestricciones(), tiposEnumerados);
                tabla.aniadeListaClavesForaneas(primarias, ent.getNombreTabla(), referenciadas);

                // Si es 0..1 o 1..1 poner como clave
                if (eya.getFinalRango() > 1) tabla.aniadeListaClavesPrimarias(primarias);
                else {
                    if (soloHayUnos && esLaPrimeraDel1a1) {
                        tabla.aniadeListaClavesPrimarias(primarias);
                        esLaPrimeraDel1a1 = false;
                    } else if (soloHayUnos) {
                        for (String[] clave : (Vector<String[]>) ent.getPrimaries())
                            restriccionesPerdidas.add(
                                    new RestriccionPerdida(ent.getNombreTabla() + "_" + clave[0], tr.getNombre(), RestriccionPerdida.CANDIDATA));
                        String uniques = "";
                        for (int q = 0; q < primarias.size(); q++) {
                            if (q == 0) uniques += primarias.get(q)[0];
                            else uniques += ", " + primarias.get(q)[0];
                        }
                        uniques += "#" + ent.getNombreTabla();
                        tabla.getUniques().add(uniques);
                    } else if (eya.getPrincipioRango() == 1 && eya.getFinalRango() == Integer.MAX_VALUE)
                        for (String[] clave : (Vector<String[]>) ent.getPrimaries())
                            restriccionesPerdidas.add(
                                    new RestriccionPerdida(ent.getNombreTabla() + "_" + clave[0], tr.getNombre(), RestriccionPerdida.CANDIDATA));
                }

                //crea las restricciones perdidas (cuando rangoIni > 1 o rangoFin < N) || rangoIni == 1
                if ((eya.getPrincipioRango() > 0 || eya.getFinalRango() < Integer.MAX_VALUE && eya.getFinalRango() > 1) || eya.getPrincipioRango() == 1) {
                    Tabla aux = tabla.creaClonSinAmbiguedadNiEspacios();
                    boolean recurs = false;
                    Vector<String[]> a = tabla.getPrimaries();
                    for (int j = 0; j < a.size(); j++) {
                        if (j + 1 == a.size() && j > 0) {
                            if (a.get(j)[2].equals(a.get(j - 1)[2])) {
                                if (a.get(j)[0].split("_").length > 1 && a.get(j)[0].split("_")[1].equals(a.get(j - 1)[0].split("_")[1])) {
                                    Vector<String[]> b = new Vector<String[]>();
                                    b.add(a.get(j));
                                    aux.setPrimaries(b);
                                    recurs = true;
                                }
                            }
                        }
                    }
                    restriccionesPerdidas.add(new RestriccionPerdida(recurs ? aux.restriccionIR(true, ent.getNombreTabla()) : tabla.restriccionIR(true, ent.getNombreTabla()), ent.restriccionIR(false, ""),
                            eya.getPrincipioRango(), eya.getFinalRango(), RestriccionPerdida.TOTAL));
                }
            }
            tablasRelaciones.put(tr.getIdRelacion(), tabla);
            for (int mul = 0; mul < multivalorados.size(); mul++) {
                TransferAtributo multi = multivalorados.elementAt(mul);
                this.atributoMultivalorado(multi, tr.getIdRelacion());
            }
        }
    }

    private void generaTiposEnumerados() throws ExceptionAp {
        DAODominios daoDominios = new DAODominios(Config.getPath());
        Vector<TransferDominio> dominios = daoDominios.ListaDeDominios();

        //recorremos los dominios creando sus tipos enumerados
        for (int i = 0; i < dominios.size(); i++) {
            TransferDominio td = dominios.elementAt(i);
            Enumerado enu = new Enumerado(td.getNombre(), td.getTipoBase());
            // Obtener todos sus posibles valores
            Vector<String> valores = td.getListaValores();
            for (int k = 0; k < valores.size(); k++) enu.anadeValor(valores.get(k));
            // Insertar en la tabla Hash
            tiposEnumerados.put(td.getIdDominio(), enu);
        }
    }

    public void reset() {
        tablasEntidades.clear();
        tablasRelaciones.clear();
        tablasAgregaciones.clear();
        tablasMultivalorados.clear();
        tiposEnumerados.clear();

        conexionScriptGenerado = null;
        sqlHTML = "";
    }

    public Contexto generaScriptSQL(TransferConexion conexion) throws ExceptionAp {
        reset();
        StringBuilder warnings = new StringBuilder();
        Contexto aux = validadorBD.validaBaseDeDatos(false, warnings);
        if (!aux.isExito()) return new Contexto(false, null, aux.getDatos());
        // Eliminar tablas anteriores, pero recordar que el modelo a ha sido validado
        reset();
        conexionScriptGenerado = conexion;

        // Cabeceras de los documentos
        sqlHTML = warnings.toString();

        // Creamos las tablas
        generaTablasAgregaciones(); //creamos primero las de las agregaciones porque ahi se impide que se creen las tablas de sus relaciones internas
        generaTablasEntidades();
        generaTablasRelaciones();
        generaTiposEnumerados();
        //sacamos el codigo de cada una de ellas recorriendo las hashtables e imprimiendo.
        creaTablas(conexion);
        creaEnums(conexion);
        ponClaves(conexion);
        ponRestricciones(conexion);
        
        return new Contexto(true, TC.SS_GeneracionScriptSQL, sqlHTML);
    }


    public String exportarCodigo(String text, boolean sql, boolean xml, File ruta) throws ExceptionAp {
        Contexto aux = validadorBD.validaBaseDeDatos(false, new StringBuilder());

        if (!aux.isExito()) {
            throw new ExceptionAp(TC.SCRIPT_ERROR);
        }
        if (text.isEmpty()) {
            throw new ExceptionAp(TC.MUST_GENERATE_SCRIPT);
        }

        if (xml) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("Inf_dbcase");
                doc.appendChild(rootElement);

                String[] lines = text.split("\\r?\\n");

                Element currentSection = null;
                for (String line : lines) {
                    if (line.startsWith("# ")) {
                        currentSection = doc.createElement("Section");
                        rootElement.appendChild(currentSection);

                        Element sectionTitle = doc.createElement("Title");
                        sectionTitle.appendChild(doc.createTextNode(line.substring(2))); // Remove "# "
                        currentSection.appendChild(sectionTitle);
                    } else {
                        Element lineElement = doc.createElement("Relation");
                        lineElement.appendChild(doc.createTextNode(line));
                        currentSection.appendChild(lineElement);
                    }
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(ruta);
                transformer.transform(source, result);
            } catch (Exception e) {
                throw new ExceptionAp(TC.SCRIPT_ERROR);
            }
        } else {
            text = "# " + Lenguaje.text(Lenguaje.SCRIPT_GENERATED) + "\n" +
                    (sql ? "# " + Lenguaje.text(Lenguaje.SYNTAX) + ": " + conexionScriptGenerado.getRuta() + "\n\n" : "") + text;

            try {
                FileWriter file = new FileWriter(ruta);
                file.write(text);
                file.close();
            } catch (IOException e) {
                throw new ExceptionAp(TC.SCRIPT_ERROR);
            }
        }

        return (String) aux.getDatos();
    }
    public Vector<TransferConexion> obtenerTiposDeConexion() {
        Vector<String> nombres = FactoriaConectores.obtenerTodosLosConectores();
        Vector<TransferConexion> conexiones;
        conexiones = new Vector<TransferConexion>();
        conexiones.clear();
        for (int i = 0; i < nombres.size(); i++)
            conexiones.add(new TransferConexion(i, nombres.get(i)));
        return conexiones;
    }
    
    public TransferConexion getConexScriptGenerado() {
    	return conexionScriptGenerado;
    }

    public boolean mismoTipo(TransferConexion tc) {
    	return tc.getTipoConexion() == conexionScriptGenerado.getTipoConexion();
    }
    
    public void ejecutarScriptEnDBMS(TransferConexion tc, String sql) throws ExceptionAp{

        // Ejecutar en DBMS
        System.out.println("Datos de conexion a la base de datos");
        System.out.println("------------------------------------");
        System.out.println("DBMS: " + tc.getRuta() + "(" + tc.getTipoConexion() + ")");
        System.out.println("Usuario: " + tc.getUsuario());
        // System.out.println("Password: " + tc.getPassword());

        System.out.println("Intentando conectar...");
        ConectorDBMS conector = FactoriaConectores.obtenerConector(tc.getTipoConexion());
        try {
            conector.abrirConexion(tc.getRuta(), tc.getUsuario(), tc.getPassword());
        } catch (SQLException e) {
            // Avisar por consola
            System.out.println("ERROR: No se pudo abrir una conexion con la base de datos");
            System.out.println("MOTIVO");
            System.out.println(e.getMessage());

            throw new ExceptionAp(TC.NO_DB_CONEXION, ": \n" + e.getMessage());
        }
        
        String ordenActual = null;
        try {
            // Crear la base de datos
            conector.usarDatabase(tc.getDatabase());
            // Ejecutar cada orden

            String[] orden = sql.split(";");
            for (int i = 0; i < orden.length; i++) {
                if ((orden[i] != null) && (!orden[i].trim().equals("")) && (!orden[i].trim().equals("\n"))) {
                    ordenActual = orden[i].trim() + ";";

                    // Eliminar los comentarios y lineas en blanco
                    if (ordenActual.startsWith("--") && !ordenActual.contains("\n")) continue;
                    while (ordenActual.startsWith("--") || ordenActual.startsWith("\n"))
                        ordenActual = ordenActual.substring(ordenActual.indexOf("\n") + 1);
                    // Ejecutar la orden
                    conector.ejecutarOrden(ordenActual);
                }
            }
        } catch (SQLException e) {
            throw new ExceptionAp(TC.CANT_EXECUTE_SCRIPT, ": \n" + ordenActual + "\n" + e.getMessage());
        }
        
        try {
            conector.cerrarConexion();
        } catch (SQLException e) {
        	throw new ExceptionAp(TC.CANT_CLOSE_CONEXION, ": \n" + e.getMessage());
        }
        
        System.out.println("Conexion cerrada correctamente");
        
    }

    private void creaTablas(TransferConexion conexion) throws ExceptionAp {
        sqlHTML += "<div class='card'><h2>" + Lenguaje.text(Lenguaje.TABLES) + "</h2>";

        Iterator tablasM = tablasMultivalorados.iterator();
        while (tablasM.hasNext()) {
            Tabla t = (Tabla) tablasM.next();
            sqlHTML += t.codigoHTMLCreacionDeTabla(conexion);
        }

        Iterator tablasR = tablasRelaciones.values().iterator();
        while (tablasR.hasNext()) {
            Tabla t = (Tabla) tablasR.next();
            sqlHTML += t.codigoHTMLCreacionDeTabla(conexion);
        }

        String tablasEntidad = "";
        String tablasEntidadHTML = "";

        Iterator tablasE = tablasEntidades.values().iterator();
        while (tablasE.hasNext()) {
            Tabla t = (Tabla) tablasE.next();
            if (esPadreEnIsa(t)) {
                tablasEntidadHTML = t.codigoHTMLCreacionDeTabla(conexion) + tablasEntidadHTML;
                tablasEntidad = t.codigoEstandarCreacionDeTabla(conexion) + tablasEntidad;
            } else {
                tablasEntidadHTML += t.codigoHTMLCreacionDeTabla(conexion);
                tablasEntidad += t.codigoEstandarCreacionDeTabla(conexion);
            }
        }
        sqlHTML += tablasEntidadHTML;
        sqlHTML += "<p></p></div>";
    }

    private boolean esPadreEnIsa(Tabla tabla) throws ExceptionAp {
        boolean encontrado = false;
        DAORelaciones daoRelaciones = new DAORelaciones(Config.getPath());
        Vector<TransferRelacion> relaciones = daoRelaciones.ListaDeRelaciones();

        // recorremos las relaciones buscando las isa
        int i = 0;
        while (i < relaciones.size() && !encontrado) {
            TransferRelacion tr = relaciones.elementAt(i);
            if (tr.isIsA()) {
                // Obtener ID del padre
                Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
                int idPadre = veya.firstElement().getEntidad();

                DAOEntidades daoEntidades = new DAOEntidades(Config.getPath());
                TransferEntidad te = new TransferEntidad();
                te.setIdEntidad(idPadre);
                te = daoEntidades.consultarEntidad(te);

                Tabla t = new Tabla(te.getNombre(), te.getListaRestricciones());
                t = t.creaClonSinAmbiguedadNiEspacios();

                encontrado = t.getNombreTabla().equalsIgnoreCase(tabla.getNombreTabla());
            }
            i++;
        }
        return encontrado;
    }

    private void creaEnums(TransferConexion conexion) {
        sqlHTML += "<div class='card'><h2>" + Lenguaje.text(Lenguaje.TYPES_SECTION) + "</h2>";

        Iterator<Enumerado> tablasD = tiposEnumerados.values().iterator();
        while (tablasD.hasNext()) {
            Enumerado e = tablasD.next();
            sqlHTML += e.codigoHTMLCreacionDeEnum(conexion);
        }
        sqlHTML += "<p></p></div>";
    }

    private void ponRestricciones(TransferConexion conexion) {
        sqlHTML += "<div class='card'><h2>" + Lenguaje.text(Lenguaje.CONSTRAINTS_SECTION) + "</h2>";

        Iterator tablasE = tablasEntidades.values().iterator();
        while (tablasE.hasNext()) {
            Tabla t = (Tabla) tablasE.next();
            sqlHTML += t.codigoHTMLRestriccionesDeTabla(conexion);
        }

        // Escribir restricciones de relacion
        Iterator tablasR = tablasRelaciones.values().iterator();
        while (tablasR.hasNext()) {
            Tabla t = (Tabla) tablasR.next();
            sqlHTML += t.codigoHTMLRestriccionesDeTabla(conexion);
        }

        // Escribir restricciones de atributo
        Iterator tablasA = tablasMultivalorados.iterator();
        while (tablasA.hasNext()) {
            Tabla t = (Tabla) tablasA.next();
            sqlHTML += t.codigoHTMLRestriccionesDeTabla(conexion);
        }
        sqlHTML += "<p></p></div>";
    }

    private void ponClaves(TransferConexion conexion) throws ExceptionAp {
        sqlHTML += "<div class='card'><h2>" + Lenguaje.text(Lenguaje.KEYS_SECTION) + "</h2>";

        String restEntidad = "";
        String restEntidadHTML = "";

        Iterator tablasE = tablasEntidades.values().iterator();
        while (tablasE.hasNext()) {
            Tabla t = (Tabla) tablasE.next();
            if (esPadreEnIsa(t)) {
                restEntidadHTML = t.codigoHTMLClavesDeTabla(conexion) + restEntidadHTML;
                restEntidad = t.codigoEstandarClavesDeTabla(conexion) + restEntidad;
            } else {
                restEntidadHTML += t.codigoHTMLClavesDeTabla(conexion);
                restEntidad += t.codigoEstandarClavesDeTabla(conexion);
            }
        }

        sqlHTML += restEntidadHTML;

        Iterator tablasR = tablasRelaciones.values().iterator();
        while (tablasR.hasNext()) {
            Tabla t = (Tabla) tablasR.next();
            sqlHTML += t.codigoHTMLClavesDeTabla(conexion);
        }

        Iterator tablasM = tablasMultivalorados.iterator();
        while (tablasM.hasNext()) {
            Tabla t = (Tabla) tablasM.next();
            sqlHTML += t.codigoHTMLClavesDeTabla(conexion);
        }
        sqlHTML += "<p></p></div>";
    }

    public String restriccionesPerdidas() {
        return restriccionesPerdidas.toString();
    }

    public String restriccionesIR() {
        String mr = "";
        mr += generaIR(tablasEntidades.values().iterator());
        mr += generaIR(tablasRelaciones.values().iterator());
        mr += generaIR(tablasMultivalorados.iterator());
        return mr;
    }

    /*
     * Genera las restricciones IR dado un iterador de tabla
     * */
    private String generaIR(Iterator<Tabla> tabla) {
        String code = "";
        while (tabla.hasNext()) {
            Tabla t = (Tabla) tabla.next();
            Vector<String[]> foreigns = t.getForeigns();
            boolean abierto = false;
            String claves = "", valores = "";
            for (int j = 0; j < foreigns.size(); j++) {
                if (!abierto) {
                    code += "<p>";
                    abierto = true;
                }
                if (t.estaRepe(foreigns.elementAt(j)[0], t.getAtributos())) {
                    claves += t.getNombreTabla() + "." + foreigns.elementAt(j)[3] + "_" + foreigns.elementAt(j)[0];
                } else
                    claves += t.getNombreTabla() + "." + foreigns.elementAt(j)[0];

                valores += foreigns.elementAt(j)[2];
                if (foreigns.size() - j > 1) {
                    if (foreigns.elementAt(j + 1)[3] != foreigns.elementAt(j)[3] || foreigns.elementAt(j + 1)[2].equals(foreigns.elementAt(j)[2])) {
                        code += claves + " -> " + valores + "</p>";
                        abierto = false;
                        claves = "";
                        valores = "";
                    } else {
                        claves += ", ";
                        valores += ", ";
                    }
                } else {
                    code += claves + " -> " + valores + "</p>";
                    abierto = false;
                    claves = "";
                    valores = "";
                }
            }
        }
        return code;
    }

    public Contexto generaModeloRelacional() throws ExceptionAp {
        reset();
        StringBuilder warnings = new StringBuilder();
        Contexto aux = validadorBD.validaBaseDeDatos(true, warnings);
        if (!aux.isExito()) return new Contexto(false, null, aux.getDatos());
        restriccionesPerdidas = new RestriccionesPerdidas();
        generaTablasAgregaciones(); //creamos primero las de las agregaciones porque ahi se impide que se creen las tablas de sus relaciones internas
        generaTablasEntidades();
        generaTablasRelaciones();
        mr = warnings.toString();
        mr += "<div class='card'><h2>" + Lenguaje.text(Lenguaje.RELATIONS) + "</h2>";
        Iterator tablasE = tablasEntidades.values().iterator();
        while (tablasE.hasNext()) {
            Tabla t = (Tabla) tablasE.next();
            mr += t.modeloRelacionalDeTabla(true);
        }

        Iterator tablasR = tablasRelaciones.values().iterator();
        while (tablasR.hasNext()) {
            Tabla t = (Tabla) tablasR.next();
            mr += t.modeloRelacionalDeTabla(true);
        }

        Iterator tablasA = tablasAgregaciones.values().iterator();
        while (tablasA.hasNext()) {
            Tabla t = (Tabla) tablasR.next();
            mr += t.modeloRelacionalDeTabla(true);
        }

        Iterator tablasM = tablasMultivalorados.iterator();
        while (tablasM.hasNext()) {
            Tabla t = (Tabla) tablasM.next();
            mr += t.modeloRelacionalDeTabla(true);
        }
        mr += "<p></p></div><div class='card'><h2>" + Lenguaje.text(Lenguaje.RIC) + "</h2>";
        mr += restriccionesIR();
        mr += "<p></p></div><div class='card'><h2>" + Lenguaje.text(Lenguaje.LOST_CONSTR) + "</h2>";
        mr += restriccionesPerdidas();
        mr += "<p></p></div>";
        
        return new Contexto(true, TC.SS_GeneracionModeloRelacional, mr);
    }

    //metodos auxiliares.

    /**
     * Devuelve la lista de atributos con sus caracteristicas de manera recursiva profundizando en los atributos compuestos.
     *
     * @param ta            El atributo compuesto a tratar.
     * @param nombreEntidad el nombre de la entidad de la que proviene.
     * @param procedencia   es la cadena de nombres de los atributos padre.
     */
    private Vector<String[]> atributoCompuesto(TransferAtributo ta, String nombreEntidad, String procedencia) throws ExceptionAp {
        Vector<TransferAtributo> subs = this.dameAtributosEnTransfer(ta.getListaComponentes());
        Vector<String[]> lista = new Vector<String[]>();

        for (int i = 0; i < subs.size(); i++) {
            TransferAtributo aux = subs.elementAt(i);
            if (aux.getCompuesto()) {
                //caso recursivo
                String p = "";
                if (procedencia != "") p = procedencia + ta.getNombre() + "_";
                else p = ta.getNombre() + "_";
                lista.addAll((Collection) this.atributoCompuesto(aux, nombreEntidad, p));
            } else {
                //caso base
                String[] trio = new String[3];
                trio[0] = procedencia + ta.getNombre() + "_" + aux.getNombre();
                trio[1] = aux.getDominio();
                trio[2] = nombreEntidad;
                lista.add(trio);
            }
        }
        return lista;
    }

    /**
     * @param ta        El atributo multivalorado en cuestion
     * @param idEntidad El identificador de la entidad a la que pertenece.
     */
    private void atributoMultivalorado(TransferAtributo ta, int idEntidad) throws ExceptionAp {
        // sacamos la tabla de la entidad propietaria del atributo.
        Tabla tablaEntidad = tablasEntidades.get(idEntidad);

        //creamos la tabla.
        Tabla tablaMulti = new Tabla(tablaEntidad.getNombreTabla() + "_" + ta.getNombre(), ta.getListaRestricciones());

        // aniadimos el campo del atributo, incluso teniendo en cuenta que sea
        // compuesto.
        if (ta.getCompuesto())
            tablaMulti.aniadeListaAtributos(this.atributoCompuesto(ta,
                    tablaEntidad.getNombreTabla(), ""), ta.getListaRestricciones(), tiposEnumerados);
        else tablaMulti.aniadeAtributo(ta.getNombre(), ta.getDominio(),
                tablaEntidad.getNombreTabla(), tiposEnumerados, ta.getListaRestricciones(), ta.getUnique(), ta.getNotnull());
        tablaMulti.aniadeListaAtributos(tablaEntidad.getPrimaries(), ta.getListaRestricciones(), tiposEnumerados);

        Vector<String[]> clavesEntidad = tablaEntidad.getPrimaries();
        String[] referenciadas = new String[clavesEntidad.size()];
        for (int q = 0; q < clavesEntidad.size(); q++) referenciadas[q] = clavesEntidad.get(q)[0];

        tablaMulti.aniadeListaClavesForaneas(tablaEntidad.getPrimaries(), tablaEntidad.getNombreTabla(), referenciadas);
        tablaMulti.aniadeListaClavesPrimarias(tablaMulti.getAtributos());
        tablasMultivalorados.add(tablaMulti);
        for (String rest : (Vector<String>) ta.getListaRestricciones())
            restriccionesPerdidas.add(new RestriccionPerdida(tablaMulti.getNombreTabla(), rest, RestriccionPerdida.TABLA));
    }

    protected int objectToInt(Object ob) {
        return Integer.parseInt((String) ob);
    }

    protected Vector<TransferAtributo> dameAtributosEnTransfer(Vector sinParam) throws ExceptionAp {
        DAOAtributos daoAtributos = new DAOAtributos();
        Vector<TransferAtributo> claves = new Vector<TransferAtributo>();
        TransferAtributo aux = new TransferAtributo();
        for (int i = 0; i < sinParam.size(); i++) {
            aux.setIdAtributo(this.objectToInt(sinParam.elementAt(i)));
            aux = daoAtributos.consultarAtributo(aux);
            claves.add(aux);
        }
        return claves;
    }

    public void compruebaConexion(TransferConexion tc) throws ExceptionAp{
        System.out.println("Datos de conexion a la base de datos");
        System.out.println("------------------------------------");
        System.out.println("DBMS: " + tc.getRuta() + "(" + tc.getTipoConexion() + ")");
        System.out.println("Usuario: " + tc.getUsuario());
        System.out.println("Intentando conectar...");
        ConectorDBMS conector = FactoriaConectores.obtenerConector(tc.getTipoConexion());
        
        try {
            conector.abrirConexion(tc.getRuta(), tc.getUsuario(), tc.getPassword());
            conector.cerrarConexion();
        } catch (SQLException e) {
        	throw new ExceptionAp(TC.NO_DB_CONEXION, ": \n" + e.getMessage());
        }
        
        return;
    }
}