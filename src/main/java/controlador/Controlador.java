package controlador;


import modelo.Modelo;
import modelo.servicios.*;
import modelo.transfers.*;
import org.w3c.dom.Document;

import controlador.Factorias.FactoriaMsj;
import controlador.Factorias.FactoriaTCCtrl;
import controlador.comandos.FactoriaComandos;
import controlador.comandos.GUI_Workspace.ComandoWorkspaceNuevo;
import persistencia.DAOEntidades;
import persistencia.DAORelaciones;
import persistencia.EntidadYAridad;
import utils.UtilsFunc;
import vista.GUIPrincipal;
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
    //Recientes
    private ArchivosRecientes archivosRecent;
    private int valorZoom;
    private static Stack<Document> pilaDeshacer;
    
    //Otros
    private String path;
    private Vector<TransferAtributo> listaAtributos;
    private boolean cambios;
    private boolean nullAttrs;
    private boolean confirmarEliminaciones;
    private boolean cuadricula;
    private boolean ocultarConceptual;
    private boolean ocultarLogico;
    private boolean ocultarFisico;
    private boolean ocultarDominios;
    private File filetemp;
    private File fileguardar;
    private GUI_Pregunta panelOpciones;
    private final Theme theme;
    private int modoVista;
    private Contexto ctxt;
    private boolean modoSoporte;

    //Para boton Deshacer solo afecta a acciones con elementos
    private TC ultimoMensaje;
    private Object ultimosDatos;
    //private TransferEntidad auxTransferEntidad;
    /*
    private Vector auxTransferAtributos;
    private Point2D posAux;
    private String antigoNombreAtributo;
    private String antiguoDominioAtributo;
    private boolean antiguoCompuestoAtribuo;
    private boolean antiguoMultivaloradoAtribuo;
    private boolean antiguoNotnullAtribuo;
    private boolean antiguoUniqueAtribuo;
    private boolean antiguoClavePrimaria;*/
    //private int idPadreAntigua;
    /*private Vector<TransferEntidad> hijosAntiguo;
    private Vector<TransferEntidad> entidadesAntiguo;
    private TransferEntidad padreAntiguo;
    private TransferRelacion antiguaIsA;
    private Vector<TransferAtributo> antiguosAtributosRel;
    private Vector<TransferEntidad> antiguasEntidadesRel;
    private Vector<TransferAtributo> antiguosSubatributos;*/

    private Transfer copiado;

    private long tiempoGuardado = System.currentTimeMillis() / 1000;//ultima vez que se guardo el documento en milisegudos

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
        factoriaServicios = new FactoriaServicios(this);
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

                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
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
            Mensajes que requieren comand
             TODO COMANDOS
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
                    factoriaServicios.getServicioAgregaciones().eliminarAgregacion(agre);
                } else if (t instanceof TransferRelacion) {
                    TransferRelacion rel = (TransferRelacion) datos;
                    factoriaServicios.getServicioAgregaciones().eliminarAgregacion(rel);
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
                else factoriaServicios.getServicioEntidades().debilitarEntidad(te);
                break;
            }
            case PanelDiseno_Click_EditarNotNullAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                ctxt = factoriaServicios.getServicioAtributos().editarNotNullAtributo(ta);
                tratarContexto(ctxt);
                break;
            }
            case PanelDiseno_Click_EditarMultivaloradoAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                ctxt = factoriaServicios.getServicioAtributos().editarMultivaloradoAtributo(ta);
                tratarContexto(ctxt);
                break;
            }
            case PanelDiseno_Click_EditarClavePrimariaAtributo: {
                Vector<Object> v = (Vector<Object>) datos;
                ctxt = factoriaServicios.getServicioAtributos().editarClavePrimariaAtributo(v);
                tratarContexto(ctxt);
                break;
            }
            case PanelDiseno_MoverEntidad: {
                TransferEntidad te = (TransferEntidad) datos;
                factoriaServicios.getServicioEntidades().moverPosicionEntidad(te);
                break;
            }
            case PanelDiseno_MoverAtributo: {
                TransferAtributo ta = (TransferAtributo) datos;
                ctxt = factoriaServicios.getServicioAtributos().moverPosicionAtributo(ta);
                tratarContexto(ctxt);
                break;
            }
            case PanelDiseno_MoverRelacion: {
                TransferRelacion tr = (TransferRelacion) datos;
                factoriaServicios.getServicioRelaciones().moverPosicionRelacion(tr);
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
                factoriaGUI.getGUI(mensaje, datos, false).setActiva();
                break;
            }
            case PanelDiseno_Click_EliminarRelacionIsA: {
                Vector<Object> v = (Vector<Object>) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                boolean preguntar = (Boolean) v.get(1);
                int respuesta = 0;
                if (!confirmarEliminaciones) preguntar = false;
                if (preguntar) {
                    respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.ISA_RELATION_DELETE) + "\n" +
                                    Lenguaje.text(Lenguaje.WISH_CONTINUE),
                            Lenguaje.text(Lenguaje.DELETE_ISA_RELATION));
                }
                if (respuesta == 0) {
                	factoriaServicios.getServicioRelaciones().eliminarRelacionIsA(tr);
                    factoriaServicios.getServicioEntidades().eliminarRelacionDeEntidad(tr);
                }
                break;
            }

            case PanelDiseno_Click_InsertarRelacionIsA: {
                Point2D punto = (Point2D) datos;
                TransferRelacion tr = new TransferRelacion();
                tr.setPosicion(punto);
                factoriaServicios.getServicioRelaciones().anadirRelacionIsA(tr);
                break;
            }
           
            /*
             * Dominios
             */
            case PanelDiseno_Click_CrearDominio: {
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case PanelDiseno_Click_OrdenarValoresDominio: {
                TransferDominio td = (TransferDominio) datos;
                //TODO Mirar si ordena como queremos
                UtilsFunc.quicksort((Vector<String>) td.getListaValores());

                Vector<Object> v = new Vector();
                v.add(td);
                v.add(td.getListaValores());
                factoriaServicios.getServicioDominios().modificarElementosDominio(v);
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
	        case PanelDiseno_Click_AnadirRestriccionAAtributo: //
	        case PanelDiseno_Click_AnadirRestriccionARelacion:
	        case PanelDiseno_Click_TablaUniqueAEntidad:
	        case PanelDiseno_Click_TablaUniqueARelacion:
	        case PanelDiseno_Click_AnadirAtributoEntidad: 
	        case PanelDiseno_Click_RenombrarAtributo: //
	        case PanelDiseno_Click_InsertarRelacionNormal: 
	        case PanelDiseno_Click_RenombrarRelacion:
	        case PanelDiseno_Click_EditarDominioAtributo: //
	        case PanelDiseno_Click_AnadirSubAtributoAAtributo: //
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
            	factoriaGUI.getGUI(mensaje, datos, false).setActiva();
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
                ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            default:
                break;
        }
    }

    /*private GUI_RenombrarAgregacion getTheGUIRenombrarAgregacion() {
        return theGUIModificarAgregacion;
    }*/

    // Mensajes que manda la GUIPrincipal al Controlador
    @SuppressWarnings("static-access")
    public void mensajeDesde_GUIPrincipal(TC mensaje, Object datos) {

        switch (mensaje) {
            case GUIPrincipal_ObtenDBMSDisponibles: {
                Vector<TransferConexion> vtc =
                		factoriaServicios.getServicioSistema().obtenerTiposDeConexion();
                this.factoriaGUI.getGUIPrincipal().setListaConexiones(vtc);
                break;
            }
            case GUIPrincipal_ActualizameLaListaDeEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
                break;
            }
            case GUIPrincipal_ActualizameLaListaDeAtributos: {
            	//TODO Provisional
                this.factoriaGUI.getGUIPrincipal().setListaAtributos(factoriaServicios.getServicioAtributos().getListaDeAtributos());
                break;
            }
            case GUIPrincipal_ActualizameLaListaDeRelaciones: {
            	factoriaServicios.getServicioRelaciones().ListaDeRelaciones();
                break;
            }

            case GUIPrincipal_ActualizameLaListaDeAgregaciones: {
            	factoriaServicios.getServicioAgregaciones().ListaDeAgregaciones();
                break;
            }

            case GUIPrincipal_ActualizameLaListaDeDominios: {
            	factoriaServicios.getServicioDominios().ListaDeDominios();
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
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) {
                        filetemp.delete();
                        ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                        setCambios(false);
                    } else if (respuesta == 0) {
                        if (factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(2)) {
                            filetemp.delete();
                            ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                            setCambios(false);
                        }
                    }
                } else {
                    filetemp.delete();
                    ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                    setCambios(false);
                }
                break;
            }
            case GUI_Principal_Zoom:
            case GUI_Principal_REPORT: {
                factoriaGUI.getGUI(mensaje, datos, true);
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

            case GUI_Principal_Vista1: {
                this.factoriaGUI.getGUIPrincipal().modoProgramador();
                break;
            }
            case GUI_Principal_Vista2: {
                this.factoriaGUI.getGUIPrincipal().modoVerTodo();
                break;
            }
            case GUI_Principal_Vista3: {
                this.factoriaGUI.getGUIPrincipal().modoDiseno();
                break;
            }
            case GUI_Principal_Zoom_Aceptar: {
                this.factoriaGUI.getGUIPrincipal().cambiarZoom((int) datos);
                this.setZoom((int) datos);
                break;
            }
            case GUI_Principal_IniciaFrames: {
                factoriaGUI.destroyAll();
                break;
            }
            /*
             * Barra de menus
             */
            case GUI_Principal_Click_Submenu_Salir: {
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) guardarYSalir();
                    else if (respuesta == 0) {
                        if (factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(2)) salir();
                    } else if (respuesta == 2) {

                    }
                } else guardarYSalir();
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
                    // TODO Auto-generated catch block
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
            case GUI_Principal_Click_ModoProgramador: {
                this.factoriaGUI.getGUIPrincipal().modoProgramador();
                break;
            }
            case GUI_Principal_Click_ModoDiseno: {
                this.factoriaGUI.getGUIPrincipal().modoDiseno();
                break;
            }
            case GUI_Principal_Click_ModoVerTodo: {
                this.factoriaGUI.getGUIPrincipal().modoVerTodo();
                break;
            }
            case GUI_Principal_Click_Salir: {
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) guardarYSalir();
                    else if (respuesta == 0) {
                        if (factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(2)) salir();
                    } else if (respuesta == 2) {

                    }
                } else guardarYSalir();
                break;
            }
            case GUI_Principal_Click_Submenu_Abrir: {
            	factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setDatos(this.getModoSoporte());
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) {
                    	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(1);
                    } else if (respuesta == 0) {
                        boolean guardado = factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(2);
                        if (guardado) {
                        	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(1);
                        }
                    }
                } else {
                	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(1);
                }
                break;
            }

            case GUI_Principal_Click_Submenu_Abrir_Casos: {
                factoriaGUI.getGUI(TC.GUI_WorkSpace, false, true).setDatos(this.getModoSoporte());
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) {
                    	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(4);
                    } else if (respuesta == 0) {
                        boolean guardado = factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(2);
                        if (guardado) {
                        	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(4);
                        }
                    }
                } else {
                	factoriaGUI.getGUI(TC.GUI_WorkSpace, null, false).setActiva(4);
                }
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
                if (datos instanceof TransferEntidad) {
                    TransferEntidad te = (TransferEntidad) datos;
                    Parent_GUI gui = factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarEntidad), datos, false);
                    gui.setDatos(te);
                    gui.setActiva();
                } 
                else if (datos instanceof TransferRelacion) {
                    Parent_GUI gui = factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarRelacion), datos, false);
                    TransferRelacion tr = (TransferRelacion) datos;
                    gui.setDatos(tr);
                    gui.setActiva();
                } 
                else if (datos instanceof TransferAtributo) {
                    Vector<TransferDominio> lista = factoriaServicios.getServicioDominios().getListaDeDominios();
                    Parent_GUI gui = factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(TC.GUI_Principal_EditarAtributo), datos, false);
                    //this.getTheGUIModificarAtributo().setListaDominios(lista);
                    TransferAtributo ta = (TransferAtributo) datos;
                    
                    //Buscamos a quien pertenece este atributo
                    String nombrePadre = "";
                    DAOEntidades daoEntidades = new DAOEntidades(this.getPath());
                    Vector<TransferEntidad> listaE = daoEntidades.ListaDeEntidades();

                    DAORelaciones daoRelaciones = new DAORelaciones(this.getPath());
                    Vector<TransferRelacion> listaR = daoRelaciones.ListaDeRelaciones();

                    for (TransferEntidad transferE : listaE) {
                        Vector<String> listaA = transferE.getListaAtributos();
                        for (String s : listaA) {
                            if (s.equals(Integer.toString(ta.getIdAtributo()))) {
                                nombrePadre = transferE.getNombre();
                            }
                        }
                    }
                    for (TransferRelacion transferR : listaR) {
                        Vector<String> listaA = transferR.getListaAtributos();
                        for (String s : listaA) {
                            if (s.equals(Integer.toString(ta.getIdAtributo()))) {
                                nombrePadre = transferR.getNombre();
                            }
                        }
                    }

                    //Establecer datos para la gui correspondiente y activarla
                    Vector<Object> v = new Vector<Object>();
                    v.add(ta);
                    v.add(nombrePadre);
                    gui.setDatos(ta);
                    gui.setActiva();
                }
                break;
            }
            case GUI_Principal_Click_Submenu_GuardarComo: {
                factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(3);
                factoriaGUI.getGUIPrincipal().setTitle(getTitle());
                break;
            }
            case GUI_Principal_Click_Submenu_Nuevo: {
                if (cambios) {
                    int respuesta = panelOpciones.setActiva(
                            Lenguaje.text(Lenguaje.WISH_SAVE),
                            Lenguaje.text(Lenguaje.DBCASE), true);
                    if (respuesta == 1) {
                        filetemp.delete();
                        ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                        setCambios(false);
                    } else if (respuesta == 0) {
                        if (factoriaGUI.getGUI(TC.GUI_WorkSpace, true, true).setActiva(2)) {
                            filetemp.delete();
                            ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                            setCambios(false);
                        }
                    }
                } else {
                    filetemp.delete();
                    ejecutarComandoDelMensaje(TC.GUI_WorkSpace_Nuevo, null);
                    setCambios(false);
                }
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
            	factoriaServicios.getServicioSistema().generaModeloRelacional();
            	factoriaGUI.getGUIPrincipal().getModeloText().goToTop();
                break;
            }
            case GUI_Principal_Click_BotonGenerarScriptSQL: {
            	factoriaGUI.getGUIPrincipal().getConexionActual().setDatabase("");
            	
                TransferConexion tc = factoriaGUI.getGUIPrincipal().getConexionActual();
                factoriaServicios.getServicioSistema().generaScriptSQL(tc);
                
                // Restaurar el sistema
                factoriaGUI.getGUIPrincipal().getConexionActual().setDatabase("");
                factoriaGUI.getGUIPrincipal().getModeloText().goToTop();
                break;
            }
            case GUI_Principal_Click_BotonGenerarArchivoScriptSQL: {
                factoriaServicios.getServicioSistema().exportarCodigo(factoriaGUI.getGUIPrincipal().getCodigoText().getText(), true);
                break;
            }
            case GUI_Principal_Click_BotonGenerarArchivoModelo: {
                factoriaServicios.getServicioSistema().exportarCodigo(factoriaGUI.getGUIPrincipal().getModeloText().getText(), false);
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
    }

    // Mensajes que le mandan las GUIs al controlador
    public void mensajeDesde_GUI(TC mensaje, Object datos) {
    	
        switch (mensaje) {
            case GUIInsertarEntidad_Click_BotonInsertar: {
                TransferEntidad te = (TransferEntidad) datos;
                factoriaServicios.getServicioEntidades().anadirEntidad(te, pilaDeshacer);
                ActualizaArbol(te);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarEntidadDebil_Click_BotonInsertar: {
                TransferEntidad te = (TransferEntidad) datos;
                boolean exito = factoriaServicios.getServicioEntidades().SePuedeAnadirEntidad(te);
                //TODO this.getTheGUIInsertarEntidad().comprobadaEntidad(exito);
                ActualizaArbol(te);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarEntidadDebil_Entidad_Relacion_Repetidos: {
                JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.REPEATED_ENTITY_REL), Lenguaje.text(Lenguaje.ERROR), 0);
                break;
            }
            case GUIRenombrarEntidad_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                factoriaServicios.getServicioEntidades().renombrarEntidad(v);

                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirAtributoEntidad_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                factoriaServicios.getServicioEntidades().anadirAtributo(vectorTransfers);
                ActualizaArbol(vectorTransfers.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirAtributoAgregacion_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                factoriaServicios.getServicioAgregaciones().anadirAtributo(vectorTransfers);
                ActualizaArbol(vectorTransfers.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirAtributoRelacion_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().anadirAtributo(vectorTransfers);
                ActualizaArbol(vectorTransfers.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirAtributoEntidad_ActualizameLaListaDeDominios: {
            	factoriaServicios.getServicioDominios().ListaDeDominios();
                break;
            }
            case GUIPonerRestriccionesAEntidad_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioEntidades().setRestricciones(v);
                ActualizaArbol((Transfer) v.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIPonerRestriccionesARelacion_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().setRestricciones(v);
                ActualizaArbol((Transfer) v.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIPonerRestriccionesAAtributo_Click_BotonAceptar: {
                Vector v = (Vector<Transfer>) datos;
                ctxt = factoriaServicios.getServicioAtributos().setRestricciones(v);
                tratarContexto(ctxt);
                ActualizaArbol((Transfer) v.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarRestriccionAEntidad_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioEntidades().anadirRestriccion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIQuitarRestriccionAEntidad_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioEntidades().quitarRestriccion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarRestriccionAAtributo_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                ctxt = factoriaServicios.getServicioAtributos().anadirRestriccion(v);
                tratarContexto(ctxt);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIQuitarRestriccionAAtributo_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioAtributos().quitarRestriccion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarRestriccionARelacion_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().anadirRestriccion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIQuitarRestriccionARelacion_Click_BotonAnadir: {
                Vector v = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().quitarRestriccion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIRenombrarAtributo_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                ctxt = factoriaServicios.getServicioAtributos().renombrarAtributo(v);
                tratarContexto(ctxt);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIRenombrarAgregacion_Click_BotonRenombrar: {
                Vector v = (Vector) datos;
                TransferRelacion rel = (TransferRelacion) v.get(0);
                String nombre = (String) v.get(1);
                factoriaServicios.getServicioAgregaciones().renombrarAgregacion(rel, nombre);

                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), null, false).setInactiva();

                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEditarDominioAtributo_Click_BotonEditar: {
                Vector v = (Vector) datos;
                TransferAtributo ta = (TransferAtributo) v.get(0);
                ctxt = factoriaServicios.getServicioAtributos().editarDomnioAtributo(v);
                tratarContexto(ctxt);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEditarCompuestoAtributo_Click_BotonAceptar: {
                TransferAtributo ta = (TransferAtributo) datos;
                ctxt = factoriaServicios.getServicioAtributos().editarCompuestoAtributo(ta);
                tratarContexto(ctxt);
                ActualizaArbol(ta);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEditarMultivaloradoAtributo_Click_BotonAceptar: {
                TransferAtributo ta = (TransferAtributo) datos;
                ctxt = factoriaServicios.getServicioAtributos().editarMultivaloradoAtributo(ta);
                tratarContexto(ctxt);
                ActualizaArbol(ta);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirSubAtributoAtributo_Click_BotonAnadir: {
                Vector<Transfer> vectorTransfers = (Vector<Transfer>) datos;
                ctxt = factoriaServicios.getServicioAtributos().anadirAtributo(vectorTransfers);
                tratarContexto(ctxt);
                ActualizaArbol(vectorTransfers.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEditarClavePrimariaAtributo_Click_BotonAceptar: {
                Vector<Object> vectorAtributoyEntidad = (Vector<Object>) datos;
                //vectorAtributoyEntidad.add(0);
                ctxt = factoriaServicios.getServicioAtributos().editarClavePrimariaAtributo(vectorAtributoyEntidad);
                tratarContexto(ctxt);
                ActualizaArbol((Transfer) vectorAtributoyEntidad.get(1));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarAgregacion:
            case GUIModificarAtributo_Click_ModificarAtributo:
            case GUIAnadirEntidadARelacion_ClickBotonAnadir:
            case GUIPonerUniquesAEntidad_Click_BotonAceptar: 
            case GUIModificarEntidad_Click_ModificarEntidad: {
                ejecutarComandoDelMensaje(mensaje, datos);
                break;
            }
            /*
             * Relaciones
             */
            case GUIQuitarEntidadPadre_ClickBotonSi:
            case GUIPonerUniquesARelacion_Click_BotonAceptar: {
            	ejecutarComandoDelMensaje(mensaje, datos); break;
            }
            case GUIInsertarRelacion_Click_BotonInsertar: {
                TransferRelacion tr = (TransferRelacion) datos;
                factoriaServicios.getServicioRelaciones().anadirRelacion(tr, 0);
                ActualizaArbol(tr);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIInsertarRelacionDebil_Click_BotonInsertar: {
                TransferRelacion tr = (TransferRelacion) datos;
                boolean exito = factoriaServicios.getServicioRelaciones().SePuedeAnadirRelacion(tr);
                //TODO mirar esto: this.getTheGUIInsertarEntidad().comprobadaRelacion(exito);
                ActualizaArbol(tr);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIRenombrarRelacion_Click_BotonRenombrar: {
                Vector<Object> v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                String nuevoNombre = (String) v.get(1);
                factoriaServicios.getServicioRelaciones().renombrarRelacion(tr, nuevoNombre);
                ActualizaArbol(tr);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEstablecerEntidadPadre_ClickBotonAceptar: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().establecerEntidadPadreEnRelacionIsA(relacionIsAyEntidadPadre);
                ActualizaArbol(relacionIsAyEntidadPadre.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIAnadirEntidadHija_ActualizameListaEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
                break;
            }
            case GUIAnadirEntidadHija_ClickBotonAnadir: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().anadirEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
                ActualizaArbol(relacionIsAyEntidadPadre.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIQuitarEntidadHija_ActualizameListaEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
                break;
            }
            case GUIQuitarEntidadHija_ClickBotonQuitar: {
                Vector<Transfer> relacionIsAyEntidadPadre = (Vector<Transfer>) datos;
                factoriaServicios.getServicioRelaciones().quitarEntidadHijaEnRelacionIsA(relacionIsAyEntidadPadre);
                ActualizaArbol(relacionIsAyEntidadPadre.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            /*
             * Relaciones normales
             */
            case GUIAnadirEntidadARelacion_ActualizameListaEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
                break;
            }
            case GUIQuitarEntidadARelacion_ActualizameListaEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
                break;
            }
            case GUIQuitarEntidadARelacion_ClickBotonQuitar: {
                //Vector<Transfer> v = (Vector<Transfer>) datos;
                Vector<Object> v = (Vector<Object>) datos;
                factoriaServicios.getServicioRelaciones().quitarEntidadARelacion(v);
                ActualizaArbol((Transfer) v.get(0));
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIEditarCardinalidadEntidad_ActualizameListaEntidades: {
            	factoriaServicios.getServicioEntidades().ListaDeEntidades();
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

                factoriaServicios.getServicioRelaciones().editarAridadEntidad(v);
                ActualizaArbol(tr);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            /*
             * Dominios
             */
            case GUIInsertarDominio_Click_BotonInsertar: {
                TransferDominio td = (TransferDominio) datos;
                factoriaServicios.getServicioDominios().anadirDominio(td);

                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIRenombrarDominio_Click_BotonRenombrar: {
                Vector<Object> v = (Vector) datos;

                TransferDominio td = (TransferDominio) v.get(0);
                String nuevoNombre = (String) v.get(1);
                String dominioRenombrado = td.getNombre();
                factoriaServicios.getServicioAtributos().getListaDeAtributos();
                int cont = 0;
                TransferAtributo ta = new TransferAtributo();
                while (cont < listaAtributos.size()) {
                    ta = listaAtributos.get(cont);
                    if (ta.getDominio().equals(dominioRenombrado)) {
                        Vector<Object> vect = new Vector();
                        vect.add(ta);
                        vect.add(nuevoNombre);

                        ctxt = factoriaServicios.getServicioAtributos().editarDomnioAtributo(vect);
                        tratarContexto(ctxt);
                    }
                    cont++;
                }
                factoriaServicios.getServicioDominios().renombrarDominio(v);
                factoriaServicios.getServicioSistema().reset();
                break;
            }
            case GUIModificarDominio_Click_BotonEditar: {
                Vector<Object> v = (Vector) datos;
                factoriaServicios.getServicioDominios().modificarDominio(v);

                factoriaServicios.getServicioSistema().reset();
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
                factoriaServicios.getServicioReporte().crearIncidencia(textoIncidencia, anadirDiagrama);
                break;
            }
            case GUISeleccionarConexion_ClickNueva: {
            	factoriaGUI.getGUI(TC.Controlador_ConfigurarConexionDBMS, datos, false);
            }
            default:
                break;
        } // Switch
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

    // Mensajes que mandan los Servicios de Entidades al Controlador
    public void mensajeDesde_SE(TC mensaje, Object datos) {
        int intAux = 2;
        if (mensaje == TC.SE_EliminarEntidad_HECHO) {
            Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
            intAux = (int) aux.get(2);
        }

        if (mensaje == TC.SE_MoverPosicionEntidad_HECHO || mensaje == TC.SE_InsertarEntidad_HECHO || mensaje == TC.SE_RenombrarEntidad_HECHO || mensaje == TC.SE_AnadirAtributoAEntidad_HECHO || (mensaje == TC.SE_EliminarEntidad_HECHO && intAux == 0)) {
            //this.ultimoMensaje = mensaje;
            //this.ultimosDatos = datos;

            //this.borrarSiguientesDeshacer();

            this.guardarDeshacer();

            this.auxDeshacer = true;

            if (this.getContFicherosDeshacer() == 1)
                this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.GRAY);
            else this.factoriaGUI.getGUIPrincipal().getMyMenu().getDeshacer().setBackground(Color.WHITE);

            if (this.getContFicherosDeshacer() == this.getLimiteFicherosDeshacer() || this.auxDeshacer)
                this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.GRAY);
            else this.factoriaGUI.getGUIPrincipal().getMyMenu().getRehacer().setBackground(Color.WHITE);
        }

        switch (mensaje) {
            case SE_ListarEntidades_HECHO: {
            	/*
                this.factoriaGUI.getGUIPrincipal().setListaEntidades((Vector) datos);
                this.getTheGUIEstablecerEntidadPadre().setListaEntidades((Vector) datos);
                this.getTheGUIAnadirEntidadHija().setListaEntidades((Vector) datos);
                this.getTheGUIQuitarEntidadHija().setListaEntidades((Vector) datos);
                this.getTheGUIAnadirEntidadARelacion().setListaEntidades((Vector) datos);
                this.getTheGUIQuitarEntidadARelacion().setListaEntidades((Vector) datos);
                this.getTheGUIEditarCardinalidadEntidad().setListaEntidades((Vector) datos);
                this.getTheGUIInsertarEntidad().setListaEntidades((Vector) datos);
                this.getTheGUIModificarEntidad().setListaEntidades((Vector) datos);
                this.setListaEntidades((Vector) datos); */
                break;
            }
            case SE_InsertarEntidad_HECHO: {
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                setCambios(true);
                TransferEntidad te = (TransferEntidad) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarEntidad, te);
                //this.listaEntidades.add(te);
                break;
            }
            case SE_RenombrarEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarEntidad, te);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case SE_DebilitarEntidad_HECHO: {
                TransferEntidad te = (TransferEntidad) datos;
                setCambios(true);
                ActualizaArbol(te);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_DebilitarEntidad, te);
                break;
            }
            case SE_AnadirAtributoAEntidad_HECHO: {
                Vector<Transfer> v = (Vector<Transfer>) datos;
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(mensaje), v);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                //meter un if para cuando ya este
                TransferAtributo ta = (TransferAtributo) v.get(1);
                boolean esta = false;
                for (TransferAtributo listaAtributo : this.listaAtributos) {
                    if (ta.getIdAtributo() == listaAtributo.getIdAtributo()) {
                        esta = true;
                        break;
                    }
                }
                if (!esta) this.listaAtributos.add(ta);
                break;
            }
            case SE_EliminarEntidad_HECHO: {
                setCambios(true);
                ActualizaArbol(null);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarEntidad, datos);
                break;
            }
            case SE_MoverPosicionEntidad_HECHO: {
                setCambios(true);
                TransferEntidad te = (TransferEntidad) datos;
                /*this.posAux = te.getPosicion();
                for (TransferEntidad listaEntidade : this.listaEntidades) {
                    if (Objects.equals(te.getNombre(), listaEntidade.getNombre())) {
                        posAux = listaEntidade.getPosicion();
                    }
                }*/
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverEntidad_HECHO, te);
                break;
            }
            case SE_AnadirRestriccionAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionEntidad, te);
                //this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
                break;
            }
            case SE_QuitarRestriccionAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionEntidad, te);
                break;
            }
            case SE_setRestriccionesAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(1);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesEntidad, te);
                break;
            }
            case SE_AnadirUniqueAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                TransferEntidad clon_entidad = te.clonar();
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirUniqueEntidad, clon_entidad);
                //this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
                break;
            }
            case SE_QuitarUniqueAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                TransferEntidad clon_entidad = te.clonar();
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarUniqueEntidad, clon_entidad);
                break;
            }
            case SE_setUniquesAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(1);
                TransferEntidad clon_entidad = te.clonar();
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniquesEntidad, clon_entidad);
                break;
            }
            case SE_setUniqueUnitarioAEntidad_HECHO: {
                Vector v = (Vector) datos;
                TransferEntidad te = (TransferEntidad) v.get(0);
                TransferEntidad clon_entidad = te.clonar();
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniqueUnitarioEntidad, clon_entidad);
                break;
            }
            default: {
            	String msj_error = FactoriaMsj.getMsj(mensaje);
            	//Si el TC devuelto corresponde a un error, tomamos el mensaje correspondiente de FactoriaMsj y lo mostramos
            	if(msj_error != null) JOptionPane.showMessageDialog(null, msj_error, Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    // Mensajes que mandan los Servicios de Dominios al Controlador
    public void mensajeDesde_SD(TC mensaje, Object datos) {
        if (mensaje == TC.SD_InsertarDominio_HECHO || mensaje == TC.SD_RenombrarDominio_HECHO || mensaje == TC.SD_EliminarDominio_HECHO) {
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
        }

        switch (mensaje) {
            case SD_ListarDominios_HECHO: {
            	//TODO Cambiar esto por el Modelo
            	
            	/*
                this.factoriaGUI.getGUIPrincipal().setListaDominios((Vector) datos);
                this.getTheGUIAnadirAtributoEntidad().setListaDominios((Vector) datos);
                this.getTheGUIAnadirAtributo().setListaDominios((Vector) datos);
                this.getTheGUIEditarDominioAtributo().setListaDominios((Vector) datos);
                this.getTheGUIAnadirAtributoRelacion().setListaDominios((Vector) datos);
                this.getTheGUIAnadirSubAtributoAtributo().setListaDominios((Vector) datos);
                this.getTheGUIModificarAtributo().setListaDominios((Vector) datos);*/
                break;
            }
            //TODO Revisar este caso, se puede usar lo que se devuelve desde neogocio para meterlo en el ctxt y no se necesita pasar por FactoriaMsj en este caso
            case SD_InsertarDominio_ERROR_ValorNoValido: {
                Vector v = (Vector) datos;
                String error = (String) v.get(1);
                JOptionPane.showMessageDialog(null, error, Lenguaje.text(Lenguaje.ERROR), 0);
                break;
            }
            case SD_InsertarDominio_HECHO: {
            	factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                TransferDominio td = (TransferDominio) datos;
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarDominio, td);
                break;
            }
            case SD_RenombrarDominio_HECHO: {
                Vector v = (Vector) datos;
                TransferDominio td = (TransferDominio) v.get(0);
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarDominio, td);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case SD_EliminarDominio_HECHO: {
                setCambios(true);
                TransferDominio td = (TransferDominio) ((Vector) datos).get(0);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarDominio, td);
                break;
            }
            case SD_ModificarTipoBaseDominio_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferDominio td = (TransferDominio) v.get(0);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ModificarTipoBaseDominio, td);
                break;
            }       
            case SD_ModificarElementosDominio_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferDominio td = (TransferDominio) v.get(0);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_ModificarTipoBaseDominio, td);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), null, false).setInactiva();
                break;
            }
            default: {
            	String msj_error = FactoriaMsj.getMsj(mensaje);
            	//Si el TC devuelto corresponde a un error, tomamos el mensaje correspondiente de FactoriaMsj y lo mostramos
            	if(msj_error != null) JOptionPane.showMessageDialog(null, msj_error, Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    // Mensajes que mandan los Servicios de Atributos al Controlador
    public void mensajeDesde_SA(TC mensaje, Object datos) {
        int intAux = 2;
        if (mensaje == TC.SA_EliminarAtributo_HECHO) {
            Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
            intAux = (int) aux.get(2);
        }

		/*if (mensaje == TC.SA_EditarClavePrimariaAtributo_HECHO){
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
			intAux = (int) aux.get(2);
		}

		if (mensaje == TC.SA_EditarUniqueAtributo_HECHO){
			Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion del atributo venga de otra eliminacion
			intAux = (int) aux.get(1);
		}*/


        if (mensaje == TC.SA_MoverPosicionAtributo_HECHO || (mensaje == TC.SA_EliminarAtributo_HECHO && intAux == 0) || mensaje == TC.SA_EditarUniqueAtributo_HECHO || mensaje == TC.SA_EditarDominioAtributo_HECHO || mensaje == TC.SA_EditarCompuestoAtributo_HECHO || mensaje == TC.SA_EditarMultivaloradoAtributo_HECHO || mensaje == TC.SA_EditarNotNullAtributo_HECHO || mensaje == TC.SA_AnadirSubAtributoAtributo_HECHO || mensaje == TC.SA_EditarClavePrimariaAtributo_HECHO) {
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
        }


        switch (mensaje) {
            case SA_EliminarAtributo_HECHO: {
                setCambios(true);
                Vector<Transfer> vectorAtributoYElemMod = (Vector<Transfer>) datos;

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarAtributo, vectorAtributoYElemMod);
                ActualizaArbol(null);
                break;
            }
            case SA_RenombrarAtributo_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferAtributo ta = (TransferAtributo) v.get(0);
                //String antiguoNombre = (String) v.get(2);

                /*TransferAtributo clon_atributo = ta.clonar();
                Vector v1 = new Vector();
                v1.add(clon_atributo);
                v1.add(antiguoNombre);
                this.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_ModificarUniqueAtributo, v1);*/


                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarAtributo, ta);
                factoriaGUI.getGUI(TC.Controlador_RenombrarAtributo, null, false).setInactiva();
                break;
            }
            case SA_EditarDominioAtributo_HECHO: {
                setCambios(true);
                TransferAtributo ta = (TransferAtributo) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarDominioAtributo, ta);
                factoriaGUI.getGUI(TC.Controlador_EditarDominioAtributo, null, false).setInactiva();
                break;
            }
            case SA_EditarCompuestoAtributo_HECHO: {
                setCambios(true);
                TransferAtributo ta = (TransferAtributo) datos;
                ta.getNombre();
                ActualizaArbol(ta);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarCompuestoAtributo, ta);
                break;
            }
            case SA_EditarMultivaloradoAtributo_HECHO: {
                setCambios(true);
                TransferAtributo ta = (TransferAtributo) datos;
                ActualizaArbol(ta);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarMultivaloradoAtributo, ta);
                break;
            }
            case SA_EditarNotNullAtributo_HECHO: {
                setCambios(true);
                TransferAtributo ta = (TransferAtributo) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarNotNullAtributo, ta);
                ActualizaArbol(ta);
                break;
            }
            case SA_EditarUniqueAtributo_ERROR_DAOAtributos: {
		/*	TransferAtributo ta = (TransferAtributo) datos;
			//JOptionPane.showMessageDialog(null, Lenguaje.getMensaje(Lenguaje.ATTRIBUTES_FILE_ERROR), Lenguaje.getMensaje(Lenguaje.ERROR), 0);
			this.factoriaGUI.getGUIPrincipal().anadeMensajeAreaDeSucesos(
					"ERROR: No se ha podido editar el caracter unique del atributo \""+ ta.getNombre() + "\". " +
			"Se ha producido un error en el acceso al fichero de atributos.");*/
                break;
            }
            case SA_EditarUniqueAtributo_HECHO: {
                setCambios(true);
                Vector<Object> ve = (Vector<Object>) datos;
                TransferAtributo ta = (TransferAtributo) ve.get(0);
                ActualizaArbol(ta);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarUniqueAtributo, ta);
                break;
            }
            case SA_AnadirSubAtributoAtributo_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirSubAtributoAAtributo, v);
                factoriaGUI.getGUI(TC.Controlador_AnadirSubAtributoAAtributo, null, false).setInactiva();
                /*TransferAtributo ta = (TransferAtributo) v.get(1);
                boolean esta = false;
                for (TransferAtributo listaAtributo : this.listaAtributos) {
                    if (ta.getIdAtributo() == listaAtributo.getIdAtributo()) {
                        esta = true;
                        break;
                    }
                }
                if (!esta) this.listaAtributos.add(ta);*/
                break;
            }
            case SA_EditarClavePrimariaAtributo_HECHO: {
                setCambios(true);
                Vector<Transfer> vt = (Vector<Transfer>) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EditarClavePrimariaAtributo, vt);
                break;
            }
            /*
             * Restricciones a Atributo
             */
            case SA_AnadirRestriccionAAtributo_HECHO: {
                Vector v = (Vector) datos;
                TransferAtributo te = (TransferAtributo) v.get(0);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionAtributo, te);
                break;
            }
            case SA_QuitarRestriccionAAtributo_HECHO: {
                Vector v = (Vector) datos;
                TransferAtributo te = (TransferAtributo) v.get(0);
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionAtributo, te);
                break;
            }
            case SA_setRestriccionesAAtributo_HECHO: {
                Vector v = (Vector) datos;
                TransferAtributo te = (TransferAtributo) v.get(1);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesAtributo, te);
                break;
            }
            case SA_MoverPosicionAtributo_HECHO: {
                setCambios(true);
                TransferAtributo ta = (TransferAtributo) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverAtributo_HECHO, ta);
                break;
            }
            default: {
            	String msj_error = FactoriaMsj.getMsj(mensaje);
            	//Si el TC devuelto corresponde a un error, tomamos el mensaje correspondiente de FactoriaMsj y lo mostramos
            	if(msj_error != null) JOptionPane.showMessageDialog(null, msj_error, Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    //mensajes que manda el ServivioAgregaciones al controlador
    public void mensajeDesde_AG(TC mensaje, Object datos) {
    	Integer n_error = null;
    	
        if (mensaje == TC.SAG_RenombrarAgregacion_HECHO || mensaje == TC.SAG_InsertarAgregacion_HECHO || mensaje == TC.SAG_AnadirAtributoAAgregacion_HECHO || mensaje == TC.SAG_EliminarAgregacion_HECHO) {
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
        }

        switch (mensaje) {
            case SAG_ListarAgregacion_HECHO: { // igual hay mas clases en las que hay que cambiar la lista de agregaciones
                this.factoriaGUI.getGUIPrincipal().setListaAgregaciones((Vector) datos);
                break;
            }

            case SAG_InsertarAgregacion_HECHO: {
                setCambios(true);
                //this.getTheGUIInsertarRelacion().setInactiva();
                TransferAgregacion ta = (TransferAgregacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarAgregacion, ta);
                break;
            }

            case SAG_RenombrarAgregacion_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferAgregacion tr = (TransferAgregacion) v.get(0);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarAgregacion, tr);
                break;
            }

            case SAG_AnadirAtributoAAgregacion_HECHO: {
                Vector<Transfer> v = (Vector<Transfer>) datos;
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirAtributoAAgregacion, v);
                //this.getTheGUIAnadirAtributoEntidad().setInactiva();

                //meter un if para cuando ya este
                TransferAtributo ta = (TransferAtributo) v.get(1);
                boolean esta = false;
                for (TransferAtributo listaAtributo : this.listaAtributos) {
                    if (ta.getIdAtributo() == listaAtributo.getIdAtributo()) {
                        esta = true;
                        break;
                    }
                }
                if (!esta) this.listaAtributos.add(ta);
                break;
            }

            case SAG_EliminarAgregacion_HECHO: {
                TransferAgregacion tagre = (TransferAgregacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarAgregacion, tagre);
                ActualizaArbol(null);
                break;
            }
            default: {
            	String msj_error = FactoriaMsj.getMsj(mensaje);
            	//Si el TC devuelto corresponde a un error, tomamos el mensaje correspondiente de FactoriaMsj y lo mostramos
            	if(msj_error != null) JOptionPane.showMessageDialog(null, msj_error, Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    // Mensajes que mandan los Servicios de Relaciones al Controlador
    public void mensajeDesde_SR(TC mensaje, Object datos) {
        int intAux = 2;
        Integer n_error = null;
        TransferRelacion taux = new TransferRelacion();
        
        if (mensaje == TC.SR_EliminarRelacionNormal_HECHO) {
            Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
            intAux = (int) aux.get(1);
        }

        if (mensaje == TC.SR_InsertarRelacion_HECHO) {
            Vector<Object> aux = (Vector<Object>) datos;//auxiliar para el caso de que la eliminacion de la relacion venga de eliminar entidad debil
            //intAux = (int) aux.get(1);
            taux = (TransferRelacion) aux.get(0);
        }

        if (mensaje == TC.SR_AnadirEntidadARelacion_HECHO) {
            Vector<Object> aux = (Vector<Object>) datos;
            //intAux = (int) aux.get(aux.size()-1);
        }

        if (mensaje == TC.SR_MoverPosicionRelacion_HECHO || mensaje == TC.SR_InsertarRelacion_HECHO || mensaje == TC.SR_EliminarRelacion_HECHO || mensaje == TC.SR_RenombrarRelacion_HECHO || mensaje == TC.SR_AnadirAtributoARelacion_HECHO || mensaje == TC.SR_EstablecerEntidadPadre_HECHO || mensaje == TC.SR_QuitarEntidadPadre_HECHO || mensaje == TC.SR_AnadirEntidadHija_HECHO || mensaje == TC.SR_QuitarEntidadHija_HECHO || mensaje == TC.SR_EliminarRelacionIsA_HECHO || (mensaje == TC.SR_EliminarRelacionNormal_HECHO && intAux == 0) || mensaje == TC.SR_InsertarRelacionIsA_HECHO || mensaje == TC.SR_AnadirEntidadARelacion_HECHO || mensaje == TC.SR_QuitarEntidadARelacion_HECHO || mensaje == TC.SR_EditarCardinalidadEntidad_HECHO) {
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
        }


        switch (mensaje) {
            case SR_ListarRelaciones_HECHO: {
                this.factoriaGUI.getGUIPrincipal().setListaRelaciones((Vector) datos);
                break;
            }
            case SR_InsertarRelacion_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                Vector<Object> v = (Vector<Object>) datos;
                TransferRelacion te = (TransferRelacion) v.get(0);
                //this.listaRelaciones.add(te);//ojo
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarRelacion, te);
                break;
            }
            case SR_EliminarRelacion_HECHO: {
                setCambios(true);
                TransferRelacion tr = (TransferRelacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacion, tr);
                break;
            }
            case SR_RenombrarRelacion_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_RenombrarRelacion, tr);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case SR_DebilitarRelacion_HECHO: {
                setCambios(true);
                TransferRelacion tr = (TransferRelacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_DebilitarRelacion, tr);
                ActualizaArbol(tr);
                break;
            }
            case SR_AnadirRestriccionARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion te = (TransferRelacion) v.get(0);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirRestriccionRelacion, te);
                //this.getTheGUIAnadirRestriccionAAtributo().setInactiva();
                break;
            }
            case SR_QuitarRestriccionARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion te = (TransferRelacion) v.get(0);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarRestriccionRelacion, te);
                break;
            }
            case SR_setRestriccionesARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion te = (TransferRelacion) v.get(1);
                setCambios(true);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setRestriccionesRelacion, te);
                break;
            }
            case SR_MoverPosicionRelacion_HECHO: {
                setCambios(true);
                TransferRelacion tr = (TransferRelacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_MoverRelacion_HECHO, tr);
                break;
            }
            case SR_AnadirAtributoARelacion_HECHO: {
                setCambios(true);
                Vector<Transfer> v = (Vector<Transfer>) datos;

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(mensaje), v);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                //meter un if para cuando ya este
                TransferAtributo ta = (TransferAtributo) v.get(1);
                boolean esta = false;
                for (TransferAtributo listaAtributo : this.listaAtributos) {
                    if (ta.getIdAtributo() == listaAtributo.getIdAtributo()) {
                        esta = true;
                        break;
                    }
                }

                if (!esta) this.listaAtributos.add(ta);
                break;
            }
            case SR_EstablecerEntidadPadre_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                Vector<Transfer> vt = (Vector<Transfer>) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EstablecerEntidadPadre, vt);
                break;
            }
            case SR_QuitarEntidadPadre_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                TransferRelacion tr = (TransferRelacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarEntidadPadre, tr);
                break;
            }
            case SR_AnadirEntidadHija_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                Vector<Transfer> vt = (Vector<Transfer>) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirEntidadHija, vt);
                break;
            }
            case SR_QuitarEntidadHija_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                Vector<Transfer> vt = (Vector<Transfer>) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarEntidadHija, vt);
                break;
            }
            case SR_EliminarRelacionIsA_HECHO: {
                setCambios(true);
                TransferRelacion tr = (TransferRelacion) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacionIsA, tr);
                ActualizaArbol(null);
                break;
            }
            case SR_EliminarRelacionNormal_HECHO: {
                setCambios(true);
                Vector<Object> v = (Vector<Object>) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_EliminarRelacionNormal, tr);
                ActualizaArbol(null);
                break;
            }
            case SR_InsertarRelacionIsA_HECHO: {
                setCambios(true);
                TransferRelacion tr = (TransferRelacion) datos;
                //this.antiguaIsA = tr;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_InsertarRelacionIsA, tr);
                ActualizaArbol(tr);
                break;
            }
            case SR_AnadirEntidadARelacion_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(mensaje), v);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case SR_QuitarEntidadARelacion_HECHO: {
                setCambios(true);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                Vector<Transfer> vt = (Vector<Transfer>) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(mensaje), vt);
                break;
            }
            case SR_EditarCardinalidadEntidad_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;
                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(mensaje), v);
                factoriaGUI.getGUI(FactoriaTCCtrl.getTCCtrl(mensaje), datos, false).setInactiva();
                break;
            }
            case SR_AridadEntidadUnoUno_HECHO: {
                setCambios(true);
                Vector v = (Vector) datos;

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_CardinalidadUnoUno, v);
                break;
            } 
            case SR_AnadirUniqueARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                TransferRelacion clon_relacion = tr.clonar();
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_AnadirUniqueRelacion, clon_relacion);
                //this.getTheGUIAnadirRestriccionAEntidad().setInactiva();
                break;
            }
            case SR_QuitarUniqueARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                TransferRelacion clon_relacion = tr.clonar();
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_QuitarUniqueRelacion, clon_relacion);
                break;
            }
            case SR_setUniquesARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(1);
                TransferRelacion clon_relacion = tr.clonar();
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniquesRelacion, clon_relacion);
                break;
            }
            case SR_setUniqueUnitarioARelacion_HECHO: {
                Vector v = (Vector) datos;
                TransferRelacion tr = (TransferRelacion) v.get(0);
                TransferRelacion clon_relacion = tr.clonar();
                setCambios(true);

                this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(TC.Controlador_setUniqueUnitarioRelacion, clon_relacion);
                break;
            }
            default: {
            	String msj_error = FactoriaMsj.getMsj(mensaje);
            	//Si el TC devuelto corresponde a un error, tomamos el mensaje correspondiente de FactoriaMsj y lo mostramos
            	if(msj_error != null) JOptionPane.showMessageDialog(null, msj_error, Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    // Mensajes que mandan los Servicios del Sistema al Controlador
    @SuppressWarnings("incomplete-switch")
    public void mensajeDesde_SS(TC mensaje, Object datos) {
        switch (mensaje) {
            case SS_ValidacionM: {
                String info = (String) datos;
                this.factoriaGUI.getGUIPrincipal().escribeEnModelo(info);
                break;
            }
            case SS_ValidacionC: {
                String info = (String) datos;
                this.factoriaGUI.getGUIPrincipal().escribeEnCodigo(info);
                break;
            }
            case SS_GeneracionScriptSQL: {
                String info = (String) datos;
                this.factoriaGUI.getGUIPrincipal().escribeEnCodigo(info);
                this.factoriaGUI.getGUIPrincipal().setScriptGeneradoCorrectamente(true);
                break;
            }
            case SS_GeneracionArchivoScriptSQL: {
                String info = (String) datos;
                this.factoriaGUI.getGUIPrincipal().escribeEnCodigo(info);
                break;
            }
            case SS_GeneracionModeloRelacional: {
                String info = (String) datos;
                this.factoriaGUI.getGUIPrincipal().escribeEnModelo(info);
                break;
            }
        }
    }
    
    public Object mensaje(TC msj, Object datos) {
    	Object resultado = null;
    	switch(msj) {
    	case GetNombreAtributo: {
    		Integer id = (Integer) datos;
    		resultado = getFactoriaServicios().getServicioAtributos().getNombreAtributo(id);
    		break;
    	}
    	default: break;
    	}
    	
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
    
    public Vector<TransferAtributo> getListaAtributos() {
    	return factoriaServicios.getServicioAtributos().getListaDeAtributos();
    }
    
    public Vector<TransferEntidad> getListaEntidades() {
    	return factoriaServicios.getServicioEntidades().ListaDeEntidadesNOVoid();
    }
    
    public Vector<TransferRelacion> getListaRelaciones() {
    	return factoriaServicios.getServicioRelaciones().ListaDeRelacionesNoVoid();
    }
	
	public boolean isScriptGeneradoCorrectamente() {
		return factoriaGUI.getGUIPrincipal().getScriptGeneradoCorrectamente();
	}
	
	//Funcion específica para la funcionalidad Rehacer
	public void transferFocusRehacer() {
		factoriaGUI.getGUIPrincipal().getMyMenu().transferFocusRehacer();
	}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        //TODO Provisional
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

    public GUI_Pregunta getPanelOpciones() {
        return this.panelOpciones;
    }

    /* private GUI_TablaUniqueEntidad getTheGUITablaUniqueEntidad() {
        return this.theGUITablaUniqueEntidad;
    }

    private GUI_TablaUniqueRelacion getTheGUITablaUniqueRelacion() {
        return this.theGUITablaUniqueRelacion;
    }*/

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

    public boolean getOcultarConceptual() {
        return ocultarConceptual;
    }

    public void setOcultarConceptual(boolean c) {
        this.ocultarConceptual = c;
    }

    public boolean getOcultarLogico() {
        return ocultarLogico;
    }

    public void setOcultarLogico(boolean l) {
        this.ocultarLogico = l;
    }

    public boolean getOcultarFisico() {
        return ocultarFisico;
    }

    public void setOcultarFisico(boolean f) {
        this.ocultarFisico = f;
    }

    public boolean getOcultarDominios() {
        return ocultarDominios;
    }

    public void setOcultarDominios(boolean d) {
        this.ocultarDominios = d;
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

	private void ejecutarComandoDelMensaje(TC mensaje, Object datos) {
    	Comando com = FactoriaComandos.getComando(mensaje, this);
    	if(com != null) com.ejecutar(datos);
    	else throw new IllegalArgumentException("Comando no encontrado");
    }
    
    protected void tratarContexto(Contexto contexto) {
    	if(contexto == null) return;
    	else if(!contexto.isExito()) {
    		JOptionPane.showMessageDialog(null, FactoriaMsj.getMsj(contexto.getMensaje()), Lenguaje.text(Lenguaje.ERROR), JOptionPane.ERROR_MESSAGE); return;
    	}
    	else {
    		this.guardarDeshacer();
            this.auxDeshacer = true;
            
            Vector v = null;
            Transfer tr = null;
            
            if(contexto.getDatos() != null) {
            	v = (Vector) contexto.getDatos();
            	tr = (Transfer) v.get(0);
            }
            
            //Actualizar el árbol del panel de información
            ActualizaArbol(tr); //ActualizaArbol en realidad nunca usa el transfer, solo lo usa para ver si no es nulo.
            
            //Actualizar la GUI
            this.factoriaGUI.getGUIPrincipal().mensajesDesde_Controlador(FactoriaTCCtrl.getTCCtrl(contexto.getMensaje()), tr);
            
            //Desactivar la GUI específica correspondiente (si existe)
            Parent_GUI gui = factoriaGUI.getGUI(contexto.getMensaje(), tr, false);
            if(gui != null) gui.setInactiva();
    	}
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