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
 * Eliminates zeros from data - starting from the first non-zero element, ending
 * at the last non-zero element. More specifically:
 * <p>
 * <ul>
 * <li>
 * Let <tt>l</tt> be the index of the first non-zero element in data,</li>
 * <li>let <tt>r</tt> be the index of the last non-zero element in data</li>
 * </ul>
 * then for every element <tt>e</tt> which index is <tt>i</tt> such that:
 * <tt>l < i < r</tt> and <tt>e == 0</tt>, <tt>e</tt> is replaced with element <tt>e'</tt>
 * with index <tt>j</tt> such that:
 * <ul>
 * <li><tt>l <= j < i</tt> and <tt>e' <> 0</tt> and for all indexes
 * <tt>k: j < k < i; e[k] == 0</tt> - when {@link #isAlignToLeft() alignToLeft}
 * is true</li>
 * <li><tt>i < j <= r</tt> and <tt>e' <> 0</tt> and for all indexes
 * <tt>k: i < k < j;e[k] == 0</tt> - otherwise</li>
 * </ul>
 * </p>
 * Example:
 * <p>
 * Given data: <tt>[0,0,0,1,2,0,3,0,0,4,0]</tt> result of applying
 * ZeroEliminator is: <tt>[0,0,0,1,2,2,3,3,3,4,0]</tt> if
 * {@link #isAlignToLeft() alignToLeft} is true;
 * <tt>[0,0,0,1,2,3,3,4,4,4,0]</tt> - otherwise
 * </p>
 * 
 * @author Marcin Rzeźnicki
 * 
 */
public class ZeroEliminator implements Preprocessor {

	private boolean alignToLeft;

	/**
	 * Default constructor: {@code alignToLeft} is {@code false}
	 * 
	 * @see #ZeroEliminator(boolean)
	 */
	public ZeroEliminator() {

	}

	/**
	 * 
	 * @param alignToLeft
	 *            if {@code true} zeros will be replaced with non-zero element
	 *            to the left, if {@code false} - to the right
	 */
	public ZeroEliminator(boolean alignToLeft) {
		this.alignToLeft = alignToLeft;
	}

	@Override
	public void apply(double[] data) {
		int n = data.length;
		int l = 0, r = 0;
		// seek first non-zero cell
		for (int i = 0; i < n; i++) {
			if (data[i] != 0) {
				l = i;
				break;
			}
		}
		// seek last non-zero cell
		for (int i = n - 1; i >= 0; i--) {
			if (data[i] != 0) {
				r = i;
				break;
			}
		}
		// eliminate 0s
		if (alignToLeft)
			for (int i = l + 1; i < r; i++) {
				if (data[i] == 0) {
					data[i] = data[i - 1];
				}
			}
		else
			for (int i = r - 1; i > l; i--) {
				if (data[i] == 0) {
					data[i] = data[i + 1];
				}
			}
	}

	/**
	 * 
	 * @return {@code alignToLeft}
	 */
	public boolean isAlignToLeft() {
		return alignToLeft;
	}

	/**
	 * 
	 * @param alignToLeft
	 *            if {@code true} zeros will be replaced with non-zero element
	 *            to the left, if {@code false} - to the right
	 */
	public void setAlignToLeft(boolean alignToLeft) {
		this.alignToLeft = alignToLeft;
	}

}
