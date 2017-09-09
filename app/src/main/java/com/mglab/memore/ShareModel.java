package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-05-05.
 */

public class ShareModel {
    String email;
    String name;
    String profile;
    String user_id;
    String image_name;
    String cut_id;
    String caption;
    String image_list;
    String tag_id_list;

    public void setCut_caption(String cut_caption) {
        this.caption = cut_caption;
    }

    public void setImage_list(String image_list) {
        this.image_list = image_list;
    }

    public void setTag_id_list(String tag_id_list) {
        this.tag_id_list = tag_id_list;
    }


    public String getCut_caption() {

        return caption;
    }

    public String getImage_list() {
        return image_list;
    }

    public String getTag_id_list() {
        return tag_id_list;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public void setCut_id(String cut_id) {
        this.cut_id = cut_id;
    }

    public String getEmail() {

        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public String getCut_id() {
        return cut_id;
    }
}
