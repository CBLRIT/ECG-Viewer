ECG Viewer - Opens and manipulates raw ECG data
===============================================
Author: Dakota Williams, drw9888@rit.edu

This work is supported by the National Science Foundation CAREER Award #ACI-1350374 

Running from command line: `java -jar path/to/file/ECGViewer.jar`

Output file formats:<br/>
1. Export and Export Subset: CSV, with title row (lead number) and title column (time of sample)<br/>
2. Bad Leads: Lead numbers, one per line<br/>
3. Annotations: One lead per line of the format:<br/>
&nbsp;&lt;lead number&gt;: (&lt;annotation type&gt;, &lt;location&gt;), &lt;more annotations&gt; ...

Libraries Used:<br/>
1. <a href="http://www.jfree.org/jfreechart/">JFreeChart</a> v1.0.19 - unmodified<br/>
2. <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Math<a> v3.3 - unmodified<br/>
3. <a href="https://code.google.com/p/savitzky-golay-filter/">savitzky-golay-filter<a> - modified:<br/>
  1. SGFilter.java was updated to resolve deprecated dependencies on Apache Commons Math. A copy of the Apache license is contained in the `sgfilter` directory.
