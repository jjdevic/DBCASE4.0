package vista.frames;

import controlador.Controlador;
import controlador.TC;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import vista.Lenguaje;
import vista.componentes.MyFileChooser;
import vista.utils.ImagesPath;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;


@SuppressWarnings("serial")
public class GUI_SaveAs extends Parent_GUI {
    
    private JPanel panelPrincipal;
    private MyFileChooser jfc;
    private JLabel jLabel1;
    private int abrir;
    private boolean actuado; //vale true tras guardar o abrir, false si pulsa en cancelar o cierra la ventana
    private boolean modoSoporte = false;
    private boolean projects;

    public GUI_SaveAs(Controlador controlador, boolean dir) {
    	super(controlador);
        this.projects = dir;
        this.initComponents();
    }

    protected void initComponents() {
        this.setTitle(Lenguaje.text(Lenguaje.DBCASE));
        this.setModal(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO))).getImage());
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        panelPrincipal.setPreferredSize(new java.awt.Dimension(545, 318));
        jLabel1 = new JLabel();
        jLabel1.setFont(theme.font());
        panelPrincipal.add(jLabel1);
        jLabel1.setBounds(12, 12, 521, 14);
        jfc = new MyFileChooser();
        jfc.setFont(theme.font());
        if (this.projects) jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/projects"));
        else jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/Examples"));
        panelPrincipal.add(jfc);
        jfc.setBounds(0, 32, 547, 286);
        jfc.setDialogType(2);
        jfc.setFileSelectionMode(MyFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        XMLFileFilter filter = new XMLFileFilter();
        jfc.addChoosableFileFilter(filter);
        jfc.setApproveButtonText(Lenguaje.text(Lenguaje.SELECT));
        jfc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jfcActionPerformed(evt);
            }
        });


        this.setSize(553, 354);
        this.setContentPane(panelPrincipal);
    }

    /*
     * Activar y desactivar el dialogo
     */

    //devuelve siempre 1, salvo si se ha pulsado en cancelar
    public int setActiva(int b) {
        int actuado = 0;
        switch (b) {
            case 1: {//abrir
                if (this.modoSoporte) {
                    jfc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/incidences"));
                }
                jLabel1.setText(Lenguaje.text(Lenguaje.OPEN) + ":");
                abrir = b;
                this.centraEnPantalla();
                this.setVisible(true);
                actuado = 1;
                break;
            }
            case 2: {//guardar
                jLabel1.setText(Lenguaje.text(Lenguaje.SAVE) + ":");
                if (controlador.getFileguardar() == null || !controlador.getFileguardar().exists()) {
                    abrir = b;
                    this.centraEnPantalla();
                    this.setVisible(true);
                } else {
                	actuado = 1;
                    guardarProyecto();
                }
                
                break;
            }
            case 3: {//guardarComo
                jLabel1.setText(Lenguaje.text(Lenguaje.SAVE_AS) + ":");
                abrir = b;
                this.centraEnPantalla();
                this.setVisible(true);
                actuado = 1;
                break;
            }
            case 4: {
                jLabel1.setText(Lenguaje.text(Lenguaje.OPEN_CASOS) + ":");
                abrir = b;
                this.centraEnPantalla();
                this.setVisible(true);
                actuado = 1;
                break;
            }
        }
        return actuado;
    }

    public void setInactiva() {
        this.setVisible(false);
    }

    /*
     * Oyente del MyFileChooser
     */
    private void jfcActionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals(MyFileChooser.APPROVE_SELECTION)) {
            actuado = true;
            switch (abrir) {
                case 1:
                    abrirProyecto();
                    break;
                case 2:
                    guardarComo();
                    break;
                case 3:
                    guardarComo();
                    break;
                case 4:
                    abrirProyecto();
                    break;
            }
            this.dispose();
        }
        // Si se ha pulsado el boton cancelar
        else if (command.equals(MyFileChooser.CANCEL_SELECTION)) {
            actuado = false;
            this.dispose();
        }
    }

    private void abrirProyecto() {
        File f = this.jfc.getSelectedFile();

        //construimos la ruta
        String ruta = f.getPath();
        if (!ruta.endsWith(".xml")) ruta = ruta + ".xml";
        //si ya existe,
        if (f.exists()) {
            //compruebo que sea un xml de esta aplicación
            if (esValido(f)) {
                controlador.setFileguardar(f);
                this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir, ruta);
                this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Recent, f);
            } else {
                JOptionPane.showMessageDialog(null,
                        Lenguaje.text(Lenguaje.WRONG_FILE),
                        Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    Lenguaje.text(Lenguaje.NOT_EXIST_FILE),
                    Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarProyecto() {

        String ruta = controlador.getFileguardar().getAbsolutePath();
        if (!ruta.endsWith(".xml")) ruta = ruta + ".xml";
        if (controlador.getFileguardar().exists()) {
            this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Guardar, ruta);
            this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Recent, controlador.getFileguardar());
        }

    }

    public void guardarComo() {
        File f = this.jfc.getSelectedFile();

        String ruta = f.getPath();
        if (!ruta.endsWith(".xml")) ruta = ruta + ".xml";

        //si ya existe,
        if (f.exists()) this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Guardar, ruta);
        else this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Guardar, ruta);
        f = new File(ruta);
        this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Recent, f);
        controlador.setFileguardar(f);
    }


    /*GETTERS & SETTERS*/
    public Controlador getControlador() {
        return controlador;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public MyFileChooser getJFC() {
        return this.jfc;
    }

    /*
     * METODOS PARA MANEJAR LOS FICHEROS XML
     */
    class XMLFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
        }

        @Override
        public String getDescription() {
            return "xml files";
        }
    }


    private boolean esValido(File f) {
        try {
            Document doc = dameDoc(f);
            //comprobamos que sea de la forma de nuestros ficheros
            NodeList LC = doc.getElementsByTagName("Inf_dbcase");
            if (LC.getLength() != 1)
                return false;
            else {
                if (LC.item(0).getChildNodes().item(1).getNodeName().equals("EntityList") &&
                        LC.item(0).getChildNodes().item(3).getNodeName().equals("RelationList") &&
                        LC.item(0).getChildNodes().item(5).getNodeName().equals("AttributeList") &&
                        LC.item(0).getChildNodes().item(7).getNodeName().equals("DomainList"))
                    return true;
                else return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private Document dameDoc(File f) {
        Document doc = null;
        DocumentBuilder parser = null;
        try {
            DocumentBuilderFactory factoria = DocumentBuilderFactory.newInstance();
            parser = factoria.newDocumentBuilder();
            doc = parser.parse(f);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    Lenguaje.text(Lenguaje.ERROR) + ":\n" +
                            Lenguaje.text(Lenguaje.UNESPECTED_XML_ERROR) + " \"" + f.getName(),
                    Lenguaje.text(Lenguaje.DBCASE),
                    JOptionPane.ERROR_MESSAGE);
        }
        return doc;
    }

    public boolean getModoSoporte() {
        return modoSoporte;
    }

    public void setModoSoporte(boolean modoSoporte) {
        this.modoSoporte = modoSoporte;
    }

	@Override
	public void setDatos(Object datos) {
		if(datos != null) this.modoSoporte = (Boolean) datos;
	}

	@Override
	public void setActiva() {
	}
}
