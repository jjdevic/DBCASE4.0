package controlador.comandos.Entidad;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import misc.UtilsFunc;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;
import vista.frames.Parent_GUI;

public class ComandoClickEliminarEntidad extends Comando {

	public ComandoClickEliminarEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		Contexto resultado = null;
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
            Parent_GUI gui = ctrl.getFactoriaGUI().getGUI(TC.GUI_Pregunta, 
            		UtilsFunc.crearVector(Lenguaje.text(Lenguaje.ENTITY) + " \"" + te.getNombre() + "\" " + Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                            tieneAtributos + tieneRelacion + Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DELETE_ENTITY), null), false);
            respuesta = gui.setActiva(0);
        }
        //Si quiere borrar la entidad
        /*Se entrará con preguntar a false si se viene de eliminar una relación débil
         * (para borrar también la entidad y sus atributos)*/
        if (respuesta == 0 || !preguntar) {
            // Eliminamos sus atributos
            Vector lista_atributos = te.getListaAtributos();
            int conta = 0;
            TransferAtributo ta = new TransferAtributo();
            while (lista_atributos != null && conta < lista_atributos.size()) {
                String idAtributo = (String) lista_atributos.get(conta);
                ta.setIdAtributo(Integer.parseInt(idAtributo));
                Contexto ctxt = getFactoriaServicios().getServicioAtributos().eliminarAtributo(ta, 1);
                tratarContexto(ctxt);
                conta++;
            }
        
            //Si la entidad es débil eliminamos la relación débil asociada
            if (te.isDebil()) {
                Vector<TransferRelacion> lista_rel = getFactoriaServicios().getServicioRelaciones().ListaDeRelacionesNoVoid();
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
                                getFactoriaServicios().getServicioRelaciones().eliminarRelacionNormal(tr, 1);
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
            resultado = getFactoriaServicios().getServicioEntidades().eliminarEntidad(te, intAux);
        } 
        return resultado;
	}
}
