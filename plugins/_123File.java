
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

	public int[][] getLayout() {
		return new int[][]{
		{-1, -1}, // // first two are junk
		{-1, -1}, // //
		{-1, -1}, // 1-3 unhelpful (limb leads)
		{-1, -1}, //
		{-1, -1}, //
		{4, 0},		{5, 0}, 	{6, 0}, 	{7, 0}, 								   	//4-7
		{1, 1}, 	{2, 1}, 	{3, 1}, 	{4, 1}, 	{5, 1}, 	{6, 1}, 	{7, 1},	//8-14
		{1, 2}, 	{2, 2}, 	{3, 2}, 	{4, 2},		{5, 2},		{6, 2}, 	{7, 2},	//15-21
		{1, 3},		{2, 3},		{3, 3},		{4, 3},		{5, 3},		{6, 3},		{7, 3},	//22-28
		{1, 4},		{2, 4},		{3, 4},		{4, 4},		{5, 4},		{6, 4},		{7, 4},	//29-35
		{1, 5},		{2, 5},		{3, 5},		{4, 5},		{5, 5},		{6, 5},		{7, 5},	//36-42
		{1, 6},		{2, 6},		{3, 6},		{4, 6},		{5, 6},		{6, 6},		{7, 6},	//43-49
		{1, 7},		{2, 7},		{3, 7},		{4, 7},		{5, 7},		{6, 7},		{7, 7},	//50-56
		{1, 8},		{2, 8},		{3, 8},		{4, 8},		{5, 8},		{6, 8},		{7, 8},	//57-63
		{1, 9},		{2, 9},		{3, 9},		{4, 9},		{5, 9},		{6, 9},		{7, 9},	//64-70
		{4, 10},	{5, 10}, 	{6, 10}, 	{7, 10}, 								   	//71-74
		{4, 11},	{5, 11}, 	{6, 11}, 	{7, 11}, 									//75-78
		{1, 12},	{2, 12},	{3, 12},	{4, 12},	{5, 12},	{6, 12},	{7, 12},//79-85
		{1, 13},	{2, 13},	{3, 13},	{4, 13},	{5, 13},	{6, 13},	{7, 13},//86-92
		{0, 14},	{1, 14},	{2, 14},	{3, 14},	{4, 14},	{5, 14},	{6, 14},//93-
					{7, 14},															//  -100
		{0, 15},	{1, 15},	{2, 15},	{3, 15},	{4, 15},	{5, 15},	{6, 15},//101-
					{7, 15},															//	 -108
		{0, 16},	{1, 16},	{2, 16},	{3, 16},	{4, 16},	{5, 16},	{6, 16},//109-
					{7, 16},															//	 -116
		{1, 17},	{2, 17},	{3, 17},	{4, 17},	{5, 17},	{6, 17},	{7, 17},//117-123
		};
	}

	/**
	* read - opens a file and reads it
	* @param fileName the file to open
	* @param numLeads the number of leads, can be less than 0 (assumes default value of 8)
	* @param points (mutable) a place for data to be read into
	* @return 0 on success, failure otherwise
	*/
	public int read(String fileName, int numLeads,
			ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file \"" + fileName + "\"");
			return -1;
		}

		if(numLeads < 0) {
			numLeads = 8;
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

