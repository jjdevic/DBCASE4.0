package controlador;

import java.util.List;
import java.util.Vector;

public class Contexto {
	/**
	 * Mensaje asociado al contexto de ejecución.
	 */
	private TC mensaje;
	
	/**
	 * Indica si el contexto corresponde a una operación exitosa o no
	 */
	private boolean exito;
	
	/**
	 * Datos asociados al contexto.
	 */
	private Object datos;
	
	/**
	 * Lista de subContextos derivados de la ejecución.
	 */
	private Vector<Contexto> subContextos;

	public Contexto(boolean exito, TC mensaje) {
		this.exito = exito;
		this.mensaje = mensaje;
		this.datos = null;
		subContextos = null;
	}
	
	public Contexto(boolean exito, TC mensaje, Object datos) {
		this.exito = exito;
		this.mensaje = mensaje;
		this.datos = datos;
		subContextos = null;
	}
	
	public Contexto(boolean exito, TC mensaje, Object datos, Vector<Contexto> subContextos) {
		this.exito = exito;
		this.mensaje = mensaje;
		this.datos = datos;
		this.subContextos = subContextos;
	}
	
	public TC getMensaje() {
		return mensaje;
	}
	public void setMensaje(TC mensaje) {
		this.mensaje = mensaje;
	}
	public boolean isExito() {
		return exito;
	}
	public void setExito(boolean exito) {
		this.exito = exito;
	}
	public Object getDatos() {
		return datos;
	}
	public void setDatos(Object datos) {
		this.datos = datos;
	}
	public Vector<Contexto> getSubContextos() {
		return subContextos;
	}
	public void setSubContextos(Vector<Contexto> subContextos) {
		this.subContextos = subContextos;
	}
}