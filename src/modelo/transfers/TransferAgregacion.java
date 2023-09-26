package modelo.transfers;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Vector;


@SuppressWarnings("rawtypes")
public class TransferAgregacion extends Transfer{

	private int idAgregacion;
	private String nombre;
	private Vector listaRelaciones; //Al parecer solo puede tener una asi que en principio solo tendra un elemento guarda el Id como String
	private Vector listaAtributos;
	private Point2D posicion;
	private int volumen;//?
	private int frecuencia;//?
	private int offsetAttr=0;//?
	
	public void CopiarAgregacion(TransferAgregacion agreg) {
		// TODO Auto-generated method stub
		this.idAgregacion = agreg.idAgregacion;
		this.nombre = agreg.nombre;
		this.listaRelaciones = agreg.listaRelaciones;
		this.listaAtributos = agreg.listaAtributos;
		this.volumen = agreg.volumen;
		this.frecuencia = agreg.frecuencia;
		this.offsetAttr = agreg.offsetAttr;
		this.posicion = new Point2D.Double(agreg.getPosicion().getX(),agreg.getPosicion().getY());
	}
	
	public TransferAgregacion clonar() {
		// TODO Auto-generated method stub
		TransferAgregacion agreg = new TransferAgregacion();
		agreg.setIdAgregacion(this.idAgregacion);
		agreg.setNombre(this.nombre);
		agreg.setListaRelaciones((Vector)this.listaRelaciones.clone());
		agreg.setListaAtributos((Vector)this.listaAtributos.clone());
		agreg.setPosicion((Point2D)this.posicion.clone());
		agreg.setVolumen(this.volumen);
		agreg.setFrecuencia(this.frecuencia);
		agreg.setOffsetAttr(this.offsetAttr);
		return agreg;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getIdAgregacion() {
		return idAgregacion;
	}
	public void setIdAgregacion(int idAgregacion) {
		this.idAgregacion = idAgregacion;
	}
	public Vector getListaRelaciones() {
		return listaRelaciones;
	}
	public void setListaRelaciones(Vector listaRelaciones) {
		this.listaRelaciones = listaRelaciones;
	}
	public Point2D getPosicion() {
		Point2D p = new Point2D.Double(0,0);
		return p;
	}
	public void setPosicion(Point2D posicion) {
		this.posicion = posicion;
	}
	public Vector getListaAtributos() {
		return listaAtributos;
	}
	public void setListaAtributos(Vector listaAtributos) {
		this.listaAtributos = listaAtributos;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.nombre;
	}
	@Override
	public Shape toShape() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getVolumen() {
		// TODO Auto-generated method stub
		return volumen;
	}
	@Override
	public int getFrecuencia() {
		// TODO Auto-generated method stub
		return frecuencia;
	}
	@Override
	public void setVolumen(int v) {
		// TODO Auto-generated method stub
		volumen=v;
		
	}
	@Override
	public void setFrecuencia(int f) {
		// TODO Auto-generated method stub

		frecuencia=f;
	}
	
	public int getOffsetAttr() {
		return offsetAttr;
	}
	
	public void setOffsetAttr(int o) {
		offsetAttr=o;
	}
	
}