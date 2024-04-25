package modelo.servicios;

import excepciones.ExceptionAp;
import persistencia.DAO;

public class ServicioGeneral {

	public static boolean crearAlmacenPers(String ruta) throws ExceptionAp{
		return DAO.creaAlmacenPers(ruta);
	}
}
