package raydom.use_map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

/**
 * Created by LEe_SunJun on 2017-04-29.
 */


/*
 * 앱 실행후 가장 먼저 logo가 출력되는 activity
 * 이때 gps권한도 함께 얻는다
 */
public class SplashActivity extends Activity {

    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view);

        initialize();

        permission_ctrl();
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void getPermissionAll(){
        int PERMISSION_ALL = 1;
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }


    /*
     * GPS 권한을 얻어옴(network provider, gps provider)
     */
    public void permission_ctrl() {
        if (!hasPermissions(this, PERMISSIONS)) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                getPermissionAll();
            } else {
                getPermissionAll();
            }
        }
    }
}
