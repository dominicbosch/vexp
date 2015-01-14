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
import nano.awt.color.*;
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

public class ImagePanel extends Panel {
  /**
	 * TODO clean up, here instance also in new client
	 */
	private static final long serialVersionUID = 1L;
	JButton ColorButton = new JButton("Change Colors");
  // Button SaveImageButton = new Button("Save Image");
  // Does not work with apple
  JButton SaveDataButton = new JButton("Analysis");
  NetObserver MyNetObserver;
  VerlaufCanvas MyVerlauf;

  public ImagePanel() {

  }

  public ImagePanel(NetObserver TheObserver){
    MyNetObserver=TheObserver;
    setLayout(new FlowLayout());
    //add(SaveImageButton);
    add(SaveDataButton);
    MyVerlauf=new VerlaufCanvas(MyNetObserver.getColorScala() );
    add(MyVerlauf);
    add(ColorButton);
    ColorButton.addActionListener(new ColorListener());
    //SaveImageButton.addActionListener(new SaveListener());
    SaveDataButton.addActionListener(new SaveDataListener());
}
    class ColorListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
    ColorScala TheNew=FarbverlaufDialog.getColorScala(new Frame(),MyNetObserver.getColorScala());
    MyNetObserver.setColorScala(TheNew);
    MyVerlauf.setColorScala(TheNew);
    MyVerlauf.repaint();
  }
  }
  class SaveListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
     MyNetObserver.save("New Image");
    }
  }
  class SaveDataListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
     MyNetObserver.saveData("New Image");
    }
  }
}
