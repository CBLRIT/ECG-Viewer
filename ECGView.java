
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.UIManager;
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

	private boolean trim;
	private final ECGView thisView = this;

	private boolean canPlace = false;

	public ECGView(ECGDataSet data, String title, boolean withLabels) {
		trim = false;

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
		if(withLabels) {
			this.plot.setDomainPannable(true);
			this.plot.setRangePannable(true);
		}
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

		this.setBackground(!this.isBad() ? 
						   UIManager.getColor("Panel.background") : 
						   new Color(233, 174, 174));

		for(int i = 0; i < origData.getAnnotations().size(); i++) {
			plot.addDomainMarker(new ValueMarker(origData.getAnnotations().get(i).getLoc(), 
												 Main.getAnnotationColor(
												 	origData.getAnnotations().get(i).getType()), 
												 new BasicStroke()));
		}

		if(withLabels) {
			panel.addChartMouseListener(new ChartMouseListener() {
				public void chartMouseClicked(ChartMouseEvent event) {
					ChartEntity ce = event.getEntity();
					if(ce == null)
						return;
					Point2D p = panel.translateScreenToJava2D(event.getTrigger().getPoint());
					double x = plot.getDomainAxis().java2DToValue(p.getX(),
																  panel.getScreenDataArea(),
																  plot.getDomainAxisEdge());
					if(trim) {
						thisView.setTrim(false);
						origData.trimAnnotations(x);
						thisView.revalidate();
					} else if(canPlace) {
						origData.addAnnotation(Main.getSelectedAnnotationType(), x);
						plot.addDomainMarker(new ValueMarker(x, 
															 Main.getSelectedAnnotationColor(), 
															 new BasicStroke()));
					}
				}

				public void chartMouseMoved(ChartMouseEvent event) {}
			});
		}

//		System.out.println(origData.toArray()[1][0]);
	}

	public void setCanPlace(boolean b) {
		canPlace = b;
	}

	public void setTrim(boolean b) {
		trim = b;
	}

	public void clearAnnotations() {
		plot.clearDomainMarkers();
		this.origData.clearAnnotations();
	}

	public void setBackground(Color c) {
		this.chart.setBackgroundPaint(c);
		this.panel.revalidate();
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

	public ECGDataSet getData() {
		return this.origData;
	}

	public Object clone(boolean withLabels) {
		return new ECGView(origData, title, withLabels);
	}

	public Object deepClone(boolean withLabels) {
		return new ECGView((ECGDataSet)origData.clone(), title, withLabels);
	}

	public void revalidate() {
		DefaultXYDataset dxyd = new DefaultXYDataset();
		dxyd.addSeries(1, origData.toArray());
		this.chart.getXYPlot().setDataset(dxyd);
		this.chart.getXYPlot().setDataset(this.chart.getXYPlot().getDataset());
		this.chart.fireChartChanged();
		this.panel.revalidate();
	}

	public void detrend(int degree) {
		origData.detrend(degree);
		revalidate();
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
			case 3: 
				origData.highpassfftfilt((double)params[0], 0);
				break;
			default:
				return;
		}
		revalidate();
	}
}

