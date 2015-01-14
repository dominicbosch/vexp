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

public class EventServerAcceptThread implements Runnable{
ESStateListener MyListener;
EventServerSocket MyEventServerSocket;
private Thread EventServerAcceptThread;
int Port;

    public EventServerAcceptThread(int NewPort, ESStateListener NewListener){
    MyListener=NewListener;Port=NewPort;
    EventServerAcceptThread=new Thread(this,"EventServerAcceptThread");
    EventServerAcceptThread.start();
    }

    public void run(){
        Socket dummySocket;
        ServerSocket MyEventAcceptSocket=null;
        try{
            MyEventAcceptSocket=new ServerSocket(Port);

            
            Thread thisThread = Thread.currentThread();
  //          System.out.println("Thread start myname:"+thisThread.getName());
            while(EventServerAcceptThread==thisThread){
                try{
                   dummySocket = MyEventAcceptSocket.accept();
                   MyEventServerSocket = new EventServerSocket(dummySocket);
                   MyListener.newEventSocket(MyEventServerSocket);
                    }
                   catch(IOException e){
                   		//e.printStackTrace();
                   }
            }
  //          System.out.println("Thread stop myname:"+thisThread.getName());
  //          System.out.println("Thread Event Server stopped");
        }
        catch(IOException e){
        	//e.printStackTrace();
        }
    }
}
