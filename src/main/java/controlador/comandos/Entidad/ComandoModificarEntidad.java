package controlador.comandos.Entidad;

import java.util.Objects;
import java.util.Vector;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import modelo.transfers.Transfer;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;

public class ComandoModificarEntidad extends Comando{

	public ComandoModificarEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		Contexto resultado = null;
		Vector<Object> v = (Vector<Object>) datos;
        TransferEntidad te = (TransferEntidad) v.get(0);
        boolean eraDebil = te.isDebil();
        String nuevoNombre = (String) v.get(1);
        boolean debilitar = (boolean) v.get(2);
        TransferRelacion tr_aux = null;
        TransferEntidad tEntidadFuerte = null;
        
        //Si se quiere debilitar recogemos los datos necesarios
        if(debilitar) {
        	tr_aux = (TransferRelacion) v.get(3);
        	tEntidadFuerte = (TransferEntidad) v.get(4);
        }
        
        //Si se ha modificado su nombre la renombramos
        if (!Objects.equals(te.getNombre(), nuevoNombre)) {
            Vector<Object> v1 = new Vector<Object>();
            v1.add(te);
            v1.add(nuevoNombre);
            tratarContexto(getFactoriaServicios().getServicioEntidades().renombrarEntidad(v1));
        }
        
        //Si se ha debilitado añadimos la relación
        if (debilitar) {
        	//Recopilar datos necesarios
        	Vector<Object> v_crearRelDebil = new Vector<Object>();
        	v_crearRelDebil.add(te);
        	v_crearRelDebil.add(tr_aux.getPosicion());
        	v_crearRelDebil.add(tr_aux.getNombre());
        	v_crearRelDebil.add(tEntidadFuerte);
        	
        	//Usar comando para crear relacion debil
        	resultado = ejecutarComando(TC.Controlador_InsertarRelacionDebil, v_crearRelDebil);
        } 
        else if (eraDebil) {
        	getFactoriaServicios().getServicioEntidades().debilitarEntidad(te);
            Vector<TransferRelacion> lista_relaciones = (Vector<TransferRelacion>) ctrl.mensaje(TC.ObtenerListaRelaciones, null);
            //getFactoriaServicios().getServicioRelaciones().restablecerDebilidadRelaciones();
            for (TransferRelacion tr : lista_relaciones) {
                Vector<EntidadYAridad> eya = tr.getListaEntidadesYAridades();
                for (EntidadYAridad entidadYAridad : eya) {
                    if (entidadYAridad.getEntidad() == te.getIdEntidad() && tr.getTipo().equals("Debil"))
                        resultado = getFactoriaServicios().getServicioRelaciones().debilitarRelacion(tr);
                }
            }
        }
        getFactoriaServicios().getServicioSistema().reset();
        return resultado;
	}

}
