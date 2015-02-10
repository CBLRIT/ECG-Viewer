
import java.util.Stack;

/**
 * class UndoStack - class for holding chronological changes
 *
 * @author Dakota Williams
 */

 public class UndoStack<E> {
	private Stack<E> undoStack;
	private Stack<E> redoStack;

	/**
	 * Constructor - initializes the stacks
	 */
	public UndoStack() {
		undoStack = new Stack<E>();
		redoStack = new Stack<E>();
	}

	/**
	 * pushChange - adds a state to the undo stack
	 *
	 * @param state the state to push on
	 */
	public void pushChange(E state) {
		redoStack.clear(); //flush redo stack since history has now changed
		undoStack.push(state);
	}

	/**
	 * canUndo - determines if it's possible to undo
	 *
	 * @return true if possible to undo
	 */
	public boolean canUndo() {
		return !undoStack.empty();
	}

	/**
	 * undo - reverts the state back to the previous change
	 *
	 * @param state the current state
	 * @return the previous state
	 */
	public E undo(E state) {
		redoStack.push(state);
		return undoStack.pop();
	}

	/**
	 * canRedo - determines if it's possible to redo
	 *
	 * @return true if possible to redo
	 */
	public boolean canRedo() {
		return !redoStack.empty();
	}

	/**
	 * redo - reverts the state back to a undone change
	 *
	 * @param state the current state
	 * @return the new state
	 */
	public E redo(E state) {
		undoStack.push(state);
		return redoStack.pop();
	}

	/**
	 * reset - deletes all history
	 */
	public void reset() {
		undoStack.clear();
		redoStack.clear();
	}
}

