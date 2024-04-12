package controlador.comandos.Entidad;

import java.util.Objects;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import excepciones.ExceptionAp;
import modelo.transfers.Transfer;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.Lenguaje;

public class ComandoModificarEntidad extends Comando{

	public ComandoModificarEntidad(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
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
        	
        	//Comprobar que no es una entidad fuerte que identifica a una entidad debil
        	if (!te.isDebil() && getFactoriaServicios().getServicioRelaciones().tieneHermanoDebil(te)) {
        		debilitar = false;
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ALREADY_WEAK_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
        	}
        	else {
	        	tr_aux = (TransferRelacion) v.get(3);
	        	tEntidadFuerte = (TransferEntidad) v.get(4);
        	}
        }
        
        //Si se ha modificado su nombre la renombramos
        if (!Objects.equals(te.getNombre(), nuevoNombre)) {
            Vector<Object> v1 = new Vector<Object>();
            v1.add(te);
            v1.add(nuevoNombre);
            tratarContexto(getFactoriaServicios().getServicioEntidades().renombrarEntidad(v1));
        }
        
        //Si se quiere debilitar añadimos la relación
        if (debilitar) {
        	//Recopilar datos necesarios
        	Vector<Object> v_crearRelDebil = new Vector<Object>();
        	v_crearRelDebil.add(te);
        	
        	//Desplazar un poco la relacion para que no se impriman juntas
        	tr_aux.getPosicion().setLocation(tr_aux.getPosicion().getX(), tr_aux.getPosicion().getY() + 150);
        	v_crearRelDebil.add(tr_aux.getPosicion());
        	v_crearRelDebil.add(tr_aux.getNombre());
        	v_crearRelDebil.add(tEntidadFuerte);
        	
        	//Usar comando para crear relacion debil
        	tratarContexto(ejecutarComando(TC.Controlador_InsertarRelacionDebil, v_crearRelDebil));
        	
        	//Debilitar la entidad
        	resultado = getFactoriaServicios().getServicioEntidades().debilitarEntidad(te);
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
