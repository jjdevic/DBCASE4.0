package controlador.comandos.Vistas;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import vista.Lenguaje;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static vista.utils.Otros.*;

public class ComandoWorkspaceNuevo extends Comando {
	
	public ComandoWorkspaceNuevo(Controlador ctrl) {
		super(ctrl);
	}
	
	@Override
	public Contexto ejecutar(Object datos) {
		try {
            ctrl.setFiletemp(File.createTempFile("dbcase", "xml"));
            ctrl.crearAlmacenPers(ctrl.getFiletemp().getPath());
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
		return null;
	}
}