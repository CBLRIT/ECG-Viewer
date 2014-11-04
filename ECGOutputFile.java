
import java.io.IOException;

public interface ECGOutputFile {
	public void write(double[][][] data) throws IOException;
}
