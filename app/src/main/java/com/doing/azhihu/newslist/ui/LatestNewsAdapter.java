package com.doing.azhihu.newslist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doing.azhihu.MainActivity;
import com.doing.azhihu.R;
import com.doing.azhihu.newscontent.ui.NewsContentFragment;
import com.doing.azhihu.data.Constants;
import com.doing.azhihu.data.LatestNewsEntity;
import com.doing.azhihu.newslist.views.BannerLayout;

import java.util.List;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public class LatestNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<LatestNewsEntity.TopStoriesEntity> topStoriesEntities;
    private List<LatestNewsEntity.StoriesEntity> storiesEntities;
    private Context mContext;
    private static final int TYPE_TOP_STORIES = 0;
    private static final int TYPE_STORIES = 1;

    public LatestNewsAdapter( Activity context, LatestNewsEntity latestNewsEntity)
    {
        this.mContext = context;
        topStoriesEntities = latestNewsEntity.getTop_stories();
        storiesEntities = latestNewsEntity.getStories();
        LatestNewsEntity.StoriesEntity date = new LatestNewsEntity.StoriesEntity();
        date.setType(Constants.ITEM_TYPE_DATE);
        date.setTitle("今日热闻");
        storiesEntities.add(0, date);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType )
    {
        if (viewType == TYPE_TOP_STORIES) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_top_stories, viewGroup, false);
            return  new TopStoriesViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_latest_stories, viewGroup, false);
            return new StoriesViewHolder(itemView);
        }
    }

    boolean isFirst = true; //banner只加载一次，回滚时不用重新加载（因为每次加载banner都会根据广告条数绘制banner的小圆点；这里是避免多次重复绘制banner的小圆点）
    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, final int position )
    {
        if(viewHolder instanceof TopStoriesViewHolder){
            if (isFirst){  //只有在第一次时加载
                isFirst = false;
                final String[] imageUrls = new String[topStoriesEntities.size()];
                final String[] titles = new String[topStoriesEntities.size()];
                final int[] ids = new int[topStoriesEntities.size()];
                View.OnClickListener[] onClickListeners = new View.OnClickListener[topStoriesEntities.size()];
                for (int j = 0; j < topStoriesEntities.size(); j++) {
                    imageUrls[j] = topStoriesEntities.get(j).getImage();
                    titles[j] = topStoriesEntities.get(j).getTitle();
                    ids[j] = topStoriesEntities.get(j).getId();
                    final int n = j;  //定值，供内部类使用
                    onClickListeners[j] = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(mContext,titles[n],Toast.LENGTH_SHORT).show();
                            if(mContext instanceof MainActivity){
                                Fragment newsContentFragment = new NewsContentFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt(Constants.NEWS_ID, ids[n]);
                                newsContentFragment.setArguments(bundle);
                                ((MainActivity) mContext).getFragmentManager().beginTransaction()
                                        .add(R.id.container_main, newsContentFragment, Constants.LATEST_NEWS_FRAGMENT)
                                        .addToBackStack(Constants.LATEST_NEWS_FRAGMENT)
                                        .commit();

                            }
                        }
                    };
                }

                ((TopStoriesViewHolder) viewHolder).bannerLayout.setViewWithUrls(imageUrls);
                ((TopStoriesViewHolder) viewHolder).bannerLayout.setImageDescriptions(titles);
                ((TopStoriesViewHolder) viewHolder).bannerLayout.setImageOnClickListeners(onClickListeners);
                ((TopStoriesViewHolder) viewHolder).bannerLayout.startScroll();
            }

        }else if (viewHolder instanceof  StoriesViewHolder){
            final LatestNewsEntity.StoriesEntity storiesEntity = storiesEntities.get(position - 1);

            if(storiesEntity.getType() == Constants.ITEM_TYPE_DATE){
                ((StoriesViewHolder) viewHolder).layoutLatestNewsTitle.setVisibility(View.GONE);
                ((StoriesViewHolder) viewHolder).tvLatestNewsDate.setVisibility(View.VISIBLE);
                ((StoriesViewHolder) viewHolder).tvLatestNewsDate.setText(storiesEntity.getTitle());
                //把item颜色设置为与背景色一样
                ((StoriesViewHolder) viewHolder).layoutLatestNewsItem.setBackgroundColor(mContext.getResources().getColor(R.color.light_item_background));
            }else {
                ((StoriesViewHolder) viewHolder).layoutLatestNewsTitle.setVisibility(View.VISIBLE);
                ((StoriesViewHolder) viewHolder).tvLatestNewsDate.setVisibility(View.GONE);
                ((StoriesViewHolder) viewHolder).tvLatestNewsTitle.setText(storiesEntity.getTitle());
                //把item颜色设置为与背景色不同
                ((StoriesViewHolder) viewHolder).layoutLatestNewsItem.setBackgroundColor(mContext.getResources().getColor(R.color.light_item));
                Glide.with(mContext)
                        .load(storiesEntity.getImages().get(0))
                        .placeholder(mContext.getResources().getDrawable(R.drawable.place_holder))
                        .error(mContext.getResources().getDrawable(R.drawable.place_holder))
                        .into(((StoriesViewHolder) viewHolder).ivLatestNewsTitle);

                ((StoriesViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(mContext,storiesEntity.getTitle(),Toast.LENGTH_SHORT).show();
                        if(mContext instanceof MainActivity){
                            Fragment newsContentFragment = new NewsContentFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.NEWS_ID, storiesEntity.getId());
                            newsContentFragment.setArguments(bundle);
                            ((MainActivity) mContext).getFragmentManager().beginTransaction()
                                    .add(R.id.container_main, newsContentFragment, Constants.LATEST_NEWS_FRAGMENT)
                                    .addToBackStack(Constants.LATEST_NEWS_FRAGMENT)
                                    .commit();

                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TOP_STORIES;
        else
            return TYPE_STORIES;
    }

    @Override
    public int getItemCount()
    {
        if(storiesEntities == null)
            return 0;
        else
            return storiesEntities.size() + 1;
    }

    public static class StoriesViewHolder  extends RecyclerView.ViewHolder
    {
        LinearLayout layoutLatestNewsBackground;
        FrameLayout layoutLatestNewsItem;
        TextView tvLatestNewsDate;
        RelativeLayout layoutLatestNewsTitle;
        TextView tvLatestNewsTitle;
        ImageView ivLatestNewsTitle;
        View itemView;

        public StoriesViewHolder( View itemView )
        {
            super(itemView);
            this.itemView = itemView;
            layoutLatestNewsBackground = (LinearLayout) itemView.findViewById(R.id.layout_latest_news_background);
            layoutLatestNewsItem = (FrameLayout) itemView.findViewById(R.id.layout_latest_news_item);
            tvLatestNewsDate = (TextView) itemView.findViewById(R.id.tv_latest_news_date);
            layoutLatestNewsTitle = (RelativeLayout) itemView.findViewById(R.id.layout_latest_news_title);
            tvLatestNewsTitle = (TextView) itemView.findViewById(R.id.tv_latest_news_title);
            ivLatestNewsTitle = (ImageView) itemView.findViewById(R.id.iv_latest_news_title);
        }
    }


    public static class TopStoriesViewHolder extends  RecyclerView.ViewHolder
    {
        BannerLayout bannerLayout;
        public TopStoriesViewHolder(View itemView) {
            super(itemView);
            bannerLayout = (BannerLayout) itemView.findViewById(R.id.banner_top_stories);
        }
    }

    public void addNewsList(List<LatestNewsEntity.StoriesEntity> entities){
        storiesEntities.addAll(entities);
//        notifyDataSetChanged();
    }

}
