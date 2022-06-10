
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSVRead - reads in CSV files that this program spits out
 * @author Dakota Williams
 */
public class CSVRead extends ECGFile {
	private int numLeads;
	private double samp;
	
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

		try {
			Pattern metadata = Pattern.compile("^(\\d+) leads sampled @(\\d+\\.\\d+)ms,.*");
			String line = reader.readLine();
			Matcher metaMatch = metadata.matcher(line);
			if(metaMatch.find()) {
				numLeads = Integer.parseInt(metaMatch.group(1));
				samp = Double.parseDouble(metaMatch.group(2));
			}

			String[] tokens;
			int count = 0;
			while((line = reader.readLine()) != null) {
				line.replaceAll("\\s+", ""); //remove whitespace
				tokens = line.split(",");

				double time = Double.parseDouble(tokens[0]);

				if(time >= start && time < start+length) {
					points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
						time, 
						new ArrayList<Double>()));
					for(int i = 1; i < tokens.length; i++) {
						points.get(count).getValue().add(Double.parseDouble(tokens[i]));
					}

					count++;
				}
			}
			
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading file \"" + fileName + "\"");
			return -2;
		}
		return 0;
	}

	public double getFileLength(String filename) throws IOException {
		BufferedReader reader;
		int count = 0;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			throw new IOException("Could not find file \"" + filename + "\"");
//			return -1;
		}

		try {
			Pattern metadata = Pattern.compile("^(\\d+) leads sampled @(\\d+\\.\\d+)ms,.*");
			String line = reader.readLine();
			Matcher metaMatch = metadata.matcher(line);
			if(metaMatch.find()) {
				numLeads = Integer.parseInt(metaMatch.group(1));
				samp = Double.parseDouble(metaMatch.group(2));
			}

			count = 0;
			while((line = reader.readLine()) != null) {
				count++;
			}

			reader.close();
		} catch (IOException e) {
			throw new IOException("Error reading file \"" + filename + "\"");
//			return -2;
		}
		return count;
	}
					
	/**
	 * getSampleInterval - returns the amount of time between samples in milliseconds
	 */
	public double getSampleInterval() {
		return samp;
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
		switch(numLeads) {
			case 123:
				return new int[][]{
					{9, 0}, // 1-3 limb leads
					{9, 2}, //
					{9, 4}, //
					{4, 0},	{5, 0},	{6, 0},	{7, 0},
					{1, 1},	{2, 1},	{3, 1}, {4, 1},	{5, 1},	{6, 1},	{7, 1},
					{1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2},
					{1, 3}, {2, 3}, {3, 3}, {4, 3}, {5, 3}, {6, 3}, {7, 3},
					{1, 4}, {2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4}, {7, 4},
					{1, 5}, {2, 5}, {3, 5}, {4, 5}, {5, 5}, {6, 5}, {7, 5},
					{1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6}, {6, 6}, {7, 6},
					{1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7},
					{1, 8}, {2, 8}, {3, 8}, {4, 8}, {5, 8}, {6, 8}, {7, 8},
					{1, 9}, {2, 9}, {3, 9}, {4, 9}, {5, 9}, {6, 9}, {7, 9},
					{4, 10},{5, 10},{6, 10},{7, 10},
					{4, 11},{5, 11},{6, 11},{7, 11},
					{1, 12},{2, 12},{3, 12},{4, 12},{5, 12},{6, 12},{7, 12},
					{1, 13},{2, 13},{3, 13},{4, 13},{5, 13},{6, 13},{7, 13},
					{0, 14},{1, 14},{2, 14},{3, 14},{4, 14},{5, 14},{6, 14},{7, 14},
					{0, 15},{1, 15},{2, 15},{3, 15},{4, 15},{5, 15},{6, 15},{7, 15},
					{0, 16},{1, 16},{2, 16},{3, 16},{4, 16},{5, 16},{6, 16},{7, 16},
					{1, 17},{2, 17},{3, 17},{4, 17},{5, 17},{6, 17},{7, 17},
				};
			case 64:
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
			case 12:
				return new int[][]{
					{0, 1},	{0, 2},	{0, 3},	{0, 4},	{0, 5},
					{1, 0},	{1, 1},	{1, 2},	{1, 3},	{1, 4},	{1, 5},	{1, 6}
				};
			default:
				return null;
		}
	}

	public String[] getTitles() {
		switch(numLeads) {
			case 123:
				return new String[] {
					"Limb 1", "Limb 2", "Limb 3",
					"4", "5", "6", "7",
					"8", "9", "10", "11", "12", "13", "14",
					"15", "16", "17", "18", "19", "20", "21",
					"22", "23", "24", "25", "26", "27", "28",
					"29", "30", "31", "32", "33", "34", "35",
					"36", "37", "38", "39", "40", "41", "42",
					"43", "44", "45", "46", "47", "48", "49",
					"50", "51", "52", "53", "54", "55", "56",
					"57", "58", "59", "60", "61", "62", "63",
					"64", "65", "66", "67", "68", "69", "70",
					"71", "72", "73", "74",
					"75", "76", "77", "78",
					"79", "80", "81", "82", "83", "84", "85",
					"86", "87", "88", "89", "90", "91", "92",
					"93", "94", "95", "96", "97", "98", "99", "100",
					"101", "102", "103", "104", "105", "106", "107", "108",
					"109", "110", "111", "112", "113", "114", "115", "116",
					"117", "118", "119", "120", "121", "122", "123"
				};
			case 64:
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
			case 12:
				return new String[] {
					"I", "II", "III", "AVR", "AVL",
					"AVF", "V1", "V2", "V3", "V4", "V5", "V6"
				};
			default:
				return null;
		}
	}

	/**
	 * getExtension - gets the file type extension associated with this file type
	 * @return A string of the typical file extension of this type
	 */
	public String getExtension() {
		return "csv";
	}
}
