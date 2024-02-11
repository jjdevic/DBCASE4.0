package controlador.comandos.Relacion;

import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import utils.UtilsFunc;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoClickDebilitarRelacion extends Comando{

	public ComandoClickDebilitarRelacion(Controlador ctrl) {
		super(ctrl);
	}

	/*Aunque desde el panel de diseño no se puede debilitar una relación este caso sigue
     * utilizándose cuando se crea una entidad débil ya que debe generarse también una
     * relación débil asociada a ella.*/
	@Override
	public void ejecutar(Object datos) {
		TransferRelacion tr = (TransferRelacion) datos;
        //Si es una relacion fuerte...
        if (tr.getTipo().equals("Normal")) {
            int numDebiles = getFactoriaServicios().getServicioRelaciones().numEntidadesDebiles(tr);
            // ...y tiene más de una entidad débil no se puede debilitar
            if (numDebiles > 1) {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATION_WEAK_ENTITIES), Lenguaje.text(Lenguaje.ERROR), 0);
            } 
            else {
	            Boolean respuesta1 = null;
	            Boolean respuesta2 = null;
	            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, null, false);
	            // ...y tiene atributos y se quiere debilitar hay que eliminar sus atributos
	            if (!tr.getListaAtributos().isEmpty()) {	
	                gui.setDatos(
	                        UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WEAK_RELATION) + " \"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING2) + "\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE), null, TC.EliminarAtributosRelacion, tr, null));
	                gui.setActiva(0);
	            }
	            // ...y tiene una entidad débil hay que cambiar la cardinalidad
	            else if (numDebiles == 1 && respuesta1 != true) {
	                gui.setDatos(
	                		UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WEAK_RELATION) + "\"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.MODIFYING_CARDINALITY) + ".\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE), null, TC.ModificarCardinalidadRelacion_1a1, tr, null));
	                gui.setActiva(0);
	            } else {
	            	//Modificamos la relacion
	            	tr.getListaAtributos().clear();
	                getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
	            }

            }
        } else {
            getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
        }
		
	}

}
