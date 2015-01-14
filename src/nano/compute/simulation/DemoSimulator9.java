package nano.compute.simulation;

import java.io.IOException;
import java.util.*;

import nano.compute.CommandExecutor;
import nano.compute.Simulator;
import nano.compute.util.Ag;

public class DemoSimulator9 extends Simulator {

	final int ss = 256;
	double[][] mydata;
	boolean running=true;
	private Ag myAg;
	public DemoSimulator9() {
		super();
		

		init();

	}

	private void init() {

		addDouble("a", 50);
		addDouble("b", 5.0);
		addDouble("c", 0.6);
		
		myAg = new Ag();
		myAg.setArraySize(256);
		myAg.setScanSize(500); //in A
		myAg.setAtomRadius(2);
		myAg.setAtomWidth(1.6); //in nm
		myAg.setAgAtomSize(20.0); //in A
		myAg.init();
		
		
		mydata = myAg.getSurface();

		addCommand("startdemo", new startCommandExecutor());
		addCommand("stopdemo", new stopCommandExecutor());
	}



	public void start_simulation(){
		running=true;
	}
	public void stop_simulation(){
		running=false;System.out.println("stopdemo");
	}
	class startCommandExecutor extends CommandExecutor{
		public void execute(Hashtable tags){
			double size=gd("a")*10;
			//mydata=null;
			
			myAg.setScanSize(size);
			myAg.init();
			mydata = myAg.getSurface();
			//mydata=null;
			System.out.println(size);
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
	            		 
	            		 for(int y=0;y<ss;y++){
	            			 if (!running) break;
	            			 
	            			 for(int x=0;x<ss;x++){
	            				 
	            			int out=(int)(100*(mydata[x][y]+200));	 
	            			    //System.out.println(out);      
	            			if ((out>=-128) && (out <=127)){data[x]=(byte)out;}
        	                else{
        	                  if(out < -128){data[x]=-128;}
        	                     else{data[x]=127;};};
	            			 }
        	                     data[0]=(byte)(y-128); //first byte stores the line number from -128 to 128
 	            	            
 	            	            // send Stream to Clients
 	            	            try{source.write( data,0, 256);}catch(IOException e){}
 	            				try{Thread.sleep(200);}catch (InterruptedException e){}
        	                     
	            			 
	            			                         
	            			 	               }
	            		 
	            			 
	            			 
	            			 
	            		 
                           	       }
	            	 
	            	  
	            	       
	            	        
	            	             
	            	            
	            	             
	            	                
	         
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
      	                     }
	
                          }}
