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
package nano.db;

import java.util.Vector;
 public interface DB_inquiryDesk{
 
//general quering methodes:
 public int getEventPort(String RoomName);
 public int getStreamPort(String RoomName);
 public String getExpTypName(String RoomName);
 public Vector getConnectedUsers();
 public Vector getConnectedIp();
 public void cleanup();


// Methodes used to handle meetings:
// public void setSimulatortype(String Simulatortyp);
 public boolean newClient(int AppletID) throws AppletIDException;
 // return a boolean wheter there is a set to load
// true: the actual parametrset is in db and sould be loaded
// false: there is no parameterset in db or an other applet is already
//        running. Actual parameters of Simulator sould be saved in db.
 public void removeClient(int AppletID);
// public void removeClientbyIP(String ip);
public int getAppletId(String IP);
 public void endMeeting();

// public int getAppletID(String Username);
// public String[] getNamesInRoom();

// Methodes to handle parameters
 public void saveParameter(String label, String value, String type);
 public String loadParameter(String label);
 public void importParameterSetByName(String theName);
 public void exportParameterSetByName(String theName);

 public boolean isError();
 public String getErrorDescription();

 //public int getAppletID(String Username);
 //public String[] getNamesInRoom();
 //public void setSimulatortype(String Simulatortyp);
 //public String getSimulatortype();
 
}
