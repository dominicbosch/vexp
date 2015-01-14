/*
 * Created on 07.11.2005
 *
 */

/* 
 * Copyright (c) 2002-2005 by Tibor Gyalog, Raoul Schneider, Dino Keller, 
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

/**
 * Sample program demonstrating genearte and plot
 * an array with Si(111)7x7 topography data.
 *
 * @author Martin Guggisberg
 * @version 1.0
 *
 * This class need the JSci package from
 * sourceforge.net
 * 
 * Download from http://sourceforge.net/projects/jsci
 *
 */

package nano.compute.util;

import java.util.Iterator;
import java.util.Vector;

import JSci.maths.vectors.Double2Vector;

/**
 * @author guggisberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class Si_111_7x7 {
	/**
	 *
	 */
	private static final long serialVersionUID = 828237845182749545L;

	private int array_size = 400; // size of array in pixel or Points

	private double scansize = 80; // scansize in A

	private double si_ad_atom_size = 7.6;// si ad atomsize in A

	private double zscale = 200;

	private double zoffset = 0;

	private double atom_radius = 2.0;

	private double atom_size = 1.4;

	private final double[] e1 = { 56.60000001, 0. }; // e1 of the unit cell (long diagonal of the rhombus) in A

	private final double[] e2 = { 28.29999999, 16.33901262 }; // e2 of the unit cell (upper corner of the rhombus) in A

	private final double[][] si_ad_atomes = { { 8.031809525, 0. }, // position of the 12 ad_atoms in the unit cell
			{ 24.20323810, 0. }, { 32.34285715, 0. }, { 48.51428572, 0. },
			{ 24.28409526, -9.383261528 }, { 16.19838097, 4.621606437 },
			{ 40.42857144, -4.668289294 }, { 32.34285716, 9.336578658 },
			{ 40.42857140, 4.668289294 }, { 32.34285712, -9.336578658 },
			{ 24.28409523, 9.383261528 }, { 16.19838094, -4.621606437 } };

	private double[][] mydata;

	private double[][] ad_atome;

	private Vector si_ad_position;

	public static void main(String arg[]) {
		new Si_111_7x7();
	}

	public Si_111_7x7() {
		setArraySize(400);
		setScanSize(200.0);
		init();
	}

	/**
	 * @param i  		position in mydata to insert the array
	 * @param j  		position in mydata to insert the array
	 * @param mink		start k 
	 * @param minl		start l
	 * @param maxk		end k
	 * @param maxl		end l
	 * @param ad_atome	Array with the ad_atome data
	 * 
	 * start and end are used if the adatom overlapps partly mydata
	 * (boundary condition)
	 */
	public void add_adatoms(int i, int j, int mink, int minl, int maxk,
			int maxl, double[][] ad_atome) {
		for (int k = mink; k < maxk; k++) {
			for (int l = minl; l < maxl; l++) {
				mydata[i + k - mink][j + l - minl] += ad_atome[k][l];
			}
		}
	}

	/**
	 * @param size 
	 * @param r
	 * @param w
	 * @return array double[][] with adatom shape
	 * 
	 * f(x,y) -> exp(x*x/w/w)*(y*y/w/w)
	 * with coordinates from -r to r 
	 */
	public double[][] getgauss(int size, double r, double w) {

		double[][] gaus = new double[size][size];
		double x;
		double y;
		for (int i = 0; i < size; i++) {
			x = ((double) i / (size - 1) * 2 * r - r);
			for (int j = 0; j < size; j++) {
				y = ((double) j / (size - 1) * 2 * r - r);
				gaus[i][j] = (Math.exp(-x * x / w / w) * Math.exp(-y * y / w
						/ w));
			}
		}
		return gaus;
	}

	public double[][] getSurface() {

		return mydata;
	}

	public void init() {

		// compute size of the array for one ad_atom
		int si_ad_atom_array_size = (int) Math.round(si_ad_atom_size / scansize
				* array_size);
		//System.out.println("si_array:"+si_ad_atom_array_size);

		// generate an array for the topography of one ad_atom (arraysize in pixel, gauss radius, gauss width)
		ad_atome = getgauss(si_ad_atom_array_size, atom_radius, atom_size);

		createSiSurface();
		createSiMap();

	}

	private void createSiMap() {
		mydata = new double[array_size][array_size]; // array in which the data is ploted
		// add offset to array
		for (int i = 0; i < mydata.length; i++) {
			for (int j = 0; j < mydata.length; j++) {
				mydata[i][j] = -200;
			}
		}

		double x, y, dx, dy;
		// loop over the mydata array y-directioon in pixel
		for (int i = 0; i < mydata.length; i++) {
			// calculate the y-position in A
			y = (double) i / mydata.length * scansize;
			//break if no more atoms are in the collection
			if (si_ad_position.isEmpty())
				break;
			// loop over the mydata array in x-direction
			for (int j = 0; j < mydata.length; j++) {
				// calculate the x-position in A
				x = (double) j / mydata.length * scansize;
				// break if no more atoms are in the collection
				if (si_ad_position.isEmpty())
					break;

				Double2Vector found_position = null; // temp variable for a generated ad_atom
				// Iterate over all atoms in the list
				for (Iterator iter = si_ad_position.iterator(); iter.hasNext();) {
					Double2Vector element = (Double2Vector) iter.next();
					// if x< -2  or y<-2 this adatom is not ploted into mydata
					if ((element.getComponent(0) < -2)
							| (element.getComponent(1) < -2)) {
						found_position = element;
						j = 0; // restart the loop at x=0
						break; // break the iteration and remove this adatom
					}

					dx = Math.abs(element.getComponent(0) - x); // distance between the observed atom and x position of the loop
					dy = Math.abs(element.getComponent(1) - y); // distance between the observed atom and y position of the loop
					// if the atome is in this range of mydata
					if (((dx) <= (si_ad_atom_size / 2))
							&& ((dy) <= (si_ad_atom_size / 2))) {

						//  left and bottom border 
						//  only a part of the atom will be plotted
						// calculate the start indizes of the atom
						int start_j = (int) Math
								.round((si_ad_atom_size / 2 - dx) / scansize
										* array_size);
						int start_i = (int) Math
								.round((si_ad_atom_size / 2 - dy) / scansize
										* array_size);

						// test if the atom  do not overlap the upper and right border
						if (((mydata.length - i) > ad_atome.length)
								&& ((mydata.length - j) > ad_atome.length)) {
							add_adatoms(i, j, start_i, start_j,
									ad_atome.length, ad_atome.length, ad_atome);
						} else {
							// test if the atom do overlapp both borders -> corner right at the bottom
							if (((mydata.length - i) <= ad_atome.length)
									&& ((mydata.length - j) <= ad_atome.length)) {
								add_adatoms(i, j, start_i, start_j,
										(mydata.length - i),
										(mydata.length - j), ad_atome);
							} else {
								// test if the atom do overlapp the right border
								if ((mydata.length - i) > ad_atome.length) {
									add_adatoms(i, j, start_i, start_j,
											ad_atome.length,
											(mydata.length - j), ad_atome);
								} else {
									// in this case the atom do overlapp the upper border
									add_adatoms(i, j, start_i, start_j,
											(mydata.length - i),
											ad_atome.length, ad_atome);
								}
							}
						}
						// store the plotted element temporary 
						found_position = element;
						break;
					}
				}
				// in the case that an ad_atome was generated, remove it from the collection
				if (found_position != null) {
					si_ad_position.remove(found_position);
				}

			}
		}

	}

	private void createSiSurface() {
		//		 compute size of the array for one ad_atom
		int si_ad_atom_array_size = (int) Math.round(si_ad_atom_size / scansize
				* array_size);
		//System.out.println("si_array:"+si_ad_atom_array_size);

		// generate an array for the topography of one ad_atom (arraysize in pixel, gauss radius, gauss width)
		ad_atome = getgauss(si_ad_atom_array_size, 2.0, 1.38);

		double x_o = 8; //x-offset in A
		double y_o = 8; //y-offset in A

		si_ad_position = new Vector(); //Collections of the adatoms position

		/** Si-Surface **/
		///**
		int max_j = (int) Math.round(scansize / e1[0] + 1); // number of unitcells in x-direction
		int max_i = (int) Math.round(scansize / e2[1] + 2); //number of unitcells in y-direction
		//System.out.println("max_j: " + max_j + " max_i: " + max_i);
		for (int j = -1; j < max_j; j++) { // loop over all unit cell in x-direction
			for (int i = -1; i < max_i; i++) { // loop over all unit cell in y-direction
				for (int k = 0; k < 12; k++) { // loop over the 12 adatoms in a unit cell
					// add the atoms to the collection
					si_ad_position.add(new Double2Vector(
							si_ad_atomes[k][0] + (j - Math.round(i / 2))
									* e1[0] + i * e2[0] + x_o,
							si_ad_atomes[k][1] + j * e1[1] + i * e2[1] + y_o));

				}

			}
		}

	}

	public void setArraySize(int arraysize) {
		array_size = arraysize;
	}

	public void setAtomWidth(double w) {
		this.atom_size = w;

	}

	public void setAtomRadius(double r) {
		this.atom_radius = r;

	}

	public void setAgAtomSize(double size) {
		this.si_ad_atom_size = size;

	}

	public void setScanSize(double scan) {
		scansize = scan;
	}

}
