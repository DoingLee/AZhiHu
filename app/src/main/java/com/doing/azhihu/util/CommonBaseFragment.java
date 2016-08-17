package com.doing.azhihu.util;

import android.app.Fragment;
import android.view.View;

/**
 * Created by Doing on 2016/8/15 0015.
 */

/*
 //代替繁杂的findViewById
protected  <T extends View> T $(View rootView, int resId)

 */
public class CommonBaseFragment extends Fragment {


    public CommonBaseFragment() {
        // Required empty public constructor
    }

    /**
     *    findViewById
     *
     *    使用示例：
     *    View rootView = inflater.inflate(R.layout.fragment1,container, false);
     *    ibTakePicture = $(rootView, R.id.ib_camera_take_picture);
     */
    protected  <T extends View> T $(View rootView, int resId) {
        return (T) rootView.findViewById(resId);
    }

}
