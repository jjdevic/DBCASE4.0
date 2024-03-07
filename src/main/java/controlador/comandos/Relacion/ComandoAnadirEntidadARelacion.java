package controlador.comandos.Relacion;

import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;

public class ComandoAnadirEntidadARelacion extends Comando{

	public ComandoAnadirEntidadARelacion(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		Contexto resultado = null;
		// v tiene: [transferRelacion, idEntidad, inicioRango, finalRango, rol]
        Vector v = (Vector) datos;
        //Vamos a controlar que no se añada una segunda entidad débil a una relación débil
        TransferRelacion tr = (TransferRelacion) v.get(0);
        TransferEntidad te = (TransferEntidad) v.get(1);
        boolean cardinalidadSeleccionada = (boolean) v.get(5);
        boolean participacionSeleccionada = (boolean) v.get(6);
        boolean minMaxSeleccionado = (boolean) v.get(7);
        boolean cardinalidadMax1Seleccionada;
        if (v.size() == 8) cardinalidadMax1Seleccionada = false;
        else cardinalidadMax1Seleccionada = (boolean) v.get(8);

        tr.setRelacionConCardinalidad(cardinalidadSeleccionada);
        tr.setRelacionConParticipacion(participacionSeleccionada);
        tr.setRelacionConMinMax(minMaxSeleccionado);
        tr.setRelacionConCardinalidad1(cardinalidadMax1Seleccionada);

        Vector<EntidadYAridad> vectorTupla = tr.getListaEntidadesYAridades();
        boolean relDebil = tr.getTipo().equals("Debil");
        boolean entDebil = te.isDebil();
        boolean relTieneEntDebil = false;
        for (EntidadYAridad entidadYAridad : vectorTupla) {
            int entidad = entidadYAridad.getEntidad();
            if (getFactoriaServicios().getServicioEntidades().esDebil(entidad)) {
                relTieneEntDebil = true;
                break;
            }
        }
        if (relDebil && entDebil && relTieneEntDebil)
            JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ALREADY_WEAK_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
        else getFactoriaServicios().getServicioRelaciones().anadirEntidadARelacion(v, 0);

        //a�adimos la relacion a la entidad para que sepa a que relaciones esta conectada

        resultado = getFactoriaServicios().getServicioEntidades().anadirRelacionAEntidad(v);
        getFactoriaServicios().getServicioSistema().reset();
        return resultado;
	}

}
