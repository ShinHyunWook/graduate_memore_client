package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-05-03.
 */

public class MakeCutModel {
    String imagePath;
    private boolean selected;

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getImagePath() {

        return imagePath;
    }

    public boolean isSelected() {
        return selected;
    }
}
