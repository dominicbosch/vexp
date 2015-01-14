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
package nano.client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import nano.awt.FloatControl;
import nano.awt.FloatListener;
import nano.awt.FloatVollKreis;
import nano.compute.CommandExecutor;
import nano.compute.ParseException;
import nano.compute.Parser;
import nano.compute.Simulator;
import nano.net.Comm;
import nano.net.ESStateListener;
import nano.net.EventClientSocket;
import nano.net.EventSocket;
import nano.net.EventSocketException;
import nano.net.EventSocketListener;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * builds the panel below the "image window", with all the controllers and
 * listeners
 * 
 * @author Raoul Schneider
 * @version 1.2.1 10.04.2002 command=newclient implemented 16.08.2001 (1.0.1:
 *          documentated; 1.1: setCommandExecutor "if"-abfragen rausgenommen;
 *          1.2: Buttons "approach" und "withdraw" implementiert)
 */
public class NetSimPanel extends Panel implements FloatListener,
		EventSocketListener, ESStateListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	EventClientSocket myClientSocket = null;

	private Simulator MySimulator = null;

	private String ServerIPAddress = "localhost";

	private int PortNumber;

	// private double newValue;

	private boolean IsLocal = false;

	private boolean isruning = true;

	// private FloatControl xyz, b, g;

	// private FloatControl a, c, d, f;

	// private FloatListener MyListener;

	appletIdWriter myappletIdWriter;

	Comm MyComm;

	Parser MyParser;

	protected TextField e;

	protected TextField chatField;

	protected Button approach;

	Button withdraw;

	protected Button zoomout;

	protected Button zoom;

	// private Panel mitText;

	// private Choice ChannelSelect;

	protected DPanel DynamicPanel = new DPanel(600, 120, 1, 2);

	final int MAX_FLOAT_Control = 10;

	private FloatControl[] MyFloatControls = new FloatControl[MAX_FLOAT_Control];

	private int numFloatControls = 0;

	private Panel MyPanel;

	private String MyText;

	private Point ZoomStartPoint = new Point(0, 0), ZoomEndPoint = new Point(
			40, 40);

	/**
	 * constructor. builds the panel with the controllers s *
	 * 
	 * @param IP
	 *            Server-IP-Adress
	 * @param PortNumber
	 *            PortNumber of the Server
	 */
	public NetSimPanel(Simulator MyNewSimulator) {
		MySimulator = MyNewSimulator;
		IsLocal = true;
		MyComm = new Comm();
		initParser();
		init();
	}

	public NetSimPanel(String IP, int NewPortNumber) {
		ServerIPAddress = IP;
		PortNumber = NewPortNumber;
		myClientSocket = new EventClientSocket(IP, PortNumber);
		myClientSocket.addESStateListener(this);
		myClientSocket.addEventSocketListener(this);
		MyComm = new Comm();
		initParser();
		init();
	}

	public NetSimPanel(String IP, int NewPortNumber, int AppletID) {
		ServerIPAddress = IP;
		PortNumber = NewPortNumber;
		// System.out.println("hier");
		myClientSocket = new EventClientSocket(IP, PortNumber, AppletID);
		// System.out.println("hier mist");
		myClientSocket.addESStateListener(this);
		myClientSocket.addEventSocketListener(this);
		// System.out.println("hier 1");
		MyComm = new Comm();
		// System.out.println("hier 2");
		initParser();
		// System.out.println("hier 3");
		init();
		// System.out.println("hier 4");
	}

	public void init() {
		// System.out.println("2.3.1");
				// AWT
		
			Panel Wrap = new Panel();
			Wrap.setLayout(new BorderLayout());
			Panel Knoepfe = new Panel();
			// System.out.println("2.3.2");
			Knoepfe.setLayout(new FlowLayout());
			approach = new Button("Approach");
			// zoom = new Button("Zoom")
			// withdraw = new Button("Withdraw");
			Choice ChannelSelect = new Choice();
			ChannelSelect.addItemListener(new ChannelSelectListener());
			// ChannelSelect.addItem("Cu 111");
			// ChannelSelect.addItem("DMP AG11");
			// ChannelSelect.addItem("Cu nacl");
			// ChannelSelect.addItem("KBR");
			// ChannelSelect.addItem("Si111");
			// ChannelSelect.addItem("Molecule");
			// ChannelSelect.addItem("2 (not used)");

			ChannelSelect.addItem("1 (Friction forward)");
			// ChannelSelect.addItem("4 (not used)");
			ChannelSelect.addItem("2 (Friction backward)");

			ChannelSelect.select(0);

			Knoepfe.add(ChannelSelect);
			Knoepfe.add(approach);
			// Knoepfe.add(zoom);
			// Knoepfe.add(withdraw);

			approach.addActionListener(new approachButtonListener());
			// withdraw.addActionListener(new withdrawButtonListener());
			// chat-fenster

			Panel Text = new Panel();
			Text.setLayout(new FlowLayout());
			e = new TextField("Your Command ..", 30);
			chatField = new TextField("Incoming Messages !", 30);
			chatField.setEditable(false);
			e.addActionListener(new chat_listener());

			Text.add(e);
			// chatfield is used by dynamic gui
			Text.add(chatField);
			Wrap.add("South", Text);
			Wrap.add("Center", Knoepfe);
			Wrap.add("North", DynamicPanel);

			add(Wrap);
		
	}

	/**
	 * initialises the parser. creates a new hashtable
	 */
	public void initParser() {
		//System.out.println("initparser");
		Hashtable Befehle = new Hashtable();
		Befehle.put("set", new setCommandExecutor());
		Befehle.put("chat", new chatCommandExecutor());
		Befehle.put("status", new statusCommandExecutor());
		Befehle.put("addcontrol", new addcontrolCommandExecutor());
		Befehle.put("setAlpha", new setAlphaCommandExecutor());

		// Befehle.put("setStartPoint", new setStartPointCommandExecutor());
		// Befehle.put("setEndPoint", new setEndPointCommandExecutor());

		/*
		 * Befehle.put("request_getgui", new request_getguiCommandExecutor());
		 * Befehle.put("release_gui", new release_guiCommandExecutor());
		 */

		MyParser = new Parser(Befehle);
	}

	public void put(String message) {
		if (IsLocal) {
			try {
				MySimulator.ParseCommand(message);
				//System.out.println("put local message:" + message);
			} catch (ParseException e) {
			}
		} else {
			try {
				myClientSocket.put(message);
				//System.out.println("put client message:" + message);
			} catch (Exception exc) {
				//System.out.println("here is a problem" + exc.getMessage());
				exc.printStackTrace();
			}
		}
	}

	public void submitAppletid(int newid) {
		myappletIdWriter = new appletIdWriter(newid);
	}

	public void releaseFloatControl() {
		// System.out.println("Relase Float Controls");
		for (int i = 0; i < numFloatControls; i++) {
			if (MyFloatControls[i] != null) {
				// System.out.println("Kill Float NR "+i);
				DynamicPanel.removeComponent(MyFloatControls[i].getLabel());
				MyComm.releaseFloatListener(MyFloatControls[i]);
				MyFloatControls[i].stop_thread();
			}
		}
	}
	
	
	public FloatControl getFloatControl(String Label) {
		for (int i = 0; i < numFloatControls; i++) {
			if (MyFloatControls[i].getLabel().equals(Label)) {
				return MyFloatControls[i];
			}
		}
		return null;
	}

	public void addFloatControl(String Label, double initValue,
			double minValue, double maxValue, String preferredType,
			Properties Representation) {
		// TODO Implement preferredType
		boolean directQuit = false;
		//System.out.println("Adding FloatControl " + Label + " and set it to"
		//		+ initValue);
		// System.out.println("PreferredType="+PreferredType);
		for (int i = 0; i < numFloatControls; i++) {
			if (MyFloatControls[i].getLabel().equals(Label)) {
				MyFloatControls[i].stop_thread();
				MyComm.releaseFloatListener(MyFloatControls[i]);
				DynamicPanel.removeComponent(MyFloatControls[i].getLabel());
				// directQuit = true;
				// remove this float controll
				//	System.out.println("Delete FloatControl" + i);
				MyFloatControls[i] = MyFloatControls[numFloatControls - 1];
				//System.out.println(MyFloatControls[i].getLabel());
				
				MyFloatControls[numFloatControls - 1] = null;
				numFloatControls--;
			}
		}
		
		// System.out.println("DirectQuit="+directQuit);
		if (!directQuit) {
			// TODO RUNTIME use of other float controlls
			// MyFloatControls[numFloatControls] =
			// loadFloatControl(PreferredType,
			// Representation);
			// System.out.println("Representation:"+Representation.toString());
			if (MyFloatControls[numFloatControls] == null) {
				//System.out.println("PreferredType not found");
				Properties DefaultRepresentation = new Properties();
				DefaultRepresentation.setProperty("Width", "90");
				DefaultRepresentation.setProperty("Height", "90");
				DefaultRepresentation.setProperty("Steigung", "10");
				DefaultRepresentation.setProperty("BackgroundColor", "#F0F0F0");
				MyFloatControls[numFloatControls] = new FloatVollKreis(Label,
						0, 100, initValue, Representation);
			}
			MyFloatControls[numFloatControls].addFloatListener(this);
			MyComm.addFloatListener(MyFloatControls[numFloatControls]);
			MyPanel = new Panel();
			MyPanel.setLayout(new BorderLayout());
			MyPanel.add("Center", MyFloatControls[numFloatControls]);
			MyText = Representation.getProperty("Name", Label);
			Panel p1 = new Panel();
			p1.setLayout(new FlowLayout());
			p1.add(new Label(MyText));
			MyPanel.add("South", p1);
			DynamicPanel.addComponent(MyPanel);
			MyFloatControls[numFloatControls].setLabel(Label);
			MyFloatControls[numFloatControls].setMinValue((int) minValue);
			MyFloatControls[numFloatControls].setMaxValue((int) maxValue);
			MyFloatControls[numFloatControls].setValue(initValue);
			numFloatControls++;
		}
	}

	public FloatControl loadFloatControl(String ControlType,
			Properties Representation) {
		// TODO support other controls !!!
		// To Do different Float controlls do not work in this version
		// class could not load at run time, error InstantiationException
		// at the moment are only vollkreis controll supported
		FloatControl MyFloatControl = null;
		try {
			Class FloatControlType = Class.forName("nano.awt.Float"
					+ ControlType);
			//System.out.println(ControlType);
			Object GeneralFloatControl = FloatControlType.newInstance();
			MyFloatControl = (FloatControl) GeneralFloatControl;
			MyFloatControl.setRepresentation(Representation);
		} catch (ClassNotFoundException e) {
			System.out.println("nano.awt.Float" + ControlType + " not found.");
		} catch (ClassCastException e) {
			System.out.println("nano.awt.Float" + ControlType
					+ " ist nicht von nano.compute.Simulator vererbt.");
		} catch (InstantiationException e) {
			System.out.println("nano.awt.Float" + ControlType
					+ " ist abstrakt oder Interface.");
		} catch (IllegalAccessException e) {
			System.out.println("Zugriff auf Simulatorklasse nano.awt.Float"
					+ ControlType + " verweigert.");
		}
		return MyFloatControl;
	}

	public void newEventSocket(EventSocket NewSocket) {
	}

	public void ESStateChanged(boolean ok, String State, int ID, int RemoteID) {
		// System.out.println("New State: "+State);
	}

	/**
	 * is called if a control has been changed. changes the values to the new
	 * ones
	 * 
	 * @param label
	 *            label of Float-*-Kreis which has changed its value
	 * @param zahl
	 *            new value of the changed label
	 */
	public void FloatEventPerformed(String label, double zahl) {
		if (!Double.isNaN(zahl)) {
			if (label == "setAlpha") {
				put("command=setAlpha value=" + zahl * 3.14159265 / 180);
			} else
				// System.out.println("command=set name=" + label + " value=" +
				// zahl);
				put("command=set name=" + label + " value=" + zahl);
		}
	}

	/**
	 * is called if a input has been made from the textfield. will be sent to
	 * the parser
	 */
	public void performSocketEvent(String message) {
		//System.out.println("Socket Event: " + message);
		if (chatField != null)
			chatField.setText(message);
		try {
			//System.out.println("NetSIM Message:" + message);
			MyParser.parse(message);
		} catch (Exception exc) {
			System.out.println("Error: in SocketEventPerformed:"
					+ exc.getMessage());
		}
	}

	public void setServerIPAddress(String MyAddress) {
		ServerIPAddress = MyAddress;
	}

	public double todouble(String s) {
		Double dummy = new Double(s);
		return dummy.doubleValue();
	}

	// ====================== div. Methoden ==========================
	public double getDouble(String wert) {
		Double dummy;
		try {
			dummy = Double.valueOf(wert);
		} catch (Exception e) {
			dummy = new Double(0);
		}
		return dummy.doubleValue();
	}

	public boolean isruning() {
		return isruning;
	}

	public void setruning(boolean runing) {
		this.isruning = runing;
	}

	public Point getZoomStartPoint() {
		return this.ZoomStartPoint;
	}

	public void setZoomStartPoint(Point start) {
		this.ZoomStartPoint = start;
	}

	public Point getZoomEndPoint() {
		return this.ZoomEndPoint;
	}

	public void setZoomEndPoint(Point end) {
		this.ZoomEndPoint = end;
	}

	public void destroy() {
		releaseFloatControl();
		if (myClientSocket != null) {
			myClientSocket.destroy();
		}
	} // ====================== Classes ================================

	public String getServerIPAddress() {
		return ServerIPAddress;
	}

	class setCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			String parameter = (String) tags.get("name");
			String value = (String) tags.get("value");
			//System.out.println("name:" + parameter + " value:" + value);
			if (MyComm != null) {
				MyComm.FloatEventPerformed(parameter, (int) Math
						.round(getDouble(value)));
			}
		}
	}

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

	class addcontrolCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {

			String Max = "0", Min = "0";

			String Label = (String) tags.get("label");
		//	System.out.println("addcontrol" + Label);
			tags.remove("label");
			String Value = (String) tags.get("value");
			tags.remove("value");
			String type = (String) tags.get("type");
			tags.remove("type");
			String PreferredType = (String) tags.get("guitype");
			tags.remove("guitype");
			if (type.equals("double")) {
				Max = (String) tags.get("max");
				tags.remove("max");
				Min = (String) tags.get("min");
				tags.remove("min");
			}

			Properties Representation = new Properties();
			Enumeration List = tags.keys();
			String key;
			while (List.hasMoreElements()) {
				key = (String) List.nextElement();
				Representation.setProperty(key, (String) tags.get(key));
			}
			double max;
			double min;
			try {
				min = Double.parseDouble(Min);
				max = Double.parseDouble(Max);
				int diff = (int) (max - min);
				if (diff < 7) {
					Representation.setProperty("Steigung", "1");
				//	System.out.println("addFloatControll:Steigung 1,diff="
				//			+ diff);
				} else {
					if (diff < 70) {
						Representation.setProperty("Steigung", "10");
					//	System.out.println("addFloatControll:Steigung 10,diff="
					//			+ diff);
					} else {
						if (diff < 700) {
						Representation.setProperty("Steigung", "100");
						//	System.out
						//			.println("addFloatControll:Steigung 100,diff="
						//					+ diff);
						} else {
							Representation.setProperty("Steigung", "1000");
						//	System.out
						//			.println("addFloatControll:Steigung 1000,diff="
						//					+ diff);
						}
					}
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				System.out.println("addFloatControll:error:" + e.getMessage());
				e.printStackTrace();
			}

			if (type.equals("double")) {
				double maxValue = Double.parseDouble(Max);
				double minValue = Double.parseDouble(Min);
				double value = Double.parseDouble(Value);
				//System.out.println("addFloatControll:" + Label + "maxvalue:"
				//		+ maxValue);
				addFloatControl(Label, value, minValue, maxValue,
						PreferredType, Representation);
			}
		}
	}

	class chatCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
		}
	}

	class statusCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			String parameter = (String) tags.get("name");
			String type = (String) tags.get("type");
			if (type.equals("double")) {
				double value = getDouble((String) tags.get("value"));
				MyComm.FloatEventPerformed(parameter, value);
			}
		}
	}

	class appletIdWriter implements Runnable {
		int myid;

		private Thread appletWriterThread;

		public appletIdWriter(int newid) {
			myid = newid;
			appletWriterThread = new Thread(this, "AppletWriterThread");
			appletWriterThread.start();

		}

		public void run() {
			boolean ok = false;
			int count = 0;
			Thread thisThread = Thread.currentThread();

			while ((!ok && count < 10) && (appletWriterThread == thisThread)) {
				count++;
				try {
					myClientSocket.put("command=newclient appletid=" + myid);
					ok = true;
				} catch (EventSocketException e) {
					e.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException exp) {
						exp.printStackTrace();
					}
				}
			}
		}
	}

	class setAlphaCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			String value = (String) tags.get("value");
			if (MyComm != null) {
				MyComm.FloatEventPerformed("setAlpha", (int) Math
						.round(getDouble(value) * 180 / 3.14159265));
			}
		}
	}

	public class chat_listener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
//			System.out.println(e.getText());
			put(e.getText());
		}
	}

	public class approachButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			// System.out.println("approach0");
			put("command=approach");

			if (zoom != null) {
				if (isruning()) {
					setruning(false);
					zoom.setEnabled(true);
					zoomout.setEnabled(true);
				} else {
					setruning(true);
					zoom.setEnabled(false);
					zoomout.setEnabled(false);
				}
			}
//			System.out.println("approach" + isruning());
		}
	}

	class withdrawButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ee) {
			// System.out.println("withdraw");
			put("command=withdraw");
		}
	}
	/*
	 * public static void main(String[] args){ NetSimPanel Fenster = new
	 * NetSimPanel("localhost", 5000); Fenster.pack(); Fenster.show(); }
	 */
}