
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.IOException;
import ij.plugin.DICOM;

public class MoldTorso {
	private ArrayList<DICOM> images;
	private Mesh torsoMesh;

	public void loadImages(Path path) {
		try {
			DirectoryStream<Path> files = Files.newDirectoryStream(path, "*.dcm");
			for(Path file : files) {
				System.out.println(path.getFileName());
				DICOM d = new DICOM();
				d.open(path.toString());
				images.add(d);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}


	public void readTorsoMesh(String nodePath, String facePath) {
		/* mesh is stored in a nx3 matrix where n is the number of vertices
		 * n is not the same in nodes as it is in faces
		 *		let num of faces  = f
		 *		and num of nodes  = n
		 *							n = f/2 + 2 OR 2*(n-2) = f
		 * matlab is column major, c is row major, java is neither, so this might not be
		 * a problem...
		 */

		
	}
}

