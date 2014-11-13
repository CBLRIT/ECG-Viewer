
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import org.jfree.chart.plot.XYPlot;

public class Main {
	private static int currAnnoType = 0;
	private static HashMap<Integer, Color> annoColors = new HashMap<Integer, Color>();

	private static JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private static JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());

	private static void loadFile(String filename) {
		graphs.clear();
		model.clear();

		try {
			model.readData(filename);
		} catch (IOException e) {
			System.err.println("Could not open: " + filename);
			return;
		}
		
		for(int i = 5; i < 125; i++) {
		//	System.out.print(i + ": ");
			final ECGView graph = new ECGView(model.getDataset(i), ""+(i-1), false);
			graphs.add(graph);
			int index = dataSetPlacement[i][0]*xnum + dataSetPlacement[i][1];
			graph.getPanel().setPopupMenu(null); //turn off context menu for chart
			subPanels[index].removeAll();
			subPanels[index].add(graph.getPanel());
			final int count = i-1;
			graph.getPanel().addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					ChartFrame cf = new ChartFrame(graph, ""+count);
					cf.addWindowListener(new WindowListener() {
						public void windowClosing(WindowEvent e) {
							graph.setBackground(!graph.isBad() ? 
												UIManager.getColor("Panel.background") : 
												new Color(233, 174, 174));
							graph.revalidate();
						}

						//don't care
						public void windowOpened(WindowEvent e) {}
						public void windowIconified(WindowEvent e) {}
						public void windowDeiconified(WindowEvent e) {}
						public void windowDeactivated(WindowEvent e) {}
						public void windowActivated(WindowEvent e) {}
						public void windowClosed(WindowEvent e) {}
					});
				}

				//don't care
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});
		}
		
		lenText.setValue(Math.ceil((double)model.getDataset(42).size()
							*model.getSamplesPerSecond()));
	}

	public static int getSelectedAnnotationType() {
		return currAnnoType;
	}

	public static void setSelectedAnnotationType(int type) {
		currAnnoType = type;
	}

	public static Color getAnnotationColor(int type) {
		return annoColors.get(type);
	}

	public static Color getSelectedAnnotationColor() {
		return annoColors.get(currAnnoType);
	}

	public static void main(String args[])
			throws Exception { //TODO: fix this
		annoColors.put(0, Color.BLACK);
		annoColors.put(1, Color.ORANGE);
		annoColors.put(2, Color.GREEN);
		annoColors.put(3, Color.BLUE);


	}
}

