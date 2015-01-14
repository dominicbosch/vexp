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

package nano.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 *
 * @author Raoul Schneider
 * @version 1.3 18.7.2001 (1.2: label implemented, Zeiger-routinen upgedated auf schnellere Version; 1.2.1: getMinSize hierher implemetiert; 1.3: Textanzeige)
 */
public class FloatVollKreis extends FloatControl implements Runnable,
		MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	double steigung;

	int numMinorTicks, numMajorTicks;

	Color background, rand, ticks, zeiger, textColor;

	int anfBreite;

	int anfHoehe;

	int radius, length, pad, xMitte, yMitte;

	private volatile Thread MyJitter;

	//long LastChangeTime;
	boolean hasChanged = false;

	/**
	 * @param steig         data range in one full turn
	 */
	public FloatVollKreis(String label, double min, double max, double init,
			Properties NewRepresentation) {
		super(label, min, max, init, NewRepresentation);
		initRepresentation();
		addMouseListener(this);
		addMouseMotionListener(this);
		MyJitter = new Thread(this, "Float-Display");
		MyJitter.start();
	}

	protected void finalize() {
		stop_thread();
	}

	public void initRepresentation() {
		steigung = Double.parseDouble(Representation.getProperty("Steigung",
				"10"));
		background = new Color(Integer.decode(
				Representation.getProperty("BackgroundColor", "#F0F0F0"))
				.intValue());
		numMinorTicks = 3;
		numMajorTicks = 4;
		rand = new Color(Integer.decode(
				Representation.getProperty("RandColor", "#0000F0")).intValue());
		ticks = new Color(Integer.decode(
				Representation.getProperty("TicksColor", "#0000F0")).intValue());
		zeiger = new Color(Integer.decode(
				Representation.getProperty("ZeigerColor", "#F00000"))
				.intValue());
		textColor = new Color(Integer.decode(
				Representation.getProperty("TextColor", "#F00000")).intValue());
	}

	/**
	 * Graphic routine which paints the circle/scale
	 */
	public void paint(Graphics g) {

		int breite = getWidth();
		int hoehe = getHeight();
		anfBreite = 0;
		anfHoehe = 0;
		radius = 0;

		if (breite <= hoehe) {
			anfHoehe = getHeight() / 2 - breite / 2;
			anfBreite = 0;
			radius = (breite / 2) - 10;
		} else {
			anfBreite = getWidth() / 2 - hoehe / 2;
			anfHoehe = 0;
			radius = (hoehe / 2) - 10;
		}
		pad = (int) (radius * 0.05);
		length = 4 * pad;
		xMitte = anfBreite + radius;
		yMitte = anfHoehe + radius;
		//============ Kreis wird gezeichnet =============================
		g.setColor(background);
		g.fillOval(anfBreite + pad, anfHoehe + pad, 2 * (radius - pad),
				2 * (radius - pad));
		g.setColor(rand);
		g.drawOval(anfBreite + pad, anfHoehe + pad, 2 * (radius - pad),
				2 * (radius - pad));
		//============ kleine Einheiten werden gezeichnet ====================
		for (int i = 0; i < numMinorTicks * numMajorTicks; i++) {
			g.setColor(ticks);
			double MyCos = Math.cos(i * 2 * Math.PI
					/ (numMinorTicks * numMajorTicks));
			double MySin = Math.sin(i * 2 * Math.PI
					/ (numMinorTicks * numMajorTicks));
			g.drawLine(xMitte + (int) ((radius - pad - length) * MyCos), yMitte
					+ (int) ((radius - pad - length) * MySin), xMitte
					+ (int) ((radius - pad) * MyCos), yMitte
					+ (int) ((radius - pad) * MySin));
		}

		//============== grosse Einheiten werden gezeichnet =============
		for (int i = 0; i < numMinorTicks * numMajorTicks; i++) {
			g.setColor(ticks);
			double MyCos = Math.cos(i * 2 * Math.PI
					/ (numMinorTicks * numMajorTicks));
			double MySin = Math.sin(i * 2 * Math.PI
					/ (numMinorTicks * numMajorTicks));
			g.drawLine(xMitte + (int) ((radius - pad - length) * MyCos), yMitte
					+ (int) ((radius - pad - length) * MySin), xMitte
					+ (int) ((radius - pad) * MyCos), yMitte
					+ (int) ((radius - pad) * MySin));
		}

		paintValue(g);
	}

	public void paintValue(Graphics g) {
		//============== Display-Schrift wird gezeichnet =======================
		g.setColor(background);
		g.fillOval(anfBreite + pad + length + 1, anfHoehe + pad + length + 1,
				2 * (radius - pad - length - 1),
				2 * (radius - pad - length - 1));

		g.setColor(textColor);
		double zwWert = Math.rint(getValue() * 10.0);
		String textWert = Double.toString(zwWert / 10);
		Font font = new Font("SansSerif", Font.PLAIN, 10);
		g.setFont(font);
		g.drawString(textWert, xMitte - 11, yMitte + 15);
		//============== Zeiger wird gezeichnet =======================
		g.setColor(zeiger);
		double alpha = (getValue() * 2 * Math.PI / steigung) - (Math.PI / 2);
		int rx = (int) ((radius - pad - length - 3) * Math.cos(alpha));
		int ry = (int) ((radius - pad - length - 3) * Math.sin(alpha));
		g.drawLine(xMitte, yMitte, xMitte + rx, yMitte + ry);

	}

	//================ MouseEvent Methoden =========================

	/**
	 * catches mouse-events (when mouse pressed) and calculates the display of the scales pointer
	 */
	public void mousePressed(MouseEvent e) {
		int radius = 50;
		//int xMitte = getWidth()/2;
		//int yMitte = getHeight()/2;
		int xPos = e.getX();
		int yPos = e.getY();
		int xKoord = (xPos - xMitte);
		int yKoord = (yPos - yMitte);
		double wert = getValue();
		double alpha = (getValue() * 2 * Math.PI / steigung) - (Math.PI / 2);
		double rx = radius * Math.cos(alpha);
		double ry = radius * Math.sin(alpha);
		double sz = rx * yKoord - ry * xKoord;
		double winkel = Math.acos((xKoord * rx + yKoord * ry)
				/ (radius * Math.sqrt(xKoord * xKoord + yKoord * yKoord)));
		if (!Double.isNaN(winkel)) {
			if (sz > 0) {
				wert = getValue() + winkel * steigung / (2 * Math.PI);
			}
			if (sz < 0) {
				wert = getValue() - winkel * steigung / (2 * Math.PI);
			}
			setValue(wert);
			hasChanged = true;

		}
	}

	//============= Und noch ein Haufen Muell, den wir nicht brauchen
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		mousePressed(e);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		repaint();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		//System.out.println("Thread start myname:"+thisThread.getName());
		while (MyJitter == thisThread) {
			if (hasChanged) {
				MyListener.FloatEventPerformed(Label, value);
				hasChanged = false;
			}
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
			}
		}
      
		//System.out.println("Stopp Thread FloatControll:"+thisThread	.getName());
	}

	//new thread stop method
	public void stop_thread() {
		// System.out.println("Stop Vollkreis Thread");
		MyJitter = null;
	}

}