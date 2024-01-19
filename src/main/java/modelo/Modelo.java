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
import persistencia.DAOAtributos;

public class Modelo {
	
	
	private Vector<TransferAtributo> listaAtributos;
	
	

	public Vector<TransferAtributo> getListaAtributos() {
		return listaAtributos;
	}

	
	public Vector<TransferAtributo> getListaDeAtributos() {
        DAOAtributos dao = new DAOAtributos();
        return dao.ListaDeAtributos();
    }
	
}
