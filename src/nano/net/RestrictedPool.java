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

import nano.broadcaster.RestrictedServer;
import nano.debugger.Debg;

/**
 * 
 * @author Dominic Bosch
 * Makover for the restricted command broadcaster
 */
public class RestrictedPool implements ESStateListener, SSStateListener{
    private int num_Event,num_Stream;
    EventSocket[] arrEventSockets = new EventSocket[50];
    int[] arrRemoteIDs=new int[50];
    StreamSocket[] arrStreamSockets = new StreamSocket[50];
    SourcePipe myPipe;
    EventServerAcceptThread eventSocketAcceptor;
    StreamServerAcceptThread streamSocketAcceptor;
    RestrictedServer myServer;

    public RestrictedPool(RestrictedServer server, SourcePipe pipe, int locEvtPort, int locStrPort){
        num_Event = 0;
        num_Stream = 0;
        myPipe = pipe;
        myServer = server;
        eventSocketAcceptor = new EventServerAcceptThread(locEvtPort, this);
        streamSocketAcceptor = new StreamServerAcceptThread(locStrPort, this);
    }

    public synchronized void newEventSocket(EventSocket newSocket){
        arrEventSockets[num_Event] = newSocket;
        arrEventSockets[num_Event].addEventSocketListener(myServer);
        arrEventSockets[num_Event].addESStateListener(this);
        arrEventSockets[num_Event].setID(getNextEventID());
        num_Event++;
    }

    public synchronized void ESStateChanged(boolean ok, String state, int myID, int remoteID){
        if(state.equals("killing...")){
            deleteUser(remoteID);
        }
        if(state.equals("registered...")){
            addUser(remoteID);
        }
    }
    public void deleteUser(int appletID){
        int index=getEventSocketIndexFromRemoteID(appletID);
        deleteEventSocket(index);
        myServer.removeClient(appletID);
    }

    public synchronized void newStreamSocket(StreamSocket newSocket){
        arrStreamSockets[num_Stream] = newSocket;
        myPipe.PlugStreamSocket(arrStreamSockets[num_Stream]);
        arrStreamSockets[num_Stream].addSSStateListener(this);
        num_Stream++;
    }

    public void SSStateChanged(boolean ok, String State, int ID){
       //System.out.println("Message from Pool: SS Nr. "+ID+" changed to "+State);
    }
    
    public void put(String message) throws EventSocketException{
    	for(int i=0;i<num_Event;i++){
    		arrEventSockets[i].put(message);
    		Debg.print(2, message);
    		//System.out.println("now number: "+num_Event);
    	}
    }

    private int getNextEventID(){
        return num_Event;
    }

    private int getEventSocketIndexFromRemoteID(int id){
	    int i = 0;
	    while(arrEventSockets[i].getRemoteID() != id && i < num_Event) i++;
	    if(i == num_Event) i = -1;
	    return i;
    }
    
    private void addUser(int appletID){
        myServer.newClient(appletID);
    }

    private void endMeeting(){
        myServer.endMeeting();
    }

    private void deleteEventSocket(int index) {
        try{
            arrEventSockets[index].destroy();
        } catch(NullPointerException e) {
        	System.out.println ("NullPointer Exception could not destroy Eventsocket");
        }
        num_Event--;
        arrEventSockets[index] = arrEventSockets[num_Event];
        if (num_Event==0){endMeeting();}
     }
}
