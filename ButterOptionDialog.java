
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

public class ButterOptionDialog extends FilterDialog {
	private final ButterOptionDialog thisDialog = this;
	private double freq;
	private int order;
	private int mode;
	private boolean retVal = false;

	public ButterOptionDialog(final JFrame thisFrame, 
						   String title, 
						   boolean modal, 
						   final ECGViewHandler handler,
						   final int index) {
		super(thisFrame, title, modal, handler, index, 7);

		mode = 0;
		freq = Math.log(.125);
		order = 2;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[]{handler.shallowFilter(index, 
																id, 
																new Number[]{0, Math.log(.125), 2}, 
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
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});

		controls.add(new JLabel("Frequency Threshold"), labels);
		final JLabel leftNum = new JLabel("" + Math.log(0.125));
		controls.add(leftNum, values);
		final JSlider leftSlide = new JSlider(00001, 2500, 1250);
		final JSlider orderSlide = new JSlider(1, 10, 2);
		leftSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				leftNum.setText("" + (double)(int)leftSlide.getValue()/10000.0);
				preview[0].filter(id, new Number[]{0,
												   ((double)(int)leftSlide.getValue())/10000.0,
												   (int)orderSlide.getValue()*2});
				preview[0].revalidate();
			}
		});
		controls.add(leftSlide, slider);

		labels.gridy = 1;
		controls.add(new JLabel("Filter Order"), labels);
		final JLabel orderNum = new JLabel("4");
		values.gridy = 1;
		controls.add(orderNum, values);
		orderSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				orderNum.setText("" + ((int)orderSlide.getValue()*2));
				preview[0].filter(id, new Number[]{0,
												   ((double)(int)leftSlide.getValue())/10000.0,
												   (int)orderSlide.getValue()*2});
				preview[0].revalidate();
			}
		});
		slider.gridy = 1;
		controls.add(orderSlide, slider);

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				freq = ((double)(int)leftSlide.getValue())/10000.0;
				order = (int)orderSlide.getValue()*2;
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
		labels.gridy = 2;
		labels.anchor = GridBagConstraints.CENTER;
		controls.add(cancel, labels);
		slider.gridy = 2;
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
		return new Number[]{mode, freq, order};
	}
}

