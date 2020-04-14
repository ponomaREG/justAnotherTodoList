package com.test.taskcurrent.Services;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.test.taskcurrent.helpers.FactoryForWidget;

public class ServiceRemoteForFactoryViews extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("onGETVIEWFAc","!");
        return new FactoryForWidget(getApplicationContext(),intent);
    }
}
