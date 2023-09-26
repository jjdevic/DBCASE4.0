package modelo.servicios;

import java.util.Iterator;
import java.util.Vector;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.DAOAgregaciones;
import persistencia.DAOAtributos;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ServiciosAgregaciones {
	private Controlador controlador;
	
	//Devuelve actualizada la lista de agregaciones
	public Vector <TransferAgregacion> ListaDeAgregaciones(){
		// Creamos el DAO de agregaciones
		DAOAgregaciones dao = new DAOAgregaciones(this.controlador.getPath());
		// Utilizando el DAO obtenemos la lista de agregaciones
		Vector <TransferAgregacion> lista_agregaciones = dao.ListaDeAgregaciones();
		controlador.mensajeDesde_AG(TC.SAG_ListarAgregacion_HECHO, lista_agregaciones);
		// Se lo devolvemos al controlador
		return lista_agregaciones;
	}
	
	public void anadirAgregacion(TransferAgregacion ta){
		if (ta.getNombre().isEmpty()){
			controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreVacio, null);
			return;
		}
		DAOAgregaciones daoAgregaciones = new DAOAgregaciones(this.controlador.getPath());
		Vector<TransferAgregacion> lista = daoAgregaciones.ListaDeAgregaciones();
		for (Iterator it = lista.iterator(); it.hasNext(); ){
			TransferAgregacion elem_tr = (TransferAgregacion)it.next();
			if (elem_tr.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeYaExiste,ta);
				return;
			}
		}
		DAOEntidades daoEntidades = new DAOEntidades(this.controlador.getPath());
		Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
		for (Iterator it = listaE.iterator(); it.hasNext(); ){
			TransferEntidad elem_te = (TransferEntidad)it.next();
			if (elem_te.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste,ta);
				return;
			}
		}
		
		DAORelaciones daoRelaciones = new DAORelaciones(this.controlador.getPath());
		Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();
		for (Iterator it = listaR.iterator(); it.hasNext(); ){
			TransferRelacion elem_tr = (TransferRelacion)it.next();
			if (elem_tr.getNombre().toLowerCase().equals(ta.getNombre().toLowerCase())){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste,ta);
				return;
			}
		}
		
		int id = daoAgregaciones.anadirAgregacion(ta);
		if (id==-1)	controlador.mensajeDesde_SR(TC.SAG_InsertarAgregacion_ERROR_DAO,ta);
		else{
			ta.setIdAgregacion(id);
			controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_HECHO, daoAgregaciones.consultarAgregacion(ta));
		}
	}
	//public boolean SePuedeAnadirAgregacion(TransferAgregacion te)???
	

	public boolean perteneceAgregacion(TransferRelacion rel) {
		// TODO Auto-generated method stub
		DAOAgregaciones daoAgre = new DAOAgregaciones(this.controlador.getPath());
		Vector <TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
		for(TransferAgregacion agre: agregaciones) {
			String idRelDeAgre = (String) agre.getListaRelaciones().get(0);
			if(idRelDeAgre.equals(Integer.toString(rel.getIdRelacion()))) {
				return true;
			}
		}
		return false;
	}
	
	public void renombrarAgregacion(TransferRelacion tr, String nuevoNombre){
		TransferAgregacion ta = this.buscarAgregaciondeRelacion(tr);
		Vector<Object> v = new Vector<Object>();
		v.add(ta);
		v.add(nuevoNombre);
		v.add(ta.getNombre());
		
		// Si el nuevo nombre es vacio -> ERROR
		if (nuevoNombre.isEmpty()){
			controlador.mensajeDesde_AG(TC.SAG_RenombrarAgregacion_ERROR_NombreVacio, v);
			return;
		}
		// Si hay una relacion que ya tiene el "nuevoNombre" -> ERROR
		DAORelaciones dao = new DAORelaciones(this.controlador.getPath());
		Vector<TransferRelacion> listaRelaciones = dao.ListaDeRelaciones();
		int i = 0;
		TransferRelacion rel;
		while (i<listaRelaciones.size()){
			rel = listaRelaciones.get(i);
			if (rel.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeRelYaExiste,ta);
				return;
			}
			i++;
		}
		// Si hay una entidad que ya tiene el "nuevoNombre" -> ERROR
		DAOEntidades daoEntidades = new DAOEntidades(this.controlador.getPath());
		Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();
		/*if (listaE == null){
			controlador.mensajeDesde_SR(TC.SR_RenombrarRelacion_ERROR_DAOEntidades,v);
			return;
		}*/
		for (Iterator it = listaE.iterator(); it.hasNext(); ){
			TransferEntidad elem_te = (TransferEntidad)it.next();
			if (elem_te.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeEntYaExiste,ta);
				return;
			}
		}
		// Si hay una agregacion distinta que ya tiene el "nuevoNombre" -> ERROR
		DAOAgregaciones daoAgreg = new DAOAgregaciones(this.controlador.getPath());
		Vector<TransferAgregacion> listaAgregaciones = daoAgreg.ListaDeAgregaciones();
		int j = 0;
		TransferAgregacion agreg;
		while (j<listaAgregaciones.size()){
			agreg = listaAgregaciones.get(j);
			if (agreg.getNombre().toLowerCase().equals(nuevoNombre.toLowerCase())&& agreg.getIdAgregacion()!=ta.getIdAgregacion()){
				controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_NombreDeYaExiste,ta);
				return;
			}
			j++;
		}
		// Modificamos el nombre
		ta.setNombre(nuevoNombre);
		if (daoAgreg.modificarAgregacion(ta)==false){
			controlador.mensajeDesde_AG(TC.SAG_InsertarAgregacion_ERROR_DAO, v);	
		}
		else 
			controlador.mensajeDesde_AG(TC.SAG_RenombrarAgregacion_HECHO, v);
		return;
	}
	
	private TransferAgregacion buscarAgregaciondeRelacion(TransferRelacion rel) {
		TransferAgregacion ta = new TransferAgregacion();
		DAOAgregaciones daoAgre = new DAOAgregaciones(this.controlador.getPath());
		Vector <TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
		for(TransferAgregacion agre : agregaciones) {
			if(agre.getListaRelaciones().contains(Integer.toString(rel.getIdRelacion())))
				ta = agre;
		}
		return ta;
	}

	public void eliminarAgregacion(TransferRelacion tr) {
		String idRel = Integer.toString(tr.getIdRelacion());
		DAOAgregaciones daoAgre = new DAOAgregaciones(this.controlador.getPath());
		Vector <TransferAgregacion> agregaciones = daoAgre.ListaDeAgregaciones();
		for(TransferAgregacion agre : agregaciones) {
			Vector relacion = agre.getListaRelaciones(); // solo tiene un elemento
			if(relacion.contains(idRel)) {
				daoAgre.borrarAgregacion(agre);
				controlador.mensajeDesde_AG(TC.SAG_EliminarAgregacion_HECHO, agre);
			}
		}
	}
	
	public void eliminarAgregacion (TransferAgregacion ta) {
		DAOAgregaciones daoAgre = new DAOAgregaciones(this.controlador.getPath());
		daoAgre.borrarAgregacion(ta);
		controlador.mensajeDesde_AG(TC.SAG_EliminarAgregacion_HECHO, ta);
	}
	
	public Controlador getControlador() {
		return controlador;
	}

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}
	
	public void anadirAtributo(Vector v){
		TransferAgregacion te = (TransferAgregacion) v.get(0);
		TransferAtributo ta = (TransferAtributo) v.get(1);
		// Si nombre de atributo es vacio -> ERROR
		if (ta.getNombre().isEmpty()){ this.controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoVacio, v); return; }
		
		// Si nombre de atributo ya existe en esa entidad-> ERROR
		DAOAtributos daoAtributos = new DAOAtributos(this.controlador);
		Vector<TransferAtributo> lista = daoAtributos.ListaDeAtributos(); //lista de todos los atributos
		if (lista == null){
			// este tipo de mensajes habra que modificarlos en el controlador para que 
			// el mensaje de error que lancen sea relativo a agregaciones no a entidades
			controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos,v);
			return;
		}
		for (int i=0; i<te.getListaAtributos().size();i++)
			if(daoAtributos.nombreDeAtributo((Integer.parseInt((String)te.getListaAtributos().get(i)))).toLowerCase().equals(ta.getNombre().toLowerCase())){ 
				controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_NombreDeAtributoYaExiste,v);
				return;
			}
		
		// Si hay tamano y no es un entero positivo -> ERROR
		if(v.size()==3){
			try{
				int tamano = Integer.parseInt((String) v.get(2));
				if(tamano<1){
					this.controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_TamanoEsNegativo, v); return;
				}			
			}
			catch(Exception e){
				this.controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_TamanoNoEsEntero, v); return;
			}
		}
		// Creamos el atributo
		// de momento al no representarse, esto no se utiliza ta.setPosicion(te.nextAttributePos(ta.getPosicion()));
		int idNuevoAtributo = daoAtributos.anadirAtributo(ta);
		if(idNuevoAtributo == -1){this.controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_DAOAtributos, v); return; }
		// Anadimos el atributo a la lista de atributos de la entidad
		ta.setIdAtributo(idNuevoAtributo);
		te.getListaAtributos().add(Integer.toString(idNuevoAtributo));
		
		DAOAgregaciones daoAgregaciones = new DAOAgregaciones(this.controlador.getPath());
		if (!daoAgregaciones.modificarAgregacion(te)){
			this.controlador.mensajeDesde_SE(TC.SE_AnadirAtributoAEntidad_ERROR_DAOEntidades, v);
			return;
		}
		
		// Si todo ha ido bien devolvemos al controlador la agregacion modificada y el nuevo atributo
		this.controlador.mensajeDesde_AG(TC.SAG_AnadirAtributoAAgregacion_HECHO, v); 
	}

}
