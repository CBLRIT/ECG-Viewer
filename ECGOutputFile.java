
import java.io.IOException;

/**
 * interface ECGOutputFile - interface for file formats that data can be
 *							 written to
 *
 * @author Dakota Williams
 */
public interface ECGOutputFile {
	/**
	 * write - writes the data
	 * @throws IOException when an IOException occurs
	 *
	 * @param data the data to write
	 */
	public void write(double[][][] data) throws IOException;
}
