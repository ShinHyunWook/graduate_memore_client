package com.mglab.memore;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.cache.Resource;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener,NetDefine{

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ImageButton btn1,btn2, btn3, btn4, btn5;
    private DrawerLayout drawerLayout;
    private NavigationView drawerView;
    private ImageButton drawerBtn;
    private AsyncHttpClient asyncHttpClient;
    private String u_email,u_name;
    DisplayImageOptions options;
    ImageButton nav_logout;


    ImageView imView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
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


        set_user_info();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawerView);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageButton logout = (ImageButton) headerView.findViewById(R.id.nav_logout);
        ImageButton btn_edit = (ImageButton) headerView.findViewById(R.id.btn_edit);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserSettingActivity.class);
                startActivity(intent);
            }
        });


//        imView = (ImageView)findViewById(R.id.logoImage);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        btn1 = (ImageButton) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (ImageButton) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        btn3 = (ImageButton) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
//        btn4 = (ImageButton) findViewById(R.id.btn4);
//        btn4.setOnClickListener(this);
        btn5 = (ImageButton) findViewById(R.id.btn5);
        btn5.setOnClickListener(this);

        Resources resources = getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap_on1,null));
            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap2,null));
            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap3,null));
//            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap4,null));
            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap5,null));
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Resources resources = getResources();
                switch (position) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap_on1,null));
                            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap2,null));
                            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap3,null));
//                            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap4,null));
                            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap5,null));
                        }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap1,null));
                            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap_on2,null));
                            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap3,null));
//                            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap4,null));
                            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap5,null));
                        }
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap1,null));
                            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap2,null));
                            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap_on3,null));
//                            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap4,null));
                            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap5,null));
                        }
                        break;
                    case 3:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap1,null));
                            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap2,null));
                            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap3,null));
//                            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap_on4,null));
                            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap5,null));
                        }
                        break;
                    case 4:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn1.setImageDrawable(resources.getDrawable(R.drawable.tap1,null));
                            btn2.setImageDrawable(resources.getDrawable(R.drawable.tap2,null));
                            btn3.setImageDrawable(resources.getDrawable(R.drawable.tap3,null));
//                            btn4.setImageDrawable(resources.getDrawable(R.drawable.tap4,null));
                            btn5.setImageDrawable(resources.getDrawable(R.drawable.tap_on5,null));
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkLocationPermission();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (NavigationView) findViewById(R.id.drawerView);
        drawerBtn = (ImageButton) findViewById(R.id.btn_drawer);

        NavigationMenuView navMenuView = (NavigationMenuView) drawerView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));

        drawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            viewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.btn2) {
            viewPager.setCurrentItem(1);
        } else if (v.getId() == R.id.btn3) {
            viewPager.setCurrentItem(2);
        }
//        else if (v.getId() == R.id.btn4) {
//            viewPager.setCurrentItem(3);
//        }
        else if (v.getId() == R.id.btn5) {
            viewPager.setCurrentItem(4);
        }
    }

    private void checkLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한 없음
            repuestPermission();
        } else {
            //권한 있음
            return;
        }
    }

    private void repuestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, 1001);
                Toast.makeText(this, "제발 설정으로 가서 GPS 좀 켜주세요", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                //권한 거부
                Toast.makeText(this, "권한 허용해주세요", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_friend){
            Intent intent = new Intent(MainActivity.this,FriendSearchActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_setting){
            Intent intent = new Intent(MainActivity.this,UserSettingActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_gallery){
            Intent intent = new Intent(MainActivity.this,GalleryImages.class);
            startActivity(intent);
        }
        return false;
    }

    public void showSettingsAlert() {
        Log.i("LEOEOEO","들어온당꼐요");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("로그아웃");
        alertDialog.setMessage("로그아웃 하시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
                sharedPreferencesManager.Logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    public void set_user_info(){
        String u_id;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
        u_id = sharedPreferencesManager.getLogin();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", u_id);
        asyncHttpClient.post(SERVIERIP+"set_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                TextView user_name,user_email;
                user_name = (TextView)findViewById(R.id.user_name);
                user_email = (TextView)findViewById(R.id.user_email);

                String result = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(result);

                    String u_id;
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
                    u_id = sharedPreferencesManager.getLogin();

                    user_email.setText(u_id);
                    user_name.setText(obj.getString("name"));

                    String imgUrl = "http://"+IP+"/memore/uploads/"+obj.getString("profile");

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imView = (ImageView)findViewById(R.id.logoImage);

                    imageLoader.displayImage(imgUrl,imView, options);

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error",error);
            }
        });
    }
}
