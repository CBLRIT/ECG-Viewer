
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

	private final int defaultWidth = 100;
	private final int defaultHeight = 100;

	private ECGDataSet origData;
	private String title;

	public ECGView(ECGDataSet data, String title, boolean withLabels) {
		origData = data;
		this.title = title;

		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, data.toArray());
		
		this.xaxis = new NumberAxis("Time (msec)");
		if(!withLabels) {
			this.xaxis.setVisible(false);
		}
		this.yaxis = new NumberAxis("Potential (mV)");
		if(!withLabels) {
			this.yaxis.setVisible(false);
		}
		this.renderer = new XYLineAndShapeRenderer(true, false);
		this.plot = new XYPlot(dataset, xaxis, yaxis, renderer);
		this.chart = new JFreeChart(title, plot);
		this.chart.removeLegend();
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

	public void setBad(boolean b) {
		origData.setBad(b);
	}

	public boolean isBad() {
		return origData.isBad();
	}

	public ChartPanel getPanel() {
		return this.panel;
	}

	public Object clone(boolean withLabels) {
		return new ECGView(origData, title, withLabels);
	}
}

