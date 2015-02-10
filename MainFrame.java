
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import org.jfree.chart.plot.XYPlot;

public class MainFrame extends JFrame {
	private final int dataSetPlacement[][] =  {
		{-1, -1}, // // first two are junk
		{-1, -1}, // //
		{-1, -1}, // 1-3 unhelpful (limb leads)
		{-1, -1}, //
		{-1, -1}, //
		{4, 0},		{5, 0}, 	{6, 0}, 	{7, 0}, 								   	//4-7
		{1, 1}, 	{2, 1}, 	{3, 1}, 	{4, 1}, 	{5, 1}, 	{6, 1}, 	{7, 1},	//8-14
		{1, 2}, 	{2, 2}, 	{3, 2}, 	{4, 2},		{5, 2},		{6, 2}, 	{7, 2},	//15-21
		{1, 3},		{2, 3},		{3, 3},		{4, 3},		{5, 3},		{6, 3},		{7, 3},	//22-28
		{1, 4},		{2, 4},		{3, 4},		{4, 4},		{5, 4},		{6, 4},		{7, 4},	//29-35
		{1, 5},		{2, 5},		{3, 5},		{4, 5},		{5, 5},		{6, 5},		{7, 5},	//36-42
		{1, 6},		{2, 6},		{3, 6},		{4, 6},		{5, 6},		{6, 6},		{7, 6},	//43-49
		{1, 7},		{2, 7},		{3, 7},		{4, 7},		{5, 7},		{6, 7},		{7, 7},	//50-56
		{1, 8},		{2, 8},		{3, 8},		{4, 8},		{5, 8},		{6, 8},		{7, 8},	//57-63
		{1, 9},		{2, 9},		{3, 9},		{4, 9},		{5, 9},		{6, 9},		{7, 9},	//64-70
		{4, 10},	{5, 10}, 	{6, 10}, 	{7, 10}, 								   	//71-74
		{4, 11},	{5, 11}, 	{6, 11}, 	{7, 11}, 									//75-78
		{1, 12},	{2, 12},	{3, 12},	{4, 12},	{5, 12},	{6, 12},	{7, 12},//79-85
		{1, 13},	{2, 13},	{3, 13},	{4, 13},	{5, 13},	{6, 13},	{7, 13},//86-92
		{0, 14},	{1, 14},	{2, 14},	{3, 14},	{4, 14},	{5, 14},	{6, 14},//93-
					{7, 14},															//  -100
		{0, 15},	{1, 15},	{2, 15},	{3, 15},	{4, 15},	{5, 15},	{6, 15},//101-
					{7, 15},															//	 -108
		{0, 16},	{1, 16},	{2, 16},	{3, 16},	{4, 16},	{5, 16},	{6, 16},//109-
					{7, 16},															//	 -116
		{1, 17},	{2, 17},	{3, 17},	{4, 17},	{5, 17},	{6, 17},	{7, 17},//117-123
	};

	private int ynum = 8;
	private int xnum = 18;

	private final ECGViewHandler views;
	private final JPanel[] subPanels = new JPanel[xnum*ynum];
	private final ArrayList<ECGView> graphs = new ArrayList<ECGView>();

	private JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());

	private MainFrame thisFrame = this;

	public MainFrame(final ECGViewHandler views) {
		super("ECG Viewer");

		this.views = views;

		this.setBounds(20, 20, 1400, 750);
		this.setLayout(new BorderLayout());

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFile(fc.getSelectedFile().getAbsolutePath());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Could not load file", 
													  "Error",
													  JOptionPane.ERROR_MESSAGE);
						return;
					}

					graphs.clear();
					for(int i = 0; i < views.size(); i++) {
						int index = dataSetPlacement[i+5][0]*xnum + dataSetPlacement[i+5][1];
						final ECGView graph = views.getView(i, false);
						subPanels[index].removeAll();

						final int count = i;
						graph.getPanel().addMouseListener(new MouseListener() {
							public void mouseClicked(MouseEvent e) {
								ChartFrame cf = new ChartFrame(views, count, ""+(count+4));
								cf.addWindowListener(new WindowListener() {
									public void windowClosing(WindowEvent e) {
										graph.setBackground(!graph.isBad() ? 
														UIManager.getColor("Panel.background") : 
														new Color(233, 174, 174));
										graph.revalidate();
									}

									//don't care
									public void windowOpened(WindowEvent e) {}
									public void windowIconified(WindowEvent e) {}
									public void windowDeiconified(WindowEvent e) {}
									public void windowDeactivated(WindowEvent e) {}
									public void windowActivated(WindowEvent e) {}
									public void windowClosed(WindowEvent e) {}
								});
							}

							//don't care
							public void mouseEntered(MouseEvent e) {}
							public void mouseExited(MouseEvent e) {}
							public void mousePressed(MouseEvent e) {}
							public void mouseReleased(MouseEvent e) {}
						});

						graphs.add(graph);
						subPanels[index].add(graph.getPanel());
					}
		
					thisFrame.revalidate();
				}
			}
		});
		JMenuItem export = new JMenuItem("Export...");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter matlab = new FileNameExtensionFilter(
					"MATLAB matrix", "m");
				FileNameExtensionFilter csv = new FileNameExtensionFilter(
						"Comma Separated Values", "csv");
				fc.addChoosableFileFilter(matlab);
				fc.addChoosableFileFilter(csv);
				fc.setAcceptAllFileFilterUsed(false);
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try { 
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							views.writeDataMat(fc.getSelectedFile().getAbsolutePath());
						} else if ("Comma Separated Values".equals(extension)) {
							views.writeDataCSV(fc.getSelectedFile().getAbsolutePath());
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Error writing file: " + ex.getMessage(), 
													  "IOException", 
													  JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		JMenuItem export_subset = new JMenuItem("Export Subset...");
		export_subset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter matlab = new FileNameExtensionFilter(
					"MATLAB matrix", "m");
				FileNameExtensionFilter csv = new FileNameExtensionFilter(
					"Comma Separated Values", "csv");
				fc.addChoosableFileFilter(csv);
				fc.addChoosableFileFilter(matlab);
				fc.setAcceptAllFileFilterUsed(false);
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try { 
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							views.writeDataSubsetMat(fc.getSelectedFile().getAbsolutePath(), 
													 (Long)startText.getValue(),
													 (Long)startText.getValue()
													 		+(Long)lenText.getValue());
						} else if ("Comma Separated Values".equals(extension)) {
							views.writeDataSubsetCSV(fc.getSelectedFile().getAbsolutePath(),
													 (Long)startText.getValue(),
													 (Long)startText.getValue()
													 		+(Long)lenText.getValue());
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Error writing file: " + ex.getMessage(), 
													  "IOException", 
													  JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		JMenuItem export_badleads = new JMenuItem("Export Bad Leads...");
		export_badleads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.writeBadLeads(fc.getSelectedFile().getAbsolutePath());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Error writing file: " + ex.getMessage(), 
													  "IOException", 
													  JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		JMenuItem export_annos = new JMenuItem("Export Annotations...");
		export_annos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.writeAnnotations(fc.getSelectedFile().getAbsolutePath());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Error writing file: " + ex.getMessage(), 
													  "IOException", 
													  JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
				thisFrame.dispose();
			}
		});
		menu.add(open);
		menu.add(export);
		menu.add(export_subset);
		menu.add(export_annos);
		menu.add(export_badleads);
		menu.add(exit);
		menubar.add(menu);

		JMenu edit = new JMenu("Edit");
		final JMenuItem edit_undo = new JMenuItem("Undo");
		edit_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.undo();
			}
		});
		final JMenuItem edit_redo = new JMenuItem("Redo");
		edit_redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.redo();
			}
		});
		edit.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				edit_undo.setEnabled(views.canUndo());
				edit_redo.setEnabled(views.canRedo());
			}

			//don't care
			public void menuDeselected(MenuEvent e) {}
			public void menuCanceled(MenuEvent e) {}
		});
		edit.add(edit_undo);
		edit.add(edit_redo);
		menubar.add(edit);

		JMenu filter = new JMenu("Filter All");
		JMenuItem filter_detrend = new JMenuItem("Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetrendOptionDialog dialog = new DetrendOptionDialog(thisFrame, "Detrend", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}

				views.applyAllChanges();
				
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_constant = new JMenuItem("Constant Offset");
		filter_constant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConstantOptionDialog dialog = new ConstantOptionDialog(thisFrame, "Constant Offset", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}

				views.applyAllChanges();

				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SGOptionDialog dialog = new SGOptionDialog(thisFrame, "Savitzky-Golay Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}
				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}
				views.applyAllChanges();
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighOptionDialog dialog = new HighOptionDialog(thisFrame, "High Pass Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}
				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}
				views.applyAllChanges();
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_highfft = new JMenuItem("FFT");
		filter_highfft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FFTOptionDialog dialog = new FFTOptionDialog(thisFrame, "FFT Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}
				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}
				views.applyAllChanges();
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LowOptionDialog dialog = new LowOptionDialog(thisFrame, "Low Pass Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}
				views.applyAllChanges();
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		JMenuItem filter_wave = new JMenuItem("Wavelet");
		filter_wave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WaveletOptionDialog dialog = new WaveletOptionDialog(thisFrame, "Wavelet Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				for(int i = 0; i < views.size(); i++) {
					views.applyFilter(dialog, i);
				}
				views.applyAllChanges();
				for(int i = 0; i < graphs.size(); i++) {
					graphs.get(i).revalidate();
				}
			}
		});
		filter.add(filter_detrend);
		filter.add(filter_constant);
		filter.addSeparator();
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_highfft);
		filter.add(filter_low);
		filter.add(filter_wave);
		menubar.add(filter);

		this.setJMenuBar(menubar);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(ynum, xnum));

		for(int i = 0; i < xnum*ynum; i++) {
			subPanels[i] = new JPanel();
			mainPanel.add(subPanels[i]);
		}

		JScrollPane scrollMain = new JScrollPane(mainPanel);
		this.add(scrollMain, BorderLayout.CENTER);

		JPanel statusBar = new JPanel();
		JLabel startLabel = new JLabel("Start offset (ms):");
		startText.setValue(0L);
		startText.setColumns(10);
		startText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long start = (Long)startText.getValue();
				long len = (Long)lenText.getValue();
				for(int i = 0; i < views.size(); i++) {
					((XYPlot)graphs.get(i).getPanel().getChart().getPlot()).getDomainAxis()
																 .setAutoRange(true);
					((XYPlot)graphs.get(i).getPanel().getChart().getPlot()).getDomainAxis()
																 .setRange(start, start+len);
				}
			}
		});
		JLabel lenLabel = new JLabel("Length (ms):");
		lenText.setValue(0L);
		lenText.setColumns(10);
		lenText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long start = (Long)startText.getValue(); long len = (Long)lenText.getValue();
				for(int i = 0; i < graphs.size(); i++) {
					((XYPlot)graphs.get(i).getPanel().getChart().getPlot()).getDomainAxis()
																 .setAutoRange(true);
					((XYPlot)graphs.get(i).getPanel().getChart().getPlot()).getDomainAxis()
																 .setRange(start, start+len);
				}
			}
		});
		statusBar.add(startLabel);
		statusBar.add(startText);
		statusBar.add(lenLabel);
		statusBar.add(lenText);

		this.add(statusBar, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

}
