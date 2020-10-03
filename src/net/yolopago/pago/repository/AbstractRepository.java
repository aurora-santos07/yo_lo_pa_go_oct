package net.yolopago.pago.repository;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;

import net.yolopago.pago.db.ConfigDatabase;
import net.yolopago.pago.db.PaymentDatabase;
import net.yolopago.pago.db.ProductDatabase;
import net.yolopago.pago.db.entity.Preference;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public class AbstractRepository {
	public  CompositeDisposable compositeDisposable = new CompositeDisposable();
	protected MutableLiveData<String> msg;
	protected MutableLiveData<String> errorMsg;
	protected ConfigDatabase configDatabase;
	protected ProductDatabase productDatabase;
	protected PaymentDatabase paymentDatabase;
	protected OkHttpClient okHttpClient;

	protected AbstractRepository(Application application) {
		configDatabase = ConfigDatabase.getDatabase(application);
		productDatabase = ProductDatabase.getDatabase(application);
		paymentDatabase = PaymentDatabase.getDatabase(application);

		msg = new MutableLiveData<String>();
		errorMsg = new MutableLiveData<String>();

		okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient(application.getApplicationContext());
	}

	public void setMsg(MutableLiveData<String> msg) {
		this.msg = msg;
	}

	public void setErrorMsg(MutableLiveData<String> errorMsg) {
		this.errorMsg = errorMsg;
	}


}
