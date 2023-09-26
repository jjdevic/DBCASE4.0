package vista.diagrama.lineas;

import java.awt.Dimension;
/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.Context;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EdgeShape.IndexedRendering;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import modelo.transfers.TransferEntidad;
import modelo.transfers.TransferRelacion;
import persistencia.EntidadYAridad;
import vista.tema.Theme;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CreaLineas<V,E> implements Renderer.Edge<V, E> {
	private Theme theme;
	
	public CreaLineas() {
		this.theme = Theme.getInstancia();
	}
	
	public void paintEdge(RenderContext<V,E> rc, Layout<V, E> layout, E e) {
		GraphicsDecorator g2d = rc.getGraphicsContext();
		g2d.setColor(theme.lines());
        Graph<V,E> graph = layout.getGraph();
        
        if (!rc.getEdgeIncludePredicate().evaluate(Context.<Graph<V,E>,E>getInstance(graph,e)))
            return;
        // don't draw edge if either incident vertex is not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst(); //RelaciÃÂ³n
        V v2 = endpoints.getSecond(); //Entidad
        Collection<E> aris= graph.getEdges();
        ArrayList<Pair<V>> lista= new ArrayList<Pair<V>>();//Lista que representa el grafo
        Pair<V> par;
        int numApariciones= 0;//Numero de veces que aparece la arista a dibujar en el grafo
        String nom1="";
        String nom2="";
        for (E o : aris){
            //boolean esRecursiva = false;
        	par = graph.getEndpoints(o);
        	if (endpoints.equals(par)) {//Lo que voy a aÃÂ±adir en la lista es igual que la arista que voy a pintar
        		numApariciones++;
        		nom1 = par.getFirst().toString();
        		//if(nom2.equals(par.getSecond().toString())) esRecursiva = true;
            	nom2 = par.getSecond().toString();
        	}
        	lista.add(par);
        	
        } //Fin del bucle
        
        
        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v1)) || 
            !rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v2)))
            return;
        
        Stroke new_stroke = rc.getEdgeStrokeTransformer().transform(e);
        Stroke old_stroke = g2d.getStroke();
        if (new_stroke != null) g2d.setStroke(new_stroke);
        //Dibujo tantas aristas como asociaciones haya entre los nodos
        for (int i=numApariciones; i>0;i--)
        	drawSimpleEdge(rc, layout, e,nom1,nom2,numApariciones,i);
        
        // restore paint and stroke
        if (new_stroke != null) g2d.setStroke(old_stroke);
	}


    /**
     * Draws the edge <code>e</code>, whose endpoints are at <code>(x1,y1)</code>
     * and <code>(x2,y2)</code>, on the graphics context <code>g</code>.
     * The <code>Shape</code> provided by the <code>EdgeShapeFunction</code> instance
     * is scaled in the x-direction so that its width is equal to the distance between
     * <code>(x1,y1)</code> and <code>(x2,y2)</code>.
     */
   protected void drawSimpleEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e,String nombre1, 
						String nombre2,int numApariciones, int vuelta) {
        
        GraphicsDecorator g = rc.getGraphicsContext();
        g.setColor(theme.lines());
        Graphics2D graf2d = g.getDelegate();
        Graph<V,E> graph = layout.getGraph();
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();//RelaciÃÂ³n
        V v2 = endpoints.getSecond();//Entidad
        
        Point2D p1 = layout.transform(v1);//Coordenadas de la relacion
        Point2D p2 = layout.transform(v2);//Coordenadas de la entidad
        p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
        p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
        
        //Ancho del rectÃÂ¡ngulo donde va la entidad. SÃÂ³lo importa su tamaÃÂ±o en IsA->Hija
        int anchoRect=0;
        
        //A las coordenadas se le suma el parametro noTocan para evitar que se superpongan las lineas si hay que pintar muchas
        float xIsA = (float) p1.getX();//Coordenada x de la IsA
       	float yIsA = (float) p1.getY();//Coordenada y de la IsA
       	float xEnti = (float) p2.getX();//Coordenada x de la entidad	        	        
       	float yEnti = (float) p2.getY();//Coordenada y de la entidad
       	
       	//Variables para calcular el centro desde donde se pinta la arista, y la inclinaciÃÂ³n
       	float dx = 0, dy = 0, thetaRadians = 0;
        boolean diagonal=false;
        double entiSize;
        //quitar esto  seguro pa lo de la flecha
        /*if(numApariciones > 1) { //Para el resto se pinta siempre una arista normal no dirigida
            new LineaRecta(rc, layout, e, nombre1, graph, diagonal, nombre2, numApariciones, vuelta, thetaRadians, v1, v2, xIsA, yIsA, xEnti, yEnti, dx, dy, g);
       
        }
        else {*/
        	

        float incrementoX = 0;
        float incrementoY = 0;
		
        if(numApariciones > 1) {
			Pair p = sacarCoordenadas(nombre1,nombre2,numApariciones,vuelta,xIsA,yIsA,xEnti,yEnti);
			incrementoX = (float) p.getFirst();
			incrementoY = (float) p.getSecond();
		}

	        if((endpoints.getFirst() instanceof TransferRelacion) && 
	        		(((TransferRelacion)endpoints.getFirst()).getTipo().equals("IsA"))){
		         
	        		TransferRelacion rela =(TransferRelacion)endpoints.getFirst();
	        		EntidadYAridad padre = (EntidadYAridad)rela.getListaEntidadesYAridades().get(0);
	        		int idPadre =padre.getEntidad();        		
	        		TransferEntidad enti =(TransferEntidad)endpoints.getSecond();
	        		//Flecha del padre a la relaciÃÂ³n IsA
	        		if(enti.getIdEntidad()==idPadre){
	        			xEnti += incrementoX;
	        			xIsA += incrementoX;
	        			yEnti += incrementoY;
	        			yIsA += incrementoY;
	        			Flecha miFlecha= new Flecha();
	        			miFlecha.createArrow(yEnti,yIsA,xEnti,xIsA,true,anchoRect);
	        			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,true,anchoRect);
	        		}
	        		//Flecha de la relaciÃÂ³n IsA al hijo
	        		else{
	        			anchoRect = enti.getNombre().length();
	        			xEnti += incrementoX;
	        			xIsA += incrementoX;
	        			yEnti += incrementoY;
	        			yIsA += incrementoY;
	        			Flecha miFlecha= new Flecha();
	        			miFlecha.createArrow(yEnti,yIsA,xEnti,xIsA,false,anchoRect);
	        			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,false,anchoRect);
	        		}
		        diagonal= false;
	        }
	        //Si la relacion es IsA en lugar de una arista normal se pintarÃÂ¡ una flecha
	        else if((endpoints.getSecond() instanceof TransferRelacion) && 
	        		(((TransferRelacion)endpoints.getSecond()).getTipo().equals("IsA"))){	        
	        	TransferRelacion rela =(TransferRelacion)endpoints.getSecond();
	    		EntidadYAridad padre = (EntidadYAridad)rela.getListaEntidadesYAridades().get(0);
	    		int idPadre =padre.getEntidad();
	    		TransferEntidad enti =(TransferEntidad)endpoints.getFirst();
	    		//Flecha del padre a la relaciÃÂ³n IsA
	    		if(enti.getIdEntidad()==idPadre){
	    			xEnti += incrementoX;
        			xIsA += incrementoX;
        			yEnti += incrementoY;
        			yIsA += incrementoY;
	    			Flecha miFlecha= new Flecha();
	    			miFlecha.createArrow(yEnti,yIsA,xEnti,xIsA,true,anchoRect);
	    			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,true,anchoRect);
	    		}
	    		//Flecha de la relaciÃÂ³n IsA al hijo
	    		else{
	    			xEnti += incrementoX;
        			xIsA += incrementoX;
        			yEnti += incrementoY;
        			yIsA += incrementoY;
	    			anchoRect = enti.getNombre().length();
	    			Flecha miFlecha= new Flecha();
	    			miFlecha.createArrow(yIsA,yEnti,xIsA,xEnti,false,anchoRect);
	    			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,false,anchoRect);
	    		}
	    		diagonal=false;
	        }
	        //Si es linea de relacion a entidad
	        else if(endpoints.getFirst() instanceof TransferRelacion && endpoints.getSecond() instanceof TransferEntidad){
	        	EntidadYAridad ent = (EntidadYAridad)((TransferRelacion) endpoints.getFirst()).getEntidadYAridad(((TransferEntidad) endpoints.getSecond()).getIdEntidad());
	        	TransferRelacion tr = (TransferRelacion) endpoints.getFirst();
	        	//cardinalidad N
	        	if(ent.getPrincipioRango() > 0 && ent.getPrincipioRango() != Integer.MAX_VALUE) {
	        		xEnti += incrementoX;
        			xIsA += incrementoX;
        			yEnti += incrementoY;
        			yIsA += incrementoY;
	        		if(ent.tieneFlecha()) {
	        			entiSize = ((TransferEntidad) endpoints.getSecond()).getNombre().length();
			    		if(entiSize < 8) entiSize = 8;
			    		entiSize += 25;
	        			new DobleLineaYFlecha(rc, e, graph, diagonal, thetaRadians, xIsA, yIsA, xEnti, yEnti,entiSize, g);
	        		}
	        		else new DobleLinea(rc, e, graph, diagonal, thetaRadians, xIsA, yIsA, xEnti, yEnti, g);
	        	}else{
	        		if(ent.tieneFlecha()) {
	        			xEnti += incrementoX;
	        			xIsA += incrementoX;
	        			yEnti += incrementoY;
	        			yIsA += incrementoY;
	        			Flecha miFlecha = new Flecha();
		    			miFlecha.createArrow(yEnti,yIsA,xEnti,xIsA,false,anchoRect);
		    			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,false,anchoRect);
	        		}else new LineaRecta(rc, layout, e, nombre1, graph, diagonal, nombre2, numApariciones, vuelta, thetaRadians, v1, v2, xIsA, yIsA, xEnti, yEnti, dx, dy, g);
	        	}
	    		diagonal=false;
	        }
	        else if(endpoints.getFirst() instanceof TransferEntidad && endpoints.getSecond() instanceof TransferRelacion){
	        	EntidadYAridad ent = (EntidadYAridad)((TransferRelacion) endpoints.getFirst()).getEntidadYAridad(((TransferEntidad) endpoints.getSecond()).getIdEntidad());
	    		//cardinalidad N
	        	
	        	if(ent.getPrincipioRango() > 0) {
	        		xEnti += incrementoX;
	    			xIsA += incrementoX;
	    			yEnti += incrementoY;
	    			yIsA += incrementoY;
	        		new DobleLinea(rc, e, graph, diagonal, thetaRadians, xIsA, yIsA, xEnti, yEnti, g);
	        	}
	        	else if(ent.getFinalRango() == Integer.MAX_VALUE && (ent.getPrincipioRango() == 0 || ent.getPrincipioRango()  == Integer.MAX_VALUE)){
	        		xEnti += incrementoX;
	    			xIsA += incrementoX;
	    			yEnti += incrementoY;
	    			yIsA += incrementoY;
	        		Flecha miFlecha = new Flecha();
	    			miFlecha.createArrow(yEnti,yIsA,xEnti,xIsA,false,anchoRect);
	    			miFlecha.paintComponent(graf2d,xIsA,yIsA,xEnti,yEnti,false,anchoRect);
	    		}else if(ent.getFinalRango() == Integer.MAX_VALUE && ent.getPrincipioRango() == 1) {
	    			xEnti += incrementoX;
	    			xIsA += incrementoX;
	    			yEnti += incrementoY;
	    			yIsA += incrementoY;
	    			entiSize = ((TransferEntidad) endpoints.getSecond()).getNombre().length();
		    		if(entiSize < 8) entiSize = 8;
		    		entiSize += 25;
		    		new DobleLineaYFlecha(rc, e, graph, diagonal, thetaRadians, xIsA, yIsA, xEnti, yEnti,entiSize, g);
	    		}else new LineaRecta(rc, layout, e, nombre1, graph, diagonal, nombre2, numApariciones, vuelta, thetaRadians, v1, v2, xIsA-incrementoX, yIsA-incrementoY, xEnti-incrementoX, yEnti-incrementoY, dx, dy, g);
	    		diagonal=false;
	        }
	        else //Para el resto se pinta siempre una arista normal no dirigida
	            new LineaRecta(rc, layout, e, nombre1, graph, diagonal, nombre2, numApariciones, vuelta, thetaRadians, v1, v2, xIsA, yIsA, xEnti, yEnti, dx, dy, g);
		//}
	}
   
   Pair sacarCoordenadas(String nombre1,String nombre2, int numApariciones, int vuelta,
			float xIsA, float yIsA, float xEnti, float yEnti) {
	
       //parte de cÃ³digo nueva para las lÃ­neas de los roles

       float xCentro;
	   float yCentro;
	   float xNoCentro;
	   float yNoCentro;
	  	
       int alto = 0,anchoNoCentro=0, altoNoCentro=0;

       //Calculo el ancho mÃ­nimo entre la relaciÃ³n y la entidad.
       int ancho = Math.min(nombre1.length(),nombre2.length());
       //Hay que saber si el ancho mÃ­nimo es de la entidad o de la relaciÃ³n

       xCentro = xIsA;
		yCentro = yIsA;
		xNoCentro = xEnti;
		yNoCentro = yEnti;
       if(ancho == nombre1.length()) anchoNoCentro = nombre2.length();
       else anchoNoCentro=nombre2.length();
       

       //Si el ancho  es menor que 8 la figura tiene un tamaÃ±o fijo

       if(ancho < 8){
       	ancho = 45;
       	alto = 20;
       }
       //Si no el ancho es proporcional a la longitud del nombre
       else{
       	ancho = (ancho *5) +5;
       	alto = 25;
       }

       //Si el ancho de la otra figura es menor que 8 la figura tiene un tamaÃ±o fijo

       if(anchoNoCentro < 8){
       	anchoNoCentro = 45;
       	altoNoCentro = 20;
       }
       //Si no el ancho de la otra figura es proporcional a la longitud del nombre
       else{
       	anchoNoCentro = (anchoNoCentro *5) +5;
       	altoNoCentro = 20;
       }    
       
       	float incrementoX = 0;
       	float incrementoY = 0;
       	int epsilon = ancho /4;

       	//La separaciÃ³n entre las lÃ­neas de los roles es proporcional al nÃºmero de veces que participe la entidad en la relaciÃ³n
       	 //y la posiciÃ³n relativa entre la entidad y la relacion
       	if ((((xNoCentro+anchoNoCentro)>=(xCentro-epsilon))&&((xNoCentro-anchoNoCentro) <= xCentro+epsilon)) ||
       		((xNoCentro>=(xCentro-epsilon))&&(xNoCentro <= xCentro+epsilon))){
       		//EstÃ¡n a distintas alturas pero en la misma franja de las xs
       		incrementoY = 0;
       		if (numApariciones > 3){
       			incrementoX = (ancho*2)/(numApariciones+1);
       			incrementoX = ancho-(vuelta*incrementoX);
       			incrementoY *= vuelta;
       		}
       		else if ((numApariciones == 2)||(numApariciones == 3)){
    			if(vuelta==1) {
    				incrementoX = ancho-7;
    				incrementoX *= vuelta;
    				incrementoY = vuelta*incrementoY;
    			}
    			else if (vuelta ==2) {
    				incrementoX = -ancho+7;
    				incrementoX *= vuelta;
    				incrementoY = vuelta*incrementoY;
    			}
    			else if (vuelta==3) {
    				incrementoX = 0;
    				incrementoY = incrementoY*vuelta;
    			}
    		}
       	}
       	else if((((yNoCentro+altoNoCentro)>=(yCentro-epsilon))&&((yNoCentro-altoNoCentro)<=(yCentro+epsilon))) ||
       			((yNoCentro>=(yCentro-epsilon))&&(yNoCentro<=(yCentro+epsilon)))){

       	//EstÃ¡n en la misma franja de las ys
       		incrementoX = 0;
       		if (numApariciones > 3){
       			incrementoY = (alto*2)/(numApariciones+1);
       			incrementoY = alto - (vuelta*incrementoY);
       			incrementoX *= vuelta;
       		}
       		else if ((numApariciones == 2)||(numApariciones == 3)){
    			if(vuelta==1) {
    				incrementoX *= vuelta;
    				incrementoY = alto-7;
    			}
    			else if (vuelta ==2) {
    				incrementoX *= vuelta;
    				incrementoY = -alto+7;
    			}
    			else if (vuelta==3) {
    				incrementoX *= vuelta;
    				incrementoY *= vuelta;
    			}
    		}
       	}
       	//Diagonal inferior izquierda
       	else if(((yNoCentro-altoNoCentro)>(yCentro+epsilon)) && ((xNoCentro-anchoNoCentro)<(xCentro-epsilon))){
       		incrementoX = ancho/(numApariciones+1);
       		incrementoX = -(vuelta*incrementoX);
       		incrementoY = alto /(numApariciones+1);
       		incrementoY = alto-(vuelta*incrementoY);
       		
       	}
       	//Diagonal superior izquierda
       	else if(((yNoCentro+altoNoCentro)<(yCentro-epsilon))&&(xNoCentro-anchoNoCentro)<=(xCentro+epsilon)){
       		incrementoX = ancho/(numApariciones+1);
       		incrementoX = -ancho+(vuelta*incrementoX);
       		incrementoY = alto /(numApariciones+1);
       		incrementoY = -(vuelta*incrementoY);
       	}
       	//Diagonal superior derecha
       	else if(((xNoCentro-anchoNoCentro)>(xCentro+epsilon))&& (yNoCentro+altoNoCentro)<(yCentro-epsilon)){
       		incrementoX = ancho/(numApariciones+1);
       		incrementoX *= vuelta;
       		incrementoY = alto /(numApariciones+1);
       		incrementoY = -alto+(vuelta*incrementoY);
       	}
       	//Diagonal inferior derecha
       	else if(((xNoCentro-anchoNoCentro)>(xCentro+epsilon)&&((yNoCentro-altoNoCentro)>yCentro+epsilon))){
       		incrementoX = ancho/(numApariciones+1);
       		incrementoX = ancho-(vuelta*incrementoX);
       		incrementoY = alto /(numApariciones+1);
       		incrementoY *= vuelta;
       	}
       	
       	return new Pair<>(incrementoX, incrementoY);
       
	}
}
