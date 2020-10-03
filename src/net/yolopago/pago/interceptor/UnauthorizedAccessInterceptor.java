package net.yolopago.pago.interceptor;

import com.wizarpos.emvsample.MainApp;

import net.yolopago.pago.repository.SessionRepository;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class UnauthorizedAccessInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {
		Response mainResponse = chain.proceed(chain.request());
		if (mainResponse.code() == 401 || mainResponse.code() == 403) {
			MainApp.mainActivity.onUnauthorizedAccess();
		}

		return mainResponse;
	}
}
