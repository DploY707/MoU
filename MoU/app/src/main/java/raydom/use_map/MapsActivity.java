package raydom.use_map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.auth.Session;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Context context = this; // MapsActivity 의 context

    SQLiteDatabase db; // MoU DB
    DBHandler controller; // DB Helper for MoU
    DBOpenHelper helper;

    int total = 0;

    //data(tmp)

    private double LG,LT,DIY_LG,DIY_LT;

    private BackPressCloseHandler backPressCloseHandler;

    RelativeLayout L_menu;
    RelativeLayout mark_info;

    boolean mark_info_open = false;

    SendData send_mark;

    ImageView mBoundary;
    ImageView mark_image;

    Marker here; // my loc
    Marker DIY; // adding loc
    Marker tmp_marker;

    String mCurrentPhotoPath;
    String nickName;

    TextView mark_name;
    TextView adding;

    private GoogleMap mMap;
    ToggleButton mGPS;

    ImageButton mMENU;

    int category = 0;

    private float mDeclination; // get sensor's declination value
    private float[] mRotationMatrix = new float[9]; // get rotation value
    private SensorManager sensorManager; //to manage sensor
    private Sensor sensor; // sensor

    Geocoder geocoder;
    EditText et;

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(MapsActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == 1) { // get the User ID that login

            String resultMsg = data.getStringExtra("result_ID");
            nickName = resultMsg;

            mark_info.setVisibility(View.GONE);
            mark_info_open = false;

        } else if (requestCode == 4) {

            double lt = Double.parseDouble(data.getStringExtra("result_lt"));
            double lg = Double.parseDouble(data.getStringExtra("result_lg"));
            int ct = data.getIntExtra("result_category",0);
            String title = data.getStringExtra("result_title");
            int bm = 0;
            int id = 0 - total;
            String url = "http://i.imgur.com/NmPyWw4.png";

            controller.insert(id,lt, lg, title, url, bm, ct);

            Toast.makeText(this, "Marker Adding is success", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityForResult(new Intent(this,LoginActivity.class),1);
        startActivity(new Intent(this,ParsingActivity.class)); // getData from resource;

        LT = 37.49639;
        LG = 126.956889;

        super.onCreate(savedInstanceState);

        controller = new DBHandler(getApplicationContext());

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        send_mark = new SendData();

        mark_info = (RelativeLayout)findViewById(R.id.mark_info);

        mark_name = (TextView)findViewById(R.id.mark_name);

        mGPS = (ToggleButton)findViewById(R.id.mGPS);
        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));

        mMENU = (ImageButton)findViewById(R.id.m_menu);

        mark_image = (ImageView)findViewById(R.id.mark_image);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //make senseormanager available to use sensor service
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // make sensor available to use for magnetic field

        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    if(mGPS.isChecked()){
                        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_on_1));
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
                        sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST); //connect manager to listner

                        here.setVisible(true);
                    }
                    else{
                        lm.removeUpdates(mLocationListener);
                        mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));

                        sensorManager.unregisterListener(mSensorEventListener);

                        show_mark(category);

                        here.setVisible(false);
                    }
                }catch(SecurityException ex){

                }
            }
        });

        View t = findViewById(R.id.cover);
        t.setVisibility(View.GONE);

        Log.d("HashKey", "1");
        getAppKeyHash();

        geocoder = new Geocoder(this);
        et = (EditText)findViewById(R.id.Address);

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {

        TextView cover = (TextView)findViewById(R.id.cover);
        if(cover.getVisibility() == View.VISIBLE){
            cover.setVisibility(View.GONE);
            view_visible();
        }

        if(mark_info_open) {
            mark_info.setVisibility(View.GONE);
            mark_info_open = false;
        }

        WebView w = (WebView)findViewById(R.id.webView);
        if(w.getVisibility() == View.VISIBLE){
            w.setVisibility(View.GONE);
            view_visible();
        }

        backPressCloseHandler.onBackPressed();
    }

    private void getClick() { //터치를 통해 좌표를 받아오는 함수;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point){

                DIY_LT = point.latitude;
                DIY_LG = point.longitude;

                DIY = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(300)).position(new LatLng(point.latitude,point.longitude)).title("HERE?").zIndex(2.0f));
                DIY.setVisible(true);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DIY_LT,DIY_LG), 17.0f));

                mMap.setOnMapClickListener(null);

                RelativeLayout add_m = (RelativeLayout)findViewById(R.id.m_check2);
                add_m.bringToFront();
                add_m.setVisibility(View.VISIBLE);

                View t = findViewById(R.id.cover);
                t.setVisibility(View.GONE);
            }
        });
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            LG = longitude;
            LT = latitude;

            here.remove();

            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT,LG)).title("Title").zIndex(10.0f));
            here.setVisible(true);

            if(mGPS.isChecked()) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LT, LG), 17.0f));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private SensorEventListener mSensorEventListener = new SensorEventListener(){ //listen sensorevent
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){//when event is magnetic field event
                //Toast.makeText(MapsActivity.this,"OK",Toast.LENGTH_SHORT).show();
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values); // store event value to matrix
                float[] orientation = new float[3]; // for orientation
                SensorManager.getOrientation(mRotationMatrix,orientation);//make matrix oriented
                float bearing = (float)Math.toDegrees(orientation[0])+mDeclination; //caculate bearing

                if(mGPS.isChecked()) {
                    updatemap(bearing); //change map
                }
            }
            else{
                // Toast.makeText(MapsActivity.this,"waiting",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void updatemap(float bearing) {
        //CameraPosition oldPos = mMap.getCameraPosition();

        LatLng loc = new LatLng(LT, LG); //get current location
        CameraPosition oldPos = CameraPosition.fromLatLngZoom(loc, 17); //get cameraposition from loc
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();// build pos with bearing
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        startActivity(new Intent(this,SplashActivity.class));

        mMap = googleMap;

        LatLng Begin = new LatLng(37.49639,126.956889);

        here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(Begin).title("You").zIndex(10.0f));
        here.setVisible(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Begin));
        mMap.setMinZoomPreference(5);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 17.0f));

                if(marker.getPosition().latitude == here.getPosition().latitude && marker.getPosition().longitude == here.getPosition().longitude) {
                    return false;

                } else {
                    mark_info.setVisibility(View.VISIBLE);
                    mark_info_open = true;

                    tmp_marker = marker;

                    //마커 정보 보여주는 listener 구현 부
                    Picasso.with(context).load(get_url(marker.getPosition().latitude,marker.getPosition().longitude)).into(mark_image);

                    return false;
                }
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {

                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    mark_info.setVisibility(View.GONE);
                    mark_info_open = false;

                    mGPS.setBackgroundDrawable(getResources().getDrawable(R.drawable.gps_off_1));
                    mGPS.setChecked(false);

                    Log.d("TEST","Good1");
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
                }
            }
        });
    }

    //button event handle
    public void menu_b_clicked(View v){
        L_menu = (RelativeLayout)findViewById(R.id.lay_menu);
        if(L_menu.getVisibility() == View.GONE)
            L_menu.setVisibility(View.VISIBLE);
        else
            L_menu.setVisibility(View.GONE);
    }

    public void toilet_b_clicked(View v){

        if(category != 1) {
            no_effect();
            mBoundary = (ImageView) findViewById(R.id.toilet_effect);
            mBoundary.setVisibility(View.VISIBLE);

            category = 1;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }

    }

    public void wifi_b_clicked(View v){

        if(category != 2) {
            no_effect();
            mBoundary = (ImageView) findViewById(R.id.wifi_effect);
            mBoundary.setVisibility(View.VISIBLE);

            category = 2;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }
    }

    public void smoking_b_clicked(View v){

        if(category != 3) {
            no_effect();
            mBoundary = (ImageView) findViewById(R.id.smoking_effect);
            mBoundary.setVisibility(View.VISIBLE);

            category = 3;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }
    }

    public void landmark_b_clicked(View v){

        if(category != 4) {
            no_effect();
            mBoundary = (ImageView) findViewById(R.id.land_mark_effect);
            mBoundary.setVisibility(View.VISIBLE);

            category = 4;

        } else {
            no_effect();
        }

        mMap.clear();
        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }
    }

    public void personal_b_clicked(View v){

        if(category != 5) {
            no_effect();
            mBoundary = (ImageView) findViewById(R.id.personal_effect);
            mBoundary.setVisibility(View.VISIBLE);

            category = 5;

        } else {
            no_effect();
        }

        show_mark(category);

        if(mGPS.isChecked()) {
            here = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.here_1)).position(new LatLng(LT, LG)).title("You").zIndex(10.0f));
            here.setVisible(true);
        }
    }

    public void add_b_clicked(View v){
        if(Session.getCurrentSession().isClosed()){
            Intent intent = new Intent(getBaseContext(), AuthActivity.class);
            startActivity(intent);
        }else {

            Toast.makeText(this,"Click screen you want to add",Toast.LENGTH_SHORT).show();

            adding = (TextView) findViewById(R.id.pattern);
            adding.setVisibility(View.VISIBLE);
            mMap.clear();

            View t = findViewById(R.id.cover);
            t.setVisibility(View.VISIBLE);
            view_invisible();

            getClick();
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HashKey", "1");
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("HashKey", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    public void setting_clicked(View v){
        Intent intent = new Intent(getBaseContext(), Setting.class);
        intent.putExtra("EXTRA_SESSION_ID", nickName);
        startActivityForResult(intent,1);
    }

    public void add_ok_clicked(View v){

        send_mark.sendData2("http://39.115.18.101/login3.php",Double.toString(DIY_LT),Double.toString(DIY_LG));

        show_mark(category);

        DIY.setVisible(false);

        RelativeLayout add_m = (RelativeLayout)findViewById(R.id.m_check2);
        add_m.setVisibility(View.GONE);

        adding = (TextView)findViewById(R.id.pattern);
        adding.setVisibility(View.GONE);

        view_visible();

        Intent ADD = new Intent(this,ADD_Marker.class);

        ADD.putExtra("Result_LT",Double.toString(DIY_LT));
        ADD.putExtra("Result_LG",Double.toString(DIY_LG));

        startActivityForResult(ADD,4);
    }

    public void add_no_clicked(View v){

        DIY.setVisible(false);

        RelativeLayout add_m = (RelativeLayout)findViewById(R.id.m_check2);
        add_m.setVisibility(View.GONE);

        adding = (TextView)findViewById(R.id.pattern);
        adding.setVisibility(View.GONE);

        view_visible();
    }

    public void personal_add_clicked(View v) {

        Cursor c = controller.select_marker(tmp_marker.getPosition().latitude, tmp_marker.getPosition().longitude);
        startManagingCursor(c);
        //Cursor c = db.rawQuery("select * from Marker", null);
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("id"));
        Log.d("ddbb","id : " + id);
        int ct = c.getInt(c.getColumnIndex("category"));
        Log.d("ddbb","ct : " + ct);
        int bm = c.getInt(c.getColumnIndex("bm"));
        Log.d("ddbb","bm : " + bm);

        controller.set_personal(id,ct,bm);

        if(bm == 0 ) {
            tmp_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        } else {
            tmp_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    public void Search_clicked(View v){
        mGPS.setChecked(false);
        List<Address> list = null;
        String str = et.getText().toString();
        try{
            list = geocoder.getFromLocationName(str,10);
        }catch(IOException e){
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                //******************************************
                et.setText("일치하는 주소가 없습니다.");
                //********************************************
            } else {
                Address addr = list.get(0);
                LatLng loc = new LatLng(addr.getLatitude(),addr.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.setMinZoomPreference(17);
            }
        }
    }

    public void no_effect(){
        mark_info.setVisibility(View.GONE);
        mark_info_open = false;

        mBoundary = (ImageView) findViewById(R.id.toilet_effect);
        mBoundary.setVisibility(View.GONE);
        mBoundary = (ImageView) findViewById(R.id.wifi_effect);
        mBoundary.setVisibility(View.GONE);
        mBoundary = (ImageView) findViewById(R.id.smoking_effect);
        mBoundary.setVisibility(View.GONE);
        mBoundary = (ImageView) findViewById(R.id.land_mark_effect);
        mBoundary.setVisibility(View.GONE);
        mBoundary = (ImageView) findViewById(R.id.personal_effect);
        mBoundary.setVisibility(View.GONE);

        mMap.clear();

        category = 0;
    }

    public void view_invisible(){
        mark_info.setVisibility(View.GONE);
        mark_info_open = false;

        View b = findViewById(R.id.lay_menu);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.m_menu);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.mGPS);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.setting);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.SearchEngine);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.Address);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.Search);
        b.setVisibility(View.GONE);
    }

    public void view_visible(){
        View b = findViewById(R.id.lay_menu);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.m_menu);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.mGPS);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.setting);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.SearchEngine);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.Address);
        b.setVisibility(View.VISIBLE);
        b = findViewById(R.id.Search);
        b.setVisibility(View.VISIBLE);
    }

    public void detail_clicked(View v){
        String url = "";
        String host = "http://114.71.41.68";
        String add = "/help/map.php?";
        String lon = "longi=";
        String lat = "lati=";
        url = host + add + lon + Double.toString(tmp_marker.getPosition().latitude) + "&"+lat + Double.toString(tmp_marker.getPosition().longitude);
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        view_invisible();
        webView.setVisibility(View.VISIBLE);
    }

    public void copy_StoD_list(ArrayList<Double> a,ArrayList<String> b) {
        for(int i = 0 ; i < b.size() ; i ++) {
            a.add(Double.parseDouble(b.get(i)));
        }
    }

    public void copy_ItoI_list(ArrayList<Integer> a,ArrayList<Integer> b) {
        for(int i = 0 ; i < b.size() ; i ++) {
            a.add(b.get(i));
        }
    }

    // delete
    public void delete (double id) {
        db = helper.getWritableDatabase();
        db.delete("MarKer", "id=?", new String[]{String.valueOf(id)});
        Log.i("ddbb","정상적으로 삭제 되었습니다.");
    }

    // select

    public void show_mark(int category) {

        mMap.addMarker(new MarkerOptions().position(new LatLng(126.928117, 37.483867)).title("").zIndex(1.0f));

        Log.d("ddbb", "in show");
        Cursor c = controller.select_category(category);
        startManagingCursor(c);

        Log.d("ddbb", "get Cursor");

        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            int id = c.getInt(c.getColumnIndex("id"));
            double lt = c.getDouble(c.getColumnIndex("latitude"));
            double lg = c.getDouble(c.getColumnIndex("longitude"));
            String name = c.getString(c.getColumnIndex("name"));
            String url = c.getString(c.getColumnIndex("url"));
            int bm = c.getInt(c.getColumnIndex("bm"));

            mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)).title("").zIndex(1.0f));

            if(id < 0) {
                if (bm == 1) {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                } else {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                }
            } else {
                if (bm == 1) {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                } else {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)).title("").zIndex(1.0f));
                }
            }
        }

        c.close();
    }

    public String get_url(double latitude, double longitude) {
        Cursor c = controller.select_category(category);

        while (c.moveToNext()) {
            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.
            int id = c.getInt(c.getColumnIndex("id"));
            double lt = c.getDouble(c.getColumnIndex("latitude"));
            double lg = c.getDouble(c.getColumnIndex("longitude"));
            String name = c.getString(c.getColumnIndex("name"));
            String url = c.getString(c.getColumnIndex("url"));
            int bm = c.getInt(c.getColumnIndex("bm"));

            if(latitude == lt && longitude == lg) {
                return url;
            }
        }

        return "http://i.imgur.com/NmPyWw4.png";
    }
}
