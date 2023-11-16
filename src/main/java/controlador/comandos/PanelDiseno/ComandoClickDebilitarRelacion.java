package controlador.comandos.PanelDiseno;

import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Contexto;
import controlador.Controlador;
import controlador.comandos.Comando;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;

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
            int numDebiles = ctrl.getTheServiciosRelaciones().numEntidadesDebiles(tr);
            // ...y tiene más de una entidad débil no se puede debilitar
            if (numDebiles > 1) {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.RELATION_WEAK_ENTITIES), Lenguaje.text(Lenguaje.ERROR), 0);
            } 
            else {
	            int respuesta1 = -1;//-1 no hay conflicto, 0 el usuario dice SI, 1 el usuario dice NO
	            int respuesta2 = -1;
	            // ...y tiene atributos y se quiere debilitar hay que eliminar sus atributos
	            if (!tr.getListaAtributos().isEmpty()) {
	                respuesta1 = ctrl.getPanelOpciones().setActiva(
	                        Lenguaje.text(Lenguaje.WEAK_RELATION) + " \"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING2) + "\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE));
	            }
	            // ...y tiene una entidad débil hay que cambiar la cardinalidad
	            if (numDebiles == 1 && respuesta1 != 1) {
	                respuesta2 = ctrl.getPanelOpciones().setActiva(
	                        Lenguaje.text(Lenguaje.WEAK_RELATION) + "\"" + tr.getNombre() + "\"" +
	                                Lenguaje.text(Lenguaje.MODIFYING_CARDINALITY) + ".\n" +
	                                Lenguaje.text(Lenguaje.WISH_CONTINUE),
	                        Lenguaje.text(Lenguaje.DBCASE));
	            }
	            if (respuesta2 == 0 && respuesta1 != 1) {
	                //Aqui se fija la cardinalidad de la entidad débil como de 1 a 1.
	                Vector<Object> v = new Vector<Object>();
	                EntidadYAridad informacion;
	                int i = 0;
	                boolean actualizado = false;
	                while ((!actualizado) && (i < tr.getListaEntidadesYAridades().size())) {
	                    informacion = (EntidadYAridad) (tr.getListaEntidadesYAridades().get(i));
	                    int idEntidad = informacion.getEntidad();
	                    if (ctrl.getTheServiciosEntidades().esDebil(idEntidad)) {
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
	                        ctrl.getTheServiciosRelaciones().aridadEntidadUnoUno(v);
	                    }
	                    i++;
	                }
	            }
	            if (respuesta1 == 0 && respuesta2 != 1) {
	                // Eliminamos sus atributos
	                Vector lista_atributos = tr.getListaAtributos();
	                int cont = 0;
	                TransferAtributo ta = new TransferAtributo();
	                while (cont < lista_atributos.size()) {
	                    String idAtributo = (String) lista_atributos.get(cont);
	                    ta.setIdAtributo(Integer.parseInt(idAtributo));
	                    
	                    Contexto ctxt = ctrl.getTheServiciosAtributos().eliminarAtributo(ta, 1);
	                    ctrl.tratarContexto(ctxt);
	                    cont++;
	                }
	            }
	            if (respuesta1 != 1 && respuesta2 != 1) {
	                // Modificamos la relacion
	                tr.getListaAtributos().clear();
	                ctrl.getTheServiciosRelaciones().debilitarRelacion(tr);
	            }
            }
        } else {
            ctrl.getTheServiciosRelaciones().debilitarRelacion(tr);
        }
		
	}

}
