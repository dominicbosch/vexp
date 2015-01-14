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

package nano.client.applet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import nano.debugger.Debg;
import nano.client.ImagePanel;
import nano.client.LineSection;
import nano.client.NetObserver;
import nano.client.NetSimPanel;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import nano.net.*;

/**
* @author Dominic Bosch
* @version 2.0 30.09.2011 (complete makeover)
*/

public class ClientApplet extends JApplet implements SSStateListener{

	private static final long serialVersionUID = 1L;
	private JTextPane statusLine;
	private JComboBox comboBoxScanRange;
	private Style style;
	private static final int H_OTH = 60; // Height of the controls left top panel 
	private static final int H_BUT = 23; // height of a button
	private static final int N_BUT = 3; // max number of buttons in one column
	
	private NetSimPanel myNetSimPanel;
	private NetObserver myImage;
	private LineSection myLiner;
	private String hostURL, appletID;
	private int eventPort, streamPort;

	public void init() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClientApplet.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ClientApplet.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ClientApplet.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(ClientApplet.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {initComponents();}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	private void initComponents() {
		hostURL = getParameter("url");
		appletID = getParameter("id");
		String tmpEventPort = getParameter("eventport");
		String tmpStreamPort = getParameter("streamport");
		if(hostURL == null || appletID == null || tmpEventPort == null
				|| tmpStreamPort == null){
			System.err.println("One or more of the requested parameters haven't been found... Stopping!");
			stop();
			return;
		}
		try {
			eventPort = Integer.parseInt(tmpEventPort);
			streamPort = Integer.parseInt(tmpStreamPort);
		} catch (NumberFormatException nfe) {
			System.err.println("one of the parameters eventport and streamport couldn't be converted into an integer");
			stop();
			return;
		}
		
		JPanel panelWindow = new JPanel();
		JPanel panelGraphs = new JPanel();
		JPanel panelPlain = new JPanel();
		JPanel panelLineScan = new JPanel();
		JPanel panelControls = new JPanel();
		JPanel panelControlsRight = new JPanel();
		JPanel panelControlsLeft = new JPanel();
		JPanel panelArrows = new JPanel();
		JPanel panelStatus = new JPanel();
		
		JLabel labelArrows = new JLabel();

		this.setContentPane(panelWindow);
		this.setSize(new Dimension(650, 650));
		panelWindow.setBackground(Color.white);

		setupComponent(panelGraphs, new Dimension(540, 267), Color.black);
		panelGraphs.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setupComponent(panelPlain, new Dimension(256, 256), Color.white);
		setupComponent(panelLineScan, new Dimension(256, 256), Color.white);
		setupComponent(panelControls, new Dimension(540, H_OTH + (H_BUT + 2) * N_BUT + 30), Color.black);
		panelControls.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setupComponent(panelControlsRight, new Dimension(260, H_OTH + (H_BUT + 2) * N_BUT + 20), Color.red);
		setupComponent(panelControlsLeft, new Dimension(260, H_OTH + (H_BUT + 2) * N_BUT + 20), Color.green);
		setupComponent(panelArrows, new Dimension(75, 75), Color.magenta);
		setupComponent(panelStatus, new Dimension(520, 125), Color.magenta);
		

		StreamClientSocket MyStreamClientSocket = new StreamClientSocket(hostURL, streamPort);
		myNetSimPanel = new NetSimPanel(hostURL, eventPort);
		myImage = new NetObserver(myNetSimPanel);
		myLiner = new LineSection();
		MyStreamClientSocket.PlugAnzeige(myImage);
		MyStreamClientSocket.PlugAnzeige(myLiner);
		MyStreamClientSocket.addSSStateListener(this);
		Debg.print(2, "StreamClientSocket connected to " + MyStreamClientSocket.getRemoteAddress());
		ImagePanel myImagePanel = new ImagePanel(myImage);

		panelGraphs.setLayout(new FlowLayout());
		panelGraphs.add(myImage);
		panelGraphs.add(myLiner);
	
		add(myImagePanel);

		labelArrows.setText("Change Position on Sample:");
		setupComponent(labelArrows, new Dimension(230, 40));
		setupArrows(panelArrows);
		panelControlsRight.setAlignmentX(CENTER_ALIGNMENT);
		panelControlsRight.add(labelArrows);
		panelControlsRight.add(panelArrows);

		setupLeftControls(panelControlsLeft);
		panelControls.add(panelControlsLeft);
		panelControls.add(panelControlsRight);
		
		setupStatusLine(panelStatus);
		
		panelWindow.setLayout(new FlowLayout());
		panelWindow.add(panelGraphs);
		panelWindow.add(panelControls);
		panelWindow.add(panelStatus);
		
	}

	private void setupLeftControls(JPanel topPanel){
		JPanel panelControlsLeftTop = new JPanel();
		JPanel panelControlsLeftBottom = new JPanel();
		JPanel panelButtonsLeft = new JPanel();
		JPanel panelButtonsRight = new JPanel();
		
		setupComponent(panelControlsLeftTop, new Dimension(250, H_OTH), Color.orange);
		setupComponent(panelControlsLeftBottom, new Dimension(250, (H_BUT + 2) * N_BUT + 5), Color.blue);
		setupComponent(panelButtonsLeft, new Dimension(120, (H_BUT + 2) * N_BUT), Color.DARK_GRAY);
		setupComponent(panelButtonsRight, new Dimension(120, (H_BUT + 2) * N_BUT), Color.magenta);
		
		Dimension buttonDim = new Dimension(100, H_BUT);

/*
 * Setup Top controls
 */
		JPanel panelMoving = new JPanel();
		JPanel panelApproach = new JPanel();
		JPanel panelScanRange = new JPanel();
		JLabel labelMovingTitle = new JLabel();
		JLabel labelMovingDot = new JLabel();
		JLabel labelScanRangeTitle = new JLabel();
		JLabel labelApproachedTitle = new JLabel();
		JLabel labelApproachedDot = new JLabel();
		comboBoxScanRange = new JComboBox();

		setupComponent(panelApproach, new Dimension(120, H_BUT + 2), Color.cyan);
		setupComponent(panelMoving, new Dimension(120, H_BUT + 2), Color.YELLOW);
		setupComponent(panelScanRange, new Dimension(240, H_BUT + 7), Color.BLUE);
		panelApproach.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		panelMoving.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		panelScanRange.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
	
		
		labelApproachedTitle.setBackground(Color.white);
		labelApproachedTitle.setText("Approached: ");

		labelApproachedDot.setBackground(Color.white);
		labelApproachedDot.setIcon(new ImageIcon("dotGreen.jpg"));
		
		panelApproach.add(labelApproachedTitle);
		panelApproach.add(labelApproachedDot);
		
		labelMovingTitle.setBackground(Color.white);
		labelMovingTitle.setText("Moving: ");

		labelMovingDot.setBackground(Color.white);
		labelMovingDot.setIcon(new ImageIcon("dotGreen.jpg"));

		panelMoving.add(labelMovingTitle);
		panelMoving.add(labelMovingDot);

		setupComponent(labelScanRangeTitle, new Dimension(110, H_BUT));
		labelScanRangeTitle.setBackground(Color.white);
		labelScanRangeTitle.setText("Scan Range:");

		setupComponent(comboBoxScanRange, new Dimension(100, H_BUT));
		comboBoxScanRange.setModel(new DefaultComboBoxModel(new String[] { "1 um", "5 um", "10 um", "25 um", "50 um" }));
		comboBoxScanRange.setSelectedIndex(2);
		comboBoxScanRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				comboBoxScanRangeActionPerformed(evt);
			}
		});
		
		panelScanRange.add(labelScanRangeTitle);
		panelScanRange.add(comboBoxScanRange);

		panelControlsLeftTop.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panelControlsLeftTop.add(panelApproach);
		panelControlsLeftTop.add(panelMoving);
		panelControlsLeftTop.add(panelScanRange);

/*
 * Setup Bottom controls
 */
	/*
	 * Setup Left Controls
	 */
		JButton buttonApproach = new JButton();
		JButton buttonStart = new JButton();
		JButton buttonSave = new JButton();
		
		setupComponent(buttonApproach, buttonDim);
		setupComponent(buttonStart, buttonDim);
		setupComponent(buttonSave, buttonDim);
		
		//buttonApproach.setBackground(Color.white);
		buttonApproach.setText("Approach");
		buttonApproach.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonApproachActionPerformed(evt);
			}
		});

		//buttonStart.setBackground(Color.white);
		buttonStart.setText("Start");
		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonStartActionPerformed(evt);
			}
		});
		
		//buttonSave.setBackground(Color.white);
		buttonSave.setText("Save Scan");
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonSaveActionPerformed(evt);
			}
		});
		
		panelButtonsLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panelButtonsLeft.add(buttonApproach);
		panelButtonsLeft.add(buttonStart);
		panelButtonsLeft.add(buttonSave);

	/*
	 * Setup Right Controls
	 */
		JButton buttonWithdraw = new JButton();
		JButton buttonStop = new JButton();
		
		setupComponent(buttonStop, buttonDim);
		setupComponent(buttonWithdraw, buttonDim);
		
		//buttonWithdraw.setBackground(Color.white);
		buttonWithdraw.setText("Withdraw");
		buttonWithdraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonWithdrawActionPerformed(evt);
			}
		});

		//buttonStop.setBackground(Color.white);
		buttonStop.setText("Stop");
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonStopActionPerformed(evt);
			}
		});
		
		panelButtonsRight.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panelButtonsRight.add(buttonStop);
		panelButtonsRight.add(buttonWithdraw);

	/*
	 * Combine all elements
	 */
		panelControlsLeftBottom.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panelControlsLeftBottom.add(panelButtonsLeft);
		panelControlsLeftBottom.add(panelButtonsRight);
		
		topPanel.setLayout(new FlowLayout());
		topPanel.add(panelControlsLeftTop);
		topPanel.add(panelControlsLeftBottom);
	}
	
	private void setupArrows(JPanel topPanel){
		JLabel labelArrowUp = new JLabel();
		JLabel labelArrowDown = new JLabel();
		JLabel labelArrowLeft = new JLabel();
		JLabel labelArrowRight = new JLabel();
		
		labelArrowUp.setBackground(Color.WHITE);
		labelArrowUp.setIcon(new ImageIcon("arrow-up.jpg"));
		labelArrowUp.addMouseListener(new MouseAdapter() {  
			public void mouseReleased(MouseEvent evt){  
				labelArrowUpActionPerformed(evt);
			}  
		});
		
		labelArrowLeft.setBackground(Color.WHITE);
		labelArrowLeft.setIcon(new ImageIcon("arrow-left.jpg"));
		labelArrowLeft.addMouseListener(new MouseAdapter() {  
			public void mouseReleased(MouseEvent evt){  
				labelArrowLeftActionPerformed(evt);
			}
		});
		
		labelArrowRight.setBackground(Color.WHITE);
		labelArrowRight.setIcon(new ImageIcon("arrow-right.jpg"));
		labelArrowRight.addMouseListener(new MouseAdapter() {  
			public void mouseReleased(MouseEvent evt){  
				labelArrowRightActionPerformed(evt);
			}
		});
		
		labelArrowDown.setBackground(Color.WHITE);
		labelArrowDown.setIcon(new ImageIcon("arrow-down.jpg"));
		labelArrowDown.addMouseListener(new MouseAdapter() {  
			public void mouseReleased(MouseEvent evt){  
				labelArrowDownActionPerformed(evt);
			}
		});
		
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topPanel.add(createBlankArrow());
		topPanel.add(labelArrowUp);
		topPanel.add(createBlankArrow());
		topPanel.add(labelArrowLeft);
		topPanel.add(createBlankArrow());
		topPanel.add(labelArrowRight);
		topPanel.add(createBlankArrow());
		topPanel.add(labelArrowDown);
	}
	
	private void setupStatusLine(JPanel topPanel){
		StyleContext context = new StyleContext();
		StyledDocument document = new DefaultStyledDocument(context);
		
		style = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setSpaceAbove(style, 1);
		StyleConstants.setSpaceBelow(style, 1);

		statusLine = new JTextPane(document);

		JLabel labelStatusTitle = new JLabel();
		labelStatusTitle.setBackground(Color.white);
		labelStatusTitle.setText("RAFM Status: ");

		setupComponent(statusLine, new Dimension(510, 95));
		statusLine.setEditable(false);
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topPanel.add(labelStatusTitle);
		topPanel.add(new JScrollPane(statusLine));
	}
	
	private void setupComponent(Component comp, Dimension dim, Color col){
		setupComponent(comp, dim);
		comp.setBackground(col);
		comp.setBackground(Color.white);
	}
	
	private void setupComponent(Component comp, Dimension dim){
		comp.setMinimumSize(dim);
		comp.setMaximumSize(dim);
		comp.setPreferredSize(dim);
		comp.setSize(dim);
	}
	
	private JLabel createBlankArrow(){
		JLabel arrow = new JLabel();
		arrow.setBackground(Color.WHITE);
		arrow.setIcon(new ImageIcon("blank25x25.jpg"));
		return arrow;
	}

	private void buttonApproachActionPerformed(ActionEvent evt) {
		myNetSimPanel.put("command=autoapproach");
	}
	
	private void buttonWithdrawActionPerformed(ActionEvent evt) {
		myNetSimPanel.put("command=withdraw");
	}
	
	private void buttonStartActionPerformed(ActionEvent evt) {
		myNetSimPanel.put("command=start");
	}
	
	private void buttonStopActionPerformed(ActionEvent evt) {
		myNetSimPanel.put("command=stop");
	}
	
	private void buttonSaveActionPerformed(ActionEvent evt) {
		printInfo("Saving image");
	}
	
	private void comboBoxScanRangeActionPerformed(ActionEvent evt) {
		printInfo("ScanRange changed to " + comboBoxScanRange.getSelectedItem());
	}
	
	private void labelArrowUpActionPerformed(MouseEvent e) {
		printInfo("Up");
	}
	
	private void labelArrowRightActionPerformed(MouseEvent evt) {
		printInfo("Right");
	}
	
	private void labelArrowLeftActionPerformed(MouseEvent evt) {
		printInfo("Left");
	}
	
	private void labelArrowDownActionPerformed(MouseEvent evt) {
		printInfo("Down");
	}
	
	private void printInfo(String msg){
		System.out.println(msg);
		try {
			statusLine.getDocument().insertString(0, msg + "\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		statusLine.setCaretPosition(0);
	}

	public void newStreamSocket(StreamSocket NewSocket){}

	public void SSStateChanged(boolean ok, String State, int ID){}

}
