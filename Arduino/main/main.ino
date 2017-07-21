/* AirFuse Main Arduino Code
 * By Krisztian K. and Utkarsh S.
 */
/* Includes */
#include <SPI.h>
#include <SD.h>
#include <SparkFunESP8266WiFi.h>

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
// LED Shift Register
int led_data = 5;
int led_latch = 6;
int led_clk = 7;

// Communication
int esp_rx = 0;
int esp_tx = 1;
int sd_mosi = 11;
int sd_miso = 12;
int sd_sck = 13;

Sd2Card card;
SdVolume volume;
SdFile root;

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
  
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);                       // wait for a second
  digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
  delay(1000);                       // wait for a second
}
