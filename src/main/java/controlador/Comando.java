package controlador;

import excepciones.ExceptionAp;
import modelo.servicios.FactoriaServicios;
import modelo.transfers.Transfer;
import org.w3c.dom.Document;

import java.util.Stack;


public abstract class Comando {
	protected Controlador ctrl;
	
	public Comando(Controlador ctrl) {
		this.ctrl = ctrl;
	}
	
	/**
	 * Ejecutar el comando.
	 * @param datos
	 * @return Null si no se quiere que se trate el contexto desde fuera. Contexto a tratar en caso contrario.
	 */
	public abstract Contexto ejecutar(Object datos) throws ExceptionAp;
	
	/**
	 * Toma la factoria de servicios. Todos los comandos deben usar este método para tomar la 
	 * factoría de servicios.
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
    
    /**
     * LLama al metodo del controlador dedicado a ejecutar comandos
     */
    protected Contexto ejecutarComando(TC mensaje, Object datos) {
    	return ctrl.ejecutarComandoDelMensaje(mensaje, datos);
    }
    
    protected Stack<Document> getPilaDeshacer() {
    	return ctrl.getPilaDeshacer();
    }
    
    protected Object enviarMensaje(TC mensaje, Object datos) {
    	return ctrl.mensaje(mensaje, datos);
    }
}