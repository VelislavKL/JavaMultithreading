import java.awt.image.BufferedImage;

import org.apache.commons.math3.complex.Complex;

public class FractalRunnable implements Runnable {

	BufferedImage bi;
	int start_row;
	int end_row;
	int width;
	int height;
	String nameoffile;
	double min_re;
	double max_re;
	double min_im;
	double max_im;
	int thread_number;
	int thread_count;
	static int maxiterations;
	int colors[];
	boolean quiet;
	
	public FractalRunnable(BufferedImage bi, String nameoffile, int height, int width, int start_row, int end_row, double min_re, 
			double max_re, double min_im, double max_im, int thread_count, int thread_number, int maxiterations, int colors[], boolean quiet) {
		
		this.bi = bi;
		this.start_row = start_row;
		this.end_row = end_row;
		this.height = height;
		this.width = width;
		this.nameoffile = nameoffile;
		this.min_re = min_re;
		this.max_re = max_re;
		this.min_im = min_im;
		this.max_im = max_im;
		this.thread_count = thread_count;
		this.thread_number = thread_number;
		FractalRunnable.maxiterations = maxiterations;
		this.colors = colors;
		this.quiet = quiet;
		
	}
	
	public static Complex z_iter(Complex z, Complex c) {
		
		return c.multiply(z.multiply(-1).exp()).add(z.multiply(z));
	}
	
	public static int z_check(Complex c) {
			
		Complex z0 = new Complex(0.0, 0.0);

		Complex z_prev = z0;
		Complex z_i = null;
		
		int steps = 0;

		Double d = null;
		
		for(int i = 0; i < maxiterations; i++) {
			
			z_i = z_iter(z_prev, c);
			z_prev = z_i;
			
			d = new Double(z_prev.getReal());
			
			if (d.isInfinite() || d.isNaN()) {
				
				steps = i;
				break;
				
			}
		}

		return steps;
		
	}
	
	public void run() {

		if(quiet == false)
			System.out.println("Thread - " + thread_number + " started");
		
		ElapsedTime time = new ElapsedTime();
		
		double Re_factor = (max_re - min_re) / (width - 1);
		double Im_factor = (max_im - min_im) / (height - 1);
		
		for (int j = start_row; j < end_row; j++) {
			
			double c_im = max_im - j * Im_factor;

			for (int i = 0; i < width; i++) {
				
				double c_re = min_re + i * Re_factor;

				int r = z_check(new Complex(c_re, c_im));

				if (r < maxiterations) { // inside
					bi.setRGB(i, j, colors[r]);
				}
				//} else {
				//	bi.setRGB(i, j, 0xFF0000);
				//}
			}		
		}
		
		if(quiet == false)
		{
			System.out.println("Thread - " + thread_number + " execution time was (millis) : " + time.elapsed());
			System.out.println("Thread - " + thread_number + " stopped");
		}
		
	}

}