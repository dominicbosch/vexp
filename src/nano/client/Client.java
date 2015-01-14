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
import java.awt.event.*;
import nano.net.*;

/**
*
* @author Tibor Gyalog
* @version 1.1 16.08.01 (1.1: Panels neu angeordnet(seitwärts, war untereinander))
*/

public class Client extends Frame implements SSStateListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NetObserver Myimage;
    LineSection MyLiner;
    String URL;int EventPort,StreamPort,AppletID;

    public Client(String InitialURL, int InitialEventPort, int InitialStreamPort, int InitialID){
        //Darstellung
        URL=InitialURL;
        EventPort=InitialEventPort;
        StreamPort=InitialStreamPort;
        AppletID=InitialID;
        setSize(600, 315);
        setLayout(new BorderLayout());
        setMenuBar(new MainMenu());
        NewConnection();
        }


    public void NewConnection(){
        //Netzanbindung via Socket
        removeAll();
        //Daten-Stream
        StreamClientSocket MyStreamClientSocket = new StreamClientSocket(URL, StreamPort);
        Myimage=new NetObserver();
        MyLiner=new LineSection();
        MyStreamClientSocket.PlugAnzeige(Myimage);
        MyStreamClientSocket.PlugAnzeige(MyLiner);
        MyStreamClientSocket.addSSStateListener(this);
        //Event-Stream
        System.out.println(URL+" "+EventPort+" "+AppletID);
        NetSimPanel MyNetSimPanel=new NetSimPanel(URL, EventPort, AppletID);
        ImagePanel MyImagePanel=new ImagePanel(Myimage);
        
        //Darstellung
        Panel myPanel=new Panel();
        myPanel.setLayout(new FlowLayout());
        myPanel.add(Myimage);
        myPanel.add(MyLiner);
        add("Center",myPanel);
        //Panel NewPanel=new Panel();
        add("South",MyNetSimPanel);
        
        add("North",MyImagePanel);


        //addWindowListener(new WindowClosingListener());


        setVisible(true);
        pack();
        
        MyNetSimPanel.put("command=getgui");
          
    
    }

        public void ConnectionDetails(){
                ConnectionDetailsDialog MyDialog=new ConnectionDetailsDialog(this,URL,EventPort,StreamPort);
              //  System.out.println("So isses.");
                URL=MyDialog.getURL();
                EventPort=MyDialog.getEventPort();
               // System.out.println(EventPort);
                StreamPort=MyDialog.getStreamPort();
                }

        public class ConnectionDetailsListener implements
        ActionListener{
            public void actionPerformed(ActionEvent e){
                ConnectionDetails();
            }
        }

        public class ExitListener implements
        ActionListener{
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        }


        public class MainMenu extends MenuBar{
        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		public MainMenu(){
            Menu m=new Menu("Connection");
            MenuItem NCmi=new MenuItem("Connection...");
            NCmi.addActionListener(new ConnectionDetailsListener());
            MenuItem Exmi=new MenuItem("Exit");
            Exmi.addActionListener(new ExitListener());
            m.add(NCmi);
            m.add(Exmi);
            add(m);
            }
        }



   public void newStreamSocket(StreamSocket NewSocket){}

    public void SSStateChanged(boolean ok, String State, int ID){
        //System.out.println("SS New State: "+State);
    }

    public static void main(String args[]){
    String MyURL;int MyEventPort, MyStreamPort, MyAppletID;
        try{MyURL=args[0];
        MyEventPort=Integer.parseInt(args[1]);
        MyStreamPort=Integer.parseInt(args[2]);
        MyAppletID=Integer.parseInt(args[3]);
        new Client(MyURL, MyEventPort, MyStreamPort, MyAppletID);}
        catch(IndexOutOfBoundsException e){
           //System.out.println("Usage:");
           //System.out.println("java nano.client.Client URL EventPort StreamPort AppletID");
           //System.out.println("No '-' or ',' required, just spaces. ");
        }

    }


}
