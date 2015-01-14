Project vexp for Virtual interactive experiments

Authors: Tibor Gyalog, Raoul Schneider, Dino Keller, 
         Christian Wattinger, Martin Guggisberg 
         <http://vexp.nano-world.org/>

$Id: README.txt,v 1.5 2006/02/28 08:13:37 guggisberg Exp $

TABLE OF CONTENT
----------------
1) Introduction
2) Requirements
3) Quick Start
4) Import Vexp in Eclipse
5) Build Standalone Simulators
6) Configuration (config Files)
7) Write your own Simulator
8) LICENCE NOTES

1) INTRODUCTION:
----------------
Virtual interactive experiments (vexp3) is a framework to promote the 
development and use of cooperative e-learning experiments over the internet. 
Interactive experiments like a fully remote controllable laboratory 
containing scientific experiments which is operated by various operators
synchronously can be realized.

New in vexp3 are the standalone simulators. They allow a single 
user mode offline. 


2) REQUIREMENTS:
----------------
* Java runtime environment (JDK 1.3 or higher) on any platform.
* Ant Download from jakarta.apache.org
(optional Eclipse)



3) Quick Start
--------------
Installation under Windows NT, 2000, XP:
--------------------------------------
* Unzip vexp.zip
* Run ant
* server.bat
* client.bat 

Enter: command=getgui in the command window 


Installation under OS X, Linux, BSD, UNIX:
------------------------------------------
* Unzip vexp.zip
* Run ant
chmod 755 server.sh client.sh
* ./server.sh
* ./client.sh  

Enter: command=getgui in the command window


4) Import Vexp in Eclipse
-------------------------

Unzip vexp3.zip 
Start Eclipse
Import existing project
open build.xml
open the outline window
right click on dist_standalone (in the outline window)
choose run as "ant build"




5) Standalone Simulators
--------------------------------
build: ant dist_standalone
copy   workspace/vexp3/jardist/standalone  YOURPATH 
open  YOURPATH/index.html in any browser


6) Configuration (config Files)
--------------------------------
folder: conf

config.dat  					main configuration File 
passwd						DB-Conector File, DB-URL,DB-Name,DB-User,DB-password

ImagingSimulator.meta			Description Files for the adaptive GUI (Simulator specific)     
DemoSimulator.meta        
PushRemoteSimulator.meta  
ElectroSimulatorNT.meta   
STMSimulator.meta         
FrictionSimulatorNT.meta  
SnomSimulatorNT.meta      


gui 							not used after version vexp1.3
meta							not used after version vexp1.3
StartScript.dat             	not used after version vexp1.3
----------------

config.dat:

(without DB)

#Config file for initialisation of vexp-server.
#
command=initports eventport=5000 streamport=5002
command=initsource name=DemoSimulator
#SnomSimulatorNT, FrictionSimulatorNT, ElectrosimulatorNT
#RemoteSimulator, PushRemoteSimulator, ImagingSimulator
#STMSimulator, DemoSimulator

The portnumber of the event channel and the datachannel 
is defined on line 3.
The simulationtype is defined on line 4


(with DB)
#Config file for initialisation of vexp-server.
#
command=init_by_db roomname=elchemSim1


If vexp is running with a mysql DB, the simulator gets 
portnumbers and simulationtype from the Room table
of the DB.

For setup and configuration of the Virtual_Room DB
consult the readme_db.txt 


DemoSimulator.meta

label=a guitype=vollkreis min=-10 max=10 Name=a
label=b guitype=vollkreis min=-10 max=10 Name=b
label=c guitype=vollkreis min=-10 max=10 Name=c

All *.meta files collect experiment specific informations 
for the adaptive GUI.

The DemoSimulator has 3 interactive parameter elements.

7) Write your own Simulator:
---------------------------
*  start the DemoSimulator:
   (ant, server.sh, client.sh, see 3) Quickstart)
   
   You can start to modify the nano.compute.simulation.DemoSimulator
    
   Read more in the SimulatorTutorial.pdf
    

8)  LICENCE NOTES:
------------------
 Copyright (c) 2000 by Tibor Gyalog, Raoul Schneider, Dino Keller, 
 Christian Wattinger, Martin Guggisberg and The Regents of the University of 
 Basel. All rights reserved.

 Permission to use, copy, modify, and distribute this software and its
 documentation for any purpose, without fee, and without written agreement is
 hereby granted, provided that the above copyright notice and the following
 two paragraphs appear in all copies of this software.
 
 IN NO EVENT SHALL THE UNIVERSITY OF BASEL BE LIABLE TO ANY PARTY FOR
 DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 BASEL HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 THE UNIVERSITY OF BASEL SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 ON AN "AS IS" BASIS, AND THE UNIVERSITY OF BASEL HAS NO OBLIGATION TO
 PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

 Authors: Tibor Gyalog, Raoul Schneider, Dino Keller, 
 Christian Wattinger, Martin Guggisberg <vexp@nano-world.org>