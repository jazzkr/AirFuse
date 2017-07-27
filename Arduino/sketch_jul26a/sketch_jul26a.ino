// Included Libraries
#include <WiFiEsp.h> // Note: that this was modified to work with Arduino 101
#include <WiFiEspClient.h>

#include <stdio.h>


// struct definitions
struct fuse {
  int id;
  int fusebox;
  char name[20];
  char desc[200];
  float current_limit;
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

uint8_t ledClkSHCPPin = 7;
uint8_t ledLatchSTCPPin = 6;
uint8_t ledDataDSPin = 5;

uint8_t f1RelayPin = 2;
uint8_t f2RelayPin = 3;
uint8_t f3RelayPin = 4;




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

  f1 = (fuse){-1, id, "fuse 1", "This+is+fuse+1+of+testFuseBox1.", 1.6};
  f2 = (fuse){-1, id, "fuse 2", "This+is+fuse+2+of+testFuseBox1.", 1.6};
  f3 = (fuse){-1, id, "fuse 3", "This+is+fuse+3+of+testFuseBox1.", 1.6};

  Serial.print("Verify fuses exists, if not create them: ");
  Serial.print(verifyFusesOnServer());
  Serial.println();
}




void loop() {
  

  
  while(true);
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

