package com.example.yc.saying.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.collection;
import com.example.yc.saying.model.message_fans;
import com.example.yc.saying.model.saying;
import com.example.yc.saying.model.user_followers;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private CircleImageView headView;
    private EditText nickName;
    private EditText loginEmail;
    private EditText password;
    private EditText psw_again;
    private String nickNameText;
    private String loginEmailText;
    private String passwordText;
    private String psw_againText;
    private Button signUp;

    //关于图片选择并裁剪，请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    //裁剪图片后的宽(X)和高(Y)
    private static int output_X = 200;
    private static int output_Y = 200;
    private static String img_uri = "";

    // 2018/7/6尝试
    // 用于接收图库选择或拍照完成后的结果回调
    private static final int PHOTO_TK = 0; //图库
    private static final int PHOTO_PZ = 1; //拍照
    private static final int PHOTO_CLIP = 2; //裁剪

    private Uri contentUri;
    private Bitmap photo;
    private OutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // findView
        headView = (CircleImageView) findViewById(R.id.img_2);
        nickName = (EditText) findViewById(R.id.nickName);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.password);
        psw_again = (EditText) findViewById(R.id.psw_again);
        signUp = (Button) findViewById(R.id.signUp);

        // 点击事件
        clickEvents();
    }

    public void clickEvents() {

        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相册进行选择
                /*
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
                */
                Intent intent = new Intent(Intent.ACTION_PICK, null); //请选择文件
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*"); //返回结果和标识
                startActivityForResult(intent, PHOTO_TK);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickNameText = nickName.getText().toString();
                loginEmailText = loginEmail.getText().toString();
                passwordText = password.getText().toString();
                psw_againText = psw_again.getText().toString();
                if (nickNameText.equals("") || loginEmailText.equals("") || passwordText.equals("") || psw_againText.equals(""))
                    Toast.makeText(getApplication(), "请填写完整", Toast.LENGTH_SHORT).show();
                else if (img_uri.equals(""))
                    Toast.makeText(getApplication(), "请上传你喜欢的头像", Toast.LENGTH_SHORT).show();
                else if (!passwordText.equals(psw_againText))
                    Toast.makeText(getApplication(), "请注意：您的两次密码填写不一致", Toast.LENGTH_SHORT).show();
                else {
                    uploadImage_then_signUp(img_uri);
                }
            }
        });

    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("注册成功！尝试登录吧")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        alertDialog.show();
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
            File file = new File(Environment.getExternalStorageDirectory()+"/Pic", "head.jpg");
            FileOutputStream out = null;
            try {//打开输出流 将图片数据填入文件中
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                try {
                    out.flush();
                    img_uri = Environment.getExternalStorageDirectory()+"/Pic/head.jpg";
                    //将路径转为bitmap然后显示出来
                    Bitmap bitmap = BitmapFactory.decodeFile(img_uri);
                    headView.setImageBitmap(bitmap);
                    //上传图片
                    //uploadImage(img_uri);
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
     * 将图片上传到 Bmob
     */
    public void uploadImage_then_signUp(String uri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在注册中...");
        progressDialog.show();
        //img_path = getImagePath(uri, null);
        //Toast.makeText(getApplication(), img_path.toString(), Toast.LENGTH_LONG).show();
        final BmobFile file = new BmobFile(new File(uri));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //Toast.makeText(getApplication(), "图片上传成功", Toast.LENGTH_SHORT).show();
                    // 图片上传成功后注册用户
                    _User user = new _User();
                    user.setheadPortrait(file);
                    user.setNickName(nickNameText);
                    user.setEmail(loginEmailText);
                    user.setPassword(passwordText);
                    user.setUsername(loginEmailText);//把username与email归为一类
                    user.setFocusId_sum(0);
                    user.signUp(new SaveListener<_User>() {
                        @Override
                        public void done(_User s, BmobException e) {
                            if (e == null) {
                                _User user = new _User();
                                user.setUsername(loginEmailText);
                                user.setPassword(passwordText);
                                user.login(new SaveListener<_User>() {
                                    @Override
                                    public void done(_User s, BmobException e) {

                                        if (e == null) {
                                            // 注册成功，建立对应的user_followers数据
                                            user_followers this_user_followers = new user_followers();
                                            this_user_followers.setUser_id(s.getObjectId());
                                            this_user_followers.setUser(BmobUser.getCurrentUser(_User.class));
                                            this_user_followers.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String id, BmobException e) {
                                                    if (e == null) {
                                                        user_followers temp = new user_followers();
                                                        temp.setObjectId(id);
                                                        _User u = BmobUser.getCurrentUser(_User.class);
                                                        u.setFollower_id(temp);// 添加一对一关联
                                                        u.update(new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if (e == null) {
                                                                    //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
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

                                            // 新建默认笔记本
                                            collection this_collection = new collection();
                                            this_collection.setUserOnlyId(s.getObjectId());
                                            this_collection.setName("默认笔记本");
                                            this_collection.setIntroduction("将你喜欢的语录添加至笔记本之中~");
                                            this_collection.setImage(file);
                                            this_collection.setPublicOrNot(true);
                                            this_collection.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e == null) {
                                                        collection tempp = new collection();
                                                        tempp.setObjectId(s);
                                                        BmobRelation relation = new BmobRelation();
                                                        relation.add(tempp);
                                                        _User uu = BmobUser.getCurrentUser(_User.class);
                                                        uu.setMyCollection(relation);// 添加一对多关联
                                                        uu.update(new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if (e == null) {
                                                                    //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
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

                                            // 将官方账号列入关注列表里
                                            _User office = new _User();
                                            office.setObjectId("e0c92a7159");
                                            // 添加到多对多关联中
                                            BmobRelation relation = new BmobRelation();
                                            relation.add(office);
                                            s.setFocusId(relation);
                                            s.increment("focusId_sum");//关注人数自增1
                                            s.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        //Toast.makeText(getApplication(), "关联失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            // 官方账号的粉丝列表、人数发生相应改变
                                            final BmobRelation relation_2 = new BmobRelation();
                                            relation_2.add(s);

                                            BmobQuery<user_followers> query = new BmobQuery<user_followers>();
                                            query.addWhereEqualTo("user_id", "e0c92a7159");
                                            query.findObjects(new FindListener<user_followers>() {
                                                @Override
                                                public void done(List<user_followers> list, BmobException e) {
                                                    if (e == null) {
                                                        if (list.size() == 1) {
                                                            user_followers the_user_followers = new user_followers();
                                                            the_user_followers.setFollowerId(relation_2);
                                                            the_user_followers.increment("follower_sum");
                                                            the_user_followers.increment("message_fans_sum");
                                                            the_user_followers.increment("message_fans_read",0);
                                                            the_user_followers.increment("notification_read",0);
                                                            the_user_followers.increment("message_sayings_read",0);
                                                            the_user_followers.increment("message_sayings_sum",0);
                                                            the_user_followers.increment("message_books_read",0);
                                                            the_user_followers.increment("message_books_sum",0);
                                                            the_user_followers.update(list.get(0).getObjectId(), new UpdateListener() {
                                                                @Override
                                                                public void done(BmobException ee) {
                                                                    if(ee==null){
                                                                        //Toast.makeText(getApplication(), "关联成功", Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        //Toast.makeText(getApplication(), "关联失败" + ee.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            message_fans new_message = new message_fans();
                                            new_message.setInitiator(BmobUser.getCurrentUser(_User.class));
                                            new_message.setAcceptor(office);
                                            new_message.setAcceptor_id("e0c92a7159");
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
                                            //Toast.makeText(getApplication(), "关联失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                createDialog();
                                progressDialog.dismiss();
                            } else {
                                if (e.getErrorCode() == 202 || e.getErrorCode() == 203) {
                                    Toast.makeText(getApplication(), "抱歉，该邮箱已被注册", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                                else if (e.getErrorCode() == 301) {
                                    Toast.makeText(getApplication(), "邮箱地址必须填写规范", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getApplication(), "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
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

    /**
     * 裁剪图片的方法.
     * 用于拍照完成或者选择本地图片之后
     */
    private Uri uritempFile;

    public void startPhotoZoom(Uri uri) {
        Log.e("uri 1 =====", "" + uri);
        // 解决小米选择相册图片截取照片不进onActivityResult报“保存时发生错误，保存失败”的bug
        // 小米选择相册时如果不使用系统裁剪，直接压缩不会报这个错误，如果裁剪就会报如上错误。
        // 需要把URI地址转为图片地址，再包成file文件转为URI
        if (uri.toString().contains("com.miui.gallery.open")) {
            uri = getImageContentUri(this, new File(getRealFilePath(this, uri)));
        }
        Log.e("uri 2 =====", "" + uri);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //uritempFile为Uri类变量，实例化uritempFile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //开启临时权限
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //重点:针对7.0以上的操作
            intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
            uritempFile = uri;
        } else {
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_CLIP);
    }

    /**
     * 将URI转为图片的路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
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
        */
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PZ:
                    Uri pictur;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
                        pictur = contentUri;
                    } else {
                        pictur = Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory() + "/temp.jpg"));
                    }
                    startPhotoZoom(pictur);
                    break;
                case PHOTO_TK:
                    startPhotoZoom(data.getData());
                    break;
                case PHOTO_CLIP:
                    //裁剪后的图像转成BitMap
                    //Log.e("imageUri:" , data.getData().toString());
                    try {
                        photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //创建路径
                    String path = Environment.getExternalStorageDirectory().getPath() + "/Pic";
                    //获取外部储存目录
                    File file = new File(path);
                    Log.e("file", file.getPath());
                    //创建新目录
                    file.mkdirs();
                    //以当前时间重新命名文件
                    long i = System.currentTimeMillis();
                    //生成新的文件
                    img_uri = file.toString() + "/" + i + ".png";
                    file = new File(file.toString() + "/" + i + ".png");
                    Log.e("fileNew", file.getPath());
                    //创建输出流
                    try {
                        out = new FileOutputStream(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //压缩文件
                    boolean flag = photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    if (file.getName() != null || !file.getName().equals("")) {
                        // 显示
                        // img_uri = uritempFile.toString();
                        // Toast.makeText(getApplication(), img_uri, Toast.LENGTH_LONG).show();
                        Picasso.with(this)
                                .load(uritempFile)
                                .into(headView);
                    }
                    break;
            }
        }
    }
}

