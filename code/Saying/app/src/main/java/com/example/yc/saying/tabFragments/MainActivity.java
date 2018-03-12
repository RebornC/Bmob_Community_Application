package com.example.yc.saying.tabFragments;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.pagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private pagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 加载tabLayout与viewPager
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("关注").setIcon(R.drawable.ic_home_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("发现").setIcon(R.drawable.ic_data_usage_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("个人").setIcon(R.drawable.ic_person_white_24dp));
        // 修改样式，主要是调近了图标与文字之间的距离
        tabLayout.getTabAt(0).setCustomView(getTabView("关注",R.drawable.ic_home_white_24dp));
        tabLayout.getTabAt(1).setCustomView(getTabView("发现",R.drawable.ic_data_usage_white_24dp));
        tabLayout.getTabAt(2).setCustomView(getTabView("个人",R.drawable.ic_person_white_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        changeTabSelect(tabLayout.getTabAt(0));//打开APP时停留在第一页，故先改变其颜色
        viewPager = (ViewPager) findViewById(R.id.page);
        viewPager.setOffscreenPageLimit(3);//取消预加载
        adapter = new pagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(1);//停留在第二页
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                changeTabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    // Tab自定义view
    public View getTabView(String title, int src) {
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_item_view, null);
        TextView textView = (TextView) v.findViewById(R.id.textview);
        textView.setText(title);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageview);
        imageView.setImageResource(src);
        return v;
    }

    // 切换颜色
    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.imageview);
        TextView txt_title = (TextView) view.findViewById(R.id.textview);
        txt_title.setTextColor(getResources().getColor(R.color.colorBase1));
        if (txt_title.getText().toString().equals("关注")) {
            img_title.setImageResource(R.drawable.ic_home_green_24dp);
        } else if (txt_title.getText().toString().equals("发现")) {
            img_title.setImageResource(R.drawable.ic_data_usage_green_24dp);
        }  else if (txt_title.getText().toString().equals("个人")) {
            img_title.setImageResource(R.drawable.ic_person_green_24dp);
        }
    }

    private void changeTabNormal(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.imageview);
        TextView txt_title = (TextView) view.findViewById(R.id.textview);
        txt_title.setTextColor(getResources().getColor(R.color.colorBackground));
        if (txt_title.getText().toString().equals("关注")) {
            img_title.setImageResource(R.drawable.ic_home_white_24dp);
        } else if (txt_title.getText().toString().equals("发现")) {
            img_title.setImageResource(R.drawable.ic_data_usage_white_24dp);
        }  else if (txt_title.getText().toString().equals("个人")) {
            img_title.setImageResource(R.drawable.ic_person_white_24dp);
        }
    }

}

