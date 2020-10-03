package net.yolopago.pago.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.cancelacionPrint;
import com.wizarpos.emvsample.databinding.TxDetailBinding;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.recyclerview.TxDetailAdapter;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.viewmodel.TxDetailViewModel;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.payment.UserDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TxDetailFragment extends AbstractFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = "TxDetailFragment";
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
	private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));


	private TxDetailViewModel txDetailViewModel;
	private TxDetailBinding binding;
	private TxDetailAdapter adapter;
	public static TxDto txDto;
	protected static MainApp appState = null;
	private MainActivity mainActivity;
	public static Long folioL;
	private static TicketViewModel ticketViewModel;
	private static PaymentViewModel paymentViewModel;
	private static MerchantViewModel merchantViewModel;
	private ArrayList<TicketLineDto> listProduct = new ArrayList<>();


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.tx_detail, container, false);
		return binding.getRoot();
	}


	@SuppressLint("RestrictedApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("Error", "----onActivityCreated");
		if (mainActivity == null) {
			mainActivity = (MainActivity) getActivity();

		}
			appState = ((MainApp)mainActivity.getApplicationContext());

		paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
		ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
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
			appState.trans.setMerchantPhone(merchant.getPhone());
			appState.trans.setMerchantName(merchant.getName());
			//appState.terminalConfig.setMID(""+merchant.get_id());
		});


		if(txDto!=null) {

			if(null !=txDto.getCardBrand()) {
				if (txDto.getCardBrand().equalsIgnoreCase("visa")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ic_visa);
				} else if (txDto.getCardBrand().equalsIgnoreCase("mastercard")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ic_mastercard);
				} else if (txDto.getCardBrand().equalsIgnoreCase("amex")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ic_amex);
				} else {
					binding.cardTypeImageView.setImageResource(R.drawable.ic_credit_card);
				}
			}else{
				binding.cardTypeImageView.setImageResource(R.drawable.ic_credit_card);
			}

			binding.issuerBankTextView.setText(txDto.getIssuingBank());
			binding.cardTypeTextView.setText(txDto.getCardType());
			binding.maskedPANTextView.setText("********* " + txDto.getMaskedPAN());
			binding.cardHolderTextView.setText(txDto.getCardHolder());

			binding.idTextView.setText("" + txDto.getId());
			binding.statusTextView.setText(txDto.getPaymentStatus());
			binding.payedDateTextView.setText(DATE_FORMAT.format(txDto.getPayedDate()));
			binding.amountTextView.setText(DECIMAL_FORMAT.format(txDto.getAmount()));
			binding.merchantTextView.setText(txDto.getMerchantDto().getName());
			UserDto userDto = txDto.getSellerDto().getUserDto();
			if (userDto != null) {
				binding.sellerTextView.setText(userDto.getName() + " " + userDto.getLastname());
			}
			binding.terminalTextView.setText(txDto.getTerminalDto().getSerialNumber());

			double balance = 0.0;
			if (txDto.getAmountReturns() != null) {
				binding.amountReturnsTextView.setText(DECIMAL_FORMAT.format(txDto.getAmountReturns()));
				balance = txDto.getAmount() - txDto.getAmountReturns();
				binding.labelReturnsTextView.setText("Importe devuelto");
			} else {
				binding.amountReturnsTextView.setText("");
				balance = txDto.getAmount();
			}

			binding.cancelImageView.setVisibility(View.GONE);
			binding.cancelTextView.setVisibility(View.GONE);
			binding.fabRefund.setVisibility(View.GONE);
			if ((txDto.getAmountReturns() == null || txDto.getAmountReturns() == 0) && payedToday(txDto) && txDto.getPaymentStatus().equals("Aprobada") ) {
				binding.cancelImageView.setVisibility(View.VISIBLE);
				binding.cancelTextView.setVisibility(View.VISIBLE);
				binding.refoundTextView.setVisibility(View.INVISIBLE);
			} else if (txDto.getPaymentStatus().equals("Aprobada") && payedToday(txDto)) {
				if (balance > 0) {
					binding.fabRefund.setVisibility(View.VISIBLE);
				}
			}

			binding.resumenVenta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
					// Configura el titulo.
					alertDialogBuilder.setTitle("¡Detalle de compra!");

					// Configura el mensaje.
					alertDialogBuilder
							.setMessage("¿Desea ver el detalle de la compra de esta transaccion?")
							.setCancelable(false)
							.setPositiveButton("SI", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									getProducsList();
								}
							})
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							}).create().show();
				}
			});

			binding.cancelImageView.setOnClickListener(this);
			binding.fabRefund.setOnClickListener(this);

			binding.idLabelTextView.setText("Folio");
			binding.estadoFechaLabelTextView.setText("Estado y Fecha");
			binding.autorizoTerminalLabelTextView.setText("Terminal y Autorizador");
			binding.montoLabelTextView.setText("Monto");

			if (txDto.getPaymentStatus().equals("Aprobada") && txDto.getHasChilds()) {
				binding.labelTxReturnsTextView.setText("Devoluciones");
			} else if (txDto.getPaymentStatus().equals("Cancelada")) {
				binding.labelTxReturnsTextView.setText("Cancelacion");
			} else if (txDto.getPaymentStatus().equals("Reverso")) {
				binding.labelTxReturnsTextView.setText("Reverso");
			} else {
				binding.labelTxReturnsTextView.setText("");
				binding.idLabelTextView.setText("");
				binding.estadoFechaLabelTextView.setText("");
				binding.autorizoTerminalLabelTextView.setText("");
				binding.montoLabelTextView.setText("");
			}
			binding.listPayment.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
			adapter = new TxDetailAdapter(getContext().getApplicationContext());
			binding.listPayment.setAdapter(adapter);

			binding.swipeRefreshLayout.setOnRefreshListener(this);
		}
	}

	private void getProducsList() {
		String folio = binding.idTextView.getText().toString();
		folioL = Long.parseLong(folio);
		Log.e("error", "idfolio: " + folioL);

		paymentViewModel.IdPayment(folioL).observe(this, s -> {
			if (s.equals("OK")) {
				Long ticketPayment = PaymentRepository.verTicketP;
				Log.e("error", "error ver ticket paymet: " + ticketPayment);

				getDetailList(ticketPayment);

			}

		});



	}

	private void getDetailList(Long ticketPayment) {
		//Log.i("Information", "TicketPayment: " + ticketPayment);
		ticketViewModel.verProductos(ticketPayment).observe(this, (List l) -> {

			//listProduct = l;
			for (int i = 0; i < l.size(); i++) {
				String prod =((TicketLineDto)l.get(i)).getProduct();
				Integer item = ((TicketLineDto)l.get(i)).getItems();
				Double prec = ((TicketLineDto)l.get(i)).getPrice();
				//Log.i("Information", "lista de ticketRepository: " + prod + ", " + item + ", " + prec);
			}
			Bundle b = new Bundle();
			b.putString("totalventa", binding.amountTextView.getText().toString());
			b.putSerializable("ResumenCompra", (Serializable) l);
			FragmentResumenCompra fragmentReumenCompra = new FragmentResumenCompra();
			fragmentReumenCompra.setArguments(b);
			setFragment(fragmentReumenCompra);
		});

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cancelImageView:
				cancel(v);
				break;
			case R.id.fabRefund:
			default:
				break;
		}
	}

	private void cancel(View v) {
		if (txDto.getHasChilds().equals(Boolean.FALSE) && payedToday(txDto)) {
			AlertDialog alertDialog = askCancel(txDto);
			alertDialog.show();
		}
	}

	private void refound(View v) {
		if (txDto.getPaymentStatus().equals("Aprobada") && payedToday(txDto)) {
			Bundle bundle = new Bundle();
			bundle.putParcelable("txDto", txDto);
			Fragment fragment = new FragmentRefund();
			fragment.setArguments(bundle);
			mainActivity.loadFragment(fragment);
		}
	}

	@Override
	public void onRefresh() {
		binding.swipeRefreshLayout.setRefreshing(false);
	}

	private AlertDialog askCancel(TxDto txDto) {
		String amount = DECIMAL_FORMAT.format(txDto.getAmount());
		return new AlertDialog.Builder(getContext())
				.setTitle("Cancelar")
				.setMessage("¿Desea cancelar la transacción por un monto de " + amount +"?")
				.setIcon(R.drawable.ic_cancel)
				.setPositiveButton("Si", (dialog, whichButton) -> {

					appState.trans.setAID("");
					appState.trans.setTVR("");
					appState.trans.setTSI("");
					appState.trans.setAppLabel("");
					appState.trans.setTerminalId(Build.SERIAL);
					appState.trans.setTransAmount(((Double)(txDto.getAmount()*100)).longValue());
					Log.e("Error", "valor de cancelación: " + txDto.getAmount());

					LiveData<String> liveData = txDetailViewModel.requestCancel(
							txDto.getId());
					liveData.observe(getActivity(), s -> {
						Log.e("Fatal error", "valor de cancelación: " + s);
						if (s.equals("OK")) {
							binding.cancelImageView.setVisibility(View.INVISIBLE);
							binding.cancelTextView.setVisibility(View.INVISIBLE);
							binding.refoundTextView.setVisibility(View.INVISIBLE);
							Toast.makeText(getContext(), "¡Cancelacion realizada exitosamente!", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(getActivity().getApplicationContext(), cancelacionPrint.class);
							startActivityForResult(intent, 0);
						} else {
							Toast.makeText(getContext(), "¡No se pudo realizar la cancelación!", Toast.LENGTH_LONG).show();
						}
					});

					dialog.dismiss();
				})
				.setNegativeButton("No", (dialog, which) -> dialog.dismiss())
				.create();
	}

	public static String getCard() {
		String card = "************" + txDto.getMaskedPAN();
		return card;
	}

	public static String getAmount() {
		String amount = "" + txDto.getAmount();
		return amount;
	}


}
