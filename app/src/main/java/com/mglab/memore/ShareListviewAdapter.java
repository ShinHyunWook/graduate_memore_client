package com.mglab.memore;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-05.
 */

public class ShareListviewAdapter extends BaseAdapter implements NetDefine {
    private LayoutInflater inflater;
    private int layout;
    Context context;
    ArrayList<ShareModel> data;
    DisplayImageOptions options;
    DisplayImageOptions options_people;
    AsyncHttpClient asyncHttpClient;
//    ArrayList<ArrayList> cut_info = new ArrayList<ArrayList>();

    public ShareListviewAdapter(Context context, int layout, ArrayList<ShareModel> data) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.data = data;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.loading) // 값이 없을때
                .showImageOnFail(R.drawable.loading) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        options_people = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.main_logo) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.main_logo) // 값이 없을때
                .showImageOnFail(R.drawable.main_logo) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        LinearLayout btn_cut_detail;
        ImageView share_profile;
        ImageView cut_image;
        TextView share_name;
        TextView share_email;
        TextView cut_caption;
        LinearLayout share_cut_info;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.btn_cut_detail = (LinearLayout) convertView.findViewById(R.id.btn_cut_detail);
            viewHolder.share_profile = (ImageView) convertView.findViewById(R.id.share_profile);
            viewHolder.cut_image = (ImageView) convertView.findViewById(R.id.cut_image);
            viewHolder.share_name = (TextView) convertView.findViewById(R.id.share_name);
            viewHolder.share_email = (TextView) convertView.findViewById(R.id.share_email);

            viewHolder.cut_caption = (TextView) convertView.findViewById(R.id.cut_caption);
            viewHolder.share_cut_info = (LinearLayout) convertView.findViewById(R.id.share_cut_info);

            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageLoader imageLoader = ImageLoader.getInstance();

        viewHolder.btn_cut_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GalleryCutDetail.class);

                intent.putExtra("cut_caption", String.valueOf(data.get(position).getCut_caption()));
                intent.putExtra("cut_id", data.get(position).getCut_id());
                intent.putExtra("cut_image_list", String.valueOf(data.get(position).getImage_list()));
                intent.putExtra("cut_tag_id", String.valueOf(data.get(position).getTag_id_list()));
                intent.putExtra("share_flag", "f");

                v.getContext().startActivity(intent);
            }
        });

        if (data.get(position).getImage_name().equals("")) {
            Log.i("SILLY", data.get(position).getName() + " / " + data.get(position).getImage_name());
        } else {
//            cut_info.add(get_cut_info(data.get(position).cut_id,viewHolder.cut_caption));
            viewHolder.share_cut_info.setVisibility(View.VISIBLE);
        }

        String p_imgUrl = "http://" + IP + "/memore/uploads/" + data.get(position).getProfile();
        String imgUrl = "http://" + IP + "/memore/uploads/" + data.get(position).getImage_name();
        imageLoader.displayImage(imgUrl, viewHolder.cut_image, options);

        imageLoader.displayImage(p_imgUrl, viewHolder.share_profile, options_people);
        viewHolder.share_name.setText(data.get(position).getName());
        viewHolder.share_email.setText(data.get(position).getEmail());
        viewHolder.cut_caption.setText(data.get(position).getCut_caption());

        return convertView;
    }

//    public ArrayList<String> get_cut_info(String cut_id, final TextView cut_caption) {
//        asyncHttpClient = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        final ArrayList<String> cut_info = new ArrayList<String>();
//        params.put("cut_id", cut_id);
//        asyncHttpClient.post(SERVIERIP + "get_cut_info", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    JSONArray arr = new JSONArray(result);
//                    JSONObject obj = new JSONObject(String.valueOf(arr.get(0)));
//    //                    cut_caption.setText(obj.getString("caption"));
//                    Log.i("하하하하",obj.getString("caption"));
//                    cut_info.add(obj.getString("image_name"));
//                    cut_info.add(obj.getString("tag_id_list"));
//                    cut_info.add(obj.getString("caption"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.w("Join Error", error);
//                Log.i("THun", String.valueOf(error));
//                Log.i("ZOROTA", "sgsdgsdgsadgasg");
//            }
//        });
//        return cut_info;
//    }
}
