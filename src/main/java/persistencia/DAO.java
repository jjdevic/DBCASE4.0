package persistencia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import vista.Lenguaje;

abstract class DAO {
	
	protected String path;
	protected TransformerFactory transFac;
	
	DAO(String path) {
		this.path = path;
		this.path = this.path.replace(" ", "%20");
        this.path = this.path.replace('\\', '/');
		this.transFac = TransformerFactory.newInstance();
	}

	protected void guardaDoc(Document doc) {
		DOMSource source = new DOMSource(doc);
		try {
			Transformer transformer = transFac.newTransformer();
		
			// El FileWriter necesita espacios en la ruta
	        String path_fw = path.replace("%20", " ");
	        FileWriter f = null;
	        /*debido a que la funcion FileWriter da un error de acceso
	         * de vez en cuando, forzamos su ejecucion hasta que funcione correctamente*/
	        boolean centinela = true;
	        while (centinela) {
	            try {
	                f = new FileWriter(path_fw);
	                centinela = false;
	            } catch (IOException ignored) {
	            }
	        }
			
			StreamResult result = new StreamResult(f);
	        transformer.transform(source, result);
		} catch(TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
            e.printStackTrace();
        }
	}
	
	protected Document dameDoc() {
        Document doc = null;
        DocumentBuilder parser = null;
        try {
            DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
            parser = factoria.newDocumentBuilder();
            doc = parser.parse(this.path);
        } catch (Exception e) {
        	//TODO Cambiar esto por mensaje al controlador
            JOptionPane.showMessageDialog(
                    null,
                    Lenguaje.text(Lenguaje.ERROR) + ":\n" +
                            Lenguaje.text(Lenguaje.UNESPECTED_XML_ERROR) + " \"persistencia.xml\"",
                    Lenguaje.text(Lenguaje.DBCASE),
                    JOptionPane.ERROR_MESSAGE);
        }
        return doc;
    }
}
