package nano.compute.simulation;

import java.io.IOException;

import nano.compute.Simulator;

public class DemoSimulator8 extends Simulator {

	final int ss = 256;
	double[][] mydata;
	boolean running=true;
	double[] pointsx=new double[12];
	double[] pointsy=new double[12];
	double s=20;//double z=20;
	
	public DemoSimulator8() {
		super();
		

		init();

	}

	private void init() {

		addDouble("a", 50);
		addDouble("b", 10);
        addDouble("scansize",1);
        addDouble("p_regler",2);
        addDouble("i_regler",1);
        
     
        
 	    
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
    	
    	
    	// Create unit cell with 12 adatoms length scale in A
    	for (int i=0;i<12;i++){
    		pointsx[i]=pointsx[i]*4;
    		pointsy[i]=pointsy[i]*4;
    		
    	}
        
        
	}

	
	
	// create 7x7 pattern
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

	
	
	
	
	
	
	
	
	
	
	
	
	private double gauss(double x, double y,double x0, double y0)
	{
		
		double out;
		
		out=gd("a")*Math.exp((Math.pow(x-x0,2)+Math.pow(y-y0,2))/(gd("b")*gd("b")*(-1)));
		return out;
		
	}
	
	/*private double calcul(double x, double y) {
	 	double c=128;
		double sol;
		sol = gd("a") * Math.sin(x * Math.PI / (256 * c)) + gd("b")
				* Math.sin(y * Math.PI / (256 * c));
		return sol;
	}*/
	
	private double tunnel(double s){
		double It;
		if (s>-0.3) {
		It=6*Math.exp(-2.2*s);
		}
		else{ It=11.5;
		}
		return It;
	}	
	
	/*private double Itot(double x, double y, double s, double x0, double y0){
		double Itot2;
		double[][] prov=new double [2][12];
		//out=0;	 
		for(int i=0;i<12;i++)
		{
		prov[i]=modulo7x7(pointsx[i],pointsy[i],x,y,129.32);
			
		out+=(int)gauss(x,y,prov[0],prov[1]);	 
		Itot2=tunnel(s-gauss(x,y,pointsx[i],pointsy[i]));
		} 
		
		
		return Itot2;
	}
	*/
	private double Itot(double x, double y, double z_tip){
	
		double[] prov=new double [2];
		//Abstand s=z_tip-gauss_total(x,y);	 
		double gauss_total=0;
		for(int i=0;i<12;i++)
		{
		prov=modulo7x7(pointsx[i],pointsy[i],x,y,129.32);
			
		gauss_total+=gauss(x,y,prov[0],prov[1]);	 
		
		} 
		s=z_tip-gauss_total;
		
		return tunnel(s);
	}
	
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
	
	private byte[] regler(double y){
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
	            			 y=yi;//*gd("scansize")*10/256;
	            			 //for(int xi=0;xi<ss;xi++){}
	            			   //deltax=deltax*gd("scansize")*10/256;
	            			   
	            			   /*out=0;	 
	            			   for(int i=0;i<12;i++)
	            			   {
	            				prov=modulo7x7(pointsx[i],pointsy[i],x,y,129.32);
	            				
	            			    out+=(int)gauss(x,y,prov[0],prov[1]);	 
	            			    }         
	            			
	            			
	            			if ((out>=-128) && (out <=127)){data[xi]=(byte)out;}
        	                else{
        	                  if(out < -128){data[xi]=-128;}
        	                     else{data[xi]=127;
        	                     };};
	            			 } */
	            			 
	            			     data=regler(y);
	            			    
        	                    data[0]=(byte)(yi-128); // first byte stores the
														// line number from -128
														// to 128
 	            	            
 	            	            // send Stream to Clients
 	            	            try{source.write( data,0, 256);}catch(IOException e){}
 	            				try{Thread.sleep(200);}catch (InterruptedException e){}
        	                     
	            			 
	            			                         
	            			 	               }
	            		 
	            			 
	            			 
	            			 
	            		 
                           	       }
	            	 
	            	  
	            	       
	            	        
	            	             
	            	            
	            	             
	            	                
	         
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
	            	 
      	                     }
	
                          }}
