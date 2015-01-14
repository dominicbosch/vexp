package nano.compute.simulation;

import java.awt.*;
import java.awt.image.*;
import nano.compute.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ImagingSimulator extends Simulator {
	static boolean stop = false;

	Image TheImage;

	int MyWidth;

	int MyHeight;

	int[] ThePixels;

	byte[][] Data;

	static byte[][] LineData = new byte[256][256];

	// double nm_imgasize;

	double imagesize_nm = 630;

	Properties ImageProperties;

	public ImagingSimulator() {
		super();
		init();
		// TODO generate load and save for ImageProperties
		ImageProperties = new Properties();
		ImageProperties.setProperty("cuafm138.jpg", "630.0");
		ImageProperties.setProperty("cuna1612s.jpg", "9.0");
		ImageProperties.setProperty("kbr.jpg", "4.86");
		ImageProperties.setProperty("dmp_ag111.jpg", "30.0");
		ImageProperties.setProperty("si.jpg", "5.0");
		ImageProperties.setProperty("phorphyrin.jpg", "8.0");

	}

	public void init() {
		addDouble("red", 50.0);
		addDouble("green", 50.0);
		addDouble("blue", 50.0);
		addDouble("noise", 20);
		addDouble("xoffset", 0.0);
		addDouble("yoffset", 0.0);
		addDouble("zoom", 1.0);
		addDouble("alpha", 0);
		addCommand("approach", new approachCommandExecutor());
		addCommand("loaddata", new URLCommandExecutor());

	}

	public byte[][] getBytes(int[] pixels) {
		int red, green, blue, value;
		// System.out.println("MyHeight="+MyHeight+" MyWidth="+MyWidth);
		byte[][] b = new byte[MyHeight][MyWidth];
		for (int y = 0; y < MyHeight; y++) {
			for (int x = 0; x < MyWidth; x++) {
				red = (int) (((pixels[MyWidth * y + x] >> 16) & 255)
						* gd("red") / 100.0);
				green = (int) (((pixels[MyWidth * y + x] >> 8) & 255)
						* gd("green") / 100.0);
				blue = (int) (((pixels[MyWidth * y + x]) & 255) * gd("blue") / 100.0);
				value = (byte) ((Math.sqrt((red * red + green * green + blue
						* blue)) / 1.7 - 128.0));
				if ((value >= -128) && (value <= 127)) {
					b[y][x] = (byte) value;
				} else {
					if (value < -128) {
						b[y][x] = -128;
					} else {
						b[y][x] = 127;
					}
					;
				}
				;
			}
		}
		return b;
	}

	public int[] getPixels(Image img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		// System.out.println("MyHeight=" + h + " MyWidth=" + w);
		if (w == -1) {
			System.out
					.println("Error image empty, image folder may not be found");
			return null;
		}
		MyWidth = w;
		MyHeight = h;
		int[] pixels = new int[w * h];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (Exception e) {
			System.out.println("Pixelgrabber: " + e.getMessage());
		}
		return pixels;
	}

	public Image LoadImage(URL MyURL) {

		Canvas DummyCanvas = new Canvas();
		MediaTracker Ladekontrolle = new MediaTracker(DummyCanvas);
		Image MyImage = null;
		//System.out.println("neues Bild" + MyURL);
		// Applet
		if (Ladekontrolle != null) {
			MyImage = DummyCanvas.getToolkit().getImage(MyURL);
			Ladekontrolle.addImage(MyImage, 1);

			try {
				Ladekontrolle.waitForID(1, 500);
			} catch (InterruptedException e) {
				System.out.println("Interrupt Problem: " + e.getMessage());
			}
			if (MyImage == null) {
				System.out.println("MyImage=Null");
			}

		} else {
			System.out.println("Error: Media Tracker =  null");
			MyImage = DummyCanvas.getToolkit().getImage(MyURL);
		}
		return MyImage;

	}

	public byte[] getLine(int LineNr, byte[][] Data) {
		double xpos, ypos, span;

		span = 2 * gd("zoom") / imagesize_nm;
		if (Data != null) {
		for (int i = -128; i < 128; i++) {
			
				// Offset + span * laufparameter
				// Offset schiebt nach rechts und nach oben !!!
				// In Klammer geht es um Rasterlinien, deshalb der Faktor 256 /
				// imagesize_nm
				// xpos = ((-1) * gd("xoffset") / imagesize_nm * 256 + i) *
				// span;
				xpos = ((-2) * gd("xoffset") / imagesize_nm * 256) + i * span;

				// ypos = (gd("yoffset") / imagesize_nm * 256 + LineNr) * span;
				ypos = (2 * gd("yoffset") / imagesize_nm * 256) + LineNr * span;

				if (((xpos > (MyWidth / 2) - 1) || (xpos < (-MyWidth / 2) + 1))
						|| ((ypos > (MyHeight / 2) - 1) || (ypos < (-MyHeight / 2) + 1))) {
					LineData[LineNr + 128][i + 128] = 0;
				} else {
					LineData[LineNr + 128][i + 128] = (byte) (Data[(int) (ypos + MyHeight / 2)][(int) (xpos + MyWidth / 2)] + (Math
							.random() - 0.5)
							* gd("noise"));
				}
			
		}
		} else {
			System.out.println("Error no Data loaded, Data" + Data);
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		LineData[LineNr + 128][0] = (byte) (LineNr);
		return LineData[LineNr + 128];
	}

	public void run() {

		Thread myThread = Thread.currentThread();
		// System.out.println("Thread start myname:"+myThread.getName());
		while (SimulatorThread == myThread) {
			// forwardscan
			for (int i = 127; i > -129; i--) {

				myThread = Thread.currentThread();
				if (SimulatorThread != myThread) {
					break;
				}

				if (!stop) {

					try {
						source.write(getLine(i, Data), 0, 256);
					} catch (IOException e) {
					}
				} else {
					try {
						Thread.sleep(100);
						++i;
					} catch (InterruptedException e) {
					}
				}
				// {try{Thread.sleep(40);--i;}catch (InterruptedException e){} }

			}

			// backwardscan
			for (int i = -128; i < 128; i++) {

				myThread = Thread.currentThread();
				if (SimulatorThread != myThread) {
					break;
				}

				if (!stop) {

					try {
						source.write(getLine(i, Data), 0, 256);
					} catch (IOException e) {
					}
				} else {
					try {
						Thread.sleep(100);
						--i;
					} catch (InterruptedException e) {
					}
				}
				// {try{Thread.sleep(40);--i;}catch (InterruptedException e){} }
			}
		}
		// System.out.println("Thread stop myname:"+myThread.getName());
		// System.out.println("Thread Simulator Imagine stopped");
	}

	class approachCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {
			if (stop) {
				stop = false;
			} else {
				stop = true;
			}
			;
		}
	}

	class URLCommandExecutor extends CommandExecutor {
		public void execute(Hashtable tags) {

			String filename = "";
			try {
				filename = (String) tags.get("file");
				URL url = ImagingSimulator.class.getResource("images/"
						+ filename);

				// for an application try
				if (url != null) {

				//	System.out.println("URL:" + url.toString());

					TheImage = LoadImage(url);

				}

				else {
					try {
						url = new URL("file://./measurements/" + filename);
						// url = new URL("file:///C:/WINDOWS/Temp/measurements/"
						// + filename);
						TheImage = LoadImage(url);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						System.out.println("Error Wrong URL for Application");
						try {
							TheImage = LoadImage(ImagingSimulator.class
									.getResource("images/" + filename));

						} catch (RuntimeException e1) {
							System.out
									.println("error load image, make ant clean ant buildstanalone !!"
											+ e1.getMessage());
						}

					}

				}

			} catch (RuntimeException e1) {
				System.out
						.println("error load image, make ant clean ant buildstanalone !!"
								+ e1.getMessage());
				Data = new byte[MyHeight][MyWidth];
			}

			//System.out.println("Image loaded:" + TheImage.toString());
			ThePixels = getPixels(TheImage);
			//System.out.println("get pixels");
			if (ThePixels != null) {
				Data = getBytes(ThePixels);

				if (Data != null) {
					System.out.println("get bytes");

					try {
						imagesize_nm = Double
								.parseDouble((String) ImageProperties
										.get(filename));
					} catch (NumberFormatException e) {
						System.out.println("Numberformat Error: "
								+ e.getMessage());
						imagesize_nm = 0.0;
					}

		//			System.out.println("Size: " + imagesize_nm + "nm");
		//			System.out.println("nm/pixel: " + imagesize_nm);
		//			System.out.println("mywidth: " + MyWidth);
					// MyBoss.ReceiveEvent("command=release_gui");

					// TODO Messdaten einf�hren
					// setMETAProperty("zoom","max",Double.toString(nm_imgasize)
					// );

					sd("zoom", (imagesize_nm - 1));
					MyBoss.ReceiveEvent("command=addcontrol label=zoom value="
							+ (imagesize_nm - 1)
							+ " type=double Name=Size(nm) max=" + imagesize_nm
							+ " min=0 guitype=VollKreis");
					double offset_float_disp = imagesize_nm * 0.6;
					sd("xoffset", 0);
					MyBoss
							.ReceiveEvent("command=addcontrol label=xoffset value=0 type=double Name=X(nm) max="
									+ offset_float_disp
									+ " min="
									+ (-1 * offset_float_disp)
									+ " guitype=VollKreis");
					sd("yoffset", 0);
					MyBoss
							.ReceiveEvent("command=addcontrol label=yoffset value=0 type=double Name=Y(nm) max="
									+ offset_float_disp
									+ " min="
									+ (-1 * offset_float_disp)
									+ " guitype=VollKreis");
				} else {
					System.out
							.println("Error could not get Data: Data=" + Data);
				}

			} else {
				System.out
						.println("Error: Image could not be loades, ThePixels= "
								+ ThePixels);
			}
		}
	}

}