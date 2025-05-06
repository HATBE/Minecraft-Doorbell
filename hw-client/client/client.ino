#include <SPI.h>
#include <SD.h>
#include <LiquidCrystal_I2C.h>

#include <ArduinoJson.h>

#include <WiFiS3.h> 

LiquidCrystal_I2C lcd(0x27,16,2);
WiFiClient wifiClient;

int buzzerPin = 7;
int redLedPin = 5;
int greenLedPin = 6;
int blueLedPin = 2;

// TODO: ERROR HANDLER
//list<String> errors;

String wifiSsid;
String wifiPass;
String tcpHostname;
int tcpPort;

char delimiter = ',';

void loadConfig() {
  Serial.println("SD: Start initializing SD card...");
  if(!SD.begin(4)) {
    Serial.println("ERROR! SD: Initialization of SD card failed!");
    while(1);
  }
  Serial.println("SD: Initialization of SD card successfully done.");

  File configFile = SD.open("CONF");

  if(!configFile) {
    Serial.println("ERROR! SD: Configfile could not be found!");
    while(1);
  }

  String fileContent = "";

  while(configFile.available()) {
    fileContent += (char) configFile.read();
  }

  JsonDocument confDoc;
  deserializeJson(confDoc, fileContent);

  if(!confDoc["WIFI"]) {
    Serial.println("ERROR! Config: WIFI object could not be found in config!");
    while(1);
  }

  if(!confDoc["WIFI"]["SSID"]) {
    Serial.println("ERROR! Config: WIFI/SSID object could not be found in config!");
    while(1);
  }

  if(!confDoc["WIFI"]["PASSWORD"]) {
    Serial.println("ERROR! Config: WIFI/PASSWORD object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]) {
    Serial.println("ERROR! Config: MC object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]) {
    Serial.println("ERROR! Config: MC object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]["HOSTNAME"]) {
    Serial.println("ERROR! Config: WIFI/PASSWORD object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]["PORT"]) {
    Serial.println("ERROR! Config: WIFI/PASSWORD object could not be found in config!");
    while(1);
  }

  wifiSsid = confDoc["WIFI"]["SSID"].as<String>();
  wifiPass = confDoc["WIFI"]["PASSWORD"].as<String>();
  tcpHostname = confDoc["MC"]["HOSTNAME"].as<String>();
  tcpPort = confDoc["MC"]["PORT"].as<int>();

  configFile.close();
}

void connectToWifi() {
  Serial.println("Wifi: Start initialization of Wifi.");

  if(WiFi.status() == WL_NO_MODULE) {
    Serial.println("ERROR! Wifi: Communication with WiFi module failed!");
    while (1); 
  }

  Serial.println("Wifi: checks ok.");

  int wifiStatus = WL_IDLE_STATUS;

  // TODO: timeout
  while (wifiStatus != WL_CONNECTED) {
    wifiStatus = WiFi.begin(wifiSsid.c_str(), wifiPass.c_str()); 
    delay(500); // wait 1 seconds for new connection attempt
  }

  delay(1000); // delay for DHCP (for slow systems)) // otherwise 0.0.0.0

  Serial.println("Wifi: Connection Successfull! IP: " + WiFi.localIP().toString());
}

void connectToTCPServer() {
  Serial.println("TCP: Start connection.");

  if(!wifiClient.connect(tcpHostname.c_str(), tcpPort)) {
    Serial.println("ERROR! TCP: Error while connectiong to TCP server!");
  }

  Serial.println("TCP: Successfully connected to " + tcpHostname + ":" + tcpPort);
}

String clientRead(unsigned long timeoutMs = 2000) {
  unsigned long start = millis();

  while (!wifiClient.available()) {
    if (millis() - start >= timeoutMs) {
      Serial.println("ERROR! TCP: Timeout waiting for response.");
      return "";
    }
    delay(10);
  }

  String response = wifiClient.readStringUntil('\n');
  response.trim();
  return response;
}

void textToScreen(String line1, String line2 = "") {
  if(line1.length() > 16) {
    textToScreen("TEXT TO LONG", line2);
    return;
  }
  if(line2.length() > 16) {
    textToScreen(line1, "TEXT TO LONG");
    return;
  }

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(line1);
  lcd.setCursor(0, 1);
  lcd.print(line2);
}

void loginToTcpServer() {
  wifiClient.println("TEEST");

  String response = clientRead();
  /*if (response == "") {
    Serial.println("No response or timeout during login.");
    return;
  }*/

  Serial.println("Server response: " + response);
}

bool checkConnections() {
  if(WiFi.status() != WL_CONNECTED) {
    Serial.println("ERROR! WIFI: connection lost!");
    connectToWifi();
    return false;
  }
  if(!wifiClient.connected()) {
    Serial.println("ERROR! TCP: connection lost!");
    connectToTCPServer();
    return false;
  }
  return true;
}

void initPins() {
  pinMode(buzzerPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(blueLedPin, OUTPUT);
}

void initLcd() {
  lcd.init();
  lcd.backlight();
}

void setup() {
  Serial.begin(9600);

  initPins();
  initLcd();

  loadConfig();

  connectToWifi();
  connectToTCPServer();
  
  loginToTcpServer();

  textToScreen("ashduijasghuizdhgasgdhjasgd", "123");
}

void loop() {
  if(!checkConnections()) {
    return;
  }


  // TODO:
}

void ring() {
  //textToScreen("The bell is ringing");
  for(int i = 0; i < 2; i++) {
    for(int j = 0; j < 3; j++) {
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
    delay(1500);
  }
}