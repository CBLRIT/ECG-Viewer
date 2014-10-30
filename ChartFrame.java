
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

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
				view.setBackground(view.isBad() ? 
										UIManager.getColor("Panel.background") : 
										new Color(233, 174, 174));
				thisFrame.revalidate();
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
				DetrendOptionDialog dialog = new DetrendOptionDialog(thisFrame, "Savitzky-Golay Filter", true, view);
				dialog.applyToDataset(view);
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SGOptionDialog dialog = new SGOptionDialog(thisFrame, "Savitzky-Golay Filter", true, view);
				dialog.applyToDataset(view);
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighOptionDialog dialog = new HighOptionDialog(thisFrame, "High Pass Filter", true, view);
				dialog.applyToDataset(view);
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LowOptionDialog dialog = new LowOptionDialog(thisFrame, "Low Pass Filter", true, view);
				dialog.applyToDataset(view);
			}
		});
		filter.add(filter_detrend);
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_low);
		
		menu.add(filter);

		JMenu annotations = new JMenu("Annotation");
		JMenuItem annotations_clear = new JMenuItem("Clear");
		annotations_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.clearAnnotations();
			}
		});
		JMenuItem annotations_trim = new JMenuItem("Trim");
		annotations_trim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setTrim(true);
			}
		});
		annotations.add(annotations_clear);
		annotations.add(annotations_trim);

		menu.add(annotations);

		setJMenuBar(menu);

		view = v;
		add(view.getPanel());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}

