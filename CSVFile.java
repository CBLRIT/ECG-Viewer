
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
	public void write(double data[][][]) 
			throws IOException {
		for(int i = 2; i < data.length-5; i++) {
			out.write(",", 0, 1);
			out.write("" + (i-1), 0, new Integer(i-1).toString().length());
		}
		out.write("\n", 0, 1);

		for(int i = 0; i < data[0][0].length; i++) {
			out.write("" + data[0][0][i], 0, new Double(data[0][0][i]).toString().length());
			for(int j = 2; j < data.length-5; j++) {
				out.write("," + data[j][1][i], 0, new Double(data[j][1][i]).toString().length() + 1);
			}
			out.write("\n", 0, 1);
		}

		out.flush();
		out.close();
	}
}

