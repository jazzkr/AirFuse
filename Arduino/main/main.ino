/* AirFuse Main Arduino Code
 * By Krisztian K. and Utkarsh S.
 */
/* Includes */
#include <SPI.h>
#include <SD.h>

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
  
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);                       // wait for a second
  digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
  delay(1000);                       // wait for a second
}
