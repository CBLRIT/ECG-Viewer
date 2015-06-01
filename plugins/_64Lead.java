
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
			{3, 0}, {4, 0}, {5, 0},
			{1, 1}, {3, 1}, {5, 1},
			{1, 2}, {3, 2}, {5, 2},
			{0, 3}, {1, 3}, {2, 3}, {3, 3}, {4, 3}, {5, 3}, {6, 3},
			{0, 4}, {1, 4}, {2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4},
			{1, 5}, {2, 5}, {3, 5}, {4, 5}, {5, 5}, {6, 5},
			{3, 6}, {4, 6},
			{1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7},
			{2, 8}, {3, 8}, {4, 8}, {5, 8},
			{3, 9}, {4, 9}, {5, 9}, {6, 9},
			{3, 10}, {4, 10}, {5, 10}, {6, 10},
			{1, 11}, {3, 11}, {5, 11},
			{1, 12}, {3, 12}, {5, 12},
			{1, 13}, {3, 13}, {5, 13},
			{1, 14}, {3, 14}, {5, 14},
			{1, 15}, {3, 15}, {5, 15},
		};
	}

	public String[] getTitles() {
		return new String[] {
			  "4",   "5",   "6",
			  "9",  "11",  "13",
			 "16",  "18",  "20",
			 "22",  "23",  "24",  "25",  "26",  "27",  "28",
			 "36",  "37",  "38",  "39",  "40",  "41",  "42",
			 "44",  "45",  "46",  "47",  "48",  "49",
			 "53",  "54",
			 "58",  "59",  "60",  "61",  "62",  "63",
			 "66",  "67",  "68",  "69",
			 "71",  "72",  "73",  "74",
			 "75",  "76",  "77",  "78",
			 "80",  "82",  "84",
			 "87",  "89",  "91",
			 "95",  "97",  "99",
			"111", "113", "115",
			"118", "120", "122"
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

