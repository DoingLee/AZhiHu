package com.doing.azhihu;

import android.app.Fragment;
import android.os.Bundle;

import com.doing.azhihu.data.Constants;
import com.doing.azhihu.newslist.ui.LatestNewsFragment;
import com.doing.azhihu.util.CommonBaseActivity;

public class MainActivity extends CommonBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment latestNewsFragment = new LatestNewsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container_main, latestNewsFragment, Constants.LATEST_NEWS_FRAGMENT)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
