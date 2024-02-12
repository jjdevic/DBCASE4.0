package vista.frames;

import controlador.Controlador;
import controlador.TC;
import vista.Lenguaje;
import vista.utils.ImagesPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class GUI_Recientes extends Parent_GUI {

    
    private JPanel panelPrincipal;
    private JLabel jLabel1;
    private JComboBox<String> combo;
    private ArrayList<File> recientes = new ArrayList<File>();


    public GUI_Recientes(Controlador c, ArrayList<File> files) { 
    	super(c);
        recientes = files;

        this.setTitle(Lenguaje.text(Lenguaje.DBCASE));
        this.setModal(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagesPath.DBCASE_LOGO)).getImage());
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        panelPrincipal.setPreferredSize(new java.awt.Dimension(545, 318));

        jLabel1 = new JLabel();
        jLabel1.setFont(theme.font());
        jLabel1.setBounds(36, 25, 521, 100);
        panelPrincipal.add(jLabel1);
        jLabel1.setText(Lenguaje.text(Lenguaje.RECENT_FILES) + ":");
        //jLabel1.setVisible(true);

        initCombo();

        this.setSize(553, 354);
        this.setContentPane(panelPrincipal);
        this.centraEnPantalla();
        this.setVisible(true);
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void initCombo() {
        combo = new JComboBox<String>();
        combo.setForeground(theme.fontColor());
        combo.setFont(theme.font());
        combo.setVisible(true);
        combo.setBounds(12, 120, 521, 40);
        for (File f : recientes) {
            combo.addItem(f.getName());
        }
        panelPrincipal.add(combo);
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                comboActionPerformed(evt);
            }
        });
    }

    public void comboActionPerformed(ActionEvent e) {
        File fich = null;
        for (File f : recientes) {
            if (f.getName().equals(combo.getSelectedItem()))
                fich = f;
        }
        //construimos la ruta
        String ruta = fich.getPath();
        if (!ruta.endsWith(".xml")) ruta = ruta + ".xml";

        controlador.setFileguardar(fich);
        this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Recent, fich);
        this.controlador.mensajeDesde_GUIWorkSpace(TC.GUI_WorkSpace_Click_Abrir, ruta);
        this.dispose();

    }

	@Override
	protected void initComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDatos(Object datos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInactiva() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActiva() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int setActiva(int op) {
		// TODO Auto-generated method stub
		return 0;
	}

}
