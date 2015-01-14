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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;

import nano.client.NetSimPanel;
import nano.compute.Simulator;

class FrictionNetSimPanel extends NetSimPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Applet myApplet = null;

	FrictionStandAloneApp myFrame = null;

	/**
	 * @param MyNewSimulator
	 */
	public FrictionNetSimPanel(Simulator MyNewSimulator,
			FrictionStandAloneApplet myApplet) {
		super(MyNewSimulator);
		this.myApplet = myApplet;
	}

	public FrictionNetSimPanel(Simulator mySimulator, FrictionStandAloneApp app) {
		super(mySimulator);
		this.myFrame = app;
	}

	public void init() {
		Panel Wrap = new Panel();
		Wrap.setLayout(new BorderLayout());
		Panel Knoepfe = new Panel();
		Knoepfe.setLayout(new FlowLayout());
		Panel Knoepfe2 = new Panel();
		Knoepfe2.setLayout(new FlowLayout());

		approach = new Button("SCAN");
		approach.addActionListener(new approachButtonListener());

		Choice ChannelSelect = new Choice();
		ChannelSelect.addItemListener(new ChannelSelectListener());
		ChannelSelect.addItem("1 (Friction forward)");
		ChannelSelect.addItem("2 (Friction backward)");
		ChannelSelect.select(0);

		Choice AlphaSelect = new Choice();
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
		AlphaSelect.select(4);

		Choice PotentialSelect = new Choice();
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
		PotentialSelect.select(6);

		Label Alphalabel = new Label();
		Alphalabel.setText("Alpha:");

		Label Potentiallabel = new Label();
		Potentiallabel.setText("Potential (V):");

		e = new TextField("Your Command ..", 30);
		e.addActionListener(new chat_listener());
		// chatField = new TextField("Incoming Messages !", 30);
		// chatField.setEditable(false);

		Button ExitApplet = new Button("Exit Simulation");
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
		//	System.out.println("EXIT");
		//	System.out.println(myApplet);
		//	System.out.println(myFrame);
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
				System.out.println("kill the frame");
				myFrame.stop();
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

	// ====================== div. Methoden ==========================
}