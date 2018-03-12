package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.SayingAdapter;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.the_saying;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yc on 2018/2/9.
 */

public class MySayingActivity extends AppCompatActivity {

    private _User current_user = BmobUser.getCurrentUser(_User.class);

    private ImageView back;
    private TextView sum;
    private ImageView type;

    //列表模式：listview
    private ListView listView;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;


    //格子模式：recyclerview
    private List<the_saying> data_2 = new ArrayList<>();//必须初始化
    private RecyclerView recyclerView;
    private SayingAdapter adapter;


    private DisplayImageOptions options; // 设置图片显示相关参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_saying);

        findView();
        initData();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        sum = (TextView) findViewById(R.id.sum);
        type = (ImageView) findViewById(R.id.type);
        type.setTag(0);
        listView = (ListView) findViewById(R.id.listview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    public void initData() {
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

        BmobQuery<saying> query = new BmobQuery("saying");
        query.addWhereEqualTo("userOnlyId", current_user.getObjectId());// 查询当前用户的所有语录
        query.include("userId");
        query.order("-createdAt");
        query.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    sum.setText("共"+list.size()+"篇语录");
                    if (list.size() != 0) {
                        for (saying t : list) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("saying_id", t.getObjectId().toString());
                            temp.put("create_time", t.getCreatedAt().toString());
                            temp.put("saying_content", t.getContent().toString());
                            if (t.getImage() != null) {
                                BmobFile img = t.getImage();
                                String img_url = img.getFileUrl();
                                temp.put("saying_image", img_url);
                            } else {
                                temp.put("saying_image", "no_image");
                            }
                            data.add(temp);
                            the_saying flag = new the_saying(t.getObjectId().toString(), t.getContent().toString());
                            data_2.add(flag);
                        }
                        //列表模式：listview
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.my_saying_item_1, new String[] {"saying_id","saying_content","create_time","saying_image"}, new int[] {R.id.saying_id, R.id.saying_content, R.id.create_time, R.id.saying_image});
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
                        listView.setAdapter(simpleAdapter);

                        //格子模式：recyclerview
                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new SayingAdapter(data_2);
                        recyclerView.setAdapter(adapter);

                    } else
                        Toast.makeText(getApplicationContext(), "你还未发布任何语录哦", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
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

        type.setOnClickListener(new View.OnClickListener() {
            // Tag:0表示列表模式，1表示格子模式
            @Override
            public void onClick(View v) {
                if (type.getTag().equals(0)) {
                    type.setImageResource(R.drawable.ic_apps_24dp);
                    type.setTag(1);
                    listView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    type.setImageResource(R.drawable.ic_dehaze_24dp);
                    type.setTag(0);
                    listView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(MySayingActivity.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });



    }
}
