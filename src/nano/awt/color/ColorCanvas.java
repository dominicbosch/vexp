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
import java.awt.event.*;

/**
 * �berschrift:
 * Beschreibung:
 * Copyright:     Copyright (c) 2001
 * Organisation:
 * @author
 * @version 1.0
 */

public class ColorCanvas extends Canvas {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Image MyImage;
  MemoryImageSource Imagesource;
  int sizex=100;
  int sizey=100;
  int[] data=new int[2*sizex*sizey];
  int MyColor;
  ColorListener MyListener=null;

public Dimension getPreferredSize(){return new Dimension(2*sizex,sizey);}
public Dimension getMinimumSize(){return new Dimension(2*sizex,sizey);}

  public void addColorListener(ColorListener NewListener){
    MyListener=NewListener;
  }

   public void paint(Graphics g){
        //for(int yi=0;yi<size;yi++){for(int xi=0;xi<size;xi++){data[size*yi+xi]=(255 << 24) | (fact*height[xi][yi]) << 16 | (fact*height[xi][yi]) << 8 | (fact*height[xi][yi]);}}


        for(int i=0;i<sizey;i++){
            for(int k=0;k<sizex;k++){
              data[2*sizex*i+k]=Color.HSBtoRGB((float)(i/(double)sizey),(float)(k/(double)sizex),(float)(1.0));
            }

            for(int k=0;k<sizex;k++){
              data[2*sizex*i+k+sizex]=Color.HSBtoRGB((float)(i/(double)sizey),(float)(1.0),(float)((sizex-k)/(double)sizex));
            }
          }
          MyImage.flush();
          g.drawImage(MyImage,0,0,null);
   }

    public ColorCanvas() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      //ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
     Imagesource=new MemoryImageSource(2*sizex,sizey,data,0,2*sizex);
     MyImage=createImage(Imagesource);
    this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        this_mouseDragged(e);
      }
    });
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        this_mouseClicked(e);
      }
      public void mouseReleased(MouseEvent e) {
        this_mouseReleased(e);
      }
    });
  }

  void this_mouseClicked(MouseEvent e) {
    if(e.getX()<sizex){MyColor=Color.HSBtoRGB((float)(e.getY()/(double)sizey),(float)(e.getX()/(double)sizex),(float)(1.0));}
    else{              MyColor=Color.HSBtoRGB((float)(e.getY()/(double)sizey),(float)(1.0),(float)((2*sizex-e.getX())/(double)sizex));}
    MyListener.ColorEventPerformed(MyColor);
    }

  void this_mouseReleased(MouseEvent e) {
    this_mouseClicked(e);
  }

  void this_mouseDragged(MouseEvent e) {
    this_mouseClicked(e);
  }

}
