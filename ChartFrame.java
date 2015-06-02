
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import java.text.NumberFormat;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.ImageIcon;
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
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
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
	private ECGView view;
	private int index;
	private final JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private final JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private final JCheckBoxMenuItem file_badlead;
	private final ChartFrame thisFrame = this;
	private final JCheckBoxMenuItem anno_enable;
	private final JCheckBoxMenuItem anno_remove;
	private final JToggleButton placeAnnoButton;
	private final JToggleButton removeAnnoButton;

	/**
	 * Constructor - creates the frame and everything in it
	 *
	 * @param handler the viewhandler to use
	 * @param index the view to display
	 * @param title the title of the frame
	 */
	public ChartFrame(final ECGViewHandler handler, int index, String title) {
		super(title);
		setBounds(0, 0, 650, 650);
		setLayout(new BorderLayout());

		this.handler = handler;
		this.index = index;
		view = handler.getView(index, true);

		//start with the menu bar
		JMenuBar menu = new JMenuBar();

		//dataset menu
		JMenu file = new JMenu("Dataset");
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

		//edit menu
		JMenu edit = new JMenu("Edit");
		final JMenuItem edit_undo = new JMenuItem("Undo");
		edit_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.undo();
				thisFrame.view.update();
				thisFrame.view.redrawAnnotations();
			}
		});
		final JMenuItem edit_redo = new JMenuItem("Redo");
		edit_redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.redo();
				thisFrame.view.update();
				thisFrame.view.redrawAnnotations();
			}
		});
		edit.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				edit_undo.setEnabled(handler.canUndo());
				edit_undo.setText("Undo " + (handler.canUndo()?handler.undoMessage():""));
				edit_redo.setEnabled(handler.canRedo());
				edit_redo.setText("Redo " + (handler.canRedo()?handler.redoMessage():""));
			}

			//don't care
			public void menuDeselected(MenuEvent e) {}
			public void menuCanceled(MenuEvent e) {}
		});
		edit.add(edit_undo);
		edit.add(edit_redo);
		menu.add(edit);

		//filter menu
		JMenu filter = new JMenu("Filter");
		JMenuItem filter_detrend = new JMenuItem("Polynomial Detrend");
		filter_detrend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DetrendOptionDialog dialog = new DetrendOptionDialog(thisFrame, 
																	 "Polynomial Detrend", 
																	 true, 
																	 thisFrame.handler,
																	 thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_harmonic = new JMenuItem("Harmonic Detrend");
		filter_harmonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HarmonicDetrendDialog dialog = new HarmonicDetrendDialog(thisFrame, 
																	 "Harmonic Detrend", 
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
		JMenuItem filter_wave = new JMenuItem("Wavelet");
		filter_wave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WaveletOptionDialog dialog = new WaveletOptionDialog(thisFrame, 
															 "Wavelet Filter", 
															 true, 
															 thisFrame.handler,
															 thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_constant = new JMenuItem("Constant Offset");
		filter_constant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConstantOptionDialog dialog = new ConstantOptionDialog(thisFrame,
																	   "Constant Offset",
																	   true,
																	   thisFrame.handler,
																	   thisFrame.index);
				if(dialog.accepted()) {
					handler.applyFilter(dialog, thisFrame.index);
				}
				thisFrame.view.revalidate();
			}
		});
		JMenuItem filter_butter = new JMenuItem("Butterworth");
		filter_butter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButterOptionDialog dialog = new ButterOptionDialog(thisFrame,
																   "Butterworth",
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
		filter.add(filter_harmonic);
		filter.add(filter_constant);
		filter.addSeparator();
		filter.add(filter_savitzky);
		filter.add(filter_high);
		filter.add(filter_ffthigh);
		filter.add(filter_low);
		filter.add(filter_wave);
		filter.add(filter_butter);
		
		menu.add(filter);

		placeAnnoButton = new JToggleButton();
		placeAnnoButton.setSelected(false);
		removeAnnoButton = new JToggleButton();
		removeAnnoButton.setSelected(false);
		//annotation menu
		JMenu annotations = new JMenu("Annotation");
		anno_enable = new JCheckBoxMenuItem("Place Annotations");
		anno_enable.setState(false);
		anno_enable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanRemove(false);
				thisFrame.view.setCanPlace(anno_enable.getState());
				placeAnnoButton.setSelected(anno_enable.getState());
				removeAnnoButton.setSelected(false);
				anno_remove.setState(false);
			}
		});
		anno_remove = new JCheckBoxMenuItem("Remove Annotations");
		anno_remove.setState(false);
		anno_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanPlace(false);
				thisFrame.view.setCanRemove(anno_enable.getState());
				removeAnnoButton.setSelected(anno_enable.getState());
				anno_enable.setState(false);
				placeAnnoButton.setSelected(false);
			}
		});
		JMenuItem annotations_clear = new JMenuItem("Clear");
		annotations_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.clearAnnotations();
			}
		});
		JMenuItem annotations_auto = new JMenuItem("Find R-Waves");
		annotations_auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.handler.extractFeatures(thisFrame.index);
				thisFrame.view.redrawAnnotations();
				thisFrame.view.revalidate();
			}
		});
		annotations.add(anno_enable);
		annotations.add(anno_remove);
		annotations.add(annotations_clear);
		annotations.add(annotations_auto);
		annotations.addSeparator();
		ButtonGroup annoGroup = new ButtonGroup();
		JRadioButtonMenuItem[] annotations_colors = new JRadioButtonMenuItem[Settings.numAnnoTypes()];
		for(int i = 0; i < annotations_colors.length; i++) {
			final int count = i;
			annotations_colors[i] = new JRadioButtonMenuItem("Annotation " + (i+1) 
											+ " (" + Settings.getAnnotationTitle(i) + ")", 
											Settings.getSelectedAnnotationType()==i);
			annotations_colors[i].addActionListener(new ActionListener() {
				private final int changeNum = count;

				public void actionPerformed(ActionEvent e) {
					Settings.setSelectedAnnotationType(changeNum);
				}
			});
			annotations.add(annotations_colors[i]);
			annoGroup.add(annotations_colors[i]);
		}

		menu.add(annotations);

		setJMenuBar(menu);

		// Toolbar
		JToolBar toolbar = new JToolBar("Main");
		toolbar.setFloatable(false);
		JButton undoButton = makeToolbarButton("Undo24", "Undo");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.undo();
				thisFrame.view.update();
				thisFrame.view.redrawAnnotations();
			}
		});
		toolbar.add(undoButton);
		JButton redoButton = makeToolbarButton("Redo24", "Redo");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.redo();
				thisFrame.view.update();
				thisFrame.view.redrawAnnotations();
			}
		});
		toolbar.add(redoButton);
		toolbar.addSeparator();
		ChartFrame.class.getResource("imgs/toolbarButtonGraphics/general/Bookmarks24.gif");
		placeAnnoButton.setToolTipText("Toggle Place Annotation");
		placeAnnoButton.setIcon(
			new ImageIcon("imgs/toolbarButtonGraphics/general/Bookmarks24.gif", 
							"Toggle Place Annotation"));
		placeAnnoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanRemove(false);
				thisFrame.view.setCanPlace(placeAnnoButton.isSelected());
				anno_enable.setState(placeAnnoButton.isSelected());
				removeAnnoButton.setSelected(false);
				anno_remove.setState(false);
			}
		});
		toolbar.add(placeAnnoButton);
		ChartFrame.class.getResource("imgs/toolbarButtonGraphics/general/Remove24.gif");
		removeAnnoButton.setToolTipText("Toggle Remove Annotation");
		removeAnnoButton.setIcon(
			new ImageIcon("imgs/toolbarButtonGraphics/general/Remove24.gif", 
							"Toggle Remove Annotation"));
		removeAnnoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanPlace(false);
				thisFrame.view.setCanRemove(removeAnnoButton.isSelected());
				anno_remove.setState(removeAnnoButton.isSelected());
				anno_enable.setState(false);
				placeAnnoButton.setSelected(false);
			}
		});
		toolbar.add(removeAnnoButton);

		add(toolbar, BorderLayout.PAGE_START);
		// end toolbar

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
		setVisible(true);
	}

	/**
	 * Constructor - frame that views a composite view
	 *
	 * @param handler the viewhandler to use
	 */
	public ChartFrame(final ECGViewHandler handler) {
		super("Composite");
		setBounds(0, 0, 500, 500);
		setLayout(new BorderLayout());

		this.handler = handler;
		this.index = 0;
		view = handler.getCompositeView(true);

		//start with the menu bar
		JMenuBar menu = new JMenuBar();

		//dataset menu
		JMenu file = new JMenu("Dataset");
		file_badlead = new JCheckBoxMenuItem("Bad Lead");
		file_badlead.setState(false);
		JMenuItem file_exit = new JMenuItem("Exit");
		file_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
				thisFrame.dispose();
			}
		});
		file.add(file_exit);
		menu.add(file);

		//edit menu
		JMenu edit = new JMenu("Edit");
		final JMenuItem edit_undo = new JMenuItem("Undo");
		edit_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.undo();
				thisFrame.view.redrawAnnotations();
			}
		});
		final JMenuItem edit_redo = new JMenuItem("Redo");
		edit_redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.redo();
				thisFrame.view.redrawAnnotations();
			}
		});
		edit.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) {
				edit_undo.setEnabled(handler.canUndo());
				edit_undo.setText("Undo " + (handler.canUndo()?handler.undoMessage():""));
				edit_redo.setEnabled(handler.canRedo());
				edit_redo.setText("Redo " + (handler.canRedo()?handler.redoMessage():""));
			}

			//don't care
			public void menuDeselected(MenuEvent e) {}
			public void menuCanceled(MenuEvent e) {}
		});
		edit.add(edit_undo);
		edit.add(edit_redo);
		menu.add(edit);

		placeAnnoButton = new JToggleButton();
		placeAnnoButton.setSelected(false);
		removeAnnoButton = new JToggleButton();
		removeAnnoButton.setSelected(false);
		//annotation menu
		JMenu annotations = new JMenu("Annotation");
		anno_enable = new JCheckBoxMenuItem("Place Annotations");
		anno_enable.setState(false);
		anno_enable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanRemove(false);
				thisFrame.view.setCanPlace(anno_enable.getState());
				placeAnnoButton.setSelected(anno_enable.getState());
				removeAnnoButton.setSelected(false);
				anno_remove.setState(false);
			}
		});
		anno_remove = new JCheckBoxMenuItem("Remove Annotations");
		anno_remove.setState(false);
		anno_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanPlace(false);
				thisFrame.view.setCanRemove(anno_enable.getState());
				removeAnnoButton.setSelected(anno_enable.getState());
				anno_enable.setState(false);
				placeAnnoButton.setSelected(false);
			}
		});
		JMenuItem annotations_clear = new JMenuItem("Clear");
		annotations_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.clearAnnotations();
			}
		});
		JMenuItem annotations_auto = new JMenuItem("Find R-Waves");
		annotations_auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.handler.extractFeatures(thisFrame.index);
				thisFrame.view.redrawAnnotations();
			}
		});
		annotations.add(anno_enable);
		annotations.add(anno_remove);
		annotations.add(annotations_clear);
		annotations.add(annotations_auto);
		annotations.addSeparator();
		ButtonGroup annoGroup = new ButtonGroup();
		JRadioButtonMenuItem[] annotations_colors = new JRadioButtonMenuItem[Settings.numAnnoTypes()];
		for(int i = 0; i < annotations_colors.length; i++) {
			final int count = i;
			annotations_colors[i] = new JRadioButtonMenuItem("Annotation " + (i+1) 
											+ " (" + Settings.getAnnotationTitle(i) + ")", 
											Settings.getSelectedAnnotationType()==i);
			annotations_colors[i].addActionListener(new ActionListener() {
				private final int changeNum = count;

				public void actionPerformed(ActionEvent e) {
					Settings.setSelectedAnnotationType(changeNum);
				}
			});
			annotations.add(annotations_colors[i]);
			annoGroup.add(annotations_colors[i]);
		}

		menu.add(annotations);

		setJMenuBar(menu);

		// Toolbar
		JToolBar toolbar = new JToolBar("Main");
		toolbar.setFloatable(false);
		JButton undoButton = makeToolbarButton("Undo24", "Undo");
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.undo();
				thisFrame.view.redrawAnnotations();
			}
		});
		toolbar.add(undoButton);
		JButton redoButton = makeToolbarButton("Redo24", "Redo");
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler.redo();
				thisFrame.view.redrawAnnotations();
			}
		});
		toolbar.add(redoButton);
		toolbar.addSeparator();
		ChartFrame.class.getResource("imgs/toolbarButtonGraphics/general/Bookmarks24.gif");
		placeAnnoButton.setToolTipText("Toggle Place Annotation");
		placeAnnoButton.setIcon(
			new ImageIcon("imgs/toolbarButtonGraphics/general/Bookmarks24.gif", 
							"Toggle Place Annotation"));
		placeAnnoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanRemove(false);
				thisFrame.view.setCanPlace(placeAnnoButton.isSelected());
				anno_enable.setState(placeAnnoButton.isSelected());
				removeAnnoButton.setSelected(false);
				anno_remove.setState(false);
			}
		});
		toolbar.add(placeAnnoButton);
		ChartFrame.class.getResource("imgs/toolbarButtonGraphics/general/Remove24.gif");
		removeAnnoButton.setToolTipText("Toggle Remove Annotation");
		removeAnnoButton.setIcon(
			new ImageIcon("imgs/toolbarButtonGraphics/general/Remove24.gif", 
							"Toggle Remove Annotation"));
		removeAnnoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.view.setCanPlace(false);
				thisFrame.view.setCanRemove(removeAnnoButton.isSelected());
				anno_remove.setState(removeAnnoButton.isSelected());
				anno_enable.setState(false);
				placeAnnoButton.setSelected(false);
			}
		});
		toolbar.add(removeAnnoButton);

		add(toolbar, BorderLayout.PAGE_START);
		// end toolbar

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
		setVisible(true);
	}

	private JButton makeToolbarButton(String imgName, String toolTip) {
		String imgLoc = "imgs/toolbarButtonGraphics/general/" + imgName + ".gif";
		URL imageURL = ChartFrame.class.getResource(imgLoc);

		JButton button = new JButton();
		button.setToolTipText(toolTip);
		button.setIcon(new ImageIcon(imageURL, toolTip));

		return button;
	}
}

