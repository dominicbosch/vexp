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
import nano.compute.util.Ag;

/**
 *
 * @author Martin Guggisberg
 * @version 11. Nov. 2005 
 */

public class DemoSimulator5 extends Simulator {

	private static boolean running = true;

	private static final int ss = 256; // scan size

	private double[][] my_Ag_surface;

	public DemoSimulator5() {
		super();
		init();
	}

	public void init() {
		addDouble("p", 40.0);
		addDouble("i", 20.0);
		addDouble("c", 60.0);

		addCommand("startdemo", new startCommandExecutor());
		addCommand("stopdemo", new stopCommandExecutor());

		Ag ag_surface = new Ag();
		ag_surface.setArraySize(256);
		ag_surface.setScanSize(50); //in A
		ag_surface.setAtomRadius(2.5); // 1 / Atom Radius
		ag_surface.setAtomWidth(1.4); //in nm
		ag_surface.setAgAtomSize(28.0); //in A
		ag_surface.init();

		my_Ag_surface = ag_surface.getSurface();

	}

	private double calcIt(int x, int y, double z) {
		// I0* exp (-2*chi*s) I0 6, chi 1.1
		// IT( z-topo (x,y))

		double s = z - my_Ag_surface[x][y];
		System.out.println("s:"+s+"    map:"+my_Ag_surface[x][y]+"  x:"+x+"   y:"+y); 
		double tmp = 0;
		
		/* CUT OFF
		if (s > -0.1)
			tmp = 6 * Math.exp(-2 * 1.1 * s);
		else
			tmp = 7.5;
			
			*/
		
		tmp = 6 * Math.exp(-2 * 1.1 * s);
		
		return tmp;
	}

	public void start_simulation() {
		running = true;
	}

	public void stop_simulation() {
		running = false;
	}

	class startCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			start_simulation();
		}
	}

	class stopCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			stop_simulation();
		}
	}

	public void run() {
		byte[] data = new byte[ss];
		double int_z = 0, z_ist;
		double i_ist, i_soll, di, z_, z_offset;
		double z_zoom=100;
		while (true) {
			while (running) {
				z_ = 20;
				i_soll = 6;
				i_ist = 0;
				di = i_soll;
				boolean approached = false;
				for (int i = 0; i < 200; i++) {
					i_ist = calcIt(0, 0, z_);
					di = i_ist - i_soll;
					if (Math.abs(di) < 1) {
						approached = true;
						break;
					}
					z_ += di * gd("p") / 100;
					System.out.println("di:" + di + "   z_:" + z_ + "    i_ist"
							+ i_ist);
				}

				
				/*
				 I_ist := Itot2(x,z);
				 scanline[j] := eval(z);
				 I_diff:= I_ist - I_soll;
				 isum := isum +ireg*I_diff*delta_t;
				 delta_z := I_diff*p+isum;
				 z := z + delta_z;   
				 x := x + dx
				 */
				if (approached) {
					z_offset = z_;
					for (int yi = 0; yi < ss; yi++) { // y direction
						System.out.println("di:" + di + "   z_:" + z_
								+ "    i_ist" + i_ist);
						for (int xi = 0; xi < ss; xi++) { // x direction   
							i_ist = calcIt(xi, yi, z_);
							di = i_ist - i_soll;
							int_z += di * gd("i") / 1000;
							z_ += di * gd("p") / 100 + int_z;

							z_ist = (z_-z_offset)* z_zoom;
							//System.out.println(tmp+" a:"+gd("a")+" b:"+gd("b")+" c:"+gd("c"));
							if ((z_ist >= -128) && (z_ist <= 127)) {
								data[xi] = (byte) z_ist;
							} else {
								if (z_ist < -128) {
									data[xi] = -128;
								} else {
									data[xi] = 127;
								}
								;
							}
						
						} //

						data[0] = (byte) (yi - ss / 2); //first byte stores the line number from -128 to 128

						// send Stream to Clients
						try {
							source.write(data, 0, 256);
							source.flush();

						} catch (IOException e) {
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
				}

			} //END RUNNING
		} //END WHILE(TRUE)
	} //END RUN

}
