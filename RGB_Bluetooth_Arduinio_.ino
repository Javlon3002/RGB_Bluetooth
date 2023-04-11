
String full_command = "";
#define RED 9
#define GREEN 10
#define BLUE 11

void setup() {

  Serial.begin(9600);
  pinMode(RED, OUTPUT);
  pinMode(GREEN, OUTPUT);
  pinMode(BLUE, OUTPUT);

}

void loop() {
  if (Serial.available()) {
    char c = Serial.read();
    if (c != '\n') {
      full_command += c;
    } else {
      Serial.println(full_command);
      setColor(full_command);
      full_command = "";
    }

  }

}


void setColor(String command) {
  if (command.charAt(0) == 'R') {
    
  
    analogWrite(RED, command.substring(2).toInt());
  }
  if (command.charAt(0) == 'G') {
    analogWrite(GREEN, command.substring(2).toInt());
  }

  if (command.charAt(0) == 'B') {
    analogWrite(BLUE, command.substring(2).toInt());
  }

}


