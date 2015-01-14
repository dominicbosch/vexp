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
import java.io.*;
import Acme.JPM.Encoders.GifEncoder;


public class ImageFrame extends Frame{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
ImageCanvas MyImageCanvas;


    public ImageFrame(String name){
    super(name);
    MyImageCanvas=new ImageCanvas();
    add(MyImageCanvas);
    setVisible(true);
    pack();
    setResizable(false);
    addWindowListener(new ImageFrameListener());
    setMenuBar(new ImageFrameMenu());
    }

    public void kill(){dispose();}

    public void setImages(MemoryImageSource[] Newlinesource){
        MyImageCanvas.setImages(Newlinesource);
    }

    class ImageFrameMenu extends MenuBar{
       /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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

    class ImageFrameListener extends WindowAdapter{
        public void windowClosing(WindowEvent e){
        kill();
        }
    }


   class ImageCanvas extends Canvas{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
Image MyImages[]=new Image[256];

        public Dimension getPreferredSize(){return new Dimension(256,256);}
        public Dimension getMinimumSize(){return new Dimension(256,256);}

        public void setImages(MemoryImageSource[] Newlinesource){
            for(int i=0;i<256;i++){MyImages[i]= createImage(Newlinesource[i]);}
            repaint();
        }

        public void save(){
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

        public void paint (Graphics g) {
        for(int i=0;i<256;i++){
            g.drawImage(MyImages[i],0,i,null);
            }
        }
   }
}
