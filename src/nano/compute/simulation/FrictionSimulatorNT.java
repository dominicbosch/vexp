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
 * @author Tibor Gyalog
 * @version 1.1 15.08.2001 (1.1: zeilen 51-80 hinzugefuegt)
 */

public class FrictionSimulatorNT extends Simulator {
	private static double gx = 0, gy, factor, betrag_g, x = 0.1, y = 0.4,
			X = 0, Y; 
	
	// 1 pixel should correspond to 10nN with gain=1
	// for the KBr Lattice with l=0.8 nm
	final private static double k_factor=1/3.375;

	private static double xforce;

	//public static double scales=20, offset=0, v1=0.6;
	public static double k = 6.28 / 0.46;

	public static double alpha = 0.8;

	private static double rparallel_x = Math.cos(alpha);

	private static double rparallel_y = Math.sin(alpha);

	private static double rsenk_x = -Math.sin(alpha);

	private static double rsenk_y = Math.cos(alpha);

	private static boolean reset = true;

	public void setAlpha(double newangle) {
		////System.out.println("alpha="+newangle);
		rparallel_x = Math.cos(newangle);
		rparallel_y = Math.sin(newangle);
		rsenk_x = -Math.sin(newangle);
		rsenk_y = Math.cos(newangle);
	}

	public FrictionSimulatorNT() {
		super();
		init();
	}

	public void init() {
		addDouble("springconstant", 339.0);
		addDouble("scansize", 8.0);
		addDouble("v1", 0.6);
		addDouble("scales", 5);
		addInt("time", 70);
		addInt("iteration", 15);
		addDouble("noise", 0.2);
		addDouble("offset", 0);
		addInt("channel", 3);
		addCommand("setAlpha", new alphaCommandExecutor());
		addCommand("withdraw", new withdrawCommandExecutor());
		addCommand("approach", new approachCommandExecutor());
	}

	

	public void withdraw() {
		reset = true;
	}

	public void approach() {
		if (reset == true) {reset = false;} 
		else {
			reset = true;
		}
	}

	class alphaCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			double newangle = Double.parseDouble((String) tags.get("value"));
			setAlpha(newangle);
		}
	}

	class withdrawCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			withdraw();
		}
	}

	class approachCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			approach();
		}
	}

	double rnd() {
		return gd("noise") * (Math.random() - 0.5);
	}

	public void run() {
		x = 0;
		y = 0;
		Thread myThread = Thread.currentThread();
//		System.out.println("Thread start myname:"+myThread.getName());
		byte[] b_l = new byte[256];
		byte[] b_r = new byte[256];
		byte[] b_null = new byte[256];
		while (SimulatorThread == myThread) {
			if (reset!=true) {
				

				for (int yi = 0; yi < 256; yi++) { //Abwaerts scannen
					
					//Stop Sim
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){break;}

					for (int xi = 0; xi < 256; xi++) { //Nach rechts scannen.
						X = gd("scansize")
								/ 256
								* (xi * rparallel_x + yi
										* rsenk_x);
						Y = gd("scansize")
								/ 256
								* (xi * rparallel_y + yi
										* rsenk_y);
						for (int i = 0; i < gi("iteration"); i++) {
							gx = k * Math.sin(k * x)
									* (1 + gd("v1") * Math.cos(k * y))
									- gd("springconstant")*k_factor * (x - X) + rnd();
							gy = k * Math.sin(k * y)
									* (1 + gd("v1") * Math.cos(k * x))
									- gd("springconstant")*k_factor * (y - Y) + rnd();
							betrag_g = Math.sqrt(gx * gx + gy * gy);
							factor = gd("scansize") / (250 * betrag_g);
							x += factor * gx;
							y += factor * gy;
						}
						xforce = gd("offset")
								+ gd("scales")
								* (gd("springconstant")*k_factor * ((x - X)
										* rparallel_x + (y - Y) * rparallel_y));

						if ((xforce >= -128) && (xforce <= 127)) {
							b_r[xi] = (byte) xforce;
						} else {
							if (xforce < -128) {
								b_r[xi] = -128;
							} else {
								b_r[xi] = 127;
							}
							;
						}
						;

					} //fertig nach rechts scannen
					
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){reset=true;break;}
					try {
						Thread.sleep(gi("time"));
					} catch (InterruptedException e) {
					}
					for (int xi = 255; xi >= 0; xi--) { //Nach links scannen.
						X = gd("scansize")
								/ 256
								* (xi * rparallel_x + yi
										* rsenk_x);
						Y = gd("scansize")
								/ 256
								* (xi * rparallel_y + yi
										* rsenk_y);
						for (int i = 0; i < gi("iteration"); i++) {
							gx = k * Math.sin(k * x)
									* (1 + gd("v1") * Math.cos(k * y))
									- gd("springconstant") * k_factor* (x - X) + rnd();
							gy = k * Math.sin(k * y)
									* (1 + gd("v1") * Math.cos(k * x))
									- gd("springconstant")*k_factor * (y - Y) + rnd();
							betrag_g = Math.sqrt(gx * gx + gy * gy);
							factor = gd("scansize") / (250 * betrag_g);
							x += factor * gx;
							y += factor * gy;
						}
						xforce = gd("offset")
								+ gd("scales")
								* (gd("springconstant") *k_factor* ((x - X)
										* rparallel_x + (y - Y) * rparallel_y));

						if ((xforce >= -128) && (xforce <= 127)) {
							b_l[xi] = (byte) xforce;
						} else {
							if (xforce < -128) {
								b_l[xi] = -128;
							} else {
								b_l[xi] = 127;
							}
							;
						}
						;

					} //fertig nach links scannen
					

					b_l[0] = (byte) (yi - 128); //Zeilennummer notieren
					b_r[0] = (byte) (yi - 128); //Zeilennummer notieren
					b_null[0] = (byte) (yi - 128); //Zeilennummer notieren
					if (gi("channel") == 3) {
						try {
							source.write(b_r, 0, 256);
						} catch (IOException e) {
						}
					} else {
						if (gi("channel") == 5) {
							try {
								source.write(b_l, 0, 256);
							} catch (IOException e) {
							}
						} else {
							try {
								source.write(b_null, 0, 256);
							} catch (IOException e) {
							}
						}
					}
					try {
						myThread = Thread.currentThread();
						if (SimulatorThread != myThread){reset=true;break;}
						Thread.sleep(gi("time"));
					} catch (InterruptedException e) {
					}

					if (reset==true) {
						break;
					}
				}//Abwaerts scannen END

				for (int yi = 255; yi >= 0; yi--) {//nach oben scannen
					
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){reset=true;break;}

					for (int xi = 0; xi < 256; xi++) { //Nach rechts scannen.
						X = gd("scansize")
								/ 256
								* (xi * rparallel_x + yi
										* rsenk_x);
						Y = gd("scansize")
								/ 256
								* (xi * rparallel_y + yi
										* rsenk_y);
						for (int i = 0; i < gi("iteration"); i++) {
							gx = k * Math.sin(k * x)
									* (1 + gd("v1") * Math.cos(k * y))
									- gd("springconstant")*k_factor * (x - X) + rnd();
							gy = k * Math.sin(k * y)
									* (1 + gd("v1") * Math.cos(k * x))
									- gd("springconstant")*k_factor * (y - Y) + rnd();
							betrag_g = Math.sqrt(gx * gx + gy * gy);
							factor = gd("scansize") / (250 * betrag_g);
							x += factor * gx;
							y += factor * gy;
						}
						xforce = gd("offset")
								+ gd("scales")
								* (gd("springconstant") *k_factor* ((x - X)
										* rparallel_x + (y - Y) * rparallel_y));

						if ((xforce >= -128) && (xforce <= 127)) {
							b_r[xi] = (byte) xforce;
						} else {
							if (xforce < -128) {
								b_r[xi] = -128;
							} else {
								b_r[xi] = 127;
							}
							;
						}
						;

					} //fertig nach rechts scannen
					
					
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){reset=true;break;}
					try {
						Thread.sleep(gi("time"));
					} catch (InterruptedException e) {
					}
					
					
					
					for (int xi = 255; xi >= 0; xi--) { //Nach links scannen.
						X = gd("scansize")
								/ 256
								* (xi * rparallel_x + yi
										* rsenk_x);
						Y = gd("scansize")
								/ 256
								* (xi * rparallel_y + yi
										* rsenk_y);
						for (int i = 0; i < gi("iteration"); i++) {
							gx = k * Math.sin(k * x)
									* (1 + gd("v1") * Math.cos(k * y))
									- gd("springconstant")*k_factor * (x - X) + rnd();
							gy = k * Math.sin(k * y)
									* (1 + gd("v1") * Math.cos(k * x))
									- gd("springconstant") *k_factor* (y - Y) + rnd();
							betrag_g = Math.sqrt(gx * gx + gy * gy);
							factor = gd("scansize") / (250 * betrag_g);
							x += factor * gx;
							y += factor * gy;
						}
						xforce = gd("offset")
								+ gd("scales")
								* (gd("springconstant") *k_factor* ((x - X)
										* rparallel_x + (y - Y) * rparallel_y));

						if ((xforce >= -128) && (xforce <= 127)) {
							b_l[xi] = (byte) xforce;
						} else {
							if (xforce < -128) {
								b_l[xi] = -128;
							} else {
								b_l[xi] = 127;
							}
							;
						}
						;

					} //fertig nach links scannen

					b_l[0] = (byte) (yi - 128); //Zeilennummer notieren
					b_r[0] = (byte) (yi - 128); //Zeilennummer notieren
					b_null[0] = (byte) (yi - 128); //Zeilennummer notieren
					if (gi("channel") == 3) {
						try {
							source.write(b_r, 0, 256);
						} catch (IOException e) {
						}
					} else {
						if (gi("channel") == 5) {
							try {
								source.write(b_l, 0, 256);
							} catch (IOException e) {
							}
						} else {
							try {
								source.write(b_null, 0, 256);
							} catch (IOException e) {
							}
						}
					}
					try {
						myThread = Thread.currentThread();
						if (SimulatorThread != myThread){reset=true;break;}
						Thread.sleep(gi("time"));
					} catch (InterruptedException e) {
					}

					if (reset==true) {
						break;
					}
				}
			} else {
				x = 0;
				y = 0;
				try {
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){break;}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} //Thread(TRUE)
//		System.out.println("Thread Stop myname:"+myThread.getName());
//		System.out.println("Thread Simulator Friction stopped");
	} //END RUN

}