
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

public class HighOptionDialog extends FilterDialog {
	private final HighOptionDialog thisDialog = this;
	private double freq;
	private boolean retVal = false;

	public HighOptionDialog(final JFrame thisFrame, 
							String title, 
							boolean modal, 
							final ECGViewHandler handler,
							final int index) {
		super(thisFrame, title, modal, handler, index, 1);

		freq = 0.25;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[]{handler.shallowFilter(index, 
																id, 
																new Number[]{0.25}, 
																true)};

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
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});

		controls.add(new JLabel("Frequency Threshold"), labels);
		final JLabel leftNum = new JLabel(".25");
		controls.add(leftNum, values);
		final JSlider leftSlide = new JSlider(1, 400, 25);
		leftSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				leftNum.setText("" + ((double)(int)leftSlide.getValue())/100.0);
				preview[0].filter(id, new Number[]{((double)(int)leftSlide.getValue())/100.0});
				preview[0].revalidate();
			}
		});
		controls.add(leftSlide, slider);

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				freq = ((double)(int)leftSlide.getValue())/100.0;
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

		this.setVisible(true);
	}

	public int Id() {
		return id;
	}

	public boolean accepted() {
		return retVal;
	}

	public Number[] returnVals() {
		return new Number[]{freq};
	}
}

