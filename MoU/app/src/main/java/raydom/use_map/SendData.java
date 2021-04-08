package raydom.use_map;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * php server로 데이터를 전송
 */

public class SendData {
    int type = -1; // 1 : logininfo(should be develope the control of login)
                  // 2 : adding info
                  // 3 : comment

    String LT_TAG = "longitude"; //longitude
    String LG_TAG = "latitude"; //latitude

    String uID_TAG = "ID"; //id
    String uPW_TAG = "PW"; //pw

    String mark_name_TAG = "space_name"; //name
    String mark_option1_TAG = ""; //option1
    String mark_option2_TAG = ""; //option2
    String mark_image_TAG = "image"; //file

    String comment_TAG = ""; //comment
    String grade_TAG = ""; //grade

    int check = 0;

    SendData(){}

    SendData(int type) {
        this.type = type;
    }

    public int get_check(){
        return check;
    }

    //send ID,PW data
    public void sendData1(String url, String id, String pw){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result4 : " + params[1]);
                    byte[] outputInBytes = params[1].getBytes("UTF-8");

                    Log.d("Ray","result5 : " + outputInBytes);

                    OutputStream os = conn.getOutputStream();

                    /*OutputStreamWriter writer = new OutputStreamWriter(os);
                    writer.write(URLEncoder.encode(params[1],"UTF-8"));
                    writer.flush();
                    writer.close();*/

                    os.write(outputInBytes);
                    os.flush();
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(uID_TAG, id);
                jsonObject.put(uPW_TAG, pw);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            Log.d("Ray","result 3 : "+json);

            new HttpUtil().execute(url,json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send LT,LG data
    public void sendData2(String url, String latitude, String longitude){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);
            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            new HttpUtil().execute(url,json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData3(String url, String latitude, String longitude, String name, String op1, String op2, String file){

        class HttpUtil extends AsyncTask<String, Void, Void> {

            @Override
            public Void doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type","application/json");

                    Log.d("Ray","result_url : " + params[1] );

                    byte[] outputInBytes = params[1].getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.flush();
                    os.close();

                    Log.d("Ray","result_url2 : " + params[1].getBytes("UTF-8") );

                    int retCode = conn.getResponseCode();

                    check = retCode;
                    Log.d("Ray","result1 : "+retCode);

                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    br.close();

                    String res = response.toString();

                    Log.d("Ray","result 2 : "+res);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();

            try {

                Log.d("Url","2");
                jsonObject.put(LT_TAG, latitude);
                jsonObject.put(LG_TAG, longitude);
                jsonObject.put(mark_name_TAG, name);

                mark_option1_TAG = "agency";
                mark_option2_TAG = "locked";

                jsonObject.put(mark_option1_TAG, op1);
                jsonObject.put(mark_option2_TAG, op2);
                Log.d("Url","5");

                Log.d("Url","3");
                if(type == 1) {
                    mark_option1_TAG = "disabled";
                    mark_option2_TAG = "unisex";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","4");
                } else if(type == 2) {
                    mark_option1_TAG = "agency";
                    mark_option2_TAG = "locked";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","5");
                } else if(type == 3) {
                    mark_option1_TAG = "ashytray";
                    mark_option2_TAG = "booth";

                    jsonObject.put(mark_option1_TAG, op1);
                    jsonObject.put(mark_option2_TAG, op2);
                    Log.d("Url","6");
                } else if(type == 4) {
                    mark_option1_TAG = "explanation";

                    jsonObject.put(mark_option1_TAG, op1);
                    Log.d("Url","7");
                }

                Log.d("Url","4");
                jsonObject.put(mark_image_TAG,file);
                Log.d("Url","5");

            }catch (JSONException e){

            }

            String json = jsonObject.toString();

            new HttpUtil().execute(url,json);
            Log.d("Url","10");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
