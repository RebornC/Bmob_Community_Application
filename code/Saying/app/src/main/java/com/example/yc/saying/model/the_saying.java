package com.example.yc.saying.model;

public class the_saying {

    private String saying_id;
    private String content;

    public the_saying(String saying_id, String content) {
        this.saying_id = saying_id;
        this.content = content;
    }

    public String getSaying_id() {
        return saying_id;
    }

    public String getContent() {
        return content;
    }

}