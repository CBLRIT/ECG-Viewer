
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
				view.applyFilter(0);
				thisFrame.revalidate();
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

