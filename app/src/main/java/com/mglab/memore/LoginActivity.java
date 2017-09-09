package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class LoginActivity  extends AppCompatActivity implements NetDefine {

    private EditText editId,editpassword;
    private AsyncHttpClient asyncHttpClient;
    private Button btnLogin,btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editId = (EditText) findViewById(R.id.editId);
        editpassword = (EditText) findViewById(R.id.editpassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnLogin) {
//                    String idValue = editId.getText().toString();
//                    Toast.makeText(LoginActivity.this, idValue, Toast.LENGTH_SHORT).show();
                    infoCheck();
                }
            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,JoinActivity.class);
                startActivity(intent);
            }
        });
    }
    public void infoCheck(){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", editId.getText().toString());
        params.put("pw", editpassword.getText().toString());
        asyncHttpClient.post(SERVIERIP+"login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(new String(responseBody).equals("true")){
                    Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_LONG).show();
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(LoginActivity.this);
                    sharedPreferencesManager.setLogin(editId.getText().toString());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"로그인 정보가 올바르지 않습니다",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error",error);
            }
        });
    }
}
