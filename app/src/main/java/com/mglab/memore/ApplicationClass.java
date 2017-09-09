package com.mglab.memore;

import android.content.Context;

/**
 * Created by 신현욱 on 2017-04-30.
 */

public class ApplicationClass extends android.app.Application {

    public static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
}
