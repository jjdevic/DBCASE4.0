package modelo.conectorDBMS;

import controlador.TC;
import excepciones.ExceptionAp;
import vista.Lenguaje;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectorAccessMdb extends ConectorAccessOdbc {

    protected String _usuario;
    protected String _password;

    @Override
    public void abrirConexion(String ruta, String usuario, String password)
            throws SQLException {
        _usuario = usuario;
        _password = password;
    }

    @Override
    public void usarDatabase(String nombre) throws SQLException, ExceptionAp {
        // Obtener el conector
        String rutaCompleta = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ="
                + nombre;
        String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println(Lenguaje.text(Lenguaje.NO_CONECTOR));
            throw new SQLException(Lenguaje.text(Lenguaje.NO_CONECTOR));
        }

        // Eiminar la base de datos, y crear una nueva
        File f = new File(nombre);
        if (f.exists()) f.delete();

        boolean creado = copyfile("/data/void.mdb", nombre);
        if (!creado) {
        	throw new ExceptionAp(TC.FALLO_CREAR_ARCHIVO);
            
        }
        _conexion = DriverManager.getConnection(rutaCompleta, _usuario, _password);
        if (!_conexion.isClosed())
            System.out.println("Conectado correctamente a '" + nombre + "'...");
    }

    private static boolean copyfile(String srFile, String dtFile) {
        try {
            //File f1 = new File(srFile);
            File f2 = new File(dtFile);
            Object o = new Object();
            InputStream in = o.getClass().getResourceAsStream(srFile);
            // For Append the file.
            // OutputStream out = new FileOutputStream(f2,true);

            // For Overwrite the file.
            OutputStream out = Files.newOutputStream(f2.toPath());
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
