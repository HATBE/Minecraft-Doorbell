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
  WiFi.begin("KellerWlan", "ThisPWis2good");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
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
  Serial.println("RING RING RING");
  digitalWrite(blueLedPin, HIGH);
  for(int i = 0; i < 5; i++) {
    soundBuzzer();
  }
  digitalWrite(blueLedPin, LOW);
}

void soundBuzzer() {
    tone(buzzerPin, 1500);
    delay(500);
    tone(buzzerPin, 100);
    delay(500);
    noTone(buzzerPin);
    delay(500); 
}
