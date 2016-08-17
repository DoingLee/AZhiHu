package com.doing.azhihu.newscontent.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.doing.azhihu.R;
import com.doing.azhihu.data.Constants;
import com.doing.azhihu.data.NewsContentEntity;
import com.doing.azhihu.newscontent.presenter.INewsContentHelper;
import com.doing.azhihu.newscontent.presenter.NewsContentHelper;
import com.doing.azhihu.util.AndroidUtils;
import com.doing.azhihu.util.CommonBaseFragment;
import com.doing.azhihu.util.NetStateUtil;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public class NewsContentFragment  extends CommonBaseFragment implements INewsContentView {

    private View rootView;
    private ImageView ivContentTitleImage;
    private CollapsingToolbarLayout collapsingToolbarContent;
    private WebView webViewContent;

    private INewsContentHelper newsContentHelper;
    private int newsId;

    public NewsContentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsContentHelper = new NewsContentHelper(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_news_content, container, false);
        newsId = getArguments().getInt(Constants.NEWS_ID);
        initViews();
        init();
        return rootView;
    }

    private void initViews(){
        ivContentTitleImage = $(rootView, R.id.iv_content_title_image);
        collapsingToolbarContent = $(rootView, R.id.collapsing_toolbar_content);
        webViewContent = $(rootView,R.id.web_view_content);
    }

    private void init(){
        newsContentHelper.loadNewsContent(newsId);

        webViewContent.getSettings().setJavaScriptEnabled(true);
//        webViewContent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
//        webViewContent.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
//        webViewContent.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
//        webViewContent.getSettings().setAppCacheEnabled(true);
        AndroidUtils.showProgressDialog(getActivity(), "loading...");
    }

    @Override
    public void upDateView(boolean isSuccess, NewsContentEntity newsContent) {
        AndroidUtils.dismissProgressDialog();

        if(isSuccess == true){
            showNewsContent(newsContent);
        } else{
            if(! NetStateUtil.isConnected(getActivity())){
                AndroidUtils.showShortSnackBar(rootView, "网络连接不可用！");
            }
            else{
                AndroidUtils.showShortSnackBar(rootView, "网络请求失败！");
            }
        }
    }

    private void showNewsContent(NewsContentEntity newsContentEntity){
        collapsingToolbarContent.setTitle(newsContentEntity.getTitle());
        Glide.with(getActivity())
                .load(newsContentEntity.getImage())
                .placeholder(getActivity().getResources().getDrawable(R.drawable.place_holder))
                .error(getActivity().getResources().getDrawable(R.drawable.place_holder))
                .into(ivContentTitleImage);

        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + newsContentEntity.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        webViewContent.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

}
