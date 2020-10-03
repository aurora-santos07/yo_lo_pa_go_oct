package net.yolopago.pago.repository;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.yolopago.pago.db.dao.contract.ContractDao;
import net.yolopago.pago.db.dao.contract.MerchantDao;
import net.yolopago.pago.db.dao.contract.PreferenceDao;
import net.yolopago.pago.db.dao.contract.TicketFooterDao;
import net.yolopago.pago.db.dao.contract.TicketHeaderDao;
import net.yolopago.pago.db.dao.contract.TicketLayoutDao;
import net.yolopago.pago.db.dao.contracttype.AIDDao;
import net.yolopago.pago.db.dao.contracttype.CAPKDao;
import net.yolopago.pago.db.dao.contracttype.ContractTypeDao;
import net.yolopago.pago.db.dao.contracttype.ExceptionPANDao;
import net.yolopago.pago.db.dao.contracttype.RevokedCAPKDao;
import net.yolopago.pago.db.dao.contracttype.VoucherFooterDao;
import net.yolopago.pago.db.dao.contracttype.VoucherHeaderDao;
import net.yolopago.pago.db.dao.contracttype.VoucherLayoutDao;
import net.yolopago.pago.db.dao.notification.NotificationDao;
import net.yolopago.pago.db.dao.product.PriceDao;
import net.yolopago.pago.db.dao.product.ProductDao;
import net.yolopago.pago.db.dao.product.ProductTaxDao;
import net.yolopago.pago.db.dao.product.TaxDao;
import net.yolopago.pago.db.dao.security.SessionDao;
import net.yolopago.pago.db.dao.security.UserDao;
import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.db.entity.Preference;
import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.User;
import net.yolopago.pago.ws.NotificationSignature;
import net.yolopago.pago.ws.builder.BuilderContract;
import net.yolopago.pago.ws.builder.BuilderContractType;
import net.yolopago.pago.ws.builder.BuilderNotification;
import net.yolopago.pago.ws.builder.BuilderProduct;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationRepository extends AbstractRepository {
	private static final String TAG = "NotificationRepository";
	private static NotificationRepository instance;
	private SessionDao sessionDao;
	private UserDao userDao;

	private ContractDao contractDao;
	private MerchantDao merchantDao;
	private PreferenceDao preferenceDao;
	private TicketLayoutDao ticketLayoutDao;
	private TicketFooterDao ticketFooterDao;
	private TicketHeaderDao ticketHeaderDao;

	private ContractTypeDao contractTypeDao;
	private VoucherLayoutDao voucherLayoutDao;
	private VoucherFooterDao voucherFooterDao;
	private VoucherHeaderDao voucherHeaderDao;
	private AIDDao aidDao;
	private CAPKDao capkDao;
	private RevokedCAPKDao revokedCAPKDao;
	private ExceptionPANDao exceptionPANDao;

	private ProductDao productDao;
	private PriceDao priceDao;
	private ProductTaxDao productTaxDao;
	private TaxDao taxDao;

	private NotificationDao notificationDao;

	private Retrofit retrofitContract;
	private Retrofit retrofitPayment;
	private Retrofit retrofitContractType;
	private Retrofit retrofitProduct;
	private Retrofit retrofitNotification;

	private BuilderContract builderContract;
	private BuilderContractType builderContractType;
	private BuilderProduct builderProduct;
	private BuilderNotification builderNotification;
	private String resultRefreshCaches;


	public static NotificationRepository getInstance(Application application) {
		if (instance == null) {
			instance = new NotificationRepository(application);
		}
		return instance;
	}

	private NotificationRepository(Application application) {
		super(application);
		sessionDao = configDatabase.sessionDao();
		userDao = configDatabase.userDao();

		contractDao = configDatabase.contractDao();
		merchantDao = configDatabase.merchantDao();
		preferenceDao = configDatabase.preferenceDao();
		ticketLayoutDao = configDatabase.ticketLayoutDao();
		ticketHeaderDao = configDatabase.ticketHeaderDao();
		ticketFooterDao = configDatabase.ticketFooterDao();

		contractTypeDao = configDatabase.contractTypeDao();
		voucherLayoutDao = configDatabase.voucherLayoutDao();
		voucherHeaderDao = configDatabase.voucherHeaderDao();
		voucherFooterDao = configDatabase.voucherFooterDao();
		aidDao = configDatabase.aidDao();
		capkDao = configDatabase.capkDao();
		revokedCAPKDao = configDatabase.revokedCAPKDao();
		exceptionPANDao = configDatabase.exceptionPANDao();

		productDao = productDatabase.productDao();
		priceDao = productDatabase.priceDao();
		productTaxDao = productDatabase.productTaxDao();
		taxDao = productDatabase.taxDao();

		notificationDao = configDatabase.notificationDao();

		builderContract = new BuilderContract();
		builderContractType = new BuilderContractType();
		builderProduct = new BuilderProduct();
		builderNotification = new BuilderNotification();
	}

	public MutableLiveData<String> refreshCaches() {
		MutableLiveData<String> refreshCachesDone = new MutableLiveData<>();
		resultRefreshCaches = "";
		Disposable disposable =
				Observable.just("DB")
						.subscribeOn(Schedulers.computation())
						.flatMap((String s) -> {
							Preference[] preferences = preferenceDao.findAll();
							//initializeURLs(preferences);

							retrofitContract = new Retrofit.Builder()
									.baseUrl(ServerURL.URL_SERVER_CONTRACT)
									.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
									.addConverterFactory(GsonConverterFactory.create())
									.client(okHttpClient)
									.build();
							retrofitPayment = new Retrofit.Builder()
									.baseUrl(ServerURL.URL_SERVER_PAYMENT)
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

							User user = userDao.findFirst();
							Merchant merchant = merchantDao.getFirst();
							Session session = sessionDao.getFirst();

							//isSellerEnabled(user, merchant, session);
							refreshCachesDone.postValue(resultRefreshCaches);

							return Observable.just("Hecho");
						})
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe();
		compositeDisposable.add(disposable);

		return refreshCachesDone;
	}


	private String getModel() {
		String model = Build.MODEL;
		model = model.replaceAll("-", "_");
		model = model.replaceAll(" ", "_");
		return model;
	}

	private String getSerialNumber() {
		String serial = Build.SERIAL;
		return serial;
	}


}
