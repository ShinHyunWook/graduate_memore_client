package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-04-02.
 */

public class CutImageModel {

    private int imageId;
    private String imagePath;

    public void setImageId(int id) {
        this.imageId = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    public String getImagePath() {
        return imagePath;
    }
}
