
import java.awt.Component;
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

public class ProgressDialog implements PropertyChangeListener {
	private ProgressMonitor pm;
	private SwingWorker<Void, Void> t;

	public static void make(final SwingWorker<Void, Void> task, final Component attach) {
		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			public void run() { 
				show(task, attach);
			}
		});
	}

	private static void show(final SwingWorker<Void, Void> task, Component attach) {
		ProgressDialog content = new ProgressDialog();
		content.t = task;

		content.run(attach);
	}
	
	private void run(Component attach) {
		pm = new ProgressMonitor(attach, "", "Working...", 0, 100);
		pm.setProgress(0);
		t.addPropertyChangeListener(this);
		t.execute();
	}

	public void propertyChange(PropertyChangeEvent e) {
		if("progress" == e.getPropertyName()) {
			pm.setProgress((Integer) e.getNewValue());
			if(pm.isCanceled()) {
				t.cancel(true);
			}
		}
		if("state" == e.getPropertyName() 
				&& SwingWorker.StateValue.DONE == (SwingWorker.StateValue)e.getNewValue()) {
			pm.close();
		}
	}
}
