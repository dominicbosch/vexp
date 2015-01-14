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

public class StreamServerAcceptThread implements Runnable{
SSStateListener MyListener;
StreamServerSocket MyStreamServerSocket;
private Thread StreamServerAcceptThread;
int Port;

    public StreamServerAcceptThread(int NewPort, SSStateListener NewListener){
    MyListener=NewListener;Port=NewPort;
    
    StreamServerAcceptThread=new Thread(this,"EventServerAcceptThread");
   StreamServerAcceptThread.start();
   
    }

    public void run(){
        Socket dummySocket;
        ServerSocket MyStreamAcceptSocket=null;
        try{
            MyStreamAcceptSocket=new ServerSocket(Port);
            Thread thisThread = Thread.currentThread();
 //           System.out.println("Thread start myname:"+thisThread.getName());
            while(StreamServerAcceptThread==thisThread){
                try{
                   dummySocket = MyStreamAcceptSocket.accept();
                   MyStreamServerSocket = new StreamServerSocket(dummySocket);
                   MyListener.newStreamSocket(MyStreamServerSocket);
                    }
                   catch(IOException e){
                   		//e.printStackTrace();
                   	}
            }
  //          System.out.println("Thread StreamServer Accept stopped"+thisThread.getName());
        }
        catch(IOException e){
        	//e.printStackTrace();
        }
    }
}
