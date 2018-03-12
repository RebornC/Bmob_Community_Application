package com.example.yc.saying.tabFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.yc.saying.ui.AddSaying;
import com.example.yc.saying.ui.FocusListActivity;
import com.example.yc.saying.ui.FollowersListActivity;
import com.example.yc.saying.ui.LikeActivity;
import com.example.yc.saying.ui.LoginActivity;
import com.example.yc.saying.ui.MessageAlertActivity;
import com.example.yc.saying.ui.MyCollectionActivity;
import com.example.yc.saying.ui.MySayingActivity;
import com.example.yc.saying.R;
import com.example.yc.saying.ui.UserMessage;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.notification;
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
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class TabFragment3 extends Fragment {

    private CircleImageView headView;
    private ImageView bgView;
    private TextView name;
    private TextView brief_intro;
    private TextView focus;
    private TextView focusId_sum;
    private TextView followers;
    private TextView follower_sum;
    private View view;
    private ListView listView;
    private FloatingActionButton fab;
    private String now_user_img_url;
    private Integer notification_sum;

    private String followeList_id;//当前用户对应的粉丝user_followers的object

    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
  
    @Nullable  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment3,container,false);

        findView();
        initialization();
        clickEvents();

        return view;
    }

    public void findView() {
        headView = (CircleImageView) view.findViewById(R.id.headView);
        bgView = (ImageView) view.findViewById(R.id.bgView);
        name = (TextView) view.findViewById(R.id.name);
        brief_intro = (TextView) view.findViewById(R.id.brief_intro);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        focus = (TextView) view.findViewById(R.id.focus);
        focusId_sum = (TextView) view.findViewById(R.id.focusId_sum);
        followers = (TextView) view.findViewById(R.id.followers);
        follower_sum = (TextView) view.findViewById(R.id.follower_sum);
        listView = (ListView) view.findViewById(R.id.listview);
        listView.addHeaderView(new ViewStub(getApplicationContext()));
        listView.addFooterView(new ViewStub(getApplicationContext()));
    }

    public void initialization() {
        //初始化列表
        Integer[] images = {R.drawable.ic_account_circle_24dp, R.drawable.ic_cloud_queue_black_24dp, R.drawable.ic_import_contacts_24dp,
                R.drawable.ic_favorite, R.drawable.ic_announcement_24dp, R.drawable.ic_out_24dp};
        String[] message = {"账号资料", "我的语录", "我的笔记", "我的喜欢", "消息提示", "退出登录"};
        Integer[] icons = {R.drawable.ic_keyboard_arrow_right_24dp, R.drawable.ic_keyboard_arrow_right_24dp, R.drawable.ic_keyboard_arrow_right_24dp,
                R.drawable.ic_keyboard_arrow_right_24dp, R.drawable.ic_keyboard_arrow_right_24dp, R.drawable.ic_keyboard_arrow_right_24dp};
        for (int i = 0; i < 6; i++) {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("image", images[i]);
            temp.put("message", message[i]);
            temp.put("icon", icons[i]);
            data.add(temp);
        }
        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.frag3_item, new String[] {"image","message","icon"}, new int[] {R.id.image, R.id.message, R.id.icon});
        listView.setAdapter(simpleAdapter);
        setListViewHeightBasedOnChildren();


        //查询系统通知数量
        BmobQuery<notification> query_2 = new BmobQuery<notification>();
        query_2.count(notification.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    notification_sum = count;
                }else{
                    //Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


        _User current_user = BmobUser.getCurrentUser(_User.class);
        BmobFile now_user_img = current_user.getHeadPortrait();
        now_user_img_url = now_user_img.getFileUrl();
        //加载网络图片
        ImageLoader.getInstance().displayImage(now_user_img_url, headView);
        if (current_user.getCoverPage() != null) {
            ImageLoader.getInstance().displayImage(current_user.getCoverPage().getFileUrl(), bgView);
        }
        name.setText(current_user.getNickName().toString());
        if (current_user.getBrief_intro() == null || current_user.getBrief_intro().equals("")) {
            brief_intro.setText("简介：这个人很懒，什么也没留下...");
        } else
            brief_intro.setText("简介："+current_user.getBrief_intro());
        //focusId_sum.setText(Integer.toString(current_user.getFocusId_sum()));
        followeList_id = current_user.getFollower_id().getObjectId();
        follower_sum.setText(Integer.toString(current_user.getFollower_id().getFollower_sum()));
        findFollower_sum();

        //查询关注人数
        BmobQuery<_User> query = new BmobQuery<_User>();
        // focusId是_User表中的字段，用来存储一个用户所关注的对象
        query.addWhereRelatedTo("focusId", new BmobPointer(current_user));
        query.count(_User.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    focusId_sum.setText(Integer.toString(integer));
                } else {
                    //Toast.makeText(getApplicationContext(), "关注人数查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void clickEvents() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), AddSaying.class);
                startActivity(it);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Intent it = new Intent(getActivity(), UserMessage.class);
                        startActivity(it);
                        break;
                    case 2:
                        Intent it2 = new Intent(getActivity(), MySayingActivity.class);
                        startActivity(it2);
                        break;
                    case 3:
                        Intent it3 = new Intent(getActivity(), MyCollectionActivity.class);
                        startActivity(it3);
                        break;
                    case 4:
                        Intent it4 = new Intent(getActivity(), LikeActivity.class);
                        startActivity(it4);
                        break;
                    case 5:
                        Intent it5 = new Intent(getActivity(), MessageAlertActivity.class);
                        startActivity(it5);
                        break;
                    case 6:
                        createDialog();
                        break;
                    default:
                        break;
                }
            }
        });

        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), FocusListActivity.class);
                it.putExtra("objectId", BmobUser.getCurrentUser(_User.class).getObjectId());
                startActivity(it);
            }
        });

        focusId_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), FocusListActivity.class);
                it.putExtra("objectId", BmobUser.getCurrentUser(_User.class).getObjectId());
                startActivity(it);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), FollowersListActivity.class);
                it.putExtra("objectId", followeList_id);
                startActivity(it);
            }
        });

        follower_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), FollowersListActivity.class);
                it.putExtra("objectId", followeList_id);
                startActivity(it);
            }
        });


    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("确定退出登录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent it = new Intent(getActivity(), LoginActivity.class);
                        startActivity(it);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        alertDialog.show();
    }

    public void findFollower_sum() {
        _User current_user = BmobUser.getCurrentUser(_User.class);
        BmobQuery<_User> query = new BmobQuery<_User>();
        query.include("follower_id");
        query.getObject(current_user.getObjectId(), new QueryListener<_User>() {
            @Override
            public void done(_User object, BmobException e) {
                if(e==null){
                    //Toast.makeText(getApplicationContext(), "查询粉丝成功", Toast.LENGTH_SHORT).show();
                    follower_sum.setText(Integer.toString(object.getFollower_id().getFollower_sum()));

                    // 查询是否有未读消息，有则显示红点提示
                    boolean flag_1 = (object.getFollower_id().getMessage_fans_sum() == object.getFollower_id().getMessage_fans_read());
                    boolean flag_2 = (object.getFollower_id().getMessage_sayings_sum() == object.getFollower_id().getMessage_sayings_read());
                    boolean flag_3 = (object.getFollower_id().getMessage_books_sum() == object.getFollower_id().getMessage_books_read());
                    boolean flag_4 = (object.getFollower_id().getNotification_read() == notification_sum);
                    if (!(flag_1 == true && flag_2 == true && flag_3 == true && flag_4 == true)) {
                        Map<String,Object> temp = new LinkedHashMap<>();
                        temp.put("image", R.drawable.ic_announcement_24dp);
                        temp.put("message", "消息提示");
                        temp.put("icon", R.drawable.ic_message);
                        data.set(4,temp);
                        simpleAdapter.notifyDataSetChanged();
                    } else {
                        Map<String,Object> temp = new LinkedHashMap<>();
                        temp.put("image", R.drawable.ic_announcement_24dp);
                        temp.put("message", "消息提示");
                        temp.put("icon", R.drawable.ic_keyboard_arrow_right_24dp);
                        data.set(4,temp);
                        simpleAdapter.notifyDataSetChanged();
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "查询粉丝失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // 动态修改listview高度，使得listview能完全展开
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
        params.height = totalHeight + (listView.getDividerHeight() * (simpleAdapter.getCount() + 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        _User current_user = BmobUser.getCurrentUser(_User.class);
        BmobFile now_user_img = current_user.getHeadPortrait();
        String now_user_img_url = now_user_img.getFileUrl();
        //加载网络图片
        ImageLoader.getInstance().displayImage(now_user_img_url, headView);
        if (current_user.getCoverPage() != null) {
            ImageLoader.getInstance().displayImage(current_user.getCoverPage().getFileUrl(), bgView);
        }

        name.setText(current_user.getNickName().toString());
        if (current_user.getBrief_intro() == null || current_user.getBrief_intro().equals("")) {
            brief_intro.setText("简介：这个人很懒，什么也没留下...");
        } else
            brief_intro.setText("简介："+current_user.getBrief_intro());
        //focusId_sum.setText(Integer.toString(current_user.getFocusId_sum()));
        findFollower_sum();
    }

}  