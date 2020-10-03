package net.yolopago.pago.recyclerview;

import androidx.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import net.yolopago.pago.activity.MainActivity;
import com.wizarpos.emvsample.databinding.ItemTxBinding;
import com.wizarpos.emvsample.databinding.ItemTxheaderBinding;
import com.wizarpos.emvsample.databinding.NetworkItemBinding;

import net.yolopago.pago.fragment.FragmentTxTicket;
import net.yolopago.pago.fragment.FragmentTxVoucher;
import net.yolopago.pago.fragment.TxListFragment;
import net.yolopago.pago.recyclerview.datasource.NetworkState;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TxListAdapter extends PagedListAdapter<TxDto, RecyclerView.ViewHolder> implements View.OnClickListener,StickHeaderItemDecoration.StickyHeaderInterface{
	private static final String TAG = "VoucherRecyclerViewAdap";
	private  DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private  SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final String[] strDays = new String[] { " Domingo ", " Lunes ", " Martes ", " Miercoles ", " Jueves ", " Viernes ", " Sabado " };
	/*
	 * There are two layout types we define
	 * in this adapter:
	 * 1. progress view
	 * 2. data view
	 */
	private static final int TYPE_PROGRESS = 0;
	private static final int TYPE_HEADER = 1;
	private static final int TYPE_ITEM = 2;

	private Context context;
	private TxListFragment txListFragment;
	private NetworkState networkState;
	private Calendar calendarCurrent;
	private Calendar calendarLast;
	private Map<Long,Date> mapHeaders;

	/*
	 * The DiffUtil is defined in the constructor
	 */
	public TxListAdapter(Context context, TxListFragment txListFragment) {
		super(TxDto.DIFF_CALLBACK);
		this.context = context;
		this.txListFragment = txListFragment;

		calendarCurrent = Calendar.getInstance();
		calendarCurrent.setTimeInMillis(MainApp.getCurrentTimeInMillis());
		calendarCurrent.add(Calendar.DATE, 1);
		calendarLast = Calendar.getInstance();

		mapHeaders = new HashMap<>();
	}

	/*
	 * Default method of RecyclerView.Adapter
	 */
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		if(viewType == TYPE_PROGRESS) {
			NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
			NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
			return viewHolder;
		} else if(viewType == TYPE_HEADER) {
			ItemTxheaderBinding itemBinding = ItemTxheaderBinding.inflate(layoutInflater, parent, false);
			ItemTxheaderViewHolder viewHolder = new ItemTxheaderViewHolder(itemBinding);
			return viewHolder;
		} else {
			ItemTxBinding itemBinding = ItemTxBinding.inflate(layoutInflater, parent, false);
			ItemTxViewHolder viewHolder = new ItemTxViewHolder(itemBinding);
			return viewHolder;
		}
	}


	/*
	 * Default method of RecyclerView.Adapter
	 */
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

		//Log.i("Error", "onBindViewHolder pos:" + position + holder.getClass().getName());
		if(holder instanceof ItemTxheaderViewHolder) {
			((ItemTxheaderViewHolder)holder).bindTo(getItem(position));
			holder.itemView.setOnClickListener(this);
		} else if(holder instanceof ItemTxViewHolder) {
			((ItemTxViewHolder)holder).bindTo(getItem(position));
			holder.itemView.setOnClickListener(this);
		} else {
			((NetworkStateItemViewHolder) holder).bindView(networkState);
		}

	}


	/*
	 * Default method of RecyclerView.Adapter
	 */
	@Override
	public int getItemViewType(int position) {
		int type = TYPE_ITEM;
		if (hasExtraRow() && position == getItemCount() - 1) {
			type = TYPE_PROGRESS;
		} else if (position < 1) {
			type = TYPE_HEADER;
		} else if (position < getItemCount()) {
			TxDto txDtoCurrent = getItem(position);
			TxDto txDtoLast = getItem(position - 1);


			calendarCurrent.setTime(txDtoCurrent.getPayedDate());
			calendarLast.setTime(txDtoLast.getPayedDate());
			if (calendarCurrent.get(Calendar.DATE) != calendarLast.get(Calendar.DATE)) {
				type = TYPE_HEADER;
			}
		}
		return type;
	}


	private boolean hasExtraRow() {
		if (networkState != null && networkState != NetworkState.LOADED) {
			return true;
		} else {
			return false;
		}
	}

	public void setNetworkState(NetworkState newNetworkState) {
		NetworkState previousState = this.networkState;
		boolean previousExtraRow = hasExtraRow();
		this.networkState = newNetworkState;
		boolean newExtraRow = hasExtraRow();
	}

	@Override
	public int getHeaderPositionForItem(int itemPosition) {
		int headerPosition = 0;
		do {
			if (this.isHeader(itemPosition)) {
				headerPosition = itemPosition;
				break;
			}
			itemPosition -= 1;
		} while (itemPosition >= 0);
		return headerPosition;
	}

	@Override
	public int getHeaderLayout(int headerPosition) {
		if (getItemViewType(headerPosition)== TYPE_HEADER)
			return R.layout.item_txheader;
		else {
			return R.layout.item_txheader;
		}
	}

	@Override
	public void bindHeaderData(View header, int headerPosition) {
		TextView tvHeader = header.findViewById(R.id.yearMonthTextView);
		tvHeader.setText(DATE_FORMAT.format(getItem(headerPosition).getCapturedDate()));
		((RelativeLayout)header.findViewById(R.id.valuesView)).setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean isHeader(int itemPosition) {
		if (getItemViewType(itemPosition)== TYPE_HEADER)
			return true;
		else
			return false;
	}


	/*
	 * We define A Header ViewHolder for the list item
	 */
	public class ItemTxheaderViewHolder extends RecyclerView.ViewHolder {
		private ItemTxheaderBinding binding;
		public ItemTxheaderViewHolder(ItemTxheaderBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bindTo(TxDto txDto) {
			try {
				Calendar calendar = Calendar.getInstance();
				if(txDto.getPayedDate()!=null){
					calendar.setTime(txDto.getPayedDate());
				}
				binding.btnTicket.setTag(txDto);
				binding.btnTicket.setOnClickListener(view -> showTicket(view));
				binding.btnVoucher.setTag(txDto);
				binding.btnVoucher.setOnClickListener(view -> showVoucher(view));
				binding.btnVoucher.setVisibility(View.INVISIBLE);
				binding.btnTicket.setVisibility(View.INVISIBLE);
				binding.btnMenuItem.setOnClickListener(view -> showButtonsHeader(binding));

				binding.yearMonthTextView.setText(DATE_FORMAT.format(calendar.getTime()));
				binding.timeTextView.setText(TIME_FORMAT.format(calendar.getTime()));
				binding.tipoTextView.setText(txDto.getCardType());


				binding.statusTextView.setTag(txDto);
				binding.statusTextView.setText(txDto.getPaymentStatus());
				binding.maskedPANTextView.setText("**** **** " + txDto.getMaskedPAN());

				if (null == txDto.getAmountReturns()) {
					if (null == txDto.getAmount()) {
						binding.amountTextView.setText("$0.0");
					} else {
						binding.amountTextView.setText(DECIMAL_FORMAT.format(txDto.getAmount()));
					}
				} else {
					binding.amountTextView.setText(DECIMAL_FORMAT.format(txDto.getAmountReturns()));
					binding.amountTextView.setTextColor(Color.RED);
				}
				if(txDto.getPaymentStatus().equals("Rechazada")|| txDto.getPaymentStatus().equals("Cancelada")|| txDto.getPaymentStatus().equals("Declinada")|| txDto.getPaymentStatus().equals("Reverso")) {//"paymentStatus":
					binding.amountTextView.setTextColor(Color.RED);
				}else{
					binding.amountTextView.setTextColor(Color.parseColor("#2cc300"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != txDto.getCardBrand()) {
				if (txDto.getCardBrand().equalsIgnoreCase("visa")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_visa);
				} else if (txDto.getCardBrand().equalsIgnoreCase("mastercard")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_master);
				} else if (txDto.getCardBrand().equalsIgnoreCase("amex")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_amex);
				} else {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_generl_card);
				}
			}else {
				binding.cardTypeImageView.setImageResource(R.drawable.ylp_generl_card);
			}

		}
	}

	private void showVoucher(View view) {
		TxDto txDto = (TxDto) view.getTag();
		//Log.d("(TAG, "showTicket: "+txDto);
		MainActivity mainActivity = (MainActivity) txListFragment.getActivity();
		FragmentTxVoucher txDetailFragment = new FragmentTxVoucher();
		txDetailFragment.txDto = txDto;
		mainActivity.loadFragment(txDetailFragment);
	}

	private void showTicket(View view) {
		TxDto txDto = (TxDto) view.getTag();
		//Log.d("(TAG, "showTicket: "+txDto);
		MainActivity mainActivity = (MainActivity) txListFragment.getActivity();
		FragmentTxTicket txDetailFragment = new FragmentTxTicket();
		txDetailFragment.txDto = txDto;
		mainActivity.loadFragment(txDetailFragment);
	}

	private void showButtonsHeader(ItemTxheaderBinding current) {
		if(current.btnVoucher.getVisibility()==View.INVISIBLE) {
			current.btnVoucher.setVisibility(View.VISIBLE);
			current.btnTicket.setVisibility(View.VISIBLE);
		}else{
			current.btnVoucher.setVisibility(View.INVISIBLE);
			current.btnTicket.setVisibility(View.INVISIBLE);
		}
	}
	private void showButtons(ItemTxBinding current) {
		if(current.btnVoucher.getVisibility()==View.INVISIBLE) {
			current.btnVoucher.setVisibility(View.VISIBLE);
			current.btnTicket.setVisibility(View.VISIBLE);
		}else{
			current.btnVoucher.setVisibility(View.INVISIBLE);
			current.btnTicket.setVisibility(View.INVISIBLE);
		}
	}


	/*
	 * We define A custom ViewHolder for the list item
	 */
	public class ItemTxViewHolder extends RecyclerView.ViewHolder {
		private ItemTxBinding binding;
		public ItemTxViewHolder(ItemTxBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bindTo(TxDto txDto) {
			try {
				Calendar calendar = Calendar.getInstance();
				if(txDto.getPayedDate()!=null){
					calendar.setTime(txDto.getPayedDate());
				}
				binding.btnTicket.setTag(txDto);
				binding.btnTicket.setOnClickListener(view -> showTicket(view));
				binding.btnVoucher.setTag(txDto);
				binding.btnVoucher.setOnClickListener(view -> showVoucher(view));
				binding.btnVoucher.setVisibility(View.INVISIBLE);
				binding.btnTicket.setVisibility(View.INVISIBLE);
				binding.btnMenuItem.setOnClickListener(view -> showButtons(binding));
				binding.statusTextView.setTag(txDto);
				binding.statusTextView.setText(txDto.getPaymentStatus());
				binding.maskedPANTextView.setText("**** **** " + txDto.getMaskedPAN());
				binding.timeTextView.setText(TIME_FORMAT.format(calendar.getTime()));
				binding.tipoTextView.setText(txDto.getCardType());

				if (null == txDto.getAmountReturns()) {
					if (null == txDto.getAmount()) {
						binding.amountTextView.setText("$0.0");
					} else {
						binding.amountTextView.setText(DECIMAL_FORMAT.format(txDto.getAmount()));
					}
				} else {
					binding.amountTextView.setText(DECIMAL_FORMAT.format(txDto.getAmountReturns()));
					binding.amountTextView.setTextColor(Color.RED);
				}
				if(txDto.getPaymentStatus().equals("Rechazada") || txDto.getPaymentStatus().equals("Cancelada")|| txDto.getPaymentStatus().equals("Declinada")|| txDto.getPaymentStatus().equals("Reverso")) {//"paymentStatus":
					binding.amountTextView.setTextColor(Color.RED);
				}else{
					binding.amountTextView.setTextColor(Color.parseColor("#2cc300"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != txDto.getCardBrand()) {
				if (txDto.getCardBrand().equalsIgnoreCase("visa")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_visa);
				} else if (txDto.getCardBrand().equalsIgnoreCase("mastercard")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_master);
				} else if (txDto.getCardBrand().equalsIgnoreCase("amex")) {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_ico_amex);
				} else {
					binding.cardTypeImageView.setImageResource(R.drawable.ylp_generl_card);
				}
			}else {
				binding.cardTypeImageView.setImageResource(R.drawable.ylp_generl_card);
			}

		}
	}

	/*
	 * We define A custom ViewHolder for the progressView
	 */
	public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

		private NetworkItemBinding binding;
		public NetworkStateItemViewHolder(NetworkItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bindView(NetworkState networkState) {

			if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
				binding.errorMsg.setVisibility(View.VISIBLE);
				binding.errorMsg.setText(networkState.getMsg());
			} else {
				binding.errorMsg.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {

	}
}