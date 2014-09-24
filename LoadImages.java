
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.IOException;
import ij.plugin.DICOM;

class LoadImages {
	private ArrayList<DICOM> images;

	public LoadImages(Path path) {
		try {
			DirectoryStream<Path> files = Files.newDirectoryStream(path, "*.dcm");
			for(Path file : files) {
				System.out.println(path.getFileName());
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}

