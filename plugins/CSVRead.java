
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
	public int read(String fileName,
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

				points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
					Double.parseDouble(tokens[0]), 
					new ArrayList<Double>()));
				for(int i = 1; i < tokens.length; i++) {
					points.get(count).getValue().add(Double.parseDouble(tokens[i]));
				}

				count++;
			}
			
			reader.close();
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
			case 120:
		return new int[][]{
		{-1, -1, -1}, // // first two are junk
		{-1, -1, -1}, // //
		{-1, -1, -1}, // 1-3 unhelpful (limb leads)
		{-1, -1, -1}, //
		{-1, -1, -1}, //
		{4, 0, 4},	{5, 0, 5}, 	{6, 0, 6}, 	{7, 0, 7},
		{1, 1, 8}, 	{2, 1, 9}, 	{3, 1, 10}, {4, 1, 11},	{5, 1, 12},	{6, 1, 13},	{7, 1, 14},
		{1, 2, 15}, {2, 2, 16}, {3, 2, 17}, {4, 2, 18}, {5, 2, 19}, {6, 2, 20}, {7, 2, 21},
		{1, 3, 22}, {2, 3, 23}, {3, 3, 24}, {4, 3, 25}, {5, 3, 26}, {6, 3, 27}, {7, 3, 28},
		{1, 4, 29}, {2, 4, 30}, {3, 4, 31}, {4, 4, 32}, {5, 4, 33}, {6, 4, 34}, {7, 4, 35},
		{1, 5, 36}, {2, 5, 37}, {3, 5, 38}, {4, 5, 39}, {5, 5, 40}, {6, 5, 41}, {7, 5, 42},
		{1, 6, 43}, {2, 6, 44}, {3, 6, 45}, {4, 6, 46}, {5, 6, 47}, {6, 6, 48}, {7, 6, 49},
		{1, 7, 50}, {2, 7, 51}, {3, 7, 52}, {4, 7, 53}, {5, 7, 54}, {6, 7, 55}, {7, 7, 56},
		{1, 8, 57}, {2, 8, 58}, {3, 8, 59}, {4, 8, 60}, {5, 8, 61}, {6, 8, 62}, {7, 8, 63},
		{1, 9, 64}, {2, 9, 65}, {3, 9, 66}, {4, 9, 67}, {5, 9, 68}, {6, 9, 69}, {7, 9, 70},
		{4, 10, 71},{5, 10, 72},{6, 10, 73},{7, 10, 74},
		{4, 11, 75},{5, 11, 76},{6, 11, 77},{7, 11, 78},
		{1, 12, 79},{2, 12, 80},{3, 12, 81},{4, 12, 82},{5, 12, 83},{6, 12, 84},{7, 12, 85},
		{1, 13, 86},{2, 13, 87},{3, 13, 88},{4, 13, 89},{5, 13, 90},{6, 13, 91},{7, 13, 92},
		{0, 14, 93},{1, 14, 94},{2, 14, 95},{3, 14, 96},{4, 14, 97},{5, 14, 98},{6, 14, 99},
																				{7, 14,100},
		{0, 15,101},{1, 15,102},{2, 15,103},{3, 15,104},{4, 15,105},{5, 15,106},{6, 15,107},
																				{7, 15,108},
		{0, 16,109},{1, 16,110},{2, 16,111},{3, 16,112},{4, 16,113},{5, 16,114},{6, 16,115},
																				{7, 16,116},
		{1, 17,117},{2, 17,118},{3, 17,119},{4, 17,120},{5, 17,121},{6, 17,122},{7, 17,123},
		};
			case 64:
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
