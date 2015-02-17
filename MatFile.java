
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

/**
* See http://www.mathworks.com/help/pdf_doc/matlab/matfile_format.pdf
* for more info
*/
public class MatFile implements ECGOutputFile {
	private FileOutputStream fs;

	private static String pad(String s, int totalLen, char padWith) {
		String ret = new String(s);
		for(int i = 0; i < totalLen - s.length(); i++) {
			ret += new String(new char[]{padWith});
		}
		return ret;
	}

	public MatFile(String filename) {
		try {
			fs = new FileOutputStream(filename);
		} catch(FileNotFoundException e) {}
	}

	public void write(ECGDataSet[] data) //data - channels, x/y, entries
			throws IOException {
		int n = data[0].size();  //# of data points
		int m = data.length;	  	//# of channels

		//calculating matrix sizes
		int c = 56;
		int b = 40 + (8 + c)*n*2;
		int a = 48 + (8 + b)*m;

		ByteOrder ord = ByteOrder.nativeOrder();

		//descriptive text
		Date now = new Date();
		fs.write(pad("MATLAB 5.0 MAT-file, Created on: " + now.toString(),
					  116,
					  ' ').getBytes(), 
				 0, 
				 116);
		//flags? - not important
		fs.write(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
		//version - default
		fs.write(new byte[]{0, 1});
		//endian indicator - little
		fs.write("IM".getBytes());

		//////Start channel arrays (Cell Array)///////////
		//                                     miMATRIX
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
		//size of matrix in bytes
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(a).array());
		
		//HEADER of channel arrays             miUINT32 
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
		//                         mxCELL_CLASS
		fs.write(new byte[]{1, 0, 0, 0, 0, 0, 0, 0});
		//                                     miINT32
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
		//write matrix size
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(m).array());
		//name                                 miINT8
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
		fs.write("leads".getBytes());
		fs.write(new byte[]{0, 0, 0}); //padding
		//END HEADER

		for(int i = 0; i < m; i++) {
			//////////Start channel array (Double Array)////////
			//                                     miMATRIX
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
			//size of the matrix
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(b).array());

			//HEADER of channel array
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
			fs.write(new byte[]{1, 0, 0, 0, 0, 0, 0, 0});
			//                                     miINT32
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
			//write matrix size
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(2).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(n).array());
			//name                                 miINT8
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(0).array()); //no name
			//END HEADER

			for(int j = 0; j < n; j++) {
				for(int k = 0; k < 2; k++) {
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(c).array());

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(new byte[]{6, 0, 0, 0, 0, 0, 0, 0});

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(0).array()); //no name
			
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(9).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(ByteBuffer.allocate(8).order(ord)
										 .putDouble(data[i].getAt(j)[k]).array());
				}
			}
		}

		fs.flush();
		fs.close();
		//END
	}

	public void writeSubset(ECGDataSet[] data, int start, int end)
			throws IOException {
		int n = end-start;  //# of data points
		int m = data.length;	  	//# of channels

		//calculating matrix sizes
		int c = 56;
		int b = 40 + (8 + c)*n*2;
		int a = 48 + (8 + b)*m;

		ByteOrder ord = ByteOrder.nativeOrder();

		//descriptive text
		Date now = new Date();
		fs.write(pad("MATLAB 5.0 MAT-file, Created on: " + now.toString(),
					  116,
					  ' ').getBytes(), 
				 0, 
				 116);
		//flags? - not important
		fs.write(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
		//version - default
		fs.write(new byte[]{0, 1});
		//endian indicator - little
		fs.write("IM".getBytes());

		//////Start channel arrays (Cell Array)///////////
		//                                     miMATRIX
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
		//size of matrix in bytes
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(a).array());
		
		//HEADER of channel arrays             miUINT32 
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
		//                         mxCELL_CLASS
		fs.write(new byte[]{1, 0, 0, 0, 0, 0, 0, 0});
		//                                     miINT32
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
		//write matrix size
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(m).array());
		//name                                 miINT8
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
		fs.write("leads".getBytes());
		fs.write(new byte[]{0, 0, 0}); //padding
		//END HEADER

		for(int i = 0; i < m; i++) {
			//////////Start channel array (Double Array)////////
			//                                     miMATRIX
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
			//size of the matrix
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(b).array());

			//HEADER of channel array
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
			fs.write(new byte[]{1, 0, 0, 0, 0, 0, 0, 0});
			//                                     miINT32
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
			//write matrix size
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(2).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(n).array());
			//name                                 miINT8
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
			fs.write(ByteBuffer.allocate(4).order(ord).putInt(0).array()); //no name
			//END HEADER

			for(int j = 0; j < n; j++) {
				for(int k = 0; k < 2; k++) {
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(14).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(c).array());

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(6).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(new byte[]{6, 0, 0, 0, 0, 0, 0, 0});

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(5).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());

					fs.write(ByteBuffer.allocate(4).order(ord).putInt(1).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(0).array()); //no name
			
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(9).array());
					fs.write(ByteBuffer.allocate(4).order(ord).putInt(8).array());
					fs.write(ByteBuffer.allocate(8).order(ord)
										 .putDouble(data[i].getAt(j)[k]).array());
				}
			}
		}

		fs.flush();
		fs.close();
		//END
	}
}

