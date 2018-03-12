package com.example.yc.saying.model;

public class hot_sayings {

    private String id;
    private String content;
    private String image;//图片的uri路径

    public hot_sayings(String id, String content, String image) {
        this.id = id;
        this.content = content;
        this.image = image;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
