
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/*
 * Use this as a template for writing new plugins.
 * The methods stubbed out here are necessary for proper operation.
 */
public class _12Lead extends ECGFile {
	/**
	 * read - reads in the data
	 * @param filename The name of the file to read
	 * @param points A mutable object where the data read in is stored.
	 *				 The structure is a list of key-value pairs that hold time
	 *				 and a list of values across all leads.
	 * @return 0 on success, anything else otherwise
	 */
	public int read(String fileName, double start, double length,
					ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file \"" + fileName + "\"");
			return -1;
		}

		String line;
		String regex = "\\s+";
		double time = 0.0;
		int count = 0;
		try {
			while((line = reader.readLine()) != null) {
				if(time >= start && time < start+length) {
					String[] values = line.split(regex);

					points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
						time, new ArrayList<Double>()));

					for(int i = 2; i < 14; i++) { //skip first
						points.get(count).getValue().add(Double.parseDouble(values[i]));
					}
					count++;
				}

				time += getSampleInterval();
			}
			
		} catch (IOException e) {
			System.err.println("Error reading file \"" + fileName + "\"");
			return -2;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
											"Improper format on line " + (count+1), 
											"Error", JOptionPane.ERROR_MESSAGE);
		}

		return 0;
	}
					
	/**
	 * getSampleInterval - returns the amount of time between samples in milliseconds
	 */
	public double getSampleInterval() {
		return 1.0;
	}

	/**
	 * getLayout - returns how the leads should be visually positioned
	 * @return An array of x,y coordinates that describe how that lead should be positioned.
	 *		   The coordinates are aligned to the upper-left corner of the screen.
	 *		   Coordinates are in blocks, so pixel perfection is not necessary.
	 *		   Ex: { {1, 2},  {1, 3}, ..............., {i, j} } 
	 *				 lead 1,  lead 2, ..............., lead n
	 *  1st column 2nd row, 1st col 3rd row, ...., 5th column 5th row 
	 *		   Any coordinates with a negative number are excluded from being drawn.
	 */
	public int[][] getLayout() {
		return new int[][] {
			{0, 1},	{0, 2},	{0, 3},	{0, 4},	{0, 5},
			{1, 0},	{1, 1},	{1, 2},	{1, 3},	{1, 4},	{1, 5},	{1, 6}
		};
	}

	public String[] getTitles() {
		return new String[] {
			"I", "II", "III", "AVR", "AVL",
			"AVF", "V1", "V2", "V3", "V4", "V5", "V6"
		};
	}

	/**
	 * getExtension - gets the file type extension associated with this file type
	 * @return A string of the typical file extension of this type
	 */
	public String getExtension() {
		return "big";
	}
}

