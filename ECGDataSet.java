
import java.util.ArrayList;
import java.util.HashSet;

public class ECGDataSet {
	private ArrayList<Double[]> set;
	private boolean bad;
	private HashSet<Integer> annotations; //indicies into set

	public ECGDataSet() {
		set = new ArrayList<Double[]>();
		annotations = new HashSet<Integer>();
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

	public void toggleAnnotation(int i) {
		if(annotations.contains(i)) {
			annotations.remove(i);
		} else {
			annotations.add(i);
		}
		System.out.println(i);
	}

	public boolean isAnnotated(int i) {
		return annotations.contains(i);
	}
}
