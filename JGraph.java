
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JComponent;

public class JGraph extends JComponent {
	private ArrayList<Point2D.Double> data;
	private double xmin, xmax, ymin, ymax;

	private int scrollOffset; // current point in scroll in pixels
	private double dScroll; // scroll rate - pixels/sec

	private long lastPaintTime;

	private double scalex, scaley;

	/**
	 * drawDataToPixels - converts data to pixels and draws the data as a polyline to the screen
	 * 
	 * @param g the graphics instance to draw to
	 */
	private void drawDataToPixels(Graphics g) {
		//calculate data -> pixel scale
		scalex = (xmax - xmin) / this.getWidth(); //TODO: fix this so scrolling works
		scaley = (ymax - ymin) / this.getHeight();

		int[] xpoints = new int[data.size()];
		int[] ypoints = new int[data.size()];

		int count = 0; 

		for(int i = 0; i < data.size(); i++) {
			int x = (int)(data.get(i).x / scalex) - scrollOffset; //TODO: fix this so scrolling works
			int y = (int)((ymax - data.get(i).y) / scaley); //because UI drawing is top-left, 
												  		 //rather than bottom left
			if(x >= 0 && x <= this.getWidth()) {
				xpoints[count] = x;
				ypoints[count] = y;
				count++;
			}
		}

		g.drawPolyline(xpoints, ypoints, count);
	}

	/**
	 * Constructor - initializes data
	 *
	 * @param data the data to set
	 */
	public JGraph(ArrayList<Point2D.Double> data) {
		this.data = data;
	}
	
	/**
	 * paint - paints the screen, overrides JComponent.paint(Graphics g)
	 * 
	 * @param g the graphics instance to draw to
	 */
	@Override
	public void paint(Graphics g) {
		long now = (new Date()).getTime();
		long dt = now - lastPaintTime;
		lastPaintTime = now; //for playback independent of cycle time

		drawDataToPixels(g);

		scrollOffset += (int)(dScroll * dt);
		if(scrollOffset >= this.getWidth()) { //TODO: fix this so scrolling works 
			scrollOffset = 0; 
		}
	}
}

