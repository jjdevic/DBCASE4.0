package vista.frames;

import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controlador.Controlador;
import controlador.TC;
import modelo.transfers.TransferAgregacion;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.componentes.MyComboBoxRenderer;
import vista.imagenes.ImagePath;
import vista.lenguaje.Lenguaje;

@SuppressWarnings({"rawtypes" ,"unchecked", "serial"})
public class GUI_AnadirEntidadARelacion extends Parent_GUI{

	private Controlador controlador;
	private Vector<TransferEntidad> listaEntidades;
	private JComboBox comboEntidadesyAgregaciones;
	private JLabel jLabel1;
	private JTextField cajaFinal;
	private JRadioButton buttonMaxN;
	private JRadioButton buttonMax1;
	private JRadioButton buttonMinMax;
	private JRadioButton totalParticipation;
	private JRadioButton partialParticipation;
	private JButton botonInsertar;
	private JButton botonCancelar;
	private JTextField cajaInicio;
	private JLabel explicacion2;
	private JLabel explicacion;
	private TransferRelacion relacion;
	private JLabel explicacion3;
	private JLabel explicacion4;
	private JLabel explicacion5;
	private JTextField cajaRol;
	private JSeparator separador1;
	private Vector<String> items;

	public GUI_AnadirEntidadARelacion() {
		initComponents();
		
	}

	private void initComponents() {
		setTitle(Lenguaje.text(Lenguaje.INSERT_NEW_ENTITY_TO_RELATION));
        this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource(ImagePath.LOGODBDT)).getImage());
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        getContentPane().setLayout(null);
        getContentPane().add(getExplicacion());
        getContentPane().add(getComboEntidades());
        getContentPane().add(getExplicacion2());
        getContentPane().add(getCajaInicio());
        getContentPane().add(getCajaFinal());
        getContentPane().add(getJLabel1());
        getContentPane().add(getExplicacion3());
        getContentPane().add(getCajaRol());
        getContentPane().add(getBotonInsertar());
        getContentPane().add(getBotonCancelar());
        getContentPane().add(getButton1a1());
        getContentPane().add(getButtonNaN());
        getContentPane().add(getButtonMinMax());
        getContentPane().add(getExplicacion4());
        getContentPane().add(getTotalParticipationButton());
        getContentPane().add(getPartialParticipationButton());
        getContentPane().add(getExplicacion5());
        getContentPane().add(getSeparador1());

        
        this.setSize(300,480);
        this.addMouseListener(this);
		this.addKeyListener(this);
    }

	/*
	 * Activar y desactivar el dialogo
	 */
	
	public void setActiva(){
		// Le pedimos al controlador que nos actualice la lista de entidades
		this.controlador.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ActualizameListaEntidades, null);
		// Generamos los items del comboEntidades
		items = this.generaItems();
		//Los ordenamos alfabéticamente
		if(items.size() == 0)
			JOptionPane.showMessageDialog(
				null,
				(Lenguaje.text(Lenguaje.ERROR))+"\n" +
				(Lenguaje.text(Lenguaje.INSERT_NEW_ENTITY_TO_RELATION))+"\n" +
				(Lenguaje.text(Lenguaje.IMPOSIBLE_TO_INSERT_ENTITY))+"\n" +
				(Lenguaje.text(Lenguaje.NO_ENTITY))+"\n",
				(Lenguaje.text(Lenguaje.INSERT_ATTRIBUTE)),
				JOptionPane.PLAIN_MESSAGE);	
		else{
			this.cajaInicio.setText("");
			this.cajaFinal.setText("");
			this.buttonMax1.setEnabled(true);
			this.buttonMax1.setSelected(false);
			this.buttonMaxN.setEnabled(true);
			this.buttonMaxN.setSelected(true);
			this.buttonMinMax.setEnabled(true);
			this.buttonMinMax.setSelected(false);
			this.totalParticipation.setEnabled(true);
			this.totalParticipation.setSelected(false);
			this.partialParticipation.setEnabled(true);
			this.partialParticipation.setSelected(false);
			this.cajaInicio.setEnabled(false);
			this.cajaFinal.setEnabled(false);
			this.comboEntidadesyAgregaciones.setModel(new javax.swing.DefaultComboBoxModel(items));
			this.comboEntidadesyAgregaciones.setSelectedItem(primerItem());
			this.cajaRol.setText("");
			
			this.centraEnPantalla();
			SwingUtilities.invokeLater(doFocus);
			this.setVisible(true);
			
		}	
	}
	
	private Runnable doFocus = new Runnable() {
	     public void run() {
	         comboEntidadesyAgregaciones.grabFocus();
	     }
	 };

	private Vector<String> generaItems(){
		// Generamos los items
		int cont = 0;
		Vector<String> items = new Vector<String>(this.listaEntidades.size());
		while (cont<this.listaEntidades.size()){
			TransferEntidad te = this.listaEntidades.get(cont);
			items.add(cont,te.getNombre());
			cont++;
		}
		return items;
	}
	
	
	/*Devuelve el nombre de la primera entidad que haya en el sistema y no esté participando
	 * en la relación.*/
	private String primerItem(){
		// Filtramos la lista de entidades quitando las entidades que no intervienen
		Vector<EntidadYAridad> vectorTupla = this.getRelacion().getListaEntidadesYAridades();
		Vector vectorIdsEntidades = new Vector();
		int cont = 0; // Para saltar la entidad padre
		//Guardo en vectorIdsEntidades los ids de las entidades que ya participan en esa relacion
		while(cont<vectorTupla.size()){
			vectorIdsEntidades.add((vectorTupla.get(cont)).getEntidad());
			cont++;
		}
		cont = 0;
		boolean encontrado=false;
		Vector<TransferEntidad> listaEntidadesFiltrada = new Vector<TransferEntidad>();
		while((cont<this.getListaEntidades().size())&&(!encontrado)){
			TransferEntidad te = this.getListaEntidades().get(cont);
			if(vectorIdsEntidades.contains(te.getIdEntidad())){
				listaEntidadesFiltrada.add(te);
				cont++;
			}
			else
				encontrado= true;
			
		}
		TransferEntidad te;
		if((this.listaEntidades.size()==1)||(!encontrado))
			te = this.listaEntidades.get(0);
		else te = this.listaEntidades.get(cont);
		return te.getNombre();
	}
	
	/*Dada la posición seleccionada en el comboBox devuelve el índice correspondiente a dicho 
	 * elementeo en la lista de Entidades.  Es necesario porque al ordenar alfabeticamente se perdió 
	 * la correspondencia.*/
	private int indiceAsociado (int selec){
		boolean encontrado= false;
		int i=0;
		while ((i<this.getListaEntidades().size())&& (!encontrado)){
			if((this.items.get(selec)).equals(this.getListaEntidades().get(i).getNombre())){
				encontrado =true;
				return i;
			}
			else i++;
		}
		return i;
	}


	public void setInactiva(){
		this.setVisible(false);
	}

	/*
	 * Oyentes de los botones
	 */
	private void botonAnadirActionPerformed(java.awt.event.ActionEvent evt) {
		// Mandaremos el siguiente vector al controlador
		Vector v = new Vector();
		v.add(this.getRelacion());
		v.add(this.getListaEntidades().get(indiceAsociado(this.comboEntidadesyAgregaciones.getSelectedIndex())));
		//En función de que boton de la cardinalidad haya seleccionado se guardará una u otra:
		if(this.cajaInicio.getText().equals("")){
			v.add(String.valueOf(0));
		}
		else {
			v.add(String.valueOf(cajaInicio.getText().toLowerCase()));
		}
		if(this.cajaFinal.getText().equals("")) {
			v.add("n");
		}
		else {
			v.add(String.valueOf(this.cajaFinal.getText().toLowerCase()));

		}
		v.add(String.valueOf(this.cajaRol.getText()));
		//Incluimos en el vector cuales de los campos están marcados:
		v.add(this.buttonMax1.isSelected()||this.buttonMaxN.isSelected());
		v.add(this.totalParticipation.isSelected() || this.partialParticipation.isSelected());
		v.add(this.buttonMinMax.isSelected());
		//Marcamos si la cardinalidad 1 en particular esta selseccionada
		v.add(this.buttonMax1.isSelected());
		// Mandamos el mensaje y el vector con los datos
		this.controlador.mensajeDesde_GUI(TC.GUIAnadirEntidadARelacion_ClickBotonAnadir,v);

	}

	private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}
	public void keyPressed( KeyEvent e ) {
		switch (e.getKeyCode()){
			case 27: {
				this.setInactiva();
				break;
			}
			case 10:{
				this.botonAnadirActionPerformed(null);
				break;
			}
		}
	} 
	
	//Oyente para todos los elementos
	private KeyListener general = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==10){botonAnadirActionPerformed(null);}
			if(e.getKeyCode()==27){botonCancelarActionPerformed(null);}
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	};
	
	/*
	 * Interfaz
	 */
	private JLabel getExplicacion() {
		if(explicacion == null) {
			explicacion = new JLabel();
			explicacion.setFont(theme.font());
			explicacion.setText(Lenguaje.text(Lenguaje.SELECT_ENTITY));
			explicacion.setOpaque(false);
			explicacion.setBounds(25, 8, 115, 24);
			explicacion.setFocusable(false);
		}
		return explicacion;
	}
	
	private JComboBox getComboEntidades() {
		if(comboEntidadesyAgregaciones == null) {
			comboEntidadesyAgregaciones = new JComboBox();
			comboEntidadesyAgregaciones.setRenderer(new MyComboBoxRenderer());
			comboEntidadesyAgregaciones.setFont(theme.font());
			comboEntidadesyAgregaciones.setBounds(25, 35, 231, 27);
		}
		comboEntidadesyAgregaciones.addKeyListener(general);
		return comboEntidadesyAgregaciones;
	}
	
	private JLabel getExplicacion2() {
		if(explicacion2 == null) {
			explicacion2 = new JLabel();
			explicacion2.setFont(theme.font());
			explicacion2.setText(Lenguaje.text(Lenguaje.WRITE_NUMBERS_RELATION));
			explicacion2.setOpaque(false);
			explicacion2.setBounds(25, 65, 231, 24);
			explicacion2.setFocusable(false);
		}
		return explicacion2;
	}
	private JLabel getExplicacion4() {
		if(explicacion4 == null) {
			explicacion4 = new JLabel();
			explicacion4.setFont(theme.font());
			explicacion4.setText(Lenguaje.text(Lenguaje.WRITE_ENTITY_PARTICIPATION));
			explicacion4.setOpaque(false);
			explicacion4.setBounds(25, 160, 147, 24);
			explicacion4.setFocusable(false);
		}
		return explicacion4;
	}
	private JLabel getExplicacion5() {
		if(explicacion5 == null) {
			explicacion5 = new JLabel();
			explicacion5.setFont(theme.font());
			explicacion5.setText(Lenguaje.text(Lenguaje.LABEL_MIN_MAX));
			explicacion5.setOpaque(false);
			explicacion5.setBounds(25, 250, 231, 24);
			explicacion5.setFocusable(false);
		}
		return explicacion5;
	}
	private JSeparator getSeparador1() {
		if(separador1==null) {
			separador1=new JSeparator();
			separador1.setBounds(30,155,200,10);
		}
		return separador1;
	}
	private JTextField getCajaInicio() {
		if(cajaInicio == null) {
			cajaInicio = new JTextField();
			cajaInicio.setEditable(true);
			cajaInicio.setEnabled(true);
			cajaInicio.setFont(theme.font());
			cajaInicio.setForeground(theme.labelFontColorDark());
			cajaInicio.setBounds(86, 270, 40, 25);
			cajaInicio.addKeyListener(general);
			//cajaInicio.getDocument().addDocumentListener(documentListener);	
			/*cajaInicio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cajaInicioActionPerformed(evt);
				}
			});*/
		
		}
		/*else 
			cajaInicio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cajaInicioActionPerformed(evt);
				}
			});*/
		return cajaInicio;
	}
	
	private void cajaInicioActionPerformed(ActionEvent evt) {
		if(cajaInicio.getText()=="0") {
			this.totalParticipation.setSelected(false);
			this.partialParticipation.setSelected(true);
		}
		
		else if(cajaInicio.getText()=="1") {
			this.totalParticipation.setSelected(true);
			this.partialParticipation.setSelected(false);
		}
	}
	
	/*DocumentListener documentListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub
			// Código a ejecutar cuando el texto del JTextField cambia
			if(cajaInicio.getText()=="0") {
				totalParticipation.setSelected(false);
				partialParticipation.setSelected(true);
			}
			
			else if(cajaInicio.getText()=="1") {
				totalParticipation.setSelected(true);
				partialParticipation.setSelected(false);
			}
			
		}
		@Override
		public void insertUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub
			if(cajaInicio.getText()=="0") {
				totalParticipation.setSelected(false);
				partialParticipation.setSelected(true);
			}
			
			else if(cajaInicio.getText()=="1") {
				totalParticipation.setSelected(true);
				partialParticipation.setSelected(false);
			}
			
		}
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};*/
	
	private JTextField getCajaFinal() {
		if(cajaFinal == null) {
			cajaFinal = new JTextField();
			cajaFinal.setBounds(176, 270, 40, 25);
			cajaFinal.setFont(theme.font());
			cajaFinal.setForeground(theme.labelFontColorDark());
			cajaFinal.setEnabled(true);
			cajaFinal.setEditable(true);
			cajaFinal.addKeyListener(general);
		}
		return cajaFinal;
	}
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setEnabled(true);
			jLabel1.setFont(theme.font());
			jLabel1.setText(Lenguaje.text(Lenguaje.TO));
			jLabel1.setBounds(138, 270, 60, 25);
		}
		return jLabel1;
	}
	
	private JLabel getExplicacion3() {
		if(explicacion3 == null) {
			explicacion3 = new JLabel();
			explicacion3.setFont(theme.font());
			explicacion3.setText(Lenguaje.text(Lenguaje.WRITE_ROLL));
			explicacion3.setOpaque(false);
			explicacion3.setBounds(25, 300, 231, 24);
			explicacion3.setFocusable(false);
		}
		return explicacion3;
	}
	
	private JTextField getCajaRol() {
		if(cajaRol == null) {
			cajaRol = new JTextField();
			cajaRol.setFont(theme.font());
			cajaRol.setForeground(theme.labelFontColorDark());
			cajaRol.setBounds(25, 330, 232, 27);
		}
		cajaRol.addKeyListener(general);
		return cajaRol;
	}

	private JButton getBotonInsertar() {
		if(botonInsertar == null) {
			botonInsertar = this.botonInsertar(160,400);
			botonInsertar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					botonAnadirActionPerformed(evt);
				}
			});
			botonInsertar.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10) botonAnadirActionPerformed(null);
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return botonInsertar;
	}
	private JButton getBotonCancelar() {
		if(botonCancelar == null) {
			botonCancelar = this.botonCancelar(25,400);
			botonCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					botonCancelarActionPerformed(evt);
				}
			});
			botonCancelar.setMnemonic(27);
			botonCancelar.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10) botonCancelarActionPerformed(null);
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return botonCancelar;
	}
	/*
	 * Getters y setters
	 */
	public Controlador getControlador() {
		return controlador;
	}

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}

	public Vector<TransferEntidad> getListaEntidades() {
		return listaEntidades;
	}

	public void setListaEntidades(Vector<TransferEntidad> listaEntidades) {
		this.listaEntidades = listaEntidades;
	}

	public TransferRelacion getRelacion() {
		return relacion;
	}

	public void setRelacion(TransferRelacion relacion) {
		this.relacion = relacion;
	}
	
	/*Al seleccionar la cardinalidad 1 a 1 deshabilito el resto de botones  y los habilito 
	 * al desseleccionar*/
	private void button1a1ItemStateChanged(java.awt.event.ItemEvent evt) {
		if(this.buttonMax1.isSelected()){
			this.buttonMaxN.setSelected(false);
			this.cajaInicio.setEnabled(true);
			this.cajaFinal.setEnabled(true);
			this.cajaFinal.setEditable(false);
			this.cajaInicio.setEditable(false);
			//this.cajaFinal.setText("1");
			this.cajaInicio.setText(this.cajaInicio.getText());
		}
		else{
			if(!this.buttonMax1.isSelected()){
				this.buttonMaxN.setEnabled(true);
				this.buttonMinMax.setEnabled(true);
			}
		}
	}
	
	/*Al seleccionar la cardinalidad N a N deshabilito el resto de botones  y los habilito 
	 * al desseleccionar*/
	private void buttonNaNItemStateChanged(java.awt.event.ItemEvent evt) {
		if(this.buttonMaxN.isSelected()){
			this.buttonMax1.setSelected(false);
			this.cajaInicio.setEnabled(true);
			this.cajaFinal.setEnabled(true);
			this.cajaFinal.setEditable(false);
			//this.cajaFinal.setText("N");
			this.cajaInicio.setText(this.cajaInicio.getText());
		}
		else{
			if(!this.buttonMaxN.isSelected()){
				this.buttonMax1.setEnabled(true);
				this.buttonMinMax.setEnabled(true);
			}
		}
	}
	
	/*Al seleccionar la cardinalidad Min Max deshabilito el resto de botones  y los habilito 
	 * al desseleccionar*/
	private void buttonMinMaxItemStateChanged(java.awt.event.ItemEvent evt) {
		if(this.buttonMinMax.isSelected()){
			this.cajaInicio.setEnabled(true);
			this.cajaFinal.setEnabled(true);
			this.cajaInicio.setEditable(true);
			this.cajaFinal.setEditable(true);
		}
		
	}
	private void buttonTotalParticipationItemStateChanged(java.awt.event.ItemEvent evt) {
		if(this.totalParticipation.isSelected()) {
			this.partialParticipation.setSelected(false);
			this.cajaInicio.setEnabled(true);
			this.cajaFinal.setEnabled(true);
			this.cajaFinal.setEditable(false);
			this.cajaInicio.setEditable(false);
			this.cajaInicio.setText("1");
		}
	}
	private void buttonPartialParticipationItemStateChanged(java.awt.event.ItemEvent evt) {
		if(this.partialParticipation.isSelected()) {
			this.totalParticipation.setSelected(false);
			this.cajaInicio.setEnabled(true);
			this.cajaFinal.setEnabled(true);
			this.cajaFinal.setEditable(false);
			this.cajaInicio.setEditable(false);
			this.cajaInicio.setText("0");
		}
	}
	private JRadioButton getButton1a1() {
		if(buttonMax1 == null) {
			buttonMax1 = new JRadioButton();
			buttonMax1.setOpaque(false);
			buttonMax1.setEnabled(false);
			buttonMax1.setFont(theme.font());
			buttonMax1.setText(Lenguaje.text(Lenguaje.LABEL1A1));
			buttonMax1.setBounds(25, 100, 127, 24);
			buttonMax1.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					button1a1ItemStateChanged(evt);
				}
			});
			buttonMax1.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10){buttonMax1.setSelected(
												!buttonMax1.isSelected());}
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return buttonMax1;
	}
	
	private JRadioButton getButtonNaN() {
		if(buttonMaxN == null) {
			buttonMaxN = new JRadioButton();
			buttonMaxN.setFont(theme.font());
			buttonMaxN.setOpaque(false);
			buttonMaxN.setSelected(true);
			buttonMaxN.setText(Lenguaje.text(Lenguaje.LABELNAN));
			buttonMaxN.setBounds(25, 130, 127, 24);
			buttonMaxN.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					buttonNaNItemStateChanged(evt);
				}
			});
			buttonMaxN.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10){buttonMaxN.setSelected(!buttonMaxN.isSelected());}
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return buttonMaxN;
	}
	
	private JRadioButton getButtonMinMax() {
		if(buttonMinMax == null) {
			buttonMinMax = new JRadioButton();
			buttonMinMax.setFont(theme.font());
			buttonMinMax.setOpaque(false);
			buttonMinMax.setEnabled(false);
			buttonMinMax.setText(Lenguaje.text(Lenguaje.THE));
			buttonMinMax.setBounds(25, 270, 127, 24);
			buttonMinMax.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					buttonMinMaxItemStateChanged(evt);
				}
			});
			buttonMinMax.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10){buttonMinMax.setSelected(!buttonMinMax.isSelected());}
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return buttonMinMax;
	}
	private JRadioButton getTotalParticipationButton() {
		if(totalParticipation== null) {
			totalParticipation= new JRadioButton();
			totalParticipation.setOpaque(false);
			totalParticipation.setEnabled(false);
			totalParticipation.setFont(theme.font());
			totalParticipation.setText(Lenguaje.text(Lenguaje.LABEL_TOTAL_PARTICIPATION));
			totalParticipation.setBounds(25, 190, 127, 24);
			totalParticipation.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					buttonTotalParticipationItemStateChanged(evt);
				}
			});
			totalParticipation.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10){totalParticipation.setSelected(
												!totalParticipation.isSelected());}
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return totalParticipation;
	}
	private JRadioButton getPartialParticipationButton() {
		if(partialParticipation== null) {
			partialParticipation= new JRadioButton();
			partialParticipation.setOpaque(false);
			partialParticipation.setEnabled(false);
			partialParticipation.setFont(theme.font());
			partialParticipation.setText(Lenguaje.text(Lenguaje.LABEL_PARTIAL_PARTICIPATION));
			partialParticipation.setBounds(25, 220, 127, 24);
			partialParticipation.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					buttonPartialParticipationItemStateChanged(evt);
				}
			});
			partialParticipation.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==10){partialParticipation.setSelected(
												!partialParticipation.isSelected());}
				}
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		return partialParticipation;
	}

}
