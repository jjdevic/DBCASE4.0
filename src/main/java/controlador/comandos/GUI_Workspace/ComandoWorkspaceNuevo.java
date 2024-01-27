package controlador.comandos.GUI_Workspace;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;
import static vista.utils.Otros.PROJECTS;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controlador.Comando;
import controlador.Controlador;
import controlador.TC;
import utils.UtilsFunc;
import vista.Lenguaje;

public class ComandoWorkspaceNuevo extends Comando {
	
	public ComandoWorkspaceNuevo(Controlador ctrl) {
		super(ctrl);
	}
	
	@Override
	public void ejecutar(Object datos) {
		try {
            ctrl.setFiletemp(File.createTempFile("dbcase", "xml"));
            UtilsFunc.creaFicheroXML(ctrl.getFiletemp());
            ctrl.setPath(ctrl.getFiletemp().getAbsolutePath());
        
		} catch (IOException e) {
            JOptionPane.showMessageDialog(null, Lenguaje.text(Lenguaje.ERROR_TEMP_FILE),
                    Lenguaje.text(Lenguaje.DBCASE), JOptionPane.ERROR_MESSAGE);
        }
                        
		SwingUtilities.invokeLater(new Runnable() {             
		    @Override                                           
		    public void run() {                                 
		        getFactoriaServicios().getServicioSistema().reset();               
		        ctrl.getFactoriaGUI().getGUIPrincipal().loadInfo();                     
		        ctrl.getFactoriaGUI().getGUIPrincipal().reiniciar();               
		    }                                                   
		});                                                     
		ctrl.setCambios(false);                                      
		File temp = new File(System.getProperty("user.dir") + DIRECTORY + PROJECTS + "/temp");
		ctrl.setFileguardar(temp);                              
		File directory = new File(System.getProperty("user.dir") + DIRECTORY + INCIDENCES);
		
		if (directory.exists()) {                               
		    for (File file : Objects.requireNonNull(directory.listFiles())) {
		        if (!file.isDirectory()) {                      
		            file.delete();                              
		        }                                               
		    }                                                   
		}                                                       
		
		ctrl.setContFicherosDeshacer(0);
		ctrl.setLimiteFicherosDeshacer(0);
		ctrl.setAuxDeshacer(false);                              
		ctrl.guardarDeshacer();
		ctrl.setTiempoGuardado(System.currentTimeMillis() / 1000);
	}
}