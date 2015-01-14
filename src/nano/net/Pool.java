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

import nano.server.Server;


/**
* stellt methoden zur verwaltung der user zur verfuegung
*
* @author Tibor Gyalog
* @version 2.0 15.08.2002 (documented)
*/



public class Pool implements ESStateListener, SSStateListener
{
    private int num_Event,num_Stream;
    private int [] Port = new int[50];
    EventSocket[] MyEventSocket = new EventSocket[50];
    int[] RemoteIDs=new int[50];
    StreamSocket[] MyStreamSocket = new StreamSocket[50];
    EventSocketListener MyListener;
    SourcePipe MyPipe;
    EventServerAcceptThread MyEventAcceptSocket;
    StreamServerAcceptThread MyStreamAcceptSocket;
    Server MyServer;

    //ServerSocket MyEventAcceptSocket, MyStreamAcceptSocket;
    //Thread Acceptor;


//=================== Konstruktoren =======================

    public Pool(EventSocketListener TheMyListener, SourcePipe TheMyPipe, Server NewServer)
    {
        this(TheMyListener,TheMyPipe,5000,5002,NewServer);
    }

    public Pool(EventSocketListener TheMyListener, SourcePipe TheMyPipe, int LocalEventPort, int LocalStreamPort, Server NewServer)
    {
        num_Event = 0;num_Stream=0;
        MyListener=TheMyListener;
        MyPipe=TheMyPipe;
        MyServer=NewServer;
        MyEventAcceptSocket = new EventServerAcceptThread(LocalEventPort,this);
        MyStreamAcceptSocket = new StreamServerAcceptThread(LocalStreamPort,this);

  //          MyStreamAcceptSocket = new ServerSocket(LocalStreamPort);
    }

    public synchronized void newEventSocket(EventSocket NewSocket){
        MyEventSocket[num_Event]=NewSocket;
        MyEventSocket[num_Event].addEventSocketListener(MyListener);
        MyEventSocket[num_Event].addESStateListener(this);
        MyEventSocket[num_Event].setID(getNextEventID());
        num_Event++;
    }

    public int getNextEventID(){
        return num_Event;
    }

    public int getNextStreamID(){
        return num_Stream;
    }


    public String getIpEventClient(int nr){
    	  String ip="";
    	if (MyEventSocket[nr]!=null){
			ip=MyEventSocket[nr].getRemoteAddress();
    	}else{
    		ip="null";
    	}
    	return ip;
    }



    public int getEventSocketIndexFromRemoteID(int TheID){
    int i=0;
    while(MyEventSocket[i].getRemoteID()!=TheID && i< num_Event){i++;}
    if(i==num_Event){i=-1;}
    return i;
    }

    public synchronized void ESStateChanged(boolean ok,String state,int MyID, int RemoteID){
       //System.out.println("Message from Pool: ES Nr. "+MyID+" Remote "+RemoteID+" changed to "+state);
        if(state.equals("killing...")){
            deleteUser(RemoteID);
        }
        if(state.equals("registered...")){
            addUser(RemoteID);
        }
    }

    public void addUser(int appletID){
       //System.out.println("DB: User mit AppletID "+AppletID+" anmelden.");
        MyServer.newClient(appletID);
    }

    public void deleteUser(int AppletID){
        int Index=getEventSocketIndexFromRemoteID(AppletID);
        //System.out.println("Socket Index Eventport:"+Index+"\n");
        printESStates();
        
        //deleteStreamSocket(Index); 
        // this method is not needed because the sourcepipe
        // starts a demon thread which monitors the datasourcestream.
        
        deleteEventSocket(Index);
        //System.out.println("DB: User mit AppletID "+AppletID+" abmelden.");
        MyServer.removeClient(AppletID);
    }



    public synchronized void newStreamSocket(StreamSocket NewSocket){
        MyStreamSocket[num_Stream]=NewSocket;
        MyPipe.PlugStreamSocket(MyStreamSocket[num_Stream]);
        MyStreamSocket[num_Stream].addSSStateListener(this);
       // MyStreamSocket.setID(getNextStreamID());
        num_Stream++;
    }

    public void SSStateChanged(boolean ok, String State, int ID){
       //System.out.println("Message from Pool: SS Nr. "+ID+" changed to "+State);
    }


//====================== Methoden ========================

    public int getNum()
    {
        return num_Event;
    }



    public int[] getPort()
    {
        return Port;
    }

    public void endMeeting(){
       //System.out.println("DB: End Meeting !!");
        MyServer.endMeeting();
    }

    public void printESStates(){
   //System.out.println(num_Event+" Sockets active.");
   //System.out.println("Index,  AppletID,  state");
    for(int i=0;i<num_Event;i++){
       //System.out.println(i+"  "+MyEventSocket[i].getRemoteID()+"  "+MyEventSocket[i].getStatus());
        }
    }

    public void deleteEventSocket(int index)
    {
        try{
            MyEventSocket[index].destroy();
        }catch(NullPointerException e){
        	System.out.println ("NullPointer Exception could not destroy Eventsocket");
        	}
        num_Event--;
        MyEventSocket[index] = MyEventSocket[num_Event];
        if (num_Event==0){endMeeting();}
     }
     
    public void put(String message) throws EventSocketException{
        for(int i=0;i<num_Event;i++){MyEventSocket[i].put(message);
            //System.out.println("now number: "+num_Event);
        }
        //System.out.println("public void put()"+num_Event);
    }

    public boolean isConnected(int index)
    {
        boolean active;
        try{
            active = MyEventSocket[index].isActive();
        }catch(NullPointerException e){active = false;}
        return active;
    }


}
