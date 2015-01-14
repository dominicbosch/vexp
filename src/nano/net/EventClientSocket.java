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


/**
* extends the abstract class EventSocket
*
* @author Tibor Gyalog and Raoul Schneider
* @version 1.0 17.7.2001
*/



public class EventClientSocket extends EventSocket{
    String serveraddress;
    int PortNumber1;
    protected Thread ClientEventSocketThread;

    public EventClientSocket(String myserveraddress, int myPortNumber){
        serveraddress = myserveraddress;
        PortNumber1 = myPortNumber;
        initParser();
        ClientEventSocketThread=new Thread(this,"EventClientSocket");
        ClientEventSocketThread.start();
    }
    public EventClientSocket(String myserveraddress, int myPortNumber, int NewID){
        serveraddress = myserveraddress;
        MyID=NewID;
        PortNumber1 = myPortNumber;
        initParser();
        ClientEventSocketThread=new Thread(this,"EventClientSocket");
        ClientEventSocketThread.start();
    }

    void initsocket(){
        try{
            clientSocket =  new Socket(serveraddress, PortNumber1);
        }catch(Exception e){
        	System.out.println("Problem with socket "+e.getMessage());
        }

    }

	void initialpingpong(){
		new TryPingPong();
	}

    public void run(){
	    Thread thisThread = Thread.currentThread();
//	    System.out.println("Thread start myname:"+thisThread.getName());
        while(!getGameOver() && (ClientEventSocketThread==thisThread)){
                Enable=false;
                while(clientSocket==null){
                 init();
                 if(clientSocket==null){ try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){}}
                }
                setStatus(true, "connected.");
                listen();
                //clientSocket=null;
                setStatus(false, "disconnected.");
                kill();
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){}
        }
  //      System.out.println("Thread stop myname:"+thisThread.getName());
  //  System.out.println("Thread EventClient Socket terminated.");
    
    }
	class TryPingPong implements Runnable{
	private volatile Thread TryPingPong;
		
	public TryPingPong(){
		TryPingPong=new Thread(this,"Ping-Pong");
		TryPingPong.start();
		
		
	}
	public void run(){
		int NumTries=0;
		boolean PingPong=true;
		do{
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		try{
		NumTries++;		
		ping();
		PingPong=true;
		}catch(Exception e){reportStateChanged(false,"PingPong failed "+NumTries+" times.");PingPong=false;}
		}
		while(NumTries<5 && !PingPong);
	}
	}
}
