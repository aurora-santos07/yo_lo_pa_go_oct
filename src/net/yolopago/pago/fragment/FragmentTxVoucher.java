package net.yolopago.pago.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.cancelacionPrint;
import com.wizarpos.emvsample.constant.ConstantYLP;
import com.wizarpos.emvsample.databinding.VoucherDetailBinding;

import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.utilities.PrinterHM;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TxDetailViewModel;
import net.yolopago.pago.ws.dto.payment.PaymentDto;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentTxVoucher extends AbstractFragment  {
	private static final String TAG = "DetailProductFragment";
	private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	public TxDto txDto;
	private VoucherDetailBinding binding;
	private Product product;
	private int indexProduct;
	private static PaymentViewModel paymentViewModel;
	private static TxDetailViewModel txDetailViewModel;
	private static ContractViewModel contractViewModel;
	private PaymentDto paymentDto;
	protected static MainApp appState = null;
	private String dirMerchant="";


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.tx_detail_voucher, container, false);
		return binding.getRoot();
	}


	@SuppressLint("RestrictedApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		appState = ((MainApp)getActivity().getApplicationContext());
		paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
		txDetailViewModel = new TxDetailViewModel(MainApp.getApplicationContext(getActivity()));
		contractViewModel = ViewModelProviders.of(this).get(ContractViewModel.class);

        getPaymentDto();
		getMerchant(txDto.getMerchantDto().getId());

		binding.monto.setText("$"+DECIMAL_FORMAT.format(txDto.getAmount()));
		if(!txDto.getPaymentStatus().equals("Aprobada")) {
			binding.monto.setTextColor(Color.parseColor("#e5605e"));

		}
		binding.fieldReferenciaOriginal.setVisibility(View.INVISIBLE);

		binding.status.setText(""+txDto.getPaymentStatus());
		binding.fechaHora.setText(DATE_FORMAT.format(txDto.getPayedDate())+" "+TIME_FORMAT.format(txDto.getPayedDate()));
		binding.tarjeta.setText(""+txDto.getIssuingBank()+" "+txDto.getCardType()+" ****"+txDto.getMaskedPAN());

		binding.folio.setText(""+txDto.getId());
		binding.comercio.setText(txDto.getMerchantDto().getName());
		binding.terminal.setText(txDto.getTerminalDto().getSerialNumber());

		if(payedToday(txDto) && txDto.getPaymentStatus().equals("Aprobada")) {
			binding.btnCancelar.setVisibility(View.VISIBLE);
		}else {
			binding.btnCancelar.setVisibility(View.INVISIBLE);
		}

		if(null !=txDto.getCardBrand()) {
			if (txDto.getCardBrand().equalsIgnoreCase("visa")) {
				binding.imgBrandCard.setImageResource(R.drawable.ylp_ico_visa);
			} else if (txDto.getCardBrand().equalsIgnoreCase("mastercard")) {
				binding.imgBrandCard.setImageResource(R.drawable.ylp_ico_master);
			} else if (txDto.getCardBrand().equalsIgnoreCase("amex")) {
				binding.imgBrandCard.setImageResource(R.drawable.ic_amex);
			} else {
				binding.imgBrandCard.setImageResource(R.drawable.ylp_generl_card);
			}
		}else{
			binding.imgBrandCard.setImageResource(R.drawable.ylp_generl_card);
		}

		binding.btnCancelar.setOnClickListener(view -> cancelarTransac());
		binding.btnReimprimir.setOnClickListener(view -> reimprimirVoucher());
		binding.btnBack.setOnClickListener(view -> presentFragmentTx());

		binding.btnTicket.setOnClickListener(view -> showTicket(view));

	}

	private void reimprimirVoucher() {
		/*int tipo=ConstantYLP.RECIBO_CANCELADO;

		PrinterHM.getInstance(this.getResources()).print(appState,tipo,true,false);*/
		PrinterHM printerHM=PrinterHM.getInstance(getContext());

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
		alertDialogBuilder
				.setMessage("¿Desea imprimir el voucher?"+paymentDto.getPaymentStatus())
				.setCancelable(true)
				.setNegativeButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(paymentDto.getVoucherFile()!=null && !paymentDto.getVoucherFile().isEmpty() ){
							printerHM.printFromData(paymentDto.getVoucherFile());
						}else if(paymentDto.getPaymentStatus().equals("Reversing")) {
							int tipo=ConstantYLP.RECIBO_REVERSADO;
							appState.typePrintRecive=tipo;
							//appState.terminalConfig.setMID();
						//	appState.trans.setTerminalId();
							appState.trans.setControlNumber(paymentDto.getControlNumber());
							appState.trans.setPAN("");
							appState.trans.setCardBrandName(paymentDto.getCardBrand());
						    appState.trans.setBankName(paymentDto.getIssuingBank());
						    appState.trans.setCardTypeName(paymentDto.getCardType());
						    appState.trans.setCardTypeName(paymentDto.getCardName());
							appState.trans.setCodeAut(paymentDto.getCodeAuthorizer());
							appState.trans.setReference(paymentDto.getReference());
							appState.trans.setAID(paymentDto.getAid());
							appState.trans.setTVR(paymentDto.getTvr());
							appState.trans.setTSI(paymentDto.getTsi());
							appState.trans.setNeedSignature(0);
							appState.trans.setCardEntryMode((byte)0x00);
							appState.trans.setPaymentId(paymentDto.getId());


							appState.trans.setTransAmount((new Double(paymentDto.getAmount()*100)).longValue());
							appState.trans.setDir(dirMerchant);
							appState.trans.setMerchantName(txDto.getMerchantDto().getName());
							appState.trans.setTicketId(txDto.getId());
							//appState.terminalConfig.setMID(binding.afiliacion.getText().toString());

							//Log.e("error", "ticket getTransAmount: " +appState.trans.getTransAmount()+" -"+txDto.getAmount());
							appState.trans.setTransDate(new SimpleDateFormat("yyyyMMdd").format(paymentDto.getPayedDate()));
							appState.trans.setTransTime(new SimpleDateFormat("HHmmss").format(paymentDto.getPayedDate()));

							printerHM.print(appState,tipo,true,false);


						}else {
							Toast.makeText(getContext(), "¡Sin datos!", Toast.LENGTH_LONG).show();
						}

					}
				}).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		}).create().show();

	}

	private void getMerchant(Long idMerchant) {

		contractViewModel.getMerchant().observe(this, merchant -> {
			if (merchant!=null) {
				Log.d(TAG, "getMerchant: "+merchant.getName());
				dirMerchant=merchant.getStreet()+" "+merchant.getExternal() +" "+merchant.getInternal();
				appState.trans.setRfc("RFC:"+merchant.getTaxid());
				appState.trans.setMerchantName(merchant.getName());
				appState.trans.setDir(dirMerchant);
				//merchant.getTaxid();
			}
		});



	}


	private void cancelarTransac() {

		if (txDto.getHasChilds().equals(Boolean.FALSE) && payedToday(txDto)) {
			AlertDialog alertDialog = new AlertDialog.Builder(getContext())
					.setTitle("Cancelar")
					.setMessage("¿Quieres Cancelar la transacción? Esta acción, cancelará la operación con el Banco. ")// + DECIMAL_FORMAT.format(txDto.getAmount()) +"?")
					.setIcon(R.drawable.ic_cancel)
					.setPositiveButton("Si", (dialog, whichButton) -> {
						//Toast.makeText(getContext(), "¡No se pudo realizar la cancelación!", Toast.LENGTH_LONG).show();
						//appState= paymentViewModel.paymentRepository.getAppState();
						//appState.trans=new TransDetailInfo();
						//appState.trans.init();
						appState.trans.setAID("");
						appState.trans.setTVR("");
						appState.trans.setTSI("");
						appState.trans.setAppLabel("");
						appState.trans.setTerminalId(Build.SERIAL);
						appState.trans.setTransAmount(((Double)(txDto.getAmount()*100)).longValue());


						LiveData<String> liveData = txDetailViewModel.requestCancel(
								txDto.getId());
						liveData.observe(getActivity(), s -> {
							if (s.equals("OK")) {
								/*binding.cancelImageView.setVisibility(View.INVISIBLE);
								binding.cancelTextView.setVisibility(View.INVISIBLE);
								binding.refoundTextView.setVisibility(View.INVISIBLE);*/
								Toast.makeText(getContext(), "¡Cancelacion realizada exitosamente!", Toast.LENGTH_LONG).show();
								Intent intent = new Intent(getActivity().getApplicationContext(), cancelacionPrint.class);
								startActivityForResult(intent, 0);
							} else {
								AlertDialog alertDialog2 = new AlertDialog.Builder(getContext())
										.setMessage("¡No se pudo realizar la cancelación!")
										.setNegativeButton("OK", (dialog2, which) -> dialog2.dismiss())
										.create();
								alertDialog2.show();
							}
						});

						dialog.dismiss();
					})
					.setNegativeButton("No", (dialog, which) -> dialog.dismiss())
					.create();
			alertDialog.show();
		}


	}

	private boolean payedToday(TxDto txDto) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(MainApp.getCurrentTimeInMillis());
		Calendar calendarPay = Calendar.getInstance();
		calendarPay.setTime(txDto.getPayedDate());

		if (calendar.get(Calendar.YEAR) == calendarPay.get(Calendar.YEAR)
				&& calendar.get(Calendar.MONTH) == calendarPay.get(Calendar.MONTH)
				&& calendar.get(Calendar.DATE) == calendarPay.get(Calendar.DATE)
				&&  calendar.get(Calendar.HOUR_OF_DAY) <= 23
				&& calendar.get(Calendar.MINUTE) <= 59
		) {
			return true;
		} else {
			return false;
		}
	}

	private void presentFragmentTx(){
		TxListFragment fragmentTx = new TxListFragment();
		setFragment(fragmentTx);
	}
	private void showTicket(View view) {
		//TxDto txDto = (TxDto) view.getTag();
		//Log.d(TAG, "showTicket: "+txDto);
		FragmentTxTicket txDetailFragment = new FragmentTxTicket();
		txDetailFragment.txDto = txDto;
		setFragment(txDetailFragment);
	}


	private void getPaymentDto() {

		paymentViewModel.IdPayment(txDto.getId()).observe(this, s -> {
			if (s.equals("OK")) {
				paymentDto = PaymentRepository.paymentDto;
				if(paymentDto!=null) {
					binding.referencia.setText(paymentDto.getReference());
					if (paymentDto.getOriginalReference() != null && !paymentDto.getOriginalReference().isEmpty()) {
						binding.fieldReferenciaOriginal.setVisibility(View.VISIBLE);
						binding.referenciaOriginal.setText(paymentDto.getOriginalReference());
					}

					//Log.e(TAG, "getIdAfiliacion: " + paymentDto.getIdAfiliacion());
					binding.afiliacion.setText(paymentDto.getAfiliacion());
					binding.numeroControl.setText(paymentDto.getControlNumber());
					binding.codigoAutorizacion.setText(paymentDto.getCodeAuthorizer());


					binding.aid.setText(paymentDto.getAid());
					binding.tvr.setText(paymentDto.getTvr());
					binding.tsi.setText(paymentDto.getTsi());
					binding.apn.setText(paymentDto.getApn());
				}
			}

		});



	}

}
