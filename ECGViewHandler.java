
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
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

	public void loadFile(String file) 
			throws IOException {
		model.readData(file);
	}

	public void writeDataCSV(String file) 
			throws IOException {
		model.writeDataCSV(file);
	}

	public void writeDataMat(String file) 
			throws IOException {
		model.writeDataMat(file);
	}
	
	public void writeDataSubsetCSV(String file, double start, double end) 
			throws IOException {
		model.writeDataSubsetCSV(file, start, end);
	}

	public void writeDataSubsetMat(String file, double start, double end) 
			throws IOException {
		model.writeDataSubsetMat(file, start, end);
	}

	public void writeBadLeads(String file) 
			throws IOException {
		model.writeBadLeads(file);
	}

	public void writeAnnotations(String file) 
			throws IOException {
		model.writeAnnotations(file);
	}

	public int size() {
		return model.size();
	}

	public int leadSize(int index) {
		return model.getDataset(index).size();
	}

	public double getSampleInterval() {
		return model.getSamplesPerSecond();
	}

	public ECGView getView(int i, boolean withLabels) {
		return new ECGView(this, model.getDataset(i), i, ""+(i+4), withLabels);
	}

	public void applyFilter(FilterDialog f, int index) {
		model.applyFilter(index, f.id, f.returnVals());
	}

	public void applyChanges(int index) {
		model.commitChanges(index);
	}

	public void resetChanges(int index) {
		model.resetChanges(index);
	}

	public ECGView shallowFilter(int index, 
								 int filterId, 
								 Number[] params, 
								 boolean withLabels) {
		ECGDataSet data = (ECGDataSet)model.getDataset(index).clone();
		switch(filterId) {
			case 0:
				data.sgolayfilt((int)params[0], (int)params[1], (int)params[2]);
				break;
			case 1:
				data.highpassfilt((double)params[0]);
				break;
			case 2:
				data.lowpassfilt((double)params[0]);
				break;
			case 3: 
				data.highpassfftfilt((double)params[0], 0);
				break;
			case 4:
				data.detrend((int)params[0]);
				break;
			default:
				break;
		}
		return new ECGView(this, data, index, ""+(index+4),  withLabels);
	}

	public ArrayList<Annotation> getAnnotations() {
		return model.getAnnotations();
	}

	public void addAnnotation(int type, double i) {
		model.addAnnotation(type, i);
	}

	public void clearAnnotations() {
		model.clearAnnotations();
	}

	public void setBad(int index, boolean isBad) {
		model.setBad(index, isBad);
	}

	public boolean isBad(int index) {
		return model.isBad(index);
	}
}

