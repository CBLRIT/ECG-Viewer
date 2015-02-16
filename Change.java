
/**
 * class Change - little class for holding a model state
 *
 * @author Dakota Williams
 */

 public class Change <D, M> {
	private D data;
	private M message;

	public Change(D data, M message) {
		this.data = data;
		this.message = message;
	}

	public D getData() {
		return data;
	}

	public M getMessage() {
		return message;
	}

	public String toString() {
		return message.toString() + "\n";
	}
 }

