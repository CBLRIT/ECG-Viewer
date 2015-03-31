
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
import java.util.HashSet;
import java.util.Iterator;

/**
 * class ECGModel - class for holding all datasets
 *
 * @author Dakota Williams
 */
public class ECGModel {
	private ECGDataSet[] ignoredLeads;
	private ECGDataSet[] points;
	private int layout[][];
	private int dataOffset = 0;
	private int actualSize = 130;
	private double sampleFreq = 0;
	private HashSet<Annotation> annotations;	
	private ECGFileManager filePlugins;

	private <T> void printArrayList(ArrayList<T[]> arr) {
		for(int i = 0; i < arr.size(); i++) {
			System.out.println(Arrays.toString(arr.get(i)));
		}
	}

	/**
	 * Constructor - initializes the model
	 */
	public ECGModel() {
		ignoredLeads = new ECGDataSet[]{};
		points = new ECGDataSet[]{};
		layout = new int[][]{};
		annotations = new HashSet<Annotation>();
		filePlugins = new ECGFileManager();
		filePlugins.load();
	}

	/**
	 * clone - deep copy of the model
	 *
	 * @return a copy of the model
	 */
	public ECGModel clone() {
		ECGModel newModel = new ECGModel();

		newModel.actualSize = this.actualSize;
		newModel.sampleFreq = this.sampleFreq;
		newModel.ignoredLeads = new ECGDataSet[this.ignoredLeads.length];
		newModel.points = new ECGDataSet[this.points.length];
		newModel.layout = new int[this.layout.length][this.layout[0].length];

		for(int i = 0; i < this.ignoredLeads.length; i++) {
			newModel.ignoredLeads[i] = (ECGDataSet)this.ignoredLeads[i].clone();
		}
		for(int i = 0; i < this.points.length; i++) {
			newModel.points[i] = (ECGDataSet)this.points[i].clone();
		}
		for(Iterator<Annotation> i = this.annotations.iterator(); i.hasNext(); ) {
			newModel.annotations.add(new Annotation(i.next()));
		}
		for(int i = 0; i < this.layout.length; i++) {
			System.arraycopy(this.layout[i], 0, newModel.layout[i], 0, this.layout[0].length);
		}

		return newModel;
	}

	/**
	 * clear - clears all datasets from the model
	 */
	public void clear() {
		ignoredLeads = new ECGDataSet[]{};
		points = new ECGDataSet[]{};
		layout = new int[][]{};
		dataOffset = 0;
	}

	/**
	 * getAnnotations - gets a list of all annotations
	 *
	 * @return an ArrayList of all Annotations of this dataset
	 */
	public ArrayList<Annotation> getAnnotations() {
		return new ArrayList<Annotation>(annotations);
	}

	/**
	 * addAnnotation - adds an annotation to the dataset
	 *
	 * @param type an integer representing the type of annotation
	 * @param i the time at which the annotation should be located
	 */
	public void addAnnotation(int type, double i) {
		annotations.add(new Annotation(type, i));
	}

	/**
	 * setAnnotations - sets the annotations of the dataset
	 *
	 * @param annos the annotations
	 */
	public void setAnnotations(ArrayList<Annotation> annos) {
		annotations = new HashSet<Annotation>(annos);
	}

	/** 
	 * clearAnnotations - removes all annotations associated with this dataset
	 */
	public void clearAnnotations() {
		annotations.clear();
	}

	/**
	 * isAnnotated - removes an annotation at the specified location
	 *
	 * @param i the location to remove
	 */
	public boolean isAnnotated(double i) {
		return annotations.contains(i);
	}

	/**
	 * toArray - creates an array representation of the model
	 *
	 * @return an array of arrays of arrays, nested like so:
	 *		[channel][x, y][samples], sizes [# channels][2][# samples]
	 */
	public double[][][] toArray() {
		double[][][] arr = new double[points.length][2][points[0].size()];
		
		for(int i = 0; i < points.length; i++) {
			for(int j = 0; j < points[0].size(); j++) {
				arr[i][0][j] = points[i].getAt(j)[0];
				arr[i][1][j] = points[i].getAt(j)[1];
			}
		}

		return arr;
	}

	/**
	 * subsetToArray - creates an array representation of data between to times *
	 * @param start the time before the first sample in the subset
	 * @param end the time after the last sample in the subset
	 */
	@Deprecated
	public double[][][] subsetToArray(double start, double end) {
		long s = System.currentTimeMillis();
		double[][][] arr = new double[points.length][2][points[0].subset(start, end).size()];
		
		for(int i = 0; i < points.length; i++) {
			for(int j = 0; j < points[0].subset(start, end).size(); j++) {
				arr[i][0][j] = points[i].subset(start, end).getAt(j)[0];
				arr[i][1][j] = points[i].subset(start, end).getAt(j)[1];
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("Subset time (ms): " + (e - s));
		return arr;
	}

	/**
	 * writeDataMat - creates a MATLAB file with the data contained in this model
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeDataMat(String filename) 
			throws IOException {
		(new MatFile(filename)).write(points);
	}

	/**
	 * writeDataCSV - creates a Tab Separated Value file with the data 
	 *					contained in this model
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeDataCSV(String filename)
			throws IOException {
		(new CSVFile(filename)).write(points);
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
		(new MatFile(filename)).writeSubset(points, 
											points[0].indexBefore(start), 
											points[0].indexBefore(end));
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
		(new CSVFile(filename)).writeSubset(points, 
											points[0].indexBefore(start), 
											points[0].indexBefore(end));
	}

	/**
	 * writeBadLeads - writes the bad lead numbers one per line to a file
	 *
	 * @param filename the file to write to
	 */
	public void writeBadLeads(String filename) 
			throws IOException {
		PrintWriter out = new PrintWriter(filename);

		for(int i = 0; i < points.length; i++) {
			if(points[i].isBad()) {
				out.println((i+4));
			}
		}

		out.flush();
		out.close();
	}

	/**
	 * writeAnnotations - writes the all of the annotations to a file
	 * Format: <lead number> <annotation type> <annotation location>
	 *
	 * @param filename the name of the file to write to
	 */
	public void writeAnnotations(String filename) 
			throws IOException {
		PrintWriter out = new PrintWriter(filename);

		ArrayList<Annotation> annos = this.getAnnotations();
		for(int j = 0; j < annos.size(); j++) {
			out.println(annos.get(j));
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
		this.clear();

		ECGFile file = filePlugins.getECGFile(filename);
		if(file==null) {
			throw new IOException("Not a supported file extension");
		}

		ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> raw
			= new ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>>();
		int retval = file.read(filename, raw);
		if(retval != 0) {
			return;
		}
		int tempLayout[][] = file.getLayout();
		ArrayList<int[]> found = new ArrayList<int[]>();
		int numGoodLeads = 0;
		int numIgnoreLeads = 0;
		boolean stop = false;
		for(int i = 0; i < tempLayout.length; i++) {
			if(tempLayout[i][0] < 0 || tempLayout[i][1] < 0) {
				numIgnoreLeads++;
				if(!stop) {
					dataOffset++;
				}
				continue;
			}
			numGoodLeads++;
			found.add(tempLayout[i]);
		}

		layout = new int[found.size()][2];
		for(int i = 0; i < found.size(); i++) {
			layout[i] = new int[]{found.get(i)[0], found.get(i)[1]};
		}
		ignoredLeads = new ECGDataSet[numIgnoreLeads];
		points = new ECGDataSet[numGoodLeads];

		actualSize = tempLayout.length;
		sampleFreq = file.getSampleInterval();

		for(int i = 0; i < raw.size(); i++) {
			int igoff = 0;
			int normoff = 0;
			for(int j = 0; j < actualSize; j++) {
				if(tempLayout[j][0] < 0 || tempLayout[j][1] < 0) {
					normoff++;
					if(i == 0) {
						ignoredLeads[j-igoff] = new ECGDataSet();
					}
					ignoredLeads[j-igoff].addTuple(raw.get(i).getKey(), 
										   (double)raw.get(i).getValue().get(j));
				} else {
					igoff++;
					if(i == 0) {
						points[j-normoff] = new ECGDataSet();
					}
					points[j-normoff].addTuple(raw.get(i).getKey(), 
										   (double)raw.get(i).getValue().get(j));
				}

			}
		}

		for(int i = 0; i < points.length; i++) {
			ArrayList<Double> values = new ArrayList<Double>();
			for(int j = 0; j < points[i].size(); j++) {
				values.add(new Double(points[i].getAt(j)[1]));
			}

			Collections.sort(values);
			double median = 0;
			if(values.size() % 2 == 0) { //is even
				median = (values.get(values.size()/2-1) > values.get(values.size()/2)) ?
							values.get(values.size()/2-1) : values.get(values.size()/2);
			} else { //is odd
				median = values.get(values.size() / 2);
			}

			for(int j = 0; j < points[i].size(); j++) {
				points[i].getAt(j)[1] -= median;
			}
		}
	}

	/**
	 * readSubsetData - reads a subset of raw data in from a file
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 * @param filename the name of the file to read
	 * @param start the start time to read from
	 * @param end the end time to read to
	 */
	public void readSubsetData(String filename, double start, double end) 
			throws IOException, FileNotFoundException {
		readData(filename);
		for(int i = 0; i < points.length; i++) {
			points[i] = points[i].subset(start, end);
		}
	}

	/**
	 * getDataset - gets the dataset of channel number i
	 *
	 * @param i the index of the dataset
	 */
	public ECGDataSet getDataset(int i) {
		return points[i];
	}

	/**
	 * size - the number of datasets in this model
	 *
	 * @return the number of datasets in this model
	 */
	public int size() {
		return points.length;
	}

	/**
	 * setBad - sets or unsets the specified lead as a bad lead
	 *
	 * @param i the index of the lead
	 * @param isBad whether the lead should be set as bad
	 */
	public void setBad(int i, boolean isBad) {
		points[i].setBad(isBad);
	}

	/**
	 * isBad - gets whether the lead is bad or not
	 * 
	 * @param i the index of the lead
	 * @return the badness of the lead
	 */
	public boolean isBad(int i) {
		return points[i].isBad();
	}

	/**
	 * getSamplesPerSecond - gets the rate at which the samples were taken
	 *
	 * @return the number of samples taken per second
	 */
	public double getSamplesPerSecond() {
		return sampleFreq;
	}

	/**
	 * getLayout - gets the layout of leads specified by the file
	 * @return an array of coordinates
	 */
	public int[][] getLayout() {
		return layout;
	}

	/**
	 * getOffset - the number of leads before the data leads start
	 * @return the offset
	 */
	public int getOffset() {
		return dataOffset;
	}

	/**
	 * extractFeatures - finds the peak of the R waves in a signal
	 * @param index the index of the lead to use
	 */
	public void extractFeatures(int index) {
		ECGDataSet lead = points[index];
		final double range = 0.25; //50% of it's value north and south
		java.util.PriorityQueue<Double[]> maxima = //ordered by >, of 2-tuples: time and value
				new java.util.PriorityQueue<Double[]>(32, 
			 		new java.util.Comparator<Double[]>() {
			public int compare(Double[] o1, Double[] o2) { //compares values
				return -Double.compare(o1[1], o2[1]); //get the largest value
			}
			public boolean equals(Object obj) {
				return super.equals(obj);
			}
		});

		maxima.add(new Double[]{0.0,0.0});
		for(int i = 0; i < lead.size(); i++) {
			if (lead.getAt(i)[1] > maxima.peek()[1] * (1+range)) {
				maxima.clear();
			}
			if(maxima.size() == 0 || (lead.getAt(i)[1] < maxima.peek()[1] * (1+range)
									 && lead.getAt(i)[1] > maxima.peek()[1] * (1-range))) {
				int entryVal = i;
				int localMax = i;
				for(; lead.getAt(i)[1] > lead.getAt(entryVal)[1] * (1-range); i++) {
					if(lead.getAt(i)[1] > lead.getAt(localMax)[1]) {
						localMax = i;
					}
				}
				maxima.add(lead.getAt(localMax));
			}
		}

		for(Double[] p : maxima) {
			this.annotations.add(new Annotation(2, p[0]));
		}
	}

	/** 
	 * applyFilter - applies a filter to the data
	 *
	 * @param index the lead to apply the filter to
	 * @param filterNum number associated with a filter
	 *		0 = savitzky-golay filter
	 *		1 = high pass
	 *		2 = low pass
	 *		3 = fft
	 *		4 = detrend
	 * @param params the params to pass to each filter
	 *		sgfilter: 1 = left samples, 2 = right samples, 3 = polynomial degree
	 *		high pass: 1 = threshold
	 *      low pass: 1 = threshold
	 *      fft: 1 = threshold
	 *		detrend: 1 = polynomial degree
	 */
	public void applyFilter(int index, int filterNum, Number[] params) {
		switch(filterNum) {
			case 0:
				points[index].sgolayfilt((int)params[0], (int)params[1], (int)params[2]);
				break;
			case 1:
				points[index].highpassfilt((double)params[0]);
				break;
			case 2:
				points[index].lowpassfilt((double)params[0]);
				break;
			case 3: 
				points[index].highpassfftfilt((double)params[0], 0);
				break;
			case 4:
				points[index].detrend((int)params[0]);
				break;
			case 5:
				points[index].waveletfilt((double)params[0]);
				break;
			case 6:
				points[index].constofffilt((double)params[0]);
				break;
			default:
				return;
		}
	}
}

