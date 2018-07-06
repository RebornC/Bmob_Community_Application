package com.example.yc.saying.ui;

import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class UserMessage extends AppCompatActivity {

    private ImageView back;
    private ProgressBar progress;
    private AlertDialog.Builder alertDialog;
    private AlertDialog.Builder alertDialog_2;

    // 显示头像
    private ListView listView_1;
    private List<Map<String,Object>> data_1 = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    // 显示其余信息
    private String path = null;
    private ListView listView_2;
    private List<Map<String,Object>> data_2 = new ArrayList<>();
    private SimpleAdapter simpleAdapter_2;
    private String name;
    private String email;
    private String brief_intro_text;
    private String brief_intro = "点击修改";
    private String psw = "点击修改";
    private String cover = "点击修改";


    //关于图片选择并裁剪，请求识别码
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int RESULT_REQUEST = 0xa3;
    //裁剪图片后的宽(X)和高(Y)
    private static int output_X = 200;
    private static int output_Y = 200;
    private static String img_uri = "";
    private static int output_XX = 480;
    private static int output_YY = 200;

    // 2018/7/6尝试
    // 用于接收图库选择或拍照完成后的结果回调
    private static final int PHOTO_TK = 0; //图库
    private static final int PHOTO_PZ = 1; //拍照
    private static final int PHOTO_CLIP = 2; //裁剪

    private Uri contentUri;
    private Bitmap photo;
    private OutputStream out;
    private int temp = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_message);
        findView();
        initialization();
        clickEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case REQUEST:
                if (data != null) {
                    cropRawCoverPage(data.getData());
                }
                break;
            case RESULT_REQUEST:
                if (data != null) {
                    setImageToCoverPage(data);
                }
                break;
            default:
                break;
        }super.onActivityResult(requestCode, resultCode, data);
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
                        // Toast.makeText(getApplication(), img_uri, Toast.LENGTH_LONG).show();
                        if (temp == 1) {
                            uploadImage(img_uri);
                        } else {
                            uploadCoverPage(img_uri);
                        }
                    }
                    break;
            }
        }
    }

    private void findView() {
        back = (ImageView) findViewById(R.id.back);
        progress = (ProgressBar) findViewById(R.id.progress);

        listView_1 = (ListView) findViewById(R.id.listview_1);
        listView_2 = (ListView) findViewById(R.id.listview_2);
        listView_2.addHeaderView(new ViewStub(this));//顶部分割线
        listView_2.addFooterView(new ViewStub(this));//底部分割线
    }

    public void initialization() {

        // 根据ID查询单条数据
        _User current_user = BmobUser.getCurrentUser(_User.class);
        // 获得图片地址url
        BmobFile now_user_img = current_user.getHeadPortrait();
        String now_user_img_url = now_user_img.getFileUrl();
        // 获得信息
        name = current_user.getNickName().toString();
        if (current_user.getBrief_intro() == null || current_user.getBrief_intro().equals("")) {
            brief_intro_text = "这个人很懒，什么也没留下...";
        } else
            brief_intro_text = current_user.getBrief_intro().toString();
        email = current_user.getEmail().toString();

        // 用户头像实例化
        Map<String,Object> temp_1 = new LinkedHashMap<>();
        temp_1.put("text", "头像");
        temp_1.put("message_image", now_user_img_url);
        data_1.add(temp_1);
        simpleAdapter = new SimpleAdapter(this, data_1, R.layout.user_message_item, new String[] {"text","message_image"}, new int[] {R.id.text, R.id.message_image});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                //判断是否为我们要处理的对象
                if(view instanceof ImageView  && data instanceof String){
                    ImageView iv = (ImageView) view;
                    ImageLoader.getInstance().displayImage((String) data, iv);
                    return true;
                }else
                    return false;
            }
        });
        listView_1.setAdapter(simpleAdapter);

        // 用户信息实例化
        String[] user_message = new String[] {"昵称","简介","账号","密码","个人主页封面"};
        String[] message_text = new String[] {name,brief_intro,email,psw,cover};
        for (int i = 0; i < 5; i++) {
            Map<String,Object> temp_2 = new LinkedHashMap<>();
            temp_2.put("text",user_message[i]);
            temp_2.put("message_text",message_text[i]);
            data_2.add(temp_2);
        }
        simpleAdapter_2 = new SimpleAdapter(this, data_2, R.layout.user_message_item_2, new String[] {"text","message_text"}, new int[] {R.id.text, R.id.message_text});
        listView_2.setAdapter(simpleAdapter_2);

    }


    public void clickEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == 0) {
                    //打开相册进行选择
                    /*
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, CODE_GALLERY_REQUEST);
                    */
                    temp = 1;
                    Intent intent = new Intent(Intent.ACTION_PICK, null); //请选择文件
                    intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*"); //返回结果和标识
                    startActivityForResult(intent, PHOTO_TK);
                }
            }
        });

        listView_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //这里的item是从1算起而不是0
                if (arg2 == 1) {
                    //Toast.makeText(getApplicationContext(), "更改昵称"+arg2, Toast.LENGTH_SHORT).show();
                    creatDialog();
                    alertDialog.show();
                }
                if (arg2 == 2) {
                    //Toast.makeText(getApplicationContext(), "更改简介"+arg2, Toast.LENGTH_SHORT).show();
                    creatDialog_2();
                    alertDialog_2.show();
                }
                if (arg2 == 4) {
                    Intent it = new Intent(UserMessage.this, VerifyEmailActivity.class);
                    startActivity(it);
                }
                if (arg2 == 5) {
                    //打开相册进行选择
                    /*
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST);
                    */
                    temp = 2;
                    Intent intent = new Intent(Intent.ACTION_PICK, null); //请选择文件
                    intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*"); //返回结果和标识
                    startActivityForResult(intent, PHOTO_TK);
                }
            }
        });

    }

    public void creatDialog() {
        final EditText et = new EditText(UserMessage.this);
        et.setText(name);
        et.setSingleLine(true);
        //先设置好点击“修改昵称”弹出的dialog
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "昵称不能为空哦", Toast.LENGTH_SHORT).show();
                        } else {
                            name = input;
                            // 修改后端的用户昵称
                            _User current_user = BmobUser.getCurrentUser(_User.class);
                            current_user.setNickName(name);
                            current_user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        Toast.makeText(getApplication(), "昵称更换成功", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getApplication(), "昵称更换失败", Toast.LENGTH_SHORT).show();
                                    }
                                    // 修改界面的用户昵称
                                    refresh();
                                }
                            });
                        }

                    }
                })
                .setNegativeButton("取消", null).create();
    }

    public void creatDialog_2() {
        final EditText et = new EditText(UserMessage.this);
        et.setText(brief_intro_text);
        //先设置好点击“修改昵称”弹出的dialog
        alertDialog_2 = new AlertDialog.Builder(this);
        alertDialog_2.setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "简介不能为空哦", Toast.LENGTH_SHORT).show();
                        } else {
                            brief_intro_text = input;
                            _User current_user = BmobUser.getCurrentUser(_User.class);
                            current_user.setBrief_intro(brief_intro_text);
                            current_user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        Toast.makeText(getApplication(), "简介修改成功", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getApplication(), "简介修改失败", Toast.LENGTH_SHORT).show();
                                    }
                                    refresh();
                                }
                            });
                        }

                    }
                })
                .setNegativeButton("取消", null).create();
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

    public void cropRawCoverPage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 12);
        intent.putExtra("aspectY", 5);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_XX);
        intent.putExtra("outputY", output_YY);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST);
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
                    //上传图片
                    uploadImage(img_uri);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void setImageToCoverPage(Intent intent) {
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
            File file = new File(Environment.getExternalStorageDirectory()+"/Pic", "coverpage.jpg");
            FileOutputStream out = null;
            try {//打开输出流 将图片数据填入文件中
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                try {
                    out.flush();
                    img_uri = Environment.getExternalStorageDirectory()+"/Pic/coverpage.jpg";
                    //上传图片
                    uploadCoverPage(img_uri);
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
     * 由URI获取图像的绝对路径
     */
    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 将图片上传到 Bmob
     */
    public void uploadImage(String uri) {
        final BmobFile file = new BmobFile(new File(uri));
        progress.setVisibility(View.VISIBLE);
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //Toast.makeText(getApplication(), "上传成功", Toast.LENGTH_SHORT).show();
                    _User current_user = BmobUser.getCurrentUser(_User.class);
                    current_user.setheadPortrait(file);
                    current_user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                Toast.makeText(getApplication(), "头像更换成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplication(), "头像更换失败", Toast.LENGTH_SHORT).show();
                            }
                            // 更换头像
                            refresh();
                        }
                    });
                } else {
                    Toast.makeText(getApplication(), "上传失败", Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 根据一个网络连接(String url)获取bitmap图像
     */
    private Bitmap getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(10000); //设置连接服务器超时时间
            conn.setReadTimeout(5000);  //设置读取数据超时时间

            conn.connect(); //开始连接

            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            if (responseCode == 200) {
                //访问成功
                InputStream is = conn.getInputStream(); //获得服务器返回的流数据
                Bitmap bitmap = BitmapFactory.decodeStream(is); //根据流数据 创建一个bitmap对象w
                return bitmap;
            } else {
                //访问失败
                Toast.makeText(getApplication(), "访问失败", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //断开连接
            }
        }
        return null;
    }

    public void uploadCoverPage(String uri) {
        final BmobFile image_file = new BmobFile(new File(uri));
        //要先上传图片，之后才能将图片加载到新建数据列里
        progress.setVisibility(View.VISIBLE);
        image_file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    _User current_user = BmobUser.getCurrentUser(_User.class);
                    current_user.setCoverPage(image_file);
                    current_user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getApplication(), "个人主页封面更换成功", Toast.LENGTH_SHORT).show();
                            }else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getApplication(), "个人主页封面更换失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "图片上传失败", Toast.LENGTH_SHORT).show();
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
        if (temp == 1) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
        } else if (temp == 2) {
            intent.putExtra("aspectX", 12);
            intent.putExtra("aspectY", 5);
            intent.putExtra("outputX", 480);
            intent.putExtra("outputY", 200);
        }
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


    /**
     * 刷新
     */
    public void refresh() {
        finish();
        Intent it = new Intent(UserMessage.this, UserMessage.class);
        startActivity(it);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
