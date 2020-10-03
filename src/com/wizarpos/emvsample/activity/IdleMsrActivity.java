package com.wizarpos.emvsample.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.jni.ContactICCardReaderInterface;
import com.wizarpos.jni.ContactlessICCardReaderInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_config_checksum;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_kernel_checksum;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_kernel_id;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_process_type;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_version_string;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_kernel_initialize;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_force_online;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_kernel_attr;
import static com.cloudpos.jniinterface.EMVJNIInterface.loadEMVKernel;
import static com.cloudpos.jniinterface.EMVJNIInterface.registerFunctionListener;

public class IdleMsrActivity extends FuncActivity implements Constant {

    private String amountLong;
    private TextView Timer;
    private static final long Start_Time = 31000;
    private CountDownTimer countDownTimer;
    public static ArrayList<Product> listproductMSR;
    private boolean TimerRunning;
    private long TimeLeft = Start_Time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle_msr);

        Bundle bundle = getIntent().getExtras();
        amountLong = bundle.getString("amount");
        Serializable bundle1 = getIntent().getSerializableExtra("MSR");
        listproductMSR = (ArrayList<Product>) bundle1;

        if(bundle1 != null) {
            for (int i = 0; i < listproductMSR.size(); i++) {
                String data1 = listproductMSR.get(i).getProducto();
                String data2 = listproductMSR.get(i).getCantidad();
                Integer c = Integer.parseInt(data2);
                Double data3 = listproductMSR.get(i).getPrecio();
                //Double dou = Double.parseDouble(data3);
                //Log.i("Information IdleMSR", "Regresa : " + data1 + ", " + c + ", " + data3);
            }
        }


        Timer = (TextView)findViewById(R.id.timer);

        startTimer();

        if(debug)Log.e(APP_TAG, "idleActivity onCreate");
        if(appState.icInitFlag == false)
        {
            if(ContactICCardReaderInterface.init() >= 0)
            {
                Log.d(APP_TAG, "ContactICCardReaderInterfaceYLP.init() OK");
                appState.icInitFlag = true;
            }
            if(ContactlessICCardReaderInterface.init() >= 0)
            {
                Log.d(APP_TAG, "ContactlessICCardReaderInterfaceYLP.init() OK");
                appState.icInitFlag = true;
            }

        }
    }
    @Override
    public void handleMessageSafe(Message msg)
    {
        /*这里是处理信息的方法*/

        switch (msg.what)
        {
            case CARD_INSERT_NOTIFIER:
                if(isConnected()) {
                    Bundle bundle = msg.getData();
                    int nEventID = bundle.getInt("nEventID");
                    int nSlotIndex = bundle.getInt("nSlotIndex");
                    if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] C" );
                    if(   nSlotIndex == 0
                            && nEventID == SMART_CARD_EVENT_INSERT_CARD
                    )
                    {
                        cancelMSRThread();
                        appState.resetCardError = false;
                        appState.trans.setCardEntryMode(INSERT_ENTRY);
                        appState.needCard = false;
                        //sale();
                        cancelIdleTimer();
                        Intent intent = new Intent(this, Sale.class);
                        intent.putExtra("Sale", (ArrayList<Product>) listproductMSR);
                        intent.putExtra("fisico", "Pago con tarjeta");
                        startActivityForResult(intent, STATE_REQUEST_CARD);
                    }
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleMsrActivity.this);
                    // Configura el titulo.
                    alertDialogBuilder.setTitle("¡Sin acceso a internet!");
                    // Configura el mensaje.
                    alertDialogBuilder
                            .setMessage("¡Favor de verificar la terminal y dar click en continuar!")
                            .setCancelable(false)
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Bundle bundle = msg.getData();
                                    int nEventID = bundle.getInt("nEventID");
                                    int nSlotIndex = bundle.getInt("nSlotIndex");
                                    if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] D" );
                                    if(   nSlotIndex == 0
                                            && nEventID == SMART_CARD_EVENT_INSERT_CARD
                                    )
                                    {
                                        cancelMSRThread();
                                        appState.resetCardError = false;
                                        appState.trans.setCardEntryMode(INSERT_ENTRY);
                                        appState.needCard = false;

                                        cancelIdleTimer();
                                        Intent intent = new Intent(IdleMsrActivity.this, Sale.class);
                                        intent.putExtra("Sale", (ArrayList<Product>) listproductMSR);
                                        intent.putExtra("fisico", "Pago con tarjeta");
                                        startActivityForResult(intent, STATE_REQUEST_CARD);
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelMSRThread();
                                    cancelContactCard();
                                    requestProduct();
                                }
                            }).create().show();
                }
                break;
            case CARD_ERROR_NOTIFIER:
                cancelMSRThread();
                appState.trans.setEmvCardError(true);
                appState.resetCardError = true;
                appState.needCard = true;
                sale(listproductMSR);
                break;
        }
    }

    @Override
    protected void onStart() {
        if(debug)Log.e(APP_TAG, "idleActivity onStart");
        super.onStart();
        appState.theActivity = this;
        appState.initData();
        appState.trans.setTransAmount(Long.parseLong(amountLong.replaceAll("[\\D]", "")));

        appState.idleFlag = true;
        if(appState.emvParamLoadFlag == false)
        {
            loadEMVParam();
        }
        else{
            if(appState.emvParamChanged == true)
            {
                setEMVTermInfo();
            }
        }
//		idleLine1.setText("GOODS / SERVICE");
//		idleLine2.setText("PLEASE INSERT CARD");

        byte[] version = new byte[32];
        byte[] kernelChecksum = new byte[8];
        byte[] configChecksum = new byte[4];

        int len = emv_get_version_string(version, version.length);
//		idleLine3.setText(new String(version, 0, len));

        if(emv_get_kernel_checksum(kernelChecksum, kernelChecksum.length) > 0)
        {
//			idleLine4.setText("KC: " + StringUtil.toHexString(kernelChecksum, false));
        }
        if(emv_get_config_checksum(configChecksum, configChecksum.length) > 0)
        {
//			idleLine5.setText("CC: " + StringUtil.toHexString(configChecksum, false));
        }
        mHandler.setFunActivity(this);

        if(appState.icInitFlag != true)
        {
            appState.idleFlag = false;
            go2Error(R.string.error_init_ic);
            return;
        }
//    	waitContactCard();
        readAllCard();
    }

    @Override
    protected void onResume() {
        if(debug)Log.e(APP_TAG, "idleActivity onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        if(debug)Log.e(APP_TAG, "idleActivity onStop");
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        appState.idleFlag = false;
        cancelMSRThread();
        cancelContactCard();
        requestFuncMenu();
    }

    @Override
    protected void onBack()
    {
        onBackPressed();
    }

    @Override
    protected void onCancel()
    {
        onBackPressed();
    }

    @Override
    protected void onEnter()
    {
        onBackPressed();
    }

    public void loadEMVParam()
    {
        //lib path
        String tmpEmvLibDir = "";
        tmpEmvLibDir = appState.getDir("", 0).getAbsolutePath();
        tmpEmvLibDir = tmpEmvLibDir.substring(0, tmpEmvLibDir.lastIndexOf('/')) + "/lib/libEMVKernal.so";

        if (loadEMVKernel(tmpEmvLibDir.getBytes(),tmpEmvLibDir.getBytes().length) == 0)
        {
            emv_kernel_initialize();
            emv_set_kernel_attr(new byte[]{0x20}, 1);

            if(loadCAPK() == -2)
            {
                capkChecksumErrorDialog(IdleMsrActivity.this);
            }
            loadAID();
            loadExceptionFile();
            loadRevokedCAPK();
            setEMVTermInfo();

            emv_set_force_online(appState.terminalConfig.getforceOnline());


            appState.emvParamLoadFlag = true;
        }
    }

    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.okButton:
                    appState.idleFlag = false;
                    cancelMSRThread();
                    cancelContactCard();
                    requestFuncMenu();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(  int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,  resultCode, data);
        finish();
    }

    @Override
    public void onUserInteraction(){
        super.onUserInteraction();
       // pauseTimer();
        resetTimer();
       // startTimer();
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(TimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                TimerRunning = false;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleMsrActivity.this);
                // Configura el titulo.
                alertDialogBuilder.setTitle("Continuar Pago");

                // Configura el mensaje.
                alertDialogBuilder
                        .setMessage("¿Desea continuar con el pago?")
                        .setCancelable(false)
                        .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                pauseTimer();
                                resetTimer();
                                startTimer();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                cancelMSRThread();
                                cancelContactCard();
                                exit();//requestProduct();
                            }
                        }).create().show();
            }
        }.start();
        TimerRunning = true;
    }

    private void resetTimer(){
        TimeLeft = Start_Time;
        updateCountDownText();
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        TimerRunning = false;
    }

    private void updateCountDownText(){
        int minutes = (int) TimeLeft / 1000 / 60;
        int seconds = (int) TimeLeft / 1000 % 60;

        String timeLastFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        Timer.setText(timeLastFormatted);
    }


    public boolean isConnected(){
        boolean connected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)IdleMsrActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        }catch (Exception e){
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}
