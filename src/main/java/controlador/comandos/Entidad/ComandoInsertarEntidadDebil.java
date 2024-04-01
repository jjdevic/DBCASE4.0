package controlador.comandos.Entidad;

import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import controlador.Factorias.FactoriaMsj;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;

public class ComandoInsertarEntidadDebil extends Comando {

	public ComandoInsertarEntidadDebil(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		Contexto contexto = null;
		Vector<Object> v_datos = (Vector<Object>) datos;
    	
    	//Extraer datos de la entrada
    	TransferEntidad te = (TransferEntidad) v_datos.get(0);
    	Point2D p = (Point2D) v_datos.get(1);
    	String nom_relacion = (String) v_datos.get(2);
    	TransferEntidad entidad_fuerte = (TransferEntidad) v_datos.get(3);
    	
    	//Comprobar si se puede insertar la entidad
    	Contexto c_factibleEntidad, c_factibleRelacion;
    	c_factibleEntidad = getFactoriaServicios().getServicioEntidades().SePuedeAnadirEntidad(te);
        
        //Informar del error si lo hay
        if(!c_factibleEntidad.isExito()) {
        	TC mError = c_factibleEntidad.getMensaje();
        	JOptionPane.showMessageDialog(null, FactoriaMsj.getMsj(mError), Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
        } 
        else {
        	//Insertar entidad
        	contexto = getFactoriaServicios().getServicioEntidades().anadirEntidad(te, getPilaDeshacer());
        	tratarContexto(contexto);
        	
        	//Ejecutar comando insertar relacion debil
        	contexto = ejecutarComando(TC.Controlador_InsertarRelacionDebil, datos);
        }
        return contexto;
	}
}