
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Mesh - defines a mesh by its vertices and its faces
 */
public class Mesh {
	private ArrayList<double[]> verts;
	private ArrayList<double[]> faces;
	
	private enum Type {
		INT32,
		INT64,
		FLOAT,
		DOUBLE,
		NULL
	}

	/**
	 * getTypeOfByteArray - determines the correct primitive type of an array of bytes
	 *
	 * @param arr the array of bytes to determine
	 * @return the enum that represents a type
	 */
	private static Type getTypeOfByteArray(byte[] arr) {
		if(arr.length < 4 || (arr.length > 4 && arr.length < 8) || arr.length > 8) {
			return Type.NULL;
		} else if (arr.length == 4) {
			float f = ByteBuffer.wrap(arr).getFloat();
			if(f < 1.0e4f) {
				return Type.INT32;
			} else {
				return Type.FLOAT;
			}
		} else { //arr.length == 8
			double f = ByteBuffer.wrap(arr).getFloat();
			if(f < 1.0e4f) {
				return Type.INT64;
			} else {
				return Type.DOUBLE;
			}
		}
	}

	/**
	 * castFromByteArray - turns arr into the type it should be
	 *
	 * @param <T> the type to store the value into
	 * @param arr the array of bytes
	 * @returns a value of type T
	 */
	private static <T extends java.lang.Number> T castFromByteArray(byte[] arr) {
		Type t = getTypeOfByteArray(arr);
		T out;
		switch(t) {
			case INT32:
				out = (T)Integer.valueOf(ByteBuffer.wrap(arr).getInt());
				break;
			case INT64:
				out = (T)Long.valueOf(ByteBuffer.wrap(arr).getLong());
				break;
			case FLOAT:
				out = (T)Float.valueOf(ByteBuffer.wrap(arr).getFloat());
				break;
			case DOUBLE:
				out = (T)Double.valueOf(ByteBuffer.wrap(arr).getDouble());
				break;
			default:
				out = null;
		}
		return out;
	}

	/**
	 * Constructor - opens files that contain the data for the mesh
	 * 
	 * @param facePath the path to the file that contains the data for the faces
	 * @param vertPath the path to the file that contains the data for the vertices
	 * @throws FileNotFoundException if either of the files are not found
	 * @throws UnalignedCoordinateException if either of the files do not have perfect
	 *										sets of coordinates
	 */
	public Mesh(String facePath, String vertPath) 
			throws FileNotFoundException, UnalignedCoordinateException {
		File fFile = new File(facePath);
		File vFile = new File(vertPath);
		FileInputStream faceFile = new FileInputStream(facePath);
		FileInputStream vertFile = new FileInputStream(vFile);

		int vertWidth = 8; // safe to assume it'll be a double?
		int numVerts = (int)vFile.length() / (vertWidth * 3); // 3 = num coords per vert
		int numFaces = 2*(numVerts-2);
		int faceWidth = (int)fFile.length() / (numFaces * 3); 

		byte[] buf = new byte[faceWidth];
		int ret = 0;
		while(true) {
			double[] coord = new double[3];
			
			for(int i = 0; i < 3; i++) { //O(1) time
				try {
					ret = faceFile.read(buf);
				} catch (IOException e) {
					System.err.println("Something went wrong: " + e.getMessage());
					return;
				}
				if(ret == -1) {
					if(i == 0) {
						break;
					} else {
						throw new UnalignedCoordinateException("Face");
					}
				}
				//converts the bytes read in to double
				coord[i] = (Double)castFromByteArray(buf);
			}

			if(ret == -1) {
				break;
			}

			faces.add(coord);
		}
	}
}

