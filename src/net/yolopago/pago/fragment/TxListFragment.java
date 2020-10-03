package net.yolopago.pago.fragment;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import net.yolopago.pago.activity.MainActivity;
import com.wizarpos.emvsample.databinding.TxListBinding;

import net.yolopago.pago.recyclerview.SimpleRecyclerView;
import net.yolopago.pago.recyclerview.StickHeaderItemDecoration;
import net.yolopago.pago.recyclerview.TxListAdapter;
import net.yolopago.pago.viewmodel.TxListViewModel;

import java.util.Calendar;


public class TxListFragment extends AbstractFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = "TxListFragment";
	private static final String[] strMonth = new String[] { " Enero", " Febrero", " Marzo", " Abril", "Mayo", "Junio", "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre" };
	public static Calendar calendarFirst;
	public static Calendar curretDay;

	private TxListAdapter adapter2;
	private SimpleRecyclerView adapter;
	private TxListViewModel txViewModel;
	private TxListBinding binding;
	private MainActivity mainActivity;

	public TxListFragment() {

	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.tx_list, container, false);
		return binding.getRoot();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			return;
		}

		if (mainActivity == null) {
			mainActivity = (MainActivity) getActivity();
		}

		calendarFirst = Calendar.getInstance();
		calendarFirst.setTimeInMillis(MainApp.getCurrentTimeInMillis());
		calendarFirst.set(Calendar.DATE, 1);
		calendarFirst.set(Calendar.HOUR, 0);
		calendarFirst.set(Calendar.MINUTE, 0);
		calendarFirst.set(Calendar.SECOND, 0);
		calendarFirst.set(Calendar.MILLISECOND, 0);

		curretDay= Calendar.getInstance();
		curretDay.setTimeInMillis(MainApp.getCurrentTimeInMillis());
		curretDay.set(Calendar.DATE, 1);
		curretDay.set(Calendar.HOUR, 0);
		curretDay.set(Calendar.MINUTE, 0);
		curretDay.set(Calendar.SECOND, 0);
		curretDay.set(Calendar.MILLISECOND, 0);

		binding.mesButton1.setTextColor(getResources().getColor(R.color.darkGrayYLP));

		int mes=calendarFirst.get(Calendar.MONTH);
		binding.mesButton1.setText(strMonth[mes]);
		mes=(mes-1==-1)?11:(mes-1);
		binding.mesButton2.setText(strMonth[mes]);
		mes=(mes-1==-1)?11:(mes-1);
		binding.mesButton3.setText(strMonth[mes]);
		mes=(mes-1==-1)?11:(mes-1);
		binding.mesButton4.setText(strMonth[mes]);

		txViewModel = new TxListViewModel(MainApp.getApplicationContext(getActivity()));
		binding.listPayment.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
		adapter2 = new TxListAdapter(getContext().getApplicationContext(), this);
		RecyclerView recyclerView = binding.listPayment;
		LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

		recyclerView.setAdapter(adapter2);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.addItemDecoration(new StickHeaderItemDecoration(adapter2));


		txViewModel.getTxDtoLiveData().observe(this, pagedList -> {
			adapter2.submitList(pagedList);
		});

		txViewModel.getNetworkState().observe(this, networkState -> {
			adapter2.setNetworkState(networkState);
		});

		binding.listPayment.setAdapter(adapter2);

		binding.mesButton1.setOnClickListener(this);
		binding.mesButton2.setOnClickListener(this);
		binding.mesButton3.setOnClickListener(this);
		binding.mesButton4.setOnClickListener(this);
		binding.swipeRefreshLayout.setOnRefreshListener(this);
		binding.swipeRefreshLayout.setRefreshing(false);

		binding.back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentProductos fragmentProductos = new FragmentProductos();
				setFragment(fragmentProductos);
			}
		});

	}

	@Override
	public void onClick(View v) {
		binding.mesButton1.setTextColor(getResources().getColor(R.color.grayYLP));
		binding.mesButton2.setTextColor(getResources().getColor(R.color.grayYLP));
		binding.mesButton3.setTextColor(getResources().getColor(R.color.grayYLP));
		binding.mesButton4.setTextColor(getResources().getColor(R.color.grayYLP));
		switch (v.getId()) {
			case R.id.mesButton_1:
				binding.mesButton1.setTextColor(getResources().getColor(R.color.darkGrayYLP));
				month(0);
				break;
			case R.id.mesButton_2:
				binding.mesButton2.setTextColor(getResources().getColor(R.color.darkGrayYLP));
				month(-1);
				break;
			case R.id.mesButton_3:
				binding.mesButton3.setTextColor(getResources().getColor(R.color.darkGrayYLP));
				month(-2);
				break;
			case R.id.mesButton_4:
				binding.mesButton4.setTextColor(getResources().getColor(R.color.darkGrayYLP));
				month(-3);
				break;
			default:
				break;
		}
	}

	@Override
	public void onRefresh() {
		binding.swipeRefreshLayout.setRefreshing(false);
	}

	public void month(int bk_month) {
		calendarFirst.set(Calendar.YEAR, curretDay.get(Calendar.YEAR));
		calendarFirst.set(Calendar.MONTH, curretDay.get(Calendar.MONTH)+bk_month);
		txViewModel.getTxDtoLiveData().getValue().getDataSource().invalidate();
	}


}
