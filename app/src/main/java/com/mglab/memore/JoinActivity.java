package com.mglab.memore;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-03-15.
 */

public class JoinActivity extends AppCompatActivity implements NetDefine, View.OnClickListener {
    private AsyncHttpClient asyncHttpClient;
    private EditText inputId,inputName,inputPassword,inputPhone;
    private Button btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        inputId = (EditText) findViewById(R.id.inputId);
        inputName = (EditText) findViewById(R.id.inputName);
        inputPassword = (EditText) findViewById(R.id.inputPassword_1);
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnJoin) {
            join();
        }
    }

    private void join() {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", inputId.getText().toString());
        params.put("name", inputName.getText().toString());
        params.put("password", inputPassword.getText().toString());
        params.put("phone", inputPhone.getText().toString());
        asyncHttpClient.post(SERVIERIP+"register", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent intent = new Intent(JoinActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error",error);
            }
        });
    }
}
