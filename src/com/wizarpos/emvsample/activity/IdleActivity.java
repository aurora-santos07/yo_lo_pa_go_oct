package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.jniinterface.LEDInterface;
import com.cloudpos.led.LEDDevice;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.jni.ContactICCardReaderInterface;
import com.wizarpos.jni.ContactlessICCardReaderInterface;
import com.wizarpos.util.StringUtil;

import net.yolopago.pago.utilities.LEDDeviceHM;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

public class IdleActivity extends FuncActivity implements Constant
{
	//	private TextView textTitle  = null;
//	private Button   buttonBack = null;
//    private Button   buttonMore = null;
//
	private static final String TAG = "IdleActivity";
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private TextView requestCard;
//	private TextView idleLine2;
//	private TextView idleLine3;
//	private TextView idleLine4;
	private TextView montoText;
	private TextView Timer;
	private static final long Start_Time = 31000;
	private CountDownTimer countDownTimer;
	private boolean TimerRunning;
	private long TimeLeft = Start_Time;
	public static ArrayList<Product> listproduct;
	private String amountLong;
	private Long total_amount=0l;
	private MediaPlayer mediaPlayer;
	private ImageButton btnBack;


	Context context;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_idle);

		mHandler.setFunActivity(this);

		Serializable bundle1 = getIntent().getSerializableExtra("idle");
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			amountLong = bundle.getString("amount");
			//Log.e("Error", "show mount: " + amountLong);
		}


		listproduct = (ArrayList<Product>) bundle1;
		BigDecimal total=new BigDecimal(0);
		if(bundle1 != null) {
			for (int i = 0; i < listproduct.size(); i++) {

				total=total.add(BigDecimal.valueOf(listproduct.get(i).getTotal()));
				//Log.i("Information Idle", "Regresa : " + new BigDecimal(listproduct.get(i).getTotal()).doubleValue());
			}
			//Log.i("Information Idle", "Regresa Total: " +total.doubleValue());
			total_amount=total.multiply(new BigDecimal(100)).longValue();
			amountLong=DECIMAL_FORMAT.format(total);
		}
		montoText = (TextView)findViewById(R.id.monto);
		montoText.setText(amountLong);
		Timer = (TextView)findViewById(R.id.timer);

		startTimer();


//		textTitle.setText("WIZARPOS");
//
//	    buttonBack = (Button)findViewById(R.id.btn_back);
//        buttonBack.setOnClickListener(new ClickListener());
//
//        buttonMore = (Button)findViewById(R.id.btn_more);
//        buttonMore.setOnClickListener(new ClickListener());

		if(debug)Log.e(APP_TAG, "idleActivity onCreateed");

		requestCard = (TextView)findViewById(R.id.requestCard);
//    	idleLine2 = (TextView)findViewById(R.id.idleLine2);
//    	idleLine3 = (TextView)findViewById(R.id.idleLine3);
//		idleLine4 = (TextView)findViewById(R.id.idleLine4);
//		idleLine5 = (TextView)findViewById(R.id.idleLine5);
		btnBack = (ImageButton)findViewById(R.id.back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBack();
			}
		});
		if(appState.icInitFlag == false)
		{
			if(ContactICCardReaderInterface.init() >= 0)
			{
				//Log.d("(APP_TAG, "ContactICCardReaderInterface.init() OK");
				appState.icInitFlag = true;
			}
			if(ContactlessICCardReaderInterface.init() >= 0)
			{
				//Log.d("(APP_TAG, "ContactlessICCardReaderInterface.init() OK");
				appState.icInitFlag = true;
			}
		}
	}

	@Override
	public void handleMessageSafe(Message msg)
	{
		//PRUEBA CON CONTACTLESS
		//Log.e("Error", "IdleActivity message: " + msg.what);
		switch (msg.what)
		{

			case CARD_CONTACTLESS_ANTISHAKE:
				if(isConnected()) {
					cancelMSRThread();
					appState.resetCardError = false;
					appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);
					appState.needCard = false;

					sale(listproduct);
					/*Intent intentC = new Intent(IdleActivity.this, Sale.class);
					intentC.putExtra("Sale", (ArrayList<Product>) listproduct);
					intentC.putExtra("contactless", "Pago con contactless");
					startActivityForResult(intentC, STATE_REQUEST_CARD);*/
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleActivity.this);
					// Configura el titulo.
					alertDialogBuilder.setTitle("¡Sin acceso a internet!");

					// Configura el mensaje.
					alertDialogBuilder
							.setMessage("¡Favor de verificar la terminal y dar click en continuar!")
							.setCancelable(false)
							.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									cancelMSRThread();
									appState.resetCardError = false;
									appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);
									appState.needCard = false;
									//Log.d("(TAG, "sale 2 ");
									sale(listproduct);
									/*Intent intentC = new Intent(IdleActivity.this, Sale.class);
									intentC.putExtra("Sale", (ArrayList<Product>) listproduct);
									intentC.putExtra("contactless", "Pago con contactless");
									startActivityForResult(intentC, STATE_REQUEST_CARD);*/
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
			case MSR_READ_DATA_NOTIFIER:
				if(false) {

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
							requestCard.setText("Tarjeta con chip, favor de Insertar");
						}
						else{
							AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(IdleActivity.this);
							alertDialogBuilder4.setMessage("¡Error en la lectura de tarjeta!")
									.setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											cancelAllCard();
											exitTrans();
										}
									}).create().show();
							//cancelAllCard();
							//setResult(Activity.RESULT_OK, getIntent());
							//finish();
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
						cancelMSRThread();
						appState.resetCardError = false;
						appState.trans.setCardEntryMode(SWIPE_ENTRY);
						appState.needCard = false;

						//Log.d("(TAG, "sale 3 ");
						sale(listproduct);
						/*Intent intent = new Intent(this, Sale.class);
						intent.putExtra("Sale", (ArrayList<Product>) listproduct);
						intent.putExtra("fisico", "Pago con tarjeta");
						startActivityForResult(intent, STATE_REQUEST_CARD);*/
					}
					//invokePaymentMSRActivities();
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleActivity.this);
					// Configura el titulo.
					//alertDialogBuilder.setTitle("¡Sin acceso a internet!");

					// Configura el mensaje.
					alertDialogBuilder
							.setMessage("Transacción por banda no permitida en la terminal.\n Favor de insertar o aproximar tarjeta.")
							.setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									cancelAllCard();
									exitTrans();
								}
							})
							.create().show();
				}
				break;
			case CARD_INSERT_NOTIFIER:

				LEDDeviceHM.get().blinkLED1();

				if(isConnected()) {
					Bundle bundle = msg.getData();
					int nEventID = bundle.getInt("nEventID");
					int nSlotIndex = bundle.getInt("nSlotIndex");
					if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] A" );
					if(   nSlotIndex == 0
							&& nEventID == SMART_CARD_EVENT_INSERT_CARD
					)
					{
						cancelMSRThread();
						appState.resetCardError = false;
						appState.trans.setCardEntryMode(INSERT_ENTRY);
						appState.needCard = false;

						//Log.d("(TAG, "sale 4 ");
						sale(listproduct);
						/*Intent intent = new Intent(this, Sale.class);
						intent.putExtra("Sale", (ArrayList<Product>) listproduct);
						intent.putExtra("fisico", "Pago con tarjeta");
						startActivityForResult(intent, STATE_REQUEST_CARD);*/
					}
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleActivity.this);
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
									if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] B" );
									if(   nSlotIndex == 0
											&& nEventID == SMART_CARD_EVENT_INSERT_CARD
									)
									{
										cancelMSRThread();
										appState.resetCardError = false;
										appState.trans.setCardEntryMode(INSERT_ENTRY);
										appState.needCard = false;

										//Log.d("(TAG, "sale 5 ");
										sale(listproduct);
										/*Intent intent = new Intent(IdleActivity.this, Sale.class);
										intent.putExtra("Sale", (ArrayList<Product>) listproduct);
										intent.putExtra("fisico", "Pago con tarjeta");
										startActivityForResult(intent, STATE_REQUEST_CARD);*/
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
			case CARD_TAPED_NOTIFIER:
				mediaPlayer = MediaPlayer.create(this, R.raw.ncf_initiated);
				mediaPlayer.start();

                LEDDeviceHM.get().blinkLED1();


				if(isConnected()) {
					Bundle bundle = msg.getData();
					int nEventID = bundle.getInt("nEventID");
					int nSlotIndex = bundle.getInt("nSlotIndex");
					if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] A" );
					if(   nSlotIndex == 0
							&& nEventID == SMART_CARD_EVENT_INSERT_CARD
					)
					{
						cancelMSRThread();
						appState.resetCardError = false;
						appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);
						appState.needCard = false;

						//Log.d("(TAG, "sale 6 ");

						sale(listproduct);
						/*Intent intent = new Intent(this, Sale.class);
						intent.putExtra("Sale", (ArrayList<Product>) listproduct);
						intent.putExtra("fisico", "Pago con tarjeta");
						startActivityForResult(intent, STATE_REQUEST_CARD);*/
					}
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleActivity.this);
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
									if(debug)Log.d(APP_TAG, "get CONTACT_CARD_EVENT_NOTIFIER,event[" + nEventID + "]slot[" + nSlotIndex + "] B" );
									if(   nSlotIndex == 0
											&& nEventID == SMART_CARD_EVENT_INSERT_CARD
									)
									{
										cancelMSRThread();
										appState.resetCardError = false;
										appState.trans.setCardEntryMode(INSERT_ENTRY);
										appState.needCard = false;

										//Log.d("(TAG, "sale 7 ");
										sale(listproduct);
										/*Intent intent = new Intent(IdleActivity.this, Sale.class);
										intent.putExtra("Sale", (ArrayList<Product>) listproduct);
										intent.putExtra("fisico", "Pago con tarjeta");
										startActivityForResult(intent, STATE_REQUEST_CARD);*/
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
				if(appState.cardType== CARD_CONTACTLESS) {
					mediaPlayer = MediaPlayer.create(this, R.raw.nfc_failure);
					mediaPlayer.start();
				}

                LEDDeviceHM.get().blinkLED4();

				cancelIdleTimer();
				cancelAllCard();
				pauseTimer();
				AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(IdleActivity.this);
				alertDialogBuilder4.setTitle("¡Error!");
				alertDialogBuilder4.setMessage("¡Error en la lectura de tarjeta!")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								exitTrans();
							}
						}).create().show();

				break;
		}
	}

	@Override
	protected void onStart() {
		if(debug)Log.e(APP_TAG, "idleActivity onStart");
		super.onStart();
		appState.theActivity = this;
		appState.initData();
		//Long amount = Long.parseLong(to);
		//Log.e("Error", "Valor del double es: " + total_amount);
		appState.trans.setTransAmount(total_amount);

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
			Log.d(TAG, "emv_get_kernel_checksum: "+ StringUtil.toHexString(kernelChecksum, false));
//			idleLine4.setText("KC: " + StringUtil.toHexString(kernelChecksum, false));
		}
		if(emv_get_config_checksum(configChecksum, configChecksum.length) > 0)
		{
			Log.d(TAG, "emv_get_config_checksum: "+ StringUtil.toHexString(configChecksum, false));
//			idleLine5.setText("CC: " + StringUtil.toHexString(configChecksum, false));
		}
		//mHandler.setFunActivity(this);

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
		pauseTimer();
	}

	@Override
	public void onBackPressed(){
		appState.idleFlag = false;
        cancelAllCard();
		pauseTimer();
		//requestFuncMenu();
		setResult(Activity.RESULT_CANCELED);
		exit();
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
			registerFunctionListener(this);
			emv_kernel_initialize();
			emv_set_kernel_attr(new byte[]{0x20}, 1);
			if(loadCAPK() == -2)
			{
				capkChecksumErrorDialog(IdleActivity.this);
			}
			loadAID();
			loadExceptionFile();
			loadRevokedCAPK();
			setEMVTermInfo();
			emv_set_force_online(appState.terminalConfig.getforceOnline());


			appState.emvParamLoadFlag = true;
            //Log.d("(TAG, "loadEMVParam: INIT9");
		}
        //Log.d("(TAG, "loadEMVParam: FININIT");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode,resultCode,data);
		//Log.d("(TAG, "onActivityResult: requestCode:"+requestCode+" resultCode"+resultCode);
		pauseTimer();
		setResult(resultCode);
		finish();
	}

	protected void invokePaymentMSRActivities() {
		cancelIdleTimer();
		Intent intent = new Intent(getApplicationContext(), IdleMsrActivity.class);
		intent.putExtra("amount", amountLong);
		intent.putExtra("MSR", (ArrayList<Product>) listproduct);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onUserInteraction(){
		super.onUserInteraction();
		//pauseTimer();
		resetTimer();
		//startTimer();
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
				//Log.e("Error", "onFinish IdleActivity ");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IdleActivity.this);
				// Configura el titulo.
				//alertDialogBuilder.setTitle("Continuar Pago");

				// Configura el mensaje.
				alertDialogBuilder
						.setMessage("No se ha recibido forma de Pago. ¿Quieres continuar con la transacción?")
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
								//requestProduct();
								exit();
							}
						}).create().show();
			}
		}.start();
		TimerRunning = true;
	}

	private void resetTimer(){
		TimeLeft = Start_Time;
		//countDownTimer.start();
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

	public static ArrayList<Product> listIdle() {
		for (int i = 0; i < listproduct.size(); i++) {
			String data1 = listproduct.get(i).getProducto();
			String data2 = listproduct.get(i).getCantidad();
			Integer c = Integer.parseInt(data2);
			Double data3 = listproduct.get(i).getPrecio();
			//Double dou = Double.parseDouble(data3);
		}
		return listproduct;
	}

	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager)IdleActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			//Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
	}
}
