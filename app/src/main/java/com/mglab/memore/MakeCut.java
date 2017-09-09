package com.mglab.memore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wefika.flowlayout.FlowLayout;

import org.intellij.lang.annotations.JdkConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-30.
 */

public class MakeCut extends AppCompatActivity implements NetDefine,View.OnClickListener {
    String location_data;
    ArrayList<String> imageList;
    String my_id;
    private Button btnMakeCut;
    GalleryGridAdapter myImageAdapter;
    GridView gridview;
    ImageButton btn_next,addFriend;
    Button btn_complete;
    String result_tag;
    public static OnAddCut mOnAddCut;
    private File[] sourceFile = null;
    ProgressDialog progressDialog;
    AsyncHttpClient asyncHttpClient;
    EditText cutEdit;
    CutPopup p_cut = (CutPopup) CutPopup.p_cut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_cut);

        btn_next = (ImageButton)findViewById(R.id.btn_next);
        btn_complete = (Button)findViewById(R.id.btn_complete);
        addFriend = (ImageButton) findViewById(R.id.addFriend);
        cutEdit = (EditText) findViewById(R.id.cutEdit);

        btn_next.setVisibility(View.GONE);
        btn_complete.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        imageList = intent.getStringArrayListExtra("imageList");
        location_data = intent.getStringExtra("location_data");
        my_id = intent.getStringExtra("my_id");

        btnMakeCut = (Button) findViewById(R.id.btn_complete);
        btnMakeCut.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        MakeCut.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int window_size = dm.widthPixels / 4;

        myImageAdapter = new GalleryGridAdapter(this,window_size,R.layout.gridview_item);

        for(int i =0;i<imageList.size();i++){
            myImageAdapter.add(imageList.get(i));
        }

        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(myImageAdapter);

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeCut.this, TagFriend.class);
                startActivityForResult(intent, 400);
            }
        });
    }

    public interface OnAddCut {
        public void onAddCut(String imagePath, String cusScript);
    }

    public void setOnAddCut(OnAddCut onAddCut) {
        this.mOnAddCut = onAddCut;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_complete) {
            if (mOnAddCut != null) {
                mOnAddCut.onAddCut(imageList.get(0), cutEdit.getText().toString());
                upload_cut(imageList);
            } else {
                Toast.makeText(MakeCut.this, "nullnullnull", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400) {
            FlowLayout flow_layout = (FlowLayout) findViewById(R.id.flow_layout);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,FlowLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,5,0);
            try {
                String result = String.valueOf(data.getSerializableExtra("TagName"));
                try {
                    JSONArray json = new JSONArray(result);
                    ArrayList<TextView> t_name = new ArrayList<TextView>();
                    for (int i = 0; i < json.length(); i++) {
                        t_name.add(new TextView(this));
                        t_name.get(i).setText(String.valueOf(json.get(i)));
                        t_name.get(i).setGravity(Gravity.CENTER);
                        t_name.get(i).setTextColor(Color.WHITE);
                        t_name.get(i).setBackgroundResource(R.drawable.tag_round);
                        t_name.get(i).setLayoutParams(layoutParams);
                        flow_layout.addView(t_name.get(i),layoutParams);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result_tag = String.valueOf(data.getSerializableExtra("TagID"));
            }catch (Exception e){
                return;
            }
        }
    }

    public void upload_cut(final ArrayList<String> imagePathList) {

        progressDialog = ProgressDialog.show(MakeCut.this, "", "Uploading files to server.....", false);
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            sourceFile = new File[imagePathList.size()];
            for (int i = 0; i < imagePathList.size(); i++) {
                sourceFile[i] = resizeImage(imagePathList.get(i));

                Log.i("number5", sourceFile[i].getAbsolutePath());
            }
            params.put("uploaded_file[]", sourceFile);

        } catch (FileNotFoundException e) {
            e.toString();
        }
        asyncHttpClient.post("http://" + IP + "/memore/Cutuploader.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("SUPPER", "image upload success " + new String(responseBody));
                uploadToDB();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
            }
        });
    }

    public void uploadToDB() {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        try {
            for (int i = 0; i < sourceFile.length; i++) {
                File file = new File(sourceFile[i].getAbsolutePath());
                params.add("elements[]", file.getName());
            }
        } catch (Exception e) {
            e.toString();
        }

        String caption;
        if (String.valueOf(cutEdit.getText()) == "") {
            caption = "";
            params.add("caption", caption);
        } else {
            caption = String.valueOf(cutEdit.getText());
            params.add("caption", caption);
        }
        if (result_tag == null) {
            result_tag = "";
            params.add("tag_id_list", result_tag);
        } else {
            params.add("tag_id_list", result_tag);
        }
        params.add("my_id", my_id);
        params.add("location_data", location_data);
        params.add("tag_id_list", result_tag);
        Log.i("USOF", "image_list : " + imageList.toString() + " id : " + my_id + " caption : " + String.valueOf(cutEdit.getText()) + " location_data : " + location_data + " tag_id_list : " + result_tag);
        asyncHttpClient.post(SERVIERIP + "insert_cut", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                p_cut.finish();
                finish();
                Log.i("PLZ", result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("LUFY", String.valueOf(error));
            }
        });
    }

    private File resizeImage(String imagePath) {
        File tempFile = null;
        int orientation;
        Bitmap imageBitmap;

        if (imagePath == null || imagePath.length() == 0) {
            return null;
        }
        try {
            //meta tag
            imageBitmap = getBitmapFromOriginFile(imagePath);
            ExifInterface exif = new ExifInterface(imagePath);
            //방향
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (imageBitmap == null) {
            return null;
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                imageBitmap = ImageRotater.rotateBitmap(imageBitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                imageBitmap = ImageRotater.rotateBitmap(imageBitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                imageBitmap = ImageRotater.rotateBitmap(imageBitmap, 270);
                break;
        }
        FileOutputStream fOut;
        try {
            tempFile = File.createTempFile("image", ".jpg", ApplicationClass.context.getCacheDir());
            fOut = new FileOutputStream(tempFile);

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageBitmap.recycle();

        return tempFile;
    }

    private Bitmap getBitmapFromOriginFile(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = getSampleSize(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int getSampleSize(BitmapFactory.Options options) {
        int x;
        if (options.outHeight > options.outWidth) {
            x = options.outHeight;
        } else {
            x = options.outWidth;
        }
        int n = (int) Math.ceil(Math.log((double) x / (double) 1024));
        return (int) Math.pow(2, n);
    }

}
