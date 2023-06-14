#include "motor_driver.h"
#include <RF24.h>

volatile bool fired1;
volatile bool up1;

volatile bool fired2;
volatile bool up2;

volatile bool fired3;
volatile bool up3;


// 엔코더
const byte encoder1PinA = 2;
const byte encoder1PinB = 3;

const byte encoder2PinA = 18;
const byte encoder2PinB = 19;

const byte encoder3PinA = 20;
const byte encoder3PinB = 21;


// 모터 

// 방향 조절
const int moter1Dir = 22;
const int moter2Dir = 24;
const int moter3Dir = 26;

// 속도 조절
const int moter1Pwm = 13;
const int moter2Pwm = 12;
const int moter3Pwm = 11;

motorDriver M1(moter1Dir, moter1Pwm);
motorDriver M2(moter2Dir, moter2Pwm);
motorDriver M3(moter3Dir, moter3Pwm);

#define PI 3.14159265
float R = 82;
int scaling_factor = 255;

RF24 radio(7, 8); // SPI 버스에 nRF24L01 라디오를 설정하기 위해 CE, CSN 선언.
const byte address[6] = "00001"; //주소값을 5가지 문자열로 변경할 수 있으며, 송신기과 수신기가 동일한 주소로 해야됨.

void setup() {
  pinMode(moter1Dir, OUTPUT);
  pinMode(moter1Pwm, OUTPUT);

  pinMode(moter2Dir, OUTPUT);
  pinMode(moter2Pwm, OUTPUT);

  pinMode(moter3Dir, OUTPUT);
  pinMode(moter3Pwm, OUTPUT);

  radio.begin();
  radio.openReadingPipe(0, address);
  radio.setPALevel(RF24_PA_LOW); //전원공급에 관한 파워레벨을 설정합니다. 모듈 사이가 가까우면 최소로 설정합니다.

//거리가 가까운 순으로 RF24_PA_MIN / RF24_PA_LOW / RF24_PA_HIGH / RF24_PA_MAX 등으로 설정할 수 있습니다.
  radio.startListening(); //모듈을 수신기로 설정
  pinMode(encoder1PinA, INPUT_PULLUP);
  pinMode(encoder1PinB, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(encoder1PinA), isr1, CHANGE);

  pinMode(encoder2PinA, INPUT_PULLUP);
  pinMode(encoder2PinB, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(encoder2PinA), isr2, CHANGE);

  pinMode(encoder3PinA, INPUT_PULLUP);
  pinMode(encoder3PinB, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(encoder3PinA), isr3, CHANGE);
  
  Serial.begin(115200);

  // 모터 3개 작동 테스트

  // digitalWrite(moter1Dir,HIGH);
  // analogWrite(moter1Pwm,100);
  // digitalWrite(moter2Dir,HIGH);
  // analogWrite(moter2Pwm,100);
  // digitalWrite(moter3Dir,LOW);
  // analogWrite(moter3Pwm,100);
  // delay(500);
  // stop_now();
}

// 엔코더 ISR
void isr1 () {
  if (digitalRead(encoder1PinA)) {
    up1 = digitalRead(encoder1PinB);
  } else {
    up1 = !digitalRead (encoder1PinB);
  }
  fired1 = true;
}

void isr2 () {
  if (digitalRead(encoder2PinA)) {
    up2 = digitalRead(encoder2PinB);
  } else {
    up2 = !digitalRead (encoder2PinB);
  }
  fired2 = true;
}

void isr3 () {
  if (digitalRead(encoder3PinA)) {
    up3 = digitalRead(encoder3PinB);
  } else {
    up3 = !digitalRead (encoder3PinB);
  }
  fired3 = true;
}

void calc_speed(float x_dot, float y_dot, float theta_dot){
  float PWM1 = -R*theta_dot + x_dot;
  float PWM2 = -R*theta_dot - 0.5*x_dot - sin(PI/3.0)*y_dot;
  float PWM3 = -R*theta_dot - 0.5*x_dot + sin(PI/3.0)*y_dot;

  int Dir1 = (PWM1 > 0) - (PWM1 < 0);
  int Dir2 = (PWM2 > 0) - (PWM2 < 0);
  int Dir3 = -((PWM3 > 0) - (PWM3 < 0));

  PWM1 = (int)abs(PWM1*scaling_factor);
  PWM2 = (int)abs(PWM2*scaling_factor);
  PWM3 = (int)abs(PWM3*scaling_factor);

  M1.spin(Dir1, PWM1);
  M2.spin(Dir2, PWM2);
  M3.spin(Dir3, PWM3);
}

void loop() {
  char text[32] = "";
  if (radio.available()) {
    char text[32] = "";
    radio.read(&text, sizeof(text));
    Serial.println(text);
     if (strcmp(text, "high") == 0) {
      Serial.println("front");
      calc_speed(0,1,0);
    }
    else if (strcmp(text, "low") == 0) {
      Serial.println("back");
      calc_speed(0,-1,0);
    }
    else if (strcmp(text, "left") == 0) {
      Serial.println("left1");
      calc_speed(1,0,0);
    }
    else if (strcmp(text, "right") == 0) {
      Serial.println("right1");
      calc_speed(-1,0,0);
    }
    else if (strcmp(text, "rightcorner") == 0) {
      Serial.println("rightcorner");
      calc_speed(0,0,1);
    }
    else if (strcmp(text, "leftcorner") == 0) {
      Serial.println("leftcorner");
      calc_speed(0,0,-1);
    }
    else {
      calc_speed(0,0,0);
    }
    delay(100);
    calc_speed(0,0,0);
//    analogWrite(moter1Pwm,0);
//    analogWrite(moter2Pwm,0);
//    analogWrite(moter3Pwm,0);

  }
  static long rotaryCount1 = 0;
  static long rotaryCount2 = 0;
  static long rotaryCount3 = 0;

  if (fired1) {
    if (up1) {
      rotaryCount1 ++;
    } else {
      rotaryCount1 --;
    }
    fired1 = false;
    Serial.print("Count1 = ");
    Serial.println(rotaryCount1);
  }
  if (fired2) {
    if (up2) {
      rotaryCount2 ++;
    } else {
      rotaryCount2 --;
    }
    fired2 = false;
    Serial.print("Count2 = ");
    Serial.println(rotaryCount2);
  }
  if (fired3) {
    if (up3) {
      rotaryCount3 ++;
    } else {
      rotaryCount3 --;
    }
    fired3 = false;
    Serial.print("Count3 = ");
    Serial.println(rotaryCount3);
  }
}
