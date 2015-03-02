
import javax.swing.JDialog;
import javax.swing.JFrame;

public abstract class FilterDialog extends JDialog {
	protected final int id;
	/*
	 * 0 - Savitsky-Golay
	 * 1 - High Pass
	 * 2 - Low Pass
	 * 3 - FFT
	 * 4 - Detrend
	 * 5 - Wavelet
	 * 6 - Constant Detrend
	 * 7 - Butterworth
	 */

	public FilterDialog(final JFrame thisFrame, 
								 String title, 
								 boolean modal, 
								 final ECGViewHandler handler,
								 final int index,
								 int idNum) {
		super(thisFrame, title, modal);
		id = idNum;
	}

	public abstract int Id();

	public abstract boolean accepted();

	public abstract Number[] returnVals();
}

