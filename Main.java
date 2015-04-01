
public class Main {
	public static void main(String args[])
			throws Exception { //TODO: fix this
		Settings.makeDefaultSettings();
		new MainFrame(new ECGViewHandler(new ECGModel()));
	}
}

