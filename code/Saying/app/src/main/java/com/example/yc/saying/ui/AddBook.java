package com.example.yc.saying.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.user_followers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yc on 2018/2/11.
 */

public class AddBook extends AppCompatActivity {

    private _User current_user = BmobUser.getCurrentUser(_User.class);

    private ImageView back;
    private BmobFile image_file;
    private TextView add;
    private ImageView image;
    private EditText name_edittext;
    private EditText intro_edittext;
    private String name;
    private String intro;

    //关于图片选择并裁剪，请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    //裁剪图片后的宽(X)和高(Y)
    private static int output_X = 200;
    private static int output_Y = 200;
    private static String img_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);

        findView();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        add = (TextView) findViewById(R.id.add);
        image = (ImageView) findViewById(R.id.image);
        name_edittext = (EditText) findViewById(R.id.name);
        intro_edittext = (EditText) findViewById(R.id.introduction);
    }

    public void clickEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_edittext.getText().toString();
                intro = intro_edittext.getText().toString();
                if (name.equals("") && intro.equals("") && img_uri == null)
                    finish();
                else {
                    createDialog();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相册进行选择
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_edittext.getText().toString();
                intro = intro_edittext.getText().toString();
                if (name.equals(""))
                    Toast.makeText(getApplicationContext(), "笔记本标题不能为空哦", Toast.LENGTH_SHORT).show();
                else if (img_uri == null)
                    Toast.makeText(getApplicationContext(), "请上传笔记本封面图片", Toast.LENGTH_SHORT).show();
                else
                    uploadImage_then_create(img_uri);//发表语录帖子
            }
        });
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //提取图片数据，将头像的View设置为自定义图片
            Bitmap photo = extras.getParcelable("data");
            //user_img.setImageBitmap(photo);
            //创建文件夹，储存截好的头像，方便下次打开的时候读取
            File newfile = new File(Environment.getExternalStorageDirectory(),"Pic");
            if (!newfile.exists()) {
                newfile.mkdir();
            }
            File file = new File(Environment.getExternalStorageDirectory()+"/Pic", "bookcover.jpg");
            FileOutputStream out = null;
            try {//打开输出流 将图片数据填入文件中
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                try {
                    out.flush();
                    img_uri = Environment.getExternalStorageDirectory()+"/Pic/bookcover.jpg";
                    //将路径转为bitmap然后显示出来
                    Bitmap bitmap = BitmapFactory.decodeFile(img_uri);
                    image.setImageBitmap(bitmap);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 将图片上传到 Bmob然后创建新笔记本
     */
    public void uploadImage_then_create(String uri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在创建中...");
        progressDialog.show();
        //img_path = getImagePath(uri, null);
        //Toast.makeText(getApplication(), img_path.toString(), Toast.LENGTH_LONG).show();
        final BmobFile file = new BmobFile(new File(uri));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //Toast.makeText(getApplication(), "图片上传成功", Toast.LENGTH_SHORT).show();
                    // 图片上传成功后创建新笔记本
                    collection this_collection = new collection();
                    this_collection.setUserOnlyId(current_user.getObjectId());
                    this_collection.setName(name);
                    this_collection.setIntroduction(intro);
                    this_collection.setImage(file);
                    this_collection.setPublicOrNot(true);
                    this_collection.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                collection temp = new collection();
                                temp.setObjectId(s);
                                BmobRelation relation = new BmobRelation();
                                relation.add(temp);
                                current_user.setMyCollection(relation);// 添加一对多关联
                                current_user.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "<"+name+">笔记本创建成功", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Intent i = new Intent();
                                            setResult(0, i);
                                            finish();
                                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                        } else {
                                            //Toast.makeText(getApplication(), "关联失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //Toast.makeText(getApplication(), "创建成功", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplication(), "创建失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplication(), "图片上传失败", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
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
                    cropRawPhoto(data.getData());
                }
                break;
            case CODE_RESULT_REQUEST:
                if (data != null) {
                    setImageToHeadView(data);
                }
                break;
            default:
                break;
        }
    }
}
