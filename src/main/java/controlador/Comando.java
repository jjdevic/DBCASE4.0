package controlador;

import modelo.servicios.FactoriaServicios;
import modelo.transfers.Transfer;


public abstract class Comando {
	public Controlador ctrl;
	
	public Comando(Controlador ctrl) {
		this.ctrl = ctrl;
	}
	
	public abstract void ejecutar(Object datos);
	
	/**
	 * Toma la factoria de servicios. Todos los comandos deben usar este método para tomar la 
	 * factoría de servicios, al ser un método protected del controlador.
	 * @return Factoria de servicios
	 */
	protected FactoriaServicios getFactoriaServicios() {
		return ctrl.getFactoriaServicios();
	}
	
	/**
	 * LLama al método tratarContexto del controlador.
	 * @param ctxt Contexto a tratar
	 */
	protected void tratarContexto(Contexto ctxt) {
		ctrl.tratarContexto(ctxt);
	}
	
	/**
	 * LLama al método ActualizaArbol del controlador.
	 * @param tr Transfer que contiene la información que se debe actualizar.
	 */
	protected void ActualizaArbol(Transfer tr) {
		ctrl.ActualizaArbol(tr);
	}
}
