
public class MatTest {
	public static void main(String args[]) 
			throws Exception {
		double[][][] test = new double[][][] {
												{ {1.123456789, 2, 3},
												  {4, 5, 6} },
												{ {7.123456789, 8, 9},
												  {10, 11, 12} }
											 };

		MatFile m = new MatFile("test.mat");
		m.write(test);
	}
}
