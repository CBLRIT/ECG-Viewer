
import java.util.ArrayList;
import java.util.List;
import math.jwave.Transform;
import math.jwave.transforms.AncientEgyptianDecomposition;
import math.jwave.transforms.FastWaveletTransform;
import math.jwave.transforms.wavelets.daubechies.Daubechies10;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.SGFilter;
import org.apache.commons.math3.complex.Complex;
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
	 * @param gain the gain of the filter (dB)
	 * @param n the order of the filter
	 */
	public static void butterworthfilt(List<Double[]> set,
									   int mode,
									   double rate,
									   double freq,
									   double gain,
									   int n) {
		double T = 1.0/rate; //sampling period
		double wd = 2*Math.PI * freq;
		double wc = /*2.0/T **/ Math.tan(wd*T/2.0);
		double wc2 = wc*wc;

		//generate butterworth coefficients
		int upperBound = n/2; //even=n/2 odd=(n-1)/2 -> Wooo Integer Division!
		double[] coeffs;
		if(n % 2 == 0) { //even
			coeffs = new double[]{1};
		} else { //odd 
			coeffs = new double[]{1,wc};
		}
		int k = 1;
		do {
		//see https://en.wikipedia.org/wiki/Butterworth_filter#Normalized_Butterworth_polynomials
			coeffs = convolve(coeffs, 
							  new double[]{1, 
							  			   wc*-2.0*Math.cos((2.0*k+n-1.0)/(2.0*n) * Math.PI), 
										   wc2});
			k++;
		} while(k <= upperBound);
		
		//math stuff, see: http://www.robots.ox.ac.uk/~sjrob/Teaching/SP/l6.pdf section 6.4.1
		double[][] metaCoeffs = new double[coeffs.length][coeffs.length];
		double[] num = new double[]{1, -1};
		double[] denom = new double[]{1, 1};
		double[] denomPower = new double[]{1};
		for(int i = 0; i < coeffs.length; i++) {
			double[] numPower = new double[]{1};
			for(int j = 0; j < coeffs.length-i-1; j++) {
				numPower = convolve(num, numPower);
			}
			metaCoeffs[i] = convolve(numPower, denomPower);
			metaCoeffs[i] = convolve(metaCoeffs[i], new double[]{coeffs[i]});
			if(coeffs.length != i+1) {
				denomPower = convolve(denomPower, denom);
			}
		}
		
		//sum columns
		for(int i = 0; i < metaCoeffs[0].length; i++) {
			coeffs[i] = 0;
			for(int j = 0; j < metaCoeffs.length; j++) {
				coeffs[i] += metaCoeffs[j][i];
			}
		}

		//normalize denominator
		double norm = coeffs[0];
		wc2 /= norm;
		for(int i = 0; i < coeffs.length; i++) {
			coeffs[i] /= norm;
		}

		System.out.println(java.util.Arrays.toString(denomPower));

		double[] diffCoeffs = new double[denomPower.length + coeffs.length - 1];
		int i = 0;
		for(; i < denomPower.length; i++) {
			diffCoeffs[i] = denomPower[i] * wc2;
		}
		for(; i < coeffs.length-1+denomPower.length; i++) {
			diffCoeffs[i] = 0-(coeffs[i-denomPower.length+1]);
		}
		//finally have difference equation, apply to dataset
		ArrayList<Double[]> prev = new ArrayList<Double[]>();
		for(int j = 0; j < set.size(); j++) {
			Double[] point = set.get(j);
			prev.add(point);
			double newVal = 0;
			//diffCoeffs will always be odd
			for(k = 0; k < diffCoeffs.length/2+1 && k < prev.size(); k++) {
				newVal += diffCoeffs[k]*prev.get(k)[0];
			}
			for(k = 1; k < diffCoeffs.length/2 && k < prev.size(); k++) {
				newVal += diffCoeffs[diffCoeffs.length/2 + k]*prev.get(k)[1];
			}
			if(prev.size() > n+1) {
				prev.remove(0);
			}
			set.set(j, new Double[]{set.get(j)[0], newVal});
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
