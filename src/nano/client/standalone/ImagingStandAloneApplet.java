/*
 * Copyright (c) 2002 by Tibor Gyalog, Raoul Schneider, Dino Keller,
 * Christian Wattinger, Martin Guggisberg and The Regents of the University of
 * Basel. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF BASEL BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * BASEL HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE UNIVERSITY OF BASEL SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF BASEL HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * Authors: Tibor Gyalog, Raoul Schneider, Dino Keller,
 * Christian Wattinger, Martin Guggisberg <vexp@nano-world.net>
 *
 *
 */

package nano.client.standalone;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.util.Properties;
import java.util.StringTokenizer;

import nano.client.ImagePanel;
import nano.client.LineSection;
import nano.client.NetObserver;
import nano.compute.EventReceiver;
import nano.compute.ParseException;
import nano.compute.Simulator;
import nano.net.SourcePipe;

/**
 *
 * @author Tibor Gyalog
 * @version 1.1 16.08.01 (1.1: Panels neu angeordnet(seitwärts, war untereinander))
 */

public class ImagingStandAloneApplet extends Applet implements EventReceiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NetObserver Myimage;

	LineSection MyLiner;

	public Simulator MySimulator;

	ImagingNetSimPanel MyNetSimPanel;

	SourcePipe MyData;

	ImagePanel MyImagePanel;

	static Properties getProperties(String CommandLine) {
		String tag_name, tag_value;
		StringTokenizer name;
		//System.out.println(CommandLine);
		Properties MyProperties = new Properties();
		StringTokenizer st = new StringTokenizer(CommandLine);
		while (st.hasMoreTokens()) {
			name = new StringTokenizer(st.nextToken(), "=");
			tag_name = name.nextToken();
			tag_value = name.nextToken();
			MyProperties.setProperty(tag_name, tag_value);
		}
		return MyProperties;
	}

	public void init() {
		//Darstellung

		//URL MyURL = getCodeBase();
		//System.out.println(MyURL)
		//Startsimulator

	}

	public void start() {

		String SimulatorName = "ImagingSimulator";
		loadSource(SimulatorName);
		try {
			MySimulator.ParseCommand("command=getgui");
		} catch (ParseException e) {
			System.out.println("command getgui parse exception"
					+ e.getMessage());
		}
		try {
			MySimulator.ParseCommand("command=start");
		} catch (ParseException e) {
			System.out
					.println("command start parse exception" + e.getMessage());
		}

		//removeAll();   
		//setSize(800, 600);
		MyNetSimPanel = new ImagingNetSimPanel(MySimulator,this);
		Myimage = new NetObserver(MyNetSimPanel);
		setLayout(new BorderLayout());
		MyLiner = new LineSection();
		MyData = new SourcePipe(MySimulator.getSource());
		MyData.PlugAnzeige(Myimage);
		MyData.PlugAnzeige(MyLiner);
		//ImagingNetSimPanel
		
		
		MyImagePanel = new ImagePanel(Myimage);
		Panel myPanel = new Panel();
		myPanel.setLayout(new FlowLayout());
		myPanel.add(Myimage);
		myPanel.add(MyLiner);

		add("Center", myPanel);
		add("South", MyNetSimPanel);
		add("North", MyImagePanel);
		//setVisible(true);
		//pack();
		//System.out.println("4");

		MySimulator.setController(this);
		Properties[] MyProperties = new Properties[20];
		int i = 0;
		String META_Line;
		while ((META_Line = getParameter("gui" + i)) != null) {
			MyProperties[i] = getProperties(META_Line);
			//System.out.println(MyProperties[i]);
			i++;
		}
		MySimulator.loadMetaData(MyProperties, i);
		i = 0;
		while ((META_Line = getParameter("start" + i)) != null) {
			try {
				MySimulator.ParseCommand(META_Line);
			} catch (ParseException e) {
			}
			i++;
		}

		MyNetSimPanel.put("command=getgui");
		//System.out.println("getgui");

	}

	public void stop() {
		MyData.stopthread();
		
		try {
			MySimulator.ParseCommand("command=stop");
		} catch (ParseException e) {
			System.out.println("command start parse exception" + e.getMessage());
		}
		
		
		this.MyData = null;
		//ystem.out.println("S4");
		MyImagePanel.removeAll();
		//System.out.println("S5");
		this.MyImagePanel = null;
		//System.out.println("S6");
		MyLiner.removeNotify();
		//System.out.println("S7");
		this.MyLiner = null;
		//System.out.println("S8");
		Myimage.removeNotify();
		//System.out.println("S9");
		this.Myimage = null;

		//System.out.println("D2");
		this.MyNetSimPanel.destroy();
		MySimulator = null;

	}

	public void destroy() {

	}

	public void ReceiveEvent(String NewEvent) {
		//System.out.println("ReceiveEvent:" + NewEvent);
		//Stand_alone !!!
		MyNetSimPanel.performSocketEvent(NewEvent);
		
	}

	public void loadSource(String SourceName) {
		try {
			Class SimulatorType = Class.forName("nano.compute.simulation."
					+ SourceName);
			Object GeneralSimulator = SimulatorType.newInstance();
			MySimulator = (Simulator) GeneralSimulator;
		}
		//catch(IOException e){e.printStackTrace();}
		catch (ClassNotFoundException e) {
			System.out.println("nano.compute.simulation." + SourceName
					+ " not found.");
			e.printStackTrace();
			System.exit(0);
		} catch (ClassCastException e) {
			System.out.println("nano.compute.simulation." + SourceName
					+ " ist nicht von nano.compute.Simulator vererbt.");
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			System.out.println("nano.compute.simulation." + SourceName
					+ " ist abstrakt oder Interface.");
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			System.out
					.println("Zugriff auf Simulatorklasse nano.compute.simulation."
							+ SourceName + " verweigert.");
			e.printStackTrace();
			System.exit(0);
		}
	}

}