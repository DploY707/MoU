package raydom.use_map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by LEe_SunJun on 2017-06-05.
 */

public class ADD_Marker extends Activity {

    RelativeLayout form;

    TextView skip;

    ImageView check;

    Drawable alpha;

    EditText m_name;
    EditText m_agency;
    RadioGroup group1;
    RadioGroup group2;
    EditText m_exp;
    EditText m_exp2;

    boolean image_check = false;

    ImageButton b;
    Bitmap send_bit;

    SendData sendData;

    String LG,LT;
    String option1,option2;

    int t_category = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ui);

        LT = getIntent().getStringExtra("Result_LT");
        LG = getIntent().getStringExtra("Result_LG");

        skip = (TextView)findViewById(R.id.skip);

        alpha = ((ImageView)findViewById(R.id.toilet_add_check)).getDrawable();
        alpha.setAlpha(90);
        alpha = ((ImageView)findViewById(R.id.wifi_add_check)).getDrawable();
        alpha.setAlpha(90);
        alpha = ((ImageView)findViewById(R.id.smoke_add_check)).getDrawable();
        alpha.setAlpha(90);
        alpha = ((ImageView)findViewById(R.id.landmark_add_check)).getDrawable();
        alpha.setAlpha(90);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        initialize();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode,Intent data) {
        CameraPhoto cp = new CameraPhoto();

        switch(requestCode) {
            case 4:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    image_check = true;

                    send_bit = BitmapFactory.decodeStream(imageStream);

                    b.setImageBitmap(send_bit);
                }
        }
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

    public void toilet_add_clicked(View v) {
        skip.setVisibility(View.GONE);

        no_effect();
        no_form();
        no_output();

        t_category = 1;

        b = (ImageButton)findViewById(R.id.pic_toilet_button);

        m_name = (EditText)findViewById(R.id.toilet_name_edit);
        group1 = (RadioGroup)findViewById(R.id.check_disabled);

        check = (ImageView)findViewById(R.id.toilet_add_check);
        check.setVisibility(View.VISIBLE);

        form = (RelativeLayout)findViewById(R.id.setting_toilet);
        form.setVisibility(View.VISIBLE);
    }

    public void wifi_add_clicked(View v) {
        skip.setVisibility(View.GONE);
        no_effect();
        no_form();
        no_output();

        t_category = 2;

        b = (ImageButton)findViewById(R.id.pic_wifi_button);

        m_name = (EditText)findViewById(R.id.wifi_name_edit);

        check = (ImageView)findViewById(R.id.wifi_add_check);
        check.setVisibility(View.VISIBLE);

        form = (RelativeLayout)findViewById(R.id.setting_wifi);
        form.setVisibility(View.VISIBLE);
    }

    public void smoke_add_clicked(View v) {
        skip.setVisibility(View.GONE);
        no_effect();
        no_form();
        no_output();

        t_category = 3;

        b = (ImageButton)findViewById(R.id.pic_smoke_button);

        m_name = (EditText)findViewById(R.id.smoke_name_edit);

        check = (ImageView)findViewById(R.id.smoke_add_check);
        check.setVisibility(View.VISIBLE);

        form = (RelativeLayout)findViewById(R.id.setting_smoke);
        form.setVisibility(View.VISIBLE);
    }

    public void landmark_add_clicked(View v) {
        skip.setVisibility(View.GONE);
        no_effect();
        no_form();
        no_output();

        t_category = 4;

        b = (ImageButton)findViewById(R.id.pic_landmark_button);

        m_name = (EditText)findViewById(R.id.landmark_name_edit);

        check = (ImageView)findViewById(R.id.landmark_add_check);
        check.setVisibility(View.VISIBLE);

        form = (RelativeLayout)findViewById(R.id.setting_landmark);
        form.setVisibility(View.VISIBLE);

        m_exp = (EditText)findViewById(R.id.landmark_name_edit);
    }

    public void trash_add_clicked(View v) {
        skip.setVisibility(View.GONE);
        no_effect();
        no_form();
        no_output();

        t_category = 9;

        b = (ImageButton)findViewById(R.id.pic_trash_button);

        m_name = (EditText)findViewById(R.id.trash_name_edit);

        check = (ImageView)findViewById(R.id.trash_add_check);
        check.setVisibility(View.VISIBLE);

        form = (RelativeLayout)findViewById(R.id.setting_trash);
        form.setVisibility(View.VISIBLE);

        m_exp2 = (EditText)findViewById(R.id.trash_explanation_edit);
    }

    public void cancel_clicked(View v) {
        initialize();
    }

    public int isEqual(int i, int j){
        if(i == j)
            return 0;
        else
            return 1;
    }

    public void submit_clicked(View v) {
        Intent intent = new Intent();

        intent.putExtra("result_category",t_category);
        intent.putExtra("result_title",m_name.getText());
        intent.putExtra("result_lt",LT);
        intent.putExtra("result_lg",LG);

        setResult(RESULT_OK,intent);

        Log.d("Url","-1");

        int data1 = -1; // nothing selected, first radio or only radio
        int data2 = -1; // nothing selected, second radio

        RadioGroup rg1;
        RadioGroup rg2;

        int selectedId;

        if(t_category == 1) {
            rg1 = (RadioGroup) findViewById(R.id.check_disabled);
            selectedId = rg1.getCheckedRadioButtonId();
            data1 = isEqual(selectedId, R.id.disabled_b_exist);

            rg2 = (RadioGroup) findViewById(R.id.check_unisex);
            selectedId = rg2.getCheckedRadioButtonId();
            data2 = isEqual(selectedId, R.id.unisex_b_yes);
        }else if(t_category == 2){
            rg1 = (RadioGroup) findViewById(R.id.check_locked);
            selectedId = rg1.getCheckedRadioButtonId();
            data1 = isEqual(selectedId, R.id.loc);
        }else if(t_category == 3) {
            rg1 = (RadioGroup) findViewById(R.id.ashtray);
            selectedId = rg1.getCheckedRadioButtonId();
            data1 = isEqual(selectedId, R.id.ashtray_b_exist);

            rg1 = (RadioGroup) findViewById(R.id.Booth_group);
            selectedId = rg1.getCheckedRadioButtonId();
            data2 = isEqual(selectedId, R.id.exist);
        }
        Toast.makeText(this,t_category + " : " + data1 + " : " + data2,Toast.LENGTH_SHORT).show();

        sendData = new SendData(t_category);

        String image_str = "";

        if(image_check) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            send_bit.compress(Bitmap.CompressFormat.PNG, 90, stream);
            byte[] byte_arr = stream.toByteArray();
            image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
        }

        if(t_category == 1) {
            String url = "http://114.71.41.68/image/toilet_upload.php";
            sendData.sendData3(url, LT, LG, m_name.getText().toString(), Integer.toString(data1), Integer.toString(data2), image_str);
        } else if(t_category == 2) {
            String url = "http://114.71.41.68/image/wifi_upload.php";
            sendData.sendData3(url, LT, LG, m_name.getText().toString(), Integer.toString(data1), m_agency.getText().toString(), image_str);
        } else if(t_category == 3) {
            String url = "http://114.71.41.68/image/smoke_upload.php";
            sendData.sendData3(url, LT, LG, m_name.getText().toString(), Integer.toString(data1), Integer.toString(data2), image_str);
        } else if(t_category == 4) {
            String url = "http://114.71.41.68/image/landmark_upload.php";
            sendData.sendData3(url, LT, LG, m_name.getText().toString(), m_exp.getText().toString(), "", image_str);
        } else if(t_category == 9) {
            String url = "http://114.71.41.68/image/trash_upload.php";
            sendData.sendData3(url, LT, LG, m_name.getText().toString(), m_exp2.getText().toString(), "", image_str);
        }

        Log.d("Url","99");
        initialize();
    }

    public  void pic_tb_clicked(View v) {
        CameraPhoto cp = new CameraPhoto();
        Intent intent = cp.selectPicture();
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),cp.PICK_IMAGE);
    }

    public  void pic_wb_clicked(View v) {
        CameraPhoto cp = new CameraPhoto();
        Intent intent = cp.selectPicture();
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),cp.PICK_IMAGE);
    }

    public  void pic_sb_clicked(View v) {
        CameraPhoto cp = new CameraPhoto();
        Intent intent = cp.selectPicture();
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),cp.PICK_IMAGE);
    }

    public  void pic_lb_clicked(View v) {
        CameraPhoto cp = new CameraPhoto();
        Intent intent = cp.selectPicture();
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),cp.PICK_IMAGE);
    }

    public  void pic_trb_clicked(View v) {
        CameraPhoto cp = new CameraPhoto();
        Intent intent = cp.selectPicture();
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),cp.PICK_IMAGE);
    }

    public void no_effect() {
        check = (ImageView)findViewById(R.id.toilet_add_check);
        check.setVisibility(View.GONE);
        check = (ImageView)findViewById(R.id.wifi_add_check);
        check.setVisibility(View.GONE);
        check = (ImageView)findViewById(R.id.smoke_add_check);
        check.setVisibility(View.GONE);
        check = (ImageView)findViewById(R.id.landmark_add_check);
        check.setVisibility(View.GONE);
    }

    public void no_form() {
        form = (RelativeLayout)findViewById(R.id.setting_toilet);
        form.setVisibility(View.GONE);
        form = (RelativeLayout)findViewById(R.id.setting_wifi);
        form.setVisibility(View.GONE);
        form = (RelativeLayout)findViewById(R.id.setting_smoke);
        form.setVisibility(View.GONE);
        form = (RelativeLayout)findViewById(R.id.setting_landmark);
        form.setVisibility(View.GONE);
    }

    public void no_output() {
        m_name = null;
        m_agency = null;
        group1 = null;
        group2 = null;
        m_exp = null;
        m_exp2 = null;
    }
}
