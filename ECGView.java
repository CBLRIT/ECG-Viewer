
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class ECGView extends JComponent {
	private XYPlot plot;
	private NumberAxis xaxis;
	private NumberAxis yaxis;
	private XYLineAndShapeRenderer renderer;

	public ECGView(XYDataset data) {
		xaxis = new NumberAxis("x");
		yaxis = new NumberAxis("y");
		renderer = new XYLineAndShapeRenderer(true, false);
		plot = new XYPlot(data, xaxis, yaxis, renderer);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		plot.draw(g2d, this.getBounds(null), null, null, null);
	}
}

