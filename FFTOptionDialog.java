
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

public class FFTOptionDialog extends JDialog {
	private final FFTOptionDialog thisDialog = this;
	private double freq;

	public FFTOptionDialog(final JFrame thisFrame, String title, boolean modal, final ECGView view) {
		super(thisFrame, title, modal);

		freq = 60;

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

		controls.add(new JLabel("Frequency Threshold"), labels);
		final JLabel leftNum = new JLabel("60.0");
		controls.add(leftNum, values);
		final JSlider leftSlide = new JSlider(2000, 13000, 6000);
		leftSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				leftNum.setText("" + ((double)(int)leftSlide.getValue())/100.0);
				thisDialog.remove(preview[0].getPanel());
				preview[0] = (ECGView)view.deepClone(true);
				preview[0].applyFilter(3, ((double)(int)leftSlide.getValue())/100.0);
				thisDialog.add(preview[0].getPanel());
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

		preview[0].applyFilter(3, ((double)(int)leftSlide.getValue())/100.0);
		
		this.add(preview[0].getPanel(), BorderLayout.CENTER);

		this.setVisible(true);
	}

	public void applyToDataset(ECGDataSet view) {
		view.highpassfftfilt(freq, 0);
	}
}

