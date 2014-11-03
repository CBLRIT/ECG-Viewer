
public class Filters {
	public static void detrend(ArrayList<Double[]> set, int detrendPolynomial) {
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

	public static void sgolayfilt(ArrayList<Double[]> set, int left, int right, int degree) {
		double[][] data = this.toArray(set);
		double[] coeffs = SGFilter.computeSGCoefficients(left, right, degree);
	//	ContinuousPadder p = new ContinuousPadder();
		SGFilter sgFilter = new SGFilter(left, right);
	//	sgFilter.appendPreprocessor(p);
		data[1] = sgFilter.smooth(data[1], coeffs);
		for(int i = 0; i < set.size(); i++) {
			set.set(i, new Double[]{set.get(i)[0], data[1][i]});
		}
	}

	public static void lowpassfilt(ArrayList<Double[]> set, double freq) {
		Double RC = 1/(2*Math.PI)/freq;
		Double dt = set.get(1)[0] - set.get(0)[0];
		Double a = dt / (RC + dt);

		Double lasty = set.get(0)[1];
		for(int i = 1; i < set.size(); i++) {
			Double x = set.get(i)[1];

			lasty = lasty + a * (x - lasty);
			set.set(i, new Double[]{set.get(i)[0], lasty});
		}
	}

	public static void highpassfiltArrayList<Double[]> set, (double freq) {
		Double RC = 1/(2*Math.PI)/freq;
		Double dt = set.get(1)[0] - set.get(0)[0];
		Double a = RC / (RC + dt);

		Double lasty = set.get(0)[1];
		for(int i = 1; i < set.size(); i++) {
			Double x = set.get(i)[1];
			Double lastx = set.get(i-1)[1];

			lasty = a * (lasty + x - lastx);
			set.set(i, new Double[]{set.get(i)[0], lasty});
		}
	}
	
	private double[][] toArray(ArrayList<Double[]> set) {
		double[][] ret = new double[2][set.size()];

		for(int j = 0; j < set.size(); j++) {
			//do a transpose
			ret[0][j] = set.get(j)[0];
			ret[1][j] = set.get(j)[1];
		}
		return ret;
	}
}
