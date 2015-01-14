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

package nano.client;

import java.awt.*;
import java.util.Iterator;

import javax.swing.JPanel;

import nano.awt.FloatVollKreis;

public class DPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Panel MyPanel;

	int width, height;

	public DPanel(int w, int h, int zeilen, int spalten) {
		width = w;
		height = h;
		// setBackground(Color.white);
		MyPanel = new Panel();
		MyPanel.setLayout(new GridLayout(zeilen, spalten));
		add(MyPanel);
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	/**
	 * @param i
	 */
	public void removeComponent(String mylabel) {
        boolean exitnow = false;
		for (int i = 0; i < MyPanel.getComponentCount(); i++) {

			Component mycomp = MyPanel.getComponent(i);
			Object myclass = mycomp.getClass();

			if (myclass.toString().equals("class javax.swing.JPanel")) {

				//System.out.println("componenent class::" + myclass.toString());
				JPanel mydPanel = (JPanel) mycomp;

				for (int j = 0; j < mydPanel.getComponentCount(); j++) {
                    
					Component mydpan = mydPanel.getComponent(j);
					//System.out.println("dpanel Class:" + mydpan.getClass());
					//System.out.println("dpanel Name:" + mydpan.getName());
                    
					if (mydpan.getClass().toString().equals("class nano.awt.FloatVollKreis"))
					{
						FloatVollKreis myfloat = (FloatVollKreis)mydpan;
						//System.out.println("kacklable"+myfloat.getLabel());
					
					// System.out.println("compgetname:"+mycomp.+"
					// mylable:"+mylabel);
					if (myfloat.getLabel() == mylabel) {
						MyPanel.remove(i);
						exitnow = true;
						break;
					}
					}
				}// END for dcomponen
			}// END IF SWING
			
			if (myclass.toString().equals("class java.awt.Panel")) {

				//System.out.println("componenent class::" + myclass.toString());
				Panel mydPanel = (Panel) mycomp;

				for (int j = 0; j < mydPanel.getComponentCount(); j++) {
                    
					Component mydpan = mydPanel.getComponent(j);
					//System.out.println("dpanel Class:" + mydpan.getClass());
					//System.out.println("dpanel Name:" + mydpan.getName());
                    
					if (mydpan.getClass().toString().equals("class nano.awt.FloatVollKreis"))
					{
						FloatVollKreis myfloat = (FloatVollKreis)mydpan;
						//System.out.println("kacklable"+myfloat.getLabel());
					
					// System.out.println("compgetname:"+mycomp.+"
					// mylable:"+mylabel);
					if (myfloat.getLabel() == mylabel) {
						MyPanel.remove(i);
						exitnow = true;
						break;
					}
					}
				}// END for dcomponen
			}// END IF AWT

			
			
			
			if (exitnow)break;
		}// END For component

		validateTree();

	}

	public void addComponent(Component NewComponent) {
		MyPanel.add(NewComponent);
		validateTree();
		/*
		 * repaint(); System.out.println(NewComponent.getName());
		 */
	}

	public static void main(String args[]) {
		Frame MyFrame = new Frame("Hallo Peter");
		DPanel MyPanel = new DPanel(300, 200, 6, 3);
		MyFrame.setLayout(new FlowLayout());
		MyFrame.add(MyPanel);
		MyFrame.pack();
		MyFrame.setVisible(true);
	}
}
