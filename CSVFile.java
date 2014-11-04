
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CSVFile implements ECGOutputFile {
	private OutputStreamWriter out;

	public CSVFile(String filename) {
		try { 
			out = new OutputStreamWriter(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {}
	}

	public void write(double data[][][]) 
			throws IOException {
		for(int i = 0; i < data.length; i++) {
			out.write(",", 0, 1);
			out.write("" + i, 0, new Integer(i).toString().length());
		}
		out.write("\n", 0, 1);

		for(int i = 0; i < data[0][0].length; i++) {
			out.write("" + data[0][0][i], 0, new Double(data[0][0][i]).toString().length());
			for(int j = 0; j < data.length; j++) {
				out.write("," + data[j][1][i], 0, new Double(data[j][1][i]).toString().length() + 1);
			}
			out.write("\n", 0, 1);
		}

		out.flush();
		out.close();
	}
}

