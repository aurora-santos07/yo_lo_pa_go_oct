package net.yolopago.pago.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wizarpos.emvsample.databinding.ItemTxreturnBinding;
import com.wizarpos.emvsample.databinding.NetworkItemBinding;

import net.yolopago.pago.recyclerview.datasource.NetworkState;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.payment.UserDto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TxDetailAdapter extends PagedListAdapter<TxDto, RecyclerView.ViewHolder> {
	private static final String TAG = "VoucherRecyclerViewAdap";
	private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$ ###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy\nhh:mm:ss a");
	private DecimalFormat DECIMAL_FORMAT_FOLIO = new DecimalFormat("00000");

	private static final int TYPE_PROGRESS = 0;
	private static final int TYPE_ITEM = 1;

	private Context context;
	private NetworkState networkState;


	public TxDetailAdapter(Context context) {
		super(TxDto.DIFF_CALLBACK);
		this.context = context;
	}

	/*
	 * Default method of RecyclerView.Adapter
	 */
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		Log.e("Error", "----onCreateViewHolder");
		if(viewType == TYPE_PROGRESS) {
			NetworkItemBinding networkItemBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
			NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(networkItemBinding);
			return viewHolder;
		} else {
			ItemTxreturnBinding itemBinding = ItemTxreturnBinding.inflate(layoutInflater, parent, false);
			ItemTxDetailViewHolder viewHolder = new ItemTxDetailViewHolder(itemBinding);
			return viewHolder;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ItemTxDetailViewHolder) {
			((ItemTxDetailViewHolder)holder).bindTo(getItem(position));
		} else {
			((NetworkStateItemViewHolder) holder).bindView(networkState);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (hasExtraRow() && position == getItemCount() - 1) {
			return TYPE_PROGRESS;
		} else {
			return TYPE_ITEM;
		}
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

	/*
	 * We define A custom ViewHolder for the list item
	 */
	public class ItemTxDetailViewHolder extends RecyclerView.ViewHolder {
		private ItemTxreturnBinding binding;
		public ItemTxDetailViewHolder(ItemTxreturnBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bindTo(TxDto txDto) {
			try {

				binding.idTextView.setText(DECIMAL_FORMAT_FOLIO.format(txDto.getId()));
				binding.statusTextView.setText(txDto.getPaymentStatus());
				binding.dateReturnTextView.setText(DATE_FORMAT.format(txDto.getPayedDate()));
				UserDto userDto = txDto.getSellerDto().getUserDto();
				binding.sellerTextView.setText(userDto.getName() + " " + userDto.getLastname());
				binding.terminalTextView.setText(txDto.getTerminalDto().getSerialNumber());
				if (null == txDto.getAmountReturns()) {
					binding.amountReturnsTextView.setText("");
				} else {
					binding.amountReturnsTextView.setText(DECIMAL_FORMAT.format(txDto.getAmountReturns()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

		private NetworkItemBinding binding;
		public NetworkStateItemViewHolder(NetworkItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		public void bindView(NetworkState networkState) {
//			if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
//				binding.progressBar.setVisibility(View.VISIBLE);
//			} else {
//				binding.progressBar.setVisibility(View.GONE);
//			}

			if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
				binding.errorMsg.setVisibility(View.VISIBLE);
				binding.errorMsg.setText(networkState.getMsg());
			} else {
				binding.errorMsg.setVisibility(View.GONE);
			}
		}
	}

}