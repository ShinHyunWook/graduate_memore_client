package com.mglab.memore;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.mglab.memore.R.id.inputId;
import static com.mglab.memore.R.id.inputName;
import static com.mglab.memore.R.id.inputPhone;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class Fragment2 extends Fragment implements OnMapReadyCallback, NetDefine {

    private Button btnGetGPS, btnOffGPS;
    ImageButton btnCamera;
    private Boolean isGPSEnabled = false;
    private Boolean isNetworkEnabled = false;
    private Boolean isGetLocation = false;

    private Location location;
    private Double lat, lon;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_CHANGE_UPDATES = 1000 * 5;

    protected LocationManager locationManager;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker myLocation;
    private PolylineOptions polylineOptions;
    private List<LatLng> arrayPoints;

    private String current_point;

    private AsyncHttpClient asyncHttpClient;
    private ImageView iv;
    private final ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private int captureNumber;
    private Intent intent;
    File mediaFile;
    private ArrayList<String> pathArray = new ArrayList<String>();
    private ArrayList<String> imageNameArray = new ArrayList<String>();

    String my_id;

    static int MEDIA_TYPE_IMAGE = 100;

    Switch switchGPS;
    Bundle savedInstanceState;

    DisplayImageOptions loader_options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        intent = new Intent(getActivity(), CutPopup.class);
        intent.setAction("onCreate");

        View view = (View) inflater.inflate(R.layout.fragment2, container, false);

        get_my_id();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        switchGPS = (Switch) view.findViewById(R.id.switchGPS);
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    getGPSCheck();
                } else if (isChecked == false) {
                    offGPS();
                }
            }
        });

        arrayPoints = new ArrayList<LatLng>();

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();


        //데이터베이스에                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       값입력 테스트
        Button insertData;
        insertData = (Button) view.findViewById(R.id.insertData);

        insertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                insert_data();
            }
        });
        btnCamera = (ImageButton) view.findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchGPS.isChecked() == false) {
                    showRouteSwitchAlert();
                } else {
                    captureNumber = 0;
//                mOnPopup(0);
                    checkStoragePermission();
                }
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        loader_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, bmp.getWidth()/3, bmp.getHeight()/3, false);
                    }
                })
                .considerExifParams(true)
                .build();

        return view;
    }

    public void insert_data() {
        String location_data;
        JSONManager json = new JSONManager();
        location_data = json.bindJSON((ArrayList<LatLng>) arrayPoints);

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        params.put("my_id", my_id);
        params.put("loc_time", formatDate);
        params.put("loc_data", location_data);
        asyncHttpClient.post(SERVIERIP + "save_location", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "데이터를 성공적으로 삽입하였습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(getActivity(), "데이터 삽입 실패.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showRouteSwitchAlert() {
        switchGPS.setChecked(false);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("이동경로 기록 스위치");
        alertDialog.setMessage("이동 경로 스위치가 꺼져있습니다 " +
                "\n 이동경로 기록을 시작한 후 컷을 만들어주세요.");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void mOnPopup(int size) {
        //데이터 담아서 팝업(액티비티) 호출
        intent.putExtra("size", size);
//        intent.putExtra("imagePathList", pathArray);
//        intent.putExtra("imageNameList", imageNameArray);
        intent.putExtra("location_data", current_point);
        startActivity(intent);

        MakeCut makeCut = new MakeCut();
        makeCut.setOnAddCut(new MakeCut.OnAddCut() {
            @Override
            public void onAddCut(String imagePath, String cusScript) {
                File file = new File(imagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 16;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth() + 100, bitmap.getHeight() + 120, conf);
                Canvas canvas1 = new Canvas(bmp);

                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.parseColor("#00000000"));
                color.setStyle(Paint.Style.FILL);
                color.setStrokeWidth(10); // set stroke width

                Paint color2 = new Paint();
                color2.setTextSize(35);
                color2.setColor(Color.parseColor("#ffc200"));
                color2.setStyle(Paint.Style.FILL);
                color2.setStrokeWidth(10); // set stroke width

                /**
                 * Rotater Source
                 */
                int orientation = 0;
                try {
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                } catch (IOException e) {
                    e.toString();
                }
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = ImageRotater.rotateBitmap(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = ImageRotater.rotateBitmap(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = ImageRotater.rotateBitmap(bitmap, 270);
                        break;
                    default:
                        break;
                }

                canvas1.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas1.drawRoundRect(20, 0, bitmap.getWidth() + (bmp.getWidth() - bitmap.getWidth()) / 2 + 20, bitmap.getHeight() + (bmp.getHeight() - bitmap.getHeight()) / 2, 30, 30, color2);
                }

                Point point1_draw = new Point((bitmap.getWidth() + (bmp.getWidth() - bitmap.getWidth()) / 2) / 2 + 20, bitmap.getHeight() + (bmp.getHeight() - bitmap.getHeight()) / 2 + 50);
                Point point2_draw = new Point((bitmap.getWidth() + (bmp.getWidth() - bitmap.getWidth()) / 2) / 4 + 20, bitmap.getHeight() + (bmp.getHeight() - bitmap.getHeight()) / 2);
                Point point3_draw = new Point((bitmap.getWidth() + (bmp.getWidth() - bitmap.getWidth()) / 2) / 4 * 3 + 20, bitmap.getHeight() + (bmp.getHeight() - bitmap.getHeight()) / 2);

                Path path = new Path();
                path.moveTo(point1_draw.x, point1_draw.y);
                path.lineTo(point2_draw.x, point2_draw.y);
                path.lineTo(point3_draw.x, point3_draw.y);
                path.lineTo(point1_draw.x, point1_draw.y);
                path.close();

                canvas1.drawPath(path, color2);
                canvas1.drawBitmap(bitmap, (bmp.getWidth() - bitmap.getWidth()) / 4 + 20, (bmp.getHeight() - bitmap.getHeight()) / 4, color2);


                canvas1.drawBitmap(bitmap, 0, 0, color);
//                canvas1.drawText("사진", 30, 40, color);

                markerOptions.add(new MarkerOptions().position(new LatLng(lat, lon))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 1));

                myLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 1));
                intent.setAction("makeCut");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                captureNumber++;
                //권한 있음
                DMediaScanner scanner = new DMediaScanner(getActivity());
                scanner.startScan(mediaFile.getPath());

                imageNameArray.add(mediaFile.getName());
                pathArray.add(mediaFile.getAbsolutePath());

                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 아래 정의한 capture한 사진의 저장 method를 실행 한 후
                Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                // 먼저 선언한 intent에 해당 file 명의 값을 추가로 저장한다.
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                intent.setAction("makeCut");
                startActivityForResult(camIntent, 1);
            } else if (captureNumber != 0) {
                mOnPopup(captureNumber);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        intent = getActivity().getIntent();
        try {
            Log.i("Rabbit", intent.getAction());
        } catch (Exception e) {

        }
        if (intent.getAction() != null && intent.getAction().equals("makeCut")) {
            Log.i("Rabbit", "고정...");
            //현재 위치 고정
        } else if (!intent.getAction().equals("makeCut")) {
            arrayPoints.clear();
            markerOptions.clear();
            mapView.getMapAsync(this);
        }
    }

    public void showSettingsAlert() {
        switchGPS.setChecked(false);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다" +
                "\n 설정창으로 가시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void getGPSCheck() {
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isNetworkEnabled) {
            showSettingsAlert();
        } else if (!isGPSEnabled) {
            showSettingsAlert();
        } else {
            getGPS();
        }
    }

    private void getGPS() {
        isGetLocation = true;
        if (isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("kangjunho", "권한1 없음");
                return;
            }
            Log.i("kangjunho", "권한1 있음");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_CHANGE_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }

        if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_CHANGE_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) { // GPS로 위경도 값을 받았을 경우
                lat = location.getLatitude();
                lon = location.getLongitude();

                String result = String.valueOf("GPS 수신 \n위도 : " + String.valueOf(lat) + "\n경도 : " + String.valueOf(lon));

                Log.i("kangjunho1", result);
//                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            } else { // 네트워크로 위경도 값을 받았을 경우
                lat = location.getLatitude();
                lon = location.getLongitude();

                String result = String.valueOf("네트워크 수신 \n위도 : " + String.valueOf(lat) + "\n경도 : " + String.valueOf(lon));

                Log.i("kangjunho2", result);
//                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

            googleMap.clear();
            myLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("현재위치"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));

            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.parseColor("#FF9164a8"));
            polylineOptions.width(10);
            arrayPoints.add(new LatLng(lat, lon));
            polylineOptions.addAll(arrayPoints);
            googleMap.addPolyline(polylineOptions);

            for (int i = 0; i < markerOptions.size(); i++) {
                myLocation = googleMap.addMarker(markerOptions.get(i));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.496375, 126.957448)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void offGPS() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
        Toast.makeText(getActivity(), "GPS 수신이 정상적으로 종료되었습니다", Toast.LENGTH_SHORT).show();
    }

    private void checkStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한 없음
            repuestPermission();
        } else {
            pathArray.clear();
            imageNameArray.clear();

            JSONManager json = new JSONManager();
            String location_data = json.bindJSON((ArrayList<LatLng>) arrayPoints);
            try {
                JSONArray tmp = new JSONArray(location_data);
                current_point = tmp.getString(tmp.length() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //권한 있음
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // 아래 정의한 capture한 사진의 저장 method를 실행 한 후
            Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
            // 먼저 선언한 intent에 해당 file 명의 값을 추가로 저장한다.
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            intent.setAction("makeCut");
            startActivityForResult(intent2, 1);
            return;
        }
    }

    private void repuestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, 1001);
                Toast.makeText(getActivity(), "접근권한을 허용해주세요", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /** Create a file Uri for saving an image or video */
    /**
     * 저장할 image file의 이름(URI)을 값을 반환. onCreate()에서 fileUri 값에 반영되는 값을 반환하도록 설계되어 있음
     */
    private Uri getOutputMediaFileUri(int type) {
        // 아래 capture한 사진이 저장될 file 공간을 생성하는 method를 통해 반환되는 File의 URI를 반환
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // 외부 저장소에 이 App을 통해 촬영된 사진만 저장할 directory 경로와 File을 연결
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Memore");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) { // 해당 directory가 아직 생성되지 않았을 경우 mkdirs(). 즉 directory를 생성한다.
            if (!mediaStorageDir.mkdirs()) { // 만약 mkdirs()가 제대로 동작하지 않을 경우, 오류 Log를 출력한 뒤, 해당 method 종료
                Log.d("Memore", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        // File 명으로 file의 생성 시간을 활용하도록 DateFormat 기능을 활용
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;

        }

        return mediaFile; // 생성된 File valuable을 반환
    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
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


    private void galleryAddPic(File outputFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(outputFile); //out is your output file
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
        } else {
            getActivity().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
}

