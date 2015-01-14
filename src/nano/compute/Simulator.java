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

package nano.compute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import nano.compute.simulation.ImagingSimulator;

public class Simulator implements Runnable {

	protected Thread SimulatorThread = null;

	public Parser MyParser;

	public EventReceiver MyBoss;

	static private double[] DoubleParameter = new double[100];

	static private int[] IntParameter = new int[100];

	static private boolean[] BooleanParameter = new boolean[100];

	static private String[] StringParameter = new String[100];

	public PipedOutputStream source;

	private Hashtable IntNames, DoubleNames, StringNames, BooleanNames;

	Hashtable Datatypes;

	private Hashtable METAData;

	private int Num_double, Num_int, Num_boolean, Num_String;

	String SimulatorType;

	public void setDoubleNames(Hashtable MyDoubleNames) {
		DoubleNames = MyDoubleNames;
	}

	public void setController(EventReceiver NewBoss) {
		MyBoss = NewBoss;
	}

	public void addCommand(String command, CommandExecutor MyExecutor) {
		MyParser.addCommand(command, MyExecutor);
	}

	public void setDouble(String name, double Value) {
		DoubleParameter[((Integer) DoubleNames.get(name)).intValue()] = Value;
	}

	public void sd(String name, double Value) {
		setDouble(name, Value);
	}

	public double getDouble(String name) {
		return DoubleParameter[((Integer) DoubleNames.get(name)).intValue()];
	}

	public double gd(String name) {
		return getDouble(name);
	}

	public void setInt(String name, int Value) {
		IntParameter[((Integer) IntNames.get(name)).intValue()] = Value;
	}

	public void si(String name, int Value) {
		setInt(name, Value);
	}

	public int getInt(String name) {
		return IntParameter[((Integer) IntNames.get(name)).intValue()];
	}

	public int gi(String name) {
		return getInt(name);
	}

	public void setBoolean(String name, boolean Value) {
		BooleanParameter[((Integer) BooleanNames.get(name)).intValue()] = Value;
	}

	public String getValueAsString(String Label) {
		String value = "null";
		String type = (String) Datatypes.get(Label);
		if (type != null) {
			//System.out.println("A");
			if (type.equals("double")) {
				value = "" + gd(Label);
			}
			//System.out.println("B");
		}
		return value;
	}

	public void start() {
		if (SimulatorThread == null) {
			SimulatorThread = new Thread(this, "Simulator");
			SimulatorThread.start();
		}
	}

	public boolean getBoolean(String name) {
		return BooleanParameter[((Integer) BooleanNames.get(name)).intValue()];
	}

	public void setString(String name, String Value) {
		StringParameter[((Integer) StringNames.get(name)).intValue()] = Value;
	}

	public String getString(String name) {
		return StringParameter[((Integer) StringNames.get(name)).intValue()];
	}

	public void ParseCommand(String command) throws ParseException {
		//System.out.println("cmd"+command);
		MyParser.parse(command);
	}

	public PipedOutputStream getSource() {
		return source;
	}

	public Simulator() {
		String FullSimulatorType = getClass().getName();
		SimulatorType = FullSimulatorType.substring(FullSimulatorType
				.lastIndexOf(".") + 1);
		InitParser();
		source = new PipedOutputStream();
		DoubleNames = new Hashtable();
		IntNames = new Hashtable();
		StringNames = new Hashtable();
		BooleanNames = new Hashtable();
		Datatypes = new Hashtable();
		METAData = new Hashtable();
		Num_double = 0;
		Num_int = 0;
		Num_boolean = 0;
		Num_String = 0;
		addString("SimulatorType", SimulatorType);
		addDouble("Version", 1.0e0);
	}

	public void getMetaData(String confFolder) {
		String sep = "/";
		String filename = confFolder + sep + SimulatorType + ".meta";
	
			
			URL url = ImagingSimulator.class.getResource(filename);

			// for an application try
			if (url != null) {

				try {
					loadMetaData(url);
				} catch (IOException e) {
					System.out.println("META DATA File corrupted. :"+e.getMessage());
					System.out.println(confFolder + sep + SimulatorType + ".meta");
				}

			}
			else{
				System.out.println("Error:can not load with url, trying from filesystem");
				
				try {
					loadMetaData(confFolder + sep + SimulatorType + ".meta");
				} catch (FileNotFoundException ef) {
					System.out.println("META DATA File not found."+confFolder + sep + SimulatorType + ".meta");
				} catch (IOException e) {
					System.out.println("META DATA File corrupted.");
					System.out.println(confFolder + sep + SimulatorType + ".meta");
				} catch (java.security.AccessControlException eef) {
					System.out.println("Not enough privileges to read META DATA File.");
					System.out.println(confFolder + sep + SimulatorType + ".meta");
				}
				
			}
		
	}

	public void addDouble(String name, double Value) {
		Num_double++;
		Datatypes.put(name, new String("double"));
		DoubleNames.put(name, new Integer(Num_double));
		DoubleParameter[Num_double] = Value;
	}

	public void addInt(String name, int Value) {
		Num_int++;
		Datatypes.put(name, new String("int"));
		IntNames.put(name, new Integer(Num_int));
		IntParameter[Num_int] = Value;
	}

	public void addBoolean(String name, boolean Value) {
		Num_boolean++;
		Datatypes.put(name, new String("boolean"));
		BooleanNames.put(name, new Integer(Num_boolean));
		BooleanParameter[Num_boolean] = Value;
	}

	public void addString(String name, String Value) {
		Num_String++;
		Datatypes.put(name, new String("String"));
		StringNames.put(name, new Integer(Num_String));
		StringParameter[Num_String] = Value;
	}

	public void setMETAProperty(String Label, String key, String value) {
		Properties TheProperties = (Properties) METAData.get(Label);
		if (TheProperties == null) {
			TheProperties = new Properties();
		}
		TheProperties.setProperty(key, value);
		METAData.put(Label, TheProperties);
	}

	public String getMETAProperty(String Label, String key) {
		Properties TheProperties = (Properties) METAData.get(Label);
		return TheProperties.getProperty(key, "null");
	}

	public Properties getMETAProperties(String Label) {
		Properties MyProperty = (Properties) METAData.get(Label);
		//System.out.println(MyProperty.toString());
		return MyProperty;
	}

	public static String getString(Properties MyProperty) {
		String key, value, result;
		Enumeration AllPropKeys = MyProperty.keys();
		String allResult = "";
		while (AllPropKeys.hasMoreElements()) {
			key = (String) AllPropKeys.nextElement();
			value = MyProperty.getProperty(key, "null");
			result = key + "=" + value + " ";
			allResult = allResult.concat(result);
			//System.out.println(allResult);
		}
		return allResult;
	}

	public void SetMETAProperties(String Label, Properties NewProperties) {
		METAData.put(Label, NewProperties);
	}

	public String[] getMETA_Information() {
		String[] MyCommandArray = new String[100];
		int index = 0;
		Enumeration AllPropKeys;
		Properties MyProperty;
		String result, Label, key, value, allResult = "";
		Enumeration AllKeys = METAData.keys();
		//System.out.println("in getMETA_inf empty: "+METAData.isEmpty());
		while (AllKeys.hasMoreElements()) {
			Label = (String) AllKeys.nextElement();
			//System.out.println("Next Label: "+Label);
			String type = (String) Datatypes.get(Label);
			String Value = getValueAsString(Label);
			//System.out.println("Value     : "+Value);
			MyProperty = (Properties) METAData.get(Label);
			AllPropKeys = MyProperty.keys();
			allResult = "label=" + Label + " value=" + Value + " type=" + type
					+ " ";
			while (AllPropKeys.hasMoreElements()) {
				key = (String) AllPropKeys.nextElement();
				value = MyProperty.getProperty(key, "null");
				result = key + "=" + value + " ";
				allResult = allResult.concat(result);
			}
			MyCommandArray[index] = allResult;
			index++;
		}
		String[] ReturnArray = new String[index];
		for (int i = 0; i < index; i++) {
			ReturnArray[i] = MyCommandArray[i];
			//System.out.println(ReturnArray[i]);
		}
		return ReturnArray;
	}

	static Properties getProperties(String CommandLine) {
		String tag_name, tag_value;
		StringTokenizer name;
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

	public void loadMetaData(Properties[] NewProperties, int number_of_lines) {
		String Label;
		for (int i = 0; i < number_of_lines; i++) {
			Label = NewProperties[i].getProperty("label", "");
			if (!Label.equals("")) {
				NewProperties[i].remove("label");
				SetMETAProperties(Label, NewProperties[i]);
			} else {
				//System.out.println("Line "+i+": No Label defined.");
			}
		}
	}

	public void loadMetaData(String filename) throws IOException {
		String mymessage, Label;
		int i = 0;
		BufferedReader batchfile = new BufferedReader(new FileReader(filename));
		//System.out.println("load_parameter"+filename);
		while ((mymessage = batchfile.readLine()) != null) {
			i++;
			//System.out.println(mymessage);
			Properties NewProperties = getProperties(mymessage);
			Label = NewProperties.getProperty("label", "");
			if (!Label.equals("")) {
				NewProperties.remove("label");
				SetMETAProperties(Label, NewProperties);
			} else {
				//System.out.println("Line "+i+": No Label defined.");
			}
		}
		batchfile.close();
	}

	
	public void loadMetaData(URL filename) throws IOException {
		String mymessage, Label;
		int i = 0;
		
		InputStreamReader myir = new InputStreamReader(filename.openStream());
		BufferedReader batchfile = new BufferedReader(myir);
		//System.out.println("load_parameter"+filename);
		while ((mymessage = batchfile.readLine()) != null) {
			i++;
			//System.out.println(mymessage);
			Properties NewProperties = getProperties(mymessage);
			Label = NewProperties.getProperty("label", "");
			if (!Label.equals("")) {
				NewProperties.remove("label");
				SetMETAProperties(Label, NewProperties);
			} else {
				//System.out.println("Line "+i+": No Label defined.");
			}
		}
		batchfile.close();
	}

	
	
	public void run() {
		Thread myThread = Thread.currentThread();
		System.out.println("Thread start myname:"+myThread.getName());
		while (SimulatorThread == myThread) {
			//System.out.println("Thread:"+getDouble("Test"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Thread stop myname:"+myThread.getName());
	}

	/*   public static void main(String[] args){
	 Simulator M=new Simulator();
	 M.start();
	 M.addDouble("Test",5.0e0);
	 double f=M.getDouble("Test");
	 //System.out.println(f);
	 M.setDouble("Test",3.0e0);
	 f=M.getDouble("Test");
	 //System.out.println(f);
	 try{M.ParseCommand("command=set name=Test value=50");}catch(ParseException e){System.out.println("Parse Exception: "+e);e.printStackTrace();}
	 try{M.ParseCommand("command=set name=Fest value=50");}catch(ParseException e){System.out.println("Parse Exception: "+e);e.printStackTrace();}
	 }
	 */
	private void InitParser() {
		Hashtable Befehle = new Hashtable();
		Befehle.put("set", new setCommandExecutor());
		Befehle.put("get", new getCommandExecutor());
		Befehle.put("load", new loadCommandExecutor());
		Befehle.put("stop", new stopCommandExecutor());
		Befehle.put("start", new startCommandExecutor());
		Befehle.put("getgui", new GetGUICommandExecutor());
		Befehle.put("setproperty", new setPropertyCommandExecutor());
		MyParser = new Parser(Befehle);
	}

	public class setCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) throws ParseException {
			try {
				String name = (String) tags.get("name");
				String type = (String) Datatypes.get(name);
				String value = (String) tags.get("value");
				if (type.equals("double")) {
					setDouble(name, Double.parseDouble(value));
				} else {
					if (type.equals("int")) {
						setInt(name, (int) Double.parseDouble(value));
					} else {
						if (type.equals("boolean")) {
							setBoolean(name, Boolean.valueOf(value)
									.booleanValue());
						} else {
							if (type.equals("String")) {
								setString(name, value);
							} else {
								throw new ParseException("Unknown Data type.");
							}
						}
					}
				}
			} catch (Exception e) {
				throw new ParseException("Set-command exception.");
			}
		}
	}

	class getCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) throws ParseException {
			String value, name, Sendstring;
			try {
				name = (String) tags.get("name");
				if (name == null) {
					Sendstring = "command=status";
					Enumeration e = Datatypes.keys();
					String label, type;
					while (e.hasMoreElements()) {
						label = (String) e.nextElement();
						type = (String) Datatypes.get(label);
						if (type.equals("double")) {
							value = getDouble(label) + "";
						} else {
							if (type.equals("int")) {
								value = getInt(label) + "";
							} else {
								if (type.equals("boolean")) {
									value = getBoolean(label) + "";
								} else {
									if (type.equals("String")) {
										value = getString(label) + "";
									} else {
										throw new ParseException(
												"Unknown Data type.");
									}
								}
							}
						}
						Sendstring = "command=status name=" + label + " type="
								+ type + " value=" + value;
						//System.out.println(Sendstring);
						MyBoss.ReceiveEvent(Sendstring);
					}

				} else {
					String type = (String) Datatypes.get(name);
					//String value=(String)tags.get("value");
					if (type.equals("double")) {
						value = getDouble(name) + "";
					} else {
						if (type.equals("int")) {
							value = getInt(name) + "";
						} else {
							if (type.equals("boolean")) {
								value = getBoolean(name) + "";
							} else {
								if (type.equals("String")) {
									value = getString(name) + "";
								} else {
									throw new ParseException(
											"Unknown Data type.");
								}
							}
						}
					}

					Sendstring = "command=status name=" + name + " type="
							+ type + " value=" + value;
					//System.out.println(Sendstring);
					MyBoss.ReceiveEvent(Sendstring);
				}
			} catch (Exception e) {
				throw new ParseException("Get-command exception.");
			}

		}
	}

	class loadCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) throws ParseException {
			String name;
			try {
				name = (String) tags.get("name");
				if (name == null) {
					Enumeration e = Datatypes.keys();
					String label, Sendstring;
					while (e.hasMoreElements()) {
						label = (String) e.nextElement();
						Sendstring = "command=load name=" + label;
						//System.out.println(Sendstring);
						MyBoss.ReceiveEvent(Sendstring);
					}
				}
			} catch (Exception e) {
				throw new ParseException("Get-command exception.");
			}
		}
	}

	class stopCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			SimulatorThread = null;
		}
	}

	class setPropertyCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			String Label = (String) tags.get("label");
			String key = (String) tags.get("key");
			String value = (String) tags.get("value");
			setMETAProperty(Label, key, value);
		}
	}

	class GetGUICommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			//System.out.println("GetGUI Execute1");
			String[] Info = getMETA_Information();
			//System.out.println("INfo length"+Info.length);
			//System.out.println("myboss:"+MyBoss.toString());
			for (int i = 0; i < Info.length; i++) {
				MyBoss.ReceiveEvent("command=addcontrol " + Info[i]);
				//System.out.println("tomyboss"+i+Info[i]);
			}
		}
	}

	class startCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			start();
		}
	}

}