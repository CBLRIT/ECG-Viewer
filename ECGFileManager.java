
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class ECGFileManager {
	private HashSet<Class<? extends ECGFile>> classes;

	public ECGFileManager() {
		classes = new HashSet<Class<? extends ECGFile>>();
	}

	public void load() {
		File folder = new File("plugins");
		File[] sources = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".java");
			}
		});
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler != null) {
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sources));
			compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
			if(diagnostics.getDiagnostics().size() != 0) {
				try {
					PrintWriter errors = new PrintWriter("plugins/error.log");
					for(Diagnostic diagnostic : diagnostics.getDiagnostics()) {
						errors.println(((JavaFileObject)diagnostic.getSource()).getName()+":"+diagnostic.getLineNumber()+":"+diagnostic.getColumnNumber()+"->"+diagnostic.getMessage(null));
					}
					errors.flush();
					errors.close();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Could not write to error log", "Error", JOptionPane.ERROR_MESSAGE);
				}
				JOptionPane.showMessageDialog(null, "There were plugin compilation errors. All or some of the plugins will not be added. Check \"plugins/error.log\" for details.", "Compilation error", JOptionPane.WARNING_MESSAGE);
			}
		} else if (sources.length != 0) {
			JOptionPane.showMessageDialog(null, "Source plugins not supported, please install a Java Development Kit (JDK).", "Cannot Compile Plugin", JOptionPane.WARNING_MESSAGE);
		}

		File[] classfiles = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".class");
			}
		});

		try {
			ClassLoader loader = new URLClassLoader(new URL[]{folder.toURI().toURL()});
			for(int i = 0; i < classfiles.length; i++) {
				try {
					Class<? extends ECGFile> _class = loader.loadClass(classfiles[i].getName().substring(0, classfiles[i].getName().indexOf(".class"))).asSubclass(ECGFile.class);
					classes.add(_class);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null, "Could not create ClassLoader", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getClassNames() {
		return classes.toString();
	}

	/*public ECGFile getECGFile(String filename) {
		
	}*/
}

