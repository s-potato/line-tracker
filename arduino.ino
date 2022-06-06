#define BASESPEED 150
#define DURATION 5
#define FAST 200
#define SLOW 150

int state = 0;
int statePrev = 0;

int prev = 0;

int out1 = 2;
int out2 = 4;
int enA = 5;

int out3 = 3;
int out4 = 7;
int enB = 6;

int leftIn = 13;
int centerIn = 12;
int rightIn = 11;
int current_time = 0;
int previous_time = 0;

float p = 0;
float i = 0;
float d = 0;
float previous_error = 0;
float error = 0;
float PID = 0;

float Kp = 30;
float Ki = 0;
float Kd = 0;

int read_pos() {
  int pos;
  if (digitalRead(leftIn) == HIGH && digitalRead(centerIn) == LOW && digitalRead(rightIn) == LOW)
    pos = -2;
  else if (digitalRead(leftIn) == HIGH && digitalRead(centerIn) == HIGH && digitalRead(rightIn) == LOW)
    pos = -1;
  else if (digitalRead(leftIn) == LOW && digitalRead(centerIn) == HIGH && digitalRead(rightIn) == LOW)
    pos = 0;
  else if (digitalRead(leftIn) == LOW && digitalRead(centerIn) == HIGH && digitalRead(rightIn) == HIGH)
    pos = 1;
  else if (digitalRead(leftIn) == LOW && digitalRead(centerIn) == LOW && digitalRead(rightIn) == HIGH)
    pos = 2;
  else
    pos = previous_error;
  return pos;
}

void pid_calc()
{
  int error = read_pos();
  
  current_time = millis();
  int timediff = current_time - previous_time;
  previous_time = current_time;
  
  p = error;
  i = i + error*timediff;
  d = (error - previous_error)/timediff;

  previous_error = p;

  PID = int(Kp*p + Ki*i + Kd*d);
}

void setup()
{
    // put your setup code here, to run once:
    pinMode(enA, OUTPUT);
    pinMode(out1, OUTPUT);
    pinMode(out2, OUTPUT);

    pinMode(enB, OUTPUT);
    pinMode(out3, OUTPUT);
    pinMode(out4, OUTPUT);

    pinMode(leftIn, INPUT);
    pinMode(centerIn, INPUT);
    pinMode(rightIn, INPUT);

    previous_time = millis();
    Serial.begin(9600); // Default communication rate of the Bluetooth module
    stopEngine();
}

void goStraight()
{
    digitalWrite(out1, HIGH);
    digitalWrite(out2, LOW);
    digitalWrite(out3, HIGH);
    digitalWrite(out4, LOW);
    analogWrite(enA, FAST);
    analogWrite(enB, FAST);
    delay(DURATION);
}

void goRight()
{
    digitalWrite(out1, HIGH);
    digitalWrite(out2, LOW);
    digitalWrite(out3, HIGH);
    digitalWrite(out4, LOW);
    analogWrite(enA, SLOW);
    analogWrite(enB, FAST);
    delay(DURATION);
}

void goLeft()
{
    digitalWrite(out1, HIGH);
    digitalWrite(out2, LOW);
    digitalWrite(out3, HIGH);
    digitalWrite(out4, LOW);
    analogWrite(enA, FAST);
    analogWrite(enB, SLOW);
    delay(DURATION);
}

void goBack()
{
    digitalWrite(out1, LOW);
    digitalWrite(out2, HIGH);
    digitalWrite(out3, LOW);
    digitalWrite(out4, HIGH);
    analogWrite(enA, FAST);
    analogWrite(enB, FAST);
    delay(DURATION);
}

void stopEngine()
{
    digitalWrite(out1, LOW);
    digitalWrite(out2, LOW);
    digitalWrite(out3, LOW);
    digitalWrite(out4, LOW);
    analogWrite(enA, 0);
    analogWrite(enB, 0);
    delay(DURATION);
}

void goAuto()
{
    // Left * 1, Center * 2, Right * 4
    int left = 0;
    int center = 0;
    int right = 0;
    if (digitalRead(leftIn) == HIGH)
        left = 1;
    if (digitalRead(centerIn) == HIGH)
        center = 2;
    if (digitalRead(rightIn) == HIGH)
        right = 4;
    int current = left + center + right;
    int mode = 0;

    switch (current)
    {
    case 0:
        mode = 5 - prev + 2;
        break;
    case 5:
        mode = prev;
        break;
    case 1:
        mode = 3;
        break;
    case 4:
        mode = 4;
        break;
    default:
        mode = 2;
    }
    prev = mode;
    switch (mode)
    {
    case 2:
    {
        Serial.println("MODE: STRAIGHT");
        goStraight();

        break;
    }
    case 3:
    {
        Serial.println("MODE: LEFT");
        goLeft();

        break;
    }
    case 4:
    {
        Serial.println("MODE: RIGHT");
        goRight();

        break;
    }
    case 5:
    {
        Serial.println("MODE: BACK");
        goBack();

        break;
    }
    default:
    {
        Serial.println("MODE: OFF");
        stopEngine();

        break;
    }
    }
}

void motor_control()
{
    // Calculating the effective motor speed:
    int right_motor_speed = BASESPEED - PID;
    int left_motor_speed = BASESPEED + PID;
    
    // The motor speed should not exceed the max PWM value
    constrain(left_motor_speed, 0, 255);
    constrain(right_motor_speed, 0, 255);
    
    analogWrite(enB, left_motor_speed);   //Left Motor Speed
    analogWrite(enA, right_motor_speed);  //Right Motor Speed
    digitalWrite(out1, HIGH);
    digitalWrite(out2, LOW);
    digitalWrite(out3, HIGH);
    digitalWrite(out4, LOW);
}

void loop()
{
    if (Serial.available() > 0)
    {                          // Checks whether data is comming from the serial port
        state = Serial.read(); // Reads the data from the serial port
        statePrev = state;
    }
    else
    {
        state = statePrev;
    }

    switch (state)
    {
    case '1':
        Serial.println("MODE: AUTO");
        pid_calc();
        motor_control();
        state = 1;
        break;
    case '2':
        Serial.println("MODE: STRAIGHT");
        goStraight();
        state = 2;
        break;
    case '3':
        Serial.println("MODE: LEFT");
        goLeft();
        state = 3;
        break;
    case '4':
        Serial.println("MODE: RIGHT");
        goRight();
        state = 4;
        break;
    case '5':
        Serial.println("MODE: BACK");
        goBack();
        state = 5;
        break;
    default:
        Serial.println("MODE: OFF");
        stopEngine();
        state = 0;
        break;
    }
}