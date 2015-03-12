
public class ECGFileTest {
	public static void main(String args[]) {
		ECGFileManager e = new ECGFileManager();
		e.load();
		System.out.println(e.getClassNames());
	}
}
