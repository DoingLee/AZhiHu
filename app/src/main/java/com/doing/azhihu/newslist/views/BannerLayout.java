package com.doing.azhihu.newslist.views;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doing.azhihu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doing on 2016/8/10 0010.
 *
 * 注意要一一对应：
 * setImageDescriptions
 * setViewWithXXX
 * setImageOnClickListeners
 * startScroll
 */
public class BannerLayout extends RelativeLayout
{
    private RelativeLayout banner;
    private TextView tvImageDescription;
    private LinearLayout linearLayoutPointGroup;
    private ViewPager viewPager;

    private String[] imageDescriptions;
    private View.OnClickListener[] imageOnClickListeners;
    private List<ImageView> imageList;

    private int preEnablePosition = 0; // 前一个被选中的点的索引位置 默认情况下为0
    private boolean isStop = false;  //是否停止自动滚动计算子线程  不会停止

    private Handler mViewHandler ;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        banner = (RelativeLayout) layoutInflater.inflate(R.layout.banner_layout, this, true);
        init();
    }

    private void init(){
        viewPager = (ViewPager) banner.findViewById(R.id.viewpager);
        linearLayoutPointGroup = (LinearLayout) banner.findViewById(R.id.ll_point_group);
        tvImageDescription = (TextView) banner.findViewById(R.id.tv_image_miaoshu);

        mViewHandler = new Handler(Looper.getMainLooper());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int position) {
                // 取余后的索引
                int newPosition = position % imageList.size();

                // 根据索引设置图片的描述
                tvImageDescription.setText(imageDescriptions[newPosition]);

                // 把上一个点设置为被选中
                linearLayoutPointGroup.getChildAt(preEnablePosition).setEnabled(false);

                // 根据索引设置那个点被选中
                linearLayoutPointGroup.getChildAt(newPosition).setEnabled(true);

                preEnablePosition = newPosition;

            }
        });
//        viewPager.setOffscreenPageLimit(3); //http://stackoverflow.com/questions/13559353/how-to-solve-for-viewpager-the-specified-child-already-has-a-parent-you-must
    }

    /**
     *    initStatus需要用户设置（setImageDescriptions、setViewWithXXX、setImageOnClickListeners）后才能设置初始化状态
     */
    private void initStatus() {
        //此三个参数必须初始化
        if(imageDescriptions == null || imageOnClickListeners == null || imageList == null){
//            这里应该抛出异常
            throw new UnsupportedOperationException("BannerLayout 没有初始化成功！");
        }

        // 初始化图片描述和哪一个点被选中
        tvImageDescription.setText(imageDescriptions[0]);
        linearLayoutPointGroup.getChildAt(0).setEnabled(true);

        // 初始化viewpager的默认position.MAX_value的一半
        int index = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % imageList.size());
        viewPager.setCurrentItem(index); // 设置当前viewpager选中的pager页，会触发OnPageChangeListener中的onPageSelected方法

        // 开启线程无限自动移动
        Thread myThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isStop) {
                    //每个两秒钟发一条消息到主线程，更新viewpager界面
                    SystemClock.sleep(3000);
                    mViewHandler.post(new Runnable() {
                        public void run() {
                            // 此方法在主线程中执行
                            int newIndex = viewPager.getCurrentItem() + 1;
                            viewPager.setCurrentItem(newIndex);
                        }
                    });
                }
            }
        });
        myThread.start(); // 用来更细致的划分  比如页面失去焦点时候停止子线程恢复焦点时再开启
    }

    public void startScroll(){
        viewPager.setAdapter(new MyAdapter());
        initStatus();
    }

    public void cancelScrollThread() {
        isStop = true;
    }

    public void setImageDescriptions(String[] imageDescriptions){
        this.imageDescriptions = imageDescriptions;
    }

    public void setImageOnClickListeners(View.OnClickListener[] imageOnClickListeners){
        this.imageOnClickListeners = imageOnClickListeners;
    }

    public void setViewWithUrls(String[] urls){
        imageList = new ArrayList<ImageView>();

        //重新加入图片和点
        ImageView iv; //图片
        View dotView; //点
        LayoutParams params;

        for (String url : urls) {
            iv = new ImageView(getContext());
            Glide.with(getContext())
                    .load(url)// 加载图片资源
                    .placeholder(R.drawable.place_holder)// 占位图片
                    .centerCrop()//  fitCenter()
                    .into(iv);
            imageList.add(iv);

            // 每循环一次添加一个点到现形布局中
            dotView = new View(getContext());
            dotView.setBackgroundResource(R.drawable.point_background);
            params = new LayoutParams(5, 5);
            params.leftMargin = 5;
            dotView.setEnabled(false);
            dotView.setLayoutParams(params);
            linearLayoutPointGroup.addView(dotView); // 向线性布局中添加“点”
        }
    }

    public void setViewWithRes(int[] resourceIds){
        imageList = new ArrayList<ImageView>();

        //重新加入图片和点
        ImageView iv; //图片
        View dotView; //点
        LayoutParams params;
        for (int id : resourceIds) {
            iv = new ImageView(getContext());
            iv.setBackgroundResource(id);
            imageList.add(iv);

            // 每循环一次添加一个点到现形布局中
            dotView = new View(getContext());
            dotView.setBackgroundResource(R.drawable.point_background);
            params = new LayoutParams(5, 5);
            params.leftMargin = 5;
            dotView.setEnabled(false);
            dotView.setLayoutParams(params);
            linearLayoutPointGroup.addView(dotView); // 向线性布局中添加“点”
        }
    }

    class MyAdapter extends PagerAdapter {

        /**
         * 销毁对象
         *
         * @param position
         *            将要被销毁对象的索引位置
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageList.get(position % imageList.size()));
        }

        /**
         * 初始化一个对象
         *
         * @param position
         *            将要被创建的对象的索引位置
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = imageList.get(position % imageList.size());
            // 为每一个page添加点击事件
            view.setOnClickListener(imageOnClickListeners[position % imageList.size()]);

            //避免出现ViewPager问题：java.lang.IllegalStateException: The specified child already has a parent
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }

            ((ViewPager)container).addView(view,0);
            return view;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Integer.MAX_VALUE;
        }

        /**
         * 复用对象 true 复用对象 false 用的是object
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

    }
}
