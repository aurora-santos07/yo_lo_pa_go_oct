package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import com.cloudpos.jniinterface.IFuntionListener;
import androidx.appcompat.app.AppCompatActivity;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.transaction.TransDefine;
import com.wizarpos.jni.MsrInterface;
import com.wizarpos.jni.PinPadInterface;
import com.wizarpos.util.NumberUtil;
import com.wizarpos.util.StringUtil;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.utilities.CipherHM;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.cloudpos.jniinterface.EMVJNIInterface.close_reader;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_aidparam_add;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_aidparam_clear;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_capkparam_add;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_capkparam_clear;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_exception_file_add;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_exception_file_clear;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_preprocess_qpboc;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_revoked_cert_add;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_revoked_cert_clear;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_anti_shake;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_kernel_type;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_trans_amount;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_terminal_param_set_tlv;
import static com.cloudpos.jniinterface.EMVJNIInterface.get_card_type;
import static com.cloudpos.jniinterface.EMVJNIInterface.open_reader;

public class FuncActivity extends AppCompatActivity implements Constant, IFuntionListener
{
	private static final String TAG = "FuncActivity";

	protected static WeakReferenceHandler mHandler = new WeakReferenceHandler(null);
    protected static Socket socket = null;
    protected static FuncActivity funcRef;
    protected static MainApp appState = null;
    
    protected static Thread msrThread = null;
    protected static boolean msrThreadActived = false;
    protected static boolean readMSRCard = false;
    protected static boolean msrClosed = true;
    
    protected static boolean contactOpened = false;
    protected static boolean contactlessOpened = false;
    
    protected static Thread mOpenPinpadThread = null;
    
	private Timer mTimerSeconds;
    private int mIntIdleSeconds;
    private boolean mBoolInitialized=false;
	private byte mTimerMode = 0;
    private int idleTimer = DEFAULT_IDLE_TIME_SECONDS;

	public void handleMessageSafe(Message msg){}

	protected static class WeakReferenceHandler extends Handler{

	    private WeakReference<FuncActivity> mActivity;
	    public WeakReferenceHandler(FuncActivity activity){
	        mActivity = new WeakReference<FuncActivity>(activity);
        }

        public void setFunActivity(FuncActivity activity){
            mActivity = new WeakReference<FuncActivity>(activity);
        }
		@Override
		public void handleMessage(Message msg) {
			FuncActivity activity = mActivity.get();
			if(activity != null){
				activity.handleMessageSafe(msg);
			}
		}
	}

	public void capkChecksumErrorDialog(Context context) 
	{
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("CAPK:" + appState.failedCAPKInfo + "\nChecksum Error");

		builder.setPositiveButton("确认", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

 	@Override
    public void emvProcessCallback(byte[] data)
	{
		if(debug)Log.d(APP_TAG, "emvProcessNextCompleted HM"+ CipherHM.bytesToHex(data));
		appState.trans.setEMVStatus(data[0]);
		appState.trans.setEMVRetCode(data[1]);
		
     	Message msg = new Message();
     	msg.what = EMV_PROCESS_NEXT_COMPLETED_NOTIFIER;
     	mHandler.sendMessage(msg);
	}
    
	@Override
    public void cardEventOccured(int eventType)
	{

 		if(debug)Log.d(APP_TAG, "get cardEventOccured:"+eventType);
 		Message msg = new Message();
 		if(eventType == SMART_CARD_EVENT_INSERT_CARD)
 		{
 			appState.cardType = get_card_type();
 			if(debug)Log.d(APP_TAG, "cardType = " + appState.cardType);
 			
 			if(appState.cardType == CARD_CONTACT)
 			{
 				msg.what = CARD_INSERT_NOTIFIER;
 				mHandler.sendMessage(msg);
 			}
 			else if(appState.cardType == CARD_CONTACTLESS)
 			{
 				msg.what = CARD_TAPED_NOTIFIER;
 				mHandler.sendMessage(msg);
 			}
 			else{
 				appState.cardType = -1;
 			}
 		}
 		else if(eventType == SMART_CARD_EVENT_POWERON_ERROR)
 		{
 			appState.cardType = -1;
 			msg.what = CARD_ERROR_NOTIFIER;
			mHandler.sendMessage(msg);
 		}
 		else if(eventType == SMART_CARD_EVENT_REMOVE_CARD)
 		{
 			appState.cardType = -1;
 		}
 		else if(eventType == SMART_CARD_EVENT_CONTALESS_HAVE_MORE_CARD)
 		{
 			appState.cardType = -1;
 			msg.what = CONTACTLESS_HAVE_MORE_CARD_NOTIFIER;
			mHandler.sendMessage(msg);
 		}
 		else if(eventType == SMART_CARD_EVENT_CONTALESS_ANTI_SHAKE){
 		    msg.what = CARD_CONTACTLESS_ANTISHAKE;
 		    mHandler.sendMessage(msg);
        }

	}
	
    public void setEMVTermInfo()
    {
		byte[] termInfo = new byte[256];
		int offset = 0;
		// 5F2A: Transaction Currency Code
		termInfo[offset] = (byte)0x5F;
		termInfo[offset+1] = 0x2A;
		termInfo[offset+2] = 2;
		offset += 3;
		System.arraycopy(StringUtil.hexString2bytes(appState.terminalConfig.getCurrencyCode()),
			0, termInfo, offset, 2);
		offset += 2;
		// 5F36: Transaction Currency Exponent
		termInfo[offset] = (byte)0x5F;
		termInfo[offset+1] = 0x36;
		termInfo[offset+2] = 1;
		termInfo[offset+3] = appState.terminalConfig.getCurrencyExponent();
		offset += 4;
		// 9F16: Merchant Identification
		if (appState.terminalConfig.getMID().length() == 15) {
			termInfo[offset] = (byte)0x9F;
			termInfo[offset+1] = 0x16;
			termInfo[offset+2] = 15;
			offset += 3;
			System.arraycopy(appState.terminalConfig.getMID().getBytes(), 0, termInfo, offset, 15);
			offset += 15;
		}
		// 9F1A: Terminal Country Code
		termInfo[offset] = (byte)0x9F;
		termInfo[offset+1] = 0x1A;
		termInfo[offset+2] = 2;
		offset += 3;
		System.arraycopy(StringUtil.hexString2bytes(appState.terminalConfig.getCountryCode()),
			0, termInfo, offset, 2);
		offset += 2;
		// 9F1C: Terminal Identification
		if (appState.terminalConfig.getTID().length() == 8) {
			termInfo[offset] = (byte)0x9F;
			termInfo[offset+1] = 0x1C;
			termInfo[offset+2] = 8;
			offset += 3;
			System.arraycopy(appState.terminalConfig.getTID().getBytes(), 0, termInfo, offset, 8);
			offset += 8;
		}
		// 9F1E: IFD Serial Number
		String ifd = Build.SERIAL;
		if(ifd.length() > 0)
		{
			//Log.d("(TAG, "9F1E: IFD Serial Number setEMVTermInfo: "+ifd);
			termInfo[offset] = (byte) 0x9F;
			termInfo[offset + 1] = 0x1E;
			termInfo[offset + 2] = (byte) ifd.length();
			offset += 3;
			System.arraycopy(ifd.getBytes(), 0, termInfo, offset, ifd.length());
			offset += ifd.length();

		}
		// 9F33: Terminal Capabilities
		termInfo[offset] = (byte)0x9F;
		termInfo[offset+1] = 0x33;
		termInfo[offset+2] = 3;
		offset += 3;
		System.arraycopy(StringUtil.hexString2bytes(appState.terminalConfig.getTerminalCapabilities()),
			0, termInfo, offset, 3);
		offset += 3;
		// 9F35: Terminal Type
		termInfo[offset] = (byte)0x9F;
		termInfo[offset+1] = 0x35;
		termInfo[offset+2] = 1;
		termInfo[offset+3] = StringUtil.hexString2bytes(appState.terminalConfig.getTerminalType())[0];
		offset += 4;
		// 9F40: Additional Terminal Capabilities
		termInfo[offset] = (byte)0x9F;
		termInfo[offset+1] = 0x40;
		termInfo[offset+2] = 5;
		offset += 3;
		System.arraycopy(StringUtil.hexString2bytes(appState.terminalConfig.getAdditionalTerminalCapabilities()),
			0, termInfo, offset, 5);
		offset += 5;
		// 9F4E: Merchant Name and Location
		int merNameLength = appState.terminalConfig.getMerchantName1().length();
		if (merNameLength > 0) {
			termInfo[offset] = (byte)0x9F;
			termInfo[offset+1] = 0x4E;
			termInfo[offset+2] = (byte)merNameLength;
			offset += 3;
			System.arraycopy(appState.terminalConfig.getMerchantName1().getBytes(), 0, termInfo, offset, merNameLength);
			offset += merNameLength;
		}
		// 9F66: TTQ first byte
		termInfo[offset] = (byte)0x9F;
		termInfo[offset+1] = 0x66;
		termInfo[offset+2] = 1;
		termInfo[offset+3] = appState.terminalConfig.getTTQ();
		offset += 4;

		/*// DF13: TAC-DENAIL

		termInfo[offset] = (byte)0xDF;
		termInfo[offset+1] = 0x13;
		termInfo[offset+2] = 1;
		termInfo[offset+3] = (byte)0xff;
		offset += 4;*/

		// DF19: Contactless floor limit
		if(appState.terminalConfig.getContactlessFloorLimit() >= 0)
		{
			termInfo[offset] = (byte)0xDF;
			termInfo[offset+1] = 0x19;
			termInfo[offset+2] = 6;
			offset += 3;
			System.arraycopy(NumberUtil.intToBcd(appState.terminalConfig.getContactlessFloorLimit(), 6),
				0, termInfo, offset, 6);
			offset += 6;
		}
		// DF20: Contactless transaction limit
		//appState.terminalConfig.setContactlessLimit(40000);
		if(appState.terminalConfig.getContactlessLimit() >= 0)
		{
			termInfo[offset] = (byte)0xDF;
			termInfo[offset+1] = 0x20;
			termInfo[offset+2] = 6;
			offset += 3;
			System.arraycopy(NumberUtil.intToBcd(appState.terminalConfig.getContactlessLimit(), 6),
				0, termInfo, offset, 6);
			offset += 6;
		}
		// DF21: CVM limit
		//appState.terminalConfig.setCvmLimit(300);
		if(appState.terminalConfig.getCvmLimit() >= 0)
		{
			termInfo[offset] = (byte)0xDF;
			termInfo[offset+1] = 0x21;
			termInfo[offset+2] = 6;
			offset += 3;
			System.arraycopy(NumberUtil.intToBcd(appState.terminalConfig.getCvmLimit(), 6),
				0, termInfo, offset, 6);
			offset += 6;
		}
		// EF01: Status check support
		termInfo[offset] = (byte)0xEF;
		termInfo[offset+1] = 0x01;
		termInfo[offset+2] = 1;
		termInfo[offset+3] = appState.terminalConfig.getStatusCheckSupport();
		offset += 4;

		emv_terminal_param_set_tlv(termInfo, offset);
    }
    
	void setEMVTransAmount(String strAmt)
	{
		byte[] amt = new byte[strAmt.length() + 1];
		System.arraycopy(strAmt.getBytes(), 0, amt, 0, strAmt.length());
		emv_set_trans_amount(amt);
	}
	

    public static boolean loadAID()
    {
    	appState.aids = appState.aidService.query();
    	emv_aidparam_clear();
    	byte[] aidInfo = null;
    	for(int i=0; i< appState.aids.length; i++)
    	{
			if(appState.aids[i] != null)
			{
	    		aidInfo = appState.aids[i].getDataBuffer();
				if(emv_aidparam_add(aidInfo, aidInfo.length) < 0)
					return false;
			}
			else
			{
				break;
			}
    	}
		return true;
    }
    
    public static int loadCAPK()
    {
    	appState.capks = appState.capkService.query();
    	emv_capkparam_clear();
    	byte[] capkInfo = null;
    	for(int i=0; i< appState.capks.length; i++)
    	{
			if(appState.capks[i] != null)
			{
	    		capkInfo = appState.capks[i].getDataBuffer();
				int ret = emv_capkparam_add(capkInfo, capkInfo.length);
	    		if( ret < 0)
				{
					appState.failedCAPKInfo = appState.capks[i].getRID() + "_" + appState.capks[i].getCapki();
	    			return ret;
				}
			}
			else
			{
				break;
			}
    	}
		return 0;
    }
    
    public static boolean loadExceptionFile()
    {
    	appState.exceptionFiles = appState.exceptionFileService.query();
    	emv_exception_file_clear();
    	byte[] exceptionFileInfo = null;
    	for(int i=0; i< appState.exceptionFiles.length; i++)
    	{
			if(appState.exceptionFiles[i] != null)
			{
				exceptionFileInfo = appState.exceptionFiles[i].getDataBuffer();
				if(emv_exception_file_add(exceptionFileInfo) < 0)
					return false;
			}
			else
			{
				break;
			}
    	}
		return true;
    }
    
    public static boolean loadRevokedCAPK()
    {
    	appState.revokedCapks = appState.revokedCAPKService.query();
    	emv_revoked_cert_clear();
    	byte[] revokedCAPKInfo = null;
    	for(int i=0; i< appState.revokedCapks.length; i++)
    	{
			if(appState.revokedCapks[i] != null)
			{
				revokedCAPKInfo = appState.revokedCapks[i].getDataBuffer();
				if(revokedCAPKInfo != null)
				{
					if(emv_revoked_cert_add(revokedCAPKInfo) < 0)
						return false;
				}
			}
			else
			{
				break;
			}
    	}
		return true;
    }
    
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        appState = ((MainApp)getApplicationContext());
    }

	@Override
	protected void onStart()
	{
		if(debug)Log.d(APP_TAG, "FuncActivity onStart");
		super.onStart();
		appState.theActivity = this;
	}

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    public void onTouch()
    {
        //if(debug)Log.d(APP_TAG, "mIntIdleSeconds = 0");
    	mIntIdleSeconds=0;
    }

    public void cancelIdleTimer()
    {
    	mIntIdleSeconds=0;
    	if (mTimerSeconds != null)
        {
            if(debug)Log.d(APP_TAG, "timer cancelled");
        	mTimerSeconds.cancel();
        }
    }
    
    public void startIdleTimer(byte timerMode, int timerSecond)
    {
    	idleTimer = timerSecond;
    	mTimerMode = timerMode;
        //initialize idle counter
        mIntIdleSeconds=0;
    	if (mBoolInitialized == false)
        {
        	if(debug)Log.d(APP_TAG, "timer start");
    		mBoolInitialized = true;
            //create timer to tick every second
            mTimerSeconds = new Timer();
            mTimerSeconds.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    mIntIdleSeconds++;
                    if (mIntIdleSeconds == idleTimer)
                    {
                    	if(mTimerMode == TIMER_IDLE)
                    	{
                    		go2Idle();
                    	}
                    	else
                    	{
                    		if(appState.needClearPinpad == true)
             		        {
             		        	// clear pinpad
             		        	appState.needClearPinpad = false;
                 	    		PinPadInterface.setText(0, null, 0, 0);
                 	    		PinPadInterface.setText(1, null, 0, 0);
             		        }
            			    
                    		setResult(Activity.RESULT_CANCELED, getIntent());
	                    	exit();
                    	}
                    }
                }
            }, 0, 1000);
        }
    }

    protected boolean connectSocket(String ip, int port, int timeout)
    {
	    try {
	    	socket = new Socket(); 
	    	socket.setSoTimeout(timeout); // 设置读超时
	    	SocketAddress remoteAddr = new InetSocketAddress(ip, port); 
	    	if(debug)
	    	{
	    		//Log.d("(APP_TAG, "Connect IP[" + ip + "]port[" + port + "]");
	    	}
	    	socket.connect(remoteAddr, timeout);
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		}
	    if(socket!= null && socket.isConnected())
	    {
	    	return true;
	    }
	    return false;
    }
    
    protected void disconnectSocket()
    {
		if(debug)Log.d(APP_TAG, "disconnectSocket");
    	try {
            if(socket != null)
            {
            	socket.close();
            }
        } catch (IOException e) {

        }
    }
    

	protected synchronized void readAllCard()
	{
		if(appState.acceptMSR)
		{
			startMSRThread();
		}

		if(appState.acceptContactCard)
		{
			contactOpened = true;
			open_reader(1);
		}
		if(appState.acceptContactlessCard)
		{
			contactlessOpened = true;
            emv_set_anti_shake(0);
            appState.msrPollResult = -1;
            open_reader(2);
		}
	}

    protected void waitContactCard()
    {
    	contactOpened = true;
    	open_reader(1);
    }
    
    protected void cancelAllCard()
    {
   		cancelMSRThread();
    	cancelContactCard();
    	cancelContactlessCard();
    }
    
    protected void cancelContactCard()
    {
    	if(debug)Log.d(APP_TAG, "Close contact card");
    	if(contactOpened)
    	{
    		contactOpened = false;
    		close_reader(1);
    	}
    }
    
    
    protected void cancelContactlessCard()
    {
    	if(contactlessOpened)
    	{
			if(appState.cardType == CARD_CONTACTLESS) {
				MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.nfc_failure);
				mediaPlayer.start();
			}
    		contactlessOpened = false;
    		close_reader(2);
    	}
    }
    
    private void notifyContactlessCardOpenError()
    {
    	Message msg = new Message();
    	msg.what = CARD_OPEN_ERROR_NOTIFIER;
    	mHandler.sendMessage(msg);
    }

	protected void startMSRThread()
	{
		if(   readMSRCard == false
			&& appState.msrError == false
			)
		{
			while(msrThreadActived){

			}
			msrThread=new MSRThread();
			msrThread.start();
		}
	}

	protected void cancelMSRThread()
	{
		if(readMSRCard == true)
		{
			readMSRCard = false;
		}
	}

	protected void notifyMSR()
	{
		Message msg = new Message();
		msg.what = MSR_READ_DATA_NOTIFIER;
		mHandler.sendMessage(msg);
	}

	protected void notifyMsrOpenError()
	{
		Message msg = new Message();
		msg.what = MSR_OPEN_ERROR_NOTIFIER;
		mHandler.sendMessage(msg);
	}

	protected void notifyMsrReadError()
	{
		Message msg = new Message();
		msg.what = MSR_READ_ERROR_NOTIFIER;
		mHandler.sendMessage(msg);
	}

	protected boolean read_track2_data()
	{
		if(debug)Log.d(APP_TAG, "read_track2_data");
		int trackDatalength;
		byte[] byteArry = new byte[255];
		trackDatalength = MsrInterface.getTrackData(1, byteArry, byteArry.length);  // nTrackIndex: 0-Track1; 1-track2; 2-track3
		Log.d(APP_TAG, "Ver variable: " + trackDatalength);
		if(debug)
		{
			String strDebug = "";
			for(int i=0; i<trackDatalength; i++)
				strDebug += String.format("%02X ", byteArry[i]);
			Log.d(APP_TAG, "track2 Data: " + strDebug);
		}
		if(trackDatalength > 0)
		{
			if(   trackDatalength > 37
				|| trackDatalength < 21
				)
			{
				return false;
			}

			int panStart = -1;
			int panEnd = -1;
			for (int i = 0; i < trackDatalength; i++)
			{
				if (byteArry[i] >= (byte) '0' && byteArry[i] <= (byte) '9')
				{
					if( panStart == -1)
					{
						panStart = i;
					}
				}
				else if (byteArry[i] == (byte) '=')
				{
					/* Field separator */
					panEnd = i;
					break;
				}
				else
				{
					panStart = -1;
					panEnd = -1;
					break;
				}
			}
			if (panEnd == -1 || panStart == -1)
			{
				return false;
			}
			appState.trans.setPAN(new String(byteArry, panStart, panEnd - panStart));
			appState.trans.setExpiry(new String(byteArry, panEnd + 1, 4));
			appState.trans.setServiceCode(new String(byteArry, panEnd + 5, 3));
			appState.trans.setTrack2Data(byteArry, 0, trackDatalength);
			Log.i(APP_TAG, "error variable: " + trackDatalength);
			appState.trans.setCardEntryMode(SWIPE_ENTRY);
			return true;
		}
		return false;
	}

	protected void read_track3_data()
	{
		if(debug)Log.d(APP_TAG, "read_track3_data");
		int trackDatalength;
		byte[] byteArry = new byte[255];
		trackDatalength = MsrInterface.getTrackData(2, byteArry, byteArry.length);  // nTrackIndex: 0-Track1; 1-track2; 2-track3
		if(debug)
		{
			String strDebug = "";
			for(int i=0; i<trackDatalength; i++)
				strDebug += String.format("%02X ", byteArry[i]);
			Log.d(APP_TAG, "track3 Data: " + strDebug);
		}
		if(trackDatalength > 0)
		{
			appState.trans.setTrack3Data(byteArry, 0, trackDatalength);
		}
	}

	class MSRThread extends Thread
	{
		public void run()
		{
			super.run();
			msrThreadActived = true;
			readMSRCard = false;
			if(msrClosed == true)
			{
				if(MsrInterface.open() >= 0)
				{
					msrClosed = false;
				}
			}
			if(msrClosed == false)
			{
				readMSRCard = true;
				do{
					int nReturn = -1;
					nReturn = MsrInterface.poll(500);
					appState.msrPollResult = nReturn;
					if(debug)Log.d(APP_TAG, "MsrInterface.poll, " + nReturn);
					if(readMSRCard == false)
					{
						MsrInterface.close();
						msrClosed = true;
						if(debug)Log.d(APP_TAG, "MsrInterface.close");
					}
					else if(nReturn >= 0)
					{
						if(read_track2_data())
						{
							read_track3_data();
							MsrInterface.close();
							readMSRCard = false;
							msrClosed = true;
							notifyMSR();
						}
						else
						{
							MsrInterface.close();
							msrClosed = true;
							readMSRCard = false;
							notifyMsrReadError();
						}
					}
				}while(readMSRCard == true);
			}
			else
			{
				notifyMsrOpenError();
			}
			if(debug)Log.d(APP_TAG, "MSRThread.exit");
			msrThreadActived = false;
		}
	}


	protected void offlineSuccess()
    {
    		transSuccess();
    }

	public void saveTrans()
	{
		if(debug)Log.d(APP_TAG, "save trans");
		appState.transDetailService.save(appState.trans);
	}
	
	public void saveAdvice()
	{
		if(debug)Log.d(APP_TAG, "save advice");
		appState.adviceService.save(appState.trans);
	}
	
	public void clearTrans()
	{
		appState.transDetailService.clearTable();
	}
	
	public void clearAdvice()
	{
		appState.adviceService.clearTable();		
	}
	
	public void transSuccess()
	{
		if(appState.getTranType() != TRAN_SETTLE)
		{
		  	if ((TransDefine.transInfo[appState.getTranType()].flag & T_NOCAPTURE) == 0)
		  	{
	  			saveTrans();
	  			appState.batchInfo.incSale(Long.valueOf(appState.trans.getTransAmount()));
		  	}
		}
	}
	
    public void exit()
    {
    	cancelIdleTimer();
    	finish();
    }
    
	public void exitTrans()
	{
		cancelContactlessCard();
		cancelMSRThread();
		if(appState.cardType == CARD_CONTACT)
		{
			Log.d(TAG, "exitTrans: removeCard");
			removeCard();

		}
		else
		{
			Log.d(TAG, "exitTrans: finish");
			appState.initData();
			finish();
		}
	}
	
	// ilde
	public void go2Idle()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, IdleActivity.class);
		startActivity(intent);
	}
	
	public void go2Error(int errorCode)
	{
		cancelIdleTimer();
		appState.setErrorCode(errorCode);
		Intent intent = new Intent(this, ErrorActivity.class);
		startActivity(intent);
	}
	
	// menu
	public void requestFuncMenu()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void requestDataClear()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, DataClearActivity.class);
		startActivity(intent);
	}

	public void requestEnquireTrans()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, EnquireTransActivity.class);
		startActivity(intent);
	}

	public void showLastPBOC()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ShowLastPBOCActivity.class);
		startActivity(intent);
	}
	
	// trans flow For Result
	public void requestCard(boolean acceptMSR, boolean acceptContact, boolean acceptContactless)
	{
		cancelIdleTimer();
		appState.setState(STATE_REQUEST_CARD);
		if(appState.msrError == false)
		{
			appState.acceptMSR = acceptMSR;
		}
		else{
			appState.acceptMSR = false;
		}
		appState.acceptContactCard = acceptContact;
		appState.acceptContactlessCard = acceptContactless;
		Intent intent = new Intent(this, RequestCardActivity.class);
		startActivityForResult(intent, appState.getState());
	}
	
	public void removeCard()
	{
		cancelIdleTimer();
		appState.setState(STATE_REMOVE_CARD);
		Intent intent = new Intent(this, RemoveCardActivity.class);
		startActivityForResult(intent, appState.getState());
	}
	
//	public void requestManualCard(boolean acceptMSR, boolean acceptContact, boolean acceptContactless)
//	{
//		cancelIdleTimer();
//		appState.setState(STATE_REQUEST_CARD);
//		appState.acceptMSR = acceptMSR;
//		appState.acceptContactCard = acceptContact;
//		appState.acceptContactlessCard = acceptContactless;
//		Intent intent = new Intent(this, RequestManualCardActivity.class);
//		startActivityForResult(intent, appState.getState());
//	}
	
	public void confirmBypassPin()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ConfirmBypassPinActivity.class);
		startActivityForResult(intent, STATE_CONFIRM_BYPASS_PIN);
	}
	
	public void confirmCard()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ConfirmCardActivity.class);
		startActivityForResult(intent, STATE_CONFIRM_CARD);
	}
	
	public void inputAmount()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, InputAmountActivity.class);
		startActivityForResult(intent, STATE_INPUT_AMOUNT);
		//onActivityResult(STATE_INPUT_AMOUNT, Activity.RESULT_OK, null);
	}
	
	public void inputPIN()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, InputOfflinePINActivity.class);
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityForResult(intent, STATE_INPUT_OFFLINE_PIN);
	}
	public void processOnline()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ProcessOnlineActivity.class);
		startActivityForResult(intent, STATE_PROCESS_ONLINE);
	}
	
	public void selectEMVAppList()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, SelectEMVAppListActivity.class);
		startActivityForResult(intent, STATE_SELECT_EMV_APP);
	}
	
	public void showPBOCCardRecord()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ShowPBOCCardRecordActivity.class);
		startActivityForResult(intent, STATE_SHOW_EMV_CARD_TRANS);
	}
	
	public void showTransInfo()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, ShowTransInfoActivity.class);
		startActivityForResult(intent, STATE_SHOW_TRANS_INFO);
	}

	public void processEMVCard(byte kernelType,ArrayList<Product> listProductos)
	{
		Log.d(TAG, "processEMVCard: ");
		appState.trans.setEMVKernelType(kernelType);
		Intent intent = new Intent(this, ProcessEMVCardActivity.class);
		intent.putExtra("EMV", listProductos);
		startActivityForResult(intent, STATE_PROCESS_EMV_CARD);
	}
	
	public void showTransResult()
	{
		Log.d(TAG, "showTransResult: ");
		cancelIdleTimer();
		Intent intent = new Intent(this, TransResultActivity.class);
		startActivityForResult(intent, STATE_TRANS_END);
	}
	public void showFirma() {
		Log.d(TAG, "showFirma: ");
		Intent intent = new Intent(this, FirmaActivity.class);
		intent.putExtra("ResultCompra", "Hecho");
		startActivityForResult(intent, ConstantYLP.STATE_NEED_SING);
	}

	public void requestProduct()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	// Trans Object
	public void sale(ArrayList<Product> listProductos)
	{
		Log.d(TAG, "sale: ");
		cancelIdleTimer();
		Intent intent = new Intent(this, Sale.class);
		intent.putExtra("Sale", listProductos);
		startActivityForResult(intent, STATE_REQUEST_CARD);
	}
	
	public void settle()
	{
		cancelIdleTimer();
		Intent intent = new Intent(this, Settle.class);
		startActivity(intent);
	}
	
	public void queryCardRecord(byte recordType)
	{
		appState.recordType = recordType;
		cancelIdleTimer();
		Intent intent = new Intent(this, QueryCardRecord.class);
		startActivity(intent);
	}
	//=============== Q1 keyboard =============
	protected void onEnter()
	{
	}

	protected void onCancel()
	{
	}

	protected void onBack()
	{
	}

	protected void onDel()
	{
	}

	protected void onKeyCode(char key)
	{}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(debug) Log.i("FuncActivity", "onKeyDown:"+keyCode);
		onTouch();
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			onBack();
			break;
		case KeyEvent.KEYCODE_ESCAPE:
			onCancel();
			break;
		case KeyEvent.KEYCODE_DEL:
			onDel();
			break;
		case KeyEvent.KEYCODE_ENTER:
			onEnter();
			break;
		case 232://'.'
			onKeyCode('.');
			break;
		default:
			onKeyCode((char) ('0'+(keyCode-KeyEvent.KEYCODE_0)));
			break;
		}
		return true;
	}
	//=============== Q1 keyboard =============

	protected boolean preProcessQpboc()
	{
		//pre-process
		int res = emv_preprocess_qpboc();
		if(res < 0)
		{
			if(res == -2)
			{
				appState.setErrorCode(R.string.error_amount_zero);
			}
			else
			{
				appState.setErrorCode(R.string.error_amount_over_limit);
			}
			emv_set_kernel_type(CONTACT_EMV_KERNAL);
			return false;
		}
		return true;
	}
}
