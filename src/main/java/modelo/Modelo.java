package modelo;

import java.util.Vector;

import modelo.transfers.TransferAtributo;
import persistencia.DAOAtributos;

public class Modelo {
	private Vector<TransferAtributo> listaAtributos;
	
	public Vector<TransferAtributo> getListaDeAtributos() {
        DAOAtributos dao = new DAOAtributos();
        return dao.ListaDeAtributos();
    }
	
}
