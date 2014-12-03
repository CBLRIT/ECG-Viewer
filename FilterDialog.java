
import javax.swing.JDialog;
import javax.swing.JFrame;

public abstract class FilterDialog extends JDialog {
	protected final int id;

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

