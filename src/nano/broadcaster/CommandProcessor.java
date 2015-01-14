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
 * THE UNIVERSITY OF BASEL SPECIFICALLY DISCLAIMS ANY WARRANTIES,/*
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

import java.io.PipedOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import nano.compute.CommandExecutor;
import nano.compute.ParseException;
import nano.compute.Parser;
import nano.debugger.Debg;

/**
 * @author Dominic Bosch
 * Complete restructuring of existing vexp solution in order to gain a slim restrictive server
 */
public class CommandProcessor implements Runnable {
	static private double[] DoubleParameter = new double[100];
	static private int[] IntParameter = new int[100];
	static private boolean[] BooleanParameter = new boolean[100];
	static private String[] StringParameter = new String[100];
	
	private Thread thisThread = null;
	private Parser myParser;
	private RestrictedServer myServer;
	private PipedOutputStream source;
	private Hashtable<String, Integer> IntNames, DoubleNames, StringNames, BooleanNames;
	private Hashtable<String, String> Datatypes;

	public CommandProcessor(RestrictedServer chief) {
		myServer = chief;
		initParser();
		source = new PipedOutputStream();
		DoubleNames = new Hashtable<String, Integer>();
		IntNames = new Hashtable<String, Integer>();
		StringNames = new Hashtable<String, Integer>();
		BooleanNames = new Hashtable<String, Integer>();
		Datatypes = new Hashtable<String, String>();
	}
	
	public void start() {
		if (thisThread == null) {
			thisThread = new Thread(this, "Restricted Server instance");
			thisThread.start();
		}
	}
	
	public void run() {
		System.out.println("Thread start CommandProcessor,  myname:"+thisThread.getName());
		while (thisThread != null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		System.out.println("CommandProcessor Thread stopped");
	}

	public void parseCommand(String command) throws ParseException {
		Debg.print(2, "parsing command through parser: " + command);
		myParser.parse(command);
	}

	public PipedOutputStream getSource() {
		return source;
	}

	private void setDouble(String name, double Value) {
		DoubleParameter[((Integer) DoubleNames.get(name)).intValue()] = Value;
	}

	private double getDouble(String name) {
		return DoubleParameter[((Integer) DoubleNames.get(name)).intValue()];
	}

	private void setInt(String name, int Value) {
		IntParameter[((Integer) IntNames.get(name)).intValue()] = Value;
	}

	private int getInt(String name) {
		return IntParameter[((Integer) IntNames.get(name)).intValue()];
	}

	private void setBoolean(String name, boolean Value) {
		BooleanParameter[((Integer) BooleanNames.get(name)).intValue()] = Value;
	}

	private boolean getBoolean(String name) {
		return BooleanParameter[((Integer) BooleanNames.get(name)).intValue()];
	}

	private void setString(String name, String Value) {
		StringParameter[((Integer) StringNames.get(name)).intValue()] = Value;
	}

	private String getString(String name) {
		return StringParameter[((Integer) StringNames.get(name)).intValue()];
	}

	private void initParser() {
		Hashtable<String, CommandExecutor> Befehle = new Hashtable<String, CommandExecutor>();
		Befehle.put("set", new setCommandExecutor());
		Befehle.put("get", new getCommandExecutor());
		Befehle.put("stop", new stopCommandExecutor());
		Befehle.put("start", new startCommandExecutor());
		Befehle.put("getgui", new GetGUICommandExecutor());
		Befehle.put("goto3", new GotoSampleCommandExecutor());
		Befehle.put("autoapproach", new AutoApproachCommandExecutor());
		myParser = new Parser(Befehle);
	}

	private class setCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) throws ParseException {
			try {
				String name = (String) tags.get("name");
				String type = (String) Datatypes.get(name);
				String value = (String) tags.get("value");
				if (type.equals("double")) setDouble(name, Double.parseDouble(value));
				else if (type.equals("int")) setInt(name, (int) Double.parseDouble(value));
				else if (type.equals("boolean")) setBoolean(name, Boolean.valueOf(value).booleanValue());
				else if (type.equals("String"))setString(name, value);
				else throw new ParseException("Unknown Data type.");
			} catch (Exception e) {
				throw new ParseException("Set-command exception.");
			}
		}
	}

	private class getCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) throws ParseException {
			String value, name, sendString;
			try {
				name = (String) tags.get("name");
				if (name == null) {
					sendString = "command=status";
					Enumeration<String> e = Datatypes.keys();
					String label, type;
					while (e.hasMoreElements()) {
						label = (String) e.nextElement();
						type = (String) Datatypes.get(label);
						if (type.equals("double")) value = getDouble(label) + "";
						else if (type.equals("int")) value = getInt(label) + "";
						else if (type.equals("boolean")) value = getBoolean(label) + "";
						else if (type.equals("String")) value = getString(label) + "";
						else throw new ParseException("Unknown Data type.");
						
						sendString = "command=status name=" + label + " type="
								+ type + " value=" + value;
						myServer.receiveEvent(sendString);
					}

				} else {
					String type = (String) Datatypes.get(name);
					if (type.equals("double")) value = getDouble(name) + "";
					else if (type.equals("int"))value = getInt(name) + "";
					else if (type.equals("boolean"))value = getBoolean(name) + "";
					else if (type.equals("String"))value = getString(name) + "";
					else throw new ParseException("Unknown Data type.");
					
					sendString = "command=status name=" + name + " type="
							+ type + " value=" + value;
					myServer.receiveEvent(sendString);
				}
			} catch (Exception e) {
				throw new ParseException("Get-command exception.");
			}

		}
	}

	private class stopCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			thisThread = null;
		}
	}

	private class GetGUICommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			Debg.print(0, "Client sent get gui command but the GUI isn't dynamic with the restricted server");
		}
	}

	private class startCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			start();
		}
	}
	
	private class GotoSampleCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			start();
		}
	}
	
	private class AutoApproachCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			start();
		}
	}

}