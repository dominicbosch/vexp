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
import java.io.*;
import java.sql.*;
import java.util.*;


/**
* Implementation of DB_inquiryDesk which is based on the database design and concept 
* of neuewelt1_6. @see <a href="http://nano-world.unibas.ch/nano/intern/linux/neuewelt">http://nano-world.unibas.ch/nano/intern/linux/neuewelt</a> 
* <p>
* It follows a concept including Projects and Meetings.
* When the first client is evoked a new Meetin is starte and the internal fields
* ExperimentTyp, RoomMeetingID, ProjectID, RunningSimID and LastSimID are initialized.
* endMeeting() and all the methodes involving parameters make use of these fiels.
* If a meeting is stoped by endMeeting() all the fields except ExperimentType
* are set to -1.
* <p>
* Virtual files: If a set of parameters is exported, it is saved in a virtual file.
* A virtual file is identified uniqly by its name and the project it belongs to.
*
* @author Dino Keller
* @version 1.1  13.8.2002
*/
public class SQL_inquiryDesk implements DB_inquiryDesk {
	private boolean Error = false;
	private String ErrorDescription = null;
	private String ExperimentTyp = null;
	//    private Connection dbconnection=null;
	private Statement sqlstatement = null;
	private int RoomMeetingID = -1;
	private int RunningSimID;
	private int LastSimID;
	private int ProjectID = -1;
	/**
	* Builds a new SQL_inquiryDesk connecting to MySQL database using parameters in passwordfile.
	* Syntax of password file: URL:database_name:username:password.
	* <p>
	* Class org.gjt.mm.mysql.Driver is needed as it is loaded dynamically.
	* 
	* @param  pwdFileName  a String giving the name of of the file containing URL, name of database,
	*                      username and password of Database.
	*/
	public SQL_inquiryDesk(String pwdFileName) {
		//System.out.println("Try to connect DB ..." + pwdFileName);
		sqlstatement = connectDB(pwdFileName);
		// This is to build up db connection.
		if (sqlstatement != null) {
			cleanup_RoomMeeting(); //cleanup if the system uses DB
		} else
			System.out.println("Server running without DB");
	} // end of constructor
	/**
		 * Deletes Row in RoomMeeting table with no
		 * RunningSimID or LastSimID
		 */
	private void cleanup_RoomMeeting() {
		try {
			String querystring =
				"delete from RoomMeeting where LastSimID is null or RunningSimID is null;";
			ResultSet sqlresult = sqlstatement.executeQuery(querystring);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while deleting broken roommetings.\n"
					+ sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
	}
	public void cleanup() {
		cleanup_RoomMeeting();
	}
	/**
	* Returns the uid of the working users
	* 
	* @return           : Vector of  String with the uids
	* @author guggisberg
	*                  
	*/
	public int getAppletId(String IP) {
		int id = 0;
		try {
			ResultSet sqlresult;
			//System.out.println("Project ID:"+ProjectID);
			String querystring =
				"select UserID from Session "
					+ "where IP='"
					+ IP
					+ "' and Session_running='y';";
			sqlresult = sqlstatement.executeQuery(querystring);
			// System.out.println("SQL_RES:"+sqlresult);
			//sqlresult.absolute(1);
			//connected_users=sqlresult.getString("UserID");
			while (sqlresult.next()) {
				//removeclient from Applet table; 
				id = sqlresult.getInt("UserID");
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving UserID.\n" + sqle.getMessage();
			System.err.println(
				"SQL Error in removeClientbyIP part I applet table");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return id;
	}
	/**
	* Returns the uid of the working users
	* 
	* @return           : Vector of  String with the uids
	* @author guggisberg
	*                  
	*/
	public Vector getConnectedUsers() {
		Vector connected_users = new Vector();
		try {
			ResultSet sqlresult;
			//System.out.println("Project ID:"+ProjectID);
			String querystring =
				"select UserID from UserProject "
					+ "where ProjectID='"
					+ ProjectID
					+ "';";
			sqlresult = sqlstatement.executeQuery(querystring);
			// System.out.println("SQL_RES:"+sqlresult);
			//sqlresult.absolute(1);
			//connected_users=sqlresult.getString("UserID");
			while (sqlresult.next()) {
				String s = sqlresult.getString("UserID");
				connected_users.add(s);
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving UserID.\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return connected_users;
	}
	/**
		  * Returns the IP of the working users
		  * 
		  * @return           IP number in a Vector of String;
		  *                   
		  * @author guggisberg
		  */
	public Vector getConnectedIp() {
		Vector connected_ip = new Vector();
		String sql_snippet = "";
		try {
			Vector connected_user = getConnectedUsers();
			Iterator it = connected_user.iterator();
			while (it.hasNext()) {
				String uid = (String) it.next();
				if (it.hasNext()) {
					sql_snippet += "UserID=" + uid + " or ";
				} else {
					sql_snippet += "UserID=" + uid;
				}
			}
			ResultSet sqlresult;
			//System.out.println("Project ID:"+ProjectID);
			String querystring =
				"select IP from Session where ("
					+ sql_snippet
					+ ") and Session_running='y';";
			sqlresult = sqlstatement.executeQuery(querystring);
			System.out.println("query:" + querystring);
			while (sqlresult.next()) {
				String s = sqlresult.getString("IP");
				connected_ip.add(s);
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving UserID.\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return connected_ip;
	}
	/**
	* Returns the Stream Port number belonging to the DB entry corresponding to RoomName.
	* 
	* @param  RoomName  a String giving the name of the Room for which the Stream Port should
	*                   be looked up in the DB.
	* @return           the StreamPort entry from Table Room with RoomName corresponding to the passed parameter.
	*/
	public int getStreamPort(String RoomName) {
		int StreamPort = -1;
		try {
			ResultSet sqlresult;
			sqlresult =
				sqlstatement.executeQuery(
					"Select StreamPort from Room "
						+ "where RoomName='"
						+ RoomName
						+ "';");
			sqlresult.absolute(1);
			StreamPort = sqlresult.getInt(1);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving StreamPort:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return StreamPort;
	}
	/**
	* Returns the Event Port number belonging to the DB entry corresponding to RoomName.
	* 
	* @param  RoomName  a String giving the name of the Room for which the Event Port should
	*                   be looked up in the DB.
	* @return           the EventPort entry from Table Room with RoomName corresponding to the passed parameter.
	*/
	public int getEventPort(String RoomName) {
		int EventPort = -1;
		try {
			ResultSet sqlresult;
			sqlresult =
				sqlstatement.executeQuery(
					"Select EventPort from Room "
						+ "where RoomName='"
						+ RoomName
						+ "';");
			sqlresult.absolute(1);
			EventPort = sqlresult.getInt(1);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving EventPort:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return EventPort;
	}
	// methodes for specific roommeeting
	//sets ExpermentTyp
	//If there is no entry in table ExperimentTyp and
	//if there is no table called by the name of 
	//the Simulatortyp, they will be insert / created.
	/*public void setSimulatortype(String Simulatortyp){ 
	  ExperimentTyp=Simulatortyp;
	  try{
	    ResultSet sqlresult;
	    sqlresult = sqlstatement.executeQuery(
	        "Select ExperimentTypID from ExperimentTyp "+
	         "where ExpTypName='"+Simulatortyp+"';");
	    sqlresult.first();
	    if (!sqlresult.isFirst() ) {
	    }
	  }catch(SQLException sqle){
	    Error=true;
	    ErrorDescription="Error while retrieving Simulatortyp '"+Simulatortyp
	      +"' out of Database.";
	    System.err.println(sqle.getMessage());
	  } 
	}
	*/
	/**
	* Register new Applet in DB, check validity of AppletID and return boolean which indicates
	* wheter there are parameter in DB to be loaded.
	* <p>
	* If there is no other client present, initialize RoomMeeting. The instance of SQL_inquiryDesk
	* is initalised with ExpTypName from Table ExperimentTyp, RoomMeetingID from RoomMeeting
	* and ProjectID from Project - all corresponding to the RoomMeeting the parameter
	* AppletID is belonging to. Two sets of parameters are created with ID's RunningSimID and LastSimID.
	* <p>
	* Throws AppletIDException if AppletID cannot be found in DB. Throws AppletIDException if
	* RoomMeetingID is already initialized, but with a number not corresponding to the 
	* RoomMeetingID belonging to this AppletID.
	* <p>
	* If new Meeting has to be initialized and VirtualFile is exsisting from previous Meeting
	* within the same Project: import this VirtualFile (Copy it into new ParameterSet and
	* save the ID of this new ParameterSet in RunningSimID in analogy to importParameterSetByName()).
	* Return true to indicate that this ParameterSet should be loaded from DB to the Memory of the Simulator.
	* 
	* @param  AppletID           The AppletID which sould be passed from the DB to the Applet 
	*                            and from there to the Server
	* @return           	     a boolean. True if there is a set of parameters in the DB which sould
	*                   	     be loaded by the Simulator using loadParameter for all the possible parameters.
	* @throws AppletIDException  If AppletID cannot be found in the DB or SQL_inquiryDesk is initialised with a 
	*                            RoomMeetingID which is different from RoomMeetingID corresponding to AppletID.
	*/
	public boolean newClient(int AppletID) throws AppletIDException {
		/* 1. test, if AppletID is correct.
		 * 2. for first Client: initialize RoomMeeting
		 * 3. retun a boolan load: true if the simulator should
		 *    load the parameters out of DB, otherwise false.
		 */
		boolean load = false;
		//System.out.println("I am in SQl_inquiryDesk newClient");
		try {
			int tempRoomMeetingID;
			ResultSet sqlresult;
			sqlresult =
				sqlstatement.executeQuery(
					"Select RoomMeetingID from Applet where AppletID="
						+ AppletID
						+ ";");
			sqlresult.first();
			if (!sqlresult.isFirst())
				throw new AppletIDException("This Applet is not belonging to any Room Meeting");
			tempRoomMeetingID = sqlresult.getInt("RoomMeetingID");
			if (tempRoomMeetingID < 1)
				throw new AppletIDException("This Applet is not belonging to a valide Room Meeting");
			// Initialisation
			if (RoomMeetingID == -1)
				load = initializeRoomMeeting(AppletID);
			else {
				// check:
				if (tempRoomMeetingID != RoomMeetingID)
					throw new AppletIDException(
						"corrupted RoomMeetingID:\nThe Applet "
							+ AppletID
							+ " does not belong to RoomMeeting "
							+ RoomMeetingID);
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while trying to insert new Client:\n"
					+ sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return load;
	}
	public void removeClient(int AppletID) {
		System.out.println("start remove Client \n");
		//	Do not kill Session during unconnection 
		//	try{
		//		   ResultSet sqlresult;
		//		   //System.out.println("Project ID:"+ProjectID);
		//		   String querystring="select UserID from Applet "+
		//			"where AppletID="+AppletID+" and Endtime is null;";
		//		   sqlresult = sqlstatement.executeQuery(querystring);
		//		   System.out.println("SQL_RES:"+sqlresult+"\n");
		//		   System.out.println("queristring:"+querystring+"\n");
		//		   //sqlresult.absolute(1);
		//		   //connected_users=sqlresult.getString("UserID");
		//		   
		//
		//		   while (sqlresult.next()) {
		//			   //removeclient from Applet table; 
		//			   String id = sqlresult.getString("UserID");
		//			   removeSession(Integer.parseInt(id));
		//			   System.out.println("remove Session with ID:"+id+"\n");
		//		   }
		//		 }catch(SQLException sqle){
		//		   Error=true;
		//		   ErrorDescription="Error while retrieving UserID.\n"
		//			  +sqle.getMessage();
		//			System.err.println("SQL Error in removeClient part I applet table");
		//			System.err.println(ErrorDescription);
		//			//sqle.printStackTrace();
		//		 }	 
		//      
		try {
			String s =
				"Update Applet "
					+ "set EndTime=now() where AppletID="
					+ AppletID
					+ ";";
			//System.out.println(s);
			sqlstatement.executeUpdate(s);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error: set endtime in update applet\n" + sqle.getMessage();
			System.err.println(
				"SQL Error removeClientbyIP par II applet table");
			System.err.println(ErrorDescription);
		}
	}
	/**
	 * @param id  User ID
	 * @author guggisberg
	 */
	private void removeSession(int id) {
		try {
			String s =
				"Update Session "
					+ "set Session_running='n' where UserID="
					+ id
					+ ";";
			//System.out.println(s);
			System.out.println("Hier wird die Session beendet: \n");
			System.out.println(s);
			sqlstatement.executeUpdate(s);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription = "Error in remove Session:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
		}
	}
	private void removeClientbyIP(String ip) {
		try {
			ResultSet sqlresult;
			//System.out.println("Project ID:"+ProjectID);
			String querystring =
				"select UserID from Session " + "where IP='" + ip + "';";
			sqlresult = sqlstatement.executeQuery(querystring);
			// System.out.println("SQL_RES:"+sqlresult);
			//sqlresult.absolute(1);
			//connected_users=sqlresult.getString("UserID");
			while (sqlresult.next()) {
				//removeclient from Applet table; 
				int id = sqlresult.getInt("UserID");
				removeClient(id);
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving UserID.\n" + sqle.getMessage();
			System.err.println(
				"SQL Error in removeClientbyIP part I applet table");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		try {
			String s =
				"Update Session "
					+ "set Session_running='n' where IP="
					+ ip
					+ ";";
			//System.out.println(s);
			sqlstatement.executeUpdate(s);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error closing RoomMeeting:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
		}
	}
	/**
	* Set EndTime in table RoomMeeting for current Meeting, save current Parameters in a virtual file
	* and reset state of Fields in instance of SQL_inquiryDesk. 
	* The Fields RoomMeetingID, ProjectID, RunningSimID and LastSimID are set to -1. 
	* If another client is calling {@link nano.db.SQL_inquiryDesk#newClient newClient}, 
	* a new Meeting has to be initialized. 
	*/
	public void endMeeting() {
		try {
			sqlstatement.executeUpdate(
				"Update RoomMeeting "
					+ "set EndTime=now() where RoomMeetingID="
					+ RoomMeetingID
					+ ";");
			exportParameterSetByName(".lastMeeting");
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error closing RoomMeeting:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		RoomMeetingID = -1;
		RunningSimID = -1;
		LastSimID = -1;
		ProjectID = -1;
	}
	/*public String[] getNamesInRoom(){
	    Vector Userlist= new Vector(8);
	    String[] returnlist;
	    try{
	       ResultSet sqlresult;
	       sqlresult = sqlstatement.executeQuery(
	           "Select Realname from User, Applet "+
	       "where Applet.RoomMeetingID="+RoomMeetingID
	       +" and User.UserID=Applet.UserID;");
	       while(sqlresult.next()) Userlist.add(sqlresult.getString("Realname"));
	    }catch(SQLException sqle){
	     Error=true;
	     ErrorDescription="Error while retrieving names of the users:\n"
	      +sqle.getMessage();
	         System.err.println("SQL Error");
	         System.err.println(sqle.getMessage());
	         sqle.printStackTrace();
	    }
	
	    returnlist=new String[Userlist.size()];
	    for (int i=0;i<returnlist.length;i++)
	         returnlist[i]=(String)Userlist.elementAt(i);
	
	    return returnlist;
	}*/
	/* public int getAppletID(String Username){
	    int AppletID=-1;
	    try{
	       ResultSet sqlresult;
	       sqlresult = sqlstatement.executeQuery(
	           "Select AppletID from User, Applet, RoomMeeting "+
	       "where User.Realname='"+Username+"'"
	       +" and User.UserID=Applet.UserID"
	       +" and Applet.RoomMeetingID="+RoomMeetingID+";");
	       sqlresult.absolute(1);
	       AppletID = sqlresult.getInt(1);
	    }catch(SQLException sqle){
	     Error=true;
	     ErrorDescription="Error while retrieving names of the users:\n"
	      +sqle.getMessage();
	         System.err.println("SQL Error");
	         System.err.println(sqle.getMessage());
	         sqle.printStackTrace();
	    }
	    return AppletID;
	}*/
	//public String getSimulatortype(){ return ExperimentTyp;}
	/**
	* Save value for parameter named by label in the parameter set which contains the current parameters.
	* A meeting has to be initialized, which is done by newClient. The parameter is saved in the
	* table named after the Experiment type and in the row referd to by RunningSimID
	* (which is initialized by newClient).
	*/
	public void saveParameter(String label, String value, String type) {
		//System.out.println("I am in SQL_inquiryDesk saveParameter");
		try {
			String updatestring =
				"Update "
					+ ExperimentTyp
					+ " set "
					+ ExperimentTyp
					+ "."
					+ label
					+ " = "
					+ value
					+ " where SimulationID="
					+ RunningSimID;
			sqlstatement.executeUpdate(updatestring);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while saving Parameter "
					+ label
					+ "of "
					+ ExperimentTyp
					+ ":\n"
					+ sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
	}
	/**
	* Returns the value for parameter named after label from the set of runnning parameters.
	* This set ist found in the table named after Experiment type and in the row referd to by
	* RunningSimID (which is initialized by newClient). A meeting has to be initialized
	* (by calling newClient).
	* The value is returned as String whatever datatype it might be.
	* 
	* @param  label  a String indicating the name of the column in which the parameter is saved in the DB.
	* @return        a String contaning the actual value of the parameter named by label.
	*/
	public String loadParameter(String label) {
		//System.out.println("I am in SQL_inquiryDesk loadParameter()");
		try {
			ResultSet sqlresult;
			sqlresult =
				sqlstatement.executeQuery(
					"Select "
						+ label
						+ " from "
						+ ExperimentTyp
						+ ", RoomMeeting "
						+ " where RoomMeeting.RunningSimID = "
						+ ExperimentTyp
						+ ".SimulationID "
						+ " and RoomMeeting.RoomMeetingID = "
						+ RoomMeetingID
						+ ";");
			sqlresult.absolute(1);
			return sqlresult.getString(1);
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while looking up Parameter "
					+ label
					+ "of "
					+ ExperimentTyp
					+ " in Database:\n"
					+ sqle.getMessage();
			System.err.println("SQL Error" + ErrorDescription);
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return null;
	}
	/**
	* Saves the set of parameters used for saving running parameters in a virtual file named afer theName.
	* A virtual file is specified by the VirtualFileName and the ProjectID of the project it is belonging to.
	* If there is already a virtual file with name theName existing which belongs to the current project,
	* it will be overwritten. A meeting has to be initialized (done by newClient) as a reference to
	* the running parameters (called RunningSimID) is used and the virtual files are attributed
	* to the current project.
	* <p>
	* Pay attention to save all the parameters before calling this methode,
	* otherwise you will export deprecated parameters.
	* 
	* @param  theName  a String giving a name to the virtual file. The file will be overwritten when existing.
	* 
	*/
	public void exportParameterSetByName(String theName) {
		//System.out.println("I am in SQL_inquiryDesk exportParameterSetByName()");
		try {
			int columns;
			String columnname;
			String insertString;
			ResultSet sqlresult;
			ResultSetMetaData metadata;
			sqlstatement.execute(
				"lock tables Applet read, VirtualFile write, "
					+ ExperimentTyp
					+ " write, ExperimentTyp read, RoomMeeting write;");
			// Give name to running Parameterset:
			// check, if virtual file is allready existing
			// and update it / insert a new one.
			sqlresult =
				sqlstatement.executeQuery(
					"Select VirtualFileID "
						+ " from VirtualFile"
						+ " where ProjectID="
						+ ProjectID
						+ " and VirtualFilename='"
						+ theName
						+ "';");
			sqlresult.first();
			if (sqlresult.isFirst()) {
				int VirtualFileID = sqlresult.getInt("VirtualFileID");
				sqlresult =
					sqlstatement.executeQuery(
						"Select ExperimentTypID"
							+ " from ExperimentTyp where ExpTypName='"
							+ ExperimentTyp
							+ "';");
				sqlresult.first();
				insertString =
					"Update VirtualFile"
						+ " set SimulationID="
						+ RunningSimID
						+ ", "
						+ " ExperimentTypID="
						+ sqlresult.getInt("ExperimentTypID")
						+ ","
						+ " Date=now()"
						+ " where VirtualFileID="
						+ VirtualFileID
						+ ";";
			} else {
				insertString =
					"Insert into VirtualFile "
						+ "(SimulationID, ExperimentTypID, ProjectID, VirtualFileName, Date) "
						+ " select "
						+ RunningSimID
						+ " as SimulationID,  "
						+ " ExperimentTypID, "
						+ ProjectID
						+ " as ProjectID, '"
						+ theName
						+ "' as VirtualFileName, now() as Date"
						+ " from  ExperimentTyp "
						+ "where ExpTypName='"
						+ ExperimentTyp
						+ "';";
			}
			//System.out.println(insertString);
			sqlstatement.executeUpdate(insertString);
			// Creating new ParameterSet for running parameters
			insertString = "insert into " + ExperimentTyp + " set ";
			sqlresult =
				sqlstatement.executeQuery(
					"select * from "
						+ ExperimentTyp
						+ " where SimulationID="
						+ RunningSimID
						+ ";");
			sqlresult.first();
			metadata = sqlresult.getMetaData();
			columns = metadata.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				columnname = metadata.getColumnName(i);
				if (!columnname.equals("SimulationID")) {
					insertString += columnname
						+ "="
						+ sqlresult.getString(columnname);
					if (i != columns)
						insertString += ", ";
				}
			}
			insertString += ";";
			//System.out.println(insertString);
			sqlstatement.executeUpdate(insertString);
			sqlstatement.executeUpdate(
				"update RoomMeeting set RunningSimID=Last_insert_id()"
					+ " where RoomMeetingID="
					+ RoomMeetingID
					+ ";");
			sqlresult =
				sqlstatement.executeQuery(
					"Select RunningSimID from RoomMeeting"
						+ " where RoomMeetingID="
						+ RoomMeetingID
						+ ";");
			sqlresult.first();
			RunningSimID = sqlresult.getInt(1);
			System.out.println("RunningSimID after exporting: " + RunningSimID);
			sqlstatement.execute("unlock tables;");
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Exception while saving ParameterSet:\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
	}
	/**
	* Copies parameters from virtual file named by theName to the set of parameters used as
	* running parameters. Use loadParameter(label) to retrieve these parameters.
	* The set of running parameters is moved to the set of last parameters 
	* (refered to by LastSimID). A meeting has to be initialized (which is done by newClient)
	* as the running the last parameters (RunningSimID, LastSimID) are refered to a RoomMeeting 
	* and as the virtual file is belonging the a project. 
	* (Within a project, virtual filenames have to be unique).
	* 
	* @param  theName   a String giving the name of the virtual file that has to be copied to
	*                   the runnning parameters. 
	*/
	public void importParameterSetByName(String theName) {
		/* Looks for Virtual File Id and calls importParameter on this ID
		 * if the file is existing
		 */
		//System.out.println("I am in SQl_inquiryDesk importParameterSetByName()");
		try {
			ResultSet sqlresult;
			String s;
			// ********* Select ID of Parameter Set to import:
			s =
				"Select SimulationID from VirtualFile"
					+ " where ProjectID="
					+ ProjectID
					+ " and VirtualFileName='"
					+ theName
					+ "';";
			//System.out.println(s);
			sqlresult = sqlstatement.executeQuery(s);
			sqlresult.first();
			if (!sqlresult.isFirst()) {
				Error = true;
				ErrorDescription = "No such ParameterSet found";
			} else
				importParameterSet(sqlresult.getInt("SimulationID"));
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Cannot import Parameterset named "
					+ theName
					+ ". Reason:\n"
					+ sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
	}
	public boolean isError() {
		return Error;
	}
	public String getErrorDescription() {
		return ErrorDescription;
	}
	// test Methodes:
	public void printRunningSimID() {
		System.out.println("RunningSimID: " + RunningSimID);
	}
	public void printLastSimID() {
		System.out.println("LastSimID: " + LastSimID);
	}
	//  Private Methodes:
	//  This  method is to build up a db connection.
	//  It is invoked by the constructor.
	private Statement connectDB(String filename) {
		Connection dbconnection = null;
		Statement sqlstatement = null;
		String URL = "";
		String dbname = "";
		String username = "";
		String password = "";
		try {
			BufferedReader pwdfile =
				new BufferedReader(new FileReader(filename));
			StringTokenizer mtoken =
				new StringTokenizer(pwdfile.readLine(), ":");
			URL = mtoken.nextToken();
			dbname = mtoken.nextToken();
			username = mtoken.nextToken();
			password = mtoken.nextToken();
		} catch (IOException e) {
			connectDB_catch(
				e,
				"Cannot open passwordfile needed for databaseconnection.");
		} catch (NoSuchElementException e) {
			connectDB_catch(e, "Passwordfile corrupted.");
		}
		//System.out.println(password + URL);
		if (!password.equals("change_me")) {
			//     //System.out.println("My Informaion: "+URL+", "+dbname+", "+username+", "+password);
			try {
				Class.forName("org.gjt.mm.mysql.Driver").newInstance();
				dbconnection =
					DriverManager.getConnection(
						"jdbc:mysql://"
							+ URL
							+ "/"
							+ dbname
							+ "?user="
							+ username
							+ "&password="
							+ password);
				sqlstatement = dbconnection.createStatement();
			} catch (ClassNotFoundException e) {
				connectDB_catch(
					e,
					"SQL-Driver org.gjt.mm.mysql.Driver not found");
			} catch (InstantiationException e) {
				connectDB_catch(e, "Buliding Instance of MySQL driver failed");
			} catch (IllegalAccessException e) {
				connectDB_catch(
					e,
					"Buliding Instance of MySQL driver failed"
						+ "Class or Constructor of Driver not accesible");
			} catch (SQLException e) {
				connectDB_catch(e, "Connection to database failed");
			}
		} else {
			sqlstatement = null;
			System.out.println("Simulator-server not connected to database");
		}
		return sqlstatement;
	} // End of connectDB
	private void connectDB_catch(Exception e, String myMessage) {
		Error = true;
		ErrorDescription = myMessage;
		System.err.println(myMessage);
		System.err.println(e.getMessage());
	}
	//This method is to initializ a RoomMeeting.
	//Is is called when the first Client is coming.
	//It returns a boolean that should be returned by newClient
	private boolean initializeRoomMeeting(int AppletID) {
		/* 1. sets RoomMeetingID according to given AppletID
		 * 2. sets ExperimentTyp
		 * 3. inserts 2 new rows in the table belonging to the ExperimentTyp
		 *    and sets the RunningSimID and LastSimID to this new rows.
		 * 4. Check if there is a Virtual File existing to ths Project and load it.
		 * 5. return a boolean
		 */
		boolean load = true;
		try {
			ResultSet sqlresult;
			String updatestring;
			String querystring;
			// 1. set RoomMeetingID according to given AppletID
			sqlresult =
				sqlstatement.executeQuery(
					"Select RoomMeetingId from Applet "
						+ "where AppletID="
						+ AppletID
						+ ";");
			sqlresult.absolute(1);
			RoomMeetingID = sqlresult.getInt(1);
			//System.out.println("RoomMeeting "+ RoomMeetingID +" initialized"); 
			// 2. set ExperimentTyp
			//System.out.println("Select ProjectID from RoomMeeting "
			//  +" where RoomMeetingID="+RoomMeetingID+";");
			sqlresult =
				sqlstatement.executeQuery(
					"Select ProjectID from RoomMeeting "
						+ " where RoomMeetingID="
						+ RoomMeetingID
						+ ";");
			sqlresult.absolute(1);
			ProjectID = sqlresult.getInt(1);
			//System.out.println("in initiaiseRoomMeeting: ProjectID="+ProjectID);
			sqlresult =
				sqlstatement.executeQuery(
					"Select ExpTypName "
						+ " from RoomMeeting, Room, ExperimentTyp "
						+ " where RoomMeeting.RoomMeetingID="
						+ RoomMeetingID
						+ " and RoomMeeting.RoomID=Room.RoomID"
						+ " and Room.ExperimentTypID=ExperimentTyp.ExperimentTypID;");
			sqlresult.first();
			if (!sqlresult.isFirst()) {
				Error = true;
				ErrorDescription =
					"No experimenttype matching to this room or meeting.";
				//System.out.println("Error: "+ErrorDescription);
			} else
				ExperimentTyp = sqlresult.getString(1);
			// 3.  Look up the Field names of Table 'ExperimentTyp'
			//     insert 2 new rows in this table.
			sqlstatement.execute(
				"lock tables RoomMeeting write, " + ExperimentTyp + " write;");
			sqlresult =
				sqlstatement.executeQuery("show columns from " + ExperimentTyp);
			updatestring = "insert into " + ExperimentTyp + " set ";
			querystring =
				"select SimulationID from " + ExperimentTyp + " where ";
			while (sqlresult.next()) {
				if (!sqlresult.getString("Field").equals("SimulationID")) {
					updatestring += sqlresult.getString("Field") + "=0";
					querystring += sqlresult.getString("Field") + "=0";
					if (!sqlresult.isLast()) {
						updatestring += ", ";
						querystring += " and ";
					}
				}
			}
			updatestring += ";";
			querystring += " order by SimulationId;";
			//System.out.println("in initialise RoomMeeting: "+updatestring);
			sqlstatement.executeUpdate(updatestring); // for Running Sim
			sqlstatement.executeUpdate(updatestring); // for Last Sim
			//       Look up the SimulationID of the row inserted above
			//       and insert it into RoomMeeting
			//System.out.println("in initalise RoomMeeting "+querystring);
			sqlresult = sqlstatement.executeQuery(querystring);
			sqlresult.last();
			RunningSimID = sqlresult.getInt(1);
			sqlresult.previous();
			LastSimID = sqlresult.getInt(1);
			sqlstatement.executeUpdate(
				"update RoomMeeting set "
					+ " RunningSimID="
					+ RunningSimID
					+ ", LastSimID="
					+ LastSimID
					+ " where RoomMeetingID="
					+ RoomMeetingID
					+ " ;");
			sqlstatement.execute("unlock tables;");
			//System.out.println("in initialise RoomMeeting: RunningSimID: "+RunningSimID);
			// 4. & 5.  Check, if Virtual File exists,
			//   import it and set load
			querystring =
				"Select SimulationID from VirtualFile, ExperimentTyp"
					+ " where VirtualFile.ProjectID="
					+ ProjectID
					+ " and VirtualFile.ExperimentTypID=ExperimentTyp.ExperimentTypID"
					+ " and ExperimentTyp.ExpTypName='"
					+ ExperimentTyp
					+ "'"
					+ " order by Date desc;";
			//System.out.println("in initialiseRoomMeeting: "+querystring);
			sqlresult = sqlstatement.executeQuery(querystring);
			sqlresult.first();
			if (sqlresult.isFirst()) {
				load = true;
				//System.out.println("Virtual file exists. Set load=true");
				importParameterSet(sqlresult.getInt("SimulationID"));
			} else {
				load = false;
				//System.out.println("Virutal file does not exist. Set load=false");
			}
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while trying to insert new Client:\n"
					+ sqle.getMessage();
			System.err.println("SQL Error in initalise RoomMeeting");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return load;
	}
	// Invoked by importParameterSetByName and by initialiseRoomMeeting
	private void importParameterSet(int FileSimID) throws SQLException {
		/* 1. Lock tables
		 * 2. Create new Parameter Set using values from virtual file
		 * 3. Update RunningSim and LastSim in DB and loacal variables
		 */
		int columns;
		String columnname;
		String insertString;
		ResultSet sqlresult;
		ResultSetMetaData metadata;
		sqlstatement.execute(
			"lock tables RoomMeeting write, " + ExperimentTyp + " write;");
		//***************** Create new Parameter Set using values from virtual file:
		insertString = "insert into " + ExperimentTyp + " set ";
		sqlresult =
			sqlstatement.executeQuery(
				"select * from "
					+ ExperimentTyp
					+ " where SimulationID="
					+ FileSimID
					+ ";");
		sqlresult.first();
		metadata = sqlresult.getMetaData();
		columns = metadata.getColumnCount();
		for (int i = 1; i <= columns; i++) {
			columnname = metadata.getColumnName(i);
			if (!columnname.equals("SimulationID")) {
				insertString += columnname
					+ "="
					+ sqlresult.getString(columnname);
				if (i != columns)
					insertString += ", ";
			}
		}
		insertString += ";";
		//      //System.out.println(insertString);
		sqlstatement.executeUpdate(insertString);
		//**************** Update RunningSim and LastSim in DB and local variables:
		sqlstatement.executeUpdate(
			"update RoomMeeting "
				+ "set RunningSimID=Last_insert_id(), LastSimID="
				+ RunningSimID
				+ " where RoomMeetingID="
				+ RoomMeetingID
				+ ";");
		sqlstatement.executeUpdate(
			"delete from "
				+ ExperimentTyp
				+ " where SimulationID ="
				+ LastSimID
				+ ";");
		LastSimID = RunningSimID;
		sqlresult =
			sqlstatement.executeQuery(
				"Select RunningSimID from RoomMeeting"
					+ " where RoomMeetingID="
					+ RoomMeetingID
					+ ";");
		sqlresult.first();
		RunningSimID = sqlresult.getInt(1);
		//      //System.out.println("RunningSimID after importing: "+RunningSimID);
		sqlstatement.execute("unlock tables;");
	}
	/**
	* Returns the Name of the Experiment type the Room called RoomName is belonging to.
	* 
	* @param  RoomName  a String giving the name of the Room for which the EventPort should
	*                   be looked up in the DB.
	* @return           the DB entry for ExpTypName in Table ExperimentTyp which corresponds
	*                   to the Room called RoomName.
	*/
	public String getExpTypName(String RoomName) {
		String ExpTypName = "none";
		try {
			ResultSet sqlresult;
			String querystring =
				"select ExpTypName from ExperimentTyp, Room "
					+ "where ExperimentTyp.ExperimentTypID=Room.ExperimentTypID "
					+ "and RoomName='"
					+ RoomName
					+ "';";
			sqlresult = sqlstatement.executeQuery(querystring);
			sqlresult.absolute(1);
			ExpTypName = sqlresult.getString("ExpTypName");
		} catch (SQLException sqle) {
			Error = true;
			ErrorDescription =
				"Error while retrieving ExpTypName.\n" + sqle.getMessage();
			System.err.println("SQL Error");
			System.err.println(ErrorDescription);
			//sqle.printStackTrace();
		}
		return ExpTypName;
	}
}
