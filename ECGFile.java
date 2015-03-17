
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ECGFile - opens and reads a file with ECG data
 * @author Dakota Williams
 */
public abstract class ECGFile {
	public abstract int read(String fileName,
					ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points);
					
	public abstract double getSampleInterval();

	public abstract int[][] getLayout();

	public abstract String getExtension();
}

