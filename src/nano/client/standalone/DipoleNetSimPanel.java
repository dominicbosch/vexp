/*
 * Copyright (c) 2002 by Tibor Gyalog, Raoul Schneider, Dino Keller, Christian
 * Wattinger, Martin Guggisberg and The Regents of the University of Basel. All
 * rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF BASEL BE LIABLE TO ANY PARTY FOR DIRECT,
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE
 * USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF BASEL
 * HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF BASEL SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND THE UNIVERSITY OF BASEL HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * Authors: Tibor Gyalog, Raoul Schneider, Dino Keller, Christian Wattinger,
 * Martin Guggisberg <vexp@nano-world.net>
 * 
 *  
 */
package nano.client.standalone;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;

import nano.awt.FloatControl;
import nano.compute.Simulator;
import nano.client.*;

class DipoleNetSimPanel extends NetSimPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Applet myApplet;

	/**
	 * @param MyNewSimulator
	 */
	public DipoleNetSimPanel(Simulator MyNewSimulator,DipoleStandAloneApplet myApplet) {
		super(MyNewSimulator);
		this.myApplet =myApplet;
		
	}



	public void init() {
		Panel Wrap = new Panel();
		Wrap.setLayout(new BorderLayout());
		Panel Knoepfe = new Panel();
		Knoepfe.setLayout(new FlowLayout());
		Panel Knoepfe2 = new Panel();
		Knoepfe2.setLayout(new FlowLayout());
		
		
		approach = new Button("Start/Stop");
		approach.addActionListener(new approachButtonListener());
		
		zoom = new Button("Zoom");
		zoom.addActionListener(new zoomButtonListener());
		zoom.setEnabled(false);
		
		
		zoomout= new Button ("Zoom out");
		zoomout.addActionListener(new zoomoutButtonListener());
		zoomout.setEnabled(false);

        Button resetexperiment = new Button("New Dipoles");
        resetexperiment.addActionListener(new resetexperimentButtonListener());
		
		e = new TextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		chatField = new TextField("Incoming Messages !", 30);
		chatField.setEditable(false);

		Button ExitApplet = new Button("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());
		
		
		Knoepfe.add(approach);
		Knoepfe.add(e);
		Knoepfe.add(resetexperiment);
		Knoepfe.add(ExitApplet) ;

		
		Knoepfe2.add(zoom);
		Knoepfe2.add(zoomout);
		//		 Don't show the chatfield, but use the 
		// chatfield is used by dynamic gui

		//Knoepfe.add(chatField);
          
		
		Wrap.add("South", Knoepfe2);
		Wrap.add("Center", Knoepfe);
		Wrap.add("North", DynamicPanel);

		add(Wrap);

	}

	//===================== Command Executors =====================

	class ChannelSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {

			if ((e.getItem()).equals("1 (Friction forward)")) {
				put("command=set name=channel value=3");
			} else {
				if ((e.getItem()).equals("2 (Friction backward)")) {
					put("command=set name=channel value=5");
				} else {
					put("command=set name=channel value=0");
				}

			}
		}
	}
	
	
	
	class ExitAppletButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			//System.out.println("approach");
			put("command=stop");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.out.println("Could not wait "+e1.getMessage());
			}
			URL url;
			try {
				url = new URL(myApplet.getDocumentBase(), "index.html");
				myApplet.getAppletContext().showDocument(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				System.out.println("wrong URL:"+e.getMessage());
			}
			
		
		}
	}
	
	class PotentialSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			put("command=set name=v1 value="+e.getItem().toString());	
		}
	}
	
    class resetexperimentButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ee) {
   //         System.out.println("resetexperiment");
            put("command=resetexperiment");
        }
    }
	
	class zoomoutButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			
			FloatControl myzoom = getFloatControl("zoom");
			FloatControl xoffset=getFloatControl("xoffset");
			FloatControl yoffset=getFloatControl("yoffset");
			
			//double maxsize =  myzoom.getMaxValue();
			
			myzoom.FloatEventPerformed("zoom",8.0);
			xoffset.FloatEventPerformed("xoffset",0.0);
			yoffset.FloatEventPerformed("yoffset",0.0);
			
			
			put("command=set name=zoom value=8.0");		
			put("command=set name=yoffset value=0");
			put("command=set name=xoffset value=0");
			
			
		}
	}

	class zoomButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			double PannelSize = 256;
			double oldzoom =0;
			double oldxoffset =0;
			double oldyoffset =0;
			
			
			double unknownconst = 4.95;
			double unknownfactor = 1.115;
			
			
			FloatControl myzoom = getFloatControl("zoom");
			oldzoom = myzoom.getValue();
			
			
			
			
		//	System.out.println("zoomvalue:"+oldzoom);
			Point end = getZoomEndPoint();
			Point start = getZoomStartPoint();
			double r = end.getX()-start.getX();
		//	System.out.println("r"+r);
			
			
			double newzoom = (end.getX()-start.getX())/PannelSize*oldzoom*unknownfactor;
			
			double r2 = end.getY()-start.getY();
		//	System.out.println("r2"+r2);
			
			FloatControl xoffset=getFloatControl("xoffset");
			
			System.out.println("xcontroll"+xoffset.getValue());
//			(shift of the center in pixel)* -1 (direction) 2/PannelSize is the span (origin is in
			// the center
			double xoff= (start.getX()/PannelSize*oldzoom*unknownconst);
			//double xoff= (start.getX()+r/2-PannelSize/2)*(-1)/PannelSize*oldzoom;
			System.out.println("Xpoint "+(start.getX()+r/2-PannelSize/2));
			System.out.println("Xoff "+start.getX());
			
			oldxoffset = xoffset.getValue();
			xoffset.FloatEventPerformed("xoffset",oldxoffset+xoff);
			
			
			FloatControl yoffset=getFloatControl("yoffset");
			//(shift of the center in pixel)* -1 (direction) 2/PannelSize is the span (origin is in
			// the center
			double yoff=(start.getY()/PannelSize*oldzoom*unknownconst);
			//System.out.println("Ypointstart: "+start.getY());
			System.out.println("Ypoint"+(PannelSize/2-start.getY()-r/2));
			System.out.println("Yoff "+yoff);
			
			
			oldyoffset = yoffset.getValue();
			yoffset.FloatEventPerformed("yoffset",oldyoffset+yoff);
			
			
			
			System.out.println("zoom old"+oldzoom);
			System.out.println("zoom new"+newzoom);
	
			myzoom.FloatEventPerformed("zoom",newzoom);
			System.out.println("d"+(end.getX()-start.getX()));
			
			
					
			put("command=set name=yoffset value="+(oldyoffset+yoff));
			put("command=set name=xoffset value="+(oldxoffset+xoff));
			put("command=set name=zoom value="+newzoom);
			
			
			
			//System.out.println("approach");
			
			System.out.println("ZoomStart: "+getZoomStartPoint());
			System.out.println("ZoomEnd: "+getZoomEndPoint());
			System.out.println("ZoomValue: "+newzoom);
			
		}
	}

    
    
	class AlphaSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			double d =Integer.parseInt(e.getItem().toString())*Math.PI/180;
			put("command=setAlpha value="+d);	
		}
	}

	//====================== div. Methoden ==========================
}