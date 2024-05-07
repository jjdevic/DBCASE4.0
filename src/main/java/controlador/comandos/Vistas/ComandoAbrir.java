package controlador.comandos.Vistas;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import misc.UtilsFunc;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;

public class ComandoAbrir extends Comando {

	public ComandoAbrir(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		ctrl.setContFicherosDeshacer(0);
		ctrl.setLimiteFicherosDeshacer(0);
		ctrl.setAuxDeshacer(false);
		
        String abrirPath = (String) datos;
        String tempPath = ctrl.getFiletemp().getAbsolutePath();
        UtilsFunc.FileCopy(abrirPath, tempPath);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getFactoriaServicios().getServicioSistema().reset();
                ctrl.getFactoriaGUI().getGUIPrincipal().loadInfo();
                ctrl.getFactoriaGUI().getGUIPrincipal().reiniciar();
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
        return null;
	}

}
