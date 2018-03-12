package com.example.yc.saying.tabFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.ui.SayingActivity;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.saying;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class TabFragment1 extends Fragment {

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String user_id;
    private String[] focus_ids;

    private ListView listView;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    private DisplayImageOptions options; // 设置图片显示相关参数

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
  
    @Nullable
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment1,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        listView = (ListView) view.findViewById(R.id.sayingListView);
        initData();

        //显示listView
        search_focus_ids();

        clickEvents();

        return view;
    }

    public void search_focus_ids() {

        // 查询关注的所有用户，多对多关联，因此查询的是用户表
        BmobQuery<_User> query = new BmobQuery<_User>();
        _User user = BmobUser.getCurrentUser(_User.class);
        // focusId是UserBean表中的字段，用来存储一个用户所关注的其他用户
        query.addWhereRelatedTo("focusId", new BmobPointer(user));
        // 查询当前用户，将当前用户的动态一并呈现
        BmobQuery<_User> query_myself = new BmobQuery<_User>();
        query_myself.addWhereEqualTo("objectId", user.getObjectId().toString());
        // 合并两个条件，进行"或"查询
        List<BmobQuery<_User>> queries = new ArrayList<BmobQuery<_User>>();
        queries.add(query);
        queries.add(query_myself);
        BmobQuery<_User> mainQuery = new BmobQuery<_User>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> object,BmobException e) {
                if (e == null) {
                    if (object.size() == 0)
                        Toast.makeText(getApplicationContext(), "你还没关注任何人哦", Toast.LENGTH_SHORT).show();
                    else {
                        focus_ids = new String[object.size()];
                        for (int i = 0; i < object.size(); i++) {
                            focus_ids[i] = object.get(i).getObjectId().toString();
                            //Toast.makeText(getApplicationContext(), focus_ids[i], Toast.LENGTH_SHORT).show();
                        }
                        initialization();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void initData() {
        //Bundle bundle = getIntent().getExtras();
        //imageUrls = bundle.getStringArray(Constants.IMAGES);

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
        query.addWhereContainedIn("userOnlyId", Arrays.asList(focus_ids));  // 查询当前用户的所有语录
        query.include("userId");
        query.order("-createdAt");
        query.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {

                    if (list != null) {
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
                        listView.setAdapter(simpleAdapter);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void clickEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(getActivity(), SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
          }
        });

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
                                    data.clear();
                                    search_focus_ids();
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
    }



}  