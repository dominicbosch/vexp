# Tables used for virtual_room
# You can setup the database by 
# mysql -u username -p -h hostname < virtual_room.sql

drop database virtual_room;

create database virtual_room; 
use virtual_room;

GRANT usage on virtual_room to laborpage@'%' identified by "change_me";
GRANT usage on virtual_room to Controller@'%' identified by "change_me";

CREATE TABLE User (
  UserID int(11) DEFAULT '0' NOT NULL auto_increment,
  Username char(20) DEFAULT '' NOT NULL,
  Realname char(50),
  Title char(10),
  Password char(20),
  EMail char(30),
  Role enum('Administrator', 'User') DEFAULT 'User',
  InitialisationCode char(50),
  Access_allowed enum('y','n') default 'n',
  Mailinglist enum('y','n') default 'n',
  Language enum('d','e') default 'e',
  PRIMARY KEY (UserID),
  UNIQUE(Username, Password)
); 

CREATE TABLE Session (
  SessionNo int(11) DEFAULT '1' NOT NULL auto_increment,
  SessionKey char(50) DEFAULT '1' NOT NULL,
  UserID int(11) unsigned,
  Time datetime,
  IP char(15),
  Session_running enum('y','n'),
  PRIMARY KEY (SessionNo, SessionKey)
);

create table Applet(
  AppletID int unsigned not null auto_increment,
  UserID int unsigned not null,
  RoomMeetingID int unsigned not null,
  StartTime datetime,
  EndTime datetime,
  primary key (AppletID)
);

create table RoomMeeting(
  RoomMeetingID int unsigned not null auto_increment,
  ProjectID int unsigned not null,
  RoomID int unsigned not null,
  RunningSimID int unsigned,
  LastSimID int unsigned,
  StartTime datetime,
  EndTime   datetime,
  primary key (RoomMeetingID)
);

create table Room(
  RoomID int unsigned not null auto_increment,
  ExperimentTypID int unsigned not null,
  RoomName char(24) not null,
  StreamPort int unsigned default '5002',
  EventPort int unsigned default '5000',
  unique (RoomName),
  primary key (RoomID)
);

create table VirtualFile(
  VirtualFileID int unsigned not null auto_increment,
  ProjectID int unsigned not null,
  SimulationID int unsigned not null,
  ExperimentTypID int unsigned not null,
  VirtualFileName char(32) not null,
  Date  datetime,
  primary key (VirtualFileID)
);

create table ExperimentTyp(
  ExperimentTypID int unsigned not null auto_increment,
  ExpTypName  char(20) not null,
  unique (ExpTypName),
  primary key (ExperimentTypID)
);

create table Project(
  ProjectID int unsigned not null auto_increment,
  Projectname varchar(20),
  Laborjournal char(32) default "laborjournal.html",
  Projectpath varchar(32) default "/projects",
  MaxUsers int unsigned not null default 4,
  primary key (ProjectID)
);

create table UserProject (
  UserID int unsigned default '0' not null,
  ProjectID int unsigned default '0' not null,
  PRIMARY KEY (UserID,ProjectID),
  KEY UserID (UserID),
  KEY ProjectID (ProjectID)
);

create table ProjectForum (
  MessageID int unsigned default '0' not null,
  UserID int unsigned not null,
  ProjectID int unsigned,
  Message text,
  Date  datetime
);

# The tables for the parameters are created
# by neuewelt1_6 automatically if you creat a 
# new experiemt type. 
# VEXP does alter the table when command=getgui is called.
# 
#create table FrictionSimulatorNT(
#  SimulationID int unsigned not null auto_increment,
#  springconstant float(8),
#  scansize float(8),
#  v1 float(8),
#  scales float(8),
#  noise float(8),
#  offset float(8),
#  time int,
#  iteration int,
#  channel int,
#  primary key (SimulationID)
#);

# Privilegies
#######################################3

GRANT ALTER,SELECT,INSERT,UPDATE,DELETE,CREATE
       ON virtual_room.*
       TO laborpage@'%';

grant alter, select, insert, update, delete, create
      on virtual_room.*
      to Controller;

flush privileges;

insert into User (Username, Password, Role) values ("admin", "change_me", "Administrator");
