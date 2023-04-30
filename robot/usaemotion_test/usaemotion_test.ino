// 모터 동작 테스트

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
    // 1번 모터 전진
    digitalWrite(moter1Dir,HIGH);
    analogWrite(moter1Pwm,50);
    Serial.print("1");
    delay(2000);
    Serial.print("정지");
    analogWrite(moter1Pwm,0);

    // 2번 모터 전진
    digitalWrite(moter2Dir,HIGH);
    analogWrite(moter2Pwm,50);
    Serial.print("2");
    delay(2000);
    Serial.print("정지");
    analogWrite(moter2Pwm,0);

    digitalWrite(moter3Dir,LOW);
    analogWrite(moter3Pwm,50);
    Serial.print("3");
    delay(2000);
    analogWrite(moter3Pwm,0);
    Serial.print("정지");

    delay(4000);
    
    // 1번 모터 후진
    digitalWrite(moter1Dir,LOW);
    analogWrite(moter1Pwm,50);
    Serial.print("1");
    delay(2000);
    Serial.print("정지");
    analogWrite(moter1Pwm,0);

    // 2번 모터 후진
    digitalWrite(moter2Dir,LOW);
    analogWrite(moter2Pwm,50);
    Serial.print("2");
    delay(2000);
    Serial.print("정지");
    analogWrite(moter2Pwm,0);

    digitalWrite(moter3Dir,HIGH);
    analogWrite(moter3Pwm,50);
    Serial.print("3");
    delay(2000);
    analogWrite(moter3Pwm,0);
    Serial.print("정지");
}
