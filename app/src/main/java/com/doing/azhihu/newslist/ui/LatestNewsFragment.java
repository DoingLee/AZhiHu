package com.doing.azhihu.newslist.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doing.azhihu.R;
import com.doing.azhihu.data.BeforeNewsEntity;
import com.doing.azhihu.data.Constants;
import com.doing.azhihu.data.LatestNewsEntity;
import com.doing.azhihu.newslist.presenter.ILatestNewsHelper;
import com.doing.azhihu.newslist.presenter.LatestNewsHelper;
import com.doing.azhihu.util.AndroidUtils;
import com.doing.azhihu.util.CommonBaseFragment;
import com.doing.azhihu.util.DateUtils;
import com.doing.azhihu.util.NetStateUtil;

import java.util.List;

public class LatestNewsFragment extends CommonBaseFragment implements ILatestNewsView
{
    View rootView;
    SwipeRefreshLayout swipeRefreshLayoutLatestNews;
    RecyclerView recyclerViewLatestNews;

    ILatestNewsHelper latestNewsHelper;
    LatestNewsAdapter latestNewsAdapter;
    LinearLayoutManager recyclerViewLinearLayoutManager;
    String date; //当前recyclerView的显示news的日期，用于获取before news

    public LatestNewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latestNewsHelper = new LatestNewsHelper(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_news_list, container, false);
        initViews();
        return rootView;
    }

    private void initViews(){
        swipeRefreshLayoutLatestNews = $(rootView,R.id.swipe_refresh_layout_latest_news);
        swipeRefreshLayoutLatestNews.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayoutLatestNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                latestNewsHelper.loadLatestNews(); //从网络加载并刷新ui
            }
        });

        recyclerViewLatestNews = $(rootView, R.id.recycler_view_latest_news);
        recyclerViewLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewLatestNews.setLayoutManager(recyclerViewLinearLayoutManager);
        recyclerViewLatestNews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLatestNews.addOnScrollListener(new EndlessRecyclerOnScrollListener(recyclerViewLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                latestNewsHelper.loadBeforeNews(date); //传入当前news日期，以获得before news
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        latestNewsHelper.loadLatestNews();
        AndroidUtils.showProgressDialog(getActivity(), "loading...");
    }

    /**
     * 由presenter调用更新UI
     * @param latestNewsEntity
     */
    @Override
    public void upDateView(boolean isSuccess, LatestNewsEntity latestNewsEntity) {
        AndroidUtils.dismissProgressDialog();
        if(isSuccess == true){
            showLatestNews(latestNewsEntity);
            date = latestNewsEntity.getDate(); //第一次加载，初始化date

        } else{
            if(! NetStateUtil.isConnected(getActivity())){
                AndroidUtils.showShortSnackBar(rootView, "网络连接不可用！");

                if(swipeRefreshLayoutLatestNews.isRefreshing())
                    swipeRefreshLayoutLatestNews.setRefreshing(false);
            }
            else{
                AndroidUtils.showShortSnackBar(rootView, "网络请求失败！");

                if(swipeRefreshLayoutLatestNews.isRefreshing())
                    swipeRefreshLayoutLatestNews.setRefreshing(false);
            }
        }
    }

    private void showLatestNews(LatestNewsEntity latestNewsEntity){
        latestNewsAdapter = new LatestNewsAdapter(getActivity(),latestNewsEntity);
        recyclerViewLatestNews.setAdapter(latestNewsAdapter);

        if(swipeRefreshLayoutLatestNews.isRefreshing())
            swipeRefreshLayoutLatestNews.setRefreshing(false);
    }

    /**
     * 由presenter调用更新UI
     * @param isSuccess
     * @param beforeNewsEntity
     */
    @Override
    public void loadMoreView(boolean isSuccess, BeforeNewsEntity beforeNewsEntity) {
        if(isSuccess == true){
            List<LatestNewsEntity.StoriesEntity> storiesEntities = beforeNewsEntity.getStories();
            //加入日期头
            LatestNewsEntity.StoriesEntity dateEntity = new LatestNewsEntity.StoriesEntity();
            String dateString = DateUtils.dateToString(DateUtils.stringToDate(beforeNewsEntity.getDate(), "yyyyMMdd"), "yyyy年MM月dd日");
            dateEntity.setTitle(dateString);
            dateEntity.setType(Constants.ITEM_TYPE_DATE);
            storiesEntities.add(0, dateEntity);

            date = beforeNewsEntity.getDate();  //重新设置date
            latestNewsAdapter.addNewsList(storiesEntities);
            latestNewsAdapter.notifyDataSetChanged();

        } else{
            if(! NetStateUtil.isConnected(getActivity()))
                AndroidUtils.showShortSnackBar(rootView, "网络连接不可用！");
            else
                AndroidUtils.showShortSnackBar(rootView, "网络请求失败！");
        }

    }
}
