package vista.componentes.GUIPanels;

import controlador.Controlador;
import controlador.TC;
import misc.UtilsFunc;
import modelo.transfers.Transfer;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferAtributo;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import vista.Lenguaje;
import vista.iconos.*;
import vista.tema.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

/** Panel desde el que se pueden añadir elementos al panel de diseño */
@SuppressWarnings("serial")
public class addTransfersPanel extends JPanel {
    private Controlador controlador;
    private Vector<Transfer> listaTransfers;
    private int[] coords;
    private int diagramWidth;

    public addTransfersPanel(Controlador c, Vector<Transfer> lisTra) {
        super();
        coords = new int[2];
        Random random = new Random();
        coords[0] = random.nextInt(500 - 70 + 1) + 70;
        coords[1] = random.nextInt(500 - 50 + 1) + 50;
        ;
        Theme theme = Theme.getInstancia();
        this.controlador = c;
        this.listaTransfers = lisTra;
        IconLabel anadirEntidad = new IconLabel(new entityIcon(), Lenguaje.text(Lenguaje.ENTITY));
        IconLabel anadirRelacion = new IconLabel(new relationIcon(), Lenguaje.text(Lenguaje.RELATION));
        IconLabel anadirIsa = new IconLabel(new isaIcon(), Lenguaje.text(Lenguaje.ISA_RELATION));
        IconLabel anadirAttribute = new IconLabel(new attributeIcon(), Lenguaje.text(Lenguaje.ATTRIBUTE));
        IconLabel eliminar = new IconLabel(new deleteIcon(), Lenguaje.text(Lenguaje.DELETE));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        this.add(anadirEntidad);
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        this.add(anadirRelacion);
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        this.add(anadirIsa);
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        this.add(anadirAttribute);
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        this.add(eliminar);
        this.add(Box.createVerticalGlue());
        this.setBackground(theme.toolBar());
        this.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        anadirEntidad.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D p = new Point2D.Double(coords[0], coords[1]);
                controlador.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarEntidad, UtilsFunc.crearVector(p, null, null));
                aumentaCoords();
            }
        });
        anadirRelacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D p = new Point2D.Double(coords[0], coords[1]);
                controlador.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionNormal, p);
                aumentaCoords();
            }

        });
        anadirIsa.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D p = new Point2D.Double(coords[0], coords[1]);
                controlador.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarRelacionIsA, p);
                aumentaCoords();
            }
        });
        anadirAttribute.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getListaTransfers();
                controlador.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_InsertarAtributo, listaTransfers);
            }
        });
        eliminar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getListaTransfers();
                controlador.mensajeDesde_PanelDiseno(TC.PanelDiseno_Click_Eliminar, listaTransfers);
            }
        });
    }

    private void getListaTransfers() {
        listaTransfers = new Vector<Transfer>();
        listaTransfers.addAll((Collection<TransferRelacion>) controlador.mensaje(TC.ObtenerListaRelaciones, null));
        listaTransfers.addAll((Collection<TransferEntidad>) controlador.mensaje(TC.ObtenerListaEntidades, null));
        listaTransfers.addAll((Collection<TransferAtributo>) controlador.mensaje(TC.ObtenerListaAtributos, null));
        listaTransfers.addAll((Collection<TransferAgregacion>) controlador.mensaje(TC.ObtenerListaAgregaciones, null));
    }

    private void aumentaCoords() {
        coords[0] = coords[0] < diagramWidth ? coords[0] + 150 : 70;
        coords[1] = coords[0] == 70 ? coords[1] + 70 : coords[1];
    }

    public void setDiagramWidth(int diagramWidth) {
        this.diagramWidth = diagramWidth < 150 ? 0 : diagramWidth - 150;
    }
}
