package net.yolopago.pago.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import net.yolopago.pago.repository.NotificationRepository;

public class NotificationViewModel extends AndroidViewModel {
	private MutableLiveData<String> msg;
	private MutableLiveData<String> errorMsg;
	private NotificationRepository notificationRepository;


	public NotificationViewModel(@NonNull Application application) {
		super(application);
		msg = new MutableLiveData<>();
		errorMsg = new MutableLiveData<>();

		notificationRepository = NotificationRepository.getInstance(application);
		notificationRepository.setMsg(msg);
		notificationRepository.setErrorMsg(msg);
	}

	public MutableLiveData<String> getMsg() {
		return msg;
	}

	public MutableLiveData<String> getErrorMsg() {
		return errorMsg;
	}

	public LiveData<String> refreshCaches() {
		return notificationRepository.refreshCaches();
	}
}