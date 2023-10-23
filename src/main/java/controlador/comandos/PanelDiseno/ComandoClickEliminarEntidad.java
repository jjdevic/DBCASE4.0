package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Controlador;
import controlador.comandos.Comando;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;

public class ComandoClickEliminarEntidad extends Comando {

	public ComandoClickEliminarEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		
            Vector<Object> v = (Vector<Object>) datos;
            TransferEntidad te = (TransferEntidad) v.get(0);
            boolean preguntar = (Boolean) v.get(1);
            int intAux = (int) v.get(2);
            int respuesta = 0;
            
            if (!ctrl.getConfirmarEliminaciones()) preguntar = false;
            if (preguntar) {
                String tieneAtributos = "";
                if (!te.getListaAtributos().isEmpty())
                    tieneAtributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING) + "\n";
                String tieneRelacion = "";
                if (te.isDebil()) tieneRelacion = Lenguaje.text(Lenguaje.WARNING_DELETE_WEAK_RELATION) + "\n";
                respuesta = ctrl.getPanelOpciones().setActiva(
                        Lenguaje.text(Lenguaje.ENTITY) + " \"" + te.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                                tieneAtributos + tieneRelacion + Lenguaje.text(Lenguaje.WISH_CONTINUE),
                        Lenguaje.text(Lenguaje.DELETE_ENTITY));
            }
            //Si quiere borrar la entidad
            /*Se entrará con preguntar a false si se viene de eliminar una relación débil
             * (para borrar también la entidad y sus atributos)*/
            if ((respuesta == 0) || (!preguntar)) {
                // Eliminamos sus atributos
	            Vector lista_atributos = te.getListaAtributos();
	            int conta = 0;
	            TransferAtributo ta = new TransferAtributo(ctrl);
	            while (lista_atributos != null && conta < lista_atributos.size()) {
	                String idAtributo = (String) lista_atributos.get(conta);
	                ta.setIdAtributo(Integer.parseInt(idAtributo));
	                ctrl.getTheServiciosAtributos().eliminarAtributo(ta, 1);
	                conta++;
	            }
            
            //Si la entidad es débil eliminamos la relación débil asociada
	            if (te.isDebil()) {
	                Vector<TransferRelacion> lista_rel = ctrl.getTheServiciosRelaciones().ListaDeRelacionesNoVoid();
	                int cont = 0, aux = 0;
	                boolean encontrado = false;
	                EntidadYAridad eya;
	                int idEntidad;
	                TransferRelacion tr = new TransferRelacion();
	                //Para cada relación
	                while (cont < lista_rel.size()) {
	                    //Si la relación es débil
	                    if (lista_rel.get(cont).getTipo().equals("Debil")) {
	                        //Compruebo si las entidades asociadas son la entidad débil que se va a eliminar
	                        while ((!encontrado) && (aux < lista_rel.get(cont).getListaEntidadesYAridades().size())) {
	                            eya = (EntidadYAridad) (lista_rel.get(cont).getListaEntidadesYAridades().get(aux));
	                            idEntidad = eya.getEntidad();
	                            if (te.getIdEntidad() == idEntidad) {
	                                tr.setIdRelacion(lista_rel.get(cont).getIdRelacion());
	                                ctrl.getTheServiciosRelaciones().eliminarRelacionNormal(tr, 1);
	                                encontrado = true;
	                            }
	                            aux++;
	                        }
	                        aux = 0;
	                    }
	                    cont++;
	                }
	            }
            // Eliminamos la entidad
            ctrl.getTheServiciosEntidades().eliminarEntidad(te, intAux);
            } 
	}
}
