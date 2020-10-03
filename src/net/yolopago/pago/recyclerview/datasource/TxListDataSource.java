package net.yolopago.pago.recyclerview.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.wizarpos.emvsample.MainApp;

import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.fragment.TxListFragment;
import net.yolopago.pago.recyclerview.TxListAdapter;
import net.yolopago.pago.repository.ServerURL;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.repository.UnsafeOkHttpClient;
import net.yolopago.pago.ws.PaymentSignature;
import net.yolopago.pago.ws.dto.payment.TxDto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class TxListDataSource extends PageKeyedDataSource<Long, TxDto> {
    private static final String TAG = "TxListDataSource";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private static MutableLiveData networkState = new MutableLiveData();
    private static MutableLiveData initialLoading = new MutableLiveData();
    private Calendar calendarLast;
    private static Calendar calendarFirst;
    public static String TOKEN = "";
    private static PaymentSignature paymentSignature;
    private SessionDao sessionDao;

    static {
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
        getClendarFirst();
    }

    private PaymentRepository paymentRepository;

    private static OkHttpClient okHttpClient;

    public TxListDataSource(Context context) {
        paymentRepository = PaymentRepository.getInstance(MainApp.getInstance());
        SharedPreferences sp =MainApp.getInstance().getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        TOKEN = sp.getString("token", "");
        if (paymentSignature == null) {

            okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient(context);
            Retrofit retrofitPayment = new Retrofit.Builder()
                    .baseUrl(ServerURL.URL_SERVER_PAYMENT)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            paymentSignature = retrofitPayment.create(PaymentSignature.class);

            networkState = new MutableLiveData();
            initialLoading = new MutableLiveData();

            getClendarFirst();
        }
   }

   public static Calendar getClendarFirst(){

        if(TxListFragment.calendarFirst==null) {
            TxListFragment.calendarFirst = Calendar.getInstance();
            TxListFragment.calendarFirst.setTimeInMillis(MainApp.getCurrentTimeInMillis());
            TxListFragment.calendarFirst.set(Calendar.DATE, 1);
            TxListFragment.calendarFirst.set(Calendar.HOUR, 0);
            TxListFragment.calendarFirst.set(Calendar.MINUTE, 0);
            TxListFragment.calendarFirst.set(Calendar.SECOND, 0);
            TxListFragment.calendarFirst.set(Calendar.MILLISECOND, 0);
        }
        return TxListFragment.calendarFirst;
   }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, TxDto> callback) {
        Log.i(TAG, "Loading Page 0 Size " + params.requestedLoadSize);

        initialLoading.postValue(NetworkState.LOADING);
       // networkState.postValue(NetworkState.LOADING);

        calendarLast = Calendar.getInstance();
        calendarLast.setTime(getClendarFirst().getTime());
        calendarLast.add(Calendar.MONTH, 1);
        calendarLast.add(Calendar.DATE, -1);

        String firstDate = simpleDateFormat.format(getClendarFirst().getTime());
        String lastDate = simpleDateFormat.format(calendarLast.getTime());
        Disposable disposable =
        Observable.just("DB")
                .subscribeOn(Schedulers.computation())
                .flatMap((String s) -> {
                    try {
                        List<TxDto> txDtoList = paymentRepository.findPage(firstDate, lastDate, 0L, params.requestedLoadSize);
                        //Log.i("Error", "txDtoList Ini:"+txDtoList.size());
                        callback.onResult(txDtoList, null, 2l);
                        initialLoading.postValue(NetworkState.LOADED);
                       // networkState.postValue(NetworkState.LOADED);
                    } catch (Exception e) {
                        initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, e.getMessage()));
                        e.printStackTrace();
                    }
                    return Observable.just("Hecho");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, TxDto> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, TxDto> callback) {
        Log.i(TAG, "Loading Page " + params.key + " Size " + params.requestedLoadSize);

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        String firstDate = simpleDateFormat.format(getClendarFirst().getTime());
        String lastDate = simpleDateFormat.format(calendarLast.getTime());
        Disposable disposable =
                Observable.just("DB")
                        .subscribeOn(Schedulers.computation())
                        .flatMap((String s) -> {
                            try {
                                List<TxDto> txDtoList = paymentRepository.findPage(firstDate, lastDate, params.key - 1, params.requestedLoadSize);
                                long nextKey = (params.key == txDtoList.size()) ? null : params.key+1;
                                //Log.i("Error", "txDtoList After :"+txDtoList.size());
                                callback.onResult(txDtoList, nextKey);
                                //initialLoading.postValue(NetworkState.LOADED);
                                networkState.postValue(NetworkState.LOADED);
                            } catch (Exception e) {
                                //initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, e.getMessage()));
                                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, e.getMessage()));
                                //Log.i("Error", "Fail loadAfter" );
                            }
                            return Observable.just("Hecho");
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
    }
}