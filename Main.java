
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

public class Main {
	private static final int dataSetPlacement[][] =  {
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

	private static int ynum = 8;
	private static int xnum = 18;

	private static final JFrame main = new JFrame("ECG Viewer");
	private static final JPanel[] subPanels = new JPanel[xnum*ynum];
	private static final ArrayList<ECGView> graphs = new ArrayList<ECGView>();
	private static final ECGModel model = new ECGModel();

	private static int currAnnoType = 0;
	private static HashMap<Integer, Color> annoColors = new HashMap<Integer, Color>();

	private static JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private static JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());

	private static void loadFile(String filename) {
		graphs.clear();
		model.clear();

		try {
			model.readData(filename);
		} catch (IOException e) {
			System.err.println("Could not open: " + filename);
			return;
		}
		
		for(int i = 5; i < 125; i++) {
		//	System.out.print(i + ": ");
			final ECGView graph = new ECGView(model.getDataset(i), ""+(i-1), false);
			graphs.add(graph);
			int index = dataSetPlacement[i][0]*xnum + dataSetPlacement[i][1];
			graph.getPanel().setPopupMenu(null); //turn off context menu for chart
			subPanels[index].removeAll();
			subPanels[index].add(graph.getPanel());
			final int count = i-1;
			graph.getPanel().addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					ChartFrame cf = new ChartFrame((ECGView)graph.clone(true), ""+count);
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
		}
		
		lenText.setValue(Math.ceil((double)model.getDataset(42).size()
							*model.getSamplesPerSecond()));
	}

	public static int getSelectedAnnotationType() {
		return currAnnoType;
	}

	public static void setSelectedAnnotationType(int type) {
		currAnnoType = type;
	}

	public static Color getAnnotationColor(int type) {
		return annoColors.get(type);
	}

	public static Color getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType);
	}

	public static void main(String args[])
			throws Exception { //TODO: fix this
		annoColors.put(0, Color.BLACK);
		annoColors.put(1, Color.ORANGE);
		annoColors.put(2, Color.GREEN);
		annoColors.put(3, Color.BLUE);

		main.setBounds(20, 20, 1400, 750);
		main.setLayout(new BorderLayout());

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(main);
				if(ret == JFileChooser.APPROVE_OPTION) {
					loadFile(fc.getSelectedFile().getAbsolutePath());
					main.revalidate();
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
				int ret = fc.showSaveDialog(main);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try { 
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							model.writeDataMat(fc.getSelectedFile().getAbsolutePath());
						} else if ("Comma Separated Values".equals(extension)) {
							model.writeDataCSV(fc.getSelectedFile().getAbsolutePath());
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
				int ret = fc.showSaveDialog(main);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						model.writeBadLeads(fc.getSelectedFile().getAbsolutePath());
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
				int ret = fc.showSaveDialog(main);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						model.writeAnnotations(fc.getSelectedFile().getAbsolutePath());
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
				main.setVisible(false);
				main.dispose();
			}
		});
		menu.add(open);
		menu.add(export);
		menu.add(export_annos);
		menu.add(export_badleads);
		menu.add(exit);
		menubar.add(menu);

		JMenu filter = new JMenu("Filter All");
		JMenuItem filter_detrend = new JMenuItem("Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ECGView view = graphs.get(4);
				DetrendOptionDialog dialog = new DetrendOptionDialog(main, "Detrend", true, view);

				for(int i = 0; i < model.size(); i++) {
					dialog.applyToDataset(model.getDataset(i));
					if(i >= 4 && i < 124) {
						graphs.get(i-4).revalidate();
					}
				}
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ECGView view = graphs.get(4);
				SGOptionDialog dialog = new SGOptionDialog(main, "Savitzky-Golay Filter", true, view);
				for(int i = 0; i < model.size(); i++) {
					dialog.applyToDataset(model.getDataset(i));
					if(i >= 4 && i < 124) {
						graphs.get(i-4).revalidate();
					}
				}
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ECGView view = graphs.get(4);
				HighOptionDialog dialog = new HighOptionDialog(main, "High Pass Filter", true, view);
				for(int i = 0; i < model.size(); i++) {
					dialog.applyToDataset(model.getDataset(i));
					if(i >= 4 && i < 124) {
						graphs.get(i-4).revalidate();
					}
				}
			}
		});
		JMenuItem filter_highfft = new JMenuItem("FFT");
		filter_highfft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ECGView view = graphs.get(4);
				FFTOptionDialog dialog = new FFTOptionDialog(main, "FFT Filter", true, view);
				for(int i = 0; i < model.size(); i++) {
					dialog.applyToDataset(model.getDataset(i));
					if(i >= 4 && i < 124) {
						graphs.get(i-4).revalidate();
					}
				}
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ECGView view = graphs.get(4);
				LowOptionDialog dialog = new LowOptionDialog(main, "Low Pass Filter", true, view);

				for(int i = 0; i < model.size(); i++) {
					dialog.applyToDataset(model.getDataset(i));
					if(i >= 4 && i < 124) {
						graphs.get(i-4).revalidate();
					}
				}
			}
		});
		filter.add(filter_detrend);
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_highfft);
		filter.add(filter_low);
		menubar.add(filter);

		main.setJMenuBar(menubar);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(ynum, xnum));

		for(int i = 0; i < xnum*ynum; i++) {
			subPanels[i] = new JPanel();
			mainPanel.add(subPanels[i]);
		}
		
		JScrollPane scrollMain = new JScrollPane(mainPanel);
		main.add(scrollMain, BorderLayout.CENTER);

		JPanel statusBar = new JPanel();
		JLabel startLabel = new JLabel("Start offset (ms):");
		startText.setValue(0L);
		startText.setColumns(10);
		startText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long start = (Long)startText.getValue();
				long len = (Long)lenText.getValue();
				for(int i = 0; i < graphs.size(); i++) {
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
				long start = (Long)startText.getValue();
				long len = (Long)lenText.getValue();
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

		main.add(statusBar, BorderLayout.SOUTH);

		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);

	}
}

