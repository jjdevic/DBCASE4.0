package controlador.comandos.Relacion;

import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import excepciones.ExceptionAp;
import misc.UtilsFunc;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoDebilitarRelacion extends Comando{

	public ComandoDebilitarRelacion(Controlador ctrl) {
		super(ctrl);
	}

	/*Aunque desde el panel de diseño no se puede debilitar una relación este caso sigue
     * utilizándose cuando se crea una entidad débil ya que debe generarse también una
     * relación débil asociada a ella.*/
	
	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		Contexto resultado = null;
		TransferRelacion tr = (TransferRelacion) datos;
        //Si es una relacion fuerte...
        if (tr.getTipo().equals("Normal")) {
            int numDebiles = getFactoriaServicios().getServicioRelaciones().numEntidadesDebiles(tr);
            // ...y tiene más de una entidad débil no se puede debilitar
            if (numDebiles > 1) {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATION_WEAK_ENTITIES), Lenguaje.text(Lenguaje.ERROR), 0);
            } 
            else {
	            int respuesta1 = 1;
	            int respuesta2 = 1;
	            
	            // ...y tiene atributos y se quiere debilitar hay que eliminar sus atributos
	            if (!tr.getListaAtributos().isEmpty()) {	
	            	Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
	            			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WEAK_RELATION) + " \"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING2) + "\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE), null), false);
	                respuesta1  = gui.setActiva(0);
	            }
	            // ...y tiene una entidad débil hay que cambiar la cardinalidad
	            else if (numDebiles == 1 && respuesta1 == 0) {
	            	Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
	            			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WEAK_RELATION) + "\"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.MODIFYING_CARDINALITY) + ".\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE), null), false);
	                gui.setActiva(0);
	            } else {
	            	//Modificamos la relacion
	            	tr.getListaAtributos().clear();
	                resultado = getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
	            }

            }
        } else {
            resultado = getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
        }
		return resultado;
	}

}
