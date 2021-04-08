package raydom.use_map;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Administrator on 2017-06-13.
 */

public class CameraPhoto extends Activity{
    public static final int PICK_IMAGE = 4;
    String mCurrentPhotoPath;

    /*public void callCamera(){

    }

    private File createImangeFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp + "_";
        // File StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        return image;
    }*/

    public Intent selectPicture(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        setResult(RESULT_OK,intent);

        return intent;
    }
}
