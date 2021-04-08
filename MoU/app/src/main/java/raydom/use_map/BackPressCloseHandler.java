package raydom.use_map;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-06-16.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            activity.finish();
        }
    }
}
