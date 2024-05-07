package persistencia;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import controlador.TC;
import excepciones.ExceptionAp;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public abstract class DAO {
	
	protected String path;
	
	DAO(String path) {
		this.path = path;
		this.path = this.path.replace(" ", "%20");
        this.path = this.path.replace('\\', '/');
	}

	protected void guardaDoc(Document doc) throws ExceptionAp {
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
        	throw new ExceptionAp(TC.ERROR_PERSISTENCIA);
        }
	}
	
	protected Document dameDoc() throws ExceptionAp{
        Document doc = null;
        DocumentBuilder parser = null;
       
        try {
	        DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
	        parser = factoria.newDocumentBuilder();
	        doc = parser.parse(this.path);
        } catch(ParserConfigurationException | IOException | SAXException e) {
        	throw new ExceptionAp(TC.ERROR_PERSISTENCIA);
        }
        
        return doc;
    }
	
	public static boolean creaAlmacenPers(String ruta) throws ExceptionAp{
        FileWriter fw;
        
        try {
            fw = new FileWriter(ruta);
            fw.write("<?xml version=" + '\"' + "1.0" + '\"' + " encoding="
                    + '\"' + "utf-8" + '\"' + " ?>" + '\n');
            //"ISO-8859-1"
            fw.write("<Inf_dbcase>" + "\n");
            fw.write("<EntityList proximoID=\"1\">" + "\n" + "</EntityList>" + "\n");
            fw.write("<RelationList proximoID=\"1\">" + "\n" + "</RelationList>" + "\n");
            fw.write("<AttributeList proximoID=\"1\">" + "\n" + "</AttributeList>" + "\n");
            fw.write("<DomainList proximoID=\"1\">" + "\n" + "</DomainList>");
            fw.write("<AggregationList proximoID=\"1\">" + "\n" + "</AggregationList>");
            fw.write("</Inf_dbcase>" + "\n");
            fw.close();
            return true;
        } catch (IOException e) {
        	throw new ExceptionAp(TC.FALLO_CREAR_ARCHIVO, "\n" + ruta);
        }
    }
}
