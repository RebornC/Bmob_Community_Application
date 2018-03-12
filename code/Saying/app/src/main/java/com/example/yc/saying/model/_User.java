package com.example.yc.saying.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by yc on 2018/2/2.
 */

public class _User extends BmobUser {

    private BmobFile headPortrait;
    private BmobFile coverPage;
    private String nickName;
    private String brief_intro;
    private BmobRelation focusId;
    private BmobRelation focusBook;
    private BmobRelation focusSaying;
    private Integer focusId_sum = 0;
    private user_followers follower_id;
    private BmobRelation myCollection;

    public BmobFile getHeadPortrait() {
        return headPortrait;
    }

    public void setheadPortrait(BmobFile headPortrait) {
        this.headPortrait = headPortrait;
    }

    public BmobFile getCoverPage() {
        return coverPage;
    }

    public void setCoverPage(BmobFile coverPage) {
        this.coverPage = coverPage;
    }

    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setBrief_intro(String brief_intro) {
        this.brief_intro = brief_intro;
    }
    public String getBrief_intro() {
        return this.brief_intro;
    }

    public BmobRelation getFocusId() {
        return focusId;
    }
    public void setFocusId(BmobRelation focusId) {
        this.focusId = focusId;
    }

    public BmobRelation getFocusBook() {
        return focusBook;
    }
    public void setFocusBook(BmobRelation focusBook) {
        this.focusBook = focusBook;
    }

    public BmobRelation getFocusSaying() {
        return focusSaying;
    }
    public void setFocusSaying(BmobRelation focusSaying) {
        this.focusSaying = focusSaying;
    }

    public Integer getFocusId_sum() {
        return focusId_sum;
    }
    public void setFocusId_sum(Integer focusId_sum) {
        this.focusId_sum = focusId_sum;
    }

    public user_followers getFollower_id() {
        return this.follower_id;
    }

    public void setFollower_id(user_followers follower_id) {
        this.follower_id = follower_id;
    }

    public BmobRelation getMyCollection() {
        return myCollection;
    }
    public void setMyCollection(BmobRelation myCollection) {
        this.myCollection = myCollection;
    }

}
