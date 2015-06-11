
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import math.jwave.transforms.wavelets.*;

public class WaveletOptionDialog extends FilterDialog {
	private final WaveletOptionDialog thisDialog = this;
	private double freq;
	private int wavelet;
	private int level;
	private boolean retVal = false;

	public WaveletOptionDialog(final JFrame thisFrame, 
							   String title, 
							   boolean modal, 
							   final ECGViewHandler handler,
							   final int index) {
		
		super(thisFrame, title, modal, handler, index, 5);

		freq = 1.0;
		wavelet = 1;
		level = 5;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[]{handler.shallowFilter(index, 
																id, 
																new Number[]{1.0, 1, 5}, 
																true)};
		JPanel controls = new JPanel(new GridBagLayout());

		GridBagConstraints labels = new GridBagConstraints();
		labels.gridwidth = 6;
		labels.ipadx = 10;
		labels.anchor = GridBagConstraints.LINE_END;
		labels.gridx = 0;
		labels.gridy = 0;

		GridBagConstraints slider = new GridBagConstraints();
		slider.gridwidth = 6;
		slider.gridx = 7;
		slider.gridy = 0;

		this.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});

		final JComboBox<Wavelet> waveletChoice = new JComboBox<Wavelet>(WaveletBuilder.create2arr());
		final TextSlide levelSlide = new TextSlide(1, 15, 5, 0);

		controls.add(new JLabel("Frequency Threshold"), labels);
		final TextSlide leftSlide = new TextSlide(0, 1000, 1, 2);
		leftSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview[0].filter(id, new Number[]{(double)leftSlide.getValue(), waveletChoice.getSelectedIndex(), (int)(double)levelSlide.getValue()});
				preview[0].revalidate();
			}
		});
		controls.add(leftSlide, slider);

		labels.gridy = 1;
		controls.add(new JLabel("Wavelet"), labels);
		waveletChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				preview[0].filter(id, new Number[]{(double)leftSlide.getValue(), waveletChoice.getSelectedIndex(), (int)(double)levelSlide.getValue()});
				preview[0].revalidate();
			}
		});
		slider.gridy = 1;
		controls.add(waveletChoice, slider);
		
		labels.gridy = 2;
		controls.add(new JLabel("Decomposition Level"), labels);
		levelSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview[0].filter(id, new Number[]{(double)leftSlide.getValue(), waveletChoice.getSelectedIndex(), (int)(double)levelSlide.getValue()});
				preview[0].revalidate();
			}
		});
		slider.gridy = 2;
		controls.add(levelSlide, slider);

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				freq = (double)leftSlide.getValue();
				wavelet = waveletChoice.getSelectedIndex();
				level = (int)(double)levelSlide.getValue();
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
		labels.gridy = 3;
		labels.anchor = GridBagConstraints.CENTER;
		controls.add(cancel, labels);
		slider.gridy = 3;
		controls.add(accept, slider);

		this.add(controls, BorderLayout.NORTH);

		this.add(preview[0].getPanel(), BorderLayout.CENTER);

		this.setSize(650,650);
		this.setVisible(true);
	}

	public int Id() {
		return id;
	}

	public boolean accepted() {
		return retVal;
	}

	public Number[] returnVals() {
		return new Number[]{freq, wavelet, level};
	}
}

