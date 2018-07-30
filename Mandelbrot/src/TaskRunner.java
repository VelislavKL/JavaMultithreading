import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class TaskRunner {

	public static void main(String[] args) {
		
		// default paramaters
		int height = 640;
		int width = 480;
		double min_re = -2.0;
		double max_re = 2.0;
		double min_im = -2.0;
		double max_im = 2.0;
		int thread_count = 1;
		String nameoffile = "zad16";
		boolean quiet = false;
		
		CommandLineParser parser = new BasicParser();
		Options options = new Options();

		options.addOption("h", "height", true, "First Parameter");
		options.addOption("w", "width", true, "Second Parameter");
		options.addOption("a1", "min_real", true, "Third Parameter");
		options.addOption("a2", "max_real", true, "Fourth Parameter");
		options.addOption("b1", "min_imaginary", true, "Fifth Parameter");
		options.addOption("b2", "max_imaginary", true, "Sixth Parameter");
		options.addOption("o", "output", true, "Seventh Paramater");
		options.addOption("t", "tasks", true, "Eighth Paramater");
		options.addOption("q", "quiet", true, "Ninth Paramater");

		try {
			CommandLine commandLine = parser.parse(options, args);
			
			if(commandLine.hasOption("h")) {
				height = Integer.parseInt(commandLine.getOptionValue("h"));
			}
			if(commandLine.hasOption("w")) {
				width = Integer.parseInt(commandLine.getOptionValue("w"));
			}
			if(commandLine.hasOption("a1")) {
				min_re = Double.parseDouble(commandLine.getOptionValue("a1"));
			}
			if(commandLine.hasOption("a2")) {
				max_re = Double.parseDouble(commandLine.getOptionValue("a2"));
			}
			if(commandLine.hasOption("b1")) {
				min_im = Double.parseDouble(commandLine.getOptionValue("b1"));
			}
			if(commandLine.hasOption("b2")) {
				max_im = Double.parseDouble(commandLine.getOptionValue("b2"));
			}
			if(commandLine.hasOption("t")) {
				thread_count = Integer.parseInt(commandLine.getOptionValue("t"));
			}
			if(commandLine.hasOption("o")) {
				nameoffile = (commandLine.getOptionValue("o"));
			}
			if(commandLine.hasOption("q")) {
				quiet = Boolean.parseBoolean(commandLine.getOptionValue("q"));
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int chunk_size = height / thread_count;
		int rest = height % thread_count;
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	
		int maxiterations = 1000;
		int[] colors = new int[maxiterations];
		
        for (int i = 0; i < maxiterations; i++) {
            colors[i] = Color.HSBtoRGB(i/8f, 1, i/(i+2f));
        }
		
        Thread tr[] = new Thread[thread_count];
        
        ElapsedTime whole_time = new ElapsedTime(); 
		for(int i = 0; i < thread_count; i++) {

			int start_row = i * chunk_size;
			int end_row = (i + 1) * chunk_size;
			
			if((i == (thread_count - 1)) && (rest != 0))
				end_row+= rest;
		
			FractalRunnable r = new FractalRunnable(bi, nameoffile, height, width, start_row, end_row, min_re, max_re, min_im, max_im, 
					thread_count, i, maxiterations, colors, quiet);
			Thread t = new Thread(r);
			tr[i] = t;
			t.start();
		}
		
		for(int i = 0; i < thread_count; i++) {
			
			try {
				
				tr[i].join();

			} catch (InterruptedException e) {
				
			}	
		}
		
		System.out.println("Total execution time for current run (millis) : " + whole_time.elapsed());
		
		if(quiet == false)
			System.out.println("Threads used in current run : " + thread_count);
		
		try {
			ImageIO.write(bi, "PNG", new File(nameoffile + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
  }
}
