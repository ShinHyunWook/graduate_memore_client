package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-04-20.
 */

public class FeedModel {

    private String cutId;
    private String cutName;
    private String cutDescription;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCutId() {
        return cutId;
    }

    public void setCutId(String cutId) {
        this.cutId = cutId;
    }

    public String getCutName() {
        return cutName;
    }

    public void setCutName(String cutName) {
        this.cutName = cutName;
    }

    public String getCutDescription() {
        return cutDescription;
    }

    public void setCutDescription(String cutDescription) {
        this.cutDescription = cutDescription;
    }
}
