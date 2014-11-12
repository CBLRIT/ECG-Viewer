
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.Rectangle;
import java.awt.Shape;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * @deprecated
 * class ECGRenderer - defines a custom renderer for charts
 *
 * @author Dakota Williams
 */
public class ECGRenderer extends XYLineAndShapeRenderer {
	private ECGDataSet dataset;

	public ECGRenderer(ECGDataSet set) {
		dataset = set;
	}

	public Shape getItemShape(int row, int column) {
		if(dataset.isAnnotated(column)) {
			return new Ellipse2D.Double(-2, -2, 5, 5);
		} else {
			return new Rectangle(-2,-2,2,2);
		}
	}
}

