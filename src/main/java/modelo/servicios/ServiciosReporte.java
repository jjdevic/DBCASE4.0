package modelo.servicios;

import controlador.Controlador;
import vista.Lenguaje;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ServiciosReporte {

    public void crearIncidencia(String textoIncidencia, boolean anadirDiagrama, File filetemp) {
        try {
            //Creamos la carpeta donde incluiremos la incidencia
            File nuevaCarpeta = new File(System.getProperty("user.dir") + "/incidences/INC-" + filetemp.getName());
            if (!nuevaCarpeta.exists()) nuevaCarpeta.mkdir();

            String ruta = nuevaCarpeta.getAbsolutePath() + "/incidencia.txt";
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
            if (anadirDiagrama) {
                file = new File(nuevaCarpeta.getAbsolutePath() + "/diagrama.xml");
                Files.copy(filetemp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String textoMail = "*" + Lenguaje.text(Lenguaje.EMAIL_HEADER) + "*";
        textoMail += '\n';
        textoMail += '\n';
        textoMail += textoIncidencia;
        textoMail += '\n';
        textoMail += '\n';
        textoMail = textoMail.replace(" ", "%20");
        textoMail = textoMail.replace("\n", "%0A");

        Desktop desktop = Desktop.getDesktop();
        String asunto = "INC-" + filetemp.getName();
        String message = "mailto:ggarvi@ucm.es?subject=" + asunto + "&body=" + textoMail;
        URI uri = URI.create(message);

        try {
            desktop.mail(uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}