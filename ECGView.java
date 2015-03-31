
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
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

/**
 * class ECGView - a class that handles rendering of the charts
 *
 * @author Dakota Williams
 */
public class ECGView {
	private ChartPanel panel;
	private JFreeChart chart;
	private XYPlot plot;
	private NumberAxis xaxis;
	private NumberAxis yaxis;
	private XYLineAndShapeRenderer renderer;

	private final int defaultWidth = 200;
	private final int defaultHeight = 200;

	private final ECGViewHandler handler;
	private int index;
	private ECGDataSet data;
	private String title;

	private final ECGView thisView = this;

	private boolean canPlace = false;

	private boolean labels;

	/**
	 * Constructor - initializes the view
	 *
	 * @param data the dataset to display
	 * @param title the title of the chart
	 * @param withLabels whether labels on the chart should be shown
	 */
	public ECGView(ECGViewHandler handler, 
				   ECGDataSet data, 
				   int index,
				   String title, 
				   boolean withLabels) {
		this.handler = handler;
		this.title = title;
		this.index = index;
		this.data = data;
		this.labels = withLabels;

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
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.panel = new ChartPanel(
			chart,
			defaultWidth,
			defaultHeight,
			0,
			0,
			screenSize.width,
			screenSize.height,
			true, //buffer
			false,//properties
			false,//copy
			false,//save
			false,//print
			true, //zoom
			false //tooltip
		);
		if(!withLabels) {
			this.panel.setPopupMenu(null);
		}

		this.setBackground(!this.isBad() ? 
						   UIManager.getColor("Panel.background") : 
						   new Color(233, 174, 174));

		this.redrawAnnotations();

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
					if(canPlace) {
						thisView.handler.addAnnotation(
										thisView.handler.getSelectedAnnotationType(), x);
						plot.addDomainMarker(new ValueMarker(x, 
												 thisView.handler.getSelectedAnnotationColor(), 
												 new BasicStroke()));
					}
				}

				public void chartMouseMoved(ChartMouseEvent event) {}
			});
		}

//		System.out.println(origData.toArray()[1][0]);
	}

	/**
	 * setCanPlace - sets whether clicking the chart will place annotations
	 *
	 * @param b the value of the flag
	 */
	public void setCanPlace(boolean b) {
		canPlace = b;
	}

	/**
	 * clearAnnotations - clears annotations from the view
	 */
	public void clearAnnotations() {
		plot.clearDomainMarkers();
		this.handler.clearAnnotations();
	}

	/**
	 * redrawAnnotations - refreshes the displayed annotations
	 */
	public void redrawAnnotations() {
		plot.clearDomainMarkers();
		for(int i = 0; i < handler.getAnnotations().size(); i++) {
			plot.addDomainMarker(
				new ValueMarker(handler.getAnnotations().get(i).getLoc(), 
			 	this.handler.getAnnotationColor(
					handler.getAnnotations().get(i).getType()), 
				 	new BasicStroke()));
		}
	}

	/**
	 * setBackground - sets the background color of the panel
	 *
	 * @param c the color to set the panel to
	 */
	public void setBackground(Color c) {
		this.chart.setBackgroundPaint(c); this.panel.revalidate(); } /**
	 * setBad - sets whether the data should be marked as bad
	 *
	 * @param b the value of the flag
	 */
	public void setBad(boolean b) {
		handler.setBad(index, b);
	}

	/**
	 * isBad - gets whether the data being displayed is marked as bad
	 *
	 * @return true if bad
	 */
	public boolean isBad() {
		return handler.isBad(index);
	}

	/**
	 * getPanel - gets the Swing panel to display
	 *
	 * @return a JFreeChart ChartPanel
	 */
	public ChartPanel getPanel() {
		return this.panel;
	}

	/**
	 * clone - copies the view
	 *
	 * @param withLabels should the new view have labels on the chart?
	 */
	public Object clone(boolean withLabels) {
		return new ECGView(handler, data, index, title, withLabels);
	}

	/**
	 * revalidate - redraws the chart
	 */
	public void revalidate() {
		DefaultXYDataset dxyd = new DefaultXYDataset();
		dxyd.addSeries(1, data.toArray());
		this.chart.getXYPlot().setDataset(dxyd);
		this.chart.getXYPlot().setDataset(this.chart.getXYPlot().getDataset());
		this.chart.fireChartChanged();
		this.panel.revalidate();
	}

	/**
	 * update - updates data inside of the ECGView
	 */
	public void update() {
		ECGView newData = handler.getView(this.index, this.labels);
		this.data = newData.data;
		this.revalidate();
	}

	/**
	 * setViewingDomain - scales the view between two x values
	 *
	 * @param start the left value
	 * @param end the right value
	 */
	public void setViewingDomain(double start, double end) {
		this.plot.getDomainAxis().setAutoRange(true);
		this.plot.getDomainAxis().setRange(start, end);
	}

	/**
	 * filter - filters the current view
	 *
	 * @param filterid the type of filter to apply
	 * @param params the arguments to the filter
	 */
	public void filter(int filterid, Number[] params) {
		this.data = handler.shallowFilter(index, filterid, params, labels).data;
	}
}

