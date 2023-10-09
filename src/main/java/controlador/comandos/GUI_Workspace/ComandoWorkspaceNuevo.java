package controlador.comandos.GUI_Workspace;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;
import static vista.utils.Otros.PROJECTS;

import java.io.File;
import java.util.Objects;

import javax.swing.SwingUtilities;

import controlador.Controlador;
import controlador.comandos.Comando;

public class ComandoWorkspaceNuevo extends Comando {
	
	public ComandoWorkspaceNuevo(Controlador ctrl) {
		super(ctrl);
	}
	
	@Override
	public void ejecutar(Object datos) {
		ctrl.setPath((String) datos);                        
		SwingUtilities.invokeLater(new Runnable() {             
		    @Override                                           
		    public void run() {                                 
		        ctrl.getTheServiciosSistema().reset();               
		        ctrl.getTheGUIPrincipal().loadInfo();                     
		        ctrl.getTheGUIPrincipal().reiniciar();               
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