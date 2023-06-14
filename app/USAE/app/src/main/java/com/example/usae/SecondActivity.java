package com.example.usae;

import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import android.Manifest;
        import android.app.DatePickerDialog;
        import android.content.Intent;
        import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
        import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
        import android.speech.RecognizerIntent;
        import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.GregorianCalendar;

public class SecondActivity extends AppCompatActivity {

    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;	//permission 변수

    boolean recording = false;  //현재 녹음중인지 여부
    TextView textView;
    ImageButton recordButton;
    Socket socket;
    PrintWriter writer;
    // 전송할 정수형 변수
    int destination;
    private static int DELAY_TIME = 3000; // 3초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        CheckPermission();  //녹음 퍼미션 체크

        //UI
        recordButton = findViewById(R.id.recordButton);
        textView=findViewById(R.id.textView);
        recordButton.setOnClickListener(click);

        recordButton.setEnabled(false);
        //RecognizerIntent 객체 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recordButton.setImageResource(R.drawable.activate_button);
                textView.setText("USAE가 준비되었습니다\n버튼을 눌러주세요");
                recordButton.setEnabled(true);
            }
        }, 3000);

        //버튼 클릭 이벤트 리스터 등록
    }

    //녹음 시작 버튼이 눌린다면
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //녹음 버튼
                case R.id.recordButton:
                    if (!recording) {   //녹음 시작
                        StartRecord();
                        Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {  //이미 녹음 중이면 녹음 중지
                        StopRecord();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    RecognitionListener listener = new RecognitionListener() {
        //비어 있는 함수 구문은 보고서에도 생략해도 되고 이해하는 것도 생략해도 될듯
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
        }

        @Override
        // 음성 인식 도중 문제가 발생할 경우 예외 처리 정의 (예외 처리에 대한 단어는 검색해보는게 좋을거야)
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: //어플을 처음 실행했을때 사진 접근, 파일 접근 ,마이크 접근 권한 확인시 거절 누르면 예외 처리 발생
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            textView.setText("USAE와 연결에 실패했습니다\n재접속 해주세요");
            recordButton.setImageResource(R.drawable.unconnect_button);

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {
            //음성 인식을 하여 matches에 음성 인식 내용을 저장함.
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);	//인식 결과를 담은 ArrayList
            Toast.makeText(getApplicationContext(), "변환중", Toast.LENGTH_SHORT).show();

            //음성 인식 내용의 자료형이 arraylist인데 이를 string으로 변환시켜주는 과정
            String newText="";
            for (int i = 0; i < matches.size() ; i++) {
                newText += matches.get(i);
            }
            //인식 받은 내용중에 우리가 원하는 단어가 있는지 찾아서 명령을 인식하는 방식
            if(newText.contains("일") || newText.contains("1")) {
                textView.setText("1번 위치로 이동합니다.");
                destination = 1;
                //이 코드들은 인식한 명령을 로봇에게 전달하기 위한 코드.
                BackGroundTask b1 = new BackGroundTask();
                b1.execute();
//                destination=0;
//                Toast.makeText(getApplicationContext(), "1번 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
            } else if (newText.contains("이") || newText.contains("2")) {
                textView.setText("2번 위치로 이동합니다.");
                destination = 2;
                BackGroundTask b2 = new BackGroundTask();
                b2.execute();
//                destination=0;
//                Toast.makeText(getApplicationContext(), "2번 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
            } else if(newText.contains("삼") || newText.contains("3")) {
                textView.setText("3번 위치로 이동합니다.");
                destination=3;
                BackGroundTask b3 = new BackGroundTask();
                b3.execute();
            } else if(newText.contains("원위치") || newText.contains("홈")) {
                textView.setText("원래 위치로 이동합니다.");
                destination=4;
                BackGroundTask b5 = new BackGroundTask();
                b5.execute();
        }

            speechRecognizer.startListening(intent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    //퍼미션 체크
    void CheckPermission() {
        //안드로이드 버전이 6.0 이상
        if ( Build.VERSION.SDK_INT >= 23 ){
            //인터넷이나 녹음 권한이 없으면 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO},PERMISSION);
            }
        }
    }

    //녹음 시작
    void StartRecord() {
        recording = true;

        //마이크 이미지와 텍스트 변경
        textView.setText("음성인식 중입니다.");
        recordButton.setImageResource(R.drawable.deactivate_button);

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;
        destination=0;
        // 녹은 중지 버튼을 눌렀을때 로봇을 정지시키게 하기 위해서 정시 신호를 담은 통신을 보냄.
        BackGroundTask b4 = new BackGroundTask(); // 정지하기 위해 통신을 버튼을 눌러 음성 인식을 종료할때도 소켓 통신을 보냄
        b4.execute();
        //마이크 이미지와 텍스트 변경
        textView.setText("USAE가 준비되었습니다\n버튼을 눌러주세요");
        recordButton.setImageResource(R.drawable.activate_button);
        speechRecognizer.stopListening();   //녹음 중지
//        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

    // 소켓 통신 기능이 있는 곳.
    class BackGroundTask extends AsyncTask<Void, Void, Void> {
        Handler handler = new Handler();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (socket == null || socket.isClosed()) {
                    // IP 주소와 포트 번호에 맞게 수정
                    socket = new Socket("172.20.10.10", 6000);
                    writer = new PrintWriter(socket.getOutputStream());
                    Log.i("i", "CONNECTED");
                }
                // destination을 전역 변수로 지정해 두었는데, 음성 명령을 하게 되면 어픙에서 명령을 필터링 해서 해당 명령을 의미하는 숫자를 desination에 저장하도록 구현함.
                //1: 1번 위치 , 2: 2번 위치, 3: 3번 위치, 4: 원위치, 0: 종료 신호
                //그래서 전달하고자 하는 메시지에 destination이라는 변수를 담아
                String message = String.valueOf(destination);
                //전송
                writer.write(message);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}