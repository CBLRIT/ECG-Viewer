
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Main {
	public static void main(String args[])
			throws Exception {
	/*	JFrame main = new JFrame("This");
		main.setBounds(20, 20, 500, 500);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		JMenuItem open = new JMenuItem("Open...");
		JMenuItem exit = new JMenuItem("Exit");
		menu.add(open);
		menu.add(exit);

		main.setJMenuBar(menubar);

		ECGModel model = new ECGModel();
		model.readData("data/4916739e.dat");
		ECGView graph = new ECGView(main, model.getDataset());

		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
*/
/*		ECGModel model = new ECGModel();

		model.readData("data/4916739e.dat");
*/

		ECGFile file = new ECGFile();
		file.read("data/4916739e.dat", 154);
	}
}

