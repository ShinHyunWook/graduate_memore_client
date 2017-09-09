package com.mglab.memore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by 신현욱 on 2017-03-28.
 */

public class CutPopup extends AppCompatActivity implements NetDefine,View.OnClickListener{
    private List<CutImageModel> cutImage = new ArrayList<CutImageModel>();
    public static OnAddCut mOnAddCut;
    String my_id;
    String location_data;
    AsyncHttpClient asyncHttpClient;
    GalleryGridAdapter myImageAdapter;
    ArrayList<String> imageList;
    HashMap<Integer,String> SelectCutList = new HashMap<Integer, String>();
    ImageButton btn_next;
    public static Activity p_cut;

    @Override
    public void onClick(View v) {

    }

    public interface OnAddCut {
        public void onAddCut(String imagePath, String cusScript);
    }

    public void setOnAddCut(OnAddCut onAddCut) {
        this.mOnAddCut = onAddCut;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        setContentView(R.layout.cut_popup);

        get_my_id();
        p_cut = CutPopup.this;
        Intent intent = getIntent();
        final int size = intent.getIntExtra("size", 0);
        location_data = intent.getStringExtra("location_data");

//        imagePathList = (ArrayList<String>) getIntent().getSerializableExtra("imagePathList");
//        imageNameList = (ArrayList<String>) getIntent().getSerializableExtra("imageNameList");

        DisplayMetrics dm = new DisplayMetrics();
        CutPopup.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int window_size = dm.widthPixels / 4;
        btn_next = (ImageButton)findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageList = new ArrayList<String>();
                if(!SelectCutList.isEmpty()){
                    for(int i = 0;i<size;i++){
                        if(SelectCutList.containsKey(i)){
                            imageList.add(SelectCutList.get(i));
                        }
                    }
                }
                Intent intent = new Intent(CutPopup.this,MakeCut.class);
                intent.putExtra("imageList", imageList);
                intent.putExtra("location_data", location_data);
                intent.putExtra("my_id", my_id);
                startActivity(intent);
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gridview);
        myImageAdapter = new GalleryGridAdapter(this,window_size,R.layout.gridview_item);
        gridview.setAdapter(myImageAdapter);

        gridview.setOnItemClickListener(myOnItemClickListener);

        getGallery(size);

    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String prompt = (String) parent.getItemAtPosition(position);
            if(SelectCutList.get(position)==null){
                SelectCutList.put(position,prompt);
                ImageView layer = (ImageView) view.findViewById(R.id.imageView);
                layer.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SCREEN);
            }else{
                SelectCutList.remove(position);
                ImageView test = (ImageView) view.findViewById(R.id.imageView);
                test.clearColorFilter();
            }
        }
    };


    private void getGallery(int size) {
        Log.i("ZORO", String.valueOf(size));
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor;
        cursor = MediaStore.Images.Media.query(CutPopup.this.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                String.format("%s DESC", MediaStore.Images.Media._ID));
        cursor.moveToFirst();
        for (int i = 0; i < size; i++) {
            File file = new File(cursor.getString(1)).getAbsoluteFile();
            CutImageModel cutImageModel = new CutImageModel();
            cutImageModel.setImageId(cursor.getInt(0));
            cutImageModel.setImagePath(file.getAbsolutePath());
            cutImage.add(cutImageModel);
            myImageAdapter.add(file.getAbsolutePath());
//            galleryAdapter.notifyItemInserted(cutImage.size() - 1);
            cursor.moveToNext();
        }
//        galleryAdapter.notifyDataSetChanged();
    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(CutPopup.this);
        u_email = sharedPreferencesManager.getLogin();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", u_email);
        asyncHttpClient.post(SERVIERIP + "search_person", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    my_id = obj.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
            }
        });
    }

}
