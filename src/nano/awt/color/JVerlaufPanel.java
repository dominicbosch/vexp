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
import java.awt.image.*;

import javax.swing.JPanel;


/**
 * �berschrift:
 * Beschreibung:
 * Copyright:     Copyright (c) 2001
 * Organisation:
 * @author
 * @version 1.0
 */



public class JVerlaufPanel extends JPanel {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
int sizex=256;
  int sizey=20;

  BorderLayout borderLayout1 = new BorderLayout();
  ColorScala MyColorScala,OldScala;
  Image MyImage;
  MemoryImageSource Imagesource;
  int[] data=new int[sizex*sizey];
  boolean senkrecht;


  public Dimension getPreferredSize(){return new Dimension(sizex,sizey);}
  public Dimension getMinimumSize(){return new Dimension(sizex,sizey);}


  public JVerlaufPanel(ColorScala oldScala) {
    try {
      OldScala=oldScala;
      MyColorScala=oldScala;
      jbInit();
    }
    catch(Exception ex) {
      //ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    //this.setLayout(borderLayout1);
    Imagesource=new MemoryImageSource(sizex,sizey,data,0,sizex);
    MyImage=createImage(Imagesource);
  }

  public void setColorScala(ColorScala newScala){
  MyColorScala=newScala;
  }

     public void paint(Graphics g){
        //for(int yi=0;yi<size;yi++){for(int xi=0;xi<size;xi++){data[size*yi+xi]=(255 << 24) | (fact*height[xi][yi]) << 16 | (fact*height[xi][yi]) << 8 | (fact*height[xi][yi]);}}

        for(int i=0;i<sizex;i++){
            for(int k=0;k<sizey;k++){
              data[i+k*sizex]=MyColorScala.getColor(i);
            }

          }
          MyImage.flush();
          g.drawImage(MyImage,0,0,null);
   }

}
