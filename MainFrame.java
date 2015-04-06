
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

					thisFrame.relink();
				}
			}
		});
		JMenuItem open_subset = new JMenuItem("Open Subset...");
		open_subset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubsetFileChooser fc = new SubsetFileChooser();
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFileSubset(fc.getSelectedFile().getAbsolutePath(),
											 fc.getStartTime(),
											 fc.getLengthTime());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
													  "Could not load file",
													  "Error",
													  JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
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
		JMenuItem settings = new JMenuItem("Settings");
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.edit();
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
		menu.add(open_subset);
		menu.add(export);
		menu.add(export_subset);
		menu.add(export_annos);
		menu.add(export_badleads);
		menu.addSeparator();
		menu.add(settings);
		menu.add(exit);
		menubar.add(menu);

		JMenu edit = new JMenu("Edit");
		final JMenuItem edit_undo = new JMenuItem("Undo");
		edit_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.undo();
				thisFrame.relink();
			}
		});
		final JMenuItem edit_redo = new JMenuItem("Redo");
		edit_redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.redo();
				thisFrame.relink();
			}
		});
		edit.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				edit_undo.setEnabled(views.canUndo());
				edit_undo.setText("Undo " + (views.canUndo()?views.undoMessage():""));
				edit_redo.setEnabled(views.canRedo());
				edit_redo.setText("Redo " + (views.canRedo()?views.redoMessage():""));
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

				views.applyFilterAll(dialog);
				
				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_constant = new JMenuItem("Constant Offset");
		filter_constant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConstantOptionDialog dialog = new ConstantOptionDialog(thisFrame, "Constant Offset", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);

				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SGOptionDialog dialog = new SGOptionDialog(thisFrame, "Savitzky-Golay Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);
				
				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighOptionDialog dialog = new HighOptionDialog(thisFrame, "High Pass Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);
				
				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_highfft = new JMenuItem("FFT");
		filter_highfft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FFTOptionDialog dialog = new FFTOptionDialog(thisFrame, "FFT Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);
				
				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LowOptionDialog dialog = new LowOptionDialog(thisFrame, "Low Pass Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);
				
				thisFrame.revalidateAll();
			}
		});
		JMenuItem filter_wave = new JMenuItem("Wavelet");
		filter_wave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WaveletOptionDialog dialog = new WaveletOptionDialog(thisFrame, "Wavelet Filter", true, views, 35);
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog);

				thisFrame.revalidateAll();
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

		this.add(statusBar, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void revalidateAll() {
		for(int i = 0; i < graphs.size(); i++) {
			graphs.get(i).revalidate();
		}
	}

	private void relink() {
		graphs.clear();
		for(int i = 0; i < views.size(); i++) {
			int index = views.getLayout()[i][0]*xnum + views.getLayout()[i][1];
			final ECGView graph = views.getView(i, false);
			subPanels[index].removeAll();

			final int count = i;
			graph.getPanel().addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					ChartFrame cf = new ChartFrame(views, count, ""+(count+views.getOffset()-1));
					cf.addWindowListener(new WindowListener() {
						public void windowClosing(WindowEvent e) {
							graph.setBackground(!graph.isBad() ? 
											UIManager.getColor("Panel.background") : 
											new Color(233, 174, 174));
							graph.update();
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
