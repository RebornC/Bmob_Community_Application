package com.example.yc.saying.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by yc on 2018/2/7.
 */

public class user_followers extends BmobObject {

    private String user_id;
    private BmobRelation followerId;
    private Integer follower_sum = 0;
    private _User user;
    private Integer message_fans_sum = 0;
    private Integer message_fans_read = 0;
    private Integer message_sayings_sum = 0;
    private Integer message_sayings_read = 0;
    private Integer message_books_sum = 0;
    private Integer message_books_read = 0;
    private Integer notification_read = 0;

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public BmobRelation getFollowerId() {
        return followerId;
    }
    public void setFollowerId(BmobRelation followerId) {
        this.followerId = followerId;
    }

    public Integer getFollower_sum() {
        return follower_sum;
    }
    public void setFollower_sum(Integer follower_sum) {
        this.follower_sum = follower_sum;
    }

    public _User getUser() {
        return user;
    }
    public void setUser(_User user) {
        this.user = user;
    }

    public Integer getMessage_fans_sum() {
        return message_fans_sum;
    }
    public void setMessage_fans_sum(Integer message_fans_sum) {
        this.message_fans_sum = message_fans_sum;
    }

    public Integer getMessage_fans_read() {
        return message_fans_read;
    }
    public void setMessage_fans_read(Integer message_fans_read) {
        this.message_fans_read = message_fans_read;
    }

    public Integer getMessage_sayings_sum() {
        return message_sayings_sum;
    }
    public void setMessage_sayings_sum(Integer message_sayings_sum) {
        this.message_sayings_sum = message_sayings_sum;
    }

    public Integer getMessage_sayings_read() {
        return message_sayings_read;
    }
    public void setMessage_sayings_read(Integer message_sayings_read) {
        this.message_sayings_read = message_sayings_read;
    }

    public Integer getMessage_books_sum() {
        return message_books_sum;
    }
    public void setMessage_books_sum(Integer message_books_sum) {
        this.message_books_sum = message_books_sum;
    }

    public Integer getMessage_books_read() {
        return message_books_read;
    }
    public void setMessage_books_read(Integer message_books_read) {
        this.message_books_read = message_books_read;
    }

    public Integer getNotification_read() {
        return notification_read;
    }
    public void setNotification_read(Integer notification_read) {
        this.notification_read = notification_read;
    }
}
