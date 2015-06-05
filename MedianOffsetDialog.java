
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * class MedianOffsetDialog - creates a dialog that displays the output of a median offset
 *
 * @author Dakota Williams
 */
public class MedianOffsetDialog extends FilterDialog {
	private final MedianOffsetDialog thisDialog = this;
	private int degree;
	private boolean retVal = false;

	/**
	 * Constructor - creates the dialog
	 *
	 * @param thisFrame the parent frame
	 * @param title the title of the dialog
	 * @param modal whether the dialog should be modal
	 * @param view the view to display in the dialog (preview)
	 */
	public MedianOffsetDialog(final JFrame thisFrame, 
							   String title, 
							   boolean modal, 
							   final ECGViewHandler handler,
							   final int index) {
		super(thisFrame, title, modal, handler, index, 9);

		degree = 6;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[]{handler.shallowFilter(index, 
																id, 
																new Number[]{6}, 
																true)};

		JPanel controls = new JPanel(new GridBagLayout());

		GridBagConstraints labels = new GridBagConstraints();
		labels.gridwidth = 6;
		labels.ipadx = 10;
		labels.anchor = GridBagConstraints.LINE_END;
		labels.gridx = 0;
		labels.gridy = 0;

		GridBagConstraints slider = new GridBagConstraints();
		slider.gridwidth = 5;
		slider.gridx = 6;
		slider.gridy = 0;

		this.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				thisFrame.revalidate();
				thisDialog.dispose();
				retVal = true;
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisDialog.dispose();
			}
		});
		labels.gridy = 1;
		labels.anchor = GridBagConstraints.CENTER;
		controls.add(cancel, labels);
		slider.gridy = 1;
		controls.add(accept, slider);

		this.add(controls, BorderLayout.NORTH);

		this.add(preview[0].getPanel(), BorderLayout.CENTER);

		this.setSize(650,650);
		this.setVisible(true);
	}

	public int Id() {
		return id;
	}

	/**
	 * accepted - whether the user clicked ok
	 *
	 * @return true if ok
	 */
	public boolean accepted() {
		return retVal;
	}

	/**
	 * returnVals - returns the data gathered from the dialog
	 *
	 * @return an array of the data gathered from the dialog
	 */
	public Number[] returnVals() {
		return new Number[]{};
	}
}
