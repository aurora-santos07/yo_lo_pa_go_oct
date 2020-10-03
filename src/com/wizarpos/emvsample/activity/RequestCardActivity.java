package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_anti_shake_finish;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.transaction.TransDefine;
import com.wizarpos.util.AppUtil;

public class RequestCardActivity extends FuncActivity
{
    private static final String TAG = "RequestCardActivity";
	private TextView textTitle  = null;
	private Button   buttonBack = null;
    private Button   buttonMore = null;
    
	private TextView txtTransType = null;
	private TextView txtPrompt = null;
	private TextView txtError = null;
	private TextView txtAmount = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_request_card);
		
	    //buttonBack = (Button)findViewById(R.id.btn_back);
        //buttonBack.setOnClickListener(new ClickListener());
        
        //buttonMore = (Button)findViewById(R.id.btn_more);
        //buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
        
        txtTransType = (TextView)findViewById(R.id.tRequestCard_TransType);
        txtError = (TextView)findViewById(R.id.tRequestCard_Error);
        txtAmount = (TextView)findViewById(R.id.tRequestCard_Amount);
        txtPrompt = (TextView)findViewById(R.id.tRequestCard_Prompt);

		//Log.d("(TAG, "onCreate:RequestCardActivity ");
        
        if(appState.resetCardError == true)
        {
			AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(RequestCardActivity.this);
			// Configura el titulo.
			alertDialogBuilder1.setTitle("¡Error!");
			// Configura el mensaje.
			alertDialogBuilder1
					.setMessage("¡Error al leer chip o tarjeta, favor de revisar!")
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							cancelIdleTimer();
							cancelMSRThread();
							Intent intent = new Intent(RequestCardActivity.this, TransResultActivity.class);
							intent.putExtra("ErrorIn", "ErrorInsertar");
							startActivity(intent);
						}
					}).create().show();
        }
        else if(appState.trans.getEmvCardError() == true)
        {
        	txtError.setText("TRANS HALTED");
        	if(appState.trans.getTransAmount() > 0)
        	{
        		txtAmount.setText("AMOUNT: " + AppUtil.formatAmount(appState.trans.getTransAmount()));
        	}
        }

    }
    @Override
	public void handleMessageSafe(Message msg)
	{
		/*这里是处理信息的方法*/
		switch (msg.what)
		{
		case MSR_READ_DATA_NOTIFIER:
			if(   appState.trans.getServiceCode().length() > 0
				&& (   appState.trans.getServiceCode().getBytes()[0] == '2'
				|| appState.trans.getServiceCode().getBytes()[0] == '6'
			)
				)
			{
				if(appState.trans.getEmvCardError() == false)
				{
					startMSRThread();
					appState.promptCardIC = true;
					Toast.makeText(this, "Please Insert/Tap Card", Toast.LENGTH_SHORT).show();
				}
				else{
					cancelAllCard();
					setResult(Activity.RESULT_OK, getIntent());
					finish();
				}
			}
			else{
				if(   appState.trans.getServiceCode().length() > 0
					&& appState.trans.getServiceCode().getBytes()[0] == '1'
					)
				{
					appState.trans.setEmvCardError(false);
					appState.trans.setPanViaMSR(true);
				}
				else{
					appState.trans.setEmvCardError(false);
					appState.trans.setPanViaMSR(false);
				}
				cancelAllCard();
				setResult(Activity.RESULT_OK, getIntent());
				finish();
			}
			break;
		case MSR_OPEN_ERROR_NOTIFIER:
			appState.msrError = true;
			appState.acceptMSR = false;
			txtPrompt.setText(appState.getString(R.string.insert_card));
			break;
		case MSR_READ_ERROR_NOTIFIER:
			readAllCard();
			break;
		case CARD_INSERT_NOTIFIER:
			Bundle bundle = msg.getData();
			int nEventID = bundle.getInt("nEventID");
			int nSlotIndex = bundle.getInt("nSlotIndex");
			if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] E" );
			if(   nSlotIndex == 0
				&& nEventID == SMART_CARD_EVENT_INSERT_CARD
				)
			{
				appState.trans.setEmvCardError(false);
				if(appState.acceptContactlessCard == true)
				{
					cancelContactlessCard();
				}
				appState.trans.setCardEntryMode(INSERT_ENTRY);
				setResult(Activity.RESULT_OK, getIntent());
				exit();
			}
			break;
		case CARD_TAPED_NOTIFIER:
			bundle = msg.getData();
			nEventID = bundle.getInt("nEventID");
			if(nEventID == SMART_CARD_EVENT_INSERT_CARD)
			{
				cancelContactCard();
				cancelMSRThread();
				appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);
				if(debug)Log.d(APP_TAG, "get CONTACTLESS_CARD_EVENT_NOTIFIER" );
				setResult(Activity.RESULT_OK, getIntent());
				exit();
			}
			break;
		case CONTACTLESS_HAVE_MORE_CARD_NOTIFIER:
			if(debug)Log.d(APP_TAG, "error, have more card" );
			appState.setErrorCode(R.string.error_more_card);
			setResult(Activity.RESULT_OK, getIntent());
			exit();
			break;
		case CARD_ERROR_NOTIFIER:
			//txtError.setText("IC POWERON ERROR");
			//txtPrompt.setText("PLEASE INSERT CARD");
			appState.trans.setEmvCardError(true);
			AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(RequestCardActivity.this);
			// Configura el titulo.
			alertDialogBuilder1.setTitle("¡Error!");
			// Configura el mensaje.
			alertDialogBuilder1
					.setMessage("¡Tarjeta se introdujo de forma incorrecta!")
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							finish();
						}
					}).create().show();
			break;
		case CARD_CONTACTLESS_ANTISHAKE:
			new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						Thread.sleep(400);
					} catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
					Log.i(TAG, "anti shake finish");
					if(appState.msrPollResult == -1)
					{
						emv_anti_shake_finish(0);
					}
					else
					{
						emv_anti_shake_finish(1);
					}
				}
			}).start();
			break;
		}
	}

	@Override
    protected void onStart() { 
        super.onStart();
		appState.theActivity = this;
        mHandler.setFunActivity(this);

        txtTransType.setText(TransDefine.transInfo[appState.getTranType()].id_display_en);
   		setPrompt();
        readAllCard();
        super.startIdleTimer(TIMER_FINISH, DEFAULT_IDLE_TIME_SECONDS);
	}
    
    private void setPrompt()
    {
        txtPrompt.setText("PLEASE USE YOUR CARD");
    }
	
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    @Override
    protected void onStop()
    {
    	//cancelAllCard();
    	super.onStop();
    }
    
    @Override
    public void onBackPressed()
    {
    	cancelAllCard();
    	setResult(Activity.RESULT_CANCELED, getIntent());
    	finish();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode,  resultCode, data);
		switch(requestCode)
		{
		case STATE_REQUEST_CARD_ERROR:
			if(resultCode == Activity.RESULT_OK)
			{
				setResult(Activity.RESULT_OK, getIntent());
			}
			else
			{
				setResult(Activity.RESULT_CANCELED, getIntent());
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	/*public class ClickListener implements View.OnClickListener
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
			case R.id.btn_back:
		    	cancelAllCard();
		    	setResult(Activity.RESULT_CANCELED, getIntent());
		    	finish();
				break;
			}
		}
    }*/
}
