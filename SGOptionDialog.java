
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

public class SGOptionDialog extends FilterDialog {
	private final SGOptionDialog thisDialog = this;
	private int left;
	private int right;
	private int degree;
	private boolean retVal = false;

	public SGOptionDialog(final JFrame thisFrame, 
						  String title, 
						  boolean modal, 
						  final ECGViewHandler handler,
						  final int index) {
		super(thisFrame, title, modal, handler, index, 0);

		left = 25;
		right = 25;
		degree = 6;

		this.setLayout(new BorderLayout());

		final ECGView[] preview = new ECGView[]{handler.shallowFilter(index, 
																id, 
																new Number[]{25, 25, 6}, 
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

		controls.add(new JLabel("Left Elements to Sample"), labels);
		final TextSlide leftSlide = new TextSlide(1, 100, 25, 0);
		final TextSlide rightSlide = new TextSlide(1, 100, 25, 0);
		final TextSlide degreeSlide = new TextSlide(0, 10, 6, 0);
		leftSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview[0].filter(id, new Number[]{leftSlide.getValue().intValue(),
												   rightSlide.getValue().intValue(),
												   degreeSlide.getValue().intValue()});
				preview[0].revalidate();
			}
		});
		controls.add(leftSlide, slider);

		labels.gridy = 1;
		controls.add(new JLabel("Right Elements to Sample"), labels);
		rightSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview[0].filter(id, new Number[]{leftSlide.getValue().intValue(),
												   rightSlide.getValue().intValue(),
												   degreeSlide.getValue().intValue()});
				preview[0].revalidate();
			}
		});
		slider.gridy = 1;
		controls.add(rightSlide, slider);

		labels.gridy = 2;
		controls.add(new JLabel("Degree of Fitting Polynomial"), labels);
		degreeSlide.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview[0].filter(id, new Number[]{leftSlide.getValue().intValue(),
												   rightSlide.getValue().intValue(),
												   degreeSlide.getValue().intValue()});
				preview[0].revalidate();
			}
		});
		slider.gridy = 2;
		controls.add(degreeSlide, slider);

		final JButton accept = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept.setEnabled(false);
				cancel.setEnabled(false);
				left = leftSlide.getValue().intValue();
				right = rightSlide.getValue().intValue(); 
				degree = degreeSlide.getValue().intValue();
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
		return new Number[]{left, right, degree};
	}
}
