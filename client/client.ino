#include <ESP8266WiFi.h>

int ledPin = 5;
int buzzerPin = 4;

WiFiClient client;

void setup() {
  Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);

  WiFi.begin("KellerWlan", "ThisPWis2good");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println(WiFi.localIP());

  connectToServer();
}

void connectToServer() {
  if(client.connect("10.10.10.109", 1337)) {
    Serial.println("connected to server");
  } else {
    Serial.println("NOT connected to server");
  }
}

void loop() {
  if(client.connected()) {
     String response = client.readStringUntil('\n');

      if(response == "") {
        return;
      }

      response.trim();

      Serial.println(response);

      if(response == "RING") {
        Serial.println("RING RING RING");
          digitalWrite(ledPin, HIGH);
          soundBuzzer();
          digitalWrite(ledPin, LOW);
      }
  } else {
    connectToServer();
  }
}

void soundBuzzer() {
  for(int i = 0; i < 5; i++) {
    tone(buzzerPin, 1500);
    delay(500);
    tone(buzzerPin, 100);
    delay(500);
    noTone(buzzerPin);
    delay(500);
  }
}
