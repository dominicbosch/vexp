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
import java.awt.Choice;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nano.awt.FloatControl;
import nano.client.JNetSimPanel;
import nano.client.NetSimPanel;
import nano.client.NetSimPanel.approachButtonListener;
import nano.client.NetSimPanel.chat_listener;
import nano.client.standalone.DipoleNetSimPanel.ExitAppletButtonListener;
import nano.client.standalone.DipoleNetSimPanel.resetexperimentButtonListener;
import nano.client.standalone.DipoleNetSimPanel.zoomButtonListener;
import nano.client.standalone.DipoleNetSimPanel.zoomoutButtonListener;
import nano.client.standalone.ImagingNetSimPanel.ChannelSelectListener;
import nano.compute.ParseException;
import nano.compute.Simulator;

public class nanoNetSimPanel extends JNetSimPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Applet myApplet = null;

	nanoApp myFrame = null;

	private String CurrentSimulatorName;

	private JPanel Wrap;

	/**
	 * @param MyNewSimulator
	 */
	public nanoNetSimPanel(Simulator MyNewSimulator,
			FrictionStandAloneApplet myApplet) {
		super(MyNewSimulator);
		this.myApplet = myApplet;
	}

	public nanoNetSimPanel(Simulator mySimulator, nanoApp app) {
		super(mySimulator);
		this.myFrame = app;
		initButtons();
	}

	public nanoNetSimPanel(Simulator mySimulator, nanoApp app, boolean b) {
		super(mySimulator);
		this.myFrame = app;
		initButtons();

		// TODO Auto-generated constructor stub
	}

	public void destroy() {
		deleteButtons();
		super.destroy();

	}

	public void init() {
		// do nothing in the inherented init method
		// do it later in initButtons
	}

	public void deleteButtons() {
		for (int i = 0; i < Wrap.getComponentCount(); i++) {
			//Component tmp = Wrap.getComponent(i);
			Wrap.remove(i);
		}
		Wrap = null;
//		System.out.println("NetSim Buttons Removed");
	}

	public void initButtons() {

		if (myFrame != null)
			CurrentSimulatorName = nanoApp.getSimulatorName();

	//	System.out.println(myFrame);
	//	System.out.println("Netsim Panel new Simulator nanoNetSimPanelCurrent:" + CurrentSimulatorName);

		if (CurrentSimulatorName == "FrictionSimulatorNT")
			init_friction();

		if (CurrentSimulatorName == "ElectroSimulatorNT")
			init_electro();

		if (CurrentSimulatorName == "SnomSimulatorNT")
			init_snom();

		if (CurrentSimulatorName == "ImagingSimulator")
			init_imaging();

		
		if (CurrentSimulatorName == "SiSimulator")
			init_SiSimulator();
		
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Wrap.repaint();
		repaint();
		firePropertyChange("visible",false,true);
		setVisible(true);
	 

	}

	private void init_imaging() {
		Wrap = new JPanel();
		Wrap.setLayout(new BorderLayout());
		JPanel Knoepfe = new JPanel();
		Knoepfe.setLayout(new FlowLayout());

		JPanel Knoepfe2 = new JPanel();
		Knoepfe2.setLayout(new FlowLayout());

		approach = new JButton("SCAN");
		approach.addActionListener(new approachButtonListener());

		zoom = new JButton("Zoom");
		zoom.addActionListener(new zoomButtonListener());
		zoom.setEnabled(false);

		zoomout = new JButton("Zoom out");
		zoomout.addActionListener(new zoomoutButtonListener());
		zoomout.setEnabled(false);

		JComboBox FileSelect = new JComboBox();
		FileSelect.addItemListener(new FileSelectListener());
		FileSelect.addItem("Cu 111");
		FileSelect.addItem("DMP AG11");
		FileSelect.addItem("Cu nacl");
		FileSelect.addItem("KBR");
		FileSelect.addItem("Si111");
		FileSelect.addItem("Molecule");
		FileSelect.setSelectedIndex(0);

		e = new JTextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		chatField = new JTextField("Incoming Messages !", 30);
		chatField.setEditable(false);

		JButton ExitApplet = new JButton("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());

		Knoepfe.add(approach);
		Knoepfe.add(FileSelect);
		Knoepfe.add(e);
		Knoepfe.add(ExitApplet);

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

	private void init_snom() {
//		System.out.println("init_SNOM");
		Wrap = new JPanel();
		Wrap.setLayout(new BorderLayout());
		JPanel Knoepfe = new JPanel();
		Knoepfe.setLayout(new FlowLayout());
		JPanel Knoepfe2 = new JPanel();
		Knoepfe2.setLayout(new FlowLayout());

		approach = new JButton("Start/Stop");
		approach.addActionListener(new approachButtonListener());

		zoom = new JButton("Zoom");
		zoom.addActionListener(new zoomButtonListener());
		zoom.setEnabled(false);

		zoomout = new JButton("Zoom out");
		zoomout.addActionListener(new zoomoutButtonListener());
		zoomout.setEnabled(false);

		JButton resetexperiment = new JButton("New Dipoles");
		resetexperiment.addActionListener(new resetexperimentButtonListener());

		e = new JTextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		chatField = new JTextField("Incoming Messages !", 30);
		chatField.setEditable(false);

		JButton ExitApplet = new JButton("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());

		Knoepfe.add(approach);
		Knoepfe.add(e);
		Knoepfe.add(resetexperiment);
		Knoepfe.add(ExitApplet);

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

	private void init_electro() {

//		System.out.println("init_electro");
		Wrap = new JPanel();
		Wrap.setLayout(new BorderLayout());
		JPanel Knoepfe = new JPanel();
		Knoepfe.setLayout(new FlowLayout());
		JPanel Knoepfe2 = new JPanel();
		Knoepfe2.setLayout(new FlowLayout());

		//approach = new Button("Start/Stop");
		//approach.addActionListener(new approachButtonListener());

		//zoom = new Button("Zoom");
		//zoom.addActionListener(new zoomButtonListener());
		//zoom.setEnabled(false);

		//zoomout= new Button ("Zoom out");
		//zoomout.addActionListener(new zoomoutButtonListener());
		//zoomout.setEnabled(false);

		JButton resetexperiment = new JButton("New molecules");
		resetexperiment.addActionListener(new resetelectroexperimentButtonListener());

		e = new JTextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		chatField = new JTextField("Incoming Messages !", 30);
		chatField.setEditable(false);

		JButton ExitApplet = new JButton("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());

		//Knoepfe.add(approach);
		Knoepfe.add(e);
		Knoepfe.add(resetexperiment);
		Knoepfe.add(ExitApplet);

		//Knoepfe2.add(zoom);
		//Knoepfe2.add(zoomout);
		//		 Don't show the chatfield, but use the 
		// chatfield is used by dynamic gui

		//Knoepfe.add(chatField);

		//Wrap.add("South", Knoepfe2);
		Wrap.add("Center", Knoepfe);
		Wrap.add("North", DynamicPanel);

		add(Wrap);

	}

	private void init_friction() {
		Wrap = new JPanel();
		Wrap.setLayout(new BorderLayout());
		JPanel Knoepfe = new JPanel();
		Knoepfe.setLayout(new FlowLayout());
		JPanel Knoepfe2 = new JPanel();
		Knoepfe2.setLayout(new FlowLayout());

		approach = new JButton("SCAN");
		approach.addActionListener(new approachButtonListener());

		JComboBox ChannelSelect = new JComboBox();
		ChannelSelect.addItemListener(new ChannelSelectListener());
		ChannelSelect.addItem("1 (Friction forward)");
		ChannelSelect.addItem("2 (Friction backward)");

		JComboBox AlphaSelect = new JComboBox();
		AlphaSelect.addItemListener(new AlphaSelectListener());
		AlphaSelect.addItem("0");
		AlphaSelect.addItem("10");
		AlphaSelect.addItem("20");
		AlphaSelect.addItem("30");
		AlphaSelect.addItem("40");
		AlphaSelect.addItem("45");
		AlphaSelect.addItem("50");
		AlphaSelect.addItem("60");
		AlphaSelect.addItem("70");
		AlphaSelect.addItem("80");
		AlphaSelect.addItem("90");
		AlphaSelect.addItem("105");
		AlphaSelect.addItem("120");
		AlphaSelect.addItem("135");
		AlphaSelect.addItem("150");
		AlphaSelect.addItem("180");
		AlphaSelect.addItem("225");
		AlphaSelect.addItem("270");
		AlphaSelect.addItem("315");
		AlphaSelect.setSelectedIndex(4);
		//AlphaSelect.select(4);

		JComboBox PotentialSelect = new JComboBox();
		PotentialSelect.addItemListener(new PotentialSelectListener());
		PotentialSelect.addItem("0");
		PotentialSelect.addItem("0.1");
		PotentialSelect.addItem("0.2");
		PotentialSelect.addItem("0.3");
		PotentialSelect.addItem("0.4");
		PotentialSelect.addItem("0.5");
		PotentialSelect.addItem("0.6");
		PotentialSelect.addItem("0.7");
		PotentialSelect.addItem("0.8");
		PotentialSelect.addItem("0.9");
		PotentialSelect.addItem("1.0");
		PotentialSelect.addItem("1.5");
		PotentialSelect.addItem("2.0");
		PotentialSelect.setSelectedIndex(6);

		JLabel Alphalabel = new JLabel();
		Alphalabel.setText("Alpha:");

		JLabel Potentiallabel = new JLabel();
		Potentiallabel.setText("Potential (V):");

		e = new JTextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		// chatField = new TextField("Incoming Messages !", 30);
		// chatField.setEditable(false);

		JButton ExitApplet = new JButton("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());

		Knoepfe.add(approach);
		Knoepfe.add(ChannelSelect);
		Knoepfe.add(e);

		Knoepfe2.add(Alphalabel);
		Knoepfe2.add(AlphaSelect);
		Knoepfe2.add(Potentiallabel);
		Knoepfe2.add(PotentialSelect);
		Knoepfe2.add(ExitApplet);

		// Don't show the chatfield, but use the
		// chatfield is used by dynamic gui

		// Knoepfe.add(chatField);

		Wrap.add("South", Knoepfe2);
		Wrap.add("Center", Knoepfe);
		Wrap.add("North", DynamicPanel);

		add(Wrap);

	}
	
	
	private void init_SiSimulator() {
		Wrap = new JPanel();
		Wrap.setLayout(new BorderLayout());
		JPanel Knoepfe = new JPanel();
		Knoepfe.setLayout(new FlowLayout());
		JPanel Knoepfe2 = new JPanel();
		Knoepfe2.setLayout(new FlowLayout());

		

		JComboBox ChannelSelect = new JComboBox();
		ChannelSelect.addItemListener(new ChannelSelectListener());
		ChannelSelect.addItem("1 (Friction forward)");
		ChannelSelect.addItem("2 (Friction backward)");

		

		
		e = new JTextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		// chatField = new TextField("Incoming Messages !", 30);
		// chatField.setEditable(false);

		JButton ExitApplet = new JButton("Exit Simulation");
		ExitApplet.addActionListener(new ExitAppletButtonListener());

		
		Knoepfe.add(ExitApplet);
		Knoepfe.add(e);


		// Don't show the chatfield, but use the
		// chatfield is used by dynamic gui
		// Knoepfe.add(chatField);

		
		Wrap.add("Center", Knoepfe);
		Wrap.add("North", DynamicPanel);

		add(Wrap);

	}
	
	
	
	
	

	// ===================== Command Executors =====================

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
//			System.out.println("EXIT");
//			System.out.println(myApplet);
//			System.out.println(myFrame);
			if (myApplet != null) {

				put("command=stop");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					System.out.println("Could not wait " + e1.getMessage());
				}

				URL url;
				try {
					url = new URL(myApplet.getDocumentBase(), "index.html");
					myApplet.getAppletContext().showDocument(url);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					System.out.println("wrong URL:" + e.getMessage());
				}

			}
			if (myFrame != null) {
//				System.out.println("kill the frame");
				myFrame.stop();
				System.exit(0);
			}
		}
	}

	class PotentialSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			put("command=set name=v1 value=" + e.getItem().toString());
		}
	}

	class AlphaSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			double d = Integer.parseInt(e.getItem().toString()) * Math.PI / 180;
			put("command=setAlpha value=" + d);
		}
	}

	class zoomoutButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {

			FloatControl myzoom = getFloatControl("zoom");
			FloatControl xoffset = getFloatControl("xoffset");
			FloatControl yoffset = getFloatControl("yoffset");

			double maxsize = myzoom.getMaxValue();

			myzoom.FloatEventPerformed("zoom", maxsize);
			xoffset.FloatEventPerformed("xoffset", 0.0);
			yoffset.FloatEventPerformed("yoffset", 0.0);

			put("command=set name=zoom value=" + maxsize);
			put("command=set name=yoffset value=0");
			put("command=set name=xoffset value=0");

		}
	}

	class zoomButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			double PannelSize = 256;
			double oldzoom = 0;
			double oldxoffset = 0;
			double oldyoffset = 0;

			FloatControl myzoom = getFloatControl("zoom");
			oldzoom = myzoom.getValue();
//			System.out.println("zoomvalue:" + oldzoom);
			Point end = getZoomEndPoint();
			Point start = getZoomStartPoint();
			double r = end.getX() - start.getX();
//			System.out.println("r" + r);

			double r2 = end.getY() - start.getY();
//			System.out.println("r2" + r2);

			FloatControl xoffset = getFloatControl("xoffset");

//			System.out.println("xcontroll" + xoffset.getValue());
			//			(shift of the center in pixel)* -1 (direction) 2/PannelSize is the span (origin is in
			// the center
			double xoff = (start.getX() + r / 2 - PannelSize / 2) * (-1)
					/ PannelSize * oldzoom;
//			System.out.println("Xpoint "
//					+ (start.getX() + r / 2 - PannelSize / 2));
//			System.out.println("Xoff " + xoff);

			oldxoffset = xoffset.getValue();
			xoffset.FloatEventPerformed("xoffset", oldxoffset + xoff);

			FloatControl yoffset = getFloatControl("yoffset");
			//(shift of the center in pixel)* -1 (direction) 2/PannelSize is the span (origin is in
			// the center
			double yoff = (PannelSize / 2 - start.getY() - r / 2) * (-1)
					/ PannelSize * oldzoom;
			//System.out.println("Ypointstart: "+start.getY());
	//		System.out.println("Ypoint"
	//				+ (PannelSize / 2 - start.getY() - r / 2));
	//		System.out.println("Yoff " + yoff);

			oldyoffset = yoffset.getValue();
			yoffset.FloatEventPerformed("yoffset", oldyoffset + yoff);

			double newzoom = (end.getX() - start.getX()) / PannelSize * oldzoom;
	//		System.out.println("zoom" + oldzoom);

			myzoom.FloatEventPerformed("zoom", newzoom);
	//		System.out.println("d" + (end.getX() - start.getX()));

			put("command=set name=yoffset value=" + (oldyoffset + yoff));
			put("command=set name=xoffset value=" + (oldxoffset + xoff));
			put("command=set name=zoom value=" + newzoom);

			//System.out.println("approach");

	//		System.out.println("ZoomStart: " + getZoomStartPoint());
	//		System.out.println("ZoomEnd: " + getZoomEndPoint());
	//		System.out.println("ZoomValue: " + newzoom);

		}
	}

	class resetexperimentButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
//			System.out.println("resetexperiment");
			put("command=resetexperiment");
		}
	}
	
	
	class resetelectroexperimentButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ee) {
 //           System.out.println("resetexperiment");
            put("command=approach");
        }
    }
	
	

	class FileSelectListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {

			if ((e.getItem()).equals("DMP AG11")) {
				put("command=loaddata file=dmp_ag111.jpg");
			} else {
				if ((e.getItem()).equals("Cu 111")) {
					put("command=loaddata file=cuafm138.jpg");
				} else {
					if ((e.getItem()).equals("Cu nacl")) {
						put("command=loaddata file=cuna1612s.jpg");
					} else {
						if ((e.getItem()).equals("KBR")) {
							put("command=loaddata file=kbr.jpg");
						} else {
							if ((e.getItem()).equals("Si111")) {
								put("command=loaddata file=si.jpg");
							} else {
								if ((e.getItem()).equals("Molecule")) {
									put("command=loaddata file=phorphyrin.jpg");
								} else {
									put("command=loaddata file=kbr.jpg");
								}
							}
						}
					}
				}
			}
           
		}
	}

	// ====================== div. Methoden ==========================
}