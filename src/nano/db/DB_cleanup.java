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
import java.sql.*;


/**
* 
* @author Dino Keller
* @version 1.0 6.5.2002
*/

public class DB_cleanup{

  private Statement mystatement;
 
	
	
  public DB_cleanup(Statement mystatement){
    this.mystatement=mystatement;
    
  }
  
  public void cleanUnusedApplets(){
    try{
      ResultSet sqlresult;
      sqlresult = mystatement.executeQuery(
	    "Select AppletID from Applet left join on "
	    +" Applet.RunningID=RoomMeeting.RunningSimID"
	    +" where RoomMeeting.RoomMeetingID is null;");
      //System.out.println("Applets to kill:");
      while (sqlresult.next()){
	//System.out.println("Applet "+sqlresult.getInt("AppletID") );
      }
    }catch(SQLException sqle){
         System.err.println(sqle.getMessage());
         //sqle.printStackTrace();
    }
  }
	
}
