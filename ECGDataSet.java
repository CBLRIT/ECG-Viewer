
import java.util.ArrayList;
import java.util.HashSet;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.SGFilter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class ECGDataSet {
	private final int detrendPolynomial = 6;

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

	public void detrend() {
		PolynomialCurveFitter p = PolynomialCurveFitter.create(detrendPolynomial);
		WeightedObservedPoints wop = new WeightedObservedPoints();
		for(int i = 0; i < set.size(); i++) {
			wop.add(set.get(i)[0], set.get(i)[1]);
		}
		double[] coeff = p.fit(wop.toList());
		for(int h = 0; h < set.size(); h++) {
			double val = set.get(h)[0];
			double off = 0;
			for(int i = detrendPolynomial; i >= 0; i--) {
				off += coeff[i] * Math.pow(val, i);
			}
			set.set(h, new Double[]{set.get(h)[0], set.get(h)[1]-off});
		}
	}

	public void sgolayfilt() {
		double[][] data = this.toArray();
		double[] coeffs = SGFilter.computeSGCoefficients(25, 25, 6);
	//	ContinuousPadder p = new ContinuousPadder();
		SGFilter sgFilter = new SGFilter(25, 25);
	//	sgFilter.appendPreprocessor(p);
		data[1] = sgFilter.smooth(data[1], coeffs);
		for(int i = 0; i < set.size(); i++) {
			set.set(i, new Double[]{set.get(i)[0], data[1][i]});
		}
	}
}
