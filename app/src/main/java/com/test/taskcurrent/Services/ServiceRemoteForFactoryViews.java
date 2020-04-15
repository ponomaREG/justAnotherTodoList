package com.test.taskcurrent.Services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.test.taskcurrent.helpers.FactoryForWidget;

public class ServiceRemoteForFactoryViews extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FactoryForWidget(getApplicationContext(),intent);
    }
}
