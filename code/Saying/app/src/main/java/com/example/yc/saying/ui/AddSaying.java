package com.example.yc.saying.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.saying;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yc on 2018/2/3.
 */

public class AddSaying extends AppCompatActivity {
    private ImageView back;
    private String path = null;
    private BmobFile image_file;
    private TextView publish;
    private ImageView Saying_image;
    private EditText Saying_provenance_edittext;
    private EditText Saying_author_edittext;
    private EditText Saying_topic_edittext;
    private EditText Saying_content_edittext;
    private String Saying_provenance;
    private String Saying_author;
    private String Saying_topic;
    private String Saying_content;

    //请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_saying);

        findView();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        publish = (TextView) findViewById(R.id.publish);
        Saying_image = (ImageView) findViewById(R.id.Saying_image);
        Saying_provenance_edittext = (EditText) findViewById(R.id.Saying_provenance);
        Saying_author_edittext = (EditText) findViewById(R.id.Saying_author);
        Saying_topic_edittext = (EditText) findViewById(R.id.Saying_topic);
        Saying_content_edittext = (EditText) findViewById(R.id.Saying_content);
    }

    public void clickEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Saying_provenance = Saying_provenance_edittext.getText().toString();
                Saying_author = Saying_author_edittext.getText().toString();
                Saying_topic = Saying_topic_edittext.getText().toString();
                Saying_content = Saying_content_edittext.getText().toString();
                if (Saying_provenance.equals("") && Saying_author.equals("") && Saying_topic.equals("") && Saying_content.equals("") && path == null)
                    finish();
                else {
                    createDialog();
                }
            }
        });

        Saying_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相册进行选择
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Saying_content = Saying_content_edittext.getText().toString();
                if (Saying_content.equals(""))
                    Toast.makeText(getApplicationContext(), "内容为空，无法发布", Toast.LENGTH_SHORT).show();
                else
                    upLoad();//发表语录帖子
            }
        });
    }

    public void upLoad() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在发布中...");
        progressDialog.show();
        _User user = BmobUser.getCurrentUser(_User.class);
        Saying_provenance = Saying_provenance_edittext.getText().toString();
        Saying_author = Saying_author_edittext.getText().toString();
        Saying_topic = Saying_topic_edittext.getText().toString();
        Saying_content = Saying_content_edittext.getText().toString();
        // 创建语录信息
        final saying add_saying = new saying();
        add_saying.setUserId(user);// 添加一对一关联
        add_saying.setUserOnlyId(user.getObjectId());
        add_saying.setProvenance(Saying_provenance);
        add_saying.setAuthor(Saying_author);
        add_saying.setTopic(Saying_topic);
        add_saying.setContent(Saying_content);
        // 查看需不需要上传图片
        if (path != null) {
            image_file = new BmobFile(new File(path));
            //要先上传图片，之后才能将图片加载到新建数据列里
            image_file.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        add_saying.setImage(image_file);
                        add_saying.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                    //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplication(), "发布失败", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            add_saying.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if(e==null){
                        Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    }else{
                        Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }

    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("内容无法保存，确定离开？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                if (data != null) {
                    try {
                        Uri selectdeImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        //从系统表中查询指定uri对应的照片
                        Cursor cursor = getContentResolver().query(selectdeImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        Saying_image.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

}
