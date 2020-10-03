package com.wizarpos.emvsample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.transaction.TransDefine;
import com.wizarpos.jni.PinPadCallbackHandler;
import com.wizarpos.jni.PinPadInterface;
import com.wizarpos.util.AppUtil;

public class InputPINActivity extends FuncActivity implements PinPadCallbackHandler
{
	private static final String TAG = "InputPINActivity";
	private final int PINPAD_CANCEL  = -65792;
	private final int PINPAD_TIMEOUT = -65538;
	
	private TextView textTitle  = null;
	private TextView textPin = null;
	private Button   buttonBack = null;
    private Button   buttonMore = null;
    
	private Handler mHandler;
	private Thread mReadPINThread;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		Log.d(TAG, "onCreate: onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_pin);
    	// title
        textTitle = (TextView)findViewById(R.id.tAppTitle);
		textTitle.setText(appState.getString(TransDefine.transInfo[appState.getTranType()].id_display_en));

		textPin = (TextView) findViewById(R.id.input_pin);
		if(textPin != null) textPin.setText("");

	    buttonBack = (Button)findViewById(R.id.btn_back);
        //buttonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
        
        buttonMore = (Button)findViewById(R.id.btn_more);
      //  buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
		Log.d(TAG, "onCreate: InputPINActivity");
    	mHandler = new Handler() 
		{ 
			public void handleMessage(Message msg)
			{ /*这里是处理信息的方法*/ 
				switch (msg.what)
				{ 
				case PIN_SUCCESS_NOTIFIER:
					setResult(Activity.RESULT_OK, getIntent());
					break;
				case PIN_ERROR_NOTIFIER:

					appState.setErrorCode(R.string.error_pinpad);
					break;
				case PIN_CANCELLED_NOTIFIER:
					appState.setErrorCode(R.string.error_user_cancelled);
					break;
				case PIN_TIMEOUT_NOTIFIER:
					appState.setErrorCode(R.string.error_input_timeout);
					break;
				} 
				exit();
			}
		};

    }

	@Override
	protected void onStart()
	{
		super.onStart();
		appState.theActivity = this;
		mReadPINThread=new ReadPINThread(); 
		mReadPINThread.start();
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

    }
    
    private void notifyPinSuccess()
    {
    	Message msg = new Message();
    	msg.what = PIN_SUCCESS_NOTIFIER;
    	mHandler.sendMessage(msg);
    }
    
    private void notifyPinError()
	{
    	Message msg = new Message();
    	msg.what = PIN_ERROR_NOTIFIER;
    	mHandler.sendMessage(msg);
    }
    
    private void notifyPinCancel()
    {
    	Message msg = new Message();
    	msg.what = PIN_CANCELLED_NOTIFIER;
    	mHandler.sendMessage(msg);
    }
    
    private void notifyPinTimeout()
    {
    	Message msg = new Message();
    	msg.what = PIN_TIMEOUT_NOTIFIER;
    	mHandler.sendMessage(msg);
    }

	protected char[] stars = "●●●●●●●●●●●●●●●●".toCharArray();
	public static final int PIN_AMOUNT_SHOW  = 0x10000;
	public static final int PIN_KEY_CALLBACK = 0x10001;
	private Handler commHandler = createCommHandler();

	public void processCallback(byte[] data) {
		//Log.i("processCallback", "" + data);
		if(data != null)
			commHandler.obtainMessage(PIN_KEY_CALLBACK, data[0], data[1]).sendToTarget();
	}

	@Override
	public void processCallback(int i, int i1) {
		commHandler.obtainMessage(PIN_KEY_CALLBACK, i, i1).sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	protected Handler createCommHandler()
	{	// 无 Pinpad时跳过. DuanCS@[20141001]
		return new Handler()
		{
			public void handleMessage(Message msg)
			{ /* 这里是处理信息的方法 */
				switch (msg.what)
				{
				case PIN_AMOUNT_SHOW:	// 其值已通过onFlush显示. DuanCS@[20150907]
//					setTextById(R.id.amount, msg.obj.toString());
					textPin.setText(msg.obj.toString());	// 这一行也不会执行, 因为 Pinpad.showText() 不会触发回调... DuanCS@[20150912]
					break;
				case PIN_KEY_CALLBACK:
					textPin.setText(stars, 0, msg.arg1 & 0x0F);
					break;
				}
			}
		};
	}

	class ReadPINThread extends Thread
    {
    	public void run()
    	{
			Log.d(APP_TAG, "ReadPINThread");
    		byte[] pinBlock = new byte[8];
    		byte[] zeroPAN = new byte[]{'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
    		
    		// masterKey is new byte[]{'1','1','1','1','1','1','1','1' }
			//Q1上不支持单倍长PINKEY
    		byte[] defaultPINKey = new byte[]{'2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2' };
    		
    		int ret = -1;
    		if(appState.pinpadOpened == false)
    		{
    			if(PinPadInterface.open() < 0)
    		    {
    				notifyPinError();
    				return;
    		    }
			    appState.pinpadOpened = true;
				PinPadInterface.setupCallbackHandler(InputPINActivity.this);
    		}
    		
    	    ret = PinPadInterface.updateUserKey(appState.terminalConfig.getKeyIndex(),
                                                0, 
                                                defaultPINKey, 
                                                defaultPINKey.length);
    		if(ret < 0)
    		{
    			if(debug)Log.d(APP_TAG, "pinpad open error");
    			notifyPinError();
    			PinPadInterface.close();
    			appState.pinpadOpened = false;
    			return;
    		}
			//Q1上不支持单倍长PINKEY
    		PinPadInterface.setKey(2, appState.terminalConfig.getKeyIndex(), 0, DOUBLE_KEY);
//    		PinPadInterfaceYLP.setKey(2, appState.terminalConfig.getKeyIndex(), 0, appState.terminalConfig.getKeyAlgorithm());
    		if(appState.trans.getTransAmount() > 0)
    		{
	    		byte[] text = (AppUtil.formatAmount(appState.trans.getTransAmount())).getBytes();
	    		PinPadInterface.setText(0, text, text.length, 0);
    		}
    		ret = PinPadInterface.inputPIN(zeroPAN, zeroPAN.length, pinBlock, 60000, 0);
    		if(ret < 0)
    		{
    			if(ret == PINPAD_CANCEL)
    			{
    				notifyPinCancel();
    			}
    			else if(ret == PINPAD_TIMEOUT)
    			{
    				notifyPinTimeout();
    			}
    			else{
	    			notifyPinError();
    			}
    			PinPadInterface.close();
    			appState.pinpadOpened = false;
    			return;
    		}
    		if(ret == 0)
    		{
    			appState.trans.setPinEntryMode(CANNOT_PIN);
    		}
    		else
    		{
	    		appState.trans.setPinBlock(pinBlock);
	    		appState.trans.setPinEntryMode(CAN_PIN);
    		}
    		notifyPinSuccess();
			PinPadInterface.close();
			appState.pinpadOpened = false;
		} 
    }
}