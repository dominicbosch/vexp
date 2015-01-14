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


package nano.compute.simulation;
import java.util.*;
import java.io.*;
import nano.compute.*;


/**
*
* @author Martin Guggisberg
* @version 0.9 15.04.2003 
*/


public class DemoSimulator extends Simulator{
    
    
    private static boolean running=true;
    
    private static final int ss = 256; // scan size

    public DemoSimulator(){
        super();
        init();
    }
    public void init(){
        addDouble("a",3.0);
        addDouble("b",5.0);
        addDouble("c",0.6);
       
        addCommand("startdemo", new startCommandExecutor());
        addCommand("stopdemo", new stopCommandExecutor());
    }


	public void start_simulation(){
    	running=true;
	}

	public void stop_simulation(){
    	running=false;
	}

   

    class startCommandExecutor extends CommandExecutor{
        public void execute(Hashtable tags){
            start_simulation();
        }
    }

    class stopCommandExecutor extends CommandExecutor{
        public void execute(Hashtable tags){
            stop_simulation();
        }
    }


    public void run(){
    byte[] data = new byte[ss];
    double tmp;
    while(true){
       while(running){
        for(int yi=0;yi<ss;yi++){ // y direction

            for(int xi=0;xi<ss;xi++){ // x direction
            
              //simple trigonometirc function
              tmp = gd("a")*8*Math.sin(gd("b")*xi/ss*4*Math.PI+gd("c")*yi/ss*Math.PI*2);
            
              //System.out.println(tmp+" a:"+gd("a")+" b:"+gd("b")+" c:"+gd("c"));
              if ((tmp>=-128) && (tmp <=127)){data[xi]=(byte)tmp;}
                else{
                  if(tmp < -128){data[xi]=-128;}
                     else{data[xi]=127;};};
                
                
            } //

            data[0]=(byte)(yi-ss/2); //first byte stores the line number from -128 to 128
            
            // send Stream to Clients
            try{source.write( data,0, 256);}catch(IOException e){}
			try{Thread.sleep(600);}catch (InterruptedException e){}
        }
        
      } //END RUNNING
     }  //END WHILE(TRUE)
   }    //END RUN
}
