package com.doing.azhihu.newslist.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    int lastVisibleItem;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int totalCount = mLinearLayoutManager.getItemCount();//RecyclerView中的总的条目的数量(此处代表的是可见的和不可见的总数)
        if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1==totalCount){
            //此处添加加载更多的代码，一般为取数据库或者加载网络等
            onLoadMore();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //每次滑动的时候要更新最后一条可见的item的id
        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
    }

    public abstract void onLoadMore();
}
