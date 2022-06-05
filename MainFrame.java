
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.JToolBar;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import org.jfree.chart.plot.XYPlot;

public class MainFrame extends JFrame {
	private int ynum = 10;
	private int xnum = 18;

	private final ECGViewHandler views;
	private final JPanel[] subPanels = new JPanel[xnum*ynum];
	private final ArrayList<ECGView> graphs = new ArrayList<ECGView>();

	private JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());

	private MainFrame thisFrame = this;

	private JFrame progressFrame;
	private JPanel progressPanel;
	private HashMap<String, JProgressBar> progressBars = new HashMap<>();

	public MainFrame(final ECGViewHandler views) {
		super("ECG Viewer");

		this.views = views;

		this.setBounds(20, 20, 1500, 750);
		this.setLayout(new BorderLayout());

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				int ret = JOptionPane.showConfirmDialog(
						null,
						"Are you sure you want to exit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				if(ret == 1) { // No
					return;
				}
				thisFrame.dispose();
				System.exit(0);
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});

		ActionListener notImplemented = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Not yet implemented!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		};

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFile(fc.getSelectedFile().getAbsolutePath(), "ecg");
						thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Could not load file: " + ex.getMessage(),
													  "Error",
													  JOptionPane.ERROR_MESSAGE);
						return;
					} catch (OutOfMemoryError exo) {
						JOptionPane.showMessageDialog(null,
								"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
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
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						if (fc.getLengthTime() == 0) {
							JOptionPane.showMessageDialog(null,
									"Length cannot be 0",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (fc.getLengthTime() > 60000) {
							int confirm = JOptionPane.showConfirmDialog(null,
									fc.getLengthTime() + " is a large amount of time, and the application might run out of memory. It is recommended to use 60,000 or less. Would you like to continue anyway?");
							if (confirm != 0) return;
						}
						try {
							views.loadFileSubset(fc.getSelectedFile().getAbsolutePath(),
									fc.getStartTime(),
									fc.getStartTime()+fc.getLengthTime(),
									"ecg");
							thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
						} catch (OutOfMemoryError exo) {
							JOptionPane.showMessageDialog(null,
									"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
													  "Could not load file" + ex.getMessage(),
													  "Error",
													  JOptionPane.ERROR_MESSAGE);
						return;
					} catch (NullPointerException ex) {
						JOptionPane.showMessageDialog(null,
								"No matching data was found",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});
		JMenuItem load_bad = new JMenuItem("Load Bad Leads...");
		load_bad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					views.loadBadLeads(fc.getSelectedFile().getAbsolutePath());
					thisFrame.relink();
				}
			}
		});
		JMenuItem load_annos = new JMenuItem("Load Annotations...");
		load_annos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					views.loadAnnotations(fc.getSelectedFile().getAbsolutePath());
					thisFrame.relink();
				}
			}
		});

		JMenu exportMenu = new JMenu("Export...");

		JMenuItem export = new JMenuItem("Export...");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
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
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							views.writeDataMat(path);
						} else if ("Comma Separated Values".equals(extension)) {
							views.writeDataCSV(path);
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
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
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
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							views.writeDataSubsetMat(path, 
													 (Long)startText.getValue(),
													 (Long)startText.getValue()
													 		+(Long)lenText.getValue());
						} else if ("Comma Separated Values".equals(extension)) {
							views.writeDataSubsetCSV(path,
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
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						views.writeBadLeads(path);
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
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						views.writeAnnotations(path);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Error writing file: " + ex.getMessage(), 
													  "IOException", 
													  JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JMenu analyzeMenu = new JMenu("Analyze...");
		JMenu analyzeSubset = new JMenu("Analyze subset...");

		JMenu analyzeRWave = new JMenu("R Wave");
		JMenuItem analyzeRWaveF = new JMenuItem("Frequency");
		analyzeRWaveF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFile(fc.getSelectedFile().getAbsolutePath(), "r-frequency");
						thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
								"Could not load file: " + ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (OutOfMemoryError exo) {
						JOptionPane.showMessageDialog(null,
								"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});
		JMenuItem analyzeRWaveI = new JMenuItem("Intensity");
		analyzeRWaveI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFile(fc.getSelectedFile().getAbsolutePath(), "r-intensity");
						thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
								"Could not load file: " + ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (OutOfMemoryError exo) {
						JOptionPane.showMessageDialog(null,
								"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});

		analyzeRWave.add(analyzeRWaveF);
		analyzeRWave.add(analyzeRWaveI);
		analyzeMenu.add(analyzeRWave);

		JMenu analyzeSubsetRWave = new JMenu("R Wave");
		JMenuItem analyzeSubsetRWaveF = new JMenuItem("Frequency");
		analyzeSubsetRWaveF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubsetFileChooser fc = new SubsetFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						if (fc.getLengthTime() == 0) {
							JOptionPane.showMessageDialog(null,
									"Length cannot be 0",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						try {
							views.loadFileSubset(fc.getSelectedFile().getAbsolutePath(),
									fc.getStartTime(),
									fc.getStartTime()+fc.getLengthTime(),
									"r-frequency");
							thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
						} catch (OutOfMemoryError exo) {
							JOptionPane.showMessageDialog(null,
									"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
								"Could not load file" + ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (NullPointerException ex) {
						JOptionPane.showMessageDialog(null,
								"No matching data was found",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});
		JMenuItem analyzeSubsetRWaveI = new JMenuItem("Intensity");
		analyzeSubsetRWaveI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubsetFileChooser fc = new SubsetFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						if (fc.getLengthTime() == 0) {
							JOptionPane.showMessageDialog(null,
									"Length cannot be 0",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						try {
							views.loadFileSubset(fc.getSelectedFile().getAbsolutePath(),
									fc.getStartTime(),
									fc.getStartTime()+fc.getLengthTime(),
									"r-intensity");
							thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
						} catch (OutOfMemoryError exo) {
							JOptionPane.showMessageDialog(null,
									"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
								"Could not load file" + ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (NullPointerException ex) {
						JOptionPane.showMessageDialog(null,
								"No matching data was found",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});

		analyzeSubsetRWave.add(analyzeSubsetRWaveF);
		analyzeSubsetRWave.add(analyzeSubsetRWaveI);
		analyzeSubset.add(analyzeSubsetRWave);

		JMenuItem convert_12 = new JMenuItem("Convert to 12 Lead...");
		convert_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showSaveDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						views.convertTo12(path);
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
				Settings.save();
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(
						null,
						"Are you sure you want to exit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if(ret == 1) { // No
					return;
				}
				thisFrame.setVisible(false);
				thisFrame.dispose();
				System.exit(0);
			}
		});
		menu.add(open);
		menu.add(open_subset);
		menu.add(load_annos);
		menu.add(load_bad);

		exportMenu.add(export);
		exportMenu.add(export_subset);
		exportMenu.add(export_annos);
		exportMenu.add(export_badleads);

		menu.add(exportMenu);
		menu.add(analyzeMenu);
		menu.add(analyzeSubset);

		menu.addSeparator();
		menu.add(convert_12);
		menu.addSeparator();
		menu.add(settings);
		menu.add(exit);
		menubar.add(menu);

		JMenu edit = new JMenu("Edit");
		final JMenuItem edit_selectall = new JMenuItem("Select All");
		edit_selectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < views.size(); i++) {
					graphs.get(i).setSelected(true);
					graphs.get(i).setBackground(Settings.selected);
				}
			}
		});
		final JMenuItem edit_deselectall = new JMenuItem("Deselect All");
		edit_deselectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < views.size(); i++) {
					graphs.get(i).setSelected(false);
					graphs.get(i).setBackground(Settings.normalLead);
				}
			}
		});
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
		edit.addSeparator();
		edit.add(edit_selectall);
		edit.add(edit_deselectall);
		menubar.add(edit);

		JMenu filter = new JMenu("Filter Selected");
		JMenuItem filter_detrend = new JMenuItem("Polynomial Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetrendOptionDialog dialog = new DetrendOptionDialog(thisFrame, "Polynomial Detrend", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_harmonic = new JMenuItem("Harmonic Detrend");
		filter_harmonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HarmonicDetrendDialog dialog = new HarmonicDetrendDialog(thisFrame, "Harmonic Detrend", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_constant = new JMenuItem("Constant Offset");
		filter_constant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConstantOptionDialog dialog = new ConstantOptionDialog(thisFrame, "Constant Offset", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_median = new JMenuItem("Median Offset");
		filter_median.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MedianOffsetDialog dialog = new MedianOffsetDialog(thisFrame, 
																	 "Median Offset", 
																	 true, 
																	 views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SGOptionDialog dialog = new SGOptionDialog(thisFrame, "Savitzky-Golay Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighOptionDialog dialog = new HighOptionDialog(thisFrame, "High Pass Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}
				views.applyFilterAll(dialog, thisFrame); 
			}
		});
		JMenuItem filter_highfft = new JMenuItem("FFT");
		filter_highfft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FFTOptionDialog dialog = new FFTOptionDialog(thisFrame, "FFT Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LowOptionDialog dialog = new LowOptionDialog(thisFrame, "Low Pass Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_wave = new JMenuItem("Wavelet");
		filter_wave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WaveletOptionDialog dialog = new WaveletOptionDialog(thisFrame, "Wavelet Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});
		JMenuItem filter_butter = new JMenuItem("Butterworth");
		filter_butter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButterOptionDialog dialog = new ButterOptionDialog(thisFrame, "Butterworth Filter", true, views, getSelectedLeads().get(0));
				if(!dialog.accepted()) {
					return;
				}

				views.applyFilterAll(dialog, thisFrame);
			}
		});

		JMenuItem filter_fix12 = new JMenuItem("Recalculate 12-Lead");
		filter_fix12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.fix12Lead();
				thisFrame.revalidateAll();
				thisFrame.relink();
			}
		});

		filter.add(filter_detrend);
		filter.add(filter_harmonic);
		filter.add(filter_constant);
		filter.add(filter_median);
		filter.addSeparator();
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_highfft);
		filter.add(filter_low);
		filter.add(filter_wave);
		filter.add(filter_butter);
		filter.addSeparator();
		filter.add(filter_fix12);
		menubar.add(filter);

		this.setJMenuBar(menubar);

		// Toolbar
		JToolBar toolbar = new JToolBar("Main");
		toolbar.setFloatable(false);
		JButton openButton = makeToolbarButton("Open24", "Open");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
				int ret = fc.showOpenDialog(thisFrame);
				if(ret == JFileChooser.APPROVE_OPTION) {
					try {
						views.loadFile(fc.getSelectedFile().getAbsolutePath(), "ecg");
						thisFrame.setTitle(fc.getSelectedFile().getName() + " - ECG Viewer");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, 
													  "Could not load file" + ex.getMessage(),
													  "Error",
													  JOptionPane.ERROR_MESSAGE);
						return;
					} catch (OutOfMemoryError exo) {
						JOptionPane.showMessageDialog(null,
								"Ran out of memory! Try using a smaller file, or a smaller subset of the file, or restarting the application.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					thisFrame.relink();
				}
			}
		});
		toolbar.add(openButton);
		JButton exportButton = makeToolbarButton("Export24", "Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Settings.getDefaultDirectory()));
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
						String path = fc.getSelectedFile().getAbsolutePath();
						File f = new File(path);
						if(f.exists() && !f.isDirectory()) {
							ret = JOptionPane.showConfirmDialog(
									null,
									"Are you sure you want to overwrite this file?",
									"Overwrite",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if(ret == 1) { // No
								return;
							}
						}
						String extension = fc.getFileFilter().getDescription();
						if("MATLAB matrix".equals(extension)) {
							views.writeDataMat(path);
						} else if ("Comma Separated Values".equals(extension)) {
							views.writeDataCSV(path);
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
		toolbar.add(exportButton);
		toolbar.addSeparator();
		JButton undoButton = makeToolbarButton("Undo24", "Undo");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.undo();
				thisFrame.relink();
			}
		});
		toolbar.add(undoButton);
		JButton redoButton = makeToolbarButton("Redo24", "Redo");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				views.redo();
				thisFrame.relink();
			}
		});
		toolbar.add(redoButton);

		add(toolbar, BorderLayout.PAGE_START);
		// end toolbar

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(ynum, xnum));
		BoxSelectListener bsl = new BoxSelectListener(this, mainPanel);
		mainPanel.addMouseListener(bsl);
		mainPanel.addMouseMotionListener(bsl);

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

		progressFrame = new JFrame("Progress");
		progressPanel = new JPanel();

		this.add(statusBar, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	private void revalidateAll() {
		for(int i = 0; i < graphs.size(); i++) {
			graphs.get(i).revalidate();
		}
	}

	public void relink() {
		graphs.clear();
		for(int i = 0; i < subPanels.length; i++) {
			subPanels[i].removeAll();
			subPanels[i].revalidate();
			subPanels[i].repaint();
		}
		for(int i = 0; i < views.size(); i++) {
			int index = views.getLayout()[i][0]*xnum + views.getLayout()[i][1];
			final ECGView graph = views.getView(i, false);

			final int count = i;
			graph.getPanel().addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						graph.setSelected(!graph.isSelected());
						graph.setBackground(graph.isSelected() ?
											Settings.selected :
											Settings.normalLead);
						ChartFrame cf = new ChartFrame(views, count, views.getTitles()[count]);
						cf.addWindowListener(new WindowListener() {
							public void windowClosing(WindowEvent e) {
								graph.setBackground(!graph.isBad() ? 
												Settings.normalLead : 
												Settings.badLead);
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
					} else if (e.getClickCount() == 1) {
						graph.setSelected(!graph.isSelected());
						graph.setBackground(graph.isSelected() ?
											Settings.selected :
											Settings.normalLead);
					}
				}

				//don't care
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});

			graphs.add(graph);
			subPanels[index].add(graph.getPanel());
			subPanels[index].revalidate();
			subPanels[index].repaint();
		}

		if(views.size() == 12) {
			final ECGView graph = views.getCompositeView(false);
			graph.getPanel().addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						ChartFrame cf = new ChartFrame(views);
					}
				}

				//don't care
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});

			graphs.add(graph);
			JPanel p = subPanels[2*xnum + 3];
			p.add(graph.getPanel());
			p.revalidate();
			p.repaint();
		}

		thisFrame.revalidate();
		thisFrame.repaint();
	}

	private JButton makeToolbarButton(String imgName, String toolTip) {
		String imgLoc = "imgs/toolbarButtonGraphics/general/" + imgName + ".gif";
		URL imageURL = MainFrame.class.getResource(imgLoc);

		JButton button = new JButton();
		button.setToolTipText(toolTip);
		button.setIcon(new ImageIcon(imageURL, toolTip));

		return button;
	}

	public ArrayList<Integer> getSelectedLeads() {
		ArrayList<Integer> ret = new ArrayList<Integer>();

		for(int i = 0; i < views.size(); i++) {
			if (graphs.get(i).isSelected()) {
				ret.add(i);
			}
		}

		return ret;
	}

	public void selectLeadsInBox(Point p1, Point p2) {
		int[][] layout = views.getLayout();
		for(int i = 0; i < layout.length; i++) {
			int centerx = subPanels[layout[i][0]*xnum + layout[i][1]].getLocation().x 
						+ subPanels[layout[i][0]*xnum + layout[i][1]].getWidth()/2;
			int centery = subPanels[layout[i][0]*xnum + layout[i][1]].getLocation().y 
						+ subPanels[layout[i][0]*xnum + layout[i][1]].getHeight()/2;

			if(centerx > p1.x && centerx < p2.x && centery > p1.y && centery < p2.y) {
				graphs.get(i).setSelected(true);
				graphs.get(i).setBackground(Settings.selected);
			}
		}
	}

	public void setProgressBar(String name, int val) {
		if (!progressBars.containsKey(name)) {

			JProgressBar progressBar = new JProgressBar();
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.setVisible(true);
			progressBar.setBounds(0,0,200,30);

			progressPanel.add(progressBar);
			progressFrame.add(progressPanel);

			progressFrame.setSize(200, 500);
//			progressFrame.setVisible(true);

			progressBars.put(name, progressBar);
		}
		progressBars.get("test").setValue(val);
		progressBars.get("test").revalidate();
		progressBars.get("test").repaint();
		if (val == 100) {
			progressBars.get(name).setVisible(false);
			progressPanel.remove(progressBars.get(name));
			progressBars.remove(name);
			if (progressBars.size() == 0) {
				progressFrame.setVisible(false);
			}
			progressPanel.revalidate();
			progressPanel.repaint();

			progressFrame.revalidate();
			progressFrame.repaint();
			thisFrame.revalidate();
			thisFrame.repaint();
			this.revalidate();
			this.repaint();
		}
		progressPanel.revalidate();
		progressPanel.repaint();

		progressFrame.revalidate();
		progressFrame.repaint();
	}
}

class BoxSelectListener implements MouseInputListener {
	public BoxSelectListener(MainFrame parent, JPanel local) {
		this.parent = parent;
		this.local = local;
	}

	private MainFrame parent;
	private JPanel local;
	private Point corner;

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			corner = e.getPoint();
		}
	}
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			Graphics g = local.getGraphics();
			local.paintComponents(g);

			parent.selectLeadsInBox(corner, e.getPoint());
		}
	}
	public void mouseDragged(MouseEvent e) {
		Graphics2D g = (Graphics2D)local.getGraphics();
		BufferedImage bufImg = new BufferedImage(local.getWidth(), 
												 local.getHeight(), 
												 BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = bufImg.createGraphics();
		local.paintComponents(g2);
		Point p = e.getPoint();
		g2.setColor(new Color(Settings.selected.getRed(),
							Settings.selected.getGreen(),
							Settings.selected.getBlue(),
							192));
		g2.fillRect(corner.x, corner.y, p.x - corner.x, p.y - corner.y);

		g.drawImage(bufImg, null, 0, 0);
	}
	public void mouseMoved(MouseEvent e) {}
}

