package nano.client;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import nano.awt.color.*;

public class DataFrame extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DataCanvas MyDataCanvas;

	VerlaufCanvas MyVerlauf;

	ColorScala MyColorScala;

	Label SumIndicator, MeanIndicator, SigmaIndicator;

	boolean LineStat = false;

	LineSectionData MyLineSectionData;

	HistogrammCanvas MyHistogrammCanvas;

	byte[][] Data = new byte[256][256];

	public DataFrame(String name, ColorScala NetColorScala) {
		super(name);
		setLayout(new GridLayout(2, 2));

		MyColorScala = NetColorScala;

		MyDataCanvas = new DataCanvas();

		DataPanel MyDataPanel = new DataPanel(MyDataCanvas);

		MyLineSectionData = new LineSectionData();

		MyHistogrammCanvas = new HistogrammCanvas();

		Panel p1 = new Panel();
		Panel p2 = new Panel();
		p1.setLayout(new FlowLayout());
		p2.setLayout(new FlowLayout());
		p1.add(MyDataCanvas);
		p1.add(MyLineSectionData);
		p2.add(MyDataPanel);
		p2.add(MyHistogrammCanvas);
		add("North", p1);
		add("Center", p2);
		setVisible(true);
		pack();
		setResizable(false);
		addWindowListener(new DataFrameListener());
		//setMenuBar(new ImageFrameMenu());
	}

	public void kill() {
		dispose();
	}

	public void setData(byte[][] NewData) {
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				Data[i][j] = NewData[i][j];
			}
		}
		MyHistogrammCanvas.setData(Data);
		SumIndicator.setText("Sum=" + getSum());
		MeanIndicator.setText("Mean=" + getMean());
		SigmaIndicator.setText("Sigma=" + getSigma());

	}

	public double getSum() {
		double sum = 0;
		if (LineStat) {
			byte[] LineData = MyLineSectionData.getData();
			if (LineData != null) {
				for (int i = 0; i < 256; i++) {
					sum += LineData[i];
				}
			}
		} else {
			sum = getSum(0, 0, 255, 255);
		}
		return sum;
	}

	public double getMean() {
		double mean = 0;
		if (LineStat) {
			mean = getSum() / 256.0;

		} else {
			mean = getSum() / (256.0 * 256.0);
		}
		return mean;
	}

	public double getSigma() {
		double sigma = 0;
		double mean = getMean();
		if (LineStat) {
			byte[] LineData = MyLineSectionData.getData();
			if (LineData != null) {
				for (int i = 0; i < 256; i++) {
					sigma += Math.pow((LineData[i] - mean), 2);
				}
				sigma = sigma / 256.0;
			}
		} else {
			for (int i = 0; i < 256; i++) {
				for (int j = 0; j < 256; j++) {
					sigma += Math.pow((Data[i][j] - mean), 2);
				}
			}
			sigma = sigma / (256.0 * 256.0);
		}
		return sigma;
	}

	public double getSum(int obenlinksx, int obenlinksy, int untenrechtsx,
			int untenrechtsy) {
		obenlinksx = Math.max(obenlinksx, 0);
		obenlinksy = Math.max(obenlinksy, 0);
		untenrechtsx = Math.min(untenrechtsx, 256);
		untenrechtsy = Math.min(untenrechtsy, 256);
		double sum = 0;
		for (int i = obenlinksy; i <= untenrechtsy; i++) {
			for (int j = obenlinksx; j <= untenrechtsx; j++) {
				sum += Data[i][j];
			}
		}
		return sum;
	}

	public void setLineData(byte[] NewData) {
		MyLineSectionData.setData(NewData);
	}

	public byte[] getLineSection(int x0, int y0, int x1, int y1) {
		int xi, yi;
		byte[] LineSection = new byte[256];
		double dx = (x1 - x0) / 265.0;
		double dy = (y1 - y0) / 265.0;
		for (int i = 0; i < 256; i++) {
			xi = x0 + (int) (i * dx);
			yi = y0 + (int) (i * dy);
			LineSection[i] = Data[yi][xi];
		}
		return LineSection;
	}

	public void changeColorScala() {
		ColorScala TheNew = FarbverlaufDialog.getColorScala(new Frame(),
				MyColorScala);
		MyColorScala = TheNew;
		MyVerlauf.setColorScala(TheNew);
		MyDataCanvas.repaint();
		MyVerlauf.repaint();
	}

	class DataFrameListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			kill();
		}
	}

	class DataPanel extends Panel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		DataCanvas MyCanvas;

		public DataPanel(DataCanvas NewDataCanvas) {
			setLayout(new GridLayout(10, 1));
			/*
			 * TODO implement a dropdown list such as below 
			 * DataOrLine = new DefaultComboBoxModel(new String[] { "1 um", "5 um", "10 um", "25 um", "50 um" })
			 * 
			 */
			Choice DataOrLine = new Choice();
			DataOrLine.addItemListener(new DataOrLineListener());
			DataOrLine.addItem("2D Data");
			DataOrLine.addItem("LineSection");
			add(DataOrLine);
			//add(new Panel());
			//add(new Panel());

			MyCanvas = NewDataCanvas;

			Label info = new Label("Statistic informations");
			add(info);
			Label info2 = new Label("x-y-Range 0..256, 0..256");
			add(info2);
			Label info3 = new Label("z-Range -128 .. 127");
			add(info3);
			SumIndicator = new Label("Sum=" + getSum());
			add(SumIndicator);
			MeanIndicator = new Label("Mean=" + getMean());
			add(MeanIndicator);
			SigmaIndicator = new Label("Sigma=" + getSigma());
			add(SigmaIndicator);
			JButton ChangeColorButton = new JButton("Change Color");
			ChangeColorButton.addActionListener(new ChangeColorListener());

			//add(new Panel());add(new Panel());
			MyVerlauf = new VerlaufCanvas(MyColorScala);
			add(MyVerlauf);
			add(ChangeColorButton, null);
		}
	}

	class DataOrLineListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if ((e.getItem()).equals("LineSection")) {
				LineStat = true;
			} else {
				LineStat = false;
			}
			SumIndicator.setText("Sum=" + getSum());
			MeanIndicator.setText("Mean=" + getMean());
			SigmaIndicator.setText("Sigma=" + getSigma());
		}
	}

	class ChangeColorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			changeColorScala();
		}
	}

	class DataCanvas extends Canvas implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Image MyImages[] = new Image[256];

		int AlreadyPoints = 0;

		int[] Pointx = new int[2];

		int[] Pointy = new int[2];

		public DataCanvas() {
			addMouseListener(this);
		}

		public Dimension getPreferredSize() {
			return new Dimension(256, 256);
		}

		public Dimension getMinimumSize() {
			return new Dimension(256, 256);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			AlreadyPoints++;
			////System.out.println("Already "+AlreadyPoints+" Points");
			if (AlreadyPoints >= 3) {
				AlreadyPoints = 1;
			}
			Pointx[AlreadyPoints - 1] = e.getX();
			Pointy[AlreadyPoints - 1] = e.getY();
			//if(AlreadyPoints==2){getGraphics().drawLine(Pointx[0],Pointy[0],Pointx[1],Pointy[1]);}
			repaint();
			if (AlreadyPoints == 2) {
				setLineData(getLineSection(Pointx[0], Pointy[0], Pointx[1],
						Pointy[1]));
				SumIndicator.setText("Sum=" + getSum());
				MeanIndicator.setText("Mean=" + getMean());
				SigmaIndicator.setText("Sigma=" + getSigma());

			}
		}

		/*
		 * public void save(){ Image offscreen=createImage(256,256); Graphics
		 * g=offscreen.getGraphics(); for(int i=0;i <256;i++){
		 * g.drawImage(MyImages[i],0,i,null); } try{ FileOutputStream out=new
		 * FileOutputStream(new File("MeinBildli.gif")); GifEncoder encoder=new
		 * GifEncoder(offscreen,out); encoder.encode(); out.close();
		 * }catch(IOException e){ //e.printStackTrace(); } }
		 */
		public void paint(Graphics g) {
			Image LineImage;
			final int Radius = 3;
			int[] LineData = new int[256 * 256];
			for (int i = 0; i < 256; i++) {
				for (int j = 0; j < 256; j++) {
					LineData[256 * i + j] = MyColorScala
							.getColor(Data[i][j] + 128);
				}
			}
			MemoryImageSource linesource = new MemoryImageSource(256, 256,
					LineData, 0, 256);
			//LineImage.flush();
			LineImage = createImage(linesource);
			g.drawImage(LineImage, 0, 0, null);
			if (AlreadyPoints == 1) {
				g.setColor(Color.red);
				g.fillOval(Pointx[0] - Radius, Pointy[0] - Radius, 2 * Radius,
						2 * Radius);
				g.setColor(Color.white);
				g.drawOval(Pointx[0] - Radius, Pointy[0] - Radius, 2 * Radius,
						2 * Radius);
			}

			if (AlreadyPoints == 2) {
				g.setColor(Color.red);
				g.drawLine(Pointx[0], Pointy[0], Pointx[1], Pointy[1]);
				g.fillOval(Pointx[0] - Radius, Pointy[0] - Radius, 2 * Radius,
						2 * Radius);
				g.fillOval(Pointx[1] - Radius, Pointy[1] - Radius, 2 * Radius,
						2 * Radius);
				g.setColor(Color.white);
				g.drawOval(Pointx[0] - Radius, Pointy[0] - Radius, 2 * Radius,
						2 * Radius);
				g.drawOval(Pointx[1] - Radius, Pointy[1] - Radius, 2 * Radius,
						2 * Radius);
			}
		}
	}

	public class LineSectionData extends Canvas implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Graphics myg;

		byte[] Data1;

		int dx = 4, height = 256, res = 4, width = 256, numpoints = 256,
				plotlines = 2;

		int[] Flag_x = new int[256];

		int act_x, num_Flags = 0;

		public LineSectionData() {
			setBackground(Color.black);
			setForeground(Color.green);
			setFont(new Font("Arial", Font.BOLD, 10));
			addMouseListener(this);
		}

		public byte[] getData() {
			return Data1;
		}

		public Dimension getPreferredSize() {
			return new Dimension(256, 256);
		}

		public Dimension getMinimumSize() {
			return new Dimension(256, 256);
		}

		public void setData(byte[] NewData) {
			Data1 = NewData;
			MyPaint();
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			boolean add = true;
			act_x = e.getX();
			for (int i = 1; i <= num_Flags; i++) {
				if (Math.abs(act_x - Flag_x[i - 1]) <= 3) {
					deleteFlag(i);
					add = false;
					break;
				}
			}
			if (add) {
				addFlag(act_x);
			}
		}

		public void deleteFlag(int i) {
			Flag_x[i - 1] = Flag_x[num_Flags - 1];
			num_Flags--;
			repaint();
		}

		public void addFlag(int x) {
			Flag_x[num_Flags] = x;
			num_Flags++;
			repaint();
		}

		public void MyPaint() {
			height = getHeight();
			width = getWidth();
			numpoints = 256 / res;
			dx = width / numpoints;
			if (myg == null) {
				myg = getGraphics();
			} else {
				if (Data1 != null) {
					myg.clearRect(0, 0, dx + 1, height);
					myg.setColor(Color.green);
					for (int xi = 2; xi < numpoints; xi++) {
						myg.clearRect(dx * (xi - 1) + 1, 0, dx, height);
						//myg.drawLine(dx*(xi-1),Data[res*(xi-1)]+(height/2),dx*xi,Data[res*xi]+(height/2));
						myg.drawLine(dx * (xi - 1), (height / 2)
								- Data1[res * (xi - 1)], dx * xi, (height / 2)
								- Data1[res * xi]);
					}
					myg.setColor(Color.red);
					for (int i = 1; i <= num_Flags; i++) {
						paintFlag(myg, i);
					}
				}
			}
		}

		public void paintFlag(Graphics myg, int index) {
			int[] x = new int[3];
			int[] y = new int[3];
			x[0] = Flag_x[index - 1];
			y[0] = (height / 2) - Data1[Flag_x[index - 1]];
			//y[0]=Data[Flag_x[index-1]]-1+(height/2);
			x[1] = x[0] - 3;
			x[2] = x[0] + 3;
			y[1] = y[0] - 5;
			y[2] = y[1];
			myg.fillPolygon(x, y, 3);
			String MyText = " " + Flag_x[index - 1];
			myg.drawString(MyText, x[0] - 10, y[0] - 20);
			MyText = " " + Data1[Flag_x[index - 1]];
			myg.drawString(MyText, x[0] - 10, y[0] - 10);
		}

		public void paint(Graphics g) {
			MyPaint();
		}

	}

}