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
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;
import JSci.awt.ContourPlot;
import JSci.maths.vectors.Double2Vector;




/**
 * @author guggisberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class Display_Ag extends Frame {
	/**
	 *
	 */
	
	public static void main(String arg[]) {
		new Display_Ag();
	}

	public Display_Ag() {
		super("Ag");
		// add exit menu
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
				System.exit(0);
			}
		});
            // plot the field
		
		
		
		
		Ag myAg = new Ag();
		myAg.setArraySize(256);
		myAg.setScanSize(50); //in A
		myAg.setAtomRadius(2.5); // 1 / Atom Radius
		myAg.setAtomWidth(1.4); //in nm
		myAg.setAgAtomSize(28.0); //in A
		myAg.init();
		
		
		double[][] mydata = myAg.getSurface();
		
		ContourPlot myplot = new ContourPlot(mydata);
		myplot.setBackground(Color.WHITE);
		add(myplot, "Center");
		setSize(400 + 30, 400 + 30);
		setVisible(true);
	}

	}
