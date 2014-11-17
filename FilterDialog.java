
import javax.swing.JDialog;
import javax.swing.JFrame;

public abstract class FilterDialog extends JDialog {
	public final int id;

	public abstract FilterDialog(final JFrame thisFrame, 
								 String title, 
								 boolean modal, 
								 final ECGView view);

	public abstract boolean accepted();

	public abstract Number[] returnVals();
}

