
public class Annotation {
	private int type;
	private double location;

	public Annotation(int type, double loc) {
		this.type = type;
		this.location = loc;
	}

	public double getLoc() {
		return location;
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return "(" + type + ", " + location + ")";
	}
}

