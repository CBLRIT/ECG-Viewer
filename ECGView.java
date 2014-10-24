
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
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

	private final ECGDataSet origData;
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
			500,
			500,
			true, //buffer
			false,//properties
			false,//copy
			false,//save
			false,//print
			true, //zoom
			false //tooltip
		);
		if(withLabels) {
			panel.addChartMouseListener(new ChartMouseListener() {
				public void chartMouseClicked(ChartMouseEvent event) {
//					ChartEntity ce = event.getEntity();
//					if(ce == null)
//						return;
					Point2D p = panel.translateScreenToJava2D(event.getTrigger().getPoint());
					double x = plot.getDomainAxis().java2DToValue(p.getX(),
																  panel.getScreenDataArea(),
																  plot.getDomainAxisEdge());
//					double y = plot.getRangeAxis().java2DToValue(p.getY(),
//																 panel.getScreenDataArea(),
//																 plot.getRangeAxisEdge());

					//origData.toggleAnnotation(((XYItemEntity)ce).getItem());
					plot.addDomainMarker(new ValueMarker(x));

				}

				public void chartMouseMoved(ChartMouseEvent event) {}
			});
		}
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

	public Object deepClone(boolean withLabels) {
		return new ECGView((ECGDataSet)origData.clone(), title, withLabels);
	}

	public void detrend() {
		origData.detrend();
		DefaultXYDataset dxyd = new DefaultXYDataset();
		dxyd.addSeries(1, origData.toArray());
		this.chart.getXYPlot().setDataset(dxyd);
	}

	public void applyFilter(int which, Number... params) {
		switch(which) {
			case 0:
				origData.sgolayfilt((int)params[0], (int)params[1], (int)params[2]);
				break;
			case 1:
				origData.highpassfilt((double)params[0]);
				break;
			case 2:
				origData.lowpassfilt((double)params[0]);
				break;
			default:
				return;
		}
		DefaultXYDataset dxyd = new DefaultXYDataset();
		dxyd.addSeries(1, origData.toArray());
		this.chart.getXYPlot().setDataset(dxyd);
	}
}

