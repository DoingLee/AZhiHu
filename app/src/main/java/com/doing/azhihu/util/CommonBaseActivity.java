package com.doing.azhihu.util;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 //代替繁杂的findViewById
 protected  <T extends View> T $(int resId)
 */

public class CommonBaseActivity extends AppCompatActivity {

    private static final String BASE_TAG = CommonBaseActivity.class.getSimpleName();

    /**
     *    findViewById
     *
     *    使用示例：
     *    ibTakePicture = $(R.id.ib_camera_take_picture);
     */
    protected  <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

}
