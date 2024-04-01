package controlador.comandos.Atributo;

import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;

public class ComandoModificarAtributo extends Comando {

	public ComandoModificarAtributo(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		Vector<Object> v = (Vector<Object>) datos;
        TransferAtributo ta = (TransferAtributo) v.get(0);
        
        ctrl.setAntigoNombreAtributo(ta.getNombre());
        ctrl.setAntiguoDominioAtributo(ta.getDominio());
        ctrl.setAntiguoCompuestoAtributo(ta.getCompuesto());
        ctrl.setAntiguoMultivaloradoAtributo(ta.getMultivalorado());
        ctrl.setAntiguoNotnullAtributo(ta.getNotnull());
        ctrl.setAntiguoUniqueAtributo(ta.getUnique());
        ctrl.setAntiguoClavePrimaria(ta.getClavePrimaria());
        
        String nuevoNombre = (String) v.get(1);
        boolean clavePrimaraSelected = (boolean) v.get(2);
        boolean compuestoSelected = (boolean) v.get(3);
        boolean notNullSelected = (boolean) v.get(4);
        boolean uniqueSelected = (boolean) v.get(5);
        boolean multivaloradoSelected = (boolean) v.get(6);
        
        Contexto ctxt;

        //Creamos un vector para renombrar
        Vector<Object> vRenombrar = new Vector<Object>();
        vRenombrar.add(ta);
        vRenombrar.add(nuevoNombre);
        if (!ta.getNombre().equals(nuevoNombre)) {
            ctxt = getFactoriaServicios().getServicioAtributos().renombrarAtributo(vRenombrar);
            tratarContexto(ctxt);
        }
        //Creamos un vector para modificar el dominio
        Vector<Object> vDominio = new Vector<Object>();
        vDominio.add(ta);
        String dominio = (String) v.get(7);
        if (v.size() == 9) { //Significa que el vector tiene un campo tamaño
            String tamano = (String) v.get(8);
            vDominio.add(dominio + "(" + tamano + ")");
            vDominio.add(tamano);
        } else {
            vDominio.add(dominio);
        }
        ctxt = getFactoriaServicios().getServicioAtributos().editarDomnioAtributo(vDominio);
        tratarContexto(ctxt);
        //Buscamos si el atributo pertenece a una entidad y si es asi a cual

        Vector<TransferEntidad> entidades = (Vector<TransferEntidad>) ctrl.mensaje(TC.ObtenerListaEntidades, null);
        TransferEntidad te = new TransferEntidad();
        boolean encontrado = false;
        for (TransferEntidad entidade : entidades) {
            Vector<String> atributos = entidade.getListaAtributos();
            for (String atributo : atributos) {
                if (atributo.equals(Integer.toString(ta.getIdAtributo()))) {
                    te = entidade;
                    encontrado = true;
                }
            }
        }
        //Modificamos los valores ClavePrimaria, Compuesto, Unique, NotNull y Multivalorado si es necesario
        if (encontrado && clavePrimaraSelected != ta.isClavePrimaria()) {
            Vector<Object> vClavePrimaria = new Vector<Object>();
            vClavePrimaria.add(ta);
            vClavePrimaria.add(te);
            //vClavePrimaria.add(0);
            ctxt = getFactoriaServicios().getServicioAtributos().editarClavePrimariaAtributo(vClavePrimaria);
            tratarContexto(ctxt);
        }
        if (compuestoSelected != ta.getCompuesto()) {
            ctxt = getFactoriaServicios().getServicioAtributos().editarCompuestoAtributo(ta);
            tratarContexto(ctxt);
        }
        if (uniqueSelected != ta.getUnique()) {
            ctxt = getFactoriaServicios().getServicioAtributos().editarUniqueAtributo(ta);
            tratarContexto(ctxt);
        }
        if (notNullSelected != ta.getNotnull()) {
            ctxt = getFactoriaServicios().getServicioAtributos().editarNotNullAtributo(ta);
            tratarContexto(ctxt);
        }
        if (multivaloradoSelected != ta.isMultivalorado()) {
        	ctxt = getFactoriaServicios().getServicioAtributos().editarMultivaloradoAtributo(ta);
        	tratarContexto(ctxt);
        }
        getFactoriaServicios().getServicioSistema().reset();
        return null;
	}

}
