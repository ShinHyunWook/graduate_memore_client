package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-04-27.
 */

public class FeedCutModel {
    String image_name;
    String tag_list;
    String caption;

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setTag_list(String tag_list) {
        this.tag_list = tag_list;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_name() {

        return image_name;
    }

    public String getTag_list() {
        return tag_list;
    }

    public String getCaption() {
        return caption;
    }
}
