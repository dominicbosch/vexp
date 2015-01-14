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

package nano.net;

import java.io.*;

/**
 *
 * @author Tibor Gyalog
 * @version 1.0 17.7.2001
 */

public class SourcePipe implements Runnable {
	PipedInputStream datastream;

	int Number_of_Streams = 0, Number_of_Pipes = 0, Number_of_Anzeigen = 0;

	StreamSocket[] MyOutStreams;

	PipedOutputStream[] MyPipeStreams;

	Display MyAnzeigen[];

	PipedOutputStream src;

	private Thread Observer;

	public SourcePipe(PipedOutputStream src) {
		this.src = src;
		MyOutStreams = new StreamSocket[100];
		MyAnzeigen = new Display[20];
		MyPipeStreams = new PipedOutputStream[15];
		Observer = new Thread(this, "OBSERVER");
		Observer.start();
		//System.out.println("SOURCEPIPE INITIATED");
	}

	public void init() {
		//System.out.println("Init");
		try {
			datastream = new PipedInputStream(src);
		} catch (IOException e) {
			System.out.println("PIPE NEVER CONNECTED");
			//System.exit(0);
		}
	}
	
	public void stopthread() {
		
		Observer = null;	
		Number_of_Streams=0;
		
	    
	   
	}

	public void run() {

		init();
		byte Mybytes[] = new byte[256];
		Thread myThread = Thread.currentThread();
	//	System.out.println("Thread start myname:"+myThread.getName());
		while (Observer == myThread) {
			try {
				while (datastream.read(Mybytes, 0, 256) != -1) {
					;
					for (int i = 0; i < Number_of_Streams; i++) {
						try {
							MyOutStreams[i].put(Mybytes);
						} catch (Exception e) {
							System.out.print("E");
						}
						if (!(MyOutStreams[i].isActive())) {
							deleteStream(i);
						}
					}
					for (int i = 0; i < Number_of_Pipes; i++) {
						MyPipeStreams[i].write(Mybytes, 0, 256);
						MyPipeStreams[i].flush();
					}
					for (int i = 0; i < Number_of_Anzeigen; i++) {
						MyAnzeigen[i].write(Mybytes);
					
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						System.out.print("Interrupted");
					}

				}
			} catch (IOException e) {
				System.out.println("Error: No data from Foreign Source.");
			}

			try {
				Thread.sleep(1000);
				//System.out.print(":");
			} catch (InterruptedException e) {
				System.out.print("Interrupted");
			}
		}
		try {
			datastream.close();
		} catch (IOException e) {
			System.out.println(
			"error close sourcepipestream "+e.getMessage());
		}
		datastream=null;
//		System.out.println("Thread stop myname:"+myThread.getName());
//		System.out.println("ObserverThread stopped");
	}

	public void PlugAnzeige(Display NewAnzeige) {
		MyAnzeigen[Number_of_Anzeigen] = NewAnzeige;
		Number_of_Anzeigen++;
	}

	public void PlugStreamSocket(StreamSocket NewNetStream) {
		MyOutStreams[Number_of_Streams] = NewNetStream;
		Number_of_Streams++;
	}

	public void PlugStreamPipe(PipedOutputStream NewNetStream) {
		MyPipeStreams[Number_of_Pipes] = NewNetStream;
		Number_of_Pipes++;
	}

	public void deleteStream(int i) {
		Number_of_Streams--;
		MyOutStreams[i] = MyOutStreams[Number_of_Streams];
	}

}