package office.drive.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.receiveTextview);

        startService();

        Intent intent = getIntent();
        if (intent == null) {
            Log.i("Message:" , "No exist Intent");
        }
        else {
            String message = intent.getStringExtra("message");
            setReceivedMessage(message);
        }
    }

    public void startService () {
        Intent intent = new Intent(this, MessageService.class); //서비스 생성. 모두 인텐트에 넘겨줌
        startService(intent);                                   //서비스 시작
    }

    //activity 전환
    public void sendMessage(View v) {
        Intent intent = new Intent(this, SendMessageActivity.class);
        startActivity(intent);
    }

    public void setReceivedMessage(String message) {
        textView.setText(message);
    }
}
