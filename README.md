ECG Viewer - Opens and manipulates raw ECG data
===============================================
Author: Dakota Williams, drw9888@rit.edu

This work is supported by the National Science Foundation CAREER Award #ACI-1350374 

Running from command line: <i>java -jar path/to/file/ECGViewer.jar</i>

Output file formats:
1. Export and Export Subset: CSV, with title row (lead number) and title column (time of sample)<br/>
2. Bad Leads: Lead numbers, one per line<br/>
3. Annotations: One lead per line of the format:<br/>
			&lt;lead number&gt;: (&lt;annotation type&gt;, &lt;location&gt;), &lt;more annotations&gt; ...
