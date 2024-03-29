
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingWorker;

public class ECGViewHandler {
	private ECGModel model;

	private String xAxisLabel = "Time (msec)", yAxisLabel = "Potential (mV)";

	public ECGViewHandler(ECGModel model) {
		this.model = model;
	}

	public void loadFile(String file, String mode)
			throws IOException {
		model.readData(file, 0, Double.POSITIVE_INFINITY, mode);
	}

	public void loadFileSubset(String file, double start, double end, String mode)
			throws IOException, NullPointerException {
		try {
			model.readSubsetData(file, start, end, mode);
		} catch (NullPointerException ex) {
			this.model = new ECGModel();
			throw ex;
		}
	}

	public double getFileLength(String filename) throws IOException {
		return model.getFileLength(filename);
	}

	public void loadBadLeads(String file) {
		model.readBadLeads(file);
	}

	public void loadAnnotations(String file) {
		model.readAnnotations(file);
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

	public void convertTo12(String file) 
			throws IOException {
		model.convertTo12(file);
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

	public String[] getTitles() {
		return model.getTitles();
	}

	public int getOffset() {
		return model.getOffset();
	}

	public ECGView getView(int i, boolean withLabels) {
		return new ECGView(this, model.getDataset(i), i, model.getTitle(i), withLabels);
	}

	public ECGView getCompositeView(boolean withLabels) {
		ECGView v = new ECGView(this, model.getDataset(0), 0, "Composite", withLabels);
		for(int i = 1; i < model.size(); i++) {
			v.addDataset(model.getDataset(i));
		}
		if(withLabels) {
			v.addLegend();
		}
		return v;
	}

	public void fix12Lead() {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		for(int i = 0; i < model.size(); i++) {
			changes.put(i, (ECGDataSet)model.getDataset(i).clone());
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
						changes, 
						"12 lead fix"));
		model.interpolate12Lead();
	}

	public void applyFilter(FilterDialog f, int index) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		changes.put(
				index, 
				(ECGDataSet)model.getDataset(index).clone());
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
			changes, 
			"Apply filter " + f.Id() + " to lead " + model.getTitles()[index]));
		model.applyFilter(index, f.Id(), f.returnVals());
	}

	public void applyFilterAll(final FilterDialog f, final MainFrame attach) {
		final HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>(model.size());
		for(int i = 0; i < model.size(); i++) {
			changes.put(i, (ECGDataSet)model.getDataset(i).clone());
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
						changes, 
						"Apply filter " + f.Id() + " to leads"));
		ProgressDialog.make(new SwingWorker<Void, Void>() {
			@Override
			public Void doInBackground() {
				setProgress(0);
				for(Iterator<Integer> i = attach.getSelectedLeads().iterator(); i.hasNext();) {
					int ind = i.next();
					model.applyFilter(ind, f.Id(), f.returnVals());
					setProgress((int)((double)ind/(double)model.size()*100));
					if(isCancelled()) {
						model.undo();
						model.resetFuture();
						break;
					}
				}
				return null;
			}

			@Override
			public void done() {
				attach.relink();
			}
		}, attach);
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
				data.waveletfilt((double)params[0], (int)params[1], (int)params[2]);
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
			case 8:
				data.harmonicDetrend();
				break;
			case 9:
				data.medianDetrend();
				break;
			default:
				break;
		}
		return new ECGView(this, data, index, getTitles()[index],  withLabels);
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

	public void removeAnnotation(int index) {
		HashMap<Integer, Undoable> changes = new HashMap<Integer, Undoable>();
		ArrayList<Annotation> annotations = model.getAnnotations();
		int j = 0;
		for(Annotation a : annotations) {
			changes.put(j, a);
			j++;
		}
		model.pushChange(new Change<HashMap<Integer, Undoable>, String>(
					changes,
					"Remove annotation"));
		model.removeAnnotation(index);
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

	public String getxAxisLabel() {
		return xAxisLabel;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}
}

