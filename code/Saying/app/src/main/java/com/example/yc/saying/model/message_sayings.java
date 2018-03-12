package com.example.yc.saying.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by yc on 2018/3/5.
 */

public class message_sayings extends BmobObject {

    private _User initiator;
    private String acceptor_id;
    private String saying_id;
    private String saying_content;

    public _User getInitiator() {
        return initiator;
    }
    public void setInitiator(_User initiator) {
        this.initiator = initiator;
    }

    public String getAcceptor_id() {
        return acceptor_id;
    }
    public void setAcceptor_id(String acceptor_id) {
        this.acceptor_id = acceptor_id;
    }

    public String getSaying_id() {
        return saying_id;
    }
    public void setSaying_id(String saying_id) {
        this.saying_id = saying_id;
    }

    public String getSaying_content() {
        return saying_content;
    }
    public void setSaying_content(String saying_content) {
        this.saying_content = saying_content;
    }

}
