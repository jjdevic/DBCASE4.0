package controlador;

public class Contexto {
	private TC mensaje;
	private boolean exito;
	private Object datos;
	
	public Contexto(boolean exito, TC mensaje) {
		this.exito = exito;
		this.mensaje = mensaje;
		this.datos = null;
	}
	
	public Contexto(boolean exito, TC mensaje, Object datos) {
		this.exito = exito;
		this.mensaje = mensaje;
		this.datos = datos;
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
	
}
