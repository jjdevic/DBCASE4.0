package vista.componentes;

import java.io.File;
import java.util.ArrayList;

/**
 * Esta clase representa el conjunto de archivos que se han abierto más recientemente.
 */
public class ArchivosRecientes {

    private ArrayList<File> recientes = new ArrayList<File>();
    /** Maximo de archivos recientes contemplados */
    private static final int max_size = 10;

    /**
     * Añade como más reciente un archivo al conjunto de archivos abiertos recientemente. Si se sobrepasa
     * la capacidad máxima, se elimina el archivo menos reciente.
     * @param f Archivo a añadir a la listas
     */
    public void add(File f) {
        if (recientes.contains(f)) {
            recientes.remove(f);
        }
        recientes.add(0, f); //lo anade al principio
        if (recientes.size() > max_size) {
            recientes.remove(recientes.size() - 1);//elimina el que hace mas tiempo que no se abre
        }
    }

    public ArrayList<File> darRecientes() {
        return recientes;
    }

    public void setRecientes(ArrayList<File> v) {
        recientes = v;
    }

}
