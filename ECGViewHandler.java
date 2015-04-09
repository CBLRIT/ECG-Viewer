
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ECGViewHandler {
	private ECGModel model;	

	public ECGViewHandler(ECGModel model) {
		this.model = model;
	}

	public void loadFile(String file) 
			throws IOException {
		model.readData(file);
	}

	public void loadFileSubset(String file, double start, double end) 
			throws IOException {
		model.readSubsetData(file, start, end);
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

	public int[][] getLayout() {
		return model.getLayout();
	}

	public int getOffset() {
		return model.getOffset();
	}

	public ECGView getView(int i, boolean withLabels) {
		return new ECGView(this, model.getDataset(i), i, ""+(i+model.getOffset()-1), withLabels);
	}

	public void applyFilter(FilterDialog f, int index) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		changes.put(
				index, 
				(ECGDataSet)model.getDataset(index).clone());
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
			changes, 
			"Apply filter " + f.Id() + " to lead " + (index+model.getOffset()-1)));
		model.applyFilter(index, f.Id(), f.returnVals());
	}

	public void applyFilterAll(FilterDialog f) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>(model.size());
		for(int i = 0; i < model.size(); i++) {
			changes.put(i, (ECGDataSet)model.getDataset(i).clone());
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
						changes, 
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
			case 7:
				data.butterworthfilt((int)params[0],
									 model.getSamplesPerSecond(),
									 (double)params[1],
								 	 (int)params[2]);
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
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		ArrayList<Annotation> annotations = model.getAnnotations();
		int j = 0;
		for(Annotation a : annotations) {
			changes.put(j, a);
			j++;
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
					changes,
					"Add type " + type + " annotation at time " + i));
		model.addAnnotation(type, i);
	}

	public void clearAnnotations() {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		ArrayList<Annotation> annotations = model.getAnnotations();
		int i = 0;
		for(Annotation a : annotations) {
			changes.put(i, a);
			i++;
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
					changes,
					"Clear annotations"));
		model.clearAnnotations();
	}

	public void extractFeatures(int lead) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		ArrayList<Annotation> annotations = model.getAnnotations();
		int i = 0;
		for(Annotation a : annotations) {
			changes.put(i, a);
			i++;
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
					changes,
					"Auto Annotations"));
		model.extractFeatures(lead);
	}

	public void setBad(int index, boolean isBad) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		changes.put(index, (ECGDataSet)model.getDataset(index).clone());
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
				changes,
				(isBad?"Set":"Unset") + " bad lead " + (index+model.getOffset())));
		model.setBad(index, isBad);
	}

	public boolean isBad(int index) {
		return model.isBad(index);
	}

	public void undo() {
		model.undo();
	}

	public void redo() {
		model.redo();
	}

	public String undoMessage() {
		return model.undoMessage();
	}

	public String redoMessage() {
		return model.redoMessage();
	}

	public boolean canUndo() {
		return model.canUndo();
	}

	public boolean canRedo() {
		return model.canRedo();
	}
}

