package net.yolopago.pago.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.activity.ProcessEMVCardActivity;
import com.wizarpos.emvsample.constant.Constant;
import com.wizarpos.util.StringUtil;

import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.dao.contract.PreferenceDao;
import net.yolopago.pago.db.dao.payment.PaymentDao;
import net.yolopago.pago.db.dao.payment.TransactionDao;
import net.yolopago.pago.db.dao.payment.VoucherDao;
import net.yolopago.pago.db.dao.product.PriceDao;
import net.yolopago.pago.db.dao.product.ProductDao;
import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.entity.Payment;
import net.yolopago.pago.db.entity.Price;
import net.yolopago.pago.db.entity.Product;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.TransactionEnt;
import net.yolopago.pago.db.entity.Voucher;
import net.yolopago.pago.fragment.FragmentProductos;
import net.yolopago.pago.utilities.CipherHM;
import net.yolopago.pago.ws.PaymentSignature;
import net.yolopago.pago.ws.builder.BuilderPayment;
import net.yolopago.pago.ws.dto.payment.PaymentDto;
import net.yolopago.pago.ws.dto.payment.TransactionDto;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.payment.VoucherDto;
import net.yolopago.pago.ws.dto.security.PrincipalDto;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class PaymentRepository extends AbstractRepository {
	private static final String TAG = "PaymentRepository";
	private static PaymentRepository instance;
	private static PaymentSignature paymentSignature;
	private static String TOKEN = "";
	private static String KEY = "";
	public static Long verTicketP;
	private SessionDao sessionDao;
	private MerchantDao merchantDao;
	private ProductDao productDao;
	private PriceDao priceDao;
	public TransactionDao transactionDao;
	protected static MainApp appState = null;
	private PreferenceDao preferenceDao;
	private PaymentDao paymentDao;
	private VoucherDao voucherDao;

	private Retrofit retrofitPayment;
	private TicketRepository ticketRepository;
	private BuilderPayment builderPayment;
	private MutableLiveData<String> paymentDone;
	public static PaymentDto paymentDto;
	private Gson gson = new Gson();

	public static PaymentRepository getInstance(Application application) {
		if (instance == null) {
			instance = new PaymentRepository(application);

		}

		return instance;
	}

	public void validaKey() {
		if(KEY==null || KEY .equals("")) {
			SharedPreferences sp = appState.getInstance().getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
			KEY = sp.getString("key", "");
		}
	}

	public Retrofit getRetrofitPayment(){
		if(retrofitPayment==null){

			retrofitPayment= new Retrofit.Builder()
					.baseUrl(ServerURL.URL_SERVER_PAYMENT)
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.client(okHttpClient)
					.build();
		}
		return retrofitPayment;
	}

	public void setAppState(MainApp app){
		appState=app;
	}
	public MainApp getAppState(){
		return appState;
	}

	private PaymentRepository(Application application) {
		super(application);
		sessionDao = configDatabase.sessionDao();
		merchantDao = configDatabase.merchantDao();
		preferenceDao = configDatabase.preferenceDao();

		transactionDao = paymentDatabase.transactionDao();

		productDao = productDatabase.productDao();
		priceDao = productDatabase.priceDao();


		appState = ((MainApp)application);
		SharedPreferences sp =appState.getInstance().getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);

		TOKEN = sp.getString("token", "");
		KEY= sp.getString("key", "");

		paymentSignature = getRetrofitPayment().create(PaymentSignature.class);
	}

	public List<TxDto> findPage(String firstDate, String lastDate, Long page, Integer size) {
		List<TxDto> txDtoList = new ArrayList<TxDto>();

		//Session session = sessionDao.getFirst();
		PaymentSignature paymentSignature = getRetrofitPayment().create(PaymentSignature.class);
		Call<List<TxDto>> cListTxDto = paymentSignature.findPageByMerchant(
															firstDate, lastDate, page, size);
		try {
			Response<List<TxDto>> response = cListTxDto.execute();
			if (response.isSuccessful()) {
				txDtoList = response.body();
				//Log.i("Error", "OK response findPage");
			}else{
				//Log.i("Error", "Fail"+response);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return txDtoList;
	}

	public LiveData<String> compra(TransactionDto transactionDto) {

		paymentDone = new MutableLiveData<>();

		Disposable disposable =
				Observable.just("DB")
						.subscribeOn(Schedulers.computation())
						.flatMap((String s) -> {
							Log.d(TAG, "Compra: Insertando TRX");
							transactionDao.insert(new TransactionEnt(transactionDto));
							Session session = sessionDao.getFirst();
							TransactionDto transactionDto2 = createTransactionDto(transactionDto.getAmount());
							transactionDto.setIdMerchant(transactionDto2.getIdMerchant());

							compraTx(transactionDto, session);
							return Observable.just("Hecho");
						})
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe();
		compositeDisposable.add(disposable);
		return paymentDone;
	}


	private TransactionDto createTransactionDto
			(Double amount) {

		Long ticket = TicketRepository.verTicket;
		Long terminal = FragmentProductos.getTerminalP();
		Long Seller = FragmentProductos.getIdSellerP();
		String EMvtags = ProcessEMVCardActivity.EmvTags();
		String Track2 = ProcessEMVCardActivity.Track2();

		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setTrack2(Track2);
		transactionDto.setEmvTags(EMvtags);
		transactionDto.setIdMerchant(FragmentProductos.getIdMerchant());
		transactionDto.setIdTicket(ticket);
		transactionDto.setIdTerminal(terminal);
		transactionDto.setIdSeller(Seller);
		transactionDto.setAmount(amount);
		String masked_pan="";
			if(appState.trans.getPAN()!=null){
				masked_pan=appState.trans.getPAN();
				if(masked_pan.length()>3) {
					masked_pan = masked_pan.substring(masked_pan.length() - 4);
				}
			}
		transactionDto.setMaskedPAN(masked_pan);


		if(appState.trans.getCardEntryMode() == Constant.CONTACTLESS_ENTRY) {
			transactionDto.setPaymentType("NFC");
		}else if(appState.trans.getCardEntryMode() == Constant.INSERT_ENTRY){
			transactionDto.setPaymentType("ICC");
		}else if(appState.trans.getCardEntryMode() == Constant.SWIPE_ENTRY){
			transactionDto.setPaymentType("Stripe");
		}
		appState.trans.setRfc(FragmentProductos.getTaxIdMerchant());

		return transactionDto;
	}


	private void compraTx(TransactionDto transactionDto, Session session) {
		PaymentSignature paymentSignature = getRetrofitPayment().create(PaymentSignature.class);

		transactionDto.setTrack2(CipherHM.encryptYLP(CipherHM.hexToBytes(KEY),transactionDto.getTrack2()));
		try {

		    Call<String> cString = paymentSignature.compraTx( gson.toJson(transactionDto));

			Call<String> sVerification;
			Call<String> sReverso;
			Response<String> response = cString.execute();

			if (response.isSuccessful()) {
				String result = response.body();
                Log.e("Error", "compraTx Response:"+result);
				try {
					VoucherDto voucherDto = gson.fromJson(result, VoucherDto.class);
					if(builderPayment == null){builderPayment = new BuilderPayment();}
					Payment payment = builderPayment.build(voucherDto.getPaymentDto());
					Voucher voucher = builderPayment.build(voucherDto);
					String code="";

					if(voucherDto.isOkResponse()) {

						appState.trans.setReference(payment.getReference());
						appState.trans.setControlNumber(voucherDto.getPaymentDto().getControlNumber());
						appState.trans.setCodeAut(payment.getCodeAuthorizer());
						appState.trans.setBankName(payment.getIssuingBank());
						appState.trans.setCardTypeName(payment.getCardType());
						appState.trans.setCardBrandName(payment.getCardBrand());
						appState.trans.setPaymentId(voucherDto.getPaymentDto().getId());
						appState.terminalConfig.setMID(voucherDto.getPaymentDto().getIdAfiliacion());
						appState.trans.setOperador(voucherDto.getPaymentDto().getSellerDto().getUserDto().getName()+" "+voucherDto.getPaymentDto().getSellerDto().getUserDto().getLastname()+" "+voucherDto.getPaymentDto().getSellerDto().getUserDto().getLastname2());
						//Log.d(TAG, "compraTx: "+payment.getDatosEmv());
						if(payment.getDatosEmv()!=null) {

							byte[] issuerData = CipherHM.hexStringToByteArray(payment.getDatosEmv());
							appState.trans.setIssuerAuthData(issuerData, 0, issuerData.length);
						}
						appState.trans.setTransDate(new SimpleDateFormat("yyyyMMdd").format(new Date(payment.getPayedDate())));
						appState.trans.setTransTime(new SimpleDateFormat("HHmmss").format(new Date(payment.getPayedDate())));
						if(payment.getResultAuthorizer()!=null) {
							appState.trans.setResponseCode(new byte[]{(byte) payment.getResultAuthorizer().charAt(0), (byte) payment.getResultAuthorizer().charAt(1)});
							code=" ("+payment.getResultAuthorizer()+")";
						}else{
							appState.trans.setResponseCode(new byte[]{'F', 'F'});
						}


					}
					if (voucherDto.isOkResponse() && payment.getResultAcquirer().equals("D") ){
                        appState.trans.setFailReason(payment.getReason()+code);
						paymentDone.postValue("ErrorD");
					}else if (voucherDto.isOkResponse() && payment.getResultAcquirer().equals("R") ){
                        appState.trans.setFailReason(payment.getReason()+code);
                        paymentDone.postValue("ErrorR");
                    }else if(voucherDto.isOkResponse() && payment.getResultAcquirer().equals("A")){
						paymentDone.postValue("Hecho");
					}else if(voucherDto.isOkResponse() && payment.getResultAcquirer().equals("REV")){
						appState.trans.setFailReason("Pago reversado intentelo nuevamnte");
						paymentDone.postValue("ErrorRev");
					}else{

						paymentDone.postValue("ErrorCon");
					}

				} catch (Exception e) {
					paymentDone.postValue("ErrorEx");
					e.printStackTrace();
				}
			}else {
				paymentDone.postValue("ErrorCon");
			}
		}
		catch(SocketTimeoutException  e){
			paymentDone.postValue("ErrorT");
			e.printStackTrace();
		}
		catch (IOException e) {
			paymentDone.postValue("ErrorIO");
			e.printStackTrace();
		}catch (Exception e) {
			paymentDone.postValue("ErrorEx");
			e.printStackTrace();
		}
	}

	public LiveData<String> getPaymentByIdTicket(Long idTicket) {
		//Log.d(TAG, "getPaymentByIdTicket: ");
		MutableLiveData<String> stringLiveData = new MutableLiveData<>();
		paymentSignature.getPaymentByIdTicket(idTicket).enqueue(new Callback<List<PaymentDto>>() {

			@Override
			public void onResponse(Call<List<PaymentDto>> call, Response<List<PaymentDto>> response) {
				//Log.d(TAG, "getPaymentByIdTicket: Respondio");
				if(response.isSuccessful()){
					try {
						//pegando los datos
						List<PaymentDto> listpayment = response.body();
						if(listpayment.size()>0) {
							PaymentDto paymentRes=new PaymentDto();
							for (PaymentDto p : listpayment) {
								paymentRes=p;
							}
							appState.trans.setReference(paymentRes.getReference());
							appState.trans.setControlNumber(paymentRes.getControlNumber());
							appState.trans.setCodeAut(paymentRes.getCodeAuthorizer());
							appState.trans.setBankName(paymentRes.getIssuingBank());
							appState.trans.setCardTypeName(paymentRes.getCardType());
							appState.trans.setCardBrandName(paymentRes.getCardBrand());
							if(paymentRes.getResultAuthorizer()!=null) {
								appState.trans.setResponseCode(new byte[]{(byte) paymentRes.getResultAuthorizer().charAt(0), (byte) paymentRes.getResultAuthorizer().charAt(1)});
							}
							if(paymentRes.getDatosEmv()!=null) {

								byte[] issuerData = CipherHM.hexStringToByteArray(paymentRes.getDatosEmv());
								appState.trans.setIssuerAuthData(issuerData, 0, issuerData.length);
							}
							if(paymentRes.getResultAcquirer() == null){
								stringLiveData.postValue("TryAgain");
							}else if(paymentRes.getResultAcquirer().equals("A")){
							    stringLiveData.postValue("Hecho");
							}else if(paymentRes.getResultAcquirer().equals("R")) {
								stringLiveData.postValue("ErrorR");
							}else if(paymentRes.getResultAcquirer().equals("D")){
								stringLiveData.postValue("ErrorD");
							}else if(paymentRes.getResultAcquirer().equals("REV")){
								stringLiveData.postValue("ErrorRev");
							}else {
								stringLiveData.postValue("ErrorCon");
							}
						}else {
							stringLiveData.postValue("ErrorCon");
						}

					} catch (Exception e) {
						e.printStackTrace();
						stringLiveData.postValue("ErrorEx");
					}
				}else{
					stringLiveData.postValue("ErrorEx");
				}
			}

			@Override
			public void onFailure(Call<List<PaymentDto>> call, Throwable t) {
				stringLiveData.postValue("ErrorEx");
			}
		});
		return stringLiveData;
	}

	public LiveData<String> getReverseByIdTicket(Long idTicket) {

		MutableLiveData<String> stringLiveData = new MutableLiveData<>();
		paymentSignature.getReverseByIdTicket( idTicket).enqueue(new Callback<PaymentDto>() {

			@Override
			public void onResponse(Call<PaymentDto> call, Response<PaymentDto> response) {
				if(response.isSuccessful()){
					try {
						//pegando los datos
						PaymentDto paymentDto = response.body();
						if(paymentDto.getId()==null ||
								paymentDto.getPaymentStatus().equals("Reversado")||
								paymentDto.getPaymentStatus().equals("Reversing")){

							    Disposable db = Observable.just("DB")
									.subscribeOn(Schedulers.computation())
									.flatMap((String s) -> {
										transactionDao.deleteAll();
										return Observable.just("");
									}).observeOn(AndroidSchedulers.mainThread())
									.subscribe();

						}
					} catch (Exception e) {
						e.printStackTrace();
						stringLiveData.postValue("ErrorEx");
					}
				}else{
					stringLiveData.postValue("Error");
				}
			}

			@Override
			public void onFailure(Call<PaymentDto> call, Throwable t) {
				stringLiveData.postValue("ErrorEx");
				//Log.e("Error", "ErrorEx "+t.getLocalizedMessage());
			}
		});
		return stringLiveData;
	}
	public LiveData<String> addFilePayment(Long idPayment,String data) {
		//Log.d(TAG, "Entrando addFilePayment: "+idPayment);
		PaymentSignature paymentSignature = getRetrofitPayment().create(PaymentSignature.class);
		MutableLiveData<String> stringLiveData = new MutableLiveData<>();
		paymentSignature.addFilePayment(idPayment, data).enqueue(new Callback<String>() {

			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				//Log.d(TAG, "addFilePayment: Respondio"+response.toString());
				if(response.isSuccessful()){
					try {
						//Log.d(TAG, "addFilePayment: response.body();"+response.body());
						stringLiveData.postValue(response.body());
					} catch (Exception e) {
						e.printStackTrace();
						stringLiveData.postValue("Error");
					}
				}else{
					stringLiveData.postValue("Error");
				}
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				//Log.d(TAG, "onResponse: addFilePayment:"+t.getLocalizedMessage());
				stringLiveData.postValue("Error");
			}
		});
		return stringLiveData;
	}
    public LiveData<String> solicitaLLave(String passkey ,String numeroSerie, String crc32) {
        //Log.d(TAG, "solicitarLLave: ");
		PaymentSignature paymentSignature = getRetrofitPayment().create(PaymentSignature.class);
        MutableLiveData<String> stringLiveData = new MutableLiveData<>();
        paymentSignature.solicitaLLave(passkey,numeroSerie,crc32).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
              //  Log.d(TAG, "solicitarLLave: Respondio"+response.body());
                if(response.isSuccessful()){
                    try {
                       // Log.d(TAG, "onResponse: response.body();"+response.body());
						stringLiveData.postValue(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                        stringLiveData.postValue("Error");
                    }
                }else{
                    stringLiveData.postValue("Error");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
				//Log.d(TAG, "onResponse: onFailere:"+t.getLocalizedMessage());
                stringLiveData.postValue("Error");
                //Log.e("Error", "ErrorEx ");
            }
        });
        return stringLiveData;
    }


	public LiveData<String> getTicketPayment(Long idpayment) {
		Log.i("Information", "Obteniendo idPayment"+idpayment);
		MutableLiveData<String> stringLiveData = new MutableLiveData<>();
		paymentSignature.getTicketPayment( idpayment).enqueue(new Callback<List<PaymentDto>>() {

			@Override
			public void onResponse(Call<List<PaymentDto>> call, Response<List<PaymentDto>> response) {
				if(response.isSuccessful()){
					try {
						//pegando los datos
						List<PaymentDto> listpayment = response.body();
						//retona los datos
						Log.i("Information", "Obteniendo idticket de la transaccion"+ response.body());
						for (PaymentDto p : listpayment) {
							verTicketP = p.getIdTicket();
							paymentDto=p;
							Log.i("Information", "Obteniendo idticket:"+p.getIdTicket());
						}
                        stringLiveData.postValue("OK");

					} catch (Exception e) {
						e.printStackTrace();
						stringLiveData.postValue("Error");
					}
				}else{
					stringLiveData.postValue("Error");
					//Log.e("Error", "Sin exito en la respuesta idticket: " + response);
                    //Log.e("Error", response.message());
				}
			}

			@Override
			public void onFailure(Call<List<PaymentDto>> call, Throwable t) {
				stringLiveData.postValue("Error");
				//Log.e("Error", "Fail");
			}
		});

		return stringLiveData;
	}

	public LiveData<Product> getProduct() {
		return productDao.loadFirst();
	}

	public LiveData<Price> getPrice(Long idPrice) {
		return priceDao.loadById(idPrice);
	}

	public LiveData<String> requestCancel(Long idPayment) {
		MutableLiveData<String> stringLiveData = new MutableLiveData<>();

		paymentSignature.cancelacion(idPayment)
				.enqueue(new Callback<PaymentDto>() {
					@Override
					public void onResponse(Call<PaymentDto> call, Response<PaymentDto> response) {
						if (response.isSuccessful()) {
							final PaymentDto paymentDtoCancel = response.body();
							//Log.d("(TAG, "requestCancel: "+gson.toJson(paymentDtoCancel));
							appState.trans.setReference(paymentDtoCancel.getReference());
							appState.trans.setControlNumber(""+paymentDtoCancel.getControlNumber());
							appState.trans.setCodeAut(paymentDtoCancel.getCodeAuthorizer());
							appState.trans.setBankName(paymentDtoCancel.getIssuingBank());
							appState.trans.setCardTypeName(paymentDtoCancel.getCardType());
							appState.trans.setCardBrandName(paymentDtoCancel.getCardBrand());
							appState.trans.setOriginalReference(paymentDtoCancel.getOriginalReference());
							appState.trans.setPAN(paymentDtoCancel.getMaskedPAN());
							appState.trans.setPaymentId(paymentDtoCancel.getId());
							appState.trans.setExpiry("    ");

							appState.getCurrentDateTime();
							appState.trans.setTransDate(   appState.currentYear
									+ StringUtil.fillZero(Integer.toString(appState.currentMonth), 2)
									+ StringUtil.fillZero(Integer.toString(appState.currentDay), 2)
							);
							appState.trans.setTransTime(   StringUtil.fillZero(Integer.toString(appState.currentHour), 2)
									+ StringUtil.fillZero(Integer.toString(appState.currentMinute), 2)
									+ StringUtil.fillZero(Integer.toString(appState.currentSecond), 2)
							);
							stringLiveData.postValue("OK");

						} else {
							stringLiveData.postValue("Error");
						}
					}

					@Override
					public void onFailure(Call<PaymentDto> call, Throwable t) {
						stringLiveData.postValue("Failed network");
					}
				});

		return stringLiveData;
	}

	public LiveData<PrincipalDto> getPaymentSession(String user,String password,String terminal) {

		MutableLiveData<PrincipalDto> stringLiveData = new MutableLiveData<>();
		paymentSignature.getPaymentSesion(Credentials.basic(user, password), terminal).enqueue(new Callback<PrincipalDto>() {
			@Override
			public void onResponse(Call<PrincipalDto> call, Response<PrincipalDto> response) {
				if (response.isSuccessful()) {
					stringLiveData.postValue(response.body());
					//Log.i(TAG, "Response exitoso: " + response.body());
				} else {
					stringLiveData.postValue(null);
					//Log.i(TAG, "Response fail: " + response);
				}
			}

			@Override
			public void onFailure(Call<PrincipalDto> call, Throwable t) {
				stringLiveData.postValue(null);
				//Log.e(TAG, "canTransac"+t.getLocalizedMessage());
			}
		});

		return stringLiveData;
	}

	public LiveData<String> logOut() {

		MutableLiveData<String> stringLiveData = new MutableLiveData<>();
		paymentSignature.paymentLogOut().enqueue(new Callback<String>() {
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


	public boolean isConnected(){
		boolean connected = false;
		try{
			ConnectivityManager cm = (ConnectivityManager) getAppState().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		}catch (Exception e){
			//Log.e("Connectivity Exception", e.getMessage());
		}
		return connected;
	}
}

