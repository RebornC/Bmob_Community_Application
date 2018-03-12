package com.example.yc.saying.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by yc on 2018/2/27.
 */

public class message_fans extends BmobObject {

    private _User initiator;
    private _User acceptor;
    private String acceptor_id;

    public _User getInitiator() {
        return initiator;
    }
    public void setInitiator(_User initiator) {
        this.initiator = initiator;
    }

    public _User getAcceptor() {
        return acceptor;
    }
    public void setAcceptor(_User acceptor) {
        this.acceptor = acceptor;
    }

    public String getAcceptor_id() {
        return acceptor_id;
    }
    public void setAcceptor_id(String acceptor_id) {
        this.acceptor_id = acceptor_id;
    }

}
