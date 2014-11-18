
import javax.swing.JDialog;
import javax.swing.JFrame;

public abstract class FilterDialog extends JDialog {
	public final int id;

	public FilterDialog(final JFrame thisFrame, 
								 String title, 
								 boolean modal, 
								 final ECGViewHandler handler,
								 final int index) {
		super(thisFrame, title, modal);
	}

	public abstract boolean accepted();

	public abstract Number[] returnVals();
}

