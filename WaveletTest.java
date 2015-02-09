
import java.util.Arrays;
import math.jwave.Transform;
import math.jwave.transforms.AncientEgyptianDecomposition;
import math.jwave.transforms.FastWaveletTransform;
import math.jwave.transforms.wavelets.Haar1;

public class WaveletTest {
	public static void main(String[] args) {
		Transform t = new Transform(
							new AncientEgyptianDecomposition(
								new FastWaveletTransform( 
									new Haar1( ) ) ) );
		double threshold = 1.5;

		double[ ] arrTime = { 4., 5., 6., 5.5, 7., 3., 4.};

		double[ ] arrHilb = t.forward( arrTime ); // 1-D AED FWT Haar forward

		System.out.println(Arrays.toString(arrTime));
		System.out.println(Arrays.toString(arrHilb));
		for(int i = 0; i < arrHilb.length; i++) {
			if(Math.abs(arrHilb[i]) > threshold) {
				arrHilb[i] = 1;
			}
		}

		double[ ] arrReco = t.reverse( arrHilb ); // 1-D AED FWT Haar reverse
														
		System.out.println(Arrays.toString(arrReco));
	}
}

