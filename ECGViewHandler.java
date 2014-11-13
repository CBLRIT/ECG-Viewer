
public class ECGViewHandler {
	private static final ArrayList<ECGView> graphs = new ArrayList<ECGView>();
	private ECGModel model;	

	public ECGViewHandler(ECGModel model) {
		this.model = model;

		for(int i = 0; i < model.size(); i++) {
			graphs.add(new ECGView(model.getDataset(i), ""+(i-1), false));
		}
	}
}
