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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HistogrammCanvas extends Canvas implements MouseListener{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Graphics myg; 
byte[][] Data;
int[] Hist;
int dx=4,height=256,res=4,width=256,numpoints=256,plotlines=2;
int max=0;
int peakpos = 0;
Label PeakIndicator;

    public HistogrammCanvas(){
        setBackground(Color.lightGray);
        setForeground(Color.darkGray);
        setFont(new Font("Arial",Font.BOLD,12));
        //PeakIndicator = new Label("Peak=Button not pressed");
	    //add(PeakIndicator);
        Hist=new int[256];
        addMouseListener(this);
        }

     public Dimension getPreferredSize(){return new Dimension(256,256);}
     public Dimension getMinimumSize(){return new Dimension(256,256);}
     public void mouseReleased(MouseEvent e){}
     public void mouseClicked(MouseEvent e){}
     public void mouseExited(MouseEvent e){}
     public void mouseEntered(MouseEvent e){}
     public void mousePressed(MouseEvent e){
     
     
     //PeakIndicator.setText("Peak= "+e.getX());
   
     	peakpos=e.getX()-128;
     	//System.out.println("mouse-pressed "+peakpos);
     	this.repaint();
     
     }


    public void setData(byte[][] NewData){
        Data=NewData;
        max=0;
        for(int i=0;i<256;i++)for(int j=0;j<256;j++){
            Hist[Data[i][j]+128]++;
            if(Hist[Data[i][j]+128]>max){max=Hist[Data[i][j]+128];}
        }
        double factor=240.0/max;
        ////System.out.println("Faktor="+factor);
        for(int i=0;i<256;i++){Hist[i]=(int)(Hist[i]*factor);}
        repaint();
    }

  public void paint(Graphics g){
            height=getHeight();
            width=getWidth();
            numpoints=256/res;
            dx=width/numpoints;
            g.clearRect(0,0,dx+1,height);
            for(int xi=2;xi<numpoints;xi++){
              g.clearRect(dx*(xi-1)+1,0,dx,height);
              g.setColor(Color.red);
              g.drawLine(dx*(xi-1),254-Hist[res*(xi-1)],dx*xi,254-Hist[res*xi]);
              g.setColor(Color.darkGray);
              g.drawLine(dx*xi,256,dx*xi,254-Hist[res*xi]);
            }
            //g.setColor(Color.red);
            g.drawString("Peak= "+peakpos,3,15);
    }



}
