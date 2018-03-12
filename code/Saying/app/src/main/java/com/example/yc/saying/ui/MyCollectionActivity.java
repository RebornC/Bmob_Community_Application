package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.BookAdapter;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.the_collection;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yc on 2018/2/11.
 */

public class MyCollectionActivity extends AppCompatActivity {

    private _User current_user = BmobUser.getCurrentUser(_User.class);

    private ImageView back;
    private ImageView add;

    //格子模式：recyclerview
    private List<the_collection> data = new ArrayList<>();//必须初始化
    private RecyclerView recyclerView;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_collection);

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        add = (ImageView) findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    public void initialization() {

        BmobQuery<collection> query = new BmobQuery("collection");
        query.addWhereEqualTo("userOnlyId", current_user.getObjectId());// 查询当前用户的所有笔记本
        query.order("createdAt");
        query.findObjects(new FindListener<collection>() {
            @Override
            public void done(List<collection> list, BmobException e) {
                if (e == null) {
                    data.clear();
                    if (list.size() != 0) {
                        for (collection t : list) {
                            the_collection flag = new the_collection(t.getObjectId().toString(), t.getName().toString(), t.getImage().getFileUrl());
                            data.add(flag);
                        }
                        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new BookAdapter(data);
                        recyclerView.setAdapter(adapter);

                    } else
                        Toast.makeText(getApplicationContext(), "快去创建笔记本吧~", Toast.LENGTH_SHORT).show();
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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MyCollectionActivity.this, AddBook.class);
                startActivityForResult(it, 0);
            }
        });

    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        data.clear();
        adapter = new BookAdapter(data);
        recyclerView.setAdapter(adapter);
        initialization();
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                initialization();
                break;
            default:
                break;
        }
    }

}
