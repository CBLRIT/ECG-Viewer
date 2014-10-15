
import javax.swing.JFrame;

public class ChartFrame extends JFrame {
	private ECGView view;

	public ChartFrame(ECGView v, String title) {
		super(title);
		setBounds(0, 0, 500, 500);

		view = v;
		add(view.getPanel());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}

