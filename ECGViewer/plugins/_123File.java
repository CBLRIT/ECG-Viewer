
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * _123File - opens and reads a .123 file with ECG data
 * @author Dakota Williams
 */
public class _123File extends ECGFile {
	private double sint;

	public String getExtension() {
		return "123";
	}

	public int[][] getLayout() {
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
	}

	/**
	* read - opens a file and reads it
	* @param fileName the file to open
	* @param points (mutable) a place for data to be read into
	* @return 0 on success, failure otherwise
	*/
	public int read(String fileName,
			ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points) {
		int numLeads = 120;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file \"" + fileName + "\"");
			return -1;
		}

		//loop over header info
		String line;
		try {
			for(int i = 0; i < 13; i++) {
				line = reader.readLine();
				if(i == 5) {
					sint = 1.0 / Double.parseDouble(line) * 1000.0;
				}
			}

			line = reader.readLine();
			String regex = "\\s+";
			int count = 0;
			while(line != null) {
				String[] words = line.split(regex);
				double time = (double)(Integer.parseInt(words[1])-1) * sint;

				points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
					time, new ArrayList<Double>()));

				for(int i = 0; i < 5; i++) {
					points.get(count).getValue().add(0.0);
				}

				for(int i = 2; i < numLeads+2; i++) {
					points.get(count).getValue().add(Double.parseDouble(words[i]));
				}

				for(int i = 0; i < 5; i++) {
					points.get(count).getValue().add(0.0);
				}
				
				count++;
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Something happened while reading "
												+ fileName + "\n" + e.getMessage());
			return -2;
		}

		return 0;
	}

	public double getSampleInterval() {
		return sint;
	}
}

