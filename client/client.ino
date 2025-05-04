#include <ESP8266WiFi.h>

int greenLedPin = 5;
int redLedPin = 14;
int blueLedPin = 12;
int buzzerPin = 4;

String delimiter = "/:/";

// \/ CONFIG START \/
String clientId = "35d3c3be-bf1a-4629-834b-43cb8aeb1fcc";
String clientPassword = "Qn$18v,8rXmt";
String wifiName = "KellerWlan";
String wifiPassword = "ThisPWis2good";
String serverHostname = "10.10.10.109";
uint16_t serverPort = 1337;
// /\ CONFIG START /\

WiFiClient client;
bool loggedIn = false;

void setup() {
  Serial.begin(9600);

  delay(200);

  pinMode(greenLedPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(blueLedPin, OUTPUT);

  connectToWifi();
  connectToServer();
}

bool connectToWifi() {
  Serial.println("start Wifi connection");

  WiFi.begin(wifiName, wifiPassword);

  Serial.print("connected to to WIfi: " + wifiName);
  
  // TODO: timeout on wifi connection! and then when timeout return false

  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(blueLedPin, HIGH);
    delay(250);
    digitalWrite(blueLedPin, LOW);
    Serial.print(".");
    delay(250);
  }
  Serial.print("\n");

  Serial.println("IP: " + WiFi.localIP().toString());
  return true;
}

bool connectToServer() {
  if(client.connect(serverHostname, serverPort)) {
    Serial.println("connected to tcp server: " + serverHostname + ":" + serverPort);

    // LOGIN
    client.println("LOGIN" + delimiter + clientId + delimiter + clientPassword);

    String response = clientRead();

    Serial.println(response);

    if(response != "ACK") {
      // denied login
      Serial.println("Login not successful!");
      ledErrorState();
      return false;
    } 

    ledSuccessState();

    // login successful
    // TODO:
    loggedIn = true;
    Serial.println("Login successful!");

    //Serial.println("resp: " + response);

    return true;
  } else {
    Serial.println("NOT connected to server");
    return false;
  }
}

void ledErrorState() {
  digitalWrite(redLedPin, HIGH);
  digitalWrite(greenLedPin, LOW);
}

void ledSuccessState() {
  digitalWrite(redLedPin, LOW);
  digitalWrite(greenLedPin, HIGH);
}

bool checkConnection() {
  if(WiFi.status() != WL_CONNECTED) {
    ledErrorState();
    connectToWifi();
    return false;
  }
  
  if(!client.connected()) {
   ledErrorState();
    connectToServer();
    return false;
  }

  ledSuccessState();
  return true;
}

String clientRead() {
  String response = client.readStringUntil('\n');

  response.trim();

  return response;
}

void loop() {
  if(!checkConnection()) {
    return;
  }

  if(!loggedIn) {
    return;
  }

  String response = clientRead();

  if(response == "") {
    return;
  }

  if(response == "RING") {
    ring();
  }
}

void ring() {
  for(int i = 0; i < 2; i++) {
    for(int j = 0; j < 3; j++) {
      soundBuzzer();
    }
    delay(1500);
  }
}

void soundBuzzer() {
  digitalWrite(blueLedPin, HIGH);
  tone(buzzerPin, 660);
  delay(700); 
  tone(buzzerPin, 550);
  delay(700); 
  digitalWrite(blueLedPin, LOW);
  tone(buzzerPin, 440); 
  delay(1000); 
  noTone(buzzerPin);
}
