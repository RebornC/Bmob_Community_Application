package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model.saying;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yc on 2018/2/23.
 */

public class PopularActivitySaying extends AppCompatActivity {
    private String activity_title;
    private ImageView back;
    private TextView title;
    private Button hot_sayings;
    private Button actual_sayings;

    private DisplayImageOptions options; // 设置图片显示相关参数

    private ListView listview_1;
    private List<Map<String,Object>> data_1 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_1;

    private ListView listview_2;
    private List<Map<String,Object>> data_2 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_saying);

        Intent it = getIntent();
        activity_title = it.getStringExtra("activity_title");

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title.setText(activity_title);
        hot_sayings = (Button) findViewById(R.id.hot_sayings);
        hot_sayings.setTag(1);//表示被选择
        actual_sayings = (Button) findViewById(R.id.actual_sayings);
        actual_sayings.setTag(0);//表示不被选择
        listview_1 = (ListView) findViewById(R.id.listview1);
        listview_2 = (ListView) findViewById(R.id.listview2);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.upload) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.upload) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.upload) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
    }

    public void initialization() {

        // 查询该话题的相关语录

        // 按收藏量排序
        BmobQuery<saying> query1 = new BmobQuery<saying>();
        query1.addWhereEqualTo("topic", activity_title);
        query1.include("userId");
        query1.order("-like_sum");
        query1.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getApplicationContext(), "当前还未有用户参与该活动喔"+"\n"+"快争做第一人吧！", Toast.LENGTH_SHORT).show();
                    }
                    else {
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
                            data_1.add(temp);
                        }
                        simpleAdapter_1 = new SimpleAdapter(getApplicationContext(), data_1, R.layout.saying_item_5, new String[] {"saying_id","user_name","saying_content","create_time","saying_image","user_image"}, new int[] {R.id.saying_id, R.id.user_name, R.id.saying_content, R.id.create_time, R.id.saying_image, R.id.user_image});
                        // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                        // 利用imageloader框架，对SimpleAdapter进行处理
                        simpleAdapter_1.setViewBinder(new SimpleAdapter.ViewBinder() {
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
                        listview_1.setAdapter(simpleAdapter_1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // 按收藏量排序
        BmobQuery<saying> query2 = new BmobQuery<saying>();
        query2.addWhereEqualTo("topic", activity_title);
        query2.include("userId");
        query2.order("-createdAt");
        query2.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        //Toast.makeText(getApplicationContext(), "当前还未有用户参与该活动喔"+"\n"+"快争做第一人吧！", Toast.LENGTH_SHORT).show();
                    }
                    else {
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
                            data_2.add(temp);
                        }
                        simpleAdapter_2 = new SimpleAdapter(getApplicationContext(), data_2, R.layout.saying_item_5, new String[] {"saying_id","user_name","saying_content","create_time","saying_image","user_image"}, new int[] {R.id.saying_id, R.id.user_name, R.id.saying_content, R.id.create_time, R.id.saying_image, R.id.user_image});
                        // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                        // 利用imageloader框架，对SimpleAdapter进行处理
                        simpleAdapter_2.setViewBinder(new SimpleAdapter.ViewBinder() {
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
                        listview_2.setAdapter(simpleAdapter_2);
                    }
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

        hot_sayings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hot_sayings.getTag().equals(0)) {
                    hot_sayings.setBackgroundResource(R.drawable.shape_6);
                    actual_sayings.setBackgroundResource(R.drawable.shape_7);
                    hot_sayings.setTag(1);
                    actual_sayings.setTag(0);
                    listview_2.setVisibility(View.GONE);
                    listview_1.setVisibility(View.VISIBLE);
                }
            }
        });

        actual_sayings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actual_sayings.getTag().equals(0)) {
                    actual_sayings.setBackgroundResource(R.drawable.shape_6);
                    hot_sayings.setBackgroundResource(R.drawable.shape_7);
                    actual_sayings.setTag(1);
                    hot_sayings.setTag(0);
                    listview_1.setVisibility(View.GONE);
                    listview_2.setVisibility(View.VISIBLE);
                }
            }
        });

        listview_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(PopularActivitySaying.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

        listview_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(PopularActivitySaying.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });
    }

}
