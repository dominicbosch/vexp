package nano.compute.simulation;

import java.io.IOException;

import nano.compute.Simulator;

public class DemoSimulator6 extends Simulator {
	final int ss = 256;

	final boolean running = true;

	private double[][] si_ad_atomes;

	private final double[][] einheitszelle = { { 8.031809525, 0. }, // position
																	// of the 12
																	// ad_atoms
																	// in the
																	// unit cell
			{ 24.20323810, 0. }, { 32.34285715, 0. }, { 48.51428572, 0. },
			{ 24.28409526, -9.383261528 }, { 16.19838097, 4.621606437 },
			{ 40.42857144, -4.668289294 }, { 32.34285716, 9.336578658 },
			{ 40.42857140, 4.668289294 }, { 32.34285712, -9.336578658 },
			{ 24.28409523, 9.383261528 }, { 16.19838094, -4.621606437 } };

	public DemoSimulator6() {
		super();
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		addDouble("a", 120.0);
		addDouble("b", 120.0);
		addDouble("c", 0.6);

		byte[] data = new byte[ss];
		int nr_einheits_zellen = 2;
		si_ad_atomes = new double[12 * 13][2];
		int e1 = 56;
		int e2 = 6;
		int k = 0;

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[0 + j][0] = einheitszelle[j][0];
			si_ad_atomes[0 + j][1] = einheitszelle[j][1];
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[12 + j][0] = einheitszelle[j][0] + 28.3;
			si_ad_atomes[12 + j][1] = einheitszelle[j][1] - 16.34;
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[24 + j][0] = einheitszelle[j][0] + 28.3;
			si_ad_atomes[24 + j][1] = einheitszelle[j][1] + 16.34;
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[36 + j][0] = einheitszelle[j][0] - 28.3;
			si_ad_atomes[36 + j][1] = einheitszelle[j][1] - 16.34;
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[48 + j][0] = einheitszelle[j][0] - 28.3;
			si_ad_atomes[48 + j][1] = einheitszelle[j][1] + 16.34;
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[60 + j][0] = einheitszelle[j][0];
			si_ad_atomes[60 + j][1] = einheitszelle[j][1] - 32.68;
		}

		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[72 + j][0] = einheitszelle[j][0];
			si_ad_atomes[72 + j][1] = einheitszelle[j][1] + 32.68;
		}
		
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[84 + j][0] = einheitszelle[j][0]-56.6;
			si_ad_atomes[84 + j][1] = einheitszelle[j][1]+32.68;
			
		}
		
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[96 + j][0] = einheitszelle[j][0]-56.6;
			si_ad_atomes[96 + j][1] = einheitszelle[j][1]-32.68;
			
		}
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[108 + j][0] = einheitszelle[j][0]+56.6;
			si_ad_atomes[108 + j][1] = einheitszelle[j][1]+32.68;
			
		}
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[120 + j][0] = einheitszelle[j][0]+56.6;
			si_ad_atomes[120 + j][1] = einheitszelle[j][1]-32.68;
			
		}
		
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[132 + j][0] = einheitszelle[j][0]-56.6;
			si_ad_atomes[132 + j][1] = einheitszelle[j][1];
			
		}
		for (int j = 0; j < einheitszelle.length; j++) {

			si_ad_atomes[144 + j][0] = einheitszelle[j][0]+56.6;
			si_ad_atomes[144 + j][1] = einheitszelle[j][1];
			
		}
	}

	public void run() {

		double tmp;
        byte[] data = new byte[ss];
		while (true) {
			while (running) {
				// zuerst approach bei (1,1) ?? erst raten

				double tipz = 20;
				double It;
				It = getIt(tipz - topo(0.1, 0.1));

				// ;

				// s -> if s>-0.3 then I0* exp (-2*chi*s) else 11.5 end if;

				// dann rastern: H�hendiff auswerten und tunnelstrom regeln
				for (int yi = 0; yi < ss; yi++) { // y direction
					for (int xi = 0; xi < ss; xi++) { // x direction

						// simple trigonometirc function
						// tmp =
						// 75*Math.exp(-(((double)xi-gd("b"))*((double)xi-gd("b"))+((double)yi-gd("c"))*((double)yi-gd("c")))/gd("a")/gd("a"));
						// tmp=50*Math.exp(-((xi-128)*(xi-128)+(yi-128)*(yi-128))/200);
						// System.out.print("-"+tmp);
						// System.out.println(tmp+" a:"+gd("a")+" b:"+gd("b")+"
						// c:"+gd("c"));
						tmp = 30* (topo(xi/2-20,yi/2-30))-128;
						//System.out.println(tmp);
						//System.out.println(topo(xi/7-20,yi/7-40));

						if ((tmp >= -128) && (tmp <= 127)) {
							data[xi] = (byte) tmp;
						} else {
							if (tmp < -128) {
								data[xi] = -128;
							} else {
								data[xi] = 127;
							}
							;
						}
						;
					} //
					data[0] = (byte) (yi - ss / 2); // first byte stores the
													// line number from -128 to
													// 128
					// send Stream to Clients
					try {
						source.write(data, 0, 256);
					} catch (IOException e) {
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			} // END RUNNING
		} // END WHILE(TRUE)
	} // END RUN

	private double topo(double x, double y) {
		double topo = 0;
		double gb = 3;//glockenbreite
		double hoehe = 10;
		// System.out.println(si_ad_atomes.length);
		for (int i = 0; i < si_ad_atomes.length; i++) {
			topo += hoehe
					* Math
							.exp(-((x - si_ad_atomes[i][0])
									* (x - si_ad_atomes[i][0]) + (y - si_ad_atomes[i][1])
									* (y - si_ad_atomes[i][1]))/gb/gb);
		}
		return topo;
	}

	private double getIt(double d) {
		// TODO Auto-generated method stub
		if (d > -0.3)
			return 6 * Math.exp(2.2 * d);

		return 11.5;
	}

}
