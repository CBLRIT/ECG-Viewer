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

/**
 * De-trends data by setting straight line between the first and the last point
 * and subtracting it from data. Having applied filters to data you should
 * reverse detrending by using {@link TrendRemover#retrend(double[], double[])}
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class TrendRemover implements Preprocessor {

	@Override
	public void apply(double[] data) {
		// de-trend data so to avoid boundary distortion
		// we will achieve this by setting straight line from end to beginning
		// and subtracting it from the trend
		int n = data.length;
		if (n <= 2)
			return;
		double y0 = data[0];
		double slope = (data[n - 1] - y0) / (n - 1);
		for (int x = 0; x < n; x++) {
			data[x] -= (slope * x + y0);
		}
	}

	/**
	 * Reverses the effect of {@link #apply(double[])} by modifying {@code
	 * newData}
	 * 
	 * @param newData
	 *            processed data
	 * @param data
	 *            original data
	 */
	public void retrend(double[] newData, double[] data) {
		int n = data.length;
		double y0 = data[0];
		double slope = (data[n - 1] - y0) / (n - 1);
		for (int x = 0; x < n; x++) {
			newData[x] += slope * x + y0;
		}
	}

}
