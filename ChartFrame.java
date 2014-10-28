
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
				final JDialog dialog = new JDialog(thisFrame, "Savitzky-Golay Filter", true);
				dialog.setLayout(new BorderLayout());

				final ECGView[] preview = new ECGView[1];
				preview[0] = (ECGView)view.deepClone(true);

				JPanel controls = new JPanel(new GridBagLayout());

				GridBagConstraints labels = new GridBagConstraints();
				labels.gridwidth = 6;
				labels.ipadx = 10;
				labels.anchor = GridBagConstraints.LINE_END;
				labels.gridx = 0;
				labels.gridy = 0;

				GridBagConstraints values = new GridBagConstraints();
				values.gridx = 6;
				values.gridy = 0;

				GridBagConstraints slider = new GridBagConstraints();
				slider.gridwidth = 5;
				slider.gridx = 7;
				slider.gridy = 0;

				dialog.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
				dialog.setResizable(false);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						e.getWindow().dispose();
					}
				});

				final JSlider degreeSlide = new JSlider(0, 10, 6);
				controls.add(new JLabel("Degree of Fitting Polynomial"), labels);
				final JLabel degreeNum = new JLabel("6");
				controls.add(degreeNum, values);
				degreeSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
					//	System.out.println(degreeSlide.getValue());
						degreeNum.setText("" + degreeSlide.getValue());
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].detrend(degreeSlide.getValue());
					//	System.out.println(degreeSlide.getValue());
						dialog.add(preview[0].getPanel());
					}
				});
				controls.add(degreeSlide, slider);

				final JButton accept = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				accept.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						accept.setEnabled(false);
						cancel.setEnabled(false);
						view.detrend(degreeSlide.getValue());
						thisFrame.revalidate();
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				labels.gridy = 1;
				labels.anchor = GridBagConstraints.CENTER;
				controls.add(cancel, labels);
				slider.gridy = 1;
				controls.add(accept, slider);

				dialog.add(controls, BorderLayout.NORTH);

				preview[0].detrend(degreeSlide.getValue());
				dialog.add(preview[0].getPanel(), BorderLayout.CENTER);

				dialog.setVisible(true);
			}
		});
		JMenuItem filter_savitzky = new JMenuItem("Savitzky-Golay");
		filter_savitzky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog(thisFrame, "Savitzky-Golay Filter", true);
				dialog.setLayout(new BorderLayout());

				final ECGView[] preview = new ECGView[1];
				preview[0] = (ECGView)view.deepClone(true);

				JPanel controls = new JPanel(new GridBagLayout());

				GridBagConstraints labels = new GridBagConstraints();
				labels.gridwidth = 6;
				labels.ipadx = 10;
				labels.anchor = GridBagConstraints.LINE_END;
				labels.gridx = 0;
				labels.gridy = 0;

				GridBagConstraints values = new GridBagConstraints();
				values.gridx = 6;
				values.gridy = 0;

				GridBagConstraints slider = new GridBagConstraints();
				slider.gridwidth = 5;
				slider.gridx = 7;
				slider.gridy = 0;

				dialog.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
				dialog.setResizable(false);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						e.getWindow().dispose();
					}
				});

				controls.add(new JLabel("Left Elements to Sample"), labels);
				final JLabel leftNum = new JLabel("25");
				controls.add(leftNum, values);
				final JSlider leftSlide = new JSlider(1, 100, 25);
				final JSlider rightSlide = new JSlider(1, 100, 25);
				final JSlider degreeSlide = new JSlider(0, 10, 6);
				leftSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						leftNum.setText("" + leftSlide.getValue());
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].applyFilter(0,
											leftSlide.getValue(),
											rightSlide.getValue(),
											degreeSlide.getValue());
						dialog.add(preview[0].getPanel());
					}
				});
				controls.add(leftSlide, slider);

				labels.gridy = 1;
				controls.add(new JLabel("Right Elements to Sample"), labels);
				final JLabel rightNum = new JLabel("25");
				values.gridy = 1;
				controls.add(rightNum, values);
				rightSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						rightNum.setText("" + rightSlide.getValue());
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].applyFilter(0,
											leftSlide.getValue(),
											rightSlide.getValue(),
											degreeSlide.getValue());
						dialog.add(preview[0].getPanel());
					}
				});
				slider.gridy = 1;
				controls.add(rightSlide, slider);

				labels.gridy = 2;
				controls.add(new JLabel("Degree of Fitting Polynomial"), labels);
				final JLabel degreeNum = new JLabel("6");
				values.gridy = 2;
				controls.add(degreeNum, values);
				degreeSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
					//	System.out.println(degreeSlide.getValue());
						degreeNum.setText("" + degreeSlide.getValue());
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].applyFilter(0,
											   leftSlide.getValue(),
											   rightSlide.getValue(),
											   degreeSlide.getValue());
					//	System.out.println(degreeSlide.getValue());
						dialog.add(preview[0].getPanel());
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
				labels.gridy = 3;
				labels.anchor = GridBagConstraints.CENTER;
				controls.add(cancel, labels);
				slider.gridy = 3;
				controls.add(accept, slider);

				dialog.add(controls, BorderLayout.NORTH);

				preview[0].applyFilter(0,
									   leftSlide.getValue(),
									   rightSlide.getValue(),
									   degreeSlide.getValue());
				
				dialog.add(preview[0].getPanel(), BorderLayout.CENTER);

				dialog.setVisible(true);
			}
		});
		JMenuItem filter_high = new JMenuItem("High Pass");
		filter_high.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog(thisFrame, "High Pass Filter", true);
				dialog.setLayout(new BorderLayout());

				final ECGView[] preview = new ECGView[1];
				preview[0] = (ECGView)view.deepClone(true);

				JPanel controls = new JPanel(new GridBagLayout());

				GridBagConstraints labels = new GridBagConstraints();
				labels.gridwidth = 6;
				labels.ipadx = 10;
				labels.anchor = GridBagConstraints.LINE_END;
				labels.gridx = 0;
				labels.gridy = 0;

				GridBagConstraints values = new GridBagConstraints();
				values.gridx = 6;
				values.gridy = 0;

				GridBagConstraints slider = new GridBagConstraints();
				slider.gridwidth = 5;
				slider.gridx = 7;
				slider.gridy = 0;

				dialog.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
				dialog.setResizable(false);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						e.getWindow().dispose();
					}
				});

				controls.add(new JLabel("Frequency Threshold"), labels);
				final JLabel leftNum = new JLabel(".25");
				controls.add(leftNum, values);
				final JSlider leftSlide = new JSlider(1, 400, 25);
				leftSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						leftNum.setText("" + ((double)(int)leftSlide.getValue())/100.0);
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].applyFilter(1, ((double)(int)leftSlide.getValue())/100.0);
						dialog.add(preview[0].getPanel());
					}
				});
				controls.add(leftSlide, slider);

				final JButton accept = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				accept.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						accept.setEnabled(false);
						cancel.setEnabled(false);
						view.applyFilter(1, ((double)(int)leftSlide.getValue())/100.0);
						thisFrame.revalidate();
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				labels.gridy = 1;
				labels.anchor = GridBagConstraints.CENTER;
				controls.add(cancel, labels);
				slider.gridy = 1;
				controls.add(accept, slider);

				dialog.add(controls, BorderLayout.NORTH);

				preview[0].applyFilter(1, ((double)(int)leftSlide.getValue())/100.0);
				
				dialog.add(preview[0].getPanel(), BorderLayout.CENTER);

				dialog.setVisible(true);
			}
		});
		JMenuItem filter_low = new JMenuItem("Low Pass");
		filter_low.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog(thisFrame, "Low Pass Filter", true);
				dialog.setLayout(new BorderLayout());

				final ECGView[] preview = new ECGView[1];
				preview[0] = (ECGView)view.deepClone(true);

				JPanel controls = new JPanel(new GridBagLayout());

				GridBagConstraints labels = new GridBagConstraints();
				labels.gridwidth = 6;
				labels.ipadx = 10;
				labels.anchor = GridBagConstraints.LINE_END;
				labels.gridx = 0;
				labels.gridy = 0;

				GridBagConstraints values = new GridBagConstraints();
				values.gridx = 6;
				values.gridy = 0;

				GridBagConstraints slider = new GridBagConstraints();
				slider.gridwidth = 5;
				slider.gridx = 7;
				slider.gridy = 0;

				dialog.setBounds(thisFrame.getX(), thisFrame.getY(), 500, 400);
				dialog.setResizable(false);
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						e.getWindow().dispose();
					}
				});

				controls.add(new JLabel("Frequency Threshold"), labels);
				final JLabel leftNum = new JLabel("40.0");
				controls.add(leftNum, values);
				final JSlider leftSlide = new JSlider(3000, 7000, 4000);
				leftSlide.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						leftNum.setText("" + ((double)(int)leftSlide.getValue())/100.0);
						dialog.remove(preview[0].getPanel());
						preview[0] = (ECGView)view.deepClone(true);
						preview[0].applyFilter(2, ((double)(int)leftSlide.getValue())/100.0);
						dialog.add(preview[0].getPanel());
					}
				});
				controls.add(leftSlide, slider);

				final JButton accept = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				accept.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						accept.setEnabled(false);
						cancel.setEnabled(false);
						view.applyFilter(2, ((double)(int)leftSlide.getValue())/100.0);
						thisFrame.revalidate();
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				labels.gridy = 1;
				labels.anchor = GridBagConstraints.CENTER;
				controls.add(cancel, labels);
				slider.gridy = 1;
				controls.add(accept, slider);

				dialog.add(controls, BorderLayout.NORTH);

				preview[0].applyFilter(2, ((double)(int)leftSlide.getValue())/100.0);
				
				dialog.add(preview[0].getPanel(), BorderLayout.CENTER);

				dialog.setVisible(true);
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

