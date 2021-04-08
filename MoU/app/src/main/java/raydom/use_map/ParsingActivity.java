package raydom.use_map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LeeSunjun on 2017-05-27.
 */

    /*
     *php server 로 부터 data를 받아옴
     */

public class ParsingActivity extends Activity {

    SQLiteDatabase db; // MoU DB
    DBHandler controller; // DB Helper for MoU

    ArrayList<String> m_lglist = new ArrayList<String>();
    ArrayList<String> m_ltlist = new ArrayList<String>();
    ArrayList<Integer> m_category = new ArrayList<Integer>();
    ArrayList<String> m_title = new ArrayList<String>();
    ArrayList<String> m_png = new ArrayList<String>();
    ArrayList<Integer> m_MarkID = new ArrayList<Integer>();

    String myJSON;
    JSONArray mark_info = null;

    int total = 0;

    ArrayList<String> tmp_json = new ArrayList<String>();

    private static final String TAG_LT = "latitude";
    private static final String TAG_LG = "longitude";
    private static final String TAG_TITLE = "name";
    private static final String TAG_PNG = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        controller = new DBHandler(getApplicationContext());

        /*
        //getData("http://114.71.41.68/publicdata/toilet1.php",1);

        //getData("http://114.71.41.68/publicdata/toilet2.php",1);

        getData("http://114.71.41.68/publicdata/wifi.php",2);

        getData("http://114.71.41.68/publicdata/smoking.php",3);

        getData("http://114.71.41.68/publicdata/statue.php",4);

        parsing_data("toilet_DJ.csv",1);
        parsing_data("toilet_GA.csv",1);

        parsing_data2("LM_statue.csv",4);
        */


        //for csv
        parsing_data("SmokingArea.csv",3);
        parsing_data("wifi_DJ.csv",2);

        parsing_data("toilet_DJ.csv",1);
        parsing_data("toilet_GA.csv",1);

        parsing_data2("LM_statue.csv",4);

        initialize();

        //for csv until here
    }


    /*
     * csv파일로 부터 데이터 받아옴
     */
    public void parsing_data(String file_name,int category) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(file_name)));

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {

                Log.d("ddbb", " 1 : " + nextLine[0] + " 2 : " + nextLine[1] + " 3 : " + category);

                controller.insert(total, Double.parseDouble(nextLine[1]), Double.parseDouble(nextLine[0]), "", "http://i.imgur.com/cWOF3ct.jpg", 0, category);

                total++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsing_data2(String file_name,int category) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(file_name)));

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {

                controller.insert(total, Double.parseDouble(nextLine[2]), Double.parseDouble(nextLine[1]), "", "", 0, category);

                total++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsing_diy() {

    }

    /*
     *php server 로부터 json 형식으로 데이터를 받아올때 tag 구분하여 Arraylist에 저장
     */
    public void parse_php(int category) {

        try {

                JSONArray JA = new JSONArray(myJSON);
                //JSONArray JA = new JSONArray(tmp_json.get(j));

                mark_info = JA;

                for (int i = 0; i < mark_info.length(); i++) {
                JSONObject c = mark_info.getJSONObject(i);
                String LT = c.getString(TAG_LT);
                String LG = c.getString(TAG_LG);
                String NAME = c.getString(TAG_TITLE);
                String PNG = c.getString(TAG_PNG);

                m_lglist.add(LG);
                m_ltlist.add(LT);
                m_category.add(category);
                m_title.add(NAME);
                m_png.add(PNG);
            }

                Intent intent = new Intent();

                intent.putStringArrayListExtra("result_lglist", m_lglist);
                intent.putStringArrayListExtra("result_ltlist", m_ltlist);
                intent.putIntegerArrayListExtra("result_category", m_category);
                intent.putStringArrayListExtra("result_NAME", m_title);
                intent.putStringArrayListExtra("result_PNG", m_png);

                setResult(RESULT_OK, intent);
                initialize();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
     * anysc task 방식으로 php와의 통신(데이터를 받아옴)
     */
    public void getData(String url, final int category) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            String tmp;

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                       // Log.d("SUNJUN", "length " + sb.toString().length());
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                parse_php(category);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);

        }

    private void initialize(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,0);
    }
}
