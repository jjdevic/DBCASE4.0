package persistencia;

import excepciones.ExceptionAp;
import modelo.transfers.EntidadYAridad;
import modelo.transfers.TransferRelacion;
import org.w3c.dom.*;

import java.awt.geom.Point2D;
import java.util.Vector;

@SuppressWarnings("rawtypes")
public class DAORelaciones extends DAO {

    // Atributos
    private Document doc;

    // Constructora del DAO
    public DAORelaciones(String path) throws ExceptionAp {
    	super(path);
        this.doc = dameDoc();
    }

    // Metodos del DAOCllientes
    public int anadirRelacion(TransferRelacion tc) throws ExceptionAp {//tr.getTipo().equalsIgnoreCase("Normal")
        // Resultado que se devolvera
        int resultado = 0;
        // Sacamos la <ListaRelaciones>
        NodeList LC = doc.getElementsByTagName("RelationList");
        // Sacamos el nodo
        Node listado = LC.item(0);
        //nueva Id de la relacion.
        int proximoID = dameRelacion(listado);
        //this.dameRelacion(doc);
        listado.getAttributes().item(0).setNodeValue(Integer.toString(proximoID + 1));
        Node ListaRelaciones = LC.item(0);
        // Ya estamos situados
        Element raiz = doc.createElement("Relation");
        raiz.setAttribute("idRelacion", Integer.toString(proximoID));
        // Nombre
        Element elem = doc.createElement("Name");
        elem.appendChild(doc.createTextNode(tc.getNombre()));
        raiz.appendChild(elem);
        // Tipo
        elem = doc.createElement("Type");
        elem.appendChild(doc.createTextNode(tc.getTipo()));
        raiz.appendChild(elem);
        // ListaEntidadesYAridades
        Element raizListaEntidadesYAridades = doc.createElement("EntityAndArityList");
        raiz.appendChild(raizListaEntidadesYAridades);
        //estamos en la lista de Atributos.
        for (int cont = 0; cont < tc.getListaEntidadesYAridades().size(); cont++) {
            Element Atributo = doc.createElement("EntityAndArity");
            EntidadYAridad e = (EntidadYAridad) tc.getListaEntidadesYAridades().elementAt(cont);
            Atributo.appendChild(doc.createTextNode(e.hazCadenaChachi()));
            raizListaEntidadesYAridades.appendChild(Atributo);
        }
        // ListaAtributos
        Element raizListaAtributos = doc.createElement("AttribList");
        raiz.appendChild(raizListaAtributos);
        //estamos en la lista de Atributos.
        for (int cont = 0; cont < tc.getListaAtributos().size(); cont++) {
            Element cp = doc.createElement("Attrib");
            cp.appendChild(doc.createTextNode(tc.getListaAtributos().elementAt(cont).toString()));
            raizListaAtributos.appendChild(cp);
        }
        if (!tc.isIsA()) {
            // ListaRestricciones
            Element raizListaRestricciones = doc.createElement("AssertionList");
            raiz.appendChild(raizListaRestricciones);

            for (int cont = 0; cont < tc.getListaRestricciones().size(); cont++) {
                Element cp = doc.createElement("Assertion");
                cp.appendChild(doc.createTextNode(tc.getListaRestricciones().elementAt(cont).toString()));
                raizListaRestricciones.appendChild(cp);
            }
            // ListaUniques
            Element raizListaUniques = doc.createElement("UniqueList");
            raiz.appendChild(raizListaUniques);
            //estamos en la lista de Uniques.
            for (int cont = 0; cont < tc.getListaUniques().size(); cont++) {
                Element cp = doc.createElement("Uniques");
                cp.appendChild(doc.createTextNode(tc.getListaUniques().elementAt(cont).toString()));
                raizListaUniques.appendChild(cp);
            }
        }
        //posicion
        elem = doc.createElement("Position");
        //System.out.println(tc.getNombre());
        elem.appendChild(doc.createTextNode((int) (tc.getPosicion().getX()) + "," + (int) (tc.getPosicion().getY())));
        raiz.appendChild(elem);

        // Lo añadimos a la lista de Relaciones
        ListaRelaciones.appendChild(raiz);
        // Actualizamos el resultado
        resultado = proximoID;
        // Guardamos los cambios en el fichero xml y controlamos la excepcion
        this.guardaDoc(doc);
        // Devolvemos el resultado de la operacion
        return resultado;
    }

    public TransferRelacion consultarRelacion(TransferRelacion tc) {
        // Relacion que devolveremos
        TransferRelacion transfer = null;
        // Obtenemos el nodo del Relacion
        Node nodoRelacionBuscado = dameNodoRelacion(tc.getIdRelacion());
        // Lo transformamos a Relacion si es distinto de null
        if (nodoRelacionBuscado != null)
            transfer = nodoRelacionATransferRelacion(nodoRelacionBuscado);
        // Lo devolvemos
        return transfer;
    }

    public boolean modificarRelacion(TransferRelacion tc) throws ExceptionAp {
        // Resultado que devolveremos
        boolean respuesta = true;
        // Obtenemos el Relacion
        Node RelacionBuscado = dameNodoRelacion(tc.getIdRelacion());
        if (RelacionBuscado != null) {
            // Cambiamos los datos del Relacion
            ponValorAElemento(dameNodoPedidoDeRelacion(RelacionBuscado, "Name"), tc.getNombre());
            ponValorAElemento(dameNodoPedidoDeRelacion(RelacionBuscado, "Type"), tc.getTipo());
            ponValorAElemento(dameNodoPedidoDeRelacion(RelacionBuscado,
                    "Position"), ((int) (tc.getPosicion().getX()) + "," + (int) (tc.getPosicion().getY())));

            //--------------------------------------
            Node listaC = dameNodoPedidoDeRelacion(RelacionBuscado, "EntityAndArityList");
            int i = 0;
            Node n;
            while (i < listaC.getChildNodes().getLength()) {
                n = this.dameNodoPedidoDeRelacion(listaC, "EntityAndArity");
                if (n != null) listaC.removeChild(n);
                i++;
            }

            for (int cont = 0; cont < tc.getListaEntidadesYAridades().size(); cont++) {
                Element Atributo = doc.createElement("EntityAndArity");
                EntidadYAridad e = (EntidadYAridad) tc.getListaEntidadesYAridades().elementAt(cont);
                Atributo.appendChild(doc.createTextNode(e.hazCadenaChachi()));
                listaC.appendChild(Atributo);
            }


            Node listaV = dameNodoPedidoDeRelacion(RelacionBuscado, "AttribList");
            int j = 0;
            while (j < listaV.getChildNodes().getLength()) {
                n = this.dameNodoPedidoDeRelacion(listaV, "Attrib");
                if (n != null) listaV.removeChild(n);
                j++;
            }

            for (int cont = 0; cont < tc.getListaAtributos().size(); cont++) {
                Element clavePrimaria = doc.createElement("Attrib");
                clavePrimaria.appendChild(doc.createTextNode(tc.getListaAtributos().elementAt(cont).toString()));
                listaV.appendChild(clavePrimaria);
            }
            //---------------------------
            Node listaR = dameNodoPedidoDeRelacion(RelacionBuscado, "AssertionList");
            int k = 0;
            while (k < listaR.getChildNodes().getLength()) {
                n = this.dameNodoPedidoDeRelacion(listaR, "Assertion");
                if (n != null) listaR.removeChild(n);
                k++;
            }
            if (!tc.isIsA()) {
                for (int cont = 0; cont < tc.getListaRestricciones().size(); cont++) {
                    Element restriccion = doc.createElement("Assertion");
                    restriccion.appendChild(doc.createTextNode(tc.getListaRestricciones().elementAt(cont).toString()));
                    listaR.appendChild(restriccion);
                }

                Node listaU = dameNodoPedidoDeRelacion(RelacionBuscado, "UniqueList");
                k = 0;
                while (k < listaU.getChildNodes().getLength()) {
                    n = this.dameNodoPedidoDeRelacion(listaU, "Uniques");
                    if (n != null) listaU.removeChild(n);
                    k++;
                }
                for (int cont = 0; cont < tc.getListaUniques().size(); cont++) {
                    Element unique = doc.createElement("Uniques");
                    unique.appendChild(doc.createTextNode(tc.getListaUniques().elementAt(cont).toString()));
                    listaU.appendChild(unique);
                }
            }
        } else respuesta = false;
        // Guardamos los cambios en el fichero xml y controlamos la excepcion
        this.guardaDoc(doc);
        return respuesta;
    }


    public boolean borrarRelacion(TransferRelacion tc) throws ExceptionAp{
        Node RelacionBuscado = dameNodoRelacion(tc.getIdRelacion());
        NodeList LC = doc.getElementsByTagName("RelationList");
        // Sacamos el nodo
        Node raiz = LC.item(0);
        boolean borrado = false;
        if ((RelacionBuscado != null) && (raiz != null)) {
            raiz.removeChild(RelacionBuscado);
            borrado = true;
        }
        this.guardaDoc(doc);

        return borrado;
    }

    public Vector<TransferRelacion> ListaDeRelaciones() {
        // Vector que devolveremos
        Vector<TransferRelacion> vectorDeTransfers = new Vector<TransferRelacion>();
        TransferRelacion tr = new TransferRelacion();
        TransferRelacion aux = new TransferRelacion();
        // Obtenemos los Relaciones y los vamos anadiendo
        NodeList lista = doc.getElementsByTagName("Relation");
        int numRelaciones = lista.getLength(); //vemos cuantas relaciones hay en el XML
        String id;

        for (int i = 0; i < numRelaciones; i++) {
            //obtenemos el ID de cada relacion, la consultamos y la metemos en el vector.
            id = lista.item(i).getAttributes().item(0).getNodeValue();
            aux.setIdRelacion(Integer.parseInt(id));
            tr = this.consultarRelacion(aux);
            vectorDeTransfers.add(tr);
        }
        //devolvemos las relaciones con todos sus datos.
        return vectorDeTransfers;
    }

    // Metodos privados


    private Node dameNodoRelacion(int id) {
        Node RelacionBuscado = null;
        NodeList LC = doc.getElementsByTagName("Relation");
        // Obtener el Relacion id de un nodo Relacion
        boolean encontrado = false;
        int cont = 0;
        while ((!encontrado) && (cont < LC.getLength())) {
            // Obtenemos el nodo
            Node nodoRelacionActual = LC.item(cont);
            // Obtenemos sus Relaciones
            NamedNodeMap RelacionesNodoRelacionActual = nodoRelacionActual
                    .getAttributes();
            // Obtenemos el id del Relacion
            int idNodoRelacionActual = Integer.parseInt(RelacionesNodoRelacionActual.item(0).getNodeValue());
            // Comparamos
            if (id == idNodoRelacionActual) {
                encontrado = true;
                RelacionBuscado = nodoRelacionActual;
            } else cont++;
        }
        return RelacionBuscado;
    }

    private TransferRelacion nodoRelacionATransferRelacion(Node nodo) {
        int id = dameRelacion(nodo);
        String nombre = dameValorDelElemento(dameNodoPedidoDeRelacion(nodo, "Name"));
        String tipo = dameValorDelElemento(dameNodoPedidoDeRelacion(nodo, "Type"));
        Vector listaC = nodoListaAObjetoLista(dameNodoPedidoDeRelacion(nodo, "EntityAndArityList"), "EntityAndArity");
        Vector listaV = nodoListaAObjetoLista(dameNodoPedidoDeRelacion(nodo, "AttribList"), "Attrib");
        Vector listaR = nodoListaAObjetoLista(dameNodoPedidoDeRelacion(nodo, "AssertionList"), "Assertion");
        Vector listaU = nodoListaAObjetoLista(dameNodoPedidoDeRelacion(nodo, "UniqueList"), "Uniques");
        Point2D posicion = this.damePunto(dameValorDelElemento(dameNodoPedidoDeRelacion(nodo, "Position")));

        // Creamos el transfer
        TransferRelacion transfer = new TransferRelacion();
        transfer.setIdRelacion(id);
        transfer.setNombre(nombre);
        transfer.setTipo(tipo);
        transfer.setListaEntidadesYAridades(listaC);
        transfer.setListaAtributos(listaV);
        transfer.setListaRestricciones(listaR);
        transfer.setListaUniques(listaU);
        transfer.setPosicion(posicion);
        return transfer;
    }

    @SuppressWarnings("unchecked")
    private Vector nodoListaAObjetoLista(Node nodo, String tipoLista) {
        //tipoLista puede ser : Atributo y clavePrimaria
        //Resultado que devolveremos
        Vector lista = new Vector();
        // Sacamos la lista de hijos
        NodeList LD = nodo.getChildNodes();
        // Buscamos en la lista los nodos del tipo que queremos (tipolista)
        int cont = 0;
        while (cont < LD.getLength()) {
            Node aux = LD.item(cont);
            if (aux.getNodeName() == tipoLista) {
                String elem = dameValorDelElemento(aux);
                if (tipoLista == "EntityAndArity") {
                    EntidadYAridad e = new EntidadYAridad();
                    lista.addElement(e.sacaValoresDeString(elem));
                } else lista.addElement(elem);
            }
            cont++;
        }
        // Devolvemos la lista
        return lista;
    }
    
    /**
     * 
     * @param idEntidad
     * @return Vector con los identificadores de los padres de la entidad, vector vacío si no los tiene.
     */
    public Vector<Integer> getPadres(int idEntidad) {
    	Vector<Integer> padres = new Vector<Integer>();
    	Vector<TransferRelacion> relaciones = ListaDeRelaciones();
    	
    	for(TransferRelacion relacion: relaciones) {
    		if(relacion.isIsA()) {
    			Vector<EntidadYAridad> veya = relacion.getListaEntidadesYAridades();
    			
    			if(veya != null && !veya.isEmpty() && veya.size() > 1) {
    				EntidadYAridad eyaPadre = veya.firstElement();
    				
    				//Si no es el padre de la relacion
    				if(eyaPadre.getEntidad() != idEntidad) {
    					//Sabemos que una entidad hija solo puede aparecer una vez en la relacion IsA, luego podemos usar este metodo.
        				EntidadYAridad eyaHijo = relacion.getEntidadYAridad(idEntidad);
        				
        				if(eyaHijo != null) {
        					padres.add(eyaPadre.getEntidad());
        				}
    				}
    			}
    		}
    	}
    	
    	return padres;
    }

    private String dameValorDelElemento(Node elemento) {
        return elemento.getFirstChild().getNodeValue().toString();
    }

    private void ponValorAElemento(Node elemento, String clavePrimaria) {
        if (elemento.getNodeName() != "Role")
            elemento.getFirstChild().setNodeValue(clavePrimaria);
        else elemento.setNodeValue(clavePrimaria);
    }

    private int dameRelacion(Node Relacion) {
        return Integer.parseInt(Relacion.getAttributes().item(0).getNodeValue());
    }

    private Node dameNodoPedidoDeRelacion(Node Relacion, String elemento) {
        Node nodoBuscado = null;
        // Generamos los hijos de Relacion
        NodeList hijos = Relacion.getChildNodes();
        // Buscamos hasta encontar un elemento "elemento"
        int cont = 0;
        Node aux = hijos.item(0);
        if (aux != null)
            while ((aux.getNodeName() != (elemento)) && (cont < hijos.getLength()))
                aux = hijos.item(cont++);
        // Ya tenemos en aux el nodoBuscado
        nodoBuscado = aux;
        // Lo devolvemos
        return nodoBuscado;
    }

    private Point2D damePunto(String posicion) {
        Point2D p = new Point2D.Double();
        String primero, segundo;
        int coma = posicion.indexOf(",");
        primero = posicion.substring(0, coma);
        segundo = posicion.substring(coma + 1, posicion.length());
        p.setLocation(Double.parseDouble(primero), Double.parseDouble(segundo));
        return p;
    }
}