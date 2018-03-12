package com.example.yc.saying.tabFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.ui.ArticleActivity;
import com.example.yc.saying.ui.PopularActivity;
import com.example.yc.saying.R;
import com.example.yc.saying.ui.SearchActivity;
import com.example.yc.saying.utils.VpSwipeRefreshLayout;
import com.example.yc.saying.adapter.Frag2RecyclerAdapter;
import com.example.yc.saying.adapter.MyPagerAdapter;
import com.example.yc.saying.model.article;
import com.example.yc.saying.model.popularActivities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class TabFragment2 extends Fragment {

    private View view;
    private VpSwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ScrollView scrollView;
    private Button search;

    // Bannar轮播
    private MyPagerAdapter mAdapter;
    private List<ImageView> mItems;
    private ImageView[] mBottomImages;
    private LinearLayout mBottomLiner;
    private ViewPager mViewPager;
    private int currentViewPagerItem;
    private String[] imageUrl = new String[3];
    private String[] popular_activity_id = new String[3];
    // 是否自动播放
    private boolean isAutoPlay;
    private MyHandler mHandler;
    private Thread mThread;

    // 热门内容板块
    private List<Integer> data = new ArrayList<>();//必须初始化
    private RecyclerView recyclerView;
    private Frag2RecyclerAdapter adapter;

    // 文章推荐
    private ListView listView;
    private List<Map<String,Object>> data_2 = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private DisplayImageOptions options; // 设置图片显示相关参数
  
    @Nullable  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment2,container,false);

        findView();
        initialization();
        bannarSlide();
        clickEvents();

        return view;
    }

    public void findView() {
        swipeRefreshLayout = (VpSwipeRefreshLayout) view.findViewById(R.id.refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        listView = (ListView) view.findViewById(R.id.listview);
        search = (Button) view.findViewById(R.id.search_button);
        mHandler = new MyHandler(this);
        //配置轮播图ViewPager
        mViewPager = ((ViewPager) view.findViewById(R.id.page));
        mItems = new ArrayList<>();
        mAdapter = new MyPagerAdapter(mItems, getApplicationContext());
        mViewPager.setAdapter(mAdapter);
        isAutoPlay = true;
        mBottomLiner = (LinearLayout) view.findViewById(R.id.live_indicator);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.upload) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.upload) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成

        scrollView = (ScrollView) view.findViewById(R.id.scroll);
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setEnabled(scrollView.getScrollY() == 0);
                    }
                }
            });
        }

    }

    public void initialization() {
        //寻找创建时间最近的三个活动图片
        BmobQuery<popularActivities> query = new BmobQuery("popularActivities");
        query.order("-createdAt");
        query.setLimit(3);//返回三条数据；
        query.findObjects(new FindListener<popularActivities>() {
            @Override
            public void done(List<popularActivities> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i ++) {
                        imageUrl[i] = list.get(i).getImage().getFileUrl();
                        popular_activity_id[i] = list.get(i).getObjectId();
                    }
                    //TODO: 添加ImageView
                    addImageView();
                    mAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mAdapter);
                    //设置底部4个小点
                    setBottomIndicator();
                } else {
                    Toast.makeText(getApplicationContext(), "热门活动查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 热门内容板块
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        data.add(R.mipmap.frag2_user_img);
        data.add(R.mipmap.frag2_saying_img);
        data.add(R.mipmap.frag2_note_img);
        data.add(R.mipmap.frag2_article_img);
        adapter = new Frag2RecyclerAdapter(data);
        recyclerView.setAdapter(adapter);

        // 文章推荐
        //寻找创建时间最近的三篇文章
        BmobQuery<article> query_2 = new BmobQuery("article");
        query_2.order("-createdAt");
        query_2.setLimit(3);//返回三条数据；
        query_2.findObjects(new FindListener<article>() {
            @Override
            public void done(List<article> list, BmobException e) {
                if (e == null) {
                    if (list != null) {
                        for (article t : list) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("objectId", t.getObjectId().toString());
                            temp.put("title", "『"+t.getTitle().toString());
                            temp.put("intro", t.getIntro().toString());
                            temp.put("image", t.getImage().getFileUrl());
                            data_2.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data_2, R.layout.article_item, new String[] {"objectId","title","intro","image"}, new int[] {R.id.objectId, R.id.title, R.id.intro, R.id.image});
                        // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                        // 利用imageloader框架，对SimpleAdapter进行处理
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
                                //判断是否为我们要处理的对象
                                if(view instanceof ImageView && data instanceof String){
                                    ImageView iv = (ImageView) view;
                                    ImageLoader.getInstance().displayImage((String) data, iv, options);
                                    return true;
                                }else
                                    return false;
                            }
                        });
                        listView.setAdapter(simpleAdapter);
                        setListViewHeightBasedOnChildren();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clickEvents() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //控件停留两秒
                            Thread.sleep(2000);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //更新数据
                                    mItems.clear();
                                    mBottomLiner.removeAllViews();
                                    mThread.interrupt();
                                    data.clear();
                                    data_2.clear();
                                    initialization();
                                    //停止
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), SearchActivity.class);
                startActivity(it);
            }
        });

        ///////////////////////////////////////////////////////////////////////////
        // ViewPager的监听事件
        ///////////////////////////////////////////////////////////////////////////

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isAutoPlay = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isAutoPlay = true;
                        break;
                }
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentViewPagerItem = position;
                if (mItems != null) {
                    position %= mBottomImages.length;
                    int total = mBottomImages.length;
                    for (int i = 0; i < total; i++) {
                        if (i == position) {
                            mBottomImages[i].setImageResource(R.drawable.indicator_select);
                        } else {
                            mBottomImages[i].setImageResource(R.drawable.indicator_no_select);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该文章的objectId
                TextView t = (TextView) v.findViewById(R.id.objectId);
                Intent it = new Intent(getActivity(), ArticleActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });
    }



    private void addImageView(){
        ImageView view0 = new ImageView(getApplicationContext());
        ImageLoader.getInstance().displayImage(imageUrl[0], view0);
        ImageView view1 = new ImageView(getApplicationContext());
        ImageLoader.getInstance().displayImage(imageUrl[1], view1);
        ImageView view2 = new ImageView(getApplicationContext());
        ImageLoader.getInstance().displayImage(imageUrl[2], view2);

        view0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view2.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mItems.add(view0);
        mItems.add(view1);
        mItems.add(view2);

        view0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it0 = new Intent(getActivity(), PopularActivity.class);
                it0.putExtra("objectId", popular_activity_id[0]);
                startActivity(it0);
            }
        });

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(getActivity(), PopularActivity.class);
                it1.putExtra("objectId", popular_activity_id[1]);
                startActivity(it1);
            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = new Intent(getActivity(), PopularActivity.class);
                it2.putExtra("objectId", popular_activity_id[2]);
                startActivity(it2);
            }
        });
    }

    private void setBottomIndicator() {
        //获取指示器(下面三个小点)
        mBottomImages = new ImageView[mItems.size()];
        for (int i = 0; i < mBottomImages.length; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(20, 0, 20, 0);
            imageView.setLayoutParams(params);
            //如果当前是第一个 设置为选中状态
            if (i == 0) {
                imageView.setImageResource(R.drawable.indicator_select);
            } else {
                imageView.setImageResource(R.drawable.indicator_no_select);
            }
            mBottomImages[i] = imageView;
            //添加到父容器
            mBottomLiner.addView(imageView);
        }

        //让底部小圆点其在最大值的中间开始滑动, 一定要在 mBottomImages初始化之前完成
        int mid = MyPagerAdapter.MAX_SCROLL_VALUE / 2;
        mViewPager.setCurrentItem(mid);
        currentViewPagerItem = mid;

    }

    public void bannarSlide() {

        //定时发送消息
        mThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        mThread.start();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 为防止内存泄漏, 声明自己的Handler并弱引用Activity
    ///////////////////////////////////////////////////////////////////////////
    private static class MyHandler extends Handler {
        private WeakReference<TabFragment2> mWeakReference;

        public MyHandler(TabFragment2 activity) {
            mWeakReference = new WeakReference<TabFragment2>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    TabFragment2 activity = mWeakReference.get();
                    if (activity.isAutoPlay) {
                        activity.mViewPager.setCurrentItem(++activity.currentViewPagerItem);
                    }
                    break;
            }

        }
    }

    //动态修改listview高度，使得listview能完全展开
    private void setListViewHeightBasedOnChildren() {
        if (listView == null) {
            return;
        }
        if (simpleAdapter == null) {
            return;
        }
        int totalHeight = 0;
        //Toast.makeText(getApplication(), Integer.toString(simpleAdapter.getCount()), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < simpleAdapter.getCount(); i++) {
            View listItem = simpleAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (simpleAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}  