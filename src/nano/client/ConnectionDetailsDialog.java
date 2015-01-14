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

public class ConnectionDetailsDialog extends Dialog{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
String URL;
int EventPort,StreamPort;
TextField myURLField,myEventField,myStreamField;

    public ConnectionDetailsDialog(Frame MyFrame, String OldURL, int OldEventPort, int OldStreamPort){
        super(MyFrame,"Connection Details", true);
        setLayout(new GridLayout(4,1));
        URL=OldURL;EventPort=OldEventPort;StreamPort=OldStreamPort;
        myURLField=new TextField(URL,40);
        myEventField=new TextField(""+EventPort,7);
        myStreamField=new TextField(""+StreamPort,7);
        Button OKButton=new Button("OK");
        OKButton.addActionListener(new OKListener());
        Button CancelButton=new Button("Cancel");
        CancelButton.addActionListener(new CancelListener());
        Panel Panel1=new Panel();
        Panel1.setLayout(new FlowLayout());
        Panel1.add(new Label("URL"));
        Panel1.add(myURLField);
        add(Panel1);

        Panel Panel2=new Panel();
        Panel2.setLayout(new FlowLayout());
        Panel2.add(new Label("Event Port"));
        Panel2.add(myEventField);
        add(Panel2);

        Panel Panel3=new Panel();
        Panel3.setLayout(new FlowLayout());
        Panel3.add(new Label("Stream Port"));
        Panel3.add(myStreamField);
        add(Panel3);

        Panel Panel4=new Panel();
        Panel4.setLayout(new FlowLayout());
        Panel4.add(OKButton);
        Panel4.add(CancelButton);
        add(Panel4);

        pack();
        setVisible(true);
    }

    public String getURL(){return URL;}
    public int getStreamPort(){return StreamPort;}
    public int getEventPort(){return EventPort;}

    public void OK(){
        URL=myURLField.getText();
        EventPort=Integer.parseInt(myEventField.getText());
        StreamPort=Integer.parseInt(myStreamField.getText());
        Cancel();
    }

    public void Cancel(){
        dispose();
    }

    public class OKListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                OK();
            }
        }
    public class CancelListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                Cancel();
            }
        }
}
