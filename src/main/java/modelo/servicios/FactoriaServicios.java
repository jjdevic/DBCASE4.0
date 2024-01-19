package modelo.servicios;

import controlador.Controlador;

public class FactoriaServicios {
	
	private ServiciosEntidades servicioEntidades;
	private ServiciosAtributos servicioAtributos;
	private ServiciosRelaciones servicioRelaciones;
	private ServiciosDominios servicioDominios;
	private GeneradorEsquema servicioSistema;
	private ServiciosReporte servicioReporte;
	private ServiciosAgregaciones servicioAgregaciones;
	
	public FactoriaServicios(Controlador controlador) {
		servicioEntidades = new ServiciosEntidades();
        servicioEntidades.setControlador(controlador);
        servicioAtributos = new ServiciosAtributos();
        //servicioAtributos.setControlador(controlador);
        servicioRelaciones = new ServiciosRelaciones();
        servicioRelaciones.setControlador(controlador);
        servicioDominios = new ServiciosDominios();
        servicioDominios.setControlador(controlador);
        servicioSistema = new GeneradorEsquema();
        servicioSistema.reset();
        servicioSistema.setControlador(controlador);
        servicioReporte = new ServiciosReporte();
        servicioReporte.setControlador(controlador);
        servicioAgregaciones = new ServiciosAgregaciones();
        servicioAgregaciones.setControlador(controlador);
	}
	
	public ServiciosEntidades getServicioEntidades() {
		return servicioEntidades;
	}

	public ServiciosAtributos getServicioAtributos() {
		return servicioAtributos;
	}

	public ServiciosRelaciones getServicioRelaciones() {
		return servicioRelaciones;
	}

	public ServiciosDominios getServicioDominios() {
		return servicioDominios;
	}

	public GeneradorEsquema getServicioSistema() {
		return servicioSistema;
	}

	public ServiciosReporte getServicioReporte() {
		return servicioReporte;
	}

	public ServiciosAgregaciones getServicioAgregaciones() {
		return servicioAgregaciones;
	}

}
