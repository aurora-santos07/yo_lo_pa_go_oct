package net.yolopago.pago.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;

import com.wizarpos.emvsample.MainApp;
import net.yolopago.pago.recyclerview.datasource.NetworkState;
import net.yolopago.pago.recyclerview.datasource.TxDetailDataSourceFactory;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TxDetailViewModel extends ViewModel {
	private static final String TAG = "TxDetailViewModel";
	private Executor executor;
	private LiveData<NetworkState> networkState;
	private LiveData<PagedList<TxDto>> txDtoLiveData;
	private MainApp mainApp;
	private PaymentRepository paymentRepository;

	public TxDetailViewModel(@NonNull MainApp mainApp) {
		this.mainApp = mainApp;
		init();
	}

	private void init() {
		executor = Executors.newFixedThreadPool(2);

		TxDetailDataSourceFactory txDetailDataSourceFactory = new TxDetailDataSourceFactory(this.mainApp.getApplicationContext());
		networkState = Transformations.switchMap(txDetailDataSourceFactory.getMutableLiveData(),
				dataSource -> dataSource.getNetworkState());

		PagedList.Config pagedListConfig =
				(new PagedList.Config.Builder())
						.setEnablePlaceholders(false)
						.setInitialLoadSizeHint(5)
						.setPageSize(5).build();

		txDtoLiveData = (new LivePagedListBuilder(txDetailDataSourceFactory, pagedListConfig))
				.setFetchExecutor(executor)
				.build();

		paymentRepository = PaymentRepository.getInstance(mainApp);
	}

	public LiveData<NetworkState> getNetworkState() {
		return networkState;
	}

	public LiveData<PagedList<TxDto>> getTxDtoLiveData() {
		return txDtoLiveData;
	}

	public LiveData<String> requestCancel(Long idPayment) {
		return paymentRepository.requestCancel(idPayment);
	}

}
