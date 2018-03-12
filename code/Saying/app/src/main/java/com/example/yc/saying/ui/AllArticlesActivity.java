package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model.article;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yc on 2018/2/21.
 */

public class AllArticlesActivity extends AppCompatActivity {
    private ImageView back;
    // 文章
    private ListView listView;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private DisplayImageOptions options; // 设置图片显示相关参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_articles);

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.upload) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.upload) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
    }

    public void initialization() {
        //寻找所有文章
        BmobQuery<article> query = new BmobQuery("article");
        query.order("-createdAt");
        query.findObjects(new FindListener<article>() {
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
                            data.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.article_item, new String[] {"objectId","title","intro","image"}, new int[] {R.id.objectId, R.id.title, R.id.intro, R.id.image});
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
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void clickEvents() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该文章的objectId
                TextView t = (TextView) v.findViewById(R.id.objectId);
                Intent it = new Intent(AllArticlesActivity.this, ArticleActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

    }


}
