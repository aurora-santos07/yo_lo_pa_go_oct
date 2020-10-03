package net.yolopago.pago.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import net.yolopago.pago.db.entity.Merchant;
import net.yolopago.pago.repository.MerchantRepository;

public class MerchantViewModel extends AndroidViewModel {
	private MutableLiveData<String> msg;
	private MutableLiveData<String> errorMsg;
	private MerchantRepository merchantRepository;


	public MerchantViewModel(@NonNull Application application) {
		super(application);
		msg = new MutableLiveData<>();
		errorMsg = new MutableLiveData<>();

		merchantRepository = MerchantRepository.getInstance(application);
		merchantRepository.setMsg(msg);
		merchantRepository.setErrorMsg(msg);
	}

	public MutableLiveData<String> getMsg() {
		return msg;
	}

	public MutableLiveData<String> getErrorMsg() {
		return errorMsg;
	}

	public LiveData<Merchant> getMerchant() {
		return merchantRepository.getMerchant();
	}
}