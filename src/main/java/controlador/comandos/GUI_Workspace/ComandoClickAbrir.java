package controlador.comandos.GUI_Workspace;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;

import java.io.File;
import java.util.Objects;

import javax.swing.SwingUtilities;

import controlador.Controlador;
import controlador.comandos.Comando;

public class ComandoClickAbrir extends Comando {

	public ComandoClickAbrir(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public void ejecutar(Object datos) {
		System.out.println("Pasa por comando click abrir");
		ctrl.setContFicherosDeshacer(0);
		ctrl.setLimiteFicherosDeshacer(0);
		ctrl.setAuxDeshacer(false);
		
        String abrirPath = (String) datos;
        String tempPath = ctrl.getFiletemp().getAbsolutePath();
        Controlador.FileCopy(abrirPath, tempPath);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ctrl.getTheServiciosSistema().reset();
                ctrl.getTheGUIPrincipal().loadInfo();
                ctrl.getTheGUIPrincipal().reiniciar();
            }
        });
        ctrl.setCambios(false);
        
        File directory = new File(System.getProperty("user.dir") + DIRECTORY + INCIDENCES);
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }
        ctrl.guardarDeshacer();
        ctrl.setTiempoGuardado(System.currentTimeMillis() / 1000);
	}

}
