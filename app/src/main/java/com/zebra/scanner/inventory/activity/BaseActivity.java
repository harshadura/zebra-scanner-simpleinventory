package com.zebra.scanner.inventory.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;
import com.zebra.scanner.inventory.app.Application;
import com.zebra.scanner.inventory.bus.BusEvent;
import com.zebra.scanner.inventory.bus.BusProvider;
import com.zebra.scanner.inventory.util.Barcode;
import com.zebra.scanner.inventory.util.Constants;
import com.zebra.scanner.inventory.util.SettingsManager;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;

/**
 * Created by HS4895 on 10/26/2017.
 */
public class BaseActivity extends AppCompatActivity implements IDcsSdkApiDelegate {

    public Bus bus = BusProvider.getInstance();
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bus.register(this);
        mContext = this;

// Setting up the SDK delegate to receive events
        Application.sdkHandler.dcssdkSetDelegate(this);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        initilize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    public void initilize(){
        int notifications_mask = 0;
// We would like to subscribe to all scanner available/not-available events
        notifications_mask |=
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value;
// We would like to subscribe to all scanner connection events
        notifications_mask |=
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value;
// We would like to subscribe to all barcode events
        notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value;

        // enable scanner detection
        Application.sdkHandler.dcssdkEnableAvailableScannersDetection(true);

// subscribe to events set in notification mask
        Application.sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
    }

    /* ###################################################################### */
    /* ########## IDcsSdkApiDelegate Protocol implementation ################ */
    /* ###################################################################### */
    @Override
    public void dcssdkEventScannerAppeared(DCSScannerInfo availableScanner) {
        System.out.println("sdfsdf");
    }

    @Override
    public void dcssdkEventScannerDisappeared(int scannerID) {
    }


    @Override
    public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo activeScanner) {
        SettingsManager.addSetting(Constants.SCANNER_IDENTITY, activeScanner.getScannerID(), mContext);
        BusEvent event = new BusEvent(BusEvent.EventCommunicationSessionEstablished, activeScanner);
        bus.post(event);
    }

    @Override
    public void dcssdkEventCommunicationSessionTerminated(int scannerID) {
    }

    @Override
    public void dcssdkEventBarcode(final byte[] barcodeData, int barcodeType, int fromScannerID) {
        Barcode barcode=new Barcode(barcodeData,barcodeType,fromScannerID);
        BusEvent event = new BusEvent(BusEvent.EventBarcode, barcode);
        bus.post(event);

    }

    @Override
    public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent){
    }

    @Override
    public void dcssdkEventAuxScannerAppeared(DCSScannerInfo newTopology, DCSScannerInfo auxScanner) {
    }


    @Override
    public void dcssdkEventImage(byte[] imageData, int fromScannerID) {

    }

    @Override
    public void dcssdkEventVideo(byte[] videoFrame, int fromScannerID) {

    }

}