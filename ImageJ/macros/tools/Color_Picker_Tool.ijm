// This tool displays the coordinates and RGB values of pixels

    macro "Color Picker Tool -C037o5477" {
        getCursorLoc(x, y, z, flags);
        v = getPixel(x,y);
        if (bitDepth==24) {
            red = (v>>16)&0xff;  // extract red byte (bits 23-17)
            green = (v>>8)&0xff; // extract green byte (bits 15-8)
            blue = v&0xff;       // extract blue byte (bits 7-0)
            if (nSlices>1)
                print("x="+x+", y="+y+", z="+z+", value=("+red+","+green+","+blue+")");
            else
                 print("x="+x+", y="+y+z+", value=("+red+","+green+","+blue+")");
     } else
            if (nSlices>1)
                print("x="+x+", y="+y+", z="+z+", value="+v);
            else
                print("x="+x+", y="+y+", value="+v);
   }
