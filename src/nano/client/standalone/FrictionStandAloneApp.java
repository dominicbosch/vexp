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
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;

import nano.client.Client;
import nano.client.ImagePanel;
import nano.client.LineSection;
import nano.client.NetObserver;
import nano.client.Client.ConnectionDetailsListener;
import nano.client.Client.ExitListener;
import nano.client.Client.MainMenu;
import nano.compute.EventReceiver;
import nano.compute.ParseException;
import nano.compute.Simulator;
import nano.net.SourcePipe;

/**
 *
 * @author Tibor Gyalog, M. Guggisberg
 */

public class FrictionStandAloneApp extends Frame implements EventReceiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static FrictionStandAloneApp myapp;

	NetObserver Myimage;

	LineSection MyLiner;

	public Simulator MySimulator;

	
	//Todo change Use your individual NetSimPanel
	FrictionNetSimPanel MyNetSimPanel;

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


	}

	public void start() {
        //todo use your specific Simulator
		String SimulatorName = "FrictionSimulatorNT";
		loadSource(SimulatorName);
				
		MyNetSimPanel = new FrictionNetSimPanel(MySimulator,this);
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
		
		MySimulator.setController(this);
		// Load Metadata for adaptive GUI from conf folder
		MySimulator.getMetaData("conf");
	
		setMenuBar(new MainMenu());
        setSize(400, 600);
       
        setVisible(true);
        pack();
		

		MyNetSimPanel.put("command=getgui");
		
		try {
			MySimulator.ParseCommand("command=start");
		} catch (ParseException e) {
			System.out.println("command start parse exception" + e.getMessage());
		}
		//System.out.println("getgui");

	}

	public void stop() {
		MyData.stopthread();
		try {
			MySimulator.ParseCommand("command=stop");
		} catch (ParseException e) {
			System.out.println("command stop parse exception" + e.getMessage());
		}
		
		this.MyData = null;
		MyImagePanel.removeAll();
		this.MyImagePanel = null;
		MyLiner.removeNotify();
		this.MyLiner = null;
		Myimage.removeNotify();
		this.Myimage = null;
		this.MyNetSimPanel.destroy();
		MySimulator = null;
        
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 System.exit(0);
	}

	public void destroy() {

	}
	
	
	  public FrictionStandAloneApp(){
	      super();  
	        }

	
	
	 public static void main(String args[]){
		    
		      myapp =  new FrictionStandAloneApp();
		      myapp.init();
		      myapp.start();

		    }

	 
	 
	  public class ExitListener implements
      ActionListener{
          public void actionPerformed(ActionEvent e){
              
        	  stop();
        	  
        	 
          }
      }


      public class MainMenu extends MenuBar{
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		public MainMenu(){
		  Menu m=new Menu("Connection");
          MenuItem Exmi=new MenuItem("Exit");
          Exmi.addActionListener(new ExitListener());
          m.add(Exmi);
          add(m);
          }
      }



	 
	 
	 
	public void ReceiveEvent(String NewEvent) {
	//	System.out.println("ReceiveEvent:" + NewEvent);
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