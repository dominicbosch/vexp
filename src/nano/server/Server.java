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

package nano.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import nano.compute.CommandExecutor;
import nano.compute.EventReceiver;
import nano.compute.ParseException;
import nano.compute.Parser;
import nano.compute.Simulator;
import nano.db.DB_inquiryDesk;
import nano.db.SQL_inquiryDesk;
import nano.net.EventSocketException;
import nano.net.EventSocketListener;
import nano.net.Pool;
import nano.net.SourcePipe;

/**
 * 
 * @author Tibor Gyalog
 * @version 2.0 15.8.2002
 */
public class Server implements EventSocketListener, EventReceiver {
	Pool MyPool;
	DB_inquiryDesk MyInquiryDesk;
	Simulator MySimulator;
	Parser MyParser;
	SourcePipe MyData;
	int localStreamPort = 6002;
	int localEventPort = 6000;
	String SourceName = "undefined";
	boolean initialized = false;

	public void initSource(String newSourceName) {
		if (!initialized) {
			SourceName = newSourceName;
		}
	}

	public void stopinit() {
		initialized = true;
	}

	public void loadSource() {
		try {
			//System.out.println(SourceName);
			Class SimulatorType = Class.forName("nano.compute.simulation."
					+ SourceName);
			Object GeneralSimulator = SimulatorType.newInstance();
			MySimulator = (Simulator) GeneralSimulator;
			//	MyInquiryDesk.setSimulatortype(SourceName);
			// This initialises MyInquiryDesk and creates the tables if they do
			// not exist.
		}
		//catch(IOException e){e.printStackTrace();}
		catch (ClassNotFoundException e) {
			System.out.println("nano.compute.simulation."+SourceName+" not found.");
			e.printStackTrace();
			System.exit(0);
		} catch (ClassCastException e) {
			System.out.println("nano.compute.simulation."+SourceName+" ist nicht von nano.compute.Simulator vererbt.");
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			System.out.println("nano.compute.simulation."+SourceName+" ist abstrakt oder Interface.");
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			System.out.println("Zugriff auf Simulatorklasse nano.compute.simulation."+SourceName+" verweigert.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void newClient(int appletID) {
		String command;
		boolean load = false;
		try {
			load = MyInquiryDesk.newClient(appletID);
		} catch (nano.db.AppletIDException e) {
			System.out.println(appletID + "Not known: " + e.getMessage());
		}
		if (load) command = "command=load";
		else command = "command=get";
		try {
			MyPool.put(command);
		} catch (EventSocketException ev) {
			ev.printStackTrace();
		}
		try {
			MySimulator.ParseCommand(command);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void removeClient(int appletID) {
		MyInquiryDesk.removeClient(appletID);
		//System.out.println("Remove Client with AppletID " + appletID);
	}

	public void endMeeting() {
		MyInquiryDesk.endMeeting();
	}
	
	public void kill() {
	//	System.out.println("Controller Methode: kill");
		try {
			MyPool.put("Ich sterbe.");
		} catch (EventSocketException ev) {
			//ev.printStackTrace();
		}
		System.exit(0);
	}

	public void ReceiveEvent(String message) {
		if (MyPool != null) {
			try {
				MyPool.put(message);
			} catch (Exception ev) {
				System.out.println("MyPool.put Problem.");
			}
		}
		try {
			MyParser.parse(message);
		} catch (ParseException ev) {
			System.out.println("ParseException");
		}
	}

	public void update() {

		MyInquiryDesk.cleanup();

		Vector activeList = new Vector();
		Vector dbList;

		// writes the ipadress of all running event stream sockets in a Vector
		for (int i = 0; i < MyPool.getNum(); i++) {
			activeList.add(MyPool.getIpEventClient(i));
		}

		// get the vector of all running clients out of the DB
		dbList = MyInquiryDesk.getConnectedIp();

		// check if DB has more active clients than running eventstream
		for (Iterator iter = dbList.iterator(); iter.hasNext();) {
			String ip = (String) iter.next();

			if (activeList.contains(ip) == false) {
				// no Stream to ip --> remove ip from DB
				int id = MyInquiryDesk.getAppletId(ip);
				System.out.println("not active:applet" + id + " with ip:" + ip
						+ "\n");
				System.out.println("Remove Client from DB with ID");
				removeClient(id);
			}
			;
		}

		// check if the vexp server has running clients which are
		//   not registerd in the DB
		for (Iterator iter = activeList.iterator(); iter.hasNext();) {
			String ip = (String) iter.next();

			if (dbList.contains(ip) == false) {
				// no Stream to ip --> remove ip from DB
				int appletID = MyInquiryDesk.getAppletId(ip);
				int index = MyPool.getEventSocketIndexFromRemoteID(appletID);
				System.out.println("Stop Event Stream to unregisterd User" + ip
						+ " Appletid: " + appletID + " Eventindex:" + index
						+ "\n");
				MyPool.deleteEventSocket(index);

			}
			;
		}

	}

	public void info() {
		//System.out.println("Controller Methode: info");
		update();
		try {
			MyPool.put("Wir sind " + MyPool.getNum() + " Benutzer");
		} catch (EventSocketException ev) {
			//ev.printStackTrace();
		}
	}

	public void status(String label, String value, String type) {
		//System.out.println("Controller Methode: status, "+label+"="+value);
		if (label != null && value != null) {
			MyInquiryDesk.saveParameter(label, value, type);
		}
	}

	// If load command is sent, we load the parameter and
	// send a set command
	public void load(String label) {
		String value = null;
		//System.out.println("Controller Methode: load "+label);
		value = MyInquiryDesk.loadParameter(label);
		if (value != null) {
			//System.out.println("command=set name="+label+" value="+value);
			try {
				MyPool.put("command=set name=" + label + " value=" + value);
			} catch (EventSocketException ev) {
				ev.printStackTrace();
			}
			try {
				MySimulator.ParseCommand("command=set name=" + label
						+ " value=" + value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void save(String VirtualFileName) {
		try {
			MyPool.put("command=get");
		} catch (EventSocketException ev) {
			ev.printStackTrace();
		}
		MyInquiryDesk.exportParameterSetByName(VirtualFileName);
	}

	public void open(String VirtualFileName) {
		MyInquiryDesk.importParameterSetByName(VirtualFileName);
		try {
			MySimulator.ParseCommand("command=load");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void init_by_db(String RoomName) {
		//System.out.println("Init by DB: \n"
		//     +"StreamPort: "+MyInquiryDesk.getStreamPort(RoomName)
		// +" EventPort: "+MyInquiryDesk.getEventPort(RoomName)
		// +" ExpTypName: "+MyInquiryDesk.getExpTypName(RoomName)
		//    );
		//System.out.println("RoomName:"+ RoomName);
		String commandstring = "command=initports eventport="
				+ MyInquiryDesk.getEventPort(RoomName) + " streamport="
				+ MyInquiryDesk.getStreamPort(RoomName);
		performSocketEvent(commandstring);
		commandstring = "command=initsource name="
				+ MyInquiryDesk.getExpTypName(RoomName);
		//System.out.println("commandstring:"+commandstring);
		performSocketEvent(commandstring);
		performSocketEvent("command=stopinit");
	}

	public void batch(String filename) throws IOException {
		String mymessage, parsedmessage;
		int i = 0;
		BufferedReader batchfile = new BufferedReader(new FileReader(filename));
		while ((mymessage = batchfile.readLine()) != null) {
			i++;
			//System.out.println("Batchfile "+filename+" Line "+i);
			parsedmessage = mymessage.trim();
			if (!parsedmessage.startsWith("#")) {
				performSocketEvent(mymessage);
			}
		}
		batchfile.close();
	}

	public Server() {
		initServer("conf");
	}

	public Server(String confFolder) {
		initServer(confFolder);
	}

	public void initServer(String confFolder) {
		Hashtable Befehle = new Hashtable();
		Befehle.put("info", new infoCommandExecutor());
		Befehle.put("kill", new killCommandExecutor());
		Befehle.put("newclient", new newclientCommandExecutor());
		Befehle.put("status", new statusCommandExecutor());
		Befehle.put("load", new loadCommandExecutor());
		Befehle.put("endMeeting", new endMeetingCommandExecutor());
		Befehle.put("save", new saveCommandExecutor());
		Befehle.put("open", new openCommandExecutor());
		Befehle.put("initports", new initportsCommandExecutor());
		Befehle.put("initsource", new initSourceCommandExecutor());
		Befehle.put("init_by_db", new init_by_dbCommandExecutor());
		Befehle.put("stopinit", new stopinitCommandExecutor());
		Befehle.put("removeClient", new removeClientCommandExecutor());
		Befehle.put("updateDB", new updateDBCommandExecutor());

		MyParser = new Parser(Befehle);
		String sep = File.separator;
		MyInquiryDesk = new SQL_inquiryDesk(confFolder + sep + "passwd");
		try {
			batch(confFolder + sep + "config.dat");
		} catch (FileNotFoundException ef) {
			//System.out.println("Config File not found.");
			System.exit(0);
		} catch (IOException e) {
			//System.out.println("Config File corrupted.");
			System.exit(0);
		}
		loadSource();
		if (MySimulator == null) {
			MySimulator = new Simulator();
		}
		MySimulator.start();
		MySimulator.setController(this);
		MySimulator.getMetaData(confFolder);
		MyData = new SourcePipe(MySimulator.getSource());
		MyPool = new Pool(this, MyData, localEventPort, localStreamPort, this);
		//String sep=File.separator;
		try {
			batch(confFolder + sep + "StartScript.dat");
		} catch (FileNotFoundException ef) {
			//System.out.println("Batch File not found.");
		} catch (IOException e) {
			//System.out.println("Batch File corrupted.");
		}
	}

	public void initports(int newlocaleventport, int newlocalstreamport) {
		if (!initialized) {
			localStreamPort = newlocalstreamport;
			localEventPort = newlocaleventport;
		}
	}

	public void performSocketEvent(String message) {
		//System.out.println("SocketEvent: ");
		//System.out.println(message);
		if (MySimulator != null) {
			try {
				MySimulator.ParseCommand(message);
			} catch (ParseException pe) {
				//System.out.println("Simulator cannot parse "+message);
			}
		}
		if (MyPool != null) {
			try {
				MyPool.put(message);
			} catch (Exception ev) {
				//System.out.println("MyPool.put Problem.");
			}
		}
		try {
			MyParser.parse(message);
		} catch (ParseException ev) {
			//System.out.println("ParseException");
		}
	}

	class killCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			kill();
		}
	}

	class infoCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			info();
		}
	}

	class newclientCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			newClient(Integer.parseInt((String) tags.get("appletid")));
		}
	}

	class statusCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			status((String) tags.get("name"), (String) tags.get("value"),
					(String) tags.get("type"));
		}
	}

	class loadCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			if (tags.get("name") != null) {
				load((String) tags.get("name"));
			}
		}
	}

	class endMeetingCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			endMeeting();
		}
	}

	class saveCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			save((String) tags.get("filename"));
		}
	}

	class openCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			open((String) tags.get("filename"));
		}
	}

	class initportsCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			initports(Integer.parseInt((String) tags.get("eventport")), Integer
					.parseInt((String) tags.get("streamport")));
		}
	}

	class initSourceCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			initSource((String) tags.get("name"));
		}
	}

	class init_by_dbCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			init_by_db((String) tags.get("roomname"));
		}
	}

	class stopinitCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			stopinit();
		}
	}

	class removeClientCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			if (tags.get("appletID") != null) {

				MyPool.deleteUser(Integer.parseInt((String) tags
						.get("appletID")));

				//removeClient(Integer.parseInt((String)
				// tags.get("appletID")));
			}
		}
	}

	class updateDBCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			update();
		}
	}

	public static void main(String[] args) {
		String confFolder;
		String sep = File.separator;
		if (args.length == 0) {
			confFolder = "." + sep + "conf";
		} else {
			confFolder = args[0];
		}
//		System.out.println("Conf-Folder: " + confFolder);
		new Server(confFolder);
	}
}