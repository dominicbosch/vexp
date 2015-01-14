package nano.compute.simulation;
import nano.compute.*;
import nano.net.*;
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

import java.io.*;

public class PushRemoteSimulator extends Simulator implements Display, ESStateListener, SSStateListener, EventSocketListener{

int  LocalEventPort=8012;
int LocalStreamPort=8014;
//EventClientSocket MyEventSocket;
//EventServerSocket MyEventServerSocket;
StreamSocket MyStreamServerSocket=null;
EventSocket MyEventServerSocket=null;
StreamServerAcceptThread MyStreamServerAcceptThread=null;
EventServerAcceptThread MyEventServerAcceptThread=null;
boolean connected=false, econnected=false;

    public void run(){

           // Read Messages, send Messages
          //System.out.print(".");

    }

   public void SSStateChanged(boolean ok, String State, int ID){
       System.out.println("PushRemoteSimulator:Message from Pushsimulator: SS Nr. "+ID+" changed to "+State);
    }

    public void ESStateChanged(boolean ok, String State, int ID, int remoteID){
       System.out.println("PushRemoteSimulator:Message from Pushsimulator: ES Nr. "+ID+" changed to "+State);
    }

    public PushRemoteSimulator(){
      super();
        addDouble("feedback",11);
        addDouble("zgain",2);
        addDouble("xtilt",0);
        addDouble("ytilt",0);
        addDouble("scanrange",10);
      init();
    }

    public void ParseCommand(String command) throws ParseException{
       //System.out.println("Command from Client to RAFM:"+command);
       
       if(MyEventServerSocket!=null){
        try{MyEventServerSocket.put(command);}catch(EventSocketException e){System.out.println("PushRemoteSimulator:EventSocketException");}
        }
        MyParser.parse(command);
    }

    public void init(){
       
		MyStreamServerAcceptThread = new StreamServerAcceptThread(LocalStreamPort,this);
  //        MyEventSocket=new EventClientSocket(URL,EventPort);
        //EventServerSocket MyEventServerSocket=new EventServerSocket(LocalEventPort);
        //MyEventServerSocket.addEventSocketListener(this);
        
        //start();
		MyEventServerAcceptThread = new EventServerAcceptThread(LocalEventPort, this);


    }

  public synchronized void newStreamSocket(StreamSocket NewSocket){
        System.out.println("PushRemoteSimulator:New Stream Socket");
            MyStreamServerSocket=NewSocket;
            MyStreamServerSocket.PlugAnzeige(this);
            connected=true;
    }
  public synchronized void newEventSocket(EventSocket NewSocket){
        System.out.println("PushRemoteSimulator:New EventSocket");
            MyEventServerSocket=NewSocket;
            MyEventServerSocket.addEventSocketListener(this);
            econnected=true;
    }

    public void performSocketEvent(String message){
       //System.out.println("Message from RAFM:"+message);
        MyBoss.ReceiveEvent(message);
    }



    public void write(byte[] b){
        //System.out.print("W");
         try{
            source.write( b,0, 256);
        }catch(IOException e){e.printStackTrace();}
    }



}
