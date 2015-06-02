
import java.util.ArrayList;
import java.util.List;
import math.jwave.Transform;
import math.jwave.transforms.AncientEgyptianDecomposition;
import math.jwave.transforms.FastWaveletTransform;
import math.jwave.transforms.wavelets.daubechies.Daubechies10;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.SGFilter;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.fitting.HarmonicCurveFitter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Filters {
	public static void detrend(List<Double[]> set, int detrendPolynomial) {
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

/*
	public static void harmonicDetrend(List<Double[]> set) {
		HarmonicCurveFitter p = HarmonicCurveFitter.create();
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
	*/

	public static void sgolayfilt(List<Double[]> set, int left, int right, int degree) {
		double[][] data = Filters.toArray(set);
		double[] coeffs = SGFilter.computeSGCoefficients(left, right, degree);
	//	ContinuousPadder p = new ContinuousPadder();
		SGFilter sgFilter = new SGFilter(left, right);
	//	sgFilter.appendPreprocessor(p);
		data[1] = sgFilter.smooth(data[1], coeffs);
		for(int i = 0; i < set.size(); i++) {
			set.set(i, new Double[]{set.get(i)[0], data[1][i]});
		}
	}

	public static void lowpassfilt(List<Double[]> set, double freq) {
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

	public static void highpassfilt(List<Double[]> set, double freq) {
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

	public static void highpassfftfilt(List<Double[]> set, double lowfreq, double highfreq) {
		/*
		 * steps:
		 * 1. pad data with 0s to get the number of samples to a power of 2
		 * 2. perform fft
		 * 3. filter based on frequency (see http://stackoverflow.com/a/2876292)
		 * 4. inverse fft
		 */

		//step 1
		ArrayList<Double[]> padded = new ArrayList<Double[]>(set);
		int padTo = Filters.findNextLargestPower2(padded.size());
		for(int i = padded.size(); i < padTo; i++) {
			padded.add(new Double[]{0.0, 0.0});
		}
		double[][] arr = Filters.toArray(padded);

		//step 2
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] out = fft.transform(arr[1], TransformType.FORWARD);

		//step 3
		double sampFreq = 1000.0 / (arr[0][1]-arr[0][0]);
		int posHalf = out.length / 2;
		for(int i = 0; i < posHalf; i++) {
			int negInd = out.length - 1 - i;
			double currFreq = (double)i * (sampFreq / (double)posHalf);
			if (currFreq > lowfreq) {
				out[i] = Complex.ZERO;
				out[negInd] = Complex.ZERO;
			} /* else if (currFreq < highfreq) {
				double scale = 1.0 - ((currFreq - highFreq) / (lowFreq - highFreq));
				out[i] = out[i].multiply(scale);
				out[negInd] = out[negInd].multiply(scale);
			} */
		}

		//step 4
		out = fft.transform(out, TransformType.INVERSE);

		//write changes
		for(int i = 0; i < set.size(); i++) {
			set.set(i, new Double[]{set.get(i)[0], out[i].getReal()});
		}
	} 

	public static void waveletfilt(List<Double[]> set, double threshold) {
		Transform waveTrans = new Transform(new AncientEgyptianDecomposition(
												new FastWaveletTransform(new Daubechies10())));
		double[][] data = toArray(set);
		double[] forward = waveTrans.forward(data[1]);

		//filter magic
		for(int i = 0; i < forward.length; i++) {
			if(Math.abs(forward[i]) < threshold) {
				forward[i] = 1;
			}
		}

		double[] inverse = waveTrans.reverse(forward);
		for(int i = 0; i < inverse.length; i++) {
			set.set(i, new Double[]{data[0][i], inverse[i]});
		}
	}

	/**
	 * constofffilt - detrend by constant offset
	 *
	 * @param set the dataset to augment
	 * @param offset how much to offset the data
	 */
	public static void constofffilt(List<Double[]> set, double offset) {
		for(int i = 0; i < set.size(); i++) {
			set.set(i, new Double[]{set.get(i)[0], set.get(i)[1] + offset});
		}
	}

	/**
	 * butterworthfilt - butterworth filter using bilinear transformation
	 *
	 * @param set the dataset to filter
	 * @param mode the mode (low pass, high pass, band pass) to use
	 * @param rate the sampling frequency
	 * @param freq the cutoff frequency
	 * @param n the order of the filter. Precondition: n is even
	 */
	public static void butterworthfilt(List<Double[]> set,
									   int mode,
									   double rate,
									   double freq,
									   int n) {
		double c = Math.pow(Math.pow(2.0, 1.0/((double)n)) - 1.0, -0.25);
		double g = 1.0;
		double p = Math.sqrt(2.0);
		double f0 = 1/rate*1000;
		double f = 0.0;

		if(mode == 0) {  //low pass
			f = c * freq / f0;
			if(!(0 < f && f < 0.125)) {
				// will not work
				return;
			}
		} else if (mode == 1) { //high pass
			f = 0.5 - c * freq / f0;
			if(!(0.375 < f && f < 0.5)) {
				// will not work
				return;
			}
		}
		
		double omega = Math.tan(Math.PI * f);
		double k1 = p * omega;
		double k2 = g * omega * omega;
		double a0 = k2 / (1.0 + k1 + k2);
		double a1 = 2.0*a0;
		double a2 = a0;
		double b1 = 2.0*a0 * (1.0 / k2 - 1.0);
		double b2 = 1.0 - (a0 + a1 + a2 + b1);
		if(mode == 1) {
			a1 = -a1;
			b1 = -b1;
		}

		double x1 = set.get(1)[1];
		double x2 = set.get(0)[1];
		double y1 = x1;
		double y2 = x2;
		for(int i = 2; i < set.size(); i++) {
			double x = set.get(i)[1];
			double y = a0*x + a1*x1 + a2*x2 + b1*y1 + b2*y2;
			x2 = x1;
			x1 = x;
			y2 = y1;
			y1 = y;
			set.set(i, new Double[]{set.get(i)[0], y});
		}
	}

	private static int findNextLargestPower2(int n) {
		if(n < 0) {
			return 0;
		}

		int i = 0;
		for(; i < 32; i++ ) {
			if(n >> i == 0) {
				break;
			}
		}
		return 1 << i;
	}
	
	private static double[][] toArray(List<Double[]> set) {
		double[][] ret = new double[2][set.size()];

		for(int j = 0; j < set.size(); j++) {
			//do a transpose
			ret[0][j] = set.get(j)[0];
			ret[1][j] = set.get(j)[1];
		}
		return ret;
	}

	private static double[] convolve(double[] a1, double[] a2) {
		double[] out = new double[a1.length + a2.length - 1];
		double[] lower = (a1.length<a2.length)?a1:a2;
		double[] higher = (lower == a1)?a2:a1;
		for(int i = 0; i < out.length; i++) {
			for(int j = 0; j < lower.length; j++) {
				if(i < j || i-j >= higher.length) {
					continue;
				}
				out[i] += lower[j] * higher[i-j];
			}
		}
		return out;
	}
}
