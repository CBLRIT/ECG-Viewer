
import java.awt.Color;
import java.util.HashMap;

public class ECGViewHandler {
	private ECGModel model;	

	private int currAnnoType = 0;
	private HashMap<Integer, Color> annoColors = new HashMap<Integer, Color>();

	private final int titleOffset = 4;

	public ECGViewHandler(ECGModel model) {
		this.model = model;

		annoColors.put(0, Color.BLACK);
		annoColors.put(1, Color.ORANGE);
		annoColors.put(2, Color.GREEN);
		annoColors.put(3, Color.BLUE);
	}

	public int getSelectedAnnotationType() {
		return currAnnoType;
	}

	public void setSelectedAnnotationType(int type) {
		currAnnoType = type;
	}

	public Color getAnnotationColor(int type) {
		return annoColors.get(type);
	}

	public Color getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType);
	}

	public void loadFile(String file) {
		model.readData(file);
	}

	public ECGView getView(int i, boolean withLabels) {
		return new ECGView(this, model.getDataset(i), ""+(i+4), withLabels);
	}
}
