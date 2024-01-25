/**
 * This is a program that reads
 * multiple files (csv, txt, sql) 
 * and has functions such as
 * checking and removing blank
 * lines, count number of lines, 
 * find and replace word and
 * generate modified version of file
 */
package jl.com;

import java.io.IOException;

/**
 * @author jr.pastorin
 * Version 1.0
 */
public class Main {
	
	public static void main(String[] args) {
		FileReader reader = new FileReader();
		try {
			reader.runFileReader();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
