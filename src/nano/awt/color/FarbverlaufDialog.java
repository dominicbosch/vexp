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

package nano.awt.color;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;

/**
 * �berschrift:
 * Beschreibung:
 * Copyright:     Copyright (c) 2001
 * Organisation:
 * @author
 * @version 1.0
 */

public class FarbverlaufDialog extends Dialog {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Panel panel1 = new Panel();
  BorderLayout borderLayout1 = new BorderLayout();
  VerlaufCanvas MyCanvas;
  ColorScala MyScala,OldScala;
  JButton button1 = new JButton();
  JButton button2 = new JButton();
  Frame myframe;
  GridLayout gridLayout1 = new GridLayout();
  Panel panel2 = new Panel();
  Panel panel3 = new Panel();
  JButton button3 = new JButton();
  JButton button4 = new JButton();

  public FarbverlaufDialog(Frame frame, String title, boolean modal, ColorScala oldScala) {
    super(frame, title, modal);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    MyScala=new ColorScala();
    MyScala.setColors(new Color(oldScala.getColor(0)),new Color(oldScala.getColor(255)));
    OldScala=new ColorScala();
    OldScala.setColors(new Color(oldScala.getColor(0)),new Color(oldScala.getColor(255)));
    myframe=frame;
    try {
      jbInit();
      add(panel1);
      pack();
      show();
    }
    catch(Exception ex) {
      //ex.printStackTrace();
    }
  }


  public FarbverlaufDialog(Frame frame, ColorScala OldScala){
    this(frame, "", true,OldScala);
  }



  public static ColorScala getColorScala(Frame frame, ColorScala oldScala){
    FarbverlaufDialog TheDialog=new FarbverlaufDialog(frame, oldScala);
    return TheDialog.MyScala;
  }

  void jbInit() throws Exception {
    panel1.setLayout(gridLayout1);
    MyCanvas=new VerlaufCanvas(MyScala);
    button1.setText("change");
    button1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button1_actionPerformed(e);
      }
    });
    button2.setText("change");
    button2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button2_actionPerformed(e);
      }
    });
    gridLayout1.setRows(2);
    gridLayout1.setColumns(1);
    button3.setText("Abort");
    button3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button3_actionPerformed(e);
      }
    });
    button4.setText("OK");
    button4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button4_actionPerformed(e);
      }
    });
    panel3.add(button1, null);

    panel3.add(MyCanvas, null);
    panel3.add(button2, null);
    panel1.add(panel3, null);
    panel1.add(panel2, null);
    panel2.add(button4, null);
    panel2.add(button3, null);

  }
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  void cancel() {
    MyScala.setColors(new Color(OldScala.getColor(0)),new Color(OldScala.getColor(255)));
    dispose();
  }

  void button1_actionPerformed(ActionEvent e) {
	  //TODO use the event
    MyScala.setColors(new Color(ColorPickerDialog.getColor(myframe,MyScala.getColor(0))), new Color(MyScala.getColor(255)));
    MyCanvas.setColorScala(MyScala);
    MyCanvas.repaint();
  }

  void button2_actionPerformed(ActionEvent e) {
//	TODO use the event
    MyScala.setColors(new Color(MyScala.getColor(0)),new Color(ColorPickerDialog.getColor(myframe,MyScala.getColor(255))));
    MyCanvas.setColorScala(MyScala);
    MyCanvas.repaint();

  }

  void button3_actionPerformed(ActionEvent e) {
//	TODO use the event
    cancel();
  }

  void button4_actionPerformed(ActionEvent e) {
//	TODO use the event
    dispose();
  }
}
