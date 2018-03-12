package com.example.yc.saying.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.utils.PicturePreviewActivity;
import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.message_books;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.user_followers;
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
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yc on 2018/2/12.
 */

public class BookActivity extends AppCompatActivity {
    private _User user = BmobUser.getCurrentUser(_User.class);
    private String objectId;
    private ImageView back;
    private ImageView likes;
    private TextView title;
    private TextView likes_sum;
    private ImageView image;
    private String image_url;
    //private TextView name;
    private TextView intro;
    private String user_id;
    private CircleImageView user_image;
    private TextView user_name;
    private Integer sum;
    private Toolbar toolbar;
    private ImageView boundary;
    private TextView likes_sum_text;
    private ImageView like_icon;

    private ListView listView;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private DisplayImageOptions options; // 设置图片显示相关参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent it = getIntent();
        objectId = it.getStringExtra("objectId");

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        back = (ImageView) findViewById(R.id.back);
        likes = (ImageView) findViewById(R.id.likes);
        likes_sum = (TextView) findViewById(R.id.likes_sum);
        title = (TextView) findViewById(R.id.title);
        //name = (TextView) findViewById(R.id.name);
        image = (ImageView) findViewById(R.id.image);
        intro = (TextView) findViewById(R.id.intro);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_name = (TextView) findViewById(R.id.user_name);
        listView = (ListView) findViewById(R.id.listview);
        listView.setFocusable(false);
        boundary = (ImageView) findViewById(R.id.boundary);
        likes_sum_text = (TextView) findViewById(R.id.likes_sum_text);
        like_icon = (ImageView) findViewById(R.id.like_icon);

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

        // 查询喜欢的所有笔记本，多对多关联
        BmobQuery<collection> query = new BmobQuery<collection>();
        // focusBook是_User表中的字段，用来存储一个用户所喜欢的笔记本
        query.addWhereRelatedTo("focusBook", new BmobPointer(user));
        query.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        //Toast.makeText(getApplicationContext(), "你还没收藏任何笔记本哦", Toast.LENGTH_SHORT).show();
                        likes.setImageResource(R.drawable.ic_favorite_border_24dp);
                        likes.setTag(0);
                    }
                    else {
                        likes.setImageResource(R.drawable.ic_favorite_border_24dp);
                        likes.setTag(0);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getObjectId().equals(objectId)) {
                                //Toast.makeText(getApplicationContext(), "你已收藏该笔记", Toast.LENGTH_SHORT).show();
                                likes.setImageResource(R.drawable.ic_favorite_24dp);
                                likes.setTag(1);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });


        BmobQuery<collection> query2 = new BmobQuery<collection>();
        query2.getObject(objectId, new QueryListener<collection>() {
            @Override
            public void done(collection object, BmobException e) {
                if(e==null){
                    user_id = object.getUserOnlyId().toString();
                    changeLayout(user_id);
                    title.setText(object.getName().toString());
                    image_url = object.getImage().getFileUrl();
                    ImageLoader.getInstance().displayImage(object.getImage().getFileUrl(), image);
                    sum = object.getLike_sum();
                    likes_sum.setText(Integer.toString(object.getLike_sum()));
                    likes_sum_text.setText("该笔记本已收获"+Integer.toString(object.getLike_sum())+"个");
                    //name.setText("『"+object.getName().toString()+"』");
                    if (object.getIntroduction().equals(""))
                        intro.setVisibility(View.GONE);
                    else {
                        intro.setVisibility(View.VISIBLE);
                        intro.setText(object.getIntroduction().toString());
                    }
                    BmobQuery<_User> query3 = new BmobQuery<_User>();
                    query3.getObject(user_id, new QueryListener<_User>() {
                        @Override
                        public void done(_User u, BmobException ee) {
                            if (ee == null) {
                                ImageLoader.getInstance().displayImage(u.getHeadPortrait().getFileUrl(), user_image);
                                user_name.setText("创建者："+u.getNickName().toString()+" >>");
                            } else {
                                Toast.makeText(getApplicationContext(), "用户查询失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // 查询包含的所有语录，多对多关联，因此查询的是saying
                    BmobQuery<saying> query4 = new BmobQuery<saying>();
                    query4.addWhereRelatedTo("collectedSayings", new BmobPointer(object));
                    query4.include("userId");//切记不要漏掉这个，不然会报空指针错误
                    query4.order("-createdAt");
                    query4.findObjects(new FindListener<saying>() {
                        @Override
                        public void done(List<saying> saying_list, BmobException eee) {
                            if (eee == null) {
                                //Toast.makeText(getApplicationContext(), saying_list.size(), Toast.LENGTH_SHORT).show();
                                if (saying_list.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "该笔记本还未添加任何语录哦~", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for (int j = 0; j < saying_list.size(); j++) {
                                        Map<String,Object> temp = new LinkedHashMap<>();
                                        temp.put("saying_id", saying_list.get(j).getObjectId().toString());
                                        temp.put("user_name", saying_list.get(j).getUserId().getNickName().toString());
                                        // 例子：对于返回的时间值“2018-1-31 18:39”，只取空格前的年月日
                                        temp.put("create_time", saying_list.get(j).getCreatedAt().toString().split(" ")[0]);
                                        temp.put("saying_content", saying_list.get(j).getContent().toString());
                                        if (saying_list.get(j).getImage() != null) {
                                            BmobFile img = saying_list.get(j).getImage();
                                            String img_url = img.getFileUrl();
                                            temp.put("saying_image", img_url);
                                        } else {
                                            temp.put("saying_image", "no_image");
                                        }
                                        BmobFile head_img = saying_list.get(j).getUserId().getHeadPortrait();
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
                                    setListViewHeightBasedOnChildren();

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "语录查询失败"+eee.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }else{
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BookActivity.this, PicturePreviewActivity.class);
                it.putExtra("url", image_url);
                startActivity(it);
            }
        });

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _User current_user = BmobUser.getCurrentUser(_User.class);
                collection this_collection = new collection();
                this_collection.setObjectId(objectId);
                BmobRelation relation = new BmobRelation();

                if (likes.getTag().equals(0)) {
                    sum = sum + 1;
                    likes_sum.setText(Integer.toString(sum));
                    likes.setImageResource(R.drawable.ic_favorite_24dp);
                    likes.setTag(1);
                    Toast.makeText(getApplicationContext(), "已表示喜欢该笔记本~", Toast.LENGTH_SHORT).show();
                    // 将笔记本列入收藏列表里
                    // 添加到多对多关联中
                    relation.add(this_collection);
                    current_user.setFocusBook(relation);
                    current_user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
                            }else{
                                //Toast.makeText(getApplication(), "关联失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // 该笔记本的收藏数量加1
                    collection s = new collection();
                    s.increment("like_sum");
                    s.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getApplication(), "该笔记本收藏量增加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "该笔记本收藏收藏量增加失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // 新增消息提示
                    BmobQuery<user_followers> query = new BmobQuery<user_followers>();
                    query.addWhereEqualTo("user_id", user_id);
                    query.findObjects(new FindListener<user_followers>() {
                        @Override
                        public void done(List<user_followers> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 1) {
                                    user_followers the_user_followers = new user_followers();
                                    the_user_followers.increment("follower_sum",0);
                                    the_user_followers.increment("message_fans_sum",0);
                                    the_user_followers.increment("message_fans_read",0);
                                    the_user_followers.increment("notification_read",0);
                                    the_user_followers.increment("message_sayings_read",0);
                                    the_user_followers.increment("message_sayings_sum",0);
                                    the_user_followers.increment("message_books_read",0);
                                    the_user_followers.increment("message_books_sum",1);
                                    the_user_followers.update(list.get(0).getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException ee) {
                                            if(ee==null){
                                                //Toast.makeText(getApplication(), "消息数量增加成功", Toast.LENGTH_SHORT).show();
                                            }else{
                                                //Toast.makeText(getApplication(), "消息数量增加失败" + ee.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                //Toast.makeText(getApplication(), "失败: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //建立新的通知
                    message_books new_message = new message_books();
                    new_message.setUser_id(current_user.getObjectId());
                    new_message.setUser_name(current_user.getNickName());
                    new_message.setAcceptor_id(user_id);
                    new_message.setFocus_book(this_collection);
                    new_message.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getApplication(), "建立通知成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "建立通知失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    sum = sum - 1;
                    likes_sum.setText(Integer.toString(sum));
                    likes.setImageResource(R.drawable.ic_favorite_border_24dp);
                    likes.setTag(0);
                    Toast.makeText(getApplicationContext(), "取消喜欢", Toast.LENGTH_SHORT).show();
                    // 移除
                    relation.remove(this_collection);
                    current_user.setFocusBook(relation);
                    current_user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
                            }else{
                                //Toast.makeText(getApplication(), "关联失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // 该笔记本的喜欢数量减1
                    collection s = new collection();
                    s.increment("like_sum", -1);
                    s.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getApplication(), "该笔记本喜欢量减少成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "该笔记本喜欢量减少失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BookActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                startActivity(it);
            }
        });

        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BookActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                startActivity(it);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.saying_id);
                // Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_SHORT).show();
                // 将该语录的objectId传递给语录详细页面
                Intent it = new Intent(BookActivity.this, SayingActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

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

    // 如果判断出这是自己创建的笔记本，则页面格式发生改变
    // 增加右侧菜单可进行设置，并将爱心图标置于头部界面下方
    public void changeLayout(String id) {
        if (user.getObjectId().equals(id)) {
            likes.setVisibility(View.GONE);
            likes_sum.setVisibility(View.GONE);
            boundary.setVisibility(View.VISIBLE);
            likes_sum_text.setVisibility(View.VISIBLE);
            like_icon.setVisibility(View.VISIBLE);
            // Toolbar作为独立控件进行使用
            toolbar.inflateMenu(R.menu.toolbar_menu_2);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            Intent it = new Intent(BookActivity.this, EditBook.class);
                            it.putExtra("objectId", objectId);
                            startActivityForResult(it, 0);
                            break;
                        case R.id.delete:
                            createDialog();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("确定删除此本笔记？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        collection s = new collection();
                        s.setObjectId(objectId);
                        s.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(BookActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    Toast.makeText(BookActivity.this, "删除失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        alertDialog.show();
    }

    public void updateBook() {
        BmobQuery<collection> query5 = new BmobQuery<collection>();
        query5.getObject(objectId, new QueryListener<collection>() {
            @Override
            public void done(collection object, BmobException e) {
                if(e==null){
                    title.setText(object.getName().toString());
                    ImageLoader.getInstance().displayImage(object.getImage().getFileUrl(), image);
                    if (object.getIntroduction().equals(""))
                        intro.setVisibility(View.GONE);
                    else {
                        intro.setVisibility(View.VISIBLE);
                        intro.setText(object.getIntroduction().toString());
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "笔记本查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                updateBook();
                break;
            default:
                break;
        }
    }

}
