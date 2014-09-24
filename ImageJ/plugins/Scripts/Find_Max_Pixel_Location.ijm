// This macro creates a point selection at the location of the lower 
// right most pixel with the value 'max', where 'max' is the highest 
// pixel value in the image.

   showStatus("Finging pixel with largest value...");
   max = 0;
   width = getWidth(); height = getHeight();
   for (y=0; y<height; y++) {
      if (y%20==0) showProgress(y, height);
      for (x=0; x<width; x++) {
          value = getPixel(x,y);
          if (value>max) {
              max = value;
              xmax = x;
              ymax = y;
          }
      }
  }
  makePoint(xmax, ymax);
  showStatus("Maximum at "+xmax+","+ymax
