package com.mglab.memore;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.R.id.list;

/**
 * Created by 신현욱 on 2017-04-16.
 */

public class TagListviewAdapter extends BaseAdapter implements NetDefine{
    private LayoutInflater inflater;
    private ArrayList<TagFriendItem> data;
    private int layout;
    private AsyncHttpClient asyncHttpClient;
    String my_id;
    int request;
    boolean check[];
    DisplayImageOptions options;


    public TagListviewAdapter(Context context, int layout, ArrayList<TagFriendItem> data, String my_id, int request){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
        this.my_id = my_id;
        this.request = request;
        check = new boolean[getCount()];

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.unknown) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.unknown) // 값이 없을때
                .showImageOnFail(R.drawable.unknown) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getName();}
    @Override
    public long getItemId(int position){return position;}

    public String getID(int position){return data.get(position).getID();}

    static class ViewHolder {
        protected ImageView profile;
        protected TextView name;
        protected TextView email;
        public TextView f_id;
        public CheckBox tag_check;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.profile = (ImageView) convertView.findViewById(R.id.item_friend_profile);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_friend_name);
            viewHolder.email = (TextView) convertView.findViewById(R.id.item_friend_email);
            viewHolder.f_id = (TextView) convertView.findViewById(R.id.item_friend_id);
            viewHolder.tag_check = (CheckBox) convertView.findViewById(R.id.tag_check);
            viewHolder.tag_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    data.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.item_friend_profile, viewHolder.profile);
            convertView.setTag(R.id.item_friend_name, viewHolder.name);
            convertView.setTag(R.id.item_friend_email, viewHolder.email);
            convertView.setTag(R.id.item_friend_id, viewHolder.f_id);
            convertView.setTag(R.id.tag_check, viewHolder.tag_check);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tag_check.setTag(position);

        TagFriendItem listviewitem=data.get(position);

//        ImageView profile=(ImageView)convertView.findViewById(R.id.item_friend_profile);

        String imgUrl = "http://" + IP + "/memore/uploads/" + listviewitem.getProfile();

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgUrl, viewHolder.profile, options);

        viewHolder.tag_check.setChecked(data.get(position).isSelected());
//        final TextView name=(TextView)convertView.findViewById(R.id.item_friend_name);
        viewHolder.name.setText(listviewitem.getName());

//        TextView email=(TextView)convertView.findViewById(R.id.item_friend_email);
        viewHolder.email.setText(listviewitem.getEmail());
//        TextView f_id=(TextView)convertView.findViewById(R.id.item_friend_id);
        viewHolder.f_id.setText(listviewitem.getID());

        viewHolder.tag_check = (CheckBox)convertView.findViewById(R.id.tag_check);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.tag_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalViewHolder.tag_check.isChecked()==true){
                    check[position] = true;
                }else if(finalViewHolder.tag_check.isChecked()==false){
                    check[position] = false;
                }
            }
        });
        return convertView;
    }

    public boolean[] getCheck(){return check;}

}
