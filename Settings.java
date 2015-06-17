
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public final class Settings {
	private static class StringColor {
		public String title;
		public Color color;
	}

	private static HashMap<Integer, StringColor> annoColors = new HashMap<Integer, StringColor>();
	private static int currAnnoType = 0;

	private static boolean badLeadInterp = false;

	private Settings() {} //no instantiation

	public static void makeDefaultSettings() {
		String[] desc = {"P-wave", "QRS-complex", "R-wave", "T-wave"};
		Color[] colors = {Color.BLACK, Color.ORANGE, Color.GREEN, Color.BLUE};

		for(int i = 0; i < desc.length; i++) {
			StringColor s = new StringColor();
			s.title = desc[i];
			s.color = colors[i];
			annoColors.put(i, s);
		}

		badLeadInterp=false;
	}

	public static void load() {
		
	}

	public static void save() {

	}

	public static int numAnnoTypes() {
		return annoColors.size();
	}

	public static int getSelectedAnnotationType() {
		return currAnnoType;
	}

	public static void setSelectedAnnotationType(int type) {
		currAnnoType = type;
	}
	
	public static Color getAnnotationColor(int type) {
		return annoColors.get(type).color;
	}

	public static Color getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType).color;
	}

	public static String getAnnotationTitle(int type) {
		return annoColors.get(type).title;
	}

	public static String getSelectedAnnotationTitle() {
		return annoColors.get(currAnnoType).title;
	}

	public static boolean isBadInterp() {	
		return badLeadInterp;
	}

	public static void setInterp(boolean interp) {
		badLeadInterp = interp;
	}

	public static void edit() {
		JDialog frame = new JDialog((Frame)null, "Settings", true);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel annotation = new JPanel();
		for(final StringColor sc : annoColors.values()) {
			JPanel p = new JPanel();
			JLabel label = new JLabel(sc.title);
			JButton color = new JButton();
			color.setBackground(sc.color);
			color.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color c = JColorChooser.showDialog((JButton) e.getSource(), "Choose Color", sc.color);
					if(c != null) {
						sc.color = c;
					}
				}
			});
			p.add(label);
			p.add(color);
			p.revalidate();
			annotation.add(p);
			annotation.revalidate();
		}
		annotation.setBorder(BorderFactory.createTitledBorder("Annotations"));
		frame.add(annotation);

		JCheckBox interp = new JCheckBox("Interpolate bad leads?", badLeadInterp);
		interp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				badLeadInterp = !badLeadInterp;
			}
		});
		frame.add(interp);

		frame.pack();
		frame.setVisible(true);
	}

	public static final Color normalLead = UIManager.getColor("Panel.background");
	public static final Color badLead = new Color(233, 174, 174);
	public static final Color selected = UIManager.getColor("ComboBox.selectionBackground");
}

