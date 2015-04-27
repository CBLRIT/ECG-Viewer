
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * class CSVFile - writes ECG data to an output file
 *
 * @author Dakota Williams
 */
public class CSVFile implements ECGOutputFile {
	private OutputStreamWriter out;

	/**
	 * Constructor - initializes the file writer
	 *
	 * @param filename the file to write to
	 */
	public CSVFile(String filename) {
		try { 
			out = new OutputStreamWriter(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {}
	}

	/**
	 * write - writes data to the file
	 *
	 * @param data The data to write. 
	 * 		Array describes (in order of outermost to innermost):
	 *			1. channel number
	 *			2. x (0) or y (1)
	 *			3. sample number
	 */
	public void write(ECGDataSet[] data, int offset) 
			throws IOException {
		for(int i = 0; i < data.length; i++) {
			out.write(",\t\t", 0, 3);
			out.write("" + (i+offset), 0, new Integer(i+offset).toString().length());
		}
		out.write("\n", 0, 1);
	
		String s;
		for(int i = 0; i < data[0].size(); i++) {
			s = String.format("%f", data[0].getAt(i)[0]);
			out.write("" + s, 0, s.length());
			for(int j = 0; j < data.length; j++) {
				s = String.format("%f", data[j].getAt(i)[1]);
				out.write(",\t" + s, 0, s.length() + 2);
			}
			out.write("\n", 0, 1);
		}

		out.flush();
		out.close();
	}

	/**
	 * writeSubset - writes part of the data to a file
	 *
	 * @param data the data to write. See write() for more detail
	 * @param start the index to start at
	 * @param end the index to end at
	 */
	public void writeSubset(ECGDataSet[] data, int start, int end, int offset)
			throws IOException {
		for(int i = 0; i < data.length; i++) {
			out.write(",\t\t", 0, 3);
			out.write("" + (i+offset), 0, new Integer(i+offset).toString().length());
		}
		out.write("\n", 0, 1);
	
		String s;
		for(int i = start; i < end; i++) {
			s = String.format("%f", data[0].getAt(i)[0]);
			out.write("" + s, 0, s.length());
			for(int j = 0; j < data.length; j++) {
				s = String.format("%f", data[j].getAt(i)[1]);
				out.write(",\t" + s, 0, s.length() + 2);
			}
			out.write("\n", 0, 1);
		}

		out.flush();
		out.close();
	}
}

