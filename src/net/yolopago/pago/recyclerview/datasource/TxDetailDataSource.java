package net.yolopago.pago.recyclerview.datasource;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import net.yolopago.pago.fragment.FragmentProductos;
import net.yolopago.pago.fragment.TxDetailFragment;
import net.yolopago.pago.repository.ServerURL;
import net.yolopago.pago.repository.UnsafeOkHttpClient;
import net.yolopago.pago.ws.PaymentSignature;
import net.yolopago.pago.ws.SessionSignature;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.security.Token;

import java.io.IOException;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TxDetailDataSource extends PageKeyedDataSource<Long, TxDto> {
    private static final String TAG = "TxListDataSource";
    private static PaymentSignature paymentSignature;
    public static String TOKEN = "";
    public static final String credentials = Credentials.basic("android-client", "123456");

    private static MutableLiveData networkState;
    private static MutableLiveData initialLoading;
    private static OkHttpClient okHttpClient;

    public TxDetailDataSource(Context context) {
        if (paymentSignature == null) {
            okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient(context);
            Retrofit retrofitSecurity = new Retrofit.Builder()
                    .baseUrl(ServerURL.URL_SERVER_SECURITY)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            SessionSignature sessionSignature = retrofitSecurity.create(SessionSignature.class);

            String user = FragmentProductos.getUsuarioP();


            Retrofit retrofitPayment = new Retrofit.Builder()
                    .baseUrl(ServerURL.URL_SERVER_PAYMENT)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            paymentSignature = retrofitPayment.create(PaymentSignature.class);

            networkState = new MutableLiveData();
            initialLoading = new MutableLiveData();
        }
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
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        paymentSignature.findPageReturns(credentials, TxDetailFragment.txDto.getId(),
                        0L, params.requestedLoadSize, TOKEN)
                .enqueue(new Callback<List<TxDto>>() {
                    @Override
                    public void onResponse(Call<List<TxDto>> call, Response<List<TxDto>> response) {
                        if(response.isSuccessful()) {
                            callback.onResult(response.body(), null, 2l);
                            initialLoading.postValue(NetworkState.LOADED);
                            networkState.postValue(NetworkState.LOADED);

                        } else {
                            initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TxDto>> call, Throwable t) {
                        String errorMessage = t == null ? "unknown error" : t.getMessage();
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, TxDto> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, TxDto> callback) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        paymentSignature.findPageReturns(credentials, TxDetailFragment.txDto.getId(),
                            params.key - 1, params.requestedLoadSize, TOKEN)
        .enqueue(new Callback<List<TxDto>>() {
            @Override
            public void onResponse(Call<List<TxDto>> call, Response<List<TxDto>> response) {
                if(response.isSuccessful()) {
                    Long nextKey = null;
                    if (response != null && params.key != response.body().size()) {
                        nextKey = params.key + 1;
                    }
                    callback.onResult(response.body(), nextKey);
                    networkState.postValue(NetworkState.LOADED);
                } else {
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<TxDto>> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }
}
