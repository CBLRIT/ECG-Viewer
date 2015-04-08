
/**
 * class Annotation - describes an annotation, contains a type and a location
 *
 * @author Dakota Williams
 */
public class Annotation implements Undoable {
	private int type;
	private double location;

	/**
	 * Constructor - creates a new Annotation
	 *
	 * @param type a number that represents the type of annotation
	 * @param loc the location of the annotation
	 */
	public Annotation(int type, double loc) {
		this.type = type;
		this.location = loc;
	}

	/**
	 * Copy Constructor - creates a copy of a given object
	 *
	 * @param anno the object to copy
	 * @return a copy of anno
	 */
	public Annotation(Annotation anno) {
		this(anno.getType(), anno.getLoc());
	}

	/**
	 * getLoc - returns the location of this annotation
	 *
	 * @return the location of this annotation
	 */
	public double getLoc() {
		return location;
	}

	/**
	 * getType - returns the type of this annotation
	 *
	 * @return the type of this annotation
	 */
	public int getType() {
		return type;
	}

	/**
	 * toString - string representation of the data
	 *
	 * @return a string of the form "[type] [location]"
	 */
	public String toString() {
		return (double)type + " " + location;
	}
}

