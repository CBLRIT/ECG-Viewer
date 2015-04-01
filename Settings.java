
import java.awt.Color;
import java.util.HashMap;

public class Settings {
	private class StringColor {
		public String title;
		public Color color;
	}

	private HashMap<Integer, StringColor> annoColors = new HashMap<Integer, StringColor>();
	private int currAnnoType = 0;

	private boolean badLeadInterp;

	public Settings() {
		annoColors = new HashMap<Integer, StringColor>();
	}

	public static Settings makeDefaultSettings() {
		Settings settings = new Settings();
		String[] desc = {"P-wave", "QRS-complex", "R-wave", "T-wave"};
		Color[] colors = {Color.BLACK, Color.ORANGE, Color.GREEN, Color.BLUE};

		for(int i = 0; i < desc.length; i++) {
			StringColor s = new StringColor();
			s.title = desc[i];
			s.color = colors[i];
			settings.annoColors.add(i, s);
		}

		settings.badLeadInterp=false;
		return settings;
	}

	public void load() {

	}

	public void save() {

	}

	public int getSelectedAnnotationType() {
		return currAnnoType;
	}

	public void setSelectedAnnotationType(int type) {
		currAnnoType = type;
	}
	
	public Color getAnnotationColor(int type) {
		return annoColors.get(type).color;
	}

	public Color getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType).color;
	}

	public String getAnnotationTitle(int type) {
		return annoColors.get(type).title;
	}

	public String getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType).title;
	}

	public boolean isBadInterp() {	
		return badLeadInterp;
	}
}
