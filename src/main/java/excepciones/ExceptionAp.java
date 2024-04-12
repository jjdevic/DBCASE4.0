package excepciones;

import controlador.TC;

public class ExceptionAp extends Exception{
	private TC m;
	private String cad;
	
	public ExceptionAp(TC m) {
		this.m = m;
		this.cad="";
	}
	
	public ExceptionAp(TC m, String cad) {
		this.m = m;
		this.cad=cad;
	}
	
	public TC getM() { return m; }
	
	public String getCad() { return cad; }
}
