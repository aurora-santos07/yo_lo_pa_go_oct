package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.Constant;

import net.yolopago.pago.db.ConfigDatabase;
import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.entity.TransactionEnt;
import net.yolopago.pago.fragment.FragmentProductos;
import net.yolopago.pago.repository.TicketRepository;
import net.yolopago.pago.utilities.LEDDeviceHM;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.ws.dto.payment.TransactionDto;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_online_result;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ProcessOnlineActivity extends FuncActivity
{
	private static final String TAG = "ProcessOnlineActivity";
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private static ArrayList<Product> listproductOnline;
	private TextView montoTransac,textTitle;
	private boolean success = false;
	private PaymentViewModel paymentViewModel;
	TransactionDto transactionDto = new TransactionDto();
	private Button   buttonBack = null;
    private Button   buttonMore = null;
	private static final long Start_Time = 25000; //milisegundos en los que expira la sesión
	private CountDownTimer countDownTimer;
	private boolean TimerRunning;
	private long TimeLeft = Start_Time;
	private static int  FIRMA_ACTIVITY=0;
	private static int  TRAN_RESULT_ACTIVITY=1;
	protected ConfigDatabase configDatabase;
	private MerchantDao merchantDao;


	boolean socketThreadRun = false;
    boolean requestDataReady = false;

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		if(debug)Log.d(APP_TAG, "processOnlineActivity onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_process_online);
        //Log.e("Fatal Error", "entrando al Oncreate");
        //startTimer();
		Serializable bundle1 = getIntent().getSerializableExtra("Online");
		listproductOnline = (ArrayList<Product>) bundle1;
		Double total=0d;
		if(bundle1 != null) {
			for (int i = 0; i < listproductOnline.size(); i++) {
				/*String data1 = listproductOnline.get(i).getProducto();
				String data2 = listproductOnline.get(i).getCantidad();
				Integer c = Integer.parseInt(data2);
				Double data3 = listproductOnline.get(i).getPrecio();
				//Double dou = Double.parseDouble(data3);
				Log.i("Information Online", "Regresa : " + data1 + ", " + c + ", " + data3);*/
				total+=listproductOnline.get(i).getTotal();
			}
		}

//    	// title
        montoTransac = (TextView)findViewById(R.id.monto);

		montoTransac.setText(DECIMAL_FORMAT.format(total));

		textTitle = (TextView)findViewById(R.id.tProcessEMVCard_TransType);
//
//	    buttonBack = (Button)findViewById(R.id.btn_back);
//        buttonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
//
//        buttonMore = (Button)findViewById(R.id.btn_more);
//        buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
		configDatabase = ConfigDatabase.getDatabase(getApplication());
		merchantDao = configDatabase.merchantDao();
		if(debug)Log.d(APP_TAG, "processOnlineActivity onCreate ap:"+appState.getProcessState());
		LEDDeviceHM.get().onLED3();
		if(appState.cardType == CARD_CONTACTLESS) {
			textTitle.setText("Procesando en línea");
		}else {
			textTitle.setText("Por favor, no retires la tarjeta");
		}
		sendPayment();

	}

	private void sendPayment() {
		appState.trans.setTrace(appState.terminalConfig.getTrace());
		// Lanza el pago
		success = true;
//		TransactionEnt transactionDto = createTransactionDto();
		//Log.e("Error", "Ver datos: " + appState.trans.getTrack2Data() + " " + appState.trans.getEMVTags());
		transactionDto.setEmvTags(appState.trans.getEMVTags());
		transactionDto.setTrack2(appState.trans.getTrack2Data());
		Double amountDouble = appState.trans.getTransAmount() / 100.00;
		paymentViewModel = new PaymentViewModel(this.getApplication());//ViewModelProviders.of(this).get(PaymentViewModel.class);
		paymentViewModel.setAppState(appState);
		TransactionDto transactionDto = createTransactionDto(amountDouble);
		//paymentViewModel.paymentRepository.transactionDao.insert(new TransactionEnt(transactionDto));
		appState.trans.setTicketId(transactionDto.getIdTicket());
		paymentViewModel.compra(transactionDto).observe(this, s -> {

			//Log.d("(APP_TAG, "COMPRA RESPONSE :"+s);

			if (s.equals("Hecho") || s.equals("ErrorR") || s.equals("ErrorD") || s.equals("ErrorRev")) {

				if (s.equals("Hecho")) {
					success = true;
					appState.trans.setEMVRetCode(Constant.APPROVE_ONLINE);
				} else if (s.equals("ErrorR")) {
				//	appState.trans.setResponseCode(new byte[]{'F', 'F'});
					success = false;
					//Log.d("(APP_TAG, "Pago Rechazo");
					appState.trans.setEMVRetCode(Constant.DECLINE_ONLINE);
				} else if (s.equals("ErrorD")) {
				//	appState.trans.setResponseCode(new byte[]{'F', 'F'});
					success = false;
					//Log.d("(APP_TAG, "Pago Declinado");
					appState.trans.setEMVRetCode(Constant.DECLINE_ONLINE);
				} else if (s.equals("ErrorRev")) {
				//	appState.trans.setResponseCode(new byte[]{'F', 'F'});
					success = false;
					//Log.d("(APP_TAG, "Pago Reversado");
				}
				appState.trans.setResponseMsg(s);
				appState.terminalConfig.incTrace();
				processResult();
				//leds.clear();
				setResult(Activity.RESULT_OK, getIntent());
				exit();
			}else {
				handleFailed(transactionDto,0,false);

			}
		});
	}

	private void handleFailed(TransactionDto transactionDto, int intentos,boolean tryAgain) {
		if(isConnected() && tryAgain==false) {
			paymentViewModel.getPaymentByIdTicket(transactionDto.getIdTicket()).observe(this, response -> {
				if (response.equals("TryAgain")){
					handleFailed(transactionDto,0,true);
				}else {
					success = false;
					//appState.trans.setResponseCode(new byte[]{'F', 'F'});
				}
				if (response.equals("Hecho")) {
					success = true;
					appState.trans.setEMVRetCode(Constant.APPROVE_ONLINE);
				} else if (response.equals("ErrorR")) {
					//Log.d("(APP_TAG, "Pago Rechazo");
					appState.trans.setEMVRetCode(Constant.DECLINE_ONLINE);
				} else if (response.equals("ErrorD")) {
					//Log.d("(APP_TAG, "Pago Declinado");
					appState.trans.setEMVRetCode(Constant.DECLINE_ONLINE);
				} else if (response.equals("ErrorRev")) {
					//Log.d("(APP_TAG, "Pago Reversado");
				}else {
					//Log.d("(APP_TAG, "Pago ONLINE_FAIL");
					appState.trans.setResponseCode(new byte[]{'F', 'F'});
					//appState.trans.setEMVRetCode(ServerURL.);
					if (response.equals("ErrorEx")) {
						//Log.d("(APP_TAG, "Pago TimeOut");
					} else if (response.equals("ErrorCon")) {
						//Log.d("(APP_TAG, "Error Con");
					}
					//Log.d("(APP_TAG, "ERROR EN COMPRA");
				}
				appState.trans.setResponseMsg(response);
				appState.terminalConfig.incTrace();
				processResult();
				if (success) {
					setResult(Activity.RESULT_OK, getIntent());
				} else {
					setResult(Activity.RESULT_CANCELED, getIntent());
				}
				//leds.clear();
				exit();
			});
		}else if (isConnected() && tryAgain ){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessOnlineActivity.this);
			alertDialogBuilder.setTitle("¡Ocurrio un Error!");
			alertDialogBuilder
					.setMessage("¡Presione Continuar para obtener el resultado de la Operación!")
					.setCancelable(false)
					.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if(intentos<3) {
								handleFailed(transactionDto, intentos + 1,false);
							}else{
								appState.trans.setResponseCode(new byte[]{'F', 'F'});
								appState.trans.setResponseMsg("ErrorCon");
								appState.terminalConfig.incTrace();
								processResult();
								setResult(Activity.RESULT_CANCELED, getIntent());
								exit();
							}
						}
					}).create().show();
		}else{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessOnlineActivity.this);
			alertDialogBuilder.setTitle("¡Sin acceso a internet!");
			alertDialogBuilder
					.setMessage("¡Favor de verificar la terminal y dar click en continuar!")
					.setCancelable(false)
					.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if(intentos<3) {
								handleFailed(transactionDto, intentos + 1,false);
							}else{
								appState.trans.setResponseCode(new byte[]{'F', 'F'});
								appState.trans.setResponseMsg("ErrorCon");
								appState.terminalConfig.incTrace();
								processResult();
								setResult(Activity.RESULT_CANCELED, getIntent());
								exit();
							}
						}
					}).create().show();
		}
	}

	private TransactionDto createTransactionDto(Double amount ) {
		Long ticket = TicketRepository.verTicket;
		//Merchant merchant = merchantDao.getFirst();
		//net.yolopago.pago.db.entity.Product product = productDao.findFirst();
		Long terminal = FragmentProductos.getTerminalP();
		Long Seller = FragmentProductos.getIdSellerP();
		String EMvtags = ProcessEMVCardActivity.EmvTags();
		//Log.e("Error", "EMVTags a enviar: " + EMvtags);
		String Track2 = ProcessEMVCardActivity.Track2();
		//Log.e("Error", "Track2 a enviar: " + Track2);

		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setTrack2(Track2);
		transactionDto.setEmvTags(EMvtags);
		//transactionDto.setIdMerchant(merchant.get_id());
		transactionDto.setIdTicket(ticket);
		transactionDto.setIdTerminal(terminal);
		transactionDto.setIdSeller(Seller);
		transactionDto.setAmount(amount);
		String masked_pan="";
		if(appState.trans.getPAN()!=null){
			masked_pan=appState.trans.getPAN();
			if(masked_pan.length()>3) {
				masked_pan = masked_pan.substring(masked_pan.length() - 4);
			}
		}
		transactionDto.setMaskedPAN(masked_pan);

		transactionDto.setTsi(appState.trans.getTSI());
		transactionDto.setAid(appState.trans.getAID());
		transactionDto.setTvr(appState.trans.getTVR());
		transactionDto.setApn(appState.trans.getAppLabel());

		transactionDto.setCardHolder(appState.trans.getCountName()!=null?appState.trans.getCountName().trim():"");

		//Log.e("Error","capturedDate: " + transactionDto.getCapturedDate());
		//Log.e("Error","amount: " + transactionDto.getAmount());
		//Log.e("Error","track1: " + transactionDto.getTrack1());
		//Log.e("Error","track2: " + transactionDto.getTrack2());
		//Log.e("Error","emvTags: " + transactionDto.getEmvTags());
		//Log.e("Error","paymentType: " + transactionDto.getPaymentType());
		//Log.e("Error","idMerchant: " + transactionDto.getIdMerchant());
		//Log.e("Error","idTicket: " + transactionDto.getIdTicket());
		//Log.e("Error","idTerminal: " + transactionDto.getIdTerminal());
		//Log.e("Error","idSeller: " + transactionDto.getIdSeller());
		//Log.e("Error","ticketAmount: " + transactionDto.getTicketAmount());
		//Log.e("Error","cardTermination: " +  transactionDto.getMaskedPAN());
		//Log.e("Error","tsi: " + transactionDto.getTsi());
		//Log.e("Error","aid: " + transactionDto.getAid());
		//Log.e("Error","tvr: " + transactionDto.getTvr());
		//Log.e("Error","apn: " +  transactionDto.getApn());
		//transactionDto.setEmvTags(FuncActivity.transactionChip.getEmvTags());
		//transactionDto.setTrack1("");
		//transactionDto.setTrack2("");


		if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY) {
			transactionDto.setPaymentType("NFC");
		}else if(appState.trans.getCardEntryMode() == INSERT_ENTRY){
			transactionDto.setPaymentType("ICC");
		}else if(appState.trans.getCardEntryMode() == SWIPE_ENTRY){
			transactionDto.setPaymentType("Stripe");
		}

		return transactionDto;
	}


	/*
		TransactionEnt transactionDto = new TransactionEnt();
		Double amountDouble = appState.trans.getTransAmount() / 100.00;
		transactionDto.setAmount(amountDouble);
//		transactionDto.setEmvTags(FuncActivity.transactionChip.getEmvTags());
		transactionDto.setTrack1("probando resultados");
		transactionDto.setTrack2(appState.trans.getTrack2Data());
		transactionDto.setEmvTags(appState.trans.getEMVTags());

		return transactionDto;
	}*/

	@Override
	public void onStart() {
		super.onStart();
		appState.theActivity = this;
	}
	
	private void processResult()
	{
		if(debug)Log.d(APP_TAG, "processResult:"+appState.getProcessState());
		switch(appState.getProcessState())
		{
		case PROCESS_NORMAL:

			if (    appState.trans.getResponseCode() != null
				 &&	appState.trans.getResponseCode()[0] == '0'
				 && appState.trans.getResponseCode()[1] == '0'
			   ) 
			{

    			if(appState.trans.getEMVOnlineFlag() == true)
    			{
					appState.trans.setEMVOnlineResult(ONLINE_SUCCESS);
					byte[] issuerData = appState.trans.getIssuerAuthData();
					if(issuerData != null && issuerData.length > 0)
    				{
    					emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), issuerData, issuerData.length);
    				}
    				else
    				{
    					emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), new byte[]{' '}, 0);
    				}
    			}
				break;
			}
			else if(   appState.trans.getResponseCode() != null
				    && appState.trans.getResponseCode()[0] == 'F'
				    && appState.trans.getResponseCode()[1] == 'F'
			       ) 
			{
				appState.trans.setEMVOnlineResult(ONLINE_FAIL);
				if(debug)Log.d(APP_TAG, "processResult ONLINE_FAIL");
				emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), new byte[]{' '}, 0);
			}
			else{
				appState.trans.setEMVOnlineResult(ONLINE_DENIAL);
				byte[] issuerData = appState.trans.getIssuerAuthData();
				if(issuerData != null && issuerData.length > 0)
				{
					emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), issuerData, issuerData.length);
				}
				else
				{
					emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), new byte[]{' '}, 0);
				}
			}
			break;
		case PROCESS_ADVICE_OFFLINE:
			break;
		}
		setResult(Activity.RESULT_OK, getIntent());
		//exit();

	}

	public static ArrayList<Product> list(){
/*		for (int i = 0; i < listproductOnline.size(); i++) {
			String data1 = listproductOnline.get(i).getProducto();
			String data2 = listproductOnline.get(i).getCantidad();
			Integer c = Integer.parseInt(data2);
			Double data3 = listproductOnline.get(i).getPrecio();
			//Double dou = Double.parseDouble(data3);
		}*/
		return listproductOnline;
	}
	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager)ProcessOnlineActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			//Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
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
				//Log.e("Error", "onFinish ProcessOnlineActivity ");
				/*if(!((Activity) ProcessOnlineActivity.this).isFinishing())
				{
					//show dialog
				}*/
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessOnlineActivity.this);
				// Configura el titulo.
				alertDialogBuilder.setTitle("Sin conexión con el servidor");

				// Configura el mensaje.
				alertDialogBuilder
						.setMessage("¡Favor de intentar más tarde!")
						.setCancelable(false)
						.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								pauseTimer();
								resetTimer();
								cancelMSRThread();
								cancelContactCard();
								requestProduct();
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
		//Timer.setText(timeLastFormatted);
	}
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy ");
		///paymentViewModel.paymentRepository.compositeDisposable.dispose();
		super.onDestroy();
	}

}
