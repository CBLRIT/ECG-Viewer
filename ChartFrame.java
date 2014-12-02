
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.jfree.chart.plot.XYPlot;

/**
 * class ChartFrame - creates a simple frame for displaying one chart in detail
 *
 * @author Dakota Williams
 */
public class ChartFrame extends JFrame {
	private ECGViewHandler handler;
	private final ECGView view;
	private int index;
	private final JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private final JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private final JCheckBoxMenuItem file_badlead;
	private final ChartFrame thisFrame = this;
	private final JCheckBoxMenuItem anno_enable;

	/**
	 * Constructor - creates the frame and everything in it
	 *
	 * @param v the view to display
	 * @param title the title of the frame
	 */
	public ChartFrame(final ECGViewHandler handler, int index, String title) {
		super(title);
		setBounds(0, 0, 500, 500);
		setLayout(new BorderLayout());

		this.handler = handler;
		this.index = index;
		view = handler.getView(index, true);

		//start with the menu bar
		JMenuBar menu = new JMenuBar();

		//dataset menu
		JMenu file = new JMenu("Dataset");
		JMenuItem file_apply = new JMenuItem("Apply Changes");
		file_apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.handler.applyChanges(thisFrame.index);
			}
		});
		file.add(file_apply);
		JMenuItem file_reset = new JMenuItem("Reset Changes");
		file_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.handler.resetChanges(thisFrame.index);
			}
		});
		file.add(file_reset);
		file_badlead = new JCheckBoxMenuItem("Bad Lead");
		file_badlead.setState(view.isBad());
		file_badlead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setBackground(thisFrame.view.isBad() ? 
										UIManager.getColor("Panel.background") : 
										new Color(233, 174, 174));
				thisFrame.revalidate();
				thisFrame.view.setBad(!view.isBad());
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

		//filter menu
		JMenu filter = new JMenu("Filter");
		JMenuItem filter_detrend = new JMenuItem("Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetrendOptionDialog dialog = new DetrendOptionDialog(thisFrame, 
																	 "Detrend", 
																	 true, 
																	 thisFrame.handler,
																	 thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SGOptionDialog dialog = new SGOptionDialog(thisFrame, 
														   "Savitzky-Golay Filter", 
														   true, 
														   thisFrame.handler,
														   thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighOptionDialog dialog = new HighOptionDialog(thisFrame, 
															   "High Pass Filter", 
															   true, 
															   thisFrame.handler,
															   thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_ffthigh = new JMenuItem("FFT High Pass");
		filter_ffthigh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FFTOptionDialog dialog = new FFTOptionDialog(thisFrame, 
															 "FFT High Pass Filter", 
															 true, 
															 thisFrame.handler,
															 thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LowOptionDialog dialog = new LowOptionDialog(thisFrame, 
															 "Low Pass Filter", 
															 true, 
															 thisFrame.handler,
															 thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		filter.add(filter_detrend);
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_ffthigh);
		filter.add(filter_low);
		
		menu.add(filter);

		//annotation menu
		JMenu annotations = new JMenu("Annotation");
		anno_enable = new JCheckBoxMenuItem("Place Annotations");
		anno_enable.setState(false);
		anno_enable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanPlace(anno_enable.getState());
			}
		});
		JMenuItem annotations_clear = new JMenuItem("Clear");
		annotations_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.clearAnnotations();
			}
		});
		annotations.add(anno_enable);
		annotations.add(annotations_clear);
		annotations.addSeparator();
		ButtonGroup annoGroup = new ButtonGroup();
		JRadioButtonMenuItem[] annotations_colors = new JRadioButtonMenuItem[4];
		String[] desc = {"T-wave", "R-wave", "QRS-complex", "P-wave"};
		for(int i = 0; i < annotations_colors.length; i++) {
			final int count = i;
			annotations_colors[i] = new JRadioButtonMenuItem("Annotation " + (i+1) 
														+ " (" + desc[i] + ")", 
														handler.getSelectedAnnotationType()==i);
			annotations_colors[i].addActionListener(new ActionListener() {
				private final int changeNum = count;

				public void actionPerformed(ActionEvent e) {
					handler.setSelectedAnnotationType(changeNum);
				}
			});
			annotations.add(annotations_colors[i]);
			annoGroup.add(annotations_colors[i]);
		}

		menu.add(annotations);

		setJMenuBar(menu);

		//add the specified view to the frame
		add(thisFrame.view.getPanel(), BorderLayout.CENTER);

		JPanel statusBar = new JPanel();
		JLabel startLabel = new JLabel("Start offset (ms):");
		startText.setValue(0L);
		startText.setColumns(10);
		startText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long start = (Long)startText.getValue();
				long len = (Long)lenText.getValue();
				thisFrame.view.setViewingDomain(start, start+len);
			}
		});
		JLabel lenLabel = new JLabel("Length (ms):");
		lenText.setValue(0L);
		lenText.setColumns(10);
		lenText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long start = (Long)startText.getValue();
				long len = (Long)lenText.getValue();
				thisFrame.view.setViewingDomain(start, start+len);
			}
		});
		statusBar.add(startLabel);
		statusBar.add(startText);
		statusBar.add(lenLabel);
		statusBar.add(lenText);
		
		lenText.setValue(thisFrame.handler.leadSize(index)*thisFrame.handler.getSampleInterval());

		add(statusBar, BorderLayout.SOUTH);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
}

