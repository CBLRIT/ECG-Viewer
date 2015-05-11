
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class TextSlide extends JPanel {
	private final JSlider slider;
	private final JFormattedTextField number;
	private final double scale;

	public TextSlide(double min, double max, double init, int precision) {
		super();
		scale = Math.pow(10.0, (double)precision);
		slider = new JSlider((int)(min * scale), (int)(max * scale), (int)(init * scale));
		number = new JFormattedTextField(NumberFormat.getNumberInstance());
		number.setValue(init);

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				number.setValue(slider.getValue() / scale);
			}
		});

		number.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setValue((int)((Double)number.getValue() * scale));
			}
		});
	}

	public Double getValue() {
		return (Double) ((double)slider.getValue() / scale);
	}

	public void setValue(Double val) {
		number.setValue(val);
		slider.setValue((int)(val * scale));
	}
}

