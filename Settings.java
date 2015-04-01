
import java.awt.Color;
import java.util.HashMap;

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
		
	}
}

