package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudpos.jniinterface.EMVJNIInterface;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.transaction.TransDefine;
import com.wizarpos.jni.PinPadCallbackHandler;

import net.yolopago.pago.utilities.CipherHM;

import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_offlinepin_times;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_tag_data;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_offlinepin_verified;
import static com.cloudpos.jniinterface.EMVJNIInterface.setEmvOfflinePinCallbackHandler;
import android.widget.RelativeLayout;


public class InputOfflinePINActivity extends FuncActivity implements PinPadCallbackHandler
{
	private static final String TAG = "InputOfflinePINActivity";
	private TextView textMsg  = null;
	private TextView textPin = null;
	private Button buttonBack = null;
    private Button buttonMore = null;
    private ImageView cross =null;
    LinearLayout relativeLayout;

	//char[] stars = "●●●●●●●●●●●●●●●●".toCharArray();
	char[] stars = "****************".toCharArray();

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_pin);
    	// title
		relativeLayout= findViewById(R.id.mainView);
		textMsg = (TextView)findViewById(R.id.text_msg);
		cross=(ImageView)findViewById(R.id.crossImageView);
		//textTitle.setText(appState.getString(TransDefine.transInfo[appState.getTranType()].id_display_en));

		byte[] CONTER = new byte[1];
		emv_get_tag_data(0x9F17, CONTER, 1); // IAC
		Log.d(TAG, "onActivityResult: run emvProcessCompleted ErrCode:"+appState.getErrorCode()+" EretCode:"+appState.trans.getEMVRetCode()+" EMVSta:"+appState.trans.getEMVStatus());
		Log.d(TAG, "onActivityResult: run emvProcessCompleted emv_offlinepin_verified:"+emv_offlinepin_verified());
		Log.d(TAG, "onActivityResult: run emvProcessCompleted emv_get_offlinepin_times:"+emv_get_offlinepin_times());
		Log.d(TAG, "onActivityResult: run emvProcessCompleted CONTER:"+ CipherHM.bytesToHex(CONTER));

		if(emv_offlinepin_verified()==-1){
			textMsg.setText("NIP Incorrecto");
			cross.setVisibility(View.VISIBLE);
			relativeLayout.setBackgroundResource(R.drawable.ylp_back_brush_red_1);
		}
		if(emv_get_offlinepin_times()+1==(int)CONTER[0]){
			textMsg.setText("Última oportunidad para ingresar el NIP");
		}

		textPin = (TextView) findViewById(R.id.input_pin);
		if(textPin != null) textPin.setText("");

	    buttonBack = (Button)findViewById(R.id.btn_back);
     //   buttonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));

        buttonMore = (Button)findViewById(R.id.btn_more);
       // buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));

		CountDownTimer countDownTimer = new CountDownTimer(2000, 2000) {
			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				if(textMsg.getText().equals("NIP Incorrecto")){//|| textMsg.getText().equals("Ultimá oportunidad para ingresar el PIN") ) {
					relativeLayout.setBackgroundResource(R.drawable.ylp_back_brush_green_1);
					cross.setVisibility(View.INVISIBLE);
					textMsg.setText("");
				}
				if(textMsg.getText().equals("Última oportunidad para ingresar el NIP") ) {
					relativeLayout.setBackgroundResource(R.drawable.ylp_back_brush_green_1);
					cross.setVisibility(View.INVISIBLE);
				}
			}
		}.start();
    }

	@Override
	protected void onStart()
	{
		super.onStart();
		setEmvOfflinePinCallbackHandler(InputOfflinePINActivity.this);
		EMVJNIInterface.emv_process_next();
		mHandler.setFunActivity(this);
	}

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onBackPressed(){
		Message msg = new Message();
		msg.what = EMV_PROCESS_NEXT_COMPLETED_NOTIFIER;
		mHandler.sendMessage(msg);
    }

	@Override
	public void handleMessageSafe(Message msg)
	{
		/*这里是处理信息的方法*/
		Log.d(TAG, "handleMessageSafe: "+msg.what);
		switch (msg.what)
		{
		case OFFLINE_PIN_NOTIFIER:
			if(textMsg.getText().equals("NIP Incorrecto")){//|| textMsg.getText().equals("Ultimá oportunidad para ingresar el PIN") ) {
				relativeLayout.setBackgroundResource(R.drawable.ylp_back_brush_green_1);
				cross.setVisibility(View.INVISIBLE
				);
				textMsg.setText("");
			}
			if(textMsg.getText().equals("Última oportunidad para ingresar el NIP") ) {
				relativeLayout.setBackgroundResource(R.drawable.ylp_back_brush_green_1);
			}
			textPin.setText(stars, 0, msg.arg1 & 0x0F);
			break;
		case EMV_PROCESS_NEXT_COMPLETED_NOTIFIER:
			setResult(Activity.RESULT_OK, getIntent());
			exit();
			break;
	}
	}

	public void processCallback(byte[] data) {
		processCallback(data[0], data[1]);
	}

	public void processCallback(int nCount, int nExtra)
	{
		Message msg = new Message();
		msg.what = OFFLINE_PIN_NOTIFIER;
		msg.arg1 = nCount;
		msg.arg2 = nExtra;
		mHandler.sendMessage(msg);
	}
}