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
* @version 11. Nov. 2005 
*/


public class DemoSimulator3 extends Simulator{
    
    
    private static boolean running=true;
    
    private static final int ss = 256; // scan size

    public DemoSimulator3(){
        super();
        init();
    }
    public void init(){
        addDouble("p",40.0);
        addDouble("i",20.0);
        addDouble("c",60.0);
       
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
    double z_ist,z_soll,dz;
    while(true){
       while(running){
    	z_ist = 128;
    	for (int i = 0; i < 200; i++) {
		  z_soll = myfunction (0,0);
		  dz = z_soll - z_ist;
		  if (Math.abs(dz) < 2) break;
		  z_ist += dz*gd("p")/100;
		  System.out.println("dz:"+dz+"z_soll:"+z_soll+"z_ist"+z_ist);
		}
    	double int_z=0;
        /*
		   I_ist := Itot2(x,z);
		   scanline[j] := eval(z);
		   I_diff:= I_ist - I_soll;
		   isum := isum +ireg*I_diff*delta_t;
           delta_z := I_diff*p+isum;
		   z := z + delta_z;   
		   x := x + dx
    	*/
    	for(int yi=0;yi<ss;yi++){ // y direction
            for(int xi=0;xi<ss;xi++){ // x direction   
              z_soll = myfunction (xi,yi);	
              dz = z_soll - z_ist;
              int_z += dz*gd("i")/1000;
              z_ist += dz*gd("p")/100+int_z;
                	
              //System.out.println(tmp+" a:"+gd("a")+" b:"+gd("b")+" c:"+gd("c"));
              if ((z_ist>=-128) && (z_ist <=127)){data[xi]=(byte)z_ist;}
                else{
                  if(z_ist < -128){data[xi]=-128;}
                     else{data[xi]=127;};};
                
                
            } //

            data[0]=(byte)(yi-ss/2); //first byte stores the line number from -128 to 128
            
            // send Stream to Clients
            try{
            	source.write( data,0, 256);
            	source.flush();
            
            }catch(IOException e){}
			try{Thread.sleep(200);}catch (InterruptedException e){}
        }
        
      } //END RUNNING
     }  //END WHILE(TRUE)
   }    //END RUN
	private double myfunction(int xi, int yi) {
//		 Stepfunction
    	if (((xi+yi*gd("c")/15)% 80) > 40 )
    	  return 80;
		return -40;}
	
}
