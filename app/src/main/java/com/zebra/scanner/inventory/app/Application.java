package com.zebra.scanner.inventory.app;

import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.SDKHandler;

import java.util.ArrayList;

/**
 * Created by HS4895 on 10/26/2017.
 */
public class Application extends android.app.Application {
    //Instance of SDK Handler
    public static SDKHandler sdkHandler;

    //Barcode data
    @Override
    public void onCreate() {
        super.onCreate();
        sdkHandler = new SDKHandler(this);
    }
}