
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ECGFile - opens and reads a file with ECG data
 * @author Dakota Williams
 */
public abstract class ECGFile {
	public abstract int read(String fileName,
					ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points);
					
	public abstract double getSampleInterval();

	public abstract int[][] getLayout();

	public abstract String[] getTitles();

	public abstract String getExtension();

	public int getNorth(int index) {
		int[][] leads = this.getLayout();

		int currX = leads[index][1];
		int currY = leads[index][0];
		int ret = -1;
		for(int i = 0; i < leads.length; i++) {
			if(leads[i][1] == currX && leads[i][0] == currY-1) {
				ret = i;
			}
		}
		
		return ret;
	}

	public int getSouth(int index) {
		int[][] leads = this.getLayout();

		int currX = leads[index][1];
		int currY = leads[index][0];
		int ret = -1;
		for(int i = 0; i < leads.length; i++) {
			if(leads[i][1] == currX && leads[i][0] == currY+1) {
				ret = i;
			}
		}
		
		return ret;
	}

	public int getEast(int index) {
		int[][] leads = this.getLayout();

		int currX = leads[index][1];
		int currY = leads[index][0];
		int ret = -1;
		int min = Integer.MAX_VALUE;
		int max = -1;
		int minInd = Integer.MAX_VALUE;

		for(int i = 0; i < leads.length; i++) {
			if(leads[i][0] == currY && leads[i][1] == currX+1) {
				ret = i;
			}
			if(leads[i][0] == currY && leads[i][1] < min) {
				min = leads[i][1];
				minInd = i;
			}
			if(leads[i][0] == currY && leads[i][1] > max) {
				max = leads[i][1];
			}
		}
		if(currX == max) { //wrap around
			return minInd;
		}
		return ret;
	}

	public int getWest(int index) {
		int[][] leads = this.getLayout();

		int currX = leads[index][1];
		int currY = leads[index][0];
		int ret = -1;
		int max = -1;
		int maxInd = -1;

		for(int i = 0; i < leads.length; i++) {
			if(leads[i][0] == currY && leads[i][1] == currX-1) {
				ret = i;
			}
			if(leads[i][0] == currY && leads[i][1] > max) {
				max = leads[i][1];
				maxInd = i;
			}
		}
		if(currX == 0) { //wrap around
			return maxInd;
		}
		return ret;
	}
}

