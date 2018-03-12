package com.example.yc.saying.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.saying;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yc on 2018/2/18.
 */

public class EditSaying extends AppCompatActivity {
    private String objectId;
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
        setContentView(R.layout.edit_saying);

        Intent it = getIntent();
        objectId = it.getStringExtra("objectId");

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

        BmobQuery<saying> query = new BmobQuery<saying>();
        query.getObject(objectId, new QueryListener<saying>() {

            @Override
            public void done(saying object, BmobException e) {
                if(e==null){
                    if (object.getImage() != null) {
                        String imageUrl = object.getImage().getFileUrl().toString();
                        ImageLoader.getInstance().displayImage(imageUrl, Saying_image);
                    }
                    Saying_provenance_edittext.setText(object.getProvenance());
                    Saying_author_edittext.setText(object.getAuthor());
                    Saying_topic_edittext.setText(object.getTopic());
                    Saying_content_edittext.setText(object.getContent());
                }else{
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
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
                    Toast.makeText(getApplicationContext(), "内容不能为空喔", Toast.LENGTH_SHORT).show();
                else
                    upLoad();//发表语录帖子
            }
        });
    }

    public void upLoad() {
        _User user = BmobUser.getCurrentUser(_User.class);
        Saying_provenance = Saying_provenance_edittext.getText().toString();
        Saying_author = Saying_author_edittext.getText().toString();
        Saying_topic = Saying_topic_edittext.getText().toString();
        Saying_content = Saying_content_edittext.getText().toString();
        // 查看需不需要上传图片
        if (path != null) {
            image_file = new BmobFile(new File(path));
            //要先上传图片，之后才能将图片加载到新建数据列里
            image_file.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        saying s = new saying();
                        s.setImage(image_file);
                        s.setProvenance(Saying_provenance);
                        s.setAuthor(Saying_author);
                        s.setTopic(Saying_topic);
                        s.setContent(Saying_content);
                        s.update(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(getApplicationContext(), "更改成功", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent();
                                    setResult(0, i);
                                    finish();
                                    //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                }else{
                                    Toast.makeText(getApplicationContext(), "更改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplication(), "更改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            saying ss = new saying();
            ss.setProvenance(Saying_provenance);
            ss.setAuthor(Saying_author);
            ss.setTopic(Saying_topic);
            ss.setContent(Saying_content);
            ss.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Toast.makeText(getApplicationContext(), "更改成功", Toast.LENGTH_SHORT).show();
                        finish();
                        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    }else{
                        Toast.makeText(getApplicationContext(), "更改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("尚未更改，确定离开？")
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
