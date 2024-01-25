package modelo;

import java.util.Vector;

import controlador.Controlador;
import modelo.servicios.GeneradorEsquema;
import modelo.servicios.ServiciosAgregaciones;
import modelo.servicios.ServiciosAtributos;
import modelo.servicios.ServiciosDominios;
import modelo.servicios.ServiciosEntidades;
import modelo.servicios.ServiciosRelaciones;
import modelo.servicios.ServiciosReporte;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAOAtributos;

public class Modelo {
	
	
	private Vector<TransferAtributo> listaAtributos;
	private Vector<TransferEntidad> listaEntidades;
    private Vector<TransferRelacion> listaRelaciones;
	
	

	public Vector<TransferAtributo> getListaAtributos() {
		return listaAtributos;
	}

	
	
	
}