
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * _64Lead: opens and reads a 64 lead dataset
 * @author Dakota Williams
 */
public class _64Lead extends ECGFile {
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

				double val = 0.0;
				for(int i = 0; i < 64; i++) {
					val = (((double)Integer.parseInt(values[i])) - 16384.0) * 10.0 / 32768.0;
					points.get(count).getValue().add(val);
				}

				time += getSampleInterval();
				count++;
			}
		} catch (IOException e) {
			System.err.println("Something happened while reading "
												+ fileName + "\n" + e.getMessage());
			return -2;
		}

		return 0;
	}
					
	/**
	 * getSampleInterval - returns the amount of time between samples in milliseconds
	 */
	public double getSampleInterval() {
		return 1000.0/1024.0; //1024 Hz * 1000 ms per s
	}

	/**
	 * getLayout - returns how the leads should be visually positioned
	 * @return An array of x,y coordinates that describe how that lead should be positioned.
	 *		   The 3rd component is an integer "title" used for that lead's number.
	 *		   The coordinates are aligned to the upper-left corner of the screen.
	 *		   Coordinates are in blocks, so pixel perfection is not necessary.
	 *		   Ex: { {1,2,1}, {1,3,2}, .............., {i, j, n} } 
	 *				lead 1, lead 2, ............., lead n
	 *  1st column 2nd row, 1st col 3rd row, ...., 5th column 5th row 
	 *		   Any coordinates with a negative number are excluded from being drawn.
	 */
	public int[][] getLayout() {
		return new int[][]{
			{3, 0, 4}, {4, 0, 5}, {5, 0, 6},
			{1, 1, 9}, {3, 1, 11}, {5, 1, 13},
			{1, 2, 16}, {3, 2, 18}, {5, 2, 20},
			{0, 3, 22}, {1, 3, 23}, {2, 3, 24}, {3, 3, 25}, {4, 3, 26}, {5, 3, 27}, {6, 3, 28},
			{0, 4, 36}, {1, 4, 37}, {2, 4, 38}, {3, 4, 39}, {4, 4, 40}, {5, 4, 41}, {6, 4, 42},
			{1, 5, 44}, {2, 5, 45}, {3, 5, 46}, {4, 5, 47}, {5, 5, 48}, {6, 5, 49},
			{3, 6, 53}, {4, 6, 54},
			{1, 7, 58}, {2, 7, 59}, {3, 7, 60}, {4, 7, 61}, {5, 7, 62}, {6, 7, 63},
			{2, 8, 66}, {3, 8, 67}, {4, 8, 68}, {5, 8, 69},
			{3, 9, 71}, {4, 9, 72}, {5, 9, 73}, {6, 9, 74},
			{3, 10, 75}, {4, 10, 76}, {5, 10, 77}, {6, 10, 78},
			{1, 11, 80}, {3, 11, 82}, {5, 11, 84},
			{1, 12, 87}, {3, 12, 89}, {5, 12, 91},
			{1, 13, 95}, {3, 13, 97}, {5, 13, 99},
			{1, 14, 111}, {3, 14, 113}, {5, 14, 115},
			{1, 15, 118}, {3, 15, 120}, {5, 15, 122},
		};
	}

	/**
	 * getExtension - gets the file type extension associated with this file type
	 * @return A string of the typical file extension of this type
	 */
	public String getExtension() {
		return "txt";
	}
}

