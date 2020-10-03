package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class Sale extends FuncActivity
{
	private static final String TAG = "Sale";
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		Log.i(TAG, " onCreate: Sale");
        appState.setTranType(TRAN_GOODS);
		appState.trans.setTransType(TRAN_GOODS);
		appState.getCurrentDateTime();
		appState.trans.setTransDate(   appState.currentYear
                                     + StringUtil.fillZero(Integer.toString(appState.currentMonth), 2)
                                     + StringUtil.fillZero(Integer.toString(appState.currentDay), 2)
                                   );
		appState.trans.setTransTime(   StringUtil.fillZero(Integer.toString(appState.currentHour), 2)
                                     + StringUtil.fillZero(Integer.toString(appState.currentMinute), 2)
                                     + StringUtil.fillZero(Integer.toString(appState.currentSecond), 2)
                                   );
		if (appState.batchInfo.getSettlePending() != 0)
		{
			Log.i(TAG, " getSettlePendinge");
			appState.setErrorCode(R.string.error_settle_first);
	    	showTransResult();
			return;
		}
		if(appState.needCard == true)
		{
			Log.i(TAG, " needCard");
			requestCard(true, true, true);
//			inputAmount();
		}
		else
		{
			if(appState.trans.getCardEntryMode() == SWIPE_ENTRY)
			{
				Log.i(TAG, " SWIPE_ENTRY");
				ArrayList<Product> listproductSale;
				Serializable bundle1 = getIntent().getSerializableExtra("Sale");
				listproductSale = (ArrayList<Product>) bundle1;
				if(bundle1 != null) {
					for (int i = 0; i < listproductSale.size(); i++) {
						String data1 = listproductSale.get(i).getProducto();
						String data2 = listproductSale.get(i).getCantidad();
						Integer c = Integer.parseInt(data2);
						Double data3 = listproductSale.get(i).getPrecio();
						//Double dou = Double.parseDouble(data3);
						//Log.i("Information Sale", "Regresa : " + data1 + ", " + c + ", " + data3);
					}
				}

				appState.trans.setEMVKernelType(CONTACT_EMV_KERNAL);
				Intent intent = new Intent(this, ProcessEMVCardActivity.class);
				//intent.putExtra("fisico1", "Pago con tarjeta");
				intent.putExtra("EMV", (ArrayList<Product>) listproductSale);
				startActivityForResult(intent, STATE_PROCESS_EMV_CARD);
				Log.e("Error", "startActivityForResult _swipe"  );
				//inputAmount();

			}else //if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY)
			{
				if(isConnected()) {

					Log.i(TAG, " CONTACT[LESS] ENTRY");
					ArrayList<Product> listproductSale;
					Serializable bundle1 = getIntent().getSerializableExtra("Sale");
					listproductSale = (ArrayList<Product>) bundle1;
					if(bundle1 != null) {
						for (int i = 0; i < listproductSale.size(); i++) {
							String data1 = listproductSale.get(i).getProducto();
							String data2 = listproductSale.get(i).getCantidad();
							Integer c = Integer.parseInt(data2);
							Double data3 = listproductSale.get(i).getPrecio();
							//Double dou = Double.parseDouble(data3);
							//Log.i("Information Sale", "Regresa : " + data1 + ", " + c + ", " + data3);
						}
					}

					Bundle bundle = getIntent().getExtras();
					if(bundle != null) {
						String contact = bundle.getString("fisico");
						Log.e("Error pago contact1 ", "tipo de pago: " + contact);
					}
					/*appState.trans.setEMVKernelType(CONTACTLESS_EMV_KERNAL);
					Intent intent = new Intent(this, ProcessEMVCardActivity.class);
					intent.putExtra("contactless1", "Pago con contactless");
					intent.putExtra("EMV", (ArrayList<Product>) listproductSale);
					startActivityForResult(intent, STATE_PROCESS_EMV_CARD);*/
					//Log.d("(TAG, "onCreate: 1");
					if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY){
						processEMVCard(CONTACTLESS_EMV_KERNAL,listproductSale);
					}else{
						processEMVCard(CONTACT_EMV_KERNAL,listproductSale);
					}

				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Sale.this);
					// Configura el titulo.
					alertDialogBuilder.setTitle("¡Sin acceso a internet!");

					// Configura el mensaje.
					alertDialogBuilder
							.setMessage("¡Favor de verificar la terminal y dar click en continuar!")
							.setCancelable(false)
							.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									ArrayList<Product> listproductSale;
									Serializable bundle1 = getIntent().getSerializableExtra("Sale");
									listproductSale = (ArrayList<Product>) bundle1;
									if(bundle1 != null) {
										for (int i = 0; i < listproductSale.size(); i++) {
											String data1 = listproductSale.get(i).getProducto();
											String data2 = listproductSale.get(i).getCantidad();
											Integer c = Integer.parseInt(data2);
											Double data3 = listproductSale.get(i).getPrecio();
											//Double dou = Double.parseDouble(data3);
											//Log.i("Information Sale", "Regresa : " + data1 + ", " + c + ", " + data3);
										}
									}

									Bundle bundle = getIntent().getExtras();
									if(bundle != null) {
										String contact = bundle.getString("fisico");
										Log.e("Error pago contact1 ", "tipo de pago: " + contact);
									}
									/*appState.trans.setEMVKernelType(CONTACTLESS_EMV_KERNAL);
									Intent intent = new Intent(Sale.this, ProcessEMVCardActivity.class);
									intent.putExtra("contactless1", "Pago con contactless");
									intent.putExtra("EMV", (ArrayList<Product>) listproductSale);
									startActivityForResult(intent, STATE_PROCESS_EMV_CARD);*/
									//Log.d("(TAG, "onCreate: 2");
									processEMVCard(CONTACTLESS_EMV_KERNAL,listproductSale);
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
			}/*else {
				if(isConnected()) {
					ArrayList<Product> listproductSale;
					Serializable bundle1 = getIntent().getSerializableExtra("Sale");
					Bundle bundle = getIntent().getExtras();
					listproductSale = (ArrayList<Product>) bundle1;
					if(bundle1 != null) {
						for (int i = 0; i < listproductSale.size(); i++) {
							String data1 = listproductSale.get(i).getProducto();
							String data2 = listproductSale.get(i).getCantidad();
							Integer c = Integer.parseInt(data2);
							Double data3 = listproductSale.get(i).getPrecio();
							//Double dou = Double.parseDouble(data3);
							//Log.i("Information Sale", "Regresa : " + data1 + ", " + c + ", " + data3);
						}
					}

					if(bundle != null) {
						String algo = bundle.getString("fisico");
						Log.e("Error pago 1", "tipo de pago: " + algo );
					}
					appState.trans.setEMVKernelType(CONTACT_EMV_KERNAL);
					Intent intent = new Intent(this, ProcessEMVCardActivity.class);
					intent.putExtra("fisico1", "Pago con tarjeta");
					intent.putExtra("EMV", (ArrayList<Product>) listproductSale);
					startActivityForResult(intent, STATE_PROCESS_EMV_CARD);
					Log.e("Error", "startActivityForResult"  );
					//processEMVCard(CONTACT_EMV_KERNAL);
				}else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Sale.this);
					// Configura el titulo.
					alertDialogBuilder.setTitle("¡Sin acceso a internet!");

					// Configura el mensaje.
					alertDialogBuilder
							.setMessage("¡Favor de verificar la terminal y despues dar click en continuar!")
							.setCancelable(false)
							.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									ArrayList<Product> listproductSale;
									Serializable bundle1 = getIntent().getSerializableExtra("Sale");
									Bundle bundle = getIntent().getExtras();
									listproductSale = (ArrayList<Product>) bundle1;
									if(bundle1 != null) {
										for (int i = 0; i < listproductSale.size(); i++) {
											String data1 = listproductSale.get(i).getProducto();
											String data2 = listproductSale.get(i).getCantidad();
											Integer c = Integer.parseInt(data2);
											Double data3 = listproductSale.get(i).getPrecio();
											//Double dou = Double.parseDouble(data3);
											//Log.i("Information Sale", "Regresa : " + data1 + ", " + c + ", " + data3);
										}
									}

									if(bundle != null) {
										String algo = bundle.getString("fisico");
										Log.e("Error pago 1", "tipo de pago: " + algo );
									}
									appState.trans.setEMVKernelType(CONTACT_EMV_KERNAL);
									Intent intent = new Intent(Sale.this, ProcessEMVCardActivity.class);
									intent.putExtra("fisico1", "Pago con tarjeta");
									intent.putExtra("EMV", (ArrayList<Product>) listproductSale);
									startActivityForResult(intent, STATE_PROCESS_EMV_CARD);
									//processEMVCard(CONTACT_EMV_KERNAL);
								}
							})
							.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									cancelMSRThread();
									cancelContactCard();
									requestProduct();
								}
							}).create().show();
				}
			}*/
		}
    }
        
	@Override
	public void onStart()
	{
        super.onStart();
		//Log.d("(TAG, "onStart");
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

	@Override
	public void startActivityForResult(Intent intent, int requestCode){
		super.startActivityForResult(intent,requestCode);
		//Log.d("(TAG, "startActivityForResult: "+this.getCallingActivity().getClassName());

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Log.d("(TAG, "onDestroy: ");
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult( requestCode,  resultCode, data);
		Log.e(TAG, "requestCode: " + requestCode +" resultCode: " + resultCode +" appState.getErrorCode: " + appState.getErrorCode());
		if(   requestCode != STATE_TRANS_END
		   && appState.getErrorCode() > 0
		  )
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Sale.this);
			alertDialogBuilder
					.setMessage(appState.getErrorCode())
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							appState.setErrorCode(0);
							exitTrans();
						}
					}).create().show();

			//showTransResult();
			return;
		}
		if(resultCode != Activity.RESULT_OK  && resultCode!=TransResultActivity.END_OK && resultCode!=TransResultActivity.END_FAIL_NO && resultCode!=TransResultActivity.END_FAIL_SI)
		{
			Log.e(TAG, "exitTrans: ");
			exitTrans();
			return;
		}
		Log.e(TAG, "continua ");
		switch(requestCode)
		{
		case STATE_INPUT_AMOUNT:
            if(appState.needCard)
            {
                requestCard(true,true, true);
            }
            else
			    inputPIN();
			break;
		case STATE_REQUEST_CARD:
            if(appState.trans.getCardEntryMode() == INSERT_ENTRY)
            {
				//Log.d("(TAG, "onCreate: 3");
				//processEMVCard(CONTACT_EMV_KERNAL, listproductSale);
            }
            else if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY)
            {
				//Log.d("(TAG, "onCreate: 4");
              // processEMVCard(CONTACTLESS_EMV_KERNAL, listproductSale);
			//	requestCard(false,false, true);
            }
            else
            {
                confirmCard();
            }
			break;
        case STATE_CONFIRM_CARD:
            inputPIN();
            break;
		case STATE_INPUT_ONLINE_PIN:
			//processOnline();
			break;
		case STATE_PROCESS_ONLINE:
			showTransResult();
			break;
		case STATE_PROCESS_EMV_CARD:
			/*if(resultCode == Activity.RESULT_CANCELED) {
				exitTrans();
			}else {
				showTransResult();
			}*/
			if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY){
				appState.trans.setNeedSignature(0);
			}
			showTransResult();

			break;
		case STATE_TRANS_END:
			setResult(resultCode);
			exitTrans();
			break;
		case STATE_REMOVE_CARD:
			exitTrans();
			break;
		case ConstantYLP.STATE_NEED_SING:

			showTransResult();
				break;
		}
	}

	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager)Sale.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
	}
}
