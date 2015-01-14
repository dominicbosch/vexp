package nano.compute.simulation;

import java.io.*;
import java.util.Hashtable;

import nano.compute.*;

/**
 *@author     Tibor Gyalog
 *@created    23. Januar 2003
 *@version    1.1 15.08.2001 (1.1: zeilen 51-80 hinzugefügt)
 */

public class ElectroSimulatorNT extends Simulator {

	/// Model zur Berechnung
	Model myElectroModel;

	// Atomistic Radius of the model
	int r = 4;

	//floor rundet ab 
	int xscale = (int) (Math.floor(256 / (2 * r)) + 1);

	int yscale = (int) (Math
			.floor(256 / (2 * r * Math.cos(Math.toRadians(30)))) + 1);

	int maxheightAu;

	int maxheightC;

	//int maxheight;
	//HAtom halbes Atom
	double[][] HAtom = new double[2 * r + 1][2 * r + 1];

	double[][] H = new double[(((2 * xscale + yscale + 2) * r + 1))][(int) ((2
			* yscale * Math.cos(Math.toRadians(30)) + 2)
			* r + 1)];

	int[][] heightC = new int[xscale + 1][yscale + 1];

	//int[][] height = new int[xscale + 1][yscale + 1];
	int[][] heightAu = new int[xscale + 1][yscale + 1];

	double highprolevel = 1.5 * r;

	int highestpoint = (int) (maxheightAu * Math.ceil(highprolevel) + r);

	int zrangefactor = 3;

	//int zrangefactor = (int) (255 / (highprolevel + r));
	//--------------------------Model Variablen---------------------------

	//VARIABLEN
	int criticalsize;

	double potential = 375; //375;

	double step_width = 20;

	double step_height = 1;

	int ViewerCnt;

	//BOOLSCHE VARIABLEN
	boolean playing = false;

	boolean wuerfel = false;

	boolean kugel = true;

	//ARRAYS
	double[] xdaten;

	double[] ydaten;

	//--------------------SurfaceCalculator Variablen---------------------

	//VARIABLEN
	int a;

	int b;

	int c;

	double eta = potential - 575;

	// WAHRSCHEINLICHKEITEN

	double probdescritical;

	final double baseprobonC = 0.005;

	//final double baseprobonC = 0.0005;
	final double baseprobonAu = 0.1;

	//final double baseprobonAu = 0.01;
	final double probproAunb = 0.111111;

	//final double probproAunb = 0.0111111;
	final double probproCnb = probproAunb / 2;

	final double desprobproAunb = probproAunb;

	final double desprobproCnb = desprobproAunb / 2;

	//Arrays
	int[][] edgeC = new int[xscale][yscale];

	int[][] checked = new int[xscale][yscale];

	int[][] clustersize = new int[xscale][yscale];

	int[][] neighboursAuAu = new int[xscale][yscale];

	int[][] neighboursAuC = new int[xscale][yscale];

	int[][] undergroundAu = new int[xscale][yscale];

	int[][] undergroundC = new int[xscale][yscale];

    int[][] neighboursAuAudes = new int[xscale][yscale];

	int[][] neighboursAuCdes = new int[xscale][yscale];

	int[][] undergroundAudes = new int[xscale][yscale];

	int[][] undergroundCdes = new int[xscale][yscale];

	double[][] desprob = new double[xscale][yscale];

	//------------------SizeCalculator Variablen--------------------------

	//KONSTANTEN
	final double sigmaAuEl = 1.26;

	final double sigmaAuC = 5.91;

	final double sigmaCEl = 4.16;

	final double Vatom = 1.69295E-29;

	final int Nmaxclassic = 300;

	final int Nmaxatomistic = 20;

	final int[] PsiAuAuN = { 0, 0, 1, 3, 6, 9, 12, 16, 19, 23, 27, 31, 36, 42,
			45, 49, 53, 57, 62, 68, 72 };

	final int[] PsiAuCN = { 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			3, 3, 3, 3 };

	final int z = 3;

	final double e = 1.602E-19;

	final double PSIAuAu = 9.65E-20;

	final double PSIAuC = PSIAuAu / 2;

	//DATENARRAYS
	double[] DGNFunctionClassic = new double[Nmaxclassic + 1];

	double[] DGNFunctionAtomistic = new double[Nmaxatomistic + 1];

	/**
	 *  Constructor for the ElectroSimulatorNT object
	 */
	public ElectroSimulatorNT() {
		super();
		init();
	}

	/**
	 *  Description of the Method
	 */
	public void init() {
		calcHAtom();

		myElectroModel = new Model();

		//addDouble("step_height",step_height);
		addDouble("step_width", step_width);
		addDouble("time", 20);
		addDouble("reaction", 255);
		addDouble("potential", this.getPotential());
		addCommand("approach", new approachCommandExecutor());

	}

	//    public void calczrangefactor() {
	//        highestpoint = (int) (maxheightAu * Math.ceil(highprolevel) + r);
	//        zrangefactor = (int) (255 / highestpoint);
	//        if (zrangefactor < (255 / (highprolevel + r))) {
	//            zrangefactor = (int) (255 / (highprolevel + r));
	//        }
	//    }

	/**
	 *  Main processing method for the ElectroSimulatorNT object
	 */
	public void run() {
		//x=0;y=0
		double hxy;

		byte[] b_r = new byte[256];
		byte[] b_null = new byte[256];
		
		Thread myThread = Thread.currentThread();
//		System.out.println("Thread start myname:"+myThread.getName());
		while (SimulatorThread == myThread) {
			//calcHFunc(height);
			//breakcondition after 16 Layers of Gold
			if (maxheightAu > 16) {

				myElectroModel.resetAllArrays();
				myElectroModel.startModel();
			}
			;
			for (int yi = 0; yi < 256; yi++) {
				//Abwaerts scannen
				// Modelgeschwindigkeit
				if ((yi % (int) gd("reaction")) == 0) {
					//naechste zeitschritt rechnen
					setPotential(gd("potential"));
					myElectroModel.next_time_step();
				}
				myThread = Thread.currentThread();
				if (SimulatorThread != myThread){
					break;
				}

				for (int xi = 0; xi < 256; xi++) {
					//Nach rechts scannen.

					hxy = (int) H[xi][yi] - 128 + 30; //Offset of the grey scale

					if ((hxy >= -128) && (hxy <= 127)) {
						b_r[xi] = (byte) hxy;
					} else {
						if (hxy < -128) {
							b_r[xi] = -128;
						} else {
							b_r[xi] = 127;
						}

					}

				}
				//fertig nach rechts scannen

				//b_l[0] = (byte) (yi - 128);
				//Zeilennummer notieren
				b_r[0] = (byte) (yi - 128);
				//Zeilennummer notieren
				b_null[0] = (byte) (yi - 128);
				//Zeilennummer notieren
				try {
					source.write(b_r, 0, 256);
				} catch (IOException e) {
				}

				try {
					//ElectroSimulatorNT.sleep(20);
					myThread = Thread.currentThread();
					if (SimulatorThread != myThread){
						break;
					}
					Thread.sleep((int) gd("time"));
				} catch (InterruptedException e) {
				}
			}

		}
//		System.out.println("Thread stop myname:"+myThread.getName());
//		System.out.println("Thread Simulator Electro stopped");
		//END WHILE(TRUE)
	}

	//END RUN
	public void calcHAtom() {
		for (int yi = 0; yi <= 2 * r; yi++) {
			for (int xi = 0; xi <= 2 * r; xi++) {
				if ((Math.pow((xi - r), 2) + Math.pow((yi - r), 2)) <= Math
						.pow(r, 2)) {
					HAtom[xi][yi] = Math.sqrt(Math.pow(r, 2)
							- Math.pow(xi - r, 2) - Math.pow(yi - r, 2));
				} else {
					HAtom[xi][yi] = 0;
				}
				//System.out.println("HAtom[" + xi + "][" + yi + "]: " + HAtom[xi][yi]);
			}
		}
	}

	public void setPotential(double pot) {
		this.potential = pot;
	}

	public double getPotential() {
		return this.potential;
	}

	//    public int getH(int x, int y) {
	//        return (int) (H[x][y]);
	//    }

	public void calcHFunc() {
		resetHFunction();
		for (int ci = 0; ci <= maxheightC; ci++) {
			//System.out.println("maxheightC = " + maxheightC);
			//System.out.println("ci = " + ci);

			for (int bi = 0; bi <= yscale; bi++) {
				for (int ai = 0; ai <= xscale; ai++) {

					if (ci <= heightC[ai][bi]) {
						int x = (2 * ai + bi + ci + 1) * r;
						//System.out.println(ai + " --> " + x);
						int y = (int) ((2 * bi * Math.cos(Math.toRadians(30))
								+ (2.0 / 3.0) * ci
								* Math.cos(Math.toRadians(30)) + 1) * r);
						//System.out.println(bi + " --> " + y);

						int k = 0;
						int l = 0;
						for (int t = (y - r); t <= (y + r); t++) {
							k = 0;
							for (int u = (x - r); u <= (x + r); u++) {
								//System.out.println("t: " + t + "; u: " + u);
								//System.out.println(zrangefactor);
								int[] newCoo = getNewCoordinates(u, t, bi);
								//if (u != newCoo[0]) {
								//System.out.println(u + " u --> " + newCoo[0]);
								//}
								//if (u != newCoo[0]) {
								//System.out.println(t + " t --> " + newCoo[1]);
								//}
								//System.out.println("H[" + u + "][" + t + "]");
								//System.out.println("H[" + k + "][" + l + "]");

								if (HAtom[k][l] != 0) {
									//                                    H[newCoo[0]][newCoo[1]] = 0;
									H[newCoo[0]][newCoo[1]] = zrangefactor
											* (highprolevel * ci + HAtom[k][l]);
								} else {
									H[newCoo[0]][newCoo[1]] += zrangefactor
											* HAtom[k][l];
								}

								//System.out.println("H[" + u + "][" + t + "]: " + H[u][t]);
								k += 1;

							}
							l += 1;

						}
					}
				}
			}
		}

		//END IF
		for (int ci = 1; ci <= maxheightAu; ci++) {
			for (int bi = 0; bi <= yscale; bi++) {
				for (int ai = 0; ai <= xscale; ai++) {

					if (ci <= heightAu[ai][bi]) {
						//System.out.println("Bei " + ai + ", " + bi + ": Goldhoehe " + heightAu[ai][bi]);
						int x = (2 * ai + bi + ci + 1) * r;
						//System.out.println(ai + " --> " + x);
						int y = (int) ((2 * bi * Math.cos(Math.toRadians(30))
								+ (2.0 / 3.0) * ci
								* Math.cos(Math.toRadians(30)) + 1) * r);
						//System.out.println(bi + " --> " + y);
						int k = 0;
						int l = 0;
						for (int t = (y - r); t <= (y + r); t++) {
							k = 0;
							for (int u = (x - r); u <= (x + r); u++) {
								//System.out.println("t: " + t + "; u: " + u);
								int[] newCoo = getNewCoordinates(u, t, bi);

								if (HAtom[k][l] != 0) {
									H[newCoo[0]][newCoo[1]] = 0;
									H[newCoo[0]][newCoo[1]] = zrangefactor
											* (highprolevel * ci + HAtom[k][l]);
								} else {
									H[newCoo[0]][newCoo[1]] += zrangefactor
											* HAtom[k][l];
								}
								//System.out.println("H[" + u + "][" + t + "]: " + H[u][t]);
								k += 1;
							}
							l += 1;
						}
					}
				}
			}
		}
	}

	public void resetHFunction() {
		for (int bi = 0; bi <= yscale; bi++) {
			for (int ai = 0; ai <= xscale; ai++) {
				int x = (2 * ai + bi + 1) * r;
				int y = (int) ((2 * bi * Math.cos(Math.toRadians(30)) + 1) * r);
				int k = 0;
				int l = 0;
				for (int t = (y - r); t <= (y + r); t++) {
					k = 0;
					for (int u = (x - r); u <= (x + r); u++) {
						int[] newCoo = getNewCoordinates(u, t, bi);

						H[newCoo[0]][newCoo[1]] = 0;

						k += 1;
					}
					l += 1;
				}
			}
		}
	}

	public int[] getNewCoordinates(int u, int t, int bi) {
		int[] newCo = new int[2];
		int AnzInX = (int) (Math.floor(255 / (2 * r)));
		int AnzInY = (int) (Math.floor(255 / (2 * r * Math.cos(Math
				.toRadians(30)))));

		if (u > 255 && t <= 255) {
			newCo[0] = u - (AnzInX + 1) * 2 * r;
			newCo[1] = t;
			if (newCo[0] < 0) {
				newCo[0] = 0;
			}
		}

		if (u <= 255 && t > 255) {
			newCo[0] = u - ((bi + 1) * r);
			newCo[1] = (int) (t - (AnzInY + 2) * 2 * r
					* Math.cos(Math.toRadians(30)));
			if (newCo[1] < 0 || newCo[0] < 0) {
				newCo[0] = 0;
				newCo[1] = 0;
			}
		}

		if (u > 255 && t > 255) {
			if (bi % 2 == 0) {
				newCo[0] = (int) (u - (AnzInX - 0.5) * 2 * r);
			} else {
				newCo[0] = (u - AnzInX * 2 * r);
			}
			newCo[1] = (int) (t - (AnzInY + 2) * 2 * r
					* Math.cos(Math.toRadians(30)));
			if (newCo[1] < 0 || newCo[0] < 0) {
				newCo[0] = 0;
				newCo[1] = 0;
			}
		}
		if (u <= 255 && t <= 255) {
			newCo[0] = u;
			newCo[1] = t;
		}
		return newCo;
	}

	class approachCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			step_width = (int) gd("step_width");
			//step_height=(int)gd("step_height");
			myElectroModel.resetAllArrays();
			myElectroModel.startModel();
		}
	}

	class Model {
		//INSTANZEN
		Thread myThread;

		//AllheightData myAuheightData, myCheightData;
		public Model() {
			startModel();
		}

		public void startModel() {
			fillHeightCArray();
		}

		public void next_time_step() {
			criticalsize = calcSize(potential);
			calcSurface();
			//myAuheightData = getAllAuheightData();
			//heightAu = myAuheightData.getheight();
			//maxheightAu = myAuheightData.getmaxheight();
			calcHFunc();
		}

		/**
		 *  Sets the potential attribute of the Model object
		 *
		 *@param  potential  The new potential value
		 *@return            Description of the Return Value
		 */
		/*
		 *  public void setPotential(double potential) {
		 *  this.potential = potential;
		 *  this.criticalsize = mySizeCalculator.calcSize(potential);
		 *  this.xdaten = mySizeCalculator.getKeimXDaten();
		 *  this.ydaten = mySizeCalculator.getKeimYDaten();
		 *  myStatusViewer.updatePotential(potential);
		 *  myStatusViewer.updateCriticalSize(criticalsize);
		 *  myGraphViewer.updateGraphData(xdaten, ydaten);
		 *  }
		 *  public void setWuerfel(boolean wuerfel) {
		 *  this.wuerfel = wuerfel;
		 *  this.criticalsize = mySizeCalculator.calcSize(potential);
		 *  this.xdaten = mySizeCalculator.getKeimXDaten();
		 *  this.ydaten = mySizeCalculator.getKeimYDaten();
		 *  myStatusViewer.updateWuerfel(wuerfel);
		 *  myStatusViewer.updateCriticalSize(criticalsize);
		 *  myGraphViewer.updateGraphData(xdaten, ydaten);
		 *  }
		 *  public void setKugel(boolean kugel) {
		 *  this.kugel = kugel;
		 *  this.criticalsize = mySizeCalculator.calcSize(potential);
		 *  this.xdaten = mySizeCalculator.getKeimXDaten();
		 *  this.ydaten = mySizeCalculator.getKeimYDaten();
		 *  myStatusViewer.updateKugel(kugel);
		 *  myStatusViewer.updateCriticalSize(criticalsize);
		 *  myGraphViewer.updateGraphData(xdaten, ydaten);
		 *  }
		 */
		//------------------------ METHODEN ZUR BERECHNUNG DER KRITISCHEN KEIMGROESSE -------------------------------
		public int calcSize(double potential) {
			eta = potential - 575.0;
			//KLASSISCHER ANSATZ:
			//KUGELMODELL
			if (kugel == true) {
				for (int i = 0; i <= Nmaxclassic; i++) {
					double Nzen = i * z * e * -(eta / 1000);
					double oberflaeche = (3 * i * Vatom)
							/ (Math.pow(((3 * Vatom * i) / (4 * Math.PI)),
									(1.0 / 3.0)));
					DGNFunctionClassic[i] = -Nzen + oberflaeche * sigmaAuEl;
					DGNFunctionClassic[i] = DGNFunctionClassic[i] / e;
					if (i == 0) {
						DGNFunctionClassic[i] = 0;
					}
				}
			}
			//KUBUSMODELL
			if (wuerfel == true) {
				for (int i = 0; i <= Nmaxclassic; i++) {
					double Nzen = i * z * e * -(eta / 1000);
					double oberflaeche = (6 * Vatom * i)
							/ (Math.pow((Vatom * i), (1.0 / 3.0)));
					DGNFunctionClassic[i] = -Nzen + (5.0 / 6.0) * oberflaeche
							* sigmaAuEl + (1.0 / 6.0) * oberflaeche * sigmaAuC
							- (1.0 / 6.0) * oberflaeche * sigmaCEl;
					DGNFunctionClassic[i] = DGNFunctionClassic[i] / e;
					if (i == 0) {
						DGNFunctionClassic[i] = 0;
					}
				}
			}
			//ATOMISTISCHER ANSATZ:
			for (int i = 0; i < Nmaxatomistic; i++) {
				DGNFunctionAtomistic[i] = (-i * z * e * -(eta / 1000) + i * 6
						* PSIAuAu - (PsiAuAuN[i] * PSIAuAu + PsiAuCN[i]
						* PSIAuC))
						/ PSIAuAu;
				if (DGNFunctionAtomistic[i] <= 0) {
					DGNFunctionAtomistic[i] = 0;
				}
			}
			int NMaximumC = 0;
			int NMaximumA = 0;
			double testvariableA = 0;
			double testvariableC = 0;
			for (int i = 0; i < Nmaxclassic; i++) {
				int iResult = ((DGNFunctionClassic[i] <= DGNFunctionClassic[i + 1]) ? (i + 1)
						: i);
				if (iResult == i + 1
						&& testvariableC <= DGNFunctionClassic[iResult]) {
					testvariableC = DGNFunctionClassic[iResult];
					NMaximumC = iResult;
				}
			}
			for (int i = 0; i < Nmaxatomistic; i++) {
				int iResult = ((DGNFunctionAtomistic[i] <= DGNFunctionAtomistic[i + 1]) ? (i + 1)
						: i);
				if (iResult == i + 1
						&& testvariableA <= DGNFunctionAtomistic[iResult]) {
					testvariableA = DGNFunctionAtomistic[iResult];
					NMaximumA = iResult;
				}
			}
			if (NMaximumA > NMaximumC) {
				criticalsize = NMaximumA;
			} else {
				criticalsize = NMaximumC;
			}
			return criticalsize;
		}

		//----------------- METHODEN ZUR BERECHNUNG DES AKTUELLEN OBERFL�CHENZUSTANDES --------------------------
		/**
		 *  Description of the Method
		 */
		public void calcSurface() {
			getAuNeighbours();
			getUnderground();
			throwGold();
			desorpGold();
		}

		/**
		 *  Gets the cheightData attribute of the SurfaceCalculator object
		 *
		 *@return    The cheightData value
		 */
		public int[][] getCheightData() {
			return heightC;
		}

		/**
		 *  Description of the Method
		 */
		public void resetAllArrays() {
			maxheightAu = 0;
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					heightAu[ai][bi] = 0;
					neighboursAuAu[ai][bi] = 0;
					neighboursAuC[ai][bi] = 0;
				}
			}
		}

		/**
		 *  Description of the Method
		 */
		public void fillEdgeCArray() {
			for (int bi = 1; bi <= yscale - 2; bi++) {
				for (int ai = 1; ai <= xscale - 2; ai++) {
					edgeC[ai][bi] = ((heightC[ai][bi + 1] ^ 2)
							+ (heightC[ai][bi - 1] ^ 2)
							+ (heightC[ai + 1][bi] ^ 2)
							+ (heightC[ai - 1][bi] ^ 2)
							+ (heightC[ai + 1][bi - 1] ^ 2) + (heightC[ai - 1][bi + 1] ^ 2))
							- 6 * (heightC[ai][bi] ^ 2);
				}
			}
		}

		/**
		 *  Description of the Method
		 */
		public void fillHeightCArray() {
			for (int b = 0; b < yscale; b++) {
				for (int a = 0; a < xscale; a++) {
					heightC[a][b] = getGraphite(a, b);
					//System.out.println("Hoehe C an der Stelle " + a + ", " + b + ": " + heightC[a][b]);
					//					if (heightC[a][b] >= maxheightC) {
					//						maxheightC = heightC[a][b];
					//					}
				}
			}
			maxheightC = (int) step_height;
		}

		/**
		 *  Gets the graphite attribute of the SurfaceCalculator object
		 *
		 *@param  a  Description of the Parameter
		 *@param  b  Description of the Parameter
		 *@return    The graphite value
		 */
		public int getGraphite(int a, int b) {
			//TODO use const a
			int hoehe = 0;
			if (b >= 5 && b <= step_width + 4) {
				hoehe += step_height;
			}
			/*
			 *  if (a >= 5 && b <= 15) {
			 *  hoehe += 1;
			 *  }
			 *  if (b >= 25 && b <= 100) {
			 *  hoehe += 1;
			 *  }
			 *  if (b >= 45 && b <= 60 && a >= 40 && a <= 55) {
			 *  hoehe += 1;
			 *  }
			 */
			return hoehe;
		}

		/**
		 *  Description of the Method
		 */
		public void throwGold() {
			double test;
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					test = getAuAdsProbs(ai, bi);
					double ran = Math.random();
					if (ran <= test) {
						heightAu[ai][bi] += 1;
						if (heightAu[ai][bi] > maxheightAu) {
							maxheightAu = heightAu[ai][bi];
						}
					}
				}
			}
		}

		/**
		 *  Gets the xKoordinate attribute of the SurfaceCalculator object
		 *
		 *@param  a  Description of the Parameter
		 *@return    The xKoordinate value
		 */
		public int getXKoordinate(int a) {
			int x = a;
			if (a < 0) {
				x = -(Math.abs(a) % xscale) + xscale;
			}
			if (a >= xscale) {
				x = a % xscale;
			}
			return x;
		}

		/**
		 *  Gets the yKoordinate attribute of the SurfaceCalculator object
		 *
		 *@param  b  Description of the Parameter
		 *@return    The yKoordinate value
		 */
		public int getYKoordinate(int b) {
			int y = b;
			if (b < 0) {
				y = -(Math.abs(y) % yscale) + yscale;
			}
			if (b >= yscale) {
				y = b % yscale;
			}
			return y;
		}

		/**
		 *  Description of the Method
		 */
		public void clearClusterSizeArray() {
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					clustersize[ai][bi] = 0;
					checked[ai][bi] = 0;
				}
			}
		}

		/**
		 *  Description of the Method
		 */
		public void desorpGold() {
			double AuDesProb;
			double ran;
			clearClusterSizeArray();
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					if (heightAu[ai][bi] > 0) {
						AuDesProb = getAuDesProbs(ai, bi);
						ran = Math.random();
						if (ran <= AuDesProb) {
							heightAu[ai][bi] -= 1;
						}
					}
				}
			}
		}

		/**
		 *  Gets the clusterSize attribute of the SurfaceCalculator object
		 *
		 *@param  a  Description of the Parameter
		 *@param  b  Description of the Parameter
		 *@return    The clusterSize value
		 */
		public int getClusterSize(int a, int b) {
			int size = 0;
			if (heightAu[a][b] == 0 && checked[a][b] == 0) {
				checked[a][b] = 1;
			}
			if (heightAu[a][b] > 0 && checked[a][b] == 0) {
				size += heightAu[a][b];
				checked[a][b] = 3;
				size += checkNeighbours(a, b);
			}
			for (int i = 0; i < 20; i++) {
				for (int bi = 0; bi < yscale; bi++) {
					for (int ai = 0; ai < xscale; ai++) {
						if (checked[ai][bi] == 2) {
							checked[ai][bi] = 3;
							size += checkNeighbours(ai, bi);
						}
					}
				}
			}
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					if (checked[ai][bi] == 3 && clustersize[ai][bi] == 0) {
						clustersize[ai][bi] = size;
					}
				}
			}
			return clustersize[a][b];
		}

		/**
		 *  Description of the Method
		 *
		 *@param  a  Description of the Parameter
		 *@param  b  Description of the Parameter
		 *@return    Description of the Return Value
		 */
		public int checkNeighbours(int a, int b) {
			int counter = 0;
			int x = a;
			int y = b;
			x += 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			x -= 1;
			y += 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			x -= 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			y -= 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			x += 1;
			y -= 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			x += 1;
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] > 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				counter += heightAu[getXKoordinate(x)][getYKoordinate(y)];
				checked[getXKoordinate(x)][getYKoordinate(y)] = 2;
			}
			if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0
					&& checked[getXKoordinate(x)][getYKoordinate(y)] == 0) {
				checked[getXKoordinate(x)][getYKoordinate(y)] = 1;
			}
			return counter;
		}

		/**
		 *  Gets the auNeighbours attribute of the SurfaceCalculator object
		 */
		public void getAuNeighbours() {
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					neighboursAuAu[ai][bi] = 0;
					neighboursAuC[ai][bi] = 0;
					boolean check = false;
					int x = ai;
					int y = bi;
					int nbAu = 0;
					int nbC = 0;
					x += 1;
					//Ueberpruefen, ob ueberhaupt ein Hoehenunterschied vorhanden ist und ob die betrachtete Position niedriger ist als der Nachbar!
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						//Ueberpruefen, ob man sich auf der Graphit-Terrasse befindet! Wenn ja, dann muss die Goldhoehe des Nachbars hoeher sein als die der
						//betrachteten Position
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						//Ueberpruefen, ob man sich an einer Graphit-Stufe befindet!
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							//Ueberpruefen, ob es ein Graphitnachbar ist!
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							//Ueberpruefen ob es ein Goldnachbar ist!
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					x -= 1;
					y += 1;
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					x -= 1;
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					y -= 1;
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					x += 1;
					y -= 1;
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					x += 1;
					if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
							+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							nbAu += 1;
						}
						if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
								nbC += 1;
								check = true;
							}
							if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
									+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
									&& check == false) {
								nbAu += 1;
							}
							check = false;
						}
					}
					neighboursAuAu[ai][bi] = nbAu;
					neighboursAuC[ai][bi] = nbC;
				}
			}
		}

		/**
		 *  Gets the auNeighbours attribute of the SurfaceCalculator object
		 *
		 *@param  ai  Description of the Parameter
		 *@param  bi  Description of the Parameter
		 *@return     The auNeighbours value
		 */
		public int[] getAuNeighbours(int ai, int bi) {
			boolean check = false;
			int x = ai;
			int y = bi;
			int nbAu = 0;
			int nbC = 0;
			x += 1;
			//Ueberpruefen, ob ueberhaupt ein Hoehenunterschied vorhanden ist und ob die betrachtete Position niedriger ist als der Nachbar!
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				//Ueberpruefen, ob man sich auf der Graphit-Terrasse befindet! Wenn ja, dann muss die Goldhoehe des Nachbars hoeher sein als die der
				//betrachteten Position
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				//Ueberpruefen, ob man sich an einer Graphit-Stufe befindet!
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					//Ueberpruefen, ob es ein Graphitnachbar ist!
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					//Ueberpruefen ob es ein Goldnachbar ist!
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			x -= 1;
			y += 1;
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			x -= 1;
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			y -= 1;
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			x += 1;
			y -= 1;
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			x += 1;
			if (heightAu[ai][bi] + heightC[ai][bi] < heightAu[getXKoordinate(x)][getYKoordinate(y)]
					+ heightC[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					nbAu += 1;
				}
				if (heightC[ai][bi] != heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]) {
						nbC += 1;
						check = true;
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]
							&& check == false) {
						nbAu += 1;
					}
					check = false;
				}
			}
			int[] AuCNb = { nbAu, nbC };
			return AuCNb;
		}

		/**
		 *  Gets the underground attribute of the SurfaceCalculator object
		 */
		public void getUnderground() {
			for (int bi = 0; bi < yscale; bi++) {
				for (int ai = 0; ai < xscale; ai++) {
					undergroundAu[ai][bi] = 0;
					undergroundC[ai][bi] = 0;
					int x = ai;
					int y = bi;
					int ugAu = 0;
					int ugC = 0;
					//Ueberpruefen, ob auf der Position, die um eins erhoeht werden soll bereits eine Goldhoehe von 1 ist!
					if (heightAu[ai][bi] >= 1) {
						ugAu += 1;
					} else {
						ugC += 1;
					}
					x += 1;
					//Ueberpruefen, ob der Nachbar auf der selben Hoehe ist!
					if (heightC[ai][bi] + heightAu[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
						// Wenn der Nachbar auf selber Hoehe ist, wird ueberprueft, ob er bereits Gold ist!
						if (heightAu[getXKoordinate(x)][getYKoordinate(y)] >= 1) {
							ugAu += 1;
						}
						if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0) {
							ugC += 1;
						}
					}
					//Ueberpruefen, ob der Nachbar hoeher ist, als die betrachtete Position!
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
						//Ueberpruefen, ob auf der gleichen Hoehe der Position ai, bi ein Kohlenstoffatom liegt oder nicht!
						if (heightC[ai][bi] + heightAu[ai][bi] <= heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							ugC += 1;
						}
						if (heightC[ai][bi] + heightAu[ai][bi] > heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							ugAu += 1;
						}
					}
					x -= 1;
					y += 1;
					if (heightC[ai][bi] + heightAu[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightAu[getXKoordinate(x)][getYKoordinate(y)] >= 1) {
							ugAu += 1;
						}
						if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0) {
							ugC += 1;
						}
					}
					if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
							+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
						if (heightC[ai][bi] + heightAu[ai][bi] <= heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							ugC += 1;
						}
						if (heightC[ai][bi] + heightAu[ai][bi] > heightC[getXKoordinate(x)][getYKoordinate(y)]) {
							ugAu += 1;
						}
					}
					undergroundAu[ai][bi] = ugAu;
					undergroundC[ai][bi] = ugC;
					int gesamt = undergroundAu[ai][bi] + undergroundC[ai][bi];
					//TODO calculate with geamt
				}
			}
		}

		/**
		 *  Gets the underground attribute of the SurfaceCalculator object
		 *
		 *@param  ai  Description of the Parameter
		 *@param  bi  Description of the Parameter
		 *@return     The underground value
		 */
		public int[] getUnderground(int ai, int bi) {
			int x = ai;
			int y = bi;
			int ugAu = 0;
			int ugC = 0;
			//Ueberpruefen, ob auf der Position, die um eins erhoeht werden soll bereits eine Goldhoehe von 1 ist!
			if (heightAu[ai][bi] >= 1) {
				ugAu += 1;
			} else {
				ugC += 1;
			}
			x += 1;
			//Ueberpruefen, ob der Nachbar auf der selben Hoehe ist!
			if (heightC[ai][bi] + heightAu[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]
					+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
				// Wenn der Nachbar auf selber Hoehe ist, wird ueberprueft, ob er bereits Gold ist!
				if (heightAu[getXKoordinate(x)][getYKoordinate(y)] >= 1) {
					ugAu += 1;
				}
				if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0) {
					ugC += 1;
				}
			}
			//Ueberpruefen, ob der Nachbar hoeher ist, als die betrachtete Position!
			if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
					+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
				//Ueberpruefen, ob auf der gleichen Hoehe der Position ai, bi ein Kohlenstoffatom liegt oder nicht!
				if (heightC[ai][bi] + heightAu[ai][bi] <= heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					ugC += 1;
				}
				if (heightC[ai][bi] + heightAu[ai][bi] > heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					ugAu += 1;
				}
			}
			x -= 1;
			y += 1;
			if (heightC[ai][bi] + heightAu[ai][bi] == heightC[getXKoordinate(x)][getYKoordinate(y)]
					+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightAu[getXKoordinate(x)][getYKoordinate(y)] >= 1) {
					ugAu += 1;
				}
				if (heightAu[getXKoordinate(x)][getYKoordinate(y)] == 0) {
					ugC += 1;
				}
			}
			if (heightC[ai][bi] + heightAu[ai][bi] < heightC[getXKoordinate(x)][getYKoordinate(y)]
					+ heightAu[getXKoordinate(x)][getYKoordinate(y)]) {
				if (heightC[ai][bi] + heightAu[ai][bi] <= heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					ugC += 1;
				}
				if (heightC[ai][bi] + heightAu[ai][bi] > heightC[getXKoordinate(x)][getYKoordinate(y)]) {
					ugAu += 1;
				}
			}
			int[] AuCUg = { ugAu, ugC };
			return AuCUg;
		}

		/**
		 *  Gets the auAdsProbs attribute of the SurfaceCalculator object
		 *
		 *@param  a  Description of the Parameter
		 *@param  b  Description of the Parameter
		 *@return    The auAdsProbs value
		 */
		public double getAuAdsProbs(int a, int b) {
			double prob = 0;
			double etaprob;
			if (eta <= 0) {
				etaprob = (0.68 * (1 - Math.exp(5 * eta / 1000)) + 0.32);
			} else {
				etaprob = (0.32 * (Math.exp(-5 * eta / 1000)));
			}
			prob = (neighboursAuAu[a][b] * probproAunb + undergroundAu[a][b]
					* probproAunb + neighboursAuC[a][b] * probproCnb + undergroundC[a][b]
					* probproCnb)
					* etaprob;
			return prob;
		}

		/**
		 *  Gets the auDesProbs attribute of the TestDesRoutine3 class
		 *
		 *@param  a  Description of the Parameter
		 *@param  b  Description of the Parameter
		 *@return    The auDesProbs value
		 */
		public double getAuDesProbs(int a, int b) {
			double etaprob;
			double bonusprob;
			double Audesprob = 0;
			//int cluster = getClusterSize(a, b);
			
			if (eta <= 0) {
				probdescritical = 0.7;
				etaprob = (0.68 * (1 - Math.exp(5 * eta / 1000)) + 0.32);
			} else {
				probdescritical = 0;
				etaprob = (0.32 * (Math.exp(-5 * eta / 1000)));
			}
			if (clustersize[a][b] <= criticalsize) {
				bonusprob = (probdescritical * Math.pow(clustersize[a][b], 2.0))
						/ Math.pow(criticalsize, 2.0);
			} else {
				bonusprob = probdescritical;
			}
			if (clustersize[a][b] == 1) {
				Audesprob = 1 - (3 * desprobproCnb * etaprob);
			}
			if (clustersize[a][b] > 1 && clustersize[a][b] < criticalsize) {
				heightAu[a][b] -= 1;
				int[] nbs = getAuNeighbours(a, b);
				int[] ugs = getUnderground(a, b);
				Audesprob = 1
						- bonusprob
						- (((nbs[0] + ugs[0]) * desprobproAunb + (nbs[1] + ugs[1])
								* desprobproCnb) * etaprob);
				heightAu[a][b] += 1;
			}
			if (clustersize[a][b] >= criticalsize) {
				heightAu[a][b] -= 1;
				int[] nbs = getAuNeighbours(a, b);
				int[] ugs = getUnderground(a, b);
				Audesprob = 1
						- bonusprob
						- (((nbs[0] + ugs[0]) * desprobproCnb + (nbs[1] + ugs[1])
								* desprobproCnb) * etaprob);
				heightAu[a][b] += 1;
			}
			if (Audesprob < 0) {
				Audesprob = 0;
			}
			return Audesprob;
		}
	}

}