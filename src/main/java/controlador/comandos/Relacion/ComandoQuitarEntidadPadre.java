package controlador.comandos.Relacion;

import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;

public class ComandoQuitarEntidadPadre extends Comando {

	public ComandoQuitarEntidadPadre(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		TransferRelacion tr = (TransferRelacion) datos;
        //this.idPadreAntigua = tr.getEntidadYAridad(0).getEntidad();
        Vector<EntidadYAridad> eyaV = tr.getListaEntidadesYAridades();
        EntidadYAridad eya = eyaV.get(0);
        int idPadre = eya.getEntidad();
        TransferEntidad te = new TransferEntidad();
        Vector<TransferEntidad> listaEntidades = (Vector<TransferEntidad>) ctrl.mensaje(TC.ObtenerListaEntidades, null);
        for (TransferEntidad listaEntidade : listaEntidades) {
            if (idPadre == listaEntidade.getIdEntidad());
                //this.padreAntiguo = listaEntidade;
        }

        //obtenemos las hijas
        Vector<TransferEntidad> th = new Vector<TransferEntidad>();
        Vector<EntidadYAridad> hijas = new Vector<EntidadYAridad>();
        for (int i = 1; i < eyaV.size(); i++) {
            EntidadYAridad eyaH = eyaV.get(i);
            hijas.add(eyaH);
        }
        for (EntidadYAridad e : hijas) {
            int idHija = e.getEntidad();
            for (TransferEntidad listaEntidade : listaEntidades) {
                if (idHija == listaEntidade.getIdEntidad()) th.add(listaEntidade);
            }
        }
        //this.hijosAntiguo = th;

        getFactoriaServicios().getServicioRelaciones().quitarEntidadPadreEnRelacionIsA(tr);
        ActualizaArbol(tr);
        getFactoriaServicios().getServicioSistema().reset();
	}

}
