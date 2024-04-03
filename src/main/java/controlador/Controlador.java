package controlador;

import modelo.servicios.*;
import modelo.transfers.*;
import org.w3c.dom.Document;

import controlador.Factorias.FactoriaMsj;
import controlador.Factorias.FactoriaTCCtrl;
import controlador.comandos.FactoriaComandos;
import misc.*;
import vista.Lenguaje;
import vista.componentes.ArchivosRecientes;
import vista.frames.*;
import vista.tema.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

import static vista.utils.Otros.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Controlador {
 
    private ArchivosRecientes archivosRecent;
    private int valorZoom;
    private static Stack<Document> pilaDeshacer;
    
    //TODO Mirar este atributo
    /** Indica si ha habido cambios */
    private boolean cambios;
    /** Indica si se permite atributo nullable */
    private boolean nullAttrs;
    private boolean confirmarEliminaciones;
    private boolean cuadricula;
    private File filetemp;
    private File fileguardar;
    private final Theme theme;
    private int modoVista;
    private boolean modoSoporte;

    //Para boton Deshacer solo afecta a acciones con elementos
    private TC ultimoMensaje;
    private Object ultimosDatos;
    //private TransferEntidad auxTransferEntidad;
    private Vector auxTransferAtributos;
    private Point2D posAux;
    private String antigoNombreAtributo;
    private String antiguoDominioAtributo;
    private boolean antiguoCompuestoAtribuo;
    private boolean antiguoMultivaloradoAtribuo;
    private boolean antiguoNotnullAtribuo;
    private boolean antiguoUniqueAtribuo;
    private boolean antiguoClavePrimaria;
    private int idPadreAntigua;
    private Vector<TransferEntidad> hijosAntiguo;
    private Vector<TransferEntidad> entidadesAntiguo;
    private TransferEntidad padreAntiguo;
    private TransferRelacion antiguaIsA;
    private Vector<TransferAtributo> antiguosAtributosRel;
    private Vector<TransferEntidad> antiguasEntidadesRel;
    private Vector<TransferAtributo> antiguosSubatributos;

    private Transfer copiado;

    /** ultima vez que se guardo el documento en milisegudos */
    private long tiempoGuardado = System.currentTimeMillis() / 1000;

    private int contFicherosDeshacer = 0;
    private int limiteFicherosDeshacer = 0;
    private boolean auxDeshacer = false;
    
    private FactoriaGUI factoriaGUI;
    private FactoriaServicios factoriaServicios;

    public Controlador() {
    	archivosRecent = new ArchivosRecientes();
        cambios = false;
        theme = Theme.getInstancia();
        pilaDeshacer = new Stack<Document>();
        modoSoporte = false;
        cuadricula = false;
        factoriaGUI = new FactoriaGUI(this);
        factoriaServicios = new FactoriaServicios();
        confirmarEliminaciones = true;
        //valorZoom=0; 
    }

    // Mensajes que le manda la GUI_WorkSpace al Controlador
    public void mensajeDesde_GUIWorkSpace(TC mensaje, Object datos) {
        switch (mensaje) {
            case GUI_WorkSpace_Recent: {
                archivosRecent.add((File) datos);
                break;
            }
            case GUI_WorkSpace_Click_Abrir_Lenguaje:
            case GUI_WorkSpace_Click_Abrir_Tema: {
                String abrirPath = (String) datos;
                String tempPath = this.filetemp.getAbsolutePath();
                UtilsFunc.FileCopy(abrirPath, tempPath);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        factoriaServicios.getServicioSistema().reset();
                        factoriaGUI.getGUIPrincipal().loadInfo();
                        factoriaGUI.getGUIPrincipal().reiniciar();
                    }
                });
                setCambios(false);
                break;
            }
            case GUI_WorkSpace_Click_Abrir_Deshacer: {//tenemos que diferenciar si abrimos un nuevo proyecto o el de deshacer
                String abrirPath = (String) datos;
                String tempPath = this.filetemp.getAbsolutePath();
                UtilsFunc.FileCopy(abrirPath, tempPath);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        factoriaServicios.getServicioSistema().reset();
                        factoriaGUI.getGUIPrincipal().loadInfo();
                        factoriaGUI.getGUIPrincipal().reiniciar();
                    }
                });
                //setCambios(false);
                //this.factoriaGUI.getGUIPrincipal().getPanelDiseno().grabFocus();
                break;
            }
            case GUI_WorkSpace_Click_GuardarDeshacer: {
                String guardarPath = (String) datos;
                String tempPath = this.filetemp.getAbsolutePath();
                UtilsFunc.FileCopy(tempPath, guardarPath);

                factoriaGUI.getGUI(TC.GUI_WorkSpace, true, false).setInactiva();
                setCambios(false);
                //this.tiempoGuardado = System.currentTimeMillis()/1000;
                break;
            }
            case GUI_WorkSpace_ERROR_CreacionFicherosXML: {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.INITIAL_ERROR) + "\n" +
                        Lenguaje.text(Lenguaje.OF_XMLFILES) + "\n" + this.getPath(), Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
                break;
            }
            
            /*
            Mensajes que requieren comandos
            */
            case GUI_WorkSpace_Nuevo:
            case GUI_WorkSpace_Click_Abrir:
            case GUI_WorkSpace_Click_Guardar: 
            case GUI_WorkSpace_Click_Guardar_Backup: {
                ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            default: break;
        }
    }

    // Mensajes que manda el Panel de Diseño al Controlador
    public void mensajeDesde_PanelDiseno(TC mensaje, Object datos) {
        long tiempoActual = System.currentTimeMillis() / 1000;
        long ti = (tiempoActual - this.tiempoGuardado);
        Contexto contexto = null;
        
        if (cambios && ti > 600) // si ha pasado mas de media hora
            this.guardarBackup();

        switch (mensaje) {
            case PanelDiseno_Pertenece_A_Agregacion: {
                Vector v = (Vector) datos;
                TransferRelacion rel = (TransferRelacion) v.get(0);
                boolean perteneceAgregacion = factoriaServicios.getServicioAgregaciones().perteneceAgregacion(rel);
                v.add(perteneceAgregacion);
                break;
            }
            case PanelDiseno_Click_EliminarAgregacion: {
                Transfer t = (Transfer) datos;
                if (t instanceof TransferAgregacion) {
                    TransferAgregacion agre = (TransferAgregacion) datos;
                    contexto = factoriaServicios.getServicioAgregaciones().eliminarAgregacion(agre);
                } else if (t instanceof TransferRelacion) {
                    TransferRelacion rel = (TransferRelacion) datos;
                    contexto = factoriaServicios.getServicioAgregaciones().eliminarAgregacion(rel);
                }

                break;
            }
            case PanelDiseno_Click_InsertarAtributo: {
                Vector<Transfer> listaTransfers = (Vector<Transfer>) datos;
                //removemos IsA
                Iterator<Transfer> itr = listaTransfers.iterator();
                while (itr.hasNext()) {
                    Transfer t = itr.next();
                    if (t instanceof TransferRelacion) {
                        if (((TransferRelacion) t).isIsA())
                            itr.remove();
                    } else if (t instanceof TransferAtributo)
                        if (!((TransferAtributo) t).getCompuesto())
                            itr.remove();
                }
                if (listaTransfers.isEmpty())
                    JOptionPane.showMessageDialog(null,
                            "ERROR.\nAdd an entity, a relation or an aggregation first\n",
                            Lenguaje.text(Lenguaje.ADD_ATTRIBUTE),
                            JOptionPane.PLAIN_MESSAGE);
                else factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setActiva();
                break;
            }

            case PanelDiseno_Click_Eliminar: {
                Vector<Transfer> listaTransfers = (Vector<Transfer>) datos;
                if (listaTransfers.isEmpty())
                    JOptionPane.showMessageDialog(null,
                            "ERROR.\nAdd an entity, a relation or an aggregation first\n",
                            Lenguaje.text(Lenguaje.DELETE),
                            JOptionPane.PLAIN_MESSAGE);
                else factoriaGUI.getGUI(mensaje, datos, false).setActiva();
                break;
            }
            
            case PanelDiseno_Click_DebilitarEntidad: {
                TransferEntidad te = (TransferEntidad) datos;
                if (!te.isDebil() && factoriaServicios.getServicioRelaciones().tieneHermanoDebil(te))
                    JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ALREADY_WEAK_ENTITY), Lenguaje.text(Lenguaje.ERROR), 0);
                else contexto = factoriaServicios.getServicioEntidades().debilitarEntidad(te);
                break;
            }
            case PanelDiseno_Click_EditarNotNullAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                contexto = factoriaServicios.getServicioAtributos().editarNotNullAtributo(ta);
                break;
            }
            case PanelDiseno_Click_EditarMultivaloradoAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                contexto = factoriaServicios.getServicioAtributos().editarMultivaloradoAtributo(ta);
                break;
            }
            case PanelDiseno_Click_EditarClavePrimariaAtributo: {
                Vector<Object> v = (Vector<Object>) datos;
                contexto = factoriaServicios.getServicioAtributos().editarClavePrimariaAtributo(v);
                break;
            }
            case PanelDiseno_MoverEntidad: {
                TransferEntidad te = (TransferEntidad) datos;
                factoriaServicios.getServicioEntidades().moverPosicionEntidad(te);
                break;
            }
            case PanelDiseno_MoverAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                contexto = factoriaServicios.getServicioAtributos().moverPosicionAtributo(ta);
                break;
            }
            case PanelDiseno_MoverRelacion: {
                TransferRelacion tr = (TransferRelacion) datos;
                contexto = factoriaServicios.getServicioRelaciones().moverPosicionRelacion(tr);
                break;
            }
            case PanelDiseno_Click_AnadirAtributoRelacion: {
                TransferRelacion tr = (TransferRelacion) datos;
                // Si es una relacion debil no se pueden anadir atributos
                if (tr.getTipo().equals("Debil")) {
                    JOptionPane.showMessageDialog(
                            null, "ERROR.\n" +
                                    Lenguaje.text(Lenguaje.NO_ATTRIBUTES_RELATION) + ".\n" +
                                    Lenguaje.text(Lenguaje.THE_RELATION) + "\"" + tr.getNombre() + "\"" + Lenguaje.text(Lenguaje.IS_WEAK) + ".\n",
                            Lenguaje.text(Lenguaje.ADD_ENTITY_RELATION),
                            JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setActiva();
                break;
            }
            case PanelDiseno_Click_EliminarRelacionIsA: {
                Vector<Object> v = (Vector<Object>) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                boolean preguntar = (Boolean) v.get(1);
                boolean respuesta = false;
                if (!confirmarEliminaciones) preguntar = false;
                
                if (preguntar) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.ISA_RELATION_DELETE) + "\n" +
                                    Lenguaje.text(Lenguaje.WISH_CONTINUE),
                            Lenguaje.text(Lenguaje.DELETE_ISA_RELATION), false), false);
                    if(gui.setActiva(0) == 0) this.mensaje(TC.EliminarRelacionIsA, tr);
                } 
                else this.mensaje(TC.EliminarRelacionIsA, tr);
                
                break;
            }

            case PanelDiseno_Click_InsertarRelacionIsA: {
                Point2D punto = (Point2D) datos;
                TransferRelacion tr = new TransferRelacion();
                tr.setPosicion(punto);
                contexto = factoriaServicios.getServicioRelaciones().anadirRelacionIsA(tr);
                this.antiguaIsA = tr;
                break;
            }
           
            /*
             * Dominios
             */
            case PanelDiseno_Click_CrearDominio: {
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), null, false).setActiva();
                break;
            }
            case PanelDiseno_Click_OrdenarValoresDominio: {
                TransferDominio td = (TransferDominio) datos;
                UtilsFunc.quicksort((Vector<String>) td.getListaValores());

                Vector<Object> v = new Vector();
                v.add(td);
                v.add(td.getListaValores());
                contexto = factoriaServicios.getServicioDominios().modificarElementosDominio(v);
                break;
            }

            /*
             * Panel de informacion
             */
            case PanelDiseno_MostrarDatosEnPanelDeInformacion: {
                JTree arbol = (JTree) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MostrarDatosEnPanelDeInformacion, arbol);
                break;
            }
            case PanelDiseno_ActualizarDatosEnTablaDeVolumenes: {
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ActualizarDatosEnTablaDeVolumenes, datos);
                break;
            }
            case PanelDiseno_MostrarDatosEnTablaDeVolumenes: {
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MostrarDatosEnTablaDeVolumenes, datos);
                break;
            }
            case PanelDiseno_Click_Copiar: {
                this.copiado = (Transfer) datos;
                break;
            }

            //Casos que solo activan una GUI
            case PanelDiseno_Click_AnadirRestriccionAEntidad:
	        case PanelDiseno_Click_AnadirRestriccionAAtributo:
	        case PanelDiseno_Click_AnadirRestriccionARelacion:
	        case PanelDiseno_Click_TablaUniqueAEntidad:
	        case PanelDiseno_Click_TablaUniqueARelacion:
	        case PanelDiseno_Click_AnadirAtributoEntidad: 
	        case PanelDiseno_Click_RenombrarAtributo:
	        case PanelDiseno_Click_InsertarRelacionNormal: 
	        case PanelDiseno_Click_RenombrarRelacion:
	        case PanelDiseno_Click_EditarDominioAtributo:
	        case PanelDiseno_Click_AnadirSubAtributoAAtributo:
	        case PanelDiseno_Click_EstablecerEntidadPadre: 
	        case PanelDiseno_Click_QuitarEntidadPadre:
	        case PanelDiseno_Click_AnadirEntidadHija:
	        case PanelDiseno_Click_QuitarEntidadHija: 
	        case PanelDiseno_Click_AnadirEntidadARelacion: 
	        case PanelDiseno_Click_QuitarEntidadARelacion: 
	        case PanelDiseno_Click_EditarCardinalidadEntidad: 
	        case PanelDiseno_Click_RenombrarDominio: 
	        case PanelDiseno_Click_ModificarDominio:
            case PanelDiseno_Click_AddAgregacion:
            case PanelDiseno_Click_EditarAgregacion:
            case PanelDiseno_Click_InsertarEntidad: 
            case PanelDiseno_Click_RenombrarEntidad:{
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setActiva();
                break;
            }
            
            //Casos que requieren comandos
            case PanelDiseno_Click_EliminarEntidad: 
            case PanelDiseno_Click_EliminarAtributo:
            case PanelDiseno_Click_DebilitarRelacion:
            case PanelDiseno_Click_EditarUniqueAtributo: 
            case PanelDiseno_Click_EditarCompuestoAtributo:
            case PanelDiseno_Click_Pegar: 
            case PanelDiseno_Click_EliminarReferenciasUniqueAtributo:
            case PanelDiseno_Click_ModificarUniqueAtributo:
            case PanelDiseno_Click_EliminarRelacionNormal:
            case PanelDiseno_Click_EliminarDominio: {
                contexto = ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            default:
                break;
        }
        
        if(contexto != null) tratarContexto(contexto);
    }

    // Mensajes que manda la GUIPrincipal al Controlador
    @SuppressWarnings("static-access")
    public void mensajeDesde_GUIPrincipal(TC mensaje, Object datos) {
    	Contexto contexto = null;
        switch (mensaje) {
            case GUIPrincipal_ObtenDBMSDisponibles: {
                Vector<TransferConexion> vtc =
                		factoriaServicios.getServicioSistema().obtenerTiposDeConexion();
                this.factoriaGUI.getGUIPrincipal().setListaConexiones(vtc);
                break;
            }
            case GUI_Principal_ABOUT: {
                factoriaGUI.getAbout().setActiva(true);
                break;
            }
            case GUI_Principal_MANUAL: {
                factoriaGUI.getManual().setActiva(true);
                break;
            }
            case GUI_Principal_GALERIA: {
                factoriaGUI.getGaleria().setActiva(true);
                break;
            }
            case GUI_Principal_RESET: {
            	boolean actuar = true;
                if (cambios) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);
                	
                	int respuesta = gui.setActiva(0);
                	if(respuesta == 0) this.mensaje(TC.Guardar, null);
                	else if(respuesta == 2) actuar = false;
                }
                
                if(actuar) this.mensaje(TC.Reset, null);
                break;
            }
            case GUI_Principal_Zoom:
            case GUI_Principal_REPORT: {
                factoriaGUI.getGUI(mensaje, datos, true).setActiva();
                break;
            }

		/*case GUI_Principal_DESHACER:{
			funcionDeshacer(this.ultimoMensaje, this.ultimosDatos);
			break;
		}*/

            case GUI_Principal_DESHACER2: {
                String str = fileguardar.getPath().replace(".xml", "");
                String ruta = "";
                if (str.contains(DIRECTORY + PROJECTS))
                    ruta = str.replace(DIRECTORY + PROJECTS, "deshacer") + (this.contFicherosDeshacer - 2) + ".xml";
                else if (str.contains("Examples"))
                    ruta = str.replace("Examples", "deshacer") + (this.contFicherosDeshacer - 2) + ".xml";
                if (this.contFicherosDeshacer > 1)
                    this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Deshacer, ruta);
                else return;
                this.contFicherosDeshacer = this.contFicherosDeshacer - 1;
                this.auxDeshacer = false;
                setCambios(true);
                //this.factoriaGUI.getGUIPrincipal().getPanelDiseno().grabFocus();
                break;
            }

            case GUI_Principal_REHACER: {
                String str = fileguardar.getPath().replace(".xml", "");
                String ruta = "";

                if (str.contains(DIRECTORY + PROJECTS))
                    ruta = str.replace(DIRECTORY + PROJECTS, "deshacer") + this.contFicherosDeshacer + ".xml";
                else if (str.contains("Examples"))
                    ruta = str.replace("Examples", "deshacer") + this.contFicherosDeshacer + ".xml";

                if (this.contFicherosDeshacer == this.limiteFicherosDeshacer || this.auxDeshacer) return;
                this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Deshacer, ruta);
                ++this.contFicherosDeshacer;
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().getPanelDiseno().grabFocus();
                break;
            }
            case GUI_Principal_Zoom_Aceptar: {
                this.factoriaGUI.getGUIPrincipal().cambiarZoom((int) datos);
                this.setZoom((int) datos);
                break;
            }
            case GUI_Principal_IniciaFrames: {
            	//Al destruir todos los frames almacenados, cuando se haga una petición de tomar un frame, se creará desde cero.
                factoriaGUI.destroyAll();
                break;
            }
            /*
             * Barra de menus
             */
            case GUI_Principal_Click_Submenu_Salir: {
            	boolean actuar = true;
                
            	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
            			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);
            	int respuesta = gui.setActiva(0);
            	if(respuesta == 0) this.mensaje(TC.GuardarYSalir, null);
            	else if(respuesta == 1) this.mensaje(TC.Salir, null);
                
                break;
            }
            case GUI_Principal_NULLATTR: {
                setNullAttrs(!nullAttrs);
                this.factoriaGUI.getGUIPrincipal().loadInfo();
                break;
            }
            case GUI_Principal_ConfirmarEliminaciones: {
                setConfirmarEliminaciones(!confirmarEliminaciones);
                this.factoriaGUI.getGUIPrincipal().loadInfo();
                break;
            }
            case GUI_Principal_ModoSoporte: {
                setModoSoporte(!modoSoporte);
                this.factoriaGUI.getGUIPrincipal().loadInfo();
                break;
            }
            case GUI_Principal_Cuadricula: {
                try {
                    this.factoriaGUI.getGUIPrincipal().modoCuadricula(!cuadricula);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setCuadricula(!cuadricula);
                //this.factoriaGUI.getGUIPrincipal().loadInfo();
                break;
            }
            case GUI_Principal_Click_Imprimir: {
                this.factoriaGUI.getGUIPrincipal().imprimir();
                break;
            }
            case GUI_Principal_Vista1:
            case GUI_Principal_Click_ModoProgramador: {
                this.factoriaGUI.getGUIPrincipal().modoProgramador();
                break;
            }
            case GUI_Principal_Vista2:
            case GUI_Principal_Click_ModoVerTodo: {
                this.factoriaGUI.getGUIPrincipal().modoVerTodo();
                break;
            }
            case GUI_Principal_Vista3:
            case GUI_Principal_Click_ModoDiseno: {
                this.factoriaGUI.getGUIPrincipal().modoDiseno();
                break;
            }
            case GUI_Principal_Click_Salir: {
                if (cambios) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);

                	int respuesta = gui.setActiva(0);
                	if(respuesta == 0) this.mensaje(TC.GuardarYSalir, null);
                	else if(respuesta == 1) this.mensaje(TC.Salir, null);
                } else this.mensaje(TC.GuardarYSalir, null);
                break;
            }
            case GUI_Principal_Click_Submenu_Abrir: {
            	boolean actuar = true;
            	factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setDatos(this.getModoSoporte());
                if (cambios) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta,
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);
                	int respuesta = gui.setActiva(0);
                	
                	if(respuesta == 0) this.mensaje(TC.Guardar, null);
                	else if(respuesta == 2) actuar = false;
                }
                
                if(actuar) this.mensaje(TC.Abrir, null);
                break;
            }

            case GUI_Principal_Click_Submenu_Abrir_Casos: {
            	boolean actuar = true;
                factoriaGUI.getGUI(TC.GUI_WorkSpace, false, true).setDatos(this.getModoSoporte());
                if (cambios) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);
                	int respuesta = gui.setActiva(0);
                	
                	if(respuesta == 0) this.mensaje(TC.GuardarYSalir, null);
                	else if(respuesta == 2) actuar = false;
                } 
                if(actuar) this.mensaje(TC.AbrirCasos, null);
                break;
            }

            case GUI_Principal_Click_Submenu_Recientes: {
            	factoriaGUI.getGUI(TC.GUI_Recientes, archivosRecent.darRecientes(), true);
                break;
            }
            case GUI_Principal_Click_Submenu_Guardar: {
                factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(2);
                factoriaGUI.getGUIPrincipal().setTitle(getTitle());
                break;
            }
            case GUI_Principal_EditarElemento: {
                contexto = ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            case GUI_Principal_Click_Submenu_GuardarComo: {
                factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(3);
                factoriaGUI.getGUIPrincipal().setTitle(getTitle());
                break;
            }
            case GUI_Principal_Click_Submenu_Nuevo: {
            	boolean actuar = true;
                if (cambios) {
                	Parent_GUI gui = factoriaGUI.getGUI(TC.GUI_Pregunta, 
                			UtilsFunc.crearVector(Lenguaje.text(Lenguaje.WISH_SAVE), Lenguaje.text(Lenguaje.DBCASE), true), false);
                	int respuesta = gui.setActiva(0);
                	
                	if(respuesta == 0) this.mensaje(TC.Guardar, null);
                	else if(respuesta == 2) actuar = false;
                }
                if(actuar) this.mensaje(TC.NuevoWorkSpace, null);
                break;
            }
            case GUI_Principal_CambiarLenguaje: {
                // Extraer lenguaje seleccionado
                String lenguaje = (String) datos;

                // Cambiar lenguaje
                Lenguaje.cargaLenguaje(lenguaje);

                /* guardar, "guardado", tempguarda... y todo eso. guardar en un temporal nuevo y luego abrirlo para dejarlo como estuviese*/
                boolean cambios = this.cambios;
                File fileguardar = this.fileguardar;
                boolean aux = this.auxDeshacer;
                try {
                    if (filetemp.exists()) {
                        File guardado = File.createTempFile("dbcase", "xml");
                        UtilsFunc.FileCopy(filetemp.getAbsolutePath(), guardado.getAbsolutePath());
                        mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Lenguaje, guardado.getAbsolutePath());
                        guardado.delete();
                    } else ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                } catch (IOException e) {
                }

                this.fileguardar = fileguardar;
                this.cambios = cambios;
                this.auxDeshacer = aux;

                break;
            }
            case GUI_Principal_CambiarTema: {
                theme.changeTheme((String) datos);
                /* guardar, "guardado", tempguarda... y todo eso. guardar en un temporal nuevo y luego abrirlo para dejarlo como estuviese*/
                boolean cambios = this.cambios;
                File fileguardar = this.fileguardar;
                boolean aux = this.auxDeshacer;
                try {
                    if (filetemp.exists()) {
                        File guardado = File.createTempFile("dbcase", "xml");
                        UtilsFunc.FileCopy(filetemp.getAbsolutePath(), guardado.getAbsolutePath());
                        mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir_Tema, guardado.getAbsolutePath());
                        guardado.delete();
                    } else ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                } catch (IOException e) {
                }

                this.fileguardar = fileguardar;
                this.cambios = cambios;
                this.auxDeshacer = aux;
                break;
            }
            /*
             * Limpiar pantalla
             */
            case GUI_Principal_Click_BotonLimpiarPantalla: {
            	factoriaServicios.getServicioSistema().reset();
                break;
            }
            /*
             * Generacion del script SQL
             */
            case GUI_Principal_Click_BotonGenerarModeloRelacional: {
            	Contexto aux = factoriaServicios.getServicioSistema().generaModeloRelacional();
            	String info = (String) aux.getDatos();
                this.factoriaGUI.getGUIPrincipal().escribeEnModelo(info);
            	factoriaGUI.getGUIPrincipal().getModeloText().goToTop();
                break;
            }
            case GUI_Principal_Click_BotonGenerarScriptSQL: {
            	factoriaGUI.getGUIPrincipal().getConexionActual().setDatabase("");
            	
                TransferConexion tc = factoriaGUI.getGUIPrincipal().getConexionActual();
                Contexto aux = factoriaServicios.getServicioSistema().generaScriptSQL(tc);
                
                String info = (String) aux.getDatos();
                this.factoriaGUI.getGUIPrincipal().escribeEnCodigo(info);
                this.factoriaGUI.getGUIPrincipal().setScriptGeneradoCorrectamente(true);
                
                // Restaurar el sistema
                factoriaGUI.getGUIPrincipal().getConexionActual().setDatabase("");
                factoriaGUI.getGUIPrincipal().getModeloText().goToTop();
                break;
            }
            case GUI_Principal_Click_BotonGenerarArchivoScriptSQL: {
                String info = factoriaServicios.getServicioSistema().exportarCodigo(factoriaGUI.getGUIPrincipal().getCodigoText().getText(), true);
                this.factoriaGUI.getGUIPrincipal().escribeEnCodigo((String) info);
                break;
            }
            case GUI_Principal_Click_BotonGenerarArchivoModelo: {
                String info = factoriaServicios.getServicioSistema().exportarCodigo(factoriaGUI.getGUIPrincipal().getModeloText().getText(), false);
                break;
            }
            case GUI_Principal_Click_BotonEjecutarEnDBMS: {
            	TransferConexion tc = new TransferConexion(
                        factoriaGUI.getGUIPrincipal().getCBO().getSelectedIndex(),
                        factoriaGUI.getGUIPrincipal().getCBO().getSelectedItem().toString());
            	
                Parent_GUI gui = factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false);
                gui.setDatos(tc);
                gui.setActiva();
                break;
            }
            case GUI_Principal_Click_SubmenuAnadirEntidad: {
                Point2D punto = (Point2D) datos;
                Parent_GUI gui = factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false);
                gui.setDatos(punto);
                gui.setActiva();
                break;
            }
            default:
                break;
        } // switch
        
        if(contexto != null) tratarContexto(contexto);
    }

    // Mensajes que le mandan las GUIs al controlador
    public void mensajeDesde_GUI(TC mensaje, Object datos) {
    	Contexto contexto = null;
        switch (mensaje) {
            case GUIInsertarEntidad_Click_BotonInsertar: {
                TransferEntidad te = (TransferEntidad) datos;
                contexto = factoriaServicios.getServicioEntidades().anadirEntidad(te, pilaDeshacer);
                break;
            }
            case GUIInsertarEntidadDebil_Entidad_Relacion_Repetidos: {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENTITY_REL), Lenguaje.text(Lenguaje.ERROR), 0);
                break;
            }
            case GUIRenombrarEntidad_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                contexto = factoriaServicios.getServicioEntidades().renombrarEntidad(v);
                break;
            }
            case GUIAnadirAtributoEntidad_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioEntidades().anadirAtributo(vectorTransfers);
                break;
            }
            case GUIAnadirAtributoAgregacion_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioAgregaciones().anadirAtributo(vectorTransfers);
                break;
            }
            case GUIAnadirAtributoRelacion_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().anadirAtributo(vectorTransfers);
                break;
            }
            case GUIPonerRestriccionesAEntidad_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioEntidades().setRestricciones(v);
                break;
            }
            case GUIPonerRestriccionesARelacion_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().setRestricciones(v);
                break;
            }
            case GUIPonerRestriccionesAAtributo_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioAtributos().setRestricciones(v);
                break;
            }
            case GUIInsertarRestriccionAEntidad_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioEntidades().anadirRestriccion(v);
                break;
            }
            case GUIQuitarRestriccionAEntidad_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioEntidades().quitarRestriccion(v);
                break;
            }
            case GUIInsertarRestriccionAAtributo_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioAtributos().anadirRestriccion(v);
                break;
            }
            case GUIQuitarRestriccionAAtributo_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioAtributos().quitarRestriccion(v);
                break;
            }
            case GUIInsertarRestriccionARelacion_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().anadirRestriccion(v);
                break;
            }
            case GUIQuitarRestriccionARelacion_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().quitarRestriccion(v);
                break;
            }
            case GUIRenombrarAtributo_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                contexto = factoriaServicios.getServicioAtributos().renombrarAtributo(v);
                break;
            }
            case GUIRenombrarAgregacion_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                TransferRelacion rel = (TransferRelacion) v.get(0);
                String nombre = (String) v.get(1);
                contexto = factoriaServicios.getServicioAgregaciones().renombrarAgregacion(rel, nombre);
                break;
            }
            case GUIEditarDominioAtributo_Click_BotonEditar: {
                Vector v = (Vector) datos;
                TransferAtributo ta = (TransferAtributo) v.get(0);
                this.antiguoDominioAtributo = ta.getDominio();
                contexto = factoriaServicios.getServicioAtributos().editarDomnioAtributo(v);
                break;
            }
            case GUIEditarCompuestoAtributo_Click_BotonAceptar: {
                TransferAtributo ta = (TransferAtributo) datos;
                contexto = factoriaServicios.getServicioAtributos().editarCompuestoAtributo(ta);
                break;
            }
            case GUIEditarMultivaloradoAtributo_Click_BotonAceptar: {
                TransferAtributo ta = (TransferAtributo) datos;
                contexto = factoriaServicios.getServicioAtributos().editarMultivaloradoAtributo(ta);
                break;
            }
            case GUIAnadirSubAtributoAtributo_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioAtributos().anadirAtributo(vectorTransfers);
                break;
            }
            case GUIEditarClavePrimariaAtributo_Click_BotonAceptar: {
                Vector<Object> vectorAtributoyEntidad = (Vector<Object>) datos;
                //vectorAtributoyEntidad.add(0);
                contexto = factoriaServicios.getServicioAtributos().editarClavePrimariaAtributo(vectorAtributoyEntidad);
                break;
            }
            case GUIInsertarAgregacion:
            case GUIModificarAtributo_Click_ModificarAtributo:
            case GUIAnadirEntidadARelacion_ClickBotonAnadir:
            case GUIPonerUniquesAEntidad_Click_BotonAceptar: 
            case GUIModificarEntidad_Click_ModificarEntidad:
            case GUIInsertarEntidadDebil_Click_BotonInsertar: {
                contexto = ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            /*
             * Relaciones
             */
            case GUIQuitarEntidadPadre_ClickBotonSi:
            case GUIPonerUniquesARelacion_Click_BotonAceptar: {
            	contexto = ejecutarComandoDelMensaje(mensaje, datos);
            	break;
            }
            case GUIInsertarRelacion_Click_BotonInsertar: {
                TransferRelacion tr = (TransferRelacion) datos;
                contexto = factoriaServicios.getServicioRelaciones().anadirRelacion(tr, 0);
                break;
            }
            case GUIInsertarRelacionDebil_Click_BotonInsertar: {
                TransferRelacion tr = (TransferRelacion) datos;
                boolean exito = factoriaServicios.getServicioRelaciones().SePuedeAnadirRelacion(tr).isExito();
                factoriaGUI.getGUI(TC.Controlador_InsertarEntidad, UtilsFunc.crearVector(null, null, exito) ,false);
                break;
            }
            case GUIRenombrarRelacion_Click_BotonRenombrar: {
                Vector<Object> v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                String nuevoNombre = (String) v.get(1);
                contexto = factoriaServicios.getServicioRelaciones().renombrarRelacion(tr, nuevoNombre);
                break;
            }
            case GUIEstablecerEntidadPadre_ClickBotonAceptar: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().establecerEntidadPadreEnRelacionIsA(relacionIsAyEntidadPadre);
                break;
            }
            case GUIAnadirEntidadHija_ClickBotonAnadir: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().anadirEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
                break;
            }
            case GUIQuitarEntidadHija_ClickBotonQuitar: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                contexto = factoriaServicios.getServicioRelaciones().quitarEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
                break;
            }
            /*
             * Relaciones normales
             */
            case GUIQuitarEntidadARelacion_ClickBotonQuitar: {
                //Vector<Transfer> v = (Vector<Transfer>) datos;
                Vector<Object> v = (Vector<Object>) datos;
                contexto = factoriaServicios.getServicioRelaciones().quitarEntidadARelacion(v);
                break;
            }
            case GUIEditarCardinalidadEntidad_ClickBotonEditar: {
                Vector<Object> v = (Vector<Object>) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                //Esto es para corregir inconsistencias en relaciones ternarias y de orden superior

                boolean cardinalidadSeleccionada = (boolean) v.get(6);
                boolean participacionSeleccionada = (boolean) v.get(7);
                boolean minMaxSeleccionado = (boolean) v.get(8);
                boolean cardinalidadMax1Seleccionada = (boolean) v.get(9);

                tr.setRelacionConCardinalidad(cardinalidadSeleccionada);
                tr.setRelacionConParticipacion(participacionSeleccionada);
                tr.setRelacionConMinMax(minMaxSeleccionado);
                tr.setRelacionConCardinalidad1(cardinalidadMax1Seleccionada);

                contexto = factoriaServicios.getServicioRelaciones().editarAridadEntidad(v);
                break;
            }
            /*
             * Dominios
             */
            case GUIInsertarDominio_Click_BotonInsertar: {
                TransferDominio td = (TransferDominio) datos;
                contexto = factoriaServicios.getServicioDominios().anadirDominio(td);
                break;
            }
            case GUIRenombrarDominio_Click_BotonRenombrar: {
                Vector<Object> v = (Vector) datos;

                TransferDominio td = (TransferDominio) v.get(0);
                String nuevoNombre = (String) v.get(1);
                String dominioRenombrado = td.getNombre();
                Vector<TransferAtributo> listaAtributos = factoriaServicios.getServicioAtributos().getListaDeAtributos();
                int cont = 0;
                TransferAtributo ta = new TransferAtributo();
                while (cont < listaAtributos.size()) {
                    ta = listaAtributos.get(cont);
                    if (ta.getDominio().equals(dominioRenombrado)) {
                        Vector<Object> vect = new Vector();
                        vect.add(ta);
                        vect.add(nuevoNombre);

                        contexto = factoriaServicios.getServicioAtributos().editarDomnioAtributo(vect);
                        tratarContexto(contexto);
                    }
                    cont++;
                }
                contexto = factoriaServicios.getServicioDominios().renombrarDominio(v);
                break;
            }
            case GUIModificarDominio_Click_BotonEditar: {
                Vector<Object> v = (Vector) datos;
                contexto = factoriaServicios.getServicioDominios().modificarDominio(v);
                break;
            }

            /*
             * Conectar a DBMS
             */
            case GUIConfigurarConexionDBMS_Click_BotonEjecutar: {
                TransferConexion tc = (TransferConexion) datos;
                factoriaServicios.getServicioSistema().ejecutarScriptEnDBMS(tc, factoriaGUI.getGUIPrincipal().getInstrucciones());
                break;
            }

            case GUIConexionDBMS_PruebaConexion: {
                TransferConexion tc = (TransferConexion) datos;
                factoriaServicios.getServicioSistema().compruebaConexion(tc);
                break;
            }
            case GUIReport_ReportarIncidencia: {
                Vector<Object> d = (Vector<Object>) datos;
                String textoIncidencia = (String) d.get(0);
                boolean anadirDiagrama = (boolean) d.get(1);
                factoriaServicios.getServicioReporte().crearIncidencia(textoIncidencia, anadirDiagrama, this.filetemp);
                break;
            }
            case GUISeleccionarConexion_ClickEditar:
            case GUISeleccionarConexion_ClickNueva: {
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setActiva();
            	break;
            }
            default:
                break;
        } // Switch
        
        if(contexto != null) tratarContexto(contexto);
        factoriaServicios.getServicioSistema().reset();
    }

    private void borrarSiguientesDeshacer() {
        File directory = new File(System.getProperty("user.dir") + "/deshacer");
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.isDirectory()) {
                    String str = file.getAbsolutePath();
                    file.delete();
                }
            }
        }
    }
    
    public Object mensaje(TC msj, Object datos) {
    	Object resultado = null;
    	Contexto contexto = null;
    	switch(msj) {
    	case GetNombreAtributo: {
    		Integer id = (Integer) datos;
    		resultado = getFactoriaServicios().getServicioAtributos().getNombreAtributo(id);
    		break;
    	}
    	case EliminarRelacionIsA: {
    		TransferRelacion tr = (TransferRelacion) datos;
    		contexto = factoriaServicios.getServicioRelaciones().eliminarRelacionIsA(tr);
            if(contexto.isExito()) factoriaServicios.getServicioEntidades().eliminarRelacionDeEntidad(tr);
    		break;
    	}
    	case GuardarYSalir: this.guardarYSalir(); break;
    	case Salir: this.salir(); break;
    	case Reset: {
    		filetemp.delete();
            ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
            setCambios(false);
    		break;
    	}
    	case Abrir: factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(1); break;
    	case AbrirCasos: factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(4); break;
    	case NuevoWorkSpace: {
    		filetemp.delete();
            ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
            setCambios(false);
    		break;
    	}
    	case EliminarAtributosRelacion: {
    		TransferRelacion tr = (TransferRelacion) datos;
    		Vector lista_atributos = tr.getListaAtributos();
            int cont = 0;
            TransferAtributo ta = new TransferAtributo();
            Boolean exito = true;
            while (cont < lista_atributos.size()) {
                String idAtributo = (String) lista_atributos.get(cont);
                ta.setIdAtributo(Integer.parseInt(idAtributo));
                
                Contexto ctxt = getFactoriaServicios().getServicioAtributos().eliminarAtributo(ta, 1);
                
                //Tratar los posibles subatributos
                tratarContextos(aVectorContextos((Vector) ctxt.getDatos(), 3));
                
                //Tratar contexto principal
                tratarContexto(ctxt);
                
                if(exito) exito = ctxt.isExito();
                
                cont++;
            }
            resultado = exito;
            break;
    	} 
    	case ObtenerListaEntidades: {
    		resultado = factoriaServicios.getServicioEntidades().ListaDeEntidadesNOVoid();
    		break;
    	}
    	case ObtenerListaAtributos: {
    		resultado = factoriaServicios.getServicioAtributos().getListaDeAtributos();; 
    		break;
    	}
    	case ObtenerListaRelaciones: {
    		resultado = factoriaServicios.getServicioRelaciones().ListaDeRelacionesNoVoid(); 
    		break;
    	}
    	case ObtenerListaDominios: {
    		resultado = factoriaServicios.getServicioDominios().getListaDeDominios();
    		break;
    	}
    	case ObtenerListaAgregaciones: {
    		resultado = factoriaServicios.getServicioAgregaciones().ListaDeAgregaciones();
    		break;
    	}
    	case ModificarCardinalidadRelacion_1a1:
    	case EliminarSubatributosAtributo: {
    		contexto = ejecutarComandoDelMensaje(msj, datos);
    		break;
    	}
    	case Guardar: {
    		guardarConf(); 
    		break;
    	}
    	default: break;
    	}
    	
    	if(contexto != null) tratarContexto(contexto);
    	
    	return resultado;
    }

    private void guardarBackup() {
        String ruta = "";
        if (fileguardar != null && fileguardar.exists()) {
            ruta = fileguardar.getPath().replace(".xml", "") + "Backup.xml";
        } else {
            String str = this.filetemp.getAbsolutePath();
            ruta = str.substring(0, str.length() - 27) + "LastProyectBackup.xml";
        }
        this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Guardar_Backup, ruta);
        //File f = new File(ruta);
        //this.setFileguardar(f);
    }

    public void guardarDeshacer() {
        String str = fileguardar.getPath().replace(".xml", "");
        String ruta = "";
        if (str.contains(DIRECTORY + PROJECTS))
            ruta = str.replace(DIRECTORY + PROJECTS, "deshacer") + this.contFicherosDeshacer + ".xml";
        else if (str.contains("Examples"))
            ruta = str.replace("Examples", "deshacer") + this.contFicherosDeshacer + ".xml";
        boolean existe = existe(ruta);
        this.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_GuardarDeshacer, ruta);
        ++this.contFicherosDeshacer;
        if (!existe) ++this.limiteFicherosDeshacer;
        if (this.getContFicherosDeshacer() == 1)
            this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
        else this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);

        if (this.getContFicherosDeshacer() == this.getLimiteFicherosDeshacer() || this.auxDeshacer)
            this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
        else this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);

    }

    private boolean existe(String ruta) {
        boolean r = false;
        File directory = new File(System.getProperty("user.dir") + "/deshacer");
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getPath().equals(ruta)) {
                    r = true;
                    break;
                }
            }
        }
        return r;
    }

    private void guardarConf() {
        String ruta = "";
        if (fileguardar != null && fileguardar.exists()) ruta = fileguardar.getPath();
        ConfiguradorInicial conf = new ConfiguradorInicial(
                Lenguaje.getIdiomaActual(),
                this.factoriaGUI.getGUIPrincipal().getConexionActual().getRuta(),
                ruta, theme.getThemeName(), this.factoriaGUI.getGUIPrincipal().getPanelsMode(), nullAttrs, valorZoom
        );
        conf.ponRecientes(archivosRecent.darRecientes());
        conf.guardarFicheroCofiguracion();
    }

    private void guardarYSalir() {
        guardarConf();
        salir();
    }

    private void salir() {
        filetemp.delete();
        eliminarCarpetaDeshacer();
        System.exit(0);
    }

    private void eliminarCarpetaDeshacer() {
        File directory = new File(System.getProperty("user.dir") + "/deshacer");
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            directory.delete();
        }
    }

    protected void ActualizaArbol(Transfer t) {
        this.factoriaGUI.getGUIPrincipal().getPanelDiseno().EnviaInformacionNodo(t);
    }
    
	public boolean isScriptGeneradoCorrectamente() {
		return factoriaGUI.getGUIPrincipal().getScriptGeneradoCorrectamente();
	}
	
	//Funcion específica para la funcionalidad Rehacer
	public void transferFocusRehacer() {
		factoriaGUI.getGUIPrincipal().getMyMenu().transferFocusRehacer();
	}

    public String getPath() {
        return Config.getPath();
    }

    public void setPath(String path) {
        Config.setPath(path);
    }

    public File getFiletemp() {
        return filetemp;
    }

    public void setFiletemp(File temp) {
        this.filetemp = temp;
    }

    public File getFileguardar() {
        return fileguardar;
    }

    public void setFileguardar(File guardar) {
        this.fileguardar = guardar;
    }

    protected int getModoVista() {
        return modoVista;
    }

    protected void setModoVista(int m) {
        this.modoVista = m;
    }

    public void setCambios(boolean b) {
        cambios = b;
        factoriaGUI.getGUIPrincipal().setTitle(getTitle());
    }

    public boolean isNullAttrs() {
        return nullAttrs;
    }

    public void setNullAttrs(boolean nullAttrs) {
        this.nullAttrs = nullAttrs;
    }

    public String getTitle() {
        try {
            return Lenguaje.text(Lenguaje.DBCASE) + " - " + getFileguardar().getName() + (cambios ? "*" : "");
        } catch (NullPointerException e) {
            return Lenguaje.text(Lenguaje.DBCASE) + " - " + this.getFiletemp().getPath();
        }
    }

    public boolean getConfirmarEliminaciones() {
        return confirmarEliminaciones;
    }

    public void setConfirmarEliminaciones(boolean confirmarEliminaciones) {
        this.confirmarEliminaciones = confirmarEliminaciones;
    }

    /**
     * Getters y Setters
     */

    public boolean getModoSoporte() {
        return modoSoporte;
    }

    public void setModoSoporte(boolean modo) {
        this.modoSoporte = modo;
    }

    public void setCuadricula(boolean cuadricula) {
        this.cuadricula = cuadricula;
    }

    public boolean getCaudricula() {
        return cuadricula;
    }

    public int getZoom() {
        return valorZoom;
    }

    public void setZoom(int z) {
        valorZoom = z;
    }

    public int getContFicherosDeshacer() {
        return this.contFicherosDeshacer;
    }

    public int getLimiteFicherosDeshacer() {
        return this.limiteFicherosDeshacer;
    }

    public boolean getAuxDeshacer() {
        return this.auxDeshacer;
    }
    
    public Transfer getCopiado() {
		return copiado;
	}

	protected ArchivosRecientes getArchivosRecientes() {
		return archivosRecent;
	}

	protected void setArchivosRecientes(ArchivosRecientes archivosRecent) {
		this.archivosRecent = archivosRecent;
	}

	public void setContFicherosDeshacer(int cont) {
    	this.contFicherosDeshacer = cont;
    }
    
    public void setLimiteFicherosDeshacer(int lim) {
    	this.limiteFicherosDeshacer = lim;
    }
	
    public void setAuxDeshacer(boolean b) {
    	this.auxDeshacer = b;
    }
    
    public void setTiempoGuardado(long t) {
    	this.tiempoGuardado = t;
    }
    
	protected FactoriaServicios getFactoriaServicios() {
		return factoriaServicios;
	}

	public FactoriaGUI getFactoriaGUI() {
		return factoriaGUI;
	}

	protected Stack<Document> getPilaDeshacer() {
		return pilaDeshacer;
	}

	public void setAntiguosSubatributos(Vector<TransferAtributo> antiguosSubatributos) {
		this.antiguosSubatributos = antiguosSubatributos;
	}

	public void setAuxTransferAtributos(Vector auxTransferAtributos) {
		this.auxTransferAtributos = auxTransferAtributos;
	}

	public void setAntigoNombreAtributo(String antigoNombreAtributo) {
		this.antigoNombreAtributo = antigoNombreAtributo;
	}

	public void setAntiguoDominioAtributo(String antiguoDominioAtributo) {
		this.antiguoDominioAtributo = antiguoDominioAtributo;
	}

	public void setAntiguoCompuestoAtributo(boolean antiguoCompuestoAtribuo) {
		this.antiguoCompuestoAtribuo = antiguoCompuestoAtribuo;
	}

	public void setAntiguoMultivaloradoAtributo(boolean antiguoMultivaloradoAtribuo) {
		this.antiguoMultivaloradoAtribuo = antiguoMultivaloradoAtribuo;
	}

	public void setAntiguoNotnullAtributo(boolean antiguoNotnullAtribuo) {
		this.antiguoNotnullAtribuo = antiguoNotnullAtribuo;
	}

	public void setAntiguoUniqueAtributo(boolean antiguoUniqueAtribuo) {
		this.antiguoUniqueAtribuo = antiguoUniqueAtribuo;
	}

	public void setAntiguoClavePrimaria(boolean antiguoClavePrimaria) {
		this.antiguoClavePrimaria = antiguoClavePrimaria;
	}

	public void setIdPadreAntigua(int idPadreAntigua) {
		this.idPadreAntigua = idPadreAntigua;
	}

	public void setHijosAntiguo(Vector<TransferEntidad> hijosAntiguo) {
		this.hijosAntiguo = hijosAntiguo;
	}

	public void setPadreAntiguo(TransferEntidad padreAntiguo) {
		this.padreAntiguo = padreAntiguo;
	}

	protected Contexto ejecutarComandoDelMensaje(TC mensaje, Object datos) {
		Contexto resultado = null;
    	Comando com = FactoriaComandos.getComando(mensaje, this);
    	if(com != null) resultado = com.ejecutar(datos);
    	else throw new IllegalArgumentException("Comando no encontrado");
    	return resultado;
    }
    
    protected void tratarContexto(Contexto contexto) {
    	if(contexto == null) return;
    	else if(!contexto.isExito()) {
    		JOptionPane.showMessageDialog(null, FactoriaMsj.getMsj(contexto.getMensaje()), Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE); return;
    	}
    	else {
    		System.out.println("Tratando contexto...");
    		if(contexto.getMensaje() != null) System.out.println(contexto.getMensaje().toString());
    		//Funcionalidad deshacer/rehacer
    		
    		//this.ultimoMensaje = mensaje;
            //this.ultimosDatos = datos;
    		this.guardarDeshacer();
            this.auxDeshacer = true;
            
            if (this.getContFicherosDeshacer() == 1)
                this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
            else this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);

            if (this.getContFicherosDeshacer() == this.getLimiteFicherosDeshacer() || this.auxDeshacer)
                this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
            else this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
            
            Vector v = null;
            Transfer tr = null;
            Object ob = null;
            
            //Extraer datos del contexto
            if(contexto.getDatos() != null) {
            	v = (Vector) contexto.getDatos();
            	tr = (Transfer) v.get(0);
            	ob = v.size() > 1 ? v : tr; //Si hay más de un componente, ob será todo el vector, si no será tr
            }
            
            if(contexto.getMensaje() != null) {
            	//Actualizar la GUI Principal
            	TC mc = FactoriaTCCtrl.getTCCtrl(contexto.getMensaje());
            	String m = mc == null ? "NULO" : mc.toString();
            	System.out.println("Mensaje obtenido de FactoriaTCCtrl: " + m);
            	if(mc != null) {
            		this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(mc, ob);
            		
            		//Desactivar la GUI específica correspondiente (si existe)
                    Parent_GUI gui = factoriaGUI.getGUI(mc, null, false);
                    if(gui != null) gui.setInactiva();
            	}
            }
            
            //Actualizar el árbol del panel de información
            ActualizaArbol(tr);
    	}
    }
    
    /** Devuelve el subvector que empieza en inicio (incluido), como vector de contextos */
    protected Vector<Contexto> aVectorContextos(Vector<Object> v, int inicio) {
    	int size = v.size();
    	Vector<Contexto> v_c = new Vector<Contexto>();
    	for(int i = inicio; i < size; i++) { v_c.add((Contexto) v.get(i)); }
        return v_c;
    }
    
    protected void tratarContextos(Vector<Contexto> v) {
    	for(Contexto c: v) {tratarContexto(c);}
    }
    
	
	/*public void funcionDeshacer(TC mensaje, Object datos) {
		switch (mensaje) {
			case SE_InsertarEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				v.add(datos);
				v.add(true);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarEntidad, v);
				break;
			}
			case SE_RenombrarEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				// Mandamos mensaje + datos al controlador
				this.mensajeDesde_GUI(TC.GUIRenombrarEntidad_Click_BotonRenombrar, v);
				break;
			}
			case SE_AnadirAtributoAEntidad_HECHO: {
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);//this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
			}
			case SE_EliminarEntidad_HECHO:{
				Vector<Object> v2 = (Vector<Object>) datos;
				Vector atributos = this.auxTransferAtributos;
				Vector<TransferRelacion> relaciones = (Vector<TransferRelacion>) v2.get(1);
				this.mensajeDesde_GUI(TC.GUIInsertarEntidad_Click_BotonInsertar, v2.get(0));
				for (int i = 0; i < atributos.size(); ++i) {
					
					for (int j = 0; j < this.listaAtributos.size(); ++j) {
						//System.out.println(atributos.get(i));
						//System.out.println(this.listaAtributos.get(j).getIdAtributo());
						if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
							Vector<Object> v = new Vector<Object>();
							v.add(v2.get(0));
							v.add(this.listaAtributos.get(j));
							mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
						}	
					}
					
				}
				for (int i = 0; i < relaciones.size(); ++i) {
					Vector<Object> v = new Vector<Object>();
					v.add(relaciones.get(i));
					v.add(v2.get(0));
					v.add(String.valueOf(0));
					v.add("n");
					v.add("");
					v.add(false);
					v.add(false);
					v.add(false);
					v.add(false);
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir,v);
				}
				
				break;
			}
			
			/*case SE_MoverPosicionEntidad_HECHO:{ //ni idea de por que no funciona
				TransferEntidad te = (TransferEntidad) datos;
				Point2D pos = te.getPosicion();
				TransferEntidad teAux = new TransferEntidad();
				/*for (int i = 0; i < this.listaEntidades.size(); ++i) {
					if(te.getNombre() == this.listaEntidades.get(i).getNombre()) {
						pos = this.listaEntidades.get(i).getPosicion();
					}
				}
				te.setPosicion(this.posAux);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_MoverEntidad, te);
				this.factoriaGUI.getGUIPrincipal().getPanelDiseno().repaint();
			}*/
			/*case SA_EliminarAtributo_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(v2.get(0));
				TransferAtributo ta = (TransferAtributo) v2.get(0);
				v.add("10");
				if (v2.get(1) instanceof TransferEntidad) {
					this.mensajeDesde_GUI(TC.GUIAnadirAtributoEntidad_Click_BotonAnadir, v);
				}
				else if (v2.get(1) instanceof TransferRelacion)this.mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v);
				else if (v2.get(1) instanceof TransferAtributo)this.mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v);
				
				//Nose xq peta
				
				/*if(ta.getCompuesto()) {
					Vector atributos = this.antiguosSubatributos;
					for (int i = 0; i < atributos.size(); ++i) {
						for (int j = 0; j < this.listaAtributos.size(); ++j) {
							if (String.valueOf(atributos.get(i)).equals(String.valueOf(this.listaAtributos.get(j).getIdAtributo()))) {
								Vector<Object> v3 = new Vector<Object>();
								v3.add(ta);
								v3.add(this.listaAtributos.get(j));
								v3.add("10");
								mensajeDesde_GUI(TC.GUIAnadirSubAtributoAtributo_Click_BotonAnadir, v3);
							}	
						}
						
					}
				}
				
				break;
			}
			case SE_setUniqueUnitarioAEntidad_HECHO:{//es el ultimo mensaje cuando se renombra un atributo
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				if (v2.size() == 3) {
					TransferAtributo ta = (TransferAtributo) v2.get(1);
					if(ta.getNombre() != v2.get(2)) {
						v.add(v2.get(1));
						v.add(v2.get(2));
						this.mensajeDesde_GUI(TC.GUIRenombrarAtributo_Click_BotonRenombrar, v);
					}
				}
				break;
				/*else {
					TransferAtributo ta = (TransferAtributo) v2.get(1);
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo,ta);
				}
				
			}
			case SA_EditarUniqueAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				Vector<Object> v = new Vector<Object>();
				if (ta.getNombre() != this.antigoNombreAtributo) {
					v.add(ta);
					v.add(this.antigoNombreAtributo);
					this.mensajeDesde_GUI(TC.GUIRenombrarAtributo_Click_BotonRenombrar, v);
				}
				if (!ta.getDominio().equals(this.antiguoDominioAtributo)) {
					v.add(ta);
					v.add(this.antiguoDominioAtributo);
					this.mensajeDesde_GUI(TC.GUIEditarDominioAtributo_Click_BotonEditar, v);
				}
				if (ta.getCompuesto() != this.antiguoCompuestoAtribuo) {
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarCompuestoAtributo,ta);
				}
				if (ta.getMultivalorado() != this.antiguoMultivaloradoAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarMultivaloradoAtributo,ta);
				}
				if (ta.getNotnull() != this.antiguoNotnullAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarNotNullAtributo,ta);
				}
				/*if(ta.getUnique() != this.antiguoUniqueAtribuo){
					mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarUniqueAtributo,ta);
				}
				break;
			}
			case SA_EditarDominioAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(ta);
				v.add(this.antiguoDominioAtributo);
				this.mensajeDesde_GUI(TC.GUIEditarDominioAtributo_Click_BotonEditar, v);
				break;
			}
			case SA_EditarCompuestoAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getCompuesto() != this.antiguoCompuestoAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarCompuestoAtributo,ta);
				break;
			}
			case SA_EditarMultivaloradoAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getMultivalorado() != this.antiguoMultivaloradoAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarMultivaloradoAtributo,ta);
				break;
			}
			case SA_EditarNotNullAtributo_HECHO:{
				TransferAtributo ta = (TransferAtributo) datos;
				if (ta.getNotnull() != this.antiguoNotnullAtribuo) mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarNotNullAtributo,ta);
				break;
			}
			case SA_AnadirSubAtributoAtributo_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
			}
			case SA_EditarClavePrimariaAtributo_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				TransferAtributo ta = (TransferAtributo) v.get(0);
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EditarClavePrimariaAtributo, v);
				break;
			}
			case SD_InsertarDominio_HECHO:{
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarDominio, datos);
				this.factoriaGUI.getGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			case SD_RenombrarDominio_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				this.mensajeDesde_GUI(TC.GUIRenombrarDominio_Click_BotonRenombrar, v);
				this.factoriaGUI.getGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			case SD_EliminarDominio_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				TransferDominio td = (TransferDominio) v.get(0);
				this.mensajeDesde_GUI(TC.GUIInsertarDominio_Click_BotonInsertar, td);
				this.factoriaGUI.getGUIPrincipal().actualizaArbolDominio(null);
				break;
			}
			case SR_InsertarRelacion_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(tr);
				v.add(true);// se uede enviar el atributo this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarRelacionNormal, v);
				break;
			}
			case SR_RenombrarRelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(2));
				this.mensajeDesde_GUI(TC.GUIRenombrarRelacion_Click_BotonRenombrar,v);
				break;
			}
			//case SR_DebilitarRelacion_HECHO:{}
			case SR_AnadirAtributoARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(1));
				v.add(true);//this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarAtributo,v);	
				break;
				
			}
			case SR_EstablecerEntidadPadre_HECHO:{
				Vector<Object> v2 = (Vector<Object>) datos;
				TransferRelacion tr = (TransferRelacion) v2.get(0);
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadPadre_ClickBotonSi, tr);
				break;
				
			}
			case SR_QuitarEntidadPadre_HECHO:{
				Vector<Object> v = new Vector<Object>();
				TransferRelacion tr = (TransferRelacion) datos;
				TransferEntidad te = this.padreAntiguo;
				v.add(tr);
				v.add(te);
				this.mensajeDesde_GUI(TC.GUIEstablecerEntidadPadre_ClickBotonAceptar, v);
				
				Vector<TransferEntidad> th = this.hijosAntiguo;
				for(int i = 0; i < th.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(tr);
					v2.add(th.get(i));
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v2);
				}
				
				break;
				
			}
			case SR_AnadirEntidadHija_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadHija_ClickBotonQuitar, v);
				break;
			}
			case SR_QuitarEntidadHija_HECHO:{
				Vector<Object> v = (Vector<Object>) datos;
				this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v);
				break;
			}
			case SR_EliminarRelacionIsA_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;

				//obtenemos el padre
				TransferEntidad tP = this.padreAntiguo;
				
				//obtenemos las hijas
				Vector<TransferEntidad> th = this.hijosAntiguo;
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, tr.getPosicion());
				Vector<Object> v1 = new Vector<Object>();
				v1.add(this.antiguaIsA);
				v1.add(tP);
				this.mensajeDesde_GUI(TC.GUIEstablecerEntidadPadre_ClickBotonAceptar, v1);
				
				for(int i = 0; i < th.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(this.antiguaIsA);
					v2.add(th.get(i));
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadHija_ClickBotonAnadir, v2);
				}
				break;
			}
			case SR_EliminarRelacionNormal_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				
				//obtenemos las hijas
				Vector<TransferEntidad> vte = this.antiguasEntidadesRel;
				Vector<TransferAtributo> vta = this.antiguosAtributosRel;
				Vector<EntidadYAridad> veya = new Vector<EntidadYAridad>();
				tr.setListaEntidadesYAridades(veya);
			
				this.mensajeDesde_GUI(TC.GUIInsertarRelacion_Click_BotonInsertar, tr);
				
				for(int i = 0; i < vte.size(); ++i) {
					Vector<Object> v1 = new Vector<Object>();
					v1.add(tr);
					v1.add(vte.get(i));
					v1.add("0");
					v1.add("n");
					v1.add("");
					v1.add(true);
					v1.add(false);
					v1.add(false);
					v1.add(false);
					this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, v1);
				}
				
				for(int i = 0; i < vta.size(); ++i) {
					Vector<Object> v2 = new Vector<Object>();
					v2.add(tr);
					v2.add(vta.get(i));
					v2.add("10");
					this.mensajeDesde_GUI(TC.GUIAnadirAtributoRelacion_Click_BotonAnadir, v2);
				}
				break;
			}
			case SR_InsertarRelacionIsA_HECHO:{
				TransferRelacion tr = (TransferRelacion) datos;
				Vector<Object> v = new Vector<Object>();
				v.add(tr);
				v.add(true);// se uede enviar el atributo this.confirmarEliminaciones
				this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_EliminarRelacionIsA, v);
				break;
			}
			case SR_AnadirEntidadARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add(v2.get(4));
				this.mensajeDesde_GUI(TC.GUIQuitarEntidadARelacion_ClickBotonQuitar, v);
				break;
			}
			case SR_QuitarEntidadARelacion_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add("0");
				v.add("n");
				v.add(v2.get(2));
				v.add(true);
				v.add(false);
				v.add(false);
				v.add(false);
				this.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir, v);
				break;
			}
			case SR_EditarCardinalidadEntidad_HECHO:{
				Vector<Object> v = new Vector<Object>();
				Vector<Object> v2 = (Vector<Object>) datos;
				v.add(v2.get(0));
				v.add(v2.get(1));
				v.add("0");
				v.add("n");
				v.add(v2.get(5));
				v.add(v2.get(4));
				v.add(true);
				v.add(false);
				v.add(false);
				v.add(false);
				this.mensajeDesde_GUI(TC.GUIEditarCardinalidadEntidad_ClickBotonEditar, v);
				break;
			}
			default: break;
		}
	}
	*/
}