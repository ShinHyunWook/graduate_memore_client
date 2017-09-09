package com.mglab.memore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class Fragment5 extends Fragment implements NetDefine{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment5, container, false);
        WebView webView = (WebView)view.findViewById(R.id.web_box);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
//                webView.loadUrl("http://www.naver.com");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://"+IP+"/d3_test/");

        return view;
    }
}
