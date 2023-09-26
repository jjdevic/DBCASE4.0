package vista.componentes;

import java.io.File;
import java.util.ArrayList;

public class ArchivosRecientes {

	private ArrayList<File> recientes = new ArrayList<File>();
	//Establecemos un maximo de archivos mostrados en recientes para que sean realmente los recientes
	private static final int  max_size = 10;  
	
	public void add(File f) {
		if(recientes.contains(f)) {
			recientes.remove(f);
		}
		recientes.add(0,f); //lo añade al principio
		if(recientes.size() > max_size) {
			recientes.remove(recientes.size()-1);//elimina el que hace mas tiempo que no se abre
		}
	}
	
	public  ArrayList<File> darRecientes(){
		return recientes;
	}
	
	public void recibeRecientes( ArrayList<File>v) {
		recientes = v;
	}
	
}
