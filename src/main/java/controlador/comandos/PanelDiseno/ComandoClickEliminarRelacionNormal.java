package controlador.comandos.PanelDiseno;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;

public class ComandoClickEliminarRelacionNormal extends Comando{

	public ComandoClickEliminarRelacionNormal(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		Vector<Object> v = (Vector<Object>) datos;
        TransferRelacion tr = (TransferRelacion) v.get(0);
        int intAux = (int) v.get(2);
        Vector vtaAux = tr.getListaAtributos();
        Vector<TransferAtributo> vta = new Vector<TransferAtributo>();
        Vector<EntidadYAridad> veya = tr.getListaEntidadesYAridades();
        Vector<TransferEntidad> vte = new Vector<TransferEntidad>();

        for (Object aux : vtaAux) {
            int id = Integer.parseInt((String) aux);
            for (TransferAtributo listaAtributo : ctrl.getListaAtributos()) {
                if (id == listaAtributo.getIdAtributo()) vta.add(listaAtributo);
            }
        }

        for (EntidadYAridad entidadYAridad : veya) {
            int id = entidadYAridad.getEntidad();
            for (TransferEntidad listaEntidade : ctrl.getListaEntidades()) {
                if (id == listaEntidade.getIdEntidad()) vte.add(listaEntidade);
            }
        }

        boolean preguntar = (Boolean) v.get(1);
        int respuesta = 0;
        if (!ctrl.getConfirmarEliminaciones()) preguntar = false;
        if (preguntar) {
            String tieneAtributos = "";
            if (!tr.getListaAtributos().isEmpty())
                tieneAtributos = Lenguaje.text(Lenguaje.DELETE_ATTRIBUTES_WARNING) + "\n";
            String tieneEntidad = "";
            //Informar de que también se va a eliminar la entidad débil asociada
            if (tr.getTipo().equals("Debil"))
                tieneEntidad = Lenguaje.text(Lenguaje.WARNING_DELETE_WEAK_ENTITY) + "\n";
            respuesta = ctrl.getPanelOpciones().setActiva(
                    Lenguaje.text(Lenguaje.THE_RELATION) + " \"" + tr.getNombre() + "\" " +
                            Lenguaje.text(Lenguaje.REMOVE_FROM_SYSTEM) + "\n" +
                            tieneAtributos + tieneEntidad +
                            Lenguaje.text(Lenguaje.WISH_CONTINUE),
                    Lenguaje.text(Lenguaje.DELETE_RELATION));
        }
        //Si se desea eliminar la relación
        if (respuesta == 0) {
            // Eliminamos sus atributos
            Vector lista_atributos = tr.getListaAtributos();
            int conta = 0;
            TransferAtributo ta = new TransferAtributo();
            while (conta < lista_atributos.size()) {
                String idAtributo = (String) lista_atributos.get(conta);
                ta.setIdAtributo(Integer.parseInt(idAtributo));
                Contexto ctxt = getFactoriaServicios().getServicioAtributos().eliminarAtributo(ta, 1);
                tratarContexto(ctxt);
                conta++;
            }
            //Se elimina también la entidad débil asociada
            if (tr.getTipo().equals("Debil")) {
                Vector lista_entidades = tr.getListaEntidadesYAridades();
                int cont = 0;
                TransferEntidad te = new TransferEntidad();
                while (cont < lista_entidades.size()) {
                    EntidadYAridad eya = (EntidadYAridad) (tr.getListaEntidadesYAridades().get(cont));
                    int idEntidad = eya.getEntidad();
                    te.setIdEntidad(idEntidad);
                    //Tengo que rellenar los atributos de te
                    Vector<TransferEntidad> auxiliar = null/* TODO ctrl.getTheGUIQuitarEntidadARelacion().getListaEntidades()*/; //falla aqui				if (auxiliar == null)
                    auxiliar = ctrl.getListaEntidades();
                    boolean encontrado = false;
                    int i = 0;
                    if (auxiliar != null) {
                        while ((!encontrado) && (i < auxiliar.size())) {
                            if (auxiliar.get(i).getIdEntidad() == idEntidad) {
                                encontrado = true;
                                te.setListaAtributos(auxiliar.get(i).getListaAtributos());
                            } else
                                i++;
                        }
                    }
                    //Elimino también la entidad débil
                    if (getFactoriaServicios().getServicioEntidades().esDebil(idEntidad)) {
                        //Esto es para borrar los atributos de la entidad débil y la propia entidad débil
                        Vector<Object> vAux = new Vector<Object>();
                        vAux.add(te);
                        vAux.add(false);
                        if (vAux.size() == 2) vAux.add(1);
                        else vAux.set(2, 1);
                        ctrl.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarEntidad, vAux);
                    }
                    cont++;
                }
            }

            // Eliminamos la relacion
            getFactoriaServicios().getServicioAgregaciones().eliminarAgregacion(tr);
            getFactoriaServicios().getServicioEntidades().eliminarRelacionDeEntidad(tr);
            getFactoriaServicios().getServicioRelaciones().eliminarRelacionNormal(tr, intAux);
        }
	}

}
