
import org.jfree.data.xy.DefaultXYDataset;

public class ECGModel {
	private DefaultXYDataset points;

	public ECGModel() {}

	public void readData(String filename) {
		//do this when file format is known
	}

	public DefaultXYDataset getDataset() {
		return points;
	}
}

