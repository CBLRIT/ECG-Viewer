
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ECGDataSet {
	private List<Double[]> set;
	private boolean bad;
	private HashSet<Double> annotations; //indicies into set

	public ECGDataSet() {
		set = new ArrayList<Double[]>();
		annotations = new HashSet<Double>();
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

	public Object clone() {
		ECGDataSet eds = new ECGDataSet();
		eds.set = new ArrayList(this.set);
		eds.bad = this.bad;
		eds.annotations = new HashSet(this.annotations);
		return eds;
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

	public ArrayList<Double> getAnnotations() {
		return new ArrayList<Double>(annotations);
	}

	public void addAnnotation(double i) {
		annotations.add(i);
	}

	public void clearAnnotations() {
		annotations.clear();
	}

	public boolean isAnnotated(double i) {
		return annotations.contains(i);
	}

	public void trimAnnotations(double between) {
		Double[] annos = (Double[])annotations.toArray(new Double[annotations.size()]);

		//find closest lower than between
		double lower = 0;
		for(int i = 0; i < annos.length; i++) {
			if(annos[i] < between && lower < annos[i]) {
				lower = annos[i];
			}
		}

		//find closet higher than between
		double higher = set.get(set.size()-1)[0];
		for(int i = 0; i < annos.length; i++) {
			if(annos[i] > between && higher > annos[i]) {
				higher = annos[i];
			}
		}

		//set should be always sorted, so a binary search shouldn't break
		int lowerInd = Math.abs(Arrays.binarySearch(this.toArray()[0], lower));
		int higherInd = Math.abs(Arrays.binarySearch(this.toArray()[0], higher));

		set = set.subList(lowerInd, higherInd);
	}

	public void detrend(int detrendPolynomial) {
		Filters.detrend(set, detrendPolynomial);
	}

	public void sgolayfilt(int left, int right, int degree) {
		Filters.sgolayfilt(set, left, right, degree);
	}

	public void lowpassfilt(double freq) {
		Filters.lowpassfilt(set, freq);
	}

	public void highpassfilt(double freq) {
		Filters.highpassfilt(set, freq);
	}

	public void highpassfftfilt(double lowfreq, double highfreq) {
		Filters.highpassfftfilt(set, lowfreq, highfreq);
	}
}
