package controlador.comandos;

import controlador.Controlador;

public abstract class Comando {
	public Controlador ctrl;
	
	public Comando(Controlador ctrl) {
		this.ctrl = ctrl;
	}
	
	public abstract void ejecutar(Object datos);
}
