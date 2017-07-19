int analogPin = 1;     // potentiometer wiper (middle terminal) connected to analog pin 3

                       // outside leads to ground and +5V

int val;           // variable to store the value read
float prev_current;
float current;

void setup()
{
  Serial.begin(9600);          //  setup serial
  val = 0;
  prev_current = 0.0;
  current = 0.0;
}

void loop()
{  
  current = getCurrentReading();
  
  if (current > 0.1 && ((abs(prev_current - current)/prev_current)*100 > 5)){
    //Serial.println((abs(prev_current - current)/prev_current)*100);
    //Serial.println(prev_current);
    Serial.println(current);

    prev_current = current * 1.0;
  }
}

float getCurrentReading(){
  float voltage = 0.0;
  for (int i = 0; i < 25; i++){
    val = analogRead(analogPin);
    voltage = voltage + val / 1023.0 * 5.0 - 0.82;
  }
  voltage = voltage / 25.0;
  return voltage/0.4;
}

