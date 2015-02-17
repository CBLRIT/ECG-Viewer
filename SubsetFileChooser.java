
import java.awt.GridLayout;
import java.text.NumberFormat;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * SubsetFileChooser - Standard JFileChooser with added text boxes for start and length
 *
 * @author Dakota Williams
 */

public class SubsetFileChooser extends JFileChooser {
	private JFormattedTextField startText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField lenText 
		= new JFormattedTextField(NumberFormat.getIntegerInstance());
	
	/**
	 * Constructor - adds special components
	 */
	public SubsetFileChooser() {
		super();

		startText.setValue(0L);
		startText.setColumns(10);
		lenText.setValue(0L);
		lenText.setColumns(10);
		
		JPanel accessPanel = new JPanel();
		accessPanel.setLayout(new GridLayout(4, 1));

		accessPanel.add(new JLabel("Start offset (ms):"));
		accessPanel.add(startText);
		accessPanel.add(new JLabel("Length (ms):"));
		accessPanel.add(lenText);

		this.setAccessory(accessPanel);
	}

	public long getStartTime() {
		return (Long)startText.getValue();
	}

	public long getLengthTime() {
		return (Long)lenText.getValue();
	}
}
