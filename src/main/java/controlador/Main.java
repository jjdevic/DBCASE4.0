package controlador;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;
import static vista.utils.Otros.PROJECTS;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import utils.UtilsFunc;
import vista.Lenguaje;
import vista.tema.Theme;

public class Main {

	public static void main(String[] args) throws Exception {

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                break;
            }
        File directory = new File(System.getProperty("user.dir") + DIRECTORY);
        if (directory.mkdir()) {
            //creamos la carpeta projects si no existe
            File projects = new File(System.getProperty("user.dir") + DIRECTORY + PROJECTS);
            if (!projects.exists()) projects.mkdir();

            //Creamos la carpeta incidences si no existe
            File incidences = new File(System.getProperty("user.dir") + DIRECTORY + INCIDENCES);
            if (!incidences.exists()) incidences.mkdir();
        }

        // Obtenemos configuración inicial (si la hay)
        ConfiguradorInicial conf = new ConfiguradorInicial();
        conf.leerFicheroConfiguracion();

        // Obtenemos el lenguaje en el que vamos a trabajar

        Lenguaje.encuentraLenguajes();//AQUI
        Theme.loadThemes();
        Theme.changeTheme(conf.obtenTema());
        //valorZoom=conf.obtenZoom();

        Controlador controlador = new Controlador();

        if (conf.existeFichero()) {
            controlador.getArchivosRecientes().setRecientes(conf.darRecientes());
            Vector<String> lengs = Lenguaje.obtenLenguajesDisponibles();
            boolean encontrado = false;
            int k = 0;
            while (!encontrado && k < lengs.size()) {
                encontrado = lengs.get(k).equalsIgnoreCase(conf.obtenLenguaje());
                k++;
            }

            if (encontrado) Lenguaje.cargaLenguaje(conf.obtenLenguaje());
            else Lenguaje.cargaLenguajePorDefecto();

        } else {
            Lenguaje.cargaLenguajePorDefecto();
            Theme.loadDefaultTheme();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    controlador.setFiletemp(File.createTempFile("dbcase", "xml"));
                    UtilsFunc.creaFicheroXML(controlador.getFiletemp());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            Lenguaje.text(Lenguaje.ERROR_TEMP_FILE),
                            Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
                }
                String ruta = controlador.getFiletemp().getPath();
                controlador.setPath(ruta);
                controlador.setNullAttrs(conf.obtenNullAttr());

                // Abrimos el documento guardado últimamente
                File ultimo = new File(conf.obtenUltimoProyecto());
                if (ultimo.exists()) {
                    controlador.getArchivosRecientes().add(ultimo);
                    //controlador.setZoom(0);
                    controlador.setFileguardar(ultimo);
                    controlador.setModoVista(conf.obtenModoVista());
                    controlador.setZoom(conf.obtenZoom());
                    controlador.setNullAttrs(conf.obtenNullAttr());
                    String abrirPath = conf.obtenUltimoProyecto();
                    String tempPath = controlador.getFiletemp().getAbsolutePath();
                    UtilsFunc.FileCopy(abrirPath, tempPath);

                    // Reinicializamos la GUIPrincipal
                    controlador.getFactoriaGUI().getGUIPrincipal().setActiva(controlador.getModoVista());
                    // Reiniciamos los datos de los servicios de sistema
                    controlador.getFactoriaServicios().getServicioSistema().reset();
                    controlador.setCambios(false);
                    controlador.getFactoriaGUI().getGUIPrincipal().loadInfo();
                    controlador.getFactoriaGUI().getGUIPrincipal().cambiarZoom(controlador.getZoom());

                } else {
                    controlador.setModoVista(0);
                    controlador.getFactoriaGUI().getGUIPrincipal().setActiva(controlador.getModoVista());
                }
                // Establecemos la base de datos por defecto
                if (conf.existeFichero())
                    controlador.getFactoriaGUI().getGUIPrincipal().cambiarConexion(conf.obtenGestorBBDD());

                if (ultimo.exists()) {
                    //Almacenamos nuestro primer fichero en la carpeta usada para la tarea deshacer
                    File directorio = new File(System.getProperty("user.dir") + "/deshacer");
                    if (!directorio.exists()) {
                        if (directorio.mkdirs()) {
                            // System.out.println("Directorio creado");
                        } else {
                            System.out.println("Error al crear directorio");
                        }
                    } else {
                        for (File file : Objects.requireNonNull(directorio.listFiles())) {
                            if (!file.isDirectory()) {
                                file.delete();
                            }
                        }
                    }
                    controlador.guardarDeshacer();
                } else {
                    //Almacenamos nuestro primer fichero en la carpeta usada para la tarea deshacer
                    File temp = new File(System.getProperty("user.dir") + "/projects/temp");
                    controlador.setFileguardar(temp);
                    File directorio = new File(System.getProperty("user.dir") + "/deshacer");
                    if (!directorio.exists()) {
                        if (directorio.mkdirs()) {
                            // System.out.println("Directorio creado");
                        } else {
                            System.out.println("Error al crear directorio");
                        }
                    } else {
                        for (File file : Objects.requireNonNull(directorio.listFiles())) {
                            if (!file.isDirectory()) {
                                file.delete();
                            }
                        }
                    }
                    controlador.guardarDeshacer();
                }
            }
        });
    }
}
