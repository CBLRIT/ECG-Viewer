
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
		int leads[][] = file.getLayout();
		int north = file.getNorth(120+1);
		int south = file.getSouth(120+1);
		int east = file.getEast(120+1);
		int west = file.getWest(120+1);
		System.out.println("North: "+(north-1)+(north==-1?"":" ("+leads[north][0]+","+leads[north][1]+")"));
		System.out.println("South: "+(south-1)+(south==-1?"":" ("+leads[south][0]+","+leads[south][1]+")"));
		System.out.println("East: "+(east-1)+(east==-1?"":" ("+leads[east][0]+","+leads[east][1]+")"));
		System.out.println("West: "+(west-1)+(west==-1?"":" ("+leads[west][0]+","+leads[west][1]+")"));
	}
}
