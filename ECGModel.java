
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
//import org.jfree.data.xy.DefaultXYDataset;

public class ECGModel {
	//private DefaultXYDataset points; //x is time, y is value
	private ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Integer>>> points; //time<values>
	private final long tupleStart = 0x00046860;
	private final long tupleDiff  = 0x00000468;
	private final int tupleLength = 154;
	private final double timeStep = 1000.0/2048.0; //0.48828125

	private int fixBytes(int raw) {
		return raw;
	}

	private int getTupleVals(long i, RandomAccessFile file) 
			throws IOException, EOFException {
		int val = 0;
		for(long j = 0; j < tupleLength; j++) { // loop over values in tuple
			//read a value
			val = file.readInt();

			val = Integer.reverseBytes(val);
			if(j > 2) { // first two values are masks
				val >>= 8;
				val &= (int)points.get((int)i).getValue().get(1);
			}
			points.get((int)i).getValue().add(val);
		}
		return val;
	}

	public ECGModel() {
		points = new ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Integer>>>();
	}

	public void readData(String filename) 
			throws IOException, FileNotFoundException {
		RandomAccessFile file = new RandomAccessFile(filename, "r");

		//read in values
		for(long i = 0; /*TODO: fill this in*/; i++) { // loop over tuples in file
			points.add(new AbstractMap.SimpleEntry<Double, ArrayList<Integer>>
							(i*timeStep, new ArrayList<Integer>()));
			file.seek(tupleStart+i*tupleDiff);

			try {
				getTupleVals(i, file);
			} catch (EOFException e) {
				//System.out.println(i + "\n");
				break;
			}
		}

		//System.out.println(points.get(0).getValue().toString());
	}

	public double[][][] getDataset() {
		double[][][] ret = new double[points.get(0).getValue().size()][2][points.size()];
		for(int i = 0; i < points.size(); i++) {
			for(int k = 0; k < points.get(i).getValue().size(); k++) {
		//	ret[i] = (Double[])points.get(i).getValue().toArray();
				ret[k][0][i] = (double)points.get(i).getKey();
				ret[k][1][i] = (double)points.get(i).getValue().get(k);
			}
		}
/*		System.out.println(Arrays.toString(ret[0][0]));
		System.out.println(Arrays.toString(ret[0][1]));*/
		return ret;
	}
}

