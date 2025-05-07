#include <SPI.h>
#include <SD.h>
#include <LiquidCrystal_I2C.h>

#include <ArduinoJson.h>

#include <Base64.h>

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
String mcUsername;
String mcPassword;

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
    Serial.println("ERROR! Config: MC/HOSTNAME object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]["PORT"]) {
    Serial.println("ERROR! Config: MC/PORT object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]["USERNAME"]) {
    Serial.println("ERROR! Config: MC/USERNAME object could not be found in config!");
    while(1);
  }

  if(!confDoc["MC"]["CLIENTPASSWORD"]) {
    Serial.println("ERROR! Config: MC/CLIENTPASSWORD object could not be found in config!");
    while(1);
  }

  wifiSsid = confDoc["WIFI"]["SSID"].as<String>();
  wifiPass = confDoc["WIFI"]["PASSWORD"].as<String>();
  tcpHostname = confDoc["MC"]["HOSTNAME"].as<String>();
  tcpPort = confDoc["MC"]["PORT"].as<int>();
  mcUsername = confDoc["MC"]["USERNAME"].as<String>();
  mcPassword = confDoc["MC"]["CLIENTPASSWORD"].as<String>();

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

  if(!loginToTcpServer()) {
    while(1);
  }
}

String clientRead(unsigned long timeoutMs = 2000) {
  unsigned long start = millis();

  while (!wifiClient.available()) {
    if (millis() - start >= timeoutMs) {
      continue;
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

String encodeNetworkPackage(std::initializer_list<String> segments) {
  String package = "";
  int i = 0;
  for (auto& s : segments) {
    package += base64Encode(s);
    if (i < segments.size() - 1) {
      package += ',';
    }
    i++;
  }
  return package;
}

int decodeNetworkPackage(String package, String output[], int maxSegments) {
  int index = 0;
  int lastIndex = 0;

  while (index < maxSegments) {
    int delimiterIndex = package.indexOf(delimiter, lastIndex);
    if (delimiterIndex == -1) {
      output[index++] = base64Decode(package.substring(lastIndex));
      break;
    }

    output[index++] = base64Decode(package.substring(lastIndex, delimiterIndex));
    lastIndex = delimiterIndex + 1;
  }

  return index; 
}

String base64Encode(String input) {
  int inputLen = input.length();
  char inputBuf[inputLen + 1];
  input.toCharArray(inputBuf, sizeof(inputBuf));

  int outputLen = Base64.encodedLength(inputLen);
  char encoded[outputLen + 1];

  Base64.encode(encoded, inputBuf, inputLen);
  encoded[outputLen] = '\0';
  return String(encoded);
}

String base64Decode(String encodedStr) {
  int inputLen = encodedStr.length();
  char encodedBuf[inputLen + 1];
  encodedStr.toCharArray(encodedBuf, sizeof(encodedBuf));

  int outputLen = Base64.decodedLength(encodedBuf, inputLen);
  char decoded[outputLen + 1];

  Base64.decode(decoded, encodedBuf, inputLen);
  decoded[outputLen] = '\0';
  return String(decoded);
}

bool loginToTcpServer() {
  String loginPackage = encodeNetworkPackage({"LOGIN", mcUsername, mcPassword});

  wifiClient.println(loginPackage);

  String response = clientRead();

  if (response == "") {
    Serial.println("ERROR! Login: No response or timeout during login!");
    return false;
  }

  String package[10];
  int pLength = decodeNetworkPackage(response, package, 10);

  if(pLength != 2 && package[0] != "LOGIN") {
    Serial.println("ERROR! Login: Malformed Login Package!");
    return false;
  }

  if(package[1] != "ACK") {
    Serial.println("ERROR! Login: Wrong Credentials!");
    return false;
  }

  Serial.println("Login: ALL OK LOGIN WORKED");
  return true;
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
}

void loop() {
  if(!checkConnections()) {
    return;
  }

  String response = clientRead();

  /*if (response == "") {
    Serial.println("ERROR! LOOP: No response or timeout!");
    return;
  }*/

  String package[10];
  int pLength = decodeNetworkPackage(response, package, 10);

  if(pLength < 1) {
    Serial.println("ERROR! LOOP: Malformed package!");
    return;
  }

  Serial.println(package[0]);

  if(package[0] == "RING") {
    Serial.println("RING RING RING");
    ring();
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