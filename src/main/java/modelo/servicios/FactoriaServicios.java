package modelo.servicios;

public class FactoriaServicios {
	
	private ServiciosEntidades servicioEntidades;
	private ServiciosAtributos servicioAtributos;
	private ServiciosRelaciones servicioRelaciones;
	private ServiciosDominios servicioDominios;
	private GeneradorEsquema servicioSistema;
	private ServiciosReporte servicioReporte;
	private ServiciosAgregaciones servicioAgregaciones;
	private ServicioGeneral servicioGeneral;
	
	public ServiciosEntidades getServicioEntidades() {
		if(servicioEntidades == null) {
			servicioEntidades = new ServiciosEntidades();
		}
		return servicioEntidades;
	}

	public ServiciosAtributos getServicioAtributos() {
		if(servicioAtributos == null) {
			servicioAtributos = new ServiciosAtributos(this);
		}
		return servicioAtributos;
	}

	public ServiciosRelaciones getServicioRelaciones() {
		if(servicioRelaciones == null) {
			servicioRelaciones = new ServiciosRelaciones();
		}
		return servicioRelaciones;
	}

	public ServiciosDominios getServicioDominios() {
		if(servicioDominios == null) {
			servicioDominios = new ServiciosDominios();
		}
		return servicioDominios;
	}

	public GeneradorEsquema getServicioSistema() {
		if(servicioSistema == null) {
			servicioSistema = new GeneradorEsquema();
			servicioSistema.reset();
		}
		return servicioSistema;
	}

	public ServiciosReporte getServicioReporte() {
		if(servicioReporte == null) {
			servicioReporte = new ServiciosReporte();
		}
		return servicioReporte;
	}

	public ServiciosAgregaciones getServicioAgregaciones() {
		if(servicioAgregaciones == null) {
			servicioAgregaciones = new ServiciosAgregaciones();
		}
		return servicioAgregaciones;
	}

	public ServicioGeneral getServicioGeneral() {
		if(servicioGeneral == null) {
			servicioGeneral = new ServicioGeneral();
		}
		return servicioGeneral;
	}
}