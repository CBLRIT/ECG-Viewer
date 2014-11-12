
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//import org.jfree.data.xy.DefaultXYDataset;

/**
 * class ECGModel - class for holding all datasets
 *
 * @author Dakota Williams
 */
public class ECGModel {
	//private DefaultXYDataset points; //x is time, y is value
	private ArrayList<ECGDataSet> points; //<channel<at time<time, value>>
	private final int tupleLength = 154;
	private final int actualSize = 130;
	private double sampleFreq = 0;

	private <T> void printArrayList(ArrayList<T[]> arr) {
		for(int i = 0; i < arr.size(); i++) {
			System.out.println(Arrays.toString(arr.get(i)));
		}
	}

	/**
	 * Constructor - initializes the model
	 */
	public ECGModel() {
		points = new ArrayList<ECGDataSet>();
	}

	/**
	 * clear - clears all datasets from the model
	 */
	public void clear() {
		points.clear();
	}

	/**
	 * toArray - creates an array representation of the model
	 *
	 * @return an array of arrays of arrays, nested like so:
	 *		[channel][x, y][samples], sizes [# channels][2][# samples]
	 */
	public double[][][] toArray() {
		double[][][] arr = new double[points.size()][2][points.get(0).size()];
		
		for(int i = 0; i < points.size(); i++) {
			for(int j = 0; j < points.get(0).size(); j++) {
				arr[i][0][j] = points.get(i).getAt(j)[0];
				arr[i][1][j] = points.get(i).getAt(j)[1];
			}
		}

		return arr;
	}

	/**
	 * subsetToArray - creates an array representation of data between to times
	 *
	 * @param start the time before the first sample in the subset
	 * @param end the time after the last sample in the subset
	 */
	public double[][][] subsetToArray(double start, double end) {
		double[][][] arr = new double[points.size()][2][points.get(0).subset(start, end).size()];
		
		for(int i = 0; i < points.size(); i++) {
			for(int j = 0; j < points.get(0).subset(start, end).size(); j++) {
				arr[i][0][j] = points.get(i).subset(start, end).getAt(j)[0];
				arr[i][1][j] = points.get(i).subset(start, end).getAt(j)[1];
			}
		}

		return arr;
	}

	/**
	 * writeDataMat - creates a MATLAB file with the data contained in this model
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeDataMat(String filename) 
			throws IOException {
		(new MatFile(filename)).write(this.toArray());
	}

	/**
	 * writeDataCSV - creates a Comma Separated Value file with the data 
	 *					contained in this model
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeDataCSV(String filename)
			throws IOException {
		(new CSVFile(filename)).write(this.toArray());
	}

	/**
	 * writeDataSubsetMat - writes a subset of the data to a MATLAB file
	 *
	 * @param filename the name of the file to write to
	 * @param start the time before the first element in the subset
	 * @param end the time after the last element in the subset
	 */
	public void writeDataSubsetMat(String filename, double start, double end) 
			throws IOException {
		(new MatFile(filename)).write(this.subsetToArray(start, end));
	}

	/**
	 * writeDataSubsetCSV - writes a subset of the data to a CSV file
	 *
	 * @param filename the name of the file to write to
	 * @param start the time before the first element in the subset
	 * @param end the time after the last element in the subset
	 */
	public void writeDataSubsetCSV(String filename, double start, double end)
			throws IOException {
		(new CSVFile(filename)).write(this.subsetToArray(start, end));
	}

	/**
	 * writeBadLeads - writes the bad lead numbers one per line to a file
	 *
	 * @param filename the file to write to
	 */
	public void writeBadLeads(String filename) 
			throws IOException {
		PrintWriter out = new PrintWriter(filename);

		for(int i = 0; i < points.size(); i++) {
			if(points.get(i).isBad()) {
				out.println(i);
			}
		}

		out.flush();
		out.close();
	}

	/**
	 * writeAnnotations - writes the all of the annotations to a file
	 * Format: <lead number>: (<annotation type>, <annotation location>), <more annotations> ...
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeAnnotations(String filename) 
			throws IOException {
		PrintWriter out = new PrintWriter(filename);

		for(int i = 0; i < points.size(); i++) {
			ArrayList<Annotation> annos = points.get(i).getAnnotations();
			if(annos.size() == 0) {
				continue;
			}
			out.print(i-1 + ": ");
			for(int j = 0; j < annos.size(); j++) {
				if(j != 0) {
					out.print(", ");
				}
				out.print(annos.get(j));
			}
			out.println();
		}

		out.flush();
		out.close();
	}

	/**
	 * readData - reads raw data in from a file
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 * @param filename the name of the file to read
	 */
	public void readData(String filename) 
			throws IOException, FileNotFoundException {
		ECGFile file = new ECGFile();
		ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Integer>>> raw
			= new ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Integer>>>();
		int retval = file.read(filename, tupleLength, raw);
		if(retval < 0) {
			return;
		}

		sampleFreq = file.finfo.sint;

	//	System.out.println(raw.get(raw.size()-1).getKey().toString() + ", " + raw.get(raw.size()-1).getValue().toString());
	//	return;

		for(int i = 0; i < raw.size(); i++) {
			for(int j = 0; j < actualSize; j++) {
				if(i == 0) {
					points.add(new ECGDataSet());
				}
				points.get(j).addTuple(raw.get(i).getKey(), 
									   (double)raw.get(i).getValue().get(j)*0.125);
			}
		}

		for(int i = 0; i < actualSize; i++) {
			ArrayList<Double> values = new ArrayList<Double>();
			for(int j = 0; j < points.get(i).size(); j++) {
				values.add(new Double(points.get(i).getAt(j)[1]));
			}

			Collections.sort(values);
			double median = 0;
			if(values.size() % 2 == 0) { //is even
				median = (values.get(values.size()/2-1) > values.get(values.size()/2)) ?
							values.get(values.size()/2-1) : values.get(values.size()/2);
			} else { //is odd
				median = values.get(values.size() / 2);
			}

	/*		if (i == 4) {
				printArrayList(points.get(i));
				System.out.println(values.toString());
				System.out.println(median);
			}
	*/


			for(int j = 0; j < points.get(i).size(); j++) {
				points.get(i).getAt(j)[1] -= median;
			}

	//		System.out.println(i + ": " + points.get(i).toArray()[1][0]);
		}
		
	//	printArrayList(points.get(4));
	//	System.out.println(Arrays.toString(points.get(4).get(0)));
	}

	/**
	 * getDataset - gets the dataset of channel number i
	 *
	 * @param i the index of the dataset
	 */
	public ECGDataSet getDataset(int i) {
		return points.get(i);
	}

	/**
	 * size - the number of datasets in this model
	 *
	 * @return the number of datasets in this model
	 */
	public int size() {
		return points.size();
	}

	/**
	 * setBad - sets or unsets the specified lead as a bad lead
	 *
	 * @param i the index of the lead
	 * @param isBad whether the lead should be set as bad
	 */
	public void setBad(int i, boolean isBad) {
		points.get(i).setBad(isBad);
	}

	/**
	 * isBad - gets whether the lead is bad or not
	 * 
	 * @param i the index of the lead
	 * @return the badness of the lead
	 */
	public boolean isBad(int i) {
		return points.get(i).isBad();
	}

	/**
	 * getSamplesPerSecond - gets the rate at which the samples were taken
	 *
	 * @return the number of samples taken per second
	 */
	public double getSamplesPerSecond() {
		return sampleFreq;
	}
}

