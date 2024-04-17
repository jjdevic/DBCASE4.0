package controlador.comandos.Relacion;

import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import controlador.Factorias.FactoriaMsj;
import excepciones.ExceptionAp;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;

public class ComandoCrearRelacionDebil extends Comando{

	public ComandoCrearRelacionDebil(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) throws ExceptionAp {
		Contexto contexto = null, c_factibleRelacion;
		Vector<Object> v_datos = (Vector<Object>) datos;
		
		//Extraer datos de la entrada
    	TransferEntidad te = (TransferEntidad) v_datos.get(0);
    	Point2D p = (Point2D) v_datos.get(1);
    	String nom_relacion = (String) v_datos.get(2);
    	TransferEntidad entidad_fuerte = (TransferEntidad) v_datos.get(3);
		
		//Comprobar si se puede insertar la relacion debil
        TransferRelacion tr = new TransferRelacion();
        tr.setPosicion(p);
        tr.setNombre(nom_relacion);
        tr.setListaAtributos(new Vector());
        tr.setListaEntidadesYAridades(new Vector());
        tr.setListaRestricciones(new Vector());
        tr.setListaUniques(new Vector());
        tr.setTipo("Debil");
        
        c_factibleRelacion = getFactoriaServicios().getServicioRelaciones().SePuedeAnadirRelacion(tr);
        
        //Informar del error si lo hay
        if(!c_factibleRelacion.isExito()) {
        	JOptionPane.showMessageDialog(null, FactoriaMsj.getMsj(c_factibleRelacion.getMensaje()),
        			Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
        } 
        else {
        	//Insertar relacion
        	contexto = getFactoriaServicios().getServicioRelaciones().anadirRelacion(tr, 0);
        	tratarContexto(contexto);
        	
        	//Unir la entidad fuerte con la relación
            Vector<Object> v = new Vector<Object>();
            v.add(tr);
            v.add(entidad_fuerte);
            v.add(Integer.toString(0));//Inicio
            v.add("1");//Fin
            v.add("");//Rol
            //Incluimos en el vector MarcadaConCardinalidad(false), MarcadaConParticipacion(false), MarcadaConMinMax(false)
            v.add(true);
            v.add(false);
            v.add(false);
            
            contexto = ejecutarComando(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, v);
        	tratarContexto(contexto);

            //Unir la entidad debil con la relación
            Vector<Object> w = new Vector<Object>();
            w.add(tr);
            w.add(te);
            w.add(Integer.toString(1));//Inicio
            w.add("n");//Fin
            w.add("");//Rol
            //Incluimos en el vector MarcadaConCardinalidad(true), MarcadaConParticipacion(false), MarcadaConMinMax(false)
            w.add(true);
            w.add(false);
            w.add(false);
            w.add(false); //Hacemos que CardinalidadMaxima1 sea falso
            
            contexto = ejecutarComando(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, w);
        }
        return contexto;
	}

}
