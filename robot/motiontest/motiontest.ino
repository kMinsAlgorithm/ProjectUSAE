//전후좌우 동작 테스트


// 방향 조절
const int moter1Dir = 22;
const int moter2Dir = 24;
const int moter3Dir = 26;

// 속도 조절
const int moter1Pwm = 13;
const int moter2Pwm = 12;
const int moter3Pwm = 11;

void setup() {
    pinMode(moter1Dir, OUTPUT);
    pinMode(moter1Pwm, OUTPUT);

    pinMode(moter2Dir, OUTPUT);
    pinMode(moter2Pwm, OUTPUT);

    pinMode(moter3Dir, OUTPUT);
    pinMode(moter3Pwm, OUTPUT);
    Serial.begin(115200);
    
}

void loop() {
    Serial.print("시작");
    
    delay(3000);
    
    // 전진
    digitalWrite(moter1Dir,HIGH);
    analogWrite(moter1Pwm,0);
    digitalWrite(moter2Dir,HIGH);
    analogWrite(moter2Pwm,150);
    digitalWrite(moter3Dir,HIGH);
    analogWrite(moter3Pwm,150);

    delay(3000);

    analogWrite(moter1Pwm,0);
    analogWrite(moter2Pwm,0);
    analogWrite(moter3Pwm,0);
    
    Serial.print("정지");

    //후진
    digitalWrite(moter1Dir,LOW);
    analogWrite(moter1Pwm,0);
    digitalWrite(moter2Dir,LOW);
    analogWrite(moter2Pwm,150);
    digitalWrite(moter3Dir,LOW);
    analogWrite(moter3Pwm,150);

    delay(3000);

    analogWrite(moter1Pwm,0);
    analogWrite(moter2Pwm,0);
    analogWrite(moter3Pwm,0);

    //우측
    digitalWrite(moter1Dir,HIGH);
    analogWrite(moter1Pwm,150);
    digitalWrite(moter2Dir,LOW);
    analogWrite(moter2Pwm,90);
    digitalWrite(moter3Dir,HIGH);
    analogWrite(moter3Pwm,90);

    delay(3000);

    analogWrite(moter1Pwm,0);
    analogWrite(moter2Pwm,0);
    analogWrite(moter3Pwm,0);

    //좌측
    digitalWrite(moter1Dir,LOW);
    analogWrite(moter1Pwm,150);
    digitalWrite(moter2Dir,HIGH);
    analogWrite(moter2Pwm,90);
    digitalWrite(moter3Dir,LOW);
    analogWrite(moter3Pwm,90);

    delay(3000);

    analogWrite(moter1Pwm,0);
    analogWrite(moter2Pwm,0);
    analogWrite(moter3Pwm,0);

    Serial.print("정지");

}
