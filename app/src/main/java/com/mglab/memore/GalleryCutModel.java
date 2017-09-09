package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-05-02.
 */

public class GalleryCutModel {
    String id;
    String first_image;
    String image_list;
    String caption;
    String tag_id_list;

    public void setId(String id) {
        this.id = id;
    }

    public void setFirst_image(String first_image) {
        this.first_image = first_image;
    }

    public void setImage_list(String image_list) {
        this.image_list = image_list;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setTag_id_list(String tag_id_list) {
        this.tag_id_list = tag_id_list;
    }

    public String getId() {

        return id;
    }

    public String getFirst_image() {
        return first_image;
    }

    public String getImage_list() {
        return image_list;
    }

    public String getCaption() {
        return caption;
    }

    public String getTag_id_list() {
        return tag_id_list;
    }
}
