#include <ESP8266WiFi.h>

int greenLedPin = 5;
int redLedPin = 14;
int blueLedPin = 12;
int buzzerPin = 4;

WiFiClient client;

void setup() {
  Serial.begin(9600);

  delay(200);

  pinMode(greenLedPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(blueLedPin, OUTPUT);

  Serial.println("start Wifi connection");

  connect();
}

void connect() {
  connectToWifi();
  connectToServer();
}

void connectToWifi() {
  String wifiName = "KellerWlan";
  String wifiPassword = "ThisPWis2good";

  WiFi.begin(wifiName, wifiPassword);

  Serial.print("connected to to WIfi: " + wifiName);
  
  Serial.print("\n");
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(blueLedPin, HIGH);
    delay(250);
    digitalWrite(blueLedPin, LOW);
    Serial.print(".");
    delay(250);
  }
  Serial.print("\n");

  Serial.println(WiFi.localIP());
}

void connectToServer() {
  if(client.connect("10.10.10.109", 1337)) {
    Serial.println("connected to server");
  } else {
    Serial.println("NOT connected to server");
  }
}
bool checkConnection() {
  if(WiFi.status() != WL_CONNECTED || !client.connected()) {
    digitalWrite(redLedPin, HIGH);
    digitalWrite(greenLedPin, LOW);
    connect();
    return false;
  }

    digitalWrite(redLedPin, LOW);
    digitalWrite(greenLedPin, HIGH);
  return true;
}

void loop() {
  if(!checkConnection()) {
    return;
  }

  String response = client.readStringUntil('\n');

  if(response == "") {
    return;
  }

  response.trim();

  if(response == "RING") {
    ring();
  }
}

void ring() {
  for(int i = 0; i < 5; i++) {
    soundBuzzer();
  }
}

void soundBuzzer() {
    digitalWrite(blueLedPin, HIGH);
    tone(buzzerPin, 1500);
    delay(500);
    tone(buzzerPin, 100);
    delay(500);§
    digitalWrite(blueLedPin, LOW);
    noTone(buzzerPin);
    delay(500); 
}
