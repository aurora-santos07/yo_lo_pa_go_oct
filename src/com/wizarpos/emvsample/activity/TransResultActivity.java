package com.wizarpos.emvsample.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.cloudpos.jniinterface.PrinterInterface;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.transaction.TransDefine;

import net.yolopago.pago.db.PaymentDatabase;
import net.yolopago.pago.db.dao.payment.TransactionDao;
import net.yolopago.pago.utilities.CipherHM;
import net.yolopago.pago.utilities.LEDDeviceHM;
import net.yolopago.pago.utilities.PrinterHM;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.ticket.PdfDto;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_offlinepin_times;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_tag_data;

public class TransResultActivity extends FuncActivity
{
	public static Handler handler = new Handler();
	private static final String TAG = "TransResultActivity";
	public static int END_FAIL_SI=1001;
	public static int END_FAIL_NO=1002;
	public static int END_OK=1003;

	private TextView textLine1;
	private TextView textLine2;
	private TextView pregunta;

	private View secBotonesAceptado;
	private View secBotonesDeclinado;

	private String errno;
	private PaymentViewModel paymentViewModel;
	private Button siPago;
	private Button noPago;
	private TicketViewModel ticketViewModel;
	private Button printVaucher;
	private Button printProductos;
	public static String dataMetodoPago = "";
	PdfDto pdfDto = new PdfDto();
	public static ArrayList<Product> listproductTrans;


	private String resultCompra="";

	private int mPrintTimer = 0;
	private int intSeconds = 0;
	private Timer mTimerSeconds;
	private boolean printPaused = false;
	private ImageView img_aprobado;
	private ImageView img_declinado;
	private ImageView img_error;
	private boolean print_voucher=false;
	private boolean save_voucher=false;
	private PrinterHM printerHM;

	@Override
	public void handleMessageSafe(Message msg)
	{
		/*这里是处理信息的方法*/
		switch (msg.what)
		{
			case PRINT_PAUSE_TIMER_NOTIFIER:
				if(printPaused == true)
				{
					cancelPrintPauseTimer();
					//continuePrintReceipt();
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_trans_result);
		appState.theActivity = this;
		Log.d(TAG, "onCreate: Cargando ...");

		textLine1 = (TextView)findViewById(R.id.tTransResult_Line1);
		textLine2 = (TextView)findViewById(R.id.tTransResult_Line2);
		pregunta = (TextView)findViewById(R.id.pregunta);

		img_aprobado=(ImageView)findViewById(R.id.img_aprobado);
		img_declinado=(ImageView)findViewById(R.id.img_declinado);
		img_error=(ImageView)findViewById(R.id.img_error);
		secBotonesAceptado=(View)findViewById(R.id.secBotonesAceptado);
		secBotonesDeclinado=(View)findViewById(R.id.secBotonesDeclinado);

		secBotonesDeclinado.setVisibility(View.INVISIBLE);

		img_declinado.setVisibility(View.INVISIBLE);

		String firmaPrueba = "0";

		resultCompra="";

		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			String algo = bundle.getString("imprimir");
			String errnom = bundle.getString("ErrorIn");
			errno=errnom;
		}
		resultCompra=appState.trans.getResponseMsg();
		resultCompra=resultCompra==null?"":resultCompra;


		ImageButton salirBtn=(ImageButton)findViewById(R.id.btn_back);
		printProductos  = (Button)findViewById(R.id.okButton);
		printVaucher = (Button)findViewById(R.id.reimprimir);
		siPago = (Button)findViewById(R.id.formaPagoSi);
		noPago = (Button)findViewById(R.id.formaPagoNo);

		salirBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(TransResultActivity.END_OK);
				exit();
			}
		});

		siPago.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(TransResultActivity.END_FAIL_SI);
				exit();
			}
		});
		noPago.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(TransResultActivity.END_FAIL_NO);
				exit();
			}
		});

		printProductos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransResultActivity.this);
				alertDialogBuilder
						.setMessage("¿Desea imprimir el ticket?")
						.setCancelable(true)
						.setNegativeButton("Si", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								printProductos.setEnabled(false);
								printReceiptProductos(true,false);
								printProductos.setEnabled(true);
							}
						}).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).create().show();


			}
		});
		printVaucher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransResultActivity.this);
				alertDialogBuilder
						.setMessage("¿Desea imprimir el voucher?")
						.setCancelable(true)
						.setNegativeButton("Si", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								printVaucher.setEnabled(false);
								Reimprimir();
								printVaucher.setEnabled(true);
							}
						}).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				}).create().show();

			}
		});

		printerHM=PrinterHM.getInstance(getApplicationContext());
		mHandler.setFunActivity(this);

		if(appState.trans.getNeedSignature()==0) {
			handleResult();
		}else {
			if(resultCompra.equals("Hecho")) {
				showFirma();
			}else {
				handleResult();
			}
		}


		Log.d(TAG, "onCreate: ...FIN");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart: Starting...");
	}

	protected void handleResult(){
		Log.d(TAG, "handleResult");
		if(appState.getErrorCode() > 0)
		{
			textLine2.setText(appState.getErrorCode());
			Log.e("Error", "getErrorCode: " + textLine2.getText().toString() );
			if(   appState.trans.getCardEntryMode() == INSERT_ENTRY || appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY){
				textLine2.setText("Imprimiendo Ticket...");
			}

		}
		else
		{
			Log.d(TAG, "appState.getTranType()"+appState.getTranType());
			if(appState.getTranType() == TRAN_SETTLE)
			{
				textLine1.setText("");
				textLine2.setText("Imprimiendo Ticket...");
			}
			else{
				if(appState.trans.getCardEntryMode()  == SWIPE_ENTRY)
				{
					if(   appState.trans.getResponseCode()[0] == '0'
							|| appState.trans.getResponseCode()[1] == '0'
					)
					{
						textLine1.setText("¡Pago Aprobado!");
						if(( TransDefine.transInfo[appState.getTranType()].flag & T_NORECEIPT) == 0)
						{
							textLine2.setText("Imprimiendo Ticket...");
						}
					}
					else{
						textLine1.setText("Pago Declinado");
						textLine2.setText("Imprimiendo Ticket...");
					}
				}
				else{
					Log.d(TAG, "getEMVRetCode "+appState.trans.getEMVRetCode());
					if(   appState.trans.getEMVRetCode() == APPROVE_OFFLINE
							|| appState.trans.getEMVRetCode() == APPROVE_ONLINE
					)
					{
						Log.d(TAG, "getEMVRetCode 269");
						handleResponse();

					}
					else
					{
						if(appState.trans.getEMVRetCode() == DECLINE_ONLINE)
						{
							Log.d(TAG, "DECLINE_ONLINE");
							textLine1.setText("Pago Declinado");
							textLine2.setText("Imprimiendo voucher...");
							handleResponse();
						}else {

							handleResponse();

						}
					}
				}



			}
		}
		Log.d(TAG, "onStart: ..EDN");

	}

	private void handleResponse() {
		String messageText="ERROR";
		Log.d(TAG, "handleResponse: "+resultCompra);

		switch (resultCompra) {
			case "Hecho":
				messageText = "¡Pago Aprobado!";
				break;
			case "ErrorD":
				messageText = "Pago Declinado";
				break;
			case "ErrorR":
				messageText = "Pago Rechazado";
				break;
			case "ErrorT":
				messageText = "Tiempo Excedido";
				break;
			case "ErrorCon":
				messageText = "Error de Comunicación";
				break;
			case "ErrorRev":
				messageText = "Pago Reversado";
				break;
			default:
				messageText = "Error de Aplicacion";
				break;

		}

		textLine1.setText(messageText);
		textLine2.setText("Imprimiendo voucher...");
		textLine2.setTextColor(Color.parseColor("#65D6CB"));
		if (resultCompra.equals("Hecho")) {

			appState.typePrintRecive=ConstantYLP.RECIBO_ACEPTADO;
			textLine1.setText(messageText);
			printReceipt(true,true);
			secBotonesAceptado.setVisibility(View.VISIBLE);
			secBotonesDeclinado.setVisibility(View.GONE);
		}else{
			textLine2.setTextColor(Color.parseColor("#dd8855"));
			pregunta.setText("¿Desea intentar otro método de Pago?");

			img_declinado.setVisibility(View.VISIBLE);
			img_aprobado.setVisibility(View.INVISIBLE);

			secBotonesAceptado.setVisibility(View.GONE);
			secBotonesDeclinado.setVisibility(View.VISIBLE);

			appState.typePrintRecive = ConstantYLP.RECIBO_TIMEOUT;
			switch (resultCompra) {
				case "ErrorD":
					appState.typePrintRecive = ConstantYLP.RECIBO_DECLINADO;
					break;
				case "ErrorR":
					appState.typePrintRecive = ConstantYLP.RECIBO_RECHAZADO;
					break;
				case "ErrorT":
					appState.typePrintRecive = ConstantYLP.RECIBO_TIMEOUT;
					img_declinado.setVisibility(View.INVISIBLE);
					img_error.setVisibility(View.VISIBLE);
					break;
				case "ErrorCon":
					appState.typePrintRecive = ConstantYLP.RECIBO_CONEXION;
					img_declinado.setVisibility(View.INVISIBLE);
					img_error.setVisibility(View.VISIBLE);
					break;
				case "ErrorRev":
					appState.typePrintRecive = ConstantYLP.RECIBO_REVERSADO;
					break;
				default:
					appState.typePrintRecive = ConstantYLP.RECIBO_ERROR_APP;
					img_declinado.setVisibility(View.INVISIBLE);
					img_error.setVisibility(View.VISIBLE);

			}
			printReceipt(true,true);
			String message=appState.trans.getFailReason().equals("")?"¡Error al Intentar la operación!":appState.trans.getFailReason();
			textLine2.setText(message);


		}
		if(resultCompra.equals("Hecho")
		|| resultCompra.equals("ErrorD")
				|| resultCompra.equals("ErrorR")
				|| resultCompra.equals("ErrorRev")){

			Disposable db = Observable.just("DB")
					.subscribeOn(Schedulers.computation())
					.flatMap((String s) -> {
						PaymentDatabase paymentDatabase = PaymentDatabase.getDatabase(this.getApplication());
						TransactionDao transactionDao= paymentDatabase.transactionDao();
						transactionDao.deleteAll();
						return Observable.just("");
					}).observeOn(AndroidSchedulers.mainThread())
					.subscribe();

		}else{
			checkReversos();


		}
		if(appState.typePrintRecive == ConstantYLP.RECIBO_DECLINADO||
				appState.typePrintRecive == ConstantYLP.RECIBO_RECHAZADO||
				appState.typePrintRecive==ConstantYLP.RECIBO_ACEPTADO
		){

			byte[] COUNTER = new byte[1];
			byte[] TVR2 = new byte[5];
			emv_get_tag_data(0x95, TVR2, 5);
			emv_get_tag_data(0x9F17, COUNTER, 1);

			Log.d(TAG, "TAGS2 emv_get_offlinepin_times:"+emv_get_offlinepin_times()+" 0x9F17:"+ CipherHM.bytesToHex(COUNTER));


			if(emv_get_offlinepin_times()>COUNTER[0] || (TVR2[2]&(byte)0x20)!=(byte)0x00 ) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransResultActivity.this);
				alertDialogBuilder
						.setMessage(R.string.error_pin_try_exceeded)
						.setCancelable(false)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						})
						.create().show();
			}
		}

	}

	@Override
	protected  void onResume(){
		super.onResume();
		LEDDeviceHM.get().clear();
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

	@Override
	public void onBackPressed(){

	}

	private void startPrintPauseTimer(int timerSecond)
	{
		printPaused = true;
		mPrintTimer = timerSecond;
		intSeconds = 0;
		//create timer to tick every second
		mTimerSeconds = new Timer();
		mTimerSeconds.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				intSeconds++;
				if (intSeconds == mPrintTimer)
				{
					Message m = new Message();
					m.what = PRINT_PAUSE_TIMER_NOTIFIER;
					mHandler.sendMessage(m);
				}
			}
		}, 0, 1000);
	}

	private void cancelPrintPauseTimer()
	{
		printPaused = false;
		intSeconds = 0;
		mTimerSeconds.cancel();
	}


	private void printReceiptTemp(boolean print,boolean save)
	{
		print_voucher=print;
		save_voucher=save;
	}

	private void printReceipt(boolean print,boolean save)

	{
		//PrinterInterface.open();
		//Log.d(TAG, "run: Impresora sin papel"+PrinterInterface.queryStatus());
		Log.d(TAG, "run: before1"+System.currentTimeMillis()/1000);
		if(printerHM.hasPaper()!=1){//PrinterInterface.queryStatus() != 1){
			PrinterInterface.close();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransResultActivity.this);
			alertDialogBuilder
					.setMessage("¡Sin papel en la impresora!")
					.setCancelable(true)
					.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							printReceiptThread(print, save);
						}
					}).create().show();

		}else {

			printReceiptThread(print, save);

		}
	}

	private void  printReceiptThread(boolean print,boolean save){

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "run: before1" + System.currentTimeMillis() / 1000);

				printerHM.print(appState, appState.printReceipt, print, save);

				if (save) {
					printReceiptProductos(false, true);
				}
				Log.d(TAG, "run: before2" + System.currentTimeMillis() / 1000);
			}
		});
	}



	private void printReceiptProductos(boolean print,boolean save)
	{
		//PrinterInterface.open();
		if(printerHM.hasPaper()!=1){//PrinterInterface.queryStatus() != 1){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransResultActivity.this);
			alertDialogBuilder
					.setMessage("¡Sin papel en la impresora!")
					.setCancelable(true)
					.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							printerHM.printReceipt(appState,null,print,save);
						}
					}).create().show();

		}else {

			printerHM.printReceipt(appState,null,print,save);

		}


	}






	public void Reimprimir() {
		appState.printReceipt = 0;
		printReceipt(true,false);
	}

	public static String changePay(){
		return dataMetodoPago;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult( requestCode,  resultCode,  data);
		Log.e(TAG, "requestCode: " + requestCode +" resultCode: " + resultCode +" appState.getErrorCode: " + appState.getErrorCode());
		switch (requestCode){

			case ConstantYLP.STATE_NEED_SING:
				handleResponse();
				break;
		}
	}

	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager)TransResultActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
	}


	protected void checkReversos(){


		Disposable db = Observable.just("DB")
				.subscribeOn(Schedulers.computation())
				.flatMap((String s) -> {
					PaymentDatabase paymentDatabase = PaymentDatabase.getDatabase(this.getApplication());
					TransactionDao transactionDao= paymentDatabase.transactionDao();
					if(paymentViewModel==null){
						paymentViewModel = new PaymentViewModel(this.getApplication());
					}
					if(transactionDao.getAllLast().length>0) {
						paymentViewModel.getReverseByIdTicket(transactionDao.getAllLast()[0].getIdTicket());
					}
					return Observable.just("");
				}).observeOn(AndroidSchedulers.mainThread())
				.subscribe();
	}

}
