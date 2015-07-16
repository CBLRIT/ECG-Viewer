
import java.io.BufferedReader; import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * _ecgFile - opens and reads a .ecg file with ECG data
 * @author Dakota Williams
 */
public class _ecgFile extends ECGFile {
	private double sint;
	private int leads;

	public String getExtension() {
		return "ecg";
	}

	public int[][] getLayout() {
		if(leads == 123) {
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
		} else {
			return new int[][] {
				{0, 1},	{0, 2},	{0, 3},	{0, 4},	{0, 5},
				{1, 0},	{1, 1},	{1, 2},	{1, 3},	{1, 4},	{1, 5},	{1, 6}
			};
		}
	}

	public String[] getTitles() {
		if(leads == 123) {
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
		} else {
			return new String[] {
				"I", "II", "III", "AVR", "AVL",
				"AVF", "V1", "V2", "V3", "V4", "V5", "V6"
			};
		}
	}

	/**
	* read - opens a file and reads it
	* @param fileName the file to open
	* @param points (mutable) a place for data to be read into
	* @return 0 on success, failure otherwise
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

		//loop over header info
		int count = 0;
		int numSamp = 0;
		String line = "";
		try {
			for(int i = 0; i < 13; i++) {
				line = reader.readLine().trim();
				if(i == 5) {
					double sampsec = Double.parseDouble(line);
					sint = 1.0 / sampsec * 1000.0;
				}
				if(i == 6) {
					numSamp = Integer.parseInt(line.split("\\s+")[0]);
				}
			}

			line = reader.readLine();
			String buffer;
			leads = (line.length() - 8)/9;
			if(leads == 128) {
				leads = 123;
			} else if (leads == 8) {
				leads = 12;
			}
			int loopCount = 0;
			while(line != null && loopCount < numSamp) {
				int begin = 8;
				int end = begin + 9; //constant character offset instead of whitespace

				buffer = line.substring(0, begin).trim();
				double time = (double)(Integer.parseInt(buffer)-1) * sint;

				if(time >= start && time < start+length) {
					points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Double>>(
						time, new ArrayList<Double>()));

					for(int i = 0; i < leads; i++) {
						if(leads == 12) {
							//special cases
							if(i == 2) { // III
								double I = points.get(count).getValue().get(0);
								double II = points.get(count).getValue().get(1);
								points.get(count).getValue().add(II-I);
								continue;
							} else if (i == 3) { //AVR
								double I = points.get(count).getValue().get(0);
								double II = points.get(count).getValue().get(1);
								points.get(count).getValue().add(-0.5 *(II+I));
								continue;
							} else if (i == 4) { //AVL
								double I = points.get(count).getValue().get(0);
								double III = points.get(count).getValue().get(2);
								points.get(count).getValue().add(0.5 *(I-III));
								continue;
							} else if (i == 5) { //AVF
								double II = points.get(count).getValue().get(1);
								double III = points.get(count).getValue().get(2);
								points.get(count).getValue().add(0.5 *(II+III));
								continue;
							}
						}
						buffer = line.substring(begin, end).trim();
						points.get(count).getValue().add(Double.parseDouble(buffer));

						begin = end;
						end += 9;
					}

					count++;
				}
				loopCount++;
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Something happened while reading "
								+ fileName + "\n" + e.getMessage());
			return -2;
		} catch (Exception e) {
			System.err.println("Error on line " + count + ": \"" + line + 
								"\" in file " +fileName+"\n" + e.getMessage());
			return -3;
		}

		return 0;
	}

	public double getSampleInterval() {
		return sint;
	}
}

