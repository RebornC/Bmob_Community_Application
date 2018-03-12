package com.example.yc.saying.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by yc on 2018/1/18.
 */

public class collection extends BmobObject {

    private String name;
    private BmobFile image;
    private String introduction;
    private _User userId;
    private String userOnlyId;
    private BmobRelation collectedSayings;
    private Boolean publicOrNot;
    private Integer like_sum = 0;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public BmobFile getImage() {
        return image;
    }
    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public _User getUserId() {
        return userId;
    }
    public void setUserId(_User userId) {
        this.userId = userId;
    }

    public String getUserOnlyId() {
        return userOnlyId;
    }
    public void setUserOnlyId(String userOnlyId) {
        this.userOnlyId = userOnlyId;
    }

    public BmobRelation getCollectedSayings() {
        return collectedSayings;
    }
    public void setCollectedSayings(BmobRelation collectedSayings) {
        this.collectedSayings = collectedSayings;
    }

    public Boolean getPublicOrNot() {
        return publicOrNot;
    }

    public void setPublicOrNot(Boolean publicOrNot) {
        this.publicOrNot = publicOrNot;
    }

    public Integer getLike_sum() {
        return like_sum;
    }
    public void setLike_sum(Integer like_sum) {
        this.like_sum = like_sum;
    }

}
