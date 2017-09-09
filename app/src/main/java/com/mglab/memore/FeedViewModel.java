package com.mglab.memore;

import java.util.ArrayList;

/**
 * Created by 신현욱 on 2017-04-22.
 */

public class FeedViewModel {
    String user_id;
    String user_name;
    String user_profile;
    String post_time;
    String cut_id_list;
    String start_loc_date;
    String end_loc_date;
    String feed_id;

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getUser_profile() {

        return user_profile;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPost_time() {
        return post_time;
    }

    public String getCut_id_list() {
        return cut_id_list;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public void setCut_id_list(String cut_id_list) {
        this.cut_id_list = cut_id_list;
    }

    public String getStart_loc_date() {
        return start_loc_date;
    }

    public String getEnd_loc_date() {
        return end_loc_date;
    }

    public void setStart_loc_date(String start_loc_date) {
        this.start_loc_date = start_loc_date;
    }

    public void setEnd_loc_date(String end_loc_date) {
        this.end_loc_date = end_loc_date;
    }
}
