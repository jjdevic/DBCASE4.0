package controlador.comandos.Relacion;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import excepciones.ExceptionAp;
import modelo.transfers.EntidadYAridad;
import modelo.transfers.TransferRelacion;

import java.awt.geom.Point2D;
import java.util.Vector;

public class ComandoModificarCardinalidadRelacion1a1 extends Comando {

	public ComandoModificarCardinalidadRelacion1a1(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		TransferRelacion tr = (TransferRelacion) datos;
		Vector<Object> v = new Vector<Object>();
        EntidadYAridad informacion;
        int i = 0;
        boolean actualizado = false;
        while ((!actualizado) && (i < tr.getListaEntidadesYAridades().size())) {
            informacion = (EntidadYAridad) (tr.getListaEntidadesYAridades().get(i));
            int idEntidad = informacion.getEntidad();
            if (getFactoriaServicios().getServicioEntidades().esDebil(idEntidad)) {
                actualizado = true;
                int idRelacion = tr.getIdRelacion();
                int finRango = 1;
                int iniRango = 1;
                String nombre = tr.getNombre();
                Point2D posicion = tr.getPosicion();
                Vector<Object> listaEnti = tr.getListaEntidadesYAridades();
                EntidadYAridad aux = (EntidadYAridad) listaEnti.get(i);
                aux.setFinalRango(1);
                aux.setPrincipioRango(1);
                listaEnti.remove(i);
                listaEnti.add(aux);
                Vector<Object> listaAtri = tr.getListaAtributos();
                String tipo = tr.getTipo();
                String rol = tr.getRol();
                v.add(idRelacion);
                v.add(idEntidad);
                v.add(iniRango);
                v.add(finRango);
                v.add(nombre);
                v.add(listaEnti);
                v.add(listaAtri);
                v.add(tipo);
                v.add(rol);
                v.add(posicion);
                getFactoriaServicios().getServicioRelaciones().aridadEntidadUnoUno(v);
            }
            i++;
        }
        return null;
	}
}