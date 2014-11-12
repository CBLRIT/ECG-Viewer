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
 * Pads data to left and/or right.:
 * 
 * <p>
 * <ul>
 * <li>
 * Let <tt>l</tt> be the index of the first non-zero element in data (for left
 * padding),</li>
 * <li>let <tt>r</tt> be the index of the last non-zero element in data (for
 * right padding)</li>
 * </ul>
 * then for every element <tt>e</tt> which index is <tt>i</tt> such that:
 * <ul>
 * <li>
 * <tt>0 <= i < l</tt>, <tt>e</tt> is replaced with arithmetic mean of
 * <tt>data[l]..data[l + window_length/2 - 1]</tt> (left padding)</li>
 * <li>
 * <tt>r < i < data.length</tt>, <tt>e</tt> is replaced with arithmetic mean of
 * <tt>data[r - window_length/2 + 1]..data[r]</tt> (right padding)</li>
 * </ul>
 * </p>
 * Example:
 * <p>
 * Given data: <tt>[0,0,0,1,2,1,3,1,2,4,0]</tt> result of applying
 * MeanValuePadder with {@link #getWindowLength() window_length} = 4 is:
 * <tt>[1.5,1.5,1.5,1,2,1,3,1,2,4,0]</tt> in case of {@link #isPaddingLeft()
 * left padding}; <tt>[0,0,0,1,2,1,3,1,2,4,3]</tt> in case of
 * {@link #isPaddingRight() right padding};
 * </p>
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class MeanValuePadder implements Preprocessor {

	private boolean paddingLeft = true;

	private boolean paddingRight = true;

	private int windowLength;

	/**
	 * 
	 * @param windowLength
	 *            window length of filter which will be used to smooth data.
	 *            Padding will use half of {@code windowLength} length. In this
	 *            way padding will be suited to smoothing operation
	 * @throws IllegalArgumentException
	 *             if {@code windowLength} < 0
	 */
	public MeanValuePadder(int windowLength) {
		if (windowLength < 0)
			throw new IllegalArgumentException("windowLength < 0");
		this.windowLength = windowLength;
	}

	/**
	 * 
	 * @param windowLength
	 *            window length of filter which will be used to smooth data.
	 *            Padding will use half of {@code windowLength} length. In this
	 *            way padding will be suited to smoothing operation
	 * @param paddingLeft
	 *            enables or disables left padding
	 * @param paddingRight
	 *            enables or disables left padding
	 * @throws IllegalArgumentException
	 *             if {@code windowLength} < 0
	 */
	public MeanValuePadder(int windowLength, boolean paddingLeft,
			boolean paddingRight) {
		if (windowLength < 0)
			throw new IllegalArgumentException("windowLength < 0");
		this.windowLength = windowLength;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
	}

	@Override
	public void apply(double[] data) {
		// padding values with average of last (WINDOW_LENGTH / 2) points
		int n = data.length;
		if (paddingLeft) {
			int l = 0;
			// seek first non-zero cell
			for (int i = 0; i < n; i++) {
				if (data[i] != 0) {
					l = i;
					break;
				}
			}
			double avg = 0;
			int m = Math.min(l + windowLength / 2, n);
			for (int i = l; i < m; i++) {
				avg += data[i];
			}
			avg /= (m - l);
			for (int i = 0; i < l; i++) {
				data[i] = avg;
			}
		}
		if (paddingRight) {
			int r = 0;
			// seek last non-zero cell
			for (int i = n - 1; i >= 0; i--) {
				if (data[i] != 0) {
					r = i;
					break;
				}
			}
			double avg = 0;
			int m = Math.min(windowLength / 2, r + 1);
			for (int i = 0; i < m; i++) {
				avg += data[r - i];
			}
			avg /= m;
			for (int i = r + 1; i < n; i++) {
				data[i] = avg;
			}
		}
	}

	/**
	 * 
	 * @return {@code windowLength}
	 */
	public int getWindowLength() {
		return windowLength;
	}

	/**
	 * 
	 * @return {@code paddingLeft}
	 */
	public boolean isPaddingLeft() {
		return paddingLeft;
	}

	/**
	 * 
	 * @return {@code paddingRight}
	 */
	public boolean isPaddingRight() {
		return paddingRight;
	}

	/**
	 * 
	 * @param paddingLeft
	 *            enables or disables left padding
	 */
	public void setPaddingLeft(boolean paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	/**
	 * 
	 * @param paddingRight
	 *            enables or disables right padding
	 */
	public void setPaddingRight(boolean paddingRight) {
		this.paddingRight = paddingRight;
	}

	/**
	 * 
	 * @param windowLength
	 * @throws IllegalArgumentException
	 *             if {@code windowLength} < 0
	 */
	public void setWindowLength(int windowLength) {
		if (windowLength < 0)
			throw new IllegalArgumentException("windowLength < 0");
		this.windowLength = windowLength;
	}

}
