
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class ChartFrame extends JFrame {
	private final ECGView view;
	private final JCheckBoxMenuItem file_badlead;
	private final ChartFrame thisFrame = this;

	public ChartFrame(ECGView v, String title) {
		super(title);
		setBounds(0, 0, 500, 500);

		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("Dataset");
		file_badlead = new JCheckBoxMenuItem("Bad Lead");
		file_badlead.setState(v.isBad());
		file_badlead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setBad(!view.isBad());
				file_badlead.setState(view.isBad());
			}
		});
		file.add(file_badlead);
		JMenuItem file_exit = new JMenuItem("Exit");
		file_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
				thisFrame.dispose();
			}
		});
		file.add(file_exit);
		menu.add(file);

		JMenu filter = new JMenu("Filter");
		JMenuItem filter_detrend = new JMenuItem("Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.detrend();
				thisFrame.revalidate();
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog(thisFrame, "Savitzky-Golay Filter", true);
				dialog.setLayout(new GridLayout(4,3));
				dialog.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						e.getWindow().dispose();
					}
				});

				dialog.add(new JLabel("Left Elements to Sample"));
				final JLabel leftNum = new JLabel("25");
				dialog.add(leftNum);
				final JSlider leftSlide = new JSlider(0, 100, 25);
				leftSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						leftNum.setText("" + leftSlide.getValue());
					}
				});
				dialog.add(leftSlide);

				dialog.add(new JLabel("Right Elements to Sample"));
				final JLabel rightNum = new JLabel("25");
				dialog.add(rightNum);
				final JSlider rightSlide = new JSlider(0, 100, 25);
				rightSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						rightNum.setText("" + rightSlide.getValue());
					}
				});
				dialog.add(rightSlide);

				dialog.add(new JLabel("Degree of Fitting Polynomial"));
				final JLabel degreeNum = new JLabel("6");
				dialog.add(degreeNum);
				final JSlider degreeSlide = new JSlider(0, 10, 6);
				degreeSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						degreeNum.setText("" + degreeSlide.getValue());
					}
				});
				dialog.add(degreeSlide);

				final JButton accept = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				accept.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						accept.setEnabled(false);
						cancel.setEnabled(false);
						view.applyFilter(0, 
										 leftSlide.getValue(),
										 rightSlide.getValue(), 
										 degreeSlide.getValue());
						thisFrame.revalidate();
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				dialog.add(cancel);
				dialog.add(new JLabel()); //filler
				dialog.add(accept);

				dialog.setVisible(true);
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.applyFilter(1);
				thisFrame.revalidate();
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.applyFilter(2);
				thisFrame.revalidate();
			}
		});
		filter.add(filter_detrend);
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_low);
		
		menu.add(filter);

		setJMenuBar(menu);

		view = v;
		add(view.getPanel());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}

