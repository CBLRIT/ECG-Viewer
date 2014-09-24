// This is a macro for the old pong game
// Author Kai Uwe Barthel

// move the paddle with your mouse
// give the ball some spin by hitting it with a moving paddle


	width = 400;	
	height = 500;
	paddleWidth = width/12;
	paddleHeight = paddleWidth/3;
	w2 = width/2;
	h2 = height/2;
	lw = width/25; lw2 = lw/2;

	requires("1.34g");
	if (getVersion>="1.37e")
          call("ij.gui.ImageWindow.centerNextImage");
	newImage("Pong", "RGB black", width, height, 1);
	setColor(255,255,255);
	setLineWidth(2);
	drawLine(10,h2,width-10,h2);
	
	snapshot(); // create a backup image that can be restored later
	autoUpdate(false); // disable automatic display updates

	x=w2; y=h2;
	inc=6;
	xinc=0.5*inc; yinc=-inc;
	pc = 0;
	ph = 0;

  	yp1c = 0.1*height;
	yp1h = 0.9*height;

	frameTime = 30;
	startTime = getTime();

	getCursorLoc(xm, ym, zm, flags);
	xml = xm;

	xp1c = w2 - paddleWidth; 
	xp2c = w2 + paddleWidth; 		
	  

	// main loop	
	while (true) {
		x+=xinc; y+=yinc; 
		xinc *= 0.997;
		
	  	time = getTime();
		diff = time - startTime;
		wt = maxOf(0, frameTime - diff);
		wait(wt);
		startTime = time;
	   				
		// sides		
	  	if (x< lw2) {
			xinc = -xinc;
			x = lw2;
		}
	  	if (x>width-lw2) {
			xinc = -xinc;
			x = width-lw2;
		}
	  			
		getCursorLoc(xm, ym, zm, flags);
		
		spin = spin*0.9 + 0.1*(xm - xml);
		xml = xm;	
		if (spin > 5)
			spin = 5;
		if (spin < -5)
			spin = -5;

		xp1h = xm - paddleWidth; 
	  	xp2h = xm + paddleWidth; 

		diffx = x - paddleWidth - xp1c;	
		r = r*0.95 + 0.15* diffx  * random();
		maxSpeed = 12*random();	
		if (r > maxSpeed )
		  	r = maxSpeed ;
		if (r < -maxSpeed )
		  	r = -maxSpeed ;
	
		xp1c += r; xp2c += r; 
		
		hit=0;

		// touch upper border of human paddle
	  	if (yinc > 0 && xp1h < x && x < xp2h &&  yp1h-lw2 < y &&  yp1h-lw2+10 > y ) { 
			xinc += 0.8*spin;
			yinc -= 0.4*spin;
			hit = 1;
		}
		if (yinc < 0 && xp1c < x && x < xp2c &&  yp1c+10+lw2 >  y &&  yp1c+lw2 <  y ) { 
			hit = 1;
		}
		if (hit == 1) {
			xinc += 0.3*( random() - 0.5);
			yinc += random() - 0.5;
			yinc= -yinc;
		}
		if (abs(yinc) > 8)
			yinc *= 0.9;
		
		if (abs(yinc) < 5)
			yinc *= 1.1;
		
		reset(); // restore the backup image
	
			
		// human paddle
		setColor(0,0,255);
		setLineWidth(paddleHeight);
		//fillRect(xp1h, yp1h, xp2h-xp1h, 13);
		drawLine(xp1h+5, yp1h+6, xp2h-5, yp1h+6);

		// computer paddle
		setColor(255,0,0);
		//fillRect(xp1c, yp1c, xp2c-xp1c, 13);
		drawLine(xp1c+5, yp1c+6, xp2c-5, yp1c+6);
								
		point = 0;
		// top
		if (y<lw2) {
			point = 1;
			ph++;
		}
		// bottom
	  	else if (y>height-lw2) {
			point = 1;
			pc++;
		}
		// score
		setFont("SansSerif", 32, "bold");
		setColor(0, 255, 0);
		drawString(ph + " : " + pc , 30, 40);
		
		// ball
		setColor(255,255,255);
		setLineWidth(lw);
		drawLine(x,y,x,y);

		updateDisplay();
		
		if (point) {
			yinc=-yinc;
			wait(1000);
		}
		
		if (pc == 10 || ph == 10) {
			setFont("SansSerif", 30, "bold");
			setJustification("center");
			if (ph > pc) 
				drawString("Congratulations!" , width/2, height/3);
			else	
				drawString("Game over" , width/2, height/3);
			updateDisplay();
			exit();
		}
		
	}
}


