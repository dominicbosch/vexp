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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import nano.awt.color.ColorScala;
import nano.net.Display;

/**
 * listens to the input stream of what is scanned at the moment
 * 
 * @author Tibor Gyalog
 * @version 1.0.1 20.08.01 (documentated)
 */

public class JNetObserver extends JPanel implements Display, MouseListener,
		MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private Image[] LineImage = new Image[256];

	private JNetSimPanel myNetSimPanel = null;

	private Image ZeroLineImage;

	private MemoryImageSource ZeroLineSource;

	private Graphics myg; // added to increase performance

	private MemoryImageSource[] linesource = new MemoryImageSource[256];

	// Thread Maler;
	
	
	private ColorScala MyColorScala;

	private int LineData[][] = new int[256][256];

	private byte RawData[][] = new byte[256][256];

	private int nr_old = 0;

	private byte[] oldline = new byte[256];

	private int[] ZeroData = new int[256];

	private int MouseState = 0;

	private Point StartPoint = new Point(0, 0);

	private Point EndPoint = new Point(0, 0);

	private Point ZoomEndPoint = new Point(0, 0);

	public JNetObserver() {
		super();
		setBackground(Color.gray);
		setForeground(Color.green);
		setFont(new Font("Arial", Font.BOLD, 20));
		setMinimumSize(new Dimension(256,256));
		setBounds(0,0,256,256);
		setPreferredSize(new Dimension(256,256));
		contains(256,256);
		//Border border = BorderFactory.createRaisedBevelBorder();
		//setBorder(border );
		
		
		
		addMouseListener(this);
		addMouseMotionListener(this);
		MyColorScala = new ColorScala();
		MyColorScala.setColors(Color.black, Color.white);
		
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				LineData[i][j] = (i + j) / 2;
			}
			linesource[i] = new MemoryImageSource(256, 1, LineData[i], 0, 256);
			LineImage[i] = createImage(linesource[i]);
			ZeroData[i] = (255 << 24 | 255 << 16);
		}
		
		
		ZeroLineSource = new MemoryImageSource(256, 1, ZeroData, 0, 256);
		ZeroLineImage = createImage(ZeroLineSource);
		
	

	}

	/**
	 * @param myNetSimPanel
	 */
	public JNetObserver(JNetSimPanel myNetSimPanel) {
		this();
		this.myNetSimPanel = myNetSimPanel;

		// TODO Auto-generated constructor stub
	}

	public Dimension getPreferredSize() {
		return new Dimension(256, 256);
	}
	public Dimension getMinimumSize() {
		return new Dimension(256, 256);
	}

	public void write(byte[] newLine) {

		int nr = newLine[0] + 128;
		for (int i = 0; i < 256; i++) {
			RawData[nr][i] = newLine[i];
		}
		NewLine(nr, nr_old, oldline);
		oldline = newLine;
		nr_old = nr;
	}

	

	public void paintComponent(Graphics g) {
//		 Let UI delegate paint first 
	    // (including background filling, if I'm opaque)
	    super.paintComponent(g); 		
		for (int i = 0; i < 256; i++) {
			if (LineImage[i]!=null)
			  g.drawImage(LineImage[i], 0, i, null);
		}
		if (MouseState == 1) {
			int dx = (int) (EndPoint.getX() - StartPoint.getX());
			int dy = (int) (EndPoint.getY() - StartPoint.getY());
			int r;
			if (dx > dy) {
				r = dx;
			} else {
				r = dy;
			}
			ZoomEndPoint.x=(int) (StartPoint.getX()+r);
			ZoomEndPoint.y=(int) (StartPoint.getY()+r);
			myNetSimPanel.setZoomEndPoint(ZoomEndPoint);
			g.drawRect((int) StartPoint.getX(), (int) StartPoint.getY(), r, r);
		}
	}

	public void setColors(Color bottom, Color top) {
		MyColorScala.setColors(bottom, top);
	}

	public void setColorScala(ColorScala NewScala) {
		MyColorScala = NewScala;
	}

	public ColorScala getColorScala() {
		return MyColorScala;
	}

	public void mouseClicked(MouseEvent e) {
		int mynum = e.getClickCount();
		//System.out.println("Clickcount=" + mynum);
		if ((mynum == 1 && MouseState == 2) && !myNetSimPanel.isruning()) {
			MouseState = 0;
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!myNetSimPanel.isruning()) {
			StartPoint = e.getPoint();
			myNetSimPanel.setZoomStartPoint(StartPoint);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (MouseState == 1 && !myNetSimPanel.isruning()) {
			EndPoint = e.getPoint();
			//System.out.println("EndPoint=" + EndPoint)
			MouseState = 2;
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (!myNetSimPanel.isruning()) {
			EndPoint = e.getPoint();
			MouseState = 1;
			repaint();
		}
	}

	public void save(String filename) {
		// TODO use the filename
		ImageFrame MyFrame = new ImageFrame("Scan Image");
		MyFrame.setImages(linesource);
	}

	public void saveData(String filename) {
//		 TODO use the filename
		DataFrame MyFrame = new DataFrame("Raw Data", this.getColorScala());
		MyFrame.setData(RawData);
	}

	public void NewLine(int Line, int OldLine, byte[] NewData) {
		for (int i = 0; i < 256; i++) {
			LineData[Line][i] = MyColorScala.getColor(NewData[i] + 128);
			// (255 << 24 | NewData[i]+128 << 16 | NewData[i]+128 << 8 |
			// NewData[i]+128);
		}
		LineImage[Line].flush();
		LineImage[Line] = createImage(linesource[Line]);
		if (myg == null || ((Line % 10) == 0)) {
			// if(g==null ){
			myg = getGraphics();
		}
		if (myg != null) {
			myg.drawImage(ZeroLineImage, 0, Line, null);
			myg.drawImage(LineImage[OldLine], 0, OldLine, null);
			// g.drawLine(0,Line,256,Line);
		}	
	}
}