package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.BookAdapter;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.the_collection;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yc on 2018/2/7.
 */

public class LikeActivity extends AppCompatActivity {

    private _User user = BmobUser.getCurrentUser(_User.class);

    private ImageView back;
    private Button user_sayings;
    private Button user_books;

    private ListView lv;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private DisplayImageOptions options; // 设置图片显示相关参数

    private List<the_collection> data_2 = new ArrayList<>();//必须初始化
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private Integer bookSum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);


        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        user_sayings = (Button) findViewById(R.id.user_sayings);
        user_sayings.setTag(1);//表示被选择
        user_books = (Button) findViewById(R.id.user_books);
        user_books.setTag(0);//表示不被选择
        lv = (ListView) findViewById(R.id.listview);
        lv.setFocusable(false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setFocusable(false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.upload) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.upload) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
    }

    public void initialization() {

        // 查询收藏的语录
        BmobQuery<saying> query = new BmobQuery("saying");
        query.addWhereRelatedTo("focusSaying", new BmobPointer(user));
        query.include("userId");
        query.order("-createdAt");
        query.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        for (saying t : list) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("saying_id", t.getObjectId().toString());
                            temp.put("user_name", t.getUserId().getNickName().toString());
                            // 例子：对于返回的时间值“2018-1-31 18:39”，只取空格前的年月日
                            temp.put("create_time", t.getCreatedAt().toString().split(" ")[0]);
                            temp.put("saying_content", t.getContent().toString());
                            if (t.getImage() != null) {
                                BmobFile img = t.getImage();
                                String img_url = img.getFileUrl();
                                temp.put("saying_image", img_url);
                            } else {
                                temp.put("saying_image", "no_image");
                            }
                            BmobFile head_img = t.getUserId().getHeadPortrait();
                            String head_img_url = head_img.getFileUrl();
                            temp.put("user_image", head_img_url);
                            data.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.saying_item, new String[] {"saying_id","user_name","saying_content","create_time","saying_image","user_image"}, new int[] {R.id.saying_id, R.id.user_name, R.id.saying_content, R.id.create_time, R.id.saying_image, R.id.user_image});
                        // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                        // 利用imageloader框架，对SimpleAdapter进行处理
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
                                //判断是否为我们要处理的对象
                                if(view instanceof ImageView && data instanceof String){
                                    ImageView iv = (ImageView) view;
                                    if (data.equals("no_image"))
                                        iv.setVisibility(View.GONE);
                                    else {
                                        iv.setVisibility(View.VISIBLE);
                                        ImageLoader.getInstance().displayImage((String) data, iv, options);
                                    }
                                    return true;
                                }else
                                    return false;
                            }
                        });
                        lv.setAdapter(simpleAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "你还未收藏任何语录哦", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "语录列表查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 查询收藏的笔记本
        BmobQuery<collection> query2 = new BmobQuery("collection");
        query2.addWhereRelatedTo("focusBook", new BmobPointer(user));
        query2.order("createdAt");
        query2.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> list, BmobException e) {
                if (e == null) {
                    bookSum = list.size();
                    if (list.size() != 0) {
                        for (collection t : list) {
                            the_collection flag = new the_collection(t.getObjectId().toString(), t.getName().toString(), t.getImage().getFileUrl());
                            data_2.add(flag);
                        }
                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new BookAdapter(data_2);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "笔记本查询失败", Toast.LENGTH_SHORT).show();
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

        user_sayings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_sayings.getTag().equals(0)) {
                    user_sayings.setBackgroundResource(R.drawable.shape_8);
                    user_books.setBackgroundResource(R.drawable.shape_9);
                    user_sayings.setTag(1);
                    user_books.setTag(0);
                    recyclerView.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                }
            }
        });

        user_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_books.getTag().equals(0)) {
                    user_books.setBackgroundResource(R.drawable.shape_8);
                    user_sayings.setBackgroundResource(R.drawable.shape_9);
                    user_books.setTag(1);
                    user_sayings.setTag(0);
                    lv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (bookSum == 0)
                    Toast.makeText(getApplicationContext(), "你还未收藏任何笔记本哦", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(LikeActivity.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

    }



}
