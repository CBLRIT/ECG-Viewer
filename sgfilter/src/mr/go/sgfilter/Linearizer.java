/*
 * Copyright [2009] [Marcin Rzeźnicki]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package mr.go.sgfilter;

import static java.lang.Math.abs;

/**
 * Linearizes data by seeking points with relative difference greater than
 * {@link #getTruncateRatio() truncateRatio} and replacing them with points
 * lying on line between the first and the last of such points. Strictly:
 * <p>
 * let <tt>delta(i)</tt> be function which assigns to an element at index
 * <tt>i (data[i])</tt>, for <tt>0
 * <= i < data.length - 1</tt>, value of
 * <tt>|(data[i] - data[i+1])/data[i]|</tt>. Then for each range <tt>(j,k)</tt>
 * of data, such that
 * <tt>delta(j) > {@link #getTruncateRatio() truncateRatio}</tt> and
 * <tt>delta(k)
 * <= {@link #getTruncateRatio() truncateRatio}</tt>, <tt>data[x] = ((data[k] -
 * data[j])/(k - j)) * (x - k) + data[j])</tt> for <tt>j <= x <= k</tt>.
 * </p>
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class Linearizer implements Preprocessor {

	private float truncateRatio = 0.5f;

	/**
	 * Default constructor. {@link #getTruncateRatio() truncateRatio} is 0.5
	 */
	public Linearizer() {

	}

	/**
	 * 
	 * @param truncateRatio
	 *            maximum relative difference of subsequent data points above
	 *            which linearization begins
	 * @throws IllegalArgumentException
	 *             when {@code truncateRatio} < 0
	 */
	public Linearizer(float truncateRatio) {
		if (truncateRatio < 0f)
			throw new IllegalArgumentException("truncateRatio < 0");
		this.truncateRatio = truncateRatio;
	}

	@Override
	public void apply(double[] data) {
		int n = data.length - 1;
		double[] deltas = computeDeltas(data);
		linreg: for (int i = 0; i < n; i++) {
			if (deltas[i] > truncateRatio) {
				for (int k = i + 1; k < n; k++) {
					if (deltas[k] <= truncateRatio) {
						linest(data, i, k);
						i = k - 1;
						continue linreg;
					}
				}
				linest(data, i, n);
				break;
			}
		}
	}

	protected double[] computeDeltas(double[] data) {
		int n = data.length;
		double[] deltas = new double[n - 1];
		for (int i = 0; i < n - 1; i++) {
			if (data[i] == 0 && data[i + 1] == 0) {
				deltas[i] = 0;
			} else {
				deltas[i] = abs(1 - data[i + 1] / data[i]);
			}
		}
		return deltas;
	}

	/**
	 * 
	 * @return {@code truncateRatio}
	 */
	public float getTruncateRatio() {
		return truncateRatio;
	}

	protected void linest(double[] data, int x0, int x1) {
		if (x0 + 1 == x1)
			return;
		double slope = (data[x1] - data[x0]) / (x1 - x0);
		double y0 = data[x0];
		for (int x = x0 + 1; x < x1; x++) {
			data[x] = (slope * (x - x0) + y0);
		}
	}

	/**
	 * 
	 * @param truncateRatio
	 *            maximum relative difference of subsequent data points above
	 *            which linearization begins
	 * @throws IllegalArgumentException
	 *             when {@code truncateRatio} < 0
	 */
	public void setTruncateRatio(float truncateRatio) {
		if (truncateRatio < 0f)
			throw new IllegalArgumentException("truncateRatio < 0");
		this.truncateRatio = truncateRatio;
	}

}
