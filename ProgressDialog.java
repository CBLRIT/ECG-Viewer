
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

public class ProgressDialog extends JPanel 
							implements PropertyChangeListener, WindowListener {
	private ProgressMonitor pm;
	private SwingWorker<Void, Void> t;

	public static void make(final SwingWorker<Void, Void> task) {
		System.out.println("entered make");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				show(task);
			}
		});
	}

	private static void show(final SwingWorker<Void, Void> task) {
		System.out.println("entered show");
		JDialog dialog = new JDialog((Frame) null, true);

		final ProgressDialog content = new ProgressDialog();
		content.t = task;
		content.setOpaque(true);
		dialog.setContentPane(content);
		dialog.addWindowListener(content);

		dialog.pack();
		dialog.setVisible(true);
	}
	
	private void run() {
		System.out.println("running");
		pm = new ProgressMonitor(ProgressDialog.this, "Working...", "", 0, 100);
		pm.setProgress(0);
		t.addPropertyChangeListener(this);
		t.execute();
	}

	public void windowOpened(WindowEvent e) {
		System.out.println("window opened");
		run();
	}
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}

	public void propertyChange(PropertyChangeEvent e) {
		System.out.println("changed property: " + e.toString());
		if("progress" == e.getPropertyName()) {
			System.out.println("progress" + (Integer)e.getNewValue());
			pm.setProgress((Integer) e.getNewValue());
			if(pm.isCanceled()) {
				t.cancel(true);
			}
		}
	}

	public boolean isDone() {
		return t.isDone();
	}

	public boolean isCancelled() {
		return t.isCancelled();
	}
}
