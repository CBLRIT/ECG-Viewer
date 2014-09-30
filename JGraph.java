
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JComponent;

public class JGraph extends JComponent {
	private ArrayList<Point2D.Double> data;

	private int xpos, ypos;
	private double scale;
	
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
		int[] xpoints = new int[data.size()];
		int[] ypoints = new int[data.size()];

		int count = 0;

		for(int i = 0; i < data.size(); i++) {
			xpoints[count] = (int)((double)(data.get(i).x+xpos)*scale);
			ypoints[count] = this.getHeight() + (int)((double)(-data.get(i).y + ypos)*scale);
			count++;
		}

		g.drawPolyline(xpoints, ypoints, count);
	}

	/**
	 * Constructor - initializes data
	 *
	 * @param data the data to set
	 */
	public JGraph() {
		this.data = new ArrayList<Point2D.Double>();
	}

	/**
	 * Constructor - initializes data
	 *
	 * @param data the data to set
	 */
	public JGraph(ArrayList<Point2D.Double> data) {
		this.data = data;
		xpos = 0;
		ypos = 0;
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
		g.setClip(xpos, ypos, this.getWidth(), this.getHeight());

		xpos += (int)(dScroll * dt);
		if(xpos >= this.getWidth()) {
			xpos = 0; 
		}
	}
}

