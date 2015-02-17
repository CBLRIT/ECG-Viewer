
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
	public void write(ECGDataSet[] data) throws IOException;

	/**
	 * writeSubset - writes part of the data
	 * @throws IOException when an IOException occurs
	 *
	 * @param data the data to write
	 * @param start the index to start at
	 * @param end the index to end at
	 */
	public void writeSubset(ECGDataSet[] data, int start, int end) throws IOException;
}
