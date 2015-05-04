
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;

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
	public int read(String fileName,
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
				String[] values = line.split(regex);

				points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
					time, new ArrayList<Double>()));

				for(int i = 2; i < 14; i++) { //skip first
					points.get(count).getValue().add(Double.parseDouble(values[i]));
				}

				time += getSampleInterval();
				count++;
			}
			
		} catch (IOException e) {
			System.err.println("Error reading file \"" + fileName + "\"");
			return -2;
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
	 *		   Ex: { {1,2,1}, {1,3,2}, .............., {i, j, n} } 
	 *				 lead 1,  lead 2,  .............., lead n
	 *  1st column 2nd row, 1st col 3rd row, ...., 5th column 5th row 
	 *		   Any coordinates with a negative number are excluded from being drawn.
	 */
	public int[][] getLayout() {
		return new int[][]{
			{0, 1, 1},	{0, 2, 2},	{0, 3, 3},	{0, 4, 4},	{0, 5, 5},
			{1, 0, 6},	{1, 1, 7},	{1, 2, 8},	{1, 3, 9},	{1, 4, 10},	{1, 5, 11},	{1, 6, 12}
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

