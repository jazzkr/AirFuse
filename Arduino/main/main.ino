/* AirFuse Main Arduino Code
 * By Krisztian K. and Utkarsh S.
 */
/* Includes */
#include <SPI.h>
#include <SD.h>
#include <SparkFunESP8266WiFi.h>
#include <CurieBLE.h>

/* Pin Assignments */
// Analog
int temp_1_pin = A0;
int temp_2_pin = A1;
int temp_3_pin = A2;
int current_1_pin = A3;
int current_2_pin = A4;
int current_3_pin = A5;
// Digital
int relay_1_pin = 2;
int relay_2_pin = 3;
int relay_3_pin = 4;
// NOTE: this doesn't match schematic, need to edit
int good_led_1_pin = 5;
int good_led_2_pin = 6;
int good_led_3_pin = 7;
int trip_led_1_pin = 8;
int trip_led_2_pin = 9;
int trip_led_3_pin = 10;
// Communication
int esp_rx = 0;
int esp_tx = 1;
int sd_mosi = 11;
int sd_miso = 12;
int sd_sck = 13;

Sd2Card card;
SdVolume volume;
SdFile root;

// Load configuration bits from memory (hardcoded for now)
bool firstTimeSetup = true;

// Set up Curie BLE
BLEPeripheral ble;
/*
Other usable UUIDs (generated @ https://www.uuidgenerator.net/)
987234d7-e132-4c29-aa39-fbc82ba8e1ee
b63fadc0-713e-4928-afed-a3e2fd9f86c3
64ea688c-4699-42d3-9cfe-571f85758b61
6e09fadf-fd17-4bf9-882b-64c8fe805452
9fca11ce-afcf-4d96-a324-7662a61c37e5
4a0617c6-36c6-4388-a21e-c29ddec784e7
d33e0ebf-395c-4c65-bc64-bfee8416ab5b
e7807690-3ce5-434e-97d2-33383ba5b23b
fd8f58b3-205c-4189-8600-f92fcd78503a
*/
BLEService testService("94758d00-7a46-4ce5-a11f-10d93808580c");
BLECharCharacteristic testCharacteristic("94758d01-7a46-4ce5-a11f-10d93808580c", BLERead | BLEWrite);

// the setup function runs once when you press reset or power the board
void setup() {
  
  // LED_BUILTIN for debugging
  pinMode(LED_BUILTIN, OUTPUT);
  
  // Analog Pin Setup
  pinMode(temp_1_pin, INPUT);
  pinMode(temp_2_pin, INPUT);
  pinMode(temp_3_pin, INPUT);
  pinMode(current_1_pin, INPUT);
  pinMode(current_2_pin, INPUT);
  pinMode(current_3_pin, INPUT);
  
  // Digital Pin Setup
  pinMode(relay_1_pin, OUTPUT);
  pinMode(relay_2_pin, OUTPUT);
  pinMode(relay_3_pin, OUTPUT);
  pinMode(good_led_1_pin, OUTPUT);
  pinMode(good_led_2_pin, OUTPUT);
  pinMode(good_led_3_pin, OUTPUT);
  pinMode(trip_led_1_pin, OUTPUT);
  pinMode(trip_led_2_pin, OUTPUT);
  pinMode(trip_led_3_pin, OUTPUT);
  
  // Serial USB console (the one we can see)
  Serial.begin(9600);
  
  // Set up ESP8266
  if (esp8266.begin(115200, Serial1) {
    Serial.println("ESP8266 OK");
  }
  else {
    Serial.println("ESP8266 FAIL");
  }
      
  // Set up BLE
  ble.setLocalName("AirFuseBLE");
  ble.setAdvertisedServiceUuid(testService.uuid());
      
  ble.addAttribute(testService);
  ble.addAttribute(testCharacteristic);
      
  if (firstTimeSetup) {
    ble.begin();
    Serial.println("BLE ON");
  }
  
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);                       // wait for a second
  digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
  delay(1000);                       // wait for a second
}
