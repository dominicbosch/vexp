package nano.compute.simulation;

import java.io.IOException;
import java.util.*;

import nano.compute.CommandExecutor;
import nano.compute.Simulator;
import nano.compute.simulation.DemoSimulator9.startCommandExecutor;
import nano.compute.simulation.DemoSimulator9.stopCommandExecutor;
import nano.compute.util.Ag;

public class DemoSimulator10 extends Simulator {

	final int ss = 256;
	double[][] mydata;
	boolean running=true;
	static double[] pointsx=new double[12];
	static double[] pointsy=new double[12];
	double s=20;//double z=20;
	
	public DemoSimulator10() {
		super();
		

		init();

	}

	private void init() {

		addDouble("a", 20);
		addDouble("b", 25);
        //addDouble("E_min",1);
        
        
     
        
 	    
 	    pointsx[1]=24;
        pointsy[1]=0;
        pointsx[2]=48;
    	pointsy[2]=0;	      
    	pointsx[3]=32;
    	pointsy[3]=0;
    	pointsx[4]=8;
    	pointsy[4]=0;
        pointsx[5]=24;
    	pointsy[5]=-5.3*Math.sqrt(3);
    	pointsx[6]=16;
        pointsy[6]=2.6*Math.sqrt(3);
        pointsx[7]=40;
    	pointsy[7]=-2.6*Math.sqrt(3);
    	pointsx[8]=32;
    	pointsy[8]=5.3*Math.sqrt(3);
    	pointsx[9]=24;
    	pointsy[9]=5.3*Math.sqrt(3);
    	pointsx[10]=16;
    	pointsy[10]=-2.6*Math.sqrt(3);
    	pointsx[11]=40;
    	pointsy[11]=2.6*Math.sqrt(3);
    	pointsx[0]=32;
    	pointsy[0]=-5.3*Math.sqrt(3);
    	
    	
    	
    	for (int i=0;i<12;i++){
    		pointsx[i]=pointsx[i]*4;
    		pointsy[i]=pointsy[i]*4;
    		
    	}
        
        
	}

	
	
	
	public static double[] modulo7x7(double oldx, double oldy,double tipx,double tipy, double latticeconst){

		  oldx=oldx/latticeconst;  
		  oldy=oldy/latticeconst;  
		  tipx=tipx/latticeconst;  
		  tipy=tipy/latticeconst;  

		  double rel_x=oldx-tipx+0.8660254;
		  double rel_y=oldy-tipy;
		  double w3h=Math.sqrt(3)/2;
		  double w3d=Math.sqrt(3)/3;

		  double[] res=new double[2];
		  double[][] Rotation=new double[2][2];
		  double[][] Back = new double[2][2];

		  Rotation[0][0]=w3d;
		  Rotation[0][1]=-1.0;
		  Rotation[1][0]=w3d;
		  Rotation[1][1]=1.0;

		  Back[0][0]=w3h;
		  Back[0][1]=w3h;
		  Back[1][0]=-0.5;
		  Back[1][1]=0.5;

		  double quadx=Rotation[0][0]*rel_x+Rotation[0][1]*rel_y;
		  double quady=Rotation[1][0]*rel_x+Rotation[1][1]*rel_y;
		  double res_x=mod(quadx);
		  double res_y=mod(quady);  
		  res[0]=Back[0][0]*res_x+Back[0][1]*res_y+tipx-0.8660254;
		  res[1]=Back[1][0]*res_x+Back[1][1]*res_y+tipy;

		  res[0]=res[0]*latticeconst;  
		  res[1]=res[1]*latticeconst;  

		  return res;
		}

		public static double mod(double x){

		   return x-Math.floor(x);
		}

	
	
	
	
	
	
	
	
	
	
	
	
	/*private double gauss(double x, double y,double x0, double y0)
	{
		
		double out;
		
		out=gd("a")*Math.exp((Math.pow(x-x0,2)+Math.pow(y-y0,2))/(gd("b")*gd("b")*(-1)));
		return out;
		
	}
	*/
	
	
	
	
	/*
	private double approach(){
		double z_start=20;
		double x_start=0;
		double y_start=0;
		double x_end=256;
		double y_end=256;
		int iteration=1000;
		double dz=-z_start/iteration;
		double I_soll=5;
		int approached=0;
		double z_approached=0;
		double z=z_start;
		double dI=0.15;
		double I_ist=0;
		double[][] approach=new double[2][iteration];
		
		
		int i=0;
		while((i < iteration)&&(Math.abs(I_ist-I_soll)>=dI))
		{
			I_ist=Itot(x_start,y_start,z);
			approach[0][i]=I_ist;
			approach[1][i]=z;			
			z=z+dz;
			i++;
		}
		
			z_approached=z;
			return z_approached;
				
	}
	*/
	/*private byte[] regler(double y){
		double z=approach();
		double x_start=0;
		double x_end=ss;
		double I_soll=5;
		double isum=0;double I_ist=0;double I_diff=0;double I_difftot=0;
		int dx=1; //dx=dx*gd("scansize")*10/256;;
		byte[] scanline= new byte[ss];
		double x=x_start; 
		double newz=0;
		
		for (int j=0; j<ss; j++){
			scanline[j]=(byte)z;
			x=x+dx;
			I_ist=Itot(x,y,z);
			I_diff=I_ist-I_soll;
			I_difftot=I_difftot+I_diff;
			if (Math.abs(I_diff)>1){
				newz=gd("p_regler")/10*I_diff+gd("i_regler")/10*I_difftot;
				z=z+newz;
			}
			
		}
		return scanline;
		
	}
	*/

	
	
	
	public static double getDf(double Amp, double x, double y, double z0){
		  int precision=50;
		  double mittelwert=0.0;
		  double constant=21.0;
		  double phase=0.0;
		  double z;
		  for (int t=0;t<precision;t++){
		    phase=t*2.0*Math.PI/precision;
		    z=Amp*Math.sin(phase)+z0;
		    mittelwert+=z*Feld(x,y,z);
		  }
		  mittelwert=mittelwert/precision;
		  return constant*mittelwert/(Amp*Amp);
		}

	
	
	
	
	
// Kraft auf tip an Position (x,y,z) berechnen

	public static double Feld(double x, double y, double z){
	double kraft=0;
	double[] prov=new double[2];
// Hier alle z-Anteile der Kraefte aufsummieren
     
	for (int i=0;i<12;i++)
	{
		prov=modulo7x7(pointsx[i],pointsy[i],x,y,129.32);
		
		
		kraft=kraft+giveJones(prov[0],prov[1],-75,x,y,z);
		
	}
	
	 return kraft;
	}

	
	
	public static double giveJones(double x1, double y1, double z1, double x2,double y2, double z2)
	{
		double r;
		
		r=Math.sqrt(Math.pow(z2-z1,2)*Math.pow(x2-x1,2)*Math.pow(y2-y1,2));
		
		double r0=15.0; // Gleichgewichtsabstand
		double E_min=5; // Ionisierungsenergie
		double r0r3=(r0*r0*r0)/(r*r*r);
		double r0r6=r0r3*r0r3;
		double Fz=-12.0*E_min*r0r6/r*(1.0-r0r6);
		
		return Fz;
	}
	

	/*public static double giveForce(double z)
	{
		double n1=0.5, n2=0.5, A3=10,R=1;
		double Fconst=Math.PI*Math.PI*n1*n2*A3*R/6.0;
		double Fz2=Fconst/(z*z);
		
		return Fz2;
		
	}
		
		
	public static void dummy2(){

// ********************************
// Kraft auf entfernte Atome

	double n1=0, n2=0, A3=0,R=0,z=1;
	double Fconst=Math.PI*Math.PI*n1*n2*A3*R/6.0;
	double Fz2=Fconst/(z*z);
	}
	*/

	
	
	
	
	
	
	
	
	
		

public void run(){
        	
        	byte[] data = new byte[ss];
     	    double tmp;int out;
     	    double x,y,deltax=1;
	        boolean running=true; 
	        double[] prov=new double[2];
     	    
	        	while(true){
		
	            	 while(running){
	            		 
	            		 for(int yi=0;yi<ss;yi++)
	            		 {
	            			 //y=yi;//*gd("scansize")*10/256;
	            			 for(int xi=0;xi<ss;xi++){
	            		
	            		   out=0;		 
	            		   out=(int)(getDf(gd("a")/10,xi,yi,gd("b")/10)); 
	            				 
	            				 
	            				 
	            			 
	            			 
	            			 
	            			 
	            			 //deltax=deltax*gd("scansize")*10/256;
	            			   
	            			  /* out=0;	 
	            			   for(int i=0;i<12;i++)
	            			   {
	            				
	            				   
	            				 //prov=modulo7x7(pointsx[i],pointsy[i],x,y,129.32);
	            				//out+=(int)gauss(x,y,prov[0],prov[1]);	 
	            			    }*/         
	            			
	            			
	            			if ((out>=-128) && (out <=127)){data[xi]=(byte)out;}
        	                else{
        	                  if(out < -128){data[xi]=-128;}
        	                     else{data[xi]=127;
        	                     };};
	            			 }
	            			 
	            			 
	            			     //data=regler(y);
	            			    
        	                    data[0]=(byte)(yi-128); // first byte stores the
														// line number from -128
														// to 128
 	            	            
 	            	            // send Stream to Clients
 	            	            try{source.write( data,0, 256);}catch(IOException e){}
 	            				try{Thread.sleep(100);}catch (InterruptedException e){}
        	                     
	            			 
	            			                         
	            			 	               }
	            		 
	            			 
	            			 
	            			 
	            		 
                           	       }
	            	 
	            	  
	            	       
	            	        
	            	             
	            	            
	            	             
	            	                
	         
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
      	                     }
	
                          }}
