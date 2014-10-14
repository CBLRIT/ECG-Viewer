
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Main {
	private static final int dataSetPlacement[][] =  {
		{-1, -1}, //
		{-1, -1}, // 0-3 unhelpful
		{-1, -1}, //
		{-1, -1}, //
		{4, 0},		{5, 0}, 	{6, 0}, 	{7, 0}, //4-7
		{1, 1}, 	{2, 1}, 	{3, 1}, 	{4, 1}, 	{5, 1}, 	{6, 1}, 	{7, 1},//8-14
		{1, 2}, 	{2, 2}, 	{3, 2}, 	{4, 2},		{5, 2},		{6, 2}, 	{7, 2},//15-21
		{1, 3},		{2, 3},		{3, 3},		{4, 3},		{5, 3},		{6, 3},		{7, 3},//22-28
		{1, 4},		{2, 4},		{3, 4},		{4, 4},		{5, 4},		{6, 4},		{7, 4},//29-35
		{1, 5},		{2, 5},		{3, 5},		{4, 5},		{5, 5},		{6, 5},		{7, 5},//36-42
		{1, 6},		{2, 6},		{3, 6},		{4, 6},		{5, 6},		{6, 6},		{7, 6},//43-49
		{1, 7},		{2, 7},		{3, 7},		{4, 7},		{5, 7},		{6, 7},		{7, 7},//50-56
		{1, 8},		{2, 8},		{3, 8},		{4, 8},		{5, 8},		{6, 8},		{7, 8},//57-63
		{1, 9},		{2, 9},		{3, 9},		{4, 9},		{5, 9},		{6, 9},		{7, 9},//64-70
		{4, 10},	{5, 10}, 	{6, 10}, 	{7, 10}, //71-74
		{4, 11},	{5, 11}, 	{6, 11}, 	{7, 11}, //75-78
		{1, 12},	{2, 12},	{3, 12},	{4, 12},	{5, 12},	{6, 12},	{7, 12},//79-85
		{1, 13},	{2, 13},	{3, 13},	{4, 13},	{5, 13},	{6, 13},	{7, 13},//86-92
		{0, 14},	{1, 14},	{2, 14},	{3, 14},	{4, 14},	{5, 14},	{6, 14},//93-
					{7, 14},															//  -100
		{0, 15},	{1, 15},	{2, 15},	{3, 15},	{4, 15},	{5, 15},	{6, 15},//101-
					{7, 15},															//	 -108
		{0, 16},	{1, 16},	{2, 16},	{3, 16},	{4, 16},	{5, 16},	{6, 16},//109-
					{7, 16},															//	 -116
		{1, 17},	{2, 17},	{3, 17},	{4, 17},	{5, 17},	{6, 17},	{7, 17},//117-123
	};

	private static int ynum = 8;
	private static int xnum = 18;

	private static final JFrame main = new JFrame("This");
	private static final JPanel[] subPanels = new JPanel[xnum*ynum];

	private static void loadFile(String filename) {
		ECGModel model = new ECGModel();
		try {
			model.readData(filename);
		} catch (IOException e) {
			System.err.println("Could not open: " + filename);
			return;
		}
		
		for(int i = 4; i < 124; i++) {
		//	System.out.println(i);
			ECGView graph = new ECGView(model.getDataset(i));
			int index = dataSetPlacement[i][0]*xnum + dataSetPlacement[i][1];
			subPanels[index].removeAll();
			subPanels[index].add(graph.getPanel());
		}
	}

	public static void main(String args[])
			throws Exception {
		main.setBounds(20, 20, 1400, 750);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(main);
				if(ret == JFileChooser.APPROVE_OPTION) {
					loadFile(fc.getSelectedFile().getAbsolutePath());
					main.revalidate();
				}
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.setVisible(false);
				main.dispose();
			}
		});
		menu.add(open);
		menu.add(exit);

		main.setJMenuBar(menubar);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(ynum, xnum));

		for(int i = 0; i < xnum*ynum; i++) {
			subPanels[i] = new JPanel();
			mainPanel.add(subPanels[i]);
		}
		
		JScrollPane scrollMain = new JScrollPane(mainPanel);
		main.add(scrollMain);
		//main.add(mainPanel);

		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);

	}
}

