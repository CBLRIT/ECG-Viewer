
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
				coord[i] = ByteBuffer.wrap(buf).getDouble();
			}

			if(ret == -1) {
				break;
			}

			faces.add(coord);
		}
	}
}

