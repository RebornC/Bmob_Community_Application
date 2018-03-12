package com.example.yc.saying.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.utils.PicturePreviewActivity;
import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.message_sayings;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.user_followers;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by yc on 2018/2/3.
 */

public class SayingActivity extends AppCompatActivity {
    private _User user = BmobUser.getCurrentUser(_User.class);
    private String objectId;
    private ImageView back;
    private ImageView likes;
    private ImageView add;
    private ImageView head;
    private TextView name;
    private TextView time;
    private ImageView image;
    private TextView content;
    private String user_id;
    private TextView likes_sum;
    private Integer sum;
    private Integer selectedIndex = 0;
    private String[] arrayBook;//笔记本列表
    private Toolbar toolbar;
    private ImageView boundary;
    private TextView likes_sum_text;
    private ImageView like_icon;
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saying);

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
        add = (ImageView) findViewById(R.id.add);
        head = (ImageView) findViewById(R.id.head);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        image = (ImageView) findViewById(R.id.image);
        content = (TextView) findViewById(R.id.content);
        likes_sum = (TextView) findViewById(R.id.likes_sum);
        boundary = (ImageView) findViewById(R.id.boundary);
        likes_sum_text = (TextView) findViewById(R.id.likes_sum_text);
        like_icon = (ImageView) findViewById(R.id.like_icon);
    }

    public void initialization() {

        // 查询喜欢的所有语录，多对多关联，因此查询的是saying
        BmobQuery<saying> query = new BmobQuery<saying>();
        // focusSaying是_User表中的字段，用来存储一个用户所关注的语录
        query.addWhereRelatedTo("focusSaying", new BmobPointer(user));
        query.findObjects(new FindListener<saying>() {
            @Override
            public void done(List<saying> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        //Toast.makeText(getApplicationContext(), "你还没收藏任何语录哦", Toast.LENGTH_SHORT).show();
                        likes.setImageResource(R.drawable.ic_favorite_border_24dp);
                        likes.setTag(0);
                    }
                    else {
                        likes.setImageResource(R.drawable.ic_favorite_border_24dp);
                        likes.setTag(0);
                        for (int i = 0; i < object.size(); i++) {
                            if (object.get(i).getObjectId().equals(objectId)) {
                                //Toast.makeText(getApplicationContext(), "你已收藏该语录", Toast.LENGTH_SHORT).show();
                                likes.setImageResource(R.drawable.ic_favorite_24dp);
                                likes.setTag(1);
                                break;
                            }
                        }
                    }
                    add.setImageResource(R.drawable.ic_add);
                } else {
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });

        BmobQuery<saying> query2 = new BmobQuery<saying>();
        query2.include("userId");
        query2.getObject(objectId, new QueryListener<saying>() {

            @Override
            public void done(saying object, BmobException e) {
                if(e==null){
                    user_id = object.getUserOnlyId().toString();
                    changeLayout(user_id);//判断是否自己发布的语录
                    if (object.getUserId().getHeadPortrait() != null) {
                        String headUrl = object.getUserId().getHeadPortrait().getFileUrl().toString();
                        ImageLoader.getInstance().displayImage(headUrl, head);
                    }
                    if (object.getImage() != null) {
                        imageUrl = object.getImage().getFileUrl().toString();
                        ImageLoader.getInstance().displayImage(imageUrl, image);
                    }
                    sum = object.getLike_sum();
                    likes_sum.setText(Integer.toString(object.getLike_sum()));
                    likes_sum_text.setText("该语录已收获"+Integer.toString(object.getLike_sum())+"个");
                    name.setText(object.getUserId().getNickName().toString());
                    time.setText(object.getCreatedAt().toString());
                    String text = object.getContent().toString() + "\n";
                    if (!object.getAuthor().toString().equals(""))
                        text = text + "\n" + "by " + object.getAuthor().toString();
                    if (!object.getProvenance().toString().equals(""))
                        text = text + "\n" + "『" + object.getProvenance().toString() + "』";
                    content.setText(text);
                }else{
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // 查询创建的所有笔记本
        BmobQuery<collection> query3 = new BmobQuery<collection>();
        // myCollectio是_User表中的字段，用来存储一个用户所创建的笔记本
        query3.addWhereRelatedTo("myCollection", new BmobPointer(user));
        query3.order("createdAt");
        query3.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> object3, BmobException e) {
                if (e == null) {
                    if (object3.size() == 0) {
                        //Toast.makeText(getApplicationContext(), "你还没创建任何笔记本哦", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        arrayBook = new String[object3.size()];
                        for (int i = 0; i < object3.size(); i++) {
                            arrayBook[i] = object3.get(i).getName().toString();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "笔记本列表查询失败", Toast.LENGTH_SHORT).show();
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

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _User current_user = BmobUser.getCurrentUser(_User.class);
                saying this_saying = new saying();
                this_saying.setObjectId(objectId);
                BmobRelation relation = new BmobRelation();

                if (likes.getTag().equals(0)) {
                    sum = sum + 1;
                    likes_sum.setText(Integer.toString(sum));
                    likes.setImageResource(R.drawable.ic_favorite_24dp);
                    likes.setTag(1);
                    Toast.makeText(getApplicationContext(), "已表示喜欢该语录~", Toast.LENGTH_SHORT).show();
                    // 将语录列入收藏列表里
                    // 添加到多对多关联中
                    relation.add(this_saying);
                    current_user.setFocusSaying(relation);
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

                    // 该语录的收藏数量加1
                    saying s = new saying();
                    s.increment("like_sum");
                    s.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getApplication(), "该语录收藏量增加成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "该语录收藏量增加失败", Toast.LENGTH_SHORT).show();
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
                                    the_user_followers.increment("message_sayings_sum",1);
                                    the_user_followers.increment("message_books_read",0);
                                    the_user_followers.increment("message_books_sum",0);
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
                    message_sayings new_message = new message_sayings();
                    new_message.setInitiator(BmobUser.getCurrentUser(_User.class));
                    new_message.setAcceptor_id(user_id);
                    new_message.setSaying_id(objectId);
                    new_message.setSaying_content(content.getText().toString());
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
                    relation.remove(this_saying);
                    current_user.setFocusSaying(relation);
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
                    // 该语录的收藏数量减1
                    saying s = new saying();
                    s.increment("like_sum", -1);
                    s.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //Toast.makeText(getApplication(), "该语录收藏量减少成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "该语录收藏量减少失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdialog();
            }
        });

        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SayingActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                startActivity(it);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SayingActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                startActivity(it);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SayingActivity.this, PicturePreviewActivity.class);
                it.putExtra("url", imageUrl);
                startActivity(it);
            }
        });

    }

    public void createdialog() {
         AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("添加至以下哪本笔记？")
                    //.setIcon(R.drawable.ic_favorite_24dp)
                    .setSingleChoiceItems(arrayBook, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    selectedIndex = which;
                                }
                            })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            chooseBook(selectedIndex);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).create();
            dialog.show();
    }

    public void chooseBook(final Integer select) {
        // 根据下标找到选择的那个笔记本
        // 查询创建的所有笔记本
        BmobQuery<collection> query4 = new BmobQuery<collection>();
        // myCollectio是_User表中的字段，用来存储一个用户所创建的笔记本
        query4.addWhereRelatedTo("myCollection", new BmobPointer(user));
        query4.order("createdAt");
        query4.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> object3, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < object3.size(); i++) {
                        if (i == select) {
                            // 将语录列入该笔记本里
                            // 添加到多对多关联中
                            saying this_saying = new saying();
                            this_saying.setObjectId(objectId);
                            BmobRelation relat = new BmobRelation();
                            relat.add(this_saying);
                            object3.get(i).setCollectedSayings(relat);
                            object3.get(i).update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplication(), "添加失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // 如果判断出这是自己发布的语录，则页面格式发生改变
    // 增加右侧菜单可进行删除，并将爱心图标置于正文下面
    public void changeLayout(String id) {
        if (user.getObjectId().equals(id)) {
            likes.setVisibility(View.GONE);
            likes_sum.setVisibility(View.GONE);
            boundary.setVisibility(View.VISIBLE);
            likes_sum_text.setVisibility(View.VISIBLE);
            like_icon.setVisibility(View.VISIBLE);
            // Toolbar作为独立控件进行使用
            toolbar.inflateMenu(R.menu.toolbar_menu_1);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            Intent it = new Intent(SayingActivity.this, EditSaying.class);
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
        alertDialog.setMessage("确定删除此篇语录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saying s = new saying();
                        s.setObjectId(objectId);
                        s.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(SayingActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    Toast.makeText(SayingActivity.this, "删除失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void updateSaying() {
        BmobQuery<saying> query5 = new BmobQuery<saying>();
        query5.getObject(objectId, new QueryListener<saying>() {

            @Override
            public void done(saying object, BmobException e) {
                if(e==null) {
                    if (object.getImage() != null) {
                        String imageUrl = object.getImage().getFileUrl().toString();
                        ImageLoader.getInstance().displayImage(imageUrl, image);
                    }
                    String text = object.getContent().toString() + "\n";
                    if (!object.getAuthor().toString().equals(""))
                        text = text + "\n" + "by " + object.getAuthor().toString();
                    if (!object.getProvenance().toString().equals(""))
                        text = text + "\n" + "『" + object.getProvenance().toString() + "』";
                    content.setText(text);
                }else{
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                updateSaying();
                break;
            default:
                break;
        }
    }



}
