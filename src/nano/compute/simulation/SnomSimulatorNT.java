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

import java.io.*;
import nano.compute.*;
import JSci.maths.*;
import java.awt.*;
import java.util.*;

public class SnomSimulatorNT extends Simulator {

	final int width = 100; // Grundseitenlänge, enspricht halber
							// Feldmatrixseitenlänge

	final int anzahl = 70; // Anzahl Dipole

	// double faktor=0; // initialisierung des FarbNormierungsFaktors
	// int value; // Rechenwert.
	int qz = 0; // Vertikaler Abstand von der Fokussierebene

	final int n = 20; // n*width gibt die Bildbreite, die gerechnet wird

	private static final int ss = 256;

	int initkritRadius = 80;

	int kritRadius;

	double xmaximum = -1, ymaximum = -1, zmaximum = -1; // Durch set methode
														// ersetzen

	int[] newj = new int[anzahl];

	boolean running = true;

	int[] zoomedxposition = new int[anzahl];// Hilfsvariable für Zoom

	int[] zoomedyposition = new int[anzahl];

	int[] shiftedxposition = new int[anzahl];// Hilfsvariablen für
												// Offsetkontrolle

	int[] shiftedyposition = new int[anzahl];

	// int offset=0;
	PhotonGenerator myPhotongenerator;

	Dipole[] myDipole = new Dipole[anzahl];

	EField MyEField;

	Reflected MyReflected;

	// Bildkoordinaten

	int[] r = new int[anzahl];

	int[] q = new int[anzahl];

	public SnomSimulatorNT() {
		super();
		init();

	}

	public void resetexperiment() {
		myPhotongenerator.initGenerator();
		initDipoles(anzahl);
		// approach();
	}

	public void approach() {
		if (running == true) {
			running = false;
		} else {
			running = true;
		}

	}

	public void withdraw() {
		running = false;
	}

	public void init() {

		addDouble("zoom", 8.0);
		addDouble("xoffset", 0);
		addDouble("yoffset", 0);
		addDouble("frequenz", 4);
		// addCommand("withdraw", new WithdrawCommandExecutor());
		addCommand("approach", new ApproachCommandExecutor());
		addCommand("resetexperiment", new resetexperimentCommandExecutor());
		initDipoles(anzahl);
		// System.out.println("Dipoles created");
		MyEField = new EField();
		MyReflected = new Reflected();
		// System.out.println("E-Fields calculated");

	}

	public void initDipoles(int anzahlDipole) {
		for (int i = 0; i < anzahlDipole; i++) {
			myDipole[i] = new Dipole(n);
		}
	}

	// Zoommethode...
	// setzt neue temporäre Koordinaten für die Dipole und bestimmt dei Grösse
	// der aufgemappten e_Felder

	public void zooming(int zoomfaktor, int xoffset, int yoffset) {

		kritRadius = (initkritRadius / zoomfaktor);
		// System.out.print("radius= " + kritRadius);

		for (int i = 0; i < anzahl; i++) {
			shiftedxposition[i] = (myDipole[i].getxposition() - xoffset);
			shiftedyposition[i] = (myDipole[i].getyposition() - yoffset);
			zoomedxposition[i] = (shiftedxposition[i] / zoomfaktor);
			zoomedyposition[i] = (shiftedyposition[i] / zoomfaktor);

			// System.out.println("neues x = " +zoomedxposition[i]);
			// System.out.println("neues y = " +zoomedyposition[i]);
			if (zoomedxposition[i] < kritRadius) {
				r[i] = width - initkritRadius + (width - zoomedxposition[i])
						- 1;
			} else {
				r[i] = width - (initkritRadius);
			}
			if (zoomedyposition[i] < kritRadius) {
				q[i] = width - initkritRadius + (width - zoomedyposition[i])
						- 1;
			} else {
				q[i] = width - (initkritRadius);
			}
		}
	}

	public void run() {

		byte[] data = new byte[ss];
		double tmp;
		double photons;
		myPhotongenerator = new PhotonGenerator(anzahl);

		int zoom = (int) (gd("zoom"));
		/*
		 * int xoffset=50*(int)(gd("xoffset")); int
		 * yoffset=50*(int)(gd("yoffset"));
		 */

		int xoffset = 1 * (int) (gd("xoffset"));
		int yoffset = 1 * (int) (gd("yoffset"));

		int[] kritRadius1 = new int[anzahl];
		int[] kritRadius2 = new int[anzahl];

		Thread myThread = Thread.currentThread();
//		System.out.println("Thread start myname:"+myThread.getName());
		while (SimulatorThread == myThread) {
			while (running) {
				zooming(zoom = (int) (gd("zoom")), 50 * (int) (gd("xoffset")),
						50 * (int) (gd("yoffset")));

				// Einbettung der Bildmatrix in das Gesamtbild mit Mittelpunkt
				// am Dipolort
				// ss scan size (256)
				for (int j = 0; j < (ss); j++) {
					// Parameteränderungen abhören
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread) {
						break;
					}
					if (zoom != (int) (gd("zoom"))
							|| xoffset != (50 * (int) (gd("xoffset")))
							|| yoffset != (50 * (int) (gd("yoffset")))) {
						// Zoomprgramm abrufen und Vergleichsvariablen neu
						// setzen
						zooming(zoom = (int) (gd("zoom")),
								xoffset = (50 * (int) (gd("xoffset"))),
								yoffset = (50 * (int) (gd("yoffset"))));
					}
//					 ss scan size (256)
					for (int i = 0; i < (ss); i++) {
						tmp = 0;
//						 Anzahl Dipole
						for (int a = 0; a < anzahl; a++) {
							kritRadius2[a] = (Math.abs(j
									- (Math.abs(zoomedyposition[a]))));
							kritRadius1[a] = (Math.abs(i
									- (Math.abs(zoomedxposition[a]))));
							if (kritRadius1[a] < (kritRadius)
									&& kritRadius2[a] < (kritRadius)) {
								if (j - newj[a] != 0) {
									(q[a]) += (zoom);
									if (zoomedxposition[a] < kritRadius) {
										r[a] = width - initkritRadius
												+ (width - zoomedxposition[a])
												- 1;
									} else {
										r[a] = width - initkritRadius;
									}
								}
								if (r[a] > 199 || q[a] > 199) {
									tmp += 0;
								} else {
									photons = myPhotongenerator
											.generatePhotons(
													0.003 / gd("frequenz"),
													(MyEField.getRealEx(r[a],
															q[a]) + MyReflected
															.getRealEx(r[a],
																	q[a])) / 2,
													(MyEField.getRealEy(r[a],
															q[a]) + MyReflected
															.getRealEy(r[a],
																	q[a])) * 2,
													(MyEField.getRealEz(r[a],
															q[a]) + MyReflected
															.getRealEz(r[a],
																	q[a])) / 2,
													MyEField.getImaginaryEx(
															r[a], q[a]),
													MyEField.getImaginaryEy(
															r[a], q[a]),
													MyEField.getImaginaryEz(
															r[a], q[a]),
													myDipole[a].getNorm(),
													myDipole[a].getxcomp(),
													myDipole[a].getycomp(),
													myDipole[a].getzcomp(), a);
									// System.out.print(", "+photons);
									tmp += (photons);
									r[a] += (zoom);
								}
								newj[a] = j;
							} else {
								tmp += myPhotongenerator.background(
										0.003 / gd("frequenz"), a);
							}
						}
						// tmp*=10;
						tmp -= 115;

						if ((tmp >= -128) && (tmp <= 127)) {
							data[i] = (byte) tmp;
						} else {
							if (tmp < -128) {
								data[i] = -128;
							} else {
								data[i] = 127;
							}
							;
						}
						;
					}
					// System.out.println(j);
					data[0] = (byte) (j - ss / 2); // first byte stores the
													// line number from -128 to
													// 128

					// send Stream to Clients
					int sleeptime = (int) (100 / (gd("frequenz")));
					try {
						source.write(data, 0, 256);
					} catch (IOException e) {
					}
					try {
						myThread = Thread.currentThread();
						if (SimulatorThread != myThread){
							running=false;
							break;
						}
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
					}
					while (!running) {
						try {
							myThread = Thread.currentThread();
							if (SimulatorThread != myThread){
								running=false;
								break;
							}
							Thread.sleep(900);
						} catch (InterruptedException e) {
						}
					}
				}

			} // END RUNNING
	//		System.out.println("Thread stop myname:"+myThread.getName());
	//		System.out.println("Thread Snom Simulator stopped");
		} // END WHILE(TRUE)

	}

	class resetexperimentCommandExecutor extends CommandExecutor {
		public void execute(Hashtable h) {
			resetexperiment();
		}
	}

	class ApproachCommandExecutor extends CommandExecutor {
		public void execute(Hashtable h) {
			approach();
		}
	}

	class WithdrawCommandExecutor extends CommandExecutor {
		public void execute(Hashtable h) {
			withdraw();
		}
	}
}

class EField {

	int width = 100;

	// double x,y,z,rho,phi;
	// double dx=0.02;
	double MyRealValue2;

	double MyRealValue1;

	double MyRealValue0;

	double MyImaginaryValue2;

	double MyImaginaryValue1;

	double MyImaginaryValue0;

	double xmaximum = -1, ymaximum = -1, zmaximum = -1; // Durch set methode
														// ersetzen

	// Die Funktionswerte von I0,I1,I2
	RealWinkelIntegral2 myRealI2;

	RealWinkelIntegral1 myRealI1;

	RealWinkelIntegral0 myRealI0;

	ImaginaryWinkelIntegral2 myImaginaryI2;

	ImaginaryWinkelIntegral1 myImaginaryI1;

	ImaginaryWinkelIntegral0 myImaginaryI0;

	// Datenwerte der I0,I1 und I2. Die L�nge von 143 entspricht ca der
	// Quadratwurzel von 20000.
	// Da die Besselfunktionen allesamt radialsymmetrisch sind (f(r)), ist
	// dieser Ansatz erlaubt.
	// Das Problem reduziert sich so von 40000 auf 143 zu rechnende Integrale
	// geht aber zu Lasten einer leicht ungenaueren Aufl�sung, die aber
	// angesichts der enorm gesteigerten Effizienz verschmerzbar ist.

	double[] I2Realdata = new double[143];

	double[] I1Realdata = new double[143];

	double[] I0Realdata = new double[143];

	double[] I2Imaginarydata = new double[143];

	double[] I1Imaginarydata = new double[143];

	double[] I0Imaginarydata = new double[143];

	// Integralmatrizen

	double[] rdatax = new double[width * width];

	double[] rdatay = new double[width * width];

	double[] rdataz = new double[width * width];

	double[] idatax = new double[width * width];

	double[] idatay = new double[width * width];

	double[] idataz = new double[width * width];

	double[] rdataDoubx = new double[4 * width * width];

	double[] rdataDouby = new double[4 * width * width];

	double[] rdataDoubz = new double[4 * width * width];

	double[] idataDoubx = new double[4 * width * width];

	double[] idataDouby = new double[4 * width * width];

	double[] idataDoubz = new double[4 * width * width];

	double[][] rdataDoubx2 = new double[2 * width][2 * width];

	double[][] rdataDouby2 = new double[2 * width][2 * width];

	double[][] rdataDoubz2 = new double[2 * width][2 * width];

	double[][] idataDoubx2 = new double[2 * width][2 * width];

	double[][] idataDouby2 = new double[2 * width][2 * width];

	double[][] idataDoubz2 = new double[2 * width][2 * width];

	public EField() {
		init(0);

	}

	public void init(int q) {

		double x, y, z, rho, phi;
		double dx = 0.02;
		if (q > 8) {
			q = 8;
		}
		if (q < -8) {
			q = -8;
		}
		z = (0.03 * q);

		for (int i = 0; i < 143; i++) {
			double d = i;
			myRealI2 = new RealWinkelIntegral2((d / 50.0), z);
			myRealI1 = new RealWinkelIntegral1((d / 50.0), z);
			myRealI0 = new RealWinkelIntegral0((d / 50.0), z);

			myImaginaryI2 = new ImaginaryWinkelIntegral2((d / 50.0), z);
			myImaginaryI1 = new ImaginaryWinkelIntegral1((d / 50.0), z);
			myImaginaryI0 = new ImaginaryWinkelIntegral0((d / 50.0), z);

			MyRealValue2 = NumericalMath.richardson(100, myRealI2,
					0.6632251157578452, 1.0122909661567111);
			MyRealValue1 = NumericalMath.richardson(100, myRealI1,
					0.6632251157578452, 1.0122909661567111);
			MyRealValue0 = NumericalMath.richardson(100, myRealI0,
					0.6632251157578452, 1.0122909661567111);

			MyImaginaryValue2 = NumericalMath.richardson(100, myImaginaryI2,
					0.6632251157578452, 1.0122909661567111);
			MyImaginaryValue1 = NumericalMath.richardson(100, myImaginaryI1,
					0.6632251157578452, 1.0122909661567111);
			MyImaginaryValue0 = NumericalMath.richardson(100, myImaginaryI0,
					0.6632251157578452, 1.0122909661567111);

			I2Realdata[i] = MyRealValue2;
			I1Realdata[i] = MyRealValue1;
			I0Realdata[i] = MyRealValue0;

			I2Imaginarydata[i] = MyRealValue2;
			I1Imaginarydata[i] = MyRealValue1;
			I0Imaginarydata[i] = MyRealValue0;
		}

		for (int yi = 0; yi < width; yi++) {
			y = yi * dx;
			// System.out.print(".");
			for (int xi = 0; xi < width; xi++) {
				x = xi * dx;
				rho = Math.sqrt(x * x + y * y);
				phi = Math.atan2(x, y);

				rdatax[xi + width * yi] = getInterpolateI0Real(rho)
						+ getInterpolateI2Real(rho) * Math.cos(2 * phi);
				idatax[xi + width * yi] = getInterpolateI0Imaginary(rho)
						+ getInterpolateI2Imaginary(rho) * Math.cos(2 * phi);
				rdatay[xi + width * yi] = 2 * getInterpolateI2Real(rho)
						* Math.sin(2 * phi);
				idatay[xi + width * yi] = 2 * getInterpolateI2Imaginary(rho)
						* Math.sin(2 * phi);
				rdataz[xi + width * yi] = -2 * getInterpolateI1Real(rho)
						* Math.cos(phi);
				idataz[xi + width * yi] = -2 * getInterpolateI1Imaginary(rho)
						* Math.cos(phi);

				if (rdatax[xi + width * yi] > xmaximum) {
					xmaximum = rdatax[xi + width * yi];
				}
				if (idatax[xi + width * yi] > xmaximum) {
					xmaximum = idatax[xi + width * yi];
				}
				if (rdatay[xi + width * yi] > ymaximum) {
					ymaximum = rdatay[xi + width * yi];
				}
				if (idatay[xi + width * yi] > ymaximum) {
					ymaximum = idatay[xi + width * yi];
				}
				if (rdataz[xi + width * yi] > zmaximum) {
					zmaximum = rdataz[xi + width * yi];
				}
				if (idataz[xi + width * yi] > zmaximum) {
					zmaximum = idataz[xi + width * yi];
				}

			}
		}

		for (int yi = 0; yi < width; yi++) {// begin Sektoren f�llen
			for (int xi = 0; xi < width; xi++) {
				// x-Feld: Hier werden zwei eindimensionale Arrays mit den
				// errechneten Bildinformationen gef�llt.
				// Der erste Array beinhaltet die reale Komponente des Bildes,
				// der zweite die imagin�re.
				// Wenn man in das Bild ein Koordinatensystem legt, mit Ursprung
				// in der Bildmitte, so entspricht jede Zeile dem F�llen eines
				// Sektores.
				rdataDoubx[2 * width * width + xi + width + 2 * width * yi] = rdatax[xi
						+ width * yi];
				rdataDoubx[2 * width * width + (width - xi) + 2 * width * yi] = rdatax[xi
						+ width * yi];
				rdataDoubx[(width - xi) + 2 * width * (width - yi)] = rdatax[xi
						+ width * yi];
				rdataDoubx[width + xi + 2 * width * (width - yi)] = rdatax[xi
						+ width * yi];

				idataDoubx[2 * width * width + xi + width + 2 * width * yi] = idatax[xi
						+ width * yi];
				idataDoubx[2 * width * width + (width - xi) + 2 * width * yi] = idatax[xi
						+ width * yi];
				idataDoubx[(width - xi) + 2 * width * (width - yi)] = idatax[xi
						+ width * yi];
				idataDoubx[width + xi + 2 * width * (width - yi)] = idatax[xi
						+ width * yi];
				// y-Feld
				rdataDouby[2 * width * width + xi + width + 2 * width * yi] = rdatay[xi
						+ width * yi];
				rdataDouby[2 * width * width + (width - xi) + 2 * width * yi] = rdatay[xi
						+ width * yi];
				rdataDouby[(width - xi) + 2 * width * (width - yi)] = rdatay[xi
						+ width * yi];
				rdataDouby[width + xi + 2 * width * (width - yi)] = rdatay[xi
						+ width * yi];

				idataDouby[2 * width * width + xi + width + 2 * width * yi] = idatay[xi
						+ width * yi];
				idataDouby[2 * width * width + (width - xi) + 2 * width * yi] = idatay[xi
						+ width * yi];
				idataDouby[(width - xi) + 2 * width * (width - yi)] = idatay[xi
						+ width * yi];
				idataDouby[width + xi + 2 * width * (width - yi)] = idatay[xi
						+ width * yi];

				// z-Feld
				rdataDoubz[2 * width * width + xi + width + 2 * width * yi] = rdataz[xi
						+ width * yi];
				rdataDoubz[2 * width * width + (width - xi) + 2 * width * yi] = rdataz[xi
						+ width * yi];
				rdataDoubz[(width - xi) + 2 * width * (width - yi)] = rdataz[xi
						+ width * yi];
				rdataDoubz[width + xi + 2 * width * (width - yi)] = rdataz[xi
						+ width * yi];

				idataDoubz[2 * width * width + xi + width + 2 * width * yi] = idataz[xi
						+ width * yi];
				idataDoubz[2 * width * width + (width - xi) + 2 * width * yi] = idataz[xi
						+ width * yi];
				idataDoubz[(width - xi) + 2 * width * (width - yi)] = idataz[xi
						+ width * yi];
				idataDoubz[width + xi + 2 * width * (width - yi)] = idataz[xi
						+ width * yi];

			}
		}// end Sektoren f�llen

		// Umschreiben in 2d Matrizen

		for (int j = 0; j < 2 * width; j++) {
			for (int i = 0; i < 2 * width; i++) {
				rdataDoubx2[i][j] = rdataDoubx[i + (2 * width * j)];
				idataDoubx2[i][j] = idataDoubx[i + (2 * width * j)];

				rdataDouby2[i][j] = rdataDouby[i + (2 * width * j)];
				idataDouby2[i][j] = idataDouby[i + (2 * width * j)];

				rdataDoubz2[i][j] = rdataDoubz[i + (2 * width * j)];
				idataDoubz2[i][j] = idataDoubz[i + (2 * width * j)];
			}
		}

	}

	public double getRealEx(int x, int y) {
		return rdataDoubx2[x][y];
	}

	public double getImaginaryEx(int x, int y) {
		return idataDoubx2[x][y];
	}

	public double getRealEy(int x, int y) {
		return rdataDouby2[x][y];
	}

	public double getImaginaryEy(int x, int y) {
		return idataDouby2[x][y];
	}

	public double getRealEz(int x, int y) {
		return rdataDoubz2[x][y];
	}

	public double getImaginaryEz(int x, int y) {
		return idataDoubz2[x][y];
	}

	// Methoden, die die Funktionen interpolieren.

	public double getInterpolateI2Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I2Realdata[value];
	}

	public double getInterpolateI2Imaginary(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I2Imaginarydata[value];
	}

	public double getInterpolateI1Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I1Realdata[value];
	}

	public double getInterpolateI1Imaginary(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I1Imaginarydata[value];
	}

	public double getInterpolateI0Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I0Realdata[value];
	}

	public double getInterpolateI0Imaginary(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return I0Imaginarydata[value];
	}

}

class Dipole {

	// int n=40;
	int xposition = 0;

	int yposition = 0;

	double xkomp, ykomp, zkomp;

	public Dipole(int n) {

		xposition = (int) Math.round((Math.random() * (n - 2) * 100) + 100);
		yposition = (int) Math.round((Math.random() * (n - 2) * 100) + 100);

		xkomp = Math.random();
		ykomp = Math.random();
		zkomp = Math.random();
		// System.out.println("X-komponente = " + xkomp);

	}

	public int getxposition() {
		return xposition;
	}

	public int getyposition() {
		return yposition;
	}

	public double getxcomp() {
		return xkomp;
	}

	public double getycomp() {
		return ykomp;
	}

	public double getzcomp() {
		return zkomp;
	}

	public double getNorm() {
		return Math.sqrt(Math.pow(xkomp, 2) + Math.pow(ykomp, 2)
				+ Math.pow(zkomp, 2));
	}

}

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

// Stellt Winkelverteilung I2 bereit.
// Besselfunktion 2ter Ordnung entsteht aus den Besselfunktionen erster und
// nullter Ordnung.
class RealWinkelIntegral2 implements JSci.maths.Mapping {
	double rho, z;

	public RealWinkelIntegral2(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta)) * Math.sin(theta)
				* (1 - Math.cos(theta))
				* besselFirstTwo(rho * 1.3 * 6.28 * Math.sin(theta))
				* Math.cos(1.3 * 6.28 * z * Math.cos(theta));
	}

	public double besselFirstTwo(double x) {
		if (x == 0) {
			return 0.0;
		}
		return 2.0 / x * SpecialMath.besselFirstOne(x)
				- SpecialMath.besselFirstZero(x);
	}
}

// Stellt Winkelverteilung I0 bereit.

class RealWinkelIntegral0 implements JSci.maths.Mapping {
	double rho, z;

	public RealWinkelIntegral0(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta))
				* Math.sin(theta)
				* (1 + Math.cos(theta))
				* SpecialMath.besselFirstZero(rho * 1.3 * 6.28
						* Math.sin(theta))
				* Math.cos(1.3 * 6.28 * z * Math.cos(theta));
	}

}

// Stellt Winkelverteilung I1 bereit.

class RealWinkelIntegral1 implements JSci.maths.Mapping {
	double rho, z;

	public RealWinkelIntegral1(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta))
				* Math.sin(theta)
				* Math.sin(theta)
				* SpecialMath
						.besselFirstOne(rho * 1.3 * 6.28 * Math.sin(theta))
				* Math.cos(1.3 * 6.28 * z * Math.cos(theta));
	}
}

// Stellt imagin�re Winkelverteilung I2 bereit.

class ImaginaryWinkelIntegral2 implements JSci.maths.Mapping {
	double rho, z;

	public ImaginaryWinkelIntegral2(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta)) * Math.sin(theta)
				* (1 - Math.cos(theta))
				* besselFirstTwo(rho * 1.3 * 6.28 * Math.sin(theta))
				* Math.sin(1.3 * 6.28 * z * Math.cos(theta));
	}

	public double besselFirstTwo(double x) {
		if (x == 0) {
			return 0.0;
		}
		return 2.0 / x * SpecialMath.besselFirstOne(x)
				- SpecialMath.besselFirstZero(x);
	}
}

// Stellt imagin�re Winkelverteilung I0 bereit.

class ImaginaryWinkelIntegral0 implements JSci.maths.Mapping {
	double rho, z;

	public ImaginaryWinkelIntegral0(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta))
				* Math.sin(theta)
				* (1 + Math.cos(theta))
				* SpecialMath.besselFirstZero(rho * 1.3 * 6.28
						* Math.sin(theta))
				* Math.sin(1.3 * 6.28 * z * Math.cos(theta));
	}

}

// Stellt imagin�re Winkelverteilung I1 bereit.

class ImaginaryWinkelIntegral1 implements JSci.maths.Mapping {
	double rho, z;

	public ImaginaryWinkelIntegral1(double newrho, double newz) {
		rho = newrho;
		z = newz;
	}

	public double map(double theta) {
		return Math.sqrt(Math.cos(theta))
				* Math.sin(theta)
				* Math.sin(theta)
				* SpecialMath
						.besselFirstOne(rho * 1.3 * 6.28 * Math.sin(theta))
				* Math.sin(1.3 * 6.28 * z * Math.cos(theta));
	}
}

// Klasse, die Anzahl Photonen, die pro Pixel detektiert werden errechnet, bzw
// simuliert.

class PhotonGenerator {

	// int state=1;
	double photons = 0;

	double intensity = 0;

	double k12 = 10000000;

	double k21 = 200000000;

	double k23 = 500000;

	double k31 = 3000;

	double kpr = 0.1;

	int[] status = new int[100];

	int state = 1;

	int MyAnzahl;

	int tripletts = 0;

	int j;

	public PhotonGenerator(int anzahl) {
		MyAnzahl = anzahl;
		initGenerator();
	}

	public void initGenerator() {
		for (int i = 0; i < MyAnzahl; i++) {
			status[i] = 1;
		}
	}

	public double generatePhotons(double scandauer, double rdataDoubx2,
			double rdataDouby2, double rdataDoubz2, double idataDoubx2,
			double idataDouby2, double idataDoubz2, double norm, double dipolx,
			double dipoly, double dipolz, int number) {

		double intensity = ((Math.pow(dipolx / norm * rdataDoubx2 + dipolx
				/ norm * idataDoubx2 + dipoly / norm * rdataDouby2, 2) + Math
				.pow(dipoly / norm * idataDouby2 + dipolz / norm * rdataDoubz2
						+ dipolz / norm * idataDoubz2, 2)));

		double rate = k12 * (intensity / (1 + intensity));
		// System.out.println(intensity);
		double t = 0;
		photons = 0;

		if (status[number] == 0) {
			state = 1;
		} else {
			state = status[number];
		}

		while (t < scandauer) {
			double deltaT = 0;
			tripletts = 0;

			if (state == 4) {
				status[number] = 4;
				t = scandauer;
				return 0;
			}

			// Grundzustand
			else if (state == 1) {
				deltaT = -Math.log(Math.random()) / rate;
				state = 2;
				t += deltaT;
				status[number] = state;
				if (t > scandauer) {
					// System.out.print(photons);
					return photons;
				}

			}

			// Triplettzustand
			else if (state == 3) {
				deltaT = -Math.log(Math.random()) / (k31 + kpr);
				if (Math.random() < (kpr / (k31 + kpr))) {
					state = 4;
				} else {
					state = 1;
				}
				t += deltaT;
				status[number] = state;
				if (t > scandauer) {
					return photons;
				}
			}

			// Angeregter Singlettzustand
			else if (state == 2) {
				deltaT = -Math.log(Math.random()) / (k21 + k23);
				if (Math.random() < (k21 / (k21 + k23))) {
					state = 1;
					if (Math.random() < 0.25) {
						photons += 1;
					}
				} else {
					state = 3;
					tripletts += 1;
				}
				t += deltaT;
				status[number] = state;
				if (t > scandauer) {
					return photons;
				}
			}

		}// end while
		return photons;

	}

	public double background(double scandauer, int number) {
		double t = 0;
		double rate = 250;
		photons = 0;
		if (status[number] == 4) {
			state = 4;
		} else {
			state = 1;
		}
		while (t < scandauer) {

			double deltaT = 0;
			tripletts = 0;

			if (state == 4) {
				status[number] = 4;
				t = scandauer;
				return 0;
			}
			// Grundzustand
			else if (state == 1) {
				deltaT = -Math.log(Math.random()) / rate;
				if (t > scandauer) {
					// System.out.print(photons);
					return photons;
				}
				state = 2;
				t += deltaT;
				status[number] = state;
			}

			// Triplettzustand
			else if (state == 3) {
				deltaT = -Math.log(Math.random()) / (k31 + kpr);
				if (Math.random() < (kpr / (kpr + k31))) {
					state = 4;
				} else {
					state = 1;
				}
				t += deltaT;
				if (t > scandauer) {
					return photons;
				}
				status[number] = state;
			}

			// Angeregter Singlettzustand
			else if (state == 2) {
				deltaT = -Math.log(Math.random()) / (k21 + k23);
				if (Math.random() < (k21 / (k21 + k23))) {
					state = 1;
					if (Math.random() < 0.25) {
						photons += 1;
					}
				} else {
					state = 3;
					tripletts += 1;
				}
				t += deltaT;
				status[number] = state;
				if (t > scandauer) {
					return photons;
				}
			}

		}// end while
		return photons;

	}

}

class Reflected {

	Image img;

	int width = 100;

	double myRealValueJ0, myRealValueJ2, myRealValueJ1;

	double xmaximum = -1, ymaximum = -1, zmaximum = -1;

	int tempxvalue;

	ReflectedJ0 myRealJ0;

	ReflectedJ2 myRealJ2;

	ReflectedJ1 myRealJ1;

	double[] J0realdata = new double[143];

	double[] J2realdata = new double[143];

	double[] J1realdata = new double[143];

	double[] rdatax = new double[width * width];

	double[] rdatay = new double[width * width];

	double[] rdataz = new double[width * width];

	double[] rdataDoubx = new double[4 * width * width];

	double[] rdataDouby = new double[4 * width * width];

	double[] rdataDoubz = new double[4 * width * width];

	double[][] rdataDoubx2 = new double[2 * width][2 * width];

	double[][] rdataDouby2 = new double[2 * width][2 * width];

	double[][] rdataDoubz2 = new double[2 * width][2 * width];

	public Reflected() {
		init();
	}

	public void init() {

		double x, y, rho, phi;
		double dx = 0.02;
		for (int i = 0; i < 143; i++) {
			double d = i;
			myRealJ0 = new ReflectedJ0((d / 50.0), 0);
			myRealJ2 = new ReflectedJ2((d / 50.0), 0);
			myRealJ1 = new ReflectedJ1((d / 50.0), 0);

			myRealValueJ0 = NumericalMath.richardson(100, myRealJ0,
					0.6632251157578452, 1.0122909661567111);
			myRealValueJ2 = NumericalMath.richardson(100, myRealJ2,
					0.6632251157578452, 1.0122909661567111);
			myRealValueJ1 = NumericalMath.richardson(100, myRealJ1,
					0.6632251157578452, 1.0122909661567111);

			J0realdata[i] = myRealValueJ0;
			J2realdata[i] = myRealValueJ2;
			J1realdata[i] = myRealValueJ1;
		}

		for (int yi = 0; yi < width; yi++) {
			y = yi * dx;
			for (int xi = 0; xi < width; xi++) {
				x = xi * dx;
				rho = Math.sqrt(x * x + y * y);
				phi = Math.atan2(x, y);

				rdatax[xi + width * yi] = +getInterpolateJ0Real(rho)
						+ getInterpolateJ2Real(rho) * Math.cos(2 * phi);
				rdatay[xi + width * yi] = -getInterpolateJ2Real(rho)
						* Math.sin(2 * phi);
				rdataz[xi + width * yi] = -2 * getInterpolateJ1Real(rho)
						* Math.cos(phi);

				if (Math.abs(rdataz[xi + width * yi]) > xmaximum) {
					xmaximum = rdataz[xi + width * yi];
				}

			}
		}
		double faktor = 255.0 / xmaximum;
		// System.out.println(xmaximum);

		for (int yi = 0; yi < width; yi++) {// begin Sektoren f�llen
			for (int xi = 0; xi < width; xi++) {

				tempxvalue = (int) (faktor * rdataz[xi + width * yi]);
				// System.out.println(tempxvalue);
				rdataDoubx[2 * width * width + xi + width + 2 * width * yi] = rdatax[xi
						+ width * yi];
				rdataDoubx[2 * width * width + (width - xi) + 2 * width * yi] = rdatax[xi
						+ width * yi];
				rdataDoubx[(width - xi) + 2 * width * (width - yi)] = rdatax[xi
						+ width * yi];
				rdataDoubx[width + xi + 2 * width * (width - yi)] = rdatax[xi
						+ width * yi];

			}
		}

		/*
		 * img = createImage(new MemoryImageSource(2*width, 2*width, rdataDoubx,
		 * 0, 2*width)); repaint();
		 * 
		 */
		for (int j = 0; j < 2 * width; j++) {
			for (int i = 0; i < 2 * width; i++) {
				rdataDoubx2[i][j] = rdataDoubx[i + (2 * width * j)];
				rdataDouby2[i][j] = rdataDouby[i + (2 * width * j)];
				rdataDoubz2[i][j] = rdataDoubz[i + (2 * width * j)];

			}
		}

	}

	public double getRealEx(int x, int y) {
		return rdataDoubx2[x][y];
	}

	public double getRealEy(int x, int y) {
		return rdataDouby2[x][y];
	}

	public double getRealEz(int x, int y) {
		return rdataDoubz2[x][y];
	}

	public double getInterpolateJ0Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return J0realdata[value];
	}

	public double getInterpolateJ2Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return J2realdata[value];
	}

	public double getInterpolateJ1Real(double rho) {
		int value = (int) (Math.rint(rho * 50));
		return J1realdata[value];
	}

}

class ReflectedJ2 implements JSci.maths.Mapping {
	double rho, z;

	CalculateA myA = new CalculateA();

	public ReflectedJ2(double newrho, double newz) {
		rho = newrho;
		z = newz;

	}

	public double map(double theta) {
		return Math.sin(theta)
				* Math.sqrt(Math.cos(theta))
				* (besselFirstTwo(rho * 1.42 * 6.28 * Math.sin(theta)) * (myA
						.getA1(theta, 1.42) + myA.getA2(theta, 1.42)
						* Math.cos(theta)));

	}

	public double besselFirstTwo(double x) {
		if (x == 0) {
			return 0.0;
		}
		return 2.0 / x * SpecialMath.besselFirstOne(x)
				- SpecialMath.besselFirstZero(x);
	}
}

class ReflectedJ0 implements JSci.maths.Mapping {
	double rho, z;

	CalculateA myA = new CalculateA();

	public ReflectedJ0(double newrho, double newz) {
		rho = newrho;
		z = newz;

	}

	public double map(double theta) {
		return Math.sin(theta)
				* Math.sqrt(Math.cos(theta))
				* (SpecialMath.besselFirstZero(rho * 1.42 * 6.28
						* Math.sin(theta)) * (myA.getA1(theta, 1.42) - myA
						.getA2(theta, 1.42)
						* Math.cos(theta)));
	}

}

class ReflectedJ1 implements JSci.maths.Mapping {
	double rho, z;

	CalculateA myA = new CalculateA();

	public ReflectedJ1(double newrho, double newz) {
		rho = newrho;
		z = newz;

	}

	public double map(double theta) {
		return myA.getA2(theta, 1.42)
				* Math.pow(Math.sin(theta), 2)
				* 1.42
				* Math.sqrt(Math.cos(theta))
				* SpecialMath.besselFirstOne(rho * 1.42 * 6.28
						* Math.sin(theta));

	}

}

class CalculateA {

	public double getA1(double theta, double n) {
		return (1 - (n / Math.cos(theta) * Math.sqrt(1 - Math.pow(Math
				.sin(theta)
				/ n, 2))))
				/ (1 + (n / Math.cos(theta) * Math.sqrt(1 - Math.pow(Math
						.sin(theta)
						/ n, 2))));
	}

	public double getA2(double theta, double n) {
		return (Math.pow(n, 2) - (n / Math.cos(theta) * Math.sqrt(1 - Math.pow(
				Math.sin(theta) / n, 2))))
				/ (Math.pow(n, 2) + (n / Math.cos(theta) * Math.sqrt(1 - Math
						.pow(Math.sin(theta) / n, 2))));

	}
}
