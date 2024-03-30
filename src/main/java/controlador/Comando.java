package controlador;

import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Document;

import modelo.servicios.FactoriaServicios;
import modelo.transfers.Transfer;
import modelo.transfers.TransferEntidad;


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
	public abstract Contexto ejecutar(Object datos);
	
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
	 * LLama al metodo aVectorContextos del controlador
	 */
	protected Vector<Contexto> aVectorContextos(Vector<Object> v, int inicio) {
    	return ctrl.aVectorContextos(v, inicio);
    }
    
	/**
	 * LLama al metodo tratarContextos del controlador
	 * @param v
	 */
    protected void tratarContextos(Vector<Contexto> v) {
    	ctrl.tratarContextos(v);
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
    
	protected void setAntigoNombreAtributo(String antigoNombreAtributo) {
		ctrl.setAntigoNombreAtributo(antigoNombreAtributo);
	}

	protected void setAntiguoDominioAtributo(String antiguoDominioAtributo) {
		ctrl.setAntiguoDominioAtributo(antiguoDominioAtributo);
	}

	protected void setAntiguoCompuestoAtributo(boolean antiguoCompuestoAtribuo) {
		ctrl.setAntiguoCompuestoAtribuo(antiguoCompuestoAtribuo);
	}

	protected void setAntiguoMultivaloradoAtributo(boolean antiguoMultivaloradoAtributo) {
		ctrl.setAntiguoMultivaloradoAtribuo(antiguoMultivaloradoAtributo);
	}

	protected void setAntiguoNotnullAtributo(boolean antiguoNotnullAtributo) {
		ctrl.setAntiguoNotnullAtribuo(antiguoNotnullAtributo);
	}

	protected void setAntiguoUniqueAtributo(boolean antiguoUniqueAtributo) {
		ctrl.setAntiguoUniqueAtribuo(antiguoUniqueAtributo);
	}

	protected void setAntiguoClavePrimaria(boolean antiguoClavePrimaria) {
		ctrl.setAntiguoClavePrimaria(antiguoClavePrimaria);
	}

	protected void setIdPadreAntigua(int idPadreAntigua) {
		ctrl.setIdPadreAntigua(idPadreAntigua);
	}

	protected void setHijosAntiguo(Vector<TransferEntidad> hijosAntiguo) {
		ctrl.setHijosAntiguo(hijosAntiguo);
	}

	protected void setPadreAntiguo(TransferEntidad padreAntiguo) {
		ctrl.setPadreAntiguo(padreAntiguo);
	}
}
