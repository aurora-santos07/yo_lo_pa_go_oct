package net.yolopago.pago.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.ws.NotificationSignature;
import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;
import net.yolopago.pago.ws.dto.security.UserDto;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContractRepository extends AbstractRepository {

    private static String TOKEN = "";
    private static ContractRepository instance;
    private NotificationSignature conractSignature;
    private Retrofit retrofitContract;
    private MerchantDao merchantDao;

    public ContractRepository(Application application) {
        super(application);

        initRetrofitTiket();
        conractSignature = retrofitContract.create(NotificationSignature.class);


    }

    private void initRetrofitTiket() {
        if (retrofitContract == null) {

            retrofitContract = new Retrofit.Builder().baseUrl(ServerURL.URL_SERVER_CONTRACT)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
    }

    public static ContractRepository getInstance(Application application) {
        if (instance == null) {
            instance = new ContractRepository(application);
        }
        return instance;
    }

    public LiveData<String> canTransac(String model, String serialNumber) {

        MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        conractSignature.canTransac(model, serialNumber ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    stringLiveData.postValue(response.body());
                    //Log.i("Information", "Response exitoso: " + response.toString());
                } else {
                    stringLiveData.postValue("NOK");
                    Log.e("Error", "Sin exito en la respuesta: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                stringLiveData.postValue("Falla en el servicio de validación");
                Log.e("Error", "canTransac"+t.getLocalizedMessage());
            }
        });

        return stringLiveData;
    }

    public LiveData<MerchantDto> getMerchant() {

        MutableLiveData<MerchantDto> stringLiveData = new MutableLiveData<>();
        conractSignature.getMerchant().enqueue(new Callback<MerchantDto>() {
            @Override
            public void onResponse(Call<MerchantDto> call, Response<MerchantDto> response) {
                if (response.isSuccessful()) {

                    stringLiveData.postValue(response.body());
                    //Log.i("Information", "getMerchant exitoso: " + response.body());
                } else {
                    stringLiveData.postValue(null);
                    Log.e("Error", "Fail getMerchant: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<MerchantDto> call, Throwable t) {
                stringLiveData.postValue(null);
                Log.e("Error", "getMerchant"+t.getLocalizedMessage());
            }
        });

        return stringLiveData;
    }
    public LiveData<PrincipalDto> getContractSesion(String user, String password, String terminal) {

        MutableLiveData<PrincipalDto> stringLiveData = new MutableLiveData<>();
        conractSignature.getContractSesion(Credentials.basic(user, password), terminal).enqueue(new Callback<PrincipalDto>() {
            @Override
            public void onResponse(Call<PrincipalDto> call, Response<PrincipalDto> response) {
                if (response.isSuccessful()) {
                    stringLiveData.postValue(response.body());
                    //Log.i("Information", "Response exitoso: " + response.body());
                } else {
                    stringLiveData.postValue(null);
                    //Log.i("Information", "Response fail: " + response);
                }
            }

            @Override
            public void onFailure(Call<PrincipalDto> call, Throwable t) {
                stringLiveData.postValue(null);
                Log.e("Error", "canTransac"+t.getLocalizedMessage());
            }
        });

        return stringLiveData;
    }

    public LiveData<String> logOut() {

        MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        conractSignature.contractLogOut().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    stringLiveData.postValue(response.body());
                } else {
                    stringLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                stringLiveData.postValue(null);
            }
        });

        return stringLiveData;
    }
}