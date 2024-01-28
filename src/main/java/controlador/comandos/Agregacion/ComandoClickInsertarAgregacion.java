package controlador.comandos.Agregacion;

import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Controlador;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;

public class ComandoClickInsertarAgregacion extends Comando {

	public ComandoClickInsertarAgregacion(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector v = (Vector) datos;
        TransferRelacion t = (TransferRelacion) v.elementAt(0); //relacion sobre el que se construye la agregacion
        String nombre = (String) v.elementAt(1); //nombre de la nueva agregacion
        TransferAgregacion agreg = new TransferAgregacion();
        boolean sepuede = true;

        //comprobamos que esa relaci�n no pertenece a alguna agregacion existente:
        Vector<TransferAgregacion> agregaciones = getFactoriaServicios().getServicioAgregaciones().ListaDeAgregaciones();
        for (int i = 0; i < agregaciones.size() && sepuede; ++i) {
            TransferAgregacion actual_agreg = agregaciones.get(i);
            Vector lista_relaciones = actual_agreg.getListaRelaciones();
            String relacionId = (String) lista_relaciones.get(0); //solo hay una relacion por agregacion
            if (Integer.parseInt(relacionId) == t.getIdRelacion()) {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELACION_YA_TIENE_AGREGACION), Lenguaje.text(Lenguaje.ERROR), 0);
                sepuede = false;
            }
        }

        if (sepuede) {
            agreg.setNombre(nombre);
            Vector relaciones = new Vector();
            getFactoriaServicios().getServicioRelaciones().getSubesquema(t, relaciones);//tenemos que quitar del menu conceptual que se pueda hacer sobre entidades(comentalo)

            if (relaciones.size() == 1) {
                agreg.setListaRelaciones(relaciones);
                agreg.setListaAtributos(new Vector());

                getFactoriaServicios().getServicioAgregaciones().anadirAgregacion(agreg);
                ActualizaArbol(agreg);
                getFactoriaServicios().getServicioSistema().reset();

            } else {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.AGREG_MAS_RELACIONES), Lenguaje.text(Lenguaje.ERROR), 0);
            }
        }
	}

}
