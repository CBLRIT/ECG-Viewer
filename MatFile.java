
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
* See http://www.mathworks.com/help/pdf_doc/matlab/matfile_format.pdf
* for more info
*/
public class MatFile {
	private FileOutputStream fs;
	private final int nameSize = 8;

	public MatFile(String filename) {
		try {
		fs = new FileOutputStream(filename);
		} catch(FileNotFoundException e) {}
	}

	public void write(double[][][] data) //data - channels, x/y, entries
			throws IOException {
		int n = data[0][0].length;  //# of data points
		int m = data.length;	  	//# of channels

		//calculating matrix sizes
		int c = 2*n;
		int b = 56 + 2*n;
		int a = 48 + 64*m + 2*m*n;

		//descriptive text
		Date now = new Date();
		fs.write(("MATLAB 5.0 MAT-file, Created on: " + now.toString()).getBytes(), 0, 116);
		//flags? - not important
		fs.write(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
		//version - default
		fs.write(new byte[]{0, 1, 0, 0});
		//endian indicator - little
		int endian = ((int)'M' << 8) + (int)'I';
		fs.write(ByteBuffer.allocate(4).putInt(endian).array());

		//////Start channel arrays (Cell Array)///////////
		//                                     miMATRIX
		fs.write(ByteBuffer.allocate(4).putInt(14).array());
		//size of matrix in bytes
		fs.write(ByteBuffer.allocate(4).putInt(a).array());
		
		//HEADER of channel arrays             miUINT32 
		fs.write(ByteBuffer.allocate(4).putInt(6).array());
		fs.write(ByteBuffer.allocate(4).putInt(8).array());
		//                         mxCELL_CLASS
		fs.write(new byte[]{0, 0, 0, 1, 0, 0, 0, 0});
		//                                     miINT32
		fs.write(ByteBuffer.allocate(4).putInt(5).array());
		fs.write(ByteBuffer.allocate(4).putInt(8).array());
		//write matrix size
		fs.write(ByteBuffer.allocate(4).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).putInt(m).array());
		//name                                 miINT8
		fs.write(ByteBuffer.allocate(4).putInt(1).array());
		fs.write(ByteBuffer.allocate(4).putInt(nameSize).array());
		fs.write("leads".getBytes(), 0, nameSize);
		//END HEADER

		for(int i = 0; i < m; i++) {
			//////////Start channel array (Double Array)////////
			//                                     miMATRIX
			fs.write(ByteBuffer.allocate(4).putInt(14).array());
			//size of the matrix
			fs.write(ByteBuffer.allocate(4).putInt(b).array());

			//HEADER of channel array
			fs.write(ByteBuffer.allocate(4).putInt(6).array());
			fs.write(ByteBuffer.allocate(4).putInt(8).array());
			//                         mxDOUBLE_CLASS
			fs.write(new byte[]{0, 0, 0, 6, 0, 0, 0, 0});
			//                                     miINT32
			fs.write(ByteBuffer.allocate(4).putInt(5).array());
			fs.write(ByteBuffer.allocate(4).putInt(8).array());
			//write matrix size
			fs.write(ByteBuffer.allocate(4).putInt(2).array());
			fs.write(ByteBuffer.allocate(4).putInt(n).array());
			//name                                 miINT8
			fs.write(ByteBuffer.allocate(4).putInt(1).array());
			fs.write(ByteBuffer.allocate(4).putInt(nameSize).array());
			fs.write(("lead_" + i).getBytes(), 0, nameSize);
			//END HEADER

			//                                     miDOUBLE
			fs.write(ByteBuffer.allocate(4).putInt(9).array());
			fs.write(ByteBuffer.allocate(4).putInt(c).array());
			for(int j = 0; j < n; j++) {
				fs.write(ByteBuffer.allocate(8).putDouble(data[i][0][j]).array());
				fs.write(ByteBuffer.allocate(8).putDouble(data[i][1][j]).array());
			}
		}

		//END
	}
}

