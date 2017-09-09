package com.mglab.memore;

/**
 * Created by 신현욱 on 2017-04-06.
 */

public class FriendItem {
    private String profile;
    private String name;
    private  String email;

    public String getProfile(){
        return profile;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public FriendItem(String profile, String name,String email){
        this.profile = profile;
        this.name = name;
        this.email = email;
    }
}
