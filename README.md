ECG Viewer - Opens and manipulates raw ECG data
===============================================
Author: Dakota Williams, drw9888@rit.edu

This work is supported by the National Science Foundation CAREER Award #ACI-1350374 

Running from command line: java -jar path/to/file/ECGViewer.jar

Output file formats:
1. Export and Export Subset: CSV, with title row (lead number) and title column (time of sample)
2. Bad Leads: Lead numbers, one per line
3. Annotations: One lead per line of the format:
			<lead number>: (<annotation type>, <location>), <more annotations> ...
