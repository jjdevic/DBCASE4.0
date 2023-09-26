package modelo.servicios;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import controlador.Controlador;
import vista.lenguaje.Lenguaje;

public class ServiciosReporte {
	private Controlador controlador;

	public void crearIncidencia(String textoIncidencia, boolean anadirDiagrama) {		
		try {
			 //Creamos la carpeta donde incluiremos la incidencia
			 	File nuevaCarpeta=new File(System.getProperty("user.dir")+"/incidences/INC-"+controlador.getFiletemp().getName());
			    if (!nuevaCarpeta.exists()) nuevaCarpeta.mkdir();
			 
	            String ruta = nuevaCarpeta.getAbsolutePath()+"/incidencia.txt";
	            String contenido = textoIncidencia;
	            File file = new File(ruta);
	            // Si el archivo no existe es creado
	            if (!file.exists()) {
	                file.createNewFile();
	            }
	            FileWriter fw = new FileWriter(file);
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(contenido);
	            bw.close();
	            if(anadirDiagrama) {
	            	file=new File(nuevaCarpeta.getAbsolutePath()+"/diagrama.xml");
		            Files.copy(controlador.getFiletemp().toPath(),file.toPath(),StandardCopyOption.REPLACE_EXISTING);
	            }
	    
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		String textoMail="*"+Lenguaje.text(Lenguaje.EMAIL_HEADER)+"*";
		textoMail+='\n';
		textoMail+='\n';
		textoMail+=textoIncidencia;
		textoMail+='\n';
		textoMail+='\n';
		textoMail=textoMail.replace(" ", "%20");
		textoMail=textoMail.replace("\n", "%0A");
		
		Desktop desktop = Desktop.getDesktop();
		String asunto="INC-"+controlador.getFiletemp().getName();
		String message = "mailto:ggarvi@ucm.es?subject="+asunto+"&body="+textoMail;
		URI uri = URI.create(message);
	
		try {
			desktop.mail(uri);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public Controlador getControlador() {
		return controlador;
	}

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}
}
