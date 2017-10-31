package com.zebra.scanner.inventory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;
import com.zebra.scanner.inventory.R;
import com.zebra.scanner.inventory.bus.BusEvent;
import com.zebra.scanner.inventory.util.Constants;
import com.zebra.scanner.inventory.util.SettingsManager;
import com.zebra.scannercontrol.BarCodeView;
import com.zebra.scannercontrol.DCSSDKDefs;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zebra.scanner.inventory.app.Application.sdkHandler;

/**
 * Created by HS4895 on 10/26/2017.
 */
public class PairingActivity extends BaseActivity {
    public final String  TAG = getClass().getSimpleName();

    @BindView(R.id.barcodeView)
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        ButterKnife.bind(this);

        if (sdkHandler != null) {
            BarCodeView barCodeViewGenerated = sdkHandler.dcssdkGetPairingBarcode(DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_CRADLE_HOST, DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.SET_FACTORY_DEFAULTS);
            relativeLayout.addView(barCodeViewGenerated);
        }
    }

    @Subscribe
    public void eventBusListener(final BusEvent event) {

        if (event.getEventName().equalsIgnoreCase(BusEvent.EventCommunicationSessionEstablished)) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
    }

}
