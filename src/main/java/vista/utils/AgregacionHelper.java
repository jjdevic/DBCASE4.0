package vista.utils;

import java.awt.geom.Point2D;
import java.util.Vector;

import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;

public class AgregacionHelper {

	public Vector<Point2D> calculateAggregationPosition(TransferAgregacion ta) {
		
		//Llamada al controlador para conocer la posición de la relación, de las entidades, y de los atributos
        TransferRelacion tr = null; // = controlador.mensajeDesde_GUI(TC.LeerRelacion, tr.getId());
        Vector<TransferEntidad> vte = null;
        Vector<TransferAtributo> vta = null; //TODO atributos de la relacion (si los hay) y de las entidades (si los hay)
        
        //Recopilar todas las posiciones en un vector
        Vector<Point2D> posiciones = new Vector<Point2D>();
        posiciones.add(tr.getPosicion());
		for(TransferEntidad te: vte) posiciones.add(te.getPosicion());
		for(TransferAtributo tAtr: vta) posiciones.add(tAtr.getPosicion());
		
		//Recorrer el vector buscando las posiciones más extremas, para delimitar el rectángulo
		double minX = 0, maxX = 0, minY = 0, maxY = 0; //TODO Mirar cuál es el máximo para las x y para las y (e inicializarlos al revés)
		for(Point2D pos: posiciones) {
			if(pos.getX() < minX) minX = pos.getX();
			if(pos.getX() > maxX) maxX = pos.getX();
			
			if(pos.getY() < minY) minY = pos.getY();
			if(pos.getY() > maxY) maxY = pos.getY();
		}
		
		
		return null;
	}
}
