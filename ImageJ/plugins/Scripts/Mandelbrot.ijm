// Mandelbrot set drawing macro
// jerome.mutterer at ibmp-ulp.u-strasbg.fr 

// This macro draws a Mandelbrot set. You can zoom in by 
// making a selection on the image and restarting the macro, 
// which illustrates the use of the Results table to pass arguments 
// from one macro to the other. The first run of the macro is 
// benchmarked. 

// Example 300x300 results, all obtained with IJ 1.31l and java 1.3.1 jre :
// Pentium II-350 Mhz        53.44 secs
// Pentium III-500 Mhz       45.09 secs
// Pentium IV-1800 Mhz       11.53 secs
// MacG4-400 Mhz             57.03 secs
// MacG4-2x1000 Mhz          26.05 secs
// MacG5-1x1800 Mhz          17.00 secs

// First we define some settings about start time, drawing area, and Mandelbrot parameters.
start = getTime();
firstrun = 0;
window_size = 500;
max_size = 4; 
max_iterations = 32;
// end settings 

// Here we make sure we are working in a decent window.
n = nImages;
if (n == 0) { 
    newImage("Mandelbrot Set", "8-bit", window_size, window_size, 1);
    firstrun = 1;
}
if (n != 0) {
    titre = getTitle();
    if (titre !="Mandelbrot Set") {
        newImage("Mandelbrot Set", "8-bit", window_size, window_size, 1);
        firstrun = 1;
    }
}
run("3-3-2 RGB");
// end decent window assessment.


// Definition of the computing interval either from scratch 
// or from selection in the drawing window.
// The current r & i min & max values are stored 
// in and retrieved from the Results table.

selection = selectionType();
if (selection==-1)   {
                      print ("Starting new set...");
                      imin = -1.25;
                      imax = 1.25;
                      rmin = -2;
                      rmax = 0.5;
                     } else  {
                            getBoundingRect(xs, ys, ws, hs);
                            rrow = nResults;
                            previousrmin = getResult("r min", rrow-1);
                            deltar = getResult("deltar", rrow-1);
                            rmin = previousrmin + (xs*deltar);
                            rmax = rmin +  (ws*deltar);
                            previousimin = getResult("i min", rrow-1);
                            deltai = getResult("deltai", rrow-1);
                            imin = previousimin + (ys*deltai);
                            imax = imin + (ws*deltai);
                            }

deltar=(rmax-rmin)/(window_size-1); 
deltai=(imax-imin)/(window_size-1); 
rrow = nResults;
setResult ("r min",rrow,rmin);
setResult ("r max",rrow,rmax);
setResult ("i min",rrow,imin);
setResult ("i max",rrow,imax);
setResult ("deltai",rrow,deltai);
setResult ("deltar",rrow,deltar);
updateResults() ;

// end interval settings.

// At this point we have all we need to draw the desired region.
for (row=0; row<window_size; row++) {
     for (col=0; col<window_size; col++) {
      x=0; 
      y=0; 
      ci=(imin+row*deltai);  
      cr=(rmin+col*deltar);  
      color=0;
      do {
        color++; 
        xsq=x*x;
        ysq=y*y;
        if ((xsq+ysq)<(max_size)) {  
            y=x*2*y+ci;                
            x=xsq-ysq+cr;
            }
        }
      while ((color<max_iterations)&&((xsq+ysq)<(max_size))); 
      if (color>=max_iterations) color=0;
      setPixel (col, row, color);      
      }
      updateDisplay();                           
}
// end drawing.

// For benchmarking, let's get the time elapsed since our macro started 
stop = getTime();
if (firstrun==1) {
	print ("Benchmark : "+((stop - start)/1000)+" secs");
	}
print ("You can draw a selection");
print ("and start the macro again for a subset");




