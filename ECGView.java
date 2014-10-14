
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
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

	private final int defaultWidth = 200;
	private final int defaultHeight = 200;

	public ECGView(double[][] data) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, data);
		
		this.xaxis = new NumberAxis("x");
		this.yaxis = new NumberAxis("y");
		this.renderer = new XYLineAndShapeRenderer(true, false);
		this.plot = new XYPlot(dataset, xaxis, yaxis, renderer);
		this.chart = new JFreeChart(plot);
		this.panel = new ChartPanel(
			chart,
			defaultWidth,
			defaultHeight,
			100,
			100,
			300,
			300,
			true, //buffer
			false,//properties
			false,//copy
			false,//save
			false,//print
			true, //zoom
			false //tooltip
		);
	}

	public JPanel getPanel() {
		return this.panel;
	}
}

