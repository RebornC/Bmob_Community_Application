package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.BookAdapter;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.the_collection;
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
 * Created by yc on 2018/2/20.
 */

public class SearchActivity extends AppCompatActivity {
    private SearchView searchview;
    private Button user_button;
    private Button saying_button;
    private Button others_button;
    private Button book_button;
    private ListView listview_1;
    private List<Map<String,Object>> data_1 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_1;
    private ListView listview_2;
    private List<Map<String,Object>> data_2 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_2;
    private ListView listview_3;
    private List<Map<String,Object>> data_3 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_3;
    private List<the_collection> data_4 = new ArrayList<>();//必须初始化
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private int tag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findView();
        clickEvents();

    }

    public void findView() {
        searchview = (SearchView) findViewById(R.id.search);
        user_button = (Button) findViewById(R.id.user_button);
        user_button.setTag(1);//表示被选择
        saying_button = (Button) findViewById(R.id.saying_button);
        saying_button.setTag(0);//表示不被选择
        others_button = (Button) findViewById(R.id.others_button);
        others_button.setTag(0);//表示不被选择
        book_button = (Button) findViewById(R.id.book_button);
        book_button.setTag(0);//表示不被选择
        listview_1 = (ListView) findViewById(R.id.listview_1);
        listview_1.addFooterView(new ViewStub(getApplicationContext()));
        listview_2 = (ListView) findViewById(R.id.listview_2);
        listview_2.addFooterView(new ViewStub(getApplicationContext()));
        listview_3 = (ListView) findViewById(R.id.listview_3);
        listview_3.addFooterView(new ViewStub(getApplicationContext()));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    public void clickEvents() {
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    Toast.makeText(getApplicationContext(), "查询的关键字不能为空哦", Toast.LENGTH_SHORT).show();
                } else {
                    searchUser(query, true);
                    searchSaying(query, true);
                    searchOthers(query, true);
                    searchBook(query, true);
                }
                return true;
            }
            public boolean onQueryTextChange(String inputText) {
                if (!inputText.equals("")) {
                    searchUser(inputText, false);
                    searchSaying(inputText, false);
                    searchOthers(inputText, false);
                    searchBook(inputText, false);
                }
                return true;
            }
        });

        user_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (user_button.getTag().equals(0)) {
                    tag = 1;
                    listview_1.setVisibility(View.VISIBLE);
                    listview_2.setVisibility(View.GONE);
                    listview_3.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    user_button.setBackgroundResource(R.drawable.shape_12);
                    user_button.setTag(1);
                    saying_button.setBackgroundResource(R.drawable.shape_11);
                    saying_button.setTag(0);
                    others_button.setBackgroundResource(R.drawable.shape_11);
                    others_button.setTag(0);
                    book_button.setBackgroundResource(R.drawable.shape_11);
                    book_button.setTag(0);
                }
            }
        });

        saying_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (saying_button.getTag().equals(0)) {
                    tag = 2;
                    listview_2.setVisibility(View.VISIBLE);
                    listview_1.setVisibility(View.GONE);
                    listview_3.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    user_button.setBackgroundResource(R.drawable.shape_11);
                    user_button.setTag(0);
                    saying_button.setBackgroundResource(R.drawable.shape_12);
                    saying_button.setTag(1);
                    others_button.setBackgroundResource(R.drawable.shape_11);
                    others_button.setTag(0);
                    book_button.setBackgroundResource(R.drawable.shape_11);
                    book_button.setTag(0);
                }
            }
        });

        others_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (others_button.getTag().equals(0)) {
                    tag = 3;
                    listview_3.setVisibility(View.VISIBLE);
                    listview_1.setVisibility(View.GONE);
                    listview_2.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    user_button.setBackgroundResource(R.drawable.shape_11);
                    user_button.setTag(0);
                    saying_button.setBackgroundResource(R.drawable.shape_11);
                    saying_button.setTag(0);
                    others_button.setBackgroundResource(R.drawable.shape_12);
                    others_button.setTag(1);
                    book_button.setBackgroundResource(R.drawable.shape_11);
                    book_button.setTag(0);
                }
            }
        });

        book_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (book_button.getTag().equals(0)) {
                    tag = 4;
                    listview_1.setVisibility(View.GONE);
                    listview_2.setVisibility(View.GONE);
                    listview_3.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    user_button.setBackgroundResource(R.drawable.shape_11);
                    user_button.setTag(0);
                    saying_button.setBackgroundResource(R.drawable.shape_11);
                    saying_button.setTag(0);
                    others_button.setBackgroundResource(R.drawable.shape_11);
                    others_button.setTag(0);
                    book_button.setBackgroundResource(R.drawable.shape_12);
                    book_button.setTag(1);
                }
            }
        });

        listview_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.user_id);
                // 将该用户的objectId传递给用户详细主页
                Intent it = new Intent(SearchActivity.this, UserHomepageActivity.class);
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
                Intent it = new Intent(SearchActivity.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

        listview_3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(SearchActivity.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });
    }

    public void searchUser(String text, Boolean bool) {
        final String t = text;
        final Boolean b = bool;
        BmobQuery<_User> query1 = new BmobQuery<_User>();
        //本来是可以直接使用bmob的模糊查询的，但是要付费，所以只能另辟蹊径
        //query1.addWhereContains("nickName", text);
        query1.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        if (b == true && tag == 1) {
                            Toast.makeText(getApplicationContext(), "抱歉，查无此用户", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        data_1.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getNickName().contains(t)) {
                                Map<String,Object> temp = new LinkedHashMap<>();
                                temp.put("image", list.get(i).getHeadPortrait().getFileUrl());
                                temp.put("name", list.get(i).getNickName().toString());
                                temp.put("user_id", list.get(i).getObjectId().toString());
                                data_1.add(temp);
                            }
                        }
                        if (data_1.size() == 0) {
                            if (b == true && tag == 1) {
                                Toast.makeText(getApplicationContext(), "抱歉，查无此用户", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            simpleAdapter_1 = new SimpleAdapter(getApplicationContext(), data_1, R.layout.peoplelist_item, new String[]{"image", "name", "user_id"}, new int[]{R.id.image, R.id.name, R.id.user_id});
                            // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                            // 利用imageloader框架，对SimpleAdapter进行处理
                            simpleAdapter_1.setViewBinder(new SimpleAdapter.ViewBinder() {
                                public boolean setViewValue(View view, Object data,
                                                            String textRepresentation) {
                                    //判断是否为我们要处理的对象
                                    if (view instanceof ImageView && data instanceof String) {
                                        ImageView iv = (ImageView) view;
                                        ImageLoader.getInstance().displayImage((String) data, iv);
                                        return true;
                                    } else
                                        return false;
                                }
                            });
                            listview_1.setAdapter(simpleAdapter_1);
                        }
                    }
                } else
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchSaying(String text, Boolean bool) {
        final String containedText = text;
        final Boolean b = bool;
        BmobQuery<saying> query2 = new BmobQuery<saying>();
        query2.include("userId");
        query2.order("-createdAt");
        //本来是可以直接使用bmob的模糊查询的，但是要付费，所以只能另辟蹊径
        query2.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        if (b == true && tag == 2) {
                            Toast.makeText(getApplicationContext(), "抱歉，查不到相关的语录", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        data_2.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getContent().contains(containedText)) {
                                Map<String, Object> temp = new LinkedHashMap<>();
                                temp.put("saying_id", list.get(i).getObjectId().toString());
                                temp.put("user_name", list.get(i).getUserId().getNickName().toString());
                                // 例子：对于返回的时间值“2018-1-31 18:39”，只取空格前的年月日
                                temp.put("create_time", list.get(i).getCreatedAt().toString().split(" ")[0]);
                                temp.put("saying_content", list.get(i).getContent().toString());
                                BmobFile head_img = list.get(i).getUserId().getHeadPortrait();
                                String head_img_url = head_img.getFileUrl();
                                temp.put("user_image", head_img_url);
                                data_2.add(temp);
                            }
                        }
                        if (data_2.size() == 0) {
                            if (b == true && tag == 2) {
                                Toast.makeText(getApplicationContext(), "抱歉，查不到相关的语录", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            simpleAdapter_2 = new SimpleAdapter(getApplicationContext(), data_2, R.layout.saying_item_3, new String[]{"saying_id", "user_name", "saying_content", "create_time", "user_image"}, new int[]{R.id.saying_id, R.id.user_name, R.id.saying_content, R.id.create_time, R.id.user_image});
                            // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                            // 利用imageloader框架，对SimpleAdapter进行处理
                            simpleAdapter_2.setViewBinder(new SimpleAdapter.ViewBinder() {
                                public boolean setViewValue(View view, Object data,
                                                            String textRepresentation) {
                                    //判断是否为我们要处理的对象
                                    if (view instanceof ImageView && data instanceof String) {
                                        ImageView iv = (ImageView) view;
                                        ImageLoader.getInstance().displayImage((String) data, iv);
                                        return true;
                                    } else
                                        return false;
                                }
                            });
                            listview_2.setAdapter(simpleAdapter_2);
                        }
                    }
                } else
                    Toast.makeText(getApplicationContext(), "查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchOthers(String text, Boolean bool) {
        final String containedText = text;
        final Boolean b = bool;
        BmobQuery<saying> query3 = new BmobQuery<saying>();
        query3.include("userId");
        query3.order("-createdAt");
        query3.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        if (b == true && tag == 3) {
                            Toast.makeText(getApplicationContext(), "抱歉，查不到相关的语录", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        data_3.clear();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getProvenance().contains(containedText) || list.get(i).getAuthor().contains(containedText)) {
                                Map<String, Object> temp = new LinkedHashMap<>();
                                String t1 = "", t2 = "";
                                temp.put("saying_id", list.get(i).getObjectId().toString());
                                temp.put("user_name", list.get(i).getUserId().getNickName().toString());
                                // 例子：对于返回的时间值“2018-1-31 18:39”，只取空格前的年月日
                                temp.put("create_time", list.get(i).getCreatedAt().toString().split(" ")[0]);
                                if (!list.get(i).getProvenance().toString().equals(""))
                                    t1 = "『" + list.get(i).getProvenance().toString() + "』" + " ";
                                if (!list.get(i).getAuthor().toString().equals(""))
                                    t2 = "by "+list.get(i).getAuthor().toString();
                                temp.put("saying_provenance", t1 + t2);
                                temp.put("saying_content", list.get(i).getContent().toString());
                                BmobFile head_img = list.get(i).getUserId().getHeadPortrait();
                                String head_img_url = head_img.getFileUrl();
                                temp.put("user_image", head_img_url);
                                data_3.add(temp);
                            }
                        }
                        if (data_3.size() == 0) {
                            if (b == true && tag == 3) {
                                Toast.makeText(getApplicationContext(), "抱歉，查不到相关的语录", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            simpleAdapter_3 = new SimpleAdapter(getApplicationContext(), data_3, R.layout.saying_item_4, new String[]{"saying_id", "user_name", "saying_content", "create_time", "user_image", "saying_provenance"}, new int[]{R.id.saying_id, R.id.user_name, R.id.saying_content, R.id.create_time, R.id.user_image, R.id.saying_provenance});
                            // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                            // 利用imageloader框架，对SimpleAdapter进行处理
                            simpleAdapter_3.setViewBinder(new SimpleAdapter.ViewBinder() {
                                public boolean setViewValue(View view, Object data,
                                                            String textRepresentation) {
                                    //判断是否为我们要处理的对象
                                    if (view instanceof ImageView && data instanceof String) {
                                        ImageView iv = (ImageView) view;
                                        ImageLoader.getInstance().displayImage((String) data, iv);
                                        return true;
                                    } else
                                        return false;
                                }
                            });
                            listview_3.setAdapter(simpleAdapter_3);
                        }
                    }
                } else
                    Toast.makeText(getApplicationContext(), "查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchBook(String text, Boolean bool) {
        final String t = text;
        final Boolean b = bool;
        BmobQuery<collection> query4 = new BmobQuery("collection");
        query4.order("createdAt");
        query4.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> list, BmobException e) {
                if (e == null) {
                    data_4.clear();
                    if (list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getName().contains(t)) {
                                the_collection flag = new the_collection(list.get(i).getObjectId().toString(), list.get(i).getName().toString(), list.get(i).getImage().getFileUrl());
                                data_4.add(flag);
                            }
                        }
                        if (data_4.size() == 0) {
                            if (b == true && tag == 4) {
                                Toast.makeText(getApplicationContext(), "抱歉，查不到相关的笔记本", Toast.LENGTH_SHORT).show();
                            }
                        }
                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new BookAdapter(data_4);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "抱歉，查不到相关的笔记本", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
