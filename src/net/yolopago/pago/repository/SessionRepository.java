package net.yolopago.pago.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wizarpos.emvsample.MainApp;

import net.yolopago.pago.db.dao.contract.ContractDao;
import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.dao.contract.PreferenceDao;
import net.yolopago.pago.db.dao.contracttype.ContractTypeDao;
import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.dao.security.UserDao;
import net.yolopago.pago.db.entity.Contract;
import net.yolopago.pago.db.entity.ContractType;
import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.User;
import net.yolopago.pago.fragment.FragmentLogin;
import net.yolopago.pago.ws.SessionSignature;
import net.yolopago.pago.ws.builder.BuilderContract;
import net.yolopago.pago.ws.builder.BuilderSecurity;
import net.yolopago.pago.ws.dto.contract.ContractDto;
import net.yolopago.pago.ws.dto.contract.MerchantDto;
import net.yolopago.pago.ws.dto.contract.PreferenceDto;
import net.yolopago.pago.ws.dto.contract.TerminalDto;
import net.yolopago.pago.ws.dto.payment.SellerDto;
import net.yolopago.pago.ws.dto.security.Token;
import net.yolopago.pago.ws.dto.security.UserDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionRepository extends AbstractRepository {
	private static SessionRepository instance;
	private SessionDao sessionDao;
	private UserDao userDao;
	private ContractDao contractDao;
	private MerchantDao merchantDao;
	private PreferenceDao preferenceDao;
	private ContractTypeDao contractTypeDao;
	private Retrofit retrofitSecurity;
	private Retrofit retrofitSecurityToken;
	private Retrofit retrofitContract;
	private Retrofit retrofitContractType;
	private Token token;
	private String TokenTerminal = "";
	private BuilderSecurity builderSecurity;
	private BuilderContract builderContract;
	private String resultAuth;
	public static Boolean usuarioHabilitado;
	public static String estatusTerminal, validate,taxIdMerchant;
	public static Long usuarioLogeado, usuarioTerminal, idTerminal, idSeller,idMerchant;

	public static SessionRepository getInstance(Application application) {
		if (instance == null) {
			instance = new SessionRepository(application);
		}
		return instance;
	}

	private SessionRepository(Application application) {
		super(application);
		sessionDao = configDatabase.sessionDao();
		userDao = configDatabase.userDao();
		contractDao = configDatabase.contractDao();
		merchantDao = configDatabase.merchantDao();
		preferenceDao = configDatabase.preferenceDao();
		contractTypeDao = configDatabase.contractTypeDao();

		retrofitSecurity = new Retrofit.Builder()
				.baseUrl(ServerURL.URL_SERVER_SECURITY)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build();
		retrofitSecurityToken = new Retrofit.Builder()
				.baseUrl(ServerURL.URL_SERVER_SECURITY)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		retrofitContract = new Retrofit.Builder()
				.baseUrl(ServerURL.URL_SERVER_CONTRACT)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build();
		retrofitContractType = new Retrofit.Builder()
				.baseUrl(ServerURL.URL_SERVER_CONTRACTTYPE)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build();

		builderSecurity = new BuilderSecurity();
		builderContract = new BuilderContract();
	}



	public void deleteAll() {
		Disposable disposable =
		Observable.just("DB")
				.subscribeOn(Schedulers.computation())
				.flatMap((String s) -> {
					sessionDao.deleteAll();
					userDao.deleteAll();
					return Observable.just("Hecho");
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe();
		compositeDisposable.add(disposable);
	}



	public LiveData<Session> getSession() {
		return sessionDao.loadFirst();
	}



	private void saveMerchants(UserDto userDto, Token token, Set<MerchantDto> merchantDtos) {
		if (merchantDtos == null || merchantDtos.isEmpty()) {
			errorMsg.postValue("Vendedor sin comercios asociados.");
			return;
		}

		Session session = builderSecurity.build(token, userDto);
		User user = builderSecurity.build(userDto);

		sessionDao.deleteAll();
		userDao.deleteAll();
		sessionDao.insert(session);
		userDao.insert(user);

		contractDao.deleteAll();
		merchantDao.deleteAll();
		Map<Long, Contract> contracts = new HashMap<>();
		for(MerchantDto merchantDto : merchantDtos) {
			saveMerchant(contracts, merchantDto);
		}

		if (merchantDtos.size() == 1) {
			MerchantDto merchantDto = merchantDtos.iterator().next();
			getPreferences(merchantDto.getContractDto(), token);
		} else {
			resultAuth = "MULTI";
		}
	}

	public void saveMerchant(MerchantDto merchantDto) {
		Disposable db = Observable.just("DB")
				.subscribeOn(Schedulers.computation())
				.flatMap((String s) -> {
					//Log.d("SS", "saveMerchant: ");
					Merchant merchant = builderContract.build(merchantDto);
					merchantDao.deleteAll();
					merchantDao.insert(merchant);
					return Observable.just("");
				}).observeOn(AndroidSchedulers.mainThread())
				.subscribe();

	}

	private void saveMerchant(Map<Long, Contract> contracts, MerchantDto merchantDto) {
		Merchant merchant = builderContract.build(merchantDto);
		merchantDao.insert(merchant);

		Long idContract = merchant.getIdContract();
		if (null != idContract && 0 < idContract){
			if (! contracts.containsKey(idContract)) {
				Contract contract = builderContract.build(merchantDto.getContractDto());
				ContractType contractType = builderContract.build(merchantDto.getContractDto().getContractTypeDto());

				contractDao.insert(contract);
				contractTypeDao.insert(contractType);
			}
		}
		// Guarda sub comercios
		if (merchantDto.getMerchantDtos() != null) {
			for (MerchantDto subMerchantDto : merchantDto.getMerchantDtos()) {
				saveMerchant(contracts, subMerchantDto);
			}
		}
	}

	private void getPreferences(ContractDto contracDto, Token token) {

		resultAuth = "OK";
	}

	public LiveData<User> getUser(Long idUser) {
		return userDao.findById(idUser);
	}

	public static Long getUsuarioLogeado(){
		return usuarioLogeado;
	}

	public static Long getUsuarioTerminal(){
		return usuarioTerminal;
	}

	public static Long getIdTerminal(){		return idTerminal;	}

	public static String getEstatusTerminal(){
		return estatusTerminal;
	}

	public static Boolean getHabilitado(){
		return usuarioHabilitado;
	}


	public static Long getIdSeller(){		return idSeller;	}

	public static Long getIdMerchant(){	return idMerchant;	}

	public static String getTaxIdMerchant(){	return taxIdMerchant;	}

	public static String getValidaLogin(){
		return validate;
	}

}
