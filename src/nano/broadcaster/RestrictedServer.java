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

package nano.broadcaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import nano.debugger.Debg;
import nano.compute.CommandExecutor;
import nano.compute.ParseException;
import nano.compute.Parser;
import nano.net.EventSocketException;
import nano.net.EventSocketListener;
import nano.net.RestrictedPool;
import nano.net.SourcePipe;

/**
 * 
 * @author Tibor Gyalog
 * @version 2.0 15.8.2002
 */
public class RestrictedServer implements EventSocketListener {
	private RestrictedPool myPool;
	private Parser myParser;
	private SourcePipe myData;
	private CommandProcessor myFilter;
	private int countUnknowns;
	private int localStreamPort = 6002;
	private int localEventPort = 6000;
	

	private RestrictedServer(String confFolder) {
		initServer(confFolder);
	}

	private void initServer(String confFolder) {
		Hashtable<String, CommandExecutor> Befehle = new Hashtable<String, CommandExecutor>();
		Befehle.put("newclient", new newclientCommandExecutor());
		Befehle.put("initports", new initportsCommandExecutor());
		Befehle.put("removeClient", new removeClientCommandExecutor());
		Befehle.put("getgui", new getGuiCommandExecutor());

		myParser = new Parser(Befehle);
		String sep = File.separator;
		try {
			batch(confFolder + sep + "config.dat");
		} catch (FileNotFoundException ef) {
			System.exit(0);
		} catch (IOException e) {
			System.exit(0);
		}
		myFilter = new CommandProcessor(this);
		myFilter.start();
		myData = new SourcePipe(myFilter.getSource());
		myPool = new RestrictedPool(this, myData, localEventPort, localStreamPort);
		try {
			batch(confFolder + sep + "StartScript.dat");
		} catch (FileNotFoundException ef) {}
		catch (IOException e) {}
		Debg.print(0, "Restricted server started.");
	}
	
	private void batch(String filename) throws IOException {
		String mymessage, parsedmessage;
		BufferedReader batchfile = new BufferedReader(new FileReader(filename));
		while ((mymessage = batchfile.readLine()) != null) {
			parsedmessage = mymessage.trim();
			if (!parsedmessage.startsWith("#")) {
				performSocketEvent(mymessage);
			}
		}
		batchfile.close();
	}
	
	public void newClient(int appletID) {
		String command = "command=get";
		try {
			myPool.put(command);
		} catch (EventSocketException ev) {
			ev.printStackTrace();
		}
		try {
			myFilter.parseCommand(command);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void removeClient(int appletID) {}

	public void endMeeting() {}
	
	public void receiveEvent(String message) {
		Debg.print(2, message);
		if (myPool != null) {
			try {
				myPool.put(message);
			} catch (Exception ev) {
				System.out.println("MyPool.put Problem.");
			}
		}
		try {
			myParser.parse(message);
		} catch (ParseException ev) {
			System.out.println("ParseException");
		}
	}
	
	public void performSocketEvent(String message) {
		if(message.compareTo("Unknown Command") == 0){
			System.err.println("Unknown count now up at " + countUnknowns);
			if(countUnknowns++ % 10 == 0) System.err.println("Unknown count now up at " + countUnknowns);
		} else {
			Debg.print(2, message);
			if (myFilter != null) {
				try {
					myFilter.parseCommand(message);
				} catch (ParseException pe) {}
			}
			if (myPool != null) {
				try {
					myPool.put(message);
				} catch (Exception ev) {}
			}
			try {
				myParser.parse(message);
			} catch (ParseException ev) {}
		}
	}


	private void initports(int newlocaleventport, int newlocalstreamport) {
		localStreamPort = newlocalstreamport;
		localEventPort = newlocaleventport;
	}
	

	private class newclientCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			newClient(Integer.parseInt((String) tags.get("appletid")));
		}
	}
	
	private class initportsCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			initports(Integer.parseInt((String) tags.get("eventport")), Integer
					.parseInt((String) tags.get("streamport")));
		}
	}

	private class removeClientCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			if (tags.get("appletID") != null) {
				myPool.deleteUser(Integer.parseInt((String) tags.get("appletID")));
			}
		}
	}
	
	private class getGuiCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			Debg.print(2, "wicked!");
		}
	}

	public static void main(String[] args) {
		new RestrictedServer("conf_restricted");
	}
}