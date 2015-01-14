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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import nano.client.TopNavigationPanel;
import nano.client.JLineSection;
import nano.client.JNetObserver;
import nano.compute.EventReceiver;
import nano.compute.ParseException;
import nano.compute.Simulator;
import nano.net.SourcePipe;

/**
 *
 * @author Tibor Gyalog, M. Guggisberg
 */

public class nanoApp extends JFrame implements EventReceiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static nanoApp myapp;
	JNetObserver MySourceData;
	JLineSection MyLiner;

	public Simulator MySimulator;

	//Todo change Use your individual NetSimPanel
	nanoNetSimPanel MyNetSimPanel;

	SourcePipe MyData;

	

	private static String CurrentSimulatorName;

	//JImagePanel MyImagePanel;

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
		CurrentSimulatorName = "FrictionSimulatorNT";
	}

	public void start() {
		//todo use your specific Simulator
		
		
		loadSource(CurrentSimulatorName);
		
		MyData = new SourcePipe(MySimulator.getSource());
		MyNetSimPanel = new nanoNetSimPanel(MySimulator, this);
		MySourceData = new JNetObserver(MyNetSimPanel);
		MyLiner = new JLineSection();
		
		MyData.PlugAnzeige(MySourceData);
		MyData.PlugAnzeige(MyLiner);
		//ImagingNetSimPanel

		TopNavigationPanel TopControl = new TopNavigationPanel(MySourceData);
		JPanel MainPanel = new JPanel();
		MainPanel.setMinimumSize(new Dimension(260, 260));
		MainPanel.setLayout(new FlowLayout());
		MainPanel.add(MySourceData);
		MyLiner.setLayout(new FlowLayout());
		MainPanel.add(MyLiner);

		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(MainPanel, BorderLayout.CENTER);
		getContentPane().add(MyNetSimPanel, BorderLayout.SOUTH);
		getContentPane().add(TopControl, BorderLayout.NORTH);

		MySimulator.setController(this);
		// Load Metadata for adaptive GUI from conf folder
		MySimulator.getMetaData("conf");

		setJMenuBar(createMenuBar());

		
		
		myapp.setVisible(true);	
		myapp.pack();
		
		/*
		// add GUI Mist
		if(CurrentSimulatorName == "ImagingSimulator"){
			

		MySimulator.MyBoss.ReceiveEvent("command=addcontrol label=zoom value="
		 + 640 + " type=double Name=Size(nm) max="
		 + 640 + " min=1 guitype=VollKreis");

		 
		 MySimulator.MyBoss
		 .ReceiveEvent("command=addcontrol label=xoffset value=0 type=double Name=X(nm) max="
		 + 100
		 + " min="
		 + -100 + " guitype=VollKreis");
		 
		 MySimulator.MyBoss
		 .ReceiveEvent("command=addcontrol label=yoffset value=0 type=double Name=Y(nm) max="
		 + 100
		 + " min="
		 + -100 + " guitype=VollKreis");

		
		 
		}
        */
		

		try {
			MySimulator.ParseCommand("command=start");
		} catch (ParseException e) {
			System.out
					.println("command start parse exception" + e.getMessage());
		}
	//    System.out.println("getgui");	
		MyNetSimPanel.put("command=getgui");

	}

	public void stop() {
		/*
		for (int i = 0; i < TopControl.getComponentCount(); i++) {
			//Component tmp = Wrap.getComponent(i);
		    TopControl.remove(i);
		}
		TopControl=null;
		
		
		for (int i = 0; i < MainPanel.getComponentCount(); i++) {
			//Component tmp = Wrap.getComponent(i);
		    MainPanel.remove(i);
		}
		MainPanel=null;
		
		//removeAll();
		*/
		
		MyData.stopthread();
		
		JRootPane jr = getRootPane();
		JLayeredPane lp1 = (JLayeredPane)jr.getComponent(1);
		
		
		
		
		JPanel lp2 = (JPanel)lp1.getComponent(0);
		lp2.removeAll();
		
		/*
		//Panel lp3 =(Panel)lp2.getComponent(0);
		for (int i = 0; i < lp2.getComponentCount(); i++) {
			//Component tmp = Wrap.getComponent(i);
		    lp2.remove(i);
		}*/
		
		MyNetSimPanel.destroy();
		
		
		MySourceData.removeAll();
		
		try {
			MySimulator.ParseCommand("command=stop");
		} catch (ParseException e) {
			System.out.println("command stop parse exception" + e.getMessage());
		}

		this.MyData = null;
		MyLiner.removeNotify();
		this.MyLiner = null;
		MySourceData.removeNotify();
		this.MySourceData = null;
		
		
		
	
		MySimulator = null;
		if (MyNetSimPanel !=null)
			MyNetSimPanel = null;
		
		
		
		/*
		JRootPane myroot = (JRootPane)myapp.getComponent(0);
		JPanel jp1 =(JPanel)myroot.getComponent(0);
		JLayeredPane jl1 = (JLayeredPane)myroot.getComponent(1);
		
		JPanel mainPanel =(JPanel)jl1.getComponent(0);
		
		
		for (int i = 0; i < mainPanel.getComponentCount(); i++) {
			Component tmp = mainPanel.getComponent(i);
			
			System.out.println("nop");
		    remove(i);
		}
		*/
		/*
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}

	public void destroy() {

	}

	public void loadSource(String SourceName) {
		try {
			Class SimulatorType = Class.forName("nano.compute.simulation."
					+ SourceName);
			Object GeneralSimulator = SimulatorType.newInstance();
			if (MySimulator !=null){
				try {
					MySimulator.ParseCommand("command=stop");
//					System.out.println("Stop Simulator");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					System.out.println("Error Stop Simulator"+e.getMessage());
				}
				MySimulator=null;
			}
			
			
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

	public void ReceiveEvent(String NewEvent) {
		//System.out.println("ReceiveEvent:" + NewEvent);
		//Stand_alone !!!
		MyNetSimPanel.performSocketEvent(NewEvent);

	}

	public nanoApp() {
		super("nano Simulators");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(800, 600);
		contains(800, 600);
	}

	public static void main(String args[]) {

		myapp = new nanoApp();
		myapp.init();
		myapp.start();

	}

	public class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			stop();

			System.exit(0);

		}
	}

	public class ChangeSimListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String sim = e.getActionCommand();
			if (sim == "Friction")
				switchSimulator("FrictionSimulatorNT");
			if (sim == "ElecotroChemistry")
				switchSimulator("ElectroSimulatorNT");
			if (sim == "Fluorescent Dipoles")
				switchSimulator("SnomSimulatorNT");
			if (sim == "Imaging")
				switchSimulator("ImagingSimulator");
			if (sim == "Si(111) 7x7")
				switchSimulator("SiSimulator");
			/*
			 * For new Simulators do not forget to install the initbutton 
			 * Method in the nanonetsimpanel object 
			 */
			
//			System.out.println("sim:" + e.getActionCommand());

		}
	}

	public JMenuBar createMenuBar() {

		//			Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		JMenu m = new JMenu("Main");

		JMenuItem Exmi = new JMenuItem("Exit");
		Exmi.addActionListener(new ExitListener());
		m.add(Exmi);

		JMenu sim = new JMenu("Simulators");
		JMenuItem fri = new JMenuItem("Friction");
		fri.addActionListener(new ChangeSimListener());
		sim.add(fri);

		JMenuItem elchem = new JMenuItem("ElecotroChemistry");
		elchem.addActionListener(new ChangeSimListener());
		sim.add(elchem);

		JMenuItem di = new JMenuItem("Fluorescent Dipoles");
		di.addActionListener(new ChangeSimListener());
		sim.add(di);

		JMenuItem img = new JMenuItem("Imaging");
		img.addActionListener(new ChangeSimListener());
		sim.add(img);
		
		JMenuItem si = new JMenuItem("Si(111) 7x7");
		si.addActionListener(new ChangeSimListener());
		sim.add(si);

		menuBar.add(m);
		menuBar.add(sim);

		return menuBar;
	}

	public void switchSimulator(String newSimulatorname) {

		if (newSimulatorname != CurrentSimulatorName) {
			stop();
			CurrentSimulatorName = newSimulatorname;
			
			
			start();
			repaint();
			pack();
		}

	}
	public static String getSimulatorName(){
		return CurrentSimulatorName;
	}

}