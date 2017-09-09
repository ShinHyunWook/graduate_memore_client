package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-04-17.
 */

public class TagFriendItem {
    private String profile;
    private String name;
    private String email;
    private String ID;
    private boolean selected;

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getID() {
        return ID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public TagFriendItem(String profile, String name, String email, String ID) {
        this.profile = profile;
        this.name = name;
        this.email = email;
        this.ID = ID;
    }
}
