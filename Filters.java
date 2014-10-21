
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.SGFilter;

public class Filters {
	public static void SavitzkyGolay(double[][] data) {
		double[] coeffs = SGFilter.computeSGCoefficients(2500, 2500, 6);
		ContinuousPadder p = new ContinuousPadder();
		SGFilter sgFilter = new SGFilter(2500, 2500);
		sgFilter.appendPreprocessor(p);
		data[1] = sgFilter.smooth(data[1], coeffs);
	}
}

