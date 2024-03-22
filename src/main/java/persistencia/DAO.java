package persistencia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import vista.Lenguaje;

abstract class DAO {
	
	protected String path;
	protected TransformerFactory transFac;
	
	DAO(String path) {
		this.path = path;
		this.path = this.path.replace(" ", "%20");
        this.path = this.path.replace('\\', '/');
		this.transFac = TransformerFactory.newInstance();
		//transFac.setAttribute("indent-number", N_INDENT);
	}

	protected void guardaDoc(Document doc) {
		OutputFormat formato = new OutputFormat(doc.toString(), "utf-8", true);
        StringWriter s = new StringWriter();
        XMLSerializer ser = new XMLSerializer(s, formato);
        
        try {
            ser.serialize(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // El FileWriter necesita espacios en la ruta
        this.path = this.path.replace("%20", " ");
        FileWriter f = null;
        /*debido a que la funcion FileWriter da un error de acceso
         * de vez en cuando, forzamos su ejecucion hasta que funcione correctamente*/
        boolean centinela = true;
        while (centinela == true) {
            try {
                f = new FileWriter(this.path);
                centinela = false;
            } catch (IOException e) {
                centinela = true;
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
	
	protected Document dameDoc() {
        Document doc = null;
        DocumentBuilder parser = null;
        try {
            DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
            parser = factoria.newDocumentBuilder();
            doc = parser.parse(this.path);
        } catch (Exception e) {
        	//TODO Cambiar esto
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
