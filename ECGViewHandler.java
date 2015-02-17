
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ECGViewHandler {
	private ECGModel model;	
	private String message;
	private UndoStack<Change<ECGModel, String>> history;

	private int currAnnoType = 0;
	private HashMap<Integer, Color> annoColors = new HashMap<Integer, Color>();

	private final int titleOffset = 4;

	public ECGViewHandler(ECGModel model) {
		this.model = model;
		this.history = new UndoStack<Change<ECGModel, String>>();

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
		history.reset();
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
		this.message = "Apply filter " + f.Id() + " to lead " + (index+4);
		history.pushChange(new Change<ECGModel, String>(model.clone(), 
								"Apply filter " + f.Id() + " to lead " + (index+4)));
		model.applyFilter(index, f.Id(), f.returnVals());
	}

	public void applyFilterAll(FilterDialog f) {
		this.message = "Apply filter " + f.Id() + " to all leads";
		history.pushChange(new Change<ECGModel, String>(model.clone(), 
									"Apply filter " + f.Id() + " to all leads"));
		for(int i = 0; i < model.size(); i++) {
			model.applyFilter(i, f.Id(), f.returnVals());
		}
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
			case 5:
				data.waveletfilt((double)params[0]);
				break;
			case 6:
				data.constofffilt((double)params[0]);
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
		this.message = "Add type " + type + " annotation at time " + i;
		history.pushChange(new Change<ECGModel, String>(model.clone(), 
					"Add type " + type + " annotation at time " + i));
		model.addAnnotation(type, i);
	}

	public void clearAnnotations() {
		this.message = "Clear annotations";
		history.pushChange(new Change<ECGModel, String>(model.clone(), 
					"Clear annotations"));
		model.clearAnnotations();
	}

	public void setBad(int index, boolean isBad) {
		this.message = (isBad?"Set":"Unset") + " bad lead " + index;
		history.pushChange(new Change<ECGModel, String>(model.clone(), 
					(isBad?"Set":"Unset") + " bad lead " + index));
		model.setBad(index, isBad);
	}

	public boolean isBad(int index) {
		return model.isBad(index);
	}

	public void undo() {
		this.message = history.peekUndo().getMessage();
		Change<ECGModel, String> c = history.undo(
				new Change<ECGModel, String>(this.model, this.message));
		this.model = c.getData();
		this.message = c.getMessage();
	}

	public void redo() {
		this.message = history.peekRedo().getMessage();
		Change<ECGModel, String> c = history.redo(
				new Change<ECGModel, String>(this.model, this.message));
		this.model = c.getData();
		this.message = c.getMessage();
	}

	public String undoMessage() {
		return history.peekUndo().getMessage();
	}

	public String redoMessage() {
		return history.peekRedo().getMessage();
	}

	public boolean canUndo() {
		return history.canUndo();
	}

	public boolean canRedo() {
		return history.canRedo();
	}
}

