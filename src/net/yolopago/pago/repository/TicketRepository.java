package net.yolopago.pago.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.ws.TicketSignature;
import net.yolopago.pago.ws.builder.BuilderTicket;
import net.yolopago.pago.ws.dto.security.PrincipalDto;
import net.yolopago.pago.ws.dto.security.Token;
import net.yolopago.pago.ws.dto.ticket.PdfDto;
import net.yolopago.pago.ws.dto.ticket.TicketDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;
import net.yolopago.pago.ws.dto.ticket.TicketStatus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketRepository extends AbstractRepository {
    private static final String TAG = "TicketRepository";
    private static String TOKEN = "";
    private SessionDao sessionDao;
    //private MerchantDao merchantDao;
    private Merchant merchant;
    private Session session;
    private Gson gson = new Gson();
    private static TicketRepository instance;
    private BuilderTicket builderTicket;
    Retrofit retrofitTicket;
    public static Long verTicket;
    public static ArrayList<TicketLineDto> productos = new ArrayList<>();
    TicketSignature ticketSignature;
    private Retrofit retrofitSecurity;
    private String resultRefreshCaches;
    private MutableLiveData<String> ticketDone;

    public TicketRepository(Application application) {
        super(application);
        sessionDao = configDatabase.sessionDao();
        merchant = new Merchant();
        session = new Session();
        SharedPreferences sp = application.getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        TOKEN = sp.getString("token", "");
        initRetrofitTiket();
        ticketSignature = retrofitTicket.create(TicketSignature.class);

    }

    private void initRetrofitTiket() {
        if(retrofitTicket ==null) {
            retrofitTicket = new Retrofit.Builder().baseUrl(ServerURL.URL_SERVER_TICKET)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
    }

    public static TicketRepository getInstance(Application application) {
        if (instance == null) {
            instance = new TicketRepository(application);
        }
        return instance;
    }

    public LiveData<List> getProductClient(Long idTicket) {
        //Log.i("Information", "Obteniendo productos:"+idTicket);

        MutableLiveData<List> stringLiveData = new MutableLiveData<>();
        initRetrofitTiket();

        TicketSignature ticketSignature = retrofitTicket.create(TicketSignature.class);

        ticketSignature.getProductForTicket(idTicket).enqueue(new Callback<List<TicketLineDto>>() {
            @Override
            public void onResponse(Call<List<TicketLineDto>> call, Response<List<TicketLineDto>> response) {
                   if(response.isSuccessful()){
                       try {
                           productos.clear();
                           //pegando los datos
                           List<TicketLineDto> listlines = response.body();
                           //retona los datos
                           for (TicketLineDto lines : listlines) {
                               String prod = lines.getProduct();
                               Integer item =  lines.getItems();
                               Double prec = lines.getPrice();
                               //Log.e("errror", "imprimiendo productos del cliente: " + prod + " " + item +  " " + prec );
                               productos.add(new TicketLineDto(prod, prec, item));
                           }


                           stringLiveData.postValue(listlines);

                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                       //Log.i("Information", "Response exitoso: " + response.toString());
                   }else{
                       //Log.e("Error", "Sin exito en la respuesta: " + response.toString());
                   }
                }
            @Override
            public void onFailure(Call<List<TicketLineDto>> call, Throwable t) {
                //Log.e("Error", "No hay resultados en la lista de productos para el ticket");
            }
        });

        return stringLiveData;
    }
    public LiveData<String> addFileTicket(Long idTicket,String data) {
        //Log.i("Information", "addBMPTicket:"+idTicket);

        MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        initRetrofitTiket();

        TicketSignature ticketSignature = retrofitTicket.create(TicketSignature.class);

        ticketSignature.addFileTicket(idTicket,data).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    stringLiveData.postValue(response.body());
                    //Log.i("Information", "Response exitoso: " + response.toString());
                }else{
                    stringLiveData.postValue("NOK");
                    //Log.e("Error", "Sin exito en la respuesta: " + response.toString());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                stringLiveData.postValue("NOK");
                //Log.e("Error", "No hay resultados en la lista de productos para el ticket");
            }
        });

        return stringLiveData;
    }

    public Long getTicket(Long iduser) {
        //Log.i("Information", "Obteniendo idticket");
        //ArrayList<TicketDto> lines = new ArrayList<>();
        initRetrofitTiket();
        TicketSignature ticketSignature = retrofitTicket.create(TicketSignature.class);

        ticketSignature.getTicket().enqueue(new Callback<List<TicketDto>>() {
            @Override
            public void onResponse(Call<List<TicketDto>> call, Response<List<TicketDto>> response) {
                if(response.isSuccessful()){
                    try {
                        //pegando los datos
                        List<TicketDto> listlines = response.body();
                        //retona los datos
                        for (TicketDto c : listlines) {
                            verTicket = c.getId();
                            //Log.e("Error", "ticket information: " + c.getId());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    //Log.e("Error", "Sin exito en la respuesta idticket: " + response.toString());
                }
            }
            @Override
            public void onFailure(Call<List<TicketDto>> call, Throwable t) {
                //Log.e("Error", "No hay resultados en la lista de productos para el ticket");
            }
        });
        return verTicket;
    }

    public LiveData<TicketDto> getTicketById(Long idTicket) {
        //Log.i("Information", "getTicketById:"+idTicket);

        MutableLiveData<TicketDto> stringLiveData = new MutableLiveData<>();
        initRetrofitTiket();

        TicketSignature ticketSignature = retrofitTicket.create(TicketSignature.class);

        ticketSignature.getTicketById(idTicket).enqueue(new Callback<TicketDto>() {
            @Override
            public void onResponse(Call<TicketDto> call, Response<TicketDto> response) {
                if(response.isSuccessful()){
                    stringLiveData.postValue(response.body());
                    Log.i("Information", "getTicketById: OK" );
                }else{
                    stringLiveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<TicketDto> call, Throwable t) {
                stringLiveData.postValue(null);
                //Log.e("Error", "No hay resultados en la lista de productos para el ticket");
            }
        });

        return stringLiveData;
    }


    public LiveData<String> crearTicket(TicketDto ticketDto){

        MutableLiveData<String> ticketCreateDone= new MutableLiveData<>();
        Disposable disposable =
                Observable.just("DB")
                        .subscribeOn(Schedulers.computation())
                        .flatMap((String s) -> {
                            Retrofit retrofitTicket = new Retrofit.Builder().baseUrl(ServerURL.URL_SERVER_TICKET)
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

                            ticketDto.setTicketStatus(TicketStatus.Pending);



                            ticketSignature.crear(ticketDto).enqueue(new Callback<TicketDto>() {
                                @Override
                                public void onResponse(Call<TicketDto> call, Response<TicketDto> response) {
                                    if(response.isSuccessful()){
                                        Log.e("Error", "das ticket: " + response.body().getId());
                                        verTicket=response.body().getId();
                                        ticketCreateDone.postValue("OK");
                                    }else{
                                        ticketCreateDone.postValue("Error");
                                        //Log.e("Error", "Sin exito en la respuesta idticket: " + response.toString());
                                    }
                                }
                                @Override
                                public void onFailure(Call<TicketDto> call, Throwable t) {
                                    ticketCreateDone.postValue("Error");
                                    //Log.e("Error", "No hay resultados en la lista de productos para el ticket");
                                }
                            });
                            return Observable.just("Hecho");
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();


        return ticketCreateDone;
    }

    
    public LiveData<PrincipalDto> getTicketSession(String user,String password,String terminal) {

            MutableLiveData<PrincipalDto> stringLiveData = new MutableLiveData<>();
            ticketSignature.getTicketSesion(Credentials.basic(user, password), terminal).enqueue(new Callback<PrincipalDto>() {
                @Override
                public void onResponse(Call<PrincipalDto> call, Response<PrincipalDto> response) {
                    if (response.isSuccessful()) {
                        stringLiveData.postValue(response.body());
                        Log.i(TAG, "Response exitoso: " + response.body());
                    } else {
                        stringLiveData.postValue(null);
                        Log.i(TAG, "Response fail: " + response);
                    }
                }

                @Override
                public void onFailure(Call<PrincipalDto> call, Throwable t) {
                    stringLiveData.postValue(null);
                    Log.e(TAG, "canTransac"+t.getLocalizedMessage());
                }
            });

            return stringLiveData;
        }


    public LiveData<String> logOut() {

        MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        ticketSignature.tiketLogOut().enqueue(new Callback<String>() {
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

    public LiveData<String> guardarTicketPdf(PdfDto pdfDto){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofitTicket = new Retrofit.Builder().baseUrl(ServerURL.URL_SERVER_TICKET)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build();
        try {
            new CreatePdfTask(sessionDao, retrofitTicket, pdfDto).execute();
        } catch (Exception e) {
            errorMsg.postValue("Favor de intentar nuevamente.");
            ticketDone.postValue("Error");
            e.printStackTrace();
        }
        return ticketDone;
    }
}