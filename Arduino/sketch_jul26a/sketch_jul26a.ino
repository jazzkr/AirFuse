// Included Libraries
#include <WiFiEsp.h> // Note: that this was modified to work with Arduino 101
#include <WiFiEspClient.h>

#include <ArduinoJson.h>

#include <stdio.h>
#include <math.h>


// struct definitions
struct fuse {
  int id;
  int fusebox;
  char name[20];
  char desc[200];
  float current_limit;
  float room_temp;
  float last_current_reading;
  float last_temperature_reading;
  bool tripped; // 0 -> 'good', 1 -> 'tripped'
//  bool relay_status;
//  bool last_current_reading;
//  bool tripped;
};



// FuseBox Specific Information
char fusebox_name[] = "testFuseBox1";
char password[] = "helloWorld";
char desc[] = "This+is+a+test+AirFuse+for+Intel+Hacks+2017.";

int id = -1;

fuse f1;
fuse f2;
fuse f3;


// Arduino 101 Pin Definitions
uint8_t f1tempSensorPin = A0;
uint8_t f2tempSensorPin = A1;
uint8_t f3tempSensorPin = A2;

uint8_t f1ampSensorPin = A3;
uint8_t f2ampSensorPin = A4;
uint8_t f3ampSensorPin = A5;

uint8_t clockPin = 7;
uint8_t latchPin = 6;
uint8_t dataPin = 5;

uint8_t f1RelayPin = 2;
uint8_t f2RelayPin = 3;
uint8_t f3RelayPin = 4;


// Grove Senor Initialization
const int grove_B = 4275;               // B value of the thermistor
const int grove_R0 = 100000;            // R0 = 100k

// Current Sensor Initialization
const float amp_m = (2.7 - 0)/(700 - 258);
const float amp_b = 2.7 - amp_m * 700;

// LED Patterns
byte patterns[30] = {
  B00000010, // Green, Fuse 1
  B00000100, // Green, Fuse 2
  B00001000, // Green, Fuse 3
  B00010000, // Red, Fuse 1
  B00100000, // Red, Fuse 2
  B01000000, // Red, Fuse 3
  B00000000, // All Off
};

byte selected_pattern;

// WiFi Connection Settings
//IPAddress ip_set(192, 168, 0, 176);    // ESP8266 IP Address
char ssid[] = "DPS65";            // your network SSID (name)
char pass[] = "asrjal123";        // your network password
int status = WL_IDLE_STATUS;     // the Wifi radio's status

char server[] = "django.utkarshsaini.com";



// Initialize the WifiEspClient object
WiFiEspClient client;




void setup() {
  // initialize serial for debugging
  Serial.begin(115200);

// Pin Initialization Code
pinMode(latchPin, OUTPUT);
pinMode(clockPin, OUTPUT);
pinMode(dataPin, OUTPUT);

pinMode(f1RelayPin, OUTPUT);
pinMode(f2RelayPin, OUTPUT);
pinMode(f3RelayPin, OUTPUT);


// LED Initialization
selected_pattern = patterns[3] | patterns[4] | patterns[5];
//Serial.println(selected_pattern);
updateLEDs();

// ESP8266 Initialization Code
  
  // initialize serial for ESP module
  Serial1.begin(115200);
  // initialize ESP module
  WiFi.init(&Serial1);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }

  // you're connected now, so print out the data
  Serial.println("You're connected to the network");
  
  printWifiStatus();


  // Make sure the fuse details are online
  bool airFuseExistsOnServer = checkIfAirFuseExists();
  Serial.print("Check if AirFuse exists: ");
  Serial.print(airFuseExistsOnServer);
  Serial.println();

  if (!airFuseExistsOnServer){
    createAirFuseOnServer();
  }

  id = getAirFuseId();

  f1 = (fuse){-1, id, "fuse 1", "This+is+fuse+1+of+testFuseBox1.", 1.6, getTemperatureReading(1), -1, -1, false};
  f2 = (fuse){-1, id, "fuse 2", "This+is+fuse+2+of+testFuseBox1.", 1.6, getTemperatureReading(2), -1, -1, false};
  f3 = (fuse){-1, id, "fuse 3", "This+is+fuse+3+of+testFuseBox1.", 1.6, getTemperatureReading(3), -1, -1, false};

  Serial.print("Verify fuses exists, if not create them: ");
  Serial.print(verifyFusesOnServer());
  Serial.println();

  // AirFuse Setup
  turnOnRelay(1);
  turnOnRelay(2);
  turnOnRelay(3);

  publishFuseStatus(1);
  publishFuseStatus(2);
  publishFuseStatus(3);
  
  selected_pattern = patterns[0] | patterns[1] | patterns[2];
  updateLEDs();
  
}




void loop() {

  f1.last_temperature_reading = getTemperatureReading(1);
  f2.last_temperature_reading = getTemperatureReading(2);
  f3.last_temperature_reading = getTemperatureReading(3);
  
  f1.last_current_reading = getCurrentReading(1);
  f2.last_current_reading = getCurrentReading(2);
  f3.last_current_reading = getCurrentReading(3);

  publishCurrentReadings();

  for (int i = 1; i < 4; i++) {
    bool newfuseStatus = updateFuseStatus(i);
    bool oldfuseStatus = getOldFuseStatus(i);
    if (newfuseStatus != oldfuseStatus) {
      if (newfuseStatus){
        turnOffRelay(i);
      } else {
        turnOnRelay(i);
      }
      if (i == 1){
        f1.tripped = newfuseStatus;
      }
      if (i == 2){
        f2.tripped = newfuseStatus;
      }
      if (i == 3){
        f3.tripped = newfuseStatus;
      }
      updateLEDsBasedOnStatus();
      publishFuseStatus(i);
    }
  }

  executeAnyUserAvailableUserActions();

  updateLEDsBasedOnStatus();
  delay(1000);
}


void executeAnyUserAvailableUserActions(){
  char buffer[200];
  sprintf (buffer, "GET /AirFuse/fuseUserActions2/%d/ HTTP/1.1", f1.id);
  String data = executeGetQuery(buffer);

  if (data != ""){
    Serial.println(data);
    int idIndex;
    int actionIndex;
    int commaIndex;
    int action_id;
    String user_action;
    
    //[{"id":7,"action":"trip","executed":false,"created_at":"2017-07-27T23:38:48.407770Z","updated_at":"2017-07-27T23:38:48.407821Z","fuse":7}]
    idIndex = data.indexOf("id");
    
    while (idIndex != -1){
      data = data.substring(idIndex);
      commaIndex = data.indexOf(",");
      action_id = data.substring(4, commaIndex).toInt();
      actionIndex = data.indexOf("action");
      data = data.substring(actionIndex);
      commaIndex = data.indexOf(",");
      user_action = data.substring(9, commaIndex - 1);

      if (user_action == "trip"){
        turnOffRelay(1);
        f1.tripped = true;
        updateLEDsBasedOnStatus();
        publishFuseStatus(1);
      }

      if (user_action == "reset"){
        turnOffRelay(1);
        delay(50);
        turnOnRelay(1);
        f1.tripped = false;
        updateLEDsBasedOnStatus();
        publishFuseStatus(1);
      }

      Serial.println(action_id);
      Serial.println(user_action);

      char buffer2[200];
      sprintf (buffer, "PUT /AirFuse/fuseUserActions2/%d/%d/ HTTP/1.1", f1.id, action_id);
      sprintf (buffer2, "executed=1&fuse=%d&action=%d", f1.id, action_id);
      executePOSTQuery(buffer, buffer2);

      idIndex = data.indexOf("id");
    }
  }

  sprintf (buffer, "GET /AirFuse/fuseUserActions2/%d/ HTTP/1.1", f2.id);
  data = executeGetQuery(buffer);

  if (data != ""){
    Serial.println(data);
    int idIndex;
    int actionIndex;
    int commaIndex;
    int action_id;
    String user_action;
    
    //[{"id":7,"action":"trip","executed":false,"created_at":"2017-07-27T23:38:48.407770Z","updated_at":"2017-07-27T23:38:48.407821Z","fuse":7}]
    idIndex = data.indexOf("id");
    
    while (idIndex != -1){
      data = data.substring(idIndex);
      commaIndex = data.indexOf(",");
      action_id = data.substring(4, commaIndex).toInt();
      actionIndex = data.indexOf("action");
      data = data.substring(actionIndex);
      commaIndex = data.indexOf(",");
      user_action = data.substring(9, commaIndex - 1);

      if (user_action == "trip"){
        turnOffRelay(2);
        f2.tripped = true;
        updateLEDsBasedOnStatus();
        publishFuseStatus(2);
      }

      if (user_action == "reset"){
        turnOffRelay(2);
        delay(50);
        turnOnRelay(2);
        f2.tripped = false;
        updateLEDsBasedOnStatus();
        publishFuseStatus(2);
      }

      Serial.println(action_id);
      Serial.println(user_action);

      char buffer2[200];
      sprintf (buffer, "PUT /AirFuse/fuseUserActions2/%d/%d/ HTTP/1.1", f2.id, action_id);
      sprintf (buffer2, "executed=1&fuse=%d&action=%d", f2.id, action_id);
      executePOSTQuery(buffer, buffer2);

      idIndex = data.indexOf("id");
    }
  }

  sprintf (buffer, "GET /AirFuse/fuseUserActions2/%d/ HTTP/1.1", f3.id);
  data = executeGetQuery(buffer);

  if (data != ""){
    Serial.println(data);
    int idIndex;
    int actionIndex;
    int commaIndex;
    int action_id;
    String user_action;
    
    //[{"id":7,"action":"trip","executed":false,"created_at":"2017-07-27T23:38:48.407770Z","updated_at":"2017-07-27T23:38:48.407821Z","fuse":7}]
    idIndex = data.indexOf("id");
    
    while (idIndex != -1){
      data = data.substring(idIndex);
      commaIndex = data.indexOf(",");
      action_id = data.substring(4, commaIndex).toInt();
      actionIndex = data.indexOf("action");
      data = data.substring(actionIndex);
      commaIndex = data.indexOf(",");
      user_action = data.substring(9, commaIndex - 1);

      if (user_action == "trip"){
        turnOffRelay(3);
        f3.tripped = true;
        updateLEDsBasedOnStatus();
        publishFuseStatus(3);
      }

      if (user_action == "reset"){
        turnOffRelay(3);
        delay(50);
        turnOnRelay(3);
        f3.tripped = false;
        updateLEDsBasedOnStatus();
        publishFuseStatus(3);
      }

      Serial.println(action_id);
      Serial.println(user_action);

      char buffer2[200];
      sprintf (buffer, "PUT /AirFuse/fuseUserActions2/%d/%d/ HTTP/1.1", f3.id, action_id);
      sprintf (buffer2, "executed=1&fuse=%d&action=%d", f3.id, action_id);
      executePOSTQuery(buffer, buffer2);

      idIndex = data.indexOf("id");
    }
  }
}

bool getOldFuseStatus(int fuseNum)
{
  bool old_status = false;
  if (fuseNum == 1){
    old_status = f1.tripped;
  }
  if (fuseNum == 2){
    old_status = f2.tripped;
  }
  if (fuseNum == 3){
    old_status = f3.tripped;
  }

  return old_status;
}

bool updateFuseStatus(int fuseNum)
{
  bool found = false;
  float current_limit;
  float room_temp;
  float last_current_reading;
  float last_temperature_reading;
  bool tripped;

  if (fuseNum == 1){
    current_limit = f1.current_limit;
    room_temp = f1.room_temp;
    last_current_reading = f1.last_current_reading;
    last_temperature_reading = f1.last_temperature_reading;
    tripped = f1.tripped;
    found = true;
  }
  if (fuseNum == 2){
    current_limit = f2.current_limit;
    room_temp = f2.room_temp;
    last_current_reading = f2.last_current_reading;
    last_temperature_reading = f2.last_temperature_reading;
    tripped = f2.tripped;
    found = true;
  }
  if (fuseNum == 3){
    current_limit = f3.current_limit;
    room_temp = f3.room_temp;
    last_current_reading = f3.last_current_reading;
    last_temperature_reading = f3.last_temperature_reading;
    tripped = f3.tripped;
    found = true;
  }

  if (found){
    float percent_temp_increase = ((last_temperature_reading - room_temp)/room_temp) * 100;
    bool newtripped;
    if (tripped){
      newtripped = true;
      return newtripped;
    }
    
    if (percent_temp_increase > 12 && last_current_reading < 0.1){
      newtripped = true;
      return newtripped;
    }

    if (last_current_reading > current_limit){
      newtripped = true;
      return newtripped;
    }

    newtripped = false;
    return newtripped;
  }

  return "-1";
}

float getTemperatureReading(int fuseNum)
{
  int tempSensorPin = -1;
  if (fuseNum == 1){
    tempSensorPin = f1tempSensorPin;
  }
  if (fuseNum == 2){
    tempSensorPin = f2tempSensorPin;
  }
  if (fuseNum == 3){
    tempSensorPin = f3tempSensorPin;
  }
  if (tempSensorPin != -1){
    int a = analogRead(tempSensorPin);
  
    float R = 1023.0/a-1.0;
    R = grove_R0*R;
  
    float temperature = 1.0/(log(R/grove_R0)/grove_B+1/298.15)-273.15; // convert to temperature via datasheet
  
    //Serial.print("temperature = ");
    //Serial.println(temperature);

    return temperature;
  }
  return 0;
}

float getCurrentReading(int fuseNum){
  int tempAmpPin = -1;
  if (fuseNum == 1){
    tempAmpPin = f1ampSensorPin;
  }
  if (fuseNum == 2){
    tempAmpPin = f2ampSensorPin;
  }
  if (fuseNum == 3){
    tempAmpPin = f3ampSensorPin;
  }
  if (tempAmpPin != -1){
    float current = 0.0;
    for (int i = 0; i < 500; i++){
      int val = analogRead(tempAmpPin);
      current = current + (amp_m * val) + amp_b;
    }
    return abs(current/500.0);
  }
  return 0;
}

void turnOnRelay(int fuseNum)
{
  int relayPin = -1;
  if (fuseNum == 1){
    relayPin = f1RelayPin;
  }
  if (fuseNum == 2){
    relayPin = f2RelayPin;
  }
  if (fuseNum == 3){
    relayPin = f3RelayPin;
  }
  if (relayPin != -1){
    digitalWrite(relayPin, HIGH);
  }
}

void turnOffRelay(int fuseNum)
{
  int relayPin = -1;
  if (fuseNum == 1){
    relayPin = f1RelayPin;
  }
  if (fuseNum == 2){
    relayPin = f2RelayPin;
  }
  if (fuseNum == 3){
    relayPin = f3RelayPin;
  }
  if (relayPin != -1){
    digitalWrite(relayPin, LOW);
  }
}

void updateLEDs()
{
  digitalWrite(latchPin, LOW);
  shiftOut(dataPin, clockPin, MSBFIRST, selected_pattern);
  digitalWrite(latchPin, HIGH);
  delay(50);
  digitalWrite(latchPin, LOW);
}

void updateLEDsBasedOnStatus()
{
  selected_pattern = B00000000;
  if (f1.tripped) {
    selected_pattern = selected_pattern | patterns[3];
  } else {
    selected_pattern = selected_pattern | patterns[0];
  }
  if (f2.tripped) {
    selected_pattern = selected_pattern | patterns[4];
  } else {
    selected_pattern = selected_pattern | patterns[1];
  }
  if (f3.tripped) {
    selected_pattern = selected_pattern | patterns[5];
  } else {
    selected_pattern = selected_pattern | patterns[2];
  }
  updateLEDs();
}

void printWifiStatus()
{
  // print the SSID of the network you're attached to
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

void publishCurrentReadings()
{
  char buffer[200];
  sprintf (buffer, "fuse=%d&current=%0.2f", f1.id, f1.last_current_reading);
  executePOSTQuery("POST /AirFuse/fuseCurrentReading/ HTTP/1.1", buffer);

  sprintf (buffer, "fuse=%d&current=%0.2f", f2.id, f2.last_current_reading);
  executePOSTQuery("POST /AirFuse/fuseCurrentReading/ HTTP/1.1", buffer);

  sprintf (buffer, "fuse=%d&current=%0.2f", f3.id, f3.last_current_reading);
  executePOSTQuery("POST /AirFuse/fuseCurrentReading/ HTTP/1.1", buffer);
  return;
}

void publishFuseStatus(int fuseNum)
{
  int fuse_id = -1;
  bool status = false;
  if (fuseNum == 1) {
    fuse_id = f1.id;
    status = f1.tripped;
  }
  if (fuseNum == 2) {
    fuse_id = f2.id;
    status = f2.tripped;
  }
  if (fuseNum == 3) {
    fuse_id = f3.id;
    status = f3.tripped;
  }
  if (fuse_id != -1) {
    char buffer[200];

    if (status) {
      sprintf (buffer, "fuse=%d&status=%s", fuse_id, "tripped");
    } else {
      sprintf (buffer, "fuse=%d&status=%s", fuse_id, "good");
    }
    executePOSTQuery("POST /AirFuse/fuseStatus/ HTTP/1.1", buffer);
  }

  return;
}

bool checkIfAirFuseExists()
{
  char buffer[64];
  //"GET /AirFuse/fuseBox/ HTTP/1.1"
  sprintf (buffer, "GET /AirFuse/fuseBoxName/%s/ HTTP/1.1", fusebox_name);
  String data = executeGetQuery(buffer);
  //Serial.println(buffer);
  //Serial.print(data);

  int colonIndex = data.indexOf(':');
  if (data.substring(colonIndex + 1) == "\"Not found.\"}") {
    return false;
  } else {
    return true;
  }
}

int getAirFuseId()
{
  char buffer[64];
  //"GET /AirFuse/fuseBox/ HTTP/1.1"
  sprintf (buffer, "GET /AirFuse/fuseBoxName/%s/ HTTP/1.1", fusebox_name);
  String data = executeGetQuery(buffer);
  //Serial.println(buffer);
  //Serial.print(data);

  int colonIndex = data.indexOf(':');
  if (data.substring(colonIndex + 1) == "\"Not found.\"}") {
    return -1;
  } else {
    int commaIndex = data.indexOf(',');
    //Serial.println(data.substring(colonIndex + 1, commaIndex));
    return data.substring(colonIndex + 1, commaIndex).toInt();
  }
}

bool verifyFusesOnServer()
{
  char buffer[64];
  sprintf (buffer, "GET /AirFuse/fuse/%d/ HTTP/1.1", id);
  String data = executeGetQuery(buffer);

  //Serial.println(data);
  int colonIndex = data.indexOf(':');
  if (data.substring(colonIndex + 1) == "\"Not found.\"}") {
    return false;
  } else {
    if (data == ""){
      createFusesOnServer();
    }
      
    sprintf (buffer, "GET /AirFuse/fuse/%d/ HTTP/1.1", id);
    data = executeGetQuery(buffer);

    //Serial.println(data);

    int idIndex = data.indexOf("id");
    data = data.substring(idIndex+4);
    int commaIndex = data.indexOf(",");
    f1.id = data.substring(0,commaIndex).toInt();
    
    idIndex = data.indexOf("id");
    data = data.substring(idIndex+4);
    commaIndex = data.indexOf(",");
    f2.id = data.substring(0,commaIndex).toInt();

    idIndex = data.indexOf("id");
    data = data.substring(idIndex+4);
    commaIndex = data.indexOf(",");
    f3.id = data.substring(0,commaIndex).toInt();
   
    return true;
  }
}

void createFusesOnServer()
{
  char buffer[200];
  sprintf (buffer, "fusebox=%d&name=%s&desc=%s&current_limit=%0.2f", id, f1.name, f1.desc, f1.current_limit);
  executePOSTQuery("POST /AirFuse/fuse/ HTTP/1.1", buffer);

  sprintf (buffer, "fusebox=%d&name=%s&desc=%s&current_limit=%0.2f", id, f2.name, f2.desc, f2.current_limit);
  executePOSTQuery("POST /AirFuse/fuse/ HTTP/1.1", buffer);

  sprintf (buffer, "fusebox=%d&name=%s&desc=%s&current_limit=%0.2f", id, f3.name, f3.desc, f3.current_limit);
  executePOSTQuery("POST /AirFuse/fuse/ HTTP/1.1", buffer);
  return;
}

void createAirFuseOnServer()
{
  char buffer[200];
  sprintf (buffer, "name=%s&password=%s&desc=%s", fusebox_name, password, desc);
  executePOSTQuery("POST /AirFuse/fuseBox/ HTTP/1.1", buffer);
  return;
}

String executeGetQuery(String request)
{
  Serial.println();
  Serial.println("Starting connection to server...");
  // if you get a connection, report back via serial
  if (client.connect(server, 80)) {
    Serial.println("Connected to server");
    // Make a HTTP request
    //client.println("GET /AirFuse/fuseBox/ HTTP/1.1");
    client.println(request);
    client.println("Host: django.utkarshsaini.com");
    client.println("Connection: close");
    client.println();
  }

  while (!client.available()) {}
  
//  while (client.available()) {
//    char c = client.read();
//    Serial.write(c);
//  }

  bool gather_data = false;
  String requested_data = "";
  while(client.available())
  {
    char c = (char)client.read();
    if (!gather_data) {
      if (c == '{') {
        c = (char)client.read();
        if (c == '"'){
          gather_data = true;
          requested_data.concat("{\"");
          c = (char)client.read();
        }
      }
      if (c == '[') {
        c = (char)client.read();
        if (c == '{') {
          c = (char)client.read();
          if (c == '"'){
            gather_data = true;
            requested_data.concat("[{\"");
            c = (char)client.read();
          }
        }
      }
    }
    if (gather_data) {
      requested_data.concat(c);
    }
  }
  
  // if the server's disconnected, stop the client
  if (!client.connected()) {
    Serial.println();
    Serial.println("Disconnecting from server...");
    client.stop();
  }

  return requested_data;
}


void executePOSTQuery(String request, String data)
{
  Serial.println();
  Serial.println("Starting connection to server...");
  // if you get a connection, report back via serial
  if (client.connect(server, 80)) {
    Serial.println("Connected to server");
    // Make a HTTP request
    //client.println("GET /AirFuse/fuseBox/ HTTP/1.1");
    client.println(request);
    client.println("Host: django.utkarshsaini.com");
    client.println("Accept: */*");
    client.println("Content-Length: " + String(data.length()));
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println();
    client.println(data);
  }
  
  Serial.println();
  Serial.println("Disconnecting from server...");
  client.stop();

  return;
}

