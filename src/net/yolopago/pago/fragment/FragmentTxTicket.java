package net.yolopago.pago.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.databinding.TicketDetailBinding;

import net.yolopago.pago.adapter.TxTicketAdapter;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.utilities.PrinterHM;
import net.yolopago.pago.viewmodel.ContractViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentTxTicket extends AbstractFragment {
	private static final String TAG = "DetailProductFragment";
	private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	public TxDto txDto;
	private TicketDetailBinding binding;
	private static PaymentViewModel paymentViewModel;
	private static TicketViewModel ticketViewModel;
	private static ContractViewModel contractViewModel;
	TxTicketAdapter myAdapter;
	ArrayList<TicketLineDto> items;
	MainApp appState;
	private String dataTicket;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.tx_detail_ticket, container, false);
		return binding.getRoot();
	}


	@SuppressLint("RestrictedApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		 appState=(MainApp) this.getActivity().getApplication();

		paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
		ticketViewModel = ViewModelProviders.of(this).get(TicketViewModel.class);
		contractViewModel = ViewModelProviders.of(this).get(ContractViewModel.class);

		items = new ArrayList<TicketLineDto>();
		getProducsList();
		getMerchant(txDto.getMerchantDto().getId());

		//dataTicket=txDto.

		myAdapter = new TxTicketAdapter(this.getContext(), R.layout.list_item, items);
		binding.items.setAdapter(myAdapter);
		binding.comercio.setText(txDto.getMerchantDto().getName());
	    //binding.direccion.setText(txDto.getMerchantDto().getStreet());
		binding.rfc.setText(txDto.getMerchantDto().getTaxid());
		binding.operador.setText(txDto.getSellerDto().getUserDto().getName()+" "+txDto.getSellerDto().getUserDto().getLastname()+" "+txDto.getSellerDto().getUserDto().getLastname2());

		binding.montoTotal.setText("$"+DECIMAL_FORMAT.format(txDto.getAmount()));
		binding.fechaHora.setText(DATE_FORMAT.format(txDto.getPayedDate())+" "+TIME_FORMAT.format(txDto.getPayedDate()));

		binding.btnBack.setOnClickListener(view -> presentFragmentTx());
		binding.btnReimprimir.setOnClickListener(view -> reimprimir());



	}


	private void reimprimir() {

		PrinterHM printerHM=PrinterHM.getInstance(getContext());

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
		alertDialogBuilder
				.setMessage("¿Desea imprimir el ticket?")
				.setCancelable(true)
				.setNegativeButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(dataTicket!=null && !dataTicket.isEmpty()){
							printerHM.printFromData(dataTicket);
						}else {
							appState.trans.setTransAmount((new Double(txDto.getAmount()*100)).longValue());
							appState.trans.setDir(binding.direccion.getText().toString());
							appState.trans.setMerchantName(txDto.getMerchantDto().getName());
							appState.trans.setTicketId(PaymentRepository.verTicketP);
							appState.trans.setRfc(binding.rfc.getText().toString());
							Log.e("error", "ticket getTransAmount: " +appState.trans.getTransAmount()+" -"+txDto.getAmount());
							appState.trans.setTransDate(new SimpleDateFormat("yyyyMMdd").format(txDto.getPayedDate()));
							appState.trans.setTransTime(new SimpleDateFormat("HHmmss").format(txDto.getPayedDate()));
							ArrayList<Product> products=getProducts(items);
							printerHM.printReceipt(appState,products,true,true);
						}

					}
				}).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		}).create().show();

		//



	}

	private ArrayList<Product> getProducts(ArrayList<TicketLineDto> items) {
		ArrayList<Product> ret=new ArrayList<Product>();
		for (int i = 0; i < items.size(); i++) {
			Product product =new Product();
			product.cantidad=""+items.get(i).getItems();
			product.producto=items.get(i).getProduct();
			product.setPrecio(items.get(i).getPrice());
			product.setTotal(items.get(i).getItems()*items.get(i).getPrice());
			ret.add(product);
		}
		return ret;
	}

	private void getProducsList() {

		paymentViewModel.IdPayment(txDto.getId()).observe(this, s -> {
			if (s.equals("OK")) {
				Long ticketPayment = PaymentRepository.verTicketP;
				binding.idTicket.setText("ID ticket:"+ticketPayment);
				//binding.direccion.setText();

				Log.e("error", "ticket paymet: " +ticketPayment);
				ticketViewModel.verProductos(ticketPayment).observe(this, (List l) -> {
					items=new ArrayList();
					//listProduct = l;
					int totalProd=0;
					for (int i = 0; i < l.size(); i++) {
						items.add((TicketLineDto)l.get(i));
						totalProd+=((TicketLineDto) l.get(i)).getItems();
						/*Integer item = ((TicketLineDto)l.get(i)).getItems();
						Double prec = ((TicketLineDto)l.get(i)).getPrice();
						//Log.i("Information", "lista de ticketRepository: " + prod + ", " + item + ", " + prec);*/
					}
					binding.totalArticulos.setText(""+totalProd);
                    myAdapter.updateData(items);
				});
				ticketViewModel.getTicketById(ticketPayment).observe(this, ticketDto -> {
					if(ticketDto!=null) {
						dataTicket = ticketDto.getTicketFile();
					}
				});


			}

		});



	}
	private void getMerchant(Long idMerchant) {

		contractViewModel.getMerchant().observe(this, merchant -> {
			if (merchant!=null) {
				Log.d(TAG, "getMerchant: "+merchant.getName());
				binding.direccion.setText(merchant.getStreet()+" "+merchant.getExternal() +" "+merchant.getInternal());
				binding.rfc.setText(merchant.getTaxid());
				appState.trans.setRfc("RFC:"+merchant.getTaxid());

				//merchant.getTaxid();
			}
		});



	}


	private void presentFragmentTx(){
		TxListFragment fragmentTx = new TxListFragment();
		setFragment(fragmentTx);
	}


}
