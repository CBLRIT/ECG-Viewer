
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Main {
	static final int dataSetPlacement[][] =  {
		{-1, -1}, //
		{-1, -1}, // 0-3 unhelpful
		{-1, -1}, //
		{-1, -1}, //
		{18, 0},	{22, 0}, 	{26, 0}, 	{30, 0}, //4-7
		{6, 6}, 	{10, 6}, 	{14, 6}, 	{18, 6}, 	{22, 6}, 	{26, 6}, 	{30, 6}, //8-14
		{6, 10}, 	{10, 10}, 	{14, 10}, 	{18, 10},	{22, 10},	{26, 10}, 	{30, 10},//15-21
		{6, 14},	{10, 14},	{14, 14},	{18, 14},	{22, 14},	{26, 14},	{30, 14},//22-28
		{6, 16},	{10, 16},	{14, 16},	{18, 16},	{22, 16},	{26, 16},	{30, 16},//29-35
		{6, 18},	{10, 18},	{14, 18},	{18, 18},	{22, 18},	{26, 18},	{30, 18},//36-42
		{6, 20},	{10, 20},	{14, 20},	{18, 20},	{22, 20},	{26, 20},	{30, 20},//43-49
		{6, 22},	{10, 22},	{14, 22},	{18, 22},	{22, 22},	{26, 22},	{30, 22},//50-56
		{6, 24},	{10, 24},	{14, 24},	{18, 24},	{22, 24},	{26, 24},	{30, 24},//57-63
		{6, 26},	{10, 26},	{14, 26},	{18, 26},	{22, 26},	{26, 26},	{30, 26},//64-70
		{18, 29},	{22, 29}, 	{26, 29}, 	{30, 29}, //71-74
		{18, 32},	{22, 32}, 	{26, 32}, 	{30, 32}, //75-78
		{6, 36},	{10, 36},	{14, 36},	{18, 36},	{22, 36},	{26, 36},	{30, 36},//79-85
		{6, 40},	{10, 40},	{14, 40},	{18, 40},	{22, 40},	{26, 40},	{30, 40},//86-92
		{2, 44},	{6, 44},	{10, 44},	{14, 44},	{18, 44},	{22, 44},	{26, 44},//93-
					{30, 44},															 //  -100
		{2, 48},	{6, 48},	{10, 48},	{14, 48},	{18, 48},	{22, 48},	{26, 48},//101-
					{30, 48},															 //	  -108
		{2, 52},	{6, 52},	{10, 52},	{14, 52},	{18, 52},	{22, 52},	{26, 52},//109-
					{30, 52},															 //	  -116
		{6, 56},	{10, 56},	{14, 56},	{18, 56},	{22, 56},	{26, 56},	{30, 56},//117-123
	};

	public static void main(String args[])
			throws Exception {
		JFrame main = new JFrame("This");
		main.setBounds(20, 20, 750, 1400);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		JMenuItem open = new JMenuItem("Open...");
		JMenuItem exit = new JMenuItem("Exit");
		menu.add(open);
		menu.add(exit);

		main.setJMenuBar(menubar);

		JPanel[] subPanels = new JPanel[30*56];
		JPanel mainPanel = new JPanel(new GridLayout(30, 56));

		ECGModel model = new ECGModel();
		model.readData("data/4916739e.dat");
	
		for(int i = 0; i < 124; i++) {
			System.out.println(i);
			if(i < 4) {
				subPanels[i] = new JPanel();
			} else {  
				ECGView graph = new ECGView(model.getDataset(i));
				subPanels[i] = graph.getPanel();
			}
			mainPanel.add(subPanels[i]);
		}

		main.add(mainPanel);

		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);

	}
}

