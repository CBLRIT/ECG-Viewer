
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
 * class DetrendOptionDialog - creates a dialog that provides options for
 *							   detrending data
 *
 * @author Dakota Williams
 */
public class DetrendOptionDialog extends JDialog {
	private final DetrendOptionDialog thisDialog = this;
	private int degree;

	/**
	 * Constructor - creates the dialog
	 *
	 * @param thisFrame the parent frame
	 * @param title the title of the dialog
	 * @param modal whether the dialog should be modal
	 * @param view the view to display in the dialog (preview)
	 */
	public DetrendOptionDialog(final JFrame thisFrame, 
							   String title, 
							   boolean modal, 
							   final ECGView view) {
		super(thisFrame, title, modal);

		degree = 6;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[1];
		preview[0] = (ECGView)view.deepClone(true);

		JPanel controls = new JPanel(new GridBagLayout());

		GridBagConstraints labels = new GridBagConstraints();
		labels.gridwidth = 6;
		labels.ipadx = 10;
		labels.anchor = GridBagConstraints.LINE_END;
		labels.gridx = 0;
		labels.gridy = 0;

		GridBagConstraints values = new GridBagConstraints();
		values.gridx = 6;
		values.gridy = 0;

		GridBagConstraints slider = new GridBagConstraints();
		slider.gridwidth = 5;
		slider.gridx = 7;
		slider.gridy = 0;

		this.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});

		final JSlider degreeSlide = new JSlider(0, 10, 6);
		controls.add(new JLabel("Degree of Fitting Polynomial"), labels);
		final JLabel degreeNum = new JLabel("6");
		controls.add(degreeNum, values);
		degreeSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				degreeNum.setText("" + degreeSlide.getValue());
				thisDialog.remove(preview[0].getPanel());
				preview[0] = (ECGView)view.deepClone(true);
				preview[0].detrend(degreeSlide.getValue());
				thisDialog.add(preview[0].getPanel());
			}
		});
		controls.add(degreeSlide, slider);

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				degree = degreeSlide.getValue();
				thisFrame.revalidate();
				thisDialog.dispose();
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

		preview[0].detrend(degreeSlide.getValue());
		this.add(preview[0].getPanel(), BorderLayout.CENTER);

		this.setVisible(true);
	}

	/**
	 * applyToDataset - applies the data gathered from the dialog
	 *					to a view
	 * @param view the data to apply the detrend to
	 */
	public void applyToDataset(ECGDataSet view) {
		view.detrend(degree);
	}
}

