package com.devdelhi.pointgram.pointgram.model;

public class users_database {
    public String user_name;
    public String user_email;
    public String user_thumbnail;
    public String user_image;
    public String user_status;

    public users_database() {}

    public users_database(String name, String email, String thumbnail, String image, String status) {
        user_name = name;
        user_email = email;
        user_thumbnail = thumbnail;
        user_image = image;
        user_status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

}
