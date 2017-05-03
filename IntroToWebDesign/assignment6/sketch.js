var cnv; 
function setup() {
	cnv= createCanvas(500, 500);
  centerCanvas();
	rectMode(CENTER);
  
	r = 0;
	g = 255;
	b = 255;
	r2 = 0;
	g2 = 100;
	b2 = 255;
	counter = 0;
}
function windowResized() {
  centerCanvas();
}

function centerCanvas(){
  var x = (windowWidth - width) /2;
  var y = (windowHeight - height/1.3)/2;
  cnv.position(x,y);
}

function draw(){
	background(240);
  textSize(20); 
  
  // set a fill color
  fill(r, g, r2);
  textFont("Comic Sans MS");
  // write some text 
  text("wow", 232, 250);
  fill(255);
	translate(width/2, height/2);
	for (var i = 0; i < 8; i++) {
		push();
		rotate(TWO_PI * i / 8);
		var tx = 190 * sin(0.01*frameCount);
		stroke(r,g,b);
		translate(tx, 0);
		rect(0, 0, 20, 20);
		for (var j = 0; j < 6; j++) {
			push();
			rotate(TWO_PI * j / 6);
			var rx = 50 * cos(0.02*frameCount + 10);
			stroke(r2,g2,b2);
			rect(rx, 0, 8, 8);
			pop();
		}		
		translate()
		pop();
	}
}

function mousePressed() {
  counter+=1;
  if (counter%3 === 0){
    r = 100;
  	g = 100;
  	b = 255;
  	r2 = 100;
  	g2 = 0;
  	b2 = 255;
  }
  else if (counter%3 === 1){
  	r = 255;
  	g = 100;
  	b = 0;
  	r2 = 100;
  	g2 = 255;
  	b2 = 0;
  }
  else {
  	r = 0;
  	g = 255;
  	b = 255;
  	r2 = 0;
  	g2 = 100;
  	b2 = 255;
  }
}
