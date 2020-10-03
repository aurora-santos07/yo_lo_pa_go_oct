package net.yolopago.pago.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;

import com.wizarpos.emvsample.MainApp;
import net.yolopago.pago.recyclerview.datasource.NetworkState;
import net.yolopago.pago.recyclerview.datasource.TxListDataSourceFactory;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TxListViewModel extends ViewModel {
	private Executor executor;
	private LiveData<NetworkState> networkState;
	private LiveData<PagedList<TxDto>> txDtoLiveData;
	private MainApp mainApp;

	public TxListViewModel(@NonNull MainApp mainApp) {
		this.mainApp = mainApp;
		init();
	}

	private void init() {
		executor = Executors.newFixedThreadPool(5);

		TxListDataSourceFactory paymentDataFactory = new TxListDataSourceFactory(this.mainApp.getApplicationContext());
		networkState = Transformations.switchMap(paymentDataFactory.getMutableLiveData(),
				dataSource -> dataSource.getNetworkState());

		PagedList.Config pagedListConfig =
				(new PagedList.Config.Builder())
						.setEnablePlaceholders(false)
						.setInitialLoadSizeHint(20)
						.setPageSize(20).build();

		txDtoLiveData = (new LivePagedListBuilder(paymentDataFactory, pagedListConfig))
				.setFetchExecutor(executor)
				.build();
	}

	public LiveData<NetworkState> getNetworkState() {
		return networkState;
	}

	public LiveData<PagedList<TxDto>> getTxDtoLiveData() {
		return txDtoLiveData;
	}

}
