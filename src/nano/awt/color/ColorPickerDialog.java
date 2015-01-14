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

public class ColorPickerDialog extends Dialog implements ColorListener{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Panel panel1 = new Panel();
  BorderLayout borderLayout1 = new BorderLayout();
  Panel panel2 = new Panel();
  Panel panel3 = new Panel();
  JButton button1 = new JButton();
  JButton button2 = new JButton();
  ColorCanvas MyColorCanvas=new ColorCanvas();
  TextField textField1 = new TextField();
  int MyColor,OldColor;

  public static int getColor(Frame frame,int oldColor){
    ColorPickerDialog MyColorPickerDialog=new ColorPickerDialog(frame,oldColor);
    return MyColorPickerDialog.MyColor;
  }

  public ColorPickerDialog(Frame frame, String title, boolean modal,int MyOldColor) {
    super(frame, title, modal);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      OldColor=MyOldColor;
      jbInit();
      add(panel1);
      pack();
    }
    catch(Exception ex) {
      //ex.printStackTrace();
    }
  }

  public ColorPickerDialog(Frame frame, int MyOldColor) {
    this(frame, "", true,MyOldColor);
    show();
  }



  void jbInit() throws Exception {
    MyColorCanvas.addColorListener(this);
    MyColor=OldColor;
    textField1.setBackground(new Color(MyColor));
    panel1.setLayout(borderLayout1);
    button1.setText("OK");
    button1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button1_actionPerformed(e);
      }
    });
    button2.setText("Abort");
    button2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        button2_actionPerformed(e);
      }
    });
    textField1.setColumns(1);
    panel1.add(panel2, BorderLayout.SOUTH);
    panel2.add(button1, null);
    panel2.add(button2, null);
    panel2.add(textField1, null);
    panel1.add(panel3, BorderLayout.CENTER);
    panel3.add(MyColorCanvas);
  }
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  void cancel() {
    dispose();
  }

  public void ColorEventPerformed(int NewColor){
    textField1.setBackground(new Color(NewColor));
    MyColor=NewColor;
  }

  void button2_actionPerformed(ActionEvent e) {
    //Abbrechen
	  //TODO use the event
        dispose();
        MyColor=OldColor;
  }

  void button1_actionPerformed(ActionEvent e) {
      //TODO use the event
	  //OK-Button
      dispose();
  }
}
