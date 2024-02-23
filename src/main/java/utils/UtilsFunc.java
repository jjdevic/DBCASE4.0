package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import vista.Lenguaje;

public class UtilsFunc {
	
	public static boolean creaFicheroXML(File f) {
        FileWriter fw;
        String ruta = f.getPath();
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
            JOptionPane.showMessageDialog(null,
                    Lenguaje.text(Lenguaje.ERROR_CREATING_FILE) + "\n" + ruta,
                    Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
	
	public static void quicksort(Vector<String> a) {
        quicksort(a, 0, a.size() - 1);
    }

    // quicksort a[left] to a[right]
    public static void quicksort(Vector<String> a, int left, int right) {
        if (right <= left) return;
        int i = partition(a, left, right);
        quicksort(a, left, i - 1);
        quicksort(a, i + 1, right);
    }

    // partition a[left] to a[right], assumes left < right
    public static int partition(Vector<String> a, int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while ((a.get(++i).compareToIgnoreCase(a.get(right)) < 0))      // find item on left to swap
                ;                               // a[right] acts as sentinel
            while ((a.get(right).compareToIgnoreCase(a.get(--j)) < 0))      // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            exch(a, i, j);                      // swap two elements into place
        }
        exch(a, i, right);                      // swap with partition element
        return i;
    }

    public static void exch(Vector<String> a, int i, int j) {
        //exchanges++;
        String swap = a.get(i);
        a.set(i, a.get(j));
        a.set(j, swap);
    }

	public static void FileCopy(String sourceFile, String destinationFile) {
        try {
            File inFile = new File(sourceFile);
            File outFile = new File(destinationFile);

            FileInputStream in = new FileInputStream(inFile);
            FileOutputStream out = new FileOutputStream(outFile);

            int c;
            while ((c = in.read()) != -1) out.write(c);
            in.close();
            out.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
	
	/**
	 * Crea un vector con los objetos pasados como parametros.
	 * @return Vector resultante.
	 */
	public static Vector<Object> crearVector(Object o1, Object o2, Object o3) {
    	Vector<Object> v = new Vector<Object>();
    	v.add(o1);
    	v.add(o2);
    	v.add(o3);
    	return v;
    }
}
