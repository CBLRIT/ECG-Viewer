import java.awt.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.plugin.PlugIn;

/** This a prototype ImageJ plugin. */
public class Red_And_Blue implements PlugIn {

	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[])ip.getPixels();
		int i = 0;
		for (int y = 0; y < h; y++) {
			int red = (y * 255) / (h - 1);
			for (int x = 0; x < w; x++) {
				int blue = (x * 255) / (w - 1);
				pixels[i++] = (255 << 24) | (red << 16) | blue;
			}
		}
		new ImagePlus("Red and Blue", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }

}

