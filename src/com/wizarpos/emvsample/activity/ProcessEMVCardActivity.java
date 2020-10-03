package com.wizarpos.emvsample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.transaction.TransDefine;
import com.wizarpos.util.AppUtil;
import com.wizarpos.util.ByteUtil;
import com.wizarpos.util.StringUtil;

import net.yolopago.pago.utilities.CipherHM;
import net.yolopago.pago.utilities.LEDDeviceHM;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import static com.cloudpos.jniinterface.EMVJNIInterface.close_reader;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_anti_shake_finish;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_candidate_list;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_offlinepin_times;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_tag_data;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_get_tag_list_data;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_is_need_advice;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_is_need_signature;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_is_tag_present;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_offlinepin_verified;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_process_next;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_anti_shake;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_kernel_type;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_online_pin_entered;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_online_result;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_other_amount;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_tag_data;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_trans_amount;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_set_trans_type;
import static com.cloudpos.jniinterface.EMVJNIInterface.emv_trans_initialize;
import static com.cloudpos.jniinterface.EMVJNIInterface.open_reader;
import static com.cloudpos.jniinterface.EMVJNIInterface.open_reader_ex;
import static com.cloudpos.jniinterface.EMVJNIInterface.set_display_language;

import net.yolopago.pago.viewmodel.TicketViewModel;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;


public class ProcessEMVCardActivity extends FuncActivity {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private static final String TAG = "ProcessEMVCardActivity";
	private static MerchantViewModel merchantViewModel;
	public static ArrayList<Product> listproductEMV;
	public static String Internet = "";
	public static String CardType = "";
	private static TicketViewModel ticketViewModel;
	private TextView TipoPago, tipoPagoC,montoTransac;
	private int banorteTagList[] = {
			0x9F26, // (Application Cryptogram)
			0x9F27, // (Cryptogram Information Data)
			0x9F10, // (Issuer Application Data)
			0x9F37, // (Unpredictable Number)
			0x9F36, // (Application Transaction Counter)
			0x95, // (Terminal Verification Results)
			0x9A, // (Transaction Date)
			0x9C, // (Transaction Type)
			0x9F02, // (Amount, Authorized)
			0x9F0E, //nombre del banco *
			0x5F20, // name
			0x5F2A, // (Transaction Currency Code)
			0x82, // (Application Interchange Profile)
			0x5A, // (PAN - truncated)
			0x9F1A, // (Terminal Country Code)
			0x9F34, // (CVM Results) //Referencia
			0x9F03, // (Amount, Other)
			0x5F34, // (PAN Sequence Number)
	};

	/*private int defaultTagList[] = {0x57,
			0x5A,
			0x5F20,
			0x5F24,
			0x5F25,
			0x5F28, // 4 digitos de la tarjeta *
			0x5F30, //estatus de la ransaccion *
			0x5F2A,
			0x5F34, //Referencia
			0x82,
			0x9F0E,
			0x84,
			0x8E,
			0x95,
			0x9A, // Fecha de la transacción
			0x9B, // numero de control *
			0x9C, // terminal Id *
			0x9F01,
			0x9F02,
			0x9F03,// ARQC
			0x9F07,
			0x9F09,
			0x9F0D,
			0x9F0F,
			0x9F10,
			0x9F15,
			0x9F16,
			0x9F1A,
			0x9F1C,
			0x9F1E,
			0x9F1F,
			0x9F21, //Hora de la transaccion
			0x9F26,
			0x9F27,
			0x9F33,
			0x9F34,
			0x9F35,
			0x9F36,
			0x9F37,
			0x9F39,
			0x9F41,
			0x9F4C,
			0x9F5D,
			0x9F63,
			0x9F66,
			0x9F6C,
			0x9F74,
			0xDF31};*/

	private int defaultTagList[] = {
			0x57,//*
			0x4F,//*
			0x50,//*
			0x5A,//*
			0x82,//*
			0x84,//*
			0x8A,
			0x95,//*
			0x9A,//*
			0x9B,//*
			0x9C,//*
			0xC2,//*
			0xE2,
			0x5F20,
			0x5F24,//*
			0x5F25,//*
			0x5F28,//*
			0x5F2A,//*
			0x5F30,//*
			0x5F34,//*
			0x9F02,//*
			0x9F03,
			0x9F07,//*
			0x9F09,//*
			0x9F0D,//*
			0x9F0E,//*
			0x9F0F,//*
			0x9F10,//*
			0x9F12,//*
			0x9F15,//*
			0x9F1A,//*
			0x9F1C,//*
			0x9F1E,//*
			0x9F21,//*
			0x9F26,//*
			0x9F27,//*
			0x9F33,//*
			0x9F34,//*
			0x9F35,//*
			0x9F36,//*
			0x9F37,//*
			0x9F39,//*
			0x9F41,//*
			0x9F53
	};


	private int confirmTagList[] = {
			0x9F1C,
			0x9F27,  // Cryptogram Information Data
			0x95,    // Terminal Verification Results
			0x9B,    // TSI
			0x9F26,
			0x9F4C,
			0x9F74,
			0xDF31  // I-ssuer Script Results
	};
	//	private TextView textTitle  = null;
//	private Button   buttonBack = null;
//    private Button   buttonMore = null;
//
	private TextView textTransType = null;
	private TextView textLine1 = null;

	private Thread mEMVThread = null;
	private Thread mEMVProcessNextThread = null;
	@Override
	public void handleMessageSafe(Message msg) {
		/*这里是处理信息的方法*/

		Log.e("Error","handleMessageSafe");
		merchantViewModel = ViewModelProviders.of(this).get(MerchantViewModel.class);
		merchantViewModel.getMerchant().observe(this, merchant -> {
			String street = "";
			String external = "";
			String internal = "";
			street = merchant.getStreet()==null?"":merchant.getStreet();
			external = merchant.getExternal()==null?"":merchant.getExternal();
			internal = merchant.getInternal()==null?"":merchant.getInternal();
			String Direction = street + " " + external + " " + internal;
			appState.trans.setDir(Direction);
		});

		merchantViewModel = ViewModelProviders.of(this).get(MerchantViewModel.class);
		merchantViewModel.getMerchant().observe(this, merchant -> {
			String Nombre = "";
			Nombre = merchant.getName();
			appState.trans.setMerchantPhone(merchant.getPhone());
			appState.trans.setMerchantName(Nombre);
			//appState.terminalConfig.setMID(""+merchant.get_id());
		});

		//poner aqui el metodo cortado
		Log.e("Error", "what ="+ msg.what +" emvStatus = " + appState.trans.getEMVStatus() + ", emvRetCode = " + appState.trans.getEMVRetCode());
		switch (msg.what) {
			case EMV_PROCESS_NEXT_COMPLETED_NOTIFIER:
				if (debug)
					Log.d(APP_TAG, "EMV_PROCESS_NEXT_COMPLETED_NOTIFIER, emvStatus = " + appState.trans.getEMVStatus() + ", emvRetCode = " + appState.trans.getEMVRetCode());
				byte[] tagData;


				int tagDataLength = 0;
				switch (appState.trans.getEMVStatus()) {
					case STATUS_CONTINUE:
						appState.trans.setNeedSignature(emv_is_need_signature());
						appState.trans.setTerminalId(Build.SERIAL);
						Log.e("Error", "STATUS_CONTINUE:"+appState.trans.getEMVRetCode());
						switch (appState.trans.getEMVRetCode()) {
							case EMV_CANDIDATE_LIST:
								appState.aidNumber = emv_get_candidate_list(appState.aidList, appState.aidList.length);
								selectEMVAppList();
								break;
							case EMV_APP_SELECTED:
								if (appState.getTranType() == QUERY_CARD_RECORD || appState.trans.getTransAmount() > 0) {
									mEMVProcessNextThread = new EMVProcessNextThread();
									mEMVProcessNextThread.start();
								} else {
									inputAmount();
								}
								break;
							case EMV_READ_APP_DATA:
								if (emv_is_tag_present(0x9F79) >= 0) {
									tagData = new byte[6];
									emv_get_tag_data(0x9F79, tagData, 6);
									appState.trans.setECBalance(ByteUtil.bcdToInt(tagData));
									//Log.i("information", "EMVTag 0x9F79: " + ByteUtil. bcdToInt(tagData));
								}

								tagData = new byte[100];
								if (emv_is_tag_present(0x5A) >= 0) {
									tagDataLength = emv_get_tag_data(0x5A, tagData, tagData.length);
									appState.trans.setPAN(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
                                    //Log.i("information", "Serial: " + Build.SERIAL);
                                    //Log.i("information", "EMVTag 0x5A: " + ByteUtil. bcdToAscii(tagData));
								}

								if (emv_is_tag_present(0x9B) >= 0) {
									tagDataLength = emv_get_tag_data(0x9B, tagData, tagData.length);
									appState.trans.setControlNumber(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                                    //Log.i("information", "EMVTag 0x9B: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
								}

								if (emv_is_tag_present(0x9F03) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F03, tagData, tagData.length);
									appState.trans.setARQC(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                                    //Log.i("information", "EMVTag 0x9F03: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
								}

								if (emv_is_tag_present(0x5F34) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F34, tagData, tagData.length);
									appState.trans.setReference(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                                    //Log.i("information", "EMVTag 0x5F34: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
								}


								if (emv_is_tag_present(0x9F0E) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F0E, tagData, tagData.length);
									byte[] count1 = new byte[tagDataLength];
									System.arraycopy(tagData, 0, count1, 0, count1.length);
									//appState.trans.setBankName(StringUtil.toString(count1));
                                    //Log.i("information", "EMVTag 0x9F0E: " + StringUtil.toString(count1));
								}

								if (emv_is_tag_present(0x9A) >= 0) {
									tagDataLength = emv_get_tag_data(0x9A, tagData, tagData.length);
									byte[] count2 = new byte[tagDataLength];
									System.arraycopy(tagData, 0, count2, 0, count2.length);
									//appState.trans.setBankName(StringUtil.toString(count2));
                                    //Log.i("information", "EMVTag 0x9A: " + StringUtil.toString(count2));
								}

								if (emv_is_tag_present(0x9F21) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F21, tagData, tagData.length);
									appState.trans.setHour(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                                    //Log.i("information", "EMVTag 0x9F21: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));

								}

								if (emv_is_tag_present(0x5F30) >= 0) {
									tagDataLength = emv_get_tag_data(0x5F30, tagData, tagData.length);
									byte[] estatusT = new byte[tagDataLength];
									System.arraycopy(tagData, 0, estatusT, 0, estatusT.length);
									appState.trans.setEstatusTransfer(StringUtil.toString(estatusT));
                                    //Log.i("information", "EMVTag 0x5F30: " + StringUtil.toString(estatusT));
								}

								if (emv_is_tag_present(0x9C) >= 0) {
									tagDataLength = emv_get_tag_data(0x9C, tagData, tagData.length);
									byte[] tipo1 = new byte[tagDataLength];
									System.arraycopy(tagData, 0, tipo1, 0, tipo1.length);
									appState.trans.setVenta(StringUtil.toString(tipo1));
                                    //Log.i("information", "EMVTag 0x9C: " + StringUtil.toString(tipo1));
								}


								tagData = new byte[100];
								if (emv_is_tag_present(0x5F28) >= 0) {
									tagDataLength = emv_get_tag_data(0x5F28, tagData, tagData.length);
									appState.trans.setTDigit(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
                                    //Log.i("information", "EMVTag 0x5F28: " + StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
								}

								if (emv_is_tag_present(0x5F20) >= 0) {
									tagDataLength = emv_get_tag_data(0x5F20, tagData, tagData.length);
									byte[] count = new byte[tagDataLength];
									System.arraycopy(tagData, 0, count, 0, count.length);
									appState.trans.setCountName(StringUtil.toString(count));
                                    //Log.i("information", "EMVTag 0x5F20: " + StringUtil.toString(count));
								}

								if (emv_is_tag_present(0x9F09) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F09, tagData, tagData.length);
									byte[] count = new byte[tagDataLength];
									System.arraycopy(tagData, 0, count, 0, count.length);
									//appState.trans.setCountName(StringUtil.toString(count));
									Log.e("FatalE Error", "EMVTag 0x9F09: " + StringUtil.toString(count));
									CardType = StringUtil.toString(count);
									//adsdasdas
								}

								if (emv_is_tag_present(0x8A) >= 0) {
									tagDataLength = emv_get_tag_data(0x8A, tagData, tagData.length);
									appState.trans.setCodeAut(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                                    //Log.i("information", "EMVTag 0x8A: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
								}

								// Track1
								if (emv_is_tag_present(0x9F1F) >= 0) {
									tagDataLength = emv_get_tag_data(0x9F1F, tagData, tagData.length);
									appState.trans.setTrack1Data(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
                                    Log.e("Fatal Error", " Track 1, EMVTag 0x9F1F: " + StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
								}
								// Track2
								if (emv_is_tag_present(0x57) >= 0) {
									tagDataLength = emv_get_tag_data(0x57, tagData, tagData.length);
									appState.trans.setTrack2Data(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
                                    Log.e("Fatal Error", "Track2, EMVTag 0x57: " + StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
								}
								// CSN
								if (emv_is_tag_present(0x5F34) >= 0) {
									tagDataLength = emv_get_tag_data(0x5F34, tagData, tagData.length);
									appState.trans.setCSN(tagData[0]);
                                    Log.e("Fatal Error", "EMVTag 0x5F34: " + tagData[0] );
								}
								// Expiry
								if (emv_is_tag_present(0x5F24) >= 0) {
									tagDataLength = emv_get_tag_data(0x5F24, tagData, tagData.length);
									appState.trans.setExpiry(StringUtil.toHexString(tagData, 0, 3, false).substring(0, 4));
                                    //Log.i("information", "EMVTag 0x5F24: " + StringUtil.toHexString(tagData, 0, 3, false).substring(0, 4));
								}
								//confirmCard();
								mEMVProcessNextThread = new EMVProcessNextThread();
								mEMVProcessNextThread.start();

								break;
							case EMV_DATA_AUTH:
								byte[] TSI = new byte[2];
								byte[] TVR = new byte[5];
								emv_get_tag_data(0x9B, TSI, 2); // TSI
								emv_get_tag_data(0x95, TVR, 5); // TVR

								Log.d(TAG, "handleMessageSafe TVR "+ CipherHM.bytesToHex(TSI)+" "+CipherHM.bytesToHex(TVR));
								if ((TSI[0] & (byte) 0x80) == (byte) 0x80
										&& (TVR[0] & (byte) 0x40) == (byte) 0x00
										&& (TVR[0] & (byte) 0x08) == (byte) 0x00
										&& (TVR[0] & (byte) 0x04) == (byte) 0x00
								) {
									appState.promptOfflineDataAuthSucc = true;
								}
								mEMVProcessNextThread = new EMVProcessNextThread();
								mEMVProcessNextThread.start();
								break;
							case EMV_OFFLINE_PIN:
								Log.e("Error", "EMV_OFFLINE_PIN< ");
								set_display_language(1);
								if(appState.pinpadType == PINPAD_CUSTOM_UI)
								{
									inputPIN();
								}
								else
								{
								    textTransType.setText("Favor de introducir Firma Electronica");
									mEMVProcessNextThread = new EMVProcessNextThread();
									mEMVProcessNextThread.start();
								}
								break;
							case EMV_ONLINE_ENC_PIN:
								Log.e("Error", "EMV_ONLINE_ENC_PIN ");
								inputPIN();
								break;
							case ConstantYLP.EMV_PIN_BYPASS_CONFIRM:
								Log.e("Error", "EMV_PIN_BYPASS_CONFIRM");
								confirmBypassPin();
								break;
							case EMV_PROCESS_ONLINE:

                                if(appState.cardType == CARD_CONTACTLESS) {
                                    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.nfc_success);
                                    mediaPlayer.start();
                                }


								byte[] TSI2 = new byte[2];
								byte[] TVR2 = new byte[5];
								byte[] IAC = new byte[5];
								byte[] COUNTER = new byte[1];
								byte[] CONFIG = new byte[3];
								byte[] CONFIG2 = new byte[5];

								emv_get_tag_data(0x9F17, COUNTER, 1);
								emv_get_tag_data(0x9B, TSI2, 2); // TSI
								emv_get_tag_data(0x95, TVR2, 5); // TVR
								emv_get_tag_data(0x9F0F, IAC, 5); // IAC
								emv_get_tag_data(0x9F33, CONFIG, 3); // TVR
								emv_get_tag_data(0x9F40, CONFIG2, 5); // IAC
								Log.d(TAG, "TAGS emv_offlinepin_verified:"+emv_offlinepin_verified()+" 0x9B:"+ CipherHM.bytesToHex(TSI2)+" 0x95:"+CipherHM.bytesToHex(TVR2)+" 0x9F0F:"+CipherHM.bytesToHex(IAC));
								Log.d(TAG, "TAGS emv_get_offlinepin_times:"+emv_get_offlinepin_times()+" 0x9F17:"+CipherHM.bytesToHex(COUNTER)+" 0x9F33:"+CipherHM.bytesToHex(CONFIG)+" 0x9F40:"+CipherHM.bytesToHex(CONFIG2));
								if(appState.cardType == CARD_CONTACT  &&
										(	(TVR2[2]&(byte)0x08)!=(byte)0x00  ||
											(TVR2[2]&(byte)0x10)!=(byte)0x00 ||
												(
														(TVR2[2]&(byte)0x20)!=(byte)0x00 && (IAC[2]&(byte)0x20)==(byte)0x00
												)
										)
								){

                                    Log.d(TAG, "Error PIN");

									if((TVR2[2]&(byte)0x08)!=(byte)0x00 ){
										appState.setErrorCode(R.string.error_pin_not_entered);
									}
									if((TVR2[2]&(byte)0x10)!=(byte)0x00 ){
										appState.setErrorCode(R.string.error_pinpad);
									}
									if((TVR2[2]&(byte)0x20)!=(byte)0x00 ){
										appState.setErrorCode(R.string.error_pin_try_exceeded);
									}
									/*String errorMsg="";

									    if((TVR2[2]&(byte)0x08)!=(byte)0x00 ){
											errorMsg="PIN requerido, pad presentado, pero el pin no fue introducido";
										}
										if((TVR2[2]&(byte)0x16)!=(byte)0x00 ){
											errorMsg="Error en pinpad";
										}
										if((TVR2[2]&(byte)0x32)!=(byte)0x00 ){
											errorMsg="Intentos PIN excedidos";
										}*/
									//(TVR2[2]&(byte)0x08)!=(byte)0x00  || (TVR2[2]&(byte)0x16)!=(byte)0x00 || (TVR2[2]&(byte)0x32)!=(byte)0x00
									exit();
									/*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessEMVCardActivity.this);
									alertDialogBuilder
											.setMessage(errorMsg)
											.setCancelable(false)
											.setPositiveButton("OK", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													//cancelAllCard();
													exit();
												}
											})
											.create().show();*/

								}else {
									Log.e(TAG, "EMV_PROCESS_ONLINE" + emv_offlinepin_verified());
									if (isConnected()) {
										getEMVCardInfo();
										appState.trans.setEMVOnlineFlag(true);
										cancelIdleTimer();
										Intent intent = new Intent(this, ProcessOnlineActivity.class);
										intent.putExtra("Online", (ArrayList<Product>) listproductEMV);
										startActivityForResult(intent, STATE_PROCESS_ONLINE);
									} else {
										exit();

										/*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessEMVCardActivity.this);
										// Configura el titulo.
										alertDialogBuilder.setTitle("¡Sin acceso a internet!");

										// Configura el mensaje.
										alertDialogBuilder
												.setMessage("¡Favor de verificar la terminal y dar click en continuar!")
												.setCancelable(false)
												.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int id) {
														Internet = "Errorcon";
														getEMVCardInfo();
														Log.e("Error", "Aqui hay que  enviar los productos");
														cancelIdleTimer();
														Intent intent = new Intent(ProcessEMVCardActivity.this, ProcessOnlineActivity.class);
														intent.putExtra("Online", (ArrayList<Product>) listproductEMV);
														startActivityForResult(intent, STATE_PROCESS_ONLINE);
													}
												})
												.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int id) {

														exit();

													}
												}).create().show();*/
									}
								}
								break;
							default:
								mEMVProcessNextThread = new EMVProcessNextThread();
								mEMVProcessNextThread.start();
								break;
						}
						break;
					case STATUS_COMPLETION:
						appState.terminalConfig.incTrace();
						appState.trans.setNeedSignature(emv_is_need_signature());
						//Log.e(TAG, "getEMVCardInfo emvStatus = " + appState.trans.getEMVStatus() + ", emvRetCode = " + appState.trans.getEMVRetCode()+" OLR:"+appState.trans.getEMVOnlineResult());
						tagData = new byte[50];
						if (emv_is_tag_present(0x95) >= 0) {
							tagDataLength = emv_get_tag_data(0x95, tagData, tagData.length);
							appState.terminalConfig.setLastTVR(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                            //Log.i("information", "EMVTag 0x95: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
						}

						if (emv_is_tag_present(0x9B) >= 0) {
							tagDataLength = emv_get_tag_data(0x9B, tagData, tagData.length);
							appState.terminalConfig.setLastTSI(StringUtil.toHexString(tagData, 0, tagDataLength, false));
                            //Log.i("information", "EMVTag 0x9B: " + StringUtil.toHexString(tagData, 0, tagDataLength, false));
						}

						getEMVCardInfo();
					//	Log.e(TAG, "getEMVCardInfo emvStatus = " + appState.trans.getEMVStatus() + ", emvRetCode = " + appState.trans.getEMVRetCode()+" OLR:"+appState.trans.getEMVOnlineResult());

						Log.d(TAG, "handleMessageSafe: "+appState.trans.getEMVOnlineResult()+" tt:"+(TransDefine.transInfo[appState.getTranType()].flag & T_NOCAPTURE)+" RC"+appState.trans.getEMVRetCode());
						if ((TransDefine.transInfo[appState.getTranType()].flag & T_NOCAPTURE) == 0) {
							if (appState.trans.getEMVRetCode() == APPROVE_OFFLINE) {
								if (appState.terminalConfig.getUploadType() == 0) {
									if (appState.trans.getEMVOnlineFlag() == true
											&& appState.trans.getEMVOnlineResult() == ONLINE_FAIL
									) {
										saveAdvice();
									}
									offlineSuccess();
								} else {
									if (appState.trans.getEMVOnlineFlag() == true
											&& appState.trans.getEMVOnlineResult() == ONLINE_FAIL
									) {
										// Reversal
										appState.setProcessState(PROCESS_REVERSAL);
										Log.d(TAG, "PROCESS_REVERSAL 3");
										//processOnline();
									} else {
										// Confirm
										appState.setProcessState(PROCESS_CONFIMATION);
										//getEMVCardInfo();
										//processOnline();
									}
									return;
								}
								setResult(RESULT_OK, getIntent());
								finish();
							} else if (appState.trans.getEMVRetCode() == APPROVE_ONLINE) {
								Log.d(TAG, "getUploadType: "+appState.terminalConfig.getUploadType());
								if (appState.terminalConfig.getUploadType() == 0) {
									transSuccess();
								} else {
									appState.setProcessState(PROCESS_CONFIMATION);
									getEMVCardInfo();
									//processOnline();
									return;
								}
								setResult(RESULT_OK, getIntent());
								exit();
							}/*else if(appState.trans.getEMVOnlineResult() != ONLINE_FAIL && appState.trans.getEMVRetCode() == DECLINE_OFFLINE){
								cancelIdleTimer();
								appState.trans.setEmvCardError(true);

								byte[] TSI2 = new byte[2];
								byte[] TVR2 = new byte[5];

								emv_get_tag_data(0x9B, TSI2, 2); // TSI
								emv_get_tag_data(0x95, TVR2, 5); // TVR

								appState.setErrorCode(R.string.error_declinada_offline);

								Log.d(TAG, "DECLINE_OFFLINE :"+emv_offlinepin_verified()+" "+ CipherHM.bytesToHex(TSI2)+" "+CipherHM.bytesToHex(TVR2)+" RES:"+getResources().getString(appState.getErrorCode()));

								if((TVR2[2]&(byte)0x08)!=(byte)0x00 ){
									appState.setErrorCode(R.string.error_pin_not_entered);
								}
								if((TVR2[2]&(byte)0x16)!=(byte)0x00 ){
									appState.setErrorCode(R.string.error_pinpad);
								}
								if((TVR2[2]&(byte)0x32)!=(byte)0x00 ){
									appState.setErrorCode(R.string.error_pin_try_exceeded);
								}

								Log.d(TAG, "DECLINE_OFFLINE :"+emv_offlinepin_verified()+" "+ CipherHM.bytesToHex(TSI2)+" "+CipherHM.bytesToHex(TVR2)+" RES:"+getResources().getString(appState.getErrorCode()));

								setResult(RESULT_CANCELED, getIntent());
								finish();
								return;
							/*	AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder4.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder4
										.setMessage("¡Declinada fuera de línea!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(RESULT_CANCELED, getIntent());
												exit();
											}
										}).create().show();* /

							}*/else {

								Log.d(TAG, "COMPLETE ELSE :"+appState.trans.getEMVOnlineFlag()+" "+appState.trans.getEMVOnlineResult()+" ");

								if (appState.trans.getEMVOnlineFlag() == true
										&& appState.trans.getEMVOnlineResult() == ONLINE_FAIL
								) {
									Log.d(TAG, "getUploadType: "+appState.terminalConfig.getUploadType());
									// 通讯失败
									if (appState.terminalConfig.getUploadType() == 0) {
										Log.d(TAG, "PROCESS_REVERSAL 11");
										saveAdvice();
									} else {
										appState.setProcessState(PROCESS_REVERSAL);
										getEMVCardInfo();
										Log.d(TAG, "PROCESS_REVERSAL 1");
										//processOnline();
										return;
									}
								} else if (appState.trans.getEMVOnlineFlag() == true
										&& appState.trans.getEMVOnlineResult() == ONLINE_SUCCESS
								) {
									if (emv_is_need_advice() == 1) {
										if (appState.terminalConfig.getUploadType() == 0) {
											saveAdvice();
										} else {
											appState.setProcessState(PROCESS_ADVICE_ONLINE);
											getEMVCardInfo();
											Log.d(TAG, "PROCESS_ADVICE_ONLINE");
											//processOnline();
											return;
										}
									} else {
										if (appState.terminalConfig.getUploadType() == 0) {
											saveAdvice();
										} else {
											appState.setProcessState(PROCESS_REVERSAL);
											getEMVCardInfo();
											Log.d(TAG, "PROCESS_REVERSAL 2");
											//processOnline();
											return;
										}
									}
								} else {

									Log.d(TAG, "COMPLETE ELSE ELSE:"+appState.trans.getEMVRetCode() + " "+emv_is_need_advice() );

									if (emv_is_need_advice() == 1) {
										if (appState.terminalConfig.getUploadType() == 0) {
											saveAdvice();
											Log.d(TAG, "PROCESS_ADVICE_REVERSAL");
										} else {
											appState.setProcessState(PROCESS_ADVICE_ONLINE);
											getEMVCardInfo();
											Log.d(TAG, "PROCESS_ADVICE_REVERSAL");
											//processOnline();
											return;
										}
									}else if(appState.trans.getEMVRetCode() == DECLINE_OFFLINE){
										byte[] TSI2 = new byte[2];
										byte[] TVR2 = new byte[5];
										byte[] IAC = new byte[5];

										emv_get_tag_data(0x9B, TSI2, 2); // TSI
										emv_get_tag_data(0x95, TVR2, 5); // TVR
										emv_get_tag_data(0x9F0F, IAC, 5); // IAC

										appState.setErrorCode(R.string.error_declinada_offline);
										Log.d(TAG, "DECLINE_OFFLINE TVR:"+emv_offlinepin_verified()+" "+ CipherHM.bytesToHex(TSI2)+" "+CipherHM.bytesToHex(TVR2)+" "+CipherHM.bytesToHex(IAC)+" RES:"+getResources().getString(appState.getErrorCode()));



										if((TVR2[2]&(byte)0x08)!=(byte)0x00 ){
											appState.setErrorCode(R.string.error_pin_not_entered);
										}
										if((TVR2[2]&(byte)0x10)!=(byte)0x00 ){
											appState.setErrorCode(R.string.error_pinpad);
										}
										if((TVR2[2]&(byte)0x20)!=(byte)0x00 ){
											appState.setErrorCode(R.string.error_pin_try_exceeded);
										}

									}
								}
							}
							appState.setProcessState(PROCESS_NORMAL);
							Log.d(TAG, "PROCESS_NORMAL");
						}
						setResult(RESULT_OK, getIntent());
						finish();
						break;
					default:
						switch (appState.trans.getEMVRetCode()) {
    					case ERROR_NO_APP:
							AlertDialog.Builder alertDialogBuilder3 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
							// Configura el titulo.
							alertDialogBuilder3.setTitle("¡Error!");
							// Configura el mensaje.
							alertDialogBuilder3
									.setMessage("Solicitud no aceptada")
									.setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {

											setResult(0);
											exit();
										}
									}).create().show();
							break;
    					case ERROR_INIT_APP:
							appState.setErrorCode(R.string.error_no_app);
							AlertDialog.Builder alertDialogBuilder33 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
							// Configura el titulo.
							alertDialogBuilder33.setTitle("¡Error!");
							// Configura el mensaje.
							alertDialogBuilder33
									.setMessage("¡Error de Init EMV App!")
									.setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {

											setResult(0);
											exit();
										}
									}).create().show();
							break;
//    						//appState.trans.setEmvCardError(true);
//    						//setResult(RESULT_OK, getIntent());
//    						appState.setErrorCode(R.string.error_no_app);
//    						finish();
//    						break;
						case ERROR_OTHER_CARD:
								appState.trans.setEmvCardError(true);
								//setResult(RESULT_OK, getIntent());
								appState.setErrorCode(R.string.error_other_card);
								AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder4.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder4
										.setMessage("¡Error en la tarjeta!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												setResult(0);
												exit();
											}
										}).create().show();
								break;
						case ERROR_EXPIRED_CARD:
								appState.setErrorCode(R.string.error_expiry_card);
								setResult(0);
								exit();
								/*AlertDialog.Builder alertDialogBuilder6 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder6.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder6
										.setMessage("¡La tarjeta ya expiró!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(0);
												exit();
											}
										}).create().show();*/
								break;
						case ERROR_CARD_BLOCKED:
								appState.setErrorCode(R.string.error_card_blocked);
								setResult(0);
								exit();
								/*AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder2.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder2
										.setMessage("¡Tarjeta Bloqueada!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(0);
												exit();
											}
										}).create().show();*/
								break;
						case ERROR_APP_BLOCKED:
								appState.setErrorCode(R.string.error_app_blocked);
								setResult(0);
								exit();
								/*AlertDialog.Builder alertDialogBuilder11 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder11.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder11
										.setMessage("¡Applicación EMV bloqueada!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(0);
												exit();
											}
										}).create().show();*/
								break;
						case ERROR_SERVICE_NOT_ALLOWED:
								appState.setErrorCode(R.string.error_not_accepted);
								setResult(0);
								exit();
								/*AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder1.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder1
										.setMessage("¡Tarjeta no aceptada!")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(0);
												exit();
											}
										}).create().show();*/
								break;
						case ERROR_PINENTERY_TIMEOUT:
								appState.setErrorCode(R.string.error_pin_timeout);
							    setResult(0);
							    exit();
								/*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder
										.setMessage("¡Tiempo de espera agotado !")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {

												setResult(0);
												exit();
											}
										}).create().show();*/
								break;

						case ERROR_OFFLINE_VERIFY:
							appState.setErrorCode(R.string.error_incorrect_pin);
							setResult(0);
							exit();

								/*alertDialogBuilder = new AlertDialog.Builder(ProcessEMVCardActivity.this);
								// Configura el titulo.
								alertDialogBuilder.setTitle("¡Error!");
								// Configura el mensaje.
								alertDialogBuilder
										.setMessage("No se pudo verificar el PIN")
										.setCancelable(false)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {//¡PIN INCORRECTO!")
											public void onClick(DialogInterface dialog, int id) {
												setResult(0);
												exit();
											}
										}).create().show();*/
								break;
						case ERROR_CONTACT_DURING_CONTACTLESS:
								//contact card present during contactless transaction, process contact transaction in conditions.
								cancelContactlessCard();
								appState.trans.setEmvCardError(false);
								appState.trans.setCardEntryMode(INSERT_ENTRY);
								appState.cardType = CARD_CONTACT;
								if (open_reader_ex(1, 1) < 0) {
									appState.setErrorCode(R.string.error_ic);
									exit();
								} else {
									new EMVProcessNextThread().start();
								}
								break;
						case ERROR_PROCESS_CMD:

								appState.setErrorCode(R.string.error_read_ic);
								exit();
								break;
						default:
								switch(appState.trans.getEMVRetCode()){
									case ERROR_SEE_PHONE:
										appState.setErrorCode(R.string.error_see_phone);
										break;
									case ERROR_POWER_ON_AGAIN:
										appState.setErrorCode(R.string.error_read_ic);
										break;
									case ERROR_ANOTHER_INTERFACE:
										appState.setErrorCode(R.string.error_aother_interface);
										break;
								}
								//try {
									Log.e("ERROR", "llega aqui y termina la aplicación: " +appState.trans.getEMVRetCode());
								//}catch(Exception e){}
								setResult(RESULT_CANCELED);
								exit();
								break;
						}
						break;
				}
				break;
			case PREPROCESS_ERROR_NOTIFIER:
				if (appState.getErrorCode() == 0)
					appState.setErrorCode(R.string.error_pre_process);
				Log.e("Error", "se saco la tarjeta" + appState.getErrorCode());
				exit();
				break;
        /*case CARD_CONTACTLESS_ANTISHAKE:
            //wait seconds to check if MSR come
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (appState.msrPollResult == -1) {
                        emv_anti_shake_finish(0);
                    } else {
                        emv_anti_shake_finish(1);
                    }
                }
            }).start();
            break;*/

			case CARD_INSERT_NOTIFIER:
				Log.i(TAG, "cardInserted");
				cancelMSRThread();
				emv_anti_shake_finish(1);
				cancelContactlessCard();

				appState.trans.setEmvCardError(false);
				appState.trans.setCardEntryMode(INSERT_ENTRY);
				new EMVThread().start();

				break;
			case CARD_CONTACTLESS_ANTISHAKE:
				Log.i(TAG, "cardTaped");

				appState.trans.setEmvCardError(false);
				appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);

				new EMVThread().start();
				break;
			case CARD_TAPED_NOTIFIER:
				Log.i(TAG, "cardTaped");

				appState.trans.setEmvCardError(false);
				appState.trans.setCardEntryMode(CONTACTLESS_ENTRY);

				new EMVThread().start();
				break;

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e(TAG,"onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_process_emv_card);

		Serializable bundle1 = getIntent().getSerializableExtra("EMV");
		listproductEMV = (ArrayList<Product>) bundle1;
		Double total=0d;
		if(bundle1 != null){
			for (int i = 0; i < listproductEMV.size(); i++) {
				/*String data1 = listproductEMV.get(i).getProducto();
				String data2 = listproductEMV.get(i).getCantidad();
				Integer c = Integer.parseInt(data2);
				Double data3 = listproductEMV.get(i).getPrecio();
				Double dou = Double.parseDouble(data3);
				//Log.i("Information EMV", "Regresa : " + data1 + ", " + c + ", " + data3);
				appState.trans.setProductoTicket(data1);*/
				total+= listproductEMV.get(i).getTotal();
			}

		}
		montoTransac=(TextView) findViewById(R.id.monto);
		//TipoPago = (TextView) findViewById(R.id.tipoPago);
		//tipoPagoC = (TextView) findViewById(R.id.tipoPagoC);

		montoTransac.setText(DECIMAL_FORMAT.format(total));

		Bundle bundle = getIntent().getExtras();

		String tipopago="";

		if(appState.trans.getCardEntryMode() == Constant.CONTACTLESS_ENTRY) {
			tipopago="Pago con Contactless";
		}else if(appState.trans.getCardEntryMode() == Constant.INSERT_ENTRY){
			tipopago="Pago con Tarjeta";
		}else if(appState.trans.getCardEntryMode() == Constant.SWIPE_ENTRY){
			tipopago="Pago con Banda";
		}
//        // title
//        textTitle = (TextView)findViewById(R.id.tAppTitle);
//		textTitle.setText(appState.getString(TransDefine.transInfo[appState.getTranType()].id_display_en));
//
//	    buttonBack = (Button)findViewById(R.id.btn_back);
//        buttonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
//
//        buttonMore = (Button)findViewById(R.id.btn_more);
//        buttonMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blank));
//
		textTransType = (TextView) findViewById(R.id.tProcessEMVCard_TransType);
		//textTransType.setText(TransDefine.transInfo[appState.getTranType()].id_display_en);

		textLine1 = (TextView) findViewById(R.id.tProcessEMVCard_Line1);

		if(appState.cardType == CARD_CONTACTLESS) {
			textTransType.setText("Procesando contactless");
		}else {
			textTransType.setText("Por favor, no retires la tarjeta");
		}

		mHandler.setFunActivity(this);
		mEMVThread = new EMVThread();
		mEMVThread.start();

		LEDDeviceHM.get().onLED2();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("Error","onStart");
		appState.theActivity = this;
		if(appState.cardType == CARD_CONTACTLESS) {
			textTransType.setText("Procesando contactless");
		}else {
			textTransType.setText("Por favor, no retires la tarjeta");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult( requestCode,  resultCode,  data);
		//super.onActivityResult( requestCode,  resultCode,  data);

		Log.d(TAG, "onActivityResult: "+requestCode+" result:"+resultCode);
		//if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY){
		//	appState.trans.setNeedSignature(0);
		//}
		/*if (requestCode == STATE_PROCESS_ONLINE) {
			if (resultCode == Activity.RESULT_OK) {


				if(appState.trans.getCardEntryMode() == CONTACTLESS_ENTRY){
					appState.trans.setNeedSignature(0);
				}

				if(appState.trans.getIssuerAuthData()!=null && appState.trans.getIssuerAuthData().length>0) {
					mEMVProcessNextThread = new EMVProcessNextThread();
					mEMVProcessNextThread.start();
				}else {

					if (appState.trans.getNeedSignature() == 1) {
						Intent intent = new Intent(this, FirmaActivity.class);
						intent.putExtra("ResultCompra", "Hecho");
						startActivityForResult(intent, STATE_NEED_SING);
					} else {
						setResult(Activity.RESULT_OK, getIntent());
						Intent intent = new Intent(this, TransResultActivity.class);
						//intent.putExtra("ResultCompra", "Hecho");
						startActivityForResult(intent, STATE_TRANS_END);

					}
				}
			}else if(resultCode == Activity.RESULT_CANCELED){
				Intent intent = new Intent(this, TransResultActivity.class);
				intent.putExtra("imprimir", "error");
				//intent.putExtra("ResultCompra", "Error");
				startActivityForResult(intent,STATE_TRANS_END);
			}
		}else if(requestCode == STATE_SELECT_EMV_APP) {
			mEMVProcessNextThread = new EMVProcessNextThread();
			mEMVProcessNextThread.start();
		}else if(requestCode == STATE_NEED_SING) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, getIntent());
				Intent intent = new Intent(this, TransResultActivity.class);
				intent.putExtra("ResultCompra", "Hecho");
				startActivityForResult(intent,STATE_TRANS_END);
			}else{
				setResult(Activity.RESULT_CANCELED, getIntent());
				exit();
			}
		}else if(requestCode == STATE_TRANS_END) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(resultCode, getIntent());

			}else{
				setResult(resultCode, getIntent());

			}

			exit();
		}*/
		if (debug)
			Log.d(APP_TAG, "ProcessEMVCard onActivityResult, requesCode=" + requestCode + ",resultCode=" + resultCode+" PE:"+appState.getProcessState());
		if (appState.getProcessState() != PROCESS_NORMAL) {
			if (appState.getProcessState() == PROCESS_REVERSAL
					&& appState.trans.getEMVRetCode() == APPROVE_OFFLINE
			) {
				appState.setProcessState(PROCESS_CONFIMATION);
				getEMVCardInfo();
				Log.d(TAG, "PROCESS_CONFIMATION");
				//processOnline();
			} else if (emv_is_need_advice() == 1
					&& appState.getProcessState() != PROCESS_ADVICE_ONLINE
			) {
				appState.setProcessState(PROCESS_ADVICE_ONLINE);
				getEMVCardInfo();
				Log.d(TAG, "PROCESS_ADVICE_ONLINE");
				//processOnline();
			} else {
				appState.setProcessState(PROCESS_NORMAL);
				setResult(Activity.RESULT_OK, getIntent());
				exit();
			}
			return;
		} else {
			if (appState.getErrorCode() > 0) {
				if (requestCode == STATE_PROCESS_ONLINE) {
					Log.d(TAG, "STATE_PROCESS_ONLINE ERROR ONLINE");
					appState.trans.setEMVOnlineResult(ONLINE_FAIL);
					emv_set_online_result(appState.trans.getEMVOnlineResult(), appState.trans.getResponseCode(), new byte[]{' '}, 0);
					mEMVProcessNextThread = new EMVProcessNextThread();
					mEMVProcessNextThread.start();
					return;
				}
				exit();
				return;
			}
			if (resultCode != Activity.RESULT_OK) {

				if (requestCode == STATE_PROCESS_ONLINE) {
					appState.trans.setEMVOnlineResult(ONLINE_FAIL);
					Log.d(TAG, "onActivityResult:STATE_PROCESS_ONLINE ONLINE_FAIL "+CipherHM.bytesToHex(appState.trans.getResponseCode()));
					//appState.trans.getResponseCode()
					emv_set_online_result(ONLINE_FAIL, appState.trans.getResponseCode(), new byte[]{' '}, 0);
					mEMVProcessNextThread = new EMVProcessNextThread();
					mEMVProcessNextThread.start();

					return;
				}
				setResult(resultCode, getIntent());
				exit();
				return;
			}
			if (requestCode == STATE_INPUT_AMOUNT) {
				setEMVTransAmount(Integer.toString((int) appState.trans.getTransAmount()));
				emv_set_other_amount(new byte[]{'0', 0x00});
			} else if (requestCode == STATE_INPUT_ONLINE_PIN) {
				if (appState.trans.getPinEntryMode() == CAN_PIN) {
					emv_set_online_pin_entered(1);
				} else {
					emv_set_online_pin_entered(0);
				}
			}
			else if(requestCode == STATE_INPUT_OFFLINE_PIN)
			{
                byte[] CONTER = new byte[1];
                emv_get_tag_data(0x9F17, CONTER, 1); // IAC
				Log.d(TAG, "onActivityResult: run emvProcessCompleted ErrCode:"+appState.getErrorCode()+" EretCode:"+appState.trans.getEMVRetCode()+" EMVSta:"+appState.trans.getEMVStatus());
				Log.d(TAG, "onActivityResult: run emvProcessCompleted emv_offlinepin_verified:"+emv_offlinepin_verified());
				Log.d(TAG, "onActivityResult: run emvProcessCompleted emv_get_offlinepin_times:"+emv_get_offlinepin_times());
                Log.d(TAG, "onActivityResult: run emvProcessCompleted CONTER:"+CipherHM.bytesToHex(CONTER));

				mHandler.setFunActivity(this);
				/*if(emv_offlinepin_verified()==-1 ){
					inputPIN();
				}else{
				*/	Message msg= new Message();
					msg.what=EMV_PROCESS_NEXT_COMPLETED_NOTIFIER;
					handleMessageSafe(msg);
				//}
				/*mEMVProcessNextThread = new EMVProcessNextThread();
				mEMVProcessNextThread.start();*/
				return;
			}
		}
		mEMVProcessNextThread = new EMVProcessNextThread();
		mEMVProcessNextThread.start();
	}

//	private void setIccRevData()
//	{
//		int offset = 0;
//		byte[] tagData = new byte[50];
//		byte[] iccData = new byte[100];
//		int tagDataLength = 0;
//
//		if( emv_is_tag_present(0x9F10) >= 0)
//		{
//			tagDataLength = emv_get_tag_data(0x9F10, tagData, tagData.length);
//			iccData[offset]   = (byte)0x9F;
//			iccData[offset+1] = (byte)0x10;
//			iccData[offset+2] = (byte)tagDataLength;
//			System.arraycopy(tagData, 0, iccData, offset + 3, tagDataLength);
//			offset += (3 + tagDataLength);
//		}
//		if( emv_is_tag_present(0x9F1E) >= 0)
//		{
//			tagDataLength = emv_get_tag_data(0x9F1E, tagData, tagData.length);
//			iccData[offset]   = (byte)0x9F;
//			iccData[offset+1] = (byte)0x1E;
//			iccData[offset+2] = (byte)tagDataLength;
//			System.arraycopy(tagData, 0, iccData, offset + 3, tagDataLength);
//			offset += (3 + tagDataLength);
//		}
//		if(appState.trans.getEMVOnlineResult() == ONLINE_SUCCESS)
//		{
//			if( emv_is_tag_present(0x9F36) >= 0)
//			{
//				tagDataLength = emv_get_tag_data(0x9F36, tagData, tagData.length);
//				iccData[offset]   = (byte)0x9F;
//				iccData[offset+1] = (byte)0x36;
//				iccData[offset+2] = (byte)tagDataLength;
//				System.arraycopy(tagData, 0, iccData, offset + 3, tagDataLength);
//				offset += (3 + tagDataLength);
//			}
//		}
//		appState.trans.setICCRevData(iccData, 0, offset);
//	}

	private void getEMVCardInfo() {
		Log.e("Error","getEMVCardInfo");

		byte[] tagData = new byte[100];
		int tagDataLength = 0;

		byte[] iccData = new byte[1200];
		int offset = 0;
		if (appState.getProcessState() == PROCESS_CONFIMATION) {
			offset = emv_get_tag_list_data(confirmTagList, confirmTagList.length, iccData, iccData.length);
		} else {
			offset = emv_get_tag_list_data(defaultTagList, defaultTagList.length, iccData, iccData.length);
			String MasterCard = "";

			if(CardType.equals("Visa")){
				MasterCard = "";
			}else{
				 MasterCard = "9F530152";
			}
			String ifd = Build.SERIAL.substring(Build.SERIAL.length() - 8);
			String hexaLength=CipherHM.bytesToHex(new byte[]{(byte)ifd.length()});

			Log.e("getEMVCardInfo", ByteUtil.arrayToHexStr(iccData, offset) + "9F0306000000000000" + MasterCard + "9F1E" + hexaLength+CipherHM.bytesToHex(ifd.getBytes()));// "9F1E083236323632363236");//+
			appState.trans.setEMVTags(ByteUtil.arrayToHexStr(iccData, offset)+ "9F0306000000000000" + MasterCard + "9F1E"+hexaLength+CipherHM.bytesToHex(ifd.getBytes()));
		}
		appState.trans.setICCData(iccData, 0, offset);

		// Copia los tags requeridos por Banorte
		for (int tag : banorteTagList) {
			if (emv_is_tag_present(tag) >= 0) {
				tagDataLength = emv_get_tag_data(tag, tagData, tagData.length);
				byte[] tagBytes = new byte[tagDataLength];
				System.arraycopy(tagData, 0, tagBytes, 0, tagBytes.length);
				appState.trans.getBanorteTags().put(tag, StringUtil.toString(tagBytes));
				Log.e("Error", "Tags requeridos por banorte" + appState.trans.getBanorteTags().put(tag, StringUtil.toString(tagBytes)));
			}
		}

		if (emv_is_tag_present(0x5A) >= 0) {
			tagDataLength = emv_get_tag_data(0x5A, tagData, tagData.length);
			appState.trans.setPAN(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
			//Log.i("information", "EMVTag 0x5A: " + ByteUtil. bcdToAscii(tagData));
		}
		if (emv_is_tag_present(0x5F24) >= 0) {
			tagDataLength = emv_get_tag_data(0x5F24, tagData, tagData.length);
			appState.trans.setExpiry(StringUtil.toHexString(tagData, 0, 3, false).substring(0, 4));
			//Log.i("information", "EMVTag 0x5F24: " + StringUtil.toHexString(tagData, 0, 3, false).substring(0, 4));
		}
		appState.trans.setCountName("");
		if (emv_is_tag_present(0x5F20) >= 0) {
			tagDataLength = emv_get_tag_data(0x5F20, tagData, tagData.length);
			byte[] count = new byte[tagDataLength];
			System.arraycopy(tagData, 0, count, 0, count.length);
			appState.trans.setCountName(StringUtil.toString(count));
			//Log.i("information", "EMVTag 0x5F20: " + StringUtil.toString(count));
		}


		// AIP
		if (emv_is_tag_present(0x82) >= 0) {
			tagDataLength = emv_get_tag_data(0x82, tagData, tagData.length);
			appState.trans.setAIP(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		// TVR
		if (emv_is_tag_present(0x95) >= 0) {
			tagDataLength = emv_get_tag_data(0x95, tagData, tagData.length);
			appState.trans.setTVR(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		// TSI
		if (emv_is_tag_present(0x9B) >= 0) {
			tagDataLength = emv_get_tag_data(0x9B, tagData, tagData.length);
			appState.trans.setTSI(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}


		// Application Identifier terminal
		if (emv_is_tag_present(0x9F06) >= 0) {
			tagDataLength = emv_get_tag_data(0x9F06, tagData, tagData.length);
			appState.trans.setAID(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		// IAD
		if (emv_is_tag_present(0x9F10) >= 0) {
			tagDataLength = emv_get_tag_data(0x9F10, tagData, tagData.length);
			appState.trans.setIAD(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		// ApplicationPreferredName  9F12
		if (emv_is_tag_present(0x9F12) >= 0) {
			tagDataLength = emv_get_tag_data(0x9F12, tagData, tagData.length);
			byte[] appName = new byte[tagDataLength];
			System.arraycopy(tagData, 0, appName, 0, appName.length);
			appState.trans.setAppName(StringUtil.toString(appName));
		}

		if (emv_is_tag_present(0x9F26) >= 0) {
			tagDataLength = emv_get_tag_data(0x9F26, tagData, tagData.length);
			appState.trans.setAC(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		if (emv_is_tag_present(0x9F37) >= 0) {
			tagDataLength = emv_get_tag_data(0x9F37, tagData, tagData.length);
			appState.trans.setUnpredictableNumber(StringUtil.toHexString(tagData, 0, tagDataLength, false));
		}

		if (emv_is_tag_present(0x9F79) >= 0
				&& appState.trans.getECBalance() < 0
		) {
			tagDataLength = emv_get_tag_data(0x9F79, tagData, tagData.length);
			byte[] amt = new byte[tagDataLength];
			System.arraycopy(tagData, 0, amt, 0, amt.length);
			appState.trans.setECBalance(ByteUtil.bcdToInt(amt));
		}

		if (emv_is_tag_present(0x57) >= 0) {
			tagDataLength = emv_get_tag_data(0x57, tagData, tagData.length);
			appState.trans.setTrack2Data(StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
			//Log.e(TAG, "Track2 PROCESS EMV, EMVTag 0x57: " + StringUtil.toString(AppUtil.removeTailF(ByteUtil.bcdToAscii(tagData, 0, tagDataLength))));
			//appState.trans.setPAN(null);
			if(appState.trans.getPAN()==null || appState.trans.getPAN().equals("")){
				appState.trans.setPAN(appState.trans.getTrack2Data().split("D")[0]);
			//	Log.e(TAG, "PAN BY TRACK2 : "+appState.trans.getPAN());
			}
		}

		appState.trans.setNeedSignature(emv_is_need_signature());
	}

	public static void setEMVData() {
		Log.e("Error", "setEMVData" );
		if (appState.getTranType() == QUERY_CARD_RECORD) {
			emv_set_trans_amount(new byte[]{'0', 0x00});
			emv_set_other_amount(new byte[]{'0', 0x00});
			if (appState.recordType == 0x00) {
				emv_set_trans_type(EMV_TRANS_CARD_RECORD);
			} else {
				emv_set_trans_type(EMV_TRANS_LOAD_RECORD);
			}
		} else {
			emv_set_tag_data(0x9A, StringUtil.hexString2bytes(appState.trans.getTransDate().substring(2)), 3);
			emv_set_tag_data(0x9F21, StringUtil.hexString2bytes(appState.trans.getTransTime()), 3);
			emv_set_tag_data(0x9F41, StringUtil.hexString2bytes(StringUtil.fillZero(Integer.toString(appState.trans.getTrace()), 8)), 4);
			Log.e("Error", "emv_set_tag_data" );
			emv_set_trans_type(EMV_TRANS_GOODS_SERVICE);
		}
	}

	class EMVThread extends Thread {
		public void run() {
			super.run();
			emv_trans_initialize();
			emv_set_kernel_type(appState.trans.getEMVKernelType());
			setEMVTransAmount(Integer.toString((int) appState.trans.getTransAmount()));
			setEMVData();

			//pre-process
			if (appState.trans.getEMVKernelType() == CONTACTLESS_EMV_KERNAL && !preProcessQpboc()) {
				Message msg = new Message();
				msg.what = PREPROCESS_ERROR_NOTIFIER;
				mHandler.sendMessage(msg);
				return;
			}
			Log.e("Error", "emv_process_next" );
			emv_process_next();
		}
	}

	class EMVProcessNextThread extends Thread {
		public void run() {
			super.run();
			Log.e("Error", "EMVProcessNextThread run emv_process_next" );
			emv_process_next();
		}
	}

	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager)ProcessEMVCardActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
	}
	public static String Error(){
		return Internet;
	}

	public static String Track2(){
		String Track2 = appState.trans.getTrack2Data();
		return Track2;
	}
	public static String EmvTags(){
		String EMVTags = appState.trans.getEMVTags();
		return EMVTags;
	}


}