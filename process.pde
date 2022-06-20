import processing.serial.*;
Serial myPort;
String ledStatus="MODE: OFF";

void setup() {
  size(450, 500);
  myPort = new Serial(this, "COM8", 9600); // Starts the serial communication
  myPort.bufferUntil('\n'); // Defines up to which character the data from the serial port will be read. The character '\n' or 'New Line'
}

void serialEvent(Serial myPort) { // Checks for available data in the Serial Port
  ledStatus = myPort.readStringUntil('\n'); //Reads the data sent from the Arduino (the String "LED: OFF/ON) and it puts into the "ledStatus" variable
}
void keyPressed() {
  if (key == CODED) {
    switch(keyCode) {
    case UP:
      myPort.write('2');
      delay (10);
      break;
    case LEFT:
      myPort.write('3');
      delay (10);
    break;
      case RIGHT:
      myPort.write('4');
      delay (10);
    break;
      case DOWN:
      myPort.write('5');
      delay (10);
      break;
    default:
      myPort.write('0');
      delay (10);
      break;
    }
  }
}

void draw() {
  background(237, 240, 241);
  fill(20, 160, 133); // Green Color
  stroke(33);
  strokeWeight(1);
  rect(50, 100, 150, 50, 10); // Turn ON Button
  rect(250, 100, 150, 50, 10); // Turn OFF Button
  rect(150, 175, 150, 50, 10); // GO STRAIGHT
  rect(50, 250, 150, 50, 10); // GO LEFT
  rect(250, 250, 150, 50, 10); // GO RIGHT
  fill(255);
  textSize(32);
  text("TURN AUTO",55, 135);
  text("TURN OFF", 250, 135);
  text("GO STRAIGHT", 150, 190);
  text("GO LEFT", 50, 265);
  text("GO RIGHT", 250, 265);
  textSize(24);
  fill(33);
  text("Status:", 180, 400);
  text(ledStatus, 0, 440); // Prints the string comming from the Arduino

  // If the button "Turn ON" is pressed
  if(mousePressed && mouseX>50 && mouseX<200 && mouseY>100 && mouseY<150){
    myPort.write('1'); // Sends the character '1' and that will turn on the LED
    // Highlighs the buttons in red color when pressed
    stroke(255,0,0);
    strokeWeight(2);
    noFill();
    rect(50, 100, 150, 50, 10);
  }
  
  // If the button "Turn OFF" is pressed
  if(mousePressed && mouseX>250 && mouseX<400 && mouseY>100 && mouseY<150){
    myPort.write('0'); // Sends the character '0' and that will turn on the LED
    stroke(255,0,0);
    strokeWeight(2);
    noFill();
    rect(250, 100, 150, 50, 10);
  }
  
  // If the button "GO Straight" is pressed
  if(mousePressed && mouseX>150 && mouseX<300 && mouseY>175 && mouseY<225){
    myPort.write('2'); // Sends the character '0' and that will turn on the LED
    stroke(255,0,0);
    strokeWeight(2);
    noFill();
    rect(150, 175, 150, 50, 10);
  }

   // If the button "GO LEFT" is pressed
  if(mousePressed && mouseX>50 && mouseX<200 && mouseY>250 && mouseY<300){
    myPort.write('3'); // Sends the character '0' and that will turn on the LED
    stroke(255,0,0);
    strokeWeight(2);
    noFill();
    rect(50, 250, 150, 50, 10);
  }
  
  // If the button "GO RIGHT" is pressed
  if(mousePressed && mouseX>250 && mouseX<400 && mouseY>250 && mouseY<300){
    myPort.write('4'); // Sends the character '0' and that will turn on the LED
    stroke(255,0,0);
    strokeWeight(2);
    noFill();
    rect(250, 250, 150, 50, 10);
  }
}