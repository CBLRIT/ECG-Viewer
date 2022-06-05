
public class Main {
	static MainFrame mainframe;
	public static void main(String args[])
			throws Exception { //TODO: fix this
		Settings.load();
		mainframe = new MainFrame(new ECGViewHandler(new ECGModel()));
	}

	public static void setProgressBar(String name, int val) {
		mainframe.setProgressBar(name, val, -1);
	}

	public static void setProgressBar(String name, int val, float timeRemaining) {
		mainframe.setProgressBar(name, val, timeRemaining);
	}
}

