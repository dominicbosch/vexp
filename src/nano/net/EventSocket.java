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

package nano.net;

import java.net.*;
import java.io.*;
import java.util.*;

import nano.compute.CommandExecutor;
import nano.compute.ParseException;
import nano.compute.Parser;

/**
 * abstract class, extended by EventServerSocket and EventClientSocket
 *
 * @author Tibor Gyalog and Raoul Schneider
 * @version 1.0 17.7.2001
 */

public abstract class EventSocket implements Runnable {
	public boolean Enable;

	public String status;

	Socket clientSocket = null; //Will be replaced by "this".

	PrintStream out;

	DataInputStream in;

	EventSocketListener MyListener = null;

	ESStateListener MyESStateListener;

	int PortNumber;

	int MyID = 0;

	int RemoteID = 0;

	int PingStatus = -1;

	Parser MyParser;

	boolean GameOver = false;

	//  RUN SUBROUTINES

	boolean getGameOver() {
		return GameOver;
	}

	abstract void initsocket();

	abstract void initialpingpong();

	Parser getMyParser() {
		if (MyParser == null) {
			System.out.println("My Parser is null, inform nano@unibas.ch");
		}
		return MyParser;
	}

	void initParser() {
		Hashtable Befehle = new Hashtable();
		Befehle.put("ping", new pingCommandExecutor());
		Befehle.put("pong", new pongCommandExecutor());
		MyParser = new Parser(Befehle);
		if (MyParser != null) {//System.out.println("I have a Parser...");

		}
	}

	void init() {

		reportStateChanged(false, "Instantiated...");
		GameOver = false;
		initsocket();
		try {
			out = new PrintStream(new BufferedOutputStream(clientSocket
					.getOutputStream(), 1024), false);
			//System.out.println("verbunden?"+out);
			in = new DataInputStream(new BufferedInputStream(clientSocket
					.getInputStream()));
		} catch (Exception e) {
			reportStateChanged(false, "Init Error");
		}
		initialpingpong();
	}

	void listen() {
		String inputLine;

		reportStateChanged(true, "Listening...");
		try {
			while ((inputLine = in.readLine()) != null) { //deprecated
				//System.out.println("GET:"+inputLine);
				//                   while(true){
				//				   StringBuffer inputBuffer = new StringBuffer(100);
				//				   while (((chr = in.readChar()) != '\n') || ((chr = in.readChar()) != '\r'))
				//					inputBuffer.append(chr);
				//                   
				//                    inputLine = inputBuffer.toString();
				try {
					getMyParser().parse(inputLine);
				} catch (ParseException e) {
				}
				if ((inputLine.indexOf("command=ping") == -1)
						&& (inputLine.indexOf("command=pong") == -1)) {
					MyListener.performSocketEvent(inputLine);
				}
			}
		} catch (IOException e) {
			reportStateChanged(false, "IOException");
		}
	}

	public void reportStateChanged(boolean ok, String newState) {
		status = newState;
		try {
			MyESStateListener.ESStateChanged(ok, newState, MyID, RemoteID);
		} catch (NullPointerException e) {
		}
	}

	void kill() {
		reportStateChanged(false, "killing...");
		try {
			out.close();
		} catch (Exception e) {
		}
		try {
			in.close();
		} catch (Exception e) {
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
		}
		clientSocket = null;
	}

	// METHODS
	public void put(String message) throws EventSocketException {
		//System.out.println("Put:"+message);
		try {
			out.print(message + "\015\012");
			out.flush();
		} catch (NullPointerException ev) {
			throw new EventSocketException("Output Stream not ready.");
		}

	}

	public void setID(int NewID) {
		MyID = NewID;
	}

	public int getID() {
		return MyID;
	}

	public int getRemoteID() {
		return RemoteID;
	}

	public void addEventSocketListener(EventSocketListener NewListener) {
		MyListener = NewListener;
	}

	public void addESStateListener(ESStateListener NewListener) {
		MyESStateListener = NewListener;
	}

	public void destroy() {
		GameOver = true;
		try {
			out.close();
		} catch (Exception e) {
		}
		try {
			in.close();
		} catch (Exception e) {
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
		}
		clientSocket = null;
		//System.out.println("destroy Event Socket")
		;
		reportStateChanged(false, "Destroyed...");
	}

	public boolean isActive() {
		return Enable;
	}

	protected void setStatus(boolean enable, String myStatus) {
		status = myStatus;
		Enable = enable;
	}

	public String getStatus() {
		return status;
	}

	public String getRemoteAddress() {
		return clientSocket.getLocalAddress().toString();
	}

	public int getRemotePort() {
		return clientSocket.getPort();
	}

	public void ping() {
		//System.out.println("Ping");
		PingStatus = 1;
		reportStateChanged(true, "Waiting for Pong");
		try {
			put("command=ping requestID=" + MyID);
		} catch (Exception e) {
			reportStateChanged(false, "PingPong failed");
			PingStatus = -1;
		}
	}

	public void analyzePingPong(int NewRemoteID) {
		PingStatus = 0;
		RemoteID = NewRemoteID;
		//System.out.println("Analyze, Remote="+RemoteID);
		reportStateChanged(true, "Ping Successful");
	}

	public void pong(int NewRemoteID) {
		RemoteID = NewRemoteID;
		reportStateChanged(true, "I was pinged.");
		//System.out.println("Pong, Remote="+RemoteID);
		try {
			put("command=pong requestID=" + RemoteID + " responseID=" + MyID);
			reportStateChanged(true, "registered...");
		} catch (Exception e) {
			reportStateChanged(false, "PingPong failed");
		}
	}

	class pingCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			try {
				int RemoteID = Integer.parseInt((String) tags.get("requestID"));
				pong(RemoteID);
			} catch (Exception e) {
				System.out.println("Syntax Error in Ping");
			}
		}
	}

	class pongCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			try {
				int RemoteID = Integer
						.parseInt((String) tags.get("responseID"));
				analyzePingPong(RemoteID);
			} catch (Exception e) {
				System.out.println("Syntax Error in Pong");
			}
		}
	}

}