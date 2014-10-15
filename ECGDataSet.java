
import java.util.ArrayList;

public class ECGDataSet {
	private ArrayList<Double[]> set;
	private boolean bad;

	public ECGDataSet() {
		set = new ArrayList<Double[]>();
	}

	public boolean isBad() {return bad;}
	public void setBad(boolean b) {bad = b;}

	public void addTuple(double x, double y) {
		set.add(new Double[] {x, y});
	}

	public Double[] getAt(int index) {
		return set.get(index);
	}

	public int size() {
		return set.size();
	}

	public double[][] toArray() {
		double[][] ret = new double[2][set.size()];

		for(int j = 0; j < set.size(); j++) {
			//do a transpose
			ret[0][j] = set.get(j)[0];
			ret[1][j] = set.get(j)[1];
		}
		return ret;
	}
}

