package net.yolopago.pago.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.User;
import net.yolopago.pago.repository.SessionRepository;
import net.yolopago.pago.ws.dto.contract.MerchantDto;

public class SessionViewModel extends AndroidViewModel {
	private MutableLiveData<String> msg;
	private MutableLiveData<String> errorMsg;
	private SessionRepository sessionRepository;


	public SessionViewModel(@NonNull Application application) {
		super(application);
		msg = new MutableLiveData<>();
		errorMsg = new MutableLiveData<>();

		sessionRepository = SessionRepository.getInstance(application);
		sessionRepository.setMsg(msg);
		sessionRepository.setErrorMsg(msg);
	}

	public MutableLiveData<String> getMsg() {
		return msg;
	}

	public MutableLiveData<String> getErrorMsg() {
		return errorMsg;
	}

	public LiveData<Session> getSession() {
		return sessionRepository.getSession();
	}



	public void saveMerchant(MerchantDto merchant) {
		sessionRepository.saveMerchant(merchant);
	}

	public void deleteAll() {
		sessionRepository.deleteAll();
	}

	public LiveData<User> getUser(Long idUser) {
		return sessionRepository.getUser(idUser);
	}

}