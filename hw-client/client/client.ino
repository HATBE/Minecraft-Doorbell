#include <ESP8266WiFi.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

int greenLedPin = 15;
int redLedPin = 2;
int blueLedPin = 16;
int buzzerPin = 14;

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
bool lockedOut = false;

LiquidCrystal_I2C lcd(0x27,16,2);

void setup() {

  lcd.init();
  lcd.backlight();

  Serial.begin(9600);

  delay(200);

  pinMode(greenLedPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(blueLedPin, OUTPUT);

  connectToWifi();
  connectToServer();
}

void textToScreen(String line1, String line2 = "") {
  if(line1.length() > 16 || line2.length() > 16) {
    textToScreen("TEXT TO LONG");
    return;
  }
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(line1);
  lcd.setCursor(0, 1);
  lcd.print(line2);
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
      textToScreen("Wrong", "credentials");
      ledErrorState();
      lockedOut = true;
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
    textToScreen("Wifi error");
    connectToWifi();
    return false;
  }
  
  if(!client.connected()) {
    ledErrorState();
    textToScreen("TCP Conn Error");
    connectToServer();
    return false;
  }

  if(lockedOut) {
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

  Serial.println(response);

  if(response == "") {
    return;
  }

  if(response == "RING") {
    ring();
  }
}

void ring() {
  textToScreen("RING");
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
