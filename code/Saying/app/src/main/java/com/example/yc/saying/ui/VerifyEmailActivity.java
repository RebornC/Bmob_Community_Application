package com.example.yc.saying.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class VerifyEmailActivity extends AppCompatActivity {
    private EditText loginEmail;
    private Button verification_code;
    private String loginEmailText;
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // findView
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        verification_code = (Button) findViewById(R.id.verification_code);
        back = (ImageView) findViewById(R.id.back);

        // 点击事件
        clickEvents();
    }

    public void clickEvents() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailText = loginEmail.getText().toString();
                final String email = loginEmailText;
                if (email.equals("")) {
                    Toast.makeText(getApplication(), "请输入您的邮箱喔~", Toast.LENGTH_SHORT).show();
                } else {
                    BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplication(), "重置密码请求成功，请到" + email + "邮箱进行密码重置操作", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplication(), "失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}

