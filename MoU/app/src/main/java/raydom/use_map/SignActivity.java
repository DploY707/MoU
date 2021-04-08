package raydom.use_map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

/**
 * Created by LEe_SunJun on 2017-05-18.
 */

/*
 *sign_up button 이 눌린경우 회원가입을 위한 web site 호출
 */
public class SignActivity extends Activity {
    WebView m_web;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        m_web = (WebView)findViewById(R.id.m_web);
        m_web.loadUrl("http://114.71.41.68/join/signUp.php");
    }

    private void initialize(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0,1500);
    }
}
