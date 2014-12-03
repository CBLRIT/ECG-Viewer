
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

