package persistencia;

import excepciones.ExceptionAp;
import modelo.transfers.TipoDominio;
import modelo.transfers.TransferDominio;
import org.w3c.dom.*;

import java.util.Vector;

@SuppressWarnings("rawtypes")
public class DAODominios extends DAO{

    // Atributos
    private Document doc;

    // Constructora del DAO
    public DAODominios(String path)throws ExceptionAp {
    	super(path);
        this.doc = dameDoc();
    }

    // Metodos del DAODominios
    public int anadirDominio(TransferDominio tc) throws ExceptionAp{
        // Resultado que se devolvera
        int resultado = 0;
        //sacamos la <ListaDominios>
        NodeList LC = doc.getElementsByTagName("DomainList");
        // Sacamos el nodo
        Node listado = LC.item(0);
        //nuevo id.
        int proximoID = dameIdDominio(listado);
        //this.dameEntidad(doc);
        listado.getAttributes().item(0).setNodeValue(Integer.toString(proximoID + 1));
        Node ListaDominios = LC.item(0);
        // Ya estamos situados
        Element raiz = doc.createElement("Domain");
        raiz.setAttribute("DomainId", Integer.toString(proximoID));
        // Nombre
        Element elem = doc.createElement("Name");
        elem.appendChild(doc.createTextNode(tc.getNombre()));
        raiz.appendChild(elem);
        // Dominio
        elem = doc.createElement("BaseType");
        elem.appendChild(doc.createTextNode(tc.getTipoBase().toString()));
        raiz.appendChild(elem);
        // ListaComponentes
        Element raizListaValores = doc.createElement("ValueList");
        raiz.appendChild(raizListaValores);
        //estamos en la lista de componentes.
        for (int cont = 0; cont < tc.getListaValores().size(); cont++) {
            Element valor = doc.createElement("Value");
            valor.appendChild(doc.createTextNode(tc.getListaValores().elementAt(cont).toString()));
            raizListaValores.appendChild(valor);
        }

        // Lo añadimos a la lista de Dominios
        ListaDominios.appendChild(raiz);
        // Actualizamos el resultado
        resultado = proximoID;
        // Guardamos los cambios en el fichero xml y controlamos la excepcion
        this.guardaDoc(doc);

        // Devolvemos el resultado de la operacion
        return resultado;
    }

    public TransferDominio consultarDominio(TransferDominio tc) {
        // Entidad que devolveremos
        TransferDominio transfer = null;
        // Obtenemos el nodo del Entidad
        Node nodoDominioBuscado = dameNodoDominio(tc.getIdDominio());
        // Lo transformamos a Dominio si es distinto de null
        if (nodoDominioBuscado != null)
            transfer = nodoDominioATransferDominio(nodoDominioBuscado);
        // Lo devolvemos
        return transfer;
    }

    public boolean modificarDominio(TransferDominio tc) throws ExceptionAp {
        // Resultado que devolveremos
        boolean respuesta = true;
        // Obtenemos el Dominio
        Node DominioBuscado = dameNodoDominio(tc.getIdDominio());
        if (DominioBuscado != null) {
            // Cambiamos los datos del Dominio
            ponValorAElemento(dameNodoPedidoDeDominio(DominioBuscado, "Name"),
                    tc.getNombre());
            ponValorAElemento(dameNodoPedidoDeDominio(DominioBuscado, "BaseType"),
                    (tc.getTipoBase()).toString());
            //--------------------------------------
            Node listaV = dameNodoPedidoDeDominio(DominioBuscado, "ValueList");
            int i = 0;
            Node n;
            while (i < listaV.getChildNodes().getLength()) {
                n = this.dameNodoPedidoDeDominio(listaV, "Value");
                if (n != null) listaV.removeChild(n);
                i++;
            }

            for (int cont = 0; cont < tc.getListaValores().size(); cont++) {
                Element Valor = doc.createElement("Value");
                Valor.appendChild(doc.createTextNode(tc.getListaValores().elementAt(cont).toString()));
                listaV.appendChild(Valor);
            }

        } else
            respuesta = false;
        // Guardamos los cambios en el fichero xml y controlamos la excepcion
        this.guardaDoc(doc);

        // Devolvemos la respuesta
        return respuesta;
    }

    public boolean borrarDominio(TransferDominio tc) throws ExceptionAp{
        Node DominioBuscado = dameNodoDominio(tc.getIdDominio());
        NodeList LC = doc.getElementsByTagName("DomainList");
        // Sacamos el nodo
        Node raiz = LC.item(0);
        boolean borrado = false;
        if ((DominioBuscado != null) && (raiz != null)) {
            raiz.removeChild(DominioBuscado);
            borrado = true;
        }
        this.guardaDoc(doc);

        return borrado;
    }

    public Vector<TransferDominio> ListaDeDominios() {
        // Vector que devolveremos
        Vector<TransferDominio> vectorDeTransfers = new Vector<TransferDominio>();
        TransferDominio td = new TransferDominio();
        TransferDominio aux = new TransferDominio();
        // Obtenemos los Dominios y los vamos anadiendo
        NodeList lista = doc.getElementsByTagName("Domain");
        int numDominios = lista.getLength(); //vemos cuantos dominios hay en el XML
        String id;

        for (int i = 0; i < numDominios; i++) {
            //obtenemos el ID de cada dominio, la consultamos y la metemos en el vector.
            id = lista.item(i).getAttributes().item(0).getNodeValue();
            aux.setIdDominio(Integer.parseInt(id));
            td = this.consultarDominio(aux);
            vectorDeTransfers.add(td);
        }
        //devolvemos los dominios con todos sus datos.
        return vectorDeTransfers;
    }

    // Metodos privados

    private Node dameNodoDominio(int id) {
        Node DominioBuscado = null;
        NodeList LC = doc.getElementsByTagName("Domain");
        // Obtener el Entidad id de un nodo Entidad
        boolean encontrado = false;
        int cont = 0;
        while ((!encontrado) && (cont < LC.getLength())) {
            // Obtenemos el nodo
            Node nodoDominioActual = LC.item(cont);
            // Obtenemos sus Dominios
            NamedNodeMap DominiosNodoDominioActual = nodoDominioActual
                    .getAttributes();
            // Obtenemos el id del Dominio
            int idNodoDominioActual = Integer
                    .parseInt(DominiosNodoDominioActual.item(0).getNodeValue());
            // Comparamos
            if (id == idNodoDominioActual) {
                encontrado = true;
                DominioBuscado = nodoDominioActual;
            } else
                cont++;
        }
        return DominioBuscado;
    }

    private TransferDominio nodoDominioATransferDominio(Node nodo) {
        int id = dameIdDominio(nodo);
        String nombre = dameValorDelElemento(dameNodoPedidoDeDominio(nodo, "Name"));
        TipoDominio tipoBase = TipoDominio.valueOf(dameValorDelElemento(dameNodoPedidoDeDominio(nodo, "BaseType")));
        Vector listaValores = nodoListaAObjetoLista(dameNodoPedidoDeDominio(nodo, "ValueList"), "Value");

        // Creamos el transfer
        TransferDominio transfer = new TransferDominio();
        transfer.setIdDominio(id);
        transfer.setNombre(nombre);
        transfer.setTipoBase(tipoBase);
        transfer.setListaValores(listaValores);

        // Lo devolvemos
        return transfer;
    }

    private Vector nodoListaAObjetoLista(Node nodo, String tipoLista) {
        //tipoLista puede ser : Atributo y clavePrimaria
        //Resultado que devolveremos
        Vector<String> lista = new Vector<String>();
        // Sacamos la lista de hijos
        NodeList LD = nodo.getChildNodes();
        // Buscamos en la lista los nodos del tipo que queremos (tipolista)
        int cont = 0;
        while (cont < LD.getLength()) {
            Node aux = LD.item(cont);
            if (aux.getNodeName() == tipoLista) {
                lista.addElement(dameValorDelElemento(aux));
            }
            cont++;
        }
        // Devolvemos la lista
        return lista;
    }

    private String dameValorDelElemento(Node elemento) {
        return elemento.getFirstChild().getNodeValue().toString();
    }

    private void ponValorAElemento(Node elemento, String clavePrimaria) {
        elemento.getFirstChild().setNodeValue(clavePrimaria);
    }

    private int dameIdDominio(Node Dominio) {
        return Integer.parseInt(Dominio.getAttributes().item(0).getNodeValue());
    }

    private Node dameNodoPedidoDeDominio(Node Dominio, String elemento) {
        Node nodoBuscado = null;
        // Generamos los hijos de Dominio
        NodeList hijos = Dominio.getChildNodes();
        // Buscamos hasta encontar un elemento "elemento"
        int cont = 0;
        Node aux = hijos.item(0);
        if (aux != null)
            while (aux.getNodeName() != (elemento) && (cont < hijos.getLength())) {
                aux = hijos.item(cont++);
            }
        // Ya tenemos en aux el nodoBuscado
        nodoBuscado = aux;
        // Lo devolvemos
        return nodoBuscado;
    }
}
