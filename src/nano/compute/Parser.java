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
package nano.compute;
import java.util.*;
import java.io.*;

import nano.debugger.Debg;


/**
* 
* @author Tibor Gyalog
* @version 1.0 8.1.2001
*/

public class Parser{
	Hashtable Befehle;


	public Parser(){
	}

	public Parser(Hashtable MyBefehle){
		Befehle=MyBefehle;
	}

	public void addCommand(String command, CommandExecutor MyExecutor){
        Befehle.put(command,MyExecutor);
	}

	public void setBefehlssatz(Hashtable MyBefehle){
		Befehle=MyBefehle;
	}

	public void setCommands(Hashtable NewHashtable){
		Befehle=NewHashtable;
	}

	CommandExecutor Get(String word){
		return (CommandExecutor)Befehle.get(word);
	}

	static Hashtable SetTags(String CommandLine){
		String tag_name,tag_value;
		StringTokenizer name;
		Hashtable MyHashtable=new Hashtable();
		StringTokenizer st = new StringTokenizer(CommandLine);
		while (st.hasMoreTokens()) {
			name=new StringTokenizer(st.nextToken(),"=");
			tag_name=name.nextToken();
			tag_value=name.nextToken();
			MyHashtable.put(tag_name,tag_value);
		}
		return MyHashtable;
	}

	public  void Console() throws IOException{
		String CommandLine;
		
		
		//DataInputStream stdIn = new DataInputStream(System.in);
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Welcome to CommandParser Console mode");
		//System.out.println("Type command=shutdown to exit.");
		while(true){
			//System.out.print("Parser>");
			CommandLine = stdIn.readLine();
			if (CommandLine!=null){
				try{
					parse(CommandLine);
				}catch(ParseException ev){
					//ev.printStackTrace();
				}
        	}
		}
	}
    
	public void parse(String CommandLine) throws ParseException{
		try{
			Hashtable CommandTags=SetTags(CommandLine);
			//System.out.println("Parser: "+CommandTags.toString());
			String command=(String)(CommandTags.get("command"));
			Get(command).execute(CommandTags);}catch(NullPointerException e){
				//System.err.println("Parser Null Pointer Exception for : " + CommandLine + ", " + e.getMessage());
				Debg.explainParserError(CommandLine, Befehle);
			}
			catch(NoSuchElementException ev){
				Debg.explainParserError(CommandLine, Befehle);
				//throw new ParseException("syntax error.");
				//System.out.println("Parser Syntax Error Exception:"+ev.getMessage());
		}
	}


}
