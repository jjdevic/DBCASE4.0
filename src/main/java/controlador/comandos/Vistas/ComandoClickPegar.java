package controlador.comandos.Vistas;

import java.awt.geom.Point2D;
import java.util.Vector;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;

public class ComandoClickPegar extends Comando{

	public ComandoClickPegar(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		if (ctrl.getCopiado() instanceof TransferEntidad) {
            TransferEntidad te = (TransferEntidad) ctrl.getCopiado();
            int p = te.getPegado();
            TransferEntidad nueva = new TransferEntidad();
            Point2D punto = (Point2D) datos;
            nueva.setPosicion(punto);
            nueva.setNombre(te.getNombre() + p);
            nueva.setDebil(te.isDebil());
            nueva.setListaAtributos(new Vector());
            nueva.setListaRelaciones(new Vector());
            nueva.setListaClavesPrimarias(new Vector());
            nueva.setListaRestricciones(new Vector());
            nueva.setListaUniques(new Vector());
            ctrl.mensajeDesde_GUI(TC.GUIInsertarEntidad_Click_BotonInsertar, nueva);

            /*Vector<TransferAtributo> atributos = te.getListaAtributos();
		        for (int i = 0; i < atributos.size(); ++i) {
			        for (int j = 0; j < ctrl.listaAtributos.size(); ++j) {
                    //System.out.println(atributos.get(i));
                    //System.out.println(ctrl.listaAtributos.get(j).getIdAtributo());
                    if (String.valueOf(atributos.get(i)).equals(String.valueOf(ctrl.listaAtributos.get(j).getIdAtributo()))) {
                        Vector<Object> v = new Vector<Object>();
                        v.add(nueva);
                        TransferAtributo TA = ctrl.listaAtributos.get(j);
                        TransferAtributo nuevoTA = new TransferAtributo(ctrl);
                        double x = nueva.getPosicion().getX();
                        double y = nueva.getPosicion().getY();
                        nuevoTA.setPosicion(new Point2D.Double(x,y));
                        nuevoTA.setClavePrimaria(TA.getClavePrimaria());
                        nuevoTA.setCompuesto(TA.getCompuesto());
                        nuevoTA.setDominio(TA.getDominio());
                        nuevoTA.setFrecuencia(TA.getFrecuencia());
                        nuevoTA.setIdAtributo(TA.getIdAtributo() + 10);
                        nuevoTA.setListaComponentes(TA.getListaComponentes());
                        nuevoTA.setListaRestricciones(TA.getListaComponentes());
                        nuevoTA.setMultivalorado(TA.getMultivalorado());
                        nuevoTA.setNombre(TA.getNombre());
                        nuevoTA.setNotnull(TA.getNotnull());
                        nuevoTA.setUnique(TA.getUnique());
                        nuevoTA.setVolumen(TA.getVolumen());
                        v.add(nuevoTA);
                        v.add("10");
                        mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
				    }
			    }
		    }*/

            //nueva.setListaClavesPrimarias(te.getListaClavesPrimarias());
            nueva.setListaRestricciones(te.getListaRestricciones());
            nueva.setListaUniques(te.getListaUniques());
            //nueva.setIdEntidad(te.getIdEntidad() + 10);
            nueva.setFrecuencia(te.getFrecuencia());
            nueva.setVolumen(te.getVolumen());
            nueva.setOffsetAttr(te.getOffsetAttr());
            te.setPegado(p + 1);
            ActualizaArbol(te);
        }

        if (ctrl.getCopiado() instanceof TransferRelacion) {
            TransferRelacion tr = (TransferRelacion) ctrl.getCopiado();
            int p = tr.getPegado();
            TransferRelacion nueva = new TransferRelacion();
            Point2D punto = (Point2D) datos;
            nueva.setPosicion(punto);
            if (tr.getTipo().equals("IsA")) {
                ctrl.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, tr.getPosicion());
            } else {

                nueva.setNombre(tr.getNombre() + p);
                nueva.setCheckQuitarFlechas(tr.getCheckQuitarFlechas());
                nueva.setListaEntidadesYAridades(new Vector<EntidadYAridad>());
                nueva.setFrecuencia(tr.getFrecuencia());
                nueva.setIdRelacion(tr.getIdRelacion() + 10);
                nueva.setListaRestricciones(tr.getListaRestricciones());
                nueva.setListaUniques(tr.getListaUniques());
                nueva.setOffsetAttr(tr.getOffsetAttr());
                nueva.setRelacionConCardinalidad(tr.getRelacionConCardinalidad());
                nueva.setRelacionConCardinalidad1(tr.getRelacionConCardinalidad1());
                nueva.setRelacionConMinMax(tr.getRelacionConMinMax());
                nueva.setRelacionConParticipacion(tr.getRelacionConParticipacion());
                nueva.setVolumen(tr.getVolumen());
                nueva.setRol(tr.getRol());
                nueva.setTipo(tr.getTipo());
                nueva.setListaAtributos(new Vector());
                ctrl.mensajeDesde_GUI(TC.GUIInsertarRelacion_Click_BotonInsertar, nueva);
                Vector<TransferAtributo> atributos = tr.getListaAtributos();

                /*for (int i = 0; i < atributos.size(); ++i) {
                    for (int j = 0; j < ctrl.listaAtributos.size(); ++j) {
                        //System.out.println(atributos.get(i));
                        //System.out.println(ctrl.listaAtributos.get(j).getIdAtributo());
                        if (String.valueOf(atributos.get(i)).equals(String.valueOf(ctrl.listaAtributos.get(j).getIdAtributo()))) {
                            Vector<Object> v = new Vector<Object>();
                            v.add(nueva);
                            TransferAtributo TA = ctrl.listaAtributos.get(j);
                            TransferAtributo nuevoTA = new TransferAtributo(ctrl);
                            double x = nueva.getPosicion().getX();
                            double y = nueva.getPosicion().getY();
                            nuevoTA.setPosicion(new Point2D.Double(x,y));
                            nuevoTA.setClavePrimaria(TA.getClavePrimaria());
                            nuevoTA.setCompuesto(TA.getCompuesto());
                            nuevoTA.setDominio(TA.getDominio());
                            nuevoTA.setFrecuencia(TA.getFrecuencia());
                            nuevoTA.setIdAtributo(TA.getIdAtributo() + 10);
                            nuevoTA.setListaComponentes(TA.getListaComponentes());
                            nuevoTA.setListaRestricciones(TA.getListaComponentes());
                            nuevoTA.setMultivalorado(TA.getMultivalorado());
                            nuevoTA.setNombre(TA.getNombre());
                            nuevoTA.setNotnull(TA.getNotnull());
                            nuevoTA.setUnique(TA.getUnique());
                            nuevoTA.setVolumen(TA.getVolumen());
                            v.add(nuevoTA);
                            v.add("10");
                            mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
                        }
                    }
                }*/

                tr.setPegado(p + 1);
                ActualizaArbol(tr);
            }
        }

        if (ctrl.getCopiado() instanceof TransferAtributo) {
            return;
            /*TransferAtributo ta = (TransferAtributo) copiado;
            int p = ta.getPegado();
            TransferAtributo nuevo = new TransferAtributo(ctrl);
            Point2D punto = (Point2D) datos;
            nuevo.setPosicion(punto);
            //obtenemos a que elemento pertenece
            Transfer elem_mod = ctrl.getTheServiciosAtributos().eliminaRefererenciasAlAtributo(ta);
            nuevo.setClavePrimaria(ta.getClavePrimaria());
            nuevo.setCompuesto(ta.getCompuesto());
            nuevo.setDominio(ta.getDominio());
            nuevo.setNombre(ta.getNombre() + Integer.toString(p));
            nuevo.setIdAtributo(ta.getIdAtributo() + 10);
            nuevo.setMultivalorado(ta.getMultivalorado());
            nuevo.setNotnull(ta.getNotnull());
            nuevo.setUnique(ta.getUnique());
            nuevo.setSubatributo(ta.isSubatributo());
            nuevo.setVolumen(ta.getVolumen());
            nuevo.setListaComponentes(ta.getListaComponentes());
            nuevo.setFrecuencia(ta.getFrecuencia());
            nuevo.setListaRestricciones(ta.getListaRestricciones());
            double x = elem_mod.getPosicion().getX();
            double y = elem_mod.getPosicion().getY();
            nuevo.setPosicion(new Point2D.Double(x,y));

            if(elem_mod instanceof TransferEntidad) {
                Vector<Object> v = new Vector<Object>();
                v.add(elem_mod);
                v.add(nuevo);
                v.add("10");
                mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
            }

            else if(elem_mod instanceof TransferRelacion) {
                Vector<Object> v = new Vector<Object>();
                v.add(elem_mod);
                v.add(nuevo);
                v.add("10");
                mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
            }

            else if(elem_mod instanceof TransferAtributo) {
                Vector<Object> v = new Vector<Object>();
                v.add(elem_mod);
                v.add(nuevo);
                v.add("10");
                mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v);
            }

            ta.setPegado(p + 1);
            ActualizaArbol(ta);
            */
        }
	}

}
