
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class ECGView {
	private ChartPanel panel;
	private JFreeChart chart;
	private XYPlot plot;
	private NumberAxis xaxis;
	private NumberAxis yaxis;
	private XYLineAndShapeRenderer renderer;

	public ECGView(JFrame frame, double[][][] data) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, data[12]);
		
		xaxis = new NumberAxis("x");
		yaxis = new NumberAxis("y");
		renderer = new XYLineAndShapeRenderer(true, false);
		plot = new XYPlot(dataset, xaxis, yaxis, renderer);
		chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);

		frame.add(panel);
	}
}

