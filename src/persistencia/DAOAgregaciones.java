package persistencia;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import modelo.transfers.TransferAgregacion;
import vista.lenguaje.Lenguaje;

public class DAOAgregaciones {

	// Atributos
		private Document doc;
		private String path;
		
		// Constructora del DAO
		public DAOAgregaciones(String path){
			this.path = path;
			//this.path += "\\persistencia.xml";
			this.path = this.path.replace(" ", "%20");
			this.path = this.path.replace('\\', '/');
			this.doc = dameDoc();
		}
		
		// Metodos para el tratamiento del fichero xml
		private Document dameDoc() {
			Document doc = null;
			DocumentBuilder parser = null;
			try {
		        
				DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
				parser = factoria.newDocumentBuilder();
				doc = parser.parse(this.path);
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(
						null,
						Lenguaje.text(Lenguaje.ERROR)+":\n" +
						Lenguaje.text(Lenguaje.UNESPECTED_XML_ERROR)+" \""+ path+".xml\"",
						Lenguaje.text(Lenguaje.DBCASE),
						JOptionPane.ERROR_MESSAGE);
			}
			return doc;
		}

		private void guardaDoc() {
			OutputFormat formato = new OutputFormat(doc, "UTF-8", true);
			StringWriter s = new StringWriter();
			XMLSerializer ser = new XMLSerializer(s, formato);
			try {
				ser.serialize(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// El FileWriter necesita espacios en la ruta
			this.path = this.path.replace("%20"," ");
			FileWriter f = null;
			/*debido a que la funcion FileWriter da un error de acceso
			 * de vez en cuando, forzamos su ejecucion hasta que funcione correctamente*/
			boolean centinela=true;
			while (centinela==true){
				try {
					f = new FileWriter(this.path);
					centinela =false;
				} catch (IOException e) {
					centinela=true;
				}
			}
			this.path = this.path.replace(" ", "%20");
			ser = new XMLSerializer(f, formato);
			try {
				ser.serialize(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void apilarDeshacer(Stack<Document>pilaDeshacer) {
			pilaDeshacer.push(doc);
		}
		
		
		// Metodos del DAOCllientes
		public int anadirAgregacion(TransferAgregacion tc) {//tr.getTipo().equalsIgnoreCase("Normal")
			// Resultado que se devolvera
			int resultado = 0;
			// Sacamos la <ListaAgregaciones>
			NodeList LC = doc.getElementsByTagName("AggregationList");
			// Sacamos el nodo
			Node listado = LC.item(0);
			//nueva Id de la relacion.
			int proximoID =dameIdAgregacion(listado); 
				//this.dameRelacion(doc);
			listado.getAttributes().item(0).setNodeValue(Integer.toString(proximoID+1));
			Node ListaAgregaciones = LC.item(0);
			// Ya estamos situados
			Element raiz = doc.createElement("Aggregation");
			raiz.setAttribute("idAgregacion",Integer.toString(proximoID));
			// Nombre
			Element elem = doc.createElement("Name");
			elem.appendChild(doc.createTextNode(tc.getNombre()));
			raiz.appendChild(elem);
			// ListaRelaciones
			Element raizListaRelaciones = doc.createElement("RelatnList");
			raiz.appendChild(raizListaRelaciones);
			//estamos en la lista de relaciones.
			for (int cont = 0; cont <tc.getListaRelaciones().size(); cont++) {
				Element Relacion = doc.createElement("Relatn");
				Relacion.appendChild(doc.createTextNode(tc.getListaRelaciones().elementAt(cont).toString()));
				raizListaRelaciones.appendChild(Relacion);
			}
			// ListaAtributos
			Element raizListaAtributos = doc.createElement("AttribList");
			raiz.appendChild(raizListaAtributos);
			//estamos en la lista de Atributos.
			for (int cont = 0; cont <tc.getListaAtributos().size(); cont++) {
				Element cp = doc.createElement("Attrib");
				cp.appendChild(doc.createTextNode(tc.getListaAtributos().elementAt(cont).toString()));
				raizListaAtributos.appendChild(cp);
			}
			/*posicion   tenemos que ver como calcular la posicion
			elem = doc.createElement("Position");
			System.out.println(tc.getNombre());
			elem.appendChild(doc.createTextNode((int)(tc.getPosicion().getX())+","+(int)(tc.getPosicion().getY())));
			raiz.appendChild(elem);*/
			
			// Lo añadimos a la lista de Relaciones
			ListaAgregaciones.appendChild(raiz);
			// Actualizamos el resultado
			resultado = proximoID;
			// Guardamos los cambios en el fichero xml y controlamos la excepcion
			this.guardaDoc();
			// Devolvemos el resultado de la operacion
			return resultado;
		}

		public TransferAgregacion consultarAgregacion(TransferAgregacion tc) {
			// Agregacion que devolveremos
			TransferAgregacion transfer = null;
			// Obtenemos el nodo de la Agregacion
			Node nodoBuscado = dameNodoAgregacion(tc.getIdAgregacion());
			// Lo transformamos a agregacion si es distinto de null
			if (nodoBuscado != null)
				transfer = nodoAgregacionATransferAgregacion(nodoBuscado);
			// Lo devolvemos
			return transfer;
		}

		public boolean modificarAgregacion(TransferAgregacion tc) {
			// Resultado que devolveremos
			boolean respuesta = true;
			// Obtenemos el Relacion
			Node nodo = dameNodoAgregacion(tc.getIdAgregacion());
			if (nodo != null) {
				
				ponValorAElemento(
						dameNodoPedidoDeAgregacion(nodo, "Name"), tc
								.getNombre());
							
				//--------------------------------------
				Node listaC=dameNodoPedidoDeAgregacion(nodo,"RelatnList");
				int i=0;
				Node n;
				while (i<listaC.getChildNodes().getLength()){
					n=this.dameNodoPedidoDeAgregacion(listaC,"Relatn");
					if (n!=null)listaC.removeChild(n);
					i++;
				}
				
				for (int cont = 0; cont <tc.getListaRelaciones().size(); cont++) {
					Element Relacion = doc.createElement("Relatn");
					Relacion.appendChild(doc.createTextNode(tc.getListaRelaciones().elementAt(cont).toString()));
					listaC.appendChild(Relacion);		
				}
				
				//---------------------------
				Node listaV=dameNodoPedidoDeAgregacion(nodo,"AttribList");
				int j=0;
				while (j<listaV.getChildNodes().getLength()){
					n=this.dameNodoPedidoDeAgregacion(listaV,"Attrib");
					if (n!=null)listaV.removeChild(n);
					j++;
				}
				
				for (int cont = 0; cont <tc.getListaAtributos().size(); cont++) {
						Element clavePrimaria = doc.createElement("Attrib");
						clavePrimaria.appendChild(doc.createTextNode(tc.getListaAtributos().elementAt(cont).toString()));
						listaV.appendChild(clavePrimaria);			
				}
				//---------------------------
				
			} else respuesta = false;
			// Guardamos los cambios en el fichero xml y controlamos la excepcion
			this.guardaDoc();
			return respuesta;
		}
		

		public boolean borrarAgregacion(TransferAgregacion tc) {
			Node nodo = dameNodoAgregacion(tc.getIdAgregacion());
			NodeList LC = doc.getElementsByTagName("AggregationList");
			// Sacamos el nodo
			Node raiz = LC.item(0); 
			boolean borrado=false;
				if ((nodo != null) && (raiz!=null)){
					raiz.removeChild(nodo);
					borrado=true;
				}
				this.guardaDoc();
				
			return borrado;
		}
		
		public Vector<TransferAgregacion> ListaDeAgregaciones() {
			// Vector que devolveremos
			Vector<TransferAgregacion> vectorDeTransfers = new Vector<TransferAgregacion>();
			TransferAgregacion tr= new TransferAgregacion();
			TransferAgregacion aux= new TransferAgregacion();
			// Obtenemos las agregaciones y las vamos anadiendo
			NodeList lista=doc.getElementsByTagName("Aggregation");
			int numAgregaciones = lista.getLength(); //vemos cuantas agregaciones hay en el XML
			String id;

			for (int i=0;i<numAgregaciones;i++){
				//obtenemos el ID de cada agregacion, la consultamos y la metemos en el vector.
				id= lista.item(i).getAttributes().item(0).getNodeValue();
				aux.setIdAgregacion(Integer.parseInt(id));
				tr=this.consultarAgregacion(aux);
				vectorDeTransfers.add(tr);
			}
			//devolvemos las relaciones con todos sus datos.
			return vectorDeTransfers;
		}
		
		
		// Metodos privados

		private Node dameNodoAgregacion(int id) {
			Node buscado = null;
			NodeList LC = doc.getElementsByTagName("Aggregation");
			// Obtener la agregacion id de un nodo agregacion
			boolean encontrado = false;
			int cont = 0;
			while ((!encontrado) && (cont < LC.getLength())) {
				// Obtenemos el nodo actual
				Node nodoActual = LC.item(cont);
				// Obtenemos sus atributos
				NamedNodeMap agregacionesNodoAgregacionActual = nodoActual
						.getAttributes();
				// Obtenemos el id de la agregacion
				int idNodoActual = Integer.parseInt(agregacionesNodoAgregacionActual.item(0).getNodeValue());
				// Comparamos
				if (id == idNodoActual) {
					encontrado = true;
					buscado = nodoActual;
				} else cont++;
			}
			return buscado;
		}

		private TransferAgregacion nodoAgregacionATransferAgregacion(Node nodo) {
			int id = dameIdAgregacion(nodo);
			String nombre = dameValorDelElemento(dameNodoPedidoDeAgregacion(nodo,"Name"));
			Vector listaC = nodoListaAObjetoLista(dameNodoPedidoDeAgregacion(nodo,"RelatnList"), "Relatn");
			Vector listaV = nodoListaAObjetoLista(dameNodoPedidoDeAgregacion(nodo,"AttribList"), "Attrib");
			//Point2D posicion=this.damePunto(dameValorDelElemento(dameNodoPedidoDeAgregacion(nodo,"Position")));
			
			// Creamos el transfer
			TransferAgregacion transfer = new TransferAgregacion();
			transfer.setIdAgregacion(id);
			transfer.setNombre(nombre);
			transfer.setListaRelaciones(listaC);
			transfer.setListaAtributos(listaV);
			//transfer.setPosicion(posicion);
			return transfer;
		}

		@SuppressWarnings("unchecked")
		private Vector nodoListaAObjetoLista(Node nodo,String tipoLista){
			//Resultado que devolveremos
			Vector<String> lista = new Vector<String>();
			// Sacamos la lista de hijos
			NodeList LD = nodo.getChildNodes();
			// Buscamos en la lista los nodos del tipo que queremos (tipolista)
			int cont = 0;
			while(cont<LD.getLength()){
				Node aux = LD.item(cont);
				if (aux.getNodeName()==tipoLista && aux.hasChildNodes()){
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
			if(elemento.getNodeName()!= "Role")
				elemento.getFirstChild().setNodeValue(clavePrimaria);
			else elemento.setNodeValue(clavePrimaria);
		}

		private int dameIdAgregacion(Node aggreg) {
			return Integer.parseInt(aggreg.getAttributes().item(0).getNodeValue());
		}

		private Node dameNodoPedidoDeAgregacion(Node Relacion, String elemento) {
			Node nodoBuscado = null;
			// Generamos los hijos de Relacion
			NodeList hijos = Relacion.getChildNodes();
			// Buscamos hasta encontar un elemento "elemento"
			int cont = 0;
			Node aux = hijos.item(0);
			if (aux!=null)
			while ((aux.getNodeName() != (elemento))&&(cont<hijos.getLength()))
				aux = hijos.item(cont++);
			// Ya tenemos en aux el nodoBuscado
			nodoBuscado = aux;
			// Lo devolvemos
			return nodoBuscado;
		}
		
		private Point2D damePunto(String posicion){
			Point2D p = new Point2D.Double();
			String primero,segundo;
			int coma =posicion.indexOf(",");
			primero = posicion.substring(0,coma);
			segundo = posicion.substring(coma+1, posicion.length());
			p.setLocation(Double.parseDouble(primero),Double.parseDouble(segundo));
			return p;
		}
}
