
import java.util.AbstractMap;
import java.util.ArrayList;

public class ECGFileTest {
	public static void main(String args[]) {
		ECGFileManager e = new ECGFileManager();
		e.load();
		System.out.println(e.getClassNames());
		ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>> points
			= new ArrayList<AbstractMap.SimpleEntry<Double, ArrayList<Double>>>();
		String filename = "data/CF091714/4917c43b.dat";
		ECGFile file = e.getECGFile(filename);
		file.read(filename, points);
	}
}
