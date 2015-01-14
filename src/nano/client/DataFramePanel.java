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
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.JButton;

import nano.awt.color.*;



public class DataFramePanel extends Panel{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
DataCanvas MyDataCanvas;
VerlaufCanvas MyVerlauf;
ColorScala MyColorScala;
LineSectionData MyLineSectionData;
HistogrammCanvas MyHistogrammCanvas;
byte[][] Data=new byte[256][256];

    public DataFramePanel(){
    super();
    setLayout(new FlowLayout());
    MyColorScala=new ColorScala();
    MyDataCanvas=new DataCanvas();
    add(MyDataCanvas);
    DataPanel MyDataPanel=new DataPanel(MyDataCanvas);
    add(MyDataPanel);
    MyLineSectionData=new LineSectionData();
    add(MyLineSectionData);
    MyHistogrammCanvas=new HistogrammCanvas();
    add(MyHistogrammCanvas);
    }


    public void setData(byte[][] NewData){
        for(int i=0;i<256;i++){for(int j=0;j<256;j++){
          Data[i][j]=NewData[i][j];
        }}
        MyHistogrammCanvas.setData(Data);
    }

    public double getSum(int obenlinksx, int obenlinksy, int untenrechtsx, int untenrechtsy){
        obenlinksx=Math.max(obenlinksx,0);
        obenlinksy=Math.max(obenlinksy,0);
        untenrechtsx=Math.min(untenrechtsx,256);
        untenrechtsy=Math.min(untenrechtsy,256);
        double sum=0;
        for(int i=obenlinksy;i<=untenrechtsy;i++){for(int j=obenlinksx;j<=untenrechtsx;j++){
            sum+=Data[i][j];
        }}
        return sum;
    }

    public void setLineData(byte[] NewData){MyLineSectionData.setData(NewData);}

    public byte[] getLineSection(int x0, int y0, int x1, int y1){
        int xi,yi;
        byte[] LineSection=new byte[256];
        double dx=(x1-x0)/265.0;
        double dy=(y1-y0)/265.0;
        for(int i=0;i<256;i++){
          xi=x0+(int)(i*dx);
          yi=y0+(int)(i*dy);
          LineSection[i]=Data[yi][xi];
        }
        return LineSection;
    }

  public void changeColorScala() {
    ColorScala TheNew=FarbverlaufDialog.getColorScala(new Frame(),MyColorScala);
    MyColorScala=TheNew;
    MyVerlauf.setColorScala(TheNew);
    MyDataCanvas.repaint();
    MyVerlauf.repaint();
  }


    public double getMedian(){return 0.4;}
    public double getSigma(){return 0.4;}

// ======================== AWT-GUI =============================
/*    class ImageFrameMenu extends MenuBar{
       public ImageFrameMenu(){
         Menu m=new Menu("File");
         MenuItem SaveItem=new MenuItem("Save");
         SaveItem.addActionListener(new SaveListener());
         m.add(SaveItem);
         add(m);
       }
    }

    class SaveListener implements ActionListener{

        public void actionPerformed(ActionEvent e){
            MyImageCanvas.save();
        }
    }
*/


   class DataPanel extends Panel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DataCanvas MyCanvas;
        public DataPanel(DataCanvas NewDataCanvas){
           MyCanvas=NewDataCanvas;
           JButton SumButton = new JButton("Sum");
           SumButton.addActionListener(new SumListener());
           add(SumButton,null);
           JButton ChangeColorButton = new JButton("Change Color");
           ChangeColorButton.addActionListener(new ChangeColorListener());
           add(ChangeColorButton,null);
           MyVerlauf=new VerlaufCanvas(MyColorScala);
           add(MyVerlauf);
           }
   }

    class SumListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //System.out.println("Summe:"+getSum(0,0,255,255));
        }
    }

    class ChangeColorListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            changeColorScala();
        }
    }

   class DataCanvas extends Canvas implements MouseListener{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Image MyImages[]=new Image[256];
   int AlreadyPoints=0;
   int[] Pointx=new int[2];
   int[] Pointy=new int[2];

        public DataCanvas(){
          addMouseListener(this);
        }

        public Dimension getPreferredSize(){return new Dimension(256,256);}
        public Dimension getMinimumSize(){return new Dimension(256,256);}

        public void mouseReleased(MouseEvent e){}
        public void mouseClicked(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mousePressed(MouseEvent e){
        AlreadyPoints++;
        ////System.out.println("Already "+AlreadyPoints+" Points");
        if(AlreadyPoints>=3){
          AlreadyPoints=1;
          }
        Pointx[AlreadyPoints-1]=e.getX();
        Pointy[AlreadyPoints-1]=e.getY();
        //if(AlreadyPoints==2){getGraphics().drawLine(Pointx[0],Pointy[0],Pointx[1],Pointy[1]);}
        repaint();
        if(AlreadyPoints==2){
        setLineData(getLineSection(Pointx[0],Pointy[0],Pointx[1],Pointy[1]));
        }
        }

/*        public void save(){
            Image offscreen=createImage(256,256);
            Graphics g=offscreen.getGraphics();
            for(int i=0;i<256;i++){
                g.drawImage(MyImages[i],0,i,null);
            }
            try{
            FileOutputStream out=new FileOutputStream(new File("MeinBildli.gif"));
            GifEncoder encoder=new GifEncoder(offscreen,out);
            encoder.encode();
            out.close();
            }catch(IOException e){
            	//e.printStackTrace();
            }
        }
*/
        public void paint (Graphics g) {
        Image LineImage;
        final int Radius=3;
        int[] LineData=new int[256*256];
            for(int i=0;i<256;i++){
                for(int j=0;j<256;j++){
                  LineData[256*i+j]=MyColorScala.getColor(Data[i][j]+128);
                }
            }
            MemoryImageSource linesource=new MemoryImageSource(256,256,LineData,0,256);
            //LineImage.flush();
            LineImage = createImage(linesource);
            g.drawImage(LineImage,0,0,null);
            if(AlreadyPoints==1){
                g.setColor(Color.red);
                g.fillOval(Pointx[0]-Radius,Pointy[0]-Radius,2*Radius,2*Radius);
                g.setColor(Color.white);
                g.drawOval(Pointx[0]-Radius,Pointy[0]-Radius,2*Radius,2*Radius);
            }

            if(AlreadyPoints==2){
                g.setColor(Color.red);
                g.drawLine(Pointx[0],Pointy[0],Pointx[1],Pointy[1]);
                g.fillOval(Pointx[0]-Radius,Pointy[0]-Radius,2*Radius,2*Radius);
                g.fillOval(Pointx[1]-Radius,Pointy[1]-Radius,2*Radius,2*Radius);
                g.setColor(Color.white);
                g.drawOval(Pointx[0]-Radius,Pointy[0]-Radius,2*Radius,2*Radius);
                g.drawOval(Pointx[1]-Radius,Pointy[1]-Radius,2*Radius,2*Radius);
            }
        }
   }

public class LineSectionData extends Canvas{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Graphics myg;
byte[] Data1;
int dx=4,height=256,res=4,width=256,numpoints=256,plotlines=2;

    public LineSectionData(){
        setBackground(Color.black);
        setForeground(Color.green);
        setFont(new Font("Arial",Font.BOLD,20));
        }

     public Dimension getPreferredSize(){return new Dimension(256,256);}
     public Dimension getMinimumSize(){return new Dimension(256,256);}

    public void setData(byte[] NewData){Data1=NewData;MyPaint();}

  public void MyPaint(){
            height=getHeight();
            width=getWidth();
            numpoints=256/res;
            dx=width/numpoints;
            if(myg==null){
              myg=getGraphics();
            }
            else
            {if (Data1!=null){
            myg.clearRect(0,0,dx+1,height);
            for(int xi=2;xi<numpoints;xi++){
              myg.clearRect(dx*(xi-1)+1,0,dx,height);
              myg.drawLine(dx*(xi-1),Data1[res*(xi-1)]+(height/2),dx*xi,Data1[res*xi]+(height/2));
              }
            }}
    }
  public void paint(Graphics g){
        MyPaint();
  }


}
public static void main(String args[]){
Frame MyFrame=new Frame();
DataFramePanel MyPanel=new DataFramePanel();
MyFrame.add(MyPanel);
//MyPanel.setData(new byte[][]);
MyFrame.pack();
MyFrame.setVisible(true);
}


}
