package controlador.comandos.Vistas;

import controlador.Comando;
import controlador.Contexto;
import controlador.Controlador;
import controlador.TC;
import misc.UtilsFunc;

import java.io.File;
import java.util.Objects;

import static vista.utils.Otros.DIRECTORY;
import static vista.utils.Otros.INCIDENCES;

public class ComandoGuardar extends Comando{

	public ComandoGuardar(Controlador ctrl) {
		super(ctrl);
	}

	@Override
	public Contexto ejecutar(Object datos) {
		
		String guardarPath = (String) datos;
        String tempPath = ctrl.getFiletemp().getAbsolutePath();
        UtilsFunc.FileCopy(tempPath, guardarPath);
        ctrl.getFactoriaGUI().getGUI(TC.GUI_WorkSpace, false, false).setInactiva();
        ctrl.setCambios(false);
        
        ctrl.setTiempoGuardado(System.currentTimeMillis() / 1000);
        if (ctrl.getFileguardar().getPath() != datos) {
            File directory = new File(System.getProperty("user.dir") + DIRECTORY + INCIDENCES);
            if (directory.exists()) {
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
            File temp = new File(guardarPath);
            ctrl.setFileguardar(temp);
            ctrl.setContFicherosDeshacer(0);
            ctrl.setLimiteFicherosDeshacer(0);
            ctrl.guardarDeshacer();
        }
        return null;
	}

}
