package modelo.conectorDBMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import modelo.servicios.Enumerado;
import modelo.servicios.Tabla;
import modelo.transfers.TipoDominio;
import vista.lenguaje.Lenguaje;

/**
 * Conecta la aplicación con un gestor de bases de datos MySQL
 * 
 * @author Denis Cepeda
 */
public class ConectorMySQL extends ConectorDBMS {

	private Connection _conexion;
	
	@Override
	public void abrirConexion(String ruta, String usuario, String password) throws SQLException{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println(Lenguaje.text(Lenguaje.NO_CONECTOR));
			e.printStackTrace();
			
			return;
		} 
		
		_conexion = DriverManager.getConnection(ruta, usuario, password);
		
		if(!_conexion.isClosed())
			System.out.println("Conectado correctamente a '" + ruta + "' usando TCP/IP...");
	}

	@Override
	public void cerrarConexion() throws SQLException{
		if(_conexion != null)
			_conexion.close();
	}

	@Override
	public void ejecutarOrden(String orden) throws SQLException{
		Statement st = _conexion.createStatement();
		st.executeUpdate(orden);
		st.close();
	}
	
	@Override
	public String obtenerCodigoCreacionTabla(Tabla t) {
		// Eliminar la tabla (si existía)
		String codigo="DROP TABLE IF EXISTS "+t.getNombreTabla()+";\n";
		
		// Crear la tabla
		codigo+="CREATE TABLE "+t.getNombreTabla()+" (";
		
		// Para cada atributo...
		Vector<String[]> atributos = t.getAtributos();
		for (int i=0;i<atributos.size();i++){
			if (i>0) codigo+=", ";
			//metemos el atributo
			codigo += atributos.elementAt(i)[0];
			
			// metemos el dominio
			codigo+=" "+equivalenciaTipoMySQL(atributos.elementAt(i)[1]);
			
			// not null
			if (atributos.elementAt(i)[4].equalsIgnoreCase("1"))
				codigo+= " NOT NULL";
		}
		//cerramos la creacion de la tabla
		codigo+=");\n";
		return codigo;
	}

	@Override
	public String obtenerCodigoCreacionTablaHTML(Tabla t) {
		// Eliminar la tabla (si existía)
		String codigo="<p><strong>DROP TABLE IF EXISTS </strong>"+t.getNombreTabla()+";</p>";
		
		// Crear la tabla
		codigo+="<p><strong>CREATE TABLE </strong>"+t.getNombreTabla()+" (";
		
		// Para cada atributo...
		Vector<String[]> atributos = t.getAtributos();
		for (int i=0;i<atributos.size();i++){
			if (i>0) codigo+=", ";
			//metemos el atributo
			codigo+=atributos.elementAt(i)[0];
			
			//metemos el dominio
			String dominio = equivalenciaTipoMySQL(atributos.elementAt(i)[1]);
			codigo+=" <strong>"+dominio+"</strong>";
			
			// Indicamos si es NOT NULL
			if (atributos.elementAt(i)[4].equalsIgnoreCase("1"))
				codigo+= "<strong> NOT NULL</strong>";
			
		}
		//cerramos la creacion de la tabla
		codigo+=");</p>";
		return codigo;
	}

	@Override
	public String obtenerCodigoClavesTablaHTML(Tabla t) {
		String codigo="";
		
		//si tiene claves primarias, las añadimos.
		Vector<String[]> primaries = t.getPrimaries();
		if (!primaries.isEmpty()){
			codigo+="<p><strong>ALTER TABLE </strong>"+t.getNombreTabla()+"<strong> ADD PRIMARY KEY </strong>(";
			for (int i=0;i<primaries.size();i++){
				if (i>0)codigo+=", ";
				codigo+=primaries.elementAt(i)[0];
			}
			codigo+=");</p>";
		}
		//si tiene claves foraneas:
		Vector<String[]> foreigns = t.getForeigns();
		if(!foreigns.isEmpty()){
			
			for (int j=0;j<foreigns.size();j++){
				String NombreTabla=t.getNombreTabla();
				String claveForanea=foreigns.elementAt(j)[0];
				String tablaReferenciada=foreigns.elementAt(j)[3];				
				String atributoReferenciado=foreigns.elementAt(j)[2].split("\\.")[1];
				codigo+="<p><strong>ALTER TABLE </strong>"+NombreTabla+"<strong> ADD FOREIGN KEY </strong>("+claveForanea+") <strong> REFERENCES </strong>"+tablaReferenciada+"("+atributoReferenciado+");</p>";
				
			}
		}
		
		// Si tiene uniques, se ponen
		Vector<String> uniques = t.getUniques();
		if(!uniques.isEmpty()){
			codigo+="<p><strong>ALTER TABLE </strong>"+t.getNombreTabla()+
					"<strong> ADD UNIQUE KEY </strong> (";
			for (int j=0;j<uniques.size();j++){
				codigo+=uniques.elementAt(j);
				if(uniques.size()-j>1) codigo +=", ";
			}
			codigo+=");</p>";
		}
		
		return codigo;
	}
	
	@Override
	public String obtenerCodigoEnumerado(Enumerado e) {
		// Eliminar la tabla (si existía)
		String codigo="DROP TABLE IF EXISTS "+e.getNombre()+";\n";
		
		// Crear la tabla
		codigo+="CREATE TABLE "+e.getNombre()+" (";
		if(e.getTipo()==TipoDominio.VARCHAR)
			codigo += "value_list "+e.getTipo()+"(" + e.getLongitud() + ")";
		else codigo += "value_list " + e.getTipo();
		codigo+=") ENGINE=InnoDB;\n";
		
		// Establecer la clave primaria
		codigo+="ALTER TABLE "+e.getNombre()+" ADD PRIMARY KEY (value_list);\n";
		
		// Insertar los valores
		for (int i=0; i<e.getNumeroValores(); i++){
			String valor = e.getValor(i);
			if (valor.startsWith("'")) {
				valor = valor.substring(1, valor.length() - 1);
			}
			
			codigo += "INSERT INTO " + e.getNombre() + " values ('" + valor + "');\n";
		}
		
		codigo += "\n";
		return codigo;
	}

	@Override
	public String obtenerCodigoEnumeradoHTML(Enumerado e) {
		// Eliminar la tabla (si existía)
		String codigo="<p><strong>DROP TABLE IF EXISTS </strong>"+e.getNombre()+";</p>";
		
		// Crear la tabla
		codigo+="<p><strong>CREATE TABLE </strong>"+e.getNombre()+" (";
		if(e.getTipo()==TipoDominio.VARCHAR || e.getTipo()==TipoDominio.CHAR || e.getTipo()==TipoDominio.TEXT)
			codigo += "value_list " + "<strong>"+e.getTipo()+"(" + e.getLongitud() + ")</strong>";
		else codigo += "value_list " + "<strong>"+e.getTipo()+"</strong>";
		codigo+=")<strong> ENGINE = InnoDB</strong>;</p>";

		// Establecer la clave primaria
		codigo+="<p><strong>ALTER TABLE </strong>"+e.getNombre()+"<strong> ADD PRIMARY KEY </strong>"+"(value_list);</p>";
		
		// Insertar los valores
		for (int i=0; i<e.getNumeroValores(); i++){
			String valor = e.getValor(i);
			if (valor.startsWith("'")) valor = valor.substring(1, valor.length() - 1);
			if(e.getTipo()==TipoDominio.VARCHAR || e.getTipo()==TipoDominio.CHAR || e.getTipo()==TipoDominio.TEXT)
				codigo += "<p><strong>INSERT INTO </strong>" + e.getNombre() + "<strong> VALUES </strong>(" + "'" + valor + "'" + ");</p>";
			else codigo += "<p><strong>INSERT INTO </strong>" + e.getNombre() + "<strong> VALUES </strong>(" + valor + ");</p>";
		}
		codigo += "</p>";
		return codigo;
	}
	
	// --- --- --- MÉTODOS AUXILIARES --- --- ---
	private String equivalenciaTipoMySQL(String tipo) {
		// Tipos simples que no hay que modificar
		if (tipo.equalsIgnoreCase("INTEGER") ||
			tipo.equalsIgnoreCase("BIT") ||
			tipo.equalsIgnoreCase("DATE") ||
			tipo.equalsIgnoreCase("DATETIME") ||
			tipo.equalsIgnoreCase("BLOB")){
				return tipo;
		}
		
		// Tipos simples a modificar
		if (tipo.equalsIgnoreCase("FLOAT")){
			return "REAL";
		}
		// Tipos compuestos que no hay que modificar
		if (tipo.indexOf("(") > 0){
			String tipoSinParam = tipo.substring(0, tipo.indexOf("("));
			if (tipoSinParam.equalsIgnoreCase("CHAR") ||
				tipoSinParam.equalsIgnoreCase("VARCHAR") ||
				tipoSinParam.equalsIgnoreCase("TEXT") ||
				tipoSinParam.equalsIgnoreCase("DECIMAL") ||
				tipoSinParam.equalsIgnoreCase("INTEGER")){
				return tipo;
			}
		}
		
		// Tipos pertenecientes a los dominios creados
		return tipo + "_sinAnalizar";
	}

	@Override
	public void usarDatabase(String nombre) throws SQLException {
		ejecutarOrden ("DROP DATABASE IF EXISTS " + nombre + ";");
		ejecutarOrden ("CREATE DATABASE " + nombre +  ";");
		ejecutarOrden ("USE " + nombre + ";");
	}

}