package com.example.yc.saying.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by yc on 2018/3/5.
 */

public class message_books extends BmobObject {

    private String user_id;
    private String user_name;
    private String acceptor_id;
    private collection focus_book;

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAcceptor_id() {
        return acceptor_id;
    }
    public void setAcceptor_id(String acceptor_id) {
        this.acceptor_id = acceptor_id;
    }

    public collection getFocus_book() {
        return focus_book;
    }
    public void setFocus_book(collection focus_book) {
        this.focus_book = focus_book;
    }

}
