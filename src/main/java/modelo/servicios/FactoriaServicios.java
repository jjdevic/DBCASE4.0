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
	
	//TODO quitar este parámetro de controlador cuando los servicios ya no lo necesiten
	public FactoriaServicios(Controlador controlador) {
		servicioEntidades = new ServiciosEntidades();
        servicioAtributos = new ServiciosAtributos();
        servicioRelaciones = new ServiciosRelaciones();
        servicioRelaciones.setControlador(controlador);
        servicioDominios = new ServiciosDominios();
        servicioSistema = new GeneradorEsquema();
        servicioSistema.reset();
        servicioSistema.setControlador(controlador);
        servicioReporte = new ServiciosReporte();
        servicioReporte.setControlador(controlador);
        servicioAgregaciones = new ServiciosAgregaciones();
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