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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;



public abstract class StreamSocket implements Runnable{

// CLASS VARAIABLES
    public boolean Enable;
    public String status;
    BufferedOutputStream out;
    BufferedInputStream in;
    int PortNumber;
    Socket clientSocket = null;
    Display[] MyAnzeige=new Display[100];
    int NumberOfStreams = 0;
    int NumberOfAnzeige=0;
    StreamClientSocket[] MyOutStreams;
    int MyID=0;
    SSStateListener MySSStateListener;
    boolean GameOver=false;
    



//------ Run Subroutines ----------------------

    abstract void initsocket();

    public void reportStateChanged(boolean ok, String newState){
    	Enable=ok;
        try{MySSStateListener.SSStateChanged(ok,newState,MyID);}catch(NullPointerException e){}
    }


    void init(){
        //setName("Hello");
        reportStateChanged(false,"Instanziiert");
        GameOver=false;
        initsocket();
        try{
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            in = new BufferedInputStream(clientSocket.getInputStream());
        }catch (Exception e) {reportStateChanged(false,"Init Error");}


    }

    public void addSSStateListener(SSStateListener NewListener){
        MySSStateListener=NewListener;
    }



    void kill(){
        reportStateChanged(false,"Killing");
        try{out.close();}catch(IOException e){System.out.println("StreamSocket:could not close output (StreamSocket) "+e);}
        try{in.close();}catch(IOException e){System.out.println("StreamSocket:could not close input (StreamSocket) "+e);}
        try{clientSocket.close();}catch(IOException e){System.out.println("StreamSocket:could not close socket (StreamSocket) "+e);}
        //clientSocket=null;
        //in = null;
        //out = null;
        
    }


    public void put(byte[] b){
        try{if(out!=null){
        	out.write(b,0,256);
            out.flush();
        }}catch(IOException ev){}
    }

    public void listen(){
    	int counter=0;
    	//System.out.println("Listen");
        byte TheLine[] = new byte[256];
        reportStateChanged(true,"I am Listening...");
         try{
        while(counter<20){
        		counter++;
                while(in.read(TheLine, 0, 256)!=-1){
                    for(int ki=0;ki<NumberOfAnzeige;ki++){
                        MyAnzeige[ki].write(TheLine);}
                }
                //System.out.println("==========================>> Error");
                try{
                		Thread.sleep(500);
                	}catch (InterruptedException e){System.out.print("Interrupted");}
            }
        }catch(IOException e){reportStateChanged(false,"IOException...");}
        TheLine=null;
    }

    public void PlugStreamClientSocket(StreamClientSocket NewNetStream){
        MyOutStreams[NumberOfStreams] = NewNetStream;
        NumberOfStreams++;
    }

    public void PlugAnzeige(Display newAnzeige){
        MyAnzeige[NumberOfAnzeige] = newAnzeige;
        NumberOfAnzeige++;
    }

    boolean getGameOver(){return GameOver;}


    public void destroy(){
        GameOver=true;
        kill();
    }

    public boolean isActive(){
        return Enable;
    }

    protected void setStatus(boolean enable, String myStatus){
        status = myStatus;
        Enable = enable;
    }

    public String getRemoteAddress(){
    	if(clientSocket != null) return clientSocket.getLocalAddress().toString();
    	else return "NONE";
    }

    public int getRemotePort(){
        return clientSocket.getPort();
    }


}
