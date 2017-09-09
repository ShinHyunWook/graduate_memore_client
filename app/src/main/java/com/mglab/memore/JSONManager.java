package com.mglab.memore;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 신현욱 on 2017-03-27.
 */

public class JSONManager {
    public static String bindJSON(ArrayList<com.google.android.gms.maps.model.LatLng> list){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < list.size(); i++){
            JSONObject latJson = new JSONObject();
            JSONObject lngJson = new JSONObject();
            try{
                latJson.put("lat", list.get(i).latitude);
                latJson.put("lng", list.get(i).longitude);
            }catch (JSONException e){
                e.printStackTrace();
            }
            jsonArray.put(latJson);
//            jsonArray.put(lngJson);
        }
        return jsonArray.toString();
    }
    public static ArrayList<com.google.android.gms.maps.model.LatLng> parseJSON(String data){
        ArrayList<com.google.android.gms.maps.model.LatLng> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(data);
            Log.i("LUFY", String.valueOf(jsonArray));
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject latObject = jsonArray.getJSONObject(i);
                JSONObject lngObject = jsonArray.getJSONObject(i);
                com.google.android.gms.maps.model.LatLng latLng =
                        new com.google.android.gms.maps.model.LatLng(Double.parseDouble(latObject.get("lat").toString()), Double.parseDouble(lngObject.get("lng").toString()));
                list.add(latLng);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }
}
