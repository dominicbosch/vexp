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
* extends the abstract class StreamSocket
*
* @author Tibor Gyalog and Raoul Schneider
* @version 1.0 17.7.2001
*/


public class StreamServerSocket extends StreamSocket{

	 private Thread StreamServerSocketThread;

    public StreamServerSocket(Socket myClientSocket){
        clientSocket = myClientSocket;
        StreamServerSocketThread=new Thread(this,"StreamServerSocketThread");
        StreamServerSocketThread.start();
    }


    void initsocket(){
    }


    public void run(){
        Enable = false;
        init();
        setStatus(true, "connected");
        //try{while((in.readLine()) != null){}}catch(IOException e){}
       //System.out.println("StreamServerSocket: Init method terminated. Starting Listen()");

        listen();
       //System.out.println("StreamServerSocket: Listen method terminated. Starting kill()");
		setStatus(false,"Inexistent");
        kill();
 //      System.out.println("StreamServerSocket: Run method terminated.");
       
    }


}
