
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * class ECGDataSet - 
public class ECGDataSet {
	private List<Double[]> set;
	private boolean bad;
	private HashSet<Annotation> annotations; //indicies into set
	private double sampleFreq;

	public ECGDataSet() {
		set = new ArrayList<Double[]>();
		annotations = new HashSet<Annotation>();
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
		eds.set = new ArrayList<Double[]>(this.set);
		eds.bad = this.bad;
		eds.annotations = new HashSet<Annotation>(this.annotations);
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

	public ArrayList<Annotation> getAnnotations() {
		return new ArrayList<Annotation>(annotations);
	}

	public void addAnnotation(int type, double i) {
		annotations.add(new Annotation(type, i));
	}

	public void clearAnnotations() {
		annotations.clear();
	}

	public boolean isAnnotated(double i) {
		return annotations.contains(i);
	}

	public ECGDataSet subset(double start, double end) {
		ECGDataSet newSet = new ECGDataSet();

		for(int i = 0; i < this.set.size(); i++) {
			if(this.set.get(i)[0] >= start && this.set.get(i)[0] < end) {
				newSet.set.add(this.set.get(i));
			}
		}

		return newSet;
	}

	public void trimAnnotations(double between) {
		Annotation[] annos=(Annotation[])annotations.toArray(new Annotation[annotations.size()]);

		//find closest lower than between
		double lower = 0;
		for(int i = 0; i < annos.length; i++) {
			if(annos[i].getType() != Main.getSelectedAnnotationType()) {
				continue;
			}
			if(annos[i].getLoc() < between && lower < annos[i].getLoc()) {
				lower = annos[i].getLoc();
			}
		}

		//find closet higher than between
		double higher = set.get(set.size()-1)[0];
		for(int i = 0; i < annos.length; i++) {
			if(annos[i].getType() != Main.getSelectedAnnotationType()) {
				continue;
			}
			if(annos[i].getLoc() > between && higher > annos[i].getLoc()) {
				higher = annos[i].getLoc();
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
