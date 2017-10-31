package com.zebra.scanner.inventory.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.zebra.scanner.inventory.R;
import com.zebra.scanner.inventory.app.Application;
import com.zebra.scanner.inventory.bus.BusEvent;
import com.zebra.scanner.inventory.util.Barcode;
import com.zebra.scanner.inventory.util.Constants;
import com.zebra.scanner.inventory.util.MessageProvider;
import com.zebra.scanner.inventory.util.SettingsManager;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.RMDAttributes;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_CONFIG_NAME;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_DOM;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_FW_VERSION;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_MODEL_NUMBER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_SERIAL_NUMBER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_ACTION_HIGH_LONG_BEEP_1;

/**
 * Created by HS4895 on 10/26/2017.
 */
public class MainActivity extends BaseActivity {
    public final String  TAG = getClass().getSimpleName();

    private ProgressDialog progressDialog;

    @BindView(R.id.txtItemSugar)
    TextView txtItemSugar;
    @BindView(R.id.txtSugarStockCount)
    TextView txtSugarStockCount;
    @BindView(R.id.txtItemMilk)
    TextView txtItemMilk;
    @BindView(R.id.txtMilkStockCount)
    TextView txtMilkStockCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Subscribe
    public void eventBusListener(final BusEvent event) {

        if (event.getEventName().equalsIgnoreCase(BusEvent.EventBarcode)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Barcode barcode = (Barcode) event.getPayload();
                    Log.v("TAG", "#### Barcode data: " + barcode.toString());

                    String barcodeData = new String(barcode.getBarcodeData());
                    updateUI(barcodeData);

//                    String barcodeType = BarcodeTypes.getBarcodeTypeName(barcode.getBarcodeType());
//                    int length =  barcode.getBarcodeData().length;
//                    MessageProvider.displayAlert(mContext, "ScannerTest", "Barcode: " + barcodeData +  " | " + barcodeType + " | " + length , "OK", null, null, null);

                }
            });

        }
    }

    public void updateUI(String barcodeString){
        if (barcodeString.equals(Constants.MILK_BARCODE_ID)){
            int milkCount = Integer.valueOf(txtMilkStockCount.getText().toString());
            txtMilkStockCount.setText(--milkCount + "");

            if (milkCount%5==0){
                beeperAction();
            }
        }
        if (barcodeString.equals(Constants.SUGAR_BARCODE_ID)){
            int sugarCount = Integer.valueOf(txtSugarStockCount.getText().toString());
            txtSugarStockCount.setText(--sugarCount + "");

            if (sugarCount%5==0){
                beeperAction();
            }
        }
    }

    public void beeperAction( ) {
// set beeper to perform a HIGH pitch SHORT duration tone
        int value = RMD_ATTR_VALUE_ACTION_HIGH_LONG_BEEP_1;
        String inXML = "<inArgs><scannerID>" + getIntent().getIntExtra(
                Constants.SCANNER_ID, 0) + "</scannerID><cmdArgs><arg-int>"
                + Integer.toString(value) +"</arg-int></cmdArgs></inArgs>";
// Exceute in an AsyncTask to remove UI blocking
        int scannerId = SettingsManager.getSetting(Constants.SCANNER_IDENTITY, 0, mContext);

        new BeepAsyncTask(scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION).execute(
                new String[]{inXML});
    }

    private class BeepAsyncTask extends AsyncTask<String,Integer,Boolean> {
        int scannerId;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
        public BeepAsyncTask(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode){
            this.scannerId=scannerId;
            this.opcode=opcode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "ScannerTest", "Connect To scanner...", true);

        }
        @Override
        protected Boolean doInBackground(String... strings) {
            return executeCommand(opcode, strings[0], null, scannerId);
        }
        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(!b){
                Toast.makeText(MainActivity.this,
                        "Cannot perform beeper action", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE opCode, String inXML, StringBuilder outXML, int scannerID) {
        if (Application.sdkHandler != null)
        {
            if(outXML == null){
                outXML = new StringBuilder();
            }
            DCSSDKDefs.DCSSDK_RESULT result=Application.sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(opCode,inXML,outXML,scannerID);
            if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS)
                return true;
            else if(result==DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                return false;
        }
        return false;
    }

    private void fetchAssertInfo() {
// get current Scanner ID
        int scannerID = SettingsManager.getSetting(Constants.SCANNER_IDENTITY, 0, mContext);

        if (scannerID != -1) {
// Creating beginning of XML argument string with Scanner ID
// of Active Scanner
            String in_xml = "<inArgs><scannerID>" + scannerID
                    + " </scannerID><cmdArgs><arg-xml><attrib_list>";
// Add attribute values to list
            in_xml+= RMD_ATTR_MODEL_NUMBER;
            in_xml+=",";
            in_xml+= RMD_ATTR_SERIAL_NUMBER;
            in_xml+=",";
            in_xml+= RMD_ATTR_FW_VERSION;
            in_xml+=",";
            in_xml+= RMD_ATTR_CONFIG_NAME;
            in_xml+=",";
            in_xml+= RMD_ATTR_DOM;
            in_xml += "</attrib_list></arg-xml></cmdArgs></inArgs>";
// Run as Async Task to free up UI
            new RSMAsyncTask(scannerID,
                    DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET).execute(new String[]{in_xml});
        } else {
// Do not have a valid scanner ID, show popup error
            Toast.makeText(this, Constants.INVALID_SCANNER_ID_MSG,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.settings_menu_item:
                fetchAssertInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class RSMAsyncTask extends AsyncTask<String,Integer,Boolean>{
        int scannerId;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
        public RSMAsyncTask(int scannerId,  DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode){
            this.scannerId=scannerId;
            this.opcode=opcode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "ScannerTest", "Executing command...", true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean result = false;
            StringBuilder sb = new StringBuilder() ;
            result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET) {
                if (!result) {
                    return result;
                } else {
                    try {
                        Log.i(TAG,sb.toString());
                        int i = 0;
                        int attr_id = -1;
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    Log.i(TAG,"Name of the end tag: "+name);
                                    if (name.equals("id")) {
                                        attr_id = Integer.parseInt(text.trim());
                                        Log.i(TAG,"ID tag found: ID: "+attr_id);
                                    } else if (name.equals("value")) {
                                        final String attr_val =  text.trim();
                                        Log.i(TAG,"Value tag found: Value: "+attr_val);
                                        if (RMD_ATTR_MODEL_NUMBER == attr_id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MessageProvider.displayAlert(MainActivity.this, "ScannerTest", "Model Number: " + attr_val, "OK", null, null, null);
                                                }
                                            });

                                        } else  if (RMDAttributes.RMD_ATTR_SERIAL_NUMBER == attr_id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    ((TextView) findViewById(R.id.txtSerial)).setText(attr_val);
                                                    MessageProvider.displayAlert(MainActivity.this, "ScannerTest", "Serial number: " + attr_val, "OK", null, null, null);

                                                }
                                            });

                                        }else  if (RMD_ATTR_FW_VERSION == attr_id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MessageProvider.displayAlert(MainActivity.this, "ScannerTest", "FW version: " + attr_val, "OK", null, null, null);

//                                                    ((TextView) findViewById(R.id.txtFW)).setText(attr_val);
                                                }
                                            });

                                        }else  if (RMD_ATTR_DOM == attr_id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    ((TextView) findViewById(R.id.txtDOM)).setText(attr_val);
                                                    MessageProvider.displayAlert(MainActivity.this, "ScannerTest", "DOM: " + attr_val, "OK", null, null, null);

                                                }
                                            });

                                        }else  if (RMD_ATTR_CONFIG_NAME == attr_id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    ((TextView) findViewById(R.id.txtConfigName)).setText(attr_val);
                                                    MessageProvider.displayAlert(MainActivity.this, "ScannerTest", "Config name: " + attr_val, "OK", null, null, null);

                                                }
                                            });

                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG,e.toString());
                    }

                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        }


    }
}
